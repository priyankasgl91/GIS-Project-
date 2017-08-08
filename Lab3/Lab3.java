/* This is a GIS Project to locate wineries in Southern California.
Using ESRI Tool MOJO(Map Object Java Object).
Java programming is done to make the application.
The wineries are shown on the map and various ESRI Functionalities like zoom in, zoom out, Pan, get the information of the winery using Hotlink are pincorporated using Java
*/
import javax.swing.*;
import java.net.URI;
import javax.swing.event.*;
import java.io.*;
import java.util.Vector;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;
import com.esri.mo2.ui.bean.*; // beans used: Map,Layer,Toc,TocAdapter,Tool
        // TocEvent,Legend(a legend is part of a toc),ActateLayer
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import com.esri.mo2.data.feat.*; //ShapefileFolder, ShapefileWriter
//import com.esri.mo2.map.dpy.*;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.draw.*;
import com.esri.mo2.map.draw.BaseSimpleRenderer;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.ui.bean.Tool;
import com.esri.mo2.ui.dlg.AboutBox;
import java.awt.geom.*;
import com.esri.mo2.cs.geom.*; //using Envelope, Point, BasePointsArray
import com.esri.mo2.map.draw.*;

public class Lab3 extends JFrame {
  static Map map = new Map();
  static boolean fullMap = true;  // Map not zoomed
  static boolean helpToolOn;
  Legend legend;
  Legend legend2;
  Layer layer = new Layer();
  Layer layer2 = new Layer();
  Layer layer3 = null;
  static AcetateLayer acetLayer;
  static com.esri.mo2.map.dpy.Layer layer4;
  com.esri.mo2.map.dpy.Layer activeLayer;
  int activeLayerIndex;
  com.esri.mo2.cs.geom.Point initPoint,endPoint;
  double distance;
  JMenuBar mbar = new JMenuBar();
  JMenu file = new JMenu("File");
  JMenu theme = new JMenu("Theme");
  JMenu layercontrol = new JMenu("LayerControl");
  JMenu help = new JMenu("Help");
 JMenu about = new JMenu("About");
  JMenuItem attribitem = new JMenuItem("open attribute table", new ImageIcon(".\\icons\\tableview.gif"));
  JMenuItem createlayeritem  = new JMenuItem("create layer from selection", new ImageIcon(".\\icons\\Icon0915b.jpg"));
  static JMenuItem promoteitem = new JMenuItem("promote selected layer", new ImageIcon(".\\icons\\promote.jpg"));
  JMenuItem demoteitem = new JMenuItem("demote selected layer", new ImageIcon(".\\icons\\demote.jpg"));
  JMenuItem printitem = new JMenuItem("print",new ImageIcon(".\\icons\\print.gif"));
  JMenuItem addlyritem = new JMenuItem("add layer",new ImageIcon(".\\icons\\addtheme.gif"));
  JMenuItem remlyritem = new JMenuItem("remove layer",new ImageIcon(".\\icons\\delete.gif"));
  JMenuItem propsitem = new JMenuItem("Legend Editor",new ImageIcon(".\\icons\\properties.gif"));
  JMenu helptopics = new JMenu("Help Topics");
  JMenuItem tocitem = new JMenuItem("Table of Contents",new ImageIcon(".\\icons\\helptopic.gif"));
  JMenuItem legenditem = new JMenuItem("Legend Editor",new ImageIcon(".\\icons\\helptopic.gif"));
  JMenuItem layercontrolitem = new JMenuItem("Layer Control",new ImageIcon(".\\icons\\helptopic.gif"));
  JMenuItem helptoolitem = new JMenuItem("Help Tool",new ImageIcon(".\\icons\\help2.gif"));
  JMenuItem contactitem = new JMenuItem("Contact us");
  JMenuItem aboutitem = new JMenuItem("About MOJO...");
  JMenuItem abttoolitem = new JMenuItem("About Application");
  JMenuItem abtwinery = new JMenuItem ("About Winery");
  Toc toc = new Toc();
  String s1 = "C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\Southerncal.shp";
 String s2 = "C:\\mylayer\\Newwinery.shp"; 
  String datapathname = "";
  String legendname = "";
  ZoomPanToolBar zptb = new ZoomPanToolBar();
  static SelectionToolBar stb = new SelectionToolBar();
  JToolBar jtb = new JToolBar();
  ComponentListener complistener;
  JLabel statusLabel = new JLabel("status bar    LOC");
  static JLabel milesLabel = new JLabel("   DIST:  0 mi    ");
  static JLabel kmLabel = new JLabel("  0 km    ");
  java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");
  JPanel myjp = new JPanel();
  JPanel myjp2 = new JPanel();
  JButton prtjb = new JButton(new ImageIcon(".\\icons\\print.gif"));
  JButton addlyrjb = new JButton(new ImageIcon(".\\icons\\addtheme.gif"));
  JButton ptrjb = new JButton(new ImageIcon(".\\icons\\pointer.gif"));
  JButton distjb = new JButton(new ImageIcon(".\\icons\\measure_1.gif"));
  JButton XYjb = new JButton("XY");
  JButton hotjb = new JButton(new ImageIcon(".\\icons\\bolt.gif"));
  Toolkit tk = Toolkit.getDefaultToolkit();
  Image bolt = tk.getImage(".\\icons\\bolt.gif");  // 16x16 gif file
  java.awt.Cursor boltCursor = tk.createCustomCursor(bolt,new java.awt.Point(6,30),"bolt");
  JButton helpjb = new JButton(new ImageIcon(".\\icons\\help2.gif"));
  static Arrow arrow = new Arrow();
  static HelpTool helpTool = new HelpTool();
  //DistanceTool distanceTool= new DistanceTool();
  ActionListener lis;
  ActionListener layerlis;
  ActionListener layercontrollis;
  ActionListener helplis;
  TocAdapter mytocadapter;
  static Envelope env;
  MyPickAdapter picklis = new MyPickAdapter();
  Identify hotlink = new Identify(); //the Identify class implements a PickListener,
  static String mystate = null; // by me
  class MyPickAdapter implements PickListener {   //implements hotlink
    public void beginPick(PickEvent pe){System.out.println("begin pick");
    }  // this fires even when you click outside the states layer
    public void endPick(PickEvent pe){}
    public void foundData(PickEvent pe){
	  System.out.println("hola pick"); //fires only when a layer feature is clicked
      FeatureLayer flayer2 = (FeatureLayer) pe.getLayer();
      com.esri.mo2.data.feat.Cursor c = pe.getCursor();
      Feature f = null;
      System.out.println("inside foundData");
      Fields fields = null;
      if (c != null)
        f = (Feature)c.next();
      fields = f.getFields();
     String sname = fields.getField(6).getName(); //gets col. name for state name
      mystate = (String)f.getValue(6);
      try {
		HotPick hotpick = new HotPick();//opens dialog window with Duke in it
		hotpick.setVisible(true);
	  } catch(Exception e){}
    }
  };
  public Lab3() {
	super("Wineries in Southern California");
	helpToolOn = false;
	this.setBounds(50,50,750,500);
    zptb.setMap(map);
    stb.setMap(map);
    setJMenuBar(mbar);
    ActionListener lisZoom = new ActionListener() {
	  public void actionPerformed(ActionEvent ae){
	    fullMap = false;

	  }};
ActionListener zoomout = new ActionListener() {
	  public void actionPerformed(ActionEvent ae){
	    fullMap = false;

	  }};


	ActionListener lisFullExt = new ActionListener() {
	  public void actionPerformed(ActionEvent ae){
	    fullMap = true;}};
	MouseAdapter mlLisZoom = new MouseAdapter() {
	  public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	      try {
	        HelpDialog helpdialog = new HelpDialog((String)helpText.get(4));
            helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
      }
    };
    MouseAdapter mlLisZoomActive = new MouseAdapter() {
	  public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	      try {
		  	HelpDialog helpdialog = new HelpDialog((String)helpText.get(5));
		    helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	  } };
MouseAdapter mlLisArrow = new MouseAdapter() {
	  public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	      try {
		  	HelpDialog helpdialog = new HelpDialog((String)helpText.get(7));
		    helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	  } };
MouseAdapter mlPrint = new MouseAdapter() {
	  public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	      try {
		  	HelpDialog helpdialog = new HelpDialog((String)helpText.get(8));
		    helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	  } };
MouseAdapter mlDistance = new MouseAdapter() {
	  public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	      try {
		  	HelpDialog helpdialog = new HelpDialog((String)helpText.get(10));
		    helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	  } };
MouseAdapter mlXY = new MouseAdapter() {
	  public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	      try {
		  	HelpDialog helpdialog = new HelpDialog((String)helpText.get(11));
		    helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	  } };
MouseAdapter mlHotlink = new MouseAdapter() {
	  public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	      try {
		  	HelpDialog helpdialog = new HelpDialog((String)helpText.get(12));
		    helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	  } };
MouseAdapter mlHelp = new MouseAdapter() {
	  public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	      try {
		  	HelpDialog helpdialog = new HelpDialog((String)helpText.get(13));
		    helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	  } };
MouseAdapter mlzoomout = new MouseAdapter() {
	  public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
	      try {
		  	HelpDialog helpdialog = new HelpDialog((String)helpText.get(9));
		    helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	  } };

    
    MouseAdapter mlLisAddLayer = new MouseAdapter() {
	  public void mousePressed(MouseEvent me) {
		if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
		  try {
		  	HelpDialog helpdialog = new HelpDialog((String)helpText.get(6));
		    helpdialog.setVisible(true);
	      } catch(IOException e){}
		}
	  }
    };
	// next line gets ahold of a reference to the zoomin button
	JButton zoomInButton = (JButton)zptb.getActionComponent("ZoomIn");
        JButton zoomOutButton = (JButton)zptb.getActionComponent("ZoomOut");
         
	JButton zoomFullExtentButton =
	        (JButton)zptb.getActionComponent("ZoomToFullExtent");
	JButton zoomToSelectedLayerButton =
	      (JButton)zptb.getActionComponent("ZoomToSelectedLayer");
	zoomInButton.addActionListener(lisZoom);
        zoomOutButton.addActionListener(zoomout);
 
	zoomInButton.addMouseListener(mlLisZoom);
       
        zoomOutButton.addMouseListener(mlzoomout);
        ptrjb.addMouseListener(mlLisArrow);
prtjb.addMouseListener(mlPrint);
distjb.addMouseListener(mlDistance);
XYjb.addMouseListener(mlXY);
hotjb.addMouseListener(mlHotlink);
helpjb.addMouseListener(mlHelp);
	zoomFullExtentButton.addActionListener(lisFullExt);
        zoomFullExtentButton.addMouseListener(mlLisZoomActive);
	zoomToSelectedLayerButton.addActionListener(lisZoom);
	zoomToSelectedLayerButton.addMouseListener(mlLisZoomActive);
     
	addlyrjb.addMouseListener(mlLisAddLayer);
	complistener = new ComponentAdapter () {
	  public void componentResized(ComponentEvent ce) {
	    if(fullMap) {
		  map.setExtent(env);
	      map.zoom(1.0);    //scale is scale factor in pixels
	      map.redraw();
	    }
	  }
	};
    addComponentListener(complistener);
    lis = new ActionListener() {public void actionPerformed(ActionEvent ae){
	  Object source = ae.getSource();
	  if (source == prtjb || source instanceof JMenuItem ) {
        com.esri.mo2.ui.bean.Print mapPrint = new com.esri.mo2.ui.bean.Print();
        mapPrint.setMap(map);
        mapPrint.doPrint();// prints the map
        }
      else if (source == ptrjb) {
		arrow.arrowChores();
		map.setSelectedTool(arrow);
	    }
	  else if (source == distjb) {
		DistanceTool distanceTool = new DistanceTool();
		map.setSelectedTool(distanceTool);
        }
	  else if (source == XYjb) {
		try {
			System.out.println("XYjb listener");
		  AddXYtheme addXYtheme = new AddXYtheme();
		  addXYtheme.setMap(map);
		  addXYtheme.setVisible(false);// the file chooser needs a parent
		    // but the parent can stay behind the scenes
			System.out.println("XYjb listener done");
		  map.redraw();
		  } catch (IOException e){}
	    }
		else if (source == hotjb) {
        hotlink.setCursor(boltCursor); //set cursor for the tool
        map.setSelectedTool(hotlink);
      }
	  else if (source == helpjb) {
		helpToolOn = true;
		map.setSelectedTool(helpTool);
	  }
	  else
	    {
		try {
	      AddLyrDialog aldlg = new AddLyrDialog();
	      aldlg.setMap(map);
	      aldlg.setVisible(true);
	    } catch(IOException e){}
      }
    }};
    layercontrollis = new ActionListener() {public void
                actionPerformed(ActionEvent ae){
	  String source = ae.getActionCommand();
	  System.out.println(activeLayerIndex+" active index");
	  if (source == "promote selected layer")
		map.getLayerset().moveLayer(activeLayerIndex,++activeLayerIndex);
      else
        map.getLayerset().moveLayer(activeLayerIndex,--activeLayerIndex);
      enableDisableButtons();
      map.redraw();
    }};
    helplis = new ActionListener()
                        {public void actionPerformed(ActionEvent ae){
	  Object source = ae.getSource();
	  if (source instanceof JMenuItem) {
		String arg = ae.getActionCommand();
		if(arg == "About MOJO...") {
          AboutBox aboutbox = new AboutBox();
          aboutbox.setProductName("MOJO");
          aboutbox.setProductVersion("2.0");
          aboutbox.setVisible(true);
          aboutbox.setLocation(100,100);
	    }
	    else if(arg == "Contact us") {
		  try {
	        String s = "\n\n\n\n                 Any enquiries should be addressed to " +
	        "\n\n\n                         priyankasgl91@gmail.com";
            HelpDialog helpdialog = new HelpDialog(s);
            helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	    else if(arg == "Table of Contents") {
		  try {
	        HelpDialog helpdialog = new HelpDialog((String)helpText.get(0));
            helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	    else if(arg == "Legend Editor") {
		  try {
	        HelpDialog helpdialog = new HelpDialog((String)helpText.get(1));
            helpdialog.setVisible(true);
          } catch(IOException e){}
	    }
	    else if(arg == "Layer Control") {
		  try {
	        HelpDialog helpdialog = new HelpDialog((String)helpText.get(2));
            helpdialog.setVisible(true);
          } catch(IOException e){}
		}
		else if(arg == "Help Tool") {
	      try {
            HelpDialog helpdialog = new HelpDialog((String)helpText.get(3));
            helpdialog.setVisible(true);
	      } catch(IOException e){}
	    
	  }else if(arg == "About Application") {
			String abouttext = "This is a GIS tool to Locate the Wineries in Southern California Created by Priyanka Sehgal \n\n" + 
 "This application uses Java Prpgramming Language \n" + "along with MOJO(Map Object Java Object), provided by the company named ESRI \n"+"Step by Step Description of how the application is built is as follows \n" + "1)shapefile is obtained from http://52.26.186.219/internships/useit/content/california-counties-shapefiles \n" + "Winery shapefile is created using XY tool and a csv file which has info about the winery such as Name, City, Address, Contact, website \n" + "2)Add layer-Used to Add layer on the TOC \n"+ "3)Remove layer - Used to remove layer from toc \n" + "4)Legend Editor -This is used to change properties of the layer. \n" + "5)Open Attribute Table -This is used to display data related to layer. \n" + "6) Create layer from selection-This is used to create different \n" + "7)Layer Control -This is used to either promote or demote layer on the toc.\n" + "8)Hotlink Tool -gives the detailed information about the layer/feature selected in the form \n" + "9)Help Tool -This is used show help text for the items on the map. \n" + "10)Zoom to full extent -This is used to zoom map to full extent."
                          ;
			try {
            HelpDialog helpdialog = new HelpDialog(abouttext);
			helpdialog.setBounds(70,70,700,525);
            helpdialog.setVisible(true);
			} catch(IOException e){}
			}

else if(arg == "About Winery") {
			String abouttext = "This is a GIS tool to Locate the Wineries in Southern California \n\n" + 
 "California State has the maximum production of wine because of the suitable climate. \n" + " Almost three quarters the size of France, California accounts for nearly 90 percent of American wine production \n"+"The state's viticultural history dates back to the 18th century when Spanish missionaries planted the first vineyards to produce wine for Mass. \n"  + "Today there are more than 1,200 wineries in the state, ranging from small boutique wineries to large corporations with distribution around the globe. \n"+ "This application locates 15 wineries along with their basic details and images\n"+ "The state of California was first introduced to Vitis vinifera vines, a species of wine grapes \n" + "Most of the state's wine regions are found between the Pacific coast and the Central Valley.  \n" + " Some of the major wineries listed in these applications are: \n\n" +"1)Carter Estate Winery and Resort in Murrieta is a beautiful winery, along with facilities like \n" + " Tasting room, Spa Service,and Dining Option \n\n" + "2)Canyon Crest Winery in Riverside\n" + " They have a unique trend of customizing wine bottles. \n\n" + "3) Cobblestone Vineyards in Los Angeles \n" + "They provide the facility to buy wine online through their site \n\n" + "4)Maness Vineyards in Jamul \n" + " Best thing about this winery is that they are open on holidays too in the evening for 2 hours \n\n" + "5)Souza Family Vineyard in Tehachapi \n" + "Taste of wine with the love and hospitality of family. What else one wants!! \n\n" + "These are some of the best wineries in california listed in this application."
                         ;
			try {
            HelpDialog helpdialog = new HelpDialog(abouttext);
			helpdialog.setBounds(70,70,700,525);
            helpdialog.setVisible(true);
			} catch(IOException e){}
			}
   } }};


    layerlis = new ActionListener() {public void actionPerformed(ActionEvent ae){
	  Object source = ae.getSource();
	  if (source instanceof JMenuItem) {
		String arg = ae.getActionCommand();
		if(arg == "add layer") {
          try {
	        AddLyrDialog aldlg = new AddLyrDialog();
	        aldlg.setMap(map);
	        aldlg.setVisible(true);
          } catch(IOException e){}
	      }
	    else if(arg == "remove layer") {
	      try {
			com.esri.mo2.map.dpy.Layer dpylayer =
			   legend.getLayer();
			map.getLayerset().removeLayer(dpylayer);
			map.redraw();
			remlyritem.setEnabled(false);
			propsitem.setEnabled(false);
			attribitem.setEnabled(false);
			promoteitem.setEnabled(false);
			demoteitem.setEnabled(false);
			stb.setSelectedLayer(null);
			zptb.setSelectedLayer(null);
	      } catch(Exception e) {}
	      }
	    else if(arg == "Legend Editor") {
          LayerProperties lp = new LayerProperties();
          lp.setLegend(legend);
          lp.setSelectedTabIndex(0);
          lp.setVisible(true);
	    }
	    else if (arg == "open attribute table") {
	      try {
	        layer4 = legend.getLayer();
            AttrTab attrtab = new AttrTab();
            attrtab.setVisible(true);
	      } catch(IOException ioe){}
	    }
        else if (arg=="create layer from selection") {
						layer4 = legend.getLayer();
						FeatureLayer flayer2 = (FeatureLayer)layer4;
						if (flayer2.hasSelection()) {
							try{
								BaseSimpleRenderer renderer = new BaseSimpleRenderer();
								SimpleLineSymbol slSymbol;
								SimplePolygonSymbol spSymbol;//
								RasterMarkerSymbol rmSymbol;
								SelectionSet selectset = flayer2.getSelectionSet();
								// next line makes a new feature layer of the selections
								FeatureLayer selectedlayer = flayer2.createSelectionLayer(selectset);
								int shpType = ShapefileWriter.guessShapefileType(selectedlayer.getFeatureClass());
								int shpFileType = 0;
								if(shpType == 1)//point
								{
									System.out.println("shptype "+ShapefileWriter.guessShapefileType(selectedlayer.getFeatureClass()));
									shpFileType = 0;
									rmSymbol = new RasterMarkerSymbol();
									rmSymbol.setAntialiasing(true);
									rmSymbol.setTransparency(0.6);
									rmSymbol.setImageString( ".\\icons\\bullseye.jpg" );
									rmSymbol.setSizeX(16);
									rmSymbol.setSizeY(16);
									renderer.setSymbol(rmSymbol);
								} 
								else if(shpType == 3)//line
								{
									System.out.println("3shptype "+ShapefileWriter.guessShapefileType(selectedlayer.getFeatureClass()));
									shpFileType = 1;
									slSymbol = new SimpleLineSymbol();
									slSymbol.setStroke(new AoLineStroke(AoLineStyle.DASH_LINE, 2));
									slSymbol.setLineColor(new java.awt.Color(255,0,0));
									renderer.setSymbol(slSymbol);
								}
								else if(shpType == 5)//polygon
								{
									System.out.println("5shptype "+ShapefileWriter.guessShapefileType(selectedlayer.getFeatureClass()));
									shpFileType = 2;
									spSymbol = new SimplePolygonSymbol();
									spSymbol.setPaint(AoFillStyle.getPaint(AoFillStyle.SOLID_FILL,new java.awt.Color(255,0,0)));
									spSymbol.setBoundary(true);
									renderer.setSymbol(spSymbol);
								}
								renderer.setLayer(selectedlayer);
								selectedlayer.setRenderer(renderer);
								Layerset layerset = map.getLayerset();
								// next line places a new visible layer, e.g. Montana, on the map
								layerset.addLayer(selectedlayer);
								//selectedlayer.setVisible(true);
								if(stb.getSelectedLayers() != null)
								{
									promoteitem.setEnabled(true);
								}
								//try {
									legend2 = toc.findLegend(selectedlayer);
								//} catch (Exception e) {}
								
								CreateShapeDialog csd = new CreateShapeDialog(selectedlayer, shpFileType);
								csd.setVisible(true);
								Flash flash = new Flash(legend2);
								flash.start();
								map.redraw(); // necessary to see color immediately
							}
							catch(Exception ex)
							{System.out.println(ex);}
							}
	    }
      }
    }};
    toc.setMap(map);
    mytocadapter = new TocAdapter() {
	  public void click(TocEvent e) {
		System.out.println(activeLayerIndex+ "dex");
	    legend = e.getLegend();
	    activeLayer = legend.getLayer();
	    stb.setSelectedLayer(activeLayer);
	    zptb.setSelectedLayer(activeLayer);
	    // get active layer index for promote and demote
	    activeLayerIndex = map.getLayerset().indexOf(activeLayer);
	    // layer indices are in order added, not toc order.
	    System.out.println(activeLayerIndex + "active ndex");
		com.esri.mo2.map.dpy.Layer[] layers = {activeLayer};
              		hotlink.setSelectedLayers(layers);
	    remlyritem.setEnabled(true);
	    propsitem.setEnabled(true);
	    attribitem.setEnabled(true);
	    enableDisableButtons();
   	  }
    };
    map.addMouseMotionListener(new MouseMotionAdapter() {
	  public void mouseMoved(MouseEvent me) {
		com.esri.mo2.cs.geom.Point worldPoint = null;
		if (map.getLayerCount() > 0) {
		  worldPoint = map.transformPixelToWorld(me.getX(),me.getY());
		  String s = "X:"+df.format(worldPoint.getX())+" "+
		             "Y:"+df.format(worldPoint.getY());
		  statusLabel.setText(s);
	      }
	    else
	      statusLabel.setText("X:0.000 Y:0.000");
      }
    });

    toc.addTocListener(mytocadapter);
    remlyritem.setEnabled(false); // assume no layer initially selected
    propsitem.setEnabled(false);
    attribitem.setEnabled(false);
    promoteitem.setEnabled(false);
    demoteitem.setEnabled(false);
    printitem.addActionListener(lis);
    addlyritem.addActionListener(layerlis);
    remlyritem.addActionListener(layerlis);
    propsitem.addActionListener(layerlis);
    attribitem.addActionListener(layerlis);
    createlayeritem.addActionListener(layerlis);
    promoteitem.addActionListener(layercontrollis);
    demoteitem.addActionListener(layercontrollis);
    tocitem.addActionListener(helplis);

    legenditem.addActionListener(helplis);
    layercontrolitem.addActionListener(helplis);
    helptoolitem.addActionListener(helplis);
    contactitem.addActionListener(helplis);
    aboutitem.addActionListener(helplis);
    abttoolitem.addActionListener(helplis);
    abtwinery.addActionListener(helplis);
    file.add(addlyritem);
    file.add(printitem);
    file.add(remlyritem);
    file.add(propsitem);
    theme.add(attribitem);
    theme.add(createlayeritem);
    layercontrol.add(promoteitem);
    layercontrol.add(demoteitem);
    help.add(helptopics);
    helptopics.add(tocitem);
    helptopics.add(legenditem);
    helptopics.add(layercontrolitem);
    help.add(helptoolitem);
    help.add(contactitem);
    help.add(aboutitem);
    about.add(abttoolitem);
     about.add(abtwinery);
    mbar.add(file);
    mbar.add(theme);
    mbar.add(layercontrol);
    mbar.add(help);
    mbar.add(about);
    prtjb.addActionListener(lis);
    prtjb.setToolTipText("print map");
    addlyrjb.addActionListener(lis);
    addlyrjb.setToolTipText("add layer");
    ptrjb.addActionListener(lis);
    distjb.addActionListener(lis);
    XYjb.addActionListener(lis);
	hotlink.addPickListener(picklis);
    hotlink.setPickWidth(5);
	hotjb.addActionListener(lis);
    helpjb.addActionListener(lis);
    XYjb.setToolTipText("add a layer of points from a file");
	hotjb.setToolTipText("hotlink--click on a shape to see a picture");
    ptrjb.setToolTipText("pointer");
    distjb.setToolTipText("press-drag-release to measure a distance");
    helpjb.setToolTipText("Help Tool");
    jtb.add(prtjb);
    jtb.add(addlyrjb);
    jtb.add(ptrjb);
    jtb.add(distjb);
    jtb.add(XYjb);
	jtb.add(hotjb);
    jtb.add(helpjb);
    myjp.add(jtb);
    myjp.add(zptb); myjp.add(stb);
    myjp2.add(statusLabel);
    myjp2.add(milesLabel);myjp2.add(kmLabel);
    setuphelpText();
    getContentPane().add(map, BorderLayout.CENTER);
    getContentPane().add(myjp,BorderLayout.NORTH);
    getContentPane().add(myjp2,BorderLayout.SOUTH);
    addShapefileToMap(layer,s1);
    addShapefileToMap(layer2,s2);
    getContentPane().add(toc, BorderLayout.WEST);
	java.util.List list = toc.getAllLegends();
	com.esri.mo2.map.dpy.Layer lay1 = ((Legend)list.get(1)).getLayer();
	FeatureLayer flayer1 = (FeatureLayer)lay1;
    BaseSimpleLabelRenderer bslr1 = new BaseSimpleLabelRenderer();
    FeatureClass fclass1 = flayer1.getFeatureClass();
    String [] colnames = fclass1.getFields().getNames();
    System.out.println(colnames[6]);  // state name field
    Fields fields = fclass1.getFields();
    //
    Field field = fields.getField(6); //capture state_name field
    System.out.println(field.getName());
    bslr1.setLabelField(field); //make state_name the label field
    //flayer1.setLabelRenderer(bslr1);

com.esri.mo2.map.dpy.Layer laytemp = ((Legend)list.get(0)).getLayer();
	FeatureLayer flayertemp = (FeatureLayer)laytemp;
    BaseSimpleLabelRenderer bslrtemp = new BaseSimpleLabelRenderer();
    FeatureClass fclasstemp = flayertemp.getFeatureClass();
    String [] colnamestemp = fclasstemp.getFields().getNames();
    Fields fieldstemp = fclasstemp.getFields();
    //
    Field fieldtemp = fieldstemp.getField(2); //capture state_name field
    bslrtemp.setLabelField(fieldtemp); //make state_name the label field
    flayertemp.setLabelRenderer(bslrtemp);
	
    com.esri.mo2.map.dpy.Layer lay0 = ((Legend)list.get(0)).getLayer();
    FeatureLayer flayer0 = (FeatureLayer)lay0;
    BaseSimpleRenderer bsr1 = (BaseSimpleRenderer)flayer1.getRenderer();
   BaseSimpleRenderer bsr0 = (BaseSimpleRenderer)flayer0.getRenderer();
    SimplePolygonSymbol sym1 = (SimplePolygonSymbol)bsr1.getSymbol();
    RasterMarkerSymbol sym0 = new RasterMarkerSymbol();

    sym1.setPaint(AoFillStyle.getPaint(com.esri.mo2.map.draw.AoFillStyle.SOLID_FILL,new java.awt.Color(255,255,0)));
SampleColorSchemes pastels = new SampleColorSchemes(SampleColorSchemes.PASTELS);
sym1.setPaint(AoFillStyle.getPaint(com.esri.mo2.map.draw.AoFillStyle.SOLID_FILL,pastels.getColorAt(1)));

    sym1.setFillTransparency(.5);
 //sym0.setAntialiasing(true);
	sym0.setTransparency(1.0);
	sym0.setImageString(".\\icons\\wine.gif");
	sym0.setSizeX(16);
	sym0.setSizeY(16);
	//setRenderer(bsr0);
    bsr1.setSymbol(sym1);
    bsr0.setSymbol(sym0);
  }
  private void addShapefileToMap(Layer layer,String s) {
    String datapath = s; //"C:\\ESRI\\MOJ20\\Samples\\Data\\USA\\Southerncal.shp";
    layer.setDataset("0;"+datapath);
    map.add(layer);
  }
  private void setuphelpText() {
	String s0 =
	  "    The toc, or table of contents, is to the left of the map. \n" +
	  "    Each entry is called a 'legend' and represents a map 'layer' or \n" +
	  "    'theme'.  If you click on a legend, that layer is called the \n" +
	  "    active layer, or selected layer.  Its display (rendering) properties \n" +
	  "    can be controlled using the Legend Editor, and the legends can be \n" +
	  "    reordered using Layer Control.  Both Legend Editor and Layer Control \n" +
	  "    are separate Help Topics.  This line is e... x... t... e... n... t... e... d"  +
	  "    to test the scrollpane.";
	helpText.add(s0);
	String s1 = "  The Legend Editor is a menu item found under the File menu. \n" +
	  "    Given that a layer is selected by clicking on its legend in the table of \n" +
	  "    contents, clicking on Legend Editor will open a window giving you choices \n" +
	  "    about how to display that layer.  For example you can control the color \n" +
	  "    used to display the layer on the map, or whether to use multiple colors ";
	helpText.add(s1);
	String s2 = "  Layer Control is a Menu on the menu bar.  If you have selected a \n"+
	   " layer by clicking on a legend in the toc (table of contents) to the left of \n" +
	   " the map, then the promote and demote tools will become usable.  Clicking on \n" +
	   " promote will raise the selected legend one position higher in the toc, and \n" +
	   " clicking on demote will lower that legend one position in the toc.";
	helpText.add(s2);
	String s3 = "    This tool will allow you to learn about certain other tools. \n" +
	  "    You begin with a standard left mouse button click on the Help Tool itself. \n" +
	  "    RIGHT click on another tool and a window may give you information about the  \n" +
	  "    intended usage of the tool.  Click on the arrow tool to stop using the \n" +
	  "    help tool.";
	helpText.add(s3);
	String s4 = "If you click on the Zoom In tool, and then click on the map, you \n" +
	  " will see a part of the map in greater detail.  You can zoom in multiple times. \n" +
	  " You can also sketch a rectangular part of the map, and zoom to that.  You can \n" +
	  " undo a Zoom In with a Zoom Out or with a Zoom to Full Extent";
	helpText.add(s4);
	String s5 = "You must have a selected layer to use the Zoom to Active Layer tool.\n" +
	  "    If you then click on Zoom to Active Layer, you will be shown enough of \n" +
	  "    the full map to see all of the features in the layer you select.  If you \n" +
	  "    select a layer that shows where glaciers are, then you do not need to \n" +
	  "    see Hawaii, or any southern states, so you will see Alaska, and northern \n" +
	  "    mainland states.";
	helpText.add(s5);
	String s6 = "If you click on the Add Layer tool, a window will pop up, in which you \n" +
	  "need to use a file chooser to navigate to a shapefile definition. e.g. the shape  \n" +
	  "files in Samples\\Data\\USA.  Click on the one you want to add, e.g. uslakes.shp, \n" +
	  "and then click on the OK button.  The new layer should appear on the map.";
	helpText.add(s6);
        
       String s7 = "If you click on the Arrow tool \n" +
	  "the cursor will lose its present functionality  \n" +
	  "and the cursor will change back to arrow tool " ;
	helpText.add(s7);
       String s8 = "If you click on the Print tool \n" +
	  "you can take out the print out \n" +
	  "of the map for your reference" ;
	helpText.add(s8);
String s9 = "If you click on the Zoom Out tool, and then click on the map, you \n" +
	  " will see a part of the map in lesser detail.  You can zoom out multiple times. \n" +
	  " You can undo a Zoom Out with a Zoom In or with a Zoom to Full Extent";
	helpText.add(s9);
String s10 = "If you click on the Distance tool \n" +
	  "you can measure the distance between two points on the \n" +
	  "map by clicking on one point and dragging till the other point \n " +
          " and release the cursor";
	helpText.add(s10);
String s11 = "If you click on the XY tool \n" +
	  "this tool is used to convert csv file into \n" +
	  "shape and dbf file. \n " +
          "When we choose the csv file from our system, a popup comes to enter the name \n " +
          "and the path name where the file is to be saved ";
	helpText.add(s11);
String s12 = "If you click on the hotlink tool \n" +
	  "this tool is used to click on a shape file \n" +
	  "to see a picture and some information. " 
          ;
	helpText.add(s12);
String s13 = "If you click on the help tool \n" +
	  "left click here, then right click on a tool \n" +
	  "to learn about that tool. Click arrow tool when done " 
          ;
	helpText.add(s13);
String s14 = "If you click on the Pan tool \n" +
	  "When you click on the map after clicking this tool, you can  \n" +
	  "navigate to parts of the maps.  " 
          ;
	helpText.add(s14);

  }
  public static void main(String[] args) {
    Lab3 qstart = new Lab3();
    qstart.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
            System.out.println("Thanks, Quick Start exits");
            System.exit(0);
        }
    });
    qstart.setVisible(true);
    env = map.getExtent();
  }
  private void enableDisableButtons() {
    int layerCount = map.getLayerset().getSize();
    if (layerCount < 2) {
      promoteitem.setEnabled(false);
      demoteitem.setEnabled(false);
      }
    else if (activeLayerIndex == 0) {
      demoteitem.setEnabled(false);
      promoteitem.setEnabled(true);
	  }
    else if (activeLayerIndex == layerCount - 1) {
      promoteitem.setEnabled(false);
      demoteitem.setEnabled(true);
	  }
	else {
	  promoteitem.setEnabled(true);
	  demoteitem.setEnabled(true);
    }
  }
  private ArrayList helpText = new ArrayList(3);
}
// following is an Add Layer dialog window
class AddLyrDialog extends JDialog {
  Map map;
  ActionListener lis;
  JButton ok = new JButton("OK");
  JButton cancel = new JButton("Cancel");
  JPanel panel1 = new JPanel();
  com.esri.mo2.ui.bean.CustomDatasetEditor cus = new com.esri.mo2.ui.bean.
    CustomDatasetEditor();
  AddLyrDialog() throws IOException {
	setBounds(50,50,520,430);
	setTitle("Select a theme/layer");
	addWindowListener(new WindowAdapter() {
	  public void windowClosing(WindowEvent e) {
	    setVisible(false);
	  }
    });
	lis = new ActionListener() {
	  public void actionPerformed(ActionEvent ae) {
	    Object source = ae.getSource();
	    if (source == cancel)
	      setVisible(false);
	    else {
	      try {
			setVisible(false);
			map.getLayerset().addLayer(cus.getLayer());
			map.redraw();
			if (Lab3.stb.getSelectedLayers() != null)
			  Lab3.promoteitem.setEnabled(true);
		  } catch(IOException e){}
	    }
	  }
    };
    ok.addActionListener(lis);
    cancel.addActionListener(lis);
    getContentPane().add(cus,BorderLayout.CENTER);
    panel1.add(ok);
    panel1.add(cancel);
    getContentPane().add(panel1,BorderLayout.SOUTH);
  }
  public void setMap(com.esri.mo2.ui.bean.Map map1){
	map = map1;
  }
}

class AddXYtheme extends JDialog {
  Map map;
  Vector s2 = new Vector();
  JFileChooser jfc = new JFileChooser();
  BasePointsArray bpa = new BasePointsArray();
  	FeatureLayer XYlayer;
  AddXYtheme() throws IOException {
  System.out.println("AddXYtheme constructor");
	setBounds(50,50,520,430);
	System.out.println("AddXYtheme showopendiag");
        
	jfc.showOpenDialog(this);
	try {
	  File file  = jfc.getSelectedFile();
	  FileReader fred = new FileReader(file);
	  BufferedReader in = new BufferedReader(fred);
	  String s; // = in.readLine();
	  double x,y;
	  int n = 0;
	  System.out.println("AddXYtheme while loop");
	  while ((s = in.readLine()) != null) {
	  System.out.println("AddXYtheme inside while loop" +s);
		StringTokenizer st = new StringTokenizer(s,",");
		x = Double.parseDouble(st.nextToken());
		y = Double.parseDouble(st.nextToken());
		bpa.insertPoint(n++,new com.esri.mo2.cs.geom.Point(x,y));
		System.out.println("AddXYtheme add to s2");
		s2.addElement(st.nextToken());
		System.out.println("AddXYtheme added to s2");
	  }
	  System.out.println("AddXYtheme while loop end");
	} catch (IOException e){System.out.println(e);}
	XYfeatureLayer xyfl = new XYfeatureLayer(bpa,map,s2);
         XYlayer = xyfl;
	xyfl.setVisible(true);
	map = Lab3.map;
	map.getLayerset().addLayer(xyfl);
	map.redraw();
        CreateXYShapeDialog xydialog =
                              new CreateXYShapeDialog(XYlayer);
        xydialog.setVisible(true);
  }
  public void setMap(com.esri.mo2.ui.bean.Map map1){
  	map = map1;
  }
}
class XYfeatureLayer extends BaseFeatureLayer {
  BaseFields fields;
  private java.util.Vector featureVector;
  public XYfeatureLayer(BasePointsArray bpa,Map map,Vector s2) {
  System.out.println("XYFeature layer constructor");
	createFeaturesAndFields(bpa,map,s2);
	BaseFeatureClass bfc = getFeatureClass("MyPoints",bpa);
	setFeatureClass(bfc);
	BaseSimpleRenderer srd = new BaseSimpleRenderer();
	//SimpleMarkerSymbol sms= new SimpleMarkerSymbol();
	//TrueTypeMarkerSymbol ttm = new TrueTypeMarkerSymbol();
	//ttm.setFont(new Font("ESRI Transportation & Civic",Font.PLAIN,20));// aka esri_9
	//sms.setType(SimpleMarkerSymbol.CIRCLE_MARKER);
	//ttm.setColor(new Color(255,0,0));
	//sms.setSymbolColor(new Color(255,0,0));
	//sms.setWidth(5);
	//ttm.setCharacter("101"); //airplane
	//srd.setSymbol(ttm);
       RasterMarkerSymbol rmSymbol = new RasterMarkerSymbol();
	rmSymbol.setAntialiasing(true);
	rmSymbol.setTransparency(0.6);
	rmSymbol.setImageString(".\\icons\\wine.gif");
	rmSymbol.setSizeX(16);
	rmSymbol.setSizeY(16);
	srd.setSymbol(rmSymbol);
	setRenderer(srd);
	// without setting layer capabilities, the points will not
	// display (but the toc entry will still appear)
	XYLayerCapabilities lc = new XYLayerCapabilities();
	setCapabilities(lc);
  }
  private void createFeaturesAndFields(BasePointsArray bpa,Map map,Vector s2) {
	featureVector = new java.util.Vector();
	fields = new BaseFields();
	createDbfFields();
	for(int i=0;i<bpa.size();i++) {
	  BaseFeature feature = new BaseFeature();  //feature is a row
	  feature.setFields(fields);
        	  com.esri.mo2.cs.geom.Point p = new
	    com.esri.mo2.cs.geom.Point(bpa.getPoint(i));
System.out.println("createFeaturesAndFields " + p);
	
	  feature.setValue(0,p);
  System.out.println("createFeaturesAndFields new Integer(0)" + new Integer(0));
	  feature.setValue(1,new Integer(0));  // point data
System.out.println("createFeaturesAndFields (String)s2.elementAt(i)" + (String)s2.elementAt(i));
 	  feature.setValue(2,(String)s2.elementAt(i));
	  feature.setDataID(new BaseDataID("MyPoints",i));
	  featureVector.addElement(feature);
	}
System.out.println("createFeaturesAndFields featureVector" + featureVector);
  }
  private void createDbfFields() {
	fields.addField(new BaseField("#SHAPE#",Field.ESRI_SHAPE,0,0));
	fields.addField(new BaseField("ID",java.sql.Types.INTEGER,9,0));
	fields.addField(new BaseField("Name",java.sql.Types.VARCHAR,200,0));
  }
  public BaseFeatureClass getFeatureClass(String name,BasePointsArray bpa){
    com.esri.mo2.map.mem.MemoryFeatureClass featClass = null;
    try {
	  featClass = new com.esri.mo2.map.mem.MemoryFeatureClass(MapDataset.POINT,
	    fields);
    } catch (IllegalArgumentException iae) {}
    featClass.setName(name);
    for (int i=0;i<bpa.size();i++) {
	  featClass.addFeature((Feature) featureVector.elementAt(i));
    }
    return featClass;
  }
  private final class XYLayerCapabilities extends
       com.esri.mo2.map.dpy.LayerCapabilities {
    XYLayerCapabilities() {
	  for (int i=0;i<this.size(); i++) {
		setAvailable(this.getCapabilityName(i),true);
		setEnablingAllowed(this.getCapabilityName(i),true);
		getCapability(i).setEnabled(true);
	  }
    }
  }
}

class AttrTab extends JDialog {
  JPanel panel1 = new JPanel();
  com.esri.mo2.map.dpy.Layer layer = Lab3.layer4;
  JTable jtable = new JTable(new MyTableModel());
  JScrollPane scroll = new JScrollPane(jtable);

  public AttrTab() throws IOException {
  	setBounds(70,70,450,350);
  	setTitle("Attribute Table");
  	addWindowListener(new WindowAdapter() {
  	  public void windowClosing(WindowEvent e) {
  	    setVisible(false);
  	  }
    });
    scroll.setHorizontalScrollBarPolicy(
	   JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	// next line necessary for horiz scrollbar to work
	jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

	TableColumn tc = null;
	int numCols = jtable.getColumnCount();
	//jtable.setPreferredScrollableViewportSize(
		//new java.awt.Dimension(440,340));
	for (int j=0;j<numCols;j++) {
	  tc = jtable.getColumnModel().getColumn(j);
	  tc.setMinWidth(50);
    }
    getContentPane().add(scroll,BorderLayout.CENTER);
  }
}
class MyTableModel extends AbstractTableModel {
 // the required methods to implement are getRowCount,
 // getColumnCount, getValueAt
  com.esri.mo2.map.dpy.Layer layer = Lab3.layer4;
  MyTableModel() {
	qfilter.setSubFields(fields);
	com.esri.mo2.data.feat.Cursor cursor = flayer.search(qfilter);
	while (cursor.hasMore()) {
		ArrayList inner = new ArrayList();
		Feature f = (com.esri.mo2.data.feat.Feature)cursor.next();
		inner.add(0,String.valueOf(row));
		for (int j=1;j<fields.getNumFields();j++) {
		  inner.add(f.getValue(j).toString());
		}
	    data.add(inner);
	    row++;
    }
  }
  FeatureLayer flayer = (FeatureLayer) layer;
  FeatureClass fclass = flayer.getFeatureClass();
  String columnNames [] = fclass.getFields().getNames();
  ArrayList data = new ArrayList();
  int row = 0;
  int col = 0;
  BaseQueryFilter qfilter = new BaseQueryFilter();
  Fields fields = fclass.getFields();
  public int getColumnCount() {
	return fclass.getFields().getNumFields();
  }
  public int getRowCount() {
	return data.size();
  }
  public String getColumnName(int colIndx) {
	return columnNames[colIndx];
  }
  public Object getValueAt(int row, int col) {
	  ArrayList temp = new ArrayList();
	  temp =(ArrayList) data.get(row);
      return temp.get(col);
  }
}
class CreateShapeDialog extends JDialog {
	String name = "";
	String path = "";
	int shpType = 0;
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	JTextField nameField = new JTextField("enter layer name here, then hit ENTER",25);
	com.esri.mo2.map.dpy.FeatureLayer selectedlayer;
	ActionListener lis = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			Object o = ae.getSource();
			if (o == nameField) {
				name = nameField.getText().trim();
				path = ((ShapefileFolder)(Lab3.layer4.getLayerSource())).getPath();
			}
			else if (o == cancel)
				setVisible(false);
			else {
				try {
					name = nameField.getText().trim();
					System.out.println(selectedlayer + " source " + Lab3.layer4.getLayerSource());
					path = ((ShapefileFolder)(Lab3.layer4.getLayerSource())).getPath();
					System.out.println(selectedlayer + path + name);
					//System.out.println(ShapefileWriter.guessShapefileType(selectedlayer.getFeatureClass()));
					ShapefileWriter.writeFeatureLayer(selectedlayer,path,name,shpType);
				} catch(Exception e) {System.out.println(e);}
				setVisible(false);
			}
		}
	};

	JPanel panel1 = new JPanel();
	JLabel centerlabel = new JLabel();
	//centerlabel;
	CreateShapeDialog (com.esri.mo2.map.dpy.FeatureLayer layer5, int shpFileType) {
		try{
			selectedlayer = layer5;
			shpType = shpFileType;
			setBounds(40,350,450,150);
			setTitle("Create new shapefile?");
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setVisible(false);
				}
			});
			nameField.addActionListener(lis);
			ok.addActionListener(lis);
			cancel.addActionListener(lis);
			String s = "<HTML> To make a new shapefile from the new layer, enter<BR>" +
			"the new name you want for the layer and click OK.<BR>" +
			"You can then add it to the map in the usual way.<BR>"+
			"Click ENTER after replacing the text with your layer name";
			centerlabel.setHorizontalAlignment(JLabel.CENTER);
			centerlabel.setText(s);
			getContentPane().add(centerlabel,BorderLayout.CENTER);
			panel1.add(nameField);
			panel1.add(ok);
			panel1.add(cancel);
			getContentPane().add(panel1,BorderLayout.SOUTH);
		}
		catch(Exception ex)
		{System.out.println(ex);}
	}
}
class CreateXYShapeDialog extends JDialog  {
   String name = "";
  String path = "";
  JButton ok = new JButton("OK");
  JButton cancel = new JButton("Cancel");
  JTextField nameField = new JTextField("enter layer name here, then hit ENTER",35);
  JTextField pathField = new JTextField("enter full path name here, then hit ENTER",35);
  com.esri.mo2.map.dpy.FeatureLayer XYlayer;
  ActionListener lis = new ActionListener() {public void actionPerformed(ActionEvent ae) {
	Object o = ae.getSource();
	if (o == pathField) {
	  path = pathField.getText().trim();
	  System.out.println(path);
    }
    else if (o == nameField) {
	  name = nameField.getText().trim();//this works
	  //path = ((ShapefileFolder)(QuickStartXY3.layer4.getLayerSource())).getPath();
	  System.out.println(path+"    " + name);
    }
	else if (o == cancel)
      setVisible(false);
	else {  // ok button clicked
	  try {
		name = nameField.getText().trim();
		path = pathField.getText().trim();
		ShapefileWriter.writeFeatureLayer(XYlayer,path,name,0);
		// the following hard-coded line worked with data.csv
		//ShapefileWriter.writeFeatureLayer(XYlayer,"C:\\esri\\moj20\\shapefile","aeroportals",0);
	  } catch(Exception e) {System.out.println(e+"write error");}
	  setVisible(false);
    }
  }};
  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JLabel centerlabel = new JLabel();
  //centerlabel;
  CreateXYShapeDialog (com.esri.mo2.map.dpy.FeatureLayer layer5) {
	XYlayer = layer5;
    setBounds(40,250,600,300);
    setTitle("Create new shapefile?");
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
	    setVisible(false);
	  }
    });
    nameField.addActionListener(lis);
    pathField.addActionListener(lis);
    ok.addActionListener(lis);
    cancel.addActionListener(lis);
    String s = "<HTML> To make a new shapefile from the new layer, enter<BR>" +
      "the new name you want for the layer and hit ENTER.<BR>" +
      "then enter a path to the folder you want to use <BR>" +
      "and hit ENTER once again <BR>" +
      "You can then add it to the map in the usual way.<BR>"+
      "Click ENTER after replacing the text with your layer name";
    centerlabel.setHorizontalAlignment(JLabel.CENTER);
    centerlabel.setText(s);
    //getContentPane().add(centerlabel,BorderLayout.CENTER);
    panel1.add(centerlabel);
    panel1.add(nameField);
    panel1.add(pathField);
    panel2.add(ok);
    panel2.add(cancel);
    getContentPane().add(panel2,BorderLayout.SOUTH);
    getContentPane().add(panel1,BorderLayout.CENTER);
  }
}
class HelpDialog extends JDialog {
  JTextArea helptextarea;
  public HelpDialog(String inputText) throws IOException {
	setBounds(70,70,460,250);
  	setTitle("Help");
  	addWindowListener(new WindowAdapter() {
  	  public void windowClosing(WindowEvent e) {
  	    setVisible(false);
  	  }
    });
  	helptextarea = new JTextArea(inputText,7,40);
  	JScrollPane scrollpane = new JScrollPane(helptextarea);
    helptextarea.setEditable(false);
    getContentPane().add(scrollpane,"Center");
  }
}
class HelpTool extends Tool {
}
class Arrow extends Tool {
  public void arrowChores() { // undo measure tool residue
    Lab3.milesLabel.setText("DIST   0 mi   ");
    Lab3.kmLabel.setText("   0 km    ");
    if (Lab3.acetLayer != null)
      Lab3.map.remove(Lab3.acetLayer);
    Lab3.acetLayer = null;
    Lab3.map.repaint();
    Lab3.helpToolOn = false;
  }
}
class Flash extends Thread {
  Legend legend;
  Flash(Legend legendin) {
	legend = legendin;
  }
  public void run() {
	for (int i=0;i<12;i++) {
	  try {
		Thread.sleep(500);
		legend.toggleSelected();
	  } catch (Exception e) {}
    }
  }
}
class DistanceTool extends DragTool  {
  int startx,starty,endx,endy,currx,curry;
  com.esri.mo2.cs.geom.Point initPoint, endPoint, currPoint;
  double distance;
  public void mousePressed(MouseEvent me) {
	startx = me.getX(); starty = me.getY();
	initPoint = Lab3.map.transformPixelToWorld(me.getX(),me.getY());
  }
  public void mouseReleased(MouseEvent me) {
	  // now we create an acetatelayer instance and draw a line on it
	endx = me.getX(); endy = me.getY();
	endPoint = Lab3.map.transformPixelToWorld(me.getX(),me.getY());
    distance = (69.44 / (2*Math.PI)) * 360 * Math.acos(
				 Math.sin(initPoint.y * 2 * Math.PI / 360)
			   * Math.sin(endPoint.y * 2 * Math.PI / 360)
			   + Math.cos(initPoint.y * 2 * Math.PI / 360)
			   * Math.cos(endPoint.y * 2 * Math.PI / 360)
			   * (Math.abs(initPoint.x - endPoint.x) < 180 ?
                    Math.cos((initPoint.x - endPoint.x)*2*Math.PI/360):
                    Math.cos((360 - Math.abs(initPoint.x - endPoint.x))*2*Math.PI/360)));
    System.out.println( distance  );
    Lab3.milesLabel.setText("DIST: " + new Float((float)distance).toString() + " mi  ");
    Lab3.kmLabel.setText(new Float((float)(distance*1.6093)).toString() + " km");
    if (Lab3.acetLayer != null)
      Lab3.map.remove(Lab3.acetLayer);
    Lab3.acetLayer = new AcetateLayer() {
      public void paintComponent(java.awt.Graphics g) {
		java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
		Line2D.Double line = new Line2D.Double(startx,starty,endx,endy);
		g2d.setColor(new Color(0,0,250));
		g2d.draw(line);
      }
    };
    Graphics g = super.getGraphics();
    Lab3.map.add(Lab3.acetLayer);
    Lab3.map.redraw();
  }
  public void cancel() {};
}

class HotPickException extends Exception {
    public HotPickException(String message) {
        super(message);
    }
}

class LaunchBrowser {
	public static void launchPreferredBrowser(String url) {
	try {
		if(Desktop.isDesktopSupported())
		{
		  Desktop.getDesktop().browse(new URI(url));
		}
	}catch (Exception ex) {System.out.println(ex);};
  }
}


class HotPick extends JDialog {
  String mystate = Lab3.mystate;
  String mywinery = null;
  String mywinerypic = null;
  String website = null;
  JPanel jpanel = new JPanel();
  JPanel jpanel2 = new JPanel();
  String[][] statewinery={{"Palos Verdes","Castle Rock Winery","C:\\esri\\MOJ20\\Lab3\\wineries\\castle_rock.jpg","http://www.castlerockwinery.com/"},{"Oxnard","Herzog Wine Cellars","C:\\esri\\MOJ20\\Lab3\\wineries\\Herzog.jpg","http://herzogwinecellars.com/"},{"Murrieta","Carter Estate Winery resort","C:\\esri\\MOJ20\\Lab3\\wineries\\Carter Estate Winery.jpg","http://www.carterestatewinery.com/"},{"Ojai-Mira Monte","The Ojai Vineyard","C:\\esri\\MOJ20\\Lab3\\wineries\\The Ojai Vineyard Tasting Room.jpg","http://www.ojaivineyard.com/" },{"Ontario","Joseph Filippi Winery","C:\\esri\\MOJ20\\Lab3\\wineries\\josephfilipi.png", "https://www.josephfilippiwinery.com/"},{"Riverside","Canyon Crest Winery","C:\\esri\\MOJ20\\Lab3\\wineries\\canyon.jpg","http://canyoncrestwinery.com/"},{"San Diego","Vinavanti Urban Winery","C:\\esri\\MOJ20\\Lab3\\wineries\\Vinavanti Urban Winery.jpg","http://www.vinavantiurbanwinery.com/"},{"San Luis Obispo","Stephen Ross Winery Cellar","C:\\esri\\MOJ20\\Lab3\\wineries\\Stephen Ross Wine Cellars.jpg","http://www.stephenrosswine.com/"},{"Santa Barbara","Santa Barbara Winery","C:\\esri\\MOJ20\\Lab3\\wineries\\Santa Barbara Winery.jpg", "http://www.sbwinery.com/"},{"Central Coast","Orange Coast Winery","C:\\esri\\MOJ20\\Lab3\\wineries\\Orange Coast Winery.jpg","https://www.orangecoastwinery.com/"},{"Ventura","Four Brix Winery","C:\\esri\\MOJ20\\Lab3\\wineries\\fourbrix.jpg","https://fourbrixwine.com/"},{"Jamul","Maness Vineyards","C:\\esri\\MOJ20\\Lab3\\wineries\\maness.jpg","http://manessvineyards.com/"},{"Tehachapi","Souza Family Vineyard","C:\\esri\\MOJ20\\Lab3\\wineries\\souza.jpg","http://www.souzafamilyvineyard.com/"},{"Bakersfield","Giumarra Vineyard","C:\\esri\\MOJ20\\Lab3\\wineries\\Giumarra Vineyards Corporation.jpg","http://www.giumarra.com/"},{"Los Angeles","Cobblestone Vineyard","C:\\esri\\MOJ20\\Lab3\\wineries\\Cobblestone Vineyards.jpg","http://www.cobblestonewine.com/"}};
  HotPick() throws IOException {
        
    setBounds(350,350,500,350);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
            setVisible(false);
          }
    });
    for (int i = 0;i<51;i++)  {
          if (statewinery[i][0].equals(mystate)) {
        mywinery = statewinery[i][1];
            mywinerypic = statewinery[i][2];
            website = statewinery[i][3];
            break;
          }
    }
    setTitle(mywinery);
    JLabel label = new JLabel(mystate+":   ");
    JLabel label2 = new JLabel(mywinery);
    JLabel label3 = new JLabel(website);
    ImageIcon wineryIcon = new ImageIcon(mywinerypic);
    JLabel wineryLabel = new JLabel(wineryIcon);
    jpanel2.add(wineryLabel);
    jpanel.add(label);
    jpanel.add(label2);
    jpanel.add(label3);
    getContentPane().add(jpanel2,BorderLayout.CENTER);
    getContentPane().add(jpanel,BorderLayout.SOUTH);
  }
}

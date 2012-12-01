package de.mosgrid.chemdoodle.widget.client.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * client-side component for the MoleculeCanvas
 * 
 * @author Andreas Zink
 * 
 */
public class VMoleculeCanvas extends Widget implements Paintable {
	/* Set the CSS class name to allow styling. */
	public static final String CLASSNAME = "v-moleculecanvas";
	/* Timeout before molecule is updated in order to give other UI updates a chance */
	private static final int DEF_TIMEOUT = 100;
	// instance counter
	private static long instanceCounter = 0;

	/* instance variables */
	private long canvasID;
	private JavaScriptObject chemdoodleObject;
	protected String paintableId;
	protected ApplicationConnection client;
	private boolean isInitialized = false;
	private boolean firstInitRequestSent = false;
	// child elements
	private Element divElement;
	private Element msgElement;
	private Element canvasElement;
	// webgl support
	private boolean canvasSupport;
	private boolean webGlSupport;
	// canvas
	private int canvasWidth, canvasHeight;
	private String bgColor;
	// molecule
	private String moleculeDef;
	private String defType;
	// visualization
	private String repMode;
	private boolean cartoonStyle;
	private boolean showWater;
	private boolean useStars;
	private boolean showBackbone;
	private boolean hideRibbons;
	private boolean showNonPolymers;
	private boolean colorByChain;
	private boolean colorByResidue;

	public VMoleculeCanvas() {
		// check html5 <canvas> and WebGl support
		this.canvasSupport = checkCanvasSupport();
		this.webGlSupport = checkWebGlSupport();

		if (canvasSupport && webGlSupport) {
			// create molecule canvas
			createCanvasElement();
		} else {
			// show no-support information
			createNoSupportMsg();
		}
		setStyleName(CLASSNAME);
	}

	/**
	 * Creates a html5 canvas element as it can't be injected by JavaScript dynamically
	 */
	private void createCanvasElement() {
		canvasElement = DOM.createElement("canvas");
		if (instanceCounter == Long.MAX_VALUE) {
			instanceCounter = 0;
		}
		canvasID = instanceCounter++;

		canvasElement.setAttribute("id", "molCanvas"+canvasID);
		setElement(canvasElement);
	}

	/**
	 * Creates a div element which contains a 'no support' message
	 */
	private void createNoSupportMsg() {
		divElement = DOM.createDiv();
		setElement(divElement);
		divElement.setAttribute("align", "center");

		msgElement = DOM.createElement("p");
		String msgStyle = "font-family:Helvetica, Verdana, Arial, Sans-serif; font-size: 16px; color:white; position: relative; ";
		msgElement.setAttribute("style", msgStyle);

		String msg = "<br>Please update your browser and check your installation at<br><i>http://web.chemdoodle.com/about</i>";
		if (!canvasSupport) {
			msgElement.setInnerHTML("<b>Your browser does not support HTML5!</b>" + msg);
		} else if (!webGlSupport) {
			msgElement.setInnerHTML("<b>Your browser does not support WebGL!</b>" + msg);
		}
	}

	/**
	 * Called whenever an update is received from the server
	 */
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		if (client.updateComponent(this, uidl, true)) {
			// no changes, no update
			return;
		}

		this.client = client;
		this.paintableId = uidl.getId();

		if (canvasSupport && webGlSupport) {// if html5 and WebGl is fully supported
			if (!this.isInitialized) { // initialize first
				if (!this.firstInitRequestSent) {
					client.updateVariable(paintableId, Constants.INIT, true, true);
					this.firstInitRequestSent = true;
				}
				initialUpdate(uidl);
			} else { // update
				regularUpdate(uidl);
			}
		} else {
			updateNoSupportMsg(uidl, client);
		}
	}

	/**
	 * Helper method which places the 'no-support' msg to the correct location
	 */
	private void updateNoSupportMsg(UIDL uidl, ApplicationConnection client) {
		if (uidl.hasAttribute(Constants.CANVAS_HEIGHT) || uidl.hasAttribute(Constants.CANVAS_WIDTH)) {
			this.canvasHeight = (int) uidl.getFloatAttribute(Constants.CANVAS_HEIGHT);
			this.canvasWidth = (int) uidl.getFloatAttribute(Constants.CANVAS_WIDTH);
		}
		String oldStyle = msgElement.getAttribute("style");
		int top = (int) (canvasHeight / 2) - 50;
		msgElement.setAttribute("style", oldStyle + " top: " + top + "px;");
		divElement.appendChild(msgElement);

		// inform server side component
		client.updateVariable(paintableId, Constants.WEB_GL_SUPPORT, false, true);
	}

	/**
	 * Helper method for a regular update
	 */
	private void regularUpdate(UIDL uidl) {
		if (uidl.hasAttribute(Constants.MOLECULE_DEF)) {
			this.defType = uidl.getStringAttribute(Constants.DEF_TYPE);
			this.moleculeDef = uidl.getStringAttribute(Constants.MOLECULE_DEF);
			drawMolecule(DEF_TIMEOUT);
		}
		if (uidl.hasAttribute(Constants.REP_MODE)) {
			this.repMode = uidl.getStringAttribute(Constants.REP_MODE);
			update3DRepresentation(chemdoodleObject,repMode);
		}
		if (uidl.hasAttribute(Constants.BG_COLOR)) {
			this.bgColor = uidl.getStringAttribute(Constants.BG_COLOR);
			updateBackgroundColor(chemdoodleObject,bgColor);
		}
		if (uidl.hasAttribute(Constants.CARTOON_STYLE)) {
			this.cartoonStyle = uidl.getBooleanAttribute(Constants.CARTOON_STYLE);
			updateCartoonStyle(chemdoodleObject,cartoonStyle);
		}
		if (uidl.hasAttribute(Constants.SHOW_WATER)) {
			this.showWater = uidl.getBooleanAttribute(Constants.SHOW_WATER);
			updateShowWater(chemdoodleObject,showWater);
		}
		if (uidl.hasAttribute(Constants.STARS)) {
			this.useStars = uidl.getBooleanAttribute(Constants.STARS);
			updateUseStars(chemdoodleObject,useStars);
		}
		if (uidl.hasAttribute(Constants.SHOW_BACKBONE)) {
			this.showBackbone = uidl.getBooleanAttribute(Constants.SHOW_BACKBONE);
			updateShowBackbone(chemdoodleObject,showBackbone);
		}
		if (uidl.hasAttribute(Constants.HIDE_RIBBONS)) {
			this.hideRibbons = uidl.getBooleanAttribute(Constants.HIDE_RIBBONS);
			updateHideRibbons(chemdoodleObject,hideRibbons);
		}
		if (uidl.hasAttribute(Constants.SHOW_NON_POLYMERS)) {
			this.showNonPolymers = uidl.getBooleanAttribute(Constants.SHOW_NON_POLYMERS);
			updateShowNonPolymers(chemdoodleObject,showNonPolymers);
		}
		if (uidl.hasAttribute(Constants.COLOR_BY_CHAIN)) {
			this.colorByChain = uidl.getBooleanAttribute(Constants.COLOR_BY_CHAIN);
			updateColorByChain(chemdoodleObject,colorByChain);
		}
		if (uidl.hasAttribute(Constants.COLOR_BY_RESIDUE)) {
			this.colorByResidue = uidl.getBooleanAttribute(Constants.COLOR_BY_RESIDUE);
			updateColorByResidue(chemdoodleObject,colorByResidue);
		}

		if (uidl.hasAttribute(Constants.CANVAS_HEIGHT) || uidl.hasAttribute(Constants.CANVAS_WIDTH)) {
			this.canvasHeight = (int) uidl.getFloatAttribute(Constants.CANVAS_HEIGHT);
			this.canvasWidth = (int) uidl.getFloatAttribute(Constants.CANVAS_WIDTH);
			canvasElement.setAttribute("width", String.valueOf(canvasWidth));
			canvasElement.setAttribute("height", String.valueOf(canvasHeight));
			updateCanvasSize(chemdoodleObject,canvasElement, canvasWidth, canvasHeight);
		}
	}

	/**
	 * Helper method for the first update call
	 */
	private void initialUpdate(UIDL uidl) {
		boolean canInit = true;
		// canvas
		if (uidl.hasAttribute(Constants.CANVAS_HEIGHT)) {
			this.canvasHeight = (int) uidl.getFloatAttribute(Constants.CANVAS_HEIGHT);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.CANVAS_WIDTH)) {
			this.canvasWidth = (int) uidl.getFloatAttribute(Constants.CANVAS_WIDTH);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.BG_COLOR)) {
			this.bgColor = uidl.getStringAttribute(Constants.BG_COLOR);
		} else {
			canInit = false;
		}
		// molecule
		if (uidl.hasAttribute(Constants.MOLECULE_DEF)) {
			this.moleculeDef = uidl.getStringAttribute(Constants.MOLECULE_DEF);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.DEF_TYPE)) {
			this.defType = uidl.getStringAttribute(Constants.DEF_TYPE);
		} else {
			canInit = false;
		}
		// visualization
		if (uidl.hasAttribute(Constants.REP_MODE)) {
			this.repMode = uidl.getStringAttribute(Constants.REP_MODE);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.CARTOON_STYLE)) {
			this.cartoonStyle = uidl.getBooleanAttribute(Constants.CARTOON_STYLE);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.SHOW_WATER)) {
			this.showWater = uidl.getBooleanAttribute(Constants.SHOW_WATER);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.STARS)) {
			this.useStars = uidl.getBooleanAttribute(Constants.STARS);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.SHOW_BACKBONE)) {
			this.showBackbone = uidl.getBooleanAttribute(Constants.SHOW_BACKBONE);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.HIDE_RIBBONS)) {
			this.hideRibbons = uidl.getBooleanAttribute(Constants.HIDE_RIBBONS);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.SHOW_NON_POLYMERS)) {
			this.showNonPolymers = uidl.getBooleanAttribute(Constants.SHOW_NON_POLYMERS);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.COLOR_BY_CHAIN)) {
			this.colorByChain = uidl.getBooleanAttribute(Constants.COLOR_BY_CHAIN);
		} else {
			canInit = false;
		}
		if (uidl.hasAttribute(Constants.COLOR_BY_RESIDUE)) {
			this.colorByResidue = uidl.getBooleanAttribute(Constants.COLOR_BY_RESIDUE);
		} else {
			canInit = false;
		}
		if (canInit) {
			// first set to true because code execution can take a while
			this.isInitialized = true;
			setupChemDoodle();
			this.chemdoodleObject=createMoleculeCanvas(canvasElement.getAttribute("id"),canvasWidth, canvasHeight, bgColor, showWater, repMode, cartoonStyle, colorByResidue,
					colorByChain, showNonPolymers, hideRibbons, showBackbone, useStars);
			drawMolecule(DEF_TIMEOUT);
		}
	}

	/**
	 * Helper method for drawing the molecule on canvas. Calls the correct drawing method, dependent on molecule format.
	 * 
	 * @param timeout
	 *            Delay before molecule is drawn to give UI changes a chance (e.g. loading messages). If timeout is
	 *            negative, no timeout is used.
	 */
	private void drawMolecule(int timeout) {
		if (moleculeDef != null && !moleculeDef.equals("")) {
			// switch definition type and paint molecule
			if (defType.equals("MOL")) {
				setMoleculeAsMOL(chemdoodleObject,moleculeDef, timeout);
			} else if (defType.equals("PDB")) {
				setMoleculeAsPDB(chemdoodleObject,moleculeDef, timeout);
			} else if (defType.equals("JSON")) {
				setMoleculeAsJSON(chemdoodleObject,moleculeDef, timeout);
			}
		}
	}

	public static native boolean checkCanvasSupport() /*-{
														return  $wnd.ChemDoodle.featureDetection.supports_canvas();
														}-*/;

	public static native boolean checkWebGlSupport() /*-{
														return  $wnd.ChemDoodle.featureDetection.supports_webgl();
														}-*/;

	public static native void setupChemDoodle() /*-{
												$wnd.ChemDoodle.default_backgroundColor = '#000000';
												}-*/;

	public static native JavaScriptObject createMoleculeCanvas(String canvasElementId,int width, int height, String color, boolean showWater,
			String repMode, boolean cartoonize, boolean colorByResidue, boolean colorByChain, boolean showNonPoly,
			boolean hideRibbons, boolean backbone, boolean stars) /*-{
																	var molCanvas = new $wnd.ChemDoodle.TransformCanvas3D(canvasElementId, width, height);
																	molCanvas.specs.backgroundColor = color;
																	molCanvas.specs.atoms_useJMOLColors = true;
																	molCanvas.specs.bonds_useJMOLColors = true;
																	
																	molCanvas.specs.macro_showWater=showWater;
																	molCanvas.specs.set3DRepresentation(repMode);
																	molCanvas.specs.proteins_ribbonCartoonize = cartoonize;
																	molCanvas.specs.proteins_useAminoColors = colorByResidue;
																	molCanvas.specs.macro_colorByChain = colorByChain;
																	molCanvas.specs.atoms_display = showNonPoly;
																	molCanvas.specs.bonds_display = showNonPoly;
																	molCanvas.specs.proteins_displayRibbon = !hideRibbons;
																	molCanvas.specs.macro_displayAtoms =hideRibbons;
																	molCanvas.specs.macro_displayBonds = hideRibbons;
																	molCanvas.specs.proteins_displayBackbone = backbone;
																	molCanvas.specs.atoms_nonBondedAsStars_3D = stars;
																	
																	 molCanvas.setupScene();
																	 molCanvas.repaint();
																	 return molCanvas
																	}-*/;

	public static native void updateBackgroundColor(JavaScriptObject molCanvas,String color)/*-{
																	 molCanvas.specs.backgroundColor = color;
																	 molCanvas.setupScene();
																	 molCanvas.repaint();
																	}-*/;

	private static native void updateCanvasSize(JavaScriptObject molCanvas,Element canvasElement, int width, int height)/*-{
																								var glContext = canvasElement.getContext("webgl");
																								if (!glContext){
																									glContext = canvasElement.getContext("experimental-webgl");
																								}
																								glContext.viewport(0, 0, width, height);
																								if( molCanvas.molecule!=null){
																									 molCanvas.center();
																								}
																								 molCanvas.setupScene();
																								 molCanvas.repaint();
																								}-*/;

	public static native void updateShowWater(JavaScriptObject molCanvas,boolean showWater) /*-{
																	 molCanvas.specs.macro_showWater=showWater;
																	 molCanvas.repaint();
																	}-*/;

	public static native void update3DRepresentation(JavaScriptObject molCanvas,String rep) /*-{
																	 molCanvas.specs.set3DRepresentation(rep);
																	 molCanvas.repaint();
																	}-*/;

	public static native void setMoleculeAsPDB(JavaScriptObject molCanvas,String pdbDefinition, int timeout) /*-{
																					if(timeout>0){
																					$wnd.setTimeout(function() {  
																					var molecule = $wnd.ChemDoodle.readPDB(pdbDefinition);
																					 molCanvas.loadMolecule(molecule);
																					 molCanvas.repaint();
																					}, timeout);
																					} else {
																					var molecule = $wnd.ChemDoodle.readPDB(pdbDefinition);
																					 molCanvas.loadMolecule(molecule);
																					 molCanvas.repaint();
																					}
																					}-*/;

	public static native void setMoleculeAsMOL(JavaScriptObject molCanvas,String moleculeDefinition, int timeout) /*-{
																						if(timeout>0){
																						$wnd.setTimeout(function() {  
																						var molecule = $wnd.ChemDoodle.readMOL(moleculeDefinition, 1);
																						 molCanvas.loadMolecule(molecule);										
																						 molCanvas.repaint();
																						}, timeout);
																						} else {
																						var molecule = $wnd.ChemDoodle.readMOL(moleculeDefinition, 1);
																						 molCanvas.loadMolecule(molecule);										
																						 molCanvas.repaint();
																						}
																						}-*/;

	public static native void setMoleculeAsJSON(JavaScriptObject molCanvas,String moleculeDefinition, int timeout) /*-{			
																						if(timeout>0){
																						$wnd.setTimeout(function() {  
																						var json = moleculeDefinition;
																						var jsonObject =  eval('(' + moleculeDefinition + ')');
																						var molecule = $wnd.ChemDoodle.io.fromJSONPDB(jsonObject);
																						 molCanvas.loadMolecule(molecule);	
																						}, timeout);
																						} else {
																						var json = moleculeDefinition;
																						var jsonObject =  eval('(' + moleculeDefinition + ')');
																						var molecule = $wnd.ChemDoodle.io.fromJSONPDB(jsonObject);
																						 molCanvas.loadMolecule(molecule);
																						}
																						}-*/;

	public static native void updateCartoonStyle(JavaScriptObject molCanvas,boolean bool) /*-{
																 molCanvas.specs.proteins_ribbonCartoonize = bool;
																 molCanvas.repaint();
																}-*/;

	public static native void updateColorByResidue(JavaScriptObject molCanvas,boolean bool) /*-{
																	 molCanvas.specs.proteins_useAminoColors = bool;
																	 molCanvas.repaint();
																	}-*/;

	public static native void updateColorByChain(JavaScriptObject molCanvas,boolean bool) /*-{
																 molCanvas.specs.macro_colorByChain = bool;
																 molCanvas.repaint();
																}-*/;

	public static native void updateShowNonPolymers(JavaScriptObject molCanvas,boolean bool) /*-{
																	 molCanvas.specs.atoms_display = bool;
																	 molCanvas.specs.bonds_display = bool;
																	 molCanvas.repaint();
																	}-*/;

	public static native void updateHideRibbons(JavaScriptObject molCanvas,boolean bool) /*-{
																 molCanvas.specs.proteins_displayRibbon = !bool;
																 molCanvas.specs.macro_displayAtoms = bool;
																 molCanvas.specs.macro_displayBonds = bool;
																 molCanvas.repaint();
																}-*/;

	public static native void updateShowBackbone(JavaScriptObject molCanvas,boolean bool) /*-{
																 molCanvas.specs.proteins_displayBackbone = bool;
																 molCanvas.repaint();
																}-*/;

	public static native void updateUseStars(JavaScriptObject molCanvas,boolean bool) /*-{
															 molCanvas.specs.atoms_nonBondedAsStars_3D = bool;
															 molCanvas.repaint();
															}-*/;

}

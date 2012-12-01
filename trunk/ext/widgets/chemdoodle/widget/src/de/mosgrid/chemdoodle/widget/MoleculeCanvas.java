package de.mosgrid.chemdoodle.widget;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.chemdoodle.widget.client.ui.Constants;

/**
 * Server-Side component for the MoleculeCanvas. This canvas is a custom widget which can draw 3D molecules.
 * 
 * @author Andreas Zink
 * 
 */
@com.vaadin.ui.ClientWidget(de.mosgrid.chemdoodle.widget.client.ui.VMoleculeCanvas.class)
public class MoleculeCanvas extends AbstractComponent {
	private static final long serialVersionUID = 3326188260406022455L;

	/* constants */
	public static final float DEF_EDGE_LENGTH = 400f;
	private static final Notification NOTIF_LOADING = new Notification("Loading molecule...");
	/* instance variables */
	private List<ICanvasListener> listenerList = new ArrayList<ICanvasListener>();
	private boolean webGlSupport = true;
	private Set<String> dirtyAttributes = new HashSet<String>();

	// canvas
	private String moleculeDefinition = "";
	private DefType defType = DefType.MOL;
	private Color background = Color.BLACK;

	private RepMode representationMode = RepMode.BALL_AND_STICK;
	private boolean useCartoonStyle = false;
	private boolean useStars = false;
	private boolean showWater = false;
	private boolean showBackbone = false;
	private boolean hideRibbons = false;
	private boolean showNonPolymers = false;
	private boolean colorByChain = false;
	private boolean colorByResidue = false;

	public MoleculeCanvas() {
		init();
		setCanvasEdgeLength(DEF_EDGE_LENGTH);
	}

	public MoleculeCanvas(float canvasEdgeLength) {
		init();
		setCanvasEdgeLength(canvasEdgeLength);
	}

	public MoleculeCanvas(String moleculeDefinition, DefType defType) {
		init();
		this.moleculeDefinition = moleculeDefinition;
		this.defType = defType;
		setCanvasEdgeLength(DEF_EDGE_LENGTH);
	}

	public MoleculeCanvas(String moleculeDefinition, DefType defType, Color bgColor, float canvasEdgeLength) {
		init();
		this.moleculeDefinition = moleculeDefinition;
		this.defType = defType;
		this.background = bgColor;
		setCanvasEdgeLength(canvasEdgeLength);
	}

	private void init() {
		setImmediate(true);

		NOTIF_LOADING.setDelayMsec(250);
		// mark all attributes as dirty
		markAllAtributesDirty();
	}

	private void markAllAtributesDirty() {
		dirtyAttributes.add(Constants.BG_COLOR);
		dirtyAttributes.add(Constants.CANVAS_HEIGHT);
		dirtyAttributes.add(Constants.CANVAS_WIDTH);

		dirtyAttributes.add(Constants.MOLECULE_DEF);
		dirtyAttributes.add(Constants.DEF_TYPE);

		dirtyAttributes.add(Constants.CARTOON_STYLE);
		dirtyAttributes.add(Constants.COLOR_BY_CHAIN);
		dirtyAttributes.add(Constants.COLOR_BY_RESIDUE);
		dirtyAttributes.add(Constants.SHOW_NON_POLYMERS);
		dirtyAttributes.add(Constants.HIDE_RIBBONS);
		dirtyAttributes.add(Constants.REP_MODE);
		dirtyAttributes.add(Constants.SHOW_BACKBONE);
		dirtyAttributes.add(Constants.SHOW_WATER);
		dirtyAttributes.add(Constants.STARS);
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		// log.debug("Repainting molecule canvas.");
		super.paintContent(target);

		Set<String> dirtyAttributes = getDirtyAttributes();
		// canvas attributes
		if (dirtyAttributes.contains(Constants.CANVAS_WIDTH)) {
			target.addAttribute(Constants.CANVAS_WIDTH, getWidth());
		}
		if (dirtyAttributes.contains(Constants.CANVAS_HEIGHT)) {
			target.addAttribute(Constants.CANVAS_HEIGHT, getHeight());
		}
		if (dirtyAttributes.contains(Constants.BG_COLOR)) {
			target.addAttribute(Constants.BG_COLOR, transformToHex(getBackground()));
		}
		// molecule definition
		if (dirtyAttributes.contains(Constants.MOLECULE_DEF)) {
			target.addAttribute(Constants.MOLECULE_DEF, getMoleculeDefinition());
		}
		if (dirtyAttributes.contains(Constants.DEF_TYPE)) {
			target.addAttribute(Constants.DEF_TYPE, defType.toString());
		}
		// visualization
		if (dirtyAttributes.contains(Constants.REP_MODE)) {
			target.addAttribute(Constants.REP_MODE, getRepresentationMode().toString());
		}
		if (dirtyAttributes.contains(Constants.CARTOON_STYLE)) {
			target.addAttribute(Constants.CARTOON_STYLE, isUseCartoonStyle());
		}
		if (dirtyAttributes.contains(Constants.SHOW_WATER)) {
			target.addAttribute(Constants.SHOW_WATER, isShowWater());
		}
		if (dirtyAttributes.contains(Constants.STARS)) {
			target.addAttribute(Constants.STARS, isUseStars());
		}
		if (dirtyAttributes.contains(Constants.SHOW_BACKBONE)) {
			target.addAttribute(Constants.SHOW_BACKBONE, isShowBackbone());
		}
		if (dirtyAttributes.contains(Constants.HIDE_RIBBONS)) {
			target.addAttribute(Constants.HIDE_RIBBONS, isHideRibbons());
		}
		if (dirtyAttributes.contains(Constants.SHOW_NON_POLYMERS)) {
			target.addAttribute(Constants.SHOW_NON_POLYMERS, isShowNonPolymers());
		}
		if (dirtyAttributes.contains(Constants.COLOR_BY_CHAIN)) {
			target.addAttribute(Constants.COLOR_BY_CHAIN, isColorByChain());
		}
		if (dirtyAttributes.contains(Constants.COLOR_BY_RESIDUE)) {
			target.addAttribute(Constants.COLOR_BY_RESIDUE, isColorByResidue());
		}
	}

	/**
	 * Helper method which creates a copy of dirty attributes list
	 */
	private synchronized Set<String> getDirtyAttributes() {
		Set<String> dirtyAttributesCopy = this.dirtyAttributes;
		this.dirtyAttributes = new HashSet<String>();
		return dirtyAttributesCopy;
	}

	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);
		if (variables.containsKey(Constants.WEB_GL_SUPPORT)) {
			// missing webgl support detected
			this.webGlSupport = false;
			fireNoWebGlSupport();
		}
		if (variables.containsKey(Constants.INIT)) {
			markAllAtributesDirty();
			requestRepaint();
		}
	}

	/**
	 * Creates a javascript color string from Java Color. The color pink, becaus never used, will be treated as
	 * transparent.
	 */
	private String transformToHex(Color c) {
		String red = Integer.toHexString(c.getRed());
		String green = Integer.toHexString(c.getGreen());
		String blue = Integer.toHexString(c.getBlue());

		return "#" + extendByZero(red) + extendByZero(green) + extendByZero(blue);
	}

	private String extendByZero(String hexString) {
		if (hexString.length() == 1) {
			hexString += "0";
		}
		return hexString;
	}

	public boolean isWebGlSupport() {
		return webGlSupport;
	}

	/**
	 * Sets width and height of the canvas to given edgelength in pixels
	 */
	public void setCanvasEdgeLength(float edgeLength) {
		// log.debug("Setting canvas edge length to " + edgeLength);

		// requestRepaint is done in super.set* methods
		dirtyAttributes.add(Constants.CANVAS_WIDTH);
		setWidth(edgeLength, Sizeable.UNITS_PIXELS);
		dirtyAttributes.add(Constants.CANVAS_HEIGHT);
		setHeight(edgeLength, Sizeable.UNITS_PIXELS);
	}

	@Override
	public void setHeight(float height, int unit) {
		super.setHeight(height, unit);
		super.setWidth(height, unit);
		dirtyAttributes.add(Constants.CANVAS_HEIGHT);
		dirtyAttributes.add(Constants.CANVAS_WIDTH);
	}

	@Override
	public void setWidth(float width, int unit) {
		super.setWidth(width, unit);
		super.setHeight(width, unit);
		dirtyAttributes.add(Constants.CANVAS_HEIGHT);
		dirtyAttributes.add(Constants.CANVAS_WIDTH);
	}
	
//	@Override
//	public void setSizeFull() {
//		setCanvasEdgeLength(DEF_EDGE_LENGTH);
//	}
//
//	@Override
//	public void setSizeUndefined() {
//		setCanvasEdgeLength(DEF_EDGE_LENGTH);
//	}

//	@Override
//	public void setWidth(String width) {
//		setCanvasEdgeLength(DEF_EDGE_LENGTH);
//	}
//
//	@Override
//	public void setHeight(String height) {
//		setCanvasEdgeLength(DEF_EDGE_LENGTH);
//	}

	public boolean isShowWater() {
		return showWater;
	}

	public void setShowWater(boolean showWater) {
		// log.debug("Setting showWater=" + showWater);

		this.showWater = showWater;
		dirtyAttributes.add(Constants.SHOW_WATER);
		requestRepaint();
	}

	public void setBackground(Color bgColor) {
		// log.debug("Setting bg color to " + bgColor);
		this.background = bgColor;
		dirtyAttributes.add(Constants.BG_COLOR);
		requestRepaint();
	}

	public Color getBackground() {
		return background;
	}

	public String getMoleculeDefinition() {
		return moleculeDefinition;
	}

	public void setMoleculeDefinition(String moleculeDefinition, DefType defType) {
		// log.debug("Setting new molecule definition of type " + defType);
		if (this.getWindow() != null) {
			this.getWindow().showNotification(NOTIF_LOADING);
		}

		this.moleculeDefinition = moleculeDefinition;
		this.defType = defType;
		dirtyAttributes.add(Constants.MOLECULE_DEF);
		dirtyAttributes.add(Constants.DEF_TYPE);

		requestRepaint();
	}

	public void setMoleculeDefinition(InputStream is, DefType defType) {
		// log.debug("Setting new molecule definition with inputstream.");

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		setMoleculeDefinition(reader, defType);
	}

	public void setMoleculeDefinition(BufferedReader reader, DefType defType) {
		// log.debug("Setting new molecule definition with buffered reader.");

		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				if (defType != DefType.JSON) {
					stringBuilder.append("\n");
				}
			}
		} catch (IOException e) {
			// log.error("Could not read molecule file!", e);
		}

		setMoleculeDefinition(stringBuilder.toString(), defType);
	}

	public DefType getDefType() {
		return defType;
	}

	public RepMode getRepresentationMode() {
		return representationMode;
	}

	public void setRepresentationMode(RepMode representationMode) {
		// log.debug("Setting atom represention mode to " + representationMode);

		this.representationMode = representationMode;
		dirtyAttributes.add(Constants.REP_MODE);
		requestRepaint();
	}

	public boolean isUseCartoonStyle() {
		return useCartoonStyle;
	}

	public void setUseCartoonStyle(boolean useCartoonStyle) {
		// log.debug("Setting cartoonize to " + useCartoonStyle);

		this.useCartoonStyle = useCartoonStyle;
		dirtyAttributes.add(Constants.CARTOON_STYLE);
		requestRepaint();
	}

	public boolean isUseStars() {
		return useStars;
	}

	public void setUseStars(boolean useStars) {
		// log.debug("Setting useStars to " + useStars);

		this.useStars = useStars;
		dirtyAttributes.add(Constants.STARS);
		requestRepaint();
	}

	public boolean isShowBackbone() {
		return showBackbone;
	}

	public void setShowBackbone(boolean showBackbone) {
		// log.debug("Setting backbone to " + showBackbone);

		this.showBackbone = showBackbone;
		dirtyAttributes.add(Constants.SHOW_BACKBONE);
		requestRepaint();
	}

	public boolean isHideRibbons() {
		return hideRibbons;
	}

	public void setHideRibbons(boolean hideRibbons) {
		// log.debug("Setting hide ribbons to " + hideRibbons);

		this.hideRibbons = hideRibbons;
		dirtyAttributes.add(Constants.HIDE_RIBBONS);
		requestRepaint();
	}

	public boolean isShowNonPolymers() {
		return showNonPolymers;
	}

	public void setShowNonPolymers(boolean showNonPolymers) {
		// log.debug("Setting hide non polymers to " + hideNonPolymers);

		this.showNonPolymers = showNonPolymers;
		dirtyAttributes.add(Constants.SHOW_NON_POLYMERS);
		requestRepaint();
	}

	public boolean isColorByChain() {
		return colorByChain;
	}

	public void setColorByChain(boolean colorByChain) {
		// log.debug("Setting color by chain to " + colorByChain);

		this.colorByChain = colorByChain;
		dirtyAttributes.add(Constants.COLOR_BY_CHAIN);
		requestRepaint();
	}

	public boolean isColorByResidue() {
		return colorByResidue;
	}

	public void setColorByResidue(boolean colorByResidue) {
		// log.debug("Setting color by residue to " + colorByResidue);

		this.colorByResidue = colorByResidue;
		dirtyAttributes.add(Constants.COLOR_BY_RESIDUE);
		requestRepaint();
	}

	public void addCanvasListener(ICanvasListener l) {
		if (!listenerList.contains(l)) {
			this.listenerList.add(l);
		}
	}

	public void removeCanvasListener(ICanvasListener l) {
		this.listenerList.remove(l);
	}

	private void fireNoWebGlSupport() {
		for (ICanvasListener l : listenerList) {
			l.noWebGlSupportDetected();
		}
	}
}

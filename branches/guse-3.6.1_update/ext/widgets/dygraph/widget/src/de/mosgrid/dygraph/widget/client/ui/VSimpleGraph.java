package de.mosgrid.dygraph.widget.client.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ValueMap;

public class VSimpleGraph extends HTML implements Paintable {

	/** Set the CSS class name to allow styling. */
	public static final String CLASSNAME = "v-simplegraph";
	/* instance variables */
	private JavaScriptObject dygraphObject;
	private boolean isInitialized = false;
	private boolean initRequestSent = false;

	protected String paintableId;
	protected ApplicationConnection client;

	public VSimpleGraph() {
		super();
		setStyleName(CLASSNAME);
		getElement().setAttribute("id", "graphdiv");
		// getElement().setInnerHTML("No data loaded...");
	}

	/**
	 * Called whenever an update is received from the server
	 */
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		// This call should be made first.
		// It handles sizes, captions, tooltips, etc. automatically.
		if (client.updateComponent(this, uidl, true)) {
			// If client.updateComponent returns true there has been no changes and we
			// do not need to update anything.
			return;
		}

		this.client = client;
		this.paintableId = uidl.getId();

		boolean optionsAlreadyPainted = false;
		// if this is the first request inform server-side to mark all attributes as dirty
		if (!this.isInitialized) {
			if (!this.initRequestSent) {
				client.updateVariable(paintableId, Constants.INIT, true, true);
				this.initRequestSent = true;
			}
		}
		// only draw graph if data is available
		if (uidl.hasAttribute(Constants.VALUES)) {
			this.isInitialized = true;
			String data = uidl.getStringAttribute(Constants.VALUES);

			// must look like '{new_option1: value1, new_option2: value2}'
			StringBuilder optionBuilder = new StringBuilder("{yAxisLabelWidth: 80, maxNumberWidth: 6");
			if (uidl.hasAttribute(Constants.OPTIONS)) {
				ValueMap map = uidl.getMapAttribute(Constants.OPTIONS);
				for (String key : map.getKeySet()) {
					String value = map.getString(key);
					optionBuilder.append("," + key + ": " + value);
				}
				optionsAlreadyPainted = true;
			}
			optionBuilder.append("}");

			this.dygraphObject = createGraph(getElement(), data, optionBuilder.toString());
		}
		if (this.isInitialized) {
			if (uidl.hasAttribute("width") || uidl.hasAttribute("height")) {
				resizeGraph(dygraphObject);
			}
			if (uidl.hasAttribute(Constants.TITLE)) {
				String title = uidl.getStringAttribute(Constants.TITLE);
				updateGraphTitle(dygraphObject, title);
			}
			if (uidl.hasAttribute(Constants.X_LABEL)) {
				String xLabel = uidl.getStringAttribute(Constants.X_LABEL);
				updateXLabel(dygraphObject, xLabel);
			}
			if (uidl.hasAttribute(Constants.Y_LABEL)) {
				String yLabel = uidl.getStringAttribute(Constants.Y_LABEL);
				updateYLabel(dygraphObject, yLabel);
			}
			if (uidl.hasAttribute(Constants.OPTIONS) && !optionsAlreadyPainted) {
				ValueMap map = uidl.getMapAttribute(Constants.OPTIONS);
				StringBuilder optionBuilder = new StringBuilder("{");
				for (String key : map.getKeySet()) {
					String value = map.getString(key);
					optionBuilder.append(key + ": " + value + ", ");
				}
				// remove last comma
				optionBuilder.deleteCharAt(optionBuilder.lastIndexOf(","));
				optionBuilder.append("}");
				updateOptions(dygraphObject, optionBuilder.toString());
			}
			resizeGraph(dygraphObject);
		}
	}

	public static native JavaScriptObject createGraph(Element e, String data, String optionsAsString) /*-{
																										var jsonObject =  eval('(' + optionsAsString + ')');
																										var g = new $wnd.Dygraph(
																										e,
																										// keep this blank line!!
																										data,
																										jsonObject
																										);
																										return g;
																										}-*/;

	public static native void updateGraphTitle(JavaScriptObject g, String newTitle) /*-{
																					g.updateOptions({title: newTitle});
																					}-*/;

	public static native void updateXLabel(JavaScriptObject g, String newXLabel) /*-{
																					g.updateOptions({xlabel: newXLabel});
																					}-*/;

	public static native void updateYLabel(JavaScriptObject g, String newYLabel) /*-{
																					g.updateOptions({ylabel: newYLabel});
																					}-*/;

	public static native void updateLabels(JavaScriptObject g, String labelArray) /*-{
																					var jsonObject =  eval('(' + labelArray + ')');
																					g.updateOptions(jsonObject);
																					}-*/;

	public static native void updateOptions(JavaScriptObject g, String options) /*-{
																				var jsonObject =  eval('(' + options + ')');
																				g.updateOptions(jsonObject);
																				}-*/;

	public static native void resizeGraph(JavaScriptObject g) /*-{
																g.resize();
																}-*/;

}

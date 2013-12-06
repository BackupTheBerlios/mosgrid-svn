package de.mosgrid.dygraph.widget;

import java.util.Map;
import java.util.Set;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;

import de.mosgrid.dygraph.widget.client.ui.Constants;

/**
 * Simple xy graph
 */
@com.vaadin.ui.ClientWidget(de.mosgrid.dygraph.widget.client.ui.VSimpleGraph.class)
public class SimpleGraph extends AbstractComponent {
	/* constants */
	private static final long serialVersionUID = -6516630081958795999L;
	public static final int DEF_WIDTH = 500;
	public static final int DEF_HEIGHT = 320;
	/* instance variables */
	private IData data;

	public SimpleGraph() {
		init();
	}

	public SimpleGraph(IData data) {
		this.data = data;
		init();
	}

	private void init() {
		setImmediate(true);
		setWidth(DEF_WIDTH, UNITS_PIXELS);
		setHeight(DEF_HEIGHT, UNITS_PIXELS);
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);
		if (data != null) {
			Set<String> dirtyAttributes = data.getDirtyAttributes();

			if (dirtyAttributes.contains(Constants.VALUES) && data.getValues() != null) {
				target.addAttribute(Constants.VALUES, data.getValues());
			}
			if (dirtyAttributes.contains(Constants.TITLE) && data.getTitle() != null) {
				target.addAttribute(Constants.TITLE, data.getTitle());
			}
			if (dirtyAttributes.contains(Constants.X_LABEL) && data.getxLabel() != null) {
				target.addAttribute(Constants.X_LABEL, data.getxLabel());
			}
			if (dirtyAttributes.contains(Constants.Y_LABEL) && data.getyLabel() != null) {
				target.addAttribute(Constants.Y_LABEL, data.getyLabel());
			}
			if (dirtyAttributes.contains(Constants.OPTIONS) && data.getCustomOptions() != null) {
				target.addAttribute(Constants.OPTIONS, data.getCustomOptions());
			}
			data.clearDirtyAttributes();
		}
	}

	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);

		if (variables.containsKey(Constants.INIT)) {
			if (data != null) {
				data.markAllAttributesDirty();
				requestRepaint();
			}
		}
	}

	public IData getData() {
		return data;
	}

	public void setData(IData data) {
		this.data = data;
		requestRepaint();
	}

}

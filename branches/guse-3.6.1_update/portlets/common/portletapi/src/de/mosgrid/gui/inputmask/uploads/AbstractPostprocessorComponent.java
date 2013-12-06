package de.mosgrid.gui.inputmask.uploads;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * A simple implementation of IPostprocessorComponent. Should be used by most Postprocessors in order to get a common
 * look. This component is just a simple Panel withoud borders and margin at the bottom.
 * 
 * @author Andreas Zink
 * 
 */
public abstract class AbstractPostprocessorComponent extends CustomComponent implements IPostprocessorComponent {
	/* constants */
	private static final long serialVersionUID = -8659039340061713389L;
	public static final String CAPTION_PANEL = "Upload Postprocessing";

	/* ui components */
	private Panel mainPanel;

	public AbstractPostprocessorComponent() {
		buildMainPanel();
		setCompositionRoot(mainPanel);
	}

	private void buildMainPanel() {
		VerticalLayout panelLayout = new VerticalLayout();
		panelLayout.setSpacing(true);
		panelLayout.setImmediate(true);
		panelLayout.setMargin(false, false, true, false);
		mainPanel = new Panel(panelLayout);
		mainPanel.setStyleName(Reindeer.PANEL_LIGHT);
	}

	@Override
	public void setCaption(String caption) {
		mainPanel.setCaption(caption);
	}

	@Override
	public void addComponent(Component c) {
		mainPanel.addComponent(c);
	}

	/**
	 * Lets child classes set their individual content. Default content is a VerticalLayout
	 */
	public void setContent(ComponentContainer content) {
		mainPanel.setContent(content);
	}

}

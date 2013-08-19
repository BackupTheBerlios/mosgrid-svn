package de.mosgrid.gui.resultsheet;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

/**
 * Shall represent the finalization-element, i.e. the results, of a msml file
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultResultSheet extends CustomComponent implements IResultSheet {
	private static final long serialVersionUID = 1913595736727767714L;
	private VerticalLayout mainLayout;

	public DefaultResultSheet() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");
		mainLayout.setSpacing(true);
	}

	@Override
	public void addComponent(Component c) {
		mainLayout.addComponent(c);
	}

}

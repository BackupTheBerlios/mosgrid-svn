package de.mosgrid.gui.resultsheet;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 * This component shall contain all result-fields for one job
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultJobResultPanel extends CustomComponent {
	private static final long serialVersionUID = -2325909681544719426L;
	private Panel mainPanel;

	public DefaultJobResultPanel(String jobName) {
		buildMainPanel();
		mainPanel.setCaption(jobName);
		setCompositionRoot(mainPanel);
	}

	private void buildMainPanel() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainPanel = new Panel(mainLayout);
		mainPanel.setStyleName(Reindeer.PANEL_LIGHT);
		mainPanel.setWidth("100%");
		mainPanel.setHeight("-1px");
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");
		mainLayout.setSpacing(true);
	}

	@Override
	public void addComponent(Component c) {
		mainPanel.addComponent(c);
	}

}

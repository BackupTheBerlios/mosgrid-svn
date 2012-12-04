package de.mosgrid.gui.tabs;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.gui.panels.AbstractImportPanel;
import de.mosgrid.gui.panels.DefaultImportPanel;
import de.mosgrid.gui.panels.WelcomePanel;
import de.mosgrid.portlet.DomainPortlet;

/**
 * Tab for importing workflows. This is the entry point to the portlet for users, thus also contains the WelcomePanel.
 * The concrete import panel to be used may be set with setImportPanel(), otherwise the default implementation is used.
 * 
 * @author Andreas Zink
 * 
 */
public class ImportTab extends CustomComponent {
	private static final long serialVersionUID = -279300379179486643L;
	public final static String CAPTION = "Import";

	private DomainPortlet portlet;
	private VerticalLayout mainLayout;
	private WelcomePanel welcomePanel;
	private AbstractImportPanel importPanel;

	public ImportTab(DomainPortlet portlet) {
		this.portlet = portlet;
		initMainLayout();
		setCompositionRoot(mainLayout);
	}

	private void initMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		setWidth("100.0%");
		setHeight("-1px");

		buildWelcomePanel();
		setImportPanel(new DefaultImportPanel(portlet));
	}

	private void buildWelcomePanel() {
		String welcomeText = portlet.getWelcomeText();
		welcomePanel = new WelcomePanel(welcomeText);
		mainLayout.addComponent(welcomePanel);
	}

	public void setImportPanel(AbstractImportPanel importPanel) {
		if (this.importPanel != null) {
			mainLayout.removeComponent(this.importPanel);
		}
		this.importPanel = importPanel;
		mainLayout.addComponent(importPanel);
	}

	public WelcomePanel getWelcomePanel() {
		return welcomePanel;
	}

	public AbstractImportPanel getImportPanel() {
		return importPanel;
	}
}

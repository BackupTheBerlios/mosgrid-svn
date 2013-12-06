package de.mosgrid.gui;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.mosgrid.exceptions.ImportFailedException;
import de.mosgrid.exceptions.RemovingFailedException;
import de.mosgrid.exceptions.SubmissionFailedException;
import de.mosgrid.gui.tabs.AboutTab;
import de.mosgrid.gui.tabs.ImportTab;
import de.mosgrid.gui.tabs.SubmissionTab;
import de.mosgrid.gui.tabs.monitoring.MonitoringTab;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.portlet.listener.IImportListener;
import de.mosgrid.portlet.listener.IStatusMessageListener;
import de.mosgrid.portlet.listener.ISubmissionListener;
import de.mosgrid.util.IconProvider;
import de.mosgrid.util.IconProvider.ICONS;
import de.mosgrid.util.NotificationFactory;

/**
 * General layout for a domain specific portlet. Usually consists of a TabSheet containing the Import-, Submission-,
 * Monitoring- and About-Tab
 * 
 * @author Andreas Zink
 * 
 */
public class DomainPortletWindow extends Window implements IStatusMessageListener, IImportListener,
		ISubmissionListener, TabSheet.SelectedTabChangeListener {
	/* constants */
//	private final Logger LOGGER = LoggerFactory.getLogger(DomainPortletWindow.class);
	private static final long serialVersionUID = 2778528807980547511L;
	public static final String MESSAGE_NO_IMPORTS = "No imported workflow instances available. Please make some imports first.";
	public static final String CAPTION_INFO = "Info";
	public static final String CAPTION_HELP = "Help";
	public static final String CAPTION_ABOUT = "About";

	/* ui components */
	private DomainPortlet portlet;
	private VerticalLayout mainLayout;
	private TabSheet tabSheet;
	private HorizontalLayout buttonLayout;

	private ImportTab importTab;
	private SubmissionTab submissionTab;
	private MonitoringTab monitoringTab;
	private AboutTab aboutTab;

	// private Window helpWindow;
	private Window aboutWindow;

	public DomainPortletWindow(String windowTitle, DomainPortlet portlet) {
		super(windowTitle);
		this.portlet = portlet;
		init();
	}

	/**
	 * Initialization
	 */
	private void init() {
		buildMainLayout();
		setContent(mainLayout);

		portlet.addStatusMessageListener(this);
		portlet.addImportListener(this);
		portlet.addSubmissionListener(this);
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");

		setWidth("100%");
		setHeight("-1px");

		buildTabSheet();
		buildButtonContainer();
	}

	private void buildTabSheet() {
		// create tabs
		importTab = new ImportTab(portlet);
		submissionTab = new SubmissionTab(portlet);
		monitoringTab = new MonitoringTab(portlet);
		aboutTab = new AboutTab(portlet);

		// create tabsheet
		tabSheet = new TabSheet();
		tabSheet.setImmediate(true);
		tabSheet.setWidth("100%");
		tabSheet.setHeight("-1px");

		// add tabs to sheet
		tabSheet.addTab(importTab, ImportTab.CAPTION);
		tabSheet.addTab(submissionTab, SubmissionTab.CAPTION);
		tabSheet.addTab(monitoringTab, MonitoringTab.CAPTION);
		// tabSheet.addTab(aboutTab, AboutTab.CAPTION);

		// add tabsheet listeners
		tabSheet.addListener((TabSheet.SelectedTabChangeListener) this);

		mainLayout.addComponent(tabSheet);
	}

	private void buildButtonContainer() {
		buttonLayout = new HorizontalLayout();

		Button aboutButton = new Button();
		aboutButton.setStyleName("small");
		aboutButton.setIcon(IconProvider.getIcon(ICONS.USERS));
		aboutButton.setDescription(CAPTION_ABOUT);
		aboutButton.addListener(new ClickListener() {
			private static final long serialVersionUID = -2171537262906580792L;

			@Override
			public void buttonClick(ClickEvent event) {
				aboutButtonClicked(event);
			}
		});
		buttonLayout.addComponent(aboutButton);
		buttonLayout.setComponentAlignment(aboutButton, Alignment.MIDDLE_RIGHT);

		// work-around for having a link button
		Label helpButton = new Label("<div class=\"v-button v-button-small small\">" + "<span class=\"v-button-wrap\">"
				+ "<a href=\"" + portlet.getDomainId().getHelpUrl()
				+ "\"><img class=\"v-icon\" alt=\"\" src=\"/html/VAADIN/themes/runo/icons/16/help.png\"></a>"
				+ "<span class=\"v-button-caption\"></span>" + "</span></div>", Label.CONTENT_XHTML);
		helpButton.setWidth(null);

		// Button helpButton = new Button();
		// helpButton.setIcon(IconProvider.getIcon(ICONS.HELP));
		// helpButton.setStyleName("small");
		helpButton.setDescription(CAPTION_HELP);
		// helpButton.addListener(new ClickListener() {
		// private static final long serialVersionUID = 4114874885470746278L;
		//
		// @Override
		// public void buttonClick(ClickEvent event) {
		// helpButtonClicked(event);
		// }
		// });

		buttonLayout.addComponent(helpButton);
		buttonLayout.setComponentAlignment(helpButton, Alignment.MIDDLE_RIGHT);

		mainLayout.addComponent(buttonLayout);
		mainLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_RIGHT);
	}

	/**
	 * Helper method which gets called when 'about' button is clicked
	 */
	protected void aboutButtonClicked(ClickEvent event) {
		if (aboutWindow == null) {
			aboutWindow = new Window(CAPTION_ABOUT);
			AboutTab about = new AboutTab(portlet);
			aboutWindow.addComponent(about);
			aboutWindow.setWidth("50%");
			aboutWindow.setHeight("22%");
			addWindow(aboutWindow);
			aboutWindow.center();
		} else if (aboutWindow.getParent() == null) {
			addWindow(aboutWindow);
			aboutWindow.center();
		} else if (aboutWindow.getParent() != null) {
			removeWindow(aboutWindow);
		}
	}

	// /**
	// * Helper method which gets called when 'help' button is clicked
	// */
	// protected void helpButtonClicked(ClickEvent event) {
	// if (helpWindow == null || helpWindow.getParent() == null) {
	// HelpMessageProvider helpProvider = HelpMessageProvider.getInstance(portlet.getDomainId().getHelp());
	// helpWindow = new Window(CAPTION_HELP);
	// if (tabSheet.getSelectedTab() == importTab) {
	// helpWindow.addComponent(helpProvider.getImportHelp());
	// } else if (tabSheet.getSelectedTab() == submissionTab) {
	// helpWindow.addComponent(helpProvider.getSubmissionHelp());
	// } else if (tabSheet.getSelectedTab() == monitoringTab) {
	// helpWindow.addComponent(helpProvider.getMonitoringHelp());
	// } else {
	// Label emptyLabel = new Label("No help message available. You might browse the tutorials for answers.");
	// helpWindow.addComponent(emptyLabel);
	// }
	//
	// helpWindow.setWidth("50%");
	// helpWindow.setHeight("50%");
	// addWindow(helpWindow);
	// helpWindow.center();
	// } else if (helpWindow.getParent() != null) {
	// removeWindow(helpWindow);
	// }
	// }

	/**
	 * Bring given tab to front
	 */
	public void switchToTab(Component tab) {
		tabSheet.setSelectedTab(tab);
	}

	public DomainPortlet getPortlet() {
		return portlet;
	}

	public ImportTab getImportTab() {
		return importTab;
	}

	public SubmissionTab getSubmissionTab() {
		return submissionTab;
	}

	public MonitoringTab getMonitoringTab() {
		return monitoringTab;
	}

	public AboutTab getAboutTab() {
		return aboutTab;
	}

	public TabSheet getTabSheet() {
		return tabSheet;
	}

	@Override
	public void statusMessageChanged(String message, ThemeResource icon) {
		setStatusMessage(message, icon);
	}

	private void setStatusMessage(String message, ThemeResource icon) {
		Notification notif = NotificationFactory.createTrayNotification(CAPTION_INFO, message);
		// notif.setIcon(icon);
		showNotification(notif);
	}

	@Override
	public void selectedTabChange(SelectedTabChangeEvent event) {
		Component selectedComponent = tabSheet.getSelectedTab();
		if (selectedComponent == submissionTab) {
			if (portlet.getImportedWorkflowInstances().size() == 0) {
				// if user selects submission tab without available imports, show message
				setStatusMessage(MESSAGE_NO_IMPORTS, null);
			}
		} else if (selectedComponent == monitoringTab) {
			monitoringTab.update();
		}
	}

	@Override
	public void submissionSucceeded(ImportedWorkflow wkfImport) {
		submissionTab.update();
	}

	@Override
	public void removalSucceeded(ImportedWorkflow wkfImport) {
		submissionTab.update();
	}

	@Override
	public void importSucceeded(ImportedWorkflow wkfImport) {
		submissionTab.update();
	}

	@Override
	public void submissionFailed(ImportedWorkflow failedImport, SubmissionFailedException e) {
		submissionTab.update();

	}

	@Override
	public void removalFailed(ImportedWorkflow failedImport, RemovingFailedException e) {
		submissionTab.update();

	}

	@Override
	public void importFailed(MSMLTemplate failedImport, String userImportName, ImportFailedException e) {
		// TODO Auto-generated method stub

	}

}

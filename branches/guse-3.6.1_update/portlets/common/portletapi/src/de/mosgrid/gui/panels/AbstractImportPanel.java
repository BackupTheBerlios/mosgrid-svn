package de.mosgrid.gui.panels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.exceptions.ImportFailedException;
import de.mosgrid.gui.tabs.SubmissionTab;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportableWorkflow;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.portlet.listener.IImportListener;
import de.mosgrid.util.IconProvider;
import de.mosgrid.util.IconProvider.ICONS;
import de.mosgrid.util.NotificationFactory;

/**
 * Defines the minimum requirements of an import panel. These will maybe look slightly different for each domain.
 * 
 * @author Andreas Zink
 * 
 */
public abstract class AbstractImportPanel extends CustomComponent implements IImportListener {
	/* constants */
	private static final long serialVersionUID = 3205005696310353930L;
	private final Logger LOGGER = LoggerFactory.getLogger(AbstractImportPanel.class);

	public static final String CAPTION_PANEL = "Import a workflow";
	public static final String CAPTION_BUTTON_IMPORT = "Import";
	public static final String TOOLTIP_BUTTON_IMPORT_ = "Imports selected workflow under given name to your workflow repository.";
	public static final ThemeResource ICON_BUTTON_IMPORT = IconProvider.getIcon(ICONS.ARROW_RIGHT);

	/* instance variables */
	protected DomainPortlet portlet;
	/* ui components */
	private Panel mainPanel;
	private HorizontalLayout mainLayout;
	private VerticalLayout leftLayout;
	private VerticalLayout rightLayout;
	private HorizontalLayout buttonContainer;
	protected Button importButton;
	private ProgressIndicator importProcessIndicator;

	public AbstractImportPanel(DomainPortlet portlet) {
		this.portlet = portlet;
		portlet.addImportListener(this);
		buildMainPanel();
		setCompositionRoot(mainPanel);
	}

	private void buildMainPanel() {
		setWidth("100%");
		setHeight("-1px");
		setImmediate(true);

		mainLayout = new HorizontalLayout();
		mainLayout.setWidth("100.0%");
		mainLayout.setHeight("-1px");
		mainLayout.setSpacing(true);
		mainLayout.setImmediate(true);
		mainLayout.setMargin(true);

		mainPanel = new Panel(mainLayout);
		mainPanel.setCaption(CAPTION_PANEL);
		mainPanel.setWidth("100.0%");
		mainPanel.setHeight("-1px");
		mainPanel.setImmediate(true);

		buildLeftAndRightLayout();
	}

	private void buildLeftAndRightLayout() {
		leftLayout = new VerticalLayout();

		buildButtonContainer();
		mainLayout.addComponent(leftLayout);

		rightLayout = new VerticalLayout();
		mainLayout.addComponent(rightLayout);

		mainLayout.setExpandRatio(leftLayout, 0.3f);
		mainLayout.setExpandRatio(rightLayout, 0.7f);
	}

	private void buildButtonContainer() {
		buttonContainer = new HorizontalLayout();
		buttonContainer.setSpacing(true);
		buttonContainer.setMargin(true, false, false, false);

		buildImportButton();
		buildImportProcessIndicator();
		leftLayout.addComponent(buttonContainer);
	}

	private void buildImportProcessIndicator() {
		importProcessIndicator = new ProgressIndicator(new Float(0f));
		importProcessIndicator.setPollingInterval(250);
		resetProcessIndicator();
		buttonContainer.addComponent(importProcessIndicator);
		buttonContainer.setComponentAlignment(importProcessIndicator, Alignment.MIDDLE_LEFT);
	}

	private void buildImportButton() {
		importButton = new Button();
		importButton.setCaption(CAPTION_BUTTON_IMPORT);
		importButton.setIcon(ICON_BUTTON_IMPORT);
		importButton.setDescription(TOOLTIP_BUTTON_IMPORT_);
		importButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1254738923470234254L;

			@Override
			public void buttonClick(ClickEvent event) {
				importButtonClicked();
			}
		});
		buttonContainer.addComponent(importButton);
		buttonContainer.setComponentAlignment(importButton, Alignment.MIDDLE_LEFT);
	}

	/**
	 * Sets the content of the left side. Child classes shall override this method to set their content.
	 */
	protected void setLeftContent(ComponentContainer newContent) {
		leftLayout.addComponentAsFirst(newContent);
	}

	/**
	 * Sets the content of the right side. Child classes shall override this method to set their content.
	 */
	protected void setRightContent(ComponentContainer newContent) {
		rightLayout.addComponentAsFirst(newContent);
	}

	/**
	 * Gets called if the import button was pressed
	 */
	private void importButtonClicked() {
		// Catch any exception in order to prevent that exception is caught by submit button
		try {
			beforeImportHook();

			if (isValid()) {
				importButton.setEnabled(false);
				importProcessIndicator.setVisible(true);
				importProcessIndicator.setEnabled(true);

				// get selected wkf and given name
				ImportableWorkflow selectedWorkflow = getSelectedWorkflow();
				String importName = getImportName();

				// import wkf under given name
				portlet.importWorkflow(selectedWorkflow, importName, importProcessIndicator);
			} else {// if validation fails
				// create and show notifications
				// portlet.setStatusMessage(NotificationFactory.MSG_VALIDATIOIN_FAILED, PortletStatus.FAILED);

				Notification notif = NotificationFactory.createValidationFailedNotification();
				portlet.getMainWindow().showNotification(notif);
			}
		} catch (Exception e) {
			LOGGER.error(portlet.getUser() + " Error while importing workflow!", e);
			importFailed(null, getImportName(), new ImportFailedException(e.getMessage(), e));
		}
	}

	@Override
	public void importSucceeded(ImportedWorkflow wkfImport) {
		// create and show notification
		String msg = "Imported '" + wkfImport.getUserImportName() + "'." + NotificationFactory.BREAK_LINE
				+ "Your instance is available in the " + SubmissionTab.CAPTION + " tab now.";
		// Notification notif = NotificationFactory.createSucceededNotification(msg);
		// portlet.getMainWindow().showNotification(notif);
		portlet.setStatusMessage(msg, IconProvider.getIcon(ICONS.OK));
		importButton.setEnabled(true);
		resetProcessIndicator();
	}

	@Override
	public void importFailed(MSMLTemplate failedImport, String userImportName, ImportFailedException e) {
		// create and show notification
		String msg = "Failed to import '" + userImportName + "'." + NotificationFactory.BREAK_LINE + e.getMessage();
		Notification notif = NotificationFactory.createFailedNotification(msg);
		portlet.getMainWindow().showNotification(notif);

		importButton.setEnabled(true);
		resetProcessIndicator();
	}

	private void resetProcessIndicator() {
		importProcessIndicator.setValue(new Float(0f));
		importProcessIndicator.setVisible(false);
		importProcessIndicator.setEnabled(false);
	}

	/**
	 * Hook in import procedure. Gets called as first method after import button was clicked.
	 */
	protected abstract void beforeImportHook();

	// /**
	// * Creates a random import name with a timestemp
	// */
	// protected String createRandomImportName() {
	// return createRandomImportName(null);
	// }
	//
	// /**
	// * Creates a random import name with a time stamp together with the id
	// */
	// protected String createRandomImportName(MSMLTemplate template) {
	// String id = getDefaultTemplateName(template);
	// // create time stamp
	// Calendar rightNow = Calendar.getInstance();
	// rightNow.add(Calendar.HOUR, 1); // server is running GMT... sadly
	// SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy_HH:mm");
	// String timestamp = formatter.format(rightNow.getTime());
	//
	// return id + "_" + timestamp;
	// }
	//
	// /**
	// * Determines if the supplied value matches the default value.
	// *
	// * @param Value
	// * of the importfield.
	// * @return True if the supplied value matches "TEMPLATENAME_DATE_TIME"
	// */
	// protected boolean isDefaultName(String value, MSMLTemplate template) {
	// String id = getDefaultTemplateName(template);
	// String pattern = Pattern.quote(id) + "_\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d\\_\\d\\d:\\d\\d";
	// return Pattern.matches(pattern, value);
	// }

	// protected String getDefaultTemplateName(MSMLTemplate template) {
	// String id = "NewImport";
	// if (template != null && !"".equals(template.getName()))
	// id = template.getName();
	// return id;
	// }

	/**
	 * @return The user given import name
	 */
	public abstract String getImportName();

	/**
	 * @return The selected workflow
	 */
	public abstract ImportableWorkflow getSelectedWorkflow();

	/**
	 * @return The selected toolsuite i.e. dictionary or 'null' if no selection was made
	 */
	protected abstract IDictionary getSelectedToolSuite();

	/**
	 * @return 'true' if all selections are valid
	 */
	protected abstract boolean isValid();

}

package de.mosgrid.gui.inputmask;

import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;

import de.mosgrid.exceptions.RemovingFailedException;
import de.mosgrid.exceptions.SubmissionFailedException;
import de.mosgrid.gui.tabs.monitoring.MonitoringTab;
import de.mosgrid.msml.editors.MSMLTemplate;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.portlet.listener.ISubmissionListener;
import de.mosgrid.util.IconProvider;
import de.mosgrid.util.IconProvider.ICONS;
import de.mosgrid.util.NotificationFactory;
import de.mosgrid.util.UploadCollector;

/**
 * Abstract class for Input-Masks. Defines a footer with Submit and Remove Buttons. Also defines a component container
 * which has to be filled with components in sub-classes. All Input-Masks have to be extended from this class.
 * 
 * @author Andreas Zink
 * 
 */
public abstract class AbstractInputMask extends CustomComponent implements ISubmissionListener {
	/* constants */
	private static final long serialVersionUID = 7399398807416503242L;
	private final Logger LOGGER = LoggerFactory.getLogger(AbstractInputMask.class);

	public final static String CAPTION_BUTTON_SUBMIT = "Submit";
	public final static String CAPTION_BUTTON_REMOVE = "Cancel";
	public final static ThemeResource ICON_BUTTON_SUBMIT = IconProvider.getIcon(ICONS.ARROW_RIGHT);
	public final static ThemeResource ICON_BUTTON_REMOVE = IconProvider.getIcon(ICONS.CANCEL);
	public static final String TOOLTIP_BUTTON_SUBMIT = "Submits this workflow with given parameters.";
	public static final String TOOLTIP_BUTTON_REMOVE = "Deletes this workflow import from your local workflow repository.";
	public static final String FOOTNOTE_MANDATORY_FIELDS = "indicates mandatory input fields";

	/* instance variables */
	private DomainPortlet portlet;
	private ImportedWorkflow wkfImport;

	/* ui components */
	private VerticalLayout mainLayout;
	private ComponentContainer componentContainer;
	private HorizontalLayout footer;
	private Button submitButton;
	private Button removeButton;
	private ProgressIndicator progressIndicator;

	public AbstractInputMask(DomainPortlet portlet, ImportedWorkflow wkfImport) {
		this.portlet = portlet;
		this.portlet.addSubmissionListener(this);
		this.wkfImport = wkfImport;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setWidth("100%");
		mainLayout.setHeight("-1px");
		mainLayout.setSpacing(true);
		// mainLayout.setMargin(true);

		buildComponentContainer();
		buildFootnotes();
		buildButtonFooter();
	}

	/**
	 * Builds the main component container which is a VerticalLayout by default. Can be replaced.
	 */
	protected void buildComponentContainer() {
		VerticalLayout defaultLayout = new VerticalLayout();
		defaultLayout.setWidth("100%");
		defaultLayout.setHeight("-1px");
		defaultLayout.setSpacing(true);
		// defaultLayout.setMargin(true);
		componentContainer = defaultLayout;

		mainLayout.addComponent(componentContainer);
	}

	/**
	 * Builds the button footer
	 */
	protected void buildButtonFooter() {
		footer = new HorizontalLayout();
		footer.setWidth("100%");
		footer.setSpacing(true);

		HorizontalLayout submitContainer = new HorizontalLayout();
		submitContainer.setSpacing(true);
		// submit Button
		submitButton = new Button(CAPTION_BUTTON_SUBMIT);
		submitButton.setIcon(ICON_BUTTON_SUBMIT);
		submitButton.setDescription(TOOLTIP_BUTTON_SUBMIT);
		submitButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 3205600266051388291L;

			@Override
			public void buttonClick(ClickEvent event) {
				submitButtonClicked();
			}
		});
		submitContainer.addComponent(submitButton);
		submitContainer.setComponentAlignment(submitButton, Alignment.MIDDLE_LEFT);

		// progress indicator
		progressIndicator = new ProgressIndicator(new Float(0f));
		progressIndicator.setPollingInterval(250);
		resetProcessIndicator();
		submitContainer.addComponent(progressIndicator);
		submitContainer.setComponentAlignment(progressIndicator, Alignment.MIDDLE_LEFT);

		footer.addComponent(submitContainer);
		footer.setComponentAlignment(submitContainer, Alignment.MIDDLE_LEFT);

		// remove Button
		removeButton = new Button(CAPTION_BUTTON_REMOVE);
		removeButton.setIcon(ICON_BUTTON_REMOVE);
		removeButton.setDescription(TOOLTIP_BUTTON_REMOVE);
		removeButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7050743514806449001L;

			@Override
			public void buttonClick(ClickEvent event) {
				removeButtonClicked();
			}
		});
		footer.addComponent(removeButton);
		footer.setComponentAlignment(removeButton, Alignment.MIDDLE_RIGHT);

		mainLayout.addComponent(footer);
	}

	/**
	 * Builds all footnotes
	 */
	protected void buildFootnotes() {
		// footnote for mandatory fields which are labeled with a *
		Label footnote = new Label("<span class=\"v-required-field-indicator\">*</span> " + FOOTNOTE_MANDATORY_FIELDS,
				Label.CONTENT_XHTML);

		mainLayout.addComponent(footnote);
	}

	/**
	 * Gets called if submit button clicked
	 */
	private void submitButtonClicked() {
		// Catch any exception in order to prevent that exception is caught by submit button
		try {
			// TODO: maybe introduce BeforeSubmitException which handles exceptions on validation, after validation etc
			beforeSubmitHook();
			if (commitAndValidate()) {
				afterCommitAndValidateHook();
				setMainComponentsEnabled(false);
				setProgressIndicatorEnabled(true);

				UploadCollector collector = new UploadCollector();
				collectUploads(collector);
				// update progress
				progressIndicator.setValue(new Float(0.1));

				portlet.submitWorkflowImport(wkfImport, collector, progressIndicator);
			} else {// if validation fails
				// create and show notifications
				// portlet.setStatusMessage(NotificationFactory.MSG_VALIDATIOIN_FAILED, PortletStatus.FAILED);

				Notification notif = NotificationFactory.createValidationFailedNotification();
				portlet.getMainWindow().showNotification(notif);
			}
		} catch (EmptyValueException e) {
			// could happen while validation but should be caught in child classes
		} catch (InvalidValueException e) {
			// could happen while validation but should be caught in child classes
		} catch (Exception e) {
			// could happen while starting new thread etc.
			LOGGER.error(portlet.getUser() + " Error while submitting imported workflow!", e);
			submissionFailed(wkfImport, new SubmissionFailedException(e.getMessage(), e));
		}
	}

	/**
	 * Hook in submit procedure. Gets called as first method after submit button was clicked.
	 */
	protected abstract void beforeSubmitHook();

	/**
	 * Hook in submit procedure. Gets called after all input fields committed and validated their contents successfully.
	 */
	protected abstract void afterCommitAndValidateHook();

	@Override
	public void submissionSucceeded(ImportedWorkflow wkfImport) {
		if (this.wkfImport.equals(wkfImport)) {
			// create and show notification
			String msg = "Submitted '" + wkfImport.getUserImportName() + "'." + NotificationFactory.BREAK_LINE
					+ " Your instance is available in the " + MonitoringTab.CAPTION + " tab now.";
			// Notification notif = NotificationFactory.createSucceededNotification(msg);
			// portlet.getMainWindow().showNotification(notif);
			portlet.setStatusMessage(msg, IconProvider.getIcon(ICONS.OK));
			resetProcessIndicator();
		}
	}

	@Override
	public void submissionFailed(ImportedWorkflow failedImport, SubmissionFailedException e) {
		if (this.wkfImport.equals(failedImport)) {
			// create and show notification
			String msg = "Failed to submit '" + failedImport.getUserImportName() + "'."
					+ NotificationFactory.BREAK_LINE + e.getMessage();
			Notification notif = NotificationFactory.createFailedNotification(msg);
			portlet.getMainWindow().showNotification(notif);

			resetProcessIndicator();
		}
	}

	/**
	 * Commit user input to data sources (e.g. ScalarProperty) and validate
	 * 
	 * @return 'true' if all input is valid
	 */
	public abstract boolean commitAndValidate();

	/**
	 * Gets called directly before submission procedure of the Portlet is called. Shall collect all uploads.
	 */
	protected abstract void collectUploads(UploadCollector collector);

	/**
	 * Gets called if remove button clicked
	 */
	private void removeButtonClicked() {
		// Catch any exception in order to prevent that exception is caught by remove button
		try {
			beforeRemoveHook();

			setMainComponentsEnabled(false);
			setProgressIndicatorEnabled(true);

			portlet.removeWorkflowImport(wkfImport, progressIndicator);
		} catch (Exception e) {
			// could happen while starting new thread
			LOGGER.error(portlet.getUser() + " Error while removing imported workflow!", e);
			removalFailed(wkfImport, new RemovingFailedException(e.getMessage(), e));
		}
	}

	/**
	 * Hook in remove procedure. Gets called as first method after remove button was clicked.
	 */
	protected abstract void beforeRemoveHook();

	@Override
	public void removalSucceeded(ImportedWorkflow wkfImport) {
		if (this.wkfImport.equals(wkfImport)) {
			// create and show notification
			String msg = "Removed '" + wkfImport.getUserImportName() + "'.";
			// Notification notif = NotificationFactory.createSucceededNotification(msg);
			// portlet.getMainWindow().showNotification(notif);
			portlet.setStatusMessage(msg, IconProvider.getIcon(ICONS.OK));
			resetProcessIndicator();
		}
	}

	@Override
	public void removalFailed(ImportedWorkflow failedImport, RemovingFailedException e) {
		if (failedImport.equals(this.wkfImport)) {
			// create and show notification
			String msg = "Failed to remove '" + failedImport.getUserImportName() + "'."
					+ NotificationFactory.BREAK_LINE + e.getClass();
			Notification notif = NotificationFactory.createFailedNotification(msg);
			portlet.getMainWindow().showNotification(notif);

			resetProcessIndicator();
		}
	}

	@Override
	public void addComponent(Component c) {
		if (c != null) {
			this.componentContainer.addComponent(c);
		}
	}

	/**
	 * @return The main component container of this panel (VerticalLayout by default)
	 */
	public ComponentContainer getComponentContainer() {
		return componentContainer;
	}

	public void setComponentContainer(ComponentContainer newContainer) {
		if (newContainer != null) {
			mainLayout.removeComponent(componentContainer);
			mainLayout.addComponentAsFirst(newContainer);
			this.componentContainer = newContainer;
		}
	}

	public ComponentContainer getFooter() {
		return footer;
	}

	public DomainPortlet getPortlet() {
		return portlet;
	}

	public ASMWorkflow getWkfInstance() {
		return wkfImport.getAsmInstance();
	}

	public MSMLTemplate getTemplate() {
		return wkfImport.getTemplate();
	}

	protected Button getSubmitButton() {
		return submitButton;
	}

	protected Button getRemoveButton() {
		return removeButton;
	}

	/**
	 * Changes the endabled state of the most important components of the inputmaks. Note that we cannot disable
	 * complete input mask because we want to see the progress indicator
	 */
	public void setMainComponentsEnabled(boolean enabled) {
		componentContainer.setEnabled(enabled);
		submitButton.setEnabled(enabled);
		removeButton.setEnabled(enabled);
	}

	/**
	 * Changes the enabled state of the progress indicator
	 */
	public void setProgressIndicatorEnabled(boolean enabled) {
		progressIndicator.setVisible(enabled);
		progressIndicator.setEnabled(enabled);
	}

	/**
	 * Resets the progress indicator
	 */
	private void resetProcessIndicator() {
		progressIndicator.setValue(new Float(0f));
		setProgressIndicatorEnabled(false);
	}
}

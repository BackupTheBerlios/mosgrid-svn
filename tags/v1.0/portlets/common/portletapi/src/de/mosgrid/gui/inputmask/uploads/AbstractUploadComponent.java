package de.mosgrid.gui.inputmask.uploads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.Application;
import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.mosgrid.exceptions.PostprocessorCreationException;
import de.mosgrid.exceptions.PostprocessorException;
import de.mosgrid.exceptions.PostprocessorValidationException;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.converter.pdb.PDB2MSMLConverter;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.jaxb.bindings.MoleculeType;
import de.mosgrid.msml.jaxb.bindings.MoleculeUploadType;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobInitialization;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.UploadCollector;
import de.mosgrid.util.interfaces.IUploadPostprocessor;
import de.mosgrid.util.interfaces.IUploadPostprocessorFactory;

/**
 * Basic parent class for upload components. After Upload is done, the DomainPortlet is asked for a Postprocessor. The
 * Postprocessor may insert an additional ui component or create a postprocessed version of the uploaded file.
 * 
 * @author Andreas Zink
 * 
 */
/**
 * @author Andi
 * 
 */
public abstract class AbstractUploadComponent extends CustomComponent implements IUploadComponent {
	private static final long serialVersionUID = -8060450006154388757L;
	private final Logger LOGGER = LoggerFactory.getLogger(AbstractUploadComponent.class);

	public static final UserError ERROR_EMPTY_FIELD = new UserError("Must not be empty!");
	public static final String CAPTION_TEXTFIELD = "Filename";
	public static final String ERROR_UPLOAD_FAILED = "Failed to upload ";
	public static final String MESSAGE_UPLOAD_SUCCEEDED = "Successfully uploaded ";

	/* instance variables */
	protected ImportedWorkflow wkfImport;
	protected Job job;
	protected FileUpload msmlUploadElement;
	protected File uploadedFile;
	protected boolean uploadSucceeded;
	protected DomainPortlet portlet;
	protected List<IUploadListener> listenerList;

	private boolean initialized;

	/* ui components */
	protected VerticalLayout mainLayout;
	protected HorizontalLayout uploadLayout;
	protected IUploadPostprocessor postprocessor;
	protected IPostprocessorComponent postprocessorComponent;
	protected TextField fileNameField;
	protected ProgressIndicator uploadProgressIndicator;
	protected Button cancelButton;

	protected AbstractUploadComponent(ImportedWorkflow wkfImport, Job job, FileUpload msmlUploadElement) {
		super();
		this.wkfImport = wkfImport;
		this.job = job;
		this.msmlUploadElement = msmlUploadElement;
		this.listenerList = new ArrayList<IUploadListener>();
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	@Override
	public void attach() {
		if (!initialized) {
			// build rest of child components
			buildUploadComponent();
			buildProgressIndicator();
			buildCancelButton();
			super.attach();
			// init postprocessor
			this.portlet = getPortlet();
			if (portlet != null) {
				IUploadPostprocessorFactory ppFactory = portlet.getUploadPostprocessorFactory();
				if (ppFactory.knowsPostprocessor(wkfImport, job, msmlUploadElement)) {
					try {
						this.postprocessor = ppFactory.createPostprocessor(wkfImport, job, msmlUploadElement);
					} catch (PostprocessorCreationException e) {
						LOGGER.error(portlet.getUser()
								+ " Could not create postprocessor for upload component for job " + job.getId(), e);
					}
				}
			}
			initialized = true;
		} else {
			super.attach();
		}
	}

	private DomainPortlet getPortlet() {
		Application app = getApplication();
		if (app instanceof DomainPortlet) {
			return (DomainPortlet) app;
		}
		return null;
	}

	private void buildMainLayout() {
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(true);
		mainLayout.setSpacing(true);
		buildUploadLayout();
	}

	private void buildUploadLayout() {
		uploadLayout = new HorizontalLayout();
		uploadLayout.setImmediate(true);
		uploadLayout.setSpacing(true);

		buildTextField();

		mainLayout.addComponent(uploadLayout);
	}

	private void buildTextField() {
		fileNameField = new TextField(CAPTION_TEXTFIELD);
		fileNameField.setImmediate(true);
		// disable to prevent user input
		fileNameField.setEnabled(false);
		fileNameField.setRequired(true);

		uploadLayout.addComponent(fileNameField);
	}

	/**
	 * Shall build and add a component for uploading
	 */
	protected abstract void buildUploadComponent();

	private void buildProgressIndicator() {
		uploadProgressIndicator = new ProgressIndicator(new Float(0f));
		uploadProgressIndicator.setImmediate(true);
		uploadProgressIndicator.setEnabled(false);
		uploadProgressIndicator.setVisible(false);
		uploadProgressIndicator.setPollingInterval(100);

		uploadLayout.addComponent(uploadProgressIndicator);
		uploadLayout.setComponentAlignment(uploadProgressIndicator, Alignment.MIDDLE_LEFT);
	}

	private void buildCancelButton() {
		cancelButton = new Button("Cancel");
		cancelButton.setImmediate(true);
		cancelButton.setStyleName("small");
		cancelButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 8770914733716803466L;

			@Override
			public void buttonClick(ClickEvent event) {
				clickedUploadCancelButton();
			}
		});
		cancelButton.setVisible(false);
		uploadLayout.addComponent(cancelButton);
		uploadLayout.setComponentAlignment(cancelButton, Alignment.MIDDLE_LEFT);
	}

	/**
	 * Gets called when upload cancel button was clicked
	 */
	protected abstract void clickedUploadCancelButton();

	@Override
	public boolean isValid() {
		boolean isValid = true;
		try {
			fileNameField.validate();
		} catch (EmptyValueException e) {
			fileNameField.setComponentError(ERROR_EMPTY_FIELD);
			isValid = false;
		} catch (Exception e) {
			isValid = false;
		}
		if (postprocessor != null) {
			try {
				isValid &= postprocessor.isValidAndReady();
			} catch (PostprocessorValidationException e) {
				isValid = false;
			}
		}
		// valid if file type correct, upload succeeded and temp file was created
		return isValid && uploadSucceeded && (uploadedFile != null);
	}

	@Override
	public void startPostprocessing() throws PostprocessorException {
		if (postprocessor != null) {
			postprocessor.start();
		}
	}

	@Override
	public void collectUploads(UploadCollector collector) {
		String jobId = job.getId();
		if (msmlUploadElement.getJob() != null) {
			jobId = msmlUploadElement.getJob();
		}
		if (postprocessor != null) {
			// if postprocessor defined, delegate call
			// TODO: switch argument orderung in upload collector!
			postprocessor.collectUploads(collector, jobId, msmlUploadElement.getPort());
		} else {
			// add uploaded file
			collector.addUpload(uploadedFile, msmlUploadElement.getPort(), jobId);
		}
	}

	@Override
	public void doTemplateIntegrations(JobInitialization initialization) throws MSMLConversionException {
		if (postprocessor != null) {
			postprocessor.doTemplateIntegrations(initialization);
		} else {
			// is this a molecule upload?
			if (msmlUploadElement instanceof MoleculeUploadType) {
				if (msmlUploadElement.getFileType().equalsIgnoreCase("pdb")) {
					PDB2MSMLConverter converter = new PDB2MSMLConverter();
					try {
						MoleculeType moleculeElement = converter.convertFromStream(new FileInputStream(uploadedFile));
						initialization.setMolecule(moleculeElement);
					} catch (FileNotFoundException e) {
						throw new MSMLConversionException(e.getMessage(), e);
					}
				}// TODO: more filetypes

			}

		}
	}

	/**
	 * Helper which resets and disables the upload progress indicator
	 */
	protected void toggleProgressIndicator(boolean newState) {
		uploadProgressIndicator.setValue(0f);
		uploadProgressIndicator.setEnabled(newState);
		uploadProgressIndicator.setVisible(newState);
	}

	/**
	 * Helper which resets and disables the upload cancel button
	 */
	protected void toggleCancelButton(boolean newState) {
		cancelButton.setVisible(newState);
	}

	/**
	 * TODO: does not work. Helper which shall remove all component errors
	 */
	protected void removeAllComponentErrors() {
		fileNameField.setComponentError(null);
		// uploadComponent.removeAllComponentErrors();
	}

	@Override
	public File getUploadedFile() {
		return uploadedFile;
	}

	@Override
	public FileUpload getUploadElement() {
		return msmlUploadElement;
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (uploadedFile != null && uploadedFile.exists()) {
				LOGGER.info("Deleting temporary file " + uploadedFile + " during garbage collection...");
				uploadedFile.delete();
			}
		} catch (Exception e) {
			// nothing, just careful
		}
		super.finalize();
	}

	@Override
	public void addUploadListener(IUploadListener listener) {
		listenerList.add(listener);
	}

	@Override
	public void removeUploadListener(IUploadListener listener) {
		listenerList.remove(listener);
	}

}

package de.mosgrid.gui.inputmask;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Validator.EmptyValueException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Field;

import de.mosgrid.exceptions.PostprocessorException;
import de.mosgrid.gui.inputmask.uploads.IUploadComponent;
import de.mosgrid.msml.converter.exceptions.MSMLConversionException;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.util.UploadCollector;

/**
 * Default implementation of a Job-Form. Can be filled with upload-fields and parameter input-fields.
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultJobForm extends AbstractJobForm {
	/* constants */
	private static final long serialVersionUID = -3644129787410101002L;
	private final Logger LOGGER = LoggerFactory.getLogger(DefaultJobForm.class);
	public static final UserError ERROR_EMPTY = new UserError("Must not be empty!");

	/* instance variables */
	private Collection<IUploadComponent> uploadFields;
	private Collection<Field> inputFields;

	public DefaultJobForm(Job job) {
		super(job);
		init();
	}

	private void init() {
		uploadFields = new ArrayList<IUploadComponent>();
		inputFields = new ArrayList<Field>();
	}

	/**
	 * Adds a validatable upload field to this Job-Form
	 */
	public void addUploadField(IUploadComponent upload) {
		if (upload != null) {
			addComponent(upload);
			uploadFields.add(upload);
		}
	}

	/**
	 * Adds a validatable input field to this Job-Form
	 */
	public void addInputField(Field inputField) {
		if (inputField != null) {
			addComponent(inputField);
			inputFields.add(inputField);
		}
	}

	@Override
	public void beforeSubmit(AbstractInputMask parent) {
		// nothing to do by now
	}

	@Override
	public boolean commitAndValidate() {
		boolean isValid = true;

		for (IUploadComponent upload : uploadFields) {
			if (!upload.isValid()) {
				isValid = false;
			}
		}

		for (Field inputField : inputFields) {
			// remove older component errors first
			if (inputField instanceof AbstractComponent) {
				((AbstractComponent) inputField).setComponentError(null);
			}
			try {
				// Commit user input to data source
				inputField.commit();
			} catch (EmptyValueException e) {
				isValid = false;
				// set component error if empty is not allowed
				if (inputField instanceof AbstractComponent) {
					((AbstractComponent) inputField).setComponentError(ERROR_EMPTY);
				}
			} catch (InvalidValueException e) {
				// component error is set automatically
				isValid = false;
			}
		}

		return isValid;
	}

	@Override
	public void afterCommitAndValidate(AbstractInputMask defaultInputMask) {
		for (IUploadComponent upload : uploadFields) {
			try {
				upload.startPostprocessing();
				upload.doTemplateIntegrations(getJob().getInitialization());
			} catch (PostprocessorException e) {
				LOGGER.error(e.getMessage(),e);
			} catch (MSMLConversionException e) {
				LOGGER.error(e.getMessage(),e);
			}
		}
	}

	@Override
	public void beforeRemove(AbstractInputMask parent) {
		// nothing to do by now
	}

	/**
	 * Helper method which adds all uploaded files to the UploadCollector
	 */
	@Override
	public void collectUploads(UploadCollector collector) {
		for (IUploadComponent uploadField : uploadFields) {
			uploadField.collectUploads(collector);
		}
	}

}

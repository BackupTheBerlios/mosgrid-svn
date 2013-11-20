package de.mosgrid.util;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.vaadin.ui.Field;
import com.vaadin.ui.Label;

import de.mosgrid.exceptions.InputFieldCreationException;
import de.mosgrid.exceptions.InputMaskCreationException;
import de.mosgrid.gui.inputmask.uploads.IUploadComponent;
import de.mosgrid.msml.jaxb.bindings.EntryType;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.jaxb.bindings.JobInputUploadType;
import de.mosgrid.msml.jaxb.bindings.MoleculeUploadType;
import de.mosgrid.msml.jaxb.bindings.ParameterListType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.jaxb.bindings.PropertyListType;
import de.mosgrid.msml.jaxb.bindings.PropertyType;
import de.mosgrid.msml.jaxb.bindings.UploadList;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.msml.util.wrapper.JobEnvironment;
import de.mosgrid.msml.util.wrapper.JobInitialization;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.interfaces.IFieldFactory;
import de.mosgrid.util.interfaces.IInputMaskFactory;
import de.mosgrid.util.properties.ScalarProperty;

/**
 * General implementation of an input mask factory. Provides helper methods for creation of input fields.
 * 
 * @author Andreas Zink
 * 
 */
public abstract class AbstractInputMaskFactory implements IInputMaskFactory {
	private final Logger LOGGER = LoggerFactory.getLogger(AbstractInputMaskFactory.class);
	private IFieldFactory fieldFactory;

	public AbstractInputMaskFactory() {
		this.fieldFactory = new DefaultFieldFactory();
	}

	public AbstractInputMaskFactory(IFieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	public IFieldFactory getFieldFactory() {
		return fieldFactory;
	}

	public void setFieldFactory(IFieldFactory fieldFactory) {
		this.fieldFactory = fieldFactory;
	}

	/**
	 * Creates all input fields for parameters below the initialization element
	 * 
	 * @throws InputMaskCreationException
	 */
	protected Collection<Field> createInitInputFields(ImportedWorkflow wkfImport, Job job)
			throws InputFieldCreationException {
		LOGGER.trace("Creating input fields for " + job.getTitle());
		Collection<Field> inputFields = new ArrayList<Field>();

		// parse initialization element
		JobInitialization initElement = job.getInitialization();
		if (initElement == null)
			return inputFields;

		// parse input parameters
		ParameterListType paramListElement = initElement.getParamList();
		if (paramListElement == null)
			return inputFields;

		// iterate over editable parameters
		for (ParameterType parameter : paramListElement.getParameter()) {
			Field inputField = createInitInputField(wkfImport, job, parameter);
			if (inputField != null) {
				inputFields.add(inputField);
			}
		}
		return inputFields;
	}

	/**
	 * Creates an input field for given parameter
	 * 
	 * @throws InputMaskCreationException
	 */
	protected Field createInitInputField(ImportedWorkflow wkfImport, Job job, ParameterType parameter)
			throws InputFieldCreationException {
		Field inputField = null;
		if (parameter.isEditable() != null && parameter.isEditable()) {
			// check if param is optinal i.e. can be left empty
			boolean isOptional = false;
			if (parameter.isOptional() != null) {
				isOptional = parameter.isOptional();
			}

			if (parameter.getScalar() != null) {
				EntryType dictEntry = wkfImport.getTemplate().getDictEntry(parameter.getDictRef());
				if (dictEntry == null) {
					throw new InputFieldCreationException("Could not resolve dictionary entry "
							+ parameter.getDictRef());
				}

				ScalarProperty scalarProperty = new ScalarProperty(parameter.getScalar(), dictEntry, isOptional);
				inputField = fieldFactory.createScalarField(wkfImport, job, scalarProperty);

			} else if (parameter.getArray() != null) {
				// TODO maybe needed in future
			} else if (parameter.getMatrix() != null) {
				// TODO maybe needed in future
			}
		}
		return inputField;
	}

	/**
	 * Creates all upload fields
	 * 
	 * @throws InputMaskCreationException
	 */
	protected Collection<IUploadComponent> createUploadFields(ImportedWorkflow wkfImport, Job job)
			throws InputFieldCreationException {
		LOGGER.trace("Creating upload fields for " + job.getTitle());
		Collection<IUploadComponent> fieldList = new ArrayList<IUploadComponent>();

		// parse initialization element
		JobInitialization initElement = job.getInitialization();
		if (initElement == null) {
			LOGGER.info("No Initialization element for job " + job.getTitle() + ". This is not CML conform.");
			return fieldList;
		}

		// parse upload list
		UploadList uploadList = initElement.getUploadList();
		if (uploadList == null) {
			return fieldList;
		}
		if (uploadList.getMoleculeUpload() != null) {
			for (MoleculeUploadType fileUpload : uploadList.getMoleculeUpload()) {
				IUploadComponent uploadField = createUploadField(wkfImport, job, fileUpload);
				if (uploadField != null) {
					fieldList.add(uploadField);
				}
			}
		}
		if (uploadList.getJobInputUpload() != null) {
			for (JobInputUploadType fileUpload : uploadList.getJobInputUpload()) {
				IUploadComponent uploadField = createUploadField(wkfImport, job, fileUpload);
				if (uploadField != null) {
					fieldList.add(uploadField);
				}
			}
		}

		return fieldList;
	}

	/**
	 * Creates one upload field
	 * 
	 * @throws InputMaskCreationException
	 */
	protected IUploadComponent createUploadField(ImportedWorkflow wkfImport, Job job, FileUpload fileUpload)
			throws InputFieldCreationException {
		return fieldFactory.createUploadField(wkfImport, job, fileUpload);
	}

	/**
	 * Creates all input fields for parameters below the environment element (properties)
	 * 
	 * @throws InputMaskCreationException
	 */
	protected Collection<Field> createEnvironInputFields(ImportedWorkflow wkfImport, Job job)
			throws InputFieldCreationException {
		LOGGER.trace("Creating environment input fields for " + job.getTitle());
		Collection<Field> inputFields = new ArrayList<Field>();

		// parse environment element
		JobEnvironment environElement = job.getEnvironment();
		if (environElement == null)
			return inputFields;

		// parse environ properties
		PropertyListType propertyList = environElement.getProperties();
		if (propertyList == null)
			return inputFields;

		for (PropertyType property : propertyList.getProperty()) {
			Field inputField = createEnvironInputField(wkfImport, job, property);
			if (inputField != null) {
				inputFields.add(inputField);
			}
		}
		return inputFields;
	}

	/**
	 * Creates an input fields for a parameters below the environment element (properties)
	 * 
	 * @throws InputMaskCreationException
	 */
	protected Field createEnvironInputField(ImportedWorkflow wkfImport, Job job, PropertyType property)
			throws InputFieldCreationException {
		Field inputField = null;
		if (property.isEditable() != null && property.isEditable()) {
			if (property.getScalar() != null) {
				EntryType dictEntry = wkfImport.getTemplate().getDictEntry(property.getDictRef());
				if (dictEntry == null) {
					throw new InputFieldCreationException("Could not resolve dictionary entry " + property.getDictRef());
				}

				ScalarProperty scalarProperty = new ScalarProperty(property.getScalar(), dictEntry, false);
				inputField = fieldFactory.createScalarField(wkfImport, job, scalarProperty);

			} else if (property.getArray() != null) {
				// TODO maybe needed in future
			} else if (property.getMatrix() != null) {
				// TODO maybe needed in future
			}
		}

		return inputField;
	}

	/**
	 * Creates Labels for parameters which are not editable below the initialization element
	 * 
	 * @throws InputMaskCreationException
	 */
	protected Collection<Label> createLabelsForNotEditableParameters(ImportedWorkflow wkfImport, Job job)
			throws InputFieldCreationException {
		LOGGER.trace("Creating labels for not editable parameters for " + job.getTitle());
		Collection<Label> inputFields = new ArrayList<Label>();

		// parse initialization element
		JobInitialization initElement = job.getInitialization();
		if (initElement == null)
			return inputFields;

		// parse parameters
		ParameterListType paramListElement = initElement.getParamList();
		if (paramListElement == null)
			return inputFields;

		// iterate over editable parameters
		for (ParameterType parameter : paramListElement.getParameter()) {
			Label paramLabel = createLabelForNotEditableParameters(wkfImport, job, parameter);
			if (paramLabel != null) {
				inputFields.add(paramLabel);
			}
		}
		return inputFields;
	}

	/**
	 * Creates a label for a non-editable parameter in the form "TITLE = VALUE"
	 * 
	 * @throws InputFieldCreationException
	 */
	private Label createLabelForNotEditableParameters(ImportedWorkflow wkfImport, Job job, ParameterType parameter)
			throws InputFieldCreationException {
		Label label = null;
		if (parameter.isEditable() != null && parameter.isEditable() == false) {
			if (parameter.getScalar() != null) {
				EntryType dictEntry = wkfImport.getTemplate().getDictEntry(parameter.getDictRef());
				if (dictEntry == null) {
					throw new InputFieldCreationException("Could not resolve dictionary entry "
							+ parameter.getDictRef());
				}

				// parse tooltip
				StringBuilder tooltipBuilder = new StringBuilder();
				for (Element child : dictEntry.getDescription().getAny()) {
					tooltipBuilder.append(child.getTextContent());
				}

				String value = parameter.getScalar().getValue();
				if (value != null && value.trim().length() > 0) {
					label = new Label(dictEntry.getTitle() + " = " + value);
				} else {
					label = new Label(dictEntry.getTitle() + " is not set");
				}

				if (label != null) {
					label.setDescription(tooltipBuilder.toString());
				}

			}
		}
		return label;
	}

}

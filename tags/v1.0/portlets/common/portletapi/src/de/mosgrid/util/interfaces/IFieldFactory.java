package de.mosgrid.util.interfaces;

import com.vaadin.ui.Field;

import de.mosgrid.exceptions.InputFieldCreationException;
import de.mosgrid.gui.inputmask.uploads.IUploadComponent;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.properties.ScalarProperty;

/**
 * Defines general methods for field factories. A field factory shall create input fields for input masks.
 * 
 * @author Andreas Zink
 * 
 */
public interface IFieldFactory {

	/**
	 * Shall create an input field for given scalar property
	 * 
	 * @param wkfImport
	 *            The workflow import for which an input mask is created
	 * @param job
	 *            The parent job of the input field being created
	 * @param scalarProperty
	 *            The property source for the new input field
	 */
	Field createScalarField(ImportedWorkflow wkfImport, Job job, ScalarProperty scalarProperty)
			throws InputFieldCreationException;

	// TODO: creation of array and matrix fields if needed in future

	/**
	 * Shall create an validatable upload field for given file upload element
	 * 
	 * @param wkfImport
	 *            The workflow import for which an input mask is created
	 * @param job
	 *            The parent job of the input field being created
	 * @param uploadElement
	 *            The file upload cml element to be used
	 */
	IUploadComponent createUploadField(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement)
			throws InputFieldCreationException;

}

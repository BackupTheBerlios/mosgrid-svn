package de.mosgrid.util;

import java.util.ArrayList;
import java.util.List;

import de.mosgrid.exceptions.PostprocessorCreationException;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.interfaces.IUploadPostprocessor;
import de.mosgrid.util.interfaces.IUploadPostprocessorFactory;

/**
 * The default implementation of the UploadPostprocessorFactory. Allows to register several UploadPostprocessorFactories
 * which are then searched for postprocessors on create requests. The registered factories are searched in the order
 * they are added!
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultUploadPostprocessorFactory implements IUploadPostprocessorFactory {
	private List<IUploadPostprocessorFactory> factories = new ArrayList<IUploadPostprocessorFactory>();

	/**
	 * Adds a factory. At any request, the factories are searched in the order they are added! This means the first
	 * factory which defines a postprocessors for given arguments will be used.
	 */
	public void addFactory(IUploadPostprocessorFactory factory) {
		factories.add(factory);
	}

	public void removeFactory(IUploadPostprocessorFactory factory) {
		factories.remove(factory);
	}

	@Override
	public IUploadPostprocessor createPostprocessor(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement)
			throws PostprocessorCreationException {
		for (IUploadPostprocessorFactory factory : factories) {
			IUploadPostprocessor postprocessor = factory.createPostprocessor(wkfImport, job, uploadElement);
			if (postprocessor != null) {
				return postprocessor;
			}
		}
		return null;
	}

	@Override
	public boolean knowsPostprocessor(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement) {
		for (IUploadPostprocessorFactory factory : factories) {
			boolean isKnown = factory.knowsPostprocessor(wkfImport, job, uploadElement);
			if (isKnown) {
				return true;
			}
		}
		return false;
	}

}

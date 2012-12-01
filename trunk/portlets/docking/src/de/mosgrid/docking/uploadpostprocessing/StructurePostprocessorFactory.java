package de.mosgrid.docking.uploadpostprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mosgrid.exceptions.PostprocessorCreationException;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.util.interfaces.IUploadPostprocessor;
import de.mosgrid.util.interfaces.IUploadPostprocessorFactory;

public class StructurePostprocessorFactory implements IUploadPostprocessorFactory {

	/* known file types */
	public static final String PDB = "pdb";
	private static final List<String> knownFileTypes = new ArrayList<String>();
	static {
		knownFileTypes.add(PDB);
	}

	@Override
	public IUploadPostprocessor createPostprocessor(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement)
			throws PostprocessorCreationException {
		String filetype = uploadElement.getFileType();
		if (filetype != null) {
			filetype = filetype.toLowerCase();
			for (String knownFiletype : knownFileTypes) {
				if (filetype.endsWith(knownFiletype)) {
					return createPostprocessor(wkfImport, job, uploadElement, knownFiletype);
				}
			}
		}
		return null;
	}

	@Override
	public boolean knowsPostprocessor(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement) {
		String filetype = uploadElement.getFileType();
		if (filetype != null) {
			filetype = filetype.toLowerCase();
			for (String knownFiletype : knownFileTypes) {
				if (filetype.endsWith(knownFiletype)) {
					return true;
				}
			}
		}
		return false;
	}

	protected IUploadPostprocessor createPostprocessor(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement,
			String filetype) throws PostprocessorCreationException {

		if (filetype.equals(PDB)) {
			return new PDBPostprocessor(wkfImport.getTemplate(), job.getInitialization());
		}// .... else if
		return null;
	}

	/**
	 * @return A list of known file types which have a postprocessor assigned
	 */
	public static List<String> getKnownfiletypes() {
		return Collections.unmodifiableList(knownFileTypes);
	}

}

package de.mosgrid.md.uploadpostprocessing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.exceptions.PostprocessorCreationException;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.DomainId;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.md.uploadpostprocessing.PDBGroup;
import de.mosgrid.md.uploadpostprocessing.PDBPostprocessor;
import de.mosgrid.util.interfaces.IUploadPostprocessor;
import de.mosgrid.util.interfaces.IUploadPostprocessorFactory;

/**
 * A PostprocessorFactory for molecule file uploads.
 * 
 * @author Andreas Zink
 * 
 */
public class StructurePostprocessorFactory

implements IUploadPostprocessorFactory {
	@SuppressWarnings("unused")
	private final Logger LOGGER = LoggerFactory.getLogger(StructurePostprocessorFactory.class);

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
			if (wkfImport.getDomainId() == DomainId.MOLECULAR_DYNAMICS) {
				// in MD, only amino acids are allowed
				return new PDBPostprocessor(PDBGroup.AA);
			} else if(wkfImport.getDomainId()==DomainId.DOCKING) {
				//TODO: create new pp for docking
				return null;
			}
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

package de.mosgrid.remd.util;

import de.mosgrid.exceptions.InputFieldCreationException;
import de.mosgrid.gui.inputmask.uploads.DefaultUploadSelectionComponent;
import de.mosgrid.gui.inputmask.uploads.IUploadComponent;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.remd.properties.RemdProperties;
import de.mosgrid.remd.ui.upload.CustomUploadSelectionComponent;
import de.mosgrid.util.DefaultFieldFactory;

public class CustomFieldFactory extends DefaultFieldFactory {

	// the last upload component for pdb files for a REMD workflow
	private DefaultUploadSelectionComponent lastProteinUpload;

	@Override
	public IUploadComponent createUploadField(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement)
			throws InputFieldCreationException {
		// create special upload fields for some jobs

		if (job.getId().equals(RemdProperties.get(RemdProperties.ID_REMD_INPUT))) {
			if (uploadElement.getFileType().equals("pdb")) {
				String jobId = RemdProperties.get(RemdProperties.REMD_PREP_PDB_JOB_ID);
				String filename = RemdProperties.get(RemdProperties.REMD_PREP_PDB_FILE_NAME);

				lastProteinUpload = new CustomUploadSelectionComponent(wkfImport, job, uploadElement, jobId, filename);
				return lastProteinUpload;
			} else if (uploadElement.getFileType().equals("top")) {
				String jobId = RemdProperties.get(RemdProperties.REMD_PREP_TOPOL_JOB_ID);
				String filename = RemdProperties.get(RemdProperties.REMD_PREP_TOPOL_FILE_NAME);

				return new CustomUploadSelectionComponent(wkfImport, job, uploadElement, jobId, filename);
			} else if (uploadElement.getFileType().equals("itp")) {
				String jobId = RemdProperties.get(RemdProperties.REMD_PREP_TOPOL_JOB_ID);
				String filename = RemdProperties.get(RemdProperties.REMD_PREP_TOPOL_FILE_NAME);

				return new CustomUploadSelectionComponent(wkfImport, job, uploadElement, jobId, filename);
			}

		} else if (job.getId().equals(RemdProperties.get(RemdProperties.ID_REMD_PREP_INPUT))) {
			if (uploadElement.getFileType().equals("pdb")) {
				String jobId = RemdProperties.get(RemdProperties.PROT_PREP_JOB_ID);
				String filename = RemdProperties.get(RemdProperties.PROT_PREP_FILE_NAME);

				return new CustomUploadSelectionComponent(wkfImport, job, uploadElement, jobId, filename);
			}
		}

		return super.createUploadField(wkfImport, job, uploadElement);
	}

	public DefaultUploadSelectionComponent getLastProteinUpload() {
		return lastProteinUpload;
	}

}

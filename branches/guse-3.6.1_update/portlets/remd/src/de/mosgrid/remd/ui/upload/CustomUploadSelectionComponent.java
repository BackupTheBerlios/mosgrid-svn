package de.mosgrid.remd.ui.upload;

import java.lang.reflect.Constructor;

import de.mosgrid.gui.inputmask.uploads.DefaultUploadSelectionComponent;
import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;

/**
 * Custom upload selection which adds a auto search button for XFS uploads
 * 
 * @author Andreas Zink
 *
 */
public class CustomUploadSelectionComponent extends DefaultUploadSelectionComponent {
	private static final long serialVersionUID = -9030822117316148304L;
	
	private String _jobId;
	private String _filename;
	
	public CustomUploadSelectionComponent(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement, String jobId, String filename) {
		super(wkfImport, job, uploadElement);
		_jobId=jobId;
		_filename=filename;
	}
	
	@Override
	protected void createXfsUploadComponent(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement)
			throws SecurityException, NoSuchMethodException {
		Constructor<CustomXfsUploadComponent> c = CustomXfsUploadComponent.class.getConstructor(ImportedWorkflow.class, Job.class,
				FileUpload.class,SingleAutoSelectBrowser.class);
		Object[] args = { wkfImport, job, uploadElement,new SingleAutoSelectBrowser(_jobId, _filename) };
		
		addSelectable(SELECTION_XFS,c,args);
	}

}

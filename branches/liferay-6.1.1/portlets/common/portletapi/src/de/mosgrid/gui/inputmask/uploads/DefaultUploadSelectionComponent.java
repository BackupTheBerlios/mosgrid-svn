package de.mosgrid.gui.inputmask.uploads;

import java.lang.reflect.Constructor;

import de.mosgrid.msml.jaxb.bindings.FileUpload;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.ImportedWorkflow;

/**
 * Default implementation of the Upload selection. Allows selection from XFS or local. This component will be created by
 * DefaultInputmaskFactory.
 * 
 * @author Andreas Zink
 * 
 */
public class DefaultUploadSelectionComponent extends UploadSelectionComponent {
	private static final long serialVersionUID = -588731464931295602L;

	public static final String CAPTION_SELECTION = "File Upload:";
	public static final String TOOLTIP_SELECTION = "Please select the origin of your upload. You may choose a file from XTreemFS cloud storage or from your local computer.";

	public static final String SELECTION_XFS = "XtreemFS";
	public static final String SELECTION_LOCAL = "Local";
	public static final String SELECTION_METADATA = "Metadata Search (beta)";

	protected ImportedWorkflow _wkfImport;
	protected Job _job;
	protected FileUpload _uploadElement;

	public DefaultUploadSelectionComponent(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement) {
		super(CAPTION_SELECTION, TOOLTIP_SELECTION);
		_wkfImport = wkfImport;
		_job = job;
		_uploadElement = uploadElement;
	}

	@Override
	public void attach() {
		try {
			createLocalUploadComponent(_wkfImport, _job, _uploadElement);
			createXfsUploadComponent(_wkfImport, _job, _uploadElement);
			// TODO reactivate if done
			 createMetaUploadComponent(_wkfImport, _job, _uploadElement);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.attach();
	}

	protected void createLocalUploadComponent(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement)
			throws SecurityException, NoSuchMethodException {
		Constructor<LocalUploadComponent> c = LocalUploadComponent.class.getConstructor(ImportedWorkflow.class,
				Job.class, FileUpload.class);
		Object[] args = { wkfImport, job, uploadElement };

		addSelectable(SELECTION_LOCAL, c, args);
	}

	protected void createXfsUploadComponent(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement)
			throws SecurityException, NoSuchMethodException {
		Constructor<XfsUploadComponent> c = XfsUploadComponent.class.getConstructor(ImportedWorkflow.class, Job.class,
				FileUpload.class);
		Object[] args = { wkfImport, job, uploadElement };

		addSelectable(SELECTION_XFS, c, args);
	}

	protected void createMetaUploadComponent(ImportedWorkflow wkfImport, Job job, FileUpload uploadElement)
			throws SecurityException, NoSuchMethodException {
		Constructor<MetaDataSearchComponent> c = MetaDataSearchComponent.class.getConstructor(ImportedWorkflow.class,
				Job.class, FileUpload.class);
		Object[] args = { wkfImport, job, uploadElement };

		addSelectable(SELECTION_METADATA, c, args);
	}

}

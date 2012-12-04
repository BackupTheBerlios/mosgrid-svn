package de.mosgrid.remd.ui.upload;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Window.Notification;

import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.msml.jaxb.bindings.JobInputUploadType;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.remd.properties.RemdProperties;
import de.mosgrid.util.NotificationFactory;
import de.mosgrid.util.UploadCollector;
import de.mosgrid.util.XfsBridge;

public class DemuxAutoSelect extends MultiAutoSelect {
	private static final long serialVersionUID = -3844143775198832660L;
	private final Logger LOGGER = LoggerFactory.getLogger(DemuxAutoSelect.class);

	private boolean renamedSuccessful;

	public DemuxAutoSelect(DomainPortlet portlet, ImportedWorkflow wkfImport, Job job) {
		super(portlet, job, wkfImport, RemdProperties.get(RemdProperties.REMD_MAIN_JOB_ID));
	}

	@Override
	public void beforeSubmit(AbstractInputMask parent) {
		if (foundResultFiles() && !areFilesAlreadyRenamed()) {
			try {
				String baseReplicaName = RemdProperties.get(RemdProperties.REMD_DEFNM);
				String baseTprName = cut(RemdProperties.get(RemdProperties.REMD_TPR_NAME));
				renameAll(baseReplicaName, ".xtc");

				// rename first log
				portlet.getXfsBridge().rename(baseReplicaName + "0.log", cut(baseReplicaName) + "0.log",
						getAbsoluteResultPath());
				// rename first tpr
				portlet.getXfsBridge().rename(baseTprName + "0.tpr", cut(baseTprName) + "0.tpr",
						getAbsoluteResultPath());

				renamedSuccessful = true;
			} catch (Exception e) {
				LOGGER.error(portlet.getUser() + " Error while renaming REMD result files.", e);
				Notification notif = NotificationFactory.createErrorNotification("Error!",
						"Could not rename REMD result files.<br>" + e.getMessage());
				getWindow().showNotification(notif);
			}
		}
	}

	@Override
	public boolean commitAndValidate() {
		return foundResultFiles() && (renamedSuccessful);
	}

	@Override
	public void collectUploads(UploadCollector collector) {
		String baseTprName = cut(RemdProperties.get(RemdProperties.REMD_TPR_NAME));
		String baseReplicaName = cut(RemdProperties.get(RemdProperties.REMD_DEFNM));

		String basePath = getAbsoluteResultPath() + (getAbsoluteResultPath().endsWith("/") ? "" : "/");

		List<JobInputUploadType> uploadList = getJob().getInitialization().getUploadList().getJobInputUpload();
		for (JobInputUploadType upload : uploadList) {
			if (upload.getFileType().equals("tpr")) {
				String xfsURL = XfsBridge.createURL(basePath + cut(baseTprName) + "0.tpr");
				collector.addUploaded(xfsURL, upload.getPort(), upload.getJob());
			} else if (upload.getFileType().equals("log")) {
				String xfsURL = XfsBridge.createURL(basePath + cut(baseReplicaName) + "0.log");
				collector.addUploaded(xfsURL, upload.getPort(), upload.getJob());
			} else {
				String xfsURL = XfsBridge.createURL(basePath + baseReplicaName + "." + upload.getFileType());
				collector.addUploaded(xfsURL, upload.getPort(), upload.getJob());
			}
		}
	}

	@Override
	public void afterCommitAndValidate(AbstractInputMask parent) {
		List<JobInputUploadType> uploadList = getJob().getInitialization().getUploadList().getJobInputUpload();
		for (JobInputUploadType upload : uploadList) {
			if (upload.getFileType().equals("xtc")) {
				portlet.getAsmService().setNumberOfInputFiles(portlet.getUser().getUserID(),
						wkfImport.getAsmInstance().getWorkflowName(), upload.getJob(), upload.getPort(),
						getReplicaCount());
			}
		}
	}

	@Override
	public void beforeRemove(AbstractInputMask parent) {
		// TODO Auto-generated method stub

	}

}

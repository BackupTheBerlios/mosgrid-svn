package de.mosgrid.remd.ui.upload;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Window.Notification;

import de.mosgrid.gui.inputmask.AbstractInputMask;
import de.mosgrid.msml.jaxb.bindings.JobInputUploadType;
import de.mosgrid.msml.jaxb.bindings.ParameterType;
import de.mosgrid.msml.util.wrapper.Job;
import de.mosgrid.portlet.DomainPortlet;
import de.mosgrid.portlet.ImportedWorkflow;
import de.mosgrid.remd.properties.RemdProperties;
import de.mosgrid.util.NotificationFactory;
import de.mosgrid.util.UploadCollector;
import de.mosgrid.util.XfsBridge;

public class AppendAutoSelect extends MultiAutoSelect {
	private static final long serialVersionUID = -4055377203199743871L;
	private final Logger LOGGER = LoggerFactory.getLogger(AppendAutoSelect.class);

	private boolean renamedSuccessful;

	public AppendAutoSelect(DomainPortlet portlet, ImportedWorkflow wkfImport, Job job) {
		super(portlet, job, wkfImport, RemdProperties.get(RemdProperties.REMD_MAIN_JOB_ID));
	}

	@Override
	public void beforeSubmit(AbstractInputMask parent) {
		if (foundResultFiles() && !areFilesAlreadyRenamed()) {
			try {
				renameInputFiles();
			} catch (Exception e) {
				LOGGER.error(portlet.getUser() + " Error while renaming REMD result files.", e);
				Notification notif = NotificationFactory.createErrorNotification("Error!",
						"Could not rename REMD result files.<br>" + e.getMessage());
				getWindow().showNotification(notif);
			}
		}
	}

	private void renameInputFiles() throws IOException {
		String baseTprName = RemdProperties.get(RemdProperties.REMD_TPR_NAME);
		renameAll(baseTprName, ".tpr");

		String baseReplicaName = RemdProperties.get(RemdProperties.REMD_DEFNM);
		renameAll(baseReplicaName, ".log");
		renameAll(baseReplicaName, ".cpt");
		renameAll(baseReplicaName, ".trr");
		renameAll(baseReplicaName, ".xtc");
		renameAll(baseReplicaName, ".edr");

		renamedSuccessful = true;
	}

	@Override
	public boolean commitAndValidate() {
		return foundResultFiles() && (renamedSuccessful || areFilesAlreadyRenamed());
	}

	// set remote input URLs
	@Override
	public void collectUploads(UploadCollector collector) {
		String baseTprName = cut(RemdProperties.get(RemdProperties.REMD_TPR_NAME));
		String baseReplicaName = cut(RemdProperties.get(RemdProperties.REMD_DEFNM));

		String basePath = getAbsoluteResultPath() + (getAbsoluteResultPath().endsWith("/") ? "" : "/");

		List<JobInputUploadType> uploadList = getJob().getInitialization().getUploadList().getJobInputUpload();
		for (JobInputUploadType upload : uploadList) {
			if (upload.getFileType().equals("tpr")) {
				String xfsURL = XfsBridge.createURL(basePath + baseTprName + ".tpr");
				collector.addUploaded(xfsURL, upload.getPort(), upload.getJob());
			} else {
				String xfsURL = XfsBridge.createURL(basePath + baseReplicaName + "." + upload.getFileType());
				collector.addUploaded(xfsURL, upload.getPort(), upload.getJob());
			}
		}
	}

	// Set number of executions
	@Override
	public void afterCommitAndValidate(AbstractInputMask parent) {
		List<JobInputUploadType> uploadList = getJob().getInitialization().getUploadList().getJobInputUpload();
		for (JobInputUploadType upload : uploadList) {
			portlet.getAsmService().setNumberOfInputFiles(portlet.getUser().getUserID(),
					wkfImport.getAsmInstance().getWorkflowName(), upload.getJob(), upload.getPort(), getReplicaCount());
		}

		String mdrunId = RemdProperties.get(RemdProperties.REMD_MAIN_JOB_ID);
		String multiDictId = RemdProperties.get(RemdProperties.DICT_ENTRY_MULTI);
		Job mdrun = wkfImport.getTemplate().getJobWithID(mdrunId);
		for (ParameterType parameter : mdrun.getInitialization().getParamList().getParameter()) {
			if (parameter.getDictRef().endsWith(multiDictId)) {
				parameter.getScalar().setValue(Integer.valueOf(getReplicaCount()).toString());
			}
		}
	}

	@Override
	public void beforeRemove(AbstractInputMask parent) {
		// TODO Auto-generated method stub

	}

}

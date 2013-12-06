package de.mosgrid.adapter;

import hu.sztaki.lpds.pgportal.services.asm.ASMJob;
import hu.sztaki.lpds.pgportal.services.asm.ASMService;
import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mosgrid.adapter.base.InputInfo;
import de.mosgrid.util.UploadCollector;
import de.mosgrid.util.XfsBridge;

public class InputInfoHolder {

	private final Logger LOGGER = LoggerFactory.getLogger(InputInfoHolder.class);
	private List<InputInfo> _infos;

	InputInfoHolder(List<InputInfo> infos) {
		_infos = infos;
	}

	// TODO fill in necessary methods

	List<InputInfo> getInfos() {
		return _infos;
	}

	public void addUploads(UploadCollector collector) throws InputSupplyException {
		for (InputInfo info : getInfos()) {
			if (info.mustBeUploaded()) {
//				collector.addUpload((IUploadableInputInfo) info);
				((IUploadableInputInfo) info).addUploads(collector);
			}
		}
	}

	public void setInputParams(final ASMWorkflow wkfInstance, final String userId) throws InputSupplyException {
		for (InputInfo info : getInfos()) {
			if (!info.mustBeSetToParams())
				continue;
			ASMJob job = wkfInstance.getJobs().get(info.getJobName());
			if (job == null)
				throw new InputSupplyException("Could not find job: " + info.getJobName());

			String oldparams = ASMService.getInstance().getCommandLineArg(userId,
					wkfInstance.getWorkflowName(), job.getJobname());
			String params = ((ICommandLineParameterInputInfo) info).adjustCommandLine(oldparams);
			LOGGER.debug("Altered input parameters for " + info.getJobName() + "\n\tFROM: " + oldparams + "\n\tTO: "
					+ params);
			ASMService.getInstance().setCommandLineArg(userId, wkfInstance.getWorkflowName(),
					job.getJobname(), params);
		}
	}

	// TODO: setting remote paths by adapter? used?
	public List<String> setOuputRemotes(final ASMWorkflow wkfInstance, final String basePath, final String userId) {
		List<String> directoriesToCreate = new ArrayList<String>();
		for (ASMJob job : wkfInstance.getJobs().values()) {
			boolean doAdd = false;
			String path = basePath + "/" + job.getJobname() + "/";

			for (Entry<String, String> port : job.getOutput_ports().entrySet()) {
				//if port is not empty
				if (ASMService.getInstance().getRemoteOutputPath(userId,
						wkfInstance.getWorkflowName(), job.getJobname(), port.getKey()) != null
						|| !ASMService
								.getInstance()
								.getRemoteOutputPath(userId, wkfInstance.getWorkflowName(),
										job.getJobname(), port.getKey()).trim().equals("")) {
					//set remote url
					//TODO: url points to name of port and not to internal file name
					String url = XfsBridge.createURL(path + port.getValue());
					ASMService.getInstance().setRemoteInputPath(userId, wkfInstance.getWorkflowName(),
							job.getJobname(), port.getKey(), url);
					doAdd = true;
				}
			}

			if (doAdd && !directoriesToCreate.contains(path))
				directoriesToCreate.add(path);
		}
		return directoriesToCreate;
	}

}

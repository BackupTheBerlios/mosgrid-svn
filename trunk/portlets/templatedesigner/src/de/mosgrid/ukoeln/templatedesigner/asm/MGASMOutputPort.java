package de.mosgrid.ukoeln.templatedesigner.asm;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;

import java.util.Map.Entry;

public class MGASMOutputPort extends MGASMPort {

	public MGASMOutputPort(ASMService service,
			String userID, MGASMWorkflow workflow, 
			MGASMJob job, Entry<String, String> port) {
		super(service, userID, workflow, job, port);
	}

	@Override
	public String remotePath() {
		return getService().getRemoteOutputPath(getUserID(), getWorkflow().getWorkflowName(), 
				getJob().getJobName(), getName());
	}

}

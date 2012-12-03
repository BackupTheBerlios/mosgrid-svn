package de.mosgrid.ukoeln.templatedesigner.asm;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;

import java.util.Map.Entry;

/**
 * Wrapped ASM Input port. It allows to get the remote path.
 * Functionality may be extended if needed.
 * 
 * @author mkruse0
 *
 */
public class MGASMInputPort extends MGASMPort {

	/**
	 * Port is identified by workflow, job and portname.
	 * 
	 * @param service ASM Service
	 * @param userID UserID
	 * @param workflow ASM workflow
	 * @param job ASM job
	 * @param port Portentry as retrieved from ASM with entries key is the portnumber and the value portname.
	 */
	public MGASMInputPort(ASMService service,
			String userID, MGASMWorkflow workflow, 
			MGASMJob job, Entry<String, String> port) {
		super(service, userID, workflow, job, port);
	}

	@Override
	public String remotePath() {
		return getService().getRemoteInputPath(getUserID(), 
				getWorkflow().getWorkflowName(), getJob().getJobName(), getName());
	}
}

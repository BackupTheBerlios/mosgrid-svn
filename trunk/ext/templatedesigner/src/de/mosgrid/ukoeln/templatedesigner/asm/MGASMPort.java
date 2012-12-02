package de.mosgrid.ukoeln.templatedesigner.asm;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;

import java.util.Map.Entry;

public abstract class MGASMPort extends MGASMBaseObject {

	private final String _name;
	private final String _number;
	private final MGASMJob _job;
	private final MGASMWorkflow _workflow;

	public MGASMPort(ASMService service, String userID, MGASMWorkflow workflow, MGASMJob job, Entry<String, String> port) {
		super(service, userID);
		_name = port.getValue();
		_number = port.getKey();
		_workflow = workflow;
		_job = job;
	}

	public String getName() {
		return _name;
	}
	
	public String getNumber() {
		return _number;
	}
	
	protected MGASMJob getJob() {
		return _job;
	}
	
	protected MGASMWorkflow getWorkflow() {
		return _workflow;
	}
	
	public abstract String remotePath();
}

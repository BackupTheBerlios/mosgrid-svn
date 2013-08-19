package de.mosgrid.ukoeln.templatedesigner.asm;

import hu.sztaki.lpds.pgportal.services.asm.ASMService;

import java.util.Map.Entry;

/**
 * Basis for a wrapped asm port. It provides functionality to retrieve port's name
 * and port's number. Inherited objects must provide functionality to retrieve the remote path. 
 * 
 * @author mkruse0
 *
 */
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

	/**
	 * Get port's name.
	 * 
	 * @return Port's name
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Get port's number.
	 * 
	 * @return Port's number
	 */
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

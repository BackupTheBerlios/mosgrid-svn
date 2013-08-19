package de.mosgrid.ukoeln.templatedesigner.asm;

import hu.sztaki.lpds.pgportal.services.asm.ASMJob;
import hu.sztaki.lpds.pgportal.services.asm.ASMService;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;

/**
 * Wrapped ASM job. It provides functionality to get input- and output-ports and jobname.
 *  
 * @author mkruse0
 *
 */
public class MGASMJob extends MGASMBaseObject {
	
	private ASMJob _job;
	private ASMService _service;
	private Hashtable<String, MGASMInputPort> _inputports = new Hashtable<String, MGASMInputPort>();
	private Hashtable<String, MGASMOutputPort> _outputports = new Hashtable<String, MGASMOutputPort>();

	/**
	 * Additionally to the objects needed by the base object, the MGASMJob needs the ASMJob to
	 * be wrapped. During initialization all ports of the asm-job are also wrapped.
	 *  
	 * @param service ASM-Service.
	 * @param userID UserID
	 * @param workflow ASM-Workflow.
	 * @param job ASM-Job.
	 */
	public MGASMJob(ASMService service, String userID, MGASMWorkflow workflow, ASMJob job) {
		super(service, userID);
		_job = job;
		_service = service;
		
		for (Entry<String, String> port : _job.getInput_ports().entrySet()) {
			_inputports.put(port.getValue(), new MGASMInputPort(_service, userID, workflow, this, port));
		}

		for (Entry<String, String> port : _job.getOutput_ports().entrySet()) {
			_outputports.put(port.getValue(), new MGASMOutputPort(_service, userID, workflow, this, port));
		}
	}

	/**
	 * Get wrapped input port by portname.
	 * 
	 * @param name Portname.
	 * @return Wrapped port.
	 */
	public MGASMPort getInputPort(String name) {
		return _inputports.get(name);
	}

	/**
	 * Get wrapped output port by portname.
	 * 
	 * @param name Portname.
	 * @return Wrapped port.
	 */
	public MGASMPort getOutputPort(String name) {
		return _outputports.get(name);
	}
	
	/**
	 * Get job's name.
	 * 
	 * @return Job's name.
	 */
	public String getJobName() {
		return _job.getJobname();
	}

	/**
	 * Get all wrapped input ports.
	 * 
	 * @return Collection of all input ports.
	 */
	public Collection<MGASMInputPort> getInputPorts() {
		return _inputports.values();
	}
	
	/**
	 * Get all wrapped output ports.
	 * 
	 * @return collection of all output ports.
	 */
	public Collection<MGASMOutputPort> getOutputPorts() {
		return _outputports.values();
	}
}

package de.mosgrid.ukoeln.templatedesigner.asm;

import hu.sztaki.lpds.pgportal.services.asm.ASMJob;
import hu.sztaki.lpds.pgportal.services.asm.ASMService;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;

public class MGASMJob extends MGASMBaseObject {
	
	private ASMJob _job;
	private ASMService _service;
	private Hashtable<String, MGASMInputPort> _inputports = new Hashtable<String, MGASMInputPort>();
	private Hashtable<String, MGASMOutputPort> _outputports = new Hashtable<String, MGASMOutputPort>();

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

	public MGASMPort getInputPort(String name) {
		return _inputports.get(name);
	}

	public MGASMPort getOutputPort(String name) {
		return _outputports.get(name);
	}
	
	public String getJobName() {
		return _job.getJobname();
	}

	public Collection<MGASMInputPort> getInputPorts() {
		return _inputports.values();
	}
	
	public Collection<MGASMOutputPort> getOutputPorts() {
		return _outputports.values();
	}
}

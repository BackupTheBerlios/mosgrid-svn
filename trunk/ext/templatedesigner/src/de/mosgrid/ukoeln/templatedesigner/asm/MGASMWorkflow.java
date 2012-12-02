package de.mosgrid.ukoeln.templatedesigner.asm;

import hu.sztaki.lpds.pgportal.services.asm.ASMJob;
import hu.sztaki.lpds.pgportal.services.asm.ASMService;
import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;

import de.mosgrid.portlet.MosgridUser;

public class MGASMWorkflow extends MGASMBaseObject {

	private final Hashtable<String, MGASMJob> _jobs = new Hashtable<String, MGASMJob>();
	private final String _workflowID;
	private final ASMWorkflow _wkfl;
	
	public MGASMWorkflow(ASMService service, String userID, String workflowID) {
		super(service, userID);

		_workflowID = workflowID;
		_wkfl = service.getASMWorkflow(getUserID(), _workflowID);
		init();
	}
	
	public MGASMWorkflow(ASMService service, MosgridUser user, ASMWorkflow wkfl) {
		super(service, user.getUserID());
		
		_workflowID = wkfl.getWorkflowID();
		_wkfl = wkfl;
		init();
	}

	private void init() {
		for (Entry<String, ASMJob> job : _wkfl.getJobs().entrySet()) {
			MGASMJob mgJob = new MGASMJob(getService(), getUserID(), this, job.getValue());
			_jobs.put(job.getKey(), mgJob);
		}
	}

	public MGASMJob getJob(String value) {
		return _jobs.get(value);
	}
	
	public String getWorkflowName() {
		return _workflowID;
	}

	public Collection<MGASMJob> getJobs() {
		return _jobs.values();
	}

}

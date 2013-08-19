package de.mosgrid.ukoeln.templatedesigner.asm;

import hu.sztaki.lpds.pgportal.services.asm.ASMJob;
import hu.sztaki.lpds.pgportal.services.asm.ASMService;
import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map.Entry;

import de.mosgrid.portlet.MosgridUser;

/**
 * Wrapped ASM workflow. This class provides functionality to get jobs and workflowname. The jobs themselves
 * wrap asm ports. This way you can easily access ports and their functionality. 
 * 
 * @author mkruse0
 *
 */
public class MGASMWorkflow extends MGASMBaseObject {

	private final Hashtable<String, MGASMJob> _jobs = new Hashtable<String, MGASMJob>();
	private final String _workflowID;
	private final ASMWorkflow _wkfl;
	
	/**
	 * Instantiates a wrapped workflow. During initialization the workflow is scanned for jobs and found jobs
	 * are wrapped themselves.
	 * 
	 * @param service ASM service.
	 * @param user User id.
	 * @param wkfl ASM workflow.
	 */
	public MGASMWorkflow(ASMService service, String userID, String workflowID) {
		super(service, userID);

		_workflowID = workflowID;
		_wkfl = service.getASMWorkflow(getUserID(), _workflowID);
		init();
	}
	
	/**
	 * Instantiates a wrapped workflow. During initialization the workflow is scanned for jobs and found jobs
	 * are wrapped themselves.
	 * 
	 * @param service ASM service.
	 * @param user Mosgrid user to retrieve user id from.
	 * @param wkfl ASM workflow.
	 */
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

	/**
	 * Gets job by name.
	 * @param value Job's name.
	 * 
	 * @return Corresponding wrapped job.
	 */
	public MGASMJob getJob(String value) {
		return _jobs.get(value);
	}
	
	/**
	 * Get workflow's name.
	 * 
	 * @return Wrapped workflow's name.
	 */
	public String getWorkflowName() {
		return _workflowID;
	}

	/**
	 * Get all jobs.
	 * 
	 * @return A collection of all wrapped jobs.
	 */
	public Collection<MGASMJob> getJobs() {
		return _jobs.values();
	}

}

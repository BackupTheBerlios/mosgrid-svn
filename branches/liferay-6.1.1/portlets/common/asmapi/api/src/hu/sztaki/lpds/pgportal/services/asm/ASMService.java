/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.pgportal.services.asm;

import hu.sztaki.lpds.pgportal.services.asm.threads.ASMUploadThread;
import hu.sztaki.lpds.pgportal.services.asm.constants.StatusConstants;
import hu.sztaki.lpds.pgportal.services.asm.constants.StatusColorConstants;
import hu.sztaki.lpds.pgportal.services.asm.constants.DownloadTypeConstants;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.WorkflowInstanceStatusBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.WorkflowInstanceBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.RunningJobDetailsBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.OverviewJobStatusBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.JobStatisticsBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMJobInstanceBean;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.com.WorkflowSubmitThread;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.JobStatusData;
import hu.sztaki.lpds.pgportal.service.base.data.UserData;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowRunTime;
import hu.sztaki.lpds.pgportal.service.workflow.RealWorkflowUtils;
import hu.sztaki.lpds.pgportal.service.workflow.Sorter;
import hu.sztaki.lpds.pgportal.service.workflow.WorkflowUpDownloadUtils;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMResourceBean;
import hu.sztaki.lpds.pgportal.services.asm.constants.NotificationTypeConstants;
import hu.sztaki.lpds.pgportal.services.asm.exceptions.ASMException;
import hu.sztaki.lpds.pgportal.services.asm.exceptions.download.*;
import hu.sztaki.lpds.pgportal.services.asm.exceptions.general.*;
import hu.sztaki.lpds.pgportal.services.asm.exceptions.importation.*;
import hu.sztaki.lpds.pgportal.services.asm.exceptions.upload.*;
import hu.sztaki.lpds.repository.inf.PortalRepositoryClient;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.wfi.com.WorkflowInformationBean;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.wfi.inf.PortalWfiClient;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobInstanceBean;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FileUtils;

/**
 * Manages all instances for all users that created workflow instance
 * 
 * @author akosbalasko
 * @version 3.4
 * 
 */
public class ASMService {

	// this hashtable contains all workflows identified by username
	private Hashtable<String, ArrayList<ASMWorkflow>> workflows;
	public String STORAGE = "";
	public String WFS = "";
	private long uploadMaxSize = 10485760;
	public String PORTAL = "";
	public String GEMLCA = "gemlca";

	// MOSGRID BUG FIXES
	SimpleDateFormat gUSE_DateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");

	private static ASMService instance = null;

	/**
	 * Public function to provide Singleton mechanism
	 * 
	 * @return stored or new object of ifself
	 */

	public static ASMService getInstance() {

		if (instance == null) {
			instance = new ASMService();

		}
		return instance;
	}

	/**
	 * Protected constructor function
	 * 
	 */

	protected ASMService() {
		workflows = new Hashtable<String, ArrayList<ASMWorkflow>>();
	}

	/**
	 * putASM_instance Adding
	 * 
	 * @param userId
	 * @param workflow
	 */

	private void putWorkflow(String userId, ASMWorkflow workflow) {

		if (workflows.get(userId) == null) {
			workflows.put(userId, new ArrayList<ASMWorkflow>());
			loadASMWorkflows(userId);
		}

		else {
			workflows.get(userId).add(workflow);
		}

	}

	private void removeWorkflow(String userID, ASMWorkflow workflow) {

		workflows.get(userID).remove(workflow);

	}

	public void init() {

	}

	public String getPortalID() {
		return PropertyLoader.getInstance().getProperty("service.url");
	}

	/*
	 * Gets the ASM workflow what is identified by workflowname, returns null if workflow does not exists with the
	 * specified name
	 * 
	 * @param userId ID of the user
	 * 
	 * @param workflowname name of the ASM workflow
	 * 
	 * @return ASMWorkflow object
	 */
	public ASMWorkflow getASMWorkflow(String userId, String workflowname) {
		Hashtable hsh = new Hashtable();
		ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
		WFS = st.getServiceUrl();
		st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
		STORAGE = st.getServiceUrl();

		PORTAL = getPortalID();
		if (workflows.get(userId) == null) {
			workflows.put(userId, new ArrayList<ASMWorkflow>());
			loadASMWorkflows(userId);

		}
		for (int i = 0; i < workflows.get(userId).size(); ++i) {
			if (workflows.get(userId).get(i).getWorkflowName().equals(new String(workflowname)))
				return workflows.get(userId).get(i);
		}
		return null;
	}

	private void loadASMWorkflows(String userId) {
		try {
			ArrayList<ASMWorkflow> storedworkflows = getWorkflows(userId);
			workflows.put(userId, storedworkflows);
			for (int i = 0; i < storedworkflows.size(); ++i) {

				updateASMWorkflowStatus(userId, storedworkflows.get(i).getWorkflowName());
			}

		} catch (ClassNotFoundException ex) {
			throw new ASM_GeneralWebServiceException(ex.getCause(), userId);

		} catch (InstantiationException ex) {
			throw new ASM_GeneralWebServiceException(ex.getCause(), userId);
		} catch (IllegalAccessException ex) {
			throw new ASM_GeneralWebServiceException(ex.getCause(), userId);
		}
	}

	/**
	 * Imports a workflow/application/project/graph stored in the local Repository component of gUSE
	 * 
	 * @param userId
	 *            - Id of the user
	 * @param userworkflowname
	 *            - name of the workflow given by the user
	 * @param ownerId
	 *            - Id of the owner of the workflow that should be imported
	 * @param impWfType
	 *            - Type of the workflow (see ASMRepositoryItemType object)
	 * @param importworkflowName
	 *            - Name of the workflow to be imported
	 * @return String - name of the generated workflow
	 */

	public String ImportWorkflow(String userId, String userworkflowname, String ownerId, String impWfType,
			String importworkflowName) {
		try {
			ArrayList<RepositoryWorkflowBean> wfList = getWorkflowsFromRepository2Array(ownerId, impWfType, new Long(
					importworkflowName));

			RepositoryWorkflowBean selectedBean = (RepositoryWorkflowBean) wfList.get(0);
			if (selectedBean == null) {
				throw new Import_NotValidWorkflowNameException(userId, importworkflowName);

			}

			String storageID = WorkflowUpDownloadUtils.getInstance().getStorageID();
			String wfsID = WorkflowUpDownloadUtils.getInstance().getWfsID();
			PORTAL = this.getPortalID();

			selectedBean.setPortalID(PORTAL);
			selectedBean.setStorageID(storageID);
			selectedBean.setWfsID(wfsID);
			selectedBean.setUserID(userId);
			// String generated_id = Long.toString(System.currentTimeMillis());
			String concrete_wf_name = userworkflowname;
			selectedBean.setNewGrafName("g_" + userworkflowname);
			selectedBean.setNewRealName(concrete_wf_name);

			selectedBean.setNewAbstName("t_" + userworkflowname);

			// //System.out.println("selBean zip path : " + selectedBean.getZipRepositoryPath());
			//
			// import item from repository...
			Hashtable hsh = new Hashtable();
			// hsh.put("url", bean.getWfsID());
			ServiceType st = InformationBase.getI().getService("repository", "portal", hsh, new Vector());
			PortalRepositoryClient repoClient = (PortalRepositoryClient) Class.forName(st.getClientObject())
					.newInstance();
			repoClient.setServiceURL(st.getServiceUrl());
			repoClient.setServiceID(st.getServiceID());
			String retStr = repoClient.importWorkflow(selectedBean);

			// updating ASMs in memory
			String importWkfID = null;
			Date newestDate = null;
			Enumeration workflow_enum = PortalCacheService.getInstance().getUser(userId).getWorkflows().keys();
			while (workflow_enum.hasMoreElements()) {
				WorkflowData act_data = ((WorkflowData) PortalCacheService.getInstance().getUser(userId).getWorkflows()
						.get(workflow_enum.nextElement()));
				// check if id starts with correct prefix
				// System.out.println("ID: " + act_data.getWorkflowID());
				if (act_data.getWorkflowID().startsWith(userworkflowname)) {
					// parse timestamp of import
					Date importDate = parseDate(act_data.getWorkflowID());
					if (importDate != null) {
						// System.out.println("DATE: " + gUSE_DateFormat.format(importDate));
						if (newestDate == null || importDate.after(newestDate)) {
							importWkfID = act_data.getWorkflowID();
							// System.out.println("SETTING: " + importWkfID);
							newestDate = importDate;
						}
					}
				}
			}
			ASMWorkflow newImportedWorkflow = this.getRealASMWorkflow(userId, importWkfID);
			if (newImportedWorkflow == null) {
				throw new Import_FailedException(userId, userworkflowname);

			} else {
				putWorkflow(userId, newImportedWorkflow);
				updateASMWorkflowStatus(userId, importWkfID);
			}

			return importWkfID;

		} catch (InstantiationException ex) {
			throw new ASM_GeneralWebServiceException(ex.getCause(), userId, userworkflowname);

		} catch (IllegalAccessException ex) {
			throw new ASM_GeneralWebServiceException(ex.getCause(), userId, userworkflowname);
		} catch (Exception ex) {
			throw new ASM_GeneralWebServiceException(ex.getCause(), userId, userworkflowname);
		}

	}

	/**
	 * MOSGRID BUG FIX, creates da date object of given workflow id which carries a timestamp
	 */
	private Date parseDate(String workflowId) {
		int dateBeginIndex = workflowId.lastIndexOf("_") + 1;
		String dateString = workflowId.substring(dateBeginIndex);
		Date date = null;
		try {
			date = gUSE_DateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Modifies remote input file's path in a specified port
	 * 
	 * @param userID
	 *            ID of the user
	 * @param workflowID
	 *            ID of the Workflow
	 * @param jobID
	 *            ID of the Job
	 * @param portID
	 *            ID of the port
	 * @param newRemotePath
	 *            Remote file path what's to be set on the specified workflow/job/port
	 */
	public void setRemoteInputPath(String userID, String workflowID, String jobID, String portID, String newRemotePath) {
		setRemotePath(userID, workflowID, jobID, portID, newRemotePath, "input");
	}

	/**
	 * Returns the remote input's path that is adjusted for a specified port
	 * 
	 * @param userID
	 *            ID of the user
	 * @param workflowID
	 *            ID of the Workflow
	 * @param jobID
	 *            ID of the Job
	 * @param portID
	 *            ID of the port
	 * @return String remote path
	 */
	public String getRemoteInputPath(String userID, String workflowID, String jobID, String portID) {
		return getRemotePath(userID, workflowID, jobID, portID, "input");
	}

	/**
	 * Modifies remote file's path in a specified port
	 * 
	 * @param userID
	 *            ID of the user
	 * @param workflowID
	 *            ID of the Workflow
	 * @param jobID
	 *            ID of the Job
	 * @param portID
	 *            ID of the port
	 * @param newRemotePath
	 *            Remote file path what's to be set on the specified workflow/job/port
	 */
	public void setRemoteOutputPath(String userID, String workflowID, String jobID, String portID, String newRemotePath) {
		setRemotePath(userID, workflowID, jobID, portID, newRemotePath, "output");
	}

	/**
	 * Returns the remote output's path that is adjusted for a specified port
	 * 
	 * @param userID
	 *            ID of the user
	 * @param workflowID
	 *            ID of the Workflow
	 * @param jobID
	 *            ID of the Job
	 * @param portID
	 *            ID of the port
	 * @return String remote path
	 */
	public String getRemoteOutputPath(String userID, String workflowID, String jobID, String portID) {
		return getRemotePath(userID, workflowID, jobID, portID, "output");
	}

	private String getRemotePath(String userID, String workflowID, String jobID, String portID, String io) {
		try {
			Vector<JobPropertyBean> jobs = getConfigData(userID, workflowID);
			for (JobPropertyBean j : jobs) {
				// System.out.println("jobname i get is : " +j.getName());
				if (j.getName().equals(new String(jobID))) {
					Vector<PortDataBean> ports = null;
					if (io.equals("input")) {
						ports = j.getInputs();
					} else {
						ports = j.getOutputs();
					}
					for (PortDataBean p : ports) {
						// Iterator keys = p.getData().keySet().iterator();
						if (p.getSeq() == Long.parseLong(portID)) {
							return (String) p.getData().get("remote");
						}
					}
				}
			}// MoSGrid autosave
			if (autoSave) {
				this.saveConfigData(userID, workflowID, jobs);
			}
		} catch (Exception e) {
			throw new ASMException("Getting remote file path on " + workflowID + " " + jobID + " " + portID + " "
					+ "failed.");

		}
		return null;

	}

	private void setRemotePath(String userID, String workflowID, String jobID, String portID, String newRemotePath,
			String io) {
		try {
			Vector<JobPropertyBean> jobs = getConfigData(userID, workflowID);
			for (JobPropertyBean j : jobs) {
				// System.out.println("jobname i get is : " +j.getName());
				if (j.getName().equals(new String(jobID))) {
					Vector<PortDataBean> ports = null;
					if (io.equals("input")) {
						ports = j.getInputs();
					} else {
						ports = j.getOutputs();
					}
					for (PortDataBean p : ports) {
						// Iterator keys = p.getData().keySet().iterator();
						if (p.getSeq() == Long.parseLong(portID)) {
							p.getData().put("remote", newRemotePath);
						}
					}
				}
			}// MoSGrid autosave
			if (autoSave) {
				this.saveConfigData(userID, workflowID, jobs);
			}
		} catch (Exception e) {
			throw new ASMException("Setting remote file path on " + workflowID + " " + jobID + " " + portID + " to "
					+ newRemotePath + " failed.");

		}
	}

	/**
	 * 
	 * Getting ASM related workflows
	 * 
	 * @param userId
	 *            - id of the user
	 * @return - Hashtable <String,ASMWorkflow> : String is the name of the workflow
	 */
	public ArrayList<ASMWorkflow> getASMWorkflows(String userId) {
		if (workflows.get(userId) == null) {
			workflows.put(userId, new ArrayList<ASMWorkflow>());
			loadASMWorkflows(userId);

		}
		Iterator inst_iter = workflows.get(userId).iterator();
		while (inst_iter.hasNext()) {
			ASMWorkflow current = (ASMWorkflow) inst_iter.next();
			String concreteWfName = current.getWorkflowName();
			if (this.getASMWorkflow(userId, concreteWfName) != null) {

				updateASMWorkflowStatus(userId, concreteWfName);
				updateASMWorkflowStatistics(userId, concreteWfName);
			} else {
				workflows.get(userId).remove(current);
				// inst_iter.remove();
			}
		}
		return workflows.get(userId);
	}

	private void updateASMWorkflowStatus(String userId, String concrete_wf_name) {
		WorkflowInstanceStatusBean statusbean = this.getWorkflowStatus(userId, concrete_wf_name);
		this.getASMWorkflow(userId, concrete_wf_name).setStatusbean(statusbean);
	}

	private void updateASMWorkflowStatistics(String userId, String concreteWfName) {
		JobStatisticsBean statbean = getWorkflowStatistics(userId, concreteWfName);
		this.getASMWorkflow(userId, concreteWfName).setStatisticsBean(statbean);
	}

	private JobStatisticsBean getWorkflowStatistics(String userID, String workflowID) {
		JobStatisticsBean statBean = new JobStatisticsBean();
		int finishedjobs = 0;
		int errorjobs = 0;
		// getting jobs statuses
		String runtimeID = getRuntimeID(userID, workflowID);
		if (runtimeID != null) {
			// setting number of finished/error jobs!!!!
			ConcurrentHashMap<String, WorkflowData> workflows = PortalCacheService.getInstance().getUser(userID)
					.getWorkflows();
			WorkflowData wrk_data = workflows.get(workflowID);

			long finishedJobNumber = wrk_data.getFinishedStatus();
			long submittedJobNumber = wrk_data.getSubmittedStatus();
			long errorJobNumber = wrk_data.getErrorStatus(); // errorstatus fails!!!!!
			long runningJobNumber = wrk_data.getRunningStatus();
			// long estimatedJobNumber =
			// wrk_data.getErrorStatus()+wrk_data.getFinishedStatus()+wrk_data.getRunningStatus()+wrk_data.getSubmittedStatus()+wrk_data.getSuspendStatus();

			statBean.setErrorJobs(errorJobNumber);

			statBean.setSubmittedJobs(submittedJobNumber);
			statBean.setRunningJobs(runningJobNumber);
			statBean.setFinishedJobs(finishedJobNumber);

		}
		return statBean;
	}

	private String getStdOutFile(String userID, String workflowID, String jobID, String pidID, String runtimeID) {
		return getLogFile(userID, workflowID, jobID, pidID, runtimeID, "stdout.log");
	}

	private String getStdErrFile(String userID, String workflowID, String jobID, String pidID, String runtimeID) {
		return getLogFile(userID, workflowID, jobID, pidID, runtimeID, "stderr.log");
	}

	private String getSystemLogFile(String userID, String workflowID, String jobID, String pidID, String runtimeID) {
		return getLogFile(userID, workflowID, jobID, pidID, runtimeID, "gridnfo.log");
	}

	private String getLogFile(String userID, String workflowID, String jobID, String pidID, String runtimeID,
			String fileID) {
		{
			InputStream is = null;
			try {
				WorkflowData t = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID);
				Hashtable<String, String> params = new Hashtable<String, String>();
				params.put("portalID", PropertyLoader.getInstance().getProperty("service.url"));
				params.put("userID", userID);
				params.put("workflowID", workflowID);
				params.put("wfsID", WFS);
				params.put("jobID", jobID);
				params.put("pidID", pidID);
				params.put("runtimeID", runtimeID);
				params.put("fileID", fileID);
				Hashtable hsh = new Hashtable();
				hsh.put("url", t.getStorageID());
				ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
				PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
				ps.setServiceURL(st.getServiceUrl());
				ps.setServiceID("/viewer");

				is = ps.getStream(params);

				try {
					return convertStreamToString(is);
				} catch (Exception ex) {
					throw new ASM_GeneralWebServiceException(ex.getCause(), userID);
				}
			} catch (IOException ex) {
				throw new ASM_GeneralWebServiceException(ex.getCause(), userID);

			} catch (ClassNotFoundException ex) {
				throw new ASM_GeneralWebServiceException(ex.getCause(), userID);
			} catch (InstantiationException ex) {
				throw new ASM_GeneralWebServiceException(ex.getCause(), userID);
			} catch (IllegalAccessException ex) {
				throw new ASM_GeneralWebServiceException(ex.getCause(), userID);
			} finally {
				try {
					is.close();
				} catch (IOException ex) {

				}
			}
		}

	}

	/**
	 * Converts an inputstream to string
	 * 
	 * @param is
	 *            - InputStream
	 * @return string
	 * @throws Exception
	 */

	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = reader.readLine();
		String formattedString = "";
		while (line != null) {
			formattedString += line + "\n";
			sb.append(line + "\n");
			line = reader.readLine();
		}
		is.close();
		// return sb.toString();
		return formattedString;
	}

	/**
	 * 
	 * Gets and returns detailed informations about a workflow (e.g. statuses of the current workflow instance, overall
	 * statistics)
	 * 
	 * @param userID
	 *            - ID of the user
	 * @param workflowID
	 *            - ID of the workflow
	 * @return WorkflowInstanceBean object that contains information
	 * @throws ASM_NoValidRuntimeIDException
	 *             -it's thrown if there is No valid runtime ID
	 */

	public WorkflowInstanceBean getDetails(String userID, String workflowID) throws ASM_NoValidRuntimeIDException {

		String runtimeID = (String) PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID)
				.getAllRuntimeInstance().keys().nextElement();
		if (runtimeID != null) {

			if (PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID)
					.getJobsStatus().isEmpty()) {
				Hashtable prp = new Hashtable();
				prp.put("url", PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getWfsID());
				ServiceType st = InformationBase.getI().getService("wfs", "portal", prp, new Vector());
				try {
					PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
					pc.setServiceURL(st.getServiceUrl());
					pc.setServiceID(st.getServiceID());
					ComDataBean cmb = new ComDataBean();
					cmb.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
					cmb.setUserID(userID);
					cmb.setWorkflowID(workflowID);
					cmb.setWorkflowRuntimeID(runtimeID);
					int getmax = 2500;
					long cnt = 0;
					int retCnt = getmax;

					while (retCnt == getmax) {
						cmb.setSize(cnt);

						Vector<JobInstanceBean> retVector = new Vector<JobInstanceBean>();
						retVector = pc.getWorkflowInstanceJobs(cmb);
						// //System.out.println("wspgrade doInstanceDetails retVector.size() : " + retVector.size());
						for (int i = 0; i < retVector.size(); i++) {
							JobInstanceBean tmp = retVector.get(i);
							// //System.out.println("wspgrade doInstanceDetails tmp : " + tmp.getJobID() +", "+
							// tmp.getPID() +", "+ tmp.getStatus() +", "+ tmp.getResource());
							PortalCacheService
									.getInstance()
									.getUser(userID)
									.getWorkflow(workflowID)
									.getRuntime(runtimeID)
									.addJobbStatus(tmp.getJobID(), "" + tmp.getPID(), "" + tmp.getStatus(),
											tmp.getResource(), -1);
						}
						//
						retCnt = retVector.size();
						cnt++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			WorkflowInstanceBean workflowinstance = new WorkflowInstanceBean();

			// first key : jobname, second key : status code , second value : instancenumber
			UserData userdata = PortalCacheService.getInstance().getUser(userID);
			WorkflowData workflowdata = userdata.getWorkflow(workflowID);
			WorkflowRunTime runtimedata = workflowdata.getRuntime(runtimeID);
			Hashtable<String, Hashtable<String, String>> jobinstances = runtimedata.getCollectionJobsStatus();

			Iterator jobiterator = jobinstances.keySet().iterator();
			while (jobiterator.hasNext()) {

				String jobname = (String) jobiterator.next();

				RunningJobDetailsBean jobinstance = new RunningJobDetailsBean();
				jobinstance.setName(jobname);

				Hashtable<String, String> statuses = jobinstances.get(jobname);
				Iterator overviewstatusit = statuses.keySet().iterator();
				while (overviewstatusit.hasNext()) {
					String status = overviewstatusit.next().toString();

					// Integer status = Integer.parseInt((String)overviewstatusit.next().toString());

					OverviewJobStatusBean overview = new OverviewJobStatusBean();
					overview.setStatuscode(status);
					overview.setNumberofinstances(statuses.get(status));
					jobinstance.getStatisticsBean().getOverviewedstatuses().add(overview);
				}

				// first key : pid value : JobStatusData
				Hashtable<String, JobStatusData> runtimeinstances = new Hashtable<String, JobStatusData>();
				// runtimedata.getJobStatus("" + jobname).putAll(runtimeinstances);
				runtimeinstances.putAll(runtimedata.getJobStatus("" + jobname));
				Iterator instanceiterator = runtimeinstances.keySet().iterator();
				while (instanceiterator.hasNext()) {
					ASMJobInstanceBean instance = new ASMJobInstanceBean();

					String instanceID = (String) instanceiterator.next();
					String pid = runtimeinstances.get(instanceID).getPid();
					String resource = runtimeinstances.get(instanceID).getResource();
					String status = Integer.toString(runtimeinstances.get(instanceID).getStatus());
					String stdout = this.getStdOutFile(userID, workflowID, jobname, pid, runtimeID);

					String stderr = this.getStdErrFile(userID, workflowID, jobname, pid, runtimeID);

					String systemlog = this.getSystemLogFile(userID, workflowID, jobname, pid, runtimeID);

					instance.setId(instanceID);
					instance.setStatus(status);
					instance.setErrorText(stderr);
					instance.setOutputText(stdout);
					instance.setLogbookText(systemlog);
					instance.setUsedResource(resource);
					jobinstance.getInstances().add(instance);
				}
				workflowinstance.getJobs().add(jobinstance);
			}
			return workflowinstance;
		} else {
			throw new ASM_NoValidRuntimeIDException();
		}

	}

	/**
	 * Gets the command line arguments of a specified job in a specified workflow
	 * 
	 * 
	 * @param userID
	 *            - Id of the user
	 * @param selected_concrete
	 *            - name of the workflow
	 * @param selected_job
	 *            - name of the job
	 * @return - command line argument
	 */

	public synchronized String getCommandLineArg(String userID, String selected_concrete, String selected_job) {
		String actual_param = "";
		try {

			Vector wfconfig = getConfigData(userID, selected_concrete);
			for (int i = 0; i < wfconfig.size(); ++i) {
				JobPropertyBean jobprop = (JobPropertyBean) wfconfig.get(i);

				if (jobprop.getName().equals(new String(selected_job))) {
					actual_param = (String) jobprop.getExe().get("params");
				}

				/*
				 * Set keys = jobprop.getLabel().keySet(); Iterator it = keys.iterator(); while (it.hasNext()) { String
				 * key = (String) it.next(); String label = (String) jobprop.getLabel().get(key); String inh = (String)
				 * jobprop.getInherited().get(key); if (!(label.equals("") || label.equals("null")) &&
				 * (inh.equals("---") || inh.equals("null"))) { //System.out.println("szerintem ez egy free attr : " +
				 * jobprop.getExe().get(key)); } }
				 */
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		}
		return actual_param;
	}

	/**
	 * 
	 * Sets the command line argument of a specified job
	 * 
	 * @param userId
	 *            - id of the user
	 * @param selected_concrete
	 *            - name of the workflow
	 * @param selected_job
	 *            - name of the job
	 * @param commandline
	 *            - string to be set as command line argument
	 */
	public synchronized void setCommandLineArg(String userId, String selected_concrete, String selected_job,
			String commandline) {
		// System.out.println("setting command line arg : concrete " + selected_concrete + "... job... : " +
		// selected_job + " ... command line : " + commandline);
		try {
			Vector<JobPropertyBean> jobs = getConfigData(userId, selected_concrete);
			Vector<JobPropertyBean> new_jobs = new Vector<JobPropertyBean>();
			for (int i = 0; i < jobs.size(); ++i) {
				JobPropertyBean actjob = jobs.get(i);

				// System.out.println("job : " + actjob.getName() + " txt is : " + actjob.getTxt() + " param is : " +
				// actjob.getExe().get("params"));
				if (actjob.getName().equals(new String(selected_job))) {
					actjob.getExe().put("params", commandline);
					// System.out.println("Saving job command line arguments: " + actjob.getExe().get("params"));
				}
				new_jobs.add(actjob);
			}
			// MoSGrid autosave
			if (autoSave) {
				saveConfigData(userId, selected_concrete, new_jobs);
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		}
		// System.out.println("command line saved");

	}

	/**
	 * Returns the number of the required nodes of a given job in a given workflow. If type of the job is not MPI,
	 * "NotMPIJobException" will be thrown
	 * 
	 * @param userID
	 *            - user ID
	 * @param workflowID
	 *            - workflow ID
	 * @param jobID
	 *            - job ID
	 * @return - Number of the nodes as string
	 * @throws hu.sztaki.lpds.pgportal.services.asm.exceptions.general.NotMPIJobException
	 */

	public synchronized String getNodeNumber(String userID, String workflowID, String jobID) throws NotMPIJobException {
		String actual_param = "";
		try {

			Vector wfconfig = getConfigData(userID, workflowID);
			for (int i = 0; i < wfconfig.size(); ++i) {
				JobPropertyBean jobprop = (JobPropertyBean) wfconfig.get(i);

				if (jobprop.getName().equals(new String(jobID))) {
					if ("MPI".equals(jobprop.getExe().get("type"))) {
						actual_param = (String) jobprop.getExe().get("nodenumber");
					} else {
						throw new NotMPIJobException();
					}
				}

				/*
				 * Set keys = jobprop.getLabel().keySet(); Iterator it = keys.iterator(); while (it.hasNext()) { String
				 * key = (String) it.next(); String label = (String) jobprop.getLabel().get(key); String inh = (String)
				 * jobprop.getInherited().get(key); if (!(label.equals("") || label.equals("null")) &&
				 * (inh.equals("---") || inh.equals("null"))) { //System.out.println("szerintem ez egy free attr : " +
				 * jobprop.getExe().get(key)); } }
				 */
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		}
		return actual_param;
	}

	/**
	 * Set nodenumber property of a given MPI job. If the type of the job is not MPI, "NotMPIException" will be thrown
	 * 
	 * @param userId
	 *            - user ID
	 * @param workflowID
	 *            - workflow ID
	 * @param jobID
	 *            - job ID
	 * @param nodenumber
	 *            - nodenumber to be set to the job
	 * @throws hu.sztaki.lpds.pgportal.services.asm.exceptions.general.NotMPIJobException
	 */
	public synchronized void setNodeNumber(String userId, String workflowID, String jobID, int nodenumber)
			throws NotMPIJobException {
		// System.out.println("setting command line arg : concrete " + selected_concrete + "... job... : " +
		// selected_job + " ... command line : " + commandline);
		try {
			Vector<JobPropertyBean> jobs = getConfigData(userId, workflowID);
			Vector<JobPropertyBean> new_jobs = new Vector<JobPropertyBean>();
			for (int i = 0; i < jobs.size(); ++i) {
				JobPropertyBean actjob = jobs.get(i);

				// System.out.println("job : " + actjob.getName() + " txt is : " + actjob.getTxt() + " param is : " +
				// actjob.getExe().get("params"));
				if (actjob.getName().equals(new String(jobID))) {
					if ("MPI".equals(actjob.getExe().get("type"))) {
						actjob.getExe().put("nodenumber", Integer.toString(nodenumber));
					} else {
						throw new NotMPIJobException(userId, workflowID, jobID);
					}
					// System.out.println("Saving job command line arguments: " + actjob.getExe().get("params"));
				}
				new_jobs.add(actjob);
			}
			// MoSGrid autosave
			if (autoSave) {
				saveConfigData(userId, workflowID, new_jobs);
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		}
		// System.out.println("command line saved");

	}

	/**
	 * Deletes a workflow
	 * 
	 * @param userID
	 *            - id of the user which owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 */

	public void DeleteWorkflow(String userID, String workflowID) {

		// deleting from ASM

		ASMWorkflow inst = getASMWorkflow(userID, workflowID);
		removeWorkflow(userID, inst);

		WorkflowData wData = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID);
		Hashtable hsh = new Hashtable();
		ServiceType st;
		PortalWfsClient pc = null;
		// storage
		try {
			hsh = new Hashtable();
			hsh.put("url", wData.getStorageID());
			st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
			PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
			ps.setServiceURL(st.getServiceUrl());
			ps.setServiceID(st.getServiceID());

			ComDataBean tmp = new ComDataBean();
			PORTAL = this.getPortalID();
			tmp.setPortalID(PORTAL);
			tmp.setUserID(userID);
			tmp.setWorkflowID(wData.getWorkflowID());

			ps.deleteWorkflow(tmp);

			Enumeration wfenm = PortalCacheService.getInstance().getUser(userID).getTemplateWorkflows().keys();
			while (wfenm.hasMoreElements()) {
				String wfkey = "" + wfenm.nextElement();
				if (PortalCacheService.getInstance().getUser(userID).getTemplateWorkflow(wfkey).getGraf()
						.equals(wData.getGraf())) {

					// delete template workflow
					ComDataBean template_tmp = new ComDataBean();
					PORTAL = this.getPortalID();
					template_tmp.setPortalID(PORTAL);
					template_tmp.setUserID(userID);
					template_tmp.setWorkflowID(wfkey);
					// System.out.println("Deleting " + wfkey + " from storage ");
					ps.deleteWorkflow(template_tmp);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// wfs
		hsh = new Hashtable();
		hsh.put("url", wData.getWfsID());
		st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());

		try {
			pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
			pc.setServiceURL(st.getServiceUrl());
			pc.setServiceID(st.getServiceID());
			ComDataBean tmp = new ComDataBean();
			PORTAL = this.getPortalID();
			tmp.setPortalID(PORTAL);
			tmp.setUserID(userID);
			tmp.setWorkflowID(wData.getWorkflowID());
			pc.deleteWorkflow(tmp);

			// delete from timing workflow list
			PortalCacheService.getInstance().getUser(userID).deleteWorkflow(wData.getWorkflowID());

		} catch (Exception e) {
			e.printStackTrace();
		}

		// delete template
		// System.out.println("template is : " + wData.getTemplate());
		WorkflowData temp_data = PortalCacheService.getInstance().getUser(userID)
				.getTemplateWorkflow(wData.getTemplate());
		try {
			ComDataBean template_tmp = new ComDataBean();
			PORTAL = this.getPortalID();
			template_tmp.setPortalID(PORTAL);
			template_tmp.setUserID(userID);
			template_tmp.setWorkflowID(wData.getTemplate());
			pc.deleteWorkflow(template_tmp);

			// delete from timing workflow list
			PortalCacheService.getInstance().getUser(userID).deleteWorkflow(wData.getWorkflowID());

		} catch (Exception e) {
			e.printStackTrace();
		}

		// delete template workflow from portal cache
		Enumeration wfenm = PortalCacheService.getInstance().getUser(userID).getTemplateWorkflows().keys();
		String key = "";
		while (wfenm.hasMoreElements()) {
			key = "" + wfenm.nextElement();
			if (PortalCacheService.getInstance().getUser(userID).getTemplateWorkflow(key).getGraf().equals(workflowID)) {
				// delete template workflow from wfs
				hsh = new Hashtable();

				st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
				try {
					pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
					pc.setServiceURL(st.getServiceUrl());
					pc.setServiceID(st.getServiceID());
					ComDataBean tmp = new ComDataBean();
					PORTAL = this.getPortalID();
					tmp.setPortalID(PORTAL);
					tmp.setUserID(userID);
					tmp.setWorkflowID(key);
					pc.deleteWorkflow(tmp);
					// delete from timing workflow list
					PortalCacheService.getInstance().getUser(userID).deleteWorkflow(key);

				} catch (Exception e) {
					e.printStackTrace();
				}
				// delete template workflow from portal cache
				PortalCacheService.getInstance().getUser(userID).getTemplateWorkflows().remove(key);

				// delete graph from wfs
				ComDataBean cmd = new ComDataBean();
				cmd.setWorkflowID(workflowID);
				cmd.setUserID(userID);
				PORTAL = this.getPortalID();
				cmd.setPortalID(PORTAL);

				pc.deleteWorkflowGraf(cmd);

			}
		}

		// delete graf workflow from portal cache
		PortalCacheService.getInstance().getUser(userID).getAbstactWorkflows().remove(workflowID);
		//

		PortalCacheService.getInstance().getUser(userID).deleteWorkflow(wData.getWorkflowID());

	}

	private String getPortID(String userID, String workflowID, String jobID, String port)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		try {

			Vector<JobPropertyBean> jobs = getConfigData(userID, workflowID);

			for (JobPropertyBean j : jobs) {

				// System.out.println("jobname i get is : " +j.getName());
				if (j.getName().equals(new String(jobID))) {

					for (PortDataBean p : (Vector<PortDataBean>) j.getInputs()) {
						Iterator keys = p.getData().keySet().iterator();
						while (keys.hasNext()) {
							String key = (String) keys.next();

						}

						if (Long.toString(p.getSeq()).equals(new String(port))) {

							return Long.toString(p.getId());
						}

					}

				}
			}

		} catch (ClassNotFoundException ex) {
			throw ex;

		} catch (InstantiationException ex) {
			throw ex;
		} catch (IllegalAccessException ex) {
			throw ex;
		}

		throw new ASM_NoMachingPortIDException(userID, workflowID);
	}

	/**
	 * 
	 * Uploads a file from the user's local machine to the *portal* server only. It won't store the file in the Storage
	 * component of gUSE, and won't update database under WFS component
	 * 
	 * @param file
	 *            - file to upload (can be get from the ActionRequest)
	 * @param userID
	 *            - ID of the user
	 * @param filename
	 *            - the file should be placed using this name
	 * @return the uploaded file stored on the portal server
	 * @throws Exception
	 */
	public File uploadFiletoPortalServer(FileItem file, String userID, String filename) throws Exception {
		// System.out.println("doUPLOADFiletoPortalServer called ...");

		File serverSideFile = null;
		try {
			String tempDir = System.getProperty("java.io.tmpdir") + "/uploads/" + userID;
			File f = new File(tempDir);
			if (!f.exists())
				f.mkdirs();
			serverSideFile = new File(tempDir, filename);
			file.write(serverSideFile);
			file.delete();

		} catch (FileUploadException fue) {
			// response.setRenderParameter("full", "error.upload");

			throw new Upload_ErrorDuringUploadException(fue.getCause(), userID);

			// context.log("[FileUploadPortlet] - failed to upload file - "+ fue.toString());

		} catch (Exception e) {

			// response.setRenderParameter("full", "error.exception");
			throw new Upload_GeneralException(e.getCause(), userID);
			// context.log("[FileUploadPortlet] - failed to upload file - "+ e.toString());

		}
		return serverSideFile;
	}

	/**
	 * Sets a resource for a job specified in arguments
	 * 
	 * @param userID
	 *            - id of the user
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 * @param DCI
	 *            - name of the DCI (glite, pbs etc)
	 * @param resource
	 *            - name of the resource
	 * @param queue
	 *            - name of the queue
	 */
	public void setResource(String userID, String workflowID, String jobID, String type, String grid, String resource,
			String queue) {
		try {
			Vector<JobPropertyBean> workflowconfig = this.getConfigData(userID, workflowID);
			for (JobPropertyBean jobprop : workflowconfig) {
				// System.out.println("JobID : " + jobprop.getId());
				if (jobprop.getName().equals(jobID)) {

					if (type != null) {
						jobprop.getExe().put("gridtype", type);
					}
					if (grid != null) {
						jobprop.getExe().put("grid", grid);
					}
					if (resource != null) {
						jobprop.getExe().put("resource", resource);

					}
					if (queue != null) {
						jobprop.getExe().put("jobmanager", queue);

					}

				}

			}
			// MoSGrid autosave
			if (autoSave) {
				this.saveConfigData(userID, workflowID, workflowconfig);
			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * Returns the resource where the job is going to be submitted to.
	 * 
	 * @param userID
	 *            - ID of the user
	 * @param workflowID
	 *            - ID of the workflow
	 * @param jobID
	 *            - ID of the job
	 * @return - an ASMResourceBean object or null in any case of errors
	 */
	public ASMResourceBean getResource(String userID, String workflowID, String jobID) {

		try {
			Vector<JobPropertyBean> workflowconfig = this.getConfigData(userID, workflowID);
			for (JobPropertyBean jobprop : workflowconfig) {
				// System.out.println("JobID : " + jobprop.getId());
				if (jobprop.getName().equals(jobID)) {
					String type = (String) jobprop.getExe().get("gridtype");
					String grid = (String) jobprop.getExe().get("grid");
					String resource = (String) jobprop.getExe().get("resource");
					String queue = (String) jobprop.getExe().get("jobmanager");

					return new ASMResourceBean(type, grid, resource, queue);

				}

			}

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;

	}

	/**
	 * Sets an input text as local input file and associates it to a port
	 * 
	 * @param userID
	 *            - id of the user
	 * @param filecontent
	 *            - input text content
	 * @param workflowID
	 *            - id of the workflow, which contains the job
	 * @param jobID
	 *            - id of the job, which contains the port
	 * @param portID
	 *            - id of the port
	 * @throws IOException
	 */
	public void setInputText(String userID, String filecontent, String workflowID, String jobID, String portID)
			throws IOException {

		// saving filecontent to a file;
		String tempdir = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "uploads/" + userID;
		File tempdirf = new File(tempdir);
		if (!tempdirf.exists()) {
			tempdirf.mkdirs();
		}
		File tempfile = new File(tempdir + "/input_" + portID + "_file");
		if (tempfile.exists()) {
			// file exists delete it!
			tempfile.delete();
		}
		// System.out.println("setinputtext filecontent: "+ tempfile);
		tempfile.createNewFile();
		FileUtils.writeStringToFile(tempfile, filecontent);
		try {
			this.placeUploadedFile(userID, tempfile, workflowID, jobID, portID);
		} catch (Exception e) {
			// System.out.println("placing file "+tempfile +" failed");
		}
	}

	/**
	 * 
	 * Gets the uploaded file stored on the portal server's temporary folder and uploads it to Storage component of gUSE
	 * then updates the database managed by WFS component
	 * 
	 * @param userID
	 *            - ID of the user
	 * @param fileOnPortalServer
	 *            - file on the portal server
	 * @param workflowID
	 *            - Name of the workflow
	 * @param jobID
	 *            - name of the job
	 * @param portID
	 *            - ID of the port (0..15)
	 * @throws Exception
	 */
	public void placeUploadedFile(String userID, File fileOnPortalServer, String workflowID, String jobID, String portID)
			throws Exception

	{

		// System.out.println("placeUploadedFile started...");
		try {
			String SID = this.getPortID(userID, workflowID, jobID, portID);
			Hashtable h = new Hashtable();
			PORTAL = this.getPortalID();
			h.put("portalID", this.PORTAL);
			h.put("userID", userID);
			h.put("workflowID", workflowID);
			h.put("jobID", jobID);
			String sfile = "input_" + portID;
			h.put("sfile", sfile);
			String confID = userID + String.valueOf(System.currentTimeMillis());
			h.put("confID", confID);
			h.put("sid", confID);
			String uploadField = "";
			String uploadingitem = fileOnPortalServer.getName();

			uploadField = "input_" + portID + "_file";

			Hashtable hsh = new Hashtable();
			ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
			PortalStorageClient psc = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
			psc.setServiceURL(st.getServiceUrl());
			psc.setServiceID("/upload");
			if (fileOnPortalServer != null) {
				// System.out.println("Upload to storage called : uploadField is : " + uploadField );
				Enumeration e = h.keys();
				while (e.hasMoreElements()) {
					String elem = (String) e.nextElement();
					// System.out.println("key in h : " + elem);
					// System.out.println("value in h : " + h.get(elem));
				}

				psc.fileUpload(fileOnPortalServer, uploadField, h);

				// uploadThread
				if (uploadingitem != null) {
					ASMUploadThread uploadthread = new ASMUploadThread(userID, workflowID, jobID, portID, confID, SID,
							uploadingitem);
					uploadthread.start();
					boolean isgo = false;
					while (!isgo) {
						for (int i = 0; i < 100; ++i)
							for (int j = 0; j < 100; ++j) {
								String s = System.getProperty("user.dir");
							}
						isgo = uploadthread.isGo();

					}

				}

			}
		} catch (Exception ex) {
			// response.setRenderParameter("full", "error.upload");
			ex.printStackTrace();
			throw new Upload_GeneralException(ex.getCause(), userID);

		}

	}

	public String getRuntimeID(String userID, String workflowID) {

		ConcurrentHashMap runtimes = ((ConcurrentHashMap) PortalCacheService.getInstance().getUser(userID)
				.getWorkflow(workflowID).getAllRuntimeInstance());

		if (runtimes.size() > 0) {

			Object firstID = runtimes.keySet().iterator().next();

			return firstID.toString();

		}

		return null;

	}

	// downloadtype can be : InstanceOutputs, InstanceAll, All, AllbutLogs,AllInputs,AllOutputs
	private InputStream getFileStreamFromStorage(String userID, String workflowID, int downloadtype) {
		InputStream is = null;
		try {
			Hashtable hsh = new Hashtable();
			try {
				hsh.put("url", STORAGE);
			} catch (Exception e) {
			}
			Hashtable<String, String> params = new Hashtable<String, String>();
			params.put("portalID", PORTAL);
			params.put("wfsID", WFS);
			params.put("userID", userID);
			params.put("workflowID", workflowID);
			// params.put("jobID", jobID);

			// TODO : modify pidID to handle parametric output ports!!!

			// params.put("pidID", pID);
			String runtimeID = getRuntimeID(userID, workflowID);

			if (runtimeID != null) {
				switch (downloadtype) {

				case DownloadTypeConstants.All:
					params.put("downloadType", "all");
					params.put("instanceType", "all");
					params.put("outputLogType", "all");
					break;
				case DownloadTypeConstants.AllInputs:
					params.put("downloadType", "inputs");
					break;
				case DownloadTypeConstants.AllOutputs:
					params.put("downloadType", "outputs_all");
					break;
				case DownloadTypeConstants.AllButLogs:
					params.put("downloadType", "all");
					params.put("instanceType", "all");
					params.put("outputLogType", "none");
					break;
				case DownloadTypeConstants.InstanceAll:
					params.put("downloadType", "inputs_" + runtimeID);
					params.put("instanceType", "one_" + runtimeID);

					break;
				case DownloadTypeConstants.InstanceOutputs:
					params.put("downloadType", "outputs_" + runtimeID);

					break;

				}

				ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
				PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
				ps.setServiceURL(st.getServiceUrl());
				ps.setServiceID("/download");
				is = ps.getStream(params);

				return is;
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);

		} catch (InstantiationException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);

		} catch (IOException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);

		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);

		}

		return null;
	}

	private void convertOutputZip(String userId, String workflowId, String jobId, String fileName, InputStream is,
			OutputStream os) throws IOException {

		InputStream exactFile = null;
		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry entry;
		String runtimeID = getRuntimeID(userId, workflowId);
		ZipOutputStream zos = new ZipOutputStream(os);
		while ((entry = zis.getNextEntry()) != null) {

			if (jobId == null
					|| (entry.getName().contains(jobId + "/outputs/" + runtimeID + "/") && (fileName == null || (fileName != null && entry
							.getName().endsWith(fileName))))) {
				int size;
				byte[] buffer = new byte[2048];

				String parentDir = entry.getName().split("/")[entry.getName().split("/").length - 2];
				String fileNameInZip = parentDir + "/"
						+ entry.getName().split("/")[entry.getName().split("/").length - 1];
				ZipEntry newFile = new ZipEntry(fileNameInZip);
				zos.putNextEntry(newFile);

				while ((size = zis.read(buffer, 0, buffer.length)) != -1) {

					zos.write(buffer, 0, size);
				}
				zos.closeEntry();

			}
		}
		zis.close();
		zos.close();

	}

	/**
	 * 
	 * It gets the file specified by the attributes (userID/workflowID/jobID/portID) and passes it back to the
	 * outputstream of the specified response
	 * 
	 * @param userID
	 *            - ID of the user
	 * @param workflowID
	 *            - Name of the workflow
	 * @param jobID
	 *            - Name of the job
	 * @param fileName
	 *            - Name of the file
	 * @param response
	 *            - response that should contain the file to download
	 * @throws ASM_GeneralException
	 */
	public void getFileStream(String userID, String workflowID, String jobID, String fileName,
			HttpServletResponse response) throws Upload_GeneralException {
		InputStream is = null;
		try {

			is = getFileStreamFromStorage(userID, workflowID, DownloadTypeConstants.InstanceAll);
			this.convertOutputZip(userID, workflowID, jobID, fileName, is, response.getOutputStream());

		} catch (IOException ex) {
			throw new Download_GettingFileStreamException(ex.getCause(), userID, workflowID);
		}
	}

	/**
	 * 
	 * It gets the file specified by the attributes (userID/workflowID/jobID/portID) and passes it back to the
	 * outputstream of the specified response
	 * 
	 * @param userID
	 *            - ID of the user
	 * @param workflowID
	 *            - Name of the workflow
	 * @param jobID
	 *            - Name of the job
	 * @param portID
	 *            - ID of the port (0..15)
	 * 
	 * @throws ASM_GeneralException
	 * @return String - Path of the file stored on the portal server
	 */
	public String getFiletoPortalServer(String userID, String workflowID, String jobID, String portID)
			throws Download_GettingFileToPortalServiceException {
		String downloadfolder = PropertyLoader.getInstance().getProperty("prefix.dir") + "tmp/users/" + userID
				+ "/workflow_outputs";

		if (!new File(downloadfolder).exists()) {
			File down_folder = new File(downloadfolder);
			down_folder.mkdirs();
		}
		String outputfile = downloadfolder + "/" + workflowID;
		File f = new File(outputfile);
		InputStream is = null;
		try {

			is = getFileStreamFromStorage(userID, workflowID, DownloadTypeConstants.InstanceAll);
			java.io.OutputStream out = new FileOutputStream(f);
			byte[] b = new byte[1024];
			int nm;
			while ((nm = is.read(b)) > (-1)) {

				out.write(b, 0, nm);
			}
			is.close();
			out.close();
			return outputfile;
		} catch (IOException ex) {
			throw new Download_GettingFileToPortalServiceException(ex.getCause(), userID, workflowID);
		}
	}

	/**
	 * 
	 * It gets the file specified by the attributes (userID/workflowID/jobID/portID) and passes it back to the
	 * outputstream of the specified ResourceResponse It can be used if file downloading should work using Ajax
	 * technology
	 * 
	 * @param userID
	 *            - ID of the user
	 * @param workflowID
	 *            - Name of the workflow
	 * @param jobID
	 *            - Name of the job
	 * @param fileName
	 *            - name of the file
	 * @param response
	 *            - response that should contain the file to download
	 * @throws ASM_GeneralException
	 */
	public void getFileStream(String userID, String workflowID, String jobID, String fileName, ResourceResponse response)
			throws Download_GettingFileStreamException

	{

		InputStream is = null;
		try {

			is = getFileStreamFromStorage(userID, workflowID, DownloadTypeConstants.InstanceAll);
			this.convertOutputZip(userID, workflowID, jobID, fileName, is, response.getPortletOutputStream());

		} catch (IOException ex) {
			throw new Download_GettingFileStreamException(ex.getCause(), userID, workflowID);
		}
	}

	/**
	 * 
	 * Provides outputs through response object added as a parameter
	 * 
	 * @param userId
	 *            id of the user
	 * @param workflowId
	 *            name of the workflow
	 * @param response
	 *            object that will contain the output stream
	 * @throws ASM_GeneralException
	 */
	public void getWorkflowOutputs(String userId, String workflowId, ResourceResponse response) {
		try {
			InputStream is = null;
			is = getFileStreamFromStorage(userId, workflowId, DownloadTypeConstants.All);
			this.convertOutputZip(userId, workflowId, null, null, is, response.getPortletOutputStream());

		} catch (IOException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * 
	 * Gets the workflows from the local repository exported by a specified user
	 * 
	 * @param owner
	 *            - user who exported the workflow
	 * @param type
	 *            - type of the exportation (application,project,graph)
	 * @return - vector of the workflows
	 * @throws Exception
	 */

	public synchronized Vector<ASMRepositoryItemBean> getWorkflowsFromRepository(String owner, String type)
			throws Exception {
		try {
			Long id = new Long(0);
			return getWorkflowsFromRepository2Vector(owner, type, id);
		} catch (Exception e) {
			throw e;
		}
	}

	private synchronized ArrayList<RepositoryWorkflowBean> getWorkflowsFromRepository2Array(String owner, String type,
			Long id) throws ASM_UnknownErrorException {
		try {
			// get repository workflow item list from wfs...
			Hashtable hsh = new Hashtable();
			ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
			PortalWfsClient wfsClient = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
			wfsClient.setServiceURL(st.getServiceUrl());
			wfsClient.setServiceID(st.getServiceID());
			RepositoryWorkflowBean bean = new RepositoryWorkflowBean();
			bean.setId(id);
			bean.setUserID(owner);
			bean.setWorkflowType(type);
			Vector<RepositoryWorkflowBean> wfList = (Vector<RepositoryWorkflowBean>) wfsClient.getRepositoryItems(bean);
			ArrayList<RepositoryWorkflowBean> ret_list = new ArrayList<RepositoryWorkflowBean>();
			if (wfList == null) {
				throw new ASM_UnknownErrorException();
			} else {

				for (RepositoryWorkflowBean repbean : wfList) {
					ret_list.add(repbean);
				}

				return ret_list;
			}
		} catch (ClassNotFoundException ex) {
			throw new ASM_GeneralWebServiceException(ex.getCause(), owner);
		} catch (InstantiationException ex) {
			throw new ASM_GeneralWebServiceException(ex.getCause(), owner);
		} catch (IllegalAccessException ex) {
			throw new ASM_GeneralWebServiceException(ex.getCause(), owner);
		}

	}

	private synchronized Vector<ASMRepositoryItemBean> getWorkflowsFromRepository2Vector(String owner, String type,
			Long id) throws Exception {
		// get repository workflow item list from wfs...
		Hashtable hsh = new Hashtable();
		// hsh.put("url", bean.getWfsID());
		ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
		PortalWfsClient wfsClient = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
		wfsClient.setServiceURL(st.getServiceUrl());
		wfsClient.setServiceID(st.getServiceID());
		//
		RepositoryWorkflowBean bean = new RepositoryWorkflowBean();

		bean.setId(id);
		bean.setUserID(owner);
		bean.setWorkflowType(type);
		Vector<RepositoryWorkflowBean> wfList = (Vector<RepositoryWorkflowBean>) wfsClient.getRepositoryItems(bean);

		if (wfList == null) {
			throw new ASM_UnknownErrorException();

		}
		Vector<ASMRepositoryItemBean> repitemlist = new Vector<ASMRepositoryItemBean>();
		for (int i = 0; i < wfList.size(); ++i) {
			ASMRepositoryItemBean itembean = new ASMRepositoryItemBean();
			RepositoryWorkflowBean rwbean = wfList.get(i);
			itembean.setExportText(rwbean.getExportText());
			itembean.setExportType(rwbean.getExportType());
			itembean.setId(rwbean.getId());
			itembean.setUserID(rwbean.getUserID());
			itembean.setItemID(rwbean.getWorkflowID());

			repitemlist.add(itembean);
		}

		return repitemlist;
	}

	/**
	 * 
	 * Gets list of the users who have exported anything to the repository
	 * 
	 * @param type
	 *            - type of the exported workflow
	 * @return - vector of userIds - Developers of Workflows that have already exported to the local repository
	 * @throws Exception
	 */

	public synchronized Vector<String> getWorkflowDevelopers(String type) throws Exception {
		// get repository workflow item list from wfs...
		Hashtable hsh = new Hashtable();

		ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
		PortalWfsClient wfsClient = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
		wfsClient.setServiceURL(st.getServiceUrl());
		wfsClient.setServiceID(st.getServiceID());

		RepositoryWorkflowBean bean = new RepositoryWorkflowBean();
		Long id = new Long(0);
		bean.setId(id);

		bean.setWorkflowType(type);
		//
		Vector<RepositoryWorkflowBean> wfList = wfsClient.getRepositoryItems(bean);
		if (wfList == null) {
			throw new ASM_UnknownErrorException();

		}

		Vector<String> owners = new Vector<String>();
		for (int i = 0; i < wfList.size(); ++i) {
			String userId = wfList.get(i).getUserID();

			if (!owners.contains(new String(userId)))
				owners.add(userId);
		}

		return owners;
	}

	/**
	 * 
	 * Gets the workflows of a specified user (uses portalchache)
	 * 
	 * 
	 * @param userID
	 *            - Id of the user
	 * @return - vector of workflowData objects (workflows)
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public synchronized ArrayList<ASMWorkflow> getWorkflows(String userID) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		ArrayList<ASMWorkflow> tmpworkflows = new ArrayList<ASMWorkflow>();
		Vector<WorkflowData> workflows = (Vector<WorkflowData>) Sorter.getInstance().sortFromValues(
				PortalCacheService.getInstance().getUser(userID).getWorkflows());
		for (int i = 0; i < workflows.size(); ++i) {
			if (workflows.get(i).getWorkflowID() != null && !workflows.get(i).getWorkflowID().equals("null")) {
				ASMWorkflow inst = getRealASMWorkflow(userID, workflows.get(i).getWorkflowID());
				tmpworkflows.add(inst);
			}
		}
		return tmpworkflows;
	}

	private ASMWorkflow getRealASMWorkflow(String userID, String workflowID) {

		Vector<JobPropertyBean> joblist = ((Vector<JobPropertyBean>) getWorkflow(userID, workflowID));
		ASMWorkflow inst = new ASMWorkflow(userID);
		Hashtable<String, ASMJob> jobs = new Hashtable<String, ASMJob>();

		for (int j = 0; j < joblist.size(); ++j) {

			String jobname = joblist.get(j).getName();

			// getting input ports
			Vector<PortDataBean> input_portlist = joblist.get(j).getInputs();
			Hashtable<String, String> input_ports = new Hashtable<String, String>();
			for (int k = 0; k < input_portlist.size(); ++k) {
				String portseq = Long.toString(input_portlist.get(k).getSeq());
				String portname = input_portlist.get(k).getName();

				input_ports.put(portseq, portname);
			}
			// getting outupt ports
			Vector<PortDataBean> output_portlist = joblist.get(j).getOutputs();
			Hashtable<String, String> output_ports = new Hashtable<String, String>();
			for (int k = 0; k < output_portlist.size(); ++k) {
				String portseq = Long.toString(output_portlist.get(k).getSeq());
				String portname = output_portlist.get(k).getName();
				output_ports.put(portseq, portname);
			}

			ASMJob asm_job = new ASMJob(jobname, input_ports, output_ports);
			jobs.put(jobname, asm_job);
		}

		inst.setJobs(jobs);
		inst.setWorkflowName(workflowID);
		return inst;

	}

	private Vector getWorkflow(String userID, String workflowID) {
		Vector v = null;
		try {
			ServiceType st = InformationBase.getI().getService("wfs", "portal", new Hashtable(), new Vector());
			PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
			pc.setServiceURL(st.getServiceUrl());
			pc.setServiceID(st.getServiceID());
			ComDataBean commdata = new ComDataBean();
			PORTAL = this.getPortalID();
			commdata.setPortalID(PORTAL);
			commdata.setUserID(userID);
			commdata.setWorkflowID(workflowID);
			// Vector v=pc.getWorkflowJobs(new PortalUserWorkflowBean(pUser,PORTAL, workflowID));
			v = pc.getWorkflowConfigData(commdata);

		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		}
		return v;
	}

	/**
	 * 
	 * Gets the status of a specified workflow
	 * 
	 * @param userId
	 *            - Id of the user
	 * @param workflowId
	 *            - Id of the workflow
	 * @return - InstanceStatusbean object that contains status and the actual statuscolor
	 */
	public synchronized WorkflowInstanceStatusBean getWorkflowStatus(String userId, String workflowId) {

		String statuscolor = "";
		String status = "";
		StatusConstants cons = new StatusConstants();
		Set instances = PortalCacheService.getInstance().getUser(userId).getWorkflow(workflowId)
				.getAllRuntimeInstance().keySet();
		if (instances.size() > 0) {
			Enumeration insten = PortalCacheService.getInstance().getUser(userId).getWorkflow(workflowId)
					.getAllRuntimeInstance().keys();
			String inst = (String) insten.nextElement();
			WorkflowRunTime wfruntime = (WorkflowRunTime) PortalCacheService.getInstance().getUser(userId)
					.getWorkflow(workflowId).getAllRuntimeInstance().get(inst);
			status = cons.getStatus(Integer.toString(wfruntime.getStatus()));

		} else {
			status = cons.getStatus(cons.INIT);
		}

		StatusColorConstants colors = new StatusColorConstants();
		if (!status.equals("")) {
			statuscolor = colors.getColor(status);
		}

		return new WorkflowInstanceStatusBean(status, statuscolor);

	}

	/**
	 * Gets the configuration of a specified workflow
	 * 
	 * @param userID
	 *            - ID of the user
	 * @param workflowID
	 *            - name of the workflow
	 * @return Vector of JobProperyBeans
	 * @throws java.lang.ClassNotFoundException
	 *             no communication class
	 * @throws java.lang.InstantiationException
	 *             com.class could not be initialized.
	 * @throws java.lang.IllegalAccessException
	 *             could not have access to com.class
	 */
	private Vector<JobPropertyBean> getWorkflowConfig(String userID, String workflowID) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {

		Hashtable hsh = new Hashtable();
		hsh.put("url", WFS);
		ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
		PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
		pc.setServiceURL(st.getServiceUrl());
		pc.setServiceID(st.getServiceID());

		ComDataBean cmd = new ComDataBean();
		PORTAL = this.getPortalID();
		cmd.setPortalID(PORTAL);
		cmd.setUserID(userID);
		cmd.setWorkflowID(workflowID);
		return pc.getWorkflowConfigData(cmd);

	}

	private Vector<JobPropertyBean> getConfigData(String userID, String selected_WF) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		synchronized (TEMP_PROP_LOCK) {
			String tempSearchKey = userID + "_" + selected_WF;
			if (tempJobPropertyMap.containsKey(tempSearchKey)) {
				// look in temporarly stored map first
				return tempJobPropertyMap.get(tempSearchKey);
			} else {
				// load if not querried before
				PORTAL = this.getPortalID();
				ComDataBean tmp = new ComDataBean();
				tmp.setPortalID(PORTAL);
				tmp.setUserID(userID);
				tmp.setWorkflowID(selected_WF);

				Hashtable hsh = new Hashtable();

				ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
				PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
				pc.setServiceURL(st.getServiceUrl());
				pc.setServiceID(st.getServiceID());

				Vector<JobPropertyBean> jobProperties = pc.getWorkflowConfigData(tmp);
				// add to temp storage
				tempJobPropertyMap.put(tempSearchKey, jobProperties);
				return jobProperties;
			}
		}
	}

	/**
	 * Saves workflow configuration
	 * 
	 * @param userID
	 *            - Id of the user
	 * @param workflowID
	 *            - Id of the workflow
	 * @param pJobs
	 *            jobs that contains the configuration
	 * @throws java.lang.ClassNotFoundException
	 *             no communication class
	 * @throws java.lang.InstantiationException
	 *             com.class could not be initialized.
	 * @throws java.lang.IllegalAccessException
	 *             could not have access to com.class
	 */
	private void saveConfigData(String userID, String workflowID, Vector pJobs) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		synchronized (TEMP_PROP_LOCK) {
			Hashtable hsh = new Hashtable();
			hsh.put("url", PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getWfsID());
			ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
			PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
			pc.setServiceURL(st.getServiceUrl());
			pc.setServiceID(st.getServiceID());

			ComDataBean cmd = new ComDataBean();
			PORTAL = this.getPortalID();
			cmd.setPortalID(PORTAL);
			cmd.setUserID(userID);
			cmd.setWorkflowID(workflowID);
			// cmd.setTyp(4);

			pc.setWorkflowConfigData(cmd, pJobs);

			ComDataBean cmd_get = new ComDataBean();
			cmd_get.setPortalID(PORTAL);
			cmd_get.setUserID(userID);
			cmd_get.setWorkflowID(workflowID);

			Vector get_jobs = pc.getWorkflowConfigData(cmd_get);

			// remove from temp list
			String tempKey = userID + "_" + workflowID;
			tempJobPropertyMap.remove(tempKey);
		}
	}

	private void cleanAllWorkflowInstances(String userID, String workflowID) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {

		WorkflowData workflow = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID);

		StatusConstants cons = new StatusConstants();

		Enumeration hashKeys = workflow.getAllRuntimeInstance().keys();
		while (hashKeys.hasMoreElements()) {
			String key = (String) hashKeys.nextElement();
			WorkflowRunTime runtimeobj = (WorkflowRunTime) workflow.getAllRuntimeInstance().get(key);
			RealWorkflowUtils.getInstance().deleteWorkflowInstance(userID, workflowID, key);
		}

	}

	/**
	 * Submits a workflow Instance
	 * 
	 * @param userID
	 *            Id of the user
	 * @param workflowID
	 *            Id of the workflow
	 * @throws java.lang.ClassNotFoundException
	 *             no communication class
	 * @throws java.lang.InstantiationException
	 *             com.class could not be initialized.
	 * @throws java.lang.IllegalAccessException
	 *             could not have access to com.class
	 */
	public void submit(String userID, String workflowID) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		// deleting old workflow instance
		try {
			cleanAllWorkflowInstances(userID, workflowID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ServiceType st = InformationBase.getI().getService("wfi", "portal", new Hashtable(), new Vector());
		PortalWfiClient pc = (PortalWfiClient) Class.forName(st.getClientObject()).newInstance();
		pc.setServiceURL(st.getServiceUrl());
		pc.setServiceID(st.getServiceID());

		WorkflowRuntimeBean bean = new WorkflowRuntimeBean();
		WorkflowData data = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID);

		new WorkflowSubmitThread(PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID), userID,
				"text", "Never");
	}

	/**
	 * Submits a given workflow with a text of submission and notifies according to the given string (@see
	 * NotificationTypeConstants)
	 * 
	 * @param userID
	 *            - user ID
	 * @param workflowID
	 *            - workflow ID
	 * @param text
	 *            - Note for the submission
	 * @param notify
	 *            - type of notification (@see NotificationTypeConstants)
	 * @throws java.lang.ClassNotFoundException
	 *             no communication class
	 * @throws java.lang.InstantiationException
	 *             com.class could not be initialized.
	 * @throws java.lang.IllegalAccessException
	 *             could not have access to com.class
	 */
	public void submit(String userID, String workflowID, String text, String notify) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		// deleting old workflow instance
		try {
			cleanAllWorkflowInstances(userID, workflowID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ServiceType st = InformationBase.getI().getService("wfi", "portal", new Hashtable(), new Vector());
		PortalWfiClient pc = (PortalWfiClient) Class.forName(st.getClientObject()).newInstance();
		pc.setServiceURL(st.getServiceUrl());
		pc.setServiceID(st.getServiceID());

		WorkflowRuntimeBean bean = new WorkflowRuntimeBean();
		WorkflowData data = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID);

		new WorkflowSubmitThread(PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID), userID,
				text, notify);
	}

	/**
	 * Rescues a workflow instance
	 * 
	 * @param userID
	 *            - Id of the user
	 * @param workflowID
	 *            - Id
	 * 
	 * @throws java.lang.ClassNotFoundException
	 *             no communication class
	 * @throws java.lang.InstantiationException
	 *             com.class could not be initialized.
	 * @throws java.lang.IllegalAccessException
	 *             could not have access to com.class
	 */
	public void rescue(String userID, String workflowID) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		ServiceType st = InformationBase.getI().getService("wfi", "portal", new Hashtable(), new Vector());
		PortalWfiClient pc = (PortalWfiClient) Class.forName(st.getClientObject()).newInstance();
		pc.setServiceURL(st.getServiceUrl());
		pc.setServiceID(st.getServiceID());

		WorkflowRuntimeBean bean = new WorkflowRuntimeBean(PORTAL, STORAGE, WFS, userID, workflowID, "INSTACE", "zen");

		pc.rescueWorkflow(bean);
	}

	/**
	 * Aborts all runtime instance related to the specificated workflow
	 * 
	 * @param userID
	 *            Id of the user
	 * @param workflowID
	 *            name of the workflow
	 * @throws java.lang.ClassNotFoundException
	 *             no communication class
	 * @throws java.lang.InstantiationException
	 *             com.class could not be initialized.
	 * @throws java.lang.IllegalAccessException
	 *             could not have access to com.class
	 */
	public void abort(String userID, String workflowID) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		ServiceType st = InformationBase.getI().getService("wfi", "portal", new Hashtable(), new Vector());
		PortalWfiClient pc = (PortalWfiClient) Class.forName(st.getClientObject()).newInstance();
		pc.setServiceURL(st.getServiceUrl());
		pc.setServiceID(st.getServiceID());

		// String pPortalID,String pStorageID, String pWfsID, String pUserID,String pWorkflowID, String pInstanceText,
		// String pWorkflowTyp
		WorkflowData workflow = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID);
		Enumeration hashKeys = workflow.getAllRuntimeInstance().keys();
		while (hashKeys.hasMoreElements()) {
			String key = (String) hashKeys.nextElement();

			WorkflowRunTime runtimeobj = (WorkflowRunTime) workflow.getAllRuntimeInstance().get(key);
			pc.abortWorkflow(key);

		}
	}

	/**
	 * Gets the workflow status
	 * 
	 * @param userID
	 *            - id of the user
	 * @param workflowID
	 *            - name of the workflow
	 * @throws java.lang.ClassNotFoundException
	 *             no communication class
	 * @throws java.lang.InstantiationException
	 *             com.class could not be initialized.
	 * @throws java.lang.IllegalAccessException
	 *             could not have access to com.class
	 */
	public String getStatus(String userID, String workflowID) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		ServiceType st = InformationBase.getI().getService("wfi", "portal", new Hashtable(), new Vector());
		PortalWfiClient pc = (PortalWfiClient) Class.forName(st.getClientObject()).newInstance();
		pc.setServiceURL(st.getServiceUrl());
		pc.setServiceID(st.getServiceID());

		Vector<WorkflowInformationBean> workflows_info = pc.getInformation();

		StatusConstants cons = new StatusConstants();
		WorkflowInformationBean wf_info = null;
		for (int i = 0; i < workflows_info.size(); ++i) {

			if (workflows_info.get(i).getWorkflowid().equals(new String(workflowID))) {
				wf_info = workflows_info.get(i);
			}

		}

		if (wf_info == null) {

			return new String("unknown_error");
		} else {

			return cons.getStatus(Integer.toString(wf_info.getStatus()));
		}
	}

	public void test(String userID, String workflowID) {
		try {
			Vector<JobPropertyBean> properties = getWorkflowConfig(userID, workflowID);
			for (JobPropertyBean propertyBean : properties) {
				System.out.println("Desc");
				propertyBean.getDesc().put("TESTKEY", "DESC");
				dumpMap(propertyBean.getDesc());
				System.out.println("Desc0");
				propertyBean.getDesc0().put("TESTKEY", "DESC0");
				dumpMap(propertyBean.getDesc0());
				System.out.println("Exe");
				propertyBean.getExe().put("TESTKEY", "EXE");
				dumpMap(propertyBean.getExe());
				System.out.println("ExeDisabled");
				propertyBean.getExeDisabled().put("TESTKEY", "ExeDis");
				dumpMap(propertyBean.getExeDisabled());
				System.out.println("Inherited");
				propertyBean.getInherited().put("TESTKEY", "Inherited");
				dumpMap(propertyBean.getInherited());
				System.out.println("Label");
				propertyBean.getLabel().put("TESTKEY", "LABEL");
				dumpMap(propertyBean.getLabel());

				System.out.println("Inputs");
				Vector inputs = propertyBean.getInputs();
				for (Object obj : inputs) {

					PortDataBean portBean = (PortDataBean) obj;
					System.out.println("Port " + portBean.getName());
					System.out.println("\tData:");
					dumpMap(portBean.getData());
					System.out.println("\tDataDis:");
					dumpMap(portBean.getDataDisabled());
					System.out.println("\tDesc:");
					dumpMap(portBean.getDesc());
					System.out.println("\tInher:");
					dumpMap(portBean.getInherited());
					System.out.println("\tLabel:");
					dumpMap(portBean.getLabel());
					System.out.println("seq: " + portBean.getSeq());
					System.out.println("txt: " + portBean.getTxt());
					System.out.println("x: " + portBean.getX());
					System.out.println("y: " + portBean.getY());
					System.out.println("id: " + portBean.getId());
					System.out.println("prejob: " + portBean.getPrejob());
					System.out.println("preoutput: " + portBean.getPreoutput());

				}
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void dumpMap(Map map) {
		for (Object key : map.keySet()) {
			Object value = map.get(key);
			System.out.println("\t" + key + " -> " + value);
		}
	}

	/***********************************************
	 * MOSGRID Changes
	 ***********************************************/
	private Map<String, Vector<JobPropertyBean>> tempJobPropertyMap = new HashMap<String, Vector<JobPropertyBean>>();
	private Object TEMP_PROP_LOCK = new Object();
	private boolean autoSave = false;

	public boolean isAutoSave() {
		return autoSave;
	}

	/**
	 * Set auto save mode. If 'true' all changes will be saved immediately but may lead to overhead.
	 */
	public void setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
	}

	/**
	 * Save workflow settings manually if auto save is disabled
	 */
	public void saveWorkflowSettings(String userID, ASMWorkflow wkfInstance) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		Vector<JobPropertyBean> jobs = getConfigData(userID, wkfInstance.getWorkflowName());
		saveConfigData(userID, wkfInstance.getWorkflowName(), jobs);
	}

	/**
	 * Sets the number of expected input files of an input port (parameter sweep)
	 */
	public void setNumberOfInputFiles(String userID, String workflowID, String jobID, String portID, Integer value) {
		try {
			Vector<JobPropertyBean> jobs = getConfigData(userID, workflowID);
			for (JobPropertyBean propertyBean : jobs) {
				if (propertyBean.getName().equals(jobID)) {
					Vector inputs = propertyBean.getInputs();
					for (Object obj : inputs) {
						PortDataBean portBean = (PortDataBean) obj;

						if (portBean.getSeq() == Long.parseLong(portID)) {
							portBean.getData().put("max", value.toString());
							break;
						}
					}
					break;
				}
			}// MoSGrid autosave
			if (autoSave) {
				saveConfigData(userID, workflowID, jobs);
			}
		} catch (ClassNotFoundException e) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, e);
			e.printStackTrace();
		} catch (InstantiationException e) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, e);
			e.printStackTrace();
		}
	}

	/**
	 * Set job_desc
	 * 
	 * 
	 * 
	 * @param prop
	 *            - property name to set ("nodenumber" etc.)
	 * @param value
	 *            - property value
	 * @param userID
	 *            - id of the user who owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 */
	private void setJobDesc(String prop, String value, String userID, String workflowID, String jobID) {
		try {
			Vector<JobPropertyBean> jobs = getConfigData(userID, workflowID);

			for (JobPropertyBean j : jobs) {
				if (j.getName().equals(jobID)) {
					// j.getExe().put(prop, value);
					j.getDesc().put(prop, value);
					System.out.println("job : " + j.getName() + " set job property:" + prop + " value: " + value);
					break;
				}
			}// MoSGrid autosave
			if (autoSave) {
				saveConfigData(userID, workflowID, jobs);
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
			Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Set walltime
	 * 
	 * @param value
	 *            - walltime in ?
	 * @param userID
	 *            - id of the user who owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 */
	public void setWalltime(String userID, String workflowID, String jobID, String value) {
		setJobDesc("unicore.keyWalltime", value, userID, workflowID, jobID);
	}

	/**
	 * Set number of cores
	 * 
	 * @param value
	 *            - number of cores
	 * @param userID
	 *            - id of the user who owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 */
	public void setCoreNumber(String userID, String workflowID, String jobID, String value) {
		setJobDesc("unicore.keyCores", value, userID, workflowID, jobID);
	}

	/**
	 * Set memory
	 * 
	 * @param value
	 *            - memory in ?
	 * @param userID
	 *            - id of the user who owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 */
	public void setMemory(String userID, String workflowID, String jobID, String value) {
		setJobDesc("unicore.keyMemory", value, userID, workflowID, jobID);
	}

	/**
	 * Set workflow name
	 * 
	 * @param value
	 *            - name of the workflow
	 * @param userID
	 *            - id of the user who owns the workflow
	 * @param workflowID
	 *            - id of the workflow
	 * @param jobID
	 *            - id of the job
	 */
	public void setWorkflowName(String userID, String workflowID, String jobID, String value) {
		setJobDesc("unicore.keyWorkflowName", value, userID, workflowID, jobID);
	}
}

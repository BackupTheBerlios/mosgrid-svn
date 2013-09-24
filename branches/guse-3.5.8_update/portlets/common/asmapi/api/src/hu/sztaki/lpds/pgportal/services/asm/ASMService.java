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

import hu.sztaki.lpds.pgportal.services.asm.beans.ASMSQLQueryBean;
import hu.sztaki.lpds.pgportal.services.asm.threads.ASMUploadThread;
import hu.sztaki.lpds.pgportal.services.asm.constants.StatusConstants;
import hu.sztaki.lpds.pgportal.services.asm.constants.StatusColorConstants;
import hu.sztaki.lpds.pgportal.services.asm.constants.DownloadTypeConstants;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMRepositoryItemBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.WorkflowInstanceStatusBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.WorkflowInstanceBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.RunningJobDetailsBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.JobStatisticsBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMJobInstanceBean;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.com.WorkflowAbortThread;
import hu.sztaki.lpds.pgportal.com.WorkflowRescueThread;
import hu.sztaki.lpds.pgportal.com.WorkflowSubmitThread;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.UserData;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowRunTime;
import hu.sztaki.lpds.pgportal.service.workflow.RealWorkflowUtils;
import hu.sztaki.lpds.pgportal.service.workflow.Sorter;
import hu.sztaki.lpds.pgportal.service.workflow.UserQuotaUtils;
import hu.sztaki.lpds.pgportal.service.workflow.WorkflowUpDownloadUtils;
import hu.sztaki.lpds.pgportal.services.asm.beans.ASMResourceBean;
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
import hu.sztaki.lpds.wfi.net.webservices.StatusInfoBean;
import hu.sztaki.lpds.wfi.net.webservices.StatusIntervalBean;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
 * @author akosbalasko
 * @version 3.4
 *
 */
public class ASMService {

    //this key in hashMap workflow is the userID, and it contains all workflows(organized in an other hashmap,
    //where key is the name of the workflow, its value is the workflow object )
    // this map cannot be simply a concurrentmap, because the operations made on them are non-atomic:
	// if (workflow.contains(key)) {
	//     ...
	// } else {
	//     ...
	// }
    private Map<String, Map<String, ASMWorkflow>> workflows;
    // since we will operations from several threads on the workflow map, we need to synchronize access
    private ReadWriteLock workflowLock;
    public String GEMLCA = "gemlca";
    private static ASMService instance = new ASMService();
    
    // we don't want to force users of asm to use log4j or anything like that
    // simply use the following way to do logging here:
    // if (DEBUG_MODE) {
    //     debug("something weird");
    // }
    // since it's a final static variable, when made false and recompiled
    // all logging code will be stripped out!
    // introduced by Luis de la Garza (MoSGrid)
    private final static boolean DEBUG_MODE = false;
    
    /**
     * Public function to provide Singleton mechanism
     *
     * @return stored or new object of ifself
     */
    public static ASMService getInstance() {
        return instance;
    }

    /**
     * Protected constructor function
     *
     */
    protected ASMService() {
    	// multiple threads can access this at the same time... HashMap is not thread-safe
    	// so we will need to protect it with a read-write lock
        workflows = new HashMap<String, Map<String,ASMWorkflow>>();
        workflowLock = new ReentrantReadWriteLock(false);
    }
    
    private Map<String, ASMWorkflow> getUserASMWorkflows(final String userId) {
    	if (DEBUG_MODE) {
    		debug("getUserASMWorkflows", "Obtaining ASMWorfklows for user=" + userId);
    	}
    	final Lock readLock = workflowLock.readLock();
    	Map<String, ASMWorkflow> userWorkflows = null;
    	readLock.lock();
    	try {
    		userWorkflows = workflows.get(userId); 
    	} finally {
    		readLock.unlock();
    	}
    	if (userWorkflows == null) {
    		// we need to write to the map
    		final Lock writeLock = workflowLock.writeLock();
    		writeLock.lock();
    		try {
    			// double check! always double check, it could be that another thread beat us to it!
    			userWorkflows = workflows.get(userId);
    			if (userWorkflows == null) {
    				// note how the instance of the map is a concurrent hashmap... different threads
    				// might access this map, one of them putting information and one of them reading information
    				if (DEBUG_MODE) {
    		    		debug("getUserASMWorkflows", "refreshing workflows for user " + userId);
    		    	}
    	            loadASMWorkflowsFromPortalCacheForUser(userId);
    	            userWorkflows = workflows.get(userId);
    			} 
    		} finally {
    			writeLock.unlock();
    		}
    	}
    	if (DEBUG_MODE) {
    		debug("getUserASMWorkflows", "Obtained " + userWorkflows.size() + " workflows for user " + userId);
    	}
    	return userWorkflows;
    }

    /**
     * 
     * Adding a workflow
     *
     * @param userId
     * @param workflow
     */
    private void putWorkflow(String userId, ASMWorkflow workflow) {
    	getUserASMWorkflows(userId).put(workflow.getWorkflowName(), workflow);
    }

    private void removeWorkflowFromUser(String userID, String workflowName) {
    	// no need to use the write lock, since we are not updating outer map, just the map containing the workflows of a single user
    	// and it's a ConcurrentHashMap anyway
    	// however, we are READING from the workflowLock!
   		Map<String, ASMWorkflow> userWorkflows = null;
   		final Lock readLock = workflowLock.readLock();
   		readLock.lock();
   		try {
   			userWorkflows = workflows.get(userID);
   		} finally {
   			readLock.unlock();
   		}
   		if (userWorkflows != null) {
   			userWorkflows.remove(workflowName);
   		} else {
   			// YOLO!
   		}
    }

    @Deprecated
    public void init() {
    }

    public String getPortalURL() {
        return PropertyLoader.getInstance().getProperty("service.url");
    }

    public String getWFSURL() {
        ServiceType st = InformationBase.getI().getService("wfs", "portal", new Hashtable(), new Vector());
        return st.getServiceUrl();

    }
    public String getStorageURL() {
        ServiceType st = InformationBase.getI().getService("storage", "portal", new Hashtable(), new Vector());
        return st.getServiceUrl();

    }
    
    /*
     * Gets the ASM workflow what is identified by workflowname, returns null if workflow does not exists with the specified name
     * @param userId ID of the user
     * @param workflowname name of the ASM workflow
     * @return ASMWorkflow object
     */

    public ASMWorkflow getASMWorkflow(String userId, String workflowname) {
    	return getUserASMWorkflows(userId).get(workflowname);    	
    }

    // basically, the "expensive" operation of retrieving workflows from the wfs service
    private void loadASMWorkflowsFromPortalCacheForUser(String userId) {
    	// force a write into workflows
    	final Lock writeLock = workflowLock.writeLock();
    	writeLock.lock();
        try {
        	// load from portal cache
            final HashMap<String, ASMWorkflow> storedworkflows = getWorkflows(userId);
            workflows.put(userId, storedworkflows);
            for (final ASMWorkflow asmWorkflow : storedworkflows.values()) {
                updateASMWorkflowStatus(asmWorkflow);
            }
        } catch (ClassNotFoundException ex) {
            throw new ASM_GeneralWebServiceException(ex.getCause(), userId);
        } catch (InstantiationException ex) {
            throw new ASM_GeneralWebServiceException(ex.getCause(), userId);
        } catch (IllegalAccessException ex) {
            throw new ASM_GeneralWebServiceException(ex.getCause(), userId);
        } finally {
        	writeLock.unlock();
        }
    }

    /**
     * Imports a workflow/application/project/graph stored in the local Repository component of gUSE
     * @param userId - Id of the user
     * @param userworkflowname - name of the workflow given by the user
     * @param ownerId - Id of the owner of the workflow that should be imported
     * @param impWfType - Type of the workflow (see ASMRepositoryItemType object)
     * @param importworkflowName - Name of the workflow to be imported
     * @return String - name of the generated workflow
     */
    public String ImportWorkflow(String userId, String userworkflowname, String ownerId, String impWfType, String importworkflowName) {
        try {
            ArrayList<RepositoryWorkflowBean> wfList = getWorkflowsFromRepository2Array(ownerId, impWfType, new Long(importworkflowName));

            RepositoryWorkflowBean selectedBean = (RepositoryWorkflowBean) wfList.get(0);
            if (selectedBean == null) {
                throw new Import_NotValidWorkflowNameException(userId, importworkflowName);

            }


            String storageID = WorkflowUpDownloadUtils.getInstance().getStorageID();
            String wfsID = WorkflowUpDownloadUtils.getInstance().getWfsID();
            String portalUrl = this.getPortalURL();

            selectedBean.setPortalID(portalUrl);
            selectedBean.setStorageID(storageID);
            selectedBean.setWfsID(wfsID);
            selectedBean.setUserID(userId);
            //String generated_id = Long.toString(System.currentTimeMillis());
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
            PortalRepositoryClient repoClient = (PortalRepositoryClient) Class.forName(st.getClientObject()).newInstance();
            repoClient.setServiceURL(st.getServiceUrl());
            repoClient.setServiceID(st.getServiceID());
            String retStr = repoClient.importWorkflow(selectedBean);

            // updating ASMs in memory
            ASMWorkflow workflow = null;
            Enumeration workflow_enum = PortalCacheService.getInstance().getUser(userId).getWorkflows().keys();
            while (workflow_enum.hasMoreElements()) {
                WorkflowData act_data = ((WorkflowData) PortalCacheService.getInstance().getUser(userId).getWorkflows().get(workflow_enum.nextElement()));

                if (act_data.getWorkflowID().contains(new String(concrete_wf_name))) {


                    workflow = this.getRealASMWorkflow(userId, act_data.getWorkflowID());
                    concrete_wf_name = act_data.getWorkflowID();
                }
            }
            if (workflow == null) {
                throw new Import_FailedException(userId, userworkflowname);

            } else {

                putWorkflow(userId, workflow);
                updateASMWorkflowStatus(workflow);


            }


            return concrete_wf_name;



        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new ASM_GeneralWebServiceException(ex.getCause(), userId, userworkflowname);

        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw new ASM_GeneralWebServiceException(ex.getCause(), userId, userworkflowname);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ASM_GeneralWebServiceException(ex.getCause(), userId, userworkflowname);
        }

    }

    /**
     * Modifies remote input file's path in a specified port
     * @param userID ID of the user
     * @param workflowID ID of the Workflow
     * @param jobID ID of the Job
     * @param portID ID of the port
     * @param newRemotePath Remote file path what's to be set on the specified workflow/job/port
     */
    public void setRemoteInputPath(String userID, String workflowName, String jobName, String portNumber, String newRemotePath) {
        setRemotePath(userID, workflowName, jobName, portNumber, newRemotePath, "input");
    }

    /**
     * Returns the remote input's path that is adjusted for a specified port
     * @param userID ID of the user
     * @param workflowID ID of the Workflow
     * @param jobID ID of the Job
     * @param portID ID of the port
     * @return String remote path
     */
    public String getRemoteInputPath(String userID, String workflowName, String jobName, String portNumber) {
        return getRemotePath(userID, workflowName, jobName, portNumber, "input");
    }

    /**
     * Modifies remote file's path in a specified port
     * @param userID ID of the user
     * @param workflowID ID of the Workflow
     * @param jobID ID of the Job
     * @param portID ID of the port
     * @param newRemotePath Remote file path what's to be set on the specified workflow/job/port
     */
    public void setRemoteOutputPath(String userID, String workflowName, String jobName, String portNumber, String newRemotePath) {
        setRemotePath(userID, workflowName, jobName, portNumber, newRemotePath, "output");
    }

    /**
     * Returns the remote output's path that is adjusted for a specified port
     * @param userID ID of the user
     * @param workflowID ID of the Workflow
     * @param jobID ID of the Job
     * @param portID ID of the port
     * @return String remote path
     */
    public String getRemoteOutputPath(String userID, String workflowName, String jobName, String portNumber) {
        return getRemotePath(userID, workflowName, jobName, portNumber, "output");
    }

    private String getRemotePath(String userID, String workflowName, String jobName, String portNumber, String io) {
        try {
            Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, workflowName);
            if (this.CheckPortType(jobs, "remote", jobName, portNumber)) {
                for (JobPropertyBean j : jobs) {
                    //System.out.println("jobname i get is : " +j.getName());
                    if (j.getName().equals(new String(jobName))) {
                        Vector<PortDataBean> ports = null;
                        if (io.equals("input")) {
                            ports = j.getInputs();
                        } else {
                            ports = j.getOutputs();
                        }
                        for (PortDataBean p : ports) {
                            //Iterator keys = p.getData().keySet().iterator();
                            if (p.getSeq() == Long.parseLong(portNumber)) {
                                return (String) p.getData().get("remote");
                            }
                        }
                    }
                }
                // MoSGrid auto save
                if (autoSave) {
    				this.saveConfigData(userID, workflowName, jobs);
    			}
            //this.saveConfigData(userID, workflowID, jobs);
            }
        } catch (Exception e) {
            throw new ASMException("Getting remote file path on " + workflowName + " " + jobName + " " + portNumber + " " + "failed.");

        }
        return null;

    }
    /**
     * Returns the number of inputs stored in paramInputs.zip, if it has not been set, the method returns null
     * @param userID -ID of the user
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     * @param portNumber - port number (1..16)
     * @return - the number of the input files compressed as paramInputs.zip, if it does not exists, it returns null
     */
    public Integer getNumberOfInputs(String userID, String workflowName, String jobName, Integer portNumber){
        Integer numberOfInputs = null;
        try {
            Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, workflowName);
            for (JobPropertyBean j :jobs){
                if (j.getName().equals(new String(jobName))) {
                        Vector<PortDataBean> ports = null;
                        ports = j.getInputs();
                        for (PortDataBean p : ports) {
                            //Iterator keys = p.getData().keySet().iterator();
                            if (p.getSeq() == portNumber) {
                                numberOfInputs=Integer.parseInt((String)p.getData().get("max"));
                            }
                        }
                    }
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numberOfInputs;
    }

    /**
     * Sets the number of inputs stored within paramInputs.zip
     *
     * @param userID - ID of the given user
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     * @param portNumber - port number (0..16)
     * @param numberOfInputs - the number to be set
     */
    public void setNumberOfInputs(String userID, String workflowName, String jobName, Integer portNumber, Integer numberOfInputs){        
        try {
            Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, workflowName);
            for (JobPropertyBean j :jobs){
                if (j.getName().equals(new String(jobName))) {
                        Vector<PortDataBean> ports = null;
                        ports = j.getInputs();
                        for (PortDataBean p : ports) {
                            //Iterator keys = p.getData().keySet().iterator();
                            if (p.getSeq() == portNumber) {
                                p.getData().put("max", numberOfInputs.toString());
                                break;
                            }
                        }
                     break;
                    }

            }
            this.saveConfigData(userID, workflowName, jobs);

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void setRemotePath(String userID, String workflowName, String jobName, String portNumber, String newRemotePath, String io) {
        try {
            Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, workflowName);
            if (this.CheckPortType(jobs, "remote", jobName, portNumber)) {
                for (JobPropertyBean j : jobs) {
                    //System.out.println("jobname i get is : " +j.getName());
                    if (j.getName().equals(new String(jobName))) {
                        Vector<PortDataBean> ports = null;
                        if (io.equals("input")) {
                            ports = j.getInputs();
                        } else {
                            ports = j.getOutputs();
                        }
                        for (PortDataBean p : ports) {
                            //Iterator keys = p.getData().keySet().iterator();
                            if (p.getSeq() == Long.parseLong(portNumber)) {
                                p.getData().put("remote", newRemotePath);
                                break;
                            }
                        }
                        break;
                    }

                }
                // MoSGrid autosave
                if (autoSave) {
                	this.saveConfigData(userID, workflowName, jobs);
                }
            }
        } catch (Exception e) {
            throw new ASMException("Setting remote file path on " + workflowName + " " + jobName + " " + portNumber + " to " + newRemotePath + " failed.");

        }
    }

    /**
     * Returns the name of the workflow embededd as a job(jobId) in the workflow (workflowId)
     * if the job has different type ( single job, web-service) this method returns null
     *
     * @param userId
     * @param workflowId
     * @param jobId
     * @return
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public String getEmbeddedWorkflowName(String userId, String workflowName, String jobName) throws ClassNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        Vector<JobPropertyBean> jobs = this.getWorkflowConfig(userId, workflowName);
        for (JobPropertyBean job : jobs) {
            if (job.getName().equals(jobName) && ((String) job.getExe().get("jobistype")).equals("workflow")) {
                //jobistype=workflow
                return job.getExe().get("iworkflow").toString();
            }
        }

        return null;

    }

    /**
     *
     * Getting ASM related workflows
     *
     * @param userId - id of the user
     * @return - ArrayList <ASMWorkflow> : String is the name of the workflow
     */
    public ArrayList<ASMWorkflow> getASMWorkflows(String userId) {
    	if (DEBUG_MODE) {
    		debug("getASMWorkflows", "retrieving all ASM workflows for user=" + userId);
    	}
    	Map<String, ASMWorkflow> userWorkflowsMap = getUserASMWorkflows(userId);
    	final Collection<ASMWorkflow> userWorkflows = userWorkflowsMap.values(); 
        for (final ASMWorkflow current : userWorkflows) {
//            String concreteWfName = current.getWorkflowName();
//            if (getASMWorkflow(userId, concreteWfName) != null) {
              updateASMWorkflowStatus(current);
              updateASMWorkflowStatistics(current);
//            } else {
//                userWorkflowsMap.remove(current);
//            }
        }
        return new ArrayList<ASMWorkflow>(userWorkflows);
    }

//    private void updateASMWorkflowStatus(String userId, String concrete_wf_name) {
//        WorkflowInstanceStatusBean statusbean = this.getWorkflowStatus(userId, concrete_wf_name);
//        this.getASMWorkflow(userId, concrete_wf_name).setStatusbean(statusbean);
//    }

//    private void updateASMWorkflowStatistics(String userId, String concreteWfName) {
//        JobStatisticsBean statbean = getWorkflowStatistics(userId, concreteWfName);
//        this.getASMWorkflow(userId, concreteWfName).setStatisticsBean(statbean);
//    }
    
    private void updateASMWorkflowStatus(final ASMWorkflow workflow) {
        WorkflowInstanceStatusBean statusbean = getWorkflowStatus(workflow.getUserID(), workflow.getWorkflowName());
        workflow.setStatusbean(statusbean);
    }

    private void updateASMWorkflowStatistics(final ASMWorkflow workflow) {
        JobStatisticsBean statbean = getWorkflowStatistics(workflow.getUserID(), workflow.getWorkflowName());
        workflow.setStatisticsBean(statbean);
    }

    private JobStatisticsBean getWorkflowStatistics(String userID, String workflowName) {
        JobStatisticsBean statBean = new JobStatisticsBean();
        int finishedjobs = 0;
        int errorjobs = 0;
        // getting jobs statuses
        String runtimeID = getRuntimeID(userID, workflowName);
        if (runtimeID != null) {
            // setting number of finished/error jobs!!!!
            ConcurrentHashMap<String, WorkflowData> workflows = PortalCacheService.getInstance().getUser(userID).getWorkflows();
            WorkflowData wrk_data = workflows.get(workflowName);

            long finishedJobNumber = wrk_data.getFinishedStatus();
            long submittedJobNumber = wrk_data.getSubmittedStatus();
            long errorJobNumber = wrk_data.getErrorStatus(); // errorstatus fails!!!!!
            long runningJobNumber = wrk_data.getRunningStatus();
            //long estimatedJobNumber = wrk_data.getErrorStatus()+wrk_data.getFinishedStatus()+wrk_data.getRunningStatus()+wrk_data.getSubmittedStatus()+wrk_data.getSuspendStatus();


            statBean.setNumberOfJobsInError(errorJobNumber);
            statBean.setNumberOfJobsInSubmitted(submittedJobNumber);
            statBean.setNumberOfJobsInRunning(runningJobNumber);
            statBean.setNumberOfJobsInFinished(finishedJobNumber);
            

        }
        return statBean;
    }

    /**
     *
     * Gets and returns detailed informations about a workflow (e.g. statuses of the current workflow instance, overall statistics)
     *
     * @param userID - ID of the user
     * @param workflowID - ID of the workflow
     * @return WorkflowInstanceBean object that contains information
     * @throws ASM_NoValidRuntimeIDException -it's thrown if there is No valid runtime ID
     */
    public WorkflowInstanceBean getDetails(String userID, String workflowName) throws ASM_NoValidRuntimeIDException {
        WorkflowInstanceBean workflowinstance = new WorkflowInstanceBean();
        String runtimeID = (String) PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName).getAllRuntimeInstance().keys().nextElement();
        if (runtimeID != null) {

            Vector<StatusInfoBean> retVector = new Vector<StatusInfoBean>();
           
            Hashtable prp = new Hashtable();
            prp.put("url", PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName).getWfsID());
            ServiceType st = InformationBase.getI().getService("wfs", "portal", prp, new Vector());
            try {
                PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                retVector = pc.getInfo(runtimeID);
                
                //Hashtable<String, Hashtable<String, String>> jobinstances = runtimedata.getCollectionJobsStatus();
                for (StatusInfoBean jobStatus : retVector) {

                    String jobName = (String) jobStatus.getJobname();

                    RunningJobDetailsBean jobinstance = new RunningJobDetailsBean();
                    jobinstance.setName(jobName);
                    // setting jobs in init
                    jobinstance.getStatisticsBean().setNumberOfJobsInInit(jobStatus.getInit());
                    jobinstance.getStatisticsBean().setNumberOfJobsInError(jobStatus.getError());
                    jobinstance.getStatisticsBean().setNumberOfJobsInRunning(jobStatus.getRunning());
                    jobinstance.getStatisticsBean().setNumberOfJobsInSubmitted(jobStatus.getSubmit());
                    jobinstance.getStatisticsBean().setNumberOfJobsInFinished(jobStatus.getFinish());

                    jobinstance.getInstances().addAll(getJobInstanceDetails(userID, workflowName, runtimeID, jobName).values());



                    workflowinstance.getJobs().add(jobinstance);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            throw new ASM_NoValidRuntimeIDException();
        }

        return workflowinstance;
    }
    /*
     * Returns the list of job instances ordered by pID
     *
     */

    private HashMap<String, ASMJobInstanceBean> getJobInstanceDetails(String userId, String workflowId, String runtimeId, String jobName) {

        Vector<StatusIntervalBean> details = new Vector<StatusIntervalBean>();
        HashMap<String, ASMJobInstanceBean> jobInstanceDetails = new HashMap();

        Hashtable prp = new Hashtable();
        prp.put("url", PortalCacheService.getInstance().getUser(userId).getWorkflow(workflowId).getWfsID());
        ServiceType st = InformationBase.getI().getService("wfs", "portal", prp, new Vector());
        try {
            PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            ArrayList<String> availableStats = new ArrayList();
            availableStats.add(StatusConstants.INIT);
            availableStats.add(StatusConstants.SUBMITTED);
            availableStats.add(StatusConstants.RUNNING);
            availableStats.add(StatusConstants.ERROR);
            availableStats.add(StatusConstants.FINISHED);
            for (String stat : availableStats) {
                
                try {
                    details = pc.getJobStatusInfo(runtimeId, jobName, stat, 0, 2147483647);
                    for (StatusIntervalBean bean : details) {
                        ASMJobInstanceBean instance = new ASMJobInstanceBean();
                        instance.setUsedResource(bean.getResource());
                        long pID = bean.getStart();
                        instance.setPid(Long.toString(pID));
                        instance.setStatus(stat);
                        instance.setJobId(jobName);
                        instance.setRuntimeID(runtimeId);
                        instance.setWorkflowId(workflowId);
                        instance.setUserId(userId);
                        
                        instance.setStdOutputPath(getStdOutputFilePath(userId, workflowId, jobName, pID));
                        instance.setStdErrorPath(getStdErrorFilePath(userId, workflowId, jobName, pID));
                        instance.setLogBookPath(getLogBookFilePath(userId, workflowId, jobName, pID));

                        jobInstanceDetails.put(Long.toString(bean.getStart()), instance);
                    }
                }catch(NullPointerException ex) // no job instance available in the given state
                {
                    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobInstanceDetails;
    }

    /**
     * Gets the command line arguments of a specified job in a specified workflow
     *
     *
     * @param userID - Id of the user
     * @param selected_concrete - name of the workflow
     * @param selected_job - name of the job
     * @return - command line argument
     */
    public synchronized String getCommandLineArg(String userID, String selected_concrete, String selected_job) {
        String actual_param = "";
        try {
            Vector wfconfig = getWorkflowConfig(userID, selected_concrete);
            for (int i = 0; i < wfconfig.size(); ++i) {
                JobPropertyBean jobprop = (JobPropertyBean) wfconfig.get(i);

                if (jobprop.getName().equals(new String(selected_job))) {
                    actual_param = (String) jobprop.getExe().get("params");
                    return actual_param;
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
     *
     * Sets the command line argument of a specified job
     *
     * @param userId - id of the user
     * @param selected_concrete - name of the workflow
     * @param selected_job - name of the job
     * @param commandline - string to be set as command line argument
     */
    public synchronized void setCommandLineArg(String userId, String selected_concrete, String selected_job, String commandline) {
        //System.out.println("setting command line arg : concrete " + selected_concrete + "... job... : " + selected_job + " ... command line : " + commandline);
        try {
            Vector<JobPropertyBean> jobs = getWorkflowConfig(userId, selected_concrete);
            Vector<JobPropertyBean> new_jobs = new Vector<JobPropertyBean>();
            for (int i = 0; i < jobs.size(); ++i) {
                JobPropertyBean actjob = jobs.get(i);

                //System.out.println("job : " + actjob.getName() + " txt is : " + actjob.getTxt() + " param is : " + actjob.getExe().get("params"));
                if (actjob.getName().equals(new String(selected_job))) {
                    actjob.getExe().put("params", commandline);
                //System.out.println("Saving job command line arguments: " + actjob.getExe().get("params"));
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
    }

    /**
     * Returns the number of the required nodes of a given job in a given workflow.
     * If type of the job is not MPI, "NotMPIJobException" will be thrown
     * @param userID - user ID
     * @param workflowID - workflow ID
     * @param jobID - job ID
     * @return - Number of the nodes as string
     * @throws hu.sztaki.lpds.pgportal.services.asm.exceptions.general.NotMPIJobException
     */
    public synchronized String getNodeNumber(String userID, String workflowName, String jobName) throws NotMPIJobException {
        String actual_param = "";
        try {


            Vector wfconfig = getWorkflowConfig(userID, workflowName);
            for (int i = 0; i < wfconfig.size(); ++i) {
                JobPropertyBean jobprop = (JobPropertyBean) wfconfig.get(i);

                if (jobprop.getName().equals(new String(jobName))) {
                    if ("MPI".equals(jobprop.getExe().get("type"))) {
                        actual_param = (String) jobprop.getExe().get("nodenumber");
                    } else {
                        throw new NotMPIJobException();
                    }
                }
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
     * @param userId - user ID
     * @param workflowID - workflow ID
     * @param jobID - job ID
     * @param nodenumber - nodenumber to be set to the job
     * @throws hu.sztaki.lpds.pgportal.services.asm.exceptions.general.NotMPIJobException
     */
    public synchronized void setNodeNumber(String userId, String workflowName, String jobName, int nodenumber) throws NotMPIJobException {
        //System.out.println("setting command line arg : concrete " + selected_concrete + "... job... : " + selected_job + " ... command line : " + commandline);
        try {
            Vector<JobPropertyBean> jobs = getWorkflowConfig(userId, workflowName);
            Vector<JobPropertyBean> new_jobs = new Vector<JobPropertyBean>();
            for (int i = 0; i < jobs.size(); ++i) {
                JobPropertyBean actjob = jobs.get(i);

                //System.out.println("job : " + actjob.getName() + " txt is : " + actjob.getTxt() + " param is : " + actjob.getExe().get("params"));
                if (actjob.getName().equals(new String(jobName))) {
                    if ("MPI".equals(actjob.getExe().get("type"))) {
                        actjob.getExe().put("nodenumber", Integer.toString(nodenumber));
                    } else {
                        throw new NotMPIJobException(userId, workflowName, jobName);
                    }
                //System.out.println("Saving job command line arguments: " + actjob.getExe().get("params"));
                }
                new_jobs.add(actjob);
            }
            // MoSGrid autosave
            if (autoSave) {
            	saveConfigData(userId, workflowName, new_jobs);
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
     * Deletes a workflow
     * @param userID - id of the user which owns the workflow
     * @param workflowID - id of the workflow
     */
    public void DeleteWorkflow(String userID, String workflowName) {
        // deleting from ASM
        String portalUrl = this.getPortalURL();
        ASMWorkflow inst = getASMWorkflow(userID, workflowName);
        removeWorkflowFromUser(userID, workflowName);

        WorkflowData wData = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName);
        final Hashtable hsh = new Hashtable();
        ServiceType st;
        PortalWfsClient pc = null;
        //storage
        try {
            hsh.put("url", wData.getStorageID());
            st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
            PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
            ps.setServiceURL(st.getServiceUrl());
            ps.setServiceID(st.getServiceID());

            ComDataBean tmp = new ComDataBean();
            
            tmp.setPortalID(portalUrl);
            tmp.setUserID(userID);
            tmp.setWorkflowID(wData.getWorkflowID());

            ps.deleteWorkflow(tmp);


            Enumeration wfenm = PortalCacheService.getInstance().getUser(userID).getTemplateWorkflows().keys();
            while (wfenm.hasMoreElements()) {
                String wfkey = "" + wfenm.nextElement();
                if (PortalCacheService.getInstance().getUser(userID).getTemplateWorkflow(wfkey).getGraf().equals(wData.getGraf())) {

                    //delete template workflow
                    ComDataBean template_tmp = new ComDataBean();
                    
                    template_tmp.setPortalID(portalUrl);
                    template_tmp.setUserID(userID);
                    template_tmp.setWorkflowID(wfkey);
                    //System.out.println("Deleting " + wfkey + " from storage ");
                    ps.deleteWorkflow(template_tmp);

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        //wfs
        hsh.clear();
        hsh.put("url", wData.getWfsID());
        st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());

        try {
            pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp = new ComDataBean();
            
            tmp.setPortalID(portalUrl);
            tmp.setUserID(userID);
            tmp.setWorkflowID(wData.getWorkflowID());
            pc.deleteWorkflow(tmp);

            //delete from timing workflow list
            PortalCacheService.getInstance().getUser(userID).deleteWorkflow(wData.getWorkflowID());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // delete template
        //System.out.println("template is : " + wData.getTemplate());
        WorkflowData temp_data = PortalCacheService.getInstance().getUser(userID).getTemplateWorkflow(wData.getTemplate());
        try {
            ComDataBean template_tmp = new ComDataBean();
            
            template_tmp.setPortalID(portalUrl);
            template_tmp.setUserID(userID);
            template_tmp.setWorkflowID(wData.getTemplate());
            pc.deleteWorkflow(template_tmp);

            //delete from timing workflow list
            PortalCacheService.getInstance().getUser(userID).deleteWorkflow(wData.getWorkflowID());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // delete template workflow from portal cache
        Enumeration wfenm = PortalCacheService.getInstance().getUser(userID).getTemplateWorkflows().keys();
        String key = "";
        while (wfenm.hasMoreElements()) {
            key = "" + wfenm.nextElement();
            if (PortalCacheService.getInstance().getUser(userID).getTemplateWorkflow(key).getGraf().equals(workflowName)) {
                // delete template workflow from wfs
                hsh.clear();

                st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
                try {
                    pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
                    pc.setServiceURL(st.getServiceUrl());
                    pc.setServiceID(st.getServiceID());
                    ComDataBean tmp = new ComDataBean();
                    
                    tmp.setPortalID(portalUrl);
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
                cmd.setWorkflowID(workflowName);
                cmd.setUserID(userID);
                
                cmd.setPortalID(portalUrl);

                pc.deleteWorkflowGraf(cmd);

            }
        }

        // delete graf workflow from portal cache
        PortalCacheService.getInstance().getUser(userID).getAbstactWorkflows().remove(workflowName);
        //
        PortalCacheService.getInstance().getUser(userID).deleteWorkflow(wData.getWorkflowID());
    }

    private String getPortID(String userID, String workflowName, String jobName, String port) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        try {
            Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, workflowName);
            for (JobPropertyBean j : jobs) {
                //System.out.println("jobname i get is : " +j.getName());
                if (j.getName().equals(new String(jobName))) {
                    for (PortDataBean p : (Vector<PortDataBean>) j.getInputs()) {
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

        throw new ASM_NoMatchingPortIDException(userID, workflowName);
    }

    /**
     *
     * Uploads a file from the user's local machine to the *portal* server only.
     * It won't store the file in the Storage component of gUSE, and won't update database under WFS component
     *
     * @param file - file to upload (can be get from the ActionRequest)
     * @param userID - ID of the user
     * @param filename - the file should be placed using this name
     * @return the uploaded file stored on the portal server
     * @throws Exception
     */
    public File uploadFiletoPortalServer(FileItem file, String userID, String filename) throws Exception {
        File serverSideFile = null;
        try {
            String tempDir = System.getProperty("java.io.tmpdir") + "/uploads/" + userID;
            File f = new File(tempDir);
            if (!f.exists()) {
                f.mkdirs();
            }
            serverSideFile = new File(tempDir, filename);
            file.write(serverSideFile);
            file.delete();


        } catch (FileUploadException fue) {
            throw new Upload_ErrorDuringUploadException(fue.getCause(), userID);
        } catch (Exception e) {
            throw new Upload_GeneralException(e.getCause(), userID);
        }
        return serverSideFile;
    }

    /**
     * Sets a resource for a job specified in arguments
     *
     * @param userID - id of the user"szerintem ez egy free attr
     * @param workflowID - id of the workflow
     * @param jobID - id of the job
     * @param DCI - name of the DCI (glite, pbs etc)
     * @param resource - name of the resource
     * @param queue - name of the queue
     */
    public void setResource(String userID, String workflowName, String jobName, String type, String grid, String resource, String queue) {
        try {
            Vector<JobPropertyBean> workflowconfig = this.getWorkflowConfig(userID, workflowName);
            for (JobPropertyBean jobprop : workflowconfig) {
                //System.out.println("JobID : " + jobprop.getId());
                if (jobprop.getName().equals(jobName)) {

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
         		this.saveConfigData(userID, workflowName, workflowconfig);
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
     * @param userID - ID of the user
     * @param workflowID - ID of the workflow
     * @param jobID - ID of the job
     * @return - an ASMResourceBean object or null in any case of errors
     */
    public ASMResourceBean getResource(String userID, String workflowName, String jobName) {

        try {
            Vector<JobPropertyBean> workflowconfig = this.getWorkflowConfig(userID, workflowName);
            for (JobPropertyBean jobprop : workflowconfig) {
                //System.out.println("JobID : " + jobprop.getId());
                if (jobprop.getName().equals(jobName)) {
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
     * 
     ** Gets an input value on a port, if and only if it has set to be "input" type
     *
     * @param inputValue - value to be set to the input port
     * @param userID - id of the user
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     * @param portNumber - number of the port
     * @return input value
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public String getInputValue(String userID, String workflowName, String jobName, String portNumber) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        System.out.println("Given parameters are: userId(" + userID + ") workflowID(" + workflowName + ") jobID(" + jobName + ") portNumber(" + portNumber + ")");

        Vector<JobPropertyBean> workflowconfig = this.getWorkflowConfig(userID, workflowName);
        if (CheckPortType(workflowconfig, "value", jobName, portNumber)) {

            for (JobPropertyBean jobprop : workflowconfig) {
                //System.out.println("JobID : " + jobprop.getId());
                if (jobprop.getName().equals(jobName)) {
                    PortDataBean portProperty = getPortbySeq(jobprop, portNumber);
                    return portProperty.getData().get("value").toString();
                }

            }
        }
        return null;
    }

    /**
     *
     * Sets an input value on a port, if and only if it has set to be "input" type
     *
     * @param inputValue - value to be set to the input port
     * @param userID - id of the user
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     * @param portNumber - number of the port
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    public void setInputValue(String inputValue, String userID, String workflowName, String jobName, String portNumber) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Vector<JobPropertyBean> workflowconfig = this.getWorkflowConfig(userID, workflowName);
        if (CheckPortType(workflowconfig, "value", jobName, portNumber)) {

            for (JobPropertyBean jobprop : workflowconfig) {
                //System.out.println("JobID : " + jobprop.getId());
                if (jobprop.getName().equals(jobName)) {
                    PortDataBean portProperty = getPortbySeq(jobprop, portNumber);
                    jobprop.getInput(Long.toString(portProperty.getId())).getData().put("value", inputValue);
                }
            }
            this.saveConfigData(userID, workflowName, workflowconfig);

        } else {
            throw new InconsistentInputPortException("", userID, workflowName, jobName, portNumber);
        }
    }

    /**
     * Sets an input text as local input file and associates it to a port
     * Note: it operates only on File Input ports!
     * @param userID - id of the user
     * @param filecontent - input text content
     * @param workflowID - id of the workflow, which contains the job
     * @param jobID - id of the job, which contains the port
     * @param portNumber - number of the port
     * @throws IOException
     */
    public void setInputText(String userID, String filecontent, String workflowName, String jobName, String portNumber) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Vector<JobPropertyBean> workflowConfig = this.getWorkflowConfig(userID, workflowName);
        if (CheckPortType(workflowConfig, "file", jobName, portNumber)) {
            // saving filecontent to a file;
            String tempdir = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "uploads/" + userID;
            File tempdirf = new File(tempdir);
            if (!tempdirf.exists()) {
                tempdirf.mkdirs();
            }
            File tempfile = new File(tempdir + "/input_" + portNumber + "_file");
            if (tempfile.exists()) {
                // file exists delete it!
                tempfile.delete();
            }
            //System.out.println("setinputtext filecontent: "+ tempfile);
            tempfile.createNewFile();
            FileUtils.writeStringToFile(tempfile, filecontent);
            try {
                this.placeUploadedFile(userID, tempfile, workflowName, jobName, portNumber);
            } catch (Exception e) {
                //System.out.println("placing file "+tempfile +" failed");
            }
        } else {
            throw new InconsistentInputPortException("", userID, workflowName, jobName, portNumber);
        }
    }

    /**
     * Returns SQL Query attributes from the given workflow's job's port.
     *
     * @param userID - ID of the user
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     * @param portNumber - number of the port
     * @return ASMSQLQueryBean bean filled with the attributes
     */
    public ASMSQLQueryBean getInputSQLQuery(String userID, String workflowName, String jobName, String portNumber) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ASMSQLQueryBean queryBean = new ASMSQLQueryBean();

        Vector<JobPropertyBean> workflowConfig = this.getWorkflowConfig(userID, workflowName);
        if (CheckPortType(workflowConfig, "sqlurl", jobName, portNumber)) {

            for (JobPropertyBean job : workflowConfig) {
                if (job.getName().equals(jobName)) {
                    PortDataBean portProperty = getPortbySeq(job, portNumber);

                    String sqlUrl = portProperty.getData().get("sqlurl").toString();
                    String sqlPassword = portProperty.getData().get("sqlpass").toString();
                    String sqlUserName = portProperty.getData().get("sqluser").toString();
                    String sqlQuery = portProperty.getData().get("sqlselect").toString();
                    queryBean.setSqlUrl(sqlUrl);
                    queryBean.setSqlQuery(sqlQuery);
                    queryBean.setSqlPassword(sqlPassword);
                    queryBean.setSqlUserName(sqlUserName);
                }
            }
            this.saveConfigData(userID, workflowName, workflowConfig);
        } else {
            throw new InconsistentInputPortException("", userID, workflowName, jobName, portNumber);
        }
        return queryBean;
    }

    /**
     *
     * Sets SQL Query attributes (added as ASMSQLQueryBean argument) to the given workflow's job's port.
     *
     * @param bean - Descriptor object of the SQL Query properties (SQL url, username, etc)
     * @param userID - ID of the user
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     * @param portNumber - number of the port
     */
    public void setInputSQLQuery(ASMSQLQueryBean bean, String userID, String workflowName, String jobName, String portNumber) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Vector<JobPropertyBean> workflowConfig = this.getWorkflowConfig(userID, workflowName);
        if (CheckPortType(workflowConfig, "sqlurl", jobName, portNumber)) {
            for (JobPropertyBean job : workflowConfig) {
                if (job.getName().equals(jobName)) {
                    PortDataBean portProperty = getPortbySeq(job, portNumber);
                    job.getInput(Long.toString(portProperty.getId())).getData().put("sqlurl", bean.getSqlUrl());
                    job.getInput(Long.toString(portProperty.getId())).getData().put("sqlpass", bean.getSqlPassword());
                    job.getInput(Long.toString(portProperty.getId())).getData().put("sqluser", bean.getSqlUserName());
                    job.getInput(Long.toString(portProperty.getId())).getData().put("sqlselect", bean.getSqlQuery());

                }
            }

        } else {
            throw new InconsistentInputPortException("", userID, workflowName, jobName, portNumber);
        }
    }

    /**
     *
     * Gets the uploaded file stored on the portal server's temporary folder and uploads it to Storage component of gUSE
     * then updates the database managed by WFS component
     *
     * @param userID - ID of the user
     * @param fileOnPortalServer - file on the portal server
     * @param workflowID - Name of the workflow
     * @param jobID - name of the job
     * @param portID - ID of the port (0..15)
     * @throws Exception
     */
    public void placeUploadedFile(String userID, File fileOnPortalServer, String workflowName, String jobName, String portNumber) throws Exception {
        String portalUrl = this.getPortalURL();
        //System.out.println("placeUploadedFile started...");
        try {
            String SID = this.getPortID(userID, workflowName, jobName, portNumber);
            Hashtable h = new Hashtable();
            
            h.put("portalID", portalUrl);
            h.put("userID", userID);
            h.put("workflowID", workflowName);
            h.put("jobID", jobName);
            String sfile = "input_" + portNumber;
            h.put("sfile", sfile);
            String confID = userID + String.valueOf(System.currentTimeMillis());
            h.put("confID", confID);
            h.put("sid", confID);
            String uploadField = "";
            String uploadingitem = fileOnPortalServer.getName();

            uploadField = "input_" + portNumber + "_file";

            Hashtable hsh = new Hashtable();
            ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
            PortalStorageClient psc = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
            psc.setServiceURL(st.getServiceUrl());
            psc.setServiceID("/upload");
            if (fileOnPortalServer != null) {
                Enumeration e = h.keys();
                while (e.hasMoreElements()) {
                    String elem = (String) e.nextElement();
                }

                psc.fileUpload(fileOnPortalServer, uploadField, h);
                // uploadThread
                if (uploadingitem != null) {
                    ASMUploadThread uploadthread = new ASMUploadThread(userID, workflowName, jobName, portNumber, confID, SID, uploadingitem);
                    uploadthread.start();
                    boolean isgo = false;
                    while (!isgo) {
                        for (int i = 0; i < 100; ++i) {
                            for (int j = 0; j < 100; ++j) {
                                String s = System.getProperty("user.dir");
                            }
                        }
                        isgo = uploadthread.isGo();
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Upload_GeneralException(ex.getCause(), userID);


        }

    }

    public String getRuntimeID(String userID, String workflowName) {
        Map runtimes = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName).getAllRuntimeInstance();

        if (runtimes.size() > 0) {
            Object firstID = runtimes.keySet().iterator().next();
            return firstID.toString();
        }

        return null;
    }

    // downloadtype can be  : InstanceOutputs, InstanceAll, All, AllbutLogs,AllInputs,AllOutputs
    private InputStream getFileStreamFromStorage(String userID, String workflowName, String jobName, String pID, int downloadtype) {
        InputStream is = null;
        String portalUrl= getPortalURL();
        try {
            Hashtable hsh = new Hashtable();
            try {
                hsh.put("url", this.getStorageURL());
            } catch (Exception e) {
            }
            Hashtable<String, String> params = new Hashtable<String, String>();

            params.put("portalID", portalUrl);
            params.put("wfsID", this.getWFSURL());
            params.put("userID", userID);
            params.put("workflowID", workflowName);
            //params.put("jobID", jobID);

            //TODO : modify pidID to handle parametric output ports!!!

            //params.put("pidID", pID);
            String runtimeID = getRuntimeID(userID, workflowName);

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
                    case DownloadTypeConstants.JobOutputs:
                        if (jobName != null && pID != null) {
                            params.put("downloadType", "joboutputs_" + runtimeID);
                            params.put("jobID", jobName);
                            params.put("pidID", pID);
                            break;
                        } else {
                            return null;
                        }
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

    private void convertOutput(String userId, String workflowName, String jobName, String fileName, InputStream is, OutputStream os, boolean compress) throws IOException {

        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry;
        String runtimeID = getRuntimeID(userId, workflowName);
        ZipOutputStream zos;

        zos = new ZipOutputStream(os);

        while ((entry = zis.getNextEntry()) != null) {

            if (jobName == null || (entry.getName().contains(jobName + "/outputs/" + runtimeID + "/") && (fileName == null || (fileName != null && entry.getName().endsWith(fileName))))) {
                int size;
                byte[] buffer = new byte[2048];

                String parentDir = entry.getName().split("/")[entry.getName().split("/").length - 2];
                String fileNameInZip = parentDir + "/" + entry.getName().split("/")[entry.getName().split("/").length - 1];
                ZipEntry newFile = new ZipEntry(fileNameInZip);

                if (compress) {
                    zos.putNextEntry(newFile);
                    while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                        zos.write(buffer, 0, size);
                    }
                    zos.closeEntry();
                } else {
                    while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                        os.write(buffer, 0, size);
                    }
                    os.flush();
                }
            }
        }
        zis.close();
        if (compress) {

            zos.close();
        }
    }
    
    public InputStream getSingleInputFileStream(String userID,String workflowName,String jobName,Integer portNumber) throws MalformedURLException, IOException{
        String portalUrl = getPortalURL();
        String replPortalID = portalUrl.replace("/","_");
        String servletURL = this.getStorageURL() +"/getFile";
        String pathValue = replPortalID + "/"+userID+"/"+workflowName+"/"+jobName+"/inputs/"+portNumber.toString()+"/0";
        System.out.println("servlet called: " + servletURL+"?path="+pathValue);
        URL oracle = new URL(servletURL+"?path="+pathValue);

        URLConnection yc = oracle.openConnection();
        return yc.getInputStream();

    }
    /**
     * Returns an Inputstream of a single output file using servlet interface of Storage component (instead of using web-interface)
     *
     * @param userID - userID
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     * @param parametricID - instance number of the job (0, if it was submitted as single job, >0 specifies one of parametric execution of the job)
     * @param fileNumber - if multiple files generated, it specifies which one should be downloaded, (please note that if multiple files were generated as outputs, the fileName must have an _<number> postfix to specify the proper file)
     * @return Stream of an output file
     * @throws java.net.MalformedURLException
     * @throws java.io.IOException
     */
  public InputStream getSingleOutputFileStream(String userID,String workflowName,String jobName,Integer parametricID, String fileName) throws MalformedURLException, IOException{

      String pID = "0";
        
        if (parametricID != null){
                pID = Integer.toString(parametricID);
         }
         String portalUrl = getPortalURL();
        String replPortalID = portalUrl.replace("/","_");
        String servletURL = this.getStorageURL() +"/getFile";
        String runtimeID = this.getRuntimeID(userID, workflowName);
        String pathValue = replPortalID + "/"+userID+"/"+workflowName+"/"+jobName+"/outputs/"+runtimeID+"/"+pID+"/"+fileName;
        System.out.println("servlet called: " + servletURL+"?path="+pathValue);
        URL oracle = new URL(servletURL+"?path="+pathValue);

        URLConnection yc = oracle.openConnection();
        return yc.getInputStream();

    }

  	/**
     * returns the stream of the specified output file to the given response
     * if there are more files associated to the port (by filename)
     * note: it works on single files (pID = 0)
     * all other pIDs will be ignored.
     * @param userID - userID coming from Liferay
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     * @param fileName - filename
     * @param pID - parametric ID, orders the members of the output files with the same name. Starts from zero. If it is a single output, just set it null or zero.
     * @param outputStream - a stream object to where to output file is transferred
     */
    public void getSingleOutputFileStream(String userID, String workflowName, String jobName, String fileName, Integer pID, OutputStream outputStream) throws IOException {
        InputStream is = null;
        // getting runtime ID + getting pid
        String parametricID = "0";
        
        if (pID != null){
                parametricID = Integer.toString(pID);
         }
                is = getFileStreamFromStorage(userID, workflowName, jobName,parametricID, DownloadTypeConstants.JobOutputs);
            this.convertOutput(userID, workflowName, jobName, fileName, is, outputStream,false);
     }

    /**
      * returns the stream of the specified output file to the given response
     * if there are more files associated to the port (by filename)
     * note: it works on single files (pID = 0)
     * all other pIDs will be ignored.
     * @param userID - userID coming from Liferay
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     * @param fileName - filename
     * @param pID - parametric ID, orders the members of the output files with the same name. Starts from zero. If it is a single output, just set it null or zero.
     */
    public void getSingleOutputFileStream(String userID, String workflowName, String jobName, String fileName, Integer pID, HttpServletResponse response) throws IOException {
        InputStream is = null;
        // getting runtime ID + getting pid
        String parametricID = "0";
        
        if (pID != null) {
            parametricID = Integer.toString(pID);
        }

        is = getFileStreamFromStorage(userID, workflowName, jobName, parametricID, DownloadTypeConstants.JobOutputs);
        this.convertOutput(userID, workflowName, jobName, fileName, is, response.getOutputStream(), false);

    }

    /**
     * returns the stream of the specified output file to the given response
     *
     * @param userID - ID of the user
     * @param workflowName - Name of the workflow
     * @param jobName  - Name of the job
     * @param fileName - name of the file (it must be associated as a port on jobName job)
     * @param pID - parametric ID, orders the members of the output files with the same name. Starts from zero. If it is a single output, just set it null.
     * @param response - response object
     * @throws java.io.IOException
     */
    public void getSingleOutputFileStream(String userID, String workflowName, String jobName, String fileName, Integer pID, ResourceResponse response) throws IOException {
        InputStream is = null;
        String parametricID = "0";

        if (pID != null) {
            parametricID = Integer.toString(pID);
        }
        is = getFileStreamFromStorage(userID, workflowName, jobName, parametricID, DownloadTypeConstants.JobOutputs);
        this.convertOutput(userID, workflowName, jobName, fileName, is, response.getPortletOutputStream(), false);
    }

    /**
     * @deprecated use {@link getSingleOutputFileStream(String userID, String workflowName, String jobName, String fileName, Integer pID, HttpServletResponse response)} instead
     * It gets the file specified by the attributes (userID/workflowID/jobID/portID) and passes it back to the outputstream of the specified response
     *
     * @param userID - ID of the user
     * @param workflowID - Name of the workflow
     * @param jobID - Name of the job
     * @param fileName - Name of the file
     * @param response - response that should contain the file to download
     * @throws ASM_GeneralException
     */
    public void getFileStream(String userID, String workflowName, String jobName, String fileName, HttpServletResponse response) throws Upload_GeneralException {
        InputStream is = null;
        try {

            is = getFileStreamFromStorage(userID, workflowName, null, null, DownloadTypeConstants.JobOutputs);
            this.convertOutput(userID, workflowName, jobName, fileName, is, response.getOutputStream(), false);

        } catch (IOException ex) {
            throw new Download_GettingFileStreamException(ex.getCause(), userID, workflowName);
        }
    }
    /**
     * Returns the standard output's link of the specified job
     * @param userID - userID
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     *
     * @param pID - parametric ID (relevant if multiple job instances were submitted, in this case it means the number of the parametric job submission, othervise it can be left null, or 0)
     * @return link
     */
    public String getStdOutputFilePath(String userID,String workflowName,String jobName, Long pID){
        return getFilePath(userID, workflowName, jobName, null, pID, "stdout");
    }
    /**
     * Returns the standard error's link of the specified job
     * @param userID - userID
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     *
     * @param pID - parametric ID (relevant if multiple job instances were submitted, in this case it means the number of the parametric job submission, othervise it can be left null, or 0)
     * @return link
     */
    public String getStdErrorFilePath(String userID,String workflowName,String jobName, Long pID){
        return getFilePath(userID, workflowName, jobName, null, pID, "stderr");
    }
    /**
     * Returns the logbook's link of the specified job
     * @param userID - userID
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     *
     * @param pID - parametric ID (relevant if multiple job instances were submitted, in this case it means the number of the parametric job submission, othervise it can be left null, or 0)
     * @return link
     */
    public String getLogBookFilePath(String userID,String workflowName,String jobName, Long pID){
       return getFilePath(userID, workflowName, jobName, null, pID, "logbook");
    }

    private String getFilePath(String userID,String workflowName,String jobName,Integer portNumber, Long pID, String io){
        long tpID = 0;
        if (pID != null){
            tpID = pID;
        }
          String portalUrl = getPortalURL();
        String replPortalID = portalUrl.replace("/","_");
        String runtimeID = this.getRuntimeID(userID, workflowName);;
        String servletURL = this.getStorageURL() +"/getFile";
        String pathValue = replPortalID + "/"+userID+"/"+workflowName+"/"+jobName;
        if ("input".equals(io)){
            pathValue += "/inputs/"+portNumber.toString()+"/0";
        }
        else {
            pathValue += "/outputs/"+runtimeID+"/"+Long.toString(tpID);
            if("stdout".equals(io)){
                pathValue+="/stdout.log";
            }
            else if("stderr".equals(io)){
                pathValue+="/stderr.log";
            }
            else if("logbook".equals(io)){
                pathValue+="/gridnfo.log";
            }
        }
        return servletURL+"?path="+pathValue;
    }

    /**
     *
     * It gets the file specified by the attributes (userID/workflowID/jobID/filename) and places it on the portal server under $CATALINA_HOME/tmp/users/" + userID + "/workflow_outputs" + workflowID
     *
     * @param userID - ID of the user
     * @param workflowID - Name of the workflow
     * @param jobID - Name of the job
     * @param filename - name of the file
     * @param pID - parametric ID (in order to the parallel executions of a job)

     * @throws ASM_GeneralException
     * @return String - Path of the file stored on the portal server
     */
    public String getFiletoPortalServer(String userID, String workflowName, String jobName, String fileName, Integer pID) throws Download_GettingFileToPortalServiceException {
        String downloadfolder = PropertyLoader.getInstance().getProperty("prefix.dir") + "tmp/users/" + userID + "/workflow_outputs/" + workflowName + "/" + jobName;
        String parametricID = "0";
        if (pID != null) {
            parametricID = Integer.toString(pID);
        }

        if (!new File(downloadfolder).exists()) {
            File down_folder = new File(downloadfolder);
            down_folder.mkdirs();
        }
        String outputfile = downloadfolder + "/" + fileName;
        File f = new File(outputfile);
        InputStream is = null;

        try {

            is = getFileStreamFromStorage(userID, workflowName, jobName, parametricID, DownloadTypeConstants.JobOutputs);
            java.io.OutputStream out = new FileOutputStream(f);
            this.convertOutput(userID, workflowName, jobName, fileName, is, out, false);

            return outputfile;
        } catch (IOException ex) {
            throw new Download_GettingFileToPortalServiceException(ex.getCause(), userID, workflowName);
        }
    }

    /**
     * @deprecated use {@link getSingleOutputFileStream(String userID, String workflowName, String jobName, String fileName, Integer pID,  ResourceResponse response)} instead
     *
     * It gets the file specified by the attributes (userID/workflowID/jobID/portID) and passes it back to the outputstream of the specified ResourceResponse
     * It can be used if file downloading should work using Ajax technology
     *
     * @param userID - ID of the user
     * @param workflowID - Name of the workflow
     * @param jobID - Name of the job
     * @param fileName - name of the file
     * @param response - response that should contain the file to download
     * @throws ASM_GeneralException
     */
    public void getFileStream(String userID, String workflowName, String jobName, String fileName, ResourceResponse response) throws Download_GettingFileStreamException {

        InputStream is = null;
        try {

            is = getFileStreamFromStorage(userID, workflowName, null, null, DownloadTypeConstants.JobOutputs);
            this.convertOutput(userID, workflowName, jobName, fileName, is, response.getPortletOutputStream(), false);

        } catch (IOException ex) {
            throw new Download_GettingFileStreamException(ex.getCause(), userID, workflowName);
        }
    }

    /**
     *
     * Provides outputs through response object added as a parameter
     *
     * @param userId id of the user
     * @param workflowId name of the workflow
     * @param response object that will contain the output stream
     * @throws ASM_GeneralException
     */
    public void getWorkflowOutputs(String userId, String workflowName, ResourceResponse response) {
        try {
            InputStream is = null;
            is = getFileStreamFromStorage(userId, workflowName, null, null, DownloadTypeConstants.All);
            this.convertOutput(userId, workflowName, null, null, is, response.getPortletOutputStream(), true);

        } catch (IOException ex) {
            Logger.getLogger(ASMService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * Gets the workflows from the local repository exported by a specified user
     *
     * @param owner - user who exported the workflow
     * @param type - type of the exportation (application,project,graph)
     * @return - vector of the workflows
     * @throws Exception
     */
    public synchronized Vector<ASMRepositoryItemBean> getWorkflowsFromRepository(String owner, String type) throws Exception {
        try {
            Long id = new Long(0);
            return getWorkflowsFromRepository2Vector(owner, type, id);
        } catch (Exception e) {
            throw e;
        }
    }

    private synchronized ArrayList<RepositoryWorkflowBean> getWorkflowsFromRepository2Array(String owner, String type, Long id) throws ASM_UnknownErrorException {
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
                    //if (repbean.getUserID().equals(owner)) {
                        ret_list.add(repbean);
                    //}
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

    private synchronized Vector<ASMRepositoryItemBean> getWorkflowsFromRepository2Vector(String owner, String type, Long id) throws Exception {
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
            RepositoryWorkflowBean rwbean = wfList.get(i);
            //if (rwbean.getUserID().equals(owner)) {
                ASMRepositoryItemBean itembean = new ASMRepositoryItemBean();
                itembean.setExportText(rwbean.getExportText());
                itembean.setExportType(rwbean.getExportType());
                itembean.setId(rwbean.getId());
                itembean.setUserID(rwbean.getUserID());
                itembean.setItemID(rwbean.getWorkflowID());
                repitemlist.add(itembean);
            //}
        }


        return repitemlist;
    }

    /**
     *
     * Gets list of the users who have exported anything to the repository
     *
     * @param type - type of the exported workflow
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

            if (!owners.contains(new String(userId))) {
                owners.add(userId);
            }
        }

        return owners;
    }

    /**
     *
     * Gets the workflows of a specified user (uses portalchache)
     *
     *
     * @param userID - Id of the user
     * @return - vector of workflowData objects (workflows)
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public synchronized HashMap<String, ASMWorkflow> getWorkflows(String userID) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    	if (DEBUG_MODE) {
    		debug("getWorkflows(String)", "Obtaining worfklows from PortalCacheService for user: " + userID);
    	}
        HashMap<String, ASMWorkflow> tmpworkflows = new HashMap<String, ASMWorkflow>();
        Vector<WorkflowData> workflows = (Vector<WorkflowData>) Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(userID).getWorkflows());
        if (DEBUG_MODE) {
        	debug("getWorkflows(String)", "User " + userID + " has " + workflows.size() + " workflows");
        }
        for (int i = 0; i < workflows.size(); ++i) {
            if (workflows.get(i).getWorkflowID() != null &&
                    !workflows.get(i).getWorkflowID().equals("null")) {
                ASMWorkflow inst = getRealASMWorkflow(userID, workflows.get(i).getWorkflowID());
                if (DEBUG_MODE) {
                	debug("getWorkflows(String)", 
                			"Workflow name=" + inst.getWorkflowName() + 
                			", userID=" + inst.getUserID() + 
                			", statusBean.status=" + inst.getStatusbean().getStatus() + 
                			", statusBean.color=" + inst.getStatusbean().getColor());
                }
                tmpworkflows.put(inst.getWorkflowName(), inst);
            }
        }
        return tmpworkflows;
    }

    private ASMWorkflow getRealASMWorkflow(String userID, String workflowName) {

        Vector<JobPropertyBean> joblist = ((Vector<JobPropertyBean>) getWorkflow(userID, workflowName));
        ASMWorkflow inst = new ASMWorkflow(userID);
        Hashtable<String, ASMJob> jobs = new Hashtable<String, ASMJob>();

        for (int j = 0; j < joblist.size(); ++j) {

            String jobname = joblist.get(j).getName();

            // getting input ports
            Vector<PortDataBean> input_portlist = joblist.get(j).getInputs();
            Hashtable<String, String> input_ports = new Hashtable<String, String>();
            for (int k = 0; k < input_portlist.size(); ++k) {
                String portseq = Long.toString(input_portlist.get(k).getSeq());
                //String portname = input_portlist.get(k).getName();
                // changing port name to internal file name generated to be able to download
                String portname = (String) input_portlist.get(k).getData().get("intname");
                if (portname == null) {
                    portname = input_portlist.get(k).getName();
                }


                input_ports.put(portseq, portname);
            }
            //getting outupt ports
            Vector<PortDataBean> output_portlist = joblist.get(j).getOutputs();
            Hashtable<String, String> output_ports = new Hashtable<String, String>();
            for (int k = 0; k < output_portlist.size(); ++k) {
                String portseq = Long.toString(output_portlist.get(k).getSeq());
                // changing port name to internal file name generated to be able to download
                //String portname = output_portlist.get(k).getName();
                String portname = (String) output_portlist.get(k).getData().get("intname");
                if (portname == null) {
                    portname = output_portlist.get(k).getName();
                }
                output_ports.put(portseq, portname);
            }

            ASMJob asm_job = new ASMJob(jobname, input_ports, output_ports);
            jobs.put(jobname, asm_job);
        }

        inst.setJobs(jobs);
        inst.setWorkflowName(workflowName);

        return inst;

    }

    private Vector getWorkflow(String userID, String workflowName) {
        Vector v = null;
        try {
            ServiceType st = InformationBase.getI().getService("wfs", "portal", new Hashtable(), new Vector());
            PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            ComDataBean commdata = new ComDataBean();
            String portalUrl = this.getPortalURL();
            commdata.setPortalID(portalUrl);
            commdata.setUserID(userID);
            commdata.setWorkflowID(workflowName);

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
     * @param userId - Id of the user
     * @param workflowId - Id of the workflow
     * @return - InstanceStatusbean object that contains status and the actual statuscolor
     */
    public synchronized WorkflowInstanceStatusBean getWorkflowStatus(String userId, String workflowName) {

        String statuscolor = "";
        String status = "";
        StatusConstants cons = new StatusConstants();
        Set instances = PortalCacheService.getInstance().getUser(userId).getWorkflow(workflowName).getAllRuntimeInstance().keySet();
        if (instances.size() > 0) {
            Enumeration insten = PortalCacheService.getInstance().getUser(userId).getWorkflow(workflowName).getAllRuntimeInstance().keys();
            String inst = (String) insten.nextElement();
            WorkflowRunTime wfruntime = (WorkflowRunTime) PortalCacheService.getInstance().getUser(userId).getWorkflow(workflowName).getAllRuntimeInstance().get(inst);
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
     * @param userID - ID of the user
     * @param workflowID -  name of the workflow
     * @return Vector of JobProperyBeans
     * @throws java.lang.ClassNotFoundException no communication class
     * @throws java.lang.InstantiationException  com.class could not be initialized.
     * @throws java.lang.IllegalAccessException could not have access to com.class
     */
    public Vector<JobPropertyBean> getWorkflowConfig(String userID, String workflowName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

    	TEMP_PROP_LOCK.lock();
    	try {
    		final String tempSearchKey = userID + "_" + workflowName;
    		if (tempJobPropertyMap.containsKey(tempSearchKey)) {
    			return tempJobPropertyMap.get(tempSearchKey);
    		} else {
		        String portalUrl = this.getPortalURL();
		        ComDataBean tmp = new ComDataBean();
		        tmp.setPortalID(portalUrl);
		        tmp.setUserID(userID);
		        tmp.setWorkflowID(workflowName);
		
		        Hashtable hsh = new Hashtable();
		
		        ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
		        PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
		        pc.setServiceURL(st.getServiceUrl());
		        pc.setServiceID(st.getServiceID());
		        
		        final Vector jobProperties = pc.getWorkflowConfigData(tmp);
		        tempJobPropertyMap.put(tempSearchKey, jobProperties);
				return jobProperties;
    		}
    	} finally {
    		TEMP_PROP_LOCK.unlock();
    	}
    }

    /**
     * Saves workflow configuration
     * @param userID - Id of the user
     * @param workflowID - Id of the workflow
     * @param pJobs jobs that contains the configuration
     * @throws java.lang.ClassNotFoundException no communication class
     * @throws java.lang.InstantiationException  com.class could not be initialized.
     * @throws java.lang.IllegalAccessException could not have access to com.class
     */
    private void saveConfigData(String userID, String workflowName, Vector pJobs) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        TEMP_PROP_LOCK.lock();
        try {
	    	Hashtable hsh = new Hashtable();
	        hsh.put("url", PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName).getWfsID());
	        ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
	        PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
	        pc.setServiceURL(st.getServiceUrl());
	        pc.setServiceID(st.getServiceID());
	
	        ComDataBean cmd = new ComDataBean();
	        String portalUrl = this.getPortalURL();
	        cmd.setPortalID(portalUrl);
	        cmd.setUserID(userID);
	        cmd.setWorkflowID(workflowName);
	        //cmd.setTyp(4);
	
	        pc.setWorkflowConfigData(cmd, pJobs);
	        
			tempJobPropertyMap.remove(userID + "_" + workflowName);
        } finally {
        	TEMP_PROP_LOCK.unlock();
        }
    }

    private void cleanAllWorkflowInstances(String userID, String workflowName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        WorkflowData workflow = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName);
        for (final Map.Entry<String, WorkflowRunTime> entry : workflow.getAllRuntimeInstance().entrySet()) {
            RealWorkflowUtils.getInstance().deleteWorkflowInstance(userID, workflowName, entry.getKey());
        }
    }

    /**
     * Submits a workflow Instance
     * @param userID Id of the user
     * @param workflowID Id of the workflow
     * @throws java.lang.ClassNotFoundException no communication class
     * @throws java.lang.InstantiationException  com.class could not be initialized.
     * @throws java.lang.IllegalAccessException could not have access to com.class
     */
    public void submit(String userID, String workflowName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        submit(userID, workflowName, "text", "Never");
    }

    /**
     * Submits a given workflow with a text of submission and notifies according to the given string (@see NotificationTypeConstants)
     * @param userID - user ID
     * @param workflowID - workflow ID
     * @param text - Note for the submission
     * @param notify - type of notification (@see NotificationTypeConstants)
     * @throws java.lang.ClassNotFoundException no communication class
     * @throws java.lang.InstantiationException  com.class could not be initialized.
     * @throws java.lang.IllegalAccessException could not have access to com.class
     */
    public void submit(String userID, String workflowName, String text, String notify) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // deleting old workflow instance
        try {
            cleanAllWorkflowInstances(userID, workflowName);
        } catch (Exception e) {
            e.printStackTrace();
            if (DEBUG_MODE) {
            	debug("submit", "Error when invoking cleanAllWorkflowInstances " + e);
            }
        }

        // FIXME: what is this? is it dead code or what?
//        ServiceType st = InformationBase.getI().getService("wfi", "portal", new Hashtable(), new Vector());
//        PortalWfiClient pc = (PortalWfiClient) Class.forName(st.getClientObject()).newInstance();
//        pc.setServiceURL(st.getServiceUrl());
//        pc.setServiceID(st.getServiceID());

        //WorkflowRuntimeBean bean = new WorkflowRuntimeBean();
        //FIXME: is this one line needed? the variable 'data' is not used at all!, but the method used to set its value might
        // perform some black magic
        //WorkflowData data = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName);

        if (DEBUG_MODE) {
        	debug("submit", "Submitting workflow with name=" + workflowName + " for user=" + userID);
        }
        new WorkflowSubmitThread(PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName), userID, text, notify);
    }

    /**
     * Rescues a workflow instance
     * @param userID - Id of the user
     * @param workflowID - Id

     * @throws java.lang.ClassNotFoundException no communication class
     * @throws java.lang.InstantiationException  com.class could not be initialized.
     * @throws java.lang.IllegalAccessException could not have access to com.class
     */
    public void rescue(String userID, String workflowName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Vector errorJobPidList = new Vector();
        String portalID = this.getPortalURL();
        String runtimeID = this.getRuntimeID(userID, workflowName);
        String wfStatus = "" + PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName).getRuntime(runtimeID).getStatus();
        if (("7".equals(wfStatus)) || ("28".equals(wfStatus)) || ("23".equals(wfStatus))) {
            //
            // 23 = running/error
            if ("23".equals(wfStatus)) {
                // entering of running workflow status
                PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName).getRuntime(runtimeID).setStatus(5);
            } else {
                // entering of resuming workflow status
                PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName).getRuntime(runtimeID).setStatus(29);
            }
            //
                /*ConcurrentHashMap tmp=PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName).getRuntime(runtimeID).getJobsStatus();
            Enumeration enm0=tmp.keys();
            String ts;
            while (enm0.hasMoreElements())
            {
            Object key0=enm0.nextElement();
            Enumeration enm1=((ConcurrentHashMap)tmp.get(key0)).keys();
            while(enm1.hasMoreElements())
            {
            Object key1=enm1.nextElement();
            ts=""+((JobStatusData)((ConcurrentHashMap)tmp.get(key0)).get(key1)).getStatus();
            if (ts.equals("25")||ts.equals("22")||ts.equals("21")||ts.equals("7")||ts.equals("15")||ts.equals("13")||ts.equals("12")) {
            // entering init status
            // PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).addJobbStatus((String)key0,(String)key1,"1","",-1);
            // clearing job from registry
            PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName).getRuntime(runtimeID).removeJobStatus((String) key0, (String) key1);
            // collecting jobID/jobPID for storage cleanup
            ComDataBean comDataBean = new ComDataBean();
            comDataBean.setJobID((String) key0);
            comDataBean.setJobPID((String) key1);
            errorJobPidList.addElement(comDataBean);
            }
            }
            }*/
            if (UserQuotaUtils.getInstance().userQuotaIsFull(userID)) {

                throw new ASMException("Quota limit is exceeded");
            } else {
                new WorkflowRescueThread(portalID, userID, workflowName, runtimeID, wfStatus, errorJobPidList);
            }
        }
    }

    /**
     * Aborts all runtime instance related to the specificated workflow
     * @param userID Id of the user
     * @param workflowID name of the workflow
     * @throws java.lang.ClassNotFoundException no communication class
     * @throws java.lang.InstantiationException  com.class could not be initialized.
     * @throws java.lang.IllegalAccessException could not have access to com.class
     */
    public void abort(String userID, String workflowName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String runtimeID = this.getRuntimeID(userID, workflowName);
        if (DEBUG_MODE) {
        	debug("abort", "Aborting workflow with name=" + workflowName + ", runtimeID=" + runtimeID + ", from user with id=" + userID);
        }
        try { 
        	final UserData userData = PortalCacheService.getInstance().getUser(userID);
			if (userData != null) {
				if (DEBUG_MODE) {
					debug("abort", "User data obtained. userData=" + userData);
				}
                final WorkflowData workflowData = userData.getWorkflow(workflowName);
				if (workflowData != null) {
					if (DEBUG_MODE) {
						debug("abort", "Workflow data obtained. wfsID=" + workflowData.getWfsID());
					}
                    final WorkflowRunTime workflowRuntime = workflowData.getRuntime(runtimeID);
					if (workflowRuntime != null) {
						if (DEBUG_MODE) {
							debug("abort", "Workflow runtime obtained. runtime.text=" + workflowRuntime.getText());
						}
                        String wfStatus = "" + workflowRuntime.getStatus();
                        if (DEBUG_MODE) {
                        	debug("abort", "wfStatus=" + wfStatus);
                        }
                        if (StatusConstants.RUNNING.equals(wfStatus) || 
                        	StatusConstants.RUNNING_ERROR.equals(wfStatus) || 
                        	StatusConstants.SUBMITTED.equals(wfStatus)) {
                            workflowRuntime.setStatus(Integer.parseInt(StatusConstants.WORKFLOW_SUSPENDING));
                            new WorkflowAbortThread(userID, workflowName, runtimeID);
                        }
                    } else {
                    	if (DEBUG_MODE) {
                        	debug("abort", "workflowRuntime is null!");
                        }
                    }
                } else {
                	if (DEBUG_MODE) {
                    	debug("abort", "workflowData is null!");
                    }
                }
            } else {
            	if (DEBUG_MODE) {
                	debug("abort", "userData is null!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the workflow status
     * @param userID - id of the user
     * @param workflowID - name of the workflow
     * @throws java.lang.ClassNotFoundException no communication class
     * @throws java.lang.InstantiationException  com.class could not be initialized.
     * @throws java.lang.IllegalAccessException could not have access to com.class
     */
    public String getStatus(String userID, String workflowName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        ServiceType st = InformationBase.getI().getService("wfi", "portal", new Hashtable(), new Vector());
        PortalWfiClient pc = (PortalWfiClient) Class.forName(st.getClientObject()).newInstance();
        pc.setServiceURL(st.getServiceUrl());
        pc.setServiceID(st.getServiceID());

        Vector<WorkflowInformationBean> workflows_info = pc.getInformation();

        StatusConstants cons = new StatusConstants();
        WorkflowInformationBean wf_info = null;
        for (int i = 0; i < workflows_info.size(); ++i) {

            if (workflows_info.get(i).getWorkflowid().equals(new String(workflowName))) {
                wf_info = workflows_info.get(i);
            }
        }

        if (wf_info == null) {
            return new String("unknown_error");
        } else {
            return cons.getStatus(Integer.toString(wf_info.getStatus()));
        }
    }

    private PortDataBean getPortbySeq(JobPropertyBean job, String portNumber) {
        PortDataBean portProperty = null;

        for (PortDataBean p : (Vector<PortDataBean>) job.getInputs()) {
            if (Long.parseLong(portNumber) == p.getSeq()) {
                portProperty = p;
                break;
            }
        }
        if (portProperty == null) {
            for (PortDataBean p : (Vector<PortDataBean>) job.getOutputs()) {
                if (Long.parseLong(portNumber) == p.getSeq()) {
                    portProperty = p;
                    break;
                }
            }
        }
        return portProperty;
    }

    /**
     * Checks if the given input type has or has not been set correctly in a given input port
     *  (jobs are read from the given Vecor of JobPropertyBean object )
     * @param jobs  - vector of job descriptor objects
     * @param type - type of the input field ("file", "remote", "value", "sqlurl")
     * @param userId - user id
     * @param workflowName - name of the workflow
     * @param jobName - name of the job
     * @param portNumber - number of the port
     * @return true, if the given input port has been set according to the given type
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     */
    private boolean CheckPortType(Vector<JobPropertyBean> jobs, String type, String jobName, String portNumber) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        for (JobPropertyBean property : jobs) {
            if (property.getName().equals(jobName)) {
                PortDataBean portProperty = getPortbySeq(property, portNumber);

                if (portProperty != null) {
                    if (portProperty.getData().get(type) != null && checkTokenConsistency(type, portProperty.getData())) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    throw new PortNotExistsException();
                }
            }
        }
        throw new ASMException("Workflow does not contain job named " + jobName);

    }

    /**
     * Checks token's  consistency
     * @param type - type of the input port
     * @param portData - descriptor object of the port
     * @return - true, if input type and all of its required attributes are set, othervise it returns false
     */
    private boolean checkTokenConsistency(String type, HashMap portData) {
        Set<String> inputTokens = new HashSet<String>(Arrays.asList(new String[]{"file", "remote", "value", "sqlurl"}));
        Set<String> sqlTokens = new HashSet<String>(Arrays.asList(new String[]{"sqlpass", "sqluser", "sqlselect"}));

        // removing given type from inputTokens
        inputTokens.remove(type);
        boolean consistent = true;
        for (String token : inputTokens) {
            if (portData.get(token) != null) {
                consistent = false;
            }
        }
        // check SQL properties
        if (type.equals("sqlurl") && consistent) {
            for (String sqlProp : sqlTokens) {
                if (portData.get(sqlProp) == null) {
                    consistent = false;
                }
            }
        }
        return consistent;
    }
    
    /***********************************************
	 * MOSGRID Changes
	 ***********************************************/
	private Map<String, Vector<JobPropertyBean>> tempJobPropertyMap = new HashMap<String, Vector<JobPropertyBean>>();
	private Lock TEMP_PROP_LOCK = new ReentrantLock(false);
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
		Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, wkfInstance.getWorkflowName());
		saveConfigData(userID, wkfInstance.getWorkflowName(), jobs);
	}

	/**
	 * Sets the number of expected input files of an input port (parameter sweep)
	 */
	public void setNumberOfInputFiles(String userID, String workflowID, String jobID, String portID, Integer value) {
		try {
			Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, workflowID);
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
			Vector<JobPropertyBean> jobs = getWorkflowConfig(userID, workflowID);

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
	
	/**
	 * Returns the port sequence given its name.
	 * @param userId The user owning the workflow.
	 * @param workflowName The name of the workflow.
	 * @param jobName The name of the job.
	 * @param portName The port name.
	 * @return The name of the input port. If the job or the port does not exist, an {@link IllegalArgumentException} will be thrown.
	 */
	public long getInputPortNumberByName(final String userId, final String workflowName, final String jobName, final String portName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		// first, find the job
		final Collection<JobPropertyBean> jobProperties = getWorkflowConfig(userId, workflowName);
        JobPropertyBean job = null;
        for (final JobPropertyBean jobProperty : jobProperties) {
        	if (jobName.equals(jobProperty.getName())) {
        		job = jobProperty;
        		break;
        	}
        }
        if (job == null) {
        	throw new IllegalArgumentException("job with name " + jobName + " does not exist for workflow " + workflowName + " from user " + userId);
        }
        // now find the input in the already found job
        PortDataBean portProperty = null;
        Collection<PortDataBean> inputs = (Collection<PortDataBean>)job.getInputs();
        PortDataBean port = null;
        for (final PortDataBean portData : inputs) {
        	if (portData.getName().equals(portName)) {
        		port = portData;
        		break;
        	}
        }
        if (port == null) {
        	throw new IllegalArgumentException("port with name " + portName + " does not exist in job " + jobName + ", in workflow " + workflowName + " from user " + userId);
        }
        return port.getSeq();
    }
	
	private void debug(final String location, final Object msg) {
		// check in case someone was sloppy
		if (DEBUG_MODE) {
			System.out.println("+++ASMService+++ [" + location + ']' + msg);
		}
	}
}

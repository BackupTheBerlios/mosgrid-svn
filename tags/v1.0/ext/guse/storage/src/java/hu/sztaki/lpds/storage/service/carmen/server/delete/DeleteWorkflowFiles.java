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
package hu.sztaki.lpds.storage.service.carmen.server.delete;

import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaService;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.wfs.com.VolatileBean;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import java.util.ArrayList;

/**
 * Deletes the stored data of a workflow.
 *
 * @author lpds
 */
public class DeleteWorkflowFiles {
    
    // repository root dir
    private String repositoryDir;
    
    // File separator (for example, "/")
    private String sep;
/**
 * Loading constructor properties
 * @throws java.lang.Exception
 */
    public DeleteWorkflowFiles() throws Exception {
        LoadProperty();
    }
    
    /**
     * Init, loading initializing data.
     */
    private void LoadProperty() {
        sep = FileUtils.getInstance().getSeparator();
        repositoryDir = FileUtils.getInstance().getRepositoryDir();
        FileUtils.getInstance().createRepositoryDirectory();
    }
    
    /**
     * Returns the base directory of the current workflow.
     * In the meanwhile checks the data from the bean.
     *
     * @param comDataBean: portalID, userID, workflowID, (runtimeID)
     * @throws Exception
     */
    private String getWorkflowBaseDir(ComDataBean comDataBean) throws Exception {
        if (comDataBean != null) {
            String portalID = comDataBean.getPortalID();
            String userID = comDataBean.getUserID();
            String workflowID = comDataBean.getWorkflowID();
            if ((portalID == null) || ("".equals(portalID))) {
                throw new Exception("Not valid portalID in ComDataBean !");
            }
            if ((userID == null) || ("".equals(userID))) {
                throw new Exception("Not valid userID in ComDataBean !");
            }
            if ((workflowID == null) || ("".equals(workflowID))) {
                throw new Exception("Not valid workflowID in ComDataBean !");
            }
            return new String(repositoryDir + portalID + sep + userID + sep + workflowID + sep);
        } else {
            throw new Exception("Not valid comDataBean ! (null)");
        }
        // return new String("");
    }
    
    /**
     * Deletes all the stored data of the workflow given in the parameters.
     * The data is received in a communication object (ComDataBean).
     *
     * @param comDataBean portalID, userID, workflowID
     * @throws Exception file deletion error
     */
    public void deleteWorkflow_all(ComDataBean comDataBean) throws Exception {
        // System.out.println("");
        // System.out.println("deleteWorkflow_all begin...");
        // deleting the workflow data
        if (comDataBean != null) {
            comDataBean = convertBeanPortalID(comDataBean);
            //
            doDeleteWorkflowAll(comDataBean, getWorkflowBaseDir(comDataBean));
            //
        } else {
            throw new Exception("Not valid comDataBean ! (null)");
        }
        // System.out.println("deleteWorkflow_all end...");
    }
    
    /**
     * Deletes all the stored outputs (with all runtimIDs) of 
     * all of the jobs of the workflow given in the parameters.
     * The data is received in a communication object (ComDataBean).
     *
     * @param comDataBean portalID, userID, workflowID
     * @throws Exception deletion error
     */
    public void deleteWorkflow_outputs_all(ComDataBean comDataBean) throws Exception {
        // System.out.println("");
        // System.out.println("deleteWorkflow_outputs_all begin...");
        // workflow adatainak torlese
        if (comDataBean != null) {
            comDataBean = convertBeanPortalID(comDataBean);
            //
            doDeleteWorkflowOutputsAll(comDataBean, getWorkflowBaseDir(comDataBean));
            //
        } else {
            throw new Exception("Not valid comDataBean ! (null)");
        }
        // System.out.println("deleteWorkflow_outputs_all end...");
    }
    
    /**
     * Deletes output log files in a given workflow/job.
     * @param comDataBean portalID, userID, workflowID, runtimeID, jobID
     * @throws Exception file deletion error
     */
    public void deleteWorkflow_log_outputs(ComDataBean comDataBean) throws Exception {
        // System.out.println("");
        // System.out.println("deleteWorkflow_log_outputs begin...");
        // workflow/job log data deletion
        if (comDataBean != null) {
            comDataBean = convertBeanPortalID(comDataBean);
            //
            doDeleteWorkflowLogOutputs(comDataBean, getWorkflowBaseDir(comDataBean));
            //
        } else {
            throw new Exception("Not valid comDataBean ! (null)");
        }
        // System.out.println("deleteWorkflow_log_outputs end...");
    }
    
    /**
     * Deleted all the data of the outputs (with the given runtimeID) 
     * of all the jobs of the workflow given in the parameters. 
     * The data is received in a communication object (ComDataBean).
     *
     * @param comDataBean portalID, userID, workflowID, runtimeID
     * @throws Exception file deletion error
     */
    public void deleteWorkflow_outputs_rtID(ComDataBean comDataBean) throws Exception {
        // System.out.println("");
        // System.out.println("deleteWorkflow_outputs_rtID begin...");
        // workflow data deletion
        if (comDataBean != null) {
            comDataBean = convertBeanPortalID(comDataBean);
            String runtimeID = comDataBean.getWorkflowRuntimeID();
            if ((runtimeID == null) || ("".equals(runtimeID))) {
                throw new Exception("Not valid runtimeID in ComDataBean !");
            }
            //
            doDeleteWorkflowOutputsRtID(comDataBean, getWorkflowBaseDir(comDataBean));
            //
        } else {
            throw new Exception("Not valid comDataBean ! (null)");
        }
        // System.out.println("deleteWorkflow_outputs_rtID end...");
    }
    
    /**
     * Deletes all the volatile output files of a workflow.
     * @param volatileBean  - Workflow and volatile data
     * @throws Exception file deletion error
     */
    public void deleteWorkflow_volatile_outputs(VolatileBean volatileBean) throws Exception {
        // System.out.println("deleteWorkflow_volatile_outputs begin...");
        if (volatileBean.getComDataBean() != null) {
            volatileBean.setComDataBean(convertBeanPortalID(volatileBean.getComDataBean()));
            //
            String runtimeID = volatileBean.getComDataBean().getWorkflowRuntimeID();
            if ((runtimeID == null) || ("".equals(runtimeID))) {
                throw new Exception("Not valid runtimeID in ComDataBean !");
            }
            //
            doDeleteWorkflowVolatileOutputs(volatileBean, getWorkflowBaseDir(volatileBean.getComDataBean()));
            //
        } else {
            throw new Exception("Not valid comDataBean ! (null)");
        }
    }
    
    /**
     * Converts the portalID to a directory name in the given bean.
     *
     * @param comDataBean
     * @return comDataBean
     * @throws Exception
     */
    private ComDataBean convertBeanPortalID(ComDataBean comDataBean) throws Exception {
        String portalID = comDataBean.getPortalID();
        if ((portalID == null) || ("".equals(portalID))) {
            throw new Exception("Not valid portalID in ComDataBean !");
        } else {
            portalID = FileUtils.getInstance().convertPortalIDtoDirName(portalID);
            comDataBean.setPortalID(portalID);
        }
        return comDataBean;
    }
    
    /**
     * Deletes all stored data of the workflow given in the parameters.
     *
     * @param workflowBaseDir
     * @throws Exception
     */
    private synchronized void doDeleteWorkflowAll(ComDataBean comDataBean, String workflowBaseDir) throws Exception {
        // System.out.println(": delete wf all: " + workflowBaseDir);
        // System.out.println("portal   : "+comDataBean.getPortalID());
        // System.out.println("user     : "+comDataBean.getUserID());
        // System.out.println("workflow : "+comDataBean.getWorkflowID());
        long plussQuotaSize = 0;
        plussQuotaSize += FileUtils.getInstance().deleteDirectory(workflowBaseDir);
        // refresh workflow quota
        deleteWorkflowAllQuota(comDataBean);
    }
    
    /**
     * Deletes all the stored outputs (with all runtimIDs) of 
     * all of the jobs of the workflow given in the parameters.
     *
     * @param workflowBaseDir
     * @throws Exception
     */
    private synchronized void doDeleteWorkflowOutputsAll(ComDataBean comDataBean, String workflowBaseDir) throws Exception {
        // System.out.println(": delete wf  outputs all: " + workflowBaseDir);
        long plussQuotaSize = 0;
        // get job list
        ArrayList jobList = FileUtils.getInstance().getSubDirList(workflowBaseDir);
        // parse jobList
        if (jobList.size() > 0) {
            for (int jobPos = 0; jobPos < jobList.size(); jobPos++) {
                String workflowJobOutputDirPath = workflowBaseDir + ((String) jobList.get(jobPos)) + sep + "outputs";
                // false parameter result: the empty outputs directory stays there
                plussQuotaSize += FileUtils.getInstance().deleteDirectory(workflowJobOutputDirPath, false);
            }
        }
        // refresh workflow quota
        deleteWorkflowAllOutputsQuota(comDataBean);
    }
    
    /**
     * Deletes output log files in a given workflow/job.
     *
     * @param comDataBean: portalID, userID, workflowID, runtimeID, jobID
     * @param workflowBaseDir
     * @throws Exception
     */
    private synchronized void doDeleteWorkflowLogOutputs(ComDataBean comDataBean, String workflowBaseDir) throws Exception {
        // System.out.println(": delete wf log outputs: " + workflowBaseDir);
        long plussQuotaSize = 0;
        // old path: String workflowJobOutputRtIDDirPath = workflowBaseDir + comDataBean.getJobID() + sep + "outputs" + sep + comDataBean.getWorkflowRuntimeID() + sep;
        String workflowJobOutputRtIDDirPath = workflowBaseDir + comDataBean.getJobID() + sep + "outputs" + sep + comDataBean.getWorkflowRuntimeID() + sep + comDataBean.getJobPID() + sep;
        // old : plussQuotaSize += FileUtils.getInstance().deleteDirectoryAllLogFiles(workflowJobOutputRtIDDirPath, comDataBean.getJobPID());
        // new : because the output and log files are separated for every pid
        // all the output and log files can be deleted belonging to the job's jobpid
        // called when resumed
        plussQuotaSize += FileUtils.getInstance().deleteDirectory(workflowJobOutputRtIDDirPath, false);
        // refresh workflow quota
        deleteWorkflowRtIDLogQuota(comDataBean, plussQuotaSize);
    }
    
    /**
     * Deleted all the data of the outputs (with the given runtimeID) 
     * of all the jobs of the workflow given in the parameters. 
     *
     * @param comDataBean: portalID, userID, workflowID, runtimeID
     * @param workflowBaseDir
     * @throws Exception
     */
    private synchronized void doDeleteWorkflowOutputsRtID(ComDataBean comDataBean, String workflowBaseDir) throws Exception {
        // System.out.println(": delete wf outputs rtid: " + workflowBaseDir);
        long plussQuotaSize = 0;
        String runtimeID = comDataBean.getWorkflowRuntimeID();
        // get job list
        ArrayList jobList = FileUtils.getInstance().getSubDirList(workflowBaseDir);
        // parse jobList
        if (jobList.size() > 0) {
            for (int jobPos = 0; jobPos < jobList.size(); jobPos++) {
                String workflowJobOutputRtIDDirPath = workflowBaseDir + ((String) jobList.get(jobPos)) + sep + "outputs" + sep + runtimeID;
                plussQuotaSize += FileUtils.getInstance().deleteDirectory(workflowJobOutputRtIDDirPath);
                workflowJobOutputRtIDDirPath = workflowBaseDir + ((String) jobList.get(jobPos)) + sep + runtimeID;
                plussQuotaSize += FileUtils.getInstance().deleteDirectory(workflowJobOutputRtIDDirPath);
            }
        }
        // refresh workflow quota
        deleteWorkflowRtIDQuota(comDataBean);
    }
    
    /**
     * Deletes all the volatile output files of a workflow.
     * @param VolatileBean - Workflow and volatile data
     * @param workflowBaseDir
     * @throws Exception
     */
    private synchronized void doDeleteWorkflowVolatileOutputs(VolatileBean volatileBean, String workflowBaseDir) throws Exception {
        // System.out.println(": delete wf volatile outputs: " + workflowBaseDir);
        new DeleteVolatileOutputsThread(volatileBean, workflowBaseDir);
    }
    
    /**
     * Refreshes the data of the current workflow in the quota registry.
     *
     * @param comDataBean: portalID, userID, workflowID
     */
    private void deleteWorkflowAllQuota(ComDataBean comDataBean) {
        try {
            QuotaService.getInstance().deleteWorkflowAllQuotaItem(comDataBean.getPortalID(), comDataBean.getUserID(), comDataBean.getWorkflowID());
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
    
    /**
     * Refreshes the data of the current workflow in the quota registry.
     *
     * @param comDataBean: portalID, userID, workflowID
     */
    private void deleteWorkflowAllOutputsQuota(ComDataBean comDataBean) {
        try {
            QuotaService.getInstance().deleteWorkflowAllOutputsQuotaItem(comDataBean.getPortalID(), comDataBean.getUserID(), comDataBean.getWorkflowID());
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
    
    /**
     * Refreshes the data of the current workflow in the quota registry.
     *
     * @param comDataBean: portalID, userID, workflowID, runtimeID
     */
    private void deleteWorkflowRtIDQuota(ComDataBean comDataBean) {
        try {
            QuotaService.getInstance().deleteWorkflowRtIDQuotaItem(comDataBean.getPortalID(), comDataBean.getUserID(), comDataBean.getWorkflowID(), comDataBean.getWorkflowRuntimeID());
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
    
    /**
     * Refreshes the data of the current workflow in the quota registry.
     *
     * @param comDataBean: portalID, userID, workflowID, runtimeID
     */
    private void deleteWorkflowRtIDLogQuota(ComDataBean comDataBean, long plussQuotaSize) {
        try {
            QuotaService.getInstance().deleteWorkflowRtIDLogQuotaItem(comDataBean.getPortalID(), comDataBean.getUserID(), comDataBean.getWorkflowID(), comDataBean.getWorkflowRuntimeID(), plussQuotaSize);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

}

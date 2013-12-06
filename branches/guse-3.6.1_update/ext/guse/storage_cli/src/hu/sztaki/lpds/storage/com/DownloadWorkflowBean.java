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

package hu.sztaki.lpds.storage.com;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author lpds MTA SZTAKI
 * @version 3.3 
 */
public class DownloadWorkflowBean {
    
     /** type of exportation is workflow */ 
    private static final String exportType_work = new String("work");
    
     /** type of exportation is project */
    private static final String exportType_proj = new String("proj");
    
    /** type of exportation is application */
    private static final String exportType_appl = new String("appl");
    
    /** all the inputs must be downloaded */ 
    public static final String downloadType_inputs = new String("inputs");
    
     /** all the inputs and outputs that are specified by a given runtime ID must be downloaded (graph and real workflow case)*/ 
    public static final String downloadType_inputs_rtID = new String("inputs_");
    
    /** all the outputs specified by a given runtime ID must be downloaded */ 
    public static final String downloadType_outputs_rtID = new String("outputs_");
    
    /** all the outputs of the given job specified by a given runtime ID and pidID */
    public static final String downloadType_job_outputs_rtID = new String("joboutputs_");

    /** all the instances which can be run */
    public static final String downloadType_rtID_all = new String("all");
    
    /** none of the instances will be downloaded */ 
    public static final String instanceType_none = new String("none");
    
    /** one concrete instance will be downloaded */ 
    public static final String instanceType_one_rtID = new String("one_");
    
    /** all instances will be downloaded */ 
    public static final String instanceType_all = new String("all");
    
     /** log files won't be downloaded */ 
    public static final String outputLogType_none = new String("none");
    
    /** all of the log files will be downloaded */ 
    public static final String outputLogType_all = new String("all");
    
    // public final static String workflowDetailsInformationFileName = new String("details");

    /** name of the workflow descriptor file*/
    public final static String workflowXMLDetailsInformationFileName = new String("workflow.xml");
    /** name of the file that contains informations about the workflow */
    public final static String workflowDetailsInformationFileName = new String("info.txt");
    
    /** URL of the Workflow Service */ 
    private String wfsID;
    
    /** URL of the Portal Service */ 
    private String portalURL;
    
     /** ID of the Portal Service */ 
    private String portalID;
    
     /** ID of the user */ 
    private String userID;
    
    /** ID of the workflow */ 
    private String workflowID;
    
     /** ID of the job */ 
    private String jobID;
    
    /** ID of the parametric execution */ 
    private String pidID;
    
    //** type of the download method {all,graf,abst,real}*/ 
    private String downloadType;
    
     /** Should the instance descriptions be downloaded? {none,all} */ 
    private String instanceType;
    
    // outputLogType
    private String outputLogType;
    
    /** runtime ID */ 
    private String runtimeID;
    
    /** name of the zip file that will be downloaded */ 
    private String downZipFileName;
    
    /** type of the exportation {appl,proj,work} */ 
    private String exportType;
    
     /**
     * Class constructor
     */ 
    public DownloadWorkflowBean() {
        this.setDownloadType("");
        this.setInstanceType("");
        this.setOutputLogType("");
        this.setExportType(exportType_work);
    }
    
    /**
     * Getter method of the Workflow Storage Service
     * @return WFS id
     */ 
    public String getWfsID() {
        return wfsID;
    }
    
    /**
     * Setter method of the Workflow Storage Service
     * @param WFS id
     */ 
    public void setWfsID(String wfsID) {
        this.wfsID = wfsID;
    }
    
    /**
     * Getter method of the portalURL variable
     * @return portal URL
     */ 
    public String getPortalURL() {
        return portalURL;
    }
    
    /**
     * Setter method of the PortalURL
     * @param portalURL portal URL
     */ 
    public void setPortalURL(String portalURL) {
        this.portalURL = portalURL;
    }
    
    /**
     * Getter method of the PortalID
     * @return portal id
     */ 
    public String getPortalID() {
        return portalID;
    }
    
     /**
     * Setter method of the PortalID
     * @param portalID id
     */ 
    public void setPortalID(String portalID) {
        this.portalID = portalID;
    }
    
     /**
     * Getter method of the UserID
     * @return user name
     */ 
    public String getUserID() {
        return userID;
    }
    
    /**
     * Setter method of the UserID
     * @param userID user id
     */ 
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    /**
     * Getter method of the workflowID
     * @return workflow name
     */
    public String getWorkflowID() {
        return workflowID;
    }
    
    /**
     * Setter method of the workflowID
     * @param workflowID workflow name
     */ 
    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
    }
    
    /**
     * Getter method of the JobID.
     * @return job name
     */ 
    public String getJobID() {
        return jobID;
    }
    
    /**
     * Getter method of the JobID.
     * @param jobID job name
     */ 
    public void setJobID(String jobID) {
        this.jobID = jobID;
    }
    
     /**
     * Getter method of the job's parametric ID.
     *
     * @return parametric ID
     */ 
    public String getPidID() {
        return pidID;
    }
    
    /**
     * Setter method of the job's parametric ID.
     * @param pidID job parametric ID
     */ 
    public void setPidID(String pidID) {
        this.pidID = pidID;
    }
    
    /**
     * Getter method of the type of the downloading.
     * @return download type
     */ 
    public String getDownloadType() {
        return downloadType;
    }
    
    /**
     * Setter method of the type of the downloading.
     * @param downloadType download type
     */ 
    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }
    
     /**
     * Getter method of the type of the instance.
     * @return instance type
     */ 
    public String getInstanceType() {
        return instanceType;
    }
    
     /**
     * Setter method of the type of the instance.
     * @param instanceType instance type
     */ 
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }
    
    /**
     * Getter method of the type of the output logging.
     * @return download output log type
     */ 
    public String getOutputLogType() {
        return outputLogType;
    }
    
    /**
     * Setter method of the type of the output logging.
     * @param outputLogType download output log type
     */ 
    public void setOutputLogType(String outputLogType) {
        this.outputLogType = outputLogType;
    }
    
    /**
     * Setter method of the runtimeID (ID of the instance).
     * @return runtime id
     */ 
    public String getRuntimeID() {
        return runtimeID;
    }
    
   /**
     * Setter method of the runtimeID (ID of the instance).
     * @param runtimeID  internal id in runtime
     */ 
    public void setRuntimeID(String runtimeID) {
        this.runtimeID = runtimeID;
    }
    
    /**
     * Getter method of the name of the zip file to download.
     * @return zip file name
     */ 
    public String getDownZipFileName() {
        return downZipFileName;
    }
    
     /**
     * Setter method of the name of the zip file to download.
     * @param downZipFileName zip file name
     */ 
    public void setDownZipFileName(String downZipFileName) {
        this.downZipFileName = downZipFileName;
    }
    
     /**
     * Getter method of the ExportType.
     * @return export type
     */ 
    public String getExportType() {
        return exportType;
    }
    
    /**
     * Setter method of the ExportType.
     * @param exportType export type
     */ 
    public void setExportType(String exportType) {
        this.exportType = exportType;
    }
    
    /**
     * Checks the type of the downloading method.
     * @return true if everything must be downloaded
     */ 
    public boolean isAll() {
        // all input and output with all runtime ID (in all workflow type (graf,real,abst)) 
        return downloadType.equalsIgnoreCase("all");
    }
    
     /**
     * Checks the type of the workflow
     * @return true if the workflow is a graph
     */ 
    public boolean isGraf() {
        // (graf workflow)
        return downloadType.equalsIgnoreCase("graf");
    }
    
    /**
     * Checks the type of the workflow
     * @return true if the type of the workflow is <b>template</b>
     */ 
    public boolean isAbst() {
        // download all inputs (graph and abst workflow)
        return downloadType.equalsIgnoreCase("abst");
    }
    
    /**
     * Checks the type of the workflow.
     * @return true if the type of the workflow is <b>real</b> (concrete workflow)
     */ 
    public boolean isReal() {
        // download all outputs for all runtime ID and so on 
        return downloadType.equalsIgnoreCase("real");
    }
    
    /**
     * Checks the type of the workflow 
     * @return true if the workflow is exported as an <b>application</b>
     */ 
    public boolean isAppl() {
        //  all of the embedded workflow must be downloaded 
        // plus module mode on
        return exportType.equalsIgnoreCase("appl");
    }
    
    /**
     * Checks the type of the workflow 
     * @return true if the workflow is exported as <b> project</b>
     */ 
    public boolean isProj() {
        // all of the embedded workflow must be downloaded
        // plus module mode off
        return exportType.equalsIgnoreCase("proj");
    }
    
    /**
     * Checks the type of the workflow
     * @return true if the workflow is exported as <b>workflow</b>
     */ 
    public boolean isWork() {
        // none of the embedded workflow should be downloaded
        // plus module mode off 
        return exportType.equalsIgnoreCase("work");
    }
    
}

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

package hu.sztaki.lpds.pgportal.services.asm.beans;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.pgportal.services.asm.exceptions.general.ASM_GeneralWebServiceException;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Class to store details (output logs/statuses) for a job instance
 * @author akos
 * @version 3.3
 */
public class ASMJobInstanceBean {

    private String userId;
    private String workflowId;
    private String jobId;
    private String id;
    private String status;
    private String outputText;
    private String errorText;
    private String logbookText;
    private String usedResource;
    private String pid;
    private String WFS;
    private String stdOutputPath;
    private String stdErrorPath;
    private String logBookPath;


     /**
     * Returns the link to the logbook file to be downloaded/viewed
     * @return link
     */
    public String getLogBookPath() {
        return logBookPath;
    }

    public void setLogBookPath(String logBookPath) {
        this.logBookPath = logBookPath;
    }

     /**
     * Returns the link to the standard error to be downloaded/viewed
     * @return link
     */
    public String getStdErrorPath() {
        return stdErrorPath;
    }

    public void setStdErrorPath(String stdErrorPath) {
        this.stdErrorPath = stdErrorPath;
    }
    /**
     * Returns the link to the standard output to be downloaded/viewed
     * @return link
     */
    public String getStdOutputPath() {
        return stdOutputPath;
    }

    public void setStdOutputPath(String stdOutputPath) {
        this.stdOutputPath = stdOutputPath;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getRuntimeID() {
        return runtimeID;
    }

    public void setRuntimeID(String runtimeID) {
        this.runtimeID = runtimeID;
    }
    private String runtimeID;
    /**
     * Gets the id of the job instance
     * @return id
     */
    public String getId() {
        return id;
    }
/**
 * Setter method
 * @param id
 */
    public void setId(String id) {
        this.id = id;
    }
    public String getUsedResource() {
        return usedResource;
    }
/**
 * Setter method
 * @param usedResource
 */
    public void setUsedResource(String usedResource) {
        this.usedResource = usedResource;
    }
    
/**
 * Constructor
 * @param id - id of the job instance
 * @param status - status of the job instance
 * @param usedResource - resource, where the job instance was submitted
 * @param outputText - stdout.log
 * @param errorText - stderr.log
 * @param logbookText - gridnfo.log
 */
    public ASMJobInstanceBean(String userID,String workflowId,String jobId, String id, String status,String usedResource,String WFS) {
        this.id = id;
        this.status = status;
        this.usedResource = usedResource;
        this.outputText = "";
        this.errorText = "";
        this.logbookText = "";
        this.WFS = WFS;
    }
/**
 * @deprecated
 * Default constructor
 */
    public ASMJobInstanceBean(String WFS) {
        this.WFS = WFS;
    }
    
 /**
 * 
 * Default constructor
 */
     public ASMJobInstanceBean() {
        
    }
/**
 * @deprecated It is replaced by {@link #getStdErrorPath()}
 *
 *
 * Gets the std. error text
 * @return std.err
 */
    public String getErrorText() {
           return getLogFile(userId,workflowId,jobId,pid,runtimeID,"stderr.log");
    }

    /**
     * @deprecated
     * 
     * Setter method
     * @param errorText
     */
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
/**
 *
 * @deprecated It is replaced by {@link #getLogBookPath() }
 * 
 * Gets logging information generated by guse system (wrapper script)
 * @return
 */
    public String getLogbookText() {
          return getLogFile(userId,workflowId,jobId,pid,runtimeID,"gridnfo.log");
    }
/**
 * @deprecated
 * Setter method
 * @param logbookText
 */
    public void setLogbookText(String logbookText) {
        this.logbookText = logbookText;
    }

/**
 * @deprecated It is replaced by {@link #getStdOutputPath()  }
 * Gets the standard output text generated by the job instance
 * @return std.out
 */
    public String getOutputText() {
        return getLogFile(userId,workflowId,jobId,pid,runtimeID,"stdout.log");
    }
/**
 * Setter method
 * @param outputText
 */
    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }
/**
 * Gets the status of the job instance
 * @return
 */
    public String getStatus() {
        return status;
    }
/**
 * Setter method
 * @param status
 */
    public void setStatus(String status) {
        this.status = status;
    }

  
    
    /**
     * @deprecated
     * @param userID
     * @param workflowID
     * @param jobID
     * @param pidID
     * @param runtimeID
     * @param fileID
     * @return
     */
    private String getLogFile(String userID,String workflowID,String jobID, String pidID,String runtimeID, String fileID){
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
                    throw new ASM_GeneralWebServiceException(ex.getCause(),userID);
                }
            } catch (IOException ex) {
                throw new ASM_GeneralWebServiceException(ex.getCause(),userID);

            } catch (ClassNotFoundException ex) {
                throw new ASM_GeneralWebServiceException(ex.getCause(),userID);
            } catch (InstantiationException ex) {
                throw new ASM_GeneralWebServiceException(ex.getCause(),userID);
            } catch (IllegalAccessException ex) {
                throw new ASM_GeneralWebServiceException(ex.getCause(),userID);
            }
            finally {
                try {
                    is.close();
                } catch (IOException ex) {


                }
            }
        }

    }

    /*String stdout this.getStdOutFile(userID, workflowID, jobname, pid, runtimeID);
     String stderr = this.getStdErrFile(userID, workflowID, jobname, pid, runtimeID);
                String systemlog = this.getSystemLogFile(userID, workflowID, jobname, pid, runtimeID);
                */

    /**
  * Converts an inputstream to string
  *
  * @param is - InputStream
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
    //return sb.toString();
    return formattedString;
  }



}

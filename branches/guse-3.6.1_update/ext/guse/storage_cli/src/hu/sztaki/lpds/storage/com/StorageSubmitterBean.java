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

import java.util.Hashtable;

/**
 *
 * Communication bean that stores information about the file that will be down-, or uploaded.
 * FileGetter and FileSender objects use this
 *
 * @author lpds
 */ 
public class StorageSubmitterBean {
    
    // FileGetter use
    // FileSender use
    private String storageURL;
    
    // FileGetter use : getFilesDir
    // FileSender use : sendFilesDir
    private String filesDirPath;
    
    // FileGetter use
    // FileSender use
    private String portalID;
    
    // FileGetter use
    // FileSender use
    private String userID;
    
    // FileGetter use
    // FileSender use
    private String workflowID;
    
    // FileSender use
    private String jobID;
    
    // FileSender use
    private String runtimeID;
    
    // FileGetter use : fileRenameHash
    // FileSender use : copyHash
    private Hashtable fileHash;
    
    // FileGetter use
    // FileSender use
    private Boolean localMode;

    // FileSender use
    private String pidID;
    
    /**
     * Constructor for JavaBeans
     */
    public StorageSubmitterBean() {
    }
    
    /**
     * Constructor for easier use (FileGetter)
     *
     * @param storageURL storage ID (URL)
     * @param filesDirPath files path
     * @param portalID portal ID
     * @param userID user ID
     * @param fileHash file hashtable
     * @param localMode working mode descriptor
     */
    public StorageSubmitterBean(String storageURL, String filesDirPath, String portalID, String userID, Hashtable fileHash, Boolean localMode) {
        setStorageURL(storageURL);
        setFilesDirPath(filesDirPath);
        setPortalID(portalID);
        setUserID(userID);
        setFileHash(fileHash);
        setLocalMode(localMode);
    }
    
    /**
     * Constructor for easier use (FileSender)
     *
     * @param storageURL storage ID (URL)
     * @param filesDirPath files path
     * @param portalID portal ID
     * @param userID user ID
     * @param workflowID workflow ID
     * @param jobID job ID
     * @param runtimeID workflow runtime ID
     * @param fileHash file hashtable
     * @param localMode working mode descriptor
     * @param pidID parametric job instance ID
     */
    public StorageSubmitterBean(String storageURL, String filesDirPath, String portalID, String userID, String workflowID, String jobID, String pidID, String runtimeID, Hashtable fileHash, Boolean localMode) {
        setStorageURL(storageURL);
        setFilesDirPath(filesDirPath);
        setPortalID(portalID);
        setUserID(userID);
        setWorkflowID(workflowID);
        setJobID(jobID);
        setPidID(pidID);
        setRuntimeID(runtimeID);
        setFileHash(fileHash);
        setLocalMode(localMode);
    }
    
    /**
     * Returns the storage ID. (URL)
     * @return storage URL
     */
    public String getStorageURL() {
        return storageURL;
    }
    
    /**
     * Sets the storage ID. (URL)
     * @param storageURL storage service accessibility (URL)
     */
    public void setStorageURL(String storageURL) {
        this.storageURL = storageURL;
    }
    
    /**
     * Returns the files DIR value.
     * @return files DIR
     */
    public String getFilesDirPath() {
        return filesDirPath;
    }
    
    /**
     * Sets the files dir value.
     * @param filesDirPath  files dir
     */
    public void setFilesDirPath(String filesDirPath) {
        this.filesDirPath = filesDirPath;
    }
    
    /**
     * Returns the portal ID
     * @return portal ID
     */
    public String getPortalID() {
        return portalID;
    }
    
    /**
     * Sets the portal ID
     * @param portalID  portal ID
     */
    public void setPortalID(String portalID) {
        this.portalID = portalID;
    }
    
    /**
     * Returns the user ID
     * @return user ID
     */
    public String getUserID() {
        return userID;
    }
    
    /**
     * Sets the user ID
     * @param userID  user ID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    /**
     * Returns the workflow ID
     * @return workflow ID
     */
    public String getWorkflowID() {
        return workflowID;
    }
    
    /**
     * Sets the workflow ID
     * @param workflowID  workflow ID
     */
    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
    }
    
    /**
     * Returns the job ID
     * @return job ID
     */
    public String getJobID() {
        return jobID;
    }
    
    /**
     * Sets the job ID
     * @param jobID  job ID
     */
    public void setJobID(String jobID) {
        this.jobID = jobID;
    }
    
    /**
     * Returns the job PID ID
     * @return job PID ID
     */
    public String getPidID() {
        return pidID;
    }
    
    /**
     * Sets the job PID ID
     * @param pidID  job PID ID
     */
    public void setPidID(String pidID) {
        this.pidID = pidID;
    }
    
    /**
     * Returns the runtime id value. (instance ID)
     * @return runtime id
     */
    public String getRuntimeID() {
        return runtimeID;
    }
    
    /**
     * Sets the runtime id value. (instance ID)
     * @param runtimeID  runtime id
     */
    public void setRuntimeID(String runtimeID) {
        this.runtimeID = runtimeID;
    }
    
    /**
     * Returns the file hash value.
     * @return file hash
     */
    public Hashtable getFileHash() {
        return fileHash;
    }
    
    /**
     * Sets the file hash value.
     * @param fileHash  file hash
     */
    public void setFileHash(Hashtable fileHash) {
        this.fileHash = fileHash;
    }
    
    /**
     * Returns the local mode value.
     * @return local mode
     */
    public Boolean getLocalMode() {
        return localMode;
    }
    
    /**
     * Sets the local mode value.
     * @param localMode  locale mode
     */
    public void setLocalMode(Boolean localMode) {
        this.localMode = localMode;
    }
    
}

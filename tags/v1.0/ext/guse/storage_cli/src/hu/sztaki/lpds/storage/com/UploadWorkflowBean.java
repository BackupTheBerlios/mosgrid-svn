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
 * @author lpds
 */
public class UploadWorkflowBean {
    
    // portal ID
    private String portalID;
    
    // user ID
    private String userID;
    
    // workflow ID
    private String workflowID;
    
    // configuration ID
    private String confID;
    
    // these files have to be deleted
    private Hashtable deletedFiles;
    
    /**
     * Constructor for JavaBeans
     */
    public UploadWorkflowBean() {
        this.setUserID("");
        this.setWorkflowID("");
        this.setConfID("");
        this.setDeletedFiles(new Hashtable());
    }
    
    /**
     * Constructor for easier use
     * @param confID configuration ID
     */
    public UploadWorkflowBean(String confID) {
        this.setUserID("");
        this.setWorkflowID("");
        this.setConfID(confID);
        this.setDeletedFiles(new Hashtable());
    }
    
    /**
     * Returns the portal ID.
     * @return portalID
     */
    public String getPortalID() {
        return portalID;
    }
    
    /**
     * Sets the portal ID.
     * @param portalID
     */
    public void setPortalID(String portalID) {
        this.portalID = portalID;
    }
    
    /**
     * Returns the user ID.
     * @return user id
     */
    public String getUserID() {
        return userID;
    }
    
    /**
     * Sets the user ID.
     * @param  userID user ID on the portal
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    /**
     * Returns the workflow ID.
     * @return workflow ID
     */
    public String getWorkflowID() {
        return workflowID;
    }
    
    /**
     * Sets the workflow ID.
     * @param workflowID workflow name
     */
    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
    }
    
    /**
     * Returns the configuration ID.
     * @return confID
     */
    public String getConfID() {
        return confID;
    }
    
    /**
     * Sets the configuration ID.
     * @param confID
     */
    public void setConfID(String confID) {
        this.confID = confID;
    }
    
    /**
     * Returns the deleted file hash.
     * @return deletedFiles
     */
    public Hashtable getDeletedFiles() {
        return deletedFiles;
    }
    
    /**
     * Sets the deleted file hash.
     * @param deletedFiles
     */
    public void setDeletedFiles(Hashtable deletedFiles) {
        this.deletedFiles = deletedFiles;
    }
    
}

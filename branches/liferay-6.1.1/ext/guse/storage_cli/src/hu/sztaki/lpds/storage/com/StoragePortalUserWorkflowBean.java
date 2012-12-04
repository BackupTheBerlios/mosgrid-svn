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

/**
 * @author krisztian
 */
public class StoragePortalUserWorkflowBean {
    
    private String userID;
    
    private String portalID;
    
    private String workflowID;
    
    /**
     * Constructor for JavaBeans
     */
    public StoragePortalUserWorkflowBean() {
    }
    
    /**
     * Constructor for easier use
     * @param pUserID  user name
     * @param pPortalID portal ID
     * @param pWorkflowID  workflow ID (name)
     */
    public StoragePortalUserWorkflowBean(String pUserID, String pPortalID, String pWorkflowID) {
        portalID=pPortalID;
        userID=pUserID;
        workflowID=pWorkflowID;
    }
    
    /**
     * Returns the user ID
     * @return user ID
     */
    public String getUserID() {
        return userID;
    }
    
    /**
     * Returns the portal ID
     * @return portal ID
     */
    public String getPortalID() {
        return portalID;
    }
    
    /**
     * Returns the workflow ID
     * @return  workflow ID
     */
    public String getWorkflowID() {
        return workflowID;
    }
    
    /**
     * Sets the user ID
     * @param value user ID
     */
    public void setUserID(String value) {
        userID=value;
    }
    
    /**
     * Sets the portal ID
     * @param value portal ID
     */
    public void setPortalID(String value) { 
        portalID=value;
    }
    
    /**
     * Sets the workflow ID
     * @param value workflow ID
     */
    public void setWorkflowID(String value) {
        workflowID=value;
    }
    
}

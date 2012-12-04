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
package hu.sztaki.lpds.storage.service.carmen.server.quota;

/**
 * QuotaBean.java
 *
 * Stores the quota data of the current workflow.
 *
 * (These data needed to be refreshed 
 * in the quota repository.)
 *
 * @author lpds
 */
public class QuotaBean {
    
    private String portalID;
    
    private String userID;
    
    private String workflowID;
    
    private String runtimeID;
    
    private Long plussQuotaSize;
    
    /**
     * Constructor for JavaBeans
     */
    public QuotaBean() {
        setPortalID("");
        setUserID("");
        setWorkflowID("");
        setRuntimeID("");
        setPlussQuotaSize(new Long(0));
    }
    
    /**
     * Constructor for easier use.
     * @param portalID portal ID
     * @param userID user ID on the portal
     * @param workflowID workflow name
     * @param runtimeID runtime ID
     * @param plussQuotaSize size of the quota enlargement
     */
    public QuotaBean(String portalID, String userID, String workflowID, String runtimeID, Long plussQuotaSize) {
        setPortalID(portalID);
        setUserID(userID);
        setWorkflowID(workflowID);
        setRuntimeID(runtimeID);
        setPlussQuotaSize(plussQuotaSize);
    }
    
    /**
     * Constructor for easier use.
     * @param portalID portal ID
     * @param userID user ID on the portal
     * @param workflowID workflow name
     * @param plussQuotaSize size of the quota enlargement
     */
    public QuotaBean(String portalID, String userID, String workflowID, Long plussQuotaSize) {
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
     * @param portalID portal ID
     */
    public void setPortalID(String portalID) {
        this.portalID = portalID;
    }
    
    /**
     * Returns the user ID.
     * @return userID user ID
     */
    public String getUserID() {
        return userID;
    }
    
    /**
     * Sets the user ID.
     * @param userID user ID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    /**
     * Returns the workflow ID.
     * @return workflowID
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
     * Returns the workflow runtime, instance ID.
     * @return runtimeID
     */
    public String getRuntimeID() {
        return runtimeID;
    }
    
    /**
     * Sets the workflow runtime, instance ID.
     * @param runtimeID runtime ID
     */
    public void setRuntimeID(String runtimeID) {
        this.runtimeID = runtimeID;
    }
    
    /**
     * Returns the size of the quota enlargement in bytes.
     * @return size of the quota enlargement
     */
    public Long getPlussQuotaSize() {
        return plussQuotaSize;
    }
    
    /**
     * Sets the quota size of the quota enlargement in bytes.
     * @param plussQuotaSize size in bytes
     */
    public void setPlussQuotaSize(Long plussQuotaSize) {
        this.plussQuotaSize = plussQuotaSize;
    }
    
}

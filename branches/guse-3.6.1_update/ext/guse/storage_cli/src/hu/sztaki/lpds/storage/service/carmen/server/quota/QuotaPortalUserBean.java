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
 * QuotaPortalUserBean.java
 *
 * Stores the quota data of a user.
 */

package hu.sztaki.lpds.storage.service.carmen.server.quota;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author lpds
 */
public class QuotaPortalUserBean {
    
    private String portalID;
    
    private String userID;
    
    private Hashtable workflows = new Hashtable();
    
    /**
     * Constructor for JavaBeans
     */
    public QuotaPortalUserBean() {
    }
    
    /**
     * Constructor for easier use.
     * @param portalID portal ID
     * @param userID user name
     */
    public QuotaPortalUserBean(String portalID, String userID) {
        setPortalID(portalID);
        setUserID(userID);
        workflows = new Hashtable();
    }
    
    /**
     * Constructor for easier use.
     * @param portalID portal ID
     * @param userID user name
     * @param bean descriptor QuotaWorkflowBean of one of the user's workflow 
     */
    public QuotaPortalUserBean(String portalID, String userID, QuotaWorkflowBean bean) {
        setPortalID(portalID);
        setUserID(userID);
        workflows = new Hashtable();
        addWorkflow(bean);
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
     * Sets the user ID
     * @param value user ID
     */
    public void setUserID(String value) {
        userID = value;
    }
    
    /**
     * Sets the portal ID
     * @param value portal ID
     */
    public void setPortalID(String value) {
        portalID = value;
    }
    
    /**
     * Adds a new workflow to the user's
     * quota repository.
     * @param bean The workflow descriptor QuotaWorkflowBean
     */
    public void addWorkflow(QuotaWorkflowBean bean) {
        workflows.put(bean.getWorkflowID(), bean);
    }
    
    /**
     * Overwrites an old workflow in the user's
     * quota repository.
     * @param bean The workflow descriptor QuotaWorkflowBean
     */
    public void setWorkflow(QuotaWorkflowBean bean) {
        workflows.put(bean.getWorkflowID(), bean);
    }
    
    /**
     * Removes the given workflow
     * from the user's quota repository.
     * @param workflowID workflow ID
     */
    public void deleteWorkflow(String workflowID) {
        workflows.remove(workflowID);
    }
    
    /**
     * Returns the requested QuotaWorkflowBean
     * from the user's quota repository.
     * @param workflowID workflow ID
     * @return QuotaWorkflowBean The requested workflow quota descriptor bean
     */
    public QuotaWorkflowBean getWorkflow(String workflowID) {
        if (!workflows.containsKey(workflowID)) {
            addWorkflow(new QuotaWorkflowBean(workflowID));
        }
        return (QuotaWorkflowBean) workflows.get(workflowID);
    }
    
    /**
     * Returns the user's quotaItems repository requested in the parameters.
     * The return value contains the names (key) and of every workflow of the user
     * and the workflowHash value (value).
     *
     * Returns the data of every workflow of the user in a
     * hash form, from the user's quota repository.
     *
     * @return Hashtable requested quotaHash
     */
    public Hashtable getUserQuotaHash() {
        Hashtable quotaHash = new Hashtable();
        Enumeration enumWf = workflows.keys();
        while (enumWf.hasMoreElements()) {
            String workflowID = (String) enumWf.nextElement();
            QuotaWorkflowBean workflowBean = (QuotaWorkflowBean) workflows.get(workflowID);
            quotaHash.put(workflowID, workflowBean.getWorkflowHash());
        }
        return quotaHash;
    }
    
}

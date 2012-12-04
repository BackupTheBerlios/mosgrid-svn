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
/**
 * Data structure for the storage and portal communication
 */
package hu.sztaki.lpds.portal.com;

import java.util.Hashtable;

/**
 * @author lpds
 */
public class StoragePortalWorkflowNamesBean {
    private String portalID;
    private String storageID;
    private String wfsID;
    private String userID;
    private String workflowType;
    private Hashtable grafList;
    private Hashtable abstList;
    private Hashtable realList;
    
    /**
     * Constructor for JavaBeans
     */
    public StoragePortalWorkflowNamesBean() {
        setPortalID("");
        setStorageID("");
        setWfsID("");
        setUserID("");
        setWorkflowType("");
        setGrafList(new Hashtable());
        setAbstList(new Hashtable());
        setRealList(new Hashtable());
    }
    
    /**
     *
     * Constructor for easier use
	 
     *
     * @param pPortalID portal ID
     * @param pStorageID storage ID
     * @param pWfsID workflow storage ID
     * @param pUserID user name
     * @param workflowtype workflow type (for example: zen)
     */
    public StoragePortalWorkflowNamesBean(String pPortalID, String pStorageID, String pWfsID, String pUserID,String workflowtype) {
        setPortalID(pPortalID);
        setStorageID(pStorageID);
        setWfsID(pWfsID);
        setUserID(pUserID);
        setWorkflowType(workflowType);
        setGrafList(new Hashtable());
        setAbstList(new Hashtable());
        setRealList(new Hashtable());
    }
/**
 * Getting the workflow type
 * @return workflow type (for example: zen)
 */
    public String getWorkflowType() {return workflowType;}
/**
 * Setting the workflow type
 * @param workflowType workflow type (for example: zen)
 */
    public void setWorkflowType(String workflowType) {this.workflowType = workflowType;}
    
    /**
     * Getting the portal service ID (URL)
     * @return portal URL
     */
    public String getPortalID() {return portalID;}
    
    /**
     * Setting the portal service ID (URL)
     * @param portalID  portal URL
     */
    public void setPortalID(String portalID) {this.portalID = portalID;}
    
    /**
     * Getting the storage service ID (URL)
     * @return storage URL
     */
    public String getStorageID() {return storageID;}
    
    /**
     * Setting the storage service ID (URL)
     * @param storageID  storage URL
     */
    public void setStorageID(String storageID) {this.storageID = storageID;}    
    
    /**
     * Getting the WFS service ID (URL)
     * @return WFS URL
     */
    public String getWfsID() {return wfsID;}
    
    /**
     * Setting the WFS service ID (URL)
     * @param wfsID   wfs URL
     */
    public void setWfsID(String wfsID) {this.wfsID = wfsID;}
    
    /**
     * Getting the user ID
     * @return userID
     */
    public String getUserID() {return userID;}
    
    /**
     * Setting the user ID
     * @param userID  userID
     */
    public void setUserID(String userID) {this.userID = userID;}
    
    /**
     * Getting the list of graphs
     * @return list
     */
    public Hashtable getGrafList() {return grafList;}
    
    /**
     * Setting the list of graphs
     * @param grafList  list
     */
    public void setGrafList(Hashtable grafList) {this.grafList = grafList;}
    
    /**
     * Getting the list of templates
     * @return list
     */
    public Hashtable getAbstList() {return abstList;}
    
    /**
     * Setting the list of templates
     * @param abstList   list
     */
    public void setAbstList(Hashtable abstList) {this.abstList = abstList;}
    
    /**
     * Getting the list of workflows
     * @return list
     */
    public Hashtable getRealList() {return realList;}
    
    /**
     * Setting the list of workflows
     * @param realList list
     */
    public void setRealList(Hashtable realList) {this.realList = realList;}
    
}

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
 * Bean used for external workflow manager
 */
package hu.sztaki.lpds.portal.com;

/**
 * @author lpds
 */
public class ToolComDataBean {
    private String portalID;
    private String userID;
    private String workflowID;
    private String storageID;
    private String wfsID;
    private String workflowText;
    private String parentWorkflowID;
    private String proxyValue;
    private String grid;
    
    /**
     * Constructor without parameters for handling JavaBean
     */
    public ToolComDataBean() {}
    
    /**
     * Constructor for easier use
     *
     * @param portalID portal ID
     * @param userID user name, ID
     * @param workflowID the real, concrete workflow ID
     */
    public ToolComDataBean(String portalID, String userID, String workflowID) {
        setPortalID(portalID);
        setUserID(userID);
        setWorkflowID(workflowID);
    }
    
    /**
     * Getting portal ID
     * @return portal ID
     */
    public String getPortalID() {return portalID;}
    
    /**
     * Setting portal ID
     * @param portalID portal ID
     */
    public void setPortalID(String portalID) {this.portalID = portalID;}
    
    /**
     * Getting user ID
     * @return  user ID
     */
    public String getUserID() {return userID;}
    
    /**
     * Setting user ID
     * @param userID user ID
     */
    public void setUserID(String userID) {this.userID = userID;}
    
    /**
     * Getting the workflow ID (name)
     * @return workflow ID
     */
    public String getWorkflowID() {return workflowID;}
    
    /**
     * Setting the workflow ID (name)
     * @param workflowID workflow ID
     */
    public void setWorkflowID(String workflowID) {this.workflowID = workflowID;}
    
    /**
     * Getting storage service ID (URL)
     * @return Storage URL
     */
    public String getStorageID() {return storageID;}
    
    /**
     * Setting storage service ID (URL)
     * @param storageID Storage URL
     */
    public void setStorageID(String storageID) {this.storageID = storageID;}
    
    /**
     * Getting WFS service ID (URL)
     * @return WFS URL
     */
    public String getWfsID() {return wfsID;}
    
    /**
     * Setting WFS service ID (URL)
     * @param wfsID  WFS service
     */
    public void setWfsID(String wfsID) {this.wfsID = wfsID;}
    
    /**
     * Setting the submission descriptor of the workflow instance
     * @param workflowText instance name
     */
    public void setWorkflowText(String workflowText) {this.workflowText = workflowText;}
    
    /**
     * Getting the submission descriptor of the workflow instance
     * @return instance name
     */
    public String getWorkflowText() {return workflowText;}

    /**
     * Returns the parent, template workflow ID
     * @return parent workflow/template name
     */
    public String getParentWorkflowID() {return parentWorkflowID;}
    
    /**
     * Setting the parent, template workflow ID
     * @param parentWorkflowID parent workflow/template name
     */
    public void setParentWorkflowID(String parentWorkflowID) {this.parentWorkflowID = parentWorkflowID;}
    
    /**
     * Proxy query
     * @return proxy
     */
    public String getProxyValue() {return proxyValue;}
    
    /**
     * Proxy settings
     * @param proxyValue  proxy
     */
    public void setProxyValue(String proxyValue) {this.proxyValue = proxyValue;}
    
    /**
     * Getting the grid/VO responsible for running
     * @return grid/VO name
     */
    public String getGrid() {return grid;}
    
    /**
     * Setting the grid/VO responsible for running
     * @param grid  grid/VO name
     */
    public void setGrid(String grid) {this.grid = grid;}
    
}

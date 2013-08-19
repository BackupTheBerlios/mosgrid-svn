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
 * WorkflowRuntimeBean.java
 * Workflow runtime descriptor bean
 */
package hu.sztaki.lpds.wfi.com;

import java.util.Vector;


/**
 * @author krisztian
 */

public class WorkflowRuntimeBean
{
    private String portalID="";
    private String storageID="";
    private String wfsID="";
    private String userID="";
    private String workflowID="";
    private String instanceText="";
    private String runtimeID="";
    private String workflowType="";
    private Vector errorJobList = new Vector();

/**
 * Class constructor
 */    
    public WorkflowRuntimeBean(){}

/**
 * Class constructor
 *
 * (used in case of workflow submit)...
 *
 * @param pPortalID Portal URL
 * @param pStorageID Storage URL
 * @param pWfsID WFS URL
 * @param pUserID Portal user
 * @param pWorkflowID Workflow name
 * @param pInstanceText Workflow instance descriptor
 */    
    public WorkflowRuntimeBean(String pPortalID,String pStorageID, String pWfsID, String pUserID,String pWorkflowID, String pInstanceText, String pWorkflowType) 
    {
        portalID=pPortalID;
        storageID=pStorageID;
        wfsID=pWfsID;
        userID=pUserID;
        workflowID=pWorkflowID;
        instanceText=pInstanceText;
        workflowType=pWorkflowType;
    }

/**
 * Class constructor
 * 
 * (used in case of workflow rescue)...
 *
 * @param pPortalID Portal URL
 * @param pStorageID Storage URL
 * @param pWfsID WFS URL
 * @param pUserID Portal user
 * @param pWorkflowID Workflow name
 * @param pInstanceText Workflow instance descriptor
 * @param pWrtid Workflow instance ID for the rescue
 * @param pErrorJobList Vector containing the instances of the failed jobs
 */    
    public WorkflowRuntimeBean(String pPortalID, String pStorageID, String pWfsID, String pUserID, String pWorkflowID, String pInstanceText, String pWrtid, String pWorkflowType, Vector pErrorJobList)
    {
        portalID=pPortalID;
        storageID=pStorageID;
        wfsID=pWfsID;
        userID=pUserID;
        workflowID=pWorkflowID;
        instanceText=pInstanceText;
        runtimeID=pWrtid;
        workflowType=pWorkflowType;
        errorJobList=pErrorJobList;
    }

/**
 * Query of the workflow types
 * @return workflow type
 */    
    public String getWorkflowType(){return workflowType;}
    
/**
 * Query of the portal URL
 * @return Portal URL
 * @see String
 */    
    public String getPortalID(){return portalID;}
 
/**
 * Query of the storage URL
 * @return Storage URL
 * @see String
 */    
    public String getStorageID(){return storageID;}

/**
 * Query of the portal user
 * @return Portal user
 * @see String
 */    
    public String getUserID(){return userID;}

/**
 * Query of the workflow name
 * @return Workflow name
 * @see String
 */    
    public String getWorkflowID(){return workflowID;}
    
/**
 * Query of the WFS URL
 * @return WFS URL
 * @see String
 */    
    public String getWfsID(){return wfsID;}
    
/**
 * Query of the workflow instance descriptor
 * @return Instance descriptor
 * @see String
 */    
    public String getInstanceText(){return instanceText;}
    
/**
 * Workflow instance ID
 * @return Instance internal ID
 * @see String
 */
    public String getRuntimeID(){return runtimeID;}

/**
 * Returns the list of failed jobs
 * @return Vector containing the instances of the failed jobs
 * @see Vector
 */
    public Vector getErrorJobList(){return errorJobList;}
    
/**
 * Setting the workflow type
 * @param pvalue workflow type
 */    
    public void setWorkflowType(String pValue){workflowType=pValue;}
    
/**
 * Setting the portal URL
 * @param value Portal URL
 * @see String
 */    
    public void setPortalID(String value){portalID=value;}
    
/**
 * Setting the storage URL
 * @param value Storage URL
 * @see String
 */    
    public void setStorageID(String value){storageID=value;}
    
/**
 * Setting the portal user
 * @param value Portal user
 * @see String
 */    
    public void setUserID(String value){userID=value;}
    
/**
 * Setting the workflow name
 * @param value Workflow name
 * @see String
 */    
    public void setWorkflowID(String value){workflowID=value;}
    
/**
 * Setting the WFS URL
 * @param value WFS URL
 * @see String
 */    
    public void setWfsID(String value){wfsID=value;}
    
/**
 * Setting the description of the workflow instance
 * @param value Workflow instance description
 * @see String
 */    
    public void setInstanceText(String value){instanceText=value;}

/**
 * Setting the ID of the workflow instance
 * @param value Workflow instance ID
 * @see String
 */    
    public void setRuntimeID(String value){runtimeID=value;}

/**
 * Sets the list of failed jobs
 * @param Vector containing the instances of the failed jobs
 * @see Vector
 */
    public void setErrorJobList(Vector value){errorJobList=value;}

}

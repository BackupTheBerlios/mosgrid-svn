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
 * ResourceCollectionBean.java
 * @deprected
 * portál,workflow,user,job Bean
 */

package hu.sztaki.lpds.wfs.com;

/**
 * @author krisztian
 */
public class ResourceCollectionBean 
{
    private String portalID;
    private String userID;
    private String workflowID;
    private String jobsid;
    
/**
 * Class constructor
 */    
    public ResourceCollectionBean() {}
    
/**
 * Class constructor
 * @param pPortalID Port_l URL
 * @param pUserID Port_l user n_v
 * @param pWorkflowID  Workflow neve
 * @param pJobsID Job neve
 */    
    public ResourceCollectionBean(String pPortalID,String pUserID, String pWorkflowID, String pJobsID)
    {
        portalID=pPortalID;
        userID=pUserID;
        workflowID=pWorkflowID;
        jobsid=pJobsID;
    }
 /**
  * Visszaadja a port_l azonos_t_j_t
  * @return A port_l azonos_t_ja
  */   
    public String getPortalID(){return portalID;}
    
 /**
  * Visszaadja a felhaszn_l_ azonos_t_j_t
  * @return A felhaszn_l_ azonos_t_ja
  */   
    public String getUserID(){return userID;}
    
 /**
  * Visszaadja a workflow azonos_t_j_t
  * @return A workflow azonos_t_ja
  */   
    public String getWorkflowID(){return workflowID;}

 /**
  * Visszaadja a Job azonos_t_j_t
  * @return A job azonos_t_ja
  */   
    public String getJobsid(){return jobsid;}
    
/**
 * Be_ll_tja a port_l azonos_t_j_t
 * @param value A port_l azonos_t_ja
 */    
    public void setPortalID(String value){portalID=value;}
    
/**
 * Be_ll_tja a felhaszn_l_ azonos_t_j_t
 * @param value A felhaszn_l_ azonos_t_ja
 */    
    public void setUserID(String value){userID=value;}

/**
 * Be_ll_tja a workflow azonos_t_j_t
 * @param value A workflow azonos_t_ja
 */    
    public void setWorkflowID(String value){workflowID=value;}
 
/**
 * Be_ll_tja a job azonos_t_j_t
 * @param value A job azonos_t_ja
 */    
    public void setJobsid(String value){jobsid=value;}
    
    
}

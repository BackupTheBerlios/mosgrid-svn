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

package hu.sztaki.lpds.pgportal.services.asm;

import hu.sztaki.lpds.pgportal.services.asm.beans.WorkflowInstanceStatusBean;
import hu.sztaki.lpds.pgportal.services.asm.beans.JobStatisticsBean;
import java.util.Hashtable;


/**
 * Class to store and manage workflows that generated from a specified published application
 * @author  akos balasko MTA SZTAKI
 */
 public class ASMWorkflow implements Comparable<ASMWorkflow>{

	private final String userID;
    private String workflowID = "";
    private String workflowName = "";
    private WorkflowInstanceStatusBean statusbean = null;
    private JobStatisticsBean statisticsBean = null;
     //WorkflowData workflow = null;

     /** Stores jobs identified by jobNames as jobIDs*/
    private Hashtable<String,ASMJob> jobs = null; // jobid / portid / filename
   /**
    * Gets jobs identified by jobIDs
    * @return hashtable that contains jobs
    */
    public Hashtable<String,ASMJob> getJobs() {
        return jobs;
    }
    /**
     * Sets jobs
     * @param jobs
     */
    public void setJobs(Hashtable<String, ASMJob> jobs) {
        this.jobs = jobs;
    }
    
    private Hashtable<String,String> additionalInfo = null;
    /**
     * Gets additional informations
     * @return information
     */
    public Hashtable<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
    /**
     * Sets additional information
     * @param additionalInfo
     */
    public void setAdditionalInfo(Hashtable<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    /**
     * Gets
     * @return
     */
    public JobStatisticsBean getStatisticsBean() {
        return statisticsBean;
    }

    public void setStatisticsBean(JobStatisticsBean statisticsBean) {
        this.statisticsBean = statisticsBean;
    }
     String workflow_instanceId = "";
     
    
     
    public String getWorkflowID() {
        return workflowID;
    }

    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
    }

   

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    

    public String getWorkflow_instanceId() {
        
        return workflow_instanceId;

    }


    public void setWorkflow_instanceId(String workflow_instanceId) {
        this.workflow_instanceId = workflow_instanceId;
    }

    public void setStatusbean(WorkflowInstanceStatusBean statusbean) {
        this.statusbean = statusbean;
    }

    public WorkflowInstanceStatusBean getStatusbean() {
        return statusbean;
    }
   /*
    public void setWorkflow(WorkflowData workflow) {
        this.workflow = workflow;
    }

    public WorkflowData getWorkflow() {
        return workflow;
    }
    */
    
     public ASMWorkflow(final String userID){
         //jobs  = convertWorkflowData2Jobs(data,userID);
    	 this.userID = userID;
         statusbean = new WorkflowInstanceStatusBean();
         jobs = new   Hashtable<String,ASMJob>();
         statisticsBean = new JobStatisticsBean();
         additionalInfo = new Hashtable<String,String>();
         /*Enumeration hashKeys = data.getAllRuntimeInstance().keys();
            // Application Specific Workflows contain one Instance only
         if (hashKeys.hasMoreElements()){
            String key = (String) hashKeys.nextElement();
            System.out.println("key : " + key);
            this.workflow_instanceId = key;
         }
          *
          */

     }
     
     public String getUserID() {
    	 return userID;
     }

    public int compareTo(ASMWorkflow t) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
/*
    private  Hashtable<String,ArrayList<String>> convertWorkflowData2Jobs(WorkflowData data,String userID) {
        workflowName = data.getWorkflowID();
         Hashtable<String,ArrayList<String>> return_list = new  Hashtable<String,ArrayList<String>>();

        Iterator jobiter = data.getJobs(userID).keySet().iterator();
        while (jobiter.hasNext()){
            String jobid = (String)jobiter.next();
            JobData job = (JobData)data.getJobs(userID).get(jobid);
            
             ArrayList<String> port_values = new ArrayList<String>();
            for (int i=0;i<job.getPorts().size();++i)
            {

                PortBean port = (PortBean)job.getPorts().get(i);

                String portId = port.getId();
                String portnumber = port.getSeq();

                String porttype = port.getType();
                System.out.println("portid is : " + portId + " portseq is : " + portnumber + " porttype is : " + porttype + " port id : " + port.getId() + " port name : " + port.getName() + " port internal name : " + port.getInternalName() + " port externakl name : " + port.getExternalName());
                if (porttype.equals("input"))
                    port_values.add(portnumber);

                        //just the inputs
            }
            return_list.put(jobid, port_values);
        }
          return return_list;
    }
*/
 }
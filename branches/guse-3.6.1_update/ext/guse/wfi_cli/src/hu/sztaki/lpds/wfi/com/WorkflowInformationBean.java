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
 * Describer bean of the status and jobs of the workflow
 */

package hu.sztaki.lpds.wfi.com;

import java.util.HashMap;

/**
 * @author krisztian
 */
public class WorkflowInformationBean 
{
    private String portalid="",userid="", workflowid="",instancename="",runtimeid="";
    private int status; //workflow status
    private long jobNumbers=0; //workflow jobszama
    private HashMap statuses=new HashMap(); //osszegzett jobstatuszok
/**
 * Constructor
 */
    public WorkflowInformationBean() {}

/**
 * Query of the workflow instance ID (automatically generated)
 * @return instance ID
 */
    public String getRuntimeid() {return runtimeid;}
/**
 * Setting of the workflow instance ID (automatically generated)
 * @param runtimeid instance ID
 */
    public void setRuntimeid(String runtimeid) {this.runtimeid = runtimeid;}
/**
 * Query of the workflow instance name (can be set by the user)
 * @return instance name
 */
    public String getInstancename() {  return instancename;}
/**
 * Setting of the workflow instance name (can be set by the user)
 * @param instancename instance name
 */
    public void setInstancename(String instancename) { this.instancename = instancename;}
/**
 * Query of the workflow status
 * @return status
 */
    public int getStatus() {return status;}
/**
 * Setting of the workflow status
 * @param  status status
 */
    public void setStatus(int status) {this.status = status;}
/**
 * Query of the number of handled jobs
 * @return number of jobs
 */
    public long getJobNumbers() {return jobNumbers;}
/**
 * Setting of the number of handled jobs
 * @param jobNumbers  number of jobs
 */
    public void setJobNumbers(long jobNumbers) {this.jobNumbers = jobNumbers;}
/**
 * Query of the portal ID
 * @return portal ID
 */
    public String getPortalid() {return portalid;}
/**
 * Setting of the portal ID
 * @param portalid  portal ID
 */
    public void setPortalid(String portalid) {this.portalid = portalid;}
/**
 * Query of the status hash, includes <status ID, number of affected jobs>
 * @return status hash
 */
    public HashMap getStatuses() {return statuses;}
/**
 * Setting of the status hash, includes <status ID, number of affected jobs>
 * @param statuses  status hash
 */
    public void setStatuses(HashMap statuses) {this.statuses = statuses;}
/**
 * Query of the user ID
 * @return user ID
 */
    public String getUserid() {return userid;}
/**
 * Setting of the user ID
 * @param userid  userID
 */
    public void setUserid(String userid) {this.userid = userid;}
/**
 * Query of the workflow names
 * @return workflow name
 */
    public String getWorkflowid() {return workflowid;}
/**
 * Setting of the workflow name
 * @param workflowid workflow name
 */
    public void setWorkflowid(String workflowid) {this.workflowid = workflowid;}
/**
 * Adding new job status info to the status hash
 * @param pStatus status ID
 * @param pCount number of affected jobs
 */
    public void addJobStatusInfo(long pStatus,long pCount)
    {
        Long tmp=(Long) statuses.get(pStatus);
        if(tmp==null)
            statuses.put(pStatus, pCount);
        else
        {
            tmp=tmp.longValue()+pCount;
            statuses.put(pStatus, tmp);
        }
    }
}

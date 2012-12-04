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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.wfs.service.angie;

import java.util.Hashtable;

/**
 * @author krisztian
 */
public class JobDataCacheBean 
{
    private String workflowid="";
    private Hashtable<String,String> jobsID= new Hashtable<String,String>(); //jobname,jobid
    private Hashtable<String,Hashtable<String,String>> jobsDescription= new Hashtable<String,Hashtable<String,String>>();
    //runtimeid<jobname,description>
    
    public String getJobID(String pJob) throws NullPointerException
    {
        if(jobsID.get(pJob)==null) throw new NullPointerException("Job not exist");
        return jobsID.get(pJob);
    }

    public void setJobID(String pJob, String pID) {this.jobsID.put(pJob, pID);}

    public String getWorkflowid() {return workflowid;}

    public void setWorkflowid(String workflowid) {this.workflowid = workflowid;}
    
    public void addJobDescription(String pWRID,String pJobId,String pDesc)
    {
        if(jobsDescription.get(pWRID)==null) jobsDescription.put(pWRID,new Hashtable<String,String>());
        jobsDescription.get(pWRID).put(pJobId, pDesc);
    }
    
    public String getJobDescription(String pWRID,String pJobId) throws NullPointerException
    {
        if(jobsDescription.get(pWRID)!=null) 
            if(jobsDescription.get(pWRID).get(pJobId)!=null) 
                return jobsDescription.get(pWRID).get(pJobId);
        throw new NullPointerException("not valid descriptuion");
    }
    
    public void deleteInstanceDesciptions(String pWRID){jobsDescription.remove(pWRID);}
    
    public void deleteAllInstanceDesciptions(){jobsDescription= new Hashtable<String,Hashtable<String,String>>();}
    
}

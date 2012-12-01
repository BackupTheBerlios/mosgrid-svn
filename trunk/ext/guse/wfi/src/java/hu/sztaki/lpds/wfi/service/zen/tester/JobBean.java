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
 * Job leiro
 */

package hu.sztaki.lpds.wfi.service.zen.tester;

import java.util.Date;
import java.util.Vector;

/**
 *
 * @author krisztian karoczkai
 */
public class JobBean 
{

    private String JobID, pid;
    private Vector<String> submitTime=new Vector<String>();
    private Vector<StatusBean> status=new Vector<StatusBean>();

    public JobBean(String JobID, String pid) {
        this.JobID = JobID;
        this.pid = pid;
    }

    
    
    public String getJobID() {
        return JobID;
    }

    public void setJobID(String JobID) {
        this.JobID = JobID;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Vector<StatusBean> getStatus() {
        return status;
    }

    public void setStatus(Vector<StatusBean> status) {
        this.status = status;
    }
    public void addStatus(StatusBean status) {
        this.status.add(status);
    }

    public Vector<String> getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Vector<String> submitTime) {
        this.submitTime = submitTime;
    }
    
    public void addSubmitTime(String submitTime) 
    {

        this.submitTime.add(submitTime);
    }

    @Override
    public String toString() 
    {
        StringBuffer res=new StringBuffer();
        res.append("<job id=\""+JobID+"\" pid=\""+pid+"\">\n");
        for(String tmp:submitTime) res.append("\t<submit time=\""+(new Date((long)Float.parseFloat(tmp)))+"\" />\n ");
        for(StatusBean tmp:status) res.append("\t<status status=\""+tmp.getStatus()+"\" time=\""+(new Date((long)Float.parseFloat(tmp.getTime())))+"\" />\n ");
        res.append("</job>\n");
        return res.toString();
    }
    
    
    
}

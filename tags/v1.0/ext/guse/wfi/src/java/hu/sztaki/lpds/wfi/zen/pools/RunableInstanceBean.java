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
 * egy futtathato job peldany leiroja a submit poolban
 */

package hu.sztaki.lpds.wfi.zen.pools;

import hu.sztaki.lpds.wfi.service.zen.Base;
import hu.sztaki.lpds.wfi.service.zen.Runner;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Job;

/**
 *
 * @author krisztian karoczkai
 */
public class RunableInstanceBean{
    private String jobName;
    private long pid;
    private String wfID;

    public RunableInstanceBean() {}

    public RunableInstanceBean(String wf,String job, long pid) {
        this.jobName = job;
        this.pid = pid;
        this.wfID=wf;
    }

    public Job getJob() throws NullPointerException{return Base.getZenRunner(wfID).getJob(jobName);}

    public String getJobName(){return jobName;}
    public void setJobName(String job) {this.jobName = job;}

    public long getPid() {return pid;}

    public void setPid(long pid) {this.pid = pid;}

    public Runner getWf() {return Base.getZenRunner(wfID);}
    public String getWfID() {return wfID;}

    public void setWf(String wf) {this.wfID = wf;}


    
}

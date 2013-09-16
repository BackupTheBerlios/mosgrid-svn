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

package hu.sztaki.lpds.pgportal.services.asm.beans;

import java.util.ArrayList;

/**
 * Class to store informations about running workflow instance
 * @author akos
 */
public class WorkflowInstanceBean {
    /** ArrayList to store actual running job instances*/
    ArrayList<RunningJobDetailsBean> jobs;
    

    
    /**
     * Gets running job instances
     * @return list of job instances see @RunningJobDetailsBean
     */
    public ArrayList<RunningJobDetailsBean> getJobs() {
        return jobs;
    }
    /**
     * Sets job instance list
     * @param jobs
     */
    public void setJobs(ArrayList<RunningJobDetailsBean> jobs) {
        this.jobs = jobs;
    }
    /**
     * Default constructor
     */
    public WorkflowInstanceBean() {
        jobs = new ArrayList<RunningJobDetailsBean>();
    }
    /**
     * Constructor
     * @param jobs
     */
    public WorkflowInstanceBean(ArrayList<RunningJobDetailsBean> jobs) {
        this.jobs = jobs;
    }


}

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

import hu.sztaki.lpds.pgportal.services.asm.ASMJob;
import java.util.ArrayList;

/**
 * Class to show detailed running(!) statuses statistics etc.
 * @author akos
 */
public class RunningJobDetailsBean {
    private ArrayList<ASMJobInstanceBean> instances;
    private String name;
    

    /*private ArrayList<OverviewStatusBean> overviewedstatuses;

    public ArrayList<OverviewStatusBean> getOverviewedstatuses() {
        return overviewedstatuses;
    }

    public void setOverviewedstatuses(ArrayList<OverviewStatusBean> overviewedstatuses) {
        this.overviewedstatuses = overviewedstatuses;
    }
*/
    private JobStatisticsBean statisticsBean;


    private String instanceNumber;

    public RunningJobDetailsBean() {
        statisticsBean = new JobStatisticsBean();
        instances = new ArrayList<ASMJobInstanceBean>();
//        overviewedstatuses = new ArrayList<OverviewStatusBean>();
    }

     public JobStatisticsBean getStatisticsBean() {
        return statisticsBean;
    }

    public void setStatisticsBean(JobStatisticsBean statisticsBean) {
        this.statisticsBean = statisticsBean;
    }
    
    public String getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(String instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public ArrayList<ASMJobInstanceBean> getInstances() {
        return instances;
    }

    public void setInstances(ArrayList<ASMJobInstanceBean> instances) {
        this.instances = instances;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

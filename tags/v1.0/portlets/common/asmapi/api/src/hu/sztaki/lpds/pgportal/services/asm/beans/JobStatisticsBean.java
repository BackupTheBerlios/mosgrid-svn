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

import hu.sztaki.lpds.pgportal.services.asm.constants.StatusConstants;
import java.util.ArrayList;

/**
 * Class to store and get overall job statuses in a readable way
 * @author akos
 */
public class JobStatisticsBean {

    /* This list contains statuses and number of job instances in there*/
    private ArrayList<OverviewJobStatusBean> overviewedstatuses;
    /**
     * Getter function
     * @return list of OverviewJobStatusBean
     */
    public ArrayList<OverviewJobStatusBean> getOverviewedstatuses() {
        return overviewedstatuses;
    }
    /**
     * Setter function
     * @param overviewedstatuses
     */
    public void setOverviewedstatuses(ArrayList<OverviewJobStatusBean> overviewedstatuses) {
        this.overviewedstatuses = overviewedstatuses;
    }

    /**
     * Sets the number of the error job instances
     * @param numberofjob instances
     */
    public void setErrorJobs(long numberofjobs){
        for (int i=0;i<overviewedstatuses.size();++i){
            if (overviewedstatuses.get(i).getStatuscode().equals( StatusConstants.ERROR))
                overviewedstatuses.get(i).setNumberofinstances(Long.toString(numberofjobs));
        }
    }
    /**
     * Gets the number of the error job instances
     * @return number
     */
    public long getErrorJobs() {
        for (OverviewJobStatusBean b : overviewedstatuses){
            if (b.getStatuscode().equals(StatusConstants.ERROR))
                return Long.parseLong(b.getNumberofinstances());
        }
        return 0;
    }
    /**
     * Sets the number of the running job instances
     * @param number of job instances
     */
    public void setRunningJobs(long numberofjobs){
        for (int i=0;i<overviewedstatuses.size();++i){
            if (overviewedstatuses.get(i).getStatuscode().equals(StatusConstants.RUNNING))
                overviewedstatuses.get(i).setNumberofinstances(Long.toString(numberofjobs));
        }
    }
    /**
     * Gets the number of the running job instances
     * @return number
     */
    public long getRunningJobs() {
            for (OverviewJobStatusBean b : overviewedstatuses){
            if (b.getStatuscode().equals(StatusConstants.RUNNING))
                return Long.parseLong(b.getNumberofinstances());
        }
        return 0;
    }
    /**
     * Sets the number of the error job instances
     * @param number of job instances
     */
public void setSubmittedJobs(long numberofjobs){
        for (int i=0;i<overviewedstatuses.size();++i){
            if (overviewedstatuses.get(i).getStatuscode().equals(StatusConstants.SUBMITTED))
                overviewedstatuses.get(i).setNumberofinstances(Long.toString(numberofjobs));
        }
    }
/**
 * Gets the number of the error job instances
 * @return number
 */
    public long getSubmittedJobs() {
           for (OverviewJobStatusBean b : overviewedstatuses){
            if (b.getStatuscode().equals(StatusConstants.SUBMITTED))
                return Long.parseLong(b.getNumberofinstances());
        }
        return 0;
    }
    /**
     * Sets the number of the error job instances
     * @param number of job instances
     */
    public void setFinishedJobs(long numberofjobs){
        for (int i=0;i<overviewedstatuses.size();++i){
            if (overviewedstatuses.get(i).getStatuscode().equals(StatusConstants.FINISHED))
                overviewedstatuses.get(i).setNumberofinstances(Long.toString(numberofjobs));
        }
    }
    /**
     * Gets the number of the error job instances
     * @return number
     */
    public long getFinishedJobs() {
           for (OverviewJobStatusBean b : overviewedstatuses){
            if (b.getStatuscode().equals(StatusConstants.FINISHED))
                return Long.parseLong(b.getNumberofinstances());
        }
        return 0;
    }
/*
    private long finishedJobs=0;
    private long estimatedJobs=0;
    private long errorJobs=0;
*/
   
/**
 * Default constructor
 */
    public JobStatisticsBean() {
        overviewedstatuses = new ArrayList<OverviewJobStatusBean>();
    }

}
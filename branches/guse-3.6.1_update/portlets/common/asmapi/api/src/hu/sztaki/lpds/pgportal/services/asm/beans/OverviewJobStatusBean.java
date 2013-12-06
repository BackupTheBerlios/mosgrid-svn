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

/**
 * @deprecated 
 * Class to store overall status informations for a job (how many job instances are in a given status)
 * This class is replaced by some specific variables in JobStatisticsBean
 * @author akos
 */
public class OverviewJobStatusBean {


    private String statuscode="";
    private String numberofinstances="";

    public String getNumberofinstances() {
        return numberofinstances;
    }

    public void setNumberofinstances(String numberofinstances) {
        this.numberofinstances = numberofinstances;
    }


    public String getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(String statuscode) {
        this.statuscode = statuscode;
    }

    public OverviewJobStatusBean() {

    }

    public OverviewJobStatusBean(String statuscode, String numberofinstances) {
        this.statuscode = statuscode;
        this.numberofinstances = numberofinstances;
    }

}

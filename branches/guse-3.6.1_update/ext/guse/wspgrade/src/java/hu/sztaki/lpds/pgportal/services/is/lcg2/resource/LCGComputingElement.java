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
 * This file is part of P-GRADE Grid Portal.
 *
 * P-GRADE Grid Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * P-GRADE Grid Portal is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * P-GRADE Grid Portal.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2006-2008 MTA SZTAKI
 *
 */
/*
 */

/*
  * LCGComputingElement.java
  *
  * Created on February 8, 2005, 7:14 PM
  */

package hu.sztaki.lpds.pgportal.services.is.lcg2.resource;

import java.util.ArrayList;
import java.util.Collections;

import java.text.DecimalFormat;

/**
  *
  * @author  boci
  */
public class LCGComputingElement {
    // keys
    private String lrmsType;
    private String ceName;
    private String hostName;
    
    private String lrmsVersion;
    private int totalCpu;
    private int freeCpu;
    private int runningJob;
    private int waitingJob;
    private ArrayList voList;
    
//    private boolean isTouched;
    
    /** Creates a new instance of LCGComputingElement */
    public LCGComputingElement() {
         this.init();
    }
    
    public LCGComputingElement(String hostName, String ceName , String lrmsType) {
        this.hostName = hostName;
        this.lrmsType = lrmsType;
        this.ceName = ceName;
        this.init();
    }
    
    private void init(){
//        this.isTouched = false;
        this.lrmsVersion = "";
        this.totalCpu = -999;
        this.freeCpu = -999;
        this.runningJob = -999;
        this.waitingJob = -999;        
        this.voList = new  ArrayList();
    }
    
    public String getLrmsType(){
        return this.lrmsType;
    }
    
    public void setLrmsType(String lrmsType){
        this.lrmsType = lrmsType;
    }

    public String getCeName(){
        return this.ceName;
    }
    
    public void setCeName(String ceName){
        this.ceName = ceName;
    }
    
    public String getHostName(){
        return this.hostName;
    }
    
    public void setHostName(String hostName){
        this.hostName = hostName;
    }
    
    public String getLrmsVersion(){
        return this.lrmsVersion;
    }
    
    public void setLrmsVersion(String lrmsVersion){
        this.lrmsVersion = lrmsVersion;
    }

    public int getTotalCpuInt(){
        return this.totalCpu;
    }

    public String getTotalCpu(){
        if (this.totalCpu == -999) return "N/A";
        else return String.valueOf(this.totalCpu);
    }
    
    public void setTotalCpu(int totalCpu){
        this.totalCpu = totalCpu;
    }

    public int getFreeCpuInt(){
        return this.freeCpu;
    }

    public String getFreeCpu(){
        if (this.freeCpu == -999) return "N/A";
        else return String.valueOf(this.freeCpu);
    }
    
    
    public void setFreeCpu(int freeCpu){
        this.freeCpu = freeCpu;
    }

    public final int getCpuPercent(){
        String p = this.getCpuPercentStr();
        if (p.equals("-")) return -1;
        else {
            try {
                return Integer.valueOf(p).intValue();
            }
            catch (NumberFormatException e) {e.getMessage(); return -1;}
        }
    }
    
    public final String getCpuPercentStr(){
        if (this.totalCpu == -999 || this.freeCpu == -999 || this.totalCpu == 0   ) return "-";
        else {
            DecimalFormat myFormatter = new DecimalFormat("###");
            return myFormatter.format( 100 - ((double)this.freeCpu / (double)this.totalCpu * 100 ) );
        }
    }    
    
    
    public int getRunningJobInt(){
        return this.runningJob;
    }

    public String getRunningJob(){
        if (this.runningJob == -999) return "N/A";
        else return String.valueOf(this.runningJob);
    }
    
    
    public void setRunningJob(int runningJob ){
        this.runningJob = runningJob;
    }

    public int getWaitingJobInt(){
        return this.waitingJob;
    }

    public String getWaitingJob(){
        if (this.waitingJob == -999) return "N/A";
        else return String.valueOf(this.waitingJob);
    }
    
    public void setWaitingJob(int waitingJob ){
        this.waitingJob = waitingJob;
    }
    
    public final int getJobPercent(){
        String p = this.getJobPercentStr();
        if (p.equals("-")) return -1;
        else {
            try {
                return Integer.valueOf(p).intValue();
            }
            catch (NumberFormatException e) {e.getMessage(); return -1;}
        }
    }
    
    public final String getJobPercentStr(){
        if (this.waitingJob == -999 || this.runningJob == -999 ) return "-";
        int totalJob = this.waitingJob + this.runningJob;
        if (totalJob == 0) return "0";
        else {
            DecimalFormat myFormatter = new DecimalFormat("###");
            return myFormatter.format( (double)this.waitingJob / (double)totalJob * 100);
        }
    }    
    
    public void addVO(String voName){
//        if ( !this.voList.contains(voName) ) {
            this.voList.add(voName);
//        }
    }
/*    
    public String[] getVOList(){
        String[] list = new String[this.voList.size()];
        for (int i = 0; i<this.voList.size(); i++) {
            list[i] = (String)this.voList.get(i);
        }
        return list;
    }
  */  
    public String getSerializedVOList(){
        String list;
// HG changed 2008.02.04
        list ="";        
        if (voList != null)
        {    
           if(voList.size()!= 0)    
           {
// HG change end       
   
                list = (String)this.voList.get(0);
                for (int i = 1; i<this.voList.size(); i++) {
                      list += ", " + (String)this.voList.get(i);
                }
// HG change 20008.02.04
             }
         }   
// HG change end        
        return list;
    }
    
    public boolean isSupportVO(String voName){
        for (int i = 0; i<this.voList.size(); i++) {
            if ( voName.equals( (String)this.voList.get(i) ) ) { return true;   }
        }
        return false;
        
    }
    
/*    
    public void clearVOList(){
        this.voList.clear();
    }
    
    public boolean getIsTouched(){
        return this.isTouched;
    }
    
    public void setIsTouched(boolean value){
        this.isTouched = value;
    }
 */
}

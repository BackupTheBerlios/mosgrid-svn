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


package hu.sztaki.lpds.pgportal.services.is.lcg2.resource;

import java.util.ArrayList;

/**
  *
  * @author  Tamas Boczko
  */
public class LCGSubCluster {
    // key:
    private String subClusterName;
    
    private String osName;
    private String osRelease;
    private String cpuModel;
    private String cpuSpeed;
    private String ramSize;
    private String virtualSize;
    
    private ArrayList softwareEnvList;
    
    //private boolean isTouched;
    
    /** Creates a new instance of LCGSubCluster */
    public LCGSubCluster(String subClusterName) {
        this.subClusterName = subClusterName;
//        this.isTouched = false;
        this.osName = "N/A";
        this.osRelease = "N/A";
        this.cpuModel = "N/A";
        this.cpuSpeed = "N/A";
        this.ramSize = "N/A";
        this.virtualSize = "N/A";
        this.softwareEnvList = new ArrayList();
        
    }
    
    public String getSubClusterName(){
        return this.subClusterName;
    }
    
    public String getOsName(){
       return this.osName;
    }
    
    public void setOsName(String value){
        this.osName = value;
    }
    
    public String getOsRelease(){
        return this.osRelease;
    }
    
    public void setOsRelease(String value){
        this.osRelease = value;
    }
    
    public String getCpuModel(){
        return this.cpuModel;
    }
    
    public void setCpuModel(String value){
        this.cpuModel = value;
    }
    
    public String getCpuSpeed(){
        return this.cpuSpeed;
    }
    
    public void setCpuSpeed(String value){
        this.cpuSpeed = value;
    }
    
    public String getRamSize(){
        return this.ramSize;
    }
    
    public void setRamSize(String value){
        this.ramSize = value;
    }
    
    public String getVirtualSize(){
        return this.virtualSize;
    }

    public void setVirtualSize(String value){
        this.virtualSize = value;
    }
    
    public void addSoftwareEnv(String environment){
//        if ( !this.softwareEnvList.contains(environment) ) {
            this.softwareEnvList.add(environment);
//        }        
    }
    
    public String getSerializedSoftwareEnvList(){
        String list;
        list = (String)this.softwareEnvList.get(0);
        for (int i = 1; i<this.softwareEnvList.size(); i++) {
            list += ", " + (String)this.softwareEnvList.get(i);
        }
        return list;        
    }
/*    
    public void clearSoftwareEnvList(){
        this.softwareEnvList.clear();
    }
    
    public boolean getIsTouched(){
        return this.isTouched;
    }
    
    public void setIsTouched(boolean value){
        this.isTouched = value;
    }    
  */  
}


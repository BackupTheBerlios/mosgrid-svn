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

package hu.sztaki.lpds.pgportal.services.asm.constants;

import java.util.HashMap;

/**
 *
 * @author akos
 */
public class StatusColorConstants {

    public static String INCOMPLETED           = "white";
    public static String INIT                  = "white";
    public static String SUBMITTED             = "orange";
    public static String WAITING               = "lightorange";
    public static String SCHEDULED             = "purple";
    public static String RUNNING               = "red";
    public static String FINISHED              = "lightgreen";
    public static String ERROR                 = "lightblue";
    public static String ABORTED               = "red";
    public static String READY                 = "white";
    public static String CANCELLED             = "red";
    
    public HashMap<String,String> statuscolors = null;

    public HashMap<String, String> getStatuscolors() {
        return statuscolors;
    }

    public void setStatuscolors(HashMap<String, String> statuscolors) {
        this.statuscolors = statuscolors;
    }

    public StatusColorConstants(){
        statuscolors =new HashMap<String,String>();
        
        statuscolors.put("INCOMPLETED","white");
        statuscolors.put("INIT","white");
        statuscolors.put("SUBMITTED","orange");
        statuscolors.put("WAITING","lightorange");
        statuscolors.put("SCHEDULED","purple");
        statuscolors.put("RUNNING","red");
        statuscolors.put("FINISHED","lightgreen");
        statuscolors.put("ERROR","lightblue");
        statuscolors.put("ABORTED","red");
        statuscolors.put("READY","white");
        statuscolors.put("CANCELLED","red");
        
    }
    public String getColor(String status){
        return statuscolors.get(status);
    }
}

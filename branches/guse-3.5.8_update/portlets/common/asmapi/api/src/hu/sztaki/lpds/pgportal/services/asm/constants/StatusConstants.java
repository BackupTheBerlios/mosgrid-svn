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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author akos
 */
public class StatusConstants {

    public static String INCOMPLETED           = "0";
    public static String INIT                  = "1";
    public static String SUBMITTED             = "2";
    public static String WAITING               = "3";
    public static String SCHEDULED             = "4";
    public static String RUNNING               = "5";
    public static String FINISHED              = "6";
    public static String ERROR                 = "7";
    public static String NO_FREE_SERVICE       = "8";
    public static String ABORTED               = "15";
    public static String READY                 = "10";
    public static String CANCELLED             = "11";
    public static String CLEARED               = "12";
    public static String DONE_FAILED           = "9";
    public static String PENDING               = "13";
    public static String ACTIVE                = "14";
    public static String SUSPENDED             = "16";
    public static String UNSUBMITTED           = "17";
    public static String STAGE_IN              = "18";
    public static String STAGE_OUT             = "19";
    public static String UNKNOWN               = "20";
    public static String TERM_IS_FALSE         = "21";
    public static String WORKFLOW_SUSPENDED    = "22";
    public static String WORKFLOW_SUSPENDING   = "28";
    public static String WORKFLOW_RESUMING     = "29";
    public static String RUNNING_ERROR         = "23";
    public static String NO_INPUT              = "25";
    public static String BIG_WORKFLOW          = "37";
    public static String NOT_RUNNABLE          = "99";

    private final static ConcurrentHashMap<String,String> STATUS_MAP;
    
    static {
    	STATUS_MAP = new ConcurrentHashMap<String, String>();
    	STATUS_MAP.put("0", "INCOMPLETED");
        STATUS_MAP.put("1", "INIT");
        STATUS_MAP.put("2", "SUBMITTED");
        STATUS_MAP.put("3", "WAITING");
        STATUS_MAP.put("4", "SCHEDULED");
        STATUS_MAP.put("5", "RUNNING");
        STATUS_MAP.put("6", "FINISHED");
        STATUS_MAP.put("7", "ERROR");
        STATUS_MAP.put("8", "NO_FREE_SERVICE");
        STATUS_MAP.put("9", "ABORTED");
        STATUS_MAP.put("10", "READY");
        STATUS_MAP.put("11", "CANCELLED");
        STATUS_MAP.put("12", "CLEARED");
        STATUS_MAP.put("13", "DONE_FAILED");
        STATUS_MAP.put("14", "PENDING");
        STATUS_MAP.put("16", "ACTIVE");
        STATUS_MAP.put("17", "SUSPENDED");
        STATUS_MAP.put("18", "UNSUBMITTED");
        STATUS_MAP.put("19", "STAGE_IN");
        STATUS_MAP.put("20", "STAGE_OUT");
        STATUS_MAP.put("21", "UNKNOWN");
        STATUS_MAP.put("22", "TERM_IS_FALSE");
        STATUS_MAP.put("23", "WORKFLOW_SUSPENDED");
        STATUS_MAP.put("25", "NO_INPU");
        STATUS_MAP.put("28", "WORKFLOW_SUSPENDING");
        STATUS_MAP.put("29", "WORKFLOW_RESUMIMG");
        STATUS_MAP.put("37", "BIG_WORKFLOW");
        STATUS_MAP.put("99", "NOT_RUNNABLE");
    }

    public static Map<String, String> getStatuses() {
        return new HashMap(STATUS_MAP);
    }

    public static void setStatuses(HashMap<String, String> statusMap) {
        STATUS_MAP.clear();
        STATUS_MAP.putAll(statusMap);
    }
    
    public static String getStatus(String status){
        return STATUS_MAP.get(status);
    }
}

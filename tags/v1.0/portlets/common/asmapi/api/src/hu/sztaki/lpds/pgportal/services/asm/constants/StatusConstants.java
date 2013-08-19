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

    private static HashMap<String,String> statuses;

    static{
        statuses = new HashMap<String,String>();
        statuses.put("0", "INCOMPLETED");
        statuses.put("1", "INIT");
        statuses.put("2", "SUBMITTED");
        statuses.put("3", "WAITING");
        statuses.put("4", "SCHEDULED");
        statuses.put("5", "RUNNING");
        statuses.put("6", "FINISHED");
        statuses.put("7", "ERROR");
        statuses.put("8", "NO_FREE_SERVICE");
        statuses.put("9", "ABORTED");
        statuses.put("10", "READY");
        statuses.put("11", "CANCELLED");
        statuses.put("12", "CLEARED");
        statuses.put("13", "DONE_FAILED");
        statuses.put("14", "PENDING");
        statuses.put("16", "ACTIVE");
        statuses.put("17", "SUSPENDED");
        statuses.put("18", "UNSUBMITTED");
        statuses.put("19", "STAGE_IN");
        statuses.put("20", "STAGE_OUT");
        statuses.put("21", "UNKNOWN");
        statuses.put("22", "TERM_IS_FALSE");
        statuses.put("23", "WORKFLOW_SUSPENDED");
        statuses.put("25", "NO_INPU");
        statuses.put("28", "WORKFLOW_SUSPENDING");
        statuses.put("29", "WORKFLOW_RESUMIMG");
        statuses.put("37", "BIG_WORKFLOW");
        statuses.put("99", "NOT_RUNNABLE");

    }

//    public HashMap<String, String> getStatuses() {
//        return statuses;
//    }
//
//    public void setStatuses(HashMap<String, String> statuses) {
//        this.statuses = statuses;
//    }
    public static String getStatus(String status){
        return statuses.get(status);
    }
}

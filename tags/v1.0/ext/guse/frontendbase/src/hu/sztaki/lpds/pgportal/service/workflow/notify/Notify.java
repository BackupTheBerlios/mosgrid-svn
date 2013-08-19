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
 * Notify keys. (helper class)
 */

package hu.sztaki.lpds.pgportal.service.workflow.notify;

/**
 * @author lpds
 */
public class Notify {
    
    //*** basic keys ***//
    
    /**
     *  Enable notify
     */
    public static final String enabled = "1";
    /**
     *  Disable notify
     */
    public static final String disabled = "0";
    /**
     * Message
     */
    public static final String message = "message";
    
    // portal30 service.property file keys
    /**
     * Name of the smtp host property key
     */
    public static final String property_smtp_host = "notify.smtp.host";
    /**
     * Sleep time in hour between the frequent quota checks property key
     */
    public static final String property_quota_time = "notify.quota.time.in.hour";
    /**
     * Quota overrun in percentage key
     */
    public static final String property_quota_max_percent = "notify.quota.max.percent";
    
    //*** common keys ***//
    
    
    /**
     * now date time
     */
    public static final String now = "#now#";
    
    /**
     * user ID
     */
    public static final String user = "#user#";
    
    /**
     * portal ID
     */
    public static final String portal = "#portal#";
    
    //*** workflow status change handler keys ***//
    
    
    /**
     * workflow ID
     */
    public static final String workflow = "#workflow#";
    
    /**
     * workflow instance submit text
     */
    public static final String instance = "#instance#";
    
    /**
     * old workflow instance status
     */
    public static final String oldstatus = "#oldstatus#";
    
    /**
     * new workflow instance status
     */
    public static final String newstatus = "#newstatus#";
    
    /**
     * enable wfchg handler
     */
    public static final String wfchg_enab = "wfchg_enab";
    
    /**
     *workflow status change message template
     */
    public static final String wfchg_mess = "wfchg_mess";
    
    //*** storage quota handler keys ***//
    
    // 
    /**
     * enable quota handler
     */
    public static final String quota_enab = "quota_enab";
    
    /**
     * storage quota message template
     */
    public static final String quota_mess = "quota_mess";
    // 
    /**
     * enabled quota size
     */
    public static final String quota_space = "#quota#";
    
    /**
     * used quota size
     */
    public static final String quota_space_used = "#usedquota#";
    
    /**
     * message sending quota in percentage
     */
    public static final String quota_percent_max = "#quotapercentmax#";
    
    /**
     * current quota usage in percentage
     */
    public static final String quota_percent = "#quotapercent#";
    
    //*** email plugin keys ***//
    
    // 
    /**
     * email plugin name
     */
    public static final String plugin_email_name = "email";
    // 
    /**
     * enable email plugin
     */
    public static final String email_enab = "email_enab";
    
    /**
     * notify smtp host
     */
    public static final String email_smtp = "email_smtp";
    
    /**
     * to email address
     */
    public static final String email_addr = "email_addr";
    
    /**
     * email subject
     */
    public static final String email_subj = "email_subj";
    
}

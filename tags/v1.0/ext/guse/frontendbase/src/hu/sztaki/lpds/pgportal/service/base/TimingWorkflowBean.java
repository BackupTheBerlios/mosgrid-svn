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
 * TimingWorkflowBean.java
 */

package hu.sztaki.lpds.pgportal.service.base;

/**
 * Workflow timing descriptor bean class
 *
 * @author krisztian
 */
public class TimingWorkflowBean {
    
    private String userID="";
    
    private String workflowID="";
    
    /**
     * Class constructor
     * @param pUserID
     * @param pWorkflowID
     */
    public TimingWorkflowBean(String pUserID,String pWorkflowID) {
        userID=pUserID;
        workflowID=pWorkflowID;
    }
    
    /**
     * Getting the portal user
     * @return user login name
     */
    public String getUserID(){return userID;}
    
    /**
     * Getting the workflow
     * @return workflow name
     */
    public String getWorkflowID(){return workflowID;}
    
}

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
 * WorkflowConfigErrorBean.java
 *
 * Workflow ellenorzesekor (menteskor) keletkezo hibaka irja le.
 */

package hu.sztaki.lpds.wfs.com;

/**
 * @author lpds
 */
public class WorkflowConfigErrorBean {
    
    private long workflowID;
    
    private String jobName;
    
    private String portID;
    
    private String errorID;

    /**
     * Bean constructor
     */
    public WorkflowConfigErrorBean() {
    }
    
    /**
     * Bean constructor - errorID
     * @param workflowID workflow azonosito
     * @param jobName  job neve
     * @param portID  port azonosito
     * @param errorID error lisata
     */
    public WorkflowConfigErrorBean(long workflowID, String jobName, String portID, String errorID) {
        setWorkflowID(workflowID);
        setJobName(jobName);
        setPortID(portID);
        setErrorID(errorID);
    }
    
    /**
     * A workflow azonositot adja vissza
     * @return workflowID
     */
    public long getWorkflowID() {
        return workflowID;
    }
    
    /**
     * A workflow azonositojat allitja be
     * @param value workflow azonosito
     */
    public void setWorkflowID(long value) {
        this.workflowID = value;
    }
    
    /**
     * A port azonositot adja vissza
     * @return portID
     */
    public String getPortID() {
        return portID;
    }
    
    /**
     * A port azonositojat allitja be
     * @param value port azonosito
     */
    public void setPortID(String value) {
        this.portID = value;
    }
    
    /**
     * A error azonositot adja vissza
     * @return errorID
     */
    public String getErrorID() {
        return errorID;
    }
    
    /**
     * Az error azonositojat allitja be
     * @param value error azonosito
     */
    public void setErrorID(String value) {
        this.errorID = value;
    }

    /**
     * A job nevet adja vissza
     * @return jobName
     */
    public String getJobName() {
        return jobName;
    }
    
    /**
     * A job nevet allitja be
     * @param value jobName
     */
    public void setJobName(String value) {
        this.jobName = value;
    }

}

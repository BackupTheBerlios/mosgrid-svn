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
 * JobStatusData.java
 */

package hu.sztaki.lpds.pgportal.service.base.data;

/**
 * Class containing a status of the job
 *
 * @author krisztian
 */
public class JobStatusData {
    private String name="";  //Name
    private String pid=""; //PID
    private int status=0; //Status
    private String resource=""; //resource
    private long tim=0; //last modify timestamp
    
    /**
     * Creates a new instance of JobStatusData
     */
    public JobStatusData() {}
    
    /**
     * Constructor setting new job status data
     * @param pName user ID
     * @param pResource used resource
     * @param pStatus job status
     * @param pPid job instance parametric ID
     * @param pTim status creation number
     */
    public JobStatusData(String pName, String pPid, int pStatus, String pResource, long pTim) {
        name=pName;
        pid=pPid;
        status=pStatus;
        resource=pResource;
        tim=pTim;
    }
    
    /**
     * Getting the name of the job
     * @return String - job name
     */
    public String getName(){return name;}

    /**
     * Getting the parametric ID of the job
     * @return String - job parametric ID
     */
    public String getPid(){return pid;}
    
    /**
     * Getting the current status of the job
     * @return String - current status of the job
     */
    public int getStatus(){return status;}
    
    /**
     * Getting the resource the job runs on
     * @return String - the resource the job runs on
     */
    public String getResource(){return resource;}

    /**
     * Returns the time of the change to the current status
     * @return String - time of the change to the current status
     */
    public long getTim(){return tim;}

    /**
     * Setting the user name
     * @param name user internal ID
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setting the parametric ID of the job instance
     * @param pid PID
     */
    public void setPid(String pid) {
        this.pid = pid;
    }

    /**
     * Setting the name of the executor resource
     * @param resource resource name
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * Setting the job status ID
     * @param status status ID
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Setting the number of the status change
     * @param tim number
     */
    public void setTim(long tim) {
        this.tim = tim;
    }
    
}

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
 * JobBean.java
 */

package hu.sztaki.lpds.pgportal.service.base.data;

import java.util.Vector;

/**
 * Handles and stores a job's list data
 *
 * @author krisztian
 */
public class JobData 
{
    private String jobID="";
    private String txt="";
    private int x,y;
    private Vector ports=new Vector();
//    private long tim=0;

    /**
     * Constructor for JavaBeans
     */    
    public JobData() {}

    /**
     * Constructor for easier use
     * @param pJobID   job ID
     * @param pTxt  workflow descriptor
     * @param pX   job x coordinate
     * @param pY  job y coordinate
     * @param pPorts port list
     */
    public JobData(String pJobID, String pTxt, int pX, int pY, Vector pPorts)
    {
        jobID=pJobID;
        txt=pTxt;
        x=pX;
        y=pY;
        ports=pPorts;
    }

//    public void setTim(long pValue){tim=pValue;}
    
//    public long getTim(){return tim;}

    /**
     * Returns the JobID
     * @return jobID
     */    
    public String getJobID(){return jobID;}

    /**
     * Returns the text description of the job
     * @return job description
     */
    public String getTxt(){return txt;}
    
    /**
     * Returns the job's x coordinate
     * @return job x coordinate
     */
    public int getX(){return x;}
    
    /**
     * Returns the job's y coordinate
     * @return job y coordinate
     */
    public int getY(){return y;}
/**
 * Getting the ports belong to the job
 * @return port list
 */
    public Vector getPorts() {return ports;}
    
    /**
     * Sets the JobID
     * @param value jobID
     */
    public void setJobID(String value){jobID=value;}
    
    /**
     * Sets the text description of the job
     * @param value job description
     */
    public void setTxt(String value){txt=value;}
    
    /**
     * Sets the job's x coordinate
     * @param value job x coordinate
     */
    public void setX(int value){x=value;}
    
    /**
     * Sets the job's y coordinate
     * @param value job y coordinate
     */
    public void setY(int value){y=value;}
    
}

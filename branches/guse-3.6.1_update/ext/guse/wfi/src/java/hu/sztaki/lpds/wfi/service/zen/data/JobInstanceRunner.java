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
 * JobInstanceRunner.java
 * Parametrikus Job peldany leiro
 */

package hu.sztaki.lpds.wfi.service.zen.data;

import java.util.Hashtable;

public class JobInstanceRunner 
{
    
    private String jobName="";
    private long psNum=0;
    private int status=1;
    

/**
 * Class constructor
 * @param pJobID Job neve
 * @param pPSnum Parametrikus ID
 */    
    public JobInstanceRunner(String pJobID, long pPSnum) 
    {
        jobName=pJobID;
        psNum=pPSnum;
    }
    
/**
 * Job nevenek lekerdezese
 * @return Job neve
 */    
    public String getJobID(){return jobName;}
    
/**
 * Job PID lekerdezese
 * @return Job PID
 */    
    public long getPSID(){return psNum;}
    
/**
 * Job stetusz lekerdezese
 * @return Job statusz
 */    
    public int getStatus(){return status;}
    
/**
 * Job statusz beallitasa
 * @param pValue Job statusz
 */    
    public void setStatus(int pValue){status=pValue;}
}

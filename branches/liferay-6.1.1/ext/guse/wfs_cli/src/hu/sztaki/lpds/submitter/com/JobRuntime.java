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
/**
 * JobRuntime.java
 * Job futtatasi leiro Bean
 */
package hu.sztaki.lpds.submitter.com;

import java.util.*;

/**
 * @author krisztian
 */
public class JobRuntime
{
    private String portalID="";
    private String storageID="";
    private String userID="";
    private String workflowID="";
    private String jobID="";
    private String wfiID="";
    private String wfsID="";
    private String submitHandler="";
    private String typID="";
    private String workflowRuntimeID="";
    private long workflowSubmitID = 0;
    private int PID=0;
    private Hashtable inputsCount=new Hashtable();
    private Hashtable requirements=new Hashtable();
    private Hashtable embedOutputs=new Hashtable();
    private Hashtable params=new Hashtable();

/**
 * Class construktor
 */    
    public JobRuntime(){}

/**
 * Class construktor
 * @param pPortalID A job adatait tatalamazo portal URL-je
 * @param pWfiID A job adatait tatalamazo WFI URL-je
 * @param pStorageID A job adatait tatalamaza storage URL-je
 * @param pWfsID A job adatait tatalamazo WFS URL-je
 * @param pUserID A Jobhoz kapcsolodo portal User login neve
 * @param pWorkflowID A Job-ot tartalmazo Workflow neve
 * @param pJobID A Job neve
 * @param pPID A Job parametrikus ID-ja
 * @param pTypID A Job tipusa
 * @param pInputsCount A Job inputjai(key=port nev string value=JobIOBean)
 * @param pRequirements A Job inputjaihoz kapcsolodo feltetelek
 * @param pEmbedOutputs outputok-beagyazott workflowk eseteben a visszavezetesek is-(value=port nev string key=JobIOBean)
 * @see String
 * @see Hashtable 
 */    
    public JobRuntime(String pPortalID, String pWfiID, String pStorageID, String pWfsID, String pUserID, String pWorkflowID,String pJobID, int pPID, String pTypID, Hashtable pInputsCount, Hashtable pRequirements, Hashtable pEmbedOutputs) 
    {
        portalID=pPortalID;
        storageID=pStorageID;
        wfiID=pWfiID;
        wfsID=pWfsID;
        userID=pUserID;
        workflowID=pWorkflowID;
        jobID=pJobID;
        typID=pTypID;
        inputsCount=pInputsCount;
        requirements=pRequirements;
        PID=pPID;
        embedOutputs=pEmbedOutputs;
    }

/**
 * Class construktor
 * @param pPortalID A job adatait tatalamazo portal URL-je
 * @param pWfiID A job adatait tatalamazo WFI URL-je
 * @param pStorageID A job adatait tatalamaza storage URL-je
 * @param pWfsID A job adatait tatalamazo WFS URL-je
 * @param pUserID A Jobhoz kapcsolodo portal User login neve
 * @param pWorkflowID A Job-ot tartalmazo Workflow neve
 * @param pWorkflowRuntimeID  A Job-ot tartalmazo Workflowpeldany RunTime ID-ja
 * @param pJobID A Job neve
 * @param pPID A Job parametrikus ID-ja
 * @param pTypID A Job tipusa
 * @param pInputsCount A Job inputjai(key=port nev string value=JobIOBean)
 * @param pRequirements A Job inputjaihoz kapcsolodo feltetelek
 * @param pEmbedOutputs outputok-beagyazott workflowk eseteben a visszavezetesek is-(value=port nev string key=JobIOBean)
 * @see String
 * @see Hashtable 
 */    
    public JobRuntime(String pPortalID, String pWfiID, String pStorageID, String pWfsID, String pUserID, String pWorkflowID, String pWorkflowRuntimeID,String pJobID,int pPID, String pTypID, Hashtable pInputsCount, Hashtable pRequirements, Hashtable pEmbedOutputs) 
    {
        portalID=pPortalID;
        storageID=pStorageID;
        wfiID=pWfiID;
        wfsID=pWfsID;
        userID=pUserID;
        workflowID=pWorkflowID;
        jobID=pJobID;
        typID=pTypID;
        requirements=pRequirements;
        inputsCount=pInputsCount;
        PID=pPID;
        workflowRuntimeID=pWorkflowRuntimeID;
        embedOutputs=pEmbedOutputs;
    }

/**
 * Workflow SubmitID lekerdezese
 * @return Workflow SubmitID
 * @see long
 */
    public long getWorkflowSubmitID(){return workflowSubmitID;}

/**
 * Workflow RunTimeID lekerdezese
 * @return Workflow RunTimeID
 * @see String
 */
    public String getWorkflowRuntimeID(){return workflowRuntimeID;}
    
/**
 * Workflow nevenek lekerdezese
 * @return Workflow neve
 * @see String
 */
    public String getWorkflowID(){return workflowID;}
    
/**
 * User nevenek lekerdezese
 * @return User neve
 * @see String
 */
    public String getUserID(){return userID;}
    
/**
 * Portal URL lekerdezese
 * @return Portal URL
 * @see String
 */
    public String getPortalID(){return portalID;}
    
/**
 * Job nevenek lekerdezese
 * @return Job neve
 * @see String
 */
    public String getJobID(){return jobID;}
    
/**
 * Storage URL lekerdezese
 * @return Storage URL
 * @see String
 */
    public String getStorageID(){return storageID;}
    
/**
 * WFS URL lekerdezese
 * @return  WFS URL
 * @see String
 */
    public String getWfsID(){return wfsID;}
    
/**
 * WFI URL lekerdezese
 * @return WFI URL
 * @see String
 */
    public String getWfiID(){return wfiID;}
    
/**
 * Job tipus lekerdezese
 * @return Job tipus
 * @see String
 */
    public String getTypID(){return typID;}
    
/**
 * Parametrikus ID lekerdezese
 * @return Parametrikus ID
 */
    public int getPID(){return PID;}
    
/**
 * Job inputjai
 * @return Input lista (key=port nev string value=PID)
 * @see Hashtable
 */
    public Hashtable getInputsCount(){return inputsCount;}    
    
/**
 * Job inputjaihoz tartozo feltetelek
 * @deprected
 * @return feltetel lista, kulcs az internal file name, ertek a feltetel string
 * @see Hashtable
 */
    public Hashtable getRequirements(){return requirements;}    
    
/**
 * Outputok lista lekerdezese
 * @return (value=port nev string key=JobIOBean)
 * @see Hashtable
 */
    public Hashtable getEmbedOutputs(){return embedOutputs;}    

/**
 * Parameterek lekerdezese
 * @return Parametrek
 * @see Hashtable
 */
    public Hashtable getParams(){return params;}    
    
/**
 * Workflow RunTimeID beallitasa
 * @param value Workflow RunTimeID
 * @see String
 */
    public void setWorkflowRuntimeID(String value){workflowRuntimeID=value;}

/**
 * Workflow SubmitID beallitasa
 * @param value Workflow SubmitID
 * @see String
 */
    public void setWorkflowSubmitID(long value){workflowSubmitID=value;}
    
/**
 * Workflow nevenek beallitasa
 * @param value Workflow neve
 * @see String
 */
    public void setWorkflowID(String value){workflowID=value;}
    
/**
 * User nevenek Beallitasa
 * @param value User neve
 * @see String
 */
    public void setUserID(String value){userID=value;}
    
/**
 * Portal URL Beallitasa
 * @param value Portal URL
 * @see String
 */
    public void setPortalID(String value){portalID=value;}
    
/**
 * Job nevenek beallitasa
 * @param value Job neve
 * @see String
 */    
    public void setJobID(String value){jobID=value;}
    
/**
 * Storage URL beallitasa
 * @param value Storage URL
 * @see String
 */    
    public void setStorageID(String value){storageID=value;}
    
/**
 * WFS URL beallitasa
 * @param value WFS URL
 * @see String
 */
    public void setWfsID(String value){wfsID=value;}
    
/**
 * WFI URL beallitasa
 * @param value WFI URL
 * @see String
 */
    public void setWfiID(String value){wfiID=value;}
    
/**
 * Job tipusanak beallitasa
 * @param value Job tipus
 * @see String
 */
    public void setTypID(String value){typID=value;}
    
/**
 * Parametrikus ID Beallitasa
 * @param value Parametrikus ID
 */
    public void setPID(int value){PID=value;}
    
/**
 * Job inputjainak megadasa
 * @param value Input lista, (key=port nev string value=JobIOBean)
 * @see Hashtable
 */
    public void setInputsCount(Hashtable value){inputsCount=value;}    
    
/**
 * Job inputjaihoz tartozo feltetelek beallitasa
 * @deprected
 * @param value feltetel lista, kulcs az internal file name, ertek a feltetel string
 * @see Hashtable
 */
    public void setRequirements(Hashtable value){requirements=value;}    
    
/**
 * Outputok beallitasa
 * @param value output lista (value=port nev string key=JobIOBean)
 * @see Hashtable
 */
    public void setEmbedOutputs(Hashtable value){embedOutputs=value;}    
    
/**
 * Plusz parametereke
 * @param value Parameter hash(kulcs,ertek);
 * @see Hashtable
 */
    public void setParams(Hashtable value){params=value;}    
    
    
/**
 * Tarolt adatok stringe alakitasa
 * @return property String
 */
    public String toString() 
    {
        StringBuffer sb=new StringBuffer("");
    
        sb.append("portalID="+portalID+"/n");
        sb.append("storageID="+storageID+"/n");
        sb.append("userID="+userID+"/n");
        sb.append("workflowID="+workflowID+"/n");
        sb.append("jobID="+jobID+"/n");
        sb.append("wfiID="+wfiID+"/n");
        sb.append("wfsID="+wfsID+"/n");
        sb.append("submitHandler="+submitHandler+"/n");
        sb.append("typID="+typID+"/n");
        sb.append("workflowRuntimeID="+workflowRuntimeID+"/n");

        return sb.toString();
    }
    
}

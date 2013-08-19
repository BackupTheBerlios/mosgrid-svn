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
 */

package hu.sztaki.lpds.wfi.zen.pools.inf;

import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstance;
import hu.sztaki.lpds.wfi.zen.pools.RunableInstanceBean;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author krisztian karoczkai
 */
public interface InstancePool 
{
/**
 * Workflow letrehozasa
 * @param pZenID workflow azonositoja
 */   
    public void createWorkflow(String pZenID);
/**
 * Job letrehozasa
 * @param pZenID workflow azonositoja
 * @param pJobID job neve
 */    
    public void createJob(String pZenID,String pJobID);

/**
 * parametrikus job peldany letrehozasa
 * @param pZenID workflow azonosito
 * @param pJobID job neve
 * @param pPID ps peldany sorszama
 */    
    public void createJobInstance(String pZenID,String pJobID,long pPID);
/**
 * csatorna input teljesulese
 * @param pZeniD
 * @param pJobID
 * @param pPID
 */    
    public abstract void addInstanceJobPool(String pZeniD,String pJobID, long pPID, PSInstance pInstance);
/**
 * workflow vege
 * @param pZeniD
 */    
    public void finishWorkflow(String pZeniD);
/**
 * Job futtathatosagi vizsgalat
 * @param pZenID
 * @param pJobID
 * @param pPID
 * @return
 * @throws java.lang.NullPointerException job nem futtathato
 */    
    public RunableInstanceBean runnableJob(String pZenID,String pJobID, long pPID) throws NullPointerException;
/**
 * Parametrikus peldany lekerdezese
 * @param pZenID
 * @param pJobName
 * @param pPID
 * @return
 */    
    public PSInstance getPSInstance(String pZenID, String pJobName, long pPID);
/**
 * Workdlown belul letrehozott jobpeldanyok szama
 * @param pZenId
 * @return ennyi PSInstance peldany van a wf-ban
 */    
    public int getManagedInstance(String pZenId);
/**
 * Egy jobbol managelt peldanyok szamanak lekerdezese
 * @param pZenID
 * @param pJobName
 * @return managelt jobpeldanyok szama
 */    
    public long getInstanceCount(String pZenID, String pJobName);
/**
 * Output csatornaban levo file-ok szamanak lekerdezese
 * @param pZenID
 * @param pJobName
 * @param pOutputName
 * @return
 */    
    public long getOutputCounts(String pZenID, String pJobName, String pOutputName );
/**
 * Output file valos streamIndex-enek lekerdezese;
 * @param pZenID workflow neve
 * @param pJobName job neve
 * @param pPID parametrikus id
 * @param pOutputName output port neve
 * @param pIndex output file index
 * @return
 */
    public long getStreamIndex(String pZenID, String pJobName, long pPID, String pOutputName, long pIndex);
/**
 * Linearizalt output indexhez tartozo tenyleges output meghatarozasa
 * @param pZenID wf id
 * @param pJobName jobNeve
 * @param pOutputName outout sream neve
 * @param pIndex linerizalt output index
 * @return melyik PID melyik index-e lesz a tenyleges megfeleloje a pIndexnek
 */    
    public Hashtable<Long,Long> getPSInstanceInPreJobInstance(String pZenID, String pJobName, String pOutputName, long pIndex);

    
/**
 * Adott PID-u job elso sreamindexbeni poziciojanak meghatarozasa
 * @param pZenID workflow id
 * @param pJobName jon neve
 * @param pOutputName outptu neve
 * @param pPid job PID
 * @return sreamindex
 */
    public long getFirstSreamIndexForPID(String pZenID, String pJobName, String pOutputName, long pPid);

/**
 * Egy WF osszes adott jobjanak osses managelt peldanyanak lekerdezese
 * @param pZenID workflow id
 * @param pJobName jon neve
 * @return Jobpeldany Hash<pid,peldanyleiro>
 */
    public Hashtable<String,PSInstance> getAllJobInstances(String pZenID, String pJobName);

}

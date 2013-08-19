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
 * It contains the data of a workflow
 *
 * This workflow can be a graph,
 * template or concrete workflow too.
 */

package hu.sztaki.lpds.pgportal.service.base.data;

import hu.sztaki.lpds.wfs.com.*;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.com.ServiceType;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author krisztian
 */
public class WorkflowData 
{
    private String workflowID=null;
    private String storageID="";
    private String wfsID=null;
    private String wfsIDs=null;
    private String txt=null;
    private String graf="";
    private String template="";
    private long size;
    private Hashtable jobs=new Hashtable();
    private String timing="";
    private ConcurrentHashMap<String,WorkflowRunTime> runTimesData=new ConcurrentHashMap<String,WorkflowRunTime>();
    private String remoting="";
    private String tmp="0";
    private boolean appmain=false;
    private String workflowType="";
    
    /**
     *  constructor
     * @param pID workflow name
     * @param pWfsID URL of the wfs service which implements the storing
     * @param pWfsIDs resource ID of the wfs service which implements the storing
     * @param pTxt workflow descriptor text
     * @param pSize used storage space
     * @param pGraf implemented graph name
     */
    public WorkflowData(String pID,String pWfsID,String pWfsIDs, String pTxt, long pSize, String pGraf)
    {
//        System.out.println("CREATE:WorkflowData");
        workflowID=pID;
        wfsID=pWfsID;
        wfsIDs=pWfsIDs;
        txt=pTxt;
        size=pSize;
        graf=pGraf;
    }
    
    /**
     *  constructor
     * @param pID workflow name
     * @param pWfsID URL of the wfs service which implements the storing
     * @param pWfsIDs resource ID of the wfs service which implements the storing
     * @param pTxt workflow descriptor text
     * @param pSize used storage space
     * @param pGraf implemented graph name
     * @param pAppMain in case of an application type this is the workflow which can be started
     */
    public WorkflowData(String pID,String pWfsID,String pWfsIDs, String pTxt, long pSize, String pGraf, boolean pAppMain)
    {
//        System.out.println("CREATE:WorkflowData");
        workflowID=pID;
        wfsID=pWfsID;
        wfsIDs=pWfsIDs;
        txt=pTxt;
        size=pSize;
        graf=pGraf;
        appmain=pAppMain;
    }
    
    /**
     *  constructor
     * @param pID workflow name
     * @param pWfsID URL of the wfs service which implements the storing
     * @param pWfsIDs resource ID of the wfs service which implements the storing
     * @param pStorageID storage containing the files of the workflow
     * @param pTxt workflow descriptor text
     * @param pSize used storage space
     * @param pGraf implemented graph name
     * @param pTemplate implemented template name
     * @param pWorkflowType workflow type (for example: zen)
     */
    public WorkflowData(String pID,String pWfsID,String pWfsIDs,String pStorageID, String pTxt, long pSize, String pGraf, String pTemplate,String pWorkflowType)
    {
//        System.out.println("CREATE:WorkflowData");
        workflowID=pID;
        wfsID=pWfsID;
        wfsIDs=pWfsIDs;
        txt=pTxt;
        size=pSize;
        storageID=pStorageID;
        graf=pGraf;
        template=pTemplate;      
        workflowType=pWorkflowType;
    }
    
    /**
     * Getting the workflow type
     * @return workflow type name (for example: zen)
     */
    public String getWorkflowType()
    {
        if ((workflowType == null) || ("".equals(workflowType)) || ("null".equals(workflowType))) {
            return "zen";
        }
        return workflowType;
    }
    
    /**
     * In case of an application type this is the workflow which can be started
     * @return true = it can be started on the end user interface
     */
    public boolean isAppMain(){return appmain;}
    /**
     * Setting the workflow to be a starting workflow in case of an application type
     * @param pAppMain true = workflow to be started
     */
    public void setAppMain(boolean pAppMain){appmain=pAppMain;}
    
    /**
     * Setting a temporary entry
     * @param pValue string value
     */
    public void setTmp(String pValue){tmp=pValue;}
    /**
     * Getting a temporary entry
     * @return temporary entry
     */
    public String getTmp(){return tmp;}
    /**
     * Getting the name of the implemented graph
     * @return graph name
     */
    public String getGraf(){return graf;}
    /**
     * Setting the name of the implemented graph
     * @param pValue graph name
     */
    public void setGraf(String pValue){graf=pValue;}
    /**
     * Setting the name of the implemented template
     * @param pValue template name
     */
    public void setTemplate(String pValue){template=pValue;}
    /**
     * Getting the name of the implemented template
     * @return template name
     */
    public String getTemplate(){return template;}

    /**
     * Getting the instances which belong to the workflow
     * @return instance hash, the key is the internal instance ID, value WorkflowRunTime
     */
    public ConcurrentHashMap<String,WorkflowRunTime> getAllRuntimeInstance(){return runTimesData;}
    /**
     * Getting one instance of a workflow based on the internal instance ID
     * @param pValue instance ID
     * @return instance descriptor
     */
    public WorkflowRunTime getRuntime(String pValue){return (WorkflowRunTime)runTimesData.get(pValue);}

    /**
     * Getting the instance started from the end user interface
     * @return instance descriptor
     */
    public WorkflowRunTime getEinstance() {
        Enumeration keys = runTimesData.keys();
        while (keys.hasMoreElements()) {//traverse the workflows
            String key = (String) keys.nextElement();
            if (((WorkflowRunTime) runTimesData.get(key)).getText().equals("einstance")) {
                return (WorkflowRunTime) runTimesData.get(key);
            }
        }
        return null;
    }
    /**
     * Getting the internal ID of the instance started from the end user interface
     * @return  instance ID
     */
    public String getEinstanceID() {
        Enumeration keys = runTimesData.keys();
        while (keys.hasMoreElements()) {//traverse the workflows
            String key = (String) keys.nextElement();
            if (((WorkflowRunTime) runTimesData.get(key)).getText().equals("einstance")) {
                return ""+key;
            }
        }
        return null;
    }
        
    /**
     * Deleting the workflow instance from the registry
     * @param pValue internal instance ID
     */
    public void deleteRuntime(String pValue){runTimesData.remove(pValue);}
    /**
     * Adding new workflow instance
     * @param pValue instance ID
     * @param pData instance descriptor
     */
    public void addRuntimeID(String pValue, WorkflowRunTime pData){runTimesData.putIfAbsent(pValue, pData);}
//timing
    /**
     * Getting the workflow timing
     * @deprecated
     * @return
     */
    public String getTiming(){return timing;}
    /**
     * Setting the workflow timing
     * @deprecated
     * @param pValue time stamp
     */
    public void setTiming(String pValue){timing=pValue;}
//remoting
    /**
     * Getting the remote workflow start
     * @deprecated
     * @return start key
     */
    public String getRemoting(){return remoting;}
    /**
     * Setting the remote workflow start
     * @param pValue start key
     * @deprecated
     */
    public void setRemoting(String pValue){remoting=pValue;}
	/**
	* Setting the used storage space
	* @param pSize size in bytes
	*/
    public void setSize(long pSize){size=pSize;}
    /**
     * Setting the storage service containing files
     * @param value storage service URL
     */
    public void setStorageID(String value){storageID=value;}
    /**
     * Setting the accessibility of the wfs service storing workflow data
     * @param value wfs service URL
     */
    public void setWfsID(String value){wfsID=value;}
    /**
     * The accessibility of the resource which implements the workflow data storage wfs service
     * @param value wfs service resource
     */
    public void setWfsIDService(String value){wfsIDs=value;}
    /**
     * Workflow description setting
     * @param value longer text description
     */
    public void setTxt(String value){txt=value;}
    
    /**
     * Getting the workflow ID (name)
     * @return
     */
    public String getWorkflowID(){return workflowID;}
    /**
     * Getting the accessibility of the wfs service storing workflow data
     * @return  wfs service URL
     */
    public String getWfsID(){return wfsID;}
    /**
     * Getting the wfs service resource storing workflow data
     * @return  wfs service resource
     */
    public String getWfsIDService(){return wfsIDs;}
    /**
     * Getting the workflow descriptor
     * @return workflow descriptor
     */
    public String getTxt(){return txt;}
    /**
     * Size of the workflow
     * @return size in bytes
     */
    public long getSize(){return size;}
    /**
     * Getting the URL of the local storage service which stores the files of the workflow
     * @return storage URL
     */
    public String getStorageID(){return storageID;}
    /**
     * Getting the jobs of the workflow
     * @param pUser portal user
     * @return jobhash key
     */
    public Hashtable getJobs(String pUser){
        if(jobs.isEmpty()){
            ServiceType st=InformationBase.getI().getService("wfs","portal",new Hashtable(),new Vector());
            try{
                PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                ComDataBean tmp=new ComDataBean();
                tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                tmp.setUserID(pUser);
                tmp.setWorkflowID(workflowID);
//                Vector v=pc.getWorkflowJobs(new PortalUserWorkflowBean(pUser,PropertyLoader.getInstance().getProperty("service.url"), workflowID));
                Vector v=pc.getWorkflowJobs(tmp);
                for(int i=0;i<v.size();i++)
                {
//                    System.out.println(":::"+((JobBean)v.get(i)).getJobID()+":"+((JobBean)v.get(i)).getStatus()+":"+((JobBean)v.get(i)).getResource()+":"+((JobBean)v.get(i)).getTxt()+":"+((JobBean)v.get(i)).getX()+":"+((JobBean)v.get(i)).getY());
                    jobs.put(((JobBean)v.get(i)).getJobID(),new JobData(((JobBean)v.get(i)).getJobID(),((JobBean)v.get(i)).getTxt(),((JobBean)v.get(i)).getX(),((JobBean)v.get(i)).getY(),((JobBean)v.get(i)).getPorts()));
                }
            }
            catch(Exception e){e.printStackTrace();}
        }
        return jobs;
    }
    
    /**
     * Getting the job descriptor of a specific job (before that all the jobs had to be loaded)
     * @param pJobName job name
     * @return job descriptor
     */
    public JobData getJob(String pJobName) {return (JobData)jobs.get(pJobName);}
    
    private long getStatusSummary(int pStatus){
        Enumeration enm=runTimesData.keys();
        String key="";
        String s="";
        long res=0;
        while(enm.hasMoreElements())
        {
            key=""+enm.nextElement();
            try
            {
                s=((WorkflowRunTime)runTimesData.get(key)).getText();
                if((s!=null)&&(!s.equals(""))&&(!s.equals("null")))
                {
                    if(((WorkflowRunTime)runTimesData.get(key)).getStatus()==pStatus)res++;
                }
            }
            catch(Exception e){}
        }
        return res;
    }
    
    /**
     * Getting the number of the instances in submitted status
     * @return the number of the instances in submitted status
     */
    public long getSubmittedStatus(){return getStatusSummary(2);}
    /**
     * Getting the number of the instances in running status
     * @return the number of the instances in running status
     */
    public long getRunningStatus(){return getStatusSummary(5);}
    /**
     * Getting the number of the instances in finished status
     * @return the number of the instances in finished status
     */
    public long getFinishedStatus(){return getStatusSummary(6);}
    /**
     * Getting the number of the instances in error status
     * @return the number of the instances
     */
    public long getErrorStatus(){return getStatusSummary(7)+getStatusSummary(23);}
    /**
     * Getting the number of the instances in suspend status
     * @return the number of the instances
     */
    public long getSuspendStatus(){return getStatusSummary(22)+getStatusSummary(28);}
    
}

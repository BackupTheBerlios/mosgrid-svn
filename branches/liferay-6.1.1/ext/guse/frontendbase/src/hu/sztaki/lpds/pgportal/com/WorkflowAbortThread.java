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
 * WorkflowAbortThread.java
 * Aborts the run of a workflow
 */

package hu.sztaki.lpds.pgportal.com;

import java.util.Vector;
import java.util.Hashtable;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.JobStatusData;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowRunTime;
import hu.sztaki.lpds.portal.net.wsaxis13.StatusHandlerThread;
import hu.sztaki.lpds.wfi.inf.PortalWfiClient;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.wfs.utils.Status;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author krisztian
 */
public class WorkflowAbortThread extends Thread{

    private String userID;
    private String workflowID;
    private String runtimeID;
    private WorkflowData wData;
    
    /**
     * Class constructor
     *
     * @param pUserID - user ID
     * @param pWorkflowID - workflow ID
     * @param pRuntimeID - workflow instance ID
     * @see WorkflowData
     */
    public WorkflowAbortThread(String pUserID, String pWorkflowID, String pRuntimeID)
    {
        userID = pUserID;
        workflowID = pWorkflowID;
        runtimeID = pRuntimeID;
        wData = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID);
        start();
    }
/**
 * @see Thread#run()
 */
    @Override
    public void run(){
        setPriority(MIN_PRIORITY);
        JobStatusBean abortingStatus;
        try {sleep(1000);}
        catch (Exception e) {e.printStackTrace();}
        try {
            WorkflowRunTime instance=PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID);
            ConcurrentHashMap<String,ConcurrentHashMap<String,JobStatusData>> tmp=instance.getJobsStatus();
            Enumeration<String> enm0=tmp.keys();
            while(enm0.hasMoreElements()){
                String key0=enm0.nextElement();
                Enumeration<String> enm1=tmp.get(key0).keys();
                while(enm1.hasMoreElements()){
                    String key1=enm1.nextElement();
                    int jobStatus=tmp.get(key0).get(key1).getStatus();
                    if(Status.isNotEndAndNotInit(jobStatus)) {
                        abortingStatus=new JobStatusBean();
                        abortingStatus.setUserID(userID);
                        abortingStatus.setWorkflowID(workflowID);
                        abortingStatus.setWrtID(runtimeID);
                        abortingStatus.setJobID(key0);
                        abortingStatus.setPID(Long.parseLong(key1));
                        abortingStatus.setStatus(Status.ABORTING);
                        abortingStatus.setWorkflowStatus(""+Status.ABORTING);
                        StatusHandlerThread.getI().add(abortingStatus);
                    }
                }
            }
            sendAbort();
        } catch (Exception e) {e.printStackTrace();}
       
    }

    /**
     * Abort process, running in the background
     */
    private void sendAbort() throws Exception {
        boolean b=true;
        int failed=0;
//wfi        
        while(b)
        {
            b=false;
            Hashtable hsh=new Hashtable();
            hsh.put("url", wData.getRuntime(runtimeID).getWfiURL());
            ServiceType st=InformationBase.getI().getService("wfi","portal",hsh,new Vector());
            try
            {
                PortalWfiClient pc=(PortalWfiClient)Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(wData.getRuntime(runtimeID).getWfiURL());
                pc.setServiceID(st.getServiceID());
                pc.abortWorkflow(runtimeID);
            } catch(Exception e) {
                e.printStackTrace();
                failed++;
//                LoggingBase.getI().sendLog(new LogInfoBean("local", "portal", PropertyLoader.getInstance().getProperty("service.url"), runTimeID, wData.getWorkflowID(), "", "ERROR",e,failed));
                b=true;
                try{sleep((int)(Math.random()*failed*5000));}
                catch(Exception e0) {
                    e0.printStackTrace();
//                    LoggingBase.getI().sendLog(new LogInfoBean("local", "portal", PropertyLoader.getInstance().getProperty("service.url"), runTimeID, wData.getWorkflowID(), "", "ERROR",e0,(-1)));
                }
            }
        }
    }

}

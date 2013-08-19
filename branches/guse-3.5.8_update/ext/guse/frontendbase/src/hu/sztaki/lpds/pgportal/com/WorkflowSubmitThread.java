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
 * WorkflowSubmitThread.java
 * Workflow submit
 */

package hu.sztaki.lpds.pgportal.com;

//import hu.sztaki.lpds.logging.service.trudy.LoggingBase;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowRunTime;
import java.util.Hashtable;
import java.util.Vector;

import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.wfi.inf.PortalWfiClient;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.information.local.PropertyLoader;

/**
 * @author krisztian
 */
public class WorkflowSubmitThread extends Thread
{
    private WorkflowData wData;
    private String userID,submitText;
    private String wfchgType;
    
    /**
     * Class constructor
     * @param pData workflow descriptor
     * @param pUser portal user
     * @param pStext Workflow instance descriptor
     */
    public WorkflowSubmitThread(WorkflowData pData, String pUser,String pStext) 
    {
        wData=pData;
        userID=pUser;
        submitText=pStext;
        start();
    }
    
    /**
     * Class constructor
     * @param pData workflow descriptor
     * @param pUser Portal user
     * @param pStext Workflow instance descriptor
     * @param pWfchgType working method of notify descriptor string
     */
    public WorkflowSubmitThread(WorkflowData pData, String pUser,String pStext, String pWfchgType) 
    {
        wData=pData;
        userID=pUser;
        submitText=pStext;
        wfchgType=pWfchgType.trim();
        start();
    }
    
    /**
     * Submit process, running in the background
     */
    @Override
    public void run()
    {
        boolean b=true;
        int failed=0;
        String runtimeID="";
        ServiceType st=null;
//wfi        
        while(b)
        {
            b=false;
            Hashtable hsh=new Hashtable();
            st=InformationBase.getI().getService("wfi","portal",hsh,new Vector());
            if(st!=null)
            {
                try
                {
                    PortalWfiClient pc=(PortalWfiClient)Class.forName(st.getClientObject()).newInstance();
                    pc.setServiceURL(st.getServiceUrl());
                    pc.setServiceID(st.getServiceID());
                    runtimeID=pc.submitWorkflow(new WorkflowRuntimeBean(PropertyLoader.getInstance().getProperty("service.url"),wData.getStorageID(),wData.getWfsID(), userID, wData.getWorkflowID(),submitText,wData.getWorkflowType()));
                    //                    b=runtimeID.equals("");
                    if(b)
                    {
                        InformationBase.getI().setForbiddenService("wfi", st.getServiceUrl());
//                        LoggingBase.getI().sendLog(new LogInfoBean("local", "portal", PropertyLoader.getInstance().getProperty("service.url"), userID, wData.getWorkflowID(), "", "SERVICE OVERHEAD",st.getServiceUrl()+st.getServiceID(),failed));
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    failed++;
//                    LoggingBase.getI().sendLog(new LogInfoBean("local", "portal", PropertyLoader.getInstance().getProperty("service.url"), userID, wData.getWorkflowID(), "", "ERROR",e,failed));
                    b=true;
                    try{sleep((int)(Math.random()*failed*10000));}
                    catch(Exception e0)
                    {
//                        LoggingBase.getI().sendLog(new LogInfoBean("local", "portal", PropertyLoader.getInstance().getProperty("service.url"), userID, wData.getWorkflowID(), "", "ERROR",e0,(-1)));
                        e0.printStackTrace();
                    }
                }
            }
            else
            {
       //no resource
//                wData.setStatus(8);
                failed++;
//                LoggingBase.getI().sendLog(new LogInfoBean("local", "portal", PropertyLoader.getInstance().getProperty("service.url"), userID, wData.getWorkflowID(), "", "NO FREE SERVICE","Wfi",failed));
                try{sleep((int)(Math.random()*failed*10000));}
                catch(Exception e0){ e0.printStackTrace();}
            }
        }
        PortalCacheService.getInstance().getUser(userID).getWorkflow(wData.getWorkflowID()).addRuntimeID(runtimeID, new WorkflowRunTime(st.getServiceUrl(), st.getServiceID(), submitText));
        // set notify status change string
        PortalCacheService.getInstance().getUser(userID).getWorkflow(wData.getWorkflowID()).getRuntime(runtimeID).setNotifyWfchgType(wfchgType);
    }
    
}

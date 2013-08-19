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
 * WorkflowDeleteThread.java
 * Deletes a workflow
 */

package hu.sztaki.lpds.pgportal.com;

import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import java.util.Hashtable;
import java.util.Vector;

//import hu.sztaki.lpds.logging.service.trudy.LoggingBase;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import hu.sztaki.lpds.information.local.PropertyLoader;

/**
 * @author krisztian
 */
public class WorkflowDeleteThread extends Thread{
    private WorkflowData wData;
    private String userID;
    
    /**
     * Class constructor
     * @param pData workflow descriptor
     * @param pUser portal user
     */
    public WorkflowDeleteThread(WorkflowData pData, String pUser) 
    {
        wData=pData;
        userID=pUser;
        start();
    }
    
    /**
     * Delete process, running in the background
     */
    public void run()
    {
//wfs
        boolean b=true;
        int failed=0;
        while(b)
        {
            Hashtable hsh=new Hashtable();
            hsh.put("url",wData.getWfsID());
            ServiceType st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());
            try
            {
                PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                ComDataBean tmp=new ComDataBean();
                tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                tmp.setUserID(userID);
                tmp.setWorkflowID(wData.getWorkflowID());
                pc.deleteWorkflow(tmp);
                b=false;
            }
            catch(Exception e)
            {
                failed++;
//                LoggingBase.getI().sendLog(new LogInfoBean("local", "portal", PropertyLoader.getInstance().getProperty("service.url"), userID, wData.getWorkflowID(), "", "ERROR",e,failed));
                try{sleep((int)(Math.random()*failed*10000));}
                catch(Exception e0)
                {
                    e0.printStackTrace();
//                    LoggingBase.getI().sendLog(new LogInfoBean("local", "portal", PropertyLoader.getInstance().getProperty("service.url"), userID, wData.getWorkflowID(), "", "ERROR",e0,(-1)));
                }
            }
        }
//storage
        while(b)
        {
            Hashtable hsh=new Hashtable();
            hsh.put("url",wData.getStorageID());
            ServiceType st=InformationBase.getI().getService("storage","portal",hsh,new Vector());
            try
            {
                PortalStorageClient pc=(PortalStorageClient)Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                ComDataBean tmp=new ComDataBean();
                tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                tmp.setUserID(userID);
                tmp.setWorkflowID(wData.getWorkflowID());
                
                pc.deleteWorkflow(tmp);
                b=false;
            }
            catch(Exception e)
            {
                failed++;
//                LoggingBase.getI().sendLog(new LogInfoBean("local", "portal", PropertyLoader.getInstance().getProperty("service.url"), userID, wData.getWorkflowID(), "", "ERROR",e,failed));
                try{sleep((int)(Math.random()*failed*10000));}
                catch(Exception e0)
                {
                    e0.printStackTrace();
//                    LoggingBase.getI().sendLog(new LogInfoBean("local", "portal", PropertyLoader.getInstance().getProperty("service.url"), userID, wData.getWorkflowID(), "", "ERROR",e0,(-1)));
                }
            }
        }
        
//nyilvantartas
        PortalCacheService.getInstance().getUser(userID).deleteWorkflow(wData.getWorkflowID());
        
    }
    
}

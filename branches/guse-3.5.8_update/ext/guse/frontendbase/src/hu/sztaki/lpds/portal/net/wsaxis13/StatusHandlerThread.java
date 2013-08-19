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
 * Status handling implementation
 */
package hu.sztaki.lpds.portal.net.wsaxis13;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.logging.Logger;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowRunTime;
import hu.sztaki.lpds.pgportal.service.workflow.VolatileOutputsThread;
import hu.sztaki.lpds.pgportal.service.workflow.notify.eventhandlers.NotifyWorkflowStatusChange;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import hu.sztaki.lpds.wfs.utils.Status;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

/**
 * @author krisztian
 */
public class StatusHandlerThread extends Thread
{
    private static StatusHandlerThread instance = null;

    private BlockingQueue<JobStatusBean> data = new LinkedBlockingQueue<JobStatusBean>();
    
//    private long sleeptime = 2000;
/**
 * Constructor
 */
    public StatusHandlerThread() {}
/**
 * Getting the singleton object 
 * @return object instance
 */
    public static StatusHandlerThread getI() {
        if (instance == null) {
            instance = new StatusHandlerThread();
            instance.start();
        }
        return instance;
    }
/**
 * Adding new status information to the processing list
 * @param pData status list
 */
    public synchronized void add(Vector pData) {
        data.addAll((Collection<JobStatusBean>)pData);
    }
/**
 * Adding new status information to the processing list
 * @param pData
 */
    public synchronized void add(JobStatusBean pData) {
        data.add(pData);
//        Logger.getI().pool("incoming-status", "new-status workflow=\""+pData.getWrtID()+"\" job=\""+pData.getJobID()+"\" pid=\""+pData.getPID()+"\" job-status=\""+pData.getStatus()+"\"",data.size());
//        Logger.getI().pool(pData.getWrtID(), "new-status workflow=\""+pData.getWrtID()+"\" workflow-status=\""+pData.getWorkflowStatus()+"\" job=\""+pData.getJobID()+"\" pid=\""+pData.getPID()+"\" job-status=\""+pData.getStatus()+"\"",data.size());
//        setStatus(pData);
    }
    
//     @Override
/**
 * Processing the statuses which have already came in
 */
    @Override
    public void run() {
        while(true){
            try {
                setStatus(data.take());
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(StatusHandlerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
/**
 * Setting a status
 * @param pData status descriptor
 */
    private void setStatus(JobStatusBean pData)
    {
        WorkflowRunTime tmp=null;
        try{tmp=PortalCacheService.getInstance().getUser(pData.getUserID()).getWorkflow(pData.getWorkflowID()).getRuntime(pData.getWrtID());}
        catch(Exception e){System.out.println("--"+pData.getWorkflowID()+"/"+pData.getWrtID()+"/"+pData.getJobID()+"."+pData.getPID()+"="+pData.getStatus());}
        try
        {
            if ( tmp== null)
            {
                Hashtable hsh=new Hashtable(); 
                ServiceType st=InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
                PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                ComDataBean cmd=pc.getWorkflowInstanceDesc(pData.getWrtID());
                PortalCacheService.getInstance().getUser(pData.getUserID()).getWorkflow(pData.getWorkflowID()).addRuntimeID(pData.getWrtID(), new WorkflowRunTime(cmd.getWfiURL(),"",cmd.getTxt(), pData.getWorkflowStatus()));
            }
            //
            int actualWfStatus = PortalCacheService.getInstance().getUser(pData.getUserID()).getWorkflow(pData.getWorkflowID()).getRuntime(pData.getWrtID()).getStatus();

            if (37!=actualWfStatus){
                PortalCacheService.getInstance().getUser(pData.getUserID()).getWorkflow(pData.getWorkflowID()).getRuntime(pData.getWrtID()).setStatus(pData.getWorkflowStatus(), pData.getTim(), pData.getJobID(), ""+pData.getPID(),""+pData.getStatus(),pData.getResource());
                int newWfStatus = PortalCacheService.getInstance().getUser(pData.getUserID()).getWorkflow(pData.getWorkflowID()).getRuntime(pData.getWrtID()).getStatus();
                Logger.getI().pool("incoming-status", "work-status job=\""+pData.getJobID()+"\" pid=\""+pData.getPID()+"\" job-status=\""+pData.getStatus()+"\"",data.size());
                Logger.getI().pool("incoming-status", "pre-workflow old=\""+actualWfStatus+"\" new=\""+newWfStatus+"\" data=\""+pData.getWorkflowStatus()+"\"",data.size());
            }// !37 actualWfStatus

            // call notify check
            if (!"".equals(pData.getWorkflowStatus())) {
                if (!"".equals(PortalCacheService.getInstance().getUser(pData.getUserID()).getWorkflow(pData.getWorkflowID()).getRuntime(pData.getWrtID()).getNotifyWfchgType())) {
                    new NotifyWorkflowStatusChange().notifyHandler(pData);
                }
            }
            //
            // deleting volatile output files
            // when the workflow is finished
//            if ("6".equals(pData.getWorkflowStatus())) {
//                new VolatileOutputsThread(pData);
//            }
        }
        catch(Exception e){
            System.out.println("++"+pData.getUserID()+"/"+pData.getWorkflowID()+"/"+pData.getWrtID()+"/"+pData.getJobID()+"."+pData.getPID()+"="+pData.getStatus());
            Logger.getI().pool("incoming-status","data=\""+pData.getUserID()+"/"+pData.getWorkflowID()+"/"+pData.getWrtID()+"/"+pData.getJobID()+"."+pData.getPID()+"="+pData.getStatus()+"\"", e, 0);
            e.printStackTrace();
        }
    }

    public long getItemsInQueue(){return data.size();}
}
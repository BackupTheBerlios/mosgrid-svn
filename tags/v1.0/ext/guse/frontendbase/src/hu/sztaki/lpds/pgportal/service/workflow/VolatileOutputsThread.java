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
package hu.sztaki.lpds.pgportal.service.workflow;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowRunTime;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.wfs.com.VolatileBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Deletes the volatile output  
 * files of the finished workflows.
 *
 * @author lpds
 */
public class VolatileOutputsThread extends Thread {
    
    private JobStatusBean sBean;
/**
 * Constructor, starting thread
 * @param pData Job status descriptor
 */
    public VolatileOutputsThread(JobStatusBean pData) {
        sBean = pData;
        start();
    }
/**
 *@see Thread#run()
 */
    @Override
    public void run() {
        WorkflowRunTime wf = PortalCacheService.getInstance().getUser(sBean.getUserID()).getWorkflow(sBean.getWorkflowID()).getRuntime(sBean.getWrtID());
        if (wf.getStatus()==6) {
            if (!PortalCacheService.getInstance().getUser(sBean.getUserID()).getWorkflow(sBean.getWorkflowID()).getRuntime(sBean.getWrtID()).volatileOutputsIsDeleted()) {
                doDeleteVolatile();
            }
        }
    }
    
    /**
     * Deletes the volatile output files
     */
    private void doDeleteVolatile() {
        try {
            // System.out.println("Volatile finished workflow: " + sBean.getWorkflowID());
            //
            // creating workflow descriptor bean
            //
            ComDataBean comBean = new ComDataBean();
            comBean.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            comBean.setUserID(sBean.getUserID());
            comBean.setWorkflowID(sBean.getWorkflowID());
            comBean.setWorkflowRuntimeID(sBean.getWrtID());
            //
            // Getting the volatile output file data from the wfs
            //
            Hashtable hsh = new Hashtable();
            hsh.put("url", PortalCacheService.getInstance().getUser(sBean.getUserID()).getWorkflow(sBean.getWorkflowID()).getWfsID());
            hsh.put("service", PortalCacheService.getInstance().getUser(sBean.getUserID()).getWorkflow(sBean.getWorkflowID()).getWfsIDService());
            ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
            PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            VolatileBean volatileBean = pc.getVolatileOutputs(comBean);
            //
            // Deleting volatile output files on the storage
            //
            if (volatileBean.getVolatileVector().size() > 0) {
                // The volatile info will be sent only to the storage
                // if there is anything to be deleted
                hsh = new Hashtable();
                st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
                PortalStorageClient sc = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
                sc.setServiceURL(st.getServiceUrl());
                sc.setServiceID(st.getServiceID());
                boolean retBoolean = sc.deleteWorkflowVolatileOutputs(volatileBean);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}

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
 * WorkflowRescueThread.java
 * Workflow rescue
 */

package hu.sztaki.lpds.pgportal.com;

import java.util.Vector;
import java.util.Hashtable;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.wfi.inf.PortalWfiClient;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;

/**
 * @author krisztian
 */
public class WorkflowRescueThread extends Thread
{
    private String portalID;
    private String userID;
    private String workflowID;
    private String runtimeID;
    private String wfStatus;
    private Vector errorJobPidList;

    private WorkflowData wData;
    
    /**
     * Creating class and starting the thread
     * @param pPortalID
     * @param pUserID
     * @param pWorkflowID
     * @param pRuntimeID
     * @param PWfStatus
     * @param pErrorJobPidList
     */
    public WorkflowRescueThread(String pPortalID, String pUserID, String pWorkflowID, String pRuntimeID, String PWfStatus, Vector pErrorJobPidList) {
        portalID = pPortalID;
        userID = pUserID;
        workflowID = pWorkflowID;
        runtimeID = pRuntimeID;
        wfStatus = PWfStatus;
        errorJobPidList = pErrorJobPidList;
        wData = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID);
        //
        start();
    }
/**
 * @see Thread#run()
 */
    @Override
    public void run()
    {
        try {sleep(1000);}
        catch (Exception e) {e.printStackTrace();}

        try {sendRescue();} 
        catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Rescue process, running in the background
     */
    private void sendRescue() throws Exception
    {
            
        try
        {
//storage cleaning
                Hashtable hsh=new Hashtable();
                if (!errorJobPidList.isEmpty()) {
                    ComDataBean idBean = new ComDataBean();
                    idBean.setPortalID(portalID);
                    idBean.setUserID(userID);
                    idBean.setWorkflowID(workflowID);
                    idBean.setWorkflowRuntimeID(runtimeID);
                    //
                    hsh.put("url", wData.getStorageID());
                    ServiceType st=InformationBase.getI().getService("storage","portal",hsh,new Vector());
                    PortalStorageClient ps=(PortalStorageClient)Class.forName(st.getClientObject()).newInstance();
                    ps.setServiceURL(st.getServiceUrl());
                    ps.setServiceID(st.getServiceID());
                    ps.deleteWorkflowLogOutputs(idBean, errorJobPidList);
                }
                if (!"23".equals(wfStatus)) {
                    // nem kuldjuk feleslegessen listatek,
                    // a wfi nek csak running/error(23)
                    // eseten kuldjuk a listat...
                    errorJobPidList = new Vector();
                }
//workflow submit
                hsh=new Hashtable();
                ServiceType st=InformationBase.getI().getService("wfi", "portal", hsh, new Vector());
                PortalWfiClient pc=(PortalWfiClient)Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                String txt=(wData.getRuntime(runtimeID)==null)?"not set":wData.getRuntime(runtimeID).getText();
                WorkflowRuntimeBean t=new WorkflowRuntimeBean(PropertyLoader.getInstance().getProperty("service.url"), wData.getStorageID(), wData.getWfsID(), userID, wData.getWorkflowID(),txt, runtimeID, wData.getWorkflowType(), errorJobPidList);
                pc.rescueWorkflow(t);
                System.out.println("RESCUE");
            }
            catch(Exception e)
            {
                System.out.println("*******ERROR**********");
                e.printStackTrace();
            }
        
    }
    
}

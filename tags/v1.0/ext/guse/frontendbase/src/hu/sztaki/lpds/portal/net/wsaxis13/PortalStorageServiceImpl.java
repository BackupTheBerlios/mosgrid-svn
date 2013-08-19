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
package hu.sztaki.lpds.portal.net.wsaxis13;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowRunTime;
import hu.sztaki.lpds.portal.com.StoragePortalWorkflowNamesBean;
import hu.sztaki.lpds.portal.inf.PortalStorageService;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Handles the request from the 
 * storage on the portal side.
 *
 * @author krisztian
 */
public class PortalStorageServiceImpl implements PortalStorageService {
/**
 * Constructor
 */
    public PortalStorageServiceImpl() { }
    
    /**
     * @see PortalStorageService#newOccupied(hu.sztaki.lpds.wfs.com.ComDataBean)
     */
    public Boolean newOccupied(ComDataBean value) {
        try{
        if(PortalCacheService.getInstance().getUser(value.getUserID())!=null) {
/*            if(PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID())!=null) {
                ServiceType stStorage = InformationBase.getI().getService("storage", "portal", new Hashtable(), new Vector());
                ServiceType stWFS = InformationBase.getI().getService("wfs", "portal", new Hashtable(), new Vector());
                PortalCacheService.getInstance().getUser(value.getUserID()).addOneRealWorkflow(value.getUserID(), value.getWorkflowID(), stWFS.getServiceUrl(), stStorage.getServiceUrl(), "zen");
            }
*/
            if("allworkflow".equalsIgnoreCase(value.getWorkflowRuntimeID())) {
//                System.out.println("QUOTA:"+value.getUserID()+"/"+value.getWorkflowID()+"=>"+value.getSize().longValue());
                PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).setSize(value.getSize().longValue());
            } else {
                if(PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).getRuntime(value.getWorkflowRuntimeID()) != null) {
                    // runtime data is in the cache
                    long newSize=PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).getSize()-PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).getSize()+value.getSize().longValue();
                    PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).setSize(newSize);
                    PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).getRuntime(value.getWorkflowRuntimeID()).setSize(value.getSize().longValue());
                } else {
                    // there is no runtime data in the cache
                    long newSize=PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).getSize()+value.getSize().longValue();
                    PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).setSize(newSize);
                    //
// Getting wfiUrl and text data from the wfs
                    Hashtable hsh=new Hashtable();
                    hsh.put("url",PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).getWfsID());
                    hsh.put("service",PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).getWfsIDService());
                    ServiceType st=InformationBase.getI().getService("wfs","portal", hsh, new Vector());
                    try {
                        PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
                        pc.setServiceURL(st.getServiceUrl());
                        pc.setServiceID(st.getServiceID());
                        ComDataBean cmd=pc.getWorkflowInstanceDesc(value.getWorkflowRuntimeID());
                        PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).addRuntimeID(value.getWorkflowRuntimeID(), new WorkflowRunTime(cmd.getWfiURL(),"",cmd.getTxt(),"2"));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                    PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(value.getWorkflowID()).getRuntime(value.getWorkflowRuntimeID()).setSize(value.getSize().longValue());
                }
            }
            
        }
        }
        catch(Exception e){System.out.println("deleted wfi");}
        return new Boolean(true);
    }
    
    /**
     * Registering the names of the uploaded workflows
     * to the portal inventory.
     *
     * @param  value - workflow names
     * @return true successful name registering
     */
    public Boolean newWorkflowNames(StoragePortalWorkflowNamesBean value) {
        // System.out.println("new workflow names in portal...");
        // System.out.println("portalID   : " + value.getPortalID());
        // System.out.println("storageID  : " + value.getStorageID());
        // System.out.println("wfsID      : " + value.getWfsID());
        // System.out.println("userID     : " + value.getUserID());
        // System.out.println("grafList : " + value.getGrafList());
        // System.out.println("abstList : " + value.getAbstList());
        // System.out.println("realList : " + value.getRealList());
        // System.out.println("add new workflow to PortalCache...");
        Hashtable tempList = null;
        Enumeration enu = null;
        tempList = value.getGrafList();
        if (!tempList.isEmpty()) {
            // add graf, abstract, graph workflows...
            enu = tempList.keys();
            while (enu.hasMoreElements()) {
                String wfName = (String) enu.nextElement();
                String wfText = (String) tempList.get(wfName);
                PortalCacheService.getInstance().getUser(value.getUserID()).addAbstactWorkflows(wfName, wfText, value.getWfsID(), value.getStorageID());
            }
        }
        tempList = value.getAbstList();
        if (!tempList.isEmpty()) {
            // add abst, template workflows...
            enu = tempList.keys();
            while (enu.hasMoreElements()) {
                String wfName = (String) enu.nextElement();
                PortalCacheService.getInstance().getUser(value.getUserID()).addOneTemplateWorkflow(value.getUserID(), wfName, value.getWfsID(), value.getStorageID());
            }
        }
        tempList = value.getRealList();
        if (!tempList.isEmpty()) {
            // add real, concrete workflows...
            enu = tempList.keys();
            while (enu.hasMoreElements()) {
                String wfName = (String) enu.nextElement();
                PortalCacheService.getInstance().getUser(value.getUserID()).addOneRealWorkflow(value.getUserID(), wfName, value.getWfsID(), value.getStorageID(),value.getWorkflowType());
            }
        }
        return new Boolean(true);
    }
    
    /**
     * Checking the workflow names.
     *
     * @param  value - workflow names
     * @return  - error message if exists
     */
    public String checkWorkflowNames(StoragePortalWorkflowNamesBean value) {
        Enumeration enu;
        String msg = new String("");
        // System.out.println("userID   : " + value.getUserID());
        // System.out.println("grafList : " + value.getGrafList());
        // System.out.println("abstList : " + value.getAbstList());
        // System.out.println("realList : " + value.getRealList());
        // System.out.println("checking workflow names...");
        // parse user graf list...
        enu = value.getGrafList().keys();
        while (enu.hasMoreElements()) {
            String name = (String) enu.nextElement();
            if (PortalCacheService.getInstance().getUser(value.getUserID()).getAbstactWorkflow(name) != null) {
                msg = msg.concat(name + " (graph)\n");
            }
        }
        // parse user abst (template) list...
        enu = value.getAbstList().keys();
        while (enu.hasMoreElements()) {
            String name = (String) enu.nextElement();
            if (PortalCacheService.getInstance().getUser(value.getUserID()).getTemplateWorkflow(name) != null) {
                msg = msg.concat(name + " (template)\n");
            }
        }
        // parse user real (concrete) list...
        enu = value.getRealList().keys();
        while (enu.hasMoreElements()) {
            String name = (String) enu.nextElement();
            if (PortalCacheService.getInstance().getUser(value.getUserID()).getWorkflow(name) != null) {
                msg = msg.concat(name + " (concrete)\n");
            }
        }
        if (msg.length() > 0) {
            msg = new String("Workflow exists in user space :\n").concat(msg);
        }
        return msg;
    }
    
}

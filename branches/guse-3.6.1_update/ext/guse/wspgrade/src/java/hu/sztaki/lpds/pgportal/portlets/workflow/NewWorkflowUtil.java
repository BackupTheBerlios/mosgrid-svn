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
 * Workflow letrehozassal kapcsolatos muveletek
 */

package hu.sztaki.lpds.pgportal.portlets.workflow;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.UserData;
import hu.sztaki.lpds.storage.com.StoragePortalCopyWorkflowBean;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import java.util.Hashtable;
import java.util.Vector;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * @author krisztian karoczkai
 */
public class NewWorkflowUtil {

    private static ComDataBean createComunicationObject(ActionRequest request){
        ComDataBean nwb = new ComDataBean();
        nwb.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
        ServiceType st = InformationBase.getI().getService("storage", "portal", new Hashtable(), new Vector());
        nwb.setStorageURL(st.getServiceUrl());
        nwb.setUserID(request.getRemoteUser());
        nwb.setTxt(request.getParameter("pNewWkfDesc"));
        nwb.setWorkflowtype(request.getParameter("pNewType"));
        return nwb;
    }

/**
 * Wf letrehozasa mas wf-bol
 * @param request keres parameterei (pcwkf=masolando wf neve,pNewWkfName=uj wf neve, ha ez nincs beallitva  a workflow parametert is megvizsgalja)
 * @param response valasz
 * @throws javax.portlet.PortletException feldolgozasi hiba
 */
    public static void fromWorkflow(ActionRequest request, ActionResponse response) throws PortletException {
        String pNewWkfName="";
        if(request.getParameter("pNewWkfName")!=null)pNewWkfName=request.getParameter("pNewWkfName").trim();
        else if(request.getParameter("workflow")!=null)pNewWkfName=request.getParameter("workflow").trim();
        ComDataBean nwb = createComunicationObject(request);
        nwb.setWorkflowID(pNewWkfName);

        response.setRenderParameter("nw", pNewWkfName);
        String pcwkf = request.getParameter("pcwkf").trim();
        response.setRenderParameter("msg", "portal.newWorkflow.create");
        nwb.setParentWorkflowID(pcwkf);
        nwb.setTyp(new Integer(4));
        if(((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).getWorkflow(pcwkf) != null) {
            Hashtable hsh = new Hashtable();
            hsh.put("url", PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(pcwkf).getStorageID());
            ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
            try{
                PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
                ps.setServiceURL(st.getServiceUrl());
                ps.setServiceID(st.getServiceID());
                StoragePortalCopyWorkflowBean bean = new StoragePortalCopyWorkflowBean();
                bean.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                bean.setUserID(request.getRemoteUser());
                bean.setSourceWorkflowID(pcwkf);
                bean.setDestinWorkflowID(pNewWkfName);
                Hashtable copyHash = new Hashtable();
                copyHash.put("all", "all");
                bean.setCopyHash(copyHash);
                boolean ret = ps.copyWorkflowFiles(bean);
                ((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).newWorkflow(nwb, ((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).getWorkflow(pcwkf).getWfsID(), ((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).getWorkflow(pcwkf).getWfsIDService());
            }
            catch (Exception e) {e.printStackTrace();response.setRenderParameter("msg", "portal.newWorkflow.error");}
        }

    }


/**
 * Wf letrehozasa mas template-bol
 * @param request keres parameterei (pawkf=template neve,pNewWkfName=uj wf neve)
 * @param response valasz
 * @throws javax.portlet.PortletException feldolgozasi hiba
 */
    public static void fromTemplate(ActionRequest request, ActionResponse response) throws PortletException {
        String pNewWkfName = request.getParameter("pNewWkfName").trim();
        ComDataBean nwb = createComunicationObject(request);
        nwb.setWorkflowID(pNewWkfName);

        response.setRenderParameter("msg", "portal.newWorkflow.create");
        nwb.setParentWorkflowID(request.getParameter("pawkf"));
        nwb.setTyp(new Integer(1));
        nwb.setGraf(((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).getTemplateWorkflow(request.getParameter("pawkf")).getGraf());
        if(((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).getTemplateWorkflow(request.getParameter("pawkf")) != null) {
            ((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).newWorkflow(nwb, ((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).getTemplateWorkflow(request.getParameter("pawkf")).getWfsID(), ((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).getTemplateWorkflow(request.getParameter("pawkf")).getWfsIDService());
            Hashtable hsh = new Hashtable();
            ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
            try{
                PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
                ps.setServiceURL(st.getServiceUrl());
                ps.setServiceID(st.getServiceID());
                StoragePortalCopyWorkflowBean bean = new StoragePortalCopyWorkflowBean();
                bean.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                bean.setUserID(request.getRemoteUser());
                bean.setSourceWorkflowID(request.getParameter("pawkf"));
                bean.setDestinWorkflowID(pNewWkfName);
                Hashtable copyHash = new Hashtable();
                copyHash.put("all", "all");
                bean.setCopyHash(copyHash);
                boolean ret = ps.copyWorkflowFiles(bean);
            } catch (Exception e) {response.setRenderParameter("msg", "portal.newWorkflow.error");}
        }
    }

/**
 * Wf letrehozasa mas graph-bol
 * @param request keres parameterei (pcwkf=masolando wf neve,pNewWkfName=uj wf neve)
 * @param response valasz
 * @throws javax.portlet.PortletException feldolgozasi hiba
 */
    public static void fromGraph(ActionRequest request, ActionResponse response) throws PortletException {
        response.setRenderParameter("msg", "portal.newWorkflow.create");
        String pNewWkfName = request.getParameter("pNewWkfName").trim();
        ComDataBean nwb = createComunicationObject(request);
        nwb.setWorkflowID(pNewWkfName);

        nwb.setParentWorkflowID(request.getParameter("pgraf"));
        nwb.setTyp(new Integer(0));
        nwb.setGraf(request.getParameter("pgraf"));
        if(((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).getAbstactWorkflow(request.getParameter("pgraf")) != null)
            ((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).newWorkflow(nwb, ((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).getAbstactWorkflow(request.getParameter("pgraf")).getWfsID(), ((UserData) PortalCacheService.getInstance().getUser(request.getRemoteUser())).getAbstactWorkflow(request.getParameter("pgraf")).getWfsIDService());
    }


}

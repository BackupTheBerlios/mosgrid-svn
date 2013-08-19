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
 * NewWorkflowPortlet.java
 */

package hu.sztaki.lpds.pgportal.portlets.workflow;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.workflow.Sorter;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.portlet.*;


/**
 * @author krisztian
 */
public class NewWorkflowPortlet extends GenericWSPgradePortlet
{
    
    public NewWorkflowPortlet() {}
    
    /**
     * Portlet nyitokepernyo megjeleneskori adatok kitoltese, initializalasa
     */
    @Override
    public void doView(RenderRequest req, RenderResponse res) throws PortletException,IOException
    {
        res.setContentType("text/html");
        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(req,res);
            return;
        }
        if(req.getParameter("msg")!=null) req.setAttribute("msg", req.getParameter("msg"));
        if(req.getParameter("nw")!=null) req.setAttribute("nw", req.getParameter("nw"));
        req.setAttribute("grafs", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(req.getRemoteUser()).getAbstactWorkflows()));
        req.setAttribute("awkfs", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(req.getRemoteUser()).getTemplateWorkflows()));
        req.setAttribute("wkfs", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflows()));
        req.setAttribute("wftypes", PropertyLoader.getInstance().getProperty("guse.wspgrade.suportedwfengines").split(","));

        try
        {
            PortletRequestDispatcher dispatcher;
            dispatcher = getPortletContext().getRequestDispatcher("/jsp/workflow/newcwrk.jsp");
            req.setAttribute("aWorkflowList",PortalCacheService.getInstance().getUser(req.getRemoteUser()).getAbstactWorkflows());
            dispatcher.include(req, res);
        }
        catch (IOException e){throw new PortletException("JSPPortlet.doView exception", e);}
        
    }

    
    /**
     * Uj konkret workflow letrehozasa
     */
    public void doNew(ActionRequest request, ActionResponse response) throws PortletException {
        try {
            //
            String pNewWkfName = request.getParameter("pNewWkfName").trim();
            //
            if (pNewWkfName.equals("")) {
                response.setRenderParameter("msg", "portal.newWorkflow.noname");
            } else if (request.getParameter("pNewWkfDesc").equals("")) {
                response.setRenderParameter("msg", "portal.newWorkflow.nodesc");
            } else if (PortalCacheService.getInstance().getUser(request.getRemoteUser()).isWorkflow(pNewWkfName)) {
                response.setRenderParameter("msg", "portal.newWorkflow.nocreate");
            } else if (PortalCacheService.getInstance().getUser(request.getRemoteUser()).isTemplateWorkflow(pNewWkfName)) {
                response.setRenderParameter("msg", "portal.newWorkflow.nocreate");
            } else {
                ComDataBean nwb = new ComDataBean();
                nwb.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                ServiceType st = InformationBase.getI().getService("storage", "portal", new Hashtable(), new Vector());

                nwb.setStorageURL(st.getServiceUrl());
                nwb.setUserID(request.getRemoteUser());
                nwb.setWorkflowID(pNewWkfName);
                nwb.setTxt(request.getParameter("pNewWkfDesc"));
                nwb.setWorkflowtype(request.getParameter("pNewType"));

                response.setRenderParameter("nw", pNewWkfName);

                if (request.getParameter("ptyp").equals("graf")) {
                    if((!"".equals(request.getParameter("pgraf"))) && (request.getParameter("pgraf") != null))
                        NewWorkflowUtil.fromGraph(request, response);
                    else response.setRenderParameter("msg", "portal.newWorkflow.noparent");
                    
                }
                if (request.getParameter("ptyp").equals("abst")) {
                    if((!"".equals(request.getParameter("pawkf"))) && (request.getParameter("pawkf") != null)) 
                        NewWorkflowUtil.fromTemplate(request, response);
                    else response.setRenderParameter("msg", "portal.newWorkflow.noparent");
                }
                if (request.getParameter("ptyp").equals("conct")) {
                    String pcwkf = request.getParameter("pcwkf").trim();
                    if((!"".equals(pcwkf)) && (pcwkf != null))
                        NewWorkflowUtil.fromWorkflow(request, response);
                    else response.setRenderParameter("msg", "portal.newWorkflow.noparent");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setRenderParameter("msg", "portal.newWorkflow.error");
        }
    }
    
}

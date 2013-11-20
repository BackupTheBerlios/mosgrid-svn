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
 * LocalFileStoragePortlet.java
 */

package hu.sztaki.lpds.pgportal.portlets.file;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.com.WorkflowDeleteThread;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.workflow.Sorter;
import hu.sztaki.lpds.pgportal.util.stream.HttpDownload;
import javax.portlet.*;
import java.io.*;

/**
 * A felhasznalo tarteruleten elhelyezkedo konkret workflowk
 * kezeleset, letolteset megvalosito portlet osztaly 
 *
 * @author krisztian karoczkai
 */
public class LocalFileStoragePortlet extends GenericWSPgradePortlet
{
    public LocalFileStoragePortlet() {}
    
    @Override
    public void doView(RenderRequest req, RenderResponse res) throws PortletException,IOException {
        res.setContentType("text/html");
        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(req, res);
            return;
        }
        // Sorting...
        // olds req.setAttribute("workflows", PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflows());
        req.setAttribute("workflows", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflows()));
        // Sorting...
        long userQuota = PortalCacheService.getInstance().getUser(req.getRemoteUser()).getQuotaSpace(req.getRemoteUser());
        long userUseQuota = PortalCacheService.getInstance().getUser(req.getRemoteUser()).getUseQuotaSpace();
        long userQuotaPercent = userUseQuota * 100 / userQuota;        
        req.setAttribute("userID", req.getRemoteUser());
        req.setAttribute("userQuota", ""+userQuota);
        req.setAttribute("userUseQuota",""+userUseQuota);
        req.setAttribute("userQuotaPercent", ""+userQuotaPercent);
        req.setAttribute("portalID", PropertyLoader.getInstance().getProperty("service.url"));
        try {
            PortletRequestDispatcher dispatcher;
            dispatcher = getPortletContext().getRequestDispatcher("/jsp/workflow/storage.jsp");
            dispatcher.include(req, res);
        } catch (IOException e){throw new PortletException("JSPPortlet.doView exception", e);}
        
    }
    
    
    /**
     * Workflow torlese
     */
    public void doDelete(ActionRequest request, ActionResponse response) throws PortletException {
        new WorkflowDeleteThread(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")),request.getRemoteUser());
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException
    {
        response.setContentType("application/zip");
        response.setProperty("Content-Disposition", "inline; filename=\"" + request.getParameter("workflowID")+"_"+request.getParameter("downloadType") + "\"");

        try{HttpDownload.fileDownload(request.getParameter("downloadType"), request, response);}
        catch(Exception e){e.printStackTrace();throw new PortletException("com error");}

    }

    
}

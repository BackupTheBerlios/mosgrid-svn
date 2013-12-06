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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.pgportal.portlets.dspace;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import hu.sztaki.lpds.pgportal.service.workflow.UserQuotaUtils;
import hu.sztaki.lpds.pgportal.services.dspace.DSpaceUtil;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 *
 * @author csig
 */
public class DSpaceImportPortlet extends GenericWSPgradePortlet {

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.setContentType("text/html");
        if (!isInited()) {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);
            return;
        }
        if (!new DSpaceUtil().isConfigured()){
            getPortletContext().getRequestDispatcher("/jsp/dspace/error.jsp").include(request, response);
            return;
        }
        request.setAttribute("dspaceurl", PropertyLoader.getInstance().getProperty("dspace.url"));
        request.setAttribute("dspacecoll", PropertyLoader.getInstance().getProperty("dspace.collection"));

        PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/jsp/dspace/import.jsp");
        dispatcher.include(request, response);
    }


    /**
     * Handles a workflow download from the repository.
     *
     * @param request
     * @param response
     */
    public void doRepositoryDownload(ActionRequest request, ActionResponse response) {
        if (UserQuotaUtils.getInstance().userQuotaIsNotFull(request.getRemoteUser())) {
            // a feltoltott graf workflow ezt a nevet kapja
            String newGrafName = request.getParameter("wfimp_newGrafName");
            // a feltoltott abstract workflow ezt a nevet kapja
            String newAbstName = request.getParameter("wfimp_newAbstName");
            // a feltoltott real, konkret workflow ezt a nevet kapja
            String newRealName = request.getParameter("wfimp_newRealName");
            if (newGrafName == null) {
                newGrafName = "";
            }
            if (newAbstName == null) {
                newAbstName = "";
            }
            if (newRealName == null) {
                newRealName = "";
            }
            if (!"".equals(newAbstName)) {
                if (newAbstName.equalsIgnoreCase(newRealName)) {
                    request.setAttribute("msg", "Template workflow name equals concrete workflow name.");
                    return;
                }
            }
            Hashtable h = new Hashtable(); //for fileupload to guse storage
            h.put("newGrafName", newGrafName);
            h.put("newAbstName", newAbstName);
            h.put("newRealName", newRealName);

            // DSpace handle
            String handle = request.getParameter("dspaceHandle");
            try {
                new DSpaceUtil().importFromDspace(handle, request.getRemoteUser(), h);
                request.setAttribute("msg", "Import succesfull.");
            } catch (UnsupportedEncodingException e1) {
                request.setAttribute("msg", "Invalid handle. ");
                System.out.println(e1.getMessage());
            } catch (IOException e2) {
                request.setAttribute("msg", "Could not get data from DSpace server. ");
                System.out.println(e2.getMessage());
            } catch (Exception e3) {
                String msg="";
                if ("error.upload.500".equals(e3.getMessage())){
                    msg="Workflow exists in user space, or it is not a valid gUSE Workflow.";
                }
                request.setAttribute("msg", "Could not upload data to storage. " + msg);
                System.out.println(e3.getMessage());
            }

        } else {
            // elfogyott a quota
            request.setAttribute("msg", PortalMessageService.getI().getMessage("portal.RealWorkflowPortlet.quotaisoverfull"));
        }


    }
}

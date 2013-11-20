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
package hu.sztaki.lpds.pgportal.portlets.dspace;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.workflow.Sorter;
import hu.sztaki.lpds.pgportal.services.dspace.DSpaceUtil;
import java.io.IOException;
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
public class DSpaceExportPortlet extends GenericWSPgradePortlet {

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.setContentType("text/html");
        if (!isInited()) {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);
            return;
        }
        if (!new DSpaceUtil().isConfigured()) {
            getPortletContext().getRequestDispatcher("/jsp/dspace/error.jsp").include(request, response);
            return;
        }

        request.setAttribute("dspaceURL", PropertyLoader.getInstance().getProperty("dspace.url"));
        request.setAttribute("wkfs", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflows()));

        PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/jsp/dspace/export.jsp");
        dispatcher.include(request, response);
    }

    /**
     * Handles a workflow upload to the DSpace repository.
     *
     * @param request
     * @param response
     */
    public void doRepositoryUpload(ActionRequest request, ActionResponse response) {
        System.out.println("doRepositoryUpload - DSpace");
        // Get DSpace username and password
        if ("".equals(request.getParameter("dsemail").trim())
                || "".equals(request.getParameter("dspass").trim())
                || "".equals(request.getParameter("firstName").trim())
                || "".equals(request.getParameter("lastName").trim())
                || "".equals(request.getParameter("title").trim())
                || "-".equals(request.getParameter("workflowID").trim())) {
            request.setAttribute("dsemail", request.getParameter("dsemail").trim());
            request.setAttribute("firstName", request.getParameter("firstName").trim());
            request.setAttribute("lastName", request.getParameter("lastName").trim());

            request.setAttribute("title", request.getParameter("title").trim());
            request.setAttribute("keywords", request.getParameter("keywords").trim());
            request.setAttribute("grid", request.getParameter("grid").trim());
            request.setAttribute("vo", request.getParameter("vo").trim());

            request.setAttribute("abstract", request.getParameter("abstract").trim());
            request.setAttribute("description", request.getParameter("description").trim());

            request.setAttribute("msg", "You must fill in the required forms!");


        } else {
            try {
                new DSpaceUtil().exportToDspace(request);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

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
 * RemotingWorkflowPortlet.java
 */

package hu.sztaki.lpds.pgportal.portlets.workflow;

import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.workflow.Sorter;
import java.io.IOException;
import javax.portlet.*;

/**
 * Portlet class for managing workflows that are running by remote web service invocation. 
 *
 * @author krisztian karoczkai
 */
public class RemotingWorkflowPortlet extends GenericWSPgradePortlet
{

    /** Portlet normal view */
    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException,IOException
    {
        response.setContentType("text/html");
        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);
            return;
        }
        try
        {
            request.setAttribute("workflows", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflows()));
            PortletRequestDispatcher dispatcher=null;
            dispatcher = getPortletContext().getRequestDispatcher("/jsp/workflow/remoting.jsp");
            dispatcher.include(request, response);
        }
        catch (IOException e){throw new PortletException("JSPPortlet.doView exception", e);}
        
    }
    
    /** Portlet event */
    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException
    {
        
        if((request.getParameter("remotingtext")!=null)&&(request.getParameter("remotingworkflow")!=null))
        {PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("remotingworkflow")).setRemoting(request.getParameter("remotingtext"));}

        if(request.getParameter("remotingdelete")!=null)
        {PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("remotingdelete")).setRemoting("");}

    }
    
    
}

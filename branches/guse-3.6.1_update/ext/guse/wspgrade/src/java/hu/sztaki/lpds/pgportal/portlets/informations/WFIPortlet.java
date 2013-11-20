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
 * Informacio szolgaltatasa gUSE wfi service allapotarol
 */

package hu.sztaki.lpds.pgportal.portlets.informations;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlets.workflow.RealWorkflowPortlet;
import hu.sztaki.lpds.wfi.inf.PortalWfiClient;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author krisztian karoczkai
 */

public class WFIPortlet extends RealWorkflowPortlet
{


    @Override
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException,IOException
    {
        response.setContentType("text/html");
        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);
            return;
        }
        if(request.getAttribute("navigatepage")!=null)
        {
            getPortletContext().getRequestDispatcher((String)request.getAttribute("navigatepage")).include(request, response);
            return;
        }
        
        viewOneService(request, response);
        
    }

   
    
    private void viewOneService(RenderRequest arg0, RenderResponse arg1) throws PortletException, IOException 
    {
            Hashtable hsh=new Hashtable();
            ServiceType st=InformationBase.getI().getService("wfi","portal",hsh,new Vector());
            if(st!=null)
            {
                try
                {
                    PortalWfiClient pc=(PortalWfiClient)Class.forName(st.getClientObject()).newInstance();
                    pc.setServiceURL(st.getServiceUrl());
                    pc.setServiceID(st.getServiceID());
                    arg0.setAttribute("data", pc.getInformation());
                    arg0.setAttribute("portalid", PropertyLoader.getInstance().getProperty("service.url"));
                    arg0.setAttribute("userid", arg0.getRemoteUser());
                    if("root".equals(arg0.getRemoteUser())) getPortletContext().getRequestDispatcher("/jsp/informations/wfiroot.jsp").include(arg0, arg1);
                    else getPortletContext().getRequestDispatcher("/jsp/informations/wfiuser.jsp").include(arg0, arg1);
                }
                catch(Exception e) {getPortletContext().getRequestDispatcher("/jsp/informations/error.jsp").include(arg0, arg1);}
            }
            else getPortletContext().getRequestDispatcher("/jsp/informations/error.jsp").include(arg0, arg1);    
    }
    

    
}

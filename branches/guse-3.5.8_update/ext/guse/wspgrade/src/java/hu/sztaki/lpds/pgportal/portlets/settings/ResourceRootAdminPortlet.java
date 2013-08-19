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
package hu.sztaki.lpds.pgportal.portlets.settings;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.data.ResourceBean;
import hu.sztaki.lpds.information.inf.ResourceConfigurationClient;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * ResourceRootAdminPortlet Portlet Class
 */
public class ResourceRootAdminPortlet extends GenericWSPgradePortlet
{
/**
 * Uj eroforras felvitele
 * @param request
 * @param response
 * @throws javax.portlet.PortletException
 * @throws java.io.IOException
 */
    public void doNewResource(ActionRequest request, ActionResponse response) throws PortletException,IOException
    {
        try
        {
            if(request.getParameter("psite")!=null)
            {
                PortletSession ps=request.getPortletSession();
                ResourceConfigurationClient ic=(ResourceConfigurationClient)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                String msg=ic.addResource("", request.getRemoteUser(), (String)ps.getAttribute("midleware",ps.APPLICATION_SCOPE), (String)ps.getAttribute("newResourceType"), request.getParameter("psite"), request.getParameter("pjman"));
                response.setRenderParameter("msg", msg);
            }
        }
        catch(Exception e){e.printStackTrace();response.setRenderParameter("msg", "error.com");}
    }
/**
 * Futtato eroforras eroforras torlese
 * @param request
 * @param response
 * @throws javax.portlet.PortletException
 * @throws java.io.IOException
 */
    public void doDeleteResource(ActionRequest request, ActionResponse response) throws PortletException,IOException
    {
        try
        {
            if(request.getParameter("id")!=null)
            {
                PortletSession ps=request.getPortletSession();
                ResourceConfigurationClient ic=(ResourceConfigurationClient)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                String msg=ic.deleteResource("", request.getRemoteUser(), (String)ps.getAttribute("midleware",ps.APPLICATION_SCOPE), (String)ps.getAttribute("resourceGroup"), request.getParameter("id"));
                response.setRenderParameter("msg", msg);
            }
        }
        catch(Exception e){e.printStackTrace();response.setRenderParameter("msg", "error.com");}
    }

//uj grid/vo felvitele
    public void doNew(ActionRequest request, ActionResponse response) throws PortletException,IOException
    {
        PortletSession ps=request.getPortletSession();
        ServiceType st=InformationBase.getI().getService("resourceconfigure", "portal", new Hashtable(), new Vector());
        ResourceConfigurationClient ic=null;
        try
        {
            ic=(ResourceConfigurationClient)Class.forName(st.getClientObject()).newInstance();
            ic.setServiceURL(st.getServiceUrl());
            ic.setServiceID(st.getServiceID());
//grid/VO parameterek beallitasa
            if(request.getParameter("new.name")!=null)
            {
                String name=request.getParameter("new.name");
                HashMap props=new HashMap();
                Enumeration<String> enm=request.getParameterNames();
                String key;
                while(enm.hasMoreElements())
                {
                    key=enm.nextElement();
                    if(!"new.name".equals(key))
                        props.put(key, request.getParameter(key));
                }
                ic.setGridProperies("", request.getRemoteUser(), (String)ps.getAttribute("midleware",ps.APPLICATION_SCOPE), name, props);
            }
        }
        catch(Exception e){e.printStackTrace();response.setRenderParameter("msg", "error.com");}
    }

    @Override
    public void doView(RenderRequest request,RenderResponse response) throws PortletException,IOException 
    {
        response.setContentType("text/html");
        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);
            return;
        }
//message paremeter atadasa render kornyezetnek
        if(request.getParameter("msg")!=null) request.setAttribute("msg", request.getParameter("msg"));
//portlet session lekerdezese
        PortletSession ps=request.getPortletSession();

        List<String> midlewares=new Vector<String>(); //midleware lista
        List<String> grids=new Vector<String>(); //grid lista
// information service hivo kliens letrehozasa
        ServiceType st=InformationBase.getI().getService("resourceconfigure", "portal", new Hashtable(), new Vector());
        ResourceConfigurationClient ic=null;
        try
        {
            ic=(ResourceConfigurationClient)Class.forName(st.getClientObject()).newInstance();
            ic.setServiceURL(st.getServiceUrl());
            ic.setServiceID(st.getServiceID());
// elerheto midleware-ek lekerdezese
            midlewares=ic.getAllMidleware(PropertyLoader.getInstance().getProperty("service.url"), request.getRemoteUser());
            ps.setAttribute("midlewares", midlewares,ps.APPLICATION_SCOPE);

// kivalasztott midleware tipusanak atadasa megjelenitese
            if (request.getParameter("guse-render") == null) {
                if (ps.getAttribute("midleware", ps.APPLICATION_SCOPE) == null) {
                    request.setAttribute("rendertype", midlewares.get(0));
                } else {
                    request.setAttribute("rendertype", ps.getAttribute("midleware", ps.APPLICATION_SCOPE));
                }
            } else {
                request.setAttribute("rendertype", request.getParameter("guse-render"));
            }
            ps.setAttribute("midleware", request.getAttribute("rendertype"), ps.APPLICATION_SCOPE);

// kivalasztott midleware-k grid/vo-i nak lekerdezese
            grids=ic.getAllGrids(PropertyLoader.getInstance().getProperty("service.url"), request.getRemoteUser(),(String)request.getAttribute("rendertype"));
            ps.setAttribute("grids", grids,ps.APPLICATION_SCOPE);
// lostaban legelso grid/vo tulajdonsaglistajanak betoltese
            HashMap props=ic.getGridProperies(PropertyLoader.getInstance().getProperty("service.url"), request.getRemoteUser(),(String)ps.getAttribute("midleware",ps.APPLICATION_SCOPE),grids.get(0));
            request.setAttribute("prop", props);
// kivalasztott grid/vo eroforraslostaja
            if(request.getParameter("lres")!=null)
            {
                ps.setAttribute("resourceGroup",request.getParameter("lres"));
                Collection<ResourceBean> rlst=ic.getAllResource(PropertyLoader.getInstance().getProperty("service.url"), request.getRemoteUser(),(String)ps.getAttribute("midleware",ps.APPLICATION_SCOPE), request.getParameter("lres"));
                request.setAttribute("resouceslist", rlst);
            }
        }
        catch(Exception e)
        {
                e.printStackTrace();
//                return;
        }
//felvitelre kivalasztott grid/VO nevenek eltarolasa
        if(request.getParameter("nres")!=null)
            ps.setAttribute("newResourceType", request.getParameter("nres"));

// vezerles atadasa megjelenitesre
        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/resourceadmin/view.jsp").include(request, response);
    }

    @Override
    public void doHelp(RenderRequest request, RenderResponse response) throws PortletException,IOException
    {
        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/resourceadmin/help.jsp");
    
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException
    {
        PortletSession ps=request.getPortletSession();
        ServiceType st=InformationBase.getI().getService("resourceconfigure", "portal", new Hashtable(), new Vector());
        ResourceConfigurationClient ic=null;
        HashMap props=null;
        try
        {
            ic=(ResourceConfigurationClient)Class.forName(st.getClientObject()).newInstance();
            ic.setServiceURL(st.getServiceUrl());
            ic.setServiceID(st.getServiceID());
            props=ic.getGridProperies(PropertyLoader.getInstance().getProperty("service.url"), request.getRemoteUser(),(String)ps.getAttribute("midleware",ps.APPLICATION_SCOPE),request.getParameter("vo"));
            request.setAttribute("prop", props);
        }
        catch(Exception e)
        {
                e.printStackTrace();
                return;
        }
        request.setAttribute("selected", request.getParameter("vo"));
        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/resourceadmin/middlewares/glite_config.jsp").include(request, response);
    }


}
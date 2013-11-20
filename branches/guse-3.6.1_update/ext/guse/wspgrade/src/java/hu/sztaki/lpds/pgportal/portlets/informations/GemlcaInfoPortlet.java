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
 * GemlcaInfoPortlet.java
 */
package hu.sztaki.lpds.pgportal.portlets.informations;

import hu.sztaki.lpds.dcibridge.client.ResourceConfigurationFace;
import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.service.base.GemlcaCacheService;
import javax.portlet.*;
import java.io.*;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;


/**
 * @author krisztian karoczkai
 */
public class GemlcaInfoPortlet extends GenericWSPgradePortlet
{

    public GemlcaInfoPortlet() {
    }
    private String action = "";
    private String mainjsp = "/jsp/workflow/gemlcainfo.jsp";
    private String jsp = mainjsp;
    private PortletContext pContext;

    /**
     * Portlet inicializalasa
     */
    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        pContext = config.getPortletContext();
    }

    /**
     * Portlet UI megjelenitesehez az adatok atadasa ( Applications )
     */
    @Override
    public void doView(RenderRequest re0, RenderResponse response) throws PortletException,IOException
    {
        response.setContentType("text/html");
        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(re0, response);
            return;
        }
        openRequestAttribute(re0);
        try
        {
            if (action.equals("")) jsp = mainjsp;

            try
            {
 //Session lekerdezese
                PortletSession ps=re0.getPortletSession();
  // elerheto eroforras konfiguracio lekerdezese
            try{
                if (ps.getAttribute("resources") == null) {
                    ResourceConfigurationFace rc=(ResourceConfigurationFace)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                    List<Middleware> tmp_r=rc.get();
                    ps.setAttribute("resources", tmp_r);
                    ps.setAttribute("pub_resources", tmp_r,ps.APPLICATION_SCOPE);
                }
            }
            catch(Exception ex) {ex.printStackTrace();}

                Vector tmp=ConfigHandler.getGroups((List<Middleware>) ps.getAttribute("resources"), "gemlca");
                re0.setAttribute("grids", tmp);//GLCurl lista
            }
            catch (Exception e)
            {
                re0.setAttribute("msg", ""+e.getMessage());
            }
            re0.setAttribute("userID", re0.getRemoteUser());
            re0.setAttribute("portalID", PropertyLoader.getInstance().getProperty("service.url"));
            PortletRequestDispatcher dispatcher = null;
            dispatcher = pContext.getRequestDispatcher(jsp);
            dispatcher.include(re0, response);
        } 
        catch (IOException e)
        {
            throw new PortletException("GemlcaInfoPortlet.doView exception", e);
        }
        action = "";
        cleanRequestAttribute(re0.getPortletSession());
    }

    /**
     * GLC lista megjelenitese
     */
    public void doList(ActionRequest request, ActionResponse response) throws PortletException {
        jsp = mainjsp;
                PortletSession ps=request.getPortletSession();

        String user = request.getRemoteUser();
        try {
                Vector tmp=ConfigHandler.getGroups((List<Middleware>) ps.getAttribute("resources"), "Gemlca");
                setRequestAttribute(request.getPortletSession(),"grids", tmp);//GLCurl lista
            if (request.getParameter("GLCurl") != null) {
                setRequestAttribute(request.getPortletSession(),"sgrid", "" + request.getParameter("GLCurl"));//selected GLCurl
                setRequestAttribute(request.getPortletSession(),"GLCs", GemlcaCacheService.getInstance().getGLCList("" + PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "/users/" + user + "/x509up." + request.getParameter("GLCurl"), "" + request.getParameter("GLCurl"), -1, -1));//GLC list
                if (request.getParameter("GLC") != null) {
                    try {
                        
                        setRequestAttribute(request.getPortletSession(),"GLCparams", GemlcaCacheService.getInstance().getGLCparams("" + request.getParameter("GLCurl"), "" + request.getParameter("GLC")));//GLCparams list
                        setRequestAttribute(request.getPortletSession(),"GLCin", GemlcaCacheService.getInstance().getGLCinputNmbr("" + request.getParameter("GLCurl"), "" + request.getParameter("GLC")));
                        setRequestAttribute(request.getPortletSession(),"GLCout", GemlcaCacheService.getInstance().getGLCoutNmbr("" + request.getParameter("GLCurl"), "" + request.getParameter("GLC")));
                        setRequestAttribute(request.getPortletSession(),"sGLC", "" + request.getParameter("GLC"));//selected GLCurl
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            setRequestAttribute(request.getPortletSession(),"msg", ""+e.getMessage());
        }
    }
}

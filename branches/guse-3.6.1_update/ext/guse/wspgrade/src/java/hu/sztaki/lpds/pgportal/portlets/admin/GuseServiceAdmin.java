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
package hu.sztaki.lpds.pgportal.portlets.admin;
import hu.sztaki.lpds.information.data.GuseServiceBean;
import hu.sztaki.lpds.information.data.GuseServiceCommunicationBean;
import hu.sztaki.lpds.information.data.GuseServiceTypeBean;
import hu.sztaki.lpds.information.data.ServiceResourceBean;
import hu.sztaki.lpds.information.data.ServiceUserBean;
import hu.sztaki.lpds.information.inf.InformationClient;
import hu.sztaki.lpds.information.inf.ServiceAdminClient;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.util.List;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * GuseServiceAdmin Portlet Class
 */
public class GuseServiceAdmin extends GenericWSPgradePortlet {

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException
    {
        PortletSession ps=request.getPortletSession();

// information service hivo kliens letrehozasa
        ServiceAdminClient ic=null;
        try{ic=(ServiceAdminClient)InformationBase.getI().getServiceClient("serviceadmin", "portal");}
        catch(Exception e)
        {
                response.setRenderParameter("msg","error.guseconfiguration");
                e.printStackTrace();
                return;
        }
        //service felvitele es modositasa
        if("newservice".equals(request.getParameter("guse")))
        {
            response.setRenderParameter("guse-render", "service");

            GuseServiceBean tmp;
            if(ps.getAttribute("serviceitem",ps.APPLICATION_SCOPE)==null)
                tmp=new GuseServiceBean();
            else
                tmp=(GuseServiceBean)ps.getAttribute("serviceitem",ps.APPLICATION_SCOPE);
            tmp.setUrl(request.getParameter("purl"));
            tmp.setSurl(request.getParameter("psurl"));
            tmp.setIurl(request.getParameter("piurl"));
            tmp.setOwner("root");
            tmp.setState("true".equals(request.getParameter("pstatus")));

            try
            {
                tmp.setTyp(getServiceTypeBean(request.getParameter("pstyp"), (List<GuseServiceTypeBean>)ps.getAttribute("styps",ps.APPLICATION_SCOPE)));
            }
            catch(Exception e)
            {
                response.setRenderParameter("msg","error.request");
                e.printStackTrace();
                return;
            }
            try{response.setRenderParameter("msg",ic.dataManagement(tmp));}
            catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
        }
//service torlese
        else if(request.getParameter("dsid")!=null)
        {
            try{response.setRenderParameter("msg",ic.deleteService(request.getParameter("dsid")));}
            catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
            response.setRenderParameter("guse-render", "service");
        }
//service tipus felvitele es modositasa
        else if("newservicetype".equals(request.getParameter("guse")))
        {
            GuseServiceTypeBean tmp;
            if(ps.getAttribute("typeitem",ps.APPLICATION_SCOPE)==null)
                tmp=new GuseServiceTypeBean();
            else
                tmp=(GuseServiceTypeBean)ps.getAttribute("typeitem",ps.APPLICATION_SCOPE);

            tmp.setSname(request.getParameter("pname"));
            tmp.setTxt(request.getParameter("ptxt"));

            try{response.setRenderParameter("msg",ic.dataManagement(tmp));}
            catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
            response.setRenderParameter("guse-render", "type");
        }
//service tipus torlese
        else if(request.getParameter("dtid")!=null)
        {
            try{response.setRenderParameter("msg",ic.deleteServiceType(request.getParameter("dtid")));}
            catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
            response.setRenderParameter("guse-render", "type");
        }
//kommunikacios tipus letrehozasa es modositasa
        else if("newcommtype".equals(request.getParameter("guse")))
        {
            GuseServiceCommunicationBean tmp;
            if(ps.getAttribute("comitem",ps.APPLICATION_SCOPE)==null)
                tmp=new GuseServiceCommunicationBean();
            else
                tmp=(GuseServiceCommunicationBean)ps.getAttribute("comitem",ps.APPLICATION_SCOPE);
            tmp.setCname(request.getParameter("pname"));
            tmp.setTxt(request.getParameter("ptxt"));
            try{response.setRenderParameter("msg",ic.dataManagement(tmp));}
            catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
            response.setRenderParameter("guse-render", "comm");
        }
//service kommunikacio torlese
        else if(request.getParameter("dcid")!=null)
        {
            try{response.setRenderParameter("msg",ic.deleteServiceCom(request.getParameter("dcid")));}
            catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
            response.setRenderParameter("guse-render", "comm");
        }
// uj jogosultsag felvitele
        else if(request.getParameter("pnlu")!=null)
        {
            ServiceUserBean tmp=new ServiceUserBean();
            tmp.setLname(request.getParameter("pnlu"));
            GuseServiceBean service=(GuseServiceBean)ps.getAttribute("auth",ps.APPLICATION_SCOPE);
            if(service!=null)
            {
                try{response.setRenderParameter("msg",ic.addServiceUser(""+service.getId(),tmp));}
                catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
                response.setRenderParameter("ssid", ""+service.getId());
            }
            else response.setRenderParameter("msg", "error.input");
            response.setRenderParameter("guse-render", "service");
        }
// felhasznaloi jogosultsag torlese
        else if(request.getParameter("pdlu")!=null)
        {
            GuseServiceBean service=(GuseServiceBean)ps.getAttribute("auth",ps.APPLICATION_SCOPE);
            if(service!=null)
            {
                try{response.setRenderParameter("msg",ic.deleteServiceUser(""+service.getId(),request.getParameter("pdlu")));}
                catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
                response.setRenderParameter("ssid", ""+service.getId());
            }
            else response.setRenderParameter("msg", "error.input");
            response.setRenderParameter("guse-render", "service");
        }
// kommunikacos csatorna felvitele
        else if(request.getParameter("pcls")!=null)
        {

            long scid;
            GuseServiceCommunicationBean sc=(GuseServiceCommunicationBean)ps.getAttribute("comcom",ps.APPLICATION_SCOPE);
            if((ps.getAttribute("styps",ps.APPLICATION_SCOPE)!=null)&&(sc!=null))
            {
                try
                {
                    GuseServiceTypeBean src=getServiceTypeBean(request.getParameter("psservice"), (List<GuseServiceTypeBean>)ps.getAttribute("styps",ps.APPLICATION_SCOPE));
                    GuseServiceTypeBean dst=getServiceTypeBean(request.getParameter("pdservice"), (List<GuseServiceTypeBean>)ps.getAttribute("styps",ps.APPLICATION_SCOPE));
                    if((src!=null)&&(dst!=null))
                    {
                        ServiceResourceBean srb=new ServiceResourceBean();
                        srb.setCaller(request.getParameter("pcls"));
                        srb.setRes(request.getParameter("pres"));
                        srb.setDst(dst);
                        srb.setSrc(src);
                        try{response.setRenderParameter("msg",ic.addCommChanel(""+sc.getId(), srb));}
                        catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
                    }
                    else response.setRenderParameter("msg", "error.input");

                    response.setRenderParameter("scid", ""+sc.getId());
                }
                catch(Exception e){response.setRenderParameter("msg", "error.input");}
            }
            else response.setRenderParameter("msg", "error.input");
            response.setRenderParameter("guse-render", "comm");
        }
// kommunikacios csatorna torlese
        else if(request.getParameter("dsrid")!=null)
        {
            GuseServiceCommunicationBean sc=(GuseServiceCommunicationBean)ps.getAttribute("comcom",ps.APPLICATION_SCOPE);
            if(sc!=null)
            {    
                try{response.setRenderParameter("msg",ic.deleteCommChanel(""+sc.getId(), request.getParameter("dsrid")));}
                catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
                response.setRenderParameter("scid", ""+sc.getId());
            }
            response.setRenderParameter("guse-render", "comm");
        }
//service frissitese
        else if(request.getParameter("prefreshid")!=null)
        {
            try{response.setRenderParameter("msg",ic.refreshService(request.getParameter("prefreshid")));}
            catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
            response.setRenderParameter("guse-render", "service");
        }
//service proeprty felvitele es modositasa
        else if((request.getParameter("ppropkey")!=null)&&(request.getParameter("ppropvalue")!=null))
        {
            try
            {
                GuseServiceBean tgsb=(GuseServiceBean)ps.getAttribute("propertyitem",ps.APPLICATION_SCOPE);
                response.setRenderParameter("msg",ic.addServiceProperty(""+tgsb.getId(), "0", request.getParameter("ppropkey"), request.getParameter("ppropvalue")));
            }
            catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
            response.setRenderParameter("guse-render", "service");
        }
//service property torlese
        else if(request.getParameter("dppid")!=null)
        {
            try
            {
                GuseServiceBean tgsb=(GuseServiceBean)ps.getAttribute("propertyitem",ps.APPLICATION_SCOPE);
                response.setRenderParameter("msg",ic.addServiceProperty(""+tgsb.getId(), "0", request.getParameter("dppid"), ""));
            }
            catch(Exception e){response.setRenderParameter("msg","error.gusecom");e.printStackTrace();}
            response.setRenderParameter("guse-render", "service");
        }
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

//session takaritasa az ideiglenes elemektol
        PortletSession ps=request.getPortletSession();
        ps.removeAttribute("serviceitem",ps.APPLICATION_SCOPE);
        ps.removeAttribute("typeitem",ps.APPLICATION_SCOPE);
        ps.removeAttribute("comitem",ps.APPLICATION_SCOPE);
        ps.removeAttribute("comcom",ps.APPLICATION_SCOPE);
        ps.removeAttribute("auth",ps.APPLICATION_SCOPE);
        ps.removeAttribute("propertyitem",ps.APPLICATION_SCOPE);

// information service hivo kliens letrehozasa
        
        ServiceAdminClient ic;
        try{ic=(ServiceAdminClient)InformationBase.getI().getServiceClient("serviceadmin", "portal");}
        catch(Exception e)
        {
                e.printStackTrace();
                return;
        }

// session feltoltese
        try
        {
            ps.setAttribute("styps", ic.getAllServiceTypes(),ps.APPLICATION_SCOPE);
        }
        catch(Exception e){request.setAttribute("msg", "error.gusecom");}

// action uzenet kivezetese a feluletre
        if(request.getParameter("msg")!=null)
        {
            request.setAttribute("msg", request.getParameter("msg"));
        }

// menu parameter beallitasa
        if(request.getParameter("guse-render")==null) request.setAttribute("rendertype","service");
        else request.setAttribute("rendertype", request.getParameter("guse-render"));

// kommunikacios panel megjelenitese
        if("comm".equals(request.getParameter("guse-render")))
        {
            try
            {
                ps.setAttribute("coms", ic.getAllServiceComs(),ps.APPLICATION_SCOPE);
            }
            catch(Exception e){request.setAttribute("msg", "error.gusecom");return;}
//kommunikacios leiro megjelenitese modositasra
            if(request.getParameter("ecid")!=null)
            {
                try{ps.setAttribute("comitem",getServiceCommunicationBean(request.getParameter("ecid"), (List<GuseServiceCommunicationBean>)ps.getAttribute("coms",ps.APPLICATION_SCOPE)),ps.APPLICATION_SCOPE);}
                catch(Exception e){request.setAttribute("msg", "error.input");}
            }

            if(request.getParameter("scid")!=null)
            {
                try{ps.setAttribute("comcom",getServiceCommunicationBean(request.getParameter("scid"), (List<GuseServiceCommunicationBean>)ps.getAttribute("coms",ps.APPLICATION_SCOPE)),ps.APPLICATION_SCOPE);}
                catch(Exception e){e.printStackTrace();request.setAttribute("msg", "error.input");}
                try{request.setAttribute("typs", ic.getAllServiceTypes());}
                catch(Exception e){e.printStackTrace();request.setAttribute("msg", "error.gusecom");}
            }
        }

// service type panem megjelenitese
        else if("type".equals(request.getParameter("guse-render")))
        {

            try{request.setAttribute("styps", ic.getAllServiceTypes());}
            catch(Exception e){request.setAttribute("msg", "error.gusecom");}

// service tipus szerkesztes megjelenitese
            if(request.getParameter("etid")!=null)
            {
                try{ps.setAttribute("typeitem",getServiceTypeBean(request.getParameter("etid"), (List<GuseServiceTypeBean>)request.getAttribute("styps")),ps.APPLICATION_SCOPE);}
                catch(Exception e){request.setAttribute("msg", "error.input");}
            }
        }
// service property import
        else if("import".equals(request.getParameter("guse-render")))
        {
            if(request.getParameter("psrc")!=null)
            {
                try{
                    request.setAttribute("msg", ic.importServiceProperties(request.getParameter("pdst"), request.getParameter("psrc")));
                    System.out.println(request.getParameter("pdst")+"--"+request.getParameter("psrc"));
                }
                catch(Exception e){request.setAttribute("msg", "error.gusecom");e.printStackTrace();}
            }
        }
// service deffinicios panel megjelenitese
        else
        {
            try
            {
                ps.setAttribute("services", ic.getAllServices(),ps.APPLICATION_SCOPE);
                ps.setAttribute("coms", ic.getAllServiceComs(),ps.APPLICATION_SCOPE);
                ps.setAttribute("styps", ic.getAllServiceTypes(),ps.APPLICATION_SCOPE);
            }
            catch(Exception e){request.setAttribute("msg", "error.gusecom");}
// service modositas megjelenitese
            if(request.getParameter("esid")!=null)
            {
                try{ps.setAttribute("serviceitem",getServiceBean(request.getParameter("esid"), (List<GuseServiceBean>)ps.getAttribute("services",ps.APPLICATION_SCOPE)),ps.APPLICATION_SCOPE);}
                catch(Exception e){request.setAttribute("msg", "error.input");}
            }
// service propertik megjelenitese
            if(request.getParameter("prsid")!=null)
            {
                try
                {
                    GuseServiceBean tgsb=getServiceBean(request.getParameter("prsid"), (List<GuseServiceBean>)ps.getAttribute("services",ps.APPLICATION_SCOPE));
                    ps.setAttribute("propertyitem",tgsb,ps.APPLICATION_SCOPE);
                    try
                    {
                        InformationClient ipc=(InformationClient)InformationBase.getI().getServiceClient("information", "portal");
                        request.setAttribute("props", ipc.getAllProperties(tgsb.getUrl()));
                    }
                    catch(Exception e){e.printStackTrace();}

                }
                catch(Exception e){request.setAttribute("msg", "error.input");}
            }
//service user hozzaferes megjelenitese
            if(request.getParameter("ssid")!=null)
            {
                try{ps.setAttribute("auth",getServiceBean(request.getParameter("ssid"), (List<GuseServiceBean>)ps.getAttribute("services",ps.APPLICATION_SCOPE)),ps.APPLICATION_SCOPE);}
                catch(Exception e){request.setAttribute("msg", "error.input");}
            }
        }
        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/guseserviceadmin/view.jsp").include(request, response);


    }
    @Override
    public void doHelp(RenderRequest request, RenderResponse response) throws PortletException,IOException
    {
        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/guseserviceadmin/help.jsp").include(request, response);

    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        try{
            ServiceAdminClient sac=(ServiceAdminClient)InformationBase.getI().getServiceClient("serviceadmin", "portal");
            String xml=sac.export();
            response.setContentType("application/xml");
            response.setProperty("Content-Disposition", "inline; filename=\"gUSEcongiration.xml\"");
            response.getWriter().write(xml);
        }
        catch(Exception e){e.printStackTrace();}
    }


/**
 * GuseService kereses a service listaban ID alapjan
 * @param pID ID
 * @param pList service lista
 * @return kivalasztott service
 * @throws java.lang.Exception ha a service nem talahato, ha nem megfelelo az ID
 */
    private GuseServiceBean getServiceBean(String pID,List<GuseServiceBean> pList) throws Exception
    {
        long tmp=Long.parseLong(pID);
        for(GuseServiceBean t:pList)
            if(t.getId()==tmp) return t;
        throw new NullPointerException(pID+" not element servicelist");
    }

/**
 * GuseServiceCommunication kereses a kommunikacios listaban ID alapjan
 * @param pID ID
 * @param pList communikacios lista
 * @return kivalasztott service
 * @throws java.lang.Exception ha a service nem talahato, ha nem megfelelo az ID
 */
    private GuseServiceCommunicationBean getServiceCommunicationBean(String pID,List<GuseServiceCommunicationBean> pList) throws Exception
    {
        long tmp=Long.parseLong(pID);
        for(GuseServiceCommunicationBean t:pList)
            if(t.getId()==tmp) return t;
        throw new NullPointerException(pID+" not element in comlist");
    }
/**
 * GuseServiceCommunication kereses a kommunikacios listaban ID alapjan
 * @param pID ID
 * @param pList communikacios lista
 * @return kivalasztott service
 * @throws java.lang.Exception ha a service nem talahato, ha nem megfelelo az ID
 */
    private GuseServiceTypeBean getServiceTypeBean(String pID,List<GuseServiceTypeBean> pList) throws Exception
    {
        long tmp=Long.parseLong(pID);
        for(GuseServiceTypeBean t:pList)
            if(t.getId()==tmp) return t;
        throw new NullPointerException(pID+" not element in typelist");
    }

}
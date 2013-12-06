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
 * Servlet for receiving configuration events. 
 */

package hu.sztaki.lpds.dcibridge.config;

import dci.data.Certificate;
import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.LB;
import hu.sztaki.lpds.submitter.grids.Middleware;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author krisztian karoczkai
 */
public class ConfigServlet extends HttpServlet {
        private static final String[] BINDING_INPUTS_BOINC= new String[]{"pboincname","pboincid","pwsdl"};
        private static final String[] BINDING_INPUTS_GLITE= new String[]{"pglitename","paccessdata","ptype","plfc"};
        private static final String[] BINDING_INPUTS_GT2= new String[]{"pgt2name","paccessdata","ptype"};
        private static final String[] BINDING_INPUTS_GT4= new String[]{"pgt4name","paccessdata","ptype"};
        private static final String[] BINDING_INPUTS_GELMCA= new String[]{"pgelmcaname"};
        private static final String[] BINDING_INPUTS_UNICORE= new String[]{"punicorename"};
        private static final String[] BINDING_INPUTS_ARC= new String[]{"parcname","pconfig","ptype","paccessdata"};
        private static final String[] BINDING_INPUTS_LOCAL= new String[]{"plocalname"};
        private static final String[] BINDING_INPUTS_LSF= new String[]{"plsfname"};
        private static final String[] BINDING_INPUTS_PBS= new String[]{"ppbsname"};
        private static final String[] BINDING_INPUTS_GAE= new String[]{"pgaename"};
        private static final String[] BINDING_INPUTS_SERVICE= new String[]{"pservicename"};
        private static final String[] BINDING_INPUTS_GBAC= new String[]{"pboincname","pboincid","pwsdl","pxml"};
		private static final String[] BINDING_INPUTS_EDGI = new String[]{"pedginame","pedgiurl"};
        private static final String[] BINDING_INPUTS_CLOUDBROKER = new String[]{"pcloudbrokername","pcloudbrokerurl", "pcloudbrokeruser", "pcloudbrokerpassword"};

        private static final String[] actions=new String[]{"new","edit","info","general","logg"};
        private static final String[] systemactions=new String[]{"manager","properties","logg"};

    @Override
    public void init() throws ServletException {
        super.init();
        File f=new File(getServletContext().getRealPath("/WEB-INF/dci-bridge.xml"));
        if(Conf.getC()==null) Conf.init(f);
    }


    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        Enumeration<String> enm=request.getParameterNames();
        String key;
        System.out.println("*************************");
        while(enm.hasMoreElements()){
            key=enm.nextElement();
            System.out.println("-"+key+":"+request.getParameter(key));
        }

        response.setContentType("text/html;charset=UTF-8");
        if(request.getParameter("css")!=null)
            request.getSession().setAttribute("css", request.getParameter("css"));
/* config error*/
        request.getSession().removeAttribute("error");
        request.getSession().setAttribute("error",new ArrayList<String>());
/* config data not set*/
        request.getSession().removeAttribute("notset");
        request.getSession().setAttribute("notset",new ArrayList<String>());
/* suported middlewars*/
        request.setAttribute("dcis", Base.dcis);
/* suported actions*/
        request.setAttribute("menu", actions);
        request.setAttribute("dmenu", systemactions);
/* DCI-Bridge status*/
        request.setAttribute("status", Base.getI().isStatus());
/* all config data*/
        request.setAttribute("conf", Conf.getM());
        request.setAttribute("config", Conf.getC());
/* store actual main menu item*/
        if(request.getParameter("menu")!=null){
            request.getSession().setAttribute("menu", request.getParameter("menu"));
            request.getSession().removeAttribute("sub");
        }
/* store actual submenu item*/
        if(request.getParameter("t")!=null)
            request.getSession().setAttribute("sub", request.getParameter("t"));

//        System.out.println("Session-sub:"+request.getSession().getAttribute("sub"));
//        System.out.println("Session-menu:"+request.getSession().getAttribute("menu"));
        
        if(actions[0].equals(request.getSession().getAttribute("sub"))) newAction(request);
        if(actions[1].equals(request.getSession().getAttribute("sub"))){
            editData(request);
            if(request.getParameter("editing")!=null) editActions(request);
        }
        if(actions[2].equals(request.getSession().getAttribute("sub"))) monitoringData(request);

        if(actions[3].equals(request.getSession().getAttribute("sub"))) {
            if(isParams(request,new String[]{"pclass","pthreads","penabled"} ))generalData(request);
            else{
                String selectedMMI=(String)request.getSession().getAttribute("menu");
                request.setAttribute("data",Conf.getMiddlewareNewInstanceIfNotExist(selectedMMI));            
                request.setAttribute("certs",Certificate.values());
            }
        }

        if(actions[4].equals(request.getSession().getAttribute("sub"))) {
            String selectedMMI=(String)request.getSession().getAttribute("menu");
            request.setAttribute("loggs",Base.getI().getLogg(selectedMMI));
        }

        if(systemactions[0].equals(request.getSession().getAttribute("sub"))&& request.getParameter("status")!=null){
            String status=request.getParameter("status");
            Base.writeLogg(Base.MIDDLEWARE_SYSTEM,new LB("Service status:"+status));
            Base.getI().setStatus("1".equals(status));
        }

        if(systemactions[1].equals(request.getSession().getAttribute("sub"))){
            String workdir=request.getParameter("workdir");
            Conf.getC().getSystem().setPath(workdir);
            Base.getI().initWorkDir();

            String metabroker=request.getParameter("metabroker");
            Conf.getC().getSystem().setMetabroker(metabroker);
            try{Base.getI().getDWLThread().createMBClient();}
            catch(Exception e){e.printStackTrace();}

            try{Conf.write();}
            catch(Exception e){e.printStackTrace();}
        }

//        System.out.println("notset:"+((ArrayList<String>)request.getSession().getAttribute("notset")).size());
//        System.out.println("error:"+((ArrayList<String>)request.getSession().getAttribute("error")).size());
        getServletContext().getRequestDispatcher("/WEB-INF/jsps/index.jsp").include(request, response);
    }

    private void generalData(HttpServletRequest request){
        String middleware=(String)request.getSession().getAttribute("menu");
        try{
            dci.data.Middleware tmp=Conf.getMiddlewareNewInstanceIfNotExist(middleware);
            tmp.setPlugin(request.getParameter("pclass"));
            tmp.setThreads(Short.parseShort(request.getParameter("pthreads")));
            tmp.setEnabled(request.getParameter("penabled").equals("1"));
            tmp.getCertificate().clear();
            Enumeration<String> enm=request.getParameterNames();
            String key;
            while(enm.hasMoreElements()){
                key=enm.nextElement();
                if(key.startsWith("pcert")){
                    tmp.getCertificate().add(Certificate.valueOf(request.getParameter(key)));
                }
            }
            Conf.write();
            Base.getI().initMiddleware(tmp);
        }
        catch(Exception e){Base.writeLogg(Base.MIDDLEWARE_SYSTEM, new LB(e));}
    }
/**
 * Data providing for monitoring side 
 * @param request
 */
    private void monitoringData(HttpServletRequest request){
        String middleware=(String)request.getSession().getAttribute("menu");
        try{
            List<Middleware> tmp=Base.getI().getMiddlewares(middleware);
            request.setAttribute("queues", tmp);

            String selectedQueue=request.getParameter("pqueue");
            if(selectedQueue!=null){
                for(Middleware t:tmp)
                    if(selectedQueue.equals(t.getName()))
                        request.setAttribute("queue_data", t.getJobs());
            }
        }
        catch(Exception e){}
    }

/**
 * Implementation of new actions getting up 
 * @param request http request
 */
     private void newAction(HttpServletRequest request){
        boolean modify=false;
        if(isParams(request, BINDING_INPUTS_GBAC)) modify=ActionsUtil.newGbac(request);
        else if(isParams(request, BINDING_INPUTS_BOINC)) modify=ActionsUtil.newBoinc(request);
        else if(isParams(request, BINDING_INPUTS_GLITE)) modify=ActionsUtil.newGlite(request);
        else if(isParams(request, BINDING_INPUTS_GT2)) modify=ActionsUtil.newGt2(request);
        else if(isParams(request, BINDING_INPUTS_GT4)) modify=ActionsUtil.newGt4(request);
        else if(isParams(request, BINDING_INPUTS_GELMCA)) modify=ActionsUtil.newGelmca(request);
        else if(isParams(request, BINDING_INPUTS_UNICORE)) modify=ActionsUtil.newUnicore(request);
        else if(isParams(request, BINDING_INPUTS_ARC)) modify=ActionsUtil.newArc(request);
        else if(isParams(request, BINDING_INPUTS_LOCAL)) modify=ActionsUtil.newLocal(request);
        else if(isParams(request, BINDING_INPUTS_LSF)) modify=ActionsUtil.newLsf(request);
        else if(isParams(request, BINDING_INPUTS_PBS)) modify=ActionsUtil.newPbs(request);
        else if(isParams(request, BINDING_INPUTS_GAE)) modify=ActionsUtil.newGae(request);
        else if(isParams(request, BINDING_INPUTS_SERVICE)) modify=ActionsUtil.newService(request);
        else if(isParams(request, BINDING_INPUTS_EDGI)) modify=ActionsUtil.newEdgi(request);
        else if(isParams(request, BINDING_INPUTS_CLOUDBROKER)) modify=ActionsUtil.newCloudBroker(request);
        
		if(modify){
            String middleware=(String)request.getSession().getAttribute("menu");
            try{
                Conf.write();
                if(Conf.getMiddleware(middleware).isEnabled())
                    if(Base.getI().getMiddlewares(middleware).size()==0)
                        Base.getI().createNewMiddlewarePlugininstance(middleware, Conf.getMiddleware(middleware).getPlugin());
                    Base.getI().getMiddlewares(middleware).get(0).setConfiguration();
                    Base.writeLogg(middleware,new LB(middleware+" plugin configured"));

            }
            catch(Exception e){Base.writeLogg(middleware,new LB(e));}
// configuration update
        }
    }
/**
 * Change actions handling
 * @param request http request data
*/
    private void editActions(HttpServletRequest request){
        String middleware=(String)request.getSession().getAttribute("menu");

        if(Base.MIDDLEWARE_PBS.equals(middleware)) {
            if(isParams(request, BINDING_INPUTS_PBS))
                ActionsUtil.editPbs(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_LSF.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_LSF))
                ActionsUtil.editLsf(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_GLITE.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_GLITE))
                ActionsUtil.editGlite(request);
            else notSetParameter(request, "notset.data.any");
        }

        else if(Base.MIDDLEWARE_GT2.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_GT2))
                ActionsUtil.editGt2(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_GT4.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_GT4))
                ActionsUtil.editGt4(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_ARC.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_ARC))
                ActionsUtil.editArc(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_BOINC.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_BOINC))
                ActionsUtil.editBoinc(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_UNICORE.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_UNICORE))
                ActionsUtil.editUnicore(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_LOCAL.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_LOCAL))
                ActionsUtil.editLocal(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_GEMLCA.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_GELMCA))
                ActionsUtil.editGelmca(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_SERVICE.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_SERVICE))
                ActionsUtil.editService(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_GBAC.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_GBAC))
                ActionsUtil.editGbac(request);
            else notSetParameter(request, "notset.data.any");
        }
        else if(Base.MIDDLEWARE_GAE.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_GAE));
                //ActionsUtil.editG(request);
            else notSetParameter(request, "notset.data.any");
        }
    else if (Base.MIDDLEWARE_EDGI.equals(middleware)){
            if(isParams(request, BINDING_INPUTS_EDGI))
                ActionsUtil.editEdgi(request);
            else notSetParameter(request, "notset.data.any");
        }

		try{Conf.write();}
        catch(Exception e){e.printStackTrace(); /*@ToDo web error message */}
}

/**
 * Edit forms data
 * @param request http request data
 */
    private void editData(HttpServletRequest request){
        String selectedMMI=(String)request.getSession().getAttribute("menu");
        try{
            request.setAttribute("data",Conf.getMiddleware(selectedMMI).getItem());
//            System.out.println("size:"+((List)request.getAttribute("data")).size());
        }
        catch(Exception e){/*not configured middleware*/}
        String selectedC=request.getParameter("list");

            try{
                if(selectedC!=null)
                    request.setAttribute("item",Conf.getItem(selectedMMI, selectedC));
            }
            catch(NullPointerException e){e.printStackTrace();}

    }



/**
 * Qeury if query request parameters are exist
 * @param request request
 * @param params keresett parameter list
 * @return true, if all requested elements are exist among request parameters
 */
    private boolean isParams(HttpServletRequest request, String[] params){
        boolean res=true;
        for(String t:params)
            res=res&& request.getParameter(t)!=null && !"".equals(request.getParameter(t));
        return res;
    }

    private void notSetParameter(HttpServletRequest request, String pKey){
        ((ArrayList<String>)request.getSession().getAttribute("notset")).add(pKey);
    }

    private void errorParameter(HttpServletRequest request, String pKey){
        ((ArrayList<String>)request.getSession().getAttribute("error")).add(pKey);
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

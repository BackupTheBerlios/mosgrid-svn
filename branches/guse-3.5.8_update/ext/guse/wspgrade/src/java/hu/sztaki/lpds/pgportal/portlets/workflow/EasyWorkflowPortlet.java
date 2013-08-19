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
 * EasyWorkflowPortlet.java
 */
package hu.sztaki.lpds.pgportal.portlets.workflow;

import hu.sztaki.lpds.storage.com.UploadWorkflowBean;
import hu.sztaki.lpds.pgportal.service.base.GemlcaCacheService;
import hu.sztaki.lpds.pgportal.com.*;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.*;
import hu.sztaki.lpds.pgportal.service.workflow.RealWorkflowUtils;
import hu.sztaki.lpds.pgportal.service.workflow.UserQuotaUtils;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.dcibridge.client.ResourceConfigurationFace;
import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.pgportal.service.workflow.Sorter;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.ActionHandler;
import hu.sztaki.lpds.pgportal.util.resource.SshResourceService;
import hu.sztaki.lpds.pgportal.util.stream.HttpDownload;
import hu.sztaki.lpds.portal.util.stream.FileUploadProgressListener;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;
import hu.sztaki.lpds.wfs.com.WorkflowConfigErrorBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import javax.portlet.*;
import java.io.*;
import java.util.*;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.io.FilenameUtils;
import dci.data.Middleware;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author krisztian karoczkai
 */
public class EasyWorkflowPortlet extends GenericWSPgradePortlet {

    public EasyWorkflowPortlet() {
    }
    private long uploadMaxSize = 0;
    private String mainjsp = "/jsp/workflow/easyworkflowlist.jsp";
    private String einstance = "einstance";
    private PortletContext pContext;
    final static String GEMLCA ="gemlca";
    final static String EDGI = "edgi";

/**
 * Portlet initializing
 */
    @Override
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        pContext = config.getPortletContext();
        String m = config.getInitParameter("fileupload_upload_maxsize");
        try {uploadMaxSize = Integer.parseInt(m) * 1048576;}
        catch (NumberFormatException nfe)
        {
            uploadMaxSize = 10485760;
            getPortletContext().log("[RealWorkflowPortlet] - failed to read in filetransfer_upload_maxsize, set to 10MB");
        }
    }

    /**
     * Data transmision to vizualize Portlet UI
     */
    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException,IOException {
        response.setContentType("text/html");
        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);
            return;
        }
        openRequestAttribute(request);
       // WSPgradeLogger.viewStart(request.getRemoteUser(), this.getClass().getName());
        try {
            //if("main".equals(request.getParameter("render"))) jsp=mainjsp;
            if(request.getAttribute("jsp")==null) request.setAttribute("jsp",mainjsp);

            if (request.getAttribute("jsp").equals(mainjsp)) {
                ConcurrentHashMap wfmainhsh = new ConcurrentHashMap();
                ConcurrentHashMap wfhsh = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflows();
                Enumeration keys = wfhsh.keys();
                while (keys.hasMoreElements()) {
                    String wf = keys.nextElement().toString();
                    WorkflowData wData = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(wf);
                    if (wData.isAppMain()) {
                        //System.out.println("main found :" + wf);
                        wfmainhsh.put(wf, wfhsh.get(wf));
                    }
                }

                request.setAttribute("appWorkflowList", Sorter.getInstance().sortFromValues(wfmainhsh));//wfmainhsh
            }
            request.setAttribute("userID", request.getRemoteUser());
            request.setAttribute("portalID", PropertyLoader.getInstance().getProperty("service.url"));
            PortletRequestDispatcher dispatcher = null;
            dispatcher = pContext.getRequestDispatcher((String)request.getAttribute("jsp"));
            dispatcher.include(request, response);
        } catch (IOException e) {
            throw new PortletException("JSPPortlet.doView exception", e);
        }
        cleanRequestAttribute(request.getPortletSession());
//        action = "";
//        WSPgradeLogger.viewStop(request.getRemoteUser(), this.getClass().getName());
    }


    /**
     * Displaying Details 
     */
    public void doDetails(ActionRequest request, ActionResponse response) throws PortletException {
        String workflow = "" + request.getParameter("workflow");
        request.setAttribute("workflow", PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow));
        request.setAttribute("jsp","/jsp/workflow/easywrkinst.jsp");
    }


    /**
     * Displaying Enduser Workflow configuration interface
     */
    public void doConfigure(ActionRequest request, ActionResponse response) throws PortletException {
// Querying logged user
        String userID;
        if (request.getParameter("adminuser") == null) {
            userID = request.getRemoteUser();
        } else {
            userID = request.getParameter("adminuser");
        }

//Over the Quota
        if (UserQuotaUtils.getInstance().userQuotaIsFull(userID)) {
            request.setAttribute("msg", "portal.RealWorkflowPortlet.quotaisoverfull");
        } else {
            if (request.getParameter("workflow") != null) {
                request.getPortletSession().setAttribute("cworkflow", request.getParameter("workflow"));
            }
            String workflow = request.getParameter("workflow");
            request.setAttribute("graphs", PortalCacheService.getInstance().getUser(userID).getAbstactWorkflows());
            request.setAttribute("templates", PortalCacheService.getInstance().getUser(userID).getTemplateWorkflows());

//Session query
            PortletSession ps = request.getPortletSession();

            //Available resource configuration query
            try {
                if (ps.getAttribute("resources", ps.APPLICATION_SCOPE) == null) {
                    ResourceConfigurationFace rc = (ResourceConfigurationFace) InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                    List<Middleware> tmp_r = rc.get();
                    ps.setAttribute("resources", tmp_r, ps.APPLICATION_SCOPE);
                    ps.setAttribute("pub_resources", tmp_r, ps.APPLICATION_SCOPE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                ps.setAttribute("findwf", new Hashtable());//findinWF = new Hashtable();
                Vector eparam = getEConfParam(userID, request.getParameter("workflow"), ps);//easy parameterek                
                ps.removeAttribute("findwf");//findinWF = new Hashtable();

//                System.out.println("EASY PARAMS: ");
//                for (int i = 0; i < eparam.size(); i++) {
//                    System.out.println("param: " + i + " [" + eparam.get(i) + "]");
//                }

                request.setAttribute("easyParams", eparam);
                request.setAttribute("easyParamssize", eparam.size());
                PortalCacheService.getInstance().getUser(userID).setConfiguringEParams(eparam);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("easyParams", new Vector());
                request.setAttribute("easyParamssize", 0);
                request.setAttribute("msg", e.getMessage());
            }

            request.setAttribute("storageID", PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow).getStorageID());
            request.setAttribute("userID", userID);
            request.setAttribute("portalID", PropertyLoader.getInstance().getProperty("service.url"));
            request.setAttribute("wrkdata", PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow));

            request.setAttribute("grafs", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(userID).getAbstactWorkflows()));
            request.setAttribute("awkfs", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(userID).getTemplateWorkflows()));
            // set configure ID
            String confID = userID + String.valueOf(System.currentTimeMillis());
            request.setAttribute("confID", confID);
//            System.out.println("confID : " + confID);

            request.setAttribute("jsp","/jsp/workflow/easyconfigure.jsp");
        }
    }

    private Vector getEConfParam(String pUser, String pWorkflow,PortletSession ps) throws Exception {
        if (((Hashtable)ps.getAttribute("findwf")).containsKey(pWorkflow)) {//findinWF.get(pWorkflow) != null
            //System.out.println("during processing:"+pWorkflow);
            return new Vector();
        }
        ((Hashtable)ps.getAttribute("findwf")).put(pWorkflow, "true");
        //System.out.println("WFs during processing:"+findinWF);
        Vector eparam = new Vector();//end user parameters
        Hashtable hsh = new Hashtable();
        hsh.put("url", PortalCacheService.getInstance().getUser(pUser).getWorkflow(pWorkflow).getWfsID());
        ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
        // try
        // {
        PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
        pc.setServiceURL(st.getServiceUrl());
        pc.setServiceID(st.getServiceID());
        ComDataBean tmp = new ComDataBean();
        tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
        tmp.setUserID(pUser);
        tmp.setWorkflowID(pWorkflow);
        Vector wfconfigdt = pc.getWorkflowConfigData(tmp);

        for (int i = 0; i < wfconfigdt.size(); i++) {
            // replace special characters...
            String jobtxt = new String(((JobPropertyBean) wfconfigdt.get(i)).getTxt());
            ((JobPropertyBean) wfconfigdt.get(i)).setTxt(replaceTextS(jobtxt));

            JobPropertyBean jobprop = (JobPropertyBean) wfconfigdt.get(i);
            
            String gridtype=""+jobprop.getExe().get("gridtype");
            if (GEMLCA.equals(""+jobprop.getExe().get("gridtype"))) {//GLC lista frissites + hozzaferes ellenorzes
                Vector v = GemlcaCacheService.getInstance().getGLCList("" + PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "/users/" + pUser + "/x509up." + jobprop.getExe().get("grid"), "" + jobprop.getExe().get("grid"), 0, 0);                
            }

            // input
            for (int j = 0; j < ((JobPropertyBean) wfconfigdt.get(i)).getInputs().size(); j++) {
                PortDataBean ptmp = (PortDataBean) ((JobPropertyBean) wfconfigdt.get(i)).getInputs().get(j);
                // replace special characters...
                ptmp.setTxt(replaceTextS(ptmp.getTxt()));
                /*   System.out.println(j+" job input port getName "+ ptmp.getName());
                System.out.println(" job input port getLabel "+ ptmp.getLabel());//ha nem ures v. nem null
                System.out.println(" job input port getData "+ ptmp.getData());
                System.out.println(" job input port getDataDisabled "+ ptmp.getDataDisabled());
                System.out.println(" job input port getDesc "+ ptmp.getDesc());*/

                Set keys = ptmp.getLabel().keySet();
                Iterator it = keys.iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String label = (String) ptmp.getLabel().get(key);
                    String inh = (String) ptmp.getInherited().get(key);
                    if (!(label.equals("") || label.equals("null")) && (inh.equals("---") || inh.equals("null"))) {
                        Hashtable ph = new Hashtable();
                        ph.put("wfID", pWorkflow);
                        ph.put("jobID", "" + ((JobPropertyBean) wfconfigdt.get(i)).getId());
                        ph.put("jobName", "" + ((JobPropertyBean) wfconfigdt.get(i)).getName());
                        ph.put("type", "iport");
                        ph.put("typeID", "" + ptmp.getId());
                        ph.put("name", key);
                        ph.put("value", "" + ptmp.getData().get(key));
                        ph.put("label", label);
                        ph.put("desc", "" + ptmp.getDesc().get(key));
                        ph.put("gridtype", gridtype);
                        if (key.equals("file")) {
                            try {
                                ph.put("storageID", PortalCacheService.getInstance().getUser(pUser).getWorkflow(pWorkflow).getStorageID());
                                ph.put("userID", pUser);
                                ph.put("portalID", PropertyLoader.getInstance().getProperty("service.url"));
                                ph.put("inputID", "" + ptmp.getSeq());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        eparam.add(ph);
                    }
                }

            }
            // outputs
            for (int jo = 0; jo < ((JobPropertyBean) wfconfigdt.get(i)).getOutputs().size(); jo++) {
                PortDataBean ptmpo = (PortDataBean) ((JobPropertyBean) wfconfigdt.get(i)).getOutputs().get(jo);
                // replace special characters...
                ptmpo.setTxt(replaceTextS(ptmpo.getTxt()));

                Set keys = ptmpo.getLabel().keySet();
                Iterator it = keys.iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String label = (String) ptmpo.getLabel().get(key);
                    String inh = (String) ptmpo.getInherited().get(key);
                    if (!(label.equals("") || label.equals("null")) && (inh.equals("---") || inh.equals("null"))) {
                        Hashtable ph = new Hashtable();
                        ph.put("wfID", pWorkflow);
                        ph.put("jobID", "" + ((JobPropertyBean) wfconfigdt.get(i)).getId());
                        ph.put("jobName", "" + ((JobPropertyBean) wfconfigdt.get(i)).getName());
                        ph.put("type", "oport");
                        ph.put("typeID", "" + ptmpo.getId());
                        ph.put("name", key);
                        ph.put("value", "" + ptmpo.getData().get(key));
                        ph.put("label", label);
                        ph.put("desc", "" + ptmpo.getDesc().get(key));
                        ph.put("gridtype", gridtype);
                        eparam.add(ph);
                    }
                }
            }
            //JobPropertyBean jobprop = (JobPropertyBean) wfconfigdt.get(i);
            /*   System.out.println(" job getName "+ jobprop.getName());
            System.out.println(" job getTxt "+ jobprop.getTxt());
            System.out.println(" job getDesc "+ jobprop.getDesc());
            System.out.println(" job getDesc0 "+ jobprop.getDesc0());
            System.out.println(" job getExe "+ jobprop.getExe());
            System.out.println(" job getExeDisabled "+ jobprop.getExeDisabled());
            System.out.println(" job getId "+ jobprop.getId());
            System.out.println(" job getInherited "+ jobprop.getInherited());
            System.out.println(" job getLabel "+ jobprop.getLabel());*/

            if (((String) jobprop.getExe().get("jobistype")).equals("workflow")) {//jobistype=workflow // ha beagyazott
                System.out.println(" ----embedded job start - getName:" + jobprop.getName());
                Vector ep = getEConfParam(pUser, (String) jobprop.getExe().get("iworkflow"), ps);
                if (ep != null) {
                    for (int ie = 0; ie < ep.size(); ie++) {
                        eparam.add(ep.get(ie));
                    }
                }
                System.out.println(" ----embedded job end  - job getName:" + jobprop.getName());
            } else {
                Set keys = jobprop.getLabel().keySet();
                Iterator it = keys.iterator();
                int isGridtype = -1;
                int isGrid = -1;
                int isResource = -1;
                int isjobmanager = -1;
                HashMap resconf = new HashMap();

                while (it.hasNext()) {
                    String key = (String) it.next();
                    String label = (String) jobprop.getLabel().get(key);
                    String inh = (String) jobprop.getInherited().get(key);
                    if (!(label.equals("") || label.equals("null")) && (inh.equals("---") || inh.equals("null"))) {
                        Hashtable ph = new Hashtable();
                        ph.put("wfID", pWorkflow);
                        ph.put("jobID", "" + ((JobPropertyBean) wfconfigdt.get(i)).getId());
                        ph.put("jobName", "" + ((JobPropertyBean) wfconfigdt.get(i)).getName());
                        ph.put("type", "exe");
                        ph.put("typeID", "" + jobprop.getId());
                        ph.put("name", key);
                        ph.put("value", "" + jobprop.getExe().get(key));
                        ph.put("label", label);
                        ph.put("desc", "" + jobprop.getDesc0().get(key));
                        ph.put("gridtype", gridtype);

                        if (key.equals("binary")) {
                            try {
                                ph.put("storageID", PortalCacheService.getInstance().getUser(pUser).getWorkflow(pWorkflow).getStorageID());
                                ph.put("userID", pUser);
                                ph.put("portalID", PropertyLoader.getInstance().getProperty("service.url"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (key.equals("gridtype")) {
                            if (!jobprop.getExe().get("gridtype").equals(EDGI)) {//filter out edgi config
                                Hashtable vGridTypes = new Hashtable();
                                Vector t = ConfigHandler.getGridMidlewares((List<Middleware>) ps.getAttribute("resources", ps.APPLICATION_SCOPE));
                                for (int gt = 0; gt < t.size(); gt++) {
                                    if (!(t.get(gt).equals(GEMLCA) || t.get(gt).equals(EDGI))) {//filter out GEMLCA and EDGI
                                        vGridTypes.put("" + t.get(gt), "0");
                                    }
                                }
                                ph.put("data", vGridTypes);
                                resconf.put("gridtype", ph);
                            }
                            ph = null;
                        } else if (key.equals("grid")) {
                            try {                        
                                if (!jobprop.getExe().get("gridtype").equals(EDGI)) {//filter out edgi config
                                    ph.put("data", ConfigHandler.getGroups((List<Middleware>) ps.getAttribute("resources", ps.APPLICATION_SCOPE), (String) jobprop.getExe().get("gridtype")));
                                    resconf.put("grid", ph);
                                }
                                ph = null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (key.equals("resource")) {
                            try {
                                ph.put("data", ConfigHandler.getResources((List<Middleware>) ps.getAttribute("resources", ps.APPLICATION_SCOPE), (String) jobprop.getExe().get("gridtype"), (String) jobprop.getExe().get("grid")));
                            } catch (Exception e0) {
                                ph.put("data", new Vector());
                            }
                            
                            if (!jobprop.getExe().get("gridtype").equals(EDGI)) {//filter out edgi config
                                resconf.put("resource", ph);
                            }                                                        
                            ph = null;
                        } else if (key.equals("jobmanager")) {
                            if (jobprop.getExe().get("gridtype").equals(GEMLCA)) {
                                try {
                                    //gemlcaquery
                                    Vector v = GemlcaCacheService.getInstance().getGLCList("" + PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "/users/" + pUser + "/x509up." + jobprop.getExe().get("grid"), "" + jobprop.getExe().get("grid"), 0, 0);
                                    //gemlca sites
                                    Vector vgsites = GemlcaCacheService.getInstance().getGLCsites("" + jobprop.getExe().get("grid"), "" + jobprop.getExe().get("resource"));
                                    ph.put("data", vgsites);
                                } catch (Exception e0) {
                                    ph.put("data", new Vector());
                                }
                            } else {
                                try {
                                    if (SshResourceService.getI().isGroupforSshKey(ps, ""+jobprop.getExe().get("gridtype"))) {
                                        ph.put("data", SshResourceService.getI().getUserforHost(pUser, ""+jobprop.getExe().get("grid")) );
                                    } else {
                                        ph.put("data", ConfigHandler.getServices((List<Middleware>) ps.getAttribute("resources", ps.APPLICATION_SCOPE), (String) jobprop.getExe().get("gridtype"), (String) jobprop.getExe().get("grid"), (String) jobprop.getExe().get("resource")));
                                    }
                                } catch (Exception e0) {
                                    ph.put("data", new Vector());
                                }
                            }                            
                            
                            if (!jobprop.getExe().get("gridtype").equals(EDGI)) {//filter out edgi config
                                resconf.put("jobmanager", ph);
                            }
                            ph = null;
                        } else if ((key.equals("params")) && (jobprop.getExe().get("gridtype").equals(GEMLCA))) {
                            // gemlca parameterek
                            Vector vgparams = GemlcaCacheService.getInstance().getGLCparams("" + jobprop.getExe().get("grid"), "" + jobprop.getExe().get("resource"));
                            String ggparams = "";
                            if (ph.get("value") == null) {// default values
                                for (int x = 0; x < vgparams.size(); x++) {
                                    ((HashMap) vgparams.get(x)).put("svalue", "" + ((HashMap) vgparams.get(x)).get("value"));
                                    ((HashMap) vgparams.get(x)).put("nbr", "" + x);
                                    ggparams += ((HashMap) vgparams.get(x)).get("value") + " ";
                                }
                                ph.put("value", ggparams.trim());
                            } else {
                                String[] gsparams = ((String) ph.get("value")).split(" ");
                                if (gsparams.length == vgparams.size()) {
                                    for (int x = 0; x < vgparams.size(); x++) {
                                        ((HashMap) vgparams.get(x)).put("svalue", "" + gsparams[x]);
                                        ((HashMap) vgparams.get(x)).put("nbr", "" + x);
                                    }
                                } else {// default values
                                    for (int x = 0; x < vgparams.size(); x++) {
                                        ((HashMap) vgparams.get(x)).put("svalue", "" + ((HashMap) vgparams.get(x)).get("value"));
                                        ((HashMap) vgparams.get(x)).put("nbr", "" + x);
                                        ggparams += ((HashMap) vgparams.get(x)).get("value") + " ";
                                    }
                                    ph.put("value", ggparams.trim());
                                }
                            }
                            ph.put("gparams", vgparams);
                        }

                        //filter out edgi config
                        if (jobprop.getExe().get("gridtype").equals(EDGI)) {
                        }
                        
                        if (ph != null) {
                            eparam.add(ph);
                        }
                    }
                }

                if (resconf.size()>0){// sort 
                    if (resconf.containsKey("gridtype")){
                        isGridtype=eparam.size();
                        eparam.add(resconf.get("gridtype"));
                    }
                    if (resconf.containsKey("grid")){
                        isGrid=eparam.size();
                        eparam.add(resconf.get("grid"));
                    }
                    if (resconf.containsKey("resource")){
                        ((Hashtable)resconf.get("resource")).put("grid", ""+jobprop.getExe().get("grid"));
                        isResource=eparam.size();
                        eparam.add(resconf.get("resource"));
                    }
                    if (resconf.containsKey("jobmanager")){
                        ((Hashtable)resconf.get("jobmanager")).put("grid", ""+jobprop.getExe().get("grid"));
                        isjobmanager=eparam.size();
                        eparam.add(resconf.get("jobmanager"));
                    }
                    resconf.clear();
                }
                //dynamic management of grid, resource, jobmanager
                //i= next parameter                
                if (isGridtype > -1) {
                    if (isGrid > -1) {
                        ((Hashtable) eparam.get(isGrid)).put("egridt", isGridtype);
                        ((Hashtable) eparam.get(isGridtype)).put("i", isGrid);
                    }
                    if (isResource > -1) {
                        ((Hashtable) eparam.get(isResource)).put("egridt", isGridtype);
                    }
                    if (isjobmanager > -1) {
                        ((Hashtable) eparam.get(isjobmanager)).put("egridt", isGridtype);
                    }
                }
                if (isGrid > -1) {
                    if (isResource > -1) {
                        ((Hashtable) eparam.get(isResource)).put("egrid", isGrid);
                        ((Hashtable) eparam.get(isGrid)).put("i", isResource);
                    }
                    if (isjobmanager > -1) {
                        ((Hashtable) eparam.get(isjobmanager)).put("egrid", isGrid);
                    }                    
                }
                if (isResource > -1) {
                    if (isjobmanager > -1) {
                        ((Hashtable) eparam.get(isjobmanager)).put("eresource", isResource);
                        ((Hashtable) eparam.get(isResource)).put("i", "" + isjobmanager);
                    }                     
                }

            }
        }
        //}
        // catch(Exception e){e.printStackTrace();}        
        return eparam;
    }

    private String replaceTextS(String s1) {
        char c = 10;
        String s2 = s1.replace(String.valueOf(c), " ");
        s2 = s2.replace(String.valueOf("\'"), "");
        s2 = s2.replace(String.valueOf("\""), "");
        // System.out.println("text : " + s2);
        return s2;
    }

    /**
     * Save enduser parameters
     */
    public void doSaveEWorkflowParams(ActionRequest request, ActionResponse response) throws PortletException {
        try {
            String workflow = request.getParameter("workflow");
            //findinWF = new Hashtable();
            //  Vector eparam = getEConfParam(request.getRemoteUser(), workflow);//easy parameterek
            //  PortalCacheService.getInstance().getUser(request.getRemoteUser()).getConfiguringEParams();
            //findinWF = new Hashtable();
//            System.out.println("NEW EASY PARAMS: ");
            Hashtable wfhash = new Hashtable();
            for (int i = 0; i < PortalCacheService.getInstance().getUser(request.getRemoteUser()).getConfiguringEParams().size(); i++) {
                String nv = " ";
                if (request.getParameter("eparam_" + i) != null) {
                    nv = request.getParameter("eparam_" + i).replace('\\', '/');
                }
                ((Hashtable) PortalCacheService.getInstance().getUser(request.getRemoteUser()).getConfiguringEParams().get(i)).put("newvalue", nv);
//                System.out.println("param: " + i + " [" + PortalCacheService.getInstance().getUser(request.getRemoteUser()).getConfiguringEParams().get(i) + "]");
                if (wfhash.get(((Hashtable) PortalCacheService.getInstance().getUser(request.getRemoteUser()).getConfiguringEParams().get(i)).get("wfID")) == null) {//meg nincs ilyen wfID
                    Hashtable wfh = new Hashtable();
                    wfh.put("" + i, (Hashtable) PortalCacheService.getInstance().getUser(request.getRemoteUser()).getConfiguringEParams().get(i));
                    wfhash.put(((Hashtable) PortalCacheService.getInstance().getUser(request.getRemoteUser()).getConfiguringEParams().get(i)).get("wfID"), wfh);
                } else {
                    ((Hashtable) wfhash.get(((Hashtable) PortalCacheService.getInstance().getUser(request.getRemoteUser()).getConfiguringEParams().get(i)).get("wfID"))).put("" + i, (Hashtable) PortalCacheService.getInstance().getUser(request.getRemoteUser()).getConfiguringEParams().get(i));
                }
            }
//            System.out.println("WFre bobntott hasban(wf) hasbn(params i) hash(values) PARAMS: " + wfhash);

            Enumeration keys = wfhash.keys();
            while (keys.hasMoreElements()) {//WF-ek bejarasa
                String wfID = (String) keys.nextElement();

                Hashtable hsh = new Hashtable();
                hsh.put("url", PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(wfID).getWfsID());
                ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());

                PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                ComDataBean tmp = new ComDataBean();
                tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                tmp.setUserID(request.getRemoteUser());
                tmp.setWorkflowID(wfID);
                Vector wfconfigdt = pc.getWorkflowConfigData(tmp);
//                System.out.println("WF letoltve:" + wfID);
                Enumeration keysp = ((Hashtable) wfhash.get(wfID)).keys();
                while (keysp.hasMoreElements()) {//EParameterek bejarasa
                    String eparamID = (String) keysp.nextElement();
                    Hashtable ep = (Hashtable) ((Hashtable) wfhash.get(wfID)).get(eparamID);
//                    System.out.println("    Eparam:" + eparamID + " values ep[" + ep + "]");
                    for (int i = 0; i < wfconfigdt.size(); i++) {
                        // replace special characters...
                        String jobtxt = new String(((JobPropertyBean) wfconfigdt.get(i)).getTxt());
                        ((JobPropertyBean) wfconfigdt.get(i)).setTxt(replaceTextS(jobtxt));
                        if (("" + ((JobPropertyBean) wfconfigdt.get(i)).getId()).equals(ep.get("jobID"))) {//job id
//                            System.out.println("    Eparamjobidfound:" + ep.get("jobID"));
                            if (ep.get("type").equals("iport")) {
                                ((JobPropertyBean) wfconfigdt.get(i)).getInput("" + ep.get("typeID")).getData().put(ep.get("name"), ep.get("newvalue"));
                            } else if (ep.get("type").equals("oport")) {
                                ((JobPropertyBean) wfconfigdt.get(i)).getOutput("" + ep.get("typeID")).getData().put(ep.get("name"), ep.get("newvalue"));
                            } else {//exe
                                ((JobPropertyBean) wfconfigdt.get(i)).addExe((String) ep.get("name"), (String) ep.get("newvalue"));
                            }
                            i = wfconfigdt.size();//kilep
                        }
                    }

                }
                //save
                pc.setWorkflowConfigData(tmp, wfconfigdt);
                System.out.println("EWF saved:" + wfID);
            }

            //do not delete EINSTANCE
//            WorkflowData wData = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow);
//            if (wData.getEinstanceID() != null) {
////                System.out.println("EINSTANCE exists, delete... :" + wData.getEinstanceID());
//                RealWorkflowUtils.getInstance().deleteWorkflowInstance(request.getRemoteUser(), workflow, "" + wData.getEinstanceID());
//            }
            //
            // send configure ID to storage begin
            //
            boolean retUploadFiles = true;
            try {
                String storageID = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow).getStorageID();
                String confID = request.getParameter("confIDparam");
                // System.out.println("confID request getParameter : " + confID);
                //
                Hashtable hshsto = new Hashtable();
                hshsto.put("url", storageID);
                ServiceType sts = InformationBase.getI().getService("storage", "portal", hshsto, new Vector());
                //
                PortalStorageClient ps = (PortalStorageClient) Class.forName(sts.getClientObject()).newInstance();
                ps.setServiceURL(storageID);
                ps.setServiceID(sts.getServiceID());
                UploadWorkflowBean uwb = new UploadWorkflowBean();
                uwb.setConfID(confID);
                retUploadFiles = ps.uploadWorkflowFiles(uwb);
                if (!retUploadFiles) {
                // res.put("msg", "workflow.config.files.notuploaded");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        //
        // send configure ID to storage end
        //
        } catch (Exception e) {
            request.setAttribute("msg", "workflow.easy.saved.error");
            e.printStackTrace();
        }
        request.setAttribute("jsp",mainjsp);
        request.setAttribute("msg", "workflow.easy.saved");
    }

    /**
     *Displying concrete workflow list
     */
    public void doList(ActionRequest request, ActionResponse response) throws PortletException {
        //String workflow = request.getParameter("workflow");
        request.setAttribute("jsp",mainjsp);
    }

    /**
     * Workflow submit
     */
    public void doSubmit(ActionRequest request, ActionResponse response) throws PortletException {
        if (UserQuotaUtils.getInstance().userQuotaIsFull(request.getRemoteUser())) {
            request.setAttribute("msg", "portal.RealWorkflowPortlet.quotaisoverfull");
        } else {
            if (WorkflowInfo(request, response)) {
                doList(request, response);

                ConcurrentHashMap runTimesData = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")).getAllRuntimeInstance();
                Enumeration keys = runTimesData.keys();
                int ts;
                while (keys.hasMoreElements()) {//abort all einstance
                    String key = (String) keys.nextElement();//rtid
                    if (((WorkflowRunTime) runTimesData.get(key)).getText().equals("einstance")) {
                        // ((WorkflowRunTime) runTimesData.get(key)).;
                        if ((((WorkflowRunTime) runTimesData.get(key)).getStatus()==5) || (((WorkflowRunTime) runTimesData.get(key)).getStatus()==2)) {
                            ((WorkflowRunTime) runTimesData.get(key)).setStatus("22", 0);

                            ConcurrentHashMap tmp = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")).getRuntime(key).getJobsStatus();
                            Enumeration enm0 = tmp.keys();
                            while (enm0.hasMoreElements()) {
                                Object key0 = enm0.nextElement();
                                Enumeration enm1 = ((Hashtable) tmp.get(key0)).keys();
                                while (enm1.hasMoreElements()) {
                                    Object key1 = enm1.nextElement();
                                    ts = ((JobStatusData) ((Hashtable) tmp.get(key0)).get(key1)).getStatus();
                                    if(!(ts==6) || (ts==7) || (ts==21) || (ts==1)) {
                                        PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")).getRuntime(key).addJobbStatus((String) key0, (String) key1, "22", "", 0);
                                    }
                                }
                            }
                            System.out.println("abort EINSTANCE " + key);
                            new WorkflowAbortThread(request.getRemoteUser(),request.getParameter("workflow"), key);
                        }
                        System.out.println("delete EINSTANCE " + key);
                        RealWorkflowUtils.getInstance().deleteWorkflowInstance(request.getRemoteUser(), request.getParameter("workflow"), key);
                    }
                }
                int max = Integer.parseInt(PropertyLoader.getInstance().getProperty("repeat.submit.workflow"));
                for (int i = 0; i < max; i++) {
                    new WorkflowSubmitThread(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")), request.getRemoteUser(), einstance, request.getParameter("wfchg_type"));
                }
                request.setAttribute("msg", "portal.EasyWorkflowPortlet.doSubmit");
            }
        }
    }

    /**
     * Submission of all concrete workflows from all lists.
     */
    public void doALLSubmit(ActionRequest request, ActionResponse response) throws PortletException {
        Enumeration enm = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflows().keys();
        while (enm.hasMoreElements()) {
            new WorkflowSubmitThread(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow("" + enm.nextElement()), request.getRemoteUser(), "submit all:" + System.currentTimeMillis());
        }
        request.setAttribute("jsp",mainjsp);
    }

    private void doAbort(String userID, String workflowID, String runtimeID){
        try {
            if (PortalCacheService.getInstance().getUser(userID) != null) {
                if (PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID) != null) {
                    if (PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID) != null) {
                        int wfStatus = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).getStatus();
                        if ((5==wfStatus) || (23==wfStatus) || (2==wfStatus)) {
                            // PortalCacheService.getInstance().getUser(userID).getWorkflow(request.getParameter("workflow")).getRuntime(request.getParameter("rtid")).setStatus("22", 0);
                            // set suspending workflow status ...
                            PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).setStatus("28", 0);
                            ConcurrentHashMap tmp=PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).getJobsStatus();
                            Enumeration enm0=tmp.keys();
                            while(enm0.hasMoreElements())
                            {
                                Object key0=enm0.nextElement();
                                Enumeration enm1=((ConcurrentHashMap)tmp.get(key0)).keys();
                                while(enm1.hasMoreElements())
                                {
                                    Object key1=enm1.nextElement();
                                    // System.out.println("--"+key0+"/"+key1+"="+((JobStatusData)((Hashtable)tmp.get(key0)).get(key1)).getStatus());
                                    int ts = ((JobStatusData)((ConcurrentHashMap)tmp.get(key0)).get(key1)).getStatus();
                                    if (!(ts==6)|| ts==7 ||ts==21 || ts==1) {
                                        PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).addJobbStatus((String) key0, (String) key1, "22", "", -1);
                                    }
                                }
                            }
                            new WorkflowAbortThread(userID, workflowID, runtimeID);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * Workflow abort
     */
    public void doAbort(ActionRequest request, ActionResponse response) throws PortletException{
        doDetails(request,response);
        String userID;
        if (request.getParameter("adminuser") == null) userID = request.getRemoteUser();
        else userID = request.getParameter("adminuser");

        String workflowID = request.getParameter("workflow");
        String runtimeID = request.getParameter("rtid");
        doAbort(userID, workflowID, runtimeID);
    }

    /**
     * Aborting all workflow instances
     */
    public void doAbortAll(ActionRequest request, ActionResponse response) throws PortletException{
        String userID= request.getRemoteUser();
        String workflowID = request.getParameter("workflow");

        ConcurrentHashMap tmph=PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getAllRuntimeInstance();
        Enumeration enm=tmph.keys();
        String ts;
        while(enm.hasMoreElements())
        {
            ts=""+enm.nextElement();
            doAbort(userID, workflowID, ts);
        }
        doDetails(request, response);
    }

    /**
     * Deletion of workflow instance
     */
    public void doDeleteInstance(ActionRequest request, ActionResponse response) throws PortletException {
        doDetails(request, response);
        RealWorkflowUtils.getInstance().deleteWorkflowInstance(request.getRemoteUser(), request.getParameter("workflow"), request.getParameter("rtid"));
    }

    /**
     * Rescue a given workflow einstance
     */
    public void doRescue(ActionRequest request, ActionResponse response) throws PortletException {
        if (UserQuotaUtils.getInstance().userQuotaIsFull(request.getRemoteUser())) {
            request.setAttribute("msg", "portal.RealWorkflowPortlet.quotaisoverfull");
        } else {
            if (WorkflowInfo(request, response)) {
                doList(request, response);
                try {
                    Vector errorJobPidList = new Vector();
                    String portalID = PropertyLoader.getInstance().getProperty("service.url");
                    String userID = request.getRemoteUser();
                    String workflowID = request.getParameter("workflow");
                    System.out.println("doRescue portalID:" + portalID + " userID:" + userID + " workflowID:" + workflowID);
                    WorkflowData wData = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID);
                    String runtimeID = "" + wData.getEinstanceID();
                    System.out.println("doRescue portalID:" + portalID + " userID:" + userID + " workflowID:" + workflowID + " runtimeID:" + runtimeID);
                    //
                    // 23 = running/error
                    int wfStatus = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).getStatus();
                    if (23==wfStatus) {
                        // running workflow statusz beirasa...
                        PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).setStatus("5", 0);
                    } else {
                        // resuming workflow statusz beirasa...
                        PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).setStatus("29", 0);
                    }
                    //
                    ConcurrentHashMap tmp = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).getJobsStatus();
                    Enumeration enm0 = tmp.keys();
                    int ts;
                    while (enm0.hasMoreElements()) {
                        Object key0 = enm0.nextElement();
                        Enumeration enm1 = ((ConcurrentHashMap) tmp.get(key0)).keys();
                        while (enm1.hasMoreElements()) {
                            Object key1 = enm1.nextElement();
                            ts = ((JobStatusData) ((ConcurrentHashMap) tmp.get(key0)).get(key1)).getStatus();
                            if (ts==25||ts==21||ts==22 || ts==7 || ts==15 || ts==13 || ts==12) {
                                // beirja az init statuszt...
                                // PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).addJobbStatus((String) key0, (String) key1, "1", "", 0);
                                // kitorli a jobot a nyilvantartasbol...
                                PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).removeJobStatus((String) key0, (String) key1);
                                // storage takaritashoz ki kell gyujteni a jobID/jobPID-eket
                                ComDataBean comDataBean = new ComDataBean();
                                comDataBean.setJobID((String) key0);
                                comDataBean.setJobPID((String) key1);
                                errorJobPidList.addElement(comDataBean);
                            }
                        }
                    }
                    new WorkflowRescueThread(portalID, userID, workflowID, runtimeID, ""+wfStatus, errorJobPidList);
                    request.setAttribute("msg", "portal.EasyWorkflowPortlet.doRescue");
                } catch (Exception e) {
                    request.setAttribute("msg", "portal.EasyWorkflowPortlet.doRescue.error");
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Detailed displaying of a worflow instance data
     */
    public void doInstanceDetails(ActionRequest request, ActionResponse response) throws PortletException {
        String workflow = "" + request.getParameter("workflow");
        doDetails(request, response);
        request.getPortletSession().setAttribute("cworkflow", request.getParameter("workflow"));
        request.getPortletSession().setAttribute("detailsruntime", request.getParameter("rtid"));
//        System.out.println("!!!!doInstanceDetails rtid:"+request.getParameter("rtid")+" workflow:"+workflow);
        request.setAttribute("rtid", PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow).getRuntime(request.getParameter("rtid")).getText());
        request.setAttribute("workflow", PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow));

        if (PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")).getRuntime(request.getParameter("rtid")).getJobsStatus().equals(new Hashtable())) {
            Hashtable prp = new Hashtable();
            Vector v = new Vector();
            prp.put("url", PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")).getWfsID());
            ServiceType st = InformationBase.getI().getService("wfs", "portal", prp, new Vector());
            try {
                PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                ComDataBean cmb = new ComDataBean();
                cmb.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                cmb.setUserID(request.getRemoteUser());
                cmb.setWorkflowID(request.getParameter("workflow"));
                cmb.setWorkflowRuntimeID(request.getParameter("rtid"));
                v = pc.getWorkflowInstanceJobs(cmb);
                JobStatusBean tmp;
                for (int i = 0; i < v.size(); i++) {
                    tmp = (JobStatusBean) v.get(i);
                    PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")).getRuntime(request.getParameter("rtid")).addJobbStatus(tmp.getJobID(), "" + tmp.getPID(), "" + tmp.getStatus(), tmp.getResource(), 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        Hashtable jobs = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")).getRuntime(request.getParameter("rtid")).getCollectionJobsStatus();
        //System.out.println("---doInstanceDetails:"+jobs);
        int sinit = 0;
        int sproc = 0;
        int sfinalok = 0;
        int sfinalerr = 0;
        Enumeration jobskeys = jobs.keys();
        while (jobskeys.hasMoreElements()) {//iterate jobs
            Hashtable job = (Hashtable) jobs.get(jobskeys.nextElement());
            
            Enumeration jkeys = job.keys();
            while (jkeys.hasMoreElements()) {//paramjobs
                try {
                    String jobstatus = "" + jkeys.nextElement();//job statuses from the given gob
                    int ijobStatus = Integer.parseInt(jobstatus);
                    //System.out.println("jobstatus:"+jobstatus+" job.get(jobstatus):"+job.get(ijobStatus));
                    int jobstatusnbr = Integer.parseInt("" + job.get(ijobStatus));//?db

                    if (jobstatus.equals("1") || jobstatus.equals("28")) {//init or suspended
                        sinit += jobstatusnbr;
                    } else if (jobstatus.equals("6") || jobstatus.equals("21") || jobstatus.equals("25")) {
                        sfinalok += jobstatusnbr;
                    } else if (jobstatus.equals("7")) {
                        sfinalerr += jobstatusnbr;
                    } else {
                        sproc += jobstatusnbr;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        int sum=sinit+sproc+sfinalok+sfinalerr;
        double psinit = ((double)sinit/sum)*100;
        java.text.DecimalFormat fmt = new java.text.DecimalFormat("#");
        request.setAttribute("sinit", sinit);
        request.setAttribute("sproc", sproc);
        request.setAttribute("sfinalok", sfinalok);
        request.setAttribute("sfinalerr", sfinalerr);
        request.setAttribute("psinit", fmt.format(((double)sinit/sum)*100) );
        request.setAttribute("psproc", fmt.format(((double)sproc/sum)*100));
        request.setAttribute("psfinalok", fmt.format(((double)sfinalok/sum)*100));
        request.setAttribute("psfinalerr", fmt.format(((double)sfinalerr/sum)*100));
        request.setAttribute("ssum", sum);
//        System.out.println("---doInstanceDetails:" + sinit + "+" + sproc + "+" + sfinalok+ "+" + sfinalerr+"="+sum+" --- init%:"+psinit+"");
        
        request.setAttribute("instJobList", PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")).getRuntime(request.getParameter("rtid")).getCollectionJobsStatus());
        
    }


    /**
     * Deletion of a concrete workflow (together with all instances)
     */
    public void doDelete(ActionRequest request, ActionResponse response) throws PortletException {
        WorkflowData wData = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow"));

        String userID = request.getRemoteUser();
//wfs
        Hashtable hsh = new Hashtable();
        hsh.put("url", wData.getWfsID());
        ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
        try {
            PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp = new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(userID);
            tmp.setWorkflowID(wData.getWorkflowID());
            pc.deleteWorkflow(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
//storage
        try {
            hsh = new Hashtable();
            hsh.put("url", wData.getStorageID());
            st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
            PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
            ps.setServiceURL(st.getServiceUrl());
            ps.setServiceID(st.getServiceID());
            ComDataBean tmp = new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(userID);
            tmp.setWorkflowID(wData.getWorkflowID());
            ps.deleteWorkflow(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
//registry
        PortalCacheService.getInstance().getUser(userID).deleteWorkflow(wData.getWorkflowID());

    }

 /**
     * Displaying workflow configuration information when WF is failed
     */
    private boolean WorkflowInfo(ActionRequest request, ActionResponse response) throws PortletException {
        String userID = request.getRemoteUser();
        WorkflowData wfData = PortalCacheService.getInstance().getUser(userID).getWorkflow(request.getParameter("workflow"));
        request.setAttribute("jsp","/jsp/workflow/workflowinfo.jsp");
        Vector errorVector = new Vector();

//Session query
        PortletSession ps=request.getPortletSession();

  // Querying available resource configuration
        try{
            if(ps.getAttribute("resources",ps.APPLICATION_SCOPE)==null){
                ResourceConfigurationFace rc=(ResourceConfigurationFace)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                List<Middleware> tmp_r=rc.get();
                ps.setAttribute("resources", tmp_r,ps.APPLICATION_SCOPE);
                ps.setAttribute("pub_resources", tmp_r,ps.APPLICATION_SCOPE);
            }
        }
        catch(Exception ex) {
            System.out.println("DCI-Bridge is not available");
            WorkflowConfigErrorBean eBean = new WorkflowConfigErrorBean();
            eBean.setErrorID("error.job.dcibridgeerror");
            errorVector.add(eBean);
            request.setAttribute("errors", errorVector);
            request.setAttribute("wrkdata", wfData);
            ex.printStackTrace();
            return false;
        }

        try {
            errorVector = RealWorkflowUtils.getInstance().getWorkflowConfigErrorVector((List<dci.data.Middleware>)ps.getAttribute("resources", ps.APPLICATION_SCOPE),userID, wfData);
            request.setAttribute("errors", errorVector);
            request.setAttribute("wrkdata", wfData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (errorVector.size() != 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Workflow exportalas a repository-ba (doExport)
     */
    public void doWorkflowInfo(ActionRequest request, ActionResponse response) throws PortletException {
//        System.out.println("doWorkflowInfo()...");
        WorkflowInfo(request, response);
    }


    @Override
	public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException
    {
	 String key;
        if("editgraphURL".equals(request.getResourceID()) && request.getParameter("wfId")!=null){
            GraphEditorUtil.jnpl(request, response);
            return;
        }

        if("refreshConfigURL".equals(request.getResourceID())){
           List<String> graphs=new ArrayList<String>();
           Enumeration<String> enm=PortalCacheService.getInstance().getUser(request.getRemoteUser()).getAbstactWorkflows().keys();
           while(enm.hasMoreElements())graphs.add(enm.nextElement());

           request.setAttribute("graphs", graphs);
           request.setAttribute("workflow",request.getPortletSession().getAttribute("cworkflow"));
           getPortletContext().getRequestDispatcher("/jsp/workflow/accepteditedgraph.jsp").include(request, response);
            return;
        }

// ajax help
        if(request.getParameter("helptext")!=null) {
            super.serveResource(request, response);
            return;
        }
//output download
        if(request.getParameter("downloadType")!=null)
        {
            response.setContentType("application/zip");
            response.setProperty("Content-Disposition", "inline; filename=\"" + request.getParameter("workflowID")+"_"+request.getParameter("jobID")+"_"+request.getParameter("pidID")+"_outputs.zip\"");
            try{HttpDownload.fileDownload(request.getParameter("downloadType"), request, response);}
            catch(Exception e){e.printStackTrace();throw new PortletException("com error");}
            return ;
        }
// logs download
        if(request.getParameter("fileID")!=null)
        {
            response.setContentType("application/text");
            response.setProperty("Content-Disposition", "inline; filename=\"" + request.getParameter("workflowID")+"_"+request.getParameter("jobID")+"_"+request.getParameter("pidID")+"_"+request.getParameter("fileID")+".txt\"");
            try{HttpDownload.fileView(request, response);}
            catch(Exception e){e.printStackTrace();throw new PortletException("com error");}
            return ;
        }


// file upload status
        if(request.getParameter("uploadStatus")!=null)
        {
            PortletSession ps=request.getPortletSession();
// end of download
            if(ps.getAttribute("finaluploads")!=null)
            {
                ps.removeAttribute("finaluploads");
                ps.removeAttribute("uploaded");
                ps.removeAttribute("upload");
                ps.removeAttribute("uploading");
                response.getWriter().write("Upload");

            }
            else
            {
                try
                {
                    Vector<String> tmp=(Vector<String>)ps.getAttribute("uploaded");
                    response.getWriter().write("&nbsp;<br/>");
                    for(String t:tmp)
                        response.getWriter().write("uploaded:"+t+"<br/>");
                    FileUploadProgressListener lisener=(FileUploadProgressListener)((PortletFileUpload)ps.getAttribute("upload")).getProgressListener();
                    byte uplStatus=Byte.parseByte(lisener.getFileuploadstatus());
                    response.getWriter().write("uploading:"+ps.getAttribute("uploading")+"->"+"<div style=\"width:"+uplStatus+"px;background-color:blue; \" >"+uplStatus+"%<br/>");
                }
                catch(Exception ee)
                {
                    System.out.println("file upload has not yet begun "+ps.getId());
                    ee.printStackTrace();
                }
            }
            return ;
        }
// configuration
        Hashtable reqhash=new Hashtable();
        Enumeration enm0=request.getParameterNames();
        while(enm0.hasMoreElements())
        {
            key=""+enm0.nextElement();
            reqhash.put(key, request.getParameter(key));
        }
        reqhash.put("sid", request.getPortletSession().getId());
        try
        {
            ActionHandler t=null;
            try
            {
                String sid=request.getPortletSession().getId();
                String workflowName=""+request.getPortletSession().getAttribute("cworkflow");
                String username=request.getRemoteUser();
                String wftype=PortalCacheService.getInstance().getUser(username).getWorkflow(workflowName).getWorkflowType();
                reqhash.put("ws-pgrade.wftype", wftype);
                t= (ActionHandler)Class.forName("hu.sztaki.lpds.pgportal.servlet.ajaxactions."+wftype+"."+request.getParameter("m")).newInstance();
//                System.out.println("***SERVE-RESOURCE:"+t.getClass().getName());

            }
            catch(Exception e)
            {
                try
                {
                    t = (ActionHandler)Class.forName("hu.sztaki.lpds.pgportal.servlet.ajaxactions."+request.getParameter("m")).newInstance();
//                    System.out.println("***SERVE-RESOURCE:"+t.getClass().getName());
                }
                catch(ClassNotFoundException e0){System.out.println("classnotfound");e0.printStackTrace();}
                catch(InstantiationException e0){e0.printStackTrace();System.out.println("---Init error:hu.sztaki.lpds.pgportal.servlet.ajaxactions."+request.getParameter("m"));e0.printStackTrace();}
                catch(IllegalAccessException e0){e0.printStackTrace();System.out.println("---Illegal Access:hu.sztaki.lpds.pgportal.servlet.ajaxactions."+request.getParameter("m"));e0.printStackTrace();}
            }
            try
            {
// Session transfer
            t.setSessionVariables(request.getPortletSession());
// Parameter setting
            reqhash.put("user", request.getRemoteUser());
            if(request.getPortletSession().getAttribute("cworkflow")!=null)
                reqhash.put("workflow",request.getPortletSession().getAttribute("cworkflow"));
            if(request.getPortletSession().getAttribute("detailsruntime")!=null)
                reqhash.put("detailsruntime",request.getPortletSession().getAttribute("detailsruntime"));
            }
            catch(Exception e){e.printStackTrace();}
// Content transfer
            if(t.getDispacher(reqhash)==null)
            {
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
                try{out.print(t.getOutput(reqhash));}
                catch(Exception e){e.printStackTrace();}
                out.close();
            }
// Transfer of controlling
            else
            {
                Hashtable res=t.getParameters(reqhash);
                Enumeration enm=res.keys();
                while(enm.hasMoreElements())
                {
                    key=""+enm.nextElement();
                    request.setAttribute(key, res.get(key));
                }
                PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher(t.getDispacher(reqhash));
                dispatcher.include(request, response);
            }
        }
        catch(Exception e){e.printStackTrace();System.out.println("-----------------------Can not be initialized:"+request.getParameter("m"));}

    }

    @Override
    public void doUpload(ActionRequest request, ActionResponse response)
    {
        PortletContext context = getPortletContext();
        context.log("[FileUploadPortlet] doUpload() called");

        try
        {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            PortletFileUpload pfu = new PortletFileUpload(factory);
            pfu.setSizeMax(uploadMaxSize); // Maximum upload size
            pfu.setProgressListener((ProgressListener) new FileUploadProgressListener());

            PortletSession ps=request.getPortletSession();
             if(ps.getAttribute("uploaded")==null)
                ps.setAttribute("uploaded", new Vector<String>());
            ps.setAttribute("upload",pfu);

      //get the FileItems
            String fieldName = null;

            List fileItems = pfu.parseRequest(request);
            Iterator iter = fileItems.iterator();
            File serverSideFile=null;
            while (iter.hasNext())
            {
                FileItem item = (FileItem)iter.next();
        // retrieve hidden parameters if item is a form field
                if (item.isFormField())
                {
                    fieldName = item.getFieldName();
                }
                else
                { // item is not a form field, do file upload
                    Hashtable<String,ProgressListener> tmp=(Hashtable<String,ProgressListener>)ps.getAttribute("uploads",ps.APPLICATION_SCOPE);

                    String s = item.getName();
                    s = FilenameUtils.getName(s);
                    ps.setAttribute("uploading",s);

                    String tempDir = System.getProperty("java.io.tmpdir")+"/uploads/"+request.getRemoteUser();
                    File f=new File(tempDir);
                    if(!f.exists()) f.mkdirs();
                    serverSideFile = new File(tempDir, s);
                    item.write(serverSideFile);
                    item.delete();
                    context.log("[FileUploadPortlet] - file " + s+ " uploaded successfully to " + tempDir);
// file upload to storage
                    try
                    {
                        Hashtable h=new Hashtable();
                        h.put("portalURL",PropertyLoader.getInstance().getProperty("service.url"));
                        h.put("userID",request.getRemoteUser());

                        String uploadField="";
        // retrieve hidden parameters if item is a form field
                        for(FileItem item0:(List<FileItem>)fileItems)
                        {
                            if (item0.isFormField())
                                h.put(item0.getFieldName(), item0.getString());
                           else
                                uploadField=item0.getFieldName();

                        }

                        Hashtable hsh = new Hashtable();
                        ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
                        PortalStorageClient psc = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
                        psc.setServiceURL(st.getServiceUrl());
                        psc.setServiceID("/upload");
                        if(serverSideFile!=null)
                        {
                            psc.fileUpload(serverSideFile,uploadField, h);
                        }
                    }
                    catch (Exception ex)
                    {
                        response.setRenderParameter("full", "error.upload");
                        ex.printStackTrace();
                        return;
                    }
                    ((Vector<String>)ps.getAttribute("uploaded")).add(s);

                }

            }
//            ps.removeAttribute("uploads",ps.APPLICATION_SCOPE);
            ps.setAttribute("finaluploads","");

        }
        catch (SizeLimitExceededException see)
        {
            response.setRenderParameter("full", "error.upload.sizelimit");
            request.getPortletSession().setAttribute("finaluploads","");
            see.printStackTrace();
            context.log("[FileUploadPortlet] - failed to upload file - "+ see.toString());
            return;
        }
        catch (FileUploadException fue)
        {
            response.setRenderParameter("full", "error.upload");

            fue.printStackTrace();
            context.log("[FileUploadPortlet] - failed to upload file - "+ fue.toString());
            return;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            response.setRenderParameter("full", "error.exception");
            context.log("[FileUploadPortlet] - failed to upload file - "+ e.toString());
            return;
        }
        response.setRenderParameter("full", "action.succesfull");

    }

}

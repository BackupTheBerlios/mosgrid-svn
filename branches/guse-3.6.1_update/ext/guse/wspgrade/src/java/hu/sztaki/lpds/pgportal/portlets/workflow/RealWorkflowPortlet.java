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
 * RealWorkflowPortlet.java
 */

package hu.sztaki.lpds.pgportal.portlets.workflow;

import dci.data.Middleware;
import hu.sztaki.lpds.dcibridge.client.ResourceConfigurationFace;
import hu.sztaki.lpds.pgportal.com.*;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.*;
import hu.sztaki.lpds.pgportal.service.workflow.RealWorkflowUtils;
import hu.sztaki.lpds.pgportal.service.workflow.UserQuotaUtils;
import hu.sztaki.lpds.pgportal.service.workflow.WorkflowExportUtils;
import hu.sztaki.lpds.pgportal.ui.LineCoord;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.pgportal.service.workflow.Sorter;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;

import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.ActionHandler;
import hu.sztaki.lpds.pgportal.util.stream.HttpDownload;
import hu.sztaki.lpds.portal.util.stream.FileUploadProgressListener;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobInstanceBean;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.wfs.com.WorkflowConfigErrorBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import javax.portlet.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.io.FilenameUtils;
/**
 * @author krisztian
 */
public class RealWorkflowPortlet extends GenericWSPgradePortlet
{

    public RealWorkflowPortlet() {}
    private long uploadMaxSize = 0;
    private String workflow="";
    private String mainjsp="/jsp/workflow/realworkflowlist.jsp";


    private PortletContext pContext;

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
     protected void doView(RenderRequest req, RenderResponse response) throws PortletException,IOException
    {
        if(req.getAttribute("jsp")==null) req.setAttribute("jsp",mainjsp);
        PortletSession ps=req.getPortletSession();
//resource url for job config
        if(ps.getAttribute("ajaxSessionURL", ps.APPLICATION_SCOPE)==null){
            ResourceURL ajaxSessionURL=response.createResourceURL();
            ajaxSessionURL.setParameter("m","GetJobView");
            ps.setAttribute("ajaxSessionURL",ajaxSessionURL, ps.APPLICATION_SCOPE);
        }

        if(ps.getAttribute("ajaxGetIOViewURL", ps.APPLICATION_SCOPE)==null){
            ResourceURL ajaxGetIOViewURL=response.createResourceURL();
            ajaxGetIOViewURL.setParameter("m","GetIOView");
            ps.setAttribute("ajaxGetIOViewURL",ajaxGetIOViewURL, ps.APPLICATION_SCOPE);
        }
            ps.getAttribute("ajaxSessionURL",ps.APPLICATION_SCOPE).toString();
            ps.getAttribute("ajaxGetIOViewURL",ps.APPLICATION_SCOPE).toString();
//        System.out.println("available(ajaxSessionURL)="+ps.getAttribute("ajaxSessionURL",ps.APPLICATION_SCOPE));
//        System.out.println("available(ajaxGetIOViewURL)="+ps.getAttribute("ajaxGetIOViewURL",ps.APPLICATION_SCOPE));


        response.setContentType("text/html");
        if(!isInited())        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(req, response);
            return;
        }
        openRequestAttribute(req);
        try{
            if("main".equals(req.getParameter("render"))) req.setAttribute("jsp",mainjsp);
            // Sorting...
            if (req.getAttribute("jsp").equals(mainjsp))
                req.setAttribute("rWorkflowList", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflows()));

            // Sorting...
            PortletRequestDispatcher dispatcher=null;
            dispatcher = pContext.getRequestDispatcher((String)req.getAttribute("jsp"));
            dispatcher.include(req, response);
        }
        catch (IOException e){throw new PortletException("JSPPortlet.doView exception", e);}
        cleanRequestAttribute(req.getPortletSession());
        req.setAttribute("jsp",mainjsp);
 }


    /**
     * Displaying Details
     */
     public void doDetails(ActionRequest request, ActionResponse response) throws PortletException
    {
        workflow=""+request.getParameter("workflow");
        setRequestAttribute(request.getPortletSession(),"workflow", PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow));
        request.setAttribute("jsp","/jsp/workflow/wrkinst.jsp");
    }

    /**
     * Displaying vizualizer
     */
    public void doWkfvisualizer(ActionRequest request, ActionResponse response) throws PortletException
    {
        workflow=""+request.getParameter("workflow");
        setRequestAttribute(request.getPortletSession(),"portalurl",PropertyLoader.getInstance().getProperty("service.url"));
        setRequestAttribute(request.getPortletSession(),"wfsurl",PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow).getWfsID());
        setRequestAttribute(request.getPortletSession(),"wkfname",workflow);
        setRequestAttribute(request.getPortletSession(),"userID",request.getRemoteUser());
        setRequestAttribute(request.getPortletSession(),"rtid",request.getParameter("rtid"));
        request.setAttribute("jsp","/jsp/workflow/viswkf.jsp");
    }

    /**
     * Displaying Workflow configuration interface
     */
    public void doConfigure(ActionRequest request, ActionResponse response) throws PortletException
    {
// Querying logged user
        String userID;
        if(request.getParameter("adminuser")==null) userID=request.getRemoteUser();
        else userID=request.getParameter("adminuser");

        if(request.getParameter("pcwkf")!=null){
            String workflowName=request.getParameter("workflow");
            if(!workflowName.equals(request.getPortletSession().getAttribute("cworkflow")))
                NewWorkflowUtil.fromWorkflow(request, response);
            String graphName=PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowName).getGraf();
            if(!graphName.equals(request.getParameter("pgraf")))
                RealWorkflowUtil.changeGraph(request, response);
        }

//Over the Quota
        if (UserQuotaUtils.getInstance().userQuotaIsFull(userID))
        {
            setRequestAttribute(request.getPortletSession(),"msg","portal.RealWorkflowPortlet.quotaisoverfull");
            return;
        }
//Session query
        PortletSession ps=request.getPortletSession();

  //Available resource configuration query
        try{
            if(ps.getAttribute("resources",ps.APPLICATION_SCOPE)==null){
                ResourceConfigurationFace rc=(ResourceConfigurationFace)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                List<Middleware> tmp_r=rc.get();
                ps.setAttribute("resources", tmp_r,ps.APPLICATION_SCOPE);
                ps.setAttribute("pub_resources", tmp_r,ps.APPLICATION_SCOPE);
            }
        }
        catch(Exception ex) {ex.printStackTrace();}




        if (request.getParameter("workflow") != null) {
            ps.setAttribute("cworkflow1", request.getParameter("workflow"),ps.APPLICATION_SCOPE);
            request.getPortletSession().setAttribute("cworkflow", request.getParameter("workflow"));
        }
        workflow = request.getParameter("workflow");

        setRequestAttribute(request.getPortletSession(),"graphs",PortalCacheService.getInstance().getUser(userID).getAbstactWorkflows());
        setRequestAttribute(request.getPortletSession(),"templates",PortalCacheService.getInstance().getUser(userID).getTemplateWorkflows());

        Hashtable hsh=new Hashtable();
        hsh.put("url",PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow).getWfsID());
        ServiceType st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());
        try
        {
            PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp=new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(userID);
            tmp.setWorkflowID(workflow);
            if (PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow).getWorkflowType().equals("multinode") || PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow).getWorkflowType().equals("singlenode")) {
                PortalCacheService.getInstance().getUser(userID).setConfiguringWorkflowWFProp(pc.getWorkflowConfigData(tmp), pc.getWorkflowProps(tmp));
            }else
            {
                PortalCacheService.getInstance().getUser(userID).setConfiguringWorkflow(pc.getWorkflowConfigData(tmp));
            }
            setRequestAttribute(request.getPortletSession(),"jobs", PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow());
            Vector ltmp=new Vector();
            for(int i=0;i<PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().size();i++)
            {
                // replace special characters...
                String jobtxt = new String(((JobPropertyBean) PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(i)).getTxt());
                ((JobPropertyBean) PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(i)).setTxt(replaceTextS(jobtxt));
                // inputs
                for(int j=0;j<((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(i)).getInputs().size();j++)
                {
                    PortDataBean ptmp=(PortDataBean)((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(i)).getInputs().get(j);
                    // replace special characters...
                    ptmp.setTxt(replaceTextS(ptmp.getTxt()));
                    if(!ptmp.getPrejob().equals(""))
                    {
                        for(int k=0;k<PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().size();k++)
                        {
                            if(((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(k)).getName().equals(ptmp.getPrejob()))
                            {
                                for(int z=0;z<((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(k)).getOutputs().size();z++)
                                {
                                    if(ptmp.getPreoutput().equals(""+((PortDataBean)((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(k)).getOutputs().get(z)).getSeq()))
                                    {
                                        long x=((PortDataBean)((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(k)).getOutputs().get(z)).getX();
                                        long y=((PortDataBean)((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(k)).getOutputs().get(z)).getY();
                                        ltmp.add(new LineCoord(""+ptmp.getX(), ""+ptmp.getY(),""+x,""+y));
                                    }
                                }
                            }
                        }
                    }
                }
                // outputs
                for(int jo=0;jo<((JobPropertyBean) PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(i)).getOutputs().size();jo++) {
                    PortDataBean ptmpo = (PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(i)).getOutputs().get(jo);
                    // replace special characters...
                    ptmpo.setTxt(replaceTextS(ptmpo.getTxt()));
                }
            }
            setRequestAttribute(request.getPortletSession(),"lineList",ltmp);
        }
        catch(Exception e){e.printStackTrace();}

        String storageURL=PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow).getStorageID();
        if(storageURL==null){
            st = InformationBase.getI().getService("storage", "portal", new Hashtable(), new Vector());
            storageURL=st.getServiceUrl();
        }

        setRequestAttribute(request.getPortletSession(),"storageID",storageURL);
        setRequestAttribute(request.getPortletSession(),"userID",userID);
        setRequestAttribute(request.getPortletSession(),"portalID",PropertyLoader.getInstance().getProperty("service.url"));
        setRequestAttribute(request.getPortletSession(),"wrkdata",PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow));

        setRequestAttribute(request.getPortletSession(),"grafs", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(userID).getAbstactWorkflows()));
        setRequestAttribute(request.getPortletSession(),"awkfs", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(userID).getTemplateWorkflows()));
        //If workflow instance exists, the graph is not exchangeable.
        String enablecgraf="";
        if (!PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow).getAllRuntimeInstance().isEmpty()) {//AllWorkflow
             ConcurrentHashMap h = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow).getAllRuntimeInstance();
            if (h.size() == 1 && !(h.containsKey("AllWorkflow") || h.containsKey("allworkflow"))) {
                //"allworkflow".equalsIgnoreCase(runtimeID)
                enablecgraf = "disabled";
            } else if (h.size() > 1) {
                enablecgraf = "disabled";
            }
            // System.out.println("Modify graf LOCK:" + enablecgraf + " wfs:" + PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow).getAllRuntimeInstance());

        }
        setRequestAttribute(request.getPortletSession(),"enablecgraf", enablecgraf);
        //
        // set configure ID
        String confID = userID + String.valueOf(System.currentTimeMillis());
        setRequestAttribute(request.getPortletSession(),"confID", confID);
        //
        doList(request,response);
       request.setAttribute("jsp","/jsp/workflow/"+PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow).getWorkflowType()+"/configure.jsp");
        setRequestAttribute(request.getPortletSession(),"navigatepage", "/jsp/workflow/"+PortalCacheService.getInstance().getUser(userID).getWorkflow(workflow).getWorkflowType()+"/configure.jsp");

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
     * Displying concrete workflow list
     */
   public void doList(ActionRequest request, ActionResponse response) throws PortletException
    {
        workflow=request.getParameter("workflow");
        request.setAttribute("jsp",mainjsp);
    }

    /**
     * Workflow submit
     */

     public void doSubmit(ActionRequest request, ActionResponse response) throws PortletException
    {
        if (WorkflowInfo(request, response)) {
            request.setAttribute("jsp",mainjsp);
            if (UserQuotaUtils.getInstance().userQuotaIsFull(request.getRemoteUser()))
            {
                request.setAttribute("msg", "portal.RealWorkflowPortlet.quotaisoverfull");
            }
            else
            {
                int max=Integer.parseInt(PropertyLoader.getInstance().getProperty("repeat.submit.workflow"));
                for(int i=0;i<max;i++)
                new WorkflowSubmitThread(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")), request.getRemoteUser(), "" + request.getParameter("submittext"), request.getParameter("wfchg_type"));
                setRequestAttribute(request.getPortletSession(),"msg","portal.RealWorkflowPortlet.doSubmit");
            }
        }
    }



    /**
     * Submission of all concrete workflows from all lists.
     */
    public void doALLSubmit(ActionRequest request, ActionResponse response) throws PortletException
    {
            doList(request,response);
            if (UserQuotaUtils.getInstance().userQuotaIsFull(request.getRemoteUser()))
            {
                setRequestAttribute(request.getPortletSession(),"msg","portal.RealWorkflowPortlet.quotaisoverfull");
            }
            else
            {
                Enumeration enm=PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflows().keys();
                while(enm.hasMoreElements())
                new WorkflowSubmitThread(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(""+enm.nextElement()),request.getRemoteUser(), "submit all:"+System.currentTimeMillis());
            }
          request.setAttribute("jsp",mainjsp);
    }


    private void doAbort(String userID, String workflowID, String runtimeID)
    {
        try {
            if (PortalCacheService.getInstance().getUser(userID) != null) {
                if (PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID) != null) {
                    if (PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID) != null) {
                        String wfStatus =""+ PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).getStatus();
                        if (("5".equals(wfStatus)) || ("23".equals(wfStatus)) || ("2".equals(wfStatus))) {
                            // PortalCacheService.getInstance().getUser(userID).getWorkflow(request.getParameter("workflow")).getRuntime(request.getParameter("rtid")).setStatus("22", 0);
                            // suspending workflow status beallitasa...
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
                                    String ts =""+ ((JobStatusData)((ConcurrentHashMap)tmp.get(key0)).get(key1)).getStatus();
                                    if (!(ts.equals("6")||ts.equals("7")||ts.equals("21")||ts.equals("1"))) {
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
    public void doAbort(ActionRequest request, ActionResponse response) throws PortletException
    {
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
    public void doAbortAll(ActionRequest request, ActionResponse response) throws PortletException
    {
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
    public void doDeleteInstance(ActionRequest request, ActionResponse response) throws PortletException
    {
        doDetails(request,response);
        RealWorkflowUtils.getInstance().deleteWorkflowInstance(request.getRemoteUser(), request.getParameter("workflow"), request.getParameter("rtid"));
    }

    public void doDeleteAllInstance(ActionRequest request, ActionResponse response) throws PortletException
    {
        String userID=request.getRemoteUser();
        WorkflowData wData=PortalCacheService.getInstance().getUser(userID).getWorkflow(request.getParameter("workflow"));
        Enumeration<String> enm=wData.getAllRuntimeInstance().keys();
        String key;
        WorkflowRunTime tmp;
        while(enm.hasMoreElements())
        {
            key=enm.nextElement();
            tmp=wData.getRuntime(key);
            if((tmp.getStatus()==6)|| (tmp.getStatus()==7) || (tmp.getStatus()==22))
                RealWorkflowUtils.getInstance().deleteWorkflowInstance(userID, request.getParameter("workflow"), key);
        }
    }


    /**
     * Rescue a given workflow instance
     */
    public void doRescue(ActionRequest request, ActionResponse response) throws PortletException
    {
        if (WorkflowInfo(request, response)) {
            doDetails(request,response);
            //
            Vector errorJobPidList = new Vector();
            String portalID = PropertyLoader.getInstance().getProperty("service.url");
            String userID = request.getRemoteUser();
            String workflowID = request.getParameter("workflow");
            String runtimeID = request.getParameter("rtid");
            //
            String wfStatus =""+ PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).getStatus();
            if (("7".equals(wfStatus)) || ("28".equals(wfStatus)) || ("23".equals(wfStatus))) {
                //
                // 23 = running/error
                if ("23".equals(wfStatus)) {
                    // entering of running workflow status
                    PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).setStatus("5", 0);
                } else {
                    // entering of resuming workflow status
                    PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).setStatus("29", 0);
                }
                //
                ConcurrentHashMap tmp=PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).getJobsStatus();
                Enumeration enm0=tmp.keys();
                String ts;
                while (enm0.hasMoreElements())
                {
                    Object key0=enm0.nextElement();
                    Enumeration enm1=((ConcurrentHashMap)tmp.get(key0)).keys();
                    while(enm1.hasMoreElements())
                    {
                        Object key1=enm1.nextElement();
                        ts=""+((JobStatusData)((ConcurrentHashMap)tmp.get(key0)).get(key1)).getStatus();
                        if (ts.equals("25")||ts.equals("22")||ts.equals("21")||ts.equals("7")||ts.equals("15")||ts.equals("13")||ts.equals("12")) {
                            // entering init status
                            // PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).addJobbStatus((String)key0,(String)key1,"1","",-1);
                            // clearing job from registry
                            PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).removeJobStatus((String) key0, (String) key1);
                            // collecting jobID/jobPID for storage cleanup 
                            ComDataBean comDataBean = new ComDataBean();
                            comDataBean.setJobID((String) key0);
                            comDataBean.setJobPID((String) key1);
                            errorJobPidList.addElement(comDataBean);
                        }
                    }
                }
                if (UserQuotaUtils.getInstance().userQuotaIsFull(request.getRemoteUser()))
                {
                    request.setAttribute("msg", "portal.RealWorkflowPortlet.quotaisoverfull");
                }
                else
                {
                    new WorkflowRescueThread(portalID, userID, workflowID, runtimeID, wfStatus, errorJobPidList);
                }
            }
             // request.setAttribute("jsp",mainjsp;
            request.setAttribute("jsp","/jsp/workflow/wrkinst.jsp");
        }
    }

    /**
     * Detailed displaying of a worflow instance data
     */
    public void doInstanceDetails(ActionRequest request, ActionResponse response) throws PortletException
    { 
        String userID = request.getRemoteUser();
        String workflowID = request.getParameter("workflow");
        String runtimeID = request.getParameter("rtid");

        request.getPortletSession().setAttribute("cworkflow", workflowID);
        request.getPortletSession().setAttribute("detailsruntime", runtimeID);

        setRequestAttribute(request.getPortletSession(),"rtid",PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).getText());
        setRequestAttribute(request.getPortletSession(),"workflow",PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID));
        //
        if (PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).getJobsStatus().isEmpty()) {
            Hashtable prp=new Hashtable();
            prp.put("url",PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getWfsID());
            ServiceType st=InformationBase.getI().getService("wfs","portal",prp,new Vector());
            try {
                PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                ComDataBean cmb = new ComDataBean();
                cmb.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                cmb.setUserID(userID);
                cmb.setWorkflowID(workflowID);
                cmb.setWorkflowRuntimeID(runtimeID);
                //
                // In case of getmax overrunning it is need to query job status in more steps.
                int getmax = 2500;
                // It steps from cnt 0 to empty status or when the returned vector (retVector) size is less than getmax
                long cnt = 0;
                int retCnt = getmax;
                //
                while (retCnt == getmax) {
                    // Using ComDataBean.setSize() for stepping query cycle 
                    cmb.setSize(cnt);
                    //
                    Vector<JobInstanceBean> retVector = new Vector<JobInstanceBean>();
                    retVector = pc.getWorkflowInstanceJobs(cmb);
                    // System.out.println("wspgrade doInstanceDetails retVector.size() : " + retVector.size());
                    for (int i = 0; i < retVector.size(); i++) {
                        JobInstanceBean tmp = retVector.get(i);
                        // System.out.println("wspgrade doInstanceDetails tmp : " + tmp.getJobID() +", "+ tmp.getPID() +", "+ tmp.getStatus() +", "+ tmp.getResource());
                      //  PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getRuntime(runtimeID).addJobbStatus(tmp.getJobID(), "" + tmp.getPID(), "" + tmp.getStatus(), tmp.getResource(), -1);
                    }
                    //
                    retCnt = retVector.size();
                    cnt++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setRequestAttribute(request.getPortletSession(),"instJobList",PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow")).getRuntime(request.getParameter("rtid")).getCollectionJobsStatus());
        doDetails(request,response);
    }

    /**
     * Deletion of a concrete workflow (together with all instances)
     */
    public void doDelete(ActionRequest request, ActionResponse response) throws PortletException
    {
        WorkflowData wData=PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow"));

        String userID=request.getRemoteUser();
//wfs
        Hashtable hsh=new Hashtable();
        hsh.put("url",wData.getWfsID());
        ServiceType st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());
        try
        {
            PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp=new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(userID);
            tmp.setWorkflowID(wData.getWorkflowID());
            pc.deleteWorkflow(tmp);
        }
        catch(Exception e){e.printStackTrace();}
//storage
        try
        {
            hsh=new Hashtable();
            hsh.put("url",wData.getStorageID());
            st=InformationBase.getI().getService("storage","portal",hsh,new Vector());
            PortalStorageClient ps=(PortalStorageClient)Class.forName(st.getClientObject()).newInstance();
            ps.setServiceURL(st.getServiceUrl());
            ps.setServiceID(st.getServiceID());
            ComDataBean tmp=new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(userID);
            tmp.setWorkflowID(wData.getWorkflowID());
            ps.deleteWorkflow(tmp);
        }
        catch(Exception e){e.printStackTrace();}
//registry
        PortalCacheService.getInstance().getUser(userID).deleteWorkflow(wData.getWorkflowID());

    }

    /**
     * Displaying workflow configuration information
     */
    public void doWorkflowInfo(ActionRequest request, ActionResponse response) throws PortletException {
        WorkflowInfo(request, response);
    }

    /**
     * Workflow exportalas a repository-ba (doExport)
     */
    public void doExport(ActionRequest request, ActionResponse response) throws PortletException {
        String msg = new String("");
        try{
            RepositoryWorkflowBean bean = WorkflowExportUtils.getInstance().getBeanFromRequest(request.getParameterMap(),request.getRemoteUser());
            msg = WorkflowExportUtils.getInstance().exportWorkflow(bean);
        }catch (Exception e){
            e.printStackTrace();
            msg = e.getLocalizedMessage();
            setRequestAttribute(request.getPortletSession(),"msg",msg);
        }
        setRequestAttribute(request.getPortletSession(),"msg",msg);
    }

    /**
     * Workflow graph csange 
     */
    public void doNewGraf(ActionRequest request, ActionResponse response) throws PortletException{
        RealWorkflowUtil.changeGraph(request, response);
    }

    /**
     * Workflow template change
     */
    public void doNewTemplate(ActionRequest request, ActionResponse response) throws PortletException
    {
  //      doDetails(req,response);
    //    RealWorkflowUtils.getInstance().deleteWorkflowInstance(req.getRemoteUser(), req.getParameter("workflow"), req.getParameter("rtid"));
        String newtemplatename=""+request.getParameter("pawkf");
        String user=""+request.getRemoteUser();
       // ((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getOutput(""+pParams.get("id")).getData().put("parametric", ""+pParams.get("act"));
       // System.out.println("XXX "+pParams.get("j"));



        String wfsID=PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).getWfsID();
        Hashtable hsh=new Hashtable();
        hsh.put("url", wfsID);
        ServiceType st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());
        try
        {
            PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(wfsID);
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp=new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(user);
            tmp.setWorkflowID(workflow);

            tmp.setGraf(newtemplatename);
            String newgrafname=pc.setNewTemplate(tmp);
            PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).setTemplate(newtemplatename);
            PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).setGraf(newgrafname);
            doConfigure(request,response);
        }catch(Exception e){e.printStackTrace();}

    }

    /**
     * Workflow exportalas az ETICS rendszerbe (singlenode & multinode tipusu WF)
    public void doExportEtics(ActionRequest request, ActionResponse response) throws PortletException {
        String msg = new String("");
        String retString = "";
        try {
            String portalID = new String("http://hostURL:8080/portal30");
            String wfsServiceURL = new String("http://hostURL:8080/wfs");
            String wfsServiceID = new String("/services/urn:wfswfiservice");
            String clientObject = new String("hu.sztaki.lpds.wfs.net.wsaxis13.WfiWfsClientImpl");
            //

            WorkflowData wData=PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("workflow"));
            //

            Hashtable hsh = new Hashtable();
            hsh.put("url", wData.getWfsID());// PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflow(wfID).getWfsID()
            ServiceType st = InformationBase.getI().getService("wfs", "wfi", hsh, new Vector());


            WfiWfsClient client = (WfiWfsClient) Class.forName(st.getClientObject()).newInstance();
            client.setServiceURL(st.getServiceUrl());
            client.setServiceID(st.getServiceID());
            //
            ComDataBean comBean = new ComDataBean();
            comBean.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));//portalID
            comBean.setWorkflowtype(wData.getWorkflowType());//"workflowType"
            comBean.setWorkflowID(wData.getWorkflowID());//"workflowID"
            comBean.setGraf(wData.getGraf());//"grafID"
            comBean.setUserID(request.getRemoteUser());//"userID"
            //
            retString = client.getWfiXML(comBean);
            msg=EticsCacheService.getInstance().processWorkflowXml(retString, request.getRemoteUser());
            
            //
        } catch (Exception e) {
            e.printStackTrace();
            msg = e.getLocalizedMessage();
        }
        setRequestAttribute(request.getPortletSession(),"msg",msg);
    }
     */

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
                String wftype;
                PortletSession ps = request.getPortletSession();                
                if (request.getPortletSession().getAttribute("cworkflow") == null) {
                    System.out.println("RealWFPortlet-serveresource- request.getPortletSession().getAttribute(cworkflow)==null !!!!!!!! try cworkflow1");
                    System.out.println("cworkflow1:"+ps.getAttribute("cworkflow1", ps.APPLICATION_SCOPE));
                    workflowName = "" + ps.getAttribute("cworkflow1", ps.APPLICATION_SCOPE);
                    if (ps.getAttribute("cworkflow1", ps.APPLICATION_SCOPE) != null) {
                        request.getPortletSession().setAttribute("cworkflow", workflowName);
                    }
                }
                wftype = PortalCacheService.getInstance().getUser(username).getWorkflow(workflowName).getWorkflowType();                           
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


}

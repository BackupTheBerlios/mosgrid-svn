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
package hu.sztaki.lpds.pgportal.portlets.workflow;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.workflow.Sorter;
import hu.sztaki.lpds.pgportal.service.workflow.UserQuotaUtils;

import hu.sztaki.lpds.pgportal.service.workflow.WorkflowExportUtils;
import hu.sztaki.lpds.pgportal.util.stream.HttpDownload;
import hu.sztaki.lpds.storage.com.StoragePortalCopyWorkflowBean;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import javax.portlet.*;
import java.io.*;
import java.util.*;

/**
 * Portlet class for managing Template worflows 
 *
 * @author krisztian karoczkai
 */
public class TemplateManagerPortlet extends GenericWSPgradePortlet
{
    private PortletContext pContext;
    private String workflow="";
    private String jsp="/jsp/workflow/templatelist.jsp";
    
/**
 * Portlet initialization
 */
    @Override
    public void init(PortletConfig config) throws PortletException 
    {
        super.init(config);
        pContext = config.getPortletContext();
    }

/**
 * Data transmission for visualization of Portlet UI
 */
    @Override
    public void doView(RenderRequest req, RenderResponse response) throws PortletException,IOException
    {
        response.setContentType("text/html");
        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(req, response);
            return;
        }
        openRequestAttribute(req);
        try
        {
            Enumeration enm=PortalCacheService.getInstance().getUser(req.getRemoteUser()).getTemplateWorkflows().keys();
            String key="";
            while(enm.hasMoreElements())
            {
                key=""+enm.nextElement();
                PortalCacheService.getInstance().getUser(req.getRemoteUser()).getTemplateWorkflow(key).setTmp("0");
            }
            
            enm=PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflows().keys();
            while(enm.hasMoreElements())
            {
                key=""+enm.nextElement();
                if(
                        (PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflow(key).getRunningStatus()>0)&&
                        (!PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflow(key).getTemplate().equals(""))&&
                        (!PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflow(key).getTemplate().equals("--"))
                  )
                {
                    PortalCacheService.getInstance().getUser(req.getRemoteUser()).getTemplateWorkflow(PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflow(key).getTemplate()).setTmp("1");
                }
            }

            if (req.getAttribute("workflow") == null)
                jsp="/jsp/workflow/templatelist.jsp";
            PortletRequestDispatcher dispatcher;
            dispatcher = pContext.getRequestDispatcher(jsp);
            // Sorting...
            req.setAttribute("aWorkflowList", Sorter.getInstance().sortFromKeys(PortalCacheService.getInstance().getUser(req.getRemoteUser()).getTemplateedConcrateWorkflows()));
            req.setAttribute("aWorkflowListSorted", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(req.getRemoteUser()).getTemplateWorkflows()));
            req.setAttribute("cWorkflowList", Sorter.getInstance().sortFromKeys(PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflows()));
            // Sorting...
            req.setAttribute("puser", req.getRemoteUser());
            req.setAttribute("portalID", PropertyLoader.getInstance().getProperty("service.url"));
            ServiceType st=InformationBase.getI().getService("storage","portal",new Hashtable(),new Vector());
            req.setAttribute("storageID", st.getServiceUrl());
            dispatcher.include(req, response);
        }
        catch (IOException e){throw new PortletException("JSPPortlet.doView exception", e);}
        cleanRequestAttribute(req.getPortletSession());
    }           
    
    
    /**
     * Deleting Template workflow
     */
    public void doDelete(ActionRequest request, ActionResponse response) throws PortletException
    {     
        String storageID = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getTemplateWorkflow(request.getParameter("workflow")).getStorageID();
        ComDataBean cmd=new ComDataBean();
        cmd.setWorkflowID(request.getParameter("workflow"));
        cmd.setUserID(request.getRemoteUser());
        cmd.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
        cmd.setStorageURL(storageID);
        try
        {
//wfs            
            PortalWfsClient pc=(PortalWfsClient)Class.forName(InformationBase.getI().getService("wfs","portal", new Hashtable(),new Vector()).getClientObject()).newInstance();
            pc.setServiceURL(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getTemplateWorkflow(request.getParameter("workflow")).getWfsID());
            pc.setServiceID(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getTemplateWorkflow(request.getParameter("workflow")).getWfsIDService());
            pc.deleteWorkflow(cmd);
            Enumeration enm=PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflows().keys();
            String key="";
            while(enm.hasMoreElements())
            {
                key=""+enm.nextElement();
                if(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(key).getTemplate().equals(request.getParameter("workflow")))
                PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflows().remove(key);
            }
//storage            
            Hashtable hsh = new Hashtable();
            hsh.put("url", storageID);
            ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
            PortalStorageClient ps = (PortalStorageClient)Class.forName(st.getClientObject()).newInstance();
            ps.setServiceURL(st.getServiceUrl());
            ps.setServiceID(st.getServiceID());
            ComDataBean tmp = new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(request.getRemoteUser());
            tmp.setWorkflowID(request.getParameter("workflow"));
            ps.deleteWorkflow(tmp);
//registry                    
            PortalCacheService.getInstance().getUser(request.getRemoteUser()).getTemplateWorkflows().remove(request.getParameter("workflow"));
            setRequestAttribute(request.getPortletSession(),"msg","template.delete.ok");
        }
        catch(Exception e){setRequestAttribute(request.getPortletSession(),"msg","template.delete.error");e.printStackTrace();}

    }
    
    
    /**
     * Displaying of Template workflow page and assembling data composition
     */
    public void doCreateAbstrackt(ActionRequest request, ActionResponse response) throws PortletException
    {
// is it free the selected name? 
        if(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getTemplateWorkflow(request.getParameter("pntname"))!=null)
        {
            setRequestAttribute(request.getPortletSession(),"msg","template.workflow.exist");
            return;
        }
// is it free the selected name? 
        if(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(request.getParameter("pntname"))!=null)
        {
            setRequestAttribute(request.getPortletSession(),"msg","template.workflow.exist");
            return;
        }
// is it given the selected name and description? 
        if(request.getParameter("pntname").equals(""))
        {
            setRequestAttribute(request.getPortletSession(),"msg","template.name.notset");
            return;
        }
        if(request.getParameter("pntdesc").equals(""))
        {
            setRequestAttribute(request.getPortletSession(),"msg","template.desc.notset");
            return;
        }
        // quota overrun review
        if (UserQuotaUtils.getInstance().userQuotaIsFull(request.getRemoteUser()))
        {
            setRequestAttribute(request.getPortletSession(),"msg","portal.RealWorkflowPortlet.quotaisoverfull");
        }
        else
        {
            Hashtable hsh=new Hashtable();
            jsp="/jsp/workflow/cabsfree.jsp";
            if(request.getParameter("pfrom").equals("c")) {
                workflow=request.getParameter("pcw");
                setRequestAttribute(request.getPortletSession(),"open","true");
                jsp="/jsp/workflow/cabs.jsp";
                hsh.put("url",PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow).getWfsID());
            } else if(request.getParameter("pfrom").equals("t")) {
                workflow=request.getParameter("ptw");
                hsh.put("url",PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow).getWfsID());
            } else {
                workflow=request.getParameter("pt");
                hsh.put("url",PortalCacheService.getInstance().getUser(request.getRemoteUser()).getTemplateWorkflow(workflow).getWfsID());
            }
            
            ServiceType st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());
            try {
                PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                ComDataBean tmp=new ComDataBean();
                tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                tmp.setUserID(request.getRemoteUser());
                tmp.setWorkflowID(workflow);
                Vector vtmp=pc.getWorkflowConfigData(tmp);
                setRequestAttribute(request.getPortletSession(),"jobs",vtmp);
                setRequestAttribute(request.getPortletSession(),"workflow",workflow);
                setRequestAttribute(request.getPortletSession(),"size",""+vtmp.size());
                setRequestAttribute(request.getPortletSession(),"newWorkflow",request.getParameter("pntname"));
                setRequestAttribute(request.getPortletSession(),"pfrom",request.getParameter("pfrom"));
                setRequestAttribute(request.getPortletSession(),"pdesc",request.getParameter("pntdesc"));
            } catch(Exception e){e.printStackTrace();}            
        }
    }
    
    /**
     * Creating and saving Template workflow
     */
    public void doCreateAWorkflow(ActionRequest request, ActionResponse response) throws PortletException
    {
        Enumeration enm=request.getParameterNames();
        Vector res=new Vector();
        Vector res0=new Vector();
        
//query
        workflow=request.getParameter("workflow");
        Hashtable hsh=new Hashtable();
        if (request.getParameter("pfrom").equals("c")) {
            hsh.put("url",PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow).getWfsID());
        } else if(request.getParameter("pfrom").equals("t")) {
            hsh.put("url",PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow).getWfsID());
        } else {
            hsh.put("url",PortalCacheService.getInstance().getUser(request.getRemoteUser()).getTemplateWorkflow(workflow).getWfsID());
        }
        ServiceType st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());

        try
        {
            PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp=new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(request.getRemoteUser());
            tmp.setWorkflowID(workflow);
            res=pc.getWorkflowConfigData(tmp);
        }
        catch(Exception e){e.printStackTrace();}

//cleanup
            for(int i0=0;i0<res.size();i0++)
            {
                ((JobPropertyBean)res.get(i0)).setExe(new HashMap());
                for(int i1=0;i1<((JobPropertyBean)res.get(i0)).getInputs().size();i1++)
                {((PortDataBean)(((JobPropertyBean)res.get(i0)).getInputs()).get(i1)).setData(new HashMap());}

                for(int i1=0;i1<((JobPropertyBean)res.get(i0)).getOutputs().size();i1++)
                {((PortDataBean)(((JobPropertyBean)res.get(i0)).getOutputs()).get(i1)).setData(new HashMap());}
            }
        
//setting        
        while(enm.hasMoreElements())
        {
            String key=""+enm.nextElement();
            if(key.startsWith("job"))
            {
                int jobindex=Integer.parseInt(key.substring(3, key.indexOf("_")));
                String[] sub=key.split("_");
                if(sub.length==2)
                {
                    ((JobPropertyBean)res.get(jobindex)).addExe(sub[1], request.getParameter(key));
                }
                else
                {
                    if(sub[1].startsWith("input"))
                    {
                        ((JobPropertyBean)res.get(jobindex)).getInput(sub[1].substring(5)).getData().put(sub[2],request.getParameter(key));
                    }
                    if(sub[1].startsWith("output"))
                    {
                        ((JobPropertyBean)res.get(jobindex)).getOutput(sub[1].substring(6)).getData().put(sub[2],request.getParameter(key));
                    }
                }
            }
            if(key.startsWith("label"))
            {
                int jobindex=Integer.parseInt(key.substring(9, key.indexOf("_",9)));
                String[] sub=key.split("_");
                if(sub.length==3)
                    ((JobPropertyBean)res.get(jobindex)).addLabel(sub[2], request.getParameter(key));
                else
                {
                    if(sub[2].startsWith("input"))
                        ((JobPropertyBean)res.get(jobindex)).getInput(sub[2].substring(5)).getLabel().put(sub[3],request.getParameter(key));
                    if(sub[2].startsWith("output"))
                        ((JobPropertyBean)res.get(jobindex)).getOutput(sub[2].substring(6)).getLabel().put(sub[3],request.getParameter(key));
                }
            }
            if(key.startsWith("desc"))
            {
                int jobindex=Integer.parseInt(key.substring(8, key.indexOf("_",8)));
                String[] sub=key.split("_");
                if(sub.length==3)
                    ((JobPropertyBean)res.get(jobindex)).addDesc0(sub[2], request.getParameter(key));
                else
                {
                    if(sub[2].startsWith("input"))
                        ((JobPropertyBean)res.get(jobindex)).getInput(sub[2].substring(5)).getDesc().put(sub[3],request.getParameter(key));
                    if(sub[2].startsWith("output"))
                        ((JobPropertyBean)res.get(jobindex)).getOutput(sub[2].substring(6)).getDesc().put(sub[3],request.getParameter(key));
                }
            }
            if(key.startsWith("inh"))
            {
                int jobindex=Integer.parseInt(key.substring(7, key.indexOf("_",8)));
                String[] sub=key.split("_");
                if(sub.length==3)
                {
                    ((JobPropertyBean)res.get(jobindex)).addInherited(sub[2], request.getParameter(key));
                }
                else
                {
                    if(sub[2].startsWith("input"))
                    {
                        ((JobPropertyBean)res.get(jobindex)).getInput(sub[2].substring(5)).getInherited().put(sub[3],request.getParameter(key));
                    }
                    if(sub[2].startsWith("output"))
                    {
                        ((JobPropertyBean)res.get(jobindex)).getOutput(sub[2].substring(6)).getInherited().put(sub[3],request.getParameter(key));
                    }
                }
            }
            
        }
//logg
/*        
        for(int i=0; i<res.size();i++)
        {
            JobPropertyBean jpb=(JobPropertyBean)res.get(i);
            Iterator it=jpb.getExe().keySet().iterator();
            System.out.println("********JOB("+i+"/"+res.size()+"="+jpb.getExe().size()+")*****");
            System.out.println("--exe--");
            while(it.hasNext())
            {
                Object key=it.next();
                System.out.println("-"+key+":"+jpb.getExe().get(key)+":"+jpb.getLabel().get(key)+":"+jpb.getInherited().get(key));
            }

            for(int j=0;j<jpb.getInputs().size();j++)
            {
                PortDataBean pd=(PortDataBean)jpb.getInputs().get(i);
                it=pd.getData().keySet().iterator();
                System.out.println("********INPUT("+j+"/"+jpb.getInputs().size()+")*****");
                System.out.println("--input--");
                while(it.hasNext())
                {
                    Object key=it.next();
                    System.out.println("-"+key+":"+pd.getData().get(key)+":"+pd.getLabel().get(key)+":"+pd.getDesc().get(key)+":"+pd.getInherited().get(key));
                }
            }
        }
 */
//sending
        st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());
        try
        {
            String p=InformationBase.getI().getService("storage","portal",new Hashtable(),new Vector()).getServiceUrl(); 
            
            PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp=new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(request.getRemoteUser());
            tmp.setWorkflowID(request.getParameter("newaworkflow"));
            tmp.setTxt(request.getParameter("pdesc"));
            tmp.setParentWorkflowID(workflow);
            tmp.setTyp(new Integer(2));
            tmp.setStorageURL(p);
            pc.setWorkflowConfigData(tmp,res);
            if (request.getParameter("pfrom").equals("c")) {
                PortalCacheService.getInstance().getUser(request.getRemoteUser()).addTemplateWorkflows(request.getParameter("newaworkflow"), request.getParameter("pdesc"), st.getServiceUrl(), p, PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow).getGraf());
            } else if (request.getParameter("pfrom").equals("t")) {
                PortalCacheService.getInstance().getUser(request.getRemoteUser()).addTemplateWorkflows(request.getParameter("newaworkflow"), request.getParameter("pdesc"), st.getServiceUrl(), p, PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(workflow).getGraf());
            } else {
                PortalCacheService.getInstance().getUser(request.getRemoteUser()).addTemplateWorkflows(request.getParameter("newaworkflow"), request.getParameter("pdesc"), st.getServiceUrl(), p, PortalCacheService.getInstance().getUser(request.getRemoteUser()).getTemplateWorkflow(workflow).getGraf());
            }                       
            
            hsh=new Hashtable();
//            hsh.put("url",PortalCacheService.getInstance().getUser(req.getRemoteUser()).getWorkflow(workflow).getStorageID());
            st=InformationBase.getI().getService("storage","portal",hsh,new Vector());
            PortalStorageClient ps=(PortalStorageClient)Class.forName(st.getClientObject()).newInstance();
            ps.setServiceURL(st.getServiceUrl());
            ps.setServiceID(st.getServiceID());
            StoragePortalCopyWorkflowBean bean = new StoragePortalCopyWorkflowBean();
            bean.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            bean.setUserID(request.getRemoteUser());
            bean.setSourceWorkflowID(request.getParameter("workflow"));
            bean.setDestinWorkflowID(request.getParameter("newaworkflow"));
            Hashtable copyHash = new Hashtable();
            copyHash.put("all","all");
            // create copyHash
            /*
            String swf = bean.getSourceWorkflowID();
            String dwf = bean.getDestinWorkflowID();
            for(int ji=0; ji < res.size(); ji++) {
                JobPropertyBean jobBean = (JobPropertyBean) res.get(ji);
                String jobName = jobBean.getName();
                // parse binary
                if (jobBean.getExe().containsKey("binary")) {
                    copyHash.put("/" + swf + "/" + jobName + "/execute.bin", "/" + dwf + "/" + jobName + "/execute.bin");
                    System.out.println("/" + swf + "/" + jobName + "/execute.bin==>/" + dwf + "/" + jobName + "/execute.bin");
                }
                // parse input
                for(int pi=0; pi < jobBean.getInputs().size(); pi++) 
                {
                    String portName = String.valueOf(((PortDataBean)(jobBean.getInputs()).get(pi)).getSeq());
                    if (((PortDataBean)(jobBean.getInputs()).get(pi)).getData().containsKey("file")) 
                    {
                        copyHash.put("/" + swf + "/" + jobName + "/inputs/" + portName + "/", "/" + dwf + "/" + jobName + "/inputs/" + portName + "/");
                        System.out.println("/" + swf + "/" + jobName + "/inputs/" + portName + "/==>/" + dwf + "/" + jobName + "/inputs/" + portName + "/");
                    }   
                }
                // parse output
                for(int po=0; po < jobBean.getOutputs().size(); po++) {
                    String portName = String.valueOf(((PortDataBean)(jobBean.getOutputs()).get(po)).getSeq());
                    if (((PortDataBean)(jobBean.getOutputs()).get(po)).getData().containsKey("file")) {
                        copyHash.put("/" + swf + "/" + jobName + "/outputs/" + portName + "/", "/" + dwf + "/" + jobName + "/outputs/" + portName + "/");
                        System.out.println("/" + swf + "/" + jobName + "/outputs/" + portName + "/==>/" + dwf + "/" + jobName + "/outputs/" + portName + "/");
                    }
                }
            }
            */
            bean.setCopyHash(copyHash);
            // System.out.println("copyHash: " + copyHash);
            boolean ret = ps.copyWorkflowFiles(bean);
        }
        catch(Exception e){e.printStackTrace();}
        jsp="/jsp/workflow/templatelist.jsp";
        setRequestAttribute(request.getPortletSession(),"msg","workflow.template.created");
    }

    /**
     * Displaying of Template workflow list
     */
    public void doList(ActionRequest request, ActionResponse response) throws PortletException
    {
        jsp="/jsp/workflow/templatelist.jsp";
    }
    
    /**
     * Workflow export to repository (doExport)
     */
    public void doExport(ActionRequest request, ActionResponse response) throws PortletException {
        String msg = new String("");
        try {
            RepositoryWorkflowBean bean = WorkflowExportUtils.getInstance().getBeanFromRequest(request.getParameterMap(),request.getRemoteUser());
            msg = WorkflowExportUtils.getInstance().exportWorkflow(bean);
        } catch (Exception e) {
            e.printStackTrace();
            msg = e.getLocalizedMessage();
            setRequestAttribute(request.getPortletSession(),"msg",msg);
        }
        setRequestAttribute(request.getPortletSession(),"msg",msg);
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException
    {
// ajax help
        if(request.getParameter("helptext")!=null) {
            super.serveResource(request, response);
            return;
        }

        response.setContentType("application/zip");
        response.setProperty("Content-Disposition", "inline; filename=\"" + request.getParameter("workflowID")+"_template.zip\"");
        try{HttpDownload.fileDownload("abst", request, response);}
        catch(Exception e){throw new PortletException("com error");}

    }


}

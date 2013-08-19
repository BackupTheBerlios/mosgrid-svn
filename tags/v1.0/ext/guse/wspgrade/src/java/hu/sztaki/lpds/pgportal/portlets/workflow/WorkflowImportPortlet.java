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
import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import hu.sztaki.lpds.pgportal.service.workflow.UserQuotaUtils;
import hu.sztaki.lpds.pgportal.service.workflow.WorkflowUpDownloadUtils;
import hu.sztaki.lpds.pgportal.user.UserHandler;
import hu.sztaki.lpds.repository.inf.PortalRepositoryClient;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.portlet.*;

/**
 * Portlet for repository workflow item import and deletion
 *
 * @author lpds
 */
public class WorkflowImportPortlet extends GenericWSPgradePortlet
{
    
    private PortletContext pContext;
    
    private String jsp = new String("/jsp/workflow/wfimport.jsp");
    
    private Hashtable typeList = new Hashtable();
    
    // File separator (for example, "/")
    private String sep;
    
    public WorkflowImportPortlet() {
        typeList = new Hashtable();
        typeList.put("appl", "text.wfimport.application");
        typeList.put("proj", "text.wfimport.project");
        typeList.put("real", "text.wfimport.concrete");
        typeList.put("abst", "text.wfimport.template");
        typeList.put("graf", "text.wfimport.graph");
    }
    
    /**
     * Portlet initializing
     */
    @Override
    public void init(PortletConfig config) throws PortletException {
        super.init(config);
        pContext = config.getPortletContext();
        
    }
    
    /**
     * Uploading basic data
     */
    private void LoadProperty() {
        sep = FileUtils.getInstance().getSeparator();
    }
    
    /**
     * Data transmission to visualize Portlet UI
     */
    @Override
    public void doView(RenderRequest req, RenderResponse res) throws PortletException,IOException
    {
        res.setContentType("text/html");


        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(req, res);
            return;
        }

        LoadProperty();
        openRequestAttribute(req);

        res.getWriter().println(UserHandler.getI().getUserName(req.getRemoteUser()));
        boolean detetableItem=UserHandler.getI().isUserInRole(req.getRemoteUser(), "Administrator");
        req.setAttribute("deletable", detetableItem);
        req.setAttribute("userid", req.getRemoteUser());
/*        // System.out.println("call doView...");
        // set root things
        if (isRoot(req.getRemoteUser())) {
            req.setAttribute("isroot", "true");
        } else {
            req.setAttribute("isroot", "false");
            req.setAttribute("edelete", "disabled=\"true\"");
        }
 *
 */
        try {
            // get quota hash from repository
            Hashtable quotahash = getQuotaHashFromRepository();
            // set quota numbers
            req.setAttribute("quota", ""+quotahash.get("quota"));
            req.setAttribute("quotamax", ""+quotahash.get("quotamax"));
            Long quotaPercent = new Long(new Long(""+quotahash.get("quota")).longValue() * 100 / new Long(""+quotahash.get("quotamax")).longValue());
            req.setAttribute("quotapercent", quotaPercent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // dispatch
        try {
            PortletRequestDispatcher dispatcher;
            dispatcher = getPortletContext().getRequestDispatcher(jsp);
            dispatcher.include(req, res);
        } catch (IOException e) {
            throw new PortletException("JSPPortlet.doView exception", e);
        }

        cleanRequestAttribute(req.getPortletSession());
        
    }
    
    
    /**
     * Listing stored workflow.
     */
    public void doList(ActionRequest request, ActionResponse response) throws PortletException
    {
// System.out.println("call doList...");
        Vector wfList = new Vector();
        try {
            // get worklow type from session or req...
            if (request.getPortletSession().getAttribute("wfType") == null) {
                request.getPortletSession().setAttribute("wfType", new String("appl"));
            }
            String wfType = (String) request.getParameter("wfType");
            if (wfType == null) {
                wfType = (String) request.getPortletSession().getAttribute("wfType");
            }
            request.getPortletSession().setAttribute("wfType", wfType);
            // System.out.println("wfType : " + wfType);
            setRequestAttribute(request.getPortletSession(),"s" + wfType, " selected=\"true\"");
            setRequestAttribute(request.getPortletSession(),"wfimpListType", wfType);
            setRequestAttribute(request.getPortletSession(),"wfimpListName", typeList.get(wfType));
            // get repository workflow item list from wfs...
            wfList = getRepositoryItemListFromWfs(wfType, new Long(0));
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getLocalizedMessage();
            // msg = PortalMessageService.getI().getMessage("portal.WorkflowImportPortlet.doImport.notsuccess");
            setRequestAttribute(request.getPortletSession(),"wfimpmsg", msg);
        }
        // workflow list
        setRequestAttribute(request.getPortletSession(),"wfList", wfList);
        setRequestAttribute(request.getPortletSession(),"wfListSize",""+ wfList.size());
    }
    
    /**
     * Action invocation to decide a worflow import (doImport) or a workflow deletion (doDelete) from repository.
     */
    public void doJustDoIt(ActionRequest request, ActionResponse response) throws PortletException {
        // System.out.println("doJustDoIt()...");
        String msg = "";
        String methode = request.getParameter("impMethode");
        // System.out.println("impMethode : " + methode);
        if ((methode == null) || ("".equals(methode))) {
            methode = new String("import");
        }
        if (methode.equalsIgnoreCase("import")) {
            doImport(request, response);
        } else if (methode.equalsIgnoreCase("delete")) {
            if (isRoot(request.getRemoteUser())) {
                doDelete(request, response);
            } else {
                msg = PortalMessageService.getI().getMessage("portal.WorkflowImportPortlet.doImport.noright");
                setRequestAttribute(request.getPortletSession(),"wfimpmsg", msg);
            }
        } else {
            msg = PortalMessageService.getI().getMessage("portal.WorkflowImportPortlet.doImport.nomethode");
            setRequestAttribute(request.getPortletSession(),"wfimpmsg", msg);
        }
    }
    
    /**
     * Workflow import from repository (doImport)
     */
    public void doImport(ActionRequest request, ActionResponse response) throws PortletException {
        // System.out.println("doImport()...");
        String msg = new String("");
        try {
            if (UserQuotaUtils.getInstance().userQuotaIsNotFull(request.getRemoteUser())) {
                // the given name of graph workflow 
                String newGrafName = request.getParameter("wfimp_newGrafName");
                // the given name of abstract workflow
                String newAbstName = request.getParameter("wfimp_newAbstName");
                // the given name of uploaded real, concrete workflow
                String newRealName = request.getParameter("wfimp_newRealName");
                if (newGrafName == null) {
                    newGrafName = new String("");
                }
                if (newAbstName == null) {
                    newAbstName = new String("");
                }
                if (newRealName == null) {
                    newRealName = new String("");
                }
                if (!"".equals(newAbstName)) {
                    if (newAbstName.equalsIgnoreCase(newRealName)) {
                        // throw new Exception("Template workflow name equal concrete workflow name");
                        msg = new String("Template workflow name equal concrete workflow name");
                    }
                }
                //
            /*
            Enumeration enumParam = request.getParameterNames();
            while(enumParam.hasMoreElements()) {
                String paramName = (String) enumParam.nextElement();
                String paramValue = (String) request.getParameter(paramName);
                // System.out.println("paramName : " + paramName + " paramValue : " + paramValue);
            }
            Enumeration enumAttr = request.getAttributeNames();
            while(enumAttr.hasMoreElements()) {
                String attrName = (String) enumAttr.nextElement();
                Object attrValue = request.getAttribute(attrName);
                // System.out.println("attrName : " + attrName + " attrValue : " + attrValue);
            }
             */
                String impWfType = request.getParameter("impWfType");
                // System.out.println("impWfType : " + impWfType);
                String impItemId = request.getParameter("impItemId");
                // System.out.println("impItemId : " + impItemId);
                //
                // get repository workflow item list from wfs...
                Vector wfList = getRepositoryItemListFromWfs(impWfType, new Long(impItemId));
                // System.out.println("wfList : " + wfList);
                RepositoryWorkflowBean selectedBean = (RepositoryWorkflowBean) wfList.get(0);
                if (selectedBean == null) {
                    throw new Exception("Not valid selectedBean !");
                }
                // set import properties...
                String portalID = PropertyLoader.getInstance().getProperty("service.url");
                String storageID = WorkflowUpDownloadUtils.getInstance().getStorageID();
                String wfsID = WorkflowUpDownloadUtils.getInstance().getWfsID();
                String userID = (String) request.getRemoteUser();
                selectedBean.setPortalID(portalID);
                selectedBean.setStorageID(storageID);
                selectedBean.setWfsID(wfsID);
                selectedBean.setUserID(userID);
                selectedBean.setNewGrafName(newGrafName);
                selectedBean.setNewAbstName(newAbstName);
                selectedBean.setNewRealName(newRealName);
                // System.out.println("selBean zip path : " + selectedBean.getZipRepositoryPath());
                //
                // import item from repository...
                Hashtable hsh = new Hashtable();
                // hsh.put("url", bean.getWfsID());
                ServiceType st = InformationBase.getI().getService("repository", "portal", hsh, new Vector());
                PortalRepositoryClient repoClient = (PortalRepositoryClient) Class.forName(st.getClientObject()).newInstance();
                repoClient.setServiceURL(st.getServiceUrl());
                repoClient.setServiceID(st.getServiceID());
                String retStr = repoClient.importWorkflow(selectedBean);
                // System.out.println("retStr : " + retStr);
                if (retStr == null) {
                    throw new Exception("Not found repository client !");
                }
                if (retStr.startsWith("workflow upload successf")) {
                    // Message by reason servlet...
                    retStr = new String("");
                }
                retStr = retStr.replaceAll("\n","<br />");
                if (!"".equals(retStr)) {
                    msg = PortalMessageService.getI().getMessage("portal.WorkflowImportPortlet.doImport.error");
                } else {
                    msg = PortalMessageService.getI().getMessage("portal.WorkflowImportPortlet.doImport.success");
                }
            } else {
                // no more quota
                msg = PortalMessageService.getI().getMessage("portal.RealWorkflowPortlet.quotaisoverfull");
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = PortalMessageService.getI().getMessage("portal.WorkflowImportPortlet.doImport.notsuccess") + " : " + msg;
            // System.out.println("msg2: " + msg);
            setRequestAttribute(request.getPortletSession(),"wfimpmsg", msg);
        }
        setRequestAttribute(request.getPortletSession(),"wfimpmsg", msg);
    }
    
    /**
     * Workflow deletion from repository(doDelete)
     */
    public void doDelete(ActionRequest request, ActionResponse response) throws PortletException {
        // System.out.println("doDelete()...");
        String msg = new String("");
        try {
            String impWfType = request.getParameter("impWfType");
            // System.out.println("impWfType : " + impWfType);
            String impItemId = request.getParameter("impItemId");
            // System.out.println("impItemId : " + impItemId);
            //
            // get repository workflow item list from wfs...
            Vector wfList = getRepositoryItemListFromWfs(impWfType, new Long(impItemId));
            // System.out.println("wfList : " + wfList);
            RepositoryWorkflowBean selectedBean = (RepositoryWorkflowBean) wfList.get(0);
            if (selectedBean == null) {
                throw new Exception("Not valid selectedBean !");
            }
            // set delete properties...
            String portalID = PropertyLoader.getInstance().getProperty("service.url");
            String storageID = WorkflowUpDownloadUtils.getInstance().getStorageID();
            String wfsID = WorkflowUpDownloadUtils.getInstance().getWfsID();
            String userID = (String) request.getRemoteUser();
            selectedBean.setPortalID(portalID);
            selectedBean.setStorageID(storageID);
            selectedBean.setWfsID(wfsID);
            selectedBean.setUserID(userID);
            // System.out.println("selBean zip path : " + selectedBean.getZipRepositoryPath());
            //
            // delete item from repository...
            Hashtable hsh = new Hashtable();
            // hsh.put("url", bean.getWfsID());
            ServiceType st = InformationBase.getI().getService("repository", "portal", hsh, new Vector());
            PortalRepositoryClient repoClient = (PortalRepositoryClient) Class.forName(st.getClientObject()).newInstance();
            repoClient.setServiceURL(st.getServiceUrl());
            repoClient.setServiceID(st.getServiceID());
            String retStr = repoClient.deleteWorkflow(selectedBean);
            // System.out.println("retStr : " + retStr);
            if (retStr == null) {
                throw new Exception("Not found repository client !");
            }
            retStr = retStr.replaceAll("\n","<br />");
            if (!"".equals(retStr)) {
                msg = retStr;
            } else {
                msg = PortalMessageService.getI().getMessage("portal.WorkflowImportPortlet.doDelete.success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = PortalMessageService.getI().getMessage("portal.WorkflowImportPortlet.doDelete.notsuccess") + " : " + msg;
            // System.out.println("msg2: " + msg);
            setRequestAttribute(request.getPortletSession(),"wfimpmsg", msg);
        }
        setRequestAttribute(request.getPortletSession(),"wfimpmsg", msg);
    }
    
    /**
     * get repository workflow item list from wfs...
     */
    private Vector getRepositoryItemListFromWfs(String wfType, Long id) throws Exception {
        // get repository workflow item list from wfs...
        Hashtable hsh = new Hashtable();
        // hsh.put("url", bean.getWfsID());
        ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
        PortalWfsClient wfsClient = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
        wfsClient.setServiceURL(st.getServiceUrl());
        wfsClient.setServiceID(st.getServiceID());
        //
        RepositoryWorkflowBean bean = new RepositoryWorkflowBean();
        bean.setId(id);
        bean.setWorkflowType(wfType);
        //
        Vector wfList = wfsClient.getRepositoryItems(bean);
        if (wfList == null) {
            throw new Exception("Not valid wf list !");
        }
        // System.out.println("wfList : " + wfList);
        return wfList;
    }
    
    /**
     * get quota hash from repository...
     */
    private Hashtable getQuotaHashFromRepository() throws Exception {
        // get quota hash from repository...
        Hashtable hsh = new Hashtable();
        // hsh.put("url", bean.getWfsID());
        ServiceType st = InformationBase.getI().getService("repository", "portal", hsh, new Vector());
        PortalRepositoryClient repoClient = (PortalRepositoryClient) Class.forName(st.getClientObject()).newInstance();
        repoClient.setServiceURL(st.getServiceUrl());
        repoClient.setServiceID(st.getServiceID());
        //
        Hashtable quotaHash = repoClient.getQuota();
        if (quotaHash == null) {
            throw new Exception("Not valid quota hash !");
        }
        // System.out.println("quotaHash : " + quotaHash);
        return quotaHash;
    }
    
    /**
     * Returning a true if current user and root user is the same.
     */
    private boolean isRoot(String userID) {
        return true;// (RepositoryFileUtils.getInstance().getRootUserID().equalsIgnoreCase(userID));
    }




}

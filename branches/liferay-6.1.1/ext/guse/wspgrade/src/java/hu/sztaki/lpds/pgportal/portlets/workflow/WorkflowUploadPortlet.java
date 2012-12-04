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

import hu.sztaki.lpds.portal.util.stream.FileUploadProgressListener;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import javax.portlet.*;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import java.util.Vector;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.io.FilenameUtils;


/**
 * Portlet class for Workflow upload
 * The workflow is in a zip file in a client host.
 * The workflow uploaded by a browser to the place of use. 
 * 
 *
 * @author lpds
 */
public class WorkflowUploadPortlet extends GenericWSPgradePortlet {
    
    private PortletContext pContext;
    
    private String jsp = new String("/jsp/workflow/wfupload.jsp");
    private long uploadMaxSize = 0;
    public WorkflowUploadPortlet() {}
    
    
    /**
     * Uploading basic data
     */
    private void LoadProperty() {}


  /**
   * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
   */

    @Override
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);


        pContext = config.getPortletContext();
        LoadProperty();

        String m = config.getInitParameter("fileupload_upload_maxsize");
        try {uploadMaxSize = Integer.parseInt(m) * 1048576;}
        catch (NumberFormatException nfe)
        {
            uploadMaxSize = 10485760;
            getPortletContext().log("[FileUploadPortlet] - failed to read in filetransfer_upload_maxsize, set to 10MB");
        }
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

        if(req.getParameter("full")!=null)
            req.setAttribute("full",req.getParameter("full"));
        
        String userName = req.getRemoteUser();
        req.setAttribute("wfupportalID", PropertyLoader.getInstance().getProperty("service.url"));
        String storageID = WorkflowUpDownloadUtils.getInstance().getStorageID();
        String wfsID = WorkflowUpDownloadUtils.getInstance().getWfsID();
        req.setAttribute("wfupstorageID", storageID);
        req.setAttribute("wfupwfsID", wfsID);
        req.setAttribute("wfupuserID", userName);
        // quota review
        if (UserQuotaUtils.getInstance().userQuotaIsNotFull(userName))
        {
            req.setAttribute("quotafull", "false");
            req.setAttribute("wfupmsg", "");
        } 
        else
        {
            req.setAttribute("quotafull", "true");
            req.setAttribute("wfupmsg", PortalMessageService.getI().getMessage("portal.RealWorkflowPortlet.quotaisoverfull"));
        }
        try
        {
            PortletRequestDispatcher dispatcher;
            dispatcher = getPortletContext().getRequestDispatcher(jsp);
            dispatcher.include(req, res);
        }
        catch (IOException e) {throw new PortletException("JSPPortlet.doView exception", e);}
        
    }

   @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException
    {
        PortletMode pMode = request.getPortletMode();
      // upload -- enctype="multipart/form-data"
        if (PortletFileUpload.isMultipartContent(request))
            doUpload(request, response);

    }


    @Override
    protected  void doUpload(ActionRequest request, ActionResponse response)
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
            if(ps.getAttribute("uploads",ps.APPLICATION_SCOPE)==null)
                ps.setAttribute("uploads", new Hashtable<String,ProgressListener>());
            
      //get the FileItems
            String fieldName = null;

            List fileItems = pfu.parseRequest(request);
            Iterator iter = fileItems.iterator();
            File serverSideFile=null;
            Hashtable h=new Hashtable(); //fileupload
            while (iter.hasNext())
            {
                FileItem item = (FileItem)iter.next();
        // retrieve hidden parameters if item is a form field
                if (item.isFormField()) {
                    fieldName = item.getFieldName();
                    if("newGrafName".equals(fieldName))
                        h.put("newGrafName",item.getString());
                    if("newAbstName".equals(fieldName))
                        h.put("newAbstName",item.getString());
                    if("newRealName".equals(fieldName))
                        h.put("newRealName",item.getString());
                } else
                { // item is not a form field, do file upload
                    Hashtable<String,ProgressListener> tmp=(Hashtable<String,ProgressListener>)ps.getAttribute("uploads");//,ps.APPLICATION_SCOPE
                    pfu.setProgressListener((ProgressListener) new FileUploadProgressListener());

                    String s = item.getName();
                    s = FilenameUtils.getName(s);
                    ProgressListener pl = pfu.getProgressListener();
                    tmp.put(s, pl);
                    ps.setAttribute("uploads", tmp);


                    String tempDir = System.getProperty("java.io.tmpdir")+"/uploads/"+request.getRemoteUser();
                    File f=new File(tempDir);
                    if(!f.exists()) f.mkdirs();
                    serverSideFile = new File(tempDir, s);
                    item.write(serverSideFile);
                    item.delete();
                    context.log("[FileUploadPortlet] - file " + s+ " uploaded successfully to " + tempDir);
                }
            }
// file upload to storage
            try
            {
                ServiceType st = InformationBase.getI().getService("wfs", "portal", new Hashtable(), new Vector());
                h.put("senderObj","ZipFileSender");
                h.put("portalURL",PropertyLoader.getInstance().getProperty("service.url"));
                h.put("wfsID",st.getServiceUrl());
                h.put("userID",request.getRemoteUser());

                Hashtable hsh = new Hashtable();
//                    st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
//                    hsh.put("url", "http://localhost:8080/storage");
                st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
                PortalStorageClient psc = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
                psc.setServiceURL(st.getServiceUrl());
                psc.setServiceID("/receiver");
                if(serverSideFile!=null)
                    psc.fileUpload(serverSideFile,"fileName", h);
            }
            catch (Exception ex)
            {
                response.setRenderParameter("full", "error.upload");
                ex.printStackTrace();
                return;
            }
            ps.removeAttribute("uploads",ps.APPLICATION_SCOPE);

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

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException
    {
        try
        {
            PortletSession ps=request.getPortletSession();
            Hashtable<String,ProgressListener> tmp=(Hashtable<String,ProgressListener>)ps.getAttribute("uploads",ps.APPLICATION_SCOPE);
            Enumeration<String> enm=tmp.keys();
            String key;
            FileUploadProgressListener lisener;//=(FileUploadProgressListener)((PortletFileUpload)ps.getAttribute("upload",ps.APPLICATION_SCOPE)).getProgressListener();
            while(enm.hasMoreElements())
            {
                key=enm.nextElement();
                response.getWriter().write(key+"</br>");
                lisener=(FileUploadProgressListener)tmp.get(key);
                response.getWriter().write(lisener.getFileuploadstatus()+"%");

            }
//            response.getWriter().write(lisener.getFileuploadstatus());
        }
        catch(Exception e)
        {
            response.getWriter().write("N/A");
        }

    }

    
    
}

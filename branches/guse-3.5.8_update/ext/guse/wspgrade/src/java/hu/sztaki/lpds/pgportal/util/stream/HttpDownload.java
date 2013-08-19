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
 * Http/https alapu stream letoltes
 */

package hu.sztaki.lpds.pgportal.util.stream;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import java.io.IOException;
import java.util.Hashtable;

import java.io.InputStream;

import java.util.Enumeration;
import java.util.Vector;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
/**
 * @author krisztian karoczkai
 */

public class HttpDownload
{


    public static void fileDownload(String pDwlType,ResourceRequest request, ResourceResponse response) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
            String wf=request.getParameter("workflowID");
            WorkflowData t=null;
            if("graf".equals(pDwlType)){
                t=PortalCacheService.getInstance().getUser(request.getRemoteUser()).getAbstactWorkflow(wf);
                if(t.getWfsID()==null){
                    ServiceType st=InformationBase.getI().getService("wfs", "portal", new Hashtable(), new Vector());
                    t.setWfsID(st.getServiceUrl());
                    System.out.println("WFS:"+t.getWorkflowID()+":"+st.getServiceUrl());
                }
                if(t.getStorageID()==null){
                    ServiceType st=InformationBase.getI().getService("storage", "portal", new Hashtable(), new Vector());
                    t.setStorageID(st.getServiceUrl());
                    System.out.println("STORAGE:"+t.getWorkflowID()+":"+st.getServiceUrl());
                }
            }
            else if("abst".equals(pDwlType))
                t=PortalCacheService.getInstance().getUser(request.getRemoteUser()).getTemplateWorkflow(wf);
            else
                t=PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(wf);
            Hashtable<String,String> params=new Hashtable<String,String>();
            params.put("portalID",PropertyLoader.getInstance().getProperty("service.url"));
            params.put("userID",request.getRemoteUser());
            params.put("downloadType",pDwlType);
            params.put("workflowID",wf);
            params.put("wfsID",t.getWfsID());

// letoltes egyebb parameterei
            Enumeration<String> enm=request.getParameterNames();
            String key;
            while(enm.hasMoreElements())
            {
                key=enm.nextElement();
                if(params.get(key)==null) params.put(key, request.getParameter(key));
            }

            Hashtable hsh = new Hashtable();
            try{hsh.put("url", t.getStorageID());}
            catch(Exception e){}
            ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
            PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
            ps.setServiceURL(st.getServiceUrl());
            ps.setServiceID("/download");

            InputStream is=ps.getStream(params);
            byte[] b=new byte[1024];
            int nm;

            while((nm=is.read(b))>(-1))
                response.getPortletOutputStream().write(b,0, nm);
            is.close();
    }


    public static void fileView(ResourceRequest request, ResourceResponse response) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
/*
 			<input type="hidden" name="workflowID"   value="${workflow}">
			<input type="hidden" name="jobID"        value="${job}">
			<input type="hidden" name="pidID"        value="${bjob.pid}">
			<input type="hidden" name="runtimeID"    value="${rtid}">
			<input type="hidden" name="fileID"       value="stderr.log">
 */
            String wf=request.getParameter("workflowID");
            WorkflowData t=PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(wf);
            Hashtable<String,String> params=new Hashtable<String,String>();
            params.put("portalID",PropertyLoader.getInstance().getProperty("service.url"));
            params.put("userID",request.getRemoteUser());
            params.put("workflowID",wf);
            params.put("wfsID",t.getWfsID());

// letoltes egyebb parameterei
            Enumeration<String> enm=request.getParameterNames();
            String key;
            while(enm.hasMoreElements())
            {
                key=enm.nextElement();
                if(params.get(key)==null) params.put(key, request.getParameter(key));
            }

            Hashtable hsh = new Hashtable();
            hsh.put("url", t.getStorageID());
            ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
            PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
            ps.setServiceURL(st.getServiceUrl());
            ps.setServiceID("/viewer");

            InputStream is=ps.getStream(params);
            byte[] b=new byte[1024];
            int nm;
            response.setContentType("text/html");
            response.getPortletOutputStream().write("<textarea readonly=\"true\" cols=\"100\" rows=\"8\">\n".getBytes());
            while((nm=is.read(b))>(-1))
                response.getPortletOutputStream().write(b,0, nm);
            response.getPortletOutputStream().write("</textarea>\n".getBytes());
            is.close();
    }

}


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
package hu.sztaki.lpds.storage.service.carmen.server.download;

import hu.sztaki.lpds.storage.com.DownloadWorkflowBean;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Download Workflow Servlet.
 *
 * Returns the stored data of a workflow 
 * in zipped format (to the client browser).
 *
 * @author lpds
 */
public class DownloadWorkflowServlet extends HttpServlet {
    
    private static final long serialVersionUID = -2017669244739618900L;
    
    private DownloadWorkflowUtils downloadWorkflowUtils;
/**
 * Constructor, data initialisation
 */
    public DownloadWorkflowServlet() {
        downloadWorkflowUtils = new DownloadWorkflowUtils();
    }
    
    /**
     * Processing the download request
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException Servlet error
     * @throws IOException Communication error
     */
    protected void postProcessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DownloadWorkflowBean bean = new DownloadWorkflowBean();
        // parse parameterS (collecting parameters) collecting parameter values
        Enumeration enumParameters = request.getParameterNames();
        String parameterName = null;
        String parameterValue = null;
        while (enumParameters.hasMoreElements()) {
            parameterName = new String((String) enumParameters.nextElement()).trim();
            if ((parameterName != null) && (!"".equals(parameterName))) {
                parameterValue = null;
                parameterValue = new String(request.getParameter(parameterName)).trim();
                if ((parameterValue != null) && (!"".equals(parameterValue))) {
//                     System.out.println("HTTP param:" + parameterName+"/"+parameterValue);
                    if ("portalID".equals(parameterName)) {
                        bean.setPortalURL(parameterValue);
                    }
                    if ("wfsID".equals(parameterName)) {
                        bean.setWfsID(parameterValue);
                    }
                    if ("userID".equals(parameterName)) {
                        bean.setUserID(parameterValue);
                    }
                    if ("workflowID".equals(parameterName)) {
                        bean.setWorkflowID(parameterValue);
                    }
                    if ("jobID".equals(parameterName)) {
                        bean.setJobID(parameterValue);
                    }
                    if ("pidID".equals(parameterName)) {
                        bean.setPidID(parameterValue);
                    }
                    if ("downloadType".equals(parameterName)) {
                        bean.setDownloadType(parameterValue.trim());
                    }
                    if ("instanceType".equals(parameterName)) {
                        bean.setInstanceType(parameterValue.trim());
                    }
                    if ("outputLogType".equals(parameterName)) {
                        bean.setOutputLogType(parameterValue.trim());
                    }
                    if ("exportType".equals(parameterName)) {
                        bean.setExportType(parameterValue.trim());
                    }
                }
            }
        }
        // default settings - nem kell instance
        if ("".equals(bean.getInstanceType())) {
            bean.setInstanceType(bean.instanceType_none);
        }
        // System.out.println("instanceType: " + bean.getInstanceType());
        // default settings - ha nem "none" minden log kell
        if (!bean.outputLogType_none.equals(bean.getOutputLogType())) {
            bean.setOutputLogType(bean.outputLogType_all);
        }
        // System.out.println("outputLogType: " + bean.getOutputLogType());
        // workflow adatainak szolgaltatasa
        // set header
        response.setContentType("application/zip");
        // get runtimeID
        bean.setRuntimeID("");
        if ((bean.getDownloadType().startsWith(bean.downloadType_inputs_rtID)) || (bean.getDownloadType().startsWith(bean.downloadType_outputs_rtID)) || (bean.getDownloadType().startsWith(bean.downloadType_job_outputs_rtID))) {
            bean.setRuntimeID(bean.getDownloadType().substring((bean.getDownloadType().split("_")[0]).length() + 1));
        }
        // System.out.println("runtimeID: " + bean.getRuntimeID());
        // set download zip file name
        bean.setDownZipFileName(downloadWorkflowUtils.getDownloadZipName(bean));
        response.setHeader("Content-Disposition", "inline; filename=\"" + bean.getDownZipFileName() + "\"");
        OutputStream httpout = null;
        try {
            // get http Out stream
            httpout = response.getOutputStream();
            // init zip stream
            ZipOutputStream zipout = new ZipOutputStream(httpout);
            // calling the zip-maker
//             System.out.println("zipkeszito meghivasa...");
            downloadWorkflowUtils.downloadWorkflowZipFileToZipStream(bean, zipout);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // send 500 error to client
                // response.sendError(500, "Not valid workflow name or not exist workflow !!!");
                response.sendError(500, e.getLocalizedMessage());
                if (httpout != null) {
                    httpout.close();
                }
            } catch (Exception e2) {
                 e2.printStackTrace();
            }
             e.printStackTrace();
        } finally {
            try {
                if (httpout != null) {
                    httpout.close();
                }
            } catch (Exception e2) {
                 e2.printStackTrace();
            }
        }
//         System.out.println("DownloadWorkflowServlet postProcessRequest end...");
    }
    
/**
 * @see HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    }
    
/**
 * @see HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
 */
@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        postProcessRequest(request, response);
    }
    
/**
 * @see HttpServlet#getServletInfo()
 */
@Override
    public String getServletInfo() {
        return "DownloadWorkflowServlet_Short_description";
    }
    
}

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
package hu.sztaki.lpds.storage.service.carmen.server.viewer;

import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Viewer Servlet. Returns the content of a file stored on the storage.
 *
 * (e.g.: output file of a job, log or error message file)
 *
 * @author lpds
 */
public class ViewerServlet extends HttpServlet {
    
    private static final long serialVersionUID = 8208010082850926718L;
    
    // repository root dir
    private String repositoryDir;
    
    // File separator (for example, "/")
    private String sep;
/**
 * Constructor, loading properties
 */
    public ViewerServlet() {
        LoadProperty();
        // System.out.println("Repository Path: " + repositoryDir);
    }
    
    /**
     * Init, loading initializing data.
     */
    private void LoadProperty() {
        sep = FileUtils.getInstance().getSeparator();
        repositoryDir = FileUtils.getInstance().getRepositoryDir();
        FileUtils.getInstance().createRepositoryDirectory();
    }
    
    /**
     * Processes requests for <code>POST</code> methods.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws IOException channel handling error
     * @throws ServletException Servlet error
     */
    protected void postProcessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // System.out.println("");
        // System.out.println("ViewerServlet postProcessRequest
        // begin...");
        FileUtils.getInstance().createRepositoryDirectory();
        // parse parameterS (collecting parameters)
        // init parameters
        // portal service url
        String portalURL = new String("");
        // portal service url convert dir name
        String portalID = new String("");
        // user name
        String userID = new String("");
        // workflow name
        String workflowID = new String("");
        // job name
        String jobID = new String("");
        // runtime ID
        String runtimeID = new String("");
        // job pid ID
        String pidID = new String("");
        // megnezendo file neve
        String fileID = new String("");
        // parameter ertekek kigyujtese
        Enumeration enumParameters = request.getParameterNames();
        String parameterName = null;
        String parameterValue = null;
        while (enumParameters.hasMoreElements()) {
            parameterName = new String((String) enumParameters.nextElement()).trim();
            if ((parameterName != null) && (!"".equals(parameterName))) {
                parameterValue = null;
                parameterValue = new String(request.getParameter(parameterName)).trim();
                if ((parameterValue != null) && (!"".equals(parameterValue))) {
                    // System.out.println("parameterName : " + parameterName);
                    // System.out.println("parameterValue: " + parameterValue);
                    if ("portalID".equals(parameterName)) {
                        portalURL = parameterValue;
                    }
                    if ("userID".equals(parameterName)) {
                        userID = parameterValue;
                    }
                    if ("workflowID".equals(parameterName)) {
                        workflowID = parameterValue;
                    }
                    if ("jobID".equals(parameterName)) {
                        jobID = parameterValue;
                    }
                    if ("runtimeID".equals(parameterName)) {
                        runtimeID = parameterValue.trim();
                    }
                    if ("pidID".equals(parameterName)) {
                        pidID = parameterValue;
                    }
                    if ("fileID".equals(parameterName)) {
                        fileID = parameterValue.trim();
                    }
                }
            }
        }
        // workflow adatainak szolgaltatasa
        // set header
        // response.setContentType("text/plain");
        OutputStream httpout = null;
        try {
            // get http Out stream
            httpout = response.getOutputStream();
            // get portal id portal dir name
            portalID = FileUtils.getInstance().convertPortalIDtoDirName(portalURL);
            // get workflow dir
            String userBaseDir = new String(repositoryDir + portalID + sep + userID + sep);
            String workflowBaseDir = new String(userBaseDir + workflowID + sep);
            String jobOutputBaseDir = new String(workflowBaseDir + jobID + sep + "outputs" + sep);
            String runtimeBaseDir = new String(jobOutputBaseDir + runtimeID + sep+pidID+sep);
            String filePath = new String(runtimeBaseDir + fileID);
            System.out.println("storage:ViewerServlet filePath : " + filePath);
            if (!sendFileToStream(httpout, filePath)) {
                httpout.write(new String("Information not available !").getBytes());
            }
            if (httpout != null) {
                httpout.close();
            }
        } catch (Exception e) {
            try {
                // send 500 error to client
                // response.sendError(500, "Information not available !
                // !!!");
            } catch (Exception e2) {
                // e2.printStackTrace();
            }
            // e.printStackTrace();
        } finally {
            try {
                if (httpout != null) {
                    httpout.close();
                }
            } catch (Exception e2) {
                // e2.printStackTrace();
            }
        }
        // System.out.println("ViewerServlet postProcessRequest end...");
    }
    
    /**
     * Writes the content of the file (filepath)
     * to the out (httpout) stream.
     * @param OutputStream out - httpout stream
     * @param String filePath - file path
     */
    private boolean sendFileToStream(OutputStream out, String filePath) {
        try {
            FileUtils.getInstance().sendFileToStream(out, filePath);
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Processes requests for <code>GET</code> methods.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws IOException channel handling error
     * @throws ServletException Servlet error
     */
    protected void getProcessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // System.out.println("");
        // System.out.println("ViewerServlet getProcessRequest
        // begin...");
        // System.out.println("ViewerServlet getProcessRequest
        // end...");
    }

/**
 * @see HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) 
 */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getProcessRequest(request, response);
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
        return "ViewerServlet_Short_description";
    }
    
}

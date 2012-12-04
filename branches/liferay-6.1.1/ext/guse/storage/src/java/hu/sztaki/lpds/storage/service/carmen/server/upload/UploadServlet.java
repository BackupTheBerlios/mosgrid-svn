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
package hu.sztaki.lpds.storage.service.carmen.server.upload;

import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.XMLFileUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

/**
 * File Upload Servlet, saves all the files of a post request to the storage
 * repository. It is used during the job/input port configuration.
 *
 * (It is used usually to upload job inputs and executables.)
 *
 * Uploading parametric inputs in a zip file:
 * If the uploaded file's name equals to the value stored 
 * in the defaultParamZipName constant("paramInputs.zip"), 
 * then after the upload the zip file will be unpacked
 * to the directory of the given job's given port and the zip file will be deleted.
 *
 * @author lpds
 */
public class UploadServlet extends HttpServlet {
    
    private static final long serialVersionUID = -2552523424720618632L;
    
    // storage repository root dir, the saved files will be stored here
    private String repositoryDir;
    
    // File separator (for example, "/")
    private String sep;
    
    // use memory size, threshold
    private int maxMemorySize = 4 * 1024;
    
    // max request (uploaded file) size
    private long maxRequestSize = 500 * 1024 * 1024;
/**
 * Constructor, loading properties
 */
    public UploadServlet() {
        LoadProperty();
        // System.out.println("Sorage Repository Path: " + repositoryDir);
    }
    
    /**
     * Init, loading initialization data.
     */
    private void LoadProperty() {
        sep = FileUtils.getInstance().getSeparator();
        maxRequestSize = 500 * 1024 * 1024;
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
        // System.out.println("UploadServlet postProcessRequest begin...");
        // valtozok initializalasa
        // stores the form parameters
        Map formParameters = null;
        // stores the file parameters
        Map fileParameters = null;
        // portal service url
        String portalID = new String("");
        // user name
        String userID = new String("");
        // workflow name
        String workflowID = new String("");
        // job name
        String jobID = new String("");
        // storage file name, "input_inp01", "binary"
        String sfile = new String("");
        // configuration ID
        // doConfigure generates during its call 
        // pl: userID + System.currentTimeMillis();
        String confID = new String("");
        // the number of the files to be uploaded
        int fileCounter = 0;
        // true if requestSize is more then the size of the maxRequestSize
        boolean sizeLimitError = false;
        try {
            // Check that we have a file upload request
            ServletRequestContext servletRequestContext = new ServletRequestContext(request);
            boolean isMultipart = ServletFileUpload.isMultipartContent(servletRequestContext);
            if (isMultipart) {
                // System.out.println("Is Multipart Context.");
                FileUtils.getInstance().createRepositoryDirectory();
                // use progress begin
                // Create a factory for progress-disk-based file items
                ProgressMonitorFileItemFactory progressMonitorFileItemFactory = new ProgressMonitorFileItemFactory(request);
                // Set factory constraints
                progressMonitorFileItemFactory.setSizeThreshold(maxMemorySize);
                // Set repository dir
                File tempRepositoryDirectory = new File(repositoryDir);
                progressMonitorFileItemFactory.setRepository(tempRepositoryDirectory);
                // Create a new file upload handler
                ServletFileUpload servletFileUpload = new ServletFileUpload();
                servletFileUpload.setFileItemFactory(progressMonitorFileItemFactory);
                // Set overall request size constraint
                servletFileUpload.setSizeMax(maxRequestSize);
                // Parse the request, List /FileItem/
                List listFileItems = null;
                try {
                    // System.out.println("before_parseRequest...");
                    listFileItems = servletFileUpload.parseRequest(request);
                    // System.out.println("after_parseRequest...");
                    formParameters = new HashMap();
                    fileParameters = new HashMap();
                    for (int i = 0; i < listFileItems.size(); i++) {
                        FileItem fileItem = (FileItem) listFileItems.get(i);
                        if (fileItem.isFormField() == true) {
                            formParameters.put(fileItem.getFieldName(), fileItem.getString());
                        } else {
                            fileParameters.put(fileItem.getFieldName(), fileItem);
                            request.setAttribute(fileItem.getFieldName(), fileItem);
                        }
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                }
                // System.out.println("formParameters: " + formParameters);
                // System.out.println("fileParameters: " + fileParameters);
                // use progress end
                if ((listFileItems != null) && (listFileItems.size() > 0)) {
                    // Process the hidden items
                    Iterator iterator = listFileItems.iterator();
                    while (iterator.hasNext()) {
                        FileItem fileItem = (FileItem) iterator.next();
                        // System.out.println("getFieldname : " + fileItem.getFieldName());
                        // System.out.println("getString : " + fileItem.getString());
                        if (fileItem.isFormField()) {
                            // parameterek ertelmezese
                            if (fileItem.getFieldName().equals("portalID")) {
                                portalID = FileUtils.getInstance().convertPortalIDtoDirName(fileItem.getString());
                            }
                            if (fileItem.getFieldName().equals("userID")) {
                                userID = fileItem.getString();
                            }
                            if (fileItem.getFieldName().equals("workflowID")) {
                                workflowID = fileItem.getString();
                            }
                            if (fileItem.getFieldName().equals("jobID")) {
                                jobID = fileItem.getString();
                            }
                            if (fileItem.getFieldName().equals("sfile")) {
                                sfile = fileItem.getString();
                            }
                            if (fileItem.getFieldName().equals("confID")) {
                                confID = fileItem.getString();
                            }
                        } else {
                            if ((fileItem.getName() != null) && (!fileItem.getName().equals(""))) {
                                fileCounter++;
                            }
                        }
                    }
                    // System.out.println("FileCounter : " + fileCounter);
                    // Process the uploaded items
                    if ((portalID != null) && (userID != null) && (workflowID != null) && (jobID != null) && (sfile != null) && (confID != null) && (fileCounter > 0)) {
                        if ((!portalID.equals("")) && (!userID.equals("")) && (!workflowID.equals("")) && (!jobID.equals("")) && (!sfile.equals("")) && (!confID.equals(""))) {
                            iterator = listFileItems.iterator();
                            while (iterator.hasNext()) {
                                FileItem fileItem = (FileItem) iterator.next();
                                if (!fileItem.isFormField()) {
                                    processUploadedFiles(response, fileItem, portalID, userID, workflowID, jobID, sfile, confID);
                                }
                            }
                        }
                    }
                }
            } else {
                // System.out.println("Is NOT Multipart Context !!!");
            }
        } catch (Exception e) {
            // e.printStackTrace();
            throw new ServletException(e);
        }
        if (sizeLimitError) {
            // System.out.println("SizeLimitError: Upload file size (ReqestSize)
            // > " + this.maxRequestSize + " !");
        }
        // System.out.println("UploadServlet postProcessRequest end...");
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
        // System.out.println("UploadServlet getProcessRequest begin...");
        ServletOutputStream out = response.getOutputStream();
        String getSID = null;
        String getendSID = null;
        String retStr = new String("");
        try {
            getSID = request.getParameter("sid");
            // System.out.println("getSID: " + getSID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getendSID = request.getParameter("endsid");
            // System.out.println("getendSID: " + getendSID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        if ((getSID != null) && (getendSID == null)) {
            // get type "sid"
            // System.out.println("get type SID : " + getSID);
            if (!getSID.trim().equals("")) {
                retStr = UploadUtils.getInstance().getGenerateRetHTMLString(getSID);
            } else {
                retStr = new String("Error in parameter: sid");
            }
        } else if ((getSID == null) && (getendSID != null)) {
            // get type "endsid"
            // System.out.println("get type endSID : " + getendSID);
            if (!getendSID.trim().equals("")) {
                // check error uploads
                boolean haveError = false;
                // get file list (fileHash)
                Hashtable fileHash = UploadItemsList.getInstance().getFileHash(getendSID);
                // parse
                if ((fileHash != null) && (!fileHash.isEmpty())) {
                    Enumeration enumeration = fileHash.keys();
                    StringBuffer sb = new StringBuffer();
                    while (enumeration.hasMoreElements()) {
                        String fileName = (String) enumeration.nextElement();
                        if ((fileName != null) && (!fileName.equals(""))) {
                            UploadItemBean uploadItemBean = (UploadItemBean) fileHash.get(fileName);
                            Integer filePerCent = uploadItemBean.getPerCent();
                            String fileErrorStr = uploadItemBean.getErrorStr();
                            // System.out.println("fileName - fileErrorStr : " + fileName + " - " + fileErrorStr);
                            if (fileErrorStr.startsWith("Error")) {
                                haveError = true;
                                // System.out.println("have error in : ");
                                // retStr = new String(fileErrorStr + "</ br>");
                                sb.append(new String(fileErrorStr + "</ br>"));
                                // System.out.println("fileName : " + fileName);
                            }
                        }
                    }
                    retStr = sb.toString();
                    // System.out.println("Storage Upload Servlet haveError : " + haveError);
                    if (haveError) {
                        retStr = new String("Error in upload process ! </ br>\n" + retStr);
                    } else {
                        // System.out.println("no have error in : ");
                        retStr = new String("Upload is succesfull !" + "</ br>");
                    }
                } else {
                    retStr = new String("Error getFileHash is null !");
                }
            } else {
                retStr = new String("Error in parameter: endsid");
            }
        } else if ((getSID == null) && (getendSID == null)) {
            // getSID == null and getendSID == null
            // System.out.println("getSID : " + getSID + " - getendSID : " + getendSID);
            retStr = new String("Error in parameter: sid and endsid !");
        } else {
            retStr = new String("Error in parameters !");
        }
        out.print(retStr);
        // System.out.println("UploadServlet getProcessRequest retString: " + retStr);
        // System.out.println("UploadServlet getProcessRequest end...");
    }
    
    /**
     * The actual saving of the file happens here based on the received fileItem.
     *
     * @param response
     * @param fileItem
     * @param portalID
     * @param userID
     * @param workflowID
     * @param jobID
     * @param sfile
     */
    private void processUploadedFiles(HttpServletResponse response, FileItem fileItem, String portalID, String userID, String workflowID, String jobID, String sfile, String confID) {
        // SID, sessionID
        String SID = new String("");
        // file randomID 0 - 999999
        String randomID = new String("");
        File temporaryUploadedFile = null;
        try {
            // Process a file upload
            if (!fileItem.isFormField()) {
                String fieldName = fileItem.getFieldName();
                String fileName = fileItem.getName();
                // long fileSize = fileItem.getSize();
                boolean writeToFile = false;
                if ((fieldName != null) && (!fieldName.trim().equals(""))) {
                    // sfile = "file_SID_randomID"
                    String[] list = fieldName.split("_");
                    if (list.length >= 3) {
                        SID = list[1];
                        randomID = list[2];
                    }
                }
                if ((fileName != null) && (!fileName.trim().equals(""))) {
                    writeToFile = true;
                    // System.out.println("");
                    // System.out.println("portalID : " + portalID);
                    // System.out.println("userID : " + userID);
                    // System.out.println("workflowID : " + workflowID);
                    // System.out.println("jobID : " + jobID);
                    // System.out.println("sfile : " + sfile);
                    // System.out.println("confID : " + confID);
                    // System.out.println("SID : " + SID);
                    // System.out.println("randomID : " + randomID);
                    // System.out.println("fieldName : " + fieldName);
                    // System.out.println("fileName : " + fileName);
                    // System.out.println("fileSize : " + fileSize);
                }
                // Process a file upload
                if (writeToFile) {
                    // if the uploaded file is parametric, then paramZip = true
                    boolean paramZip = UploadUtils.getInstance().isParametricInputFile(fileName, sfile);
                    String checkZipErrStr = new String("");
                    String fileRepositoryPath = UploadUtils.getInstance().getRepositoryPath(paramZip, fileName, portalID, userID, workflowID, jobID, sfile);
                    //
                    // confID begin
                    //
                    String temporaryBase = this.repositoryDir + "temporary" + sep + confID + sep;
                    FileUtils.getInstance().createDirectory(temporaryBase);
                    String temporaryUploadedFileName = String.valueOf(System.currentTimeMillis()) + ".file";
                    String temporaryUploadedFilePath = temporaryBase + temporaryUploadedFileName;
                    String temporaryUploadedXMLFilePath = temporaryBase + temporaryUploadedFileName + ".xml";
                    // System.out.println("temporaryUploadedFilePath    : " + temporaryUploadedFilePath);
                    // System.out.println("temporaryUploadedXMLFilePath : " + temporaryUploadedXMLFilePath);
                    temporaryUploadedFile = new File(temporaryUploadedFilePath);
                    if (temporaryUploadedFile.exists()) {
                        temporaryUploadedFile.delete();
                    }
                    // Apache file save, without any crypt
                    // Moves the file stored in the temporary dir
                    // to the given directory "temporaryUploadedFile".
                    fileItem.write(temporaryUploadedFile);
                    if (paramZip) {
                        checkZipErrStr = ZipUtils.getInstance().checkParamInputZipFile(temporaryUploadedFile, fileName, jobID, sfile);
                    }
                    // System.out.println("paramZip : " + paramZip);
                    // System.out.println("checkZipErrStr : " + checkZipErrStr);
                    // create xml file data hash
                    Hashtable dataHash = new Hashtable();
                    dataHash.put("fileRepositoryPath", fileRepositoryPath);
                    dataHash.put("temporaryUploadedFilePath", temporaryUploadedFilePath);
                    dataHash.put("fileName", fileName);
                    dataHash.put("portalID", portalID);
                    dataHash.put("userID", userID);
                    dataHash.put("workflowID", workflowID);
                    dataHash.put("jobID", jobID);
                    dataHash.put("sfile", sfile);
                    dataHash.put("SID", SID);
                    dataHash.put("randomID", randomID);
                    // System.out.println("dataHash : " + dataHash);
                    XMLFileUtils.getInstance().saveDataHashToXMLFile(temporaryUploadedXMLFilePath, dataHash);
                    //
                    // confID end
                    //
                    // upload finished
                    UploadItemsList.getInstance().addFile(SID, fileName + "_" + randomID, new Integer(100), checkZipErrStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // delete temporary file
            if (temporaryUploadedFile != null) {
                if (temporaryUploadedFile.exists()) {
                    // delete temporary file
                    temporaryUploadedFile.delete();
                }
            }
            // send error
            try {
                // response.sendError(500, "Upload is not successed !!!");
                // get http output stream
                // response.setContentType("text/plain");
                OutputStream httpout = response.getOutputStream();
                httpout.write(new String("Upload is not successed !!!").getBytes());
                httpout.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
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
        return "UploadServlet_Short_description";
    }
    
}

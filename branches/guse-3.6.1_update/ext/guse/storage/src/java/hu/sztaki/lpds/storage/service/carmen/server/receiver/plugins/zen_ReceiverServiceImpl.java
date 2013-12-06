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
package hu.sztaki.lpds.storage.service.carmen.server.receiver.plugins;

import hu.sztaki.lpds.storage.service.carmen.commons.FileSenderUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.ZipUploadUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.ZipUtils;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaBean;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaService;
import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;

import java.io.File;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;

/**
 * File Receiver Servlet.
 *
 * (Zen plugin.)
 *
 * (Implementation class of the zen wfi's receiver storage service.)
 *
 * Sends and receives files from the submitter,
 * also receives from the portal when uploading.
 *
 * @author lpds
 */
public class zen_ReceiverServiceImpl implements ReceiverService {
    
    // file receiver repository root dir, ide tarolja le a megkapott fileokat
    private String repositoryDir;
    
    // File separator (for example, "/")
    private String sep;
    /**
     * Constructor, loading properties
     */
    public zen_ReceiverServiceImpl() {
        LoadProperty();
    }
    
    /**
     * Init, loading initialization data.
     */
    private void LoadProperty() {
        sep = FileUtils.getInstance().getSeparator();
        repositoryDir = FileUtils.getInstance().getRepositoryDir();
    }
    
    /**
     * @see ReceiverService#receive(com.oreilly.servlet.MultipartRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void receive(MultipartRequest multipartRequest, HttpServletResponse response) throws Exception {
        // System.out.println("");
        // System.out.println("zen_ReceiverServiceImpl begin...");
        // FileUtils.getInstance().createRepositoryDirectory();
        //
        // A kuldo, kero object name (FileSender or FileGetter or ...)
        String senderObj = multipartRequest.getParameter("senderObj").trim();
        if (senderObj == null) {
            senderObj = new String("");
        }
        // get job inputs and executeble files (submitter)
        if (senderObj.equals("FileGetter")) {
            // System.out.println("zen_ReceiverServiceImpl incoming request from
            // FileGetter...");
            // create and send input zip file
            FileGetterCreateFile(multipartRequest, response);
        }
        // upload job output files (submitter)
        if (senderObj.equals("FileSender")) {
            // System.out.println("zen_ReceiverServiceImpl incoming request from
            // FileSender...");
            // parse output zip file
            FileSenderParseFiles(multipartRequest, response);
        }
        // upload workflow zip file (portal upload jsp, repository import)
        if (senderObj.equals("ZipFileSender")) {
            // System.out.println("zen_ReceiverServiceImpl incoming request from
            // ZipFileSender...");
            // parse output zip file
            ZipFileSenderParseFile(multipartRequest, response);
        }
        // upload workflow zip file (tool)
        if (senderObj.equals("ZipFileUploader")) {
            // System.out.println("zen_ReceiverServiceImpl incoming request from
            // ZipFileUploader...");
            // parse output zip file
            ZipFileSenderParseFile(multipartRequest, response);
        }
        // upload quota informations (submitter local mode)
        if (senderObj.equals("QuotaSender")) {
            // System.out.println("FileReceiverServlet incoming request from
            // QuotaSender...");
            // parse quota informaions
            QuotaSenderParseInformations(multipartRequest, response);
        }
        // System.out.println("zen_ReceiverServiceImpl end...");
    }
    
    /**
     * Writes the input files given in the parameters (id, hash) 
     * in a zipped format to the response.
     *
     */
    private void FileGetterCreateFile(MultipartRequest multipartRequest, HttpServletResponse response) {
        try {
            // parse parameterS (parameterek begyüjtese)
            // A keresben hasznalt parametereknek ad kezdo erteket.
            // portal service url
            String portalID = new String("");
            // user name
            String userID = new String("");
            // a regi es uj file nev parokat tarolja
            // Key: newName, Value: oldPathName
            // oldPathName: "/workflow1/job1/inputs/1/file1.txt" or
            // "workflow2/job2/inputs/2/file2.txt"
            Hashtable newNamesTable = new Hashtable();
            // Az osszes parameteret kigyujti
            Enumeration enumParameters = multipartRequest.getParameterNames();
            String parameterName = null;
            String parameterValue = null;
            while (enumParameters.hasMoreElements()) {
                parameterName = new String((String) enumParameters.nextElement()).trim();
                if ((parameterName != null) && (!"".equals(parameterName))) {
                    parameterValue = null;
                    parameterValue = new String(multipartRequest.getParameter(parameterName)).trim();
                    if ((parameterValue != null) && (!"".equals(parameterValue))) {
                        // System.out.println("parameterName : " +
                        // parameterName);
                        // System.out.println("parameterValue: " +
                        // parameterValue);
                        if ("portalID".equals(parameterName)) {
                            portalID = FileUtils.getInstance().convertPortalIDtoDirName(parameterValue);
                        }
                        if ("userID".equals(parameterName)) {
                            userID = parameterValue;
                        }
                        if (parameterName.startsWith("file_")) {
                            String newName = parameterName.substring((parameterName.split("_")[0]).length() + 1);
                            // parameterValue = filePath
                            newNamesTable.put(newName, parameterValue);
                        }
                    }
                }
            }
            // System.out.println("storage ReceiverServiceImpl newNamesTable : " + newNamesTable);
            if (this.repositoryDir == null) {
                throw new Exception("Storage zen_ReceiverServlet repositoryDir is null !!!");
            }
            // create user base dir
            String userBaseDirStr = this.repositoryDir + portalID + sep + userID;
            // send files in zipStrem to out
            OutputStream out = response.getOutputStream();
            // System.out.println("storage ReceiverServiceImpl write zip to stream begin...");
            ZipUtils.getInstance().sendHashAllFilesToStream(userBaseDirStr, out, newNamesTable);
            // System.out.println("storage ReceiverServiceImpl write zip to stream end...");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // send 560 error to client
                response.sendError(560, "Server side exception !!! + " + e);
            } catch (Exception e2) {
                // e2.printStackTrace();
            }
        }
    }
    
    /**
     * Processes the file (one zip file) given in the request.
     *
     * @param multipartRequest
     */
    private void FileSenderParseFiles(MultipartRequest multipartRequest, HttpServletResponse response) {
        try {
            // parse parameterS (parameterek begyüjtese)
            // A keresben hasznalt parametereknek ad kezdo erteket.
            // portal service url
            String portalURL = new String("");
            // portal id dir name
            String portalID = new String("");
            // user name
            String userID = new String("");
            // workflow name
            String workflowID = new String("");
            // job name
            String jobID = new String("");
            // runtime ID
            String runtimeID = new String("");
            // copy hash ha kell output fileokat meg plussz helyekre
            // masolni akkor ebben a hash-ben kell megadni
            // (key: hova, value: mit)
            // pl:
            // ("/copyworkflow2/copyjob2/outputs/copyRtID2/copynewfilename2.txtcopy",
            // "newFileName2.txt");
            Hashtable copyHash = new Hashtable();
            // job pid ID
            String pidID = new String("");
            //
            // Az osszes parameteret kigyujti
            Enumeration enumParameters = multipartRequest.getParameterNames();
            String parameterName = null;
            String parameterValue = null;
            while (enumParameters.hasMoreElements()) {
                parameterName = new String((String) enumParameters.nextElement()).trim();
                if ((parameterName != null) && (!"".equals(parameterName))) {
                    parameterValue = null;
                    parameterValue = new String(multipartRequest.getParameter(parameterName)).trim();
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
                            runtimeID = parameterValue;
                        }
                        if ("copyhash".equals(parameterName)) {
                            // prepare copy hash
                            copyHash = getCopyHashFromString(parameterValue);
                        }
                        if ("pidID".equals(parameterName)) {
                            pidID = parameterValue;
                        }
                    }
                }
            }
            //
            // send files in zipStream to out
            Enumeration enumFiles = multipartRequest.getFileNames();
            while (enumFiles.hasMoreElements()) {
                String fileName = (String) enumFiles.nextElement();
                String fileOriginalFileName = multipartRequest.getOriginalFileName(fileName);
                // convert url to dirName
                portalID = FileUtils.getInstance().convertPortalIDtoDirName(portalURL);
                String jobbase = this.repositoryDir + portalID + sep + userID + sep + workflowID + sep + jobID;
                // System.out.println("jobbase: " + jobbase);
                // Make job directory
                File basedirin = new File(jobbase + sep + "inputs");
                basedirin.mkdirs();
                File basedirout = new File(jobbase + sep + "outputs");
                basedirout.mkdirs();
                // old path: File basedirrun = new File(jobbase + sep + "outputs" + sep + runtimeID);
                File basedirrun = new File(jobbase + sep + "outputs" + sep + runtimeID + sep + pidID);
                basedirrun.mkdirs();
                String newZipFilePath = new String(jobbase + sep + "outputs" + sep + fileOriginalFileName);
                File newZipFile = new File(newZipFilePath);
                if (newZipFile.exists()) {
                    newZipFile.delete();
                }
                // getFile()
                File zFile = multipartRequest.getFile(fileName);
                // move to
                zFile.renameTo(newZipFile);
                // System.out.println("fileName: " + fileName);
                // System.out.println("fileOriginalFileName: " +
                // fileOriginalFileName);
                // System.out.println("file.getAbsolutePath(): " +
                // newZipFile.getAbsolutePath());
                // System.out.println("file.getName(): " +
                // newZipFile.getName());
                // System.out.println("file.length(): " + newZipFile.length());
                // un-zip
                // old path: String baseDir = newZipFile.getParent() + sep + runtimeID;
                String baseDir = newZipFile.getParent() + sep + runtimeID + sep + pidID;
                // System.out.println("baseDir: " + baseDir);
                // System.out.println("zFile: " + zFile.getAbsolutePath());
                long plussQuotaSize = ZipUtils.getInstance().unZipFile(newZipFile, baseDir);
                // delete zip fileS
                zFile.delete();
                newZipFile.delete();
                // refresh workflow quota
                FileSenderUtils fileSenderUtils = new FileSenderUtils();
                // A localMode jelentese true eseten:
                // a submitter es a storage egy gepen van.
                boolean localMode = false;
                // add pluss file size to workflow quota
                fileSenderUtils.addPlussRtIDQuotaSizeBean(localMode, new QuotaBean(portalID, userID, workflowID, runtimeID, new Long(plussQuotaSize)));
                // parse copy hash, ertelmezes, vegrehajtas + quota frissites...
                fileSenderUtils.parseCopyHash(localMode, portalID, userID, workflowID, runtimeID, baseDir, copyHash);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // send 560 error to client
                response.sendError(560, "Server side exception !!! + " + e);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    /**
     * prepare copy hash from string
     *
     * @return hashtable
     */
    private Hashtable getCopyHashFromString(String hashStr) {
        Hashtable retHash = new Hashtable();
        try {
            String[] strList = hashStr.split("\r\n");
            String filePath = "";
            String fileName = "";
            for (int sPos = 0; sPos < strList.length; sPos++) {
                filePath = fileName;
                fileName = strList[sPos];
                if ((sPos % 2) != 0) {
                    // System.out.println("key   (filePath): " + filePath);
                    // System.out.println("value (fileName): " + fileName);
                    retHash.put(filePath, fileName);
                    filePath = "";
                    fileName = "";
                }
            }
            // System.out.println("copyHash (retHash): " + retHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retHash;
    }
    
    /**
     * Processes the file (one zip file) (download workflow zip file) 
     * given in the request.
     *
     * @param multipartRequest
     */
    private void ZipFileSenderParseFile(MultipartRequest multipartRequest, HttpServletResponse response) {
        try {
            // parse parameterS (parameterek begyujtese)
            // A keresben hasznalt parametereknek ad kezdo erteket.
            // portal service url
            String portalURL = new String("");
            // portal id dir name
            String portalID = new String("");
            // wfs ID
            String wfsID = new String("");
            // user name
            String userID = new String("");
            // Az osszes parameteret kigyujti
            // a feltoltott graf workflow ezt a nevet kapja
            String newGrafName = new String("");
            // a feltoltott abstract workflow ezt a nevet kapja
            String newAbstName = new String("");
            // a feltoltott real, konkret workflow ezt a nevet kapja
            String newRealName = new String("");
            Enumeration enumParameters = multipartRequest.getParameterNames();
            String parameterName = null;
            String parameterValue = null;
            while (enumParameters.hasMoreElements()) {
                parameterName = new String((String) enumParameters.nextElement()).trim();
                if ((parameterName != null) && (!"".equals(parameterName))) {
                    parameterValue = null;
                    parameterValue = new String(multipartRequest.getParameter(parameterName)).trim();
                    if ((parameterValue != null) && (!"".equals(parameterValue))) {
                        // System.out.println("parameterName : " + parameterName);
                        // System.out.println("parameterValue: " + parameterValue);
                        if ("portalURL".equals(parameterName)) {
                            portalURL = parameterValue;
                            // convert url to dirName
                            portalID = FileUtils.getInstance().convertPortalIDtoDirName(portalURL);
                        }
                        if ("wfsID".equals(parameterName)) {
                            wfsID = parameterValue;
                        }
                        if ("userID".equals(parameterName)) {
                            userID = parameterValue;
                        }
                        if ("newGrafName".equals(parameterName)) {
                            newGrafName = parameterValue;
                        }
                        if ("newAbstName".equals(parameterName)) {
                            newAbstName = parameterValue;
                        }
                        if ("newRealName".equals(parameterName)) {
                            newRealName = parameterValue;
                        }
                    }
                }
            }
            // send files in stream to out
            Enumeration enumFiles = multipartRequest.getFileNames();
            while (enumFiles.hasMoreElements()) {
                String fileName = (String) enumFiles.nextElement();
                String fileOriginalFileName = multipartRequest.getOriginalFileName(fileName);
                if(fileOriginalFileName == null) {
                    throw new Exception("Not valid file name !");
                }
                // System.out.println("fileOriginalFileName : " + fileOriginalFileName);
                File uploadedZipFile = null;
                String userBaseDir = repositoryDir + portalID + sep + userID;
                FileUtils.getInstance().createDirectory(userBaseDir);
                String zipPathName = userBaseDir + sep + fileOriginalFileName;
                // System.out.println("zipPathName : " + zipPathName);
                String storageURL = FileUtils.getInstance().getStorageUrl();
                uploadedZipFile = new File(zipPathName);
                // save zip file from stream
                // getFile()
                File zFile = multipartRequest.getFile(fileName);
                // System.out.println("storage zen_ReceiverServiceImpl move to temp file : zFile.getAbsolutePath() : " + zFile.getAbsolutePath() + " -=> zipPathName : " + zipPathName);
                if (!zFile.exists()) {
                    throw new Exception("Not valid zFile ! zFile.getAbsolutePath() : " + zFile.getAbsolutePath());
                }
                // move to temp file
                zFile.renameTo(uploadedZipFile);
                if (uploadedZipFile.length() == 0) {
                    throw new Exception("Not valid file ! size == 0 !");
                }
                // upload zip file to storage
                StorageWorkflowNamesBean bean = new StorageWorkflowNamesBean();
                bean.setPortalID(portalID);
                bean.setPortalURL(portalURL);
                bean.setStorageURL(storageURL);
                bean.setWfsID(wfsID);
                bean.setUserID(userID);
                bean.setZipFilePathStr(zipPathName);
                bean.setNewMainGrafName(newGrafName);
                bean.setNewMainAbstName(newAbstName);
                bean.setNewMainRealName(newRealName);
                // bean = portalURL, storageURL, wfsID, userID, zipPathName, new main workflow names
                ZipUploadUtils.getInstance().uploadZipFileToStorage(bean);
                // response.setContentType("text/html");
                OutputStream out = response.getOutputStream();
                out.write(new String("Workflow upload successfull").getBytes());
            }
        } catch (Exception e) {
            response.setStatus(500);
            e.printStackTrace();
 //           try {
//                response.sendError(500, "Server side exception !!!");
                // send 560 error to client
                // response.setContentType("text/html");
//                OutputStream out = response.getOutputStream();
                // System.out.println("e.getMessage() : " + e.getMessage());
                // System.out.println("e.getLocalizedMessage() : " + e.getLocalizedMessage());
//                out.write(new String("Workflow upload not successfull: " + e.getMessage()).getBytes());
//            } catch (Exception e2) {
//                e2.printStackTrace();
//            }
        }
    }
    
    /**
     * Refreshes the quota registry with the 
     * quota data received in the request.
     *
     * @param multipartRequest
     */
    private void QuotaSenderParseInformations(MultipartRequest multipartRequest, HttpServletResponse response) {
        try {
            // parse parameterS (parameterek begyujtese)
            // Az osszes parameteret kigyujti
            Enumeration enumParameters = multipartRequest.getParameterNames();
            String parameterName = null;
            String parameterValue = null;
            // get bean numbers
            int beanNum = 0;
            try {
                String beanNumber = new String(multipartRequest.getParameter("beanNumbers")).trim();
                beanNum = new Integer(beanNumber);
            } catch (Exception en) {
                en.printStackTrace();
            }
            // System.out.println("beanNum: " + beanNum);
            for (int vPos = 0; vPos < beanNum; vPos++) {
                try {
                    // System.out.println("vPos: " + vPos);
                    String pre = "" + vPos + "#";
                    QuotaBean bean = new QuotaBean();
                    bean.setPortalID(new String(multipartRequest.getParameter(pre + "portalID")).trim());
                    bean.setUserID(new String(multipartRequest.getParameter(pre + "userID")).trim());
                    bean.setWorkflowID(new String(multipartRequest.getParameter(pre + "workflowID")).trim());
                    bean.setRuntimeID(new String(multipartRequest.getParameter(pre + "runtimeID")).trim());
                    bean.setPlussQuotaSize(new Long(multipartRequest.getParameter(pre + "plussQuotaSize")));
                    // quota refresh...
                    QuotaService.getInstance().addPlussRtIDQuotaSize(bean);
                } catch (Exception em) {
                    em.printStackTrace();
                }
            }
            // parse files...
            // Enumeration enumFiles = multipartRequest.getFileNames();
            // while (enumFiles.hasMoreElements()) {
            // nothing to do...
            // }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // send 560 error to client
                response.sendError(560, "Server side exception !!! + " + e);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
}

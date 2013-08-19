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
package hu.sztaki.lpds.storage.service.carmen.client;

import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.ZipUploadUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.ZipUtils;
import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Sends a zipped file (download workflow zip file) to the storage
 * 
 * @author lpds
 */
public class ZipFileSender {
    
    // File separator (for example, "/")
    private String sep;
    
    // storage receiver servlet url
    private String storageURL;
    
    // the name of the zip file to be sent (full path)
    private String sendZipFilePath;
    
    // portal service url
    private String portalURL;
    
    // wfs service url
    private String wfsID;
    
    // user name
    private String userID;
    
    // the name of the uploaded graph workflow
    private String newGrafName;
    
    // the name of the uploaded abstract workflow
    private String newAbstName;
    
    // the name of the uploaded real, concrete workflow
    private String newRealName;
    
    /**
     * Parameter is the URL of the storage service
     *
     * (http://localhost:8080/storage)
     *
     * (the /receiver is not needed to the end!)
     *
     * @param storageURL
     */
    public ZipFileSender(String storageURL) {
        this.storageURL = storageURL;
        sep = FileUtils.getInstance().getSeparator();
    }
    
    /**
     * Setting the sending parameters
     *
     * @param sendZipFilePath -
     *            where the zip file to be sent is located (full path)
     * @param portalURL -
     *            portal ID (url)
     * @param wfsID
     * @param userID
     * @param newGrafName -
     *            the name of the uploaded graph workflow
     * @param newAbstName -
     *            the name of the abstract workflow
     * @param newRealName -
     *            the name of the real, concrete workflow
     * @throws Exception
     */
    public void setParameters(String sendZipFilePath, String portalURL, String wfsID, String userID, String newGrafName, String newAbstName, String newRealName) throws Exception {
        // this.portalID = FileUtils.getInstance().convertPortalIDtoDirName(portalURL);
        if (newGrafName == null) {
            newGrafName = new String("");
        }
        if (newAbstName == null) {
            newAbstName = new String("");
        }
        if (newRealName == null) {
            newRealName = new String("");
        }
        this.portalURL = portalURL;
        this.wfsID = wfsID;
        this.userID = userID;
        this.newGrafName = newGrafName;
        this.newAbstName = newAbstName;
        this.newRealName = newRealName;
        this.sendZipFilePath = sendZipFilePath;
        if (!validParameters()) {
            throw new Exception("Zip file not exist ! or not valid parameters: portalURL, wsfID, userID !");
        }
    }
    
    /**
     * Checks whether the base parameter values are real or not
     *
     * @return
     */
    private boolean validParameters() {
        if ((sendZipFilePath != null) && (portalURL != null) && (wfsID != null) && (userID != null)) {
            if ((!"".equals(sendZipFilePath)) && (!"".equals(portalURL)) && (!"".equals(wfsID)) && (!"".equals(userID))) {
                File sendZipFile = new File(sendZipFilePath);
                if ((sendZipFile.exists()) && (sendZipFile.isFile()) && (sendZipFile.length()>0)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Sends the zip file indicated by the parameters 
     * (download workflow zip file) to the storage
     *
     * @param localMode
     *
     * (if the submitter and the storage are on the same machine, than it is true, else false)
     * @throws Exception file sending error
     */
    public void sendZipFile(boolean localMode) throws Exception {
        if (localMode) {
            sendLocalZipFile();
        } else {
            // In case of repository import it is
            // always in remote mode!
            sendRemoteZipFile();
        }
    }
    
    /**
     * The zip file given as basic parameter will be copied 
     * to the given directory without network communication.
     * 
     * not use httpURLConnection.connect()
     *
     * @throws Exception
     */
    private void sendLocalZipFile() throws Exception {
        if (validParameters()) {
            String repositoryDir = FileUtils.getInstance().getRepositoryDir();
            // convert url to dirName
            String portalID = FileUtils.getInstance().convertPortalIDtoDirName(portalURL);
            String userBaseDir = repositoryDir + portalID + sep + userID;
            FileUtils.getInstance().createDirectory(userBaseDir);
            String zipPathName = userBaseDir + sep + ZipUtils.getInstance().getUniqueZipFileName();
            // System.out.println("zipPathName : " + zipPathName);
            // copy file from to
            FileUtils.getInstance().copyFileToFileWithPaths(sendZipFilePath, zipPathName);
            File uploadedZipFile = new File(zipPathName);
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
        } else {
            throw new Exception("Zip file not exist ! or not valid parameters: portalURL, wsfID, userID !");
        }
    }
    
    /**
     * Sends the zip file given in the basic 
     * parameters with POST request to the given servlet.
     */
    private void sendRemoteZipFile() throws Exception {
        if (validParameters()) {
            URL url = new URL(storageURL + "/receiver");

//            System.out.println("******************************************");
//            System.out.println(storageURL + "/receiver");
//            System.out.println("******************************************");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            RequestUtils requestUtils = new RequestUtils();
            // add my parameterS to preRequest
            requestUtils.preRequestAddParameter("senderObj", "ZipFileSender");
            requestUtils.preRequestAddParameter("wfiType", "zen");
            requestUtils.preRequestAddParameter("portalURL", this.portalURL);
            requestUtils.preRequestAddParameter("wfsID", this.wfsID);
            requestUtils.preRequestAddParameter("userID", this.userID);
            requestUtils.preRequestAddParameter("newGrafName", this.newGrafName);
            requestUtils.preRequestAddParameter("newAbstName", this.newAbstName);
            requestUtils.preRequestAddParameter("newRealName", this.newRealName);
            // create file request
            String zipFileName = ZipUtils.getInstance().getUniqueZipFileName();
            requestUtils.preRequestAddFile("zipFileName", zipFileName);
            // create postRequest
            requestUtils.createPostRequest();
            // System.out.println("boundary : " + requestUtils.getBoundary());
            // System.out.println("===========================");
            // set post httpURLConnection parameterS
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + requestUtils.getBoundary());
            httpURLConnection.setRequestMethod("POST");
            // ChunkedStream enable begin...
            // httpURLConnection.setChunkedStreamingMode(0);
            // ChunkedStream enable end...
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            try {
                httpURLConnection.connect();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Cannot connect to: " + storageURL, e);
            }
            // create connection output stream
            OutputStream out = httpURLConnection.getOutputStream();
            // send pre request data
            byte[] preBytes = requestUtils.getPreRequestStringBytes();
            out.write(preBytes);
            out.flush();
            // send zip file in stream to out
            FileUtils.getInstance().sendFileToStream(out, sendZipFilePath);
            // send post request data
            byte[] postBytes = requestUtils.getPostRequestStringBytes();
            out.write(postBytes);
            // close out connection
            out.flush();
            out.close();
            // create connection input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String retMess = in.readLine() + "\n";
            while (in.ready()) {
                retMess += in.readLine() + "\n";
            }
            //System.out.println("retMess in receiver : " + retMess);
            in.close();
            if (HttpURLConnection.HTTP_OK != httpURLConnection.getResponseCode()) {
                throw new Exception("response not HTTP_OK !");
            }
/*magic keyword must be deleted*/
            if (!"Workflow upload successfull".equals(retMess.trim())) {
                throw new Exception("-"+retMess+"-"+("Workflow upload successfull".equals(retMess)));
            }
        } else {
            throw new Exception("Zip file not exist ! or not valid parameters: portalURL, wsfID, userID !");
        }
    }
    
}

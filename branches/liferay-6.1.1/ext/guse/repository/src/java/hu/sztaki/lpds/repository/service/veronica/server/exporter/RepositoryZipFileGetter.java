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
package hu.sztaki.lpds.repository.service.veronica.server.exporter;

import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.repository.service.veronica.commons.RepositoryFileUtils;
import hu.sztaki.lpds.repository.service.veronica.commons.RepositoryRequestUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Egy zip file-t (workflow-t) ker el, tolt le a storage-rol,
 * es menti le a repository teruletere.
 *
 * @author lpds
 */
public class RepositoryZipFileGetter {
    
    // File separator (for example, "/")
    private String sep;
    
    // copy file buffer size
    private static final int bufferSize = 4 * 1024;
    
    public RepositoryZipFileGetter() {
        sep = RepositoryFileUtils.getInstance().getSeparator();
    }
    
    /**
     * A beallitott parameterek alltal meghatarozott
     * zip file-t (workflow-t) elkeri a storage-tol.
     *
     * @param RepositoryWorkflowBean bean - workflow parameterek
     * @return zip file merete byte-ban
     */
    public long getZipFiles(RepositoryWorkflowBean bean) throws Exception {
        /*
         * @param localMode - boolean
         * (localMode - ha a repository es a storage egy
         * gepen van akkor true, kulonben false)
        boolean localMode = bean.getStor and bean.getRepo...
        if (localMode) {
            return getLocalZipFiles();
        } else {
            return getRemoteZipFiles();
        }
         */
        return getRemoteZipFiles(bean);
    }
    
    /**
     * A beallitott parameterek alltal meghatarozott
     * zip file-t (workflow-t) elkeri a storage-tol.
     *
     * not use httpURLConnection.connect()
     *
     * @param RepositoryWorkflowBean bean - workflow parameterek
     *
     * @return zip file merete byte-ban
     * @throws Exception
     */
    private long getLocalZipFiles(RepositoryWorkflowBean bean) throws Exception {
        /*
        try {
            String repositoryDir = FileUtils.getInstance().getRepositoryDir();
            // create user base dir
            String userBaseDirStr = repositoryDir + portalID + sep + userID;
            // copy hash files to getFilesDir
            FileUtils.getInstance().copyHashAllFilesToDirectory(userBaseDirStr, fileRenameHash, getFilesDir);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Server Side Exeption !!!");
        }
         */
        return 0;
    }
    
    /**
     * A beallitott parameterek alltal meghatarozott zip file-t
     * (workflow-t) POST keressel keri es kapja meg a storgae-tol.
     *
     * @param RepositoryWorkflowBean bean - workflow parameterek
     *
     * @return zip file merete byte-ban
     * @throws Exception
     */
    private long getRemoteZipFiles(RepositoryWorkflowBean bean) throws Exception {
        File zipFile = new File(bean.getZipFileFullPath());
        // System.out.println("create parent dir : " + zipFile.getParent());
        FileUtils.getInstance().createDirectory(zipFile.getParent());
        // create storage download servlet url
        URL url = new URL(bean.getStorageID() + "/download");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        RepositoryRequestUtils requestUtils = new RepositoryRequestUtils();
        // add my parameterS to preRequest
        requestUtils.preRequestAddParameter("senderObj", "RepositoryZipFileGetter");
        requestUtils.preRequestAddParameter("portalID", bean.getPortalID());
        requestUtils.preRequestAddParameter("wfsID", bean.getWfsID());
        requestUtils.preRequestAddParameter("userID", bean.getUserID());
        requestUtils.preRequestAddParameter("workflowID", bean.getWorkflowID());
        requestUtils.preRequestAddParameter("downloadType", bean.getDownloadType());
        requestUtils.preRequestAddParameter("instanceType", bean.getInstanceType());
        requestUtils.preRequestAddParameter("outputLogType", bean.getOutputLogType());
        requestUtils.preRequestAddParameter("exportType", bean.getExportType());
        // set post httpURLConnection parameterS
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        boolean resp = false;
        int respCode = 0;
        try {
            httpURLConnection.connect();
            // create connection output stream
            OutputStream out = httpURLConnection.getOutputStream();
            // send pre request data
            byte[] preBytes = requestUtils.getPreRequestStringBytes();
            out.write(preBytes);
            out.flush();
            // send post request data
            byte[] postBytes = requestUtils.getPostRequestStringBytes();
            out.write(postBytes);
            // close out connection
            out.flush();
            out.close();
            // create connection input stream
            // BufferedReader in = new BufferedReader(new
            // InputStreamReader(httpURLConnection.getInputStream()));
            // BufferedReader inbr = new BufferedReader(new
            // InputStreamReader(httpURLConnection.getInputStream()));
            respCode = httpURLConnection.getResponseCode();
            // System.out.println("respCode: " + respCode);
            if (HttpURLConnection.HTTP_OK == respCode) {
                resp = true;
                int writeLen;
                byte dataBuff[] = new byte[bufferSize];
                InputStream in = httpURLConnection.getInputStream();
                // save zip file from stream
                BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(zipFile), bufferSize);
                while ((writeLen = in.read(dataBuff)) > 0) {
                    fileOut.write(dataBuff, 0, writeLen);
                }
                fileOut.flush();
                in.close();
                fileOut.close();
            } else if (respCode == 500) {
                resp = false;
                // throw new Exception("response not HTTP_OK ! respCode = (" + respCode + ")");
                throw new Exception(httpURLConnection.getResponseMessage());
            } else if (respCode == 560) {
                // response == Server Side Remote Exeption !!!
                resp = false;
                // System.out.println("Server Side Remote Exeption !!!");
                throw new Exception("Server Side Remote Exeption !!! respCode = (" + respCode + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
            zipFile.delete();
            // throw new Exception("Cannot connect to: " + url, e);
            throw e;
        }
        return zipFile.length();
    }
    
}

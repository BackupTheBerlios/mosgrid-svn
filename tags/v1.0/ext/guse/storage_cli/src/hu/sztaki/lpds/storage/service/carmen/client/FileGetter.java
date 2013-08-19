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
import hu.sztaki.lpds.storage.service.carmen.commons.ZipUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Gets the zipped content of a directory from the storage
 * 
 * @author lpds
 */
public class FileGetter {

    // File separator (for example, "/")
    private String sep;

    // storage receiver servlet URL
    private String storageUrlString;

    // storage repository directory
    private String repositoryDir;

    // the received file's (inputs) directory
    private String getFilesDir;

    // portal service URL
    private String portalURL;

    // portalURL convert to dir name
    private String portalID;

    // user name
    private String userID;

    // The hash table storing the file rename data,
    // stores the old name - new name pairs
    // oldPathName: "workflow/job1/inputs/0/file1.txt" or
    // "/workflow/job2/inputs/1/file2.txt"
    // fileRenameHash = (key: newName, value: oldPathName)
    private Hashtable fileRenameHash;

    /**
     * Parameter is the URL of the storage service(receiverServlet).
     * 
     * (http://localhost:8080/storage/receiver)
     *
     * @param storageUrlString
     */
    public FileGetter(String storageUrlString) {
        this.storageUrlString = storageUrlString;
        this.repositoryDir = FileUtils.getInstance().getRepositoryDir();
        // sep = FileUtils.getInstance().getSeparator();
        sep = System.getProperty("file.separator");
        if (sep == null) {
            sep = "/";
        }
    }

    /**
     * Searches for parameter settings
     * 

     * @param getFilesDir
     *            where the files will be unpacked
     * @param portalURL
     * @param userID
     * @param fileRenameHash
     *            rename descriptor hash
     * 
     * (key: newName, value: oldPathName)
     * 
     * ("newFileName1.txt", "/testworkflow1/job1/inputs/1/file1.txt")
     * ("newFileName2.txt", "testworkflow2/job2/inputs/2/file2.txt")
     * 
     * @throws Exception
     */
    public void setParameters(String getFilesDir, String portalURL, String userID, Hashtable fileRenameHash) throws Exception {
        this.portalURL = portalURL;
        this.portalID = FileUtils.getInstance().convertPortalIDtoDirName(portalURL);
        this.userID = userID;
        this.fileRenameHash = fileRenameHash;
        if (sep == null) {
            sep = "/";
        }
        // if "/" end
        if (getFilesDir != null) {
            if (!getFilesDir.endsWith(sep)) {
                getFilesDir += sep;
            }
        }
        FileUtils.getInstance().createDirectory(getFilesDir);
        this.getFilesDir = getFilesDir;
        if (!validParameters()) {
            throw new Exception("FilesDir not exist ! or not valid parameters: portalURL, userID or hashTable !");
        }
    }

    /**
     * Checks whether the base parameter values are real or not
     * 
     * @return
     */
    private boolean validParameters() {
        if ((getFilesDir != null) && (portalID != null) && (userID != null) && (fileRenameHash != null)) {
            if ((!"".equals(getFilesDir)) && (!"".equals(portalID)) && (!"".equals(userID))) {
                return true;
            }
        }
        return false;
    }    
    
    /**
     * Gets the files indicated by the parameters from the storage
     * 
     * @param localMode 
     * 
     * (if the submitter and the storage are on the same machine, than it is true, else false)
     * 
     * @return true/false
     * @throws Exception file access error
     */
    public boolean getFiles(boolean localMode) throws Exception {
        // remote work for debug
        // localMode = false;
        //
        // System.out.println("FileGetter local mode     : " + localMode);
        // System.out.println("FileGetter fileRenameHash : " + fileRenameHash);
        // remote work for debug
        if (localMode) {
            return getLocalFiles();
        } else {
            return getRemoteFiles();
        }
    }

    /**
     * The files given as basic parameters will be copied 
     * to the given directory without network communication.
     * 
     * not use httpURLConnection.connect()
     * 
     * @return true / false
     * @throws Exception
     */
    private boolean getLocalFiles() throws Exception {
        try {
            // create user base dir
            String userBaseDirStr = repositoryDir + portalID + sep + userID;
            int reCnt = 0;
            while (reCnt < 3) {
                try {
                    // copy hash files to getFilesDir
                    FileUtils.getInstance().copyHashAllFilesToDirectory(userBaseDirStr, fileRenameHash, getFilesDir);
                    reCnt = 4;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    reCnt++;
                    System.out.println("reCnt : " + reCnt);
                    try {
                        //sleep
                        Thread.sleep(2000);
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Server Side Exeption !!!");
        }
        return true;
    }

    /**
     * Requests and gets the files given in the basic 
     * parameters with POST request from the given servlet.
     * 
     * @return true / false
     * @throws Exception
     */
    private boolean getRemoteFiles() throws Exception {
        boolean resp = false;
        int respCode = 0;
        URL url = new URL(storageUrlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        RequestUtils requestUtils = new RequestUtils();
        // add my parameterS to preRequest
        requestUtils.preRequestAddParameter("senderObj", "FileGetter");
        requestUtils.preRequestAddParameter("wfiType", "zen");
        requestUtils.preRequestAddParameter("portalID", this.portalID);
        requestUtils.preRequestAddParameter("userID", this.userID);
        // add rename file parameters
        addRenameFileParameters(requestUtils);
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
                // System.out.println("in begin...");
                InputStream in = httpURLConnection.getInputStream();
                // InputStreamReader inputStreamReader = new
                // InputStreamReader(in);
                // int x = 0;
                // while ((x = in.read()) != 333) {
                // System.out.println("while...");
                // }
                // if (inputStreamReader.read() == 333) {
                // System.out.println("<333> begin...");
                ZipUtils.getInstance().getFilesFromStream(in, getFilesDir);
                // System.out.println("<333> end...");
                // }
                in.close();
                // System.out.println("in end...");
            }
            if (respCode == 500) {
                resp = false;
                // throw new Exception("response not HTTP_OK ! respCode = ("
                // + respCode + ")");
            }
            // response == Server Side Remote Exeption !!!
            if (respCode == 560) {
                resp = false;
                // System.out.println("Server Side Remote Exeption !!!");
                throw new Exception("Server Side Remote Exeption !!! respCode = (" + respCode + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Cannot connect to: " + storageUrlString, e);
        }
        return resp;
    }

    /**
     * Regarding to the basic parameter places the required file renaming parameters
     * 
     * @param requestUtils
     */
    private void addRenameFileParameters(RequestUtils requestUtils) {
        Enumeration enumKey = fileRenameHash.keys();
        while (enumKey.hasMoreElements()) {
            String newName = (String) enumKey.nextElement();
            String oldPathName = (String) fileRenameHash.get(newName);
            requestUtils.preRequestAddRenameFile(newName, oldPathName);
        }
    }

}

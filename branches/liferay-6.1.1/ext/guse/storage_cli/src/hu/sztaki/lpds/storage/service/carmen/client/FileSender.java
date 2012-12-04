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

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.storage.service.carmen.commons.FileSenderUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.ZipUtils;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Gets the zipped content of a directory to the storage
 *
 * @author lpds
 */
public class FileSender {
    
    // A full Upload request:
    // begin...
    // Content-type: multipart/form-data, boundary=AaB03x
    //
    // --AaB03x
    // content-disposition: form-data; name="field1Name"
    //
    // field1Value
    // --AaB03x
    // content-disposition: form-data; name="pics"; filename="file1.txt"
    // Content-Type: text/plain
    //
    // ... contents of file1.txt ...
    //
    // --AaB03x--
    // end...
    
    // POST /storage/receiver HTTP/1.1
    // Content-Length: 399
    // content-type: multipart/form-data; boundary=60868632204341
    // Cache-Control: no-cache
    // Pragma: no-cache
    // User-Agent: Java/1.5.0_06
    // Host: localhost:9080
    // Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
    // Connection: keep-alive
    //
    // --60868632204341
    // content-disposition: form-data; name="portalID"
    //
    // testportalID
    // --60868632204341
    // content-disposition: form-data; name="userID"
    //
    // testuserID
    // --60868632204341
    // content-disposition: form-data; name="testname";
    // filename="testfilename.txt"
    // content-type: application/zip
    //
    // 123...............file.tartalom...............321
    //
    // --60868632204341--HTTP/1.1 200 OK
    // Server: Apache-Coyote/1.1
    // Content-Length: 0
    // Date: Fri, 04 Aug 2006 13:37:09 GMT
    
    // File separator (for example, "/")
    private String sep;
    
    // storage receiver servlet url
    private String storageUrlString;
    
    // the directory of the files (outputs) to be sent
    private String sendFilesDir;
    
    // portal service url
    private String portalURL;
    
    // portalURL convert to dir name
    private String portalID;
    
    // user name
    private String userID;
    
    // workflow name
    private String workflowID;
    
    // job name
    private String jobID;
    
    // runtime ID
    private String runtimeID;
    
    // copy hash, if files needed to be copied to 
    // extra locations than it should be given here
    // (key: to where, value: what)
    // pl: ("/copyworkflow2/copyjob2/outputs/copyRtID2/pid/copynewfilename2.txtcopy", "newFileName2.txt");
    private Hashtable copyHash;

    // job pid ID
    private String pidID;

    /**
     * Parameter is the URL of the storage service(receiverServlet).
     *
     * (http://localhost:8080/storage/receiver)
     *
     * @param storageUrlString
     */
    public FileSender(String storageUrlString) {
        this.storageUrlString = storageUrlString;
        sep = FileUtils.getInstance().getSeparator();
    }
    
    /**
     * Sender parameter settings
     *
     * @param sendFilesDir -
     *            where the files to be sent are
     * @param portalURL
     * @param userID
     * @param workflowID
     * @param jobID
     * @param pidID
     * @param runtimeID
     * @param copyHash
     * @throws Exception
     */
    public void setParameters(String sendFilesDir, String portalURL, String userID, String workflowID, String jobID, String pidID, String runtimeID, Hashtable copyHash) throws Exception {
        this.portalURL = portalURL;
        this.portalID = FileUtils.getInstance().convertPortalIDtoDirName(portalURL);
        this.userID = userID;
        this.workflowID = workflowID;
        this.jobID = jobID;
        this.pidID = pidID;
        this.runtimeID = runtimeID;
        this.copyHash = copyHash;
        if (this.copyHash == null) {
            this.copyHash = new Hashtable();
        }
        // if "/" end
        if (!sendFilesDir.endsWith(sep)) {
            sendFilesDir += sep;
        }
        this.sendFilesDir = sendFilesDir;
        if (!validParameters()) {
            throw new Exception("FilesDir not exist ! or not valid parameters: portalURL, userID, workflowID, jobID, pidID, runtimeID !");
        }
    }
    
    /**
     * Checks whether the base parameter values are real or not
     *
     * @return
     */
    private boolean validParameters() {
        if ((sendFilesDir != null) && (portalID != null) && (userID != null) && (workflowID != null) && (jobID != null) && (pidID != null) && (runtimeID != null) && (copyHash != null)) {
            if ((!"".equals(sendFilesDir)) && (!"".equals(portalID)) && (!"".equals(userID)) && (!"".equals(workflowID)) && (!"".equals(jobID)) && (!"".equals(pidID)) && (!"".equals(runtimeID))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sends the files indicated by the parameters to the storage
     *
     * @param localMode
     *
     * (if the submitter and the storage are on the same machine, than it is true, else false)
     * @throws Exception file sending error
     */
    public void sendFiles(boolean localMode) throws Exception {
        // remote work for debug
        // localMode = false;
        //
        // System.out.println("FileSender local mode : " + localMode);
        // System.out.println("FileSender copyHash   : " + copyHash);
        // remote work for debug
        if (localMode) {
            sendLocalFiles(localMode);
        } else {
            sendRemoteFiles();
        }
    }
    
    /**
     * The files given as basic parameters will be copied (moved) 
     * to the given directory without network communication.
     *
     * not use httpURLConnection.connect()
     *
     * @throws Exception
     */
    private void sendLocalFiles(boolean localMode) throws Exception {
        // If the localMode is true:
        // the submitter and the storage are on the same machine.
        //
        FileSenderUtils fileSenderUtils = new FileSenderUtils();
        //
        if (validParameters()) {
            String repositoryDir = FileUtils.getInstance().getRepositoryDir();
            String jobbase = repositoryDir + portalID + sep + userID + sep + workflowID + sep + jobID;
            // System.out.println("jobbase: " + jobbase);
            // Make job directory
            File basedirin = new File(jobbase + sep + "inputs");
            basedirin.mkdirs();
            File basedirout = new File(jobbase + sep + "outputs");
            basedirout.mkdirs();
            // old path: File basedirrun = new File(jobbase + sep + "outputs" + sep + runtimeID);
            File basedirrun = new File(jobbase + sep + "outputs" + sep + runtimeID + sep + pidID);
            basedirrun.mkdirs();
            // old path: String runtimeBaseDir = new String(jobbase + sep + "outputs" + sep + runtimeID);
            String runtimeBaseDir = new String(jobbase + sep + "outputs" + sep + runtimeID + sep + pidID);
            // System.out.println("runtimeBaseDir: " + runtimeBaseDir);
            //
            // only move:
            long plussQuotaSize = FileUtils.getInstance().moveDirAllFilesToDirectory(sendFilesDir, runtimeBaseDir);
            // only copy:
            // long plussQuotaSize = FileUtils.getInstance().copyDirAllFilesToDirectory(sendFilesDir, runtimeBaseDir);
            //
            // parse copy hash, processing, executing + quota refresh...
            // version before the old link: fileSenderUtils.parseCopyHash(localMode, portalID, userID, workflowID, runtimeID, sendFilesDir, copyHash);
            fileSenderUtils.parseCopyHash(localMode, portalID, userID, workflowID, runtimeID, runtimeBaseDir, copyHash);
            //
            // refresh workflow quota
            // add plus file size to workflow quota
            fileSenderUtils.addPlussRtIDQuotaSizeBean(localMode, new QuotaBean(portalID, userID, workflowID, runtimeID, new Long(plussQuotaSize)));
            // System.out.println("plussQuotaSize: " + plussQuotaSize);
        } else {
            try {
                // parse copy hash, ertelmezes, vegrehajtas + quota frissites...
                fileSenderUtils.parseCopyHash(localMode, portalID, userID, workflowID, runtimeID, sendFilesDir, copyHash);
                throw new Exception("FilesDir not exist ! or not valid parameters: portalID, userID, workflowID, jobID, pidID, runtimeID !");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //
        // send all quota beans to storage
        if ("true".equals(PropertyLoader.getInstance().getProperty("guse.storageclient.localmode.sendquota"))) {
            fileSenderUtils.sendQuotaInformationsToStorage(storageUrlString);
        }
    }
    
    /**
     * Sends the files given in the basic parameters
     * with POST request to the given servlet.
     *
     */
    private void sendRemoteFiles() throws Exception {
        if (validParameters()) {
            URL url = new URL(storageUrlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            RequestUtils requestUtils = new RequestUtils();
            // add my parameterS to preRequest
            requestUtils.preRequestAddParameter("senderObj", "FileSender");
            requestUtils.preRequestAddParameter("wfiType", "zen");
            requestUtils.preRequestAddParameter("portalID", this.portalID);
            requestUtils.preRequestAddParameter("userID", this.userID);
            requestUtils.preRequestAddParameter("workflowID", this.workflowID);
            requestUtils.preRequestAddParameter("jobID", this.jobID);
            requestUtils.preRequestAddParameter("pidID", this.pidID);
            requestUtils.preRequestAddParameter("runtimeID", this.runtimeID);
            // add copy file parameters (use copyHash)
            requestUtils.preRequestAddParameter("copyhash", getCopyHashStr());
            // old methode:
            // addCopyFileParameters(requestUtils);
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
                // create connection output stream
                OutputStream out = httpURLConnection.getOutputStream();
                // send pre request data
                byte[] preBytes = requestUtils.getPreRequestStringBytes();
                out.write(preBytes);
                out.flush();
                // send files in zipStream to out
                ZipUtils.getInstance().sendDirAllFilesToStream(sendFilesDir, out);
                // send post request data
                byte[] postBytes = requestUtils.getPostRequestStringBytes();
                out.write(postBytes);
                // close out connection
                out.flush();
                out.close();
                // create connection input stream
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                in.readLine();
                in.close();
                if (HttpURLConnection.HTTP_OK != httpURLConnection.getResponseCode()) {
                    throw new Exception("response not HTTP_OK !");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Cannot connect to: " + storageUrlString, e);
            }
        } else {
            throw new Exception("FilesDir not exist ! or not valid parameters: portalID, userID, workflowID, jobID, pidID, runtimeID !");
        }
    }
    
    /**
     * Returns the copyhash descriptor hash value:
     * first row is key, second row is value and so on..
     *
     * @return copyhash string
     */
    private String getCopyHashStr() {
        // System.out.println("FileSender copyHash 2 : " + copyHash);
        String copyHashStr = "";
        try {
            Enumeration enumKey = copyHash.keys();
            while (enumKey.hasMoreElements()) {
                String filePath = (String) enumKey.nextElement();
                // System.out.println("key   : " + filePath);
                String fileName = (String) copyHash.get(filePath);
                // System.out.println("value : " + fileName);
                copyHashStr += filePath + "\r\n";
                copyHashStr += fileName + "\r\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        copyHashStr += "end" + "\r\n";
        return copyHashStr;
    }
    
}

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

import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Sends the quota information collected
 *  on the submitter side to the storage
 * (if the submitter is on local mode than
 * the quota information needed to be sent
 * with an independent call to storage)
 *
 * @author lpds
 */
public class QuotaSender {
    
    // storage receiver servlet url
    private String storageUrlString;
    
    // quota bean list
    // there are QuotaBeans in the vector
    private Vector quotaBeans;
    
    /**
     * Parameter is the URL of the storage service (receiverServlet).
     *
     * (http://localhost:8080/storage/receiver)
     *
     * @param storageUrlString
     */
    public QuotaSender(String storageUrlString) {
        this.storageUrlString = storageUrlString;
    }
    
    /**
     * Setting the sending parameters
     *
     * @param quotaBeans
     * @throws Exception
     */
    public void setParameters(Vector quotaBeans) throws Exception {
        this.quotaBeans = quotaBeans;
        if (!validParameters()) {
            throw new Exception("Not valid parameters: quotaBeans !");
        }
    }
    
    /**
     * Checks whether the base parameter values are real or not
     *
     * @return
     */
    private boolean validParameters() {
        if (quotaBeans != null) {
            return true;
        }
        return false;
    }
    
    /**
     * Sends the set parameters to the storage
     *
     * @param localMode
     *
     * (if the submitter and the storage are on the same machine, than it is true, else false)
     * @throws Exception quota data sending error
     */
    public void sendInformations(boolean localMode) throws Exception {
        sendLocal();
        //if (localMode) {
        //    sendLocal();
        //} else {
        //    sendRemote();
        //}
    }
    
    /**
     * Sends the files given in the basic 
     * parameters with POST request to the given servlet.
     */
    private void sendLocal() throws Exception {
        if (validParameters()) {
            URL url = new URL(storageUrlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            RequestUtils requestUtils = new RequestUtils();
            // add my parameterS to preRequest
            requestUtils.preRequestAddParameter("senderObj", "QuotaSender");
            requestUtils.preRequestAddParameter("beanNumbers", new String().valueOf(quotaBeans.size()));
            for (int vPos = 0; vPos < quotaBeans.size(); vPos++) {
                QuotaBean bean = (QuotaBean) quotaBeans.get(vPos);
                requestUtils.preRequestAddParameter("" + vPos + "#portalID", bean.getPortalID());
                requestUtils.preRequestAddParameter("" + vPos + "#userID", bean.getUserID());
                requestUtils.preRequestAddParameter("" + vPos + "#workflowID", bean.getWorkflowID());
                requestUtils.preRequestAddParameter("" + vPos + "#runtimeID", bean.getRuntimeID());
                requestUtils.preRequestAddParameter("" + vPos + "#plussQuotaSize", bean.getPlussQuotaSize().toString());
            }
            // ...
            // create file request
            requestUtils.preRequestAddFile("zipFileName", "dummyZipFileName.zip");
            // ...
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
                // send dummy file in out stream
                out.write(new String("dummyFile").getBytes());
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
            throw new Exception("Not valid parameters: quotaBeans !");
        }
    }
    
    // /**
    // * The files given as basic parameters will be sent 
    // * to the storage without network communication.
    // *
    // * not use httpURLConnection.connect()
    // *
    // * @throws Exception
    // */
    // private void sendRemote() throws Exception {
    //    if (validParameters()) {
    //        for (int vPos = 0; vPos < quotaBeans.size(); vPos++) {
    //            QuotaService.getInstance().addPlussRtIDQuotaSize((QuotaBean) quotaBeans.get(vPos));
    //        }
    //    } else {
    //        throw new Exception("Not valid parameters: quotaBeans !");
    //    }
    // }
    
}

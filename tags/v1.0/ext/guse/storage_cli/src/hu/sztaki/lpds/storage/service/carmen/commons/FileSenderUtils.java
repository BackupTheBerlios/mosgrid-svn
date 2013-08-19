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
package hu.sztaki.lpds.storage.service.carmen.commons;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.storage.inf.SubmitterStorageClient;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaBean;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaService;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * It is used by the FileSender and the FileReceiverServlet.(helper class)
 *
 * @author lpds
 */
public class FileSenderUtils {
    
    // private static FileSenderUtils instance = null;
    
    // File separator (for example, "/")
    private String sep;
    
    // repository root dir
    private String repositoryDir;
    
    // quota beans are in this vector
    // before we send them to the storage
    private Vector quotaBeans;
/**
 * Constructor
 */
    public FileSenderUtils() {
        // if (instance == null) {
        //    instance = this;
        sep = FileUtils.getInstance().getSeparator();
        repositoryDir = FileUtils.getInstance().getRepositoryDir();
        quotaBeans = new Vector();
        //}
    }
    
    /**
     * Returns a FileSenderUtils instance.
     *
     * @return
     */
    // public static FileSenderUtils getInstance() {
    //    if (instance == null) {
    //        instance = new FileSenderUtils();
    //    }
    //    return instance;
    // }
    
    /**
     * Loops through the copyHash given in the parameters and copies 
     * the necessary files. After every file it refreshes the target workflow's quota.
     *
     * @param localMode -
     *            The meaning of localMode in case of true:
     *            the submitter and the storage are on the same machine.
     * @param portalID -
     *            portal ID
     * @param userID -
     *            user name
     * @param workflowID -
     *            workflow ID
     * @param runtimeID -
     *            runtime ID
     * @param baseDir -
     *            where the source files are stored
     * @param copyHash -
     *            the copy descriptor hashtable
     * @throws Exception
     */
    public void parseCopyHash(boolean localMode, String portalID, String userID, String workflowID, String runtimeID, String baseDir, Hashtable copyHash) throws Exception {
        String userBaseDir = repositoryDir + portalID + sep + userID;
        // System.out.println("copyHash: " + copyHash);
        String outputsAndRuntimeID = sep + "outputs" + sep + runtimeID + sep;
        // System.out.println("outputsAndRuntimeID: " + outputsAndRuntimeID);
        Enumeration enumKey = copyHash.keys();
        while (enumKey.hasMoreElements()) {
            String filePath = (String) enumKey.nextElement();
            String fullFilePath = new String("");
            if (filePath.startsWith(sep)) {
                fullFilePath = userBaseDir + filePath;
            } else {
                fullFilePath = userBaseDir + sep + filePath;
            }
            String fileName = (String) copyHash.get(filePath);
            try {
                //
                long plussQuotaSize = 0;
                //
                if (fullFilePath.contains(outputsAndRuntimeID)) {
                    // if there is not an embedded channel output file
                    //
                    // create link
                    plussQuotaSize = FileUtils.getInstance().createLink(baseDir, fileName, fullFilePath);
                } else {
                    // if there is an embedded channel output file
                    // the output file needed to be copied under an other workflow
                    //
                    // copy
                    plussQuotaSize = FileUtils.getInstance().copyFile(baseDir, fileName, fullFilePath);
                }
                //
                File file2 = new File(fullFilePath);
                // the workflowID and the runtimeID can be
                // different in case of the execution of an embedded workflow
                String fileWorkflowID = file2.getParentFile().getParentFile().getParentFile().getParentFile().getName();
                String fileRuntimeID = file2.getParentFile().getName();
                // System.out.println("fullFilePath   : " + fullFilePath);
                // System.out.println("plussQuotaSize : " + plussQuotaSize);
                // System.out.println("workflowID     : " + workflowID);
                // System.out.println("runtimeID      : " + runtimeID);
                // System.out.println("fileWorkflowID : " + fileWorkflowID);
                // System.out.println("fileRuntimeID  : " + fileRuntimeID);
                if ((fileWorkflowID == null) || ("".equals(fileWorkflowID))) {
                    throw new Exception("FileWorkflowID not valid !");
                }
                if ((fileRuntimeID == null) || ("".equals(fileRuntimeID))) {
                    throw new Exception("FileRuntimeID not valid !");
                }
                // add plus file size to workflow quota
                addPlussRtIDQuotaSizeBean(localMode, new QuotaBean(portalID, userID, fileWorkflowID, fileRuntimeID, new Long(plussQuotaSize)));
            } catch (Exception e) {
                // System.out.println("parseCopyHash exception :");
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Refreshes the data of the current workflow in the quota repository.
     * (in the first step it puts the quota beans to a vector when 
     * all of the beans are ready it sends the vector with the call of the
     * sendQuotaInformationsToStorage() method.)
     *
     * @param localMode -
     *            The meaning of localMode in case of true:
     *            the submitter and the storage are on the same machine.
     * @param bean Quota data descriptor
     * @throws Exception quota handling error
     */
    public synchronized void addPlussRtIDQuotaSizeBean(boolean localMode, QuotaBean bean) throws Exception {
        // String portalID, String userID, String workflowID, String runtimeID, Long plussQuotaSize
        if (bean.getPlussQuotaSize().longValue() != 0) {
            // add plus file size to workflow quota
            if (localMode) {
                // in local mode
                quotaBeans.addElement(bean);
            } else {
                // in remote mode
                // this case the call 
                // comes from the ReceiverServiceImpl
                QuotaService.getInstance().addPlussRtIDQuotaSize(bean);
            }
        }
    }
    
    /**
     * Sends the quota information, gathered during 
     * the local mode, to the storage .
     * (to get them to the quota repository of the storage).
     *
     * @param storageURL  - storage URL
     */
    public void sendQuotaInformationsToStorage(String storageURL) {
        // System.out.println("sendQuotaInformationsToStorage()...");
        try {
            if (quotaBeans == null) {
                quotaBeans = new Vector();
            }
            if (!quotaBeans.isEmpty()) {
                Vector sendVector = new Vector(quotaBeans);
                quotaBeans.clear();
                quotaBeans = new Vector();
                // System.out.println("after quotaBeans size: " + quotaBeans.size());
                // System.out.println("after sendVector size: " + sendVector.size());
                // for (int vPos = 0; vPos < sendVector.size(); vPos++) {
                // QuotaBean bean = (QuotaBean) sendVector.get(vPos);
                // System.out.println("bean (" + vPos + ") : plussQuotaSize : " + bean.getPlussQuotaSize());
                // }
                Hashtable hash = new Hashtable();
                String storageID = storageURL.replaceFirst("/receiver", "");
                // System.out.println("storageID: " + storageID);
                // storageID must be here (there is no need to the "/receiver" at the end)
                hash.put("url", storageID);
                ServiceType serviceType = InformationBase.getI().getService("storage", "submitter", hash, new Vector());
                if (serviceType != null) {
                    SubmitterStorageClient client = (SubmitterStorageClient) Class.forName(serviceType.getClientObject()).newInstance();
                    client.setServiceURL(serviceType.getServiceUrl());
                    client.setServiceID(serviceType.getServiceID());
                    // storageURL must be here (there is "/receiver" at the end)
                    client.setQuotaInformation(storageURL, sendVector);
                } else {
                    throw new Exception("no information system entry : storage from submitter !");
                }
            }
        } catch(Exception e) {
            // System.out.println("error : " + e.getLocalizedMessage());
            e.printStackTrace();
            // throw e;
        }
    }
    
}

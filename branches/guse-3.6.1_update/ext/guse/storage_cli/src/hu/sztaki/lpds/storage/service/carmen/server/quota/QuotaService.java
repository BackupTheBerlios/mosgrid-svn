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
package hu.sztaki.lpds.storage.service.carmen.server.quota;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.portal.inf.StoragePortalClient;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.wfs.com.ComDataBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Registers the size of the workflows stored on the storage.
 *
 * @author lpds
 */
public class QuotaService {
    
    private static QuotaService instance = null;
    
    // repository root dir
    private String repositoryDir;
    
    // File separator (for example, "/")
    private String sep;
    
    /**
     * Quota entries: Hashtable quotaItems;
     *
     * The content of the quotaItems: every portal / 
     * one QuotaPortalUserBean belongs to one user:
     *
     * (key: "portalID + "#" + userID", value: QuotaPortalUserBean object).
     *
     * The content of the QuotaPortalUserBean:
     * one QuotaWorkflowBean belongs to every single workflow of the user.
     *
     */
    private static Hashtable quotaItems = null;
/**
 * Constructor
 */
    
    public QuotaService() {
        if (instance == null) {
            instance = this;
            quotaItems = new Hashtable();
            LoadProperty();
        }
        // first init
        try {
            // If the initQuotaService() is used, then
            // the initQuotaService() will be called two times for the first time!
            if (quotaItems == null) {
                initQuotaService();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
    
    /**
     * Returns a QuotaService instance.
     *
     * @return
     */
    public static QuotaService getInstance() {
        if (instance == null) {
            instance = new QuotaService();
        }
        return instance;
    }
    
    /**
     * Init, loads the initial data.
     */
    private void LoadProperty() {
        sep = FileUtils.getInstance().getSeparator();
        repositoryDir = FileUtils.getInstance().getRepositoryDir();
        FileUtils.getInstance().createRepositoryDirectory();
    }
    
    /**
     * Creates a registry about the size of every portal, 
     * every user and every workflow data. (Does not use threads!)
     * @throws Exception file handling error
     */
    public void initQuotaService() throws Exception {
        if ("true".equals(PropertyLoader.getInstance().getProperty("guse.storageclient.localmode.sendquota"))) {
            // get portal list
            ArrayList portalList = FileUtils.getInstance().getSubDirList(repositoryDir);
            // parse portalList
            if (portalList.size() > 0) {
                for (int portalPos = 0; portalPos < portalList.size(); portalPos++) {
                    String portalID = ((String) portalList.get(portalPos));
                    String portalBaseDir = repositoryDir + portalID;
                    // get user list
                    ArrayList userList = FileUtils.getInstance().getSubDirList(portalBaseDir);
                    // parse userList
                    if (userList.size() > 0) {
                        for (int userPos = 0; userPos < userList.size(); userPos++) {
                            String userID = ((String) userList.get(userPos));
                            String userBaseDir = portalBaseDir + sep + userID;
                            QuotaPortalUserBean portalUserBean = new QuotaPortalUserBean(portalID, userID);
                            // get workflow list
                            ArrayList workflowList = FileUtils.getInstance().getSubDirList(userBaseDir);
                            // parse workflowList
                            if (workflowList.size() > 0) {
                                for (int workflowPos = 0; workflowPos < workflowList.size(); workflowPos++) {
                                    String workflowID = ((String) workflowList.get(workflowPos));
                                    String workflowBaseDir = userBaseDir + sep + workflowID;
                                    portalUserBean.addWorkflow(getQuotaWorkflowBean(workflowID, workflowBaseDir));
                                }
                            }
                            String puID = new String(portalID + "#" + userID);
                            quotaItems.put(puID, portalUserBean);
                        }
                    }
                }
            }
        }
        // System.out.println("quotaItems: " + quotaItems);
    }
    
    /**
     * Creates a QuotaWorkflowBean about the workflow reserved 
     * by the directory received in the parameters.
     *
     * The QuotaWorkflowBean contains the size of the workflow,
     * in a detailed format.
     *
     * @param workflowID - workflow ID
     * @param baseDir - workflow base directory
     * @return workflowBean - QuotaWorkflowBean about the given directory
     */
    private QuotaWorkflowBean getQuotaWorkflowBean(String workflowID, String baseDir) throws Exception {
        if (!baseDir.endsWith(sep)) {
            baseDir += sep;
        }
        // System.out.println("getQuotaWorkflowBean() - baseDir: " + baseDir);
        QuotaWorkflowBean workflowBean = new QuotaWorkflowBean(workflowID);
        File dir = new File(baseDir);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                String[] baseEntryList = dir.list();
                if (baseEntryList.length > 0) {
                    for (int pos = 0; pos < baseEntryList.length; pos++) {
                        String baseEntryPath = baseDir + baseEntryList[pos];
                        File baseEntryFile = new File(baseEntryPath);
                        // job dirs
                        if (baseEntryFile.isDirectory()) {
                            String[] jobEntryList = baseEntryFile.list();
                            if (jobEntryList.length > 0) {
                                for (int jobpos = 0; jobpos < jobEntryList.length; jobpos++) {
                                    String jobEntry = jobEntryList[jobpos];
                                    String jobEntryPath = baseEntryPath + sep + jobEntry;
                                    File jobEntryFile = new File(jobEntryPath);
                                    // dirs
                                    if (jobEntryFile.isDirectory()) {
                                        // inputs dir
                                        if (jobEntry.equalsIgnoreCase("inputs")) {
                                            workflowBean.addOthersSize(FileUtils.getInstance().getDirectorySize(jobEntryPath));
                                        }
                                        // outputs / runtimeID dirs
                                        if (jobEntry.equalsIgnoreCase("outputs")) {
                                            String[] runtimeEntryList = jobEntryFile.list();
                                            if (runtimeEntryList.length > 0) {
                                                for (int runtimepos = 0; runtimepos < runtimeEntryList.length; runtimepos++) {
                                                    String runtimeID = runtimeEntryList[runtimepos];
                                                    String runtimeEntryPath = jobEntryPath + sep + runtimeID;
                                                    File runtimeEntryFile = new File(runtimeEntryPath);
                                                    if (runtimeEntryFile.isDirectory()) {
                                                        long addPlussSize = FileUtils.getInstance().getDirectorySize(runtimeEntryPath);
                                                        // put runtime size
                                                        workflowBean.addRuntimeSize(runtimeID, addPlussSize);
                                                    }
                                                    if (runtimeEntryFile.isFile()) {
                                                        // do nothing...
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // job executable
                                    if (jobEntryFile.isFile()) {
                                        workflowBean.addOthersSize(jobEntryFile.length());
                                    }
                                }
                            }
                        }
                        // workflow details information files
                        if (baseEntryFile.isFile()) {
                            workflowBean.addOthersSize(baseEntryFile.length());
                        }
                    }
                }
            } else {
                throw new Exception("baseDir is not directory ! (" + baseDir + ")");
            }
        } else {
            throw new Exception("baseDir is not exist ! (" + baseDir + ")");
        }
        return workflowBean;
    }
    
    /**
     * Returns the QuotaPortalUserBean belongs to 
     * the given portal user from the repository.
     *
     * @param portalID -
     *            portal ID, format: directory name made from the URL.
     *            (use example: portalID =
     *            FileUtils.getInstance().convertPortalIDtoDirName(portalID))
     * @param userID -
     *            user ID
     * @return QuotaPortalUserBean
     */
    public synchronized QuotaPortalUserBean get(String portalID, String userID) {
        String puID = new String(portalID + "#" + userID);
        // System.out.println("quota puID : " + puID);
        if (!quotaItems.containsKey(puID)) {
            quotaItems.put(puID, new QuotaPortalUserBean(portalID, userID));
        }
        return (QuotaPortalUserBean) quotaItems.get(puID);
    }
    
    /**
     * If the size of one of the workflow's runtime output changes or being created
     * (delete, receiver), then this method should be called with the required
     * parameters so the stored workflow quota will be actualized.
     *
     * @param portalID -
     *            portal ID, format: directory name from URL
     *            (use example: portalID =
     *            FileUtils.getInstance().convertPortalIDtoDirName(portalID))
     * @param userID -
     *            user ID
     * @param workflowID -
     *            workflow ID
     * @param runtimeID -
     *            runtime output ID
     * @param plussQuotaSize -
     *            size change in bytes (the negative value means decreasing)
     */
    public synchronized void addPlussRtIDQuotaSize(String portalID, String userID, String workflowID, String runtimeID, Long plussQuotaSize) {
        // System.out.println("plussQuotaSize: " + plussQuotaSize);
        long sendSizeToPortal = 0;
        sendSizeToPortal = get(portalID, userID).getWorkflow(workflowID).addRuntimeSize(runtimeID, plussQuotaSize.longValue());
        // send new quota data to portal
        // the sendSizeToPortal contains the new (increased, changed) size
        sendNewQuotaDataToPortal(portalID, userID, workflowID, runtimeID, sendSizeToPortal);
        // send all workflow size
        sendSizeToPortal = get(portalID, userID).getWorkflow(workflowID).getAllWorkflowSize();
        // send new quota data to portal
        // the sendSizeToPortal contains the new (increased, changed) size
        sendNewQuotaDataToPortal(portalID, userID, workflowID, QuotaKeys.allWorkflowKey, sendSizeToPortal);
    }
    
    /**
     * Refreshes the data of the current workflow in the quota registry.
     * @param bean Quota entry descriptor
     * @throws Exception quota handling error
     *
     */
    public synchronized void addPlussRtIDQuotaSize(QuotaBean bean) throws Exception {
        // String portalID, String userID, String workflowID, String runtimeID, Long plussQuotaSize
        this.addPlussRtIDQuotaSize(bean.getPortalID(), bean.getUserID(), bean.getWorkflowID(), bean.getRuntimeID(), bean.getPlussQuotaSize());
    }
    
    /**
     * If the size of one of the workflow's other property changes (inputs, job exec, ...) or being created
     * (upload), then this method should be called with the required
     * parameters so the stored workflow quota will be actualized.
     *
     * @param portalID -
     *            portal ID, format: directory name from URL
     *            (use example: portalID =
     *            FileUtils.getInstance().convertPortalIDtoDirName(portalID))
     * @param userID -
     *            user ID
     * @param workflowID -
     *            workflow ID
     * @param plussQuotaSize -
     *            size change in bytes (the negative value means decreasing)
     */
    public synchronized void addPlussOthersQuotaSize(String portalID, String userID, String workflowID, Long plussQuotaSize) {
        // System.out.println("plussQuotaSize: " + plussQuotaSize);
        get(portalID, userID).getWorkflow(workflowID).addOthersSize(plussQuotaSize.longValue());
        long sendSizeToPortal = get(portalID, userID).getWorkflow(workflowID).getAllWorkflowSize();
        // send new quota data to portal
        // the sendSizeToPortal contains the new (increased, changed) size
        sendNewQuotaDataToPortal(portalID, userID, workflowID, QuotaKeys.allWorkflowKey, sendSizeToPortal);
    }
    
    /**
     * Refreshes the data of the current workflow in the quota registry.
     * @param bean Quota entry descriptor
     * @throws Exception quota handling error
     */
    public synchronized void addPlussOthersQuotaSize(QuotaBean bean) throws Exception {
        // String portalID, String userID, String workflowID, Long plussQuotaSize
        this.addPlussOthersQuotaSize(bean.getPortalID(), bean.getUserID(), bean.getWorkflowID(), bean.getPlussQuotaSize());
    }
    
    /**
     * Deletes all the workflow's (given in the parameters) quota entries.
     *
     * @param portalID -
     *            portal ID, format: directory name from URL
     *            (use example: portalID =
     *            FileUtils.getInstance().convertPortalIDtoDirName(portalID))
     * @param userID -
     *            user ID
     * @param workflowID -
     *            workflow ID
     */
    public synchronized void deleteWorkflowAllQuotaItem(String portalID, String userID, String workflowID) {
        get(portalID, userID).deleteWorkflow(workflowID);
        long sendSizeToPortal = get(portalID, userID).getWorkflow(workflowID).getAllWorkflowSize();
        // send new quota data to portal
        // the sendSizeToPortal contains the new (increased, changed) size
        sendNewQuotaDataToPortal(portalID, userID, workflowID, QuotaKeys.allWorkflowKey, sendSizeToPortal);
    }
    
    /**
     * Deletes (sets to zero) all the workflow's (given in the parameters) output related quota entries.
     *
     * @param portalID -
     *            portal ID, format: directory name from URL
     *            (use example: portalID =
     *            FileUtils.getInstance().convertPortalIDtoDirName(portalID))
     * @param userID -
     *            user ID
     * @param workflowID -
     *            workflow ID
     */
    public synchronized void deleteWorkflowAllOutputsQuotaItem(String portalID, String userID, String workflowID) {
        get(portalID, userID).getWorkflow(workflowID).deleteAllOutputs();
        long sendSizeToPortal = get(portalID, userID).getWorkflow(workflowID).getAllWorkflowSize();
        // send new quota data to portal
        // the sendSizeToPortal contains the new (increased, changed) size
        sendNewQuotaDataToPortal(portalID, userID, workflowID, QuotaKeys.allWorkflowKey, sendSizeToPortal);
    }
    
    /**
     * Deletes the workflow's (given in the parameters) quota entries with the proper runtimeID.
     *
     * @param portalID -
     *            portal ID, format: directory name from URL
     *            (use example: portalID =
     *            FileUtils.getInstance().convertPortalIDtoDirName(portalID))
     * @param userID -
     *            user ID
     * @param workflowID -
     *            workflow ID
     * @param runtimeID -
     *            runtime output ID
     */
    public synchronized void deleteWorkflowRtIDQuotaItem(String portalID, String userID, String workflowID, String runtimeID) {
        get(portalID, userID).getWorkflow(workflowID).deleteRuntime(runtimeID);
        long sendSizeToPortal = get(portalID, userID).getWorkflow(workflowID).getAllWorkflowSize();
        // send new quota data to portal
        // the sendSizeToPortal contains the new (increased, changed) size
        sendNewQuotaDataToPortal(portalID, userID, workflowID, QuotaKeys.allWorkflowKey, sendSizeToPortal);
    }
    
    /**
     * Actualizes the workflow's (given in the parameters) quota entries with the proper runtimeID.
     *
     * @param portalID -
     *            portal ID, format: directory name from URL
     *            (use example: portalID =
     *            FileUtils.getInstance().convertPortalIDtoDirName(portalID))
     * @param userID -
     *            user ID
     * @param workflowID -
     *            workflow ID
     * @param runtimeID -
     *            runtime output ID
     * @param plussQuotaSize -
     *            quota change
     */
    public synchronized void deleteWorkflowRtIDLogQuotaItem(String portalID, String userID, String workflowID, String runtimeID, long plussQuotaSize) {
        addPlussRtIDQuotaSize(portalID, userID, workflowID, runtimeID, new Long(plussQuotaSize));
    }
    
    /**
     * Sends the user's quota change to the portal.
     *
     * @param userID -
     *            user ID
     * @param workflowID -
     *            workflow ID
     * @param runtimeID -
     *            runtime ID
     * @param sendSizeToPortal -
     *            contains the new (increased, changed) size
     */
    private synchronized void sendNewQuotaDataToPortal(String portalID, String userID, String workflowID, String runtimeID, long sendSizeToPortal) {
        if("true".equals(PropertyLoader.getInstance().getProperty("guse.storageclient.localmode.sendquota"))) {
            ComDataBean comDataBean = new ComDataBean();
            comDataBean.setPortalID(portalID);
            comDataBean.setUserID(userID);
            comDataBean.setWorkflowID(workflowID);
            comDataBean.setWorkflowRuntimeID(runtimeID);
            comDataBean.setSize(new Long(sendSizeToPortal));
            // send ComDataBean to portal
            // use info system
            Hashtable hash = new Hashtable();
            hash.put("url", FileUtils.getInstance().convertDirNametoPortalID(portalID));
            // hash.put("url", "http://localhost:8080/portal30");
            ServiceType serviceType = InformationBase.getI().getService("portal", "storage", hash, new Vector());
            if(serviceType != null) {
                try {
                    StoragePortalClient client = (StoragePortalClient) Class.forName(serviceType.getClientObject()).newInstance();
                    client.setServiceURL(serviceType.getServiceUrl());
                    client.setServiceID(serviceType.getServiceID());
                    client.newOccupied(comDataBean);
                } catch(Exception e) {
                    // e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Returns the quotaItems registry of the user given in the parameters.
     * The return value contains the names of all the user's workflows
     * (key) and the value of the quotaHash (value).
     *
     * @param portalID -
     *            portal ID
     * @param userID -
     *            user ID
     * @return - Hashtable (key: workflow name, value: quotaHash)
     *
     * The content of the quotaHash:
     *
     * (key: "rtID", value: The size in bytes of all outputs which belong to 
     * the workflow runtimeID given in the key)
     *
     * (key: allOutputsKey, value: total size of the outputs of every job)
     *
     * (key: allOthersKey, value: workflow details data, size of every job input and
     * executable files in bytes).
     *
     * (key: allWorkflowKey, value: workflow total size in bytes)
     *
     * note: allOutputsKey = size of every output with runtimeID
     *
     * note: allOthersKey = workflow details, total size of every job input and
     * executable files in bytes
     *
     * note: allWorkflowKey = allOutputsKey + allOthersKey total size.
     * @throws Exception Quota handling error
     */
    public synchronized Hashtable getUserQuotaItems(String portalID, String userID) throws Exception {
        portalID = FileUtils.getInstance().convertPortalIDtoDirName(portalID);
        return get(portalID, userID).getUserQuotaHash();
    }
    
}

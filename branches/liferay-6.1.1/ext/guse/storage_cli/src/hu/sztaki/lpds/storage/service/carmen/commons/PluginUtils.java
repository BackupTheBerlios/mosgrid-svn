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
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.portal.com.StoragePortalWorkflowNamesBean;
import hu.sztaki.lpds.portal.inf.StoragePortalClient;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaService;
import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;
import hu.sztaki.lpds.wfs.inf.StorageWfsClient;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * It is used by the storage plugins and the ZipUploadUtils.
 *
 * @author lpds
 */
public class PluginUtils {
    
    private static PluginUtils instance = null;
/**
 * Constructor
 */
    public PluginUtils() {
        if (instance == null) {
            instance = this;
            FileUtils.getInstance().createRepositoryDirectory();
        }
    }
    
    /**
     * Returns the PluginUtils instance.
     *
     * @return
     */
    public static PluginUtils getInstance() {
        if (instance == null) {
            instance = new PluginUtils();
        }
        return instance;
    }
    
    /**
     * Returns the data of the workflow given in the parameters to the workflow repository.
     *
     * @param  bean - workflow details
     *    storageURL - storage repository ID
     *    wfsID - workflow repository ID
     *    portalURL - portal ID
     *    userID - user ID
     *    workflowXML - workflow details xml
     *    newMainGrafName - the new name of the uploaded main graph workflow
     *    newMainAbstName - the new name of the uploaded main abstract workflows
     *    newMainRealName - the new name of the uploaded main real, concrete workflow
     *    uploadID - it will be put to the end of the runtimeID
     *      - downloadType - download type
     *      - exportType - download type (embedded details)
     * @return true if the saving of the workflow was successful
     * @throws Exception
     */
    public boolean setWorkflowXMLDetailsToWFS(StorageWorkflowNamesBean bean) throws Exception {
        // System.out.println("setWorkflowXMLDetailsToWFS...");
        // send workflow details to wfs
        // use info system
        Hashtable hash = new Hashtable();
        hash.put("url", bean.getWfsID());
        ServiceType serviceType = InformationBase.getI().getService("wfs", "storage", hash, new Vector());
        if (serviceType != null) {
            try {
                StorageWfsClient client = (StorageWfsClient) Class.forName(serviceType.getClientObject()).newInstance();
                client.setServiceURL(serviceType.getServiceUrl());
                client.setServiceID(serviceType.getServiceID());
                // System.out.println("workflowXML : " + bean.getWorkflowXML());
                String retString = client.setWorkflowXML(bean);
                // System.out.println("retString : " + retString);
                if ("".equals(retString.trim())) {
                    return true;
                } else {
                    throw new Exception(retString);
                }
            } catch(Exception e) {
                // e.printStackTrace();
                throw e;
            }
        } else {
            throw new Exception("no wfs service : wfs from storage !");
        }
    }
    
    /**
     * Gets the information from the portal if there is a name 
     * duplication with the already existing and new workflow names.
     *
     * (it is also aware of the names of the main graph,
     * abst and real workflow
     * names after renaming)
     *
     * @param  bean - basic data (userID, new and main workflow names...)
     * @param  newgrafList - names (key) and text (value) of the newly uploaded graph workflows
     * @param  newabstList - names (key) and text (value) of the newly uploaded abst workflows
     * @param  newrealList - names (key) and text (value) of the newly uploaded real workflows
     * @return checked workflow name
     * @throws Exception checking error
     */
    public String checkWorkflowNames(StorageWorkflowNamesBean bean, Hashtable newgrafList, Hashtable newabstList, Hashtable newrealList) throws Exception {
        // System.out.println("checkWorkflowNames start...");

        try {
            StoragePortalWorkflowNamesBean cbean = new StoragePortalWorkflowNamesBean();
            cbean.setUserID(bean.getUserID());
            cbean.setGrafList(newgrafList);
            cbean.setAbstList(newabstList);
            cbean.setRealList(newrealList);
            Hashtable hash = new Hashtable();
            hash.put("url", bean.getPortalURL());
            System.out.println("portalUrl : " + bean.getPortalURL());
            ServiceType serviceType = InformationBase.getI().getService("portal", "storage", hash, new Vector());
            if (serviceType != null) {
                StoragePortalClient client = (StoragePortalClient) Class.forName(serviceType.getClientObject()).newInstance();
                client.setServiceURL(serviceType.getServiceUrl());
                client.setServiceID(serviceType.getServiceID());
                return (client.checkWorkflowNames(cbean));
            } else {
                throw new Exception("no information system entry : portal from storage !");
            }
        } catch(Exception e) {
            // System.out.println("error : " + e.getLocalizedMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Sends the newly uploaded workflow name and text pairs to the portal.
     *
     * (it is also aware of the names of the main graph,
     * abst and real workflow
     * names after renaming)
     *
     * @param  bean - basic data (userID, new and main workflow names...)
     * @param  grafList - names (key) and text (value) of the newly uploaded graph workflows
     * @param  abstList - names (key) and text (value) of the newly uploaded abst workflows
     * @param  realList - names (key) and text (value) of the newly uploaded real workflows
     * @return true if the sending of the workflow was successful
     * @throws Exception communication error
     */
    public boolean sendWorkflowNamesToPortal(StorageWorkflowNamesBean bean, Hashtable grafList, Hashtable abstList, Hashtable realList) throws Exception {
        // bean = String portalURL, String storageURL, String wfsID, String userID
        StoragePortalWorkflowNamesBean combean = new StoragePortalWorkflowNamesBean();
        combean.setPortalID(bean.getPortalURL());
        combean.setStorageID(bean.getStorageURL());
        combean.setWfsID(bean.getWfsID());
        combean.setUserID(bean.getUserID());
        combean.setGrafList(grafList);
        combean.setAbstList(abstList);
        combean.setRealList(realList);
        combean.setWorkflowType(PropertyLoader.getInstance().getProperty("guse.storage.system.defaultworkflowtype"));
        
        // send workflow names to portal
        // use info system
        Hashtable hash = new Hashtable();
        hash.put("url", bean.getPortalURL());
        ServiceType serviceType = InformationBase.getI().getService("portal", "storage", hash, new Vector());
        if (serviceType != null) {
            try {
                StoragePortalClient client = (StoragePortalClient) Class.forName(serviceType.getClientObject()).newInstance();
                client.setServiceURL(serviceType.getServiceUrl());
                client.setServiceID(serviceType.getServiceID());
                return (client.newWorkflowNames(combean)).booleanValue();
            } catch(Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            throw new Exception("no wfs service : wfs from storage !");
        }
    }
    
    /**
     * Refreshes the data of the current workflow in the quota repository.
     *
     * @param portalID portal ID
     * @param userID user ID
     * @param workflowID workflow ID
     * @param plussQuotaSizeHash  quota changes descriptor
     */
    public void refreshWorkflowQuota(String portalID, String userID, String workflowID, Hashtable plussQuotaSizeHash) {
        try {
            if ((plussQuotaSizeHash!=null)&&(!plussQuotaSizeHash.isEmpty())) {
                String othersKey = new String("others");
                if (plussQuotaSizeHash.containsKey(othersKey)) {
                    Long plussQuotaSize = (Long) plussQuotaSizeHash.get(othersKey);
                    // System.out.println("plussQuotaSize others : " + plussQuotaSize);
                    if (plussQuotaSize.longValue()!=0) {
                        QuotaService.getInstance().addPlussOthersQuotaSize(portalID, userID, workflowID, plussQuotaSize);
                    }
                }
                // System.out.println("plussQuotaSizeHash: " + plussQuotaSizeHash);
                Enumeration enumKeys = plussQuotaSizeHash.keys();
                while (enumKeys.hasMoreElements()) {
                    String runtimeID = (String) enumKeys.nextElement();
                    if (!runtimeID.equals(othersKey)) {
                        // System.out.println("runtimeID : " + runtimeID);
                        Long plussQuotaSize = (Long) plussQuotaSizeHash.get(runtimeID);
                        // System.out.println("plussQuotaSize rtid : " + plussQuotaSize);
                        if (plussQuotaSize.longValue()!=0) {
                            QuotaService.getInstance().addPlussRtIDQuotaSize(portalID, userID, workflowID, runtimeID, plussQuotaSize);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Converts the workflow names to formats that can be stored in the file system
     * @param wfName workflow name
     * @return file system compatible workflow name
     */
    public String conformWorkflowNames(String wfName) {
        String retStr = wfName.trim();
        retStr = retStr.replace(" ", "_");
        retStr = retStr.replace("\"", "_");
        retStr = retStr.replace("\'", "_");
        return retStr;
    }
    
}

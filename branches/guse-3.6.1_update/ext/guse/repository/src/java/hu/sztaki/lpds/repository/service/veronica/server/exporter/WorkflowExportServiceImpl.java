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

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.repository.service.veronica.commons.RepositoryFileUtils;
import hu.sztaki.lpds.repository.service.veronica.server.quota.QuotaService;
import hu.sztaki.lpds.wfs.inf.RepositoryWfsClient;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author lpds
 */
public class WorkflowExportServiceImpl {
    
    private String sep;
    
    public WorkflowExportServiceImpl() {
        sep = RepositoryFileUtils.getInstance().getSeparator();
    }
    
    /**
     * Egy workflow-t exportal a repository teruletere.
     *
     * @param bean A workflow exportalast leiro parameterek
     * @return hibajelzes
     */
    public String exportWorkflow(RepositoryWorkflowBean bean) throws Exception {
        // System.out.println("WorkflowExportServiceImpl begin...");
        String retStr = new String("");
        // System.out.println("portalID       : " + bean.getPortalID());
        // System.out.println("storageID      : " + bean.getStorageID());
        // System.out.println("wfsID          : " + bean.getWfsID());
        // System.out.println("userID         : " + bean.getUserID());
        // System.out.println("workflowID     : " + bean.getWorkflowID());
        // System.out.println("downloadType   : " + bean.getDownloadType());
        // System.out.println("instanceType   : " + bean.getInstanceType());
        // System.out.println("outputLogType  : " + bean.getOutputLogType());
        // System.out.println("exportType     : " + bean.getExportType());
        // System.out.println("exportText     : " + bean.getExportText());
        try {
            // is free quota
            if (!QuotaService.getInstance().isFreeQuota()) {
                throw new Exception("Repository is full, quota on maximum, ran out");
            }
            // set my repositoryID
            bean.setRepositoryID(RepositoryFileUtils.getInstance().getRepositoryUrl());
            // System.out.println("repositoryID   : " + bean.getRepositoryID());
            // set unique exportID
            bean.setExportID(getUniqueExportID());
            // System.out.println("exportID       : " + bean.getExportID());
            // repository base directory
            // System.out.println("repositoryDir  : " + RepositoryFileUtils.getInstance().getRepositoryDir());
            // set zip repository path
            bean.setZipRepositoryPath(getNewZipRepositoryPath(bean));
            // System.out.println("repositoryPath : " + bean.getZipRepositoryPath());
            // set zip file path
            bean.setZipFileFullPath(RepositoryFileUtils.getInstance().getRepositoryDir() + bean.getZipRepositoryPath());
            // System.out.println("zipFileFullPath: " + bean.getZipFileFullPath());
            // get zip file from storage
            // and save repository dir
            RepositoryZipFileGetter zipFileGetter = new RepositoryZipFileGetter();
            long addQuotaSize = zipFileGetter.getZipFiles(bean);
            // add repository item to db (wfs)...
            sendRepositoryItemToWfs(bean);
            // refresh quota... (add bytes)
            QuotaService.getInstance().addZipFileSize(addQuotaSize);
            // System.out.println("WorkflowExportServiceImpl end...");
        } catch (Exception e) {
            e.printStackTrace();
            // delete zip file
            File zipFile = new File(bean.getZipFileFullPath());
            if (zipFile.exists()) {
                zipFile.delete();
            }
            throw e;
        }
        return retStr;
    }
    
    /**
     * Visszad egy egyedi exportID-t.
     *
     * @return string - exportID
     */
    private String getUniqueExportID() throws Exception {
        Long randomNum = new Long(Math.round(Math.random() * 900000) + 100000);
        return new String("expo-4815162342-" + randomNum);
    }
    
    /**
     * Visszad egy egyedi zip file path-ot,
     * a repositorin beluli eleresi uttal.
     *
     * @return string - zip file repository path.
     */
    private String getNewZipRepositoryPath(RepositoryWorkflowBean bean) throws Exception {
        String zipPath = new String("");
        if (bean.getExportType().equalsIgnoreCase("work")) {
            // exportType   : work
            // downloadType : graf, abst, real
            zipPath = "workflows" + sep + bean.getDownloadType() + sep;
        }
        if (bean.getExportType().equalsIgnoreCase("proj")) {
            // exportType   : proj
            // downloadType : all
            zipPath = "projects" + sep;
        }
        if (bean.getExportType().equalsIgnoreCase("appl")) {
            // exportType   : appl
            // downloadType : all
            zipPath = "applications" + sep;
        }
        return new String(zipPath + bean.getWorkflowID() + "_" + bean.getExportID() + ".zip");
    }
    
    /**
     * Elkuldi a wfs nek a most exportalt workflow adatait,
     * Workflow neve, zip neve, export text, stb...
     */
    private void sendRepositoryItemToWfs(RepositoryWorkflowBean bean) throws Exception {
        Hashtable hsh = new Hashtable();
        hsh.put("url", bean.getWfsID());
        ServiceType st = InformationBase.getI().getService("wfs","repository", hsh, new Vector());
        RepositoryWfsClient wfsClient = (RepositoryWfsClient) Class.forName(st.getClientObject()).newInstance();
        wfsClient.setServiceURL(st.getServiceUrl());
        wfsClient.setServiceID(st.getServiceID());
        String clientRetStr = wfsClient.setRepositoryItem(bean);
        if (clientRetStr == null) {
            throw new Exception("clientRetStr is null !!!");
        }
        if (!"".equals(clientRetStr)) {
            throw new Exception(clientRetStr);
        }
    }
    
}

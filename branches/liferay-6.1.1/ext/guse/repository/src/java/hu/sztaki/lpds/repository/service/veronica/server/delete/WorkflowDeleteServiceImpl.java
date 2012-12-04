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
package hu.sztaki.lpds.repository.service.veronica.server.delete;

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
public class WorkflowDeleteServiceImpl {
    
    private String sep;
    
    public WorkflowDeleteServiceImpl() {
        sep = RepositoryFileUtils.getInstance().getSeparator();
    }
    
    /**
     * Egy workflow-t torol ki a repository teruleterol.
     *
     * @param bean A workflow torlest leiro parameterek
     * @return hibajelzes
     */
    public String deleteWorkflow(RepositoryWorkflowBean bean) throws Exception {
        // System.out.println("WorkflowDeleteServiceImpl begin...");
        String retStr = new String("");
        // System.out.println("portalID       : " + bean.getPortalID());
        // System.out.println("storageID      : " + bean.getStorageID());
        // System.out.println("wfsID          : " + bean.getWfsID());
        // System.out.println("userID         : " + bean.getUserID());
        // System.out.println("workflowID     : " + bean.getWorkflowID());
        // System.out.println("id             : " + bean.getId());        
        // System.out.println("downloadType   : " + bean.getDownloadType());
        // System.out.println("instanceType   : " + bean.getInstanceType());
        // System.out.println("outputLogType  : " + bean.getOutputLogType());
        // System.out.println("exportType     : " + bean.getExportType());
        // System.out.println("exportText     : " + bean.getExportText());
        try {
            String fullPath = new String(RepositoryFileUtils.getInstance().getRepositoryDir() + bean.getZipRepositoryPath());
            File zipFile = new File(fullPath);
            if (zipFile.exists()) {
                long addQuotaSize = 0;
                addQuotaSize -= zipFile.length();
                zipFile.delete();
                // delete repository item from wfs (db)...
                deleteRepositoryItemFromWfs(bean);
                // refresh quota... (add bytes, pl: -x byte)
                QuotaService.getInstance().addZipFileSize(addQuotaSize);
            }
            // System.out.println("WorkflowDeleteServiceImpl end...");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return retStr;
    }
    
    /**
     * Elkuldi a wfs nek a torolni kivant workflow adatait,
     * Workflow id, neve, zip neve, export text, stb...
     * (kitorli a workflow-t a repository tablabol)
     *
     * @param bean A workflow torlest leiro parameterek
     */
    private void deleteRepositoryItemFromWfs(RepositoryWorkflowBean bean) throws Exception {
        Hashtable hsh = new Hashtable();
        hsh.put("url", bean.getWfsID());
        ServiceType st = InformationBase.getI().getService("wfs","repository", hsh, new Vector());
        RepositoryWfsClient wfsClient = (RepositoryWfsClient) Class.forName(st.getClientObject()).newInstance();
        wfsClient.setServiceURL(st.getServiceUrl());
        wfsClient.setServiceID(st.getServiceID());
        String clientRetStr = wfsClient.deleteRepositoryItem(bean);
        if (clientRetStr == null) {
            throw new Exception("clientRetStr is null !!!");
        }
        if (!"".equals(clientRetStr)) {
            throw new Exception(clientRetStr);
        }
    }
    
}

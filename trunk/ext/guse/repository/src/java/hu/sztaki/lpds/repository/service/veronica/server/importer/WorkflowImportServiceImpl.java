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
package hu.sztaki.lpds.repository.service.veronica.server.importer;

import hu.sztaki.lpds.repository.service.veronica.commons.RepositoryFileUtils;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;

/**
 * @author lpds
 */
public class WorkflowImportServiceImpl {
    
    public WorkflowImportServiceImpl() {
    }
    
    /**
     * Egy workflow-t importal a repository teruleterol.
     *
     * @param bean A workflow importalast leiro parameterek
     * @return hibajelzes
     */
    public String importWorkflow(RepositoryWorkflowBean bean) throws Exception {
         System.out.println("WorkflowImportServiceImpl begin...");
        String retStr = new String("");
         System.out.println("portalID       : " + bean.getPortalID());
         System.out.println("storageID      : " + bean.getStorageID());
         System.out.println("wfsID          : " + bean.getWfsID());
         System.out.println("userID         : " + bean.getUserID());
         System.out.println("workflowID     : " + bean.getWorkflowID());
         System.out.println("workflowType   : " + bean.getWorkflowType());
         System.out.println("id             : " + bean.getId());
         System.out.println("downloadType   : " + bean.getDownloadType());
         System.out.println("instanceType   : " + bean.getInstanceType());
         System.out.println("outputLogType  : " + bean.getOutputLogType());
         System.out.println("exportType     : " + bean.getExportType());
         System.out.println("exportText     : " + bean.getExportText());
         System.out.println("newGrafName    : " + bean.getNewGrafName());
         System.out.println("newAbstName    : " + bean.getNewAbstName());
         System.out.println("newRealName    : " + bean.getNewRealName());
        try {
            // set my repositoryID
            bean.setRepositoryID(RepositoryFileUtils.getInstance().getRepositoryUrl());
            // System.out.println("repositoryID   : " + bean.getRepositoryID());
            //
            if (bean.getNewGrafName() == null) {
                bean.setNewGrafName(new String(""));
            }
            if (bean.getNewAbstName() == null) {
                bean.setNewAbstName(new String(""));
            }
            if (bean.getNewRealName() == null) {
                bean.setNewRealName(new String(""));
            }
            if (!"".equals(bean.getNewAbstName())) {
                if (bean.getNewAbstName().equalsIgnoreCase(bean.getNewRealName())) {
                    throw new Exception("Template workflow name equal concrete workflow name");
                }
            }
            bean.setZipFileFullPath(RepositoryFileUtils.getInstance().getRepositoryDir() + bean.getZipRepositoryPath());
            System.out.println("relativepath:"+bean.getZipRepositoryPath());
            System.out.println("absolutepath:"+bean.getZipFileFullPath());

            // send zip file to storage file receiver servlet
            RepositoryZipFileSender zipFileSender = new RepositoryZipFileSender();
            zipFileSender.sendZipFile(bean);
            // System.out.println("WorkflowImportServiceImpl end...");
        } catch (Exception e) {
            System.out.println("retError: " + e.getLocalizedMessage());
             e.printStackTrace();
            throw e;
        }
        return retStr;
    }
    
}

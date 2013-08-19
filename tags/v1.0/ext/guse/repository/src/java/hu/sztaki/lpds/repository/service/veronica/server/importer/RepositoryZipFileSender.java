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

import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.storage.service.carmen.client.ZipFileSender;

/**
 * Egy zip file-t (workflow-t) kuld el, tolt fel a storage-ra.
 *
 * @author lpds
 */
public class RepositoryZipFileSender {
    
    public RepositoryZipFileSender() {
    }
    
    /**
     * A beallitott parameterek alltal meghatarozott
     * zip file-t (workflow-t) kuldi el a storage-nak.
     *
     * @param RepositoryWorkflowBean bean - workflow parameterek
     */
    public void sendZipFile(RepositoryWorkflowBean bean) throws Exception {
        // System.out.println("RepositoryZipFileSender init...");
        ZipFileSender zipFileSender = new ZipFileSender(bean.getStorageID());
        zipFileSender.setParameters(bean.getZipFileFullPath(), bean.getPortalID(), bean.getWfsID(), bean.getUserID(), bean.getNewGrafName(), bean.getNewAbstName(), bean.getNewRealName());
        System.out.println("**************************");
        System.out.println(bean.getZipFileFullPath());
        System.out.println(bean.getPortalID());
        System.out.println(bean.getWfsID());
        System.out.println(bean.getUserID());
        System.out.println(bean.getNewGrafName());
        System.out.println(bean.getNewAbstName());
        System.out.println(bean.getNewRealName());
        System.out.println("**************************");

        boolean localMode = false;
        // hostok
        // String repositoryHost[] = bean.getRepositoryID().split("/");
        // String storageHost[] = bean.getStorageID().split("/");
        // localMode = repositoryHost[2].equals(storageHost[2]);
        // System.out.println("localMode: " + localMode);
        zipFileSender.sendZipFile(localMode);
    }
    
}

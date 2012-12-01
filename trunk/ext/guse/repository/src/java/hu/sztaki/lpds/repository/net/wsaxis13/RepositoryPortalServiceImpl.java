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
package hu.sztaki.lpds.repository.net.wsaxis13;

import hu.sztaki.lpds.repository.inf.RepositoryPortalService;
import hu.sztaki.lpds.repository.service.veronica.server.quota.QuotaService;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.repository.service.veronica.server.exporter.WorkflowExportServiceImpl;
import hu.sztaki.lpds.repository.service.veronica.server.delete.WorkflowDeleteServiceImpl;
import hu.sztaki.lpds.repository.service.veronica.server.importer.WorkflowImportServiceImpl;
import java.util.Hashtable;

/**
 * @author lpds
 */
public class RepositoryPortalServiceImpl implements RepositoryPortalService {
    
    private WorkflowExportServiceImpl workflowExportServiceImpl;
    private WorkflowDeleteServiceImpl workflowDeleteServiceImpl;
    private WorkflowImportServiceImpl workflowImportServiceImpl;
    
    public RepositoryPortalServiceImpl() {
        workflowExportServiceImpl = new WorkflowExportServiceImpl();
        workflowDeleteServiceImpl = new WorkflowDeleteServiceImpl();
        workflowImportServiceImpl = new WorkflowImportServiceImpl();
    }
    
    /**
     * Egy workflow-t exportal a repository teruletere.
     *
     * @param bean A workflow exportalast leiro parameterek
     * @return hibajelzes
     */
    public String exportWorkflow(RepositoryWorkflowBean bean) {
        String retStr = new String();
        try {
            retStr = workflowExportServiceImpl.exportWorkflow(bean);
            // System.out.println("retStr : " + retStr);
            return retStr;
        } catch (Exception e) {
            return new String(e.getLocalizedMessage());
        }
    }
    
    /**
     * Egy workflow-t torol ki a repository teruleterol.
     *
     * @param bean A workflow torlest leiro parameterek
     * @return hibajelzes
     */
    public String deleteWorkflow(RepositoryWorkflowBean bean) {
        String retStr = new String();
        try {
            retStr = workflowDeleteServiceImpl.deleteWorkflow(bean);
            // System.out.println("retStr : " + retStr);
            return retStr;
        } catch (Exception e) {
            return new String(e.getLocalizedMessage());
        }
    }
    
    /**
     * Egy workflow-t importal a repository teruleterol.
     *
     * @param bean A workflow importalast leiro parameterek
     * @return hibajelzes
     */
    public String importWorkflow(RepositoryWorkflowBean bean) {
        String retStr = new String();
        try {
            retStr = workflowImportServiceImpl.importWorkflow(bean);
            // System.out.println("retStr : " + retStr);
            return retStr;
        } catch (Exception e) {
            return new String(e.getLocalizedMessage());
        }
    }
    
    /**
     * A repository-tol kerdezi le az aktualis quota erteket.
     *
     * @param bean A workflow importalast leiro parameterek
     * @return hibajelzes
     */
    public Hashtable getQuota() {
        Hashtable retHash = new Hashtable();
        try {
            retHash = QuotaService.getInstance().getQuotaHash();
            // System.out.println("retHash : " + retHash);
            return retHash;
        } catch (Exception e) {
            return new Hashtable();
        }
    }
    
}

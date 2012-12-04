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
package hu.sztaki.lpds.repository.inf;

import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import java.util.Hashtable;

/**
 * @author lpds
 */
public interface RepositoryPortalService {
    
    /**
     * Egy workflow-t exportal a repository teruletere.
     *
     * @param bean A workflow exportalast leiro parameterek
     * @return hibajelzes
     */
    public String exportWorkflow(RepositoryWorkflowBean bean);

    
    /**
     * Egy workflow-t torol ki a repository teruleterol.
     *
     * @param bean A workflow torlest leiro parameterek
     * @return hibajelzes
     */
    public String deleteWorkflow(RepositoryWorkflowBean bean);
    
    /**
     * Egy workflow-t importal a repository teruleterol.
     *
     * @param bean A workflow importalast leiro parameterek
     * @return hibajelzes
     */
    public String importWorkflow(RepositoryWorkflowBean bean);
    
    /**
     * A repository-tol kerdezi le az aktualis quota erteket.
     *
     * @param bean A workflow importalast leiro parameterek
     * @return hibajelzes
     */
    public Hashtable getQuota();
    
}

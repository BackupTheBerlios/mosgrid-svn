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
package hu.sztaki.lpds.wfs.net.wsaxis13;

import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.wfs.inf.WfsRepositoryService;
import hu.sztaki.lpds.wfs.service.angie.repository.RepositoryServiceImpl;

/**
 * @author lpds
 */
public class WfsRepositoryServiceImpl implements WfsRepositoryService {
    
    public WfsRepositoryServiceImpl() {
    }
    
    /**
     * It registers a worflows to repository registry, it invoked at export action.
     *
     * @param bean Pramateres for workflow export description
     * @return Error message
     */
    public String setRepositoryItem(RepositoryWorkflowBean bean) {
        try {
            return new RepositoryServiceImpl().setRepositoryItem(bean);
        } catch (Exception e) {
            e.printStackTrace();
            return new String(e.getLocalizedMessage());
        }
    }
    
    /**
     * It deletes a workflow from repository.
     *
     * @param bean Parmeters for workflow deleting
     * @return Error message
     */
    public String deleteRepositoryItem(RepositoryWorkflowBean bean) {
        try {
            return new RepositoryServiceImpl().deleteRepositoryItem(bean);
        } catch (Exception e) {
            e.printStackTrace();
            return new String(e.getLocalizedMessage());
        }
    }
    
}

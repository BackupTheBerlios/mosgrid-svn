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
/**
 * Definition of the client used for the storage to access the portal
 */
package hu.sztaki.lpds.portal.inf;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.portal.com.StoragePortalWorkflowNamesBean;
import hu.sztaki.lpds.wfs.com.ComDataBean;

/**
 * @author krisztian
 */
public interface StoragePortalClient extends BaseCommunicationFace
{
    
    /**
     * Quota update after upload
     * @param value  parameters
     * @exception Exception communication or service call error
     */
    public void newOccupied(ComDataBean value)throws Exception;
    
    /**
     * Setting the names of the uploaded workflows to the portal registry
     * @param value workflow names
     * @exception Exception communication or service call error
     * @return is the operation successful or not
     */
    public Boolean newWorkflowNames(StoragePortalWorkflowNamesBean value) throws Exception;
    
    /**
     * Checking workflow names
     * @param value workflow names
     * @return error message if exits
     * @exception Exception communication or service call error
     */
    public String checkWorkflowNames(StoragePortalWorkflowNamesBean value) throws Exception;
    
}

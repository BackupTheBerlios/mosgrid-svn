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
package hu.sztaki.lpds.storage.inf;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.storage.com.*;
import java.util.Vector;

/**
 * @author lpds
 * @version 3.3
 *
 * Client interface that represents communication channels from the portal to the storage service
 */
public interface SubmitterStorageClient extends BaseCommunicationFace
{
    
    /**
     * Gets the content of a workflow-folder (compressed zip )
     *
     * @param value StorageSubmitterBean specific bean storing the necessary data
     * @return Boolean boolean - success or not
     */
    public Boolean getWorkflowFiles(StorageSubmitterBean value);
    
    /**
     * Sends the content of a workflow-folder to the storage (compressed zip)
     *
     * @param value StorageSubmitterBean bean that stores information about the compressed file that will be sent
     * @return Boolean boolean - success or not
     */
    public Boolean sendWorkflowFiles(StorageSubmitterBean value);
    
    /**
     * Updates the quota management by the quota beans
     * This method will be called only if the submitter and the storage service is installed to the same machine (localMode = true in each service)
     * In this case we have to send the quota informations to the storage using an other call (this)
     *
     * @param storageURL - storage URL
     * @param value Vector - contains the QuotaBeans
     * @return Boolean boolean - success or not
     */
    public Boolean setQuotaInformation(String storageURL, Vector value);
    
}

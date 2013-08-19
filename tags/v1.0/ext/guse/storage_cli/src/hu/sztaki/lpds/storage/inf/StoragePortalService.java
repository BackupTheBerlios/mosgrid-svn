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

import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.VolatileBean;
import hu.sztaki.lpds.storage.com.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Service Interface for the communication from Storage to Portal Service
 * @author lpds
 * @version 3.3
 */
public interface StoragePortalService {
    
    /**
     * Returns the counted size of the user's workflows
     *
     * @param pUser - specific bean, stores data about the portal service, the user, and about the workflow
     * @return A hashmap that stored the workflows of the users (as keys? ) and the sizes of them in bytes
     */
    public Hashtable getAllWorkflowSize(StoragePortalUserBean pUser);
    
    /**
     * Returns the size of a specified workflow's jobs
     *
     * @param pUser  - specific bean, stores data about the portal service, the user, and about the workflow
     * for further information, see {@link StoragePortalUserWorkflowBean }
     * @return a hashmap that contains jobs as keys and size of them as values
     */
    public Hashtable getWorkflowAllJobSize(StoragePortalUserWorkflowBean pUser);
    
    /**
     * Deletes a workflow.
     * @param value ComDataBean portal, user and workflow data
     * @return boolean - success or not
     */
    public Boolean deleteWorkflow(ComDataBean value);
    
    /**
     * Deletes a specified workflow instance
     * @param value ComDataBean portal, user and workflow data
     * @return boolean - success or not
     */
    public Boolean deleteWorkflowInstance(ComDataBean value);
    
    /**
     * Deletes all the outputs of a specified workflow
     * @param value  portal, user and workflow data
     * @return boolean - success or not
     */
    public Boolean deleteWorkflowOutputs(ComDataBean value);
    
    /**
     * Deletes output log files according to a specified workflow ID, job, and parametric ID
     * @param idBean  portal, user and workflow data (IDs)
     * @param value - a vector which contains ComDataBeans, (only jobID and jobPID)
     * @return boolean - success or not
     */
    public Boolean deleteWorkflowLogOutputs(ComDataBean idBean, Vector value);
    
    /**
     * Copies the necessary input and executable files to the folder of the new workflow (workflow creation)
     *
     * @param value - specific bean, stores data about the portal service, the user, and about the workflow
     * @return boolean - success or not
     */
    public boolean copyWorkflowFiles(StoragePortalCopyWorkflowBean value);
    
    /**
     * Uploads files matching to a specified configuration ID (confID), to the Storage
     * @param value uploadWorkflowBean file descriptor
     * @return boolean - success or not
     */
    public boolean uploadWorkflowFiles(UploadWorkflowBean value);
    
    /**
     * Deletes all the volatile outputs of a workflow
     * @param value - stores data about the workflow and the volatile outputs
     * @return boolean - success or not
     */
    public boolean deleteWorkflowVolatileOutputs(VolatileBean value);
    
/**
 * Gets the uploading file status in percent
 * @param sid - id of the set of files (couple of files can be uploaded in the same request )
 * @param filename - name of the file
 * @return - percent
 *
 */
    public int getUploadingFilePercent(String sid,String filename);
    
}

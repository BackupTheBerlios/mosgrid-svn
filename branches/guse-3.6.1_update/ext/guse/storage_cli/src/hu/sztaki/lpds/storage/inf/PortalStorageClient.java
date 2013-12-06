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
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.VolatileBean;
import hu.sztaki.lpds.storage.com.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author lpds
 * @version 3.3
 * 
 * Client interface class that represents communication channels from the portal to the storage service
 */
public interface PortalStorageClient  extends BaseCommunicationFace
{
    
    /**
     * Returns the size of a specified workflow's jobs
     * 
     * @param value  - specific bean, stores data about the portal service, the user, and about the workflow
     * for further information, see {@link StoragePortalUserWorkflowBean }
     * @return a hashmap that contains jobs as keys and size of them as values
     */
    public HashMap getWorkflowAllJobSize(StoragePortalUserWorkflowBean value);
    
    /**
     * Returns the counted size of the user's workflows
     * 
     * @param value - specific bean, stores data about the portal service, the user, and about the workflow
     * @return A hashmap that stored the workflows of the users (as keys? ) and the sizes of them in bytes
     */
    public HashMap getAllWorkflowSize(StoragePortalUserBean value);
    
    /**
     * Deletes a Workflow
     * @param value  - bean that stores information about the user, the portal (ID) and the workflow
     * @return boolean - success or not
     */
    public boolean deleteWorkflow(ComDataBean value);
    
    /**
     * Deletes a specified workflow instance
     * @param value - bean that stores information about the user, the portal (ID) and the workflow
     * @return boolean - success or not
     */
    public boolean deleteWorkflowInstance(ComDataBean value);
    
    /**
     * Deletes all the outputs of a specified workflow
     * @param value - bean that stores information about the user, the portal (ID) and the workflow
     * @return boolean - success or not
     */
    public boolean deleteWorkflowOutputs(ComDataBean value);
    
    /**
     * Deletes output log files according to a specified workflow ID, job, and parameteric ID
     * 
     * @param idBean - bean that stores information about the user, the portal (ID) and the workflow
     * @param value - egy vector mely ComDataBean-eket tartalmaz, (csak jobID es jobPID)... ?akos?
     * @return boolean success or not;
     */
    public boolean deleteWorkflowLogOutputs(ComDataBean idBean, Vector value);
    
    /**
     * Copies the necessarry input and executable files to the folder of the new workflow (workflow creation)
     *
     * @param value - specific bean, stores data about the portal service, the user, and about the workflow
     * @return boolean;
     */
    public boolean copyWorkflowFiles(StoragePortalCopyWorkflowBean value);
    
    /**
     * Uploads the files belong to the given 
     * confID to the user's storage space.
     * @param value
     * @return if the operation was successful
     */
    public boolean uploadWorkflowFiles(UploadWorkflowBean value);
    
    /**
     * Deletes all the volatile output files of a workflow.
     * @param value - stores data about the workflow and the volatile outputs
     * @return if the operation was successful
     */
    public boolean deleteWorkflowVolatileOutputs(VolatileBean value);

/**
 * File upload to the storage
 * @param pFile file to be uploaded
 * @param pUploadField file name on the upload form
 * @param pValue upload parameters
 * @throws java.lang.Exception communication error
 */
    public void fileUpload(File pFile,String pUploadField,Hashtable pValue) throws Exception;


/**
 * File upload from the storage
 * @param pValue request parameters
 * @return stream containing the files
 * @throws IOException communication error
 */
    public InputStream getStream(Hashtable<String,String> pValue) throws IOException;

/**
 * Gets the uploading file status in percent
 * @param sid - id of the set of files (couple of files can be uploaded in the same request )
 * @param filename - name of the file
 * @return - percent
 *
 */
    public int getUploadingFilePercent(String sid,String filename) ;
}

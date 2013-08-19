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
package hu.sztaki.lpds.storage.net.http;

import hu.sztaki.lpds.storage.inf.SubmitterStorageClient;
import hu.sztaki.lpds.storage.service.carmen.client.FileGetter;
import hu.sztaki.lpds.storage.service.carmen.client.FileSender;
import hu.sztaki.lpds.storage.com.*;
import hu.sztaki.lpds.storage.service.carmen.client.QuotaSender;
import java.util.Vector;

/**
 * @author lpds
 */
public class SubmitterStorageClientImpl implements SubmitterStorageClient {
    
    private String serviceURL="";
    
    private String serviceID="";
/**
 * Empty constructor
 */
    public SubmitterStorageClientImpl() {}

/**
 * Constructor
 * @param pServiceUrl service URL
 */
    public SubmitterStorageClientImpl(String pServiceUrl) {}
    
    /**
     * Sets the service address to be accessed
     * @param value the access point of the used service
     */
    public void setServiceURL(String value) {
        serviceURL=value;
    }
    
    /**
     * Sets the ID of the service to be accessed
     * @param value the ID of the used service
     */
    public void setServiceID(String value) {
        serviceID=value;
    }
    
    /**
     * Gets the zipped content of a directory from the storage
     *
     * @param value StorageSubmitterBean Descriptor data of the required files
     * @return Boolean  the result of the operation
     */
    public Boolean getWorkflowFiles(StorageSubmitterBean value) {
        try {
            FileGetter fileGetter = new FileGetter(value.getStorageURL());
            fileGetter.setParameters(value.getFilesDirPath(), value.getPortalID(), value.getUserID(), value.getFileHash());
            return Boolean.valueOf(fileGetter.getFiles(value.getLocalMode()));
        } catch(Exception e) {
            e.printStackTrace();
            return Boolean.valueOf(false);
        }
    }
    
    /**
     * Sends the zipped content of a directory to the storage
     *
     * @param value StorageSubmitterBean Descriptor data of the required files
     * @return Boolean the result of the operation
     */
    public Boolean sendWorkflowFiles(StorageSubmitterBean value) {
        try {
            FileSender fileSender = new FileSender(value.getStorageURL());
            fileSender.setParameters(value.getFilesDirPath(), value.getPortalID(), value.getUserID(), value.getWorkflowID(), value.getJobID(), value.getPidID(), value.getRuntimeID(), value.getFileHash());
            fileSender.sendFiles(value.getLocalMode());
            return Boolean.valueOf(true);
        } catch(Exception e) {
            e.printStackTrace();
            return Boolean.valueOf(false);
        }
    }
    
    /**
     * Refreshes the quota registry with the quota beans coming as parameters
     *
     * (This method will be called only if 
     * the storage and the submitter are on the same
     * physical machine, so the localMode = true.
     * In this case the quota information can be sent
     * to the storage with a separate call.)
     *
     * @param storageURL - storage URL
     * @param value Vector - contains QuotaBeans
     * @return Boolean the result of the operation
     */
    public Boolean setQuotaInformation(String storageURL, Vector value) {
        try {
            QuotaSender quotaSender = new QuotaSender(storageURL);
            quotaSender.setParameters(value);
            // QuotaSender can be only used in local mode !
            quotaSender.sendInformations(true);
            return Boolean.valueOf(true);
        } catch(Exception e) {
            e.printStackTrace();
            return Boolean.valueOf(false);
        }
    }
    
}

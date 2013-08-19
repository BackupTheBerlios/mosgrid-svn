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
package hu.sztaki.lpds.pgportal.service.workflow;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Using the WorkflowDownloadPortlet and the
 * WorkflowUploadPortlet. (helper class)
 *
 * @author lpds
 */
public class WorkflowUpDownloadUtils {
    
    private static WorkflowUpDownloadUtils instance = null;

    // File separator (for example, "/")
    private String sep;
/**
 * Constructor, creating the singleton instance
 */
    public WorkflowUpDownloadUtils() {
        if (instance == null) {
            instance = this;
            sep = FileUtils.getInstance().getSeparator();
        }
    }
    
    /**
     * Returns the WorkflowDownloadUtils instance.
     *
     * @return
     */
    public static WorkflowUpDownloadUtils getInstance() {
        if (instance == null) {
            instance = new WorkflowUpDownloadUtils();
        }
        return instance;
    }
    
    /**
     * Returns a storageID (service url)
     * @return storageID
     */
    public String getStorageID() {
        ServiceType st = InformationBase.getI().getService("storage", "portal", new Hashtable(), new Vector());
        return st.getServiceUrl();
    }
    
    /**
     * Returns a wfsID (service url)
     * @return WFS service ID(URL)
     */
    public String getWfsID() {
        ServiceType st = InformationBase.getI().getService("wfs", "portal", new Hashtable(), new Vector());
        return st.getServiceUrl();
    }
    
}

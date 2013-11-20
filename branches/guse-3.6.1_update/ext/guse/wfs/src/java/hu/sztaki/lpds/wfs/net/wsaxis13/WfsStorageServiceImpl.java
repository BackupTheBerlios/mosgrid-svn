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

import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;
import hu.sztaki.lpds.wfs.service.angie.workflowxml.WorkflowXMLBuilder;
import hu.sztaki.lpds.wfs.service.angie.workflowxml.WorkflowXMLParser;

/**
 * @author lpds
 */
public class WfsStorageServiceImpl {

    // static final Object iLockGetWorkflowXML = new Object();
    // static final Object iLockSetWorkflowXML = new Object();
    public WfsStorageServiceImpl() {
    }

    /**
     * It returns workflow data in XML.
     *
     * @param value workflow data
     * @return workflow XML string
     */
    public String getWorkflowXML(StorageWorkflowNamesBean value) {
        // synchronized (iLockGetWorkflowXML) {
        try {
            // System.out.println("WfsStorageServiceImpl.getWorkflowXML...");
            // System.out.println("portalID     : " + value.getPortalID());
            // System.out.println("userID       : " + value.getUserID());
            // System.out.println("workflowID   : " + value.getWorkflowID());
            // System.out.println("downloadType : " + value.getDownloadType());
            // System.out.println("instanceType : " + value.getInstanceType());
            // System.out.println("exportType   : " + value.getExportType());
            WorkflowXMLBuilder workflowXMLBuilder = new WorkflowXMLBuilder();
            return workflowXMLBuilder.buildXMLStr(value);
        } catch (Exception e) {
            return new String("error: " + e.getLocalizedMessage());
        }
        // }
    }

    /**
     * It sets a workflow data from XML.
     *
     * @param value user data
     * @return empty string when workflow interpretation and saving is successful. In other case: error message
     *        
     */
    public String setWorkflowXML(StorageWorkflowNamesBean value) {
        // synchronized (iLockSetWorkflowXML) {
        try {
            // System.out.println("portalID     : " + value.getPortalID());
            // System.out.println("userID       : " + value.getUserID());
            // System.out.println("workflowXML  : " + value.getWorkflowXML());
            // System.out.println("downloadType : " + value.getDownloadType());
            // System.out.println("exportType   : " + value.getExportType());
            WorkflowXMLParser workflowXMLParser = new WorkflowXMLParser();
            return workflowXMLParser.parseXMLStr(value);
        } catch (Exception e) {
            e.printStackTrace();
            return new String(e.getLocalizedMessage());
        }
        // }
    }
}

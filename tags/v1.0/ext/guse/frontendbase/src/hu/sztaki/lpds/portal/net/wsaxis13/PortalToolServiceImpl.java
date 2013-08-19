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

package hu.sztaki.lpds.portal.net.wsaxis13;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.com.WorkflowSubmitThread;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.UserData;
import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import hu.sztaki.lpds.portal.com.ToolComDataBean;
import hu.sztaki.lpds.storage.com.StoragePortalCopyWorkflowBean;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Handles the request from the 
 * tool on the portal side.
 *
 * @author lpds
 */
public class PortalToolServiceImpl {
/**
 * Constructor
 */
    public PortalToolServiceImpl() { }
    
    /**
     * Uploads a proxy to the portal.
     *
     * @param userID - user ID
     * @param gridID - grid ID
     * @param proxy - the content of the proxy file
     * @return user message
     */
    public String uploadUserProxyStr(String userID, String gridID, String proxy) {
        String msg = new String("");
        try {
            System.out.println("userID       : " + userID);
            System.out.println("grid         : " + gridID);
            System.out.println("proxy value  : " + proxy);
            if ((userID == null) || ("".equals(userID))) throw new Exception("Invalid user name !");
            if ((gridID == null) || ("".equals(gridID))) throw new Exception("Invalid grid type !");
            if ((proxy == null) || ("".equals(proxy))) throw new Exception("Invalid proxy !");
            String proxyFilePath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + userID + "/x509up." + gridID;
            // old proxy file delete
            File oldProxy = new File(proxyFilePath);
            if (oldProxy.exists()) {
                if (oldProxy.isFile()) {
                    Process p = Runtime.getRuntime().exec("/bin/chmod 700 " + oldProxy.getAbsolutePath());
                    p.waitFor();
                    oldProxy.delete();
                }
            }
            //
            FileUtils.getInstance().createFile(new File(proxyFilePath), proxy);
            //
            Process p = Runtime.getRuntime().exec("/bin/chmod 400 " + oldProxy.getAbsolutePath());
            p.waitFor();
            msg = "Proxy upload success...";
        } catch(Exception e) {
            e.printStackTrace();
            msg = e.getLocalizedMessage();
        }
        return msg;
    }
    
    /**
     * Uploads a proxy to the portal.
     *
     * @param value descriptor bean
     * @return user message
     */
    public String uploadUserProxy(ToolComDataBean value) {
        return uploadUserProxyStr(value.getUserID(), value.getGrid(), value.getProxyValue());
    }
    
    /**
     * Submits a concrete workflow.
     *
     * @param portalID - portal ID
     * @param userID - user ID
     * @param workflowID - workflow ID
     * @param submittext - comment of the submit
     * @return user message
     */
    public String submittWorkflowStr(String portalID, String userID, String workflowID, String submittext) {
        String msg = new String("");
        try {
            System.out.println("portalID     : " + portalID);
            System.out.println("userID       : " + userID);
            System.out.println("workflowID   : " + workflowID);
            System.out.println("workflowText : " + submittext);
            if ((portalID == null) || ("".equals(portalID))) throw new Exception("Invalid portalID !");
            if ((userID == null) || ("".equals(userID))) throw new Exception("Invalid user name !");
            if ((workflowID == null) || ("".equals(workflowID))) throw new Exception("Invalid workflowID !");
            if ((submittext == null) || ("".equals(submittext))) throw new Exception("Invalid submitt text !");
            new WorkflowSubmitThread(PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID), userID, submittext);
            msg = PortalMessageService.getI().getMessage("portal.RealWorkflowPortlet.doSubmit");
            msg = "Workflow submitted...";
        } catch(Exception e) {
            e.printStackTrace();
            msg = e.getLocalizedMessage();
        }
        return msg;
    }
    
    /**
     * Submits a concrete workflow.
     *
     * @param value descriptor bean
     * @return user message
     */
    public String submittWorkflow(ToolComDataBean value) {
        return submittWorkflowStr(value.getPortalID(), value.getUserID(), value.getWorkflowID(), value.getWorkflowText());
    }
    
}

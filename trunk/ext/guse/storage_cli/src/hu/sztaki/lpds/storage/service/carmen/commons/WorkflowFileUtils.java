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
package hu.sztaki.lpds.storage.service.carmen.commons;

import hu.sztaki.lpds.storage.com.StoragePortalCopyWorkflowBean;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaService;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * It does the handling and copying of the files of the workflow. (helper class)
 *
 * @author lpds
 */
public class WorkflowFileUtils {
    
    private static WorkflowFileUtils instance = null;
    
    // File separator (for example, "/")
    private String sep;
    
    // repository root dir
    private String repositoryDir;
/**
 * Constructor
 */
    public WorkflowFileUtils() {
        if (instance == null) {
            instance = this;
            sep = FileUtils.getInstance().getSeparator();
            repositoryDir = FileUtils.getInstance().getRepositoryDir();
        }
    }
    
    /**
     * Returns a WorkflowFileUtils instance.
     *
     * @return
     */
    public static WorkflowFileUtils getInstance() {
        if (instance == null) {
            instance = new WorkflowFileUtils();
        }
        return instance;
    }
    
    /**
     * Loops through the copyHash given in the parameters and makes all the 
     * necessary file copies. After the copying refreshes the quota of the target workflow.
     *
     * @param  value
     *        portalID -
     *            portal ID
     *        userID -
     *            user name
     *        sourceWorkflowID -
     *            source workflow ID, the source files are copied from here
     *        destinWorkflowID -
     *            target workflow ID, where the source files needed to be copied to
     *        copyHash -
     *            copy descriptor hashtable
     * @throws Exception
     * @return successful operation
     */
    public boolean copyWorkflowFilesParseCopyHash(StoragePortalCopyWorkflowBean value) throws Exception {
        String portalID = FileUtils.getInstance().convertPortalIDtoDirName(value.getPortalID());
        String userID = value.getUserID();
        String sourceWorkflowID = value.getSourceWorkflowID();
        String destinWorkflowID = value.getDestinWorkflowID();
        Hashtable copyHash = value.getCopyHash();
        String userBaseDir = repositoryDir + portalID + sep + userID;
        // System.out.println("userBaseDir: " + userBaseDir);
        long plussQuotaSize = 0;
        if ((portalID == null) || ("".equals(portalID))) {
            throw new Exception("PortalID not valid !");
        }
        if ((userID == null) || ("".equals(userID))) {
            throw new Exception("UserID not valid !");
        }
        if ((sourceWorkflowID == null) || ("".equals(sourceWorkflowID))) {
            throw new Exception("sourceWorkflowID not valid !");
        }
        if ((destinWorkflowID == null) || ("".equals(destinWorkflowID))) {
            throw new Exception("destinWorkflowID not valid !");
        }
        if (copyHash == null) {
            throw new Exception("CopyHash not valid !");
        }
        // System.out.println("portalID         : " + portalID);
        // System.out.println("userID           : " + userID);
        // System.out.println("sourceWorkflowID : " + value.getSourceWorkflowID());
        // System.out.println("destinWorkflowID : " + value.getDestinWorkflowID());
        // System.out.println("copyHash         : " + copyHash);
        // create desWorkflow base dir
        FileUtils.getInstance().createDirectory(userBaseDir + sep + destinWorkflowID);
        // parse copyHash
        if (!copyHash.isEmpty()) {
            if (copyHash.containsKey("all")) {
                String sourcePath = userBaseDir + sep + sourceWorkflowID;
                String destinPath = userBaseDir + sep + destinWorkflowID;
                // copy all files and sub dir all files...
                // false = the outputs rtID directories don't needed to be copied
                plussQuotaSize += FileUtils.getInstance().copyDirAllFilesToDirectoryRecursive(sourcePath, destinPath, false);
            } else {
                Enumeration enumKey = copyHash.keys();
                while (enumKey.hasMoreElements()) {
                    String sourcePath = (String) enumKey.nextElement();
                    String destinPath = (String) copyHash.get(sourcePath);
                    // copy files...
                    plussQuotaSize += FileUtils.getInstance().copyFileWithPaths(userBaseDir, sourcePath, destinPath);
                }
            }
            // System.out.println("copyWorkflowFiles_plussQuotaSize : " + plussQuotaSize);
            // add plus file size to workflow quota
            refreshWorkflowQuota(portalID, userID, destinWorkflowID, plussQuotaSize);
        }
        return true;
    }
    
    /**
     * Refreshes the data of the current workflow in the quota repository.
     *
     * @param portalID
     * @param userID
     * @param workflowID
     * @param plussQuotaSize
     */
    private void refreshWorkflowQuota(String portalID, String userID, String workflowID, long plussQuotaSize) {
        try {
            if (plussQuotaSize != 0) {
                QuotaService.getInstance().addPlussOthersQuotaSize(portalID, userID, workflowID, new Long(plussQuotaSize));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

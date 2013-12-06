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
package hu.sztaki.lpds.storage.service.carmen.server.delete;

import hu.sztaki.lpds.storage.com.UploadWorkflowBean;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaService;
import java.io.File;
import java.util.Enumeration;

/**
 * Deletes the workflow files
 *
 * Deletes the files of the
 * old configuration.
 *
 * @author lpds
 */
public class DeleteUtils {
    
    private static DeleteUtils instance = null;
    
    // File separator (for example, "/")
    private String sep;
    
    // storage repository root dir
    private String repositoryDir;
/**
 * Constructor, initializing
 */
    public DeleteUtils() {
        if (instance == null) {
            instance = this;
        }
        //
        repositoryDir = FileUtils.getInstance().getRepositoryDir();
        sep = FileUtils.getInstance().getSeparator();
    }
    
    /**
     * Returns a DeleteUtils instance
     *
     * @return
     */
    public static DeleteUtils getInstance() {
        if (instance == null) {
            instance = new DeleteUtils();
        }
        return instance;
    }
    
    /**
     * Deletes the files of the old configuration.
     *
     * Deletes all the files and directories which needed
     * to be deleted because of the change of the workflow configuration
     *
     * For example: If the type of an input port changes from local file 
     * (which is stored on the storage) to something else
     * (e.g.: value, remote, sql...) then the file must 
     * be deleted from the storage.
     * The same stands to the execute files too if
     * the job type changes.
     *
     * The user quota registry will 
     * be refreshed after the deletion.
     *
     * @param  value - deletedFiles list about the files to be deleted
     * @throws Exception file deletion error
     */
    public void deleteOldFiles(UploadWorkflowBean value) throws Exception {
        // System.out.println("DeleteUtils.deleteOldFiles()...");
        //
        if (!value.getDeletedFiles().isEmpty()) {
            String portalDirName = FileUtils.getInstance().convertPortalIDtoDirName(value.getPortalID());
            String workflowBaseDir = repositoryDir + portalDirName + sep;
            workflowBaseDir += value.getUserID() + sep + value.getWorkflowID() + sep;
            // System.out.println("workflowBaseDir : " + workflowBaseDir);
            //
            long sendQuotaFressBytes = 0;
            Enumeration enu = value.getDeletedFiles().keys();
            while (enu.hasMoreElements()) {
                String keyPath = (String) enu.nextElement();
                String fullPathEntry = workflowBaseDir + keyPath;
                // System.out.println("fullPathEntry : " + fullPathEntry);
                //
                File entry = new File(fullPathEntry);
                if (entry.exists()) {
                    if (entry.isFile()) {
                        sendQuotaFressBytes -= entry.length();
                        entry.delete();
                    }
                    if (entry.isDirectory()) {
                        sendQuotaFressBytes -= FileUtils.getInstance().deleteDirectory(fullPathEntry, false);
                    }
                } // exists
            }
            // System.out.println("sendQuotaFressBytes : " + sendQuotaFressBytes);
            if (sendQuotaFressBytes != 0) {
                //
                // because we deleted files, the
                // sendQuotaFressBytes value will be negative
                //
                // send quota fresh byte to quota service...
                QuotaService.getInstance().addPlussOthersQuotaSize(portalDirName, value.getUserID(), value.getWorkflowID(), sendQuotaFressBytes);
                //
            }
        }
    }
    
}

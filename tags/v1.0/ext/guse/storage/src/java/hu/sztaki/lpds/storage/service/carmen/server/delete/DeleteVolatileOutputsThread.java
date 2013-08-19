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

import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaService;
import hu.sztaki.lpds.wfs.com.VolatileEntryBean;
import hu.sztaki.lpds.wfs.com.VolatileBean;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import java.io.File;

/**
 * Deletes all the volatile output file of a workflow.
 *
 * @author lpds
 */
public class DeleteVolatileOutputsThread extends Thread {
    
    private VolatileBean volatileBean;
    
    private String workflowBaseDir;
/**
 * Constructor
 * @param volBean descriptor of the output to be deleted
 * @param baseDir file path
 */
    public DeleteVolatileOutputsThread(VolatileBean volBean, String baseDir) {
        volatileBean = volBean;
        workflowBaseDir = baseDir;
        start();
    }
/**
 * @see Thread#run()
 */
@Override
    public void run() {
        doDeleteVolatile();
    }
    
    /**
     * Deletes the volatile output files
     */
    private void doDeleteVolatile() {
        try {
            ComDataBean comBean = volatileBean.getComDataBean();
            String sep = FileUtils.getInstance().getSeparator();
            long plussQuotaSize = 0;
            //
            for (int ePos = 0; ePos < volatileBean.getVolatileVector().size(); ePos++) {
                VolatileEntryBean entryBean = (VolatileEntryBean) volatileBean.getVolatileVector().get(ePos);
                //
                String outputDirPath = workflowBaseDir + entryBean.getJobID() + sep + "outputs" + sep + comBean.getWorkflowRuntimeID() + sep;
                // System.out.println("outputDirPath: " + outputDirPath);
                //
                // parse pid dirs...
                File pidDir = new File(outputDirPath);
                String[] filesList = pidDir.list();
                if (filesList.length > 0) {
                    for (int pos = 0; pos < filesList.length; pos++) {
                        String entryName = filesList[pos];
                        // System.out.println("entryName: " + entryName);
                        String pidPath = outputDirPath + entryName + sep;
                        // System.out.println("pidPath: " + pidPath);
                        File entry = new File(pidPath);
                        if (entry.isDirectory()) {
                            // parse pid dir...
                            plussQuotaSize += parseOutputDirFiles(pidPath, entryBean);
                        }
                    }
                }
                //
            }
            //
            // System.out.println("plussQuotaSize: " + plussQuotaSize);
            // refresh workflow quota
            refreshWorkflowQuota(comBean, plussQuotaSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Checks and deletes the volatile output 
     * files from a pid directory of a job.
     *
     */    
    private long parseOutputDirFiles(String outputDirPath, VolatileEntryBean entryBean) {
        long plussQuotaSize = 0;
        //
        // parse output files...
        //
        File outputDir = new File(outputDirPath);
        String[] filesList = outputDir.list();
        if (filesList.length > 0) {
            for (int pos = 0; pos < filesList.length; pos++) {
                String fileName = filesList[pos];
                // System.out.println("fileName: " + fileName);
                String nopid = fileName.split("_")[0];
                // System.out.println("nopid: " + nopid);
                if ((nopid.equals(entryBean.getOutputName1())) || (nopid.equals(entryBean.getOutputName2()))) {
                    // delete actual volatile output file...
                    File actualFile = new File(outputDirPath + fileName);
                    plussQuotaSize -= actualFile.length();
                    // System.out.println("deleted filePath: " + actualFile.getAbsolutePath());
                    actualFile.delete();
                }
            }
        }
        return plussQuotaSize;
    }
    
    /**
     * Refreshes the data of the current workflow in the quota registry.
     *
     * @param comDataBean - portalID, userID, workflowID, runtimeID
     * @param plussQuotaSize - quota change (in case of deletion it is a negative value)
     */
    private void refreshWorkflowQuota(ComDataBean comDataBean, long plussQuotaSize) {
        try {
            if (plussQuotaSize != 0) {
                QuotaService.getInstance().addPlussRtIDQuotaSize(comDataBean.getPortalID(), comDataBean.getUserID(), comDataBean.getWorkflowID(), comDataBean.getWorkflowRuntimeID(), new Long(plussQuotaSize));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
    
}

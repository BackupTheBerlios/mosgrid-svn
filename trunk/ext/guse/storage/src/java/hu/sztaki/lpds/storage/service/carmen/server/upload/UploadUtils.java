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
package hu.sztaki.lpds.storage.service.carmen.server.upload;

import hu.sztaki.lpds.storage.com.UploadWorkflowBean;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.XMLFileUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.ZipUtils;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaService;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Moves the file stored in the temporary dir to the given directory
 *
 * @author lpds
 */
public class UploadUtils {
    
    private static UploadUtils instance = null;
    
    // File separator (for example, "/")
    private String sep;
    
    // storage repository root dir
    private String repositoryDir;
    
    // ret html bar width
    private final Integer barWidthPx = new Integer(400);
    
    private final String defaultParamZipName = new String("paramInputs.zip");
/**
 * Constructor, creating the singleton instance
 */
    public UploadUtils() {
        if (instance == null) {
            instance = this;
        }
        //
        sep = FileUtils.getInstance().getSeparator();
        repositoryDir = FileUtils.getInstance().getRepositoryDir();
    }
    
    /**
     * Returns the UploadUtils instance.
     *
     * @return
     */
    public static UploadUtils getInstance() {
        if (instance == null) {
            instance = new UploadUtils();
        }
        return instance;
    }
    
    /**
     * Checks the given confID directory
     * and uploads the found files.
     *
     * @param  value - confID configuration ID
     * @throws Exception process error
     */
    public void uploadTemporaryFiles(UploadWorkflowBean value) throws Exception {
        String confID = value.getConfID();
        // System.out.println("uploadTemporaryFiles(confID) : " + confID);
        String temporaryBase = this.repositoryDir + "temporary" + sep + confID + sep;
        // list temporary dir files
        File temporaryBaseDir = new File(temporaryBase);
        if (temporaryBaseDir.exists()) {
            // list directory
            String[] filesList = temporaryBaseDir.list();
            if (filesList.length > 0) {
                for (int pos = 0; pos < filesList.length; pos++) {
                    String fileName = filesList[pos];
                    // System.out.println("fileName : " + fileName);
                    if (fileName.endsWith(".file")) {
                        uploadTemporaryFile(temporaryBase + fileName);
                    }
                }
            }
            // delete dir
            FileUtils.getInstance().deleteDirectory(temporaryBase);
        } else {
            // throw new Exception("Not valid confID !");
        }
    }
    
    /**
     * Uploads a file to the storage.
     *
     * @param tempFilePath - file to be uploaded
     * @throws Exception
     */
    private void uploadTemporaryFile(String tempFilePath) throws Exception {
        File uploadedFile = null;
        File tempFile = null;
        File tempXMLFile = null;
        try {
            tempFile = new File(tempFilePath);
            String tempXMLFilePath = tempFilePath + ".xml";
            tempXMLFile = new File(tempXMLFilePath);
            // System.out.println("tempFilePath    : " + tempFilePath);
            // System.out.println("tempXMLFilePath : " + tempXMLFilePath);
            // load xml file to data hash
            Hashtable dataHash = XMLFileUtils.getInstance().loadDataHashFromXMLFile(tempXMLFilePath);
            // System.out.println("loaded dataHash : " + dataHash);
            if (isValidDataHash(dataHash)) {
                String fileRepositoryPath = (String) dataHash.get("fileRepositoryPath");
                String temporaryUploadedFilePath = (String) dataHash.get("temporaryUploadedFilePath");
                String fileName = (String) dataHash.get("fileName");
                String portalID = (String) dataHash.get("portalID");
                String userID = (String) dataHash.get("userID");
                String workflowID = (String) dataHash.get("workflowID");
                String jobID = (String) dataHash.get("jobID");
                String sfile = (String) dataHash.get("sfile");
                String SID = (String) dataHash.get("SID");
                String randomID = (String) dataHash.get("randomID");
                // if parametrikus a feltoltott file akkor paramZip = true
                boolean paramZip = isParametricInputFile(fileName, sfile);
                String checkZipErrStr = new String("");
                //
                long plussQuotaSize = 0;
                // delete all input files
                plussQuotaSize -= deleteAllInputFiles(portalID, userID, workflowID, jobID, sfile);
                // System.out.println("deleteAllInputFiles: plussQuotaSize: " + plussQuotaSize);
                uploadedFile = new File(fileRepositoryPath);
                if (uploadedFile.exists()) {
                    plussQuotaSize -= uploadedFile.length();
                    uploadedFile.delete();
                }
                // Apache file save, without any crypt
                // A temporary dir ben lementett filet mozgatja
                // at a megadott helyre "uploadedFile".
                tempFile.renameTo(uploadedFile);
                if (paramZip) {
                    // parametrikus input feltoltes
                    String portBaseDir = uploadedFile.getParent();
                    // System.out.println("portBaseDir : " + portBaseDir);
                    // Check paramInput zip file
                    checkZipErrStr = ZipUtils.getInstance().checkParamInputZipFile(uploadedFile, fileName, jobID, sfile);
                    // Check error string
                    if ("OK".equals(checkZipErrStr)) {
                        // unZip parametric inputs from paramZipFile and get pluss quota size
                        plussQuotaSize = ZipUtils.getInstance().unZipFileOnlyRootFiles(uploadedFile, portBaseDir);
                    }
                    // delete paramZipFile
                    if (uploadedFile.exists()) {
                        uploadedFile.delete();
                    }
                } else {
                    checkZipErrStr = new String("OK");
                    // nem parametrikus input feltoltes
                    // refresh workflow quota
                    plussQuotaSize += uploadedFile.length();
                }
                // Check error string
                if ("OK".equals(checkZipErrStr)) {
                    // Complett Download 100 perCent
                    UploadItemsList.getInstance().addFile(SID, fileName + "_" + randomID, new Integer(100), checkZipErrStr);
                    // System.out.println("plussQuotaSize: " + plussQuotaSize);
                    // add plus file size to workflow quota
                    refreshWorkflowQuota(portalID, userID, workflowID, plussQuotaSize);
                } else {
                    // Complett Download 100 perCent and error string not null
                    UploadItemsList.getInstance().addFile(SID, fileName + "_" + randomID, new Integer(100), checkZipErrStr);
                }
            } else {
                // not valid dataHash
                // throw new Exception("Not valid temporary xml file !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // delete temporary files
            if (uploadedFile != null) {
                if (uploadedFile.exists()) {
                    uploadedFile.delete();
                }
            }
        }
        // delete temporary files
        if (tempFile != null) {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
        if (tempXMLFile != null) {
            if (tempXMLFile.exists()) {
                tempXMLFile.delete();
            }
        }
    }
    
    /**
     * Checks the content of the data hash,
     * if it contains every needed data,
     * then it returns true, else false.
     *
     * @param dataHash - hash containing data
     * @return true - if the returned hash is real
     */
    private boolean isValidDataHash(Hashtable dataHash) {
        if (dataHash.containsKey("fileRepositoryPath")) {
            if (dataHash.containsKey("temporaryUploadedFilePath")) {
                if (dataHash.containsKey("fileName")) {
                    if (dataHash.containsKey("portalID")) {
                        if (dataHash.containsKey("userID")) {
                            if (dataHash.containsKey("workflowID")) {
                                if (dataHash.containsKey("jobID")) {
                                    if (dataHash.containsKey("sfile")) {
                                        if (dataHash.containsKey("SID")) {
                                            if (dataHash.containsKey("randomID")) {
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Returns the full path to the file in the repository.
     *
     * @param paramZip - true if the uploaded file contains
     *            parametric inputs in a zipped format.
     * @param fileName
     * @param portalID
     * @param userID
     * @param workflowID
     * @param jobID
     * @param sfile
     * @return
     */
    public String getRepositoryPath(boolean paramZip, String fileName, String portalID, String userID, String workflowID, String jobID, String sfile) {
        // System.out.println("fileName: " + fileName);
        String filePath = new String("");
        String fileType = new String("");
        String portName = new String("");
        if (sfile.contains("_")) {
            fileType = sfile.split("_")[0];
            portName = sfile.split("_")[1];
        } else {
            fileType = sfile;
        }
        String jobbase = this.repositoryDir + portalID + sep + userID + sep + workflowID + sep + jobID;
        // System.out.println("jobbase: " + jobbase);
        if (fileType.startsWith("input")) {
            if (paramZip) {
                // parametrikus input zip file feltoltese
                filePath = new String(jobbase + sep + "inputs" + sep + portName + sep + fileName);
            } else {
                // normal input file feltoltese (default name = "0")
                filePath = new String(jobbase + sep + "inputs" + sep + portName + sep + "0");
            }
        }
        if (fileType.equals("binary")) {
            // normal binary (job executable file) feltoltese
            filePath = new String(jobbase + sep + FileUtils.getInstance().getDefaultBinaryName());
        }
        // System.out.println("filePath: " + filePath);
        // Make job directory
        File basediri = new File(jobbase + sep + "inputs");
        basediri.mkdirs();
        File basediro = new File(jobbase + sep + "outputs");
        basediro.mkdirs();
        if (!portName.equals("")) {
            File portbasedir = new File(jobbase + sep + "inputs" + sep + portName);
            portbasedir.mkdirs();
        }
        return filePath;
    }
    
    /**
     * Deletes all the files in the given job/input directory.
     *
     * @param portalID
     * @param userID
     * @param workflowID
     * @param jobID
     * @param sfile
     * @return plussQuotaSize
     */
    private long deleteAllInputFiles(String portalID, String userID, String workflowID, String jobID, String sfile) {
        long plussQuotaSize = 0;
        String filePath = new String("");
        String fileType = new String("");
        String portName = new String("");
        if (sfile.contains("_")) {
            fileType = sfile.split("_")[0];
            portName = sfile.split("_")[1];
        } else {
            fileType = sfile;
        }
        String jobbase = this.repositoryDir + portalID + sep + userID + sep + workflowID + sep + jobID;
        // System.out.println("jobbase: " + jobbase);
        if (fileType.startsWith("input")) {
            String inputsDirPath = new String(jobbase + sep + "inputs" + sep + portName + sep);
            try {
                plussQuotaSize += FileUtils.getInstance().deleteDirectory(inputsDirPath, false);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        return plussQuotaSize;
    }
    
    /**
     * Refreshes the current workflow data in the quota registry.
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
            // e.printStackTrace();
        }
    }
    
    /**
     * Checks whether the uploaded input file is parametric.
     *
     * @param fileName
     * @param sfile
     * @return true - if the uploaded file is a zipped parametric file 
     */
    public boolean isParametricInputFile(String fileName, String sfile) {
        // if (fileName.equals(defaultParamZipName)) {
        if (fileName.endsWith(defaultParamZipName)) {
            if (!sfile.startsWith("binary")) {
                // System.out.println("parametrikus zip a feltoltott file.");
                return true;
            }
        }
        // System.out.println("nem parametrikus zip a feltoltott file.");
        return false;
    }
    
    /**
     * Generates the response to the client's get request based on the UploadItems registry.
     * The response contains the name of the job and the upload status of all the files 
     * belonging to the job (in percentage 0-100).
     *
     * (if there is an error message then it is included also)
     *
     * @param getSID
     *            unique workflow ID
     * @return String response
     */
    public String getGenerateRetHTMLString(String getSID) {
        String preString = new String("");
        String messString = new String("");
        StringBuffer sb = new StringBuffer("");
        String retString = new String("");
        // generate div-s
        Hashtable fileHash = UploadItemsList.getInstance().getFileHash(getSID);
        if ((fileHash != null) && (!fileHash.isEmpty())) {
            Enumeration enumeration = fileHash.keys();
            while (enumeration.hasMoreElements()) {
                String fileName = (String) enumeration.nextElement();
                if ((fileName != null) && (!fileName.equals(""))) {
                    UploadItemBean uploadItemBean = (UploadItemBean) fileHash.get(fileName);
                    Integer filePerCent = uploadItemBean.getPerCent();
                    String fileErrorStr = uploadItemBean.getErrorStr();
                    // Varunk addig amig nem toltodik fel az osszes file 100 percentre
                    // es varunk addig amig nem ertekelodik ki
                    // a param input zip ellenorzes es kicsomagolasa.
                    //
                    // Az fileErrorStr "" vagy "OK" vagy "Error..." lehet.
                    // Ha "" akkor meg nem futott le a check.
                    // if ((filePerCent.intValue() < 100)) {
                    //     preString = new String("0");
                    // }
                    sb.append(addProgressBarToRetString(fileName, filePerCent, fileErrorStr));
                }
            }
        } else {
            // messString = new String("fileHash == null");
        }
        // return new String("Upload SID not valid, not found ! (getSID = " +
        // getSID + ")");
        retString = preString + sb.toString() + "</ br>" + messString;
        return retString;
    }
    
    /**
     * Generates a html div (progress bar)   
     * based on the given parameters.
     *
     * @param fileName
     * @param filePerCent
     * @param fileErrorStr
     * @return
     */
    private String addProgressBarToRetString(String fileName, Integer filePerCent, String fileErrorStr) {
        Integer barPerCentPx = new Integer(0);
        if ((barWidthPx.intValue() > 0) && (filePerCent.intValue() > 0)) {
            barPerCentPx = new Integer((int) Math.round(filePerCent.intValue() * (barWidthPx.intValue() / 100)) - 1);
        }
        StringBuffer retBuff = new StringBuffer();
        retBuff.append("</ br> File name: " + fileName + "</ br> File upload: " + filePerCent + " %" + "\n");
        if (fileErrorStr.startsWith("Error")) {
            retBuff.append("</ br> Error message: " + fileErrorStr + "\n");
        }
        retBuff.append("<div id=\"progress\" style=\"width: " + barWidthPx + "px; border: 1px solid black\">" + "\n");
        if (fileErrorStr.startsWith("Error")) {
            retBuff.append("<div id=\"progressbar\" style=\"width: " + barPerCentPx + "px; background-color: red; border: 1px solid white\">" + "\n");
        } else {
            retBuff.append("<div id=\"progressbar\" style=\"width: " + barPerCentPx + "px; background-color: blue; border: 1px solid white\">" + "\n");
        }
        retBuff.append("&nbsp;</div></div>" + "\n");
        return retBuff.toString();
    }

    /**
     * Gets the uploading file status in percent
     * @param SID - id of the set of files (couple of files can be uploaded in the same request )
     * @param filename - name of the file
     * @return - percent
     */


    public int getUploadPercentforSingleFile(String SID,String filename){
        Hashtable fileHash = UploadItemsList.getInstance().getFileHash(SID);

        if (fileHash != null){
           UploadItemBean uploadItemBean = (UploadItemBean) fileHash.get(filename);
           if (uploadItemBean != null) return uploadItemBean.getPerCent();
            else return -1;

           }
        else return -1;
    }

}

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

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.storage.com.DownloadWorkflowBean;
import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.w3c.dom.Element;

/**
 * The ZipUploadServlet and the ZipFileSender
 * uses it when a file (downloaded earlier from a zip file)
 * needed to be uploaded to the storage.
 *
 * @author lpds
 */
public class ZipUploadUtils {

    private static ZipUploadUtils instance = null;
    private static String importIDType;

    // storage repository root dir, the saved files are stored here
    private String repositoryDir;

    // File separator (for example, "/")
    private String sep;
/**
 * Constructor
 */
    public ZipUploadUtils() {
        if (instance == null) {
            instance = this;
            sep = FileUtils.getInstance().getSeparator();
            repositoryDir = FileUtils.getInstance().getRepositoryDir();
            FileUtils.getInstance().createRepositoryDirectory();
            importIDType = PropertyLoader.getInstance().getProperty("guse.storage.importid.type");
        }
    }

    /**
     * returns a ZipUploadUtils instance.
     *
     * @return singleton instance
     */
    public static ZipUploadUtils getInstance() {
        if (instance == null) {
            instance = new ZipUploadUtils();
        }
        return instance;
    }

    /**
     * Gets a zip file (path) in the parameters which contains 
     * all the files and the descriptor xml of an earlier downloaded workflow.
     * The files will be saved to the storage repository
     * Sends the workflow graph, abstract and real workflow data 
     * (can be more than one) given in the descriptor xml to the workflow storage (WFS).
     * Then tells the portal about the newly uploaded workflows.
     *
     * (if it is necessary it can rename the main workflows)
     *
     * @param bean -
     *        portalURL - portal ID
     *        storageURL - storage ID
     *        wfsID - workflow storage ID
     *        userID -user ID
     *        zipFilePathStr - the zip file to be uploaded (full path)
     *        newMainGrafName - the new name of the uploaded new main graph workflow
     *        newMainAbstName - the new name of the uploaded new main abstract, template workflow
     *        newMainRealName - the new name of the uploaded new main real, concrete workflow
     * @throws Exception
     */
    public synchronized void uploadZipFileToStorage(StorageWorkflowNamesBean bean) throws Exception {
        // synchronized is recommended in this method,
        // even if it will make it slower
        File uploadedZipFile = new File(bean.getZipFilePathStr());
        // System.out.println("call in uploadZipFileToStorage method in ZipUploadUtils.java");
        // System.out.println("zipFileSize : " + uploadedZipFile.length());
        if (uploadedZipFile.exists()) {
            if (isGoodZipFile(bean.getZipFilePathStr())) {
                // uploadID will be tailed to the end of the runtimeID
                // (ensures that all the runtimeIDs stay unique)
                bean.setUploadID("upload" + Long.toHexString(Math.round(Math.random() * 3123123) + 100000).toUpperCase());
                // System.out.println("uploadID : " + bean.getUploadID());
                bean.setPortalID(FileUtils.getInstance().convertPortalIDtoDirName(bean.getPortalURL()));
                String userBaseDir = repositoryDir + bean.getPortalID() + sep + bean.getUserID();
                // addedWfList contains all the directory names which 
                // is unpacked during this upload (real and abst dir names).
                // They are administered because in case of an error 
                // the half-done data needed to be deleted.
                HashSet addedWfList = new HashSet();
                try {
                    // get workflow details XML file, string from upload zip file
                    bean.setWorkflowXML(ZipUtils.getInstance().getStringFromZipFile(uploadedZipFile, DownloadWorkflowBean.workflowXMLDetailsInformationFileName));
                    if ((bean.getWorkflowXML() == null) || ("".equals(bean.getWorkflowXML().trim()))) {
                        throw new Exception("Not valid workflowXML !!! workflowXML = (" + bean.getWorkflowXML() + ")");
                    }
                    // System.out.println("storage upload bean.getWorkflowXML() : " + bean.getWorkflowXML());
                    // get eworkflowlist from details string
                    Element eworkflowlist = XMLUtils.getInstance().getElementWorkflowListFromXML(bean.getWorkflowXML());
                    bean.setDownloadType(eworkflowlist.getAttribute("download"));
                    bean.setExportType(eworkflowlist.getAttribute("export"));
                    // System.out.println("downloadType : " + bean.getDownloadType());
                    // System.out.println("exportType   : " + bean.getExportType());
                    // generate importID
                    // System.out.println("isAppl() : " + bean.isAppl());
                    // System.out.println("isProj() : " + bean.isProj());
                    //
                    bean.setImportID("");
                    //
                    //if (bean.isAppl() || bean.isProj()) {//  disabled
                        if ("date".equals(importIDType)) {
                            // bean.setImportID("_" + String.valueOf(System.currentTimeMillis()));
                            bean.setImportID("_" + getNowDateTimeStampWithSeconds());
                        }
                    //}
                    // System.out.println("importID : " + bean.getImportID());
                    // conform newWorkflowNames
                    bean.setNewMainGrafName(PluginUtils.getInstance().conformWorkflowNames(bean.getNewMainGrafName()));
                    if (!"".equals(bean.getNewMainGrafName())) {
                        bean.setNewMainGrafName(bean.getNewMainGrafName() + bean.getImportID());
                    }
                    bean.setNewMainAbstName(PluginUtils.getInstance().conformWorkflowNames(bean.getNewMainAbstName()));
                    if (!"".equals(bean.getNewMainAbstName())) {
                        bean.setNewMainAbstName(bean.getNewMainAbstName() + bean.getImportID());
                    }
                    bean.setNewMainRealName(PluginUtils.getInstance().conformWorkflowNames(bean.getNewMainRealName()));
                    if (!"".equals(bean.getNewMainRealName())) {
                        bean.setNewMainRealName(bean.getNewMainRealName() + bean.getImportID());
                    }
                    // get main property from eworkflowlist
                    bean.setMainGrafName(eworkflowlist.getAttribute("maingraf"));
                    if (!"".equals(bean.getMainGrafName())) {
                        bean.setMainGrafName(bean.getMainGrafName() + bean.getImportID());
                    }
                    bean.setMainAbstName(eworkflowlist.getAttribute("mainabst"));
                    if (!"".equals(bean.getMainAbstName())) {
                        bean.setMainAbstName(bean.getMainAbstName() + bean.getImportID());
                    }
                    bean.setMainRealName(eworkflowlist.getAttribute("mainreal"));
                    if (!"".equals(bean.getMainRealName())) {
                        bean.setMainRealName(bean.getMainRealName() + bean.getImportID());
                    }
                    // System.out.println("maingraf : " + bean.getMainGrafName());
                    // System.out.println("mainabst : " + bean.getMainAbstName());
                    // System.out.println("mainreal : " + bean.getMainRealName());
                    // if the rename is valid
                    if ((!"".equals(bean.getNewMainAbstName())) && (!"".equals(bean.getNewMainRealName()))) {
                        if (bean.getNewMainAbstName().equalsIgnoreCase(bean.getNewMainRealName())) {
                            throw new Exception("Template workflow name equal concrete workflow name");
                        }
                    }
                    // get workflow names and texts from eworkflowlist
                    Hashtable grafList = XMLUtils.getInstance().getWorkflowNameAndTextFromElement(eworkflowlist, "graf", bean);
                    Hashtable abstList = XMLUtils.getInstance().getWorkflowNameAndTextFromElement(eworkflowlist, "abst", bean);
                    Hashtable realList = XMLUtils.getInstance().getWorkflowNameAndTextFromElement(eworkflowlist, "real", bean);
                    // System.out.println("before grafList : " + grafList);
                    // System.out.println("before abstList : " + abstList);
                    // System.out.println("before realList : " + realList);
                    // System.out.println("workflowXML : " + bean.getWorkflowXML());
                    // Asks the portal if there is any name duplications 
                    // between the old and the newly uploaded workflows.
                    // Stores the duplicated workflow names list in the retStr
                    // if it is empty than there will be no name duplications.
                    //
                    // create renames list...
                    // in the lists starting with 'new' (newgrafList, ...)
                    // the main workflows are renamed
                    Hashtable newgrafList = new Hashtable(grafList);
                    Hashtable newabstList = new Hashtable(abstList);
                    Hashtable newrealList = new Hashtable(realList);
                    if (!"".equals(bean.getNewMainGrafName())) {
                        if (!"".equals(bean.getMainGrafName())) {
                            // put new name and old text...
                            newgrafList.put(bean.getNewMainGrafName(), grafList.get(bean.getMainGrafName()));
                            // remove old name (key)
                            newgrafList.remove(bean.getMainGrafName());
                        }
                    }
                    if (!"".equals(bean.getNewMainAbstName())) {
                        if (!"".equals(bean.getMainAbstName())) {
                            // put new name and old text...
                            newabstList.put(bean.getNewMainAbstName(), abstList.get(bean.getMainAbstName()));
                            // remove old name (key)
                            newabstList.remove(bean.getMainAbstName());
                        }
                    }
                    if (!"".equals(bean.getNewMainRealName())) {
                        if (!"".equals(bean.getMainRealName())) {
                            // put new name and old text...
                            newrealList.put(bean.getNewMainRealName(), realList.get(bean.getMainRealName()));
                            // remove old name (key)
                            newrealList.remove(bean.getMainRealName());
                        }
                    }
                    //
                    // The renamed workflow name lists will be sent to the portal
                    // and it will check them if there is any in there which already exists.
                    String retStr = PluginUtils.getInstance().checkWorkflowNames(bean, newgrafList, newabstList, newrealList);
                    // System.out.println("after grafList : " + grafList);
                    // System.out.println("after abstList : " + abstList);
                    // System.out.println("after realList : " + realList);
                    // System.out.println("retStr : " + retStr);
                    if (!"".equals(retStr)) {
                        throw new Exception(retStr);
                    }
                    //
                    // send workflows xml details to wfs
                    // bean = storageURL, wfsID, portalURL, userID, workflowXML, uploadID, main wf names, new main wf names, downloadType, exportType...
                    boolean setResult = PluginUtils.getInstance().setWorkflowXMLDetailsToWFS(bean);
                    if (!setResult) {
                        throw new Exception("Not success store workflows details xml in wfs !");
                    }
                    //
                    // The real and abst workflow files will be 
                    // copied from the zip file to the user storage area.
                    // Please note, that the names of the main real and main abst
                    // can be changed if the new main real and new main abst names
                    // are not empty strings "".
                    //
                    addedWfList.clear();
                    // parse real workflow list (and rename main real workflow dir)
                    addWorkflowFilesToStorageFromZip(uploadedZipFile, userBaseDir, bean, realList, bean.getMainRealName(), bean.getNewMainRealName(), addedWfList);
                    // System.out.println("addedWfList after real : " + addedWfList);
                    // parse abst workflow list (and rename main abst workflow dir)
                    addWorkflowFilesToStorageFromZip(uploadedZipFile, userBaseDir, bean, abstList, bean.getMainAbstName(), bean.getNewMainAbstName(), addedWfList);
                    // System.out.println("addedWfList after abst : " + addedWfList);
                    //
                    // send workflow names to portal
                    // bean = portalURL, storageURL, wfsID, userID
                    // the renamed name lists are sent to the portal and it refreshes the portal cache with them!
                    PluginUtils.getInstance().sendWorkflowNamesToPortal(bean, newgrafList, newabstList, newrealList);
                    //
                    // delete temporary zip file
                    // System.out.println("uploadedZipFile.delete(): " + uploadedZipFile.getAbsolutePath());
                    uploadedZipFile.delete();
                    //
                } catch (Exception e) {
                    e.printStackTrace();
                    // delete temporary zip file
                    if (uploadedZipFile != null) {
                        if (uploadedZipFile.exists()) {
                            // delete temporary zip file
                            uploadedZipFile.delete();
                        }
                    }
                    // If any exception delete unzipped (real and abstract) workflows dir
                    // if they have been created now.
                    try {
                        // System.out.println("addedWfList : " + addedWfList);
                        if (!addedWfList.isEmpty()) {
                            Iterator iter = addedWfList.iterator();
                            while (iter.hasNext()) {
                                String delWfName = (String) iter.next();
                                FileUtils.getInstance().deleteDirectory(userBaseDir + sep + delWfName);
                                // System.out.println("delete uploaded workflow (error) name : (" + delWfName + ")");
                            }
                        }
                    } catch (Exception e2) {
                        // e2.printStackTrace();
                    }
                    throw e;
                }
            } else {
                // delete (in case of a not working zip file)
                if (uploadedZipFile.exists()) {
                    uploadedZipFile.delete();
                }
                throw new Exception("Zip file is corrupted !");
            }
        } else {
            throw new Exception("Zip file not exist ! zipFilePathStr = (" + bean.getZipFilePathStr() + ")");
        }
    }

    /**
     * Time stamp on the server,
     * return value in string.
     * (E.g.: 2009-03-31-115033)
     * (add plus seconds)
     * @return server time stamp in string
     */
    public String getNowDateTimeStampWithSeconds() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
        return formatter.format(Calendar.getInstance().getTime());
    }

    /**
     * Copies the files of a real or abstract workflow,
     * unpacks it to the user storage space,
     * renames the main workflows if necessary.
     * @param addedWfList registered workflows
     * @param bean Workflow descriptor in the storage
     * @param mainname main workflow name
     * @param newmainname main workflow new name
     * @param uploadedZipFile uploaded ZIP file
     * @param userBaseDir user directory
     * @param wfList workflow list
     * @throws Exception file handling error
     */
    public void addWorkflowFilesToStorageFromZip(File uploadedZipFile, String userBaseDir, StorageWorkflowNamesBean bean, Hashtable wfList, String mainname, String newmainname, HashSet addedWfList) throws Exception {
        // parse real or abst workflow list
        // System.out.println("wfList : " + wfList);
        if (!wfList.isEmpty()) {
            Enumeration enu = wfList.keys();
            while (enu.hasMoreElements()) {
                // real or abst workflow name (zipwfName) in the zip file (and in the xml)
                String zipwfName = (String) enu.nextElement();
                // System.out.println("zipwfName : " + zipwfName);
                String wfName = zipwfName;
                // System.out.println("before wfName : " + wfName);
                // if the main workflow is the current and it is
                // needed to be renamed than it will be renamed
                if (mainname.equals(zipwfName)) {
                    if (!"".equals(newmainname)) {
                        if (!"".equals(mainname)) {
                            // only the main will be renamed
                            wfName = newmainname;
                        }
                    }
                }
                // System.out.println("after wfName : " + wfName);
                // System.out.println("add workflow (" + zipwfName + ") to user (" + bean.getUserID() + ") storage space (in " + wfName + " name) (original name in zip : " + zipwfName + ") from zip...");
                String workflowBaseDir = userBaseDir + sep + wfName;
                File workflowDir = new File(workflowBaseDir);
                if (!workflowDir.exists()) {
                    FileUtils.getInstance().createDirectory(workflowBaseDir);
                    // Restores the original dir name of the workflow from the original zip file
                    // System.out.println("before zipwfName : " + zipwfName);
                    if (!"".equals(bean.getImportID())) {
                        if (zipwfName.endsWith(bean.getImportID())) {
                            zipwfName = zipwfName.substring(0, (zipwfName.length() - bean.getImportID().length()));
                        }
                    }
                    // System.out.println("after zipwfName : " + zipwfName);
                    Hashtable wfPlussQuotaSizeHash = ZipUtils.getInstance().unZipWorkflowZipFileNotStoreRoot(uploadedZipFile, userBaseDir, zipwfName, wfName, bean.getUploadID());
                    // System.out.println("wfPlussQuotaSizeHash : " + wfPlussQuotaSizeHash);
                    Thread.sleep(1);
                    // add plus file size to workflow quota
                    PluginUtils.getInstance().refreshWorkflowQuota(bean.getPortalID(), bean.getUserID(), wfName, wfPlussQuotaSizeHash);
                    addedWfList.add(wfName);
                    Thread.sleep(1);
                }
            }
        }
    }

    private boolean isGoodZipFile(String zipFileName) {
        boolean ret = true;
        try {
            //
            // Checks the length of the zip file: if it is correct
            // (so there is no missing data from the end of the zip file - successful download)
            // than the "#guse#ok" such as "ZipUtils.getInstance().getZipEndString()",
            // if there is an error with the download than the "#guse#error" such as
            // "ZipUtils.getInstance().getZipEndErrorString()"
            // will be on the end of the zip file.
            //
            String endString = ZipUtils.getInstance().getZipEndErrorString();
            String zipEndLine = "";
            try {
                RandomAccessFile raf = new RandomAccessFile(zipFileName, "r");
                long zipSize = raf.length();
                raf.seek(zipSize - endString.length());
                zipEndLine = raf.readLine();
                // System.out.println("zipEndLine : " + zipEndLine);
                raf.close();
            } catch (Exception e) {
                e.printStackTrace();
                ret = false;
            }
            if (endString.equals(zipEndLine)) {
                // #guse#error
                ret = false;
            } else {
                // #guse#ok
                ret = true;
            }
            // System.out.println("zipEndLine ret1 : " + ret);
            if (ret) {
                //
                // Checking whether the zip file is corrupted
                //
                int zBUFFER = 8 * 1024;
                FileInputStream fis = new FileInputStream(zipFileName);
                CheckedInputStream checksum = new CheckedInputStream(fis, new Adler32());
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(checksum));
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    // System.out.println("Extracting: " + entry);
                    int count;
                    byte data[] = new byte[zBUFFER];
                    while ((count = zis.read(data, 0, zBUFFER)) != -1) {
                    }
                }
                zis.close();
                ret = true;
            }
            // System.out.println("zipEndLine ret2 : " + ret);
            //
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

}

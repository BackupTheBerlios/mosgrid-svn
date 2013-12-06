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
package hu.sztaki.lpds.storage.service.carmen.server.download;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.storage.com.DownloadWorkflowBean;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.XMLUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.ZipUtils;
import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;
import hu.sztaki.lpds.wfs.inf.StorageWfsClient;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.ZipOutputStream;
import org.w3c.dom.Element;

/**
 * Used by the Download Workflow Servlet (helper class).
 *
 * Returns the stored data of a workflow (project) in zipped format.
 *
 * @author lpds
 */
public class DownloadWorkflowUtils {
    
    private static final long serialVersionUID = -2022668244739518900L;
    
    // repository root dir
    private String repositoryDir;
    
    // File separator (for example, "/")
    private String sep;
/**
 * Constructor, loading properties
 */
    public DownloadWorkflowUtils() {
        LoadProperty();
    }
    
    /**
     * Init, loading starting data.
     */
    private void LoadProperty() {
        sep = FileUtils.getInstance().getSeparator();
        repositoryDir = FileUtils.getInstance().getRepositoryDir();
        FileUtils.getInstance().createRepositoryDirectory();
    }
    
    /**
     * Setting up and writing the workflow zip file to the stream.
     *
     * @param bean - download descriptor parameters
     * @param zipout - writes the content of the zip file (the workflow)
     *        to this zip stream 
     * @throws Exception file handling error
     */
    public void downloadWorkflowZipFileToZipStream(DownloadWorkflowBean bean, ZipOutputStream zipout) throws Exception
    {
        // System.out.println("");
//        System.out.println("downloadWorkflowZipFileToZipStream begin...");
        FileUtils.getInstance().createRepositoryDirectory();
        // workflow adatainak szolgaltatasa
        // convert url to dirName
        bean.setPortalID(FileUtils.getInstance().convertPortalIDtoDirName(bean.getPortalURL()));
        // get user dir
        String userBaseDir = new String(repositoryDir + bean.getPortalID() + sep + bean.getUserID() + sep);
        // System.out.println("userBaseDir: " + userBaseDir);
        // System.out.println("downloadType: " + bean.getDownloadType());
        Hashtable grafList = new Hashtable();
        Hashtable abstList = new Hashtable();
        Hashtable realList = new Hashtable();
        // workflow details xml string
        String workflowXML = new String("");
        if ((bean.isAll()) || (bean.isGraf()) || (bean.isAbst()) || (bean.isReal()) || (bean.getDownloadType().startsWith(bean.downloadType_inputs_rtID))) {
            // get workflow xml from workflow store
            // lefut: all, graf, abst es real esetekben
            workflowXML = getWorkflowXMLDetailsFromWFS(bean);
            // get eworkflowlist from details string
            Element eworkflowlist = XMLUtils.getInstance().getElementWorkflowListFromXML(workflowXML);
            // get workflow names and texts from eworkflowlist
            grafList = XMLUtils.getInstance().getWorkflowNameAndTextFromElement(eworkflowlist, "graf", null);
            abstList = XMLUtils.getInstance().getWorkflowNameAndTextFromElement(eworkflowlist, "abst", null);
            realList = XMLUtils.getInstance().getWorkflowNameAndTextFromElement(eworkflowlist, "real", null);
            // add workflow xml string to zip
            if ((bean.isAll()) || (bean.isGraf()) || (bean.isAbst()) || (bean.isReal()) || (bean.getDownloadType().startsWith(bean.downloadType_inputs_rtID)))
            {
                // add workflow xml string to zip
                String workflowDetailsPath = bean.workflowXMLDetailsInformationFileName;
                sendWorkflowDetailsToStream(userBaseDir, workflowDetailsPath, workflowXML, zipout);
            }
        }
        else {realList.put(bean.getWorkflowID(), "");}
        // add real workflows
        addWorkflowFileListToZipStream(bean, zipout, userBaseDir, realList, true);
        // add abst workflows
        addWorkflowFileListToZipStream(bean, zipout, userBaseDir, abstList, false);
        // get only job outputs (details kepernyo)
//        System.out.println("EZ VAN:"+bean.getDownloadType()+"/"+bean.downloadType_job_outputs_rtID+"::"+(bean.getDownloadType().startsWith(bean.downloadType_job_outputs_rtID)));
        if (bean.getDownloadType().startsWith(bean.downloadType_job_outputs_rtID)) {
            String workflowJobDirPath = bean.getWorkflowID() + sep + bean.getJobID();
//            System.out.println("downloadType_job_outputs_rtID : " + bean.getRuntimeID() + " - " + bean.getPidID() + " - " + bean.getOutputLogType());
            sendJobOutputsToStream(zipout, userBaseDir, workflowJobDirPath, bean.getRuntimeID(), bean.getPidID(), bean.getOutputLogType());
        }
        // add infotxt string to zip
        String workflowDetailsPath = bean.workflowDetailsInformationFileName;
        // String infotxt = getInfoTxtString(bean, grafList, abstList, realList);
        // sendWorkflowDetailsToStream(userBaseDir, workflowDetailsPath, infotxt, zipout);
        // flush zip stream
        zipout.flush();
        // finish and close zip stream
        zipout.close();
        // System.out.println("downloadWorkflowZipFileToZipStream end...");
    }
    
    /**
     * Writes the workflow directory to 
     * the zip stream given in the parameters.
     *
     * @param DownloadWorkflowBean bean - descriptor parameters
     * @param zipout - zip stream
     * @param userBaseDir - user base directory
     * @param wfList - workflow list
     * @param isReal - real workflow
     * @throws Exception
     */
    private void addWorkflowFileListToZipStream(DownloadWorkflowBean bean, ZipOutputStream zipout, String userBaseDir, Hashtable wfList, boolean isreal) throws Exception {
        if (!wfList.isEmpty()) {
            // System.out.println("wfList : " + wfList);
            // parse wfList, add workflow files to zip
            Enumeration wfenum = wfList.keys();
            while (wfenum.hasMoreElements()) {
                String wfname = (String) wfenum.nextElement(); // real or abst workflow name
                String wftext = (String) wfList.get(wfname); // real or abst workflow text
                // System.out.println("add workflow to zip : " + wfname);
                // System.out.println("wftext : " + wftext);
                addWorkflowFilesToZipStream(bean, zipout, userBaseDir, wfname, isreal);
            }
        }
    }
    
    /**
     * Writes the workflow directory to 
     * the given zip stream.
     *
     * @param DownloadWorkflowBean bean - descriptor parameters
     * @param zipout - zip stream
     * @param userBaseDir - user base directory
     * @param workflowID - workflow directory (name)
     * @throws Exception
     */
    private void addWorkflowFilesToZipStream(DownloadWorkflowBean bean, ZipOutputStream zipout, String userBaseDir, String workflowID, boolean isreal) throws Exception {
        // get workflow dir
        String workflowBaseDir = new String(userBaseDir + workflowID + sep);
        // System.out.println("workflowBaseDir: " + workflowBaseDir);
        // get job list
        ArrayList jobList = new ArrayList();
        FileUtils.getInstance().createDirectory(workflowBaseDir);
        jobList = FileUtils.getInstance().getSubDirList(workflowBaseDir);
        sendDirNameToStream(zipout, userBaseDir, workflowID);
        // add jobs data to zip
        for (int jobPos = 0; jobPos < jobList.size(); jobPos++) {
            String workflowJobDirPath = workflowID + sep + ((String) jobList.get(jobPos));
            if ((bean.isAll()) || (bean.isReal()) || (bean.isAbst()) || (bean.getDownloadType().startsWith(bean.downloadType_inputs_rtID))) {
                String runtimeID = bean.getRuntimeID();
                if ("".equals(runtimeID)) {
                    runtimeID = new String(bean.downloadType_rtID_all);
                }
                // send real workflow files to zip stream
                sendInputsToStream(zipout, userBaseDir, workflowJobDirPath);
                if (isreal) {
                    sendOutputsToStream(zipout, userBaseDir, workflowJobDirPath, runtimeID, bean.getOutputLogType());
                }
            } else if (bean.getDownloadType().equalsIgnoreCase(bean.downloadType_inputs)) {
                sendInputsToStream(zipout, userBaseDir, workflowJobDirPath);
            } else if (bean.getDownloadType().startsWith(bean.downloadType_inputs_rtID)) {
                sendInputsToStream(zipout, userBaseDir, workflowJobDirPath);
                sendOutputsToStream(zipout, userBaseDir, workflowJobDirPath, bean.getRuntimeID(), bean.getOutputLogType());
            } else if (bean.getDownloadType().startsWith(bean.downloadType_outputs_rtID)) {
                sendOutputsToStream(zipout, userBaseDir, workflowJobDirPath, bean.getRuntimeID(), bean.getOutputLogType());
            }
        }
    }
    
    /**
     * Writes the name of the workflow/job directory 
     * to the given zip stream.
     *
     * @param zipout
     * @param userbaseDir -
     *            user base directory
     * @param workflowID -
     *            workflow directory (name)
     * @throws Exception
     */
    private void sendDirNameToStream(ZipOutputStream zipout, String userbaseDir, String workflowID) throws Exception {
        ZipUtils.getInstance().sendDirNameToZipStream(userbaseDir, workflowID, zipout);
    }
    
    /**
     * Writes all the input files and the execute file of the 
     * job given in the parameters to the given zip stream.
     *
     * @param zipout
     * @param userbaseDir -
     *            user base directory
     * @param workflowJobDirPath -
     *            workflow plus job directory
     * @throws Exception
     */
    private void sendInputsToStream(ZipOutputStream zipout, String userbaseDir, String workflowJobDirPath) throws Exception {
        // ha nincs executable attol meg menjen tovabb
        try {
            String workflowJobExecuteFilePath = new String(workflowJobDirPath + sep + FileUtils.getInstance().getDefaultBinaryName());
            ZipUtils.getInstance().sendFileToZipStream(userbaseDir, workflowJobExecuteFilePath, zipout);
        } catch (Exception e) {}
        String workflowJobInputsDirPath = new String(workflowJobDirPath + sep + "inputs" + sep);
        ZipUtils.getInstance().sendInputDirAllFilesToZipStream(userbaseDir, workflowJobInputsDirPath, zipout);
    }
    
    /**
     * Writes all the outputs (rtID="all") or only the 
     * outputs with a given rtID to the given zip stream.
     *
     * @param zipout
     * @param userbaseDir -
     *            user base directory
     * @param workflowJobDirPath -
     *            workflow plus job directory
     * @param runtimeID -
     *            runtime ID
     * @param outputLogType -
     *            does the log file needed from the directory
     * @throws Exception
     */
    private void sendOutputsToStream(ZipOutputStream zipout, String userbaseDir, String workflowJobDirPath, String runtimeID, String outputLogType) throws Exception {
        if (runtimeID.equalsIgnoreCase(DownloadWorkflowBean.downloadType_rtID_all)) {
            String workflowJobOutputsDirPath = new String(workflowJobDirPath + sep + "outputs" + sep);
//            System.out.println("**sendOutputsToStream:"+userbaseDir+"/"+workflowJobOutputsDirPath);
            ZipUtils.getInstance().sendOutputDirAllFilesToZipStream(userbaseDir, workflowJobOutputsDirPath, zipout, outputLogType);
        } else {
            if (!runtimeID.trim().equals("")) {
                // sendDirNameToStream(zipout, userbaseDir, workflowJobDirPath + sep + "outputs");
                String workflowJobOutputsRuntimeIDDirPath = new String(workflowJobDirPath + sep + "outputs" + sep + runtimeID);
                ZipUtils.getInstance().sendOutputDirAllFilesToZipStream(userbaseDir, workflowJobOutputsRuntimeIDDirPath, zipout, outputLogType);
            } else {
                throw new Exception("runtimeID not valid ! runtimeID = (" + runtimeID + ")");
            }
        }
    }
    
    /**
     * Writes the output of the job with the given pid 
     * or with the given rtID to the zip stream given
     * in the parameters.
     *
     * @param zipout
     * @param userbaseDir -
     *            user base directory
     * @param workflowJobDirPath -
     *            workflow plus job directory
     * @param runtimeID -
     *            runtime ID
     * @param pidID -
     *            pid ID
     * @param outputLogType -
     *            does the log file needed from the directory
     * @throws Exception
     */
    private void sendJobOutputsToStream(ZipOutputStream zipout, String userbaseDir, String workflowJobDirPath, String runtimeID, String pidID, String outputLogType) throws Exception
    {
        if (!runtimeID.trim().equals(""))
        {
            // sendDirNameToStream(zipout, userbaseDir, workflowJobDirPath + sep + "outputs");
            // old path: String workflowJobOutputsRuntimeIDDirPath = new String(workflowJobDirPath + sep + "outputs" + sep + runtimeID);
            String workflowJobOutputsRuntimeIDDirPath = new String(workflowJobDirPath + sep + "outputs" + sep + runtimeID + sep + pidID);
//            System.out.println("storage.zip:"+userbaseDir+workflowJobOutputsRuntimeIDDirPath+"*");
            ZipUtils.getInstance().sendJobOutputDirPidFilesToZipStream(userbaseDir, workflowJobOutputsRuntimeIDDirPath, zipout, outputLogType, pidID);
        }
        else {throw new Exception("runtimeID not valid ! runtimeID = (" + runtimeID + ")");}
    }
    
    /**
     * Writes the base data (file) of the given workflow to the 
     * given zip stream.
     *
     * @param zipout
     * @param userbaseDir -
     *            user base directory
     * @param workflowDetailsPath -
     *            workflow details file path
     * @throws Exception
     */
    private void sendDetailsToStream(String userbaseDir, String workflowDetailsPath, ZipOutputStream zipout) throws Exception {
        if ((!userbaseDir.trim().equals("")) && (!workflowDetailsPath.trim().equals(""))) {
            ZipUtils.getInstance().sendFileToZipStream(userbaseDir, workflowDetailsPath, zipout);
        } else {
            throw new Exception("workflowBaseDir or userbaseDir not valid ! userBaseDir = (" + userbaseDir + ") workflowDetailsPath = (" + workflowDetailsPath + ")");
        }
    }
    
    /**
     * Writes the base data (string) of the given workflow to the 
     * given zip stream.
     *
     * @param zipout
     * @param userbaseDir -
     *            user base directory
     * @param workflowDetailsPath -
     *            workflow details entry path
     * @param workflowDetailStr -
     *            workflow details string
     * @throws Exception
     */
    private void sendWorkflowDetailsToStream(String userbaseDir, String workflowDetailsPath, String workflowDetailStr, ZipOutputStream zipout) throws Exception {
        if ((!userbaseDir.trim().equals("")) && (!workflowDetailsPath.trim().equals(""))) {
            ZipUtils.getInstance().sendStringToZipStream(userbaseDir, workflowDetailsPath, workflowDetailStr, zipout);
        } else {
            throw new Exception("workflowBaseDir or userbaseDir not valid ! userBaseDir = (" + userbaseDir + ") workflowDetailsPath = (" + workflowDetailsPath + ")");
        }
    }
    
    /**
     * Gets the data of the workflow given in the parameters from the workflow storage.
     * @param bean - parameters come in the DownloadWorkflowBean
     *  wfsID - workflow storage ID
     *  portalURL - portal ID
     *  userID - user ID
     *  workflowID - workflow ID
     *  downloadType - the download types are "all", "graf", "abst", "real"
     *  instanceType - should also the instance descriptors be downloaded "none", "all"
     *  exportType - should also the embedded workflows be downloaded  "appl", "proj", "work"
     * @return WorkflowXML String
     */
    private String getWorkflowXMLDetailsFromWFS(DownloadWorkflowBean bean) throws Exception {
        if (bean.getDownloadType().startsWith(bean.downloadType_inputs_rtID)) {
            // get workflow xml from workflow store
            // runs: in case of inputs_rtID
            // csak az xml lekerdezesnel atirtam a downloadType-t parametert
            // "downloadType_inputs_rtID"-rol "downloadType_real"-ra ("real")
            // mert szukseg van a graf es real workflow descriptor
            // tag-re is az xml-ben. (ez a kivetel)
            bean.setDownloadType("real");
        }
        // build data cbean
        StorageWorkflowNamesBean cbean = new StorageWorkflowNamesBean();
        cbean.setPortalID(bean.getPortalURL());
        cbean.setUserID(bean.getUserID());
        cbean.setWorkflowID(bean.getWorkflowID());
        cbean.setDownloadType(bean.getDownloadType());
        cbean.setInstanceType(bean.getInstanceType());
        cbean.setExportType(bean.getExportType());
        String workflowXML = new String("");
        // use info system
        Hashtable hash = new Hashtable();
        hash.put("url", bean.getWfsID());
        ServiceType serviceType = InformationBase.getI().getService("wfs", "storage", hash, new Vector());
        if(serviceType != null) {
            try {
                StorageWfsClient client = (StorageWfsClient) Class.forName(serviceType.getClientObject()).newInstance();
                client.setServiceURL(serviceType.getServiceUrl());
                client.setServiceID(serviceType.getServiceID());
                // get workflow xml...
                workflowXML = client.getWorkflowXML(cbean);
            } catch(Exception e) {
                // e.printStackTrace();
            }
        }
        if ((workflowXML == null) || ("".equals(workflowXML))) {
            throw new Exception("Not valid workflowXML !!! workflowXML = (" + workflowXML + ")");
        }
        if (workflowXML.startsWith("error: ")) {
            throw new Exception(workflowXML.substring(7));
        }
        // System.out.println("workflowXML :" + workflowXML);
        return workflowXML;
    }
    
    /**
     * Gets the data of the workflow given in the parameters from the workflow storage.
     *
     * @param DownloadWorkflowBean bean - workflow zip descriptor data
     * @param grafList - graf workflow list
     * @param abstList - abst workflow list
     * @param realList - real workflow list
     * @return infotxt String
     */
    private String getInfoTxtString(DownloadWorkflowBean bean, Hashtable grafList, Hashtable abstList, Hashtable realList) {
        StringBuffer sb = new StringBuffer();
        // sb.append("zip table of contents:" + "\n");
        sb.append("original zip file name = " + bean.getDownZipFileName() + "\n");
        if (!"".equals(bean.getDownloadType())) {
            sb.append("download workflow type = " + bean.getDownloadType() + "\n");
        }
        if ((!"".equals(bean.getInstanceType())) && (!bean.instanceType_none.equals(bean.getInstanceType()))) {
            sb.append("instance type = " + bean.getInstanceType() + "\n");
        }
        if (!"".equals(bean.getOutputLogType())) {
            sb.append("output log type = " + bean.getOutputLogType() + "\n");
        }
        if (!"".equals(bean.getExportType())) {
            sb.append("export type = " + bean.getExportType() + "\n");
        }
        if ((!grafList.isEmpty()) || (!abstList.isEmpty()) || (!realList.isEmpty())) {
            sb.append("xml contents:" + "\n");
        }
        if (!grafList.isEmpty()) {
            sb.append("graph workflow names    = " + grafList.keySet() + "\n");
        }
        if (!abstList.isEmpty()) {
            sb.append("template workflow names = " + abstList.keySet() + "\n");
        }
        if (!realList.isEmpty()) {
            sb.append("concrete workflow names = " + realList.keySet() + "\n");
        }
        return sb.toString();
    }
    
    /**
     * Returns the file name of the zip file to be downloaded 
     * regarding to the workflow data given in the parameters.
     *
     * @param bean - workflow zip descriptor data
     * @return zip file name String
     */    
    public String getDownloadZipName(DownloadWorkflowBean bean) {
        // old version : return new String("SZTAKI_" + bean.getUserID() + "_" + bean.getWorkflowID() + "_" + bean.getDownloadType() + ".zip");
        String retStr = new String(bean.getWorkflowID());
        if (bean.isProj()) {
            retStr = new String(retStr + "_" + "project");
        } else if (bean.isAppl()) {
            retStr = new String(retStr + "_" + "application");
        } else if (bean.isWork()) {
            if (bean.isAbst()) {
                retStr = new String(retStr + "_" + "template");
            } else if (bean.isGraf()) {
                retStr = new String(retStr + "_" + "graph");
            } else {
                retStr = new String(retStr + "_" + "workflow");
            }
        }
        if (bean.getDownloadType().equals(bean.downloadType_inputs)) {
            retStr = new String(retStr + "_" + "inputs");
        } else if (bean.getDownloadType().startsWith(bean.downloadType_inputs_rtID)) {
            retStr = new String(retStr + "_" + "instance");
        } else if (bean.getDownloadType().startsWith(bean.downloadType_outputs_rtID)) {
            if (bean.getDownloadType().equals("outputs_all")) {
                retStr = new String(retStr + "_" + "outputs");
            } else {
                retStr = new String(retStr + "_" + "instance_outputs");
            }
        } else if (bean.getDownloadType().startsWith(bean.downloadType_job_outputs_rtID)) {
            // ha job outputot toltenek le
            // (details kepernyo job melleti downlad gomb)
            retStr = new String(retStr + "_" + bean.getJobID() + "_" + bean.getPidID() + "_" + "joboutputs");
        }
        // else if (bean.getDownloadType().startsWith(bean.downloadType_rtID_all)) {
        //    retStr = new String(retStr + "_" + "instances");
        //}
        if (bean.getDownloadType().equals("all")) {
            retStr = new String(retStr + "_" + "all");
        }
        if (bean.getOutputLogType().equals(bean.outputLogType_none)) {
            retStr = new String(retStr + "_" + "nologs");
        }
        return retStr + ".zip";
    }
    
}

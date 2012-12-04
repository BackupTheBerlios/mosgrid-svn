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
package hu.sztaki.lpds.storage.service.carmen.server.receiver.plugins;

import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.PluginUtils;
import hu.sztaki.lpds.storage.service.carmen.commons.ZipUtils;
import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;

import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;

/**
 * File Receiver Servlet.
 *
 * DAG plugin. (other name is Condor plugin.)
 *
 * (The implementation class of the receiver storage service.)
 *
 * Sends and receives files from the submitter,
 * also receives from the portal when uploading.
 *
 * @author lpds
 */
public class dag_ReceiverServiceImpl implements ReceiverService {
    
    // file receiver repository root dir, ide tarolja le a megkapott fileokat
    private String repositoryDir;
    
    // File separator (for example, "/")
    private String sep;
    
    // max request (receive file) size
    private long maxRequestSize = 500 * 1024 * 1024;
/**
 * Constructor
 * loading properties
 */
    public dag_ReceiverServiceImpl() {
        LoadProperty();
        // System.out.println("File Receiver Repository Path: " +
        // fileReceiverRepositoryDir);
    }
    
    /**
     * Init, loading initialization data.
     */
    private void LoadProperty() {
        sep = FileUtils.getInstance().getSeparator();
        repositoryDir = FileUtils.getInstance().getRepositoryDir();
    }
    
    /**
     * @see ReceiverService#receive(com.oreilly.servlet.MultipartRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void receive(MultipartRequest multipartRequest, HttpServletResponse response) throws Exception {
        ZipParser(multipartRequest, response);
    }
    
    /**
     * Uploads and extracts the given file (zip file)
     * to the given user's directory, deals with the necessary communication
     * with the other parts of the system such as wfs, portal.
     *
     * @param multipartRequest
     */
    private void ZipParser(MultipartRequest multipartRequest, HttpServletResponse response) {
        File uploadedZipFile = null;
        try {
            // parse parameterS (parameterek begyujtese)
            // A keresben hasznalt parametereknek ad kezdo erteket.
            // portal service url
            String portalURL = new String("");
            // portal id dir name
            String portalID = new String("");
            // wfs ID
            String wfsID = new String("");
            // user name
            String userID = new String("");
            // Az osszes parameteret kigyujti
            // a feltoltott graf workflow ezt a nevet kapja
            String newGrafName = new String("");
            // a feltoltott abstract workflow ezt a nevet kapja
            String newAbstName = new String("");
            // a feltoltott real, konkret workflow ezt a nevet kapja
            String newRealName = new String("");
            //
            Enumeration enumParameters = multipartRequest.getParameterNames();
            String parameterName = null;
            String parameterValue = null;
            while (enumParameters.hasMoreElements()) {
                parameterName = new String((String) enumParameters.nextElement()).trim();
                if ((parameterName != null) && (!"".equals(parameterName))) {
                    parameterValue = null;
                    parameterValue = new String(multipartRequest.getParameter(parameterName)).trim();
                    if ((parameterValue != null) && (!"".equals(parameterValue))) {
                        // System.out.println("parameterName : " + parameterName);
                        // System.out.println("parameterValue: " + parameterValue);
                        if ("portalURL".equals(parameterName)) {
                            portalURL = parameterValue;
                            // convert url to dirName
                            portalID = FileUtils.getInstance().convertPortalIDtoDirName(portalURL);
                        }
                        if ("wfsID".equals(parameterName)) {
                            wfsID = parameterValue;
                        }
                        if ("userID".equals(parameterName)) {
                            userID = parameterValue;
                        }
                        if ("newGrafName".equals(parameterName)) {
                            newGrafName = parameterValue;
                        }
                        if ("newAbstName".equals(parameterName)) {
                            newAbstName = parameterValue;
                        }
                        if ("newRealName".equals(parameterName)) {
                            newRealName = parameterValue;
                        }
                    }
                }
            }
            // send files in stream to out
            Enumeration enumFiles = multipartRequest.getFileNames();
            while (enumFiles.hasMoreElements()) {
                String fileName = (String) enumFiles.nextElement();
                String fileOriginalFileName = multipartRequest.getOriginalFileName(fileName);
                if (fileOriginalFileName == null) {
                    throw new Exception("Not valid file name !");
                }
                // System.out.println("fileOriginalFileName : " + fileOriginalFileName);
                // String userBaseDir = repositoryDir + portalID + sep + userID;
                // no portalID level
                String userBaseDir = repositoryDir + sep + userID;
                FileUtils.getInstance().createDirectory(userBaseDir);
                String zipPathName = userBaseDir + sep + fileOriginalFileName;
                // System.out.println("zipPathName : " + zipPathName);
                String storageURL = FileUtils.getInstance().getStorageUrl();
                uploadedZipFile = new File(zipPathName);
                // save zip file from stream
                // getFile()
                File zFile = multipartRequest.getFile(fileName);
                // move to temp file
                zFile.renameTo(uploadedZipFile);
                if (uploadedZipFile.length() == 0) {
                    throw new Exception("Not valid file ! size == 0 !");
                }
                // upload zip file to storage
                StorageWorkflowNamesBean bean = new StorageWorkflowNamesBean();
                bean.setPortalID(portalID);
                bean.setPortalURL(portalURL);
                bean.setStorageURL(storageURL);
                bean.setWfsID(wfsID);
                bean.setUserID(userID);
                bean.setZipFilePathStr(zipPathName);
                bean.setNewMainGrafName(newGrafName);
                bean.setNewMainAbstName(newAbstName);
                bean.setNewMainRealName(newRealName);
                // az importID es az uploadID = ""
                bean.setImportID("");
                bean.setUploadID("");
                // bean = portalURL, storageURL, wfsID, userID, zipPathName, new main workflow name
                if (uploadedZipFile.exists()) {
                    // System.out.println("zipFileSize : " + uploadedZipFile.length());
                    if (uploadedZipFile.length() == 0) {
                        throw new Exception("Not valid zip file size = 0 !!!");
                    }
                    // get workflow name from zip file...
                    String zipWfName = ZipUtils.getInstance().getBaseDirNameFromZipFile(uploadedZipFile);
                    // System.out.println("zipWfName : " + zipWfName);
                    String newWfName = zipWfName;
                    //
                    // atnevezes ha szukseges
                    if (!"".equals(bean.getNewMainRealName())) {
                        newWfName = bean.getNewMainRealName();
                    }
                    // System.out.println("newWfName : " + newWfName);
                    //
                    // az atnevezett workflow nev listakat kuldjuk el
                    // a portalnak es azt ellenorzi le !
                    // van e mar koztuk olyan ami mar letezik...
                    // nincs graf workflow (egyelore)
                    Hashtable newGrafList = new Hashtable();
                    // nincs abst, template workflow
                    Hashtable newAbstList = new Hashtable();
                    // egy db real, kokret workflow van
                    Hashtable newRealList = new Hashtable();
                    newRealList.put(newWfName, ""); // do not set workflow text
                    // workflow nev utkozesek ellenorzese...
                    String retStr = PluginUtils.getInstance().checkWorkflowNames(bean, newGrafList, newAbstList, newRealList);
                    if (!"".equals(retStr)) {
                        throw new Exception(retStr);
                    }
                    //
                    // most kovetkezik a real workflow file-ok kimasolasa
                    // a zip-bol es lementese a user storage teruletere.
                    // figyelembe kell venni hogy kitomorites kozben a real neve
                    // megvaltozhat ha a new main real nev nem "" ures string.
                    Hashtable plussQuotaSizeHash = ZipUtils.getInstance().unZipWorkflowZipFileNotStoreRoot(uploadedZipFile, userBaseDir, zipWfName, newWfName, bean.getUploadID());
                    // System.out.println("plussQuotaSizeHash : " + plussQuotaSizeHash);
                    //
                    // send workflows xml details to wfs
                    // bean = storageURL, wfsID, portalURL, userID, workflowXML, uploadID,
                    // main wf names, new main wf names, downloadType, exportType...
                    bean.setWorkflowXML(generateWorkflowXML(newWfName));
                    // System.out.println("workflow xml : " + bean.getWorkflowXML());
                    if ("".equals(bean.getWorkflowXML())) {
                        throw new Exception("Not valid workflow details !");
                    }
                    boolean setResult = PluginUtils.getInstance().setWorkflowXMLDetailsToWFS(bean);
                    if (!setResult) {
                        throw new Exception("Not success store workflows details xml in wfs !");
                    }
                    //
                    // send workflow name to portal
                    // bean = portalURL, storageURL, wfsID, userID
                    // az atnevezett nevlistakat kuldjuk el a portalnak es azzal frissiti be a portal cache-t !
                    PluginUtils.getInstance().sendWorkflowNamesToPortal(bean, newGrafList, newAbstList, newRealList);
                    // refresh storage quota...
                    Thread.sleep(100);
                    PluginUtils.getInstance().refreshWorkflowQuota(portalID, userID, newWfName, plussQuotaSizeHash);
                    //
                    // delete temp file
                    // System.out.println("... delete ...");
                    uploadedZipFile.delete();
                    // response.setContentType("text/html");
                    OutputStream out = response.getOutputStream();
                    out.write(new String("Workflow upload successfull").getBytes());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                // delete temp file
                // System.out.println("... delete ...");
                uploadedZipFile.delete();
                // send 560 error to client
                // response.sendError(560, "Server side exception !!! + " + e);
                // response.setContentType("text/html");
                OutputStream out = response.getOutputStream();
                // System.out.println("e.getMessage() : " + e.getMessage());
                // System.out.println("e.getLocalizedMessage() : " + e.getLocalizedMessage());
                out.write(new String("Workflow upload not successfull: " + e.getMessage()).getBytes());
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    private String generateWorkflowXML(String newWfName) throws Exception {
        try {
            
            
            
            
            
// TODO : meg nem mukodik, wf letarolasa a wfs ben, perzisztencia...
// meg nem mukodik, wf letarolasa a wfs ben, perzisztencia...
// meg nem mukodik, wf letarolasa a wfs ben, perzisztencia...
// meg nem mukodik, wf letarolasa a wfs ben, perzisztencia...
// meg nem mukodik, wf letarolasa a wfs ben, perzisztencia...
// meg nem mukodik, wf letarolasa a wfs ben, perzisztencia...
// meg nem mukodik, wf letarolasa a wfs ben, perzisztencia...
            
            
            
            
            
            
            
            
            
            
            
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element eworkflowlist = doc.createElement("workflow");
            eworkflowlist.setAttribute("name", newWfName);
            eworkflowlist.setAttribute("maingraf", "");
            eworkflowlist.setAttribute("mainabst", "");
            eworkflowlist.setAttribute("mainreal", newWfName);
            eworkflowlist.setAttribute("download", "real");
            eworkflowlist.setAttribute("export", "work");
            //
            // create empty real, concrete workflow node
            Element real = doc.createElement("real");
            real.setAttribute("name", newWfName);
            real.setAttribute("text", "");
            real.setAttribute("graf", "");
            real.setAttribute("abst", "");
            // add real, concrete wf to root workflow node
            eworkflowlist.appendChild(real);
            // return workflow xml
            return transformWorkflowListToString(doc, eworkflowlist);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Not valid workflow xml data...");
        }
    }
    
    /**
     * Creates a string from the workflowlist element object.
     *
     * @param Document doc parent document
     * @param Element eworkflowlist
     * @return workflow XML descriptor string
     */
    private String transformWorkflowListToString(Document doc, Element eworkflowlist) throws Exception {
        // System.out.println("build <eworkflowlist>");
        // Create dom document
        doc.appendChild(eworkflowlist);
        // Generate XML output to string
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        // We want to pretty format the XML output...
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        // transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        // transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        // source: doc
        DOMSource source = new DOMSource(doc);
        // doc to string
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        transformer.transform(source, streamResult);
        String xmlstr = stringWriter.toString();
        // System.out.println("xmlstr : " + xmlstr);
        return xmlstr;
    }
    
}

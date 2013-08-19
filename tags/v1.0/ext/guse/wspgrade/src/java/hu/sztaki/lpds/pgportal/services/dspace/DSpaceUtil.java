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
package hu.sztaki.lpds.pgportal.services.dspace;

import edu.harvard.hul.ois.mets.helper.MetsException;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.portlet.ActionRequest;
import org.apache.commons.httpclient.HttpException;

public class DSpaceUtil {

    private static final String DSPACE_FILE_DSCRIMINATOR = "_DSPACE";
    // Username and password for anonymous DSpace user, needed for LNI
    // This user should exist as a DSpace user that belongs to no group
    // (except anonymous, which is default)
    private static final String anonDSpaceUser = "anonymous";
    private static final String anonDSpacePass = "d81lK7An663y1tC";
    //export params
    // Collection, should be "Application Workflows" on DSpace
//    private static final String collection = "dspace/44";
    // METS type to use for metadata
    private static final String METS = "MODS";

    public DSpaceUtil() {
    }

    public boolean isConfigured() {
        if (PropertyLoader.getInstance().getProperty("dspace.url") != null
                && PropertyLoader.getInstance().getProperty("dspace.collection") != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Handles a workflow download from the repository.
     *
     * This function will connect to DSpace through LNI and download a DSpace
     * Dissemination Information Package (DIP) from a specified handle. The
     * compressed workflow is then extracted from the DIP (it will be in the
     * form of 'bitstream_<#>.zip'). This compressed workflow is then uploaded
     * into the user's workflow sotrage space on guse.
     *
     * @param handle
     * @param user
     * @param h storage datas, keys: newGrafName,newAbstName,newRealName
     */
    public void importFromDspace(String handle, String user, Hashtable h) throws UnsupportedEncodingException, IOException, HttpException, Exception {
        if ("".equals(handle.trim())) {
            throw new UnsupportedEncodingException("");
        }
        String[] parts = handle.split("/");
        handle = "";
        for (int i = 0; i < parts.length; i++) {
            // Make sure we don't pass a handle with illegal URL characters
            // This should only happen if a user enters the handle incorrectly
            // If there's a problem, handle will be invalid anyways , throws UnsupportedEncodingException
            handle = handle.concat(URLEncoder.encode(parts[i].trim(), "UTF-8"));

            if (i + 1 != parts.length) {
                handle = handle.concat("/");
            }
        }

        // Create LNI client
        LNIclient lni = new LNIclient(getDSpaceURL() + "lni/dav/", anonDSpaceUser, anonDSpacePass);

        String dspaceDIP_string = getTempdir() + user + DSPACE_FILE_DSCRIMINATOR + ".zip";
        purge(dspaceDIP_string);
        try {
            // Start the GET from DSpace
            InputStream is = lni.startGet(handle, "METS", null);
            // Read the input stream from DSpace and extract bitstream
            // We have the DIP, now we would like the Item ()
            // TODO: Note that we are expecting a zip file here. Perhaps a more flexible method can be implemented.
            File bitstream = new File(dspaceDIP_string);
            unzipBitstream(is, bitstream);
            // Finish GET process
            lni.finishGet();
            uploadToStorage(user, bitstream, h);
        } finally {
            purge(dspaceDIP_string);
        }
    }

    private String getDSpaceURL() {
        String dspaceURL_INTERN = PropertyLoader.getInstance().getProperty("dspace.url");
        int j = 0;
        for (int i = 0; i < 3; i++) {
            j = dspaceURL_INTERN.indexOf("/", j) + 1;
        }

        String res = dspaceURL_INTERN.substring(0, j);
        System.out.println("RepositoryPortlet.getDSpaceURL res= " + res);
        return dspaceURL_INTERN.substring(0, j);
    }

    /**
     * Purges a subdirectory defined by the absolute path recursively
     */
    private void purge(String s) {
        File f = new File(s);
        if (f.isFile()) {
            f.delete();
        } else if (f.isDirectory()) {
            File[] members = f.listFiles();
            int size = members.length;
            while (size > 0) {
                purge(members[--size].getAbsolutePath());
            }
            f.delete();
        }
    }

    /**
     * Extracts any file from <code>inputFile</code> that begins with
     * 'bitstream' to <code>outputFile</code>.
     * Adapted from examples on
     * http://java.sun.com/developer/technicalArticles/Programming/compression/
     *
     * @param inputFile  File to extract 'bitstream...' from
     * @param outputFile  File to extract 'bitstream...' to
     */
    private static void unzipBitstream(InputStream is, File outputFile) throws IOException {
        final int BUFFER = 2048;
        BufferedOutputStream dest = null;
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry;
        try {

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().matches("bitstream.*")) {
                    int count;
                    byte data[] = new byte[BUFFER];

                    // write the files to the disk
                    FileOutputStream fos = new FileOutputStream(outputFile.getAbsoluteFile());
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                        //System.out.println("Writing: " + outputFile.getAbsoluteFile());
                    }
                    dest.flush();
                    dest.close();
                }
            }
        } finally {
            zis.close();
            try {
                dest.close();
            } catch (Exception e) {
            }
        }
    }

    private void uploadToStorage(String pUser, File serverSideFile, Hashtable h) throws Exception {
//        Hashtable h = new Hashtable(); //fileupload
//            h.put("newGrafName", "g");
//            h.put("newAbstName", "a");
//            h.put("newRealName", "r");

        ServiceType st = InformationBase.getI().getService("wfs", "portal", new Hashtable(), new Vector());
        h.put("senderObj", "ZipFileSender");
        h.put("portalURL", PropertyLoader.getInstance().getProperty("service.url"));
        h.put("wfsID", st.getServiceUrl());
        h.put("userID", pUser);

        st = InformationBase.getI().getService("storage", "portal", new Hashtable(), new Vector());
        PortalStorageClient psc = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
        psc.setServiceURL(st.getServiceUrl());
        psc.setServiceID("/receiver");

        psc.fileUpload(serverSideFile, "fileName", h);
    }

    private String getTempdir() {
        // Create temporary location for down/uploading file
        File tDir = new File(System.getProperty("java.io.tmpdir") + "/uploads/");
        if (!tDir.exists()) {
            tDir.mkdirs();
        }
        return System.getProperty("java.io.tmpdir") + "/uploads/";
    }

/// export
    /**
     * Returns a string representing METS XML metadata file to be included
     * with DSpace SIP
     *
     * @param event
     * @return XML String
     */
    private String getMETS(ActionRequest request) {
        // Get Author
        String authorFirst = request.getParameter("firstName");
        String authorLast = request.getParameter("lastName");

        // Get Information
        String title = request.getParameter("title");
        String keywords = request.getParameter("keywords");
        String grid = request.getParameter("grid");
        String vo = request.getParameter("vo");
        String type = "Workflow";//request.getParameter("type");
        //String language =

        // Get Abstract
        String wfAbstract = request.getParameter("abstract").trim();//event.getTextAreaBean("abstractArea").getValue();
        String wfDescription = request.getParameter("description").trim();//event.getTextAreaBean("descriptionArea").getValue();;

        // MODS XML metadata to send with SIP
        // At this time, "type" is sent as a keyword, rather than its DC element
        // dc.type. Also, "language" is not collected/sent. It may be possible to
        // overcome this by editing [dspace]/config/crosswalks/mods-submission.xsl
        String modsxml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<mods:mods xmlns:mods='http://www.loc.gov/mods/v3'"
                + "xmlns:xlink='http://www.w3.org/1999/xlink'"
                + "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"
                + "xsi:schemaLocation='http://www.loc.gov/mods/v3"
                + "http://www.loc.gov/standards/mods/v3/mods-3-0.xsd' version='3.0'>"
                + "<mods:titleInfo>"
                + "<mods:title>" + title + "</mods:title>"
                + "</mods:titleInfo>"
                + "<mods:name>"
                + "<mods:namePart>" + authorLast + ", " + authorFirst + "</mods:namePart>"
                + "<mods:role>"
                + "<mods:roleTerm type='text'>author</mods:roleTerm>"
                + "</mods:role>"
                + "</mods:name>";

        String allKeywords = keywords + "," + grid + "," + vo + "," + type;
        ListIterator<String> iterator = parseKeywords(allKeywords);

        while (iterator.hasNext()) {
            modsxml = modsxml.concat("<mods:subject>"
                    + "<mods:topic>" + iterator.next() + "</mods:topic>"
                    + "</mods:subject>");
        }

        if (!"".equals(wfAbstract)
                && !wfAbstract.equals("[Enter an abstract of your workflow here]")) {
            modsxml = modsxml.concat("<mods:abstract>" + wfAbstract + "</mods:abstract>");

            //if (!wfDescription.isEmpty() &&

        }
        if (!"".equals(wfDescription)
                && !wfDescription.equals("[Enter a detailed description of your workflow here]")) {
            modsxml = modsxml.concat("<mods:note xlink:type='simple'>" + wfDescription + "</mods:note>");


        }
        modsxml = modsxml.concat("</mods:mods>");


        // Tried to make DC metadata to send to DSpace, but doesn't work..
        // Here it is if you want to give it a go
        /*String dcxml = "<?xml version='1.0' encoding='UTF-8'?>" +
        "<dc:dc xmlns:dc='http://www.openarchives.org/OAI/2.0/oai_dc/' xsi:schemaLocation='http://www.openarchives.org/OAI/2.0/oai_dc.xsd'>" +
        "<dc:title>" + title + "</dc:title>" +
        "<dc:creator>" + authorFirst + " " + authorLast + "</dc:creator>" +
        "<dc:subject>Subject1</dc:subject>" +
        "<dc:subject>Subject2</dc:subject>" +
        "<dc:issued>" + getDateTime() + "</dc:issued>" +
        "<dc:language>" + language + "</dc:language>" +
        "<dc:type>" + type + "</dc:type>" +
        "<dc:abstract>Abstract will go here</dc:abstract>" +
        "<dc:description>Description will go here</dc:description>" +
        "</dc:dc>";*/


        // Decide which to use
        //if (METS.equals("MODS")) return modsxml;
        //else if (METS.equals("DC")) return dcxml;
        //else
        return modsxml;
    }

    /**
     * This function takes a string of comma-separated items and parses them,
     * returning a ListIterator.
     *
     * @param keywords  String of comma separated terms
     * @return ListIterator to iterate over list of Strings
     */
    private ListIterator<String> parseKeywords(String keywords) {

        // Parse keywords by commas
        String[] keywordList = keywords.split(",");

        // Create an ArrayList, add keywords only if not empty strings
        ArrayList<String> keywordArray = new ArrayList<String>();

        for (int i = 0; i < keywordList.length; i++) {
            //if (!keywordList[i].isEmpty()) keywordArray.add(keywordList[i].trim());
            if (!keywordList[i].equals(new String(""))) {
                keywordArray.add(keywordList[i].trim());
            }
        }

        // Make iterator, return
        ListIterator<String> keywordIterator = keywordArray.listIterator();

        return keywordIterator;
    }

    /*
     * fileDownload from storage to upload to dspace server
     */
    private String fileDownload(String pDwlType, ActionRequest request) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String wf = request.getParameter("workflowID");
        WorkflowData t = null;
        if ("graf".equals(pDwlType)) {
            t = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getAbstactWorkflow(wf);
            if (t.getWfsID() == null) {
                ServiceType st = InformationBase.getI().getService("wfs", "portal", new Hashtable(), new Vector());
                t.setWfsID(st.getServiceUrl());
                //System.out.println("WFS:" + t.getWorkflowID() + ":" + st.getServiceUrl());
            }
            if (t.getStorageID() == null) {
                ServiceType st = InformationBase.getI().getService("storage", "portal", new Hashtable(), new Vector());
                t.setStorageID(st.getServiceUrl());
                //System.out.println("STORAGE:" + t.getWorkflowID() + ":" + st.getServiceUrl());
            }
        } else if ("abst".equals(pDwlType)) {
            t = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getTemplateWorkflow(wf);

        } else {
            t = PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflow(wf);

        }
        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("portalID", PropertyLoader.getInstance().getProperty("service.url"));
        params.put("userID", request.getRemoteUser());
        params.put("downloadType", pDwlType);
        params.put("workflowID", wf);
        params.put("wfsID", t.getWfsID());

// letoltes egyebb parameterei

        params.put("downloadType", "all");
        params.put("instanceType", "none");
        params.put("outputLogType", "all");
        params.put("exportType", "proj");

//        Enumeration<String> enm = request.getParameterNames();
//        String key;
//        while (enm.hasMoreElements()) {
//            key = enm.nextElement();
//            if (params.get(key) == null) {
//                params.put(key, request.getParameter(key));
//
//            }
//        }

        Hashtable hsh = new Hashtable();
        try {
            hsh.put("url", t.getStorageID());
        } catch (Exception e) {
        }
        ServiceType st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
        PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
        ps.setServiceURL(st.getServiceUrl());
        ps.setServiceID("/download");

        InputStream is = ps.getStream(params);
        FileOutputStream os = new FileOutputStream(getTempdir() + request.getRemoteUser() + wf + ".zip");
        // Read the input stream from storage and write out to file
        final int BUFFER_SIZE = 1024 * 4;
        byte[] buffer = new byte[BUFFER_SIZE];

        while (true) {
            int count = is.read(buffer, 0, BUFFER_SIZE);
            if (-1 == count) {
                break;

            }
            os.write(buffer, 0, count);
        }
        os.close();
        is.close();

        return getTempdir() + request.getRemoteUser() + wf + ".zip";

    }

    /**
     * Handles a workflow upload to the repository.
     *
     * This function will connect to DSpace through LNI and upload a DSpace
     * Submission Information Package (SIP) to the DSpace repository. The
     * SIP consists of a compressed workflow and metadata information
     * provided by the user (in the request).
     *
     * @param request
     */
    public void exportToDspace(ActionRequest request) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String dsusername = request.getParameter("dsemail");
        String dspass = request.getParameter("dspass");

        // Get the compressed workflow file
        // File workflow = getCompressedWorkflow(username, wfpath);
        File workflow = new File(fileDownload("real", request));

        // Create DSpace SIP (Submission Information Package)
        DSpaceSIP sip = null;
        try {
            sip = new DSpaceSIP();
            sip.addBitstream(workflow, request.getParameter("workflowID") + ".zip", "ORIGINAL", true);

            // Create METS metadata
            String xml = getMETS(request);
            //System.out.println("xml:" + xml);
            // Add metadata to SIP
            sip.addDescriptiveMD(METS, xml);

        } catch (MetsException e) {
            System.out.println(e.toString());
        }

        // Create LNI clientPropertyLoader.getInstance().getProperty("dspace.url")
        LNIclient lni = new LNIclient(getDSpaceURL() + "lni/dav/", dsusername, dspass);

        // OutputStream to write sip to
        OutputStream os;

        // This will be the handle of the new Item created in DSpace
        // Returned in form 'hdl:123456789/1'
        String newItemhandle = null;

        // Send SIP to DSpace
        try {
            os = lni.startPut(PropertyLoader.getInstance().getProperty("dspace.collection"), "METS", null);

            sip.write(os);
            os.close();

            newItemhandle = lni.finishPut();
            System.out.println("newItemhandle:" + newItemhandle);
            request.setAttribute("msg", "Upload successful!<br /><a href='" + PropertyLoader.getInstance().getProperty("dspace.url") + "handle/" + newItemhandle.substring(4) + "' target='blank'>Click here to view this item on DSpace</a>");

        } catch (Exception e) {
            e.printStackTrace();
            // Set message
            request.setAttribute("msg", "Invalid e-mail or password");

            // Note: this exception may also occur if DSpace cannot understand
            // the info in the SIP. Most likely the METS XML file is
            // incorrectly formatted.

        }

        // Delete temporary file
        workflow.delete();
    }
}

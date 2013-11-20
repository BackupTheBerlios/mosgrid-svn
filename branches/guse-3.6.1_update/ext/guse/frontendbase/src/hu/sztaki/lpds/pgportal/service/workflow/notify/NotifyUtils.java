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
package hu.sztaki.lpds.pgportal.service.workflow.notify;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import java.io.File;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * The notify portlet use this. (helper class)
 *
 * @author lpds
 */
public class NotifyUtils {
    
    private static NotifyUtils instance = null;
    
    // File separator (for example, "/")
    private String sep;
    
    private String usersPrefixDir = "";
    
    private String portalUrl = "";
    
    private Hashtable pluginList;
/**
 * Creating the constructor singleton instance
 */
    public NotifyUtils() {
        if (instance == null) {
            instance = this;
        }
        sep = System.getProperty("file.separator");
        usersPrefixDir = "" + PropertyLoader.getInstance().getProperty("portal.prefix.dir") + sep + "users";
        // set and prepare portalid, portalURL...
        portalUrl = PropertyLoader.getInstance().getProperty("service.url");
        // portalUrl = portalUrl.replaceAll("http:/", "https:/");
        portalUrl = portalUrl.replaceAll("/portal30", "/gridsphere/gridsphere");
        // set plugin list
        pluginList = new Hashtable();
        getPluginList().put("plugin_001", Notify.plugin_email_name);
    }
    
    /**
     * Returns the NotifyUtils instance.
     *
     * @return
     */
    public static NotifyUtils getInstance() {
        if (instance == null) {
            instance = new NotifyUtils();
        }
        return instance;
    }
    
    /**
     * Returns the file separator value.
     *
     * @return
     */
    public String getSeparator() {
        return sep;
    }
    
    /**
     * Returns the portal path URL.
     *
     * (https://host:port/gridsphere/gridsphere)
     *
     * @return portalUrl
     */
    public String getPortalUrl() {
        return portalUrl;
    }
    
    /**
     * Returns the list of the present plugins.
     * @return plugin list
     */
    public Hashtable getPluginList() {
        return pluginList;
    }
    
    /**
     * Returns the content of a file in a string. (every line)
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    private String getFileAllLineValue(String filePath) throws Exception {
        return FileUtils.getInstance().getFileAllLineValue(filePath);
    }
    
    /**
     * Creates the given file with the content from the parameter.
     *
     * @param f - file
     * @param value - content
     * @throws Exception
     */
    private void createFile(File f, String value) throws Exception {
        FileUtils.getInstance().createFile(f, value);
    }
    
    /**
     * Returns the path of the file which 
     * contains the configuration data of the user.
     *
     * (full path)
     *
     * @param userID - user name
     * @return String - file path
     * @throws Exception
     */
    private String getConfigFilePath(String userID) throws Exception {
        String filePath = usersPrefixDir + sep + userID + sep + ".notify.xml";
        // System.out.println("filePath : " + filePath);
        return filePath;
    }
    
    /**
     * Saves the user's notify data.
     *
     * @param userID - user name - user ID
     * @param  notifyBean - user notify data
     * @throws Exception
     */
    public void saveNotifyInformations(String userID, NotifyBean notifyBean) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element enotify = doc.createElement("notify");
        Enumeration enumKeys = notifyBean.getData().keys();
        while (enumKeys.hasMoreElements()) {
            String key = (String) enumKeys.nextElement();
            String value = (String) notifyBean.getValue(key);
            Element eprop = doc.createElement("prop");
            eprop.setAttribute("key", key);
            eprop.setAttribute("value", value);
            enotify.appendChild(eprop);
        }
        doc.appendChild(enotify);
        createFile(new File(getConfigFilePath(userID)), transformDocToString(doc));
    }
    
    /**
     * Creates a string from a .doc file.
     *
     * (used before saving)
     *
     * @param Document doc - document
     * @return doc descriptor string
     * @throws Exception
     */
    private String transformDocToString(Document doc) throws Exception {
        // Generate XML output to string
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        // We want to pretty format the XML output...
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        // transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        // transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        DOMSource source = new DOMSource(doc);
        // doc to string
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        transformer.transform(source, streamResult);
        String xmlstr = stringWriter.toString();
        // System.out.println("xmlstr : " + xmlstr);
        return xmlstr;
    }
    
    /**
     * Loads the user's notify data.
     *
     * @param userID - user name - user ID
     * @return NotifyBean notifyBean - user notify data
     * @throws Exception
     */
    public NotifyBean loadNotifyInformations(String userID) throws Exception {
        NotifyBean notifyBean = new NotifyBean();
        File notifyFile = new File(getConfigFilePath(userID));
        if (notifyFile.exists()) {
            String fileValue = "" + getFileAllLineValue(notifyFile.getAbsolutePath());
            // System.out.println("fileValue : " + fileValue);
            // parse information from file...
            // Build document doc
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            // Build document from xml file
            // Document doc = builder.parse(new FileInputStream("/home/username/1/1.xml"));
            // Build document from workflowXML string
            Document doc = builder.parse(new InputSource(new StringReader(fileValue)));
            Element enotify = doc.getDocumentElement();
            NodeList propList = enotify.getChildNodes();
            if (propList.getLength() > 0) {
                for (int iNode = 0; iNode < propList.getLength(); iNode++) {
                    Node propObj = propList.item(iNode);
                    if (propObj instanceof Element) {
                        Element eprop = (Element) propObj;
                        // System.out.println("Element name: " + eprop.getNodeName());
                        if (eprop.getNodeName().equalsIgnoreCase("prop")) {
                            String key = eprop.getAttribute("key");
                            String value = eprop.getAttribute("value");
                            notifyBean.setValue(key, value);
                        }
                    }
                }
            } else {
                // delete wrong xml file...
                // System.out.println("delete wrong, not valid notify file : (" + notifyFile.getAbsolutePath() + ")...");
                notifyFile.delete();
            }
        } else {
            // throw new Exception("Not found notify file !!!");
        }
        return notifyBean;
    }
    
    /**
     * The current time on the server,
     * return value in string.
     *
     * @return server time stamp (e.g.: 2008-01-31 11:08)
     */
    public String getNowDateTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(Calendar.getInstance().getTime());
    }
    
    /**
     * The current time on the server,
     * return value in string.
     * 
     * (with seconds)
     * @return server time stamp with seconds(e.g.: 2008-01-31 11:08:33)
     */
    public String getNowDateTimeStampWithSeconds() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(Calendar.getInstance().getTime());
    }
    
}

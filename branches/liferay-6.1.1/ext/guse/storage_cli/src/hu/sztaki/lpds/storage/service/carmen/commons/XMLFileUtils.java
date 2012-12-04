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

import java.io.File;
import java.io.StringReader;
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
 * Saves and loads xml files. (helper class)
 *
 * @author lpds
 */
public class XMLFileUtils {
    
    private static XMLFileUtils instance = null;
/**
 * Constructor
 */
    public XMLFileUtils() {
        if (instance == null) {
            instance = this;
        }
        //
    }
    
    /**
     * Returns a XMLFileUtils instance.
     *
     * @return
     */
    public static XMLFileUtils getInstance() {
        if (instance == null) {
            instance = new XMLFileUtils();
        }
        return instance;
    }
    
    /**
     * Saves the data.
     *
     * @param xmlFilePath - full path of the xml file
     * @param dataHash - hashtable containing the data
     * @throws Exception
     */
    public void saveDataHashToXMLFile(String xmlFilePath, Hashtable dataHash) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element edata = doc.createElement("data");
        Enumeration enumKeys = dataHash.keys();
        while (enumKeys.hasMoreElements()) {
            String key = (String) enumKeys.nextElement();
            String value = (String) dataHash.get(key);
            Element eprop = doc.createElement("prop");
            eprop.setAttribute("key", key);
            eprop.setAttribute("value", value);
            edata.appendChild(eprop);
        }
        doc.appendChild(edata);
        File xmlFile = new File(xmlFilePath);
        FileUtils.getInstance().createFile(xmlFile, transformDocToString(doc));
    }
    
    /**
     * Creates a string from a document.
     *
     * (used before saving)
     *
     * @param Document doc - document
     * @return document descriptor string
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
     * Loads the data.
     *
     * @param xmlFilePath - full path of the xml file
     * @return Hashtable - content of the xml file
     * @throws Exception
     */
    public Hashtable loadDataHashFromXMLFile(String xmlFilePath) throws Exception {
        Hashtable retDataHash = new Hashtable();
        File xmlFile = new File(xmlFilePath);
        if (xmlFile.exists()) {
            String fileValue = "" + FileUtils.getInstance().getFileAllLineValue(xmlFile.getAbsolutePath());
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
                            retDataHash.put(key, value);
                        }
                    }
                }
            } else {
                // delete wrong xml file...
                // System.out.println("delete wrong, not valid xml file : (" + xmlFile.getAbsolutePath() + ")...");
                // xmlFile.delete();
            }
        } else {
            // throw new Exception("Not found notify file !!!");
        }
        return retDataHash;
    }
    
}

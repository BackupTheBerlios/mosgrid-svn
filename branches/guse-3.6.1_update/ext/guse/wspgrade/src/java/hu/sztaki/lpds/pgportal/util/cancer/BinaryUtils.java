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
package hu.sztaki.lpds.pgportal.util.cancer;

import java.util.Vector;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import java.io.StringReader;
import java.util.Hashtable;
import java.io.File;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Betolti a konfiguracios file-bol (cancergrid.xml)
 * az adatokat. (helper class)
 *
 * @author lpds
 */
public class BinaryUtils {
    
    private static BinaryUtils instance = null;
    
    private String prefix = null;
    
    private String defaultXmlName = "cancergrid";
    
    // xml file lastModify long number
    private long oldValue = 0;
    
    // vector from last xml file
    private Vector oldVector = null;
    
    public BinaryUtils() {
        if (instance == null) {
            instance = this;
        }
        //
        oldVector = new Vector();
        prefix = PropertyLoader.getInstance().getProperty("prefix.dir");
    }
    
    /**
     * BinaryUtils peldanyt ad vissza.
     *
     * @return
     */
    public static BinaryUtils getInstance() {
        if (instance == null) {
            instance = new BinaryUtils();
        }
        return instance;
    }
    
    /**
     * A cancergrid.xml file bol beolvassa
     *
     * (a visszateresi ertek egy vector)
     *
     * (a vectorban n darab hashtabe van)
     *
     * (minden hashtable tartalmaze egy
     * "binary" es egy "desc" kulcsot)
     *
     * @return vector - a vectorban hashtable-k vannak
     */
    public Vector getList() {
        return getList("");
    }
    
    /**
     * A cancergrid_type.xml file bol beolvassa
     *
     * (a visszateresi ertek egy vector)
     *
     * (a vectorban n darab hashtabe van)
     *
     * (minden hashtable tartalmaze egy
     * "binary" es egy "desc" kulcsot)
     *
     * @param String type - az xml neveben szereplo tipus nev
     * @return vector - a vectorban hashtable-k vannak
     */
    public Vector getList(String type) {
        if (type == null) {
            type = new String("");
        }
        String fileName = defaultXmlName;
        if (!"".equals(type)) {
            fileName += "_" + type;
        }
        fileName += ".xml";
        String xmlFilePath = new String(prefix + fileName);
        // System.out.println("xmlFilePath : " + xmlFilePath);
        return returnList(xmlFilePath);
    }
    
    /**
     * Pelada a tomcat / temp dir ben talalhato xml file-ra.
     *
     * <root>
     *    <prop binary="binary1" desc="desc aaaaaaaaa 1"/>
     *    <prop binary="binary1" desc="desc bbbbbbbbb 2"/>
     * </root>
     *
     */
    private Vector returnList(String filePath) {
        Vector retVector = new Vector();
        // System.out.println("oldValue  : " + oldValue);
        // System.out.println("oldVector : " + oldVector);
        try {
            File xmlFile = new File(filePath);
            //
            long newValue = xmlFile.lastModified();
            // System.out.println("newValue  : " + newValue);
            if (oldValue == newValue) {
                // System.out.println("return from memory...");
                retVector = oldVector;
            } else {
                // System.out.println("return from disk...");
                if (xmlFile.exists()) {
                    String fileValue = FileUtils.getInstance().getFileAllLineValue(xmlFile.getAbsolutePath());
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
                                    String binary = eprop.getAttribute("binary");
                                    String desc = eprop.getAttribute("desc");
                                    // System.out.println("binary : " + binary + ", " + "desc : " + desc);
                                    Hashtable hash = new Hashtable();
                                    hash.put("binary", binary);
                                    hash.put("desc", desc);
                                    retVector.addElement(hash);
                                }
                            }
                        }
                    }
                    oldValue = newValue;
                    oldVector = retVector;
                    // System.out.println("newVector : " + retVector);
                }
            } // old new value
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("retVector : " + retVector);
        return retVector;
    }
    
}

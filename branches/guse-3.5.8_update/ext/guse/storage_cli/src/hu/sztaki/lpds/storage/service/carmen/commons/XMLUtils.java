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

import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;
import java.io.StringReader;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * XML handling and processing. (helper class)
 *
 * @author lpds
 */
public class XMLUtils {
    
    private static XMLUtils instance = null;
/**
 * Constructor
 */
    public XMLUtils() {
        if (instance == null) {
            instance = this;
        }
    }
    
    /**
     * Returns an XMLUtils instance.
     *
     * @return static instance
     */
    public static XMLUtils getInstance() {
        if (instance == null) {
            instance = new XMLUtils();
        }
        return instance;
    }
/**
 * Checking the workflow list
 * @param eworkflowlist list to be checked
 * @return result of the checking
 * @throws java.lang.Exception any type of unexpected error (xml)
 */

    public boolean validateWorkflowList(Element eworkflowlist) throws Exception {
        boolean validList = true;
        //
        if (eworkflowlist != null) {
            NodeList workflowList = eworkflowlist.getChildNodes();
            if (workflowList.getLength() > 0) {
                // parse workflow list
                for (int wfNode = 0; wfNode < workflowList.getLength(); wfNode++) {
                    Node obj = workflowList.item(wfNode);
                    if (obj instanceof Element) {
                        Element eWorkflow = (Element) obj;
                        // System.out.println("Element name: " + eWorkflow.getNodeName());
                        String type = eWorkflow.getNodeName();
                        if (type.equalsIgnoreCase("graf")) {
                            String name = eWorkflow.getAttribute("name");
                            if ((name == null) || ("".equals(name))) {
                                validList = false;
                            }
                        }
                        if ((type.equalsIgnoreCase("real")) || (type.equalsIgnoreCase("abst"))) {
                            String name = eWorkflow.getAttribute("name");
                            if ((name == null) || ("".equals(name))) {
                                validList = false;
                            }
                            name = eWorkflow.getAttribute("graf");
                            if ((name == null) || ("".equals(name))) {
                                validList = false;
                            }
                        }
                    }
                }
            } else {
                validList = false;
            }
        } else {
            validList = false;
        }
        // System.out.println("validList : " + validList);
        if (!validList) {
            throw new Exception("Not valid workflow name or not exist workflow !!!");
        }
        return validList;
    }

    /**
     * Returns the names and the texts of 
     * a workflow from the eworkflowlist (received 
     * in the parameters) in a hashtable.
     *
     * @param type workflow type
     * @param  eworkflowlist - workflow XML root element
     * @param   bean - during application import the workflows needed to be renamed,
     *                the bean.getImportID() string will go to the end of the file name
     *                and it is set only for application import.
     * @throws Exception process error
     * @return workflow names and descriptions hash
     */
    public Hashtable getWorkflowNameAndTextFromElement(Element eworkflowlist, String type, StorageWorkflowNamesBean bean) throws Exception {
        Hashtable retHash = new Hashtable();
        if (eworkflowlist != null) {
            NodeList workflowList = eworkflowlist.getChildNodes();
            if (workflowList.getLength() > 0) {
                // parse workflows
                // System.out.println("parse workflows...");
                for (int wfNode = 0; wfNode < workflowList.getLength(); wfNode++) {
                    Node obj = workflowList.item(wfNode);
                    if (obj instanceof Element) {
                        Element eWorkflow = (Element) obj;
                        // System.out.println("Element name: " + eWorkflow.getNodeName());
                        if (eWorkflow.getNodeName().equalsIgnoreCase(type)) {
                            String name = eWorkflow.getAttribute("name");
                            if (bean != null) {
                                // in case of download it is null
                                // in case of import it is bean
                                //if (bean.isAppl() || bean.isProj()) {
                                    name = name + bean.getImportID();
                                //}
                            }
                            String text = eWorkflow.getAttribute("text");
                            retHash.put(name, text);
                        }
                    }
                }
            }
        } else {
            throw new Exception("Not valid eworkflowlist !!! eworkflowlist = (" + eworkflowlist + ")");
        }
        // System.out.println("retHash : " + retHash);
        return retHash;
    }
    
    /**
     * Returns the mainworkflow element from the 
     * workflowXML string received in the parameters.
     *
     * @param workflowXML - workflow XML descriptor string
     * @return workflow descriptor element  - eworkflowlist (eroot)
     * @throws Exception
     */
    public Element getElementWorkflowListFromXML(String workflowXML) throws Exception {
        if ((workflowXML != null) && (!"".equals(workflowXML))) {
            // Build document from workflowXML string
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(workflowXML)));
            // Element eroot = doc.getDocumentElement();
            return doc.getDocumentElement();
        } else {
            throw new Exception("Not valid workflowXML !!! workflowXML = (" + workflowXML + ")");
        }
    }
    
}

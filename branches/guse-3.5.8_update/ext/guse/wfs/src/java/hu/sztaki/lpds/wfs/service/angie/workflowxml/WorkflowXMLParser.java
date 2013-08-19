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
package hu.sztaki.lpds.wfs.service.angie.workflowxml;

import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Feldolgoz, ertelmez es adatbazisban letarol workflow-kat.
 *
 * (A workflow leiro xml-t stringben kapja.)
 *
 * (Az xml egy main graf egy main real (konkret) es
 * egy main abstract workflow-t tartalmaz,
 * ezen felul tartalmazhat
 * x darab embed graf, abst es
 * real workflow-t is.)
 *
 * @author lpds
 */
public class WorkflowXMLParser {

    private WorkflowXMLService workflowXMLService;
    private ParserUtils parserUtils;

    public WorkflowXMLParser() throws Exception {
        this.workflowXMLService = new WorkflowXMLServiceImpl();
        this.parserUtils = new ParserUtils(workflowXMLService);
    }

    /**
     * A parameterekben megadott azonositok alapjan eltarolja a workflow-t.
     *
     * @param StorageWorkflowNamesBean bean
     *      - storageID storage azonosito
     *      - portalID portal azonosito
     *      - userID felhasznalo azonosito
     *      - workflowXML a workflow leiro xml string
     *      - newMainGrafName - a feltoltott main graph, graf workflow ezt a nevet kapja
     *      - newMainAbstName - a feltoltott main template, abstract, abst workflow ezt a nevet kapja
     *      - newMainRealName - a feltoltott main cocrete, real, konkret workflow ezt a nevet kapja
     *      - uploadID - az runtimeID vegehez lessz hozzafuzve
     *      - downloadType - a letoltes tipusa
     *      - exportType - a letoltes tipusa (embed details)
     * @return ures string ha a workflow ertelmezese es a mentese sikeres
     *         hibauzenet string ha nem sikerult vagy nem volt mit menteni
     */
    public String parseXMLStr(StorageWorkflowNamesBean bean) throws Exception {
        try {
            String retStr = realParseXMLStr(bean);
            // mindig meg kell hivni...
            workflowXMLService.closeConnection();
            return retStr;
        } catch (Exception e) {
            // mindig meg kell hivni...
            workflowXMLService.closeConnection();
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * A parameterekben megadott azonositok alapjan eltarolja a workflow-t.
     *
     * @param StorageWorkflowNamesBean bean
     *      - storageID storage azonosito
     *      - portalID portal azonosito
     *      - userID felhasznalo azonosito
     *      - workflowXML a workflow leiro xml string
     *      - newMainGrafName - a feltoltott main graph, graf workflow ezt a nevet kapja
     *      - newMainAbstName - a feltoltott main template, abstract, abst workflow ezt a nevet kapja
     *      - newMainRealName - a feltoltott main cocrete, real, konkret workflow ezt a nevet kapja
     *      - uploadID - az runtimeID vegehez lessz hozzafuzve
     *      - downloadType - a letoltes tipusa
     *      - exportType - a letoltes tipusa (embed details)
     * @return ures string ha a workflow ertelmezese es a mentese sikeres
     *         hibauzenet string ha nem sikerult vagy nem volt mit menteni
     */
    private String realParseXMLStr(StorageWorkflowNamesBean bean) throws Exception {
        // System.out.println("start parseXMLStr...");
        if ((!"".equals(bean.getNewMainAbstName())) && (!"".equals(bean.getNewMainRealName()))) {
            if (bean.getNewMainAbstName().equalsIgnoreCase(bean.getNewMainRealName())) {
                throw new Exception("Template workflow name equal concrete workflow name");
            }
        }
        // Build document doc
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        // Build document from xml file
        // Document doc = builder.parse(new FileInputStream("/home/username/1/1.xml"));
        // Build document from workflowXML string
        Document doc = builder.parse(new InputSource(new StringReader(bean.getWorkflowXML())));
        Element eroot = doc.getDocumentElement();
        NodeList workflowList = eroot.getChildNodes();
        Vector grafVector = new Vector();
        Vector abstVector = new Vector();
        Vector realVector = new Vector();
        if (workflowList.getLength() > 0) {
            // parse workflows
            // System.out.println("parse workflows...");
            for (int wfNode = 0; wfNode < workflowList.getLength(); wfNode++) {
                Node obj = workflowList.item(wfNode);
                if (obj instanceof Element) {
                    Element eNode = (Element) obj;
                    // System.out.println("Element name: " + eNode.getNodeName());
                    if (eNode.getNodeName().equalsIgnoreCase("graf")) {
                        grafVector.addElement(eNode);
                    }
                    if (eNode.getNodeName().equalsIgnoreCase("abst")) {
                        abstVector.addElement(eNode);
                    }
                    if (eNode.getNodeName().equalsIgnoreCase("real")) {
                        realVector.addElement(eNode);
                    }
                }
            }
        }
        // System.out.println("grafVector : " + grafVector);
        // System.out.println("abstVector : " + abstVector);
        // System.out.println("realVector : " + realVector);
        // parse graf, graph vector
        //
        Hashtable idCacheHash = new Hashtable();
        //
        for (Object elem : grafVector) {
            Hashtable idHash = parserUtils.parseGrafWorkflowXML(bean, (Element) elem);
            idCacheHash.putAll(idHash);
        }
        //
        // System.out.println("idCacheHash: " + idCacheHash);
        //
        // parse abst, template vector
        for (Object elem : abstVector) {
            parserUtils.parseAbstWorkflowXML(bean, (Element) elem, idCacheHash);
        }
        // parse real, concrete vector (and real instances)
        for (Object elem : realVector) {
            parserUtils.parseRealWorkflowXML(bean, (Element) elem, idCacheHash);
        }
        return new String("");
    }
}

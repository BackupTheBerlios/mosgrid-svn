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

import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.HistoryBean;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;
import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;
import hu.sztaki.lpds.wfs.net.wsaxis13.WfsPortalServiceImpl;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A WorkflowXMLBuilder-t szolgalja ki. (helper class)
 *
 * A tenyleges xml element osszeallitast vegzi.
 *
 * @author lpds
 */
public class BuilderUtils {
    
    private WorkflowXMLService workflowXMLService;

    private WfsPortalServiceImpl wfsPortalService;
    
    public BuilderUtils(WorkflowXMLService workflowXMLService, WfsPortalServiceImpl wfsPortalService) throws Exception {
        this.workflowXMLService = workflowXMLService;
        this.wfsPortalService = wfsPortalService;
    }
    
    /**
     * Egy graph, graf workflow leiro element objektumot
     * allit elo majd a listahoz adja.
     *
     * @param Element eworkflowlist - ehhez a listahoz adja hozza
     * @param Document doc parent document
     * @param StorageWorkflowNamesBean bean - leiro parameterek portalid, userid
     * @param grafName - a workflow neve
     */
    public void buildGraf(Element eworkflowlist, Document doc, StorageWorkflowNamesBean bean, String grafName) throws Exception {
        if (!"".equals(grafName)) {
            // build <graf>
            Element egraf = getGrafWorkflowElement(doc, bean.getPortalID(), bean.getUserID(), grafName);
            if (egraf != null) {
                eworkflowlist.appendChild(egraf);
            }
        }
    }
    
    /**
     * Egy konkret, real workflow leiro element objektumot
     * allit elo majd a listahoz adja.
     *
     * @param Element eworkflowlist - ehhez a listahoz adja hozza
     * @param Document doc parent document
     * @param StorageWorkflowNamesBean bean - leiro parameterek portalid, userid
     * @param realName - a workflow neve
     * @param grafName - a graf workflow neve
     * @param abstName - az abst workflow neve
     */
    public void buildReal(Element eworkflowlist, Document doc, StorageWorkflowNamesBean bean, String realName, String grafName, String abstName) throws Exception {
        if (!"".equals(realName)) {
            // build <real>
            // System.out.println("realName : " + realName);
            // if ((realName == null) || ("".equals(realName))) {
            //    throw new Exception("Not valid real workflow name ! realName = (" + realName + ")");
            // }
            // create com data bean
            ComDataBean comDataBean = new ComDataBean();
            comDataBean.setPortalID(bean.getPortalID());
            comDataBean.setUserID(bean.getUserID());
            comDataBean.setWorkflowID(realName);
            comDataBean.setGraf(grafName);
            comDataBean.setParentWorkflowID(abstName);
            // get real workflow text
            ResultSet realResultSet = workflowXMLService.getWorkflowData(bean.getPortalID(), bean.getUserID(), realName);
            String realText = new String("");
            if (realResultSet.next()) {
                realText = realResultSet.getString("txt");
            }
            comDataBean.setTxt(realText);
            Element ereal = getRealOrAbstractWorkflowElement(doc, "real", comDataBean, bean);
            // instances begin
            boolean buildInstances = false;
            if ((bean.isAll()) || (bean.isReal())) {
                if ((bean.isInstanceAll()) || (bean.isInstanceOne())) {
                    buildInstances = true;
                }
            }
            // System.out.println("realName       :" + realName);
            // System.out.println("buildInstances :" + buildInstances);
            if (buildInstances) {
                // build <instances>
                // get real workflow id
                ResultSet realResultSet2 = workflowXMLService.getWorkflowData(bean.getPortalID(), bean.getUserID(), realName);
                String realWorkflowID = new String("");
                if (realResultSet2.next()) {
                    realWorkflowID = realResultSet2.getString("id");
                }
                Element einstances = getInstancesElement(doc, realWorkflowID, bean.getInstanceType(), bean.isInstanceOne());
                if (einstances.getChildNodes().getLength() > 0) {
                    ereal.appendChild(einstances);
                }
            }
            // instances end
            if (ereal != null) {
                eworkflowlist.appendChild(ereal);
            }
        }
    }
    
    /**
     * Egy abstract, abst workflow leiro element objektumot
     * allit elo majd a listahoz adja.
     *
     * @param Element eworkflowlist - ehhez a listahoz adja hozza
     * @param Document doc parent document
     * @param StorageWorkflowNamesBean bean - leiro parameterek portalid, userid
     * @param abstName - a workflow neve
     * @param grafName - a graf workflow neve
     */
    public void buildAbst(Element eworkflowlist, Document doc, StorageWorkflowNamesBean bean, String abstName, String grafName) throws Exception {
        if (!"".equals(abstName)) {
            // build <abstract>
            // System.out.println("abstName : " + abstName);
            String abstText = new String("");
            // create com data bean
            ComDataBean comDataBean = new ComDataBean();
            comDataBean.setPortalID(bean.getPortalID());
            comDataBean.setUserID(bean.getUserID());
            comDataBean.setWorkflowID(abstName);
            comDataBean.setGraf(grafName);
            // get abstract workflow text
            ResultSet abstResultSet = workflowXMLService.getWorkflowData(bean.getPortalID(), bean.getUserID(), abstName);
            // if (abstResultSet == null) {
            //     throw new Exception("Not valid abstract workflow name ! abstName = (" + abstName + ")");
            // }
            if (abstResultSet.next()) {
                abstText = abstResultSet.getString("txt");
            }
            comDataBean.setTxt(abstText);
            Element eabst = getRealOrAbstractWorkflowElement(doc, "abst", comDataBean, bean);
            if (eabst != null) {
                eworkflowlist.appendChild(eabst);
            }
        }
    }
    
    /**
     * A workflowlist element objektumbol keszit egy stringet.
     *
     * @param Document doc parent document
     * @param Element eworkflowlist
     * @return workflow xml leiro string
     */
    public String transformWorkflowListToString(Document doc, Element eworkflowlist) throws Exception {
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
    
    /**
     * Visszaad egy graf doc elementet
     *
     * @param Document doc parent document
     * @param portalid portal azonosito
     * @param userid felhasznalo azonosito
     * @param grafName aworkflow neve
     * @return Element graf workflow xml
     */
    private Element getGrafWorkflowElement(Document doc, String portalID, String userID, String grafName) throws Exception {
        Element egraf = doc.createElement("graf");
        ResultSet rsWorkflow = workflowXMLService.getAWorkflow(portalID, userID, grafName);
        if (rsWorkflow.next()) {
            String inpJobId = "";
            String outJobId = "";
            String aworkflowIdStr = rsWorkflow.getString("id");
            Long aworkflowId = new Long(aworkflowIdStr);
            String aworkflowText = rsWorkflow.getString("txt");
            ResultSet rsAllJob = workflowXMLService.getAJob(aworkflowId.longValue());
            ResultSet rsAllInp = workflowXMLService.getAInput(aworkflowId.longValue());
            ResultSet rsAllOut = workflowXMLService.getAOutput(aworkflowId.longValue());
            inpJobId = getNextPortJobId(rsAllInp);
            outJobId = getNextPortJobId(rsAllOut);
            // egraf.setAttribute("id", aworkflowIdStr);
            egraf.setAttribute("name", grafName);
            egraf.setAttribute("text", aworkflowText);
            if (rsAllJob != null) {
                try {
                    // add jobs XML elements
                    while (rsAllJob.next()) {
                        Element eJob = doc.createElement("job");
                        try {
                            String ajobIdStr = rsAllJob.getString("id");
                            // Long ajobId = new Long(ajobIdStr);
                            // ***************************************************
                            // job name
                            // eJob.setAttribute("id", ajobIdStr);
                            eJob.setAttribute("name", rsAllJob.getString("name"));
                            eJob.setAttribute("text", rsAllJob.getString("txt"));
                            eJob.setAttribute("x", rsAllJob.getString("x"));
                            eJob.setAttribute("y", rsAllJob.getString("y"));
                            // ***************************************************
                            // ports (input, output)
                            // ***************************************************
                            Element ePort = null;
                            // get input ports
                            while (ajobIdStr.equals(inpJobId)) {
                                ePort = doc.createElement("input");
                                // ePort.setAttribute("id", rsAllInp.getString("id"));
                                ePort.setAttribute("name", rsAllInp.getString("name"));
                                ePort.setAttribute("prejob", rsAllInp.getString("prejob"));
                                ePort.setAttribute("preoutput", rsAllInp.getString("preoutput"));
                                ePort.setAttribute("seq", rsAllInp.getString("seq"));
                                ePort.setAttribute("text", rsAllInp.getString("txt"));
                                ePort.setAttribute("x", rsAllInp.getString("x"));
                                ePort.setAttribute("y", rsAllInp.getString("y"));
                                eJob.appendChild(ePort);
                                inpJobId = getNextPortJobId(rsAllInp);
                            }
                            // get output ports
                            while (ajobIdStr.equals(outJobId)) {
                                ePort = doc.createElement("output");
                                // ePort.setAttribute("id", rsAllOut.getString("id"));
                                ePort.setAttribute("name", rsAllOut.getString("name"));
                                ePort.setAttribute("seq", rsAllOut.getString("seq"));
                                ePort.setAttribute("text", rsAllOut.getString("txt"));
                                ePort.setAttribute("x", rsAllOut.getString("x"));
                                ePort.setAttribute("y", rsAllOut.getString("y"));
                                eJob.appendChild(ePort);
                                outJobId = getNextPortJobId(rsAllOut);
                            }
                        } catch (Exception e) {
                            throw new Exception(e);
                        }
                        egraf.appendChild(eJob);
                    }
                    return egraf;
                } catch (Exception e) {
                    throw new Exception(e);
                }
            }
        }
        return egraf;
    }
    
    /**
     * Visszaad egy
     * abstract elementname = "abstract"
     * vagy egy
     * real element = "real" doc elementet.
     *
     * @param Document doc parent document
     * @param elementname element tag name
     * @param comDataBean workflow adatai
     * @param StorageWorkflowNamesBean bean workflow adatai
     * @return Element abstract workflow xml
     */
    private Element getRealOrAbstractWorkflowElement(Document doc, String elementname, ComDataBean comDataBean, StorageWorkflowNamesBean bean) throws Exception {
        Vector jobList = wfsPortalService.getWorkflowConfigData(comDataBean);
        if (jobList == null) {
            throw new Exception("Not valid jobList !");
        }
        return getXMLElementFromVector(elementname, doc, comDataBean, jobList, bean);
    }
    
    /**
     * XMLElement-et allit elo vector-bol
     * (a vector-ban JobPropertyBean-ek vannak)
     */
    private Element getXMLElementFromVector(String elementName, Document doc, ComDataBean comDataBean, Vector jobList, StorageWorkflowNamesBean bean) throws Exception {
        Element element = doc.createElement(elementName);
        element.setAttribute("name", comDataBean.getWorkflowID());
        element.setAttribute("text", comDataBean.getTxt());
        element.setAttribute("graf", comDataBean.getGraf());
        if ("real".equals(elementName)) {
            element.setAttribute("abst", comDataBean.getParentWorkflowID());
        }
        // parse jobList
        for (int jobPos = 0; jobPos < jobList.size(); jobPos++) {
            JobPropertyBean job = (JobPropertyBean) jobList.get(jobPos);
            Element eJob = doc.createElement("job");
            // eJob.setAttribute("id", Long.toString(job.getId()));
            eJob.setAttribute("name", job.getName());
            eJob.setAttribute("text", job.getTxt());
            eJob.setAttribute("x", Long.toString(job.getX()));
            eJob.setAttribute("y", Long.toString(job.getY()));
            // parse inputList
            Vector inputsList = job.getInputs();
            for (int portPos = 0; portPos < inputsList.size(); portPos++) {
                PortDataBean port = (PortDataBean) inputsList.get(portPos);
                Element ePort = doc.createElement("input");
                // ePort.setAttribute("id", Long.toString(port.getId()));
                ePort.setAttribute("name", port.getName());
                ePort.setAttribute("text", port.getTxt());
                ePort.setAttribute("seq", Long.toString(port.getSeq()));
                ePort.setAttribute("prejob", port.getPrejob());
                ePort.setAttribute("preoutput", port.getPreoutput());
                ePort.setAttribute("x", Long.toString(port.getX()));
                ePort.setAttribute("y", Long.toString(port.getY()));
                // parse port properties
                parsePortProperties(doc, ePort, port);
                eJob.appendChild(ePort);
            }
            // parse outputList
            Vector outputsList = job.getOutputs();
            for (int portPos = 0; portPos < outputsList.size(); portPos++) {
                PortDataBean port = (PortDataBean) outputsList.get(portPos);
                Element ePort = doc.createElement("output");
                // ePort.setAttribute("id", Long.toString(port.getId()));
                ePort.setAttribute("name", port.getName());
                ePort.setAttribute("text", port.getTxt());
                ePort.setAttribute("seq", Long.toString(port.getSeq()));
                // ePort.setAttribute("prejob", port.getPrejob());
                // ePort.setAttribute("preoutput", port.getPreoutput());
                ePort.setAttribute("x", Long.toString(port.getX()));
                ePort.setAttribute("y", Long.toString(port.getY()));
                // parse port properties
                parsePortProperties(doc, ePort, port);
                eJob.appendChild(ePort);
            }
            // parse executeList
            Iterator executeList = job.getExe().keySet().iterator();
            while (executeList.hasNext()) {
                String key = (String) executeList.next();
                String value = getStr(job.getExe().get(key));
                String label = getStr(job.getLabel().get(key));
                String desc = getStr(job.getDesc0().get(key));
                String inh = getStr(job.getInherited().get(key));
                Element eExe = doc.createElement("execute");
                eExe.setAttribute("key", key);
                eExe.setAttribute("value", value);
                eExe.setAttribute("label", label);
                eExe.setAttribute("desc", desc);
                eExe.setAttribute("inh", inh);
                eJob.appendChild(eExe);
            }
            // set module bit true
            if (bean.isAppl()) {
                Element eExe = doc.createElement("execute");
                eExe.setAttribute("key", "module");
                eExe.setAttribute("value", "true");
                eJob.appendChild(eExe);
            }
            // parse descList
            Iterator descList = job.getDesc().keySet().iterator();
            while (descList.hasNext()) {
                String key = "" + descList.next();
                String value = "" + job.getDesc().get(key);
                Element eDesc = doc.createElement("description");
                eDesc.setAttribute("key", key);
                eDesc.setAttribute("value", value);
                eJob.appendChild(eDesc);
            }
            // parse historyList
            Vector historyList = job.getHistory();
            for (int histPos = 0; histPos < historyList.size(); histPos++) {
                HistoryBean hist = (HistoryBean) historyList.get(histPos);
                Element eHist = doc.createElement("history");
                eHist.setAttribute("tim", hist.getTim());
                eHist.setAttribute("port", hist.getPort());
                eHist.setAttribute("user", hist.getUser());
                eHist.setAttribute("mdyid", hist.getMdyid());
                eHist.setAttribute("ovalue", hist.getOvalue());
                eHist.setAttribute("nvalue", hist.getNvalue());
                eJob.appendChild(eHist);
            }
            // add job to element
            element.appendChild(eJob);
        }
        return element;
    }
    
    /**
     * Egy port data bean-t jar be es eloallitja
     * az xml-hez a szukseges elementeket.
     * pl: data, label, desc es inh
     */
    private void parsePortProperties(Document doc, Element ePort, PortDataBean port) throws Exception {
        // parse port properties (data)
        Iterator portPropList = port.getData().keySet().iterator();
        while (portPropList.hasNext()) {
            String key = (String) portPropList.next();
            String value = getStr(port.getData().get(key));
            String label = getStr(port.getLabel().get(key));
            String desc = getStr(port.getDesc().get(key));
            String inh = getStr(port.getInherited().get(key));
            Element ePortProp = doc.createElement("port_prop");
            ePortProp.setAttribute("key", key);
            ePortProp.setAttribute("value", value);
            ePortProp.setAttribute("label", label);
            ePortProp.setAttribute("desc", desc);
            ePortProp.setAttribute("inh", inh);
            ePort.appendChild(ePortProp);
        }
    }
    
    /**
     * String helper.
     */
    private String getStr(Object str) throws Exception {
        if (str == null) {
            return "";
        } else {
            return (String) str;
        }
    }
    
    /**
     * Lepteti a resultSet-et es visszaadja a következö porthoz tartozo jobid-t
     * @return String
     * @throws Exception
     */
    private String getNextPortJobId(ResultSet rs) throws Exception {
        if (rs.next()) {
            return rs.getString("id_ajob");
        }
        return new String("");
    }
    
    /**
     * Visszaad egy instances doc elementet
     *
     * @param Document doc parent document
     * @param workflowID workflowID azonosito
     * @param instanceType a lekerendo instance (runtimeID)-t adja meg
     * @return Element instances
     */
    private Element getInstancesElement(Document doc, String workflowID, String instanceType, boolean isInstanceOne) throws Exception {
        try {
            String runtimeID = new String("");
            if (isInstanceOne) {
                runtimeID = instanceType.substring((instanceType.split("_")[0]).length() + 1);
            }
            // System.out.println("runtimeID: " + runtimeID);
            // ha a runtimeID == "" minden instancet (runtimeID)-t vegigjarunk
            // ami csak letezik ehhez a workflohoz
            // ha egy konkret runtimeID-t tartalmaz akkor csak azt
            //
            // build instances
            Element einstances = doc.createElement("instances");
            //
            // build instance
            ResultSet rsWorkflowProp = workflowXMLService.getWorkflowInstancesFromWorkflowProp(workflowID, runtimeID);
            while (rsWorkflowProp.next()) {
                String wrtid = rsWorkflowProp.getString("wrtid");
                String name = rsWorkflowProp.getString("name");
                String value = rsWorkflowProp.getString("value");
                // System.out.println("workflowID : " + workflowID);
                // System.out.println("wrtid      : " + wrtid);
                // System.out.println("name       : " + name);
                // System.out.println("value      : " + value);
                // szuresek
                if ((!name.equals("wfiurl")) &&(!name.equals("storageurl")) && (!runtimeID.equals("*"))) {
                    // build instance
                    Element einstance = doc.createElement("instance");
                    einstance.setAttribute("rtid", wrtid);
                    einstance.setAttribute("name", name);
                    einstance.setAttribute("value", value);
                    // add instance to einstances
                    einstances.appendChild(einstance);
                }
            }
            //
            // build jobstatus
            ResultSet rsJobStatus = workflowXMLService.getWorkflowInstancesFromJobStatus(workflowID, runtimeID);
            while (rsJobStatus.next()) {
                String wrtid = rsJobStatus.getString("wrtid");
                String jobname = rsJobStatus.getString("jobname");
                String pid = rsJobStatus.getString("pid");
                String status = rsJobStatus.getString("status");
                String resource = rsJobStatus.getString("resource");
                // System.out.println("workflowID : " + workflowID);
                // System.out.println("wrtid      : " + wrtid);
                // System.out.println("jobname    : " + jobname);
                // System.out.println("pid        : " + pid);
                // System.out.println("status     : " + status);
                // System.out.println("resource   : " + resource);
                // build jobstatus
                Element ejobstatus = doc.createElement("jobstatus");
                ejobstatus.setAttribute("rtid", wrtid);
                ejobstatus.setAttribute("job", jobname);
                ejobstatus.setAttribute("pid", pid);
                ejobstatus.setAttribute("status", status);
                ejobstatus.setAttribute("resource", resource);
                // add ejobstatus to einstances
                einstances.appendChild(ejobstatus);
            }
            //
            // build times
            ResultSet rsStatus = workflowXMLService.getWorkflowInstancesFromStatus(workflowID, runtimeID);
            while (rsStatus.next()) {
                String wrtid = rsStatus.getString("id_rt");
                String jobname = rsStatus.getString("jobname");
                String tim2 = rsStatus.getString("tim2");
                String tim3 = rsStatus.getString("tim3");
                String tim4 = rsStatus.getString("tim4");
                String tim5 = rsStatus.getString("tim5");
                String tim6 = rsStatus.getString("tim6");
                String tim7 = rsStatus.getString("tim7");
                // System.out.println("workflowID : " + workflowID);
                // System.out.println("wrtid      : " + wrtid);
                // System.out.println("jobname    : " + jobname);
                // System.out.println("tim2       : " + tim2);
                // System.out.println("tim3       : " + tim3);
                // System.out.println("tim4       : " + tim4);
                // System.out.println("tim5       : " + tim5);
                // System.out.println("tim6       : " + tim6);
                // System.out.println("tim7       : " + tim7);
                // build times
                Element estatus = doc.createElement("times");
                estatus.setAttribute("rtid", wrtid);
                estatus.setAttribute("job", jobname);
                estatus.setAttribute("tim2", tim2);
                estatus.setAttribute("tim3", tim3);
                estatus.setAttribute("tim4", tim4);
                estatus.setAttribute("tim5", tim5);
                estatus.setAttribute("tim6", tim6);
                estatus.setAttribute("tim7", tim7);
                // add estatus to einstances
                einstances.appendChild(estatus);
            }
            return einstances;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
}

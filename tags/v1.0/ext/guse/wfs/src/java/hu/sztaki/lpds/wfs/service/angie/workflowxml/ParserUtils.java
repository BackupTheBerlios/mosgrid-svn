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
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A WorkflowXMLParser-t szolgalja ki. (helper class)
 *
 * A tenyleges xml element ertelmezest vegzi.
 *
 * @author lpds
 */
public class ParserUtils {

    private WorkflowXMLService workflowXMLService;
    private WfsPortalServiceImpl wfsPortalService;

    public ParserUtils(WorkflowXMLService workflowXMLService) throws Exception {
        this.workflowXMLService = workflowXMLService;
        this.wfsPortalService = new WfsPortalServiceImpl();
    }

    /**
     * A parameterben megkapott xml element-et
     *
     * (graf, graph workflow-t)
     *
     * ertelmezi es menti le az adatbazisba.
     *
     * @param StorageWorkflowNamesBean bean - workflow parameterek
     *    portalURL - portal azonosito
     *    userID - felhasznalo azonosito
     *    mainGrafName - main graf workflow name
     *    newMainGrafName - new main graf workflow name
     *      (ezen a neven kell lementeni ha ez egy main workflow)
     * @param value workflow element
     * @return id hashtable - a graf letrehozasa kozben keletkezo id kat tartalmazza
     */
    public Hashtable parseGrafWorkflowXML(StorageWorkflowNamesBean bean, Element value) throws Exception {
        Hashtable idHash = new Hashtable();
        //
        if (value == null) {
            throw new Exception("Not valid graf (graph) workflow xml element !");
        }
        String workflowName = getGrafName(bean, value.getAttribute("name"));
        String workflowText = value.getAttribute("text");
        // System.out.println("save graf workflow(" + workflowName + ")...");
        // this is new workflow
        boolean newWorkflow = true;
        long workflowId = 0;
        long grafWorkflowId = workflowXMLService.saveAWorkflow(newWorkflow, workflowId, bean.getPortalURL(), bean.getUserID(), workflowName, workflowText);
        idHash.put("graf_" + workflowName, grafWorkflowId);
        NodeList jobList = value.getChildNodes();
        // System.out.println("jobList.size: " + jobList.getLength());
        // begin parse jobList
        if (jobList.getLength() > 0) {
            // System.out.println("parseJobList...");
            for (int jobNode = 0; jobNode < jobList.getLength(); jobNode++) {
                Node jobObj = jobList.item(jobNode);
                if (jobObj instanceof Element) {
                    Element eJob = (Element) jobObj;
                    // System.out.println("Element name: " + eJob.getNodeName());
                    if (eJob.getNodeName().equalsIgnoreCase("job")) {
                        Hashtable idJob = parseGrafWorkflowXMLJob(eJob, newWorkflow, grafWorkflowId);
                        idHash.putAll(idJob);
                    }
                }
            }
        }
        return idHash;
    }

    /**
     * A parameterben megkapott xml element-et
     *
     * (abstract, template workflow-t)
     *
     * ertelmezi es menti le az adatbazisba.
     *
     * @param StorageWorkflowNamesBean bean - workflow parameterek
     *    portalURL - portal azonosito
     *    userID - felhasznalo azonosito
     *    mainAbstName - main abst workflow name
     *    newMainAbstName - new main abst workflow name
     *      (ezen a neven kell lementeni ha ez egy main workflow)
     * @param value workflow element
     * @return true ha a workflow ertelmezese es a mentese sikeres
     */
    public boolean parseAbstWorkflowXML(StorageWorkflowNamesBean bean, Element value, Hashtable idHash) throws Exception {
        if (value == null) {
            throw new Exception("Not valid abst (template) workflow xml element !");
        }
        String workflowName = getAbstName(bean, value.getAttribute("name"));
        String workflowText = value.getAttribute("text");
        String grafName = getGrafName(bean, value.getAttribute("graf"));
        // System.out.println("save abst workflow(" + workflowName + ")...");
        if ("".equals(grafName)) {
            throw new Exception("Not valid graf parent name in abst workflow xml element !");
        }
        Vector abstWorkflowVector = getVectorFromXMLElementWithRename(bean, grafName, value, idHash);
        // make new ComDataBean ...
        ComDataBean combean = new ComDataBean();
        combean.setPortalID(bean.getPortalURL());
        combean.setUserID(bean.getUserID());
        combean.setWorkflowID(workflowName);
        combean.setTxt(workflowText);
        // jelen esetben a parent egy graf workflow neve
        combean.setParentWorkflowID(grafName);
        // a typ "3" mert upload-ban "utan" vagyunk es abst workflow-t allitunk elo
        combean.setTyp(3);
        return wfsPortalService.setWorkflowConfigDataReal(combean, abstWorkflowVector);
    }

    /**
     * A parameterben megkapott xml element-et
     *
     * (real workflow-t)
     *
     * ertelmezi es menti le az adatbazisba.
     *
     * (az instance (futasi peldany) informaciokat is lementi)
     *
     * @param StorageWorkflowNamesBean bean - workflow parameterek
     *    portalURL - portal azonosito
     *    userID - felhasznalo azonosito
     *    mainRealName - main real workflow name
     *    newMainRealName - new main real workflow name
     *      (ezen a neven kell lementeni ha ez egy main workflow)
     * @param value workflow element
     * @return true ha a workflow ertelmezese es a mentese sikeres
     */
    public boolean parseRealWorkflowXML(StorageWorkflowNamesBean bean, Element value, Hashtable idHash) throws Exception {
        if (value == null) {
            throw new Exception("Not valid real (concrete) workflow xml element !");
        }
        String workflowName = getRealName(bean, value.getAttribute("name"));
        String workflowText = value.getAttribute("text");
        String grafName = getGrafName(bean, value.getAttribute("graf"));
        String abstName = getAbstName(bean, value.getAttribute("abst"));
        // System.out.println("save real workflow(" + workflowName + ")...");
        if ("".equals(grafName)) {
            throw new Exception("Not valid graf parent name in real workflow xml element !");
        }
        // if ("".equals(abstName)) {
        // throw new Exception("Not valid abst parent name in real workflow xml element !");
        // }
        Vector realWorkflowVector = getVectorFromXMLElementWithRename(bean, grafName, value, idHash);
        // make new ComDataBean ...
        ComDataBean combean = new ComDataBean();
        combean.setPortalID(bean.getPortalURL());
        combean.setUserID(bean.getUserID());
        combean.setWorkflowID(workflowName);
        combean.setTxt(workflowText);
        if (!"".equals(abstName)) {
            // jelen esetben a parent egy abst workflow neve
            combean.setParentWorkflowID(abstName);
            // a typ "1" mert abst (template)-bol allitunk
            // elo real (konkret) workflow-t
            combean.setTyp(1);
        } else {
            // jelen esetben a parent egy graf workflow neve
            combean.setParentWorkflowID(grafName);
            // a typ "0" mert graf-bol allitunk elo real (konkret) workflow-t
            combean.setTyp(0);
        }
        boolean stepp1 = wfsPortalService.setWorkflowConfigDataReal(combean, realWorkflowVector);
        // az instances, a futasi informaciok ertelmezese, lementese
        NodeList realNodeList = value.getChildNodes();
        Element einstances = null;
        for (int wfNode = 0; wfNode < realNodeList.getLength(); wfNode++) {
            Node obj = realNodeList.item(wfNode);
            if (obj instanceof Element) {
                Element eNode = (Element) obj;
                if (eNode.getNodeName().equalsIgnoreCase("instances")) {
                    einstances = eNode;
                }
            }
        }
        boolean stepp2 = parseInstancesXML(bean, grafName, workflowName, einstances, idHash);
        return ((stepp1) && (stepp2));
    }

    /**
     * Feldolgoz egy graf workflow job-ot.
     *
     * @param eJob
     * @param newWorkflow
     * @param grafWorkflowId
     * @return id hashtable - a graf letrehozasa kozben keletkezo id kat tartalmazza
     */
    private Hashtable parseGrafWorkflowXMLJob(Element eJob, boolean newWorkflow, long grafWorkflowId) throws Exception {
        Hashtable idHash = new Hashtable();
        long jobId = 0;
        // String jobIdstr = eJob.getAttribute("id");
        String jobname = eJob.getAttribute("name");
        String jobtext = eJob.getAttribute("text");
        String jobx = eJob.getAttribute("x");
        String joby = eJob.getAttribute("y");
        // System.out.println("Job id str: " + jobIdstr);
        // System.out.println("Job id: " + jobId);
        // System.out.println("Job name: " + jobname);
        // System.out.println("Job text: " + jobtext);
        // System.out.println("Job x: " + jobx);
        // System.out.println("Job y: " + joby);
        long grafJobId = workflowXMLService.saveAJob(newWorkflow, grafWorkflowId, jobId, jobname, jobtext, jobx, joby);
        idHash.put(grafWorkflowId + "_" + jobname, grafJobId);
        NodeList portlist = eJob.getChildNodes();
        // System.out.println("portlist.size: " + portlist.getLength());
        if (portlist.getLength() > 0) {
            int portcnt = 0;
            for (int iNode = 0; iNode < portlist.getLength(); iNode++) {
                Object obj = portlist.item(iNode);
                if (obj instanceof Element) {
                    Element ePort = (Element) obj;
                    // Input
                    if ((ePort.getNodeName().equalsIgnoreCase("input")) || (ePort.getNodeName().equalsIgnoreCase("output"))) {
                        // System.out.println("Element name: " + ePort.getNodeName());
                        Hashtable idPort = parseGrafWorkflowXMLPort(ePort, newWorkflow, grafWorkflowId, grafJobId);
                        idHash.putAll(idPort);
                    }
                }
                portcnt++;
            }
        }
        return idHash;
    }

    /**
     * Feldolgoz egy graf workflow port-ot.
     *
     * @param ePort
     * @param newWorkflow
     * @param grafWorkflowId
     * @param grafJobId
     * @return id hashtable - a graf letrehozasa kozben keletkezo id kat tartalmazza
     */
    private Hashtable parseGrafWorkflowXMLPort(Element ePort, boolean newWorkflow, long grafWorkflowId, long grafJobId) throws Exception {
        Hashtable idHash = new Hashtable();
        long portId = 0;
        // String portidstr = ePort.getAttribute("id");
        String portname = ePort.getAttribute("name");
        String portseq = ePort.getAttribute("seq");
        String porttext = ePort.getAttribute("text");
        String portx = ePort.getAttribute("x");
        String porty = ePort.getAttribute("y");
        // System.out.println("port id str: " + portidstr);
        // System.out.println("port id: " + portId);
        // System.out.println("port name: " + portname);
        // System.out.println("port seq: " + portseq);
        // System.out.println("port text: " + porttext);
        // System.out.println("port x: " + portx);
        // System.out.println("port y: " + porty);
        // Input
        if (ePort.getNodeName().equalsIgnoreCase("input")) {
            String prejob = ePort.getAttribute("prejob");
            String preoutput = ePort.getAttribute("preoutput");
            long grafPortId = workflowXMLService.saveAInput(newWorkflow, grafJobId, portId, portname, prejob, preoutput, portseq, porttext, portx, porty);
            idHash.put(grafWorkflowId + "_" + grafJobId + "_" + portseq, grafPortId);
        }
        // Output
        if (ePort.getNodeName().equalsIgnoreCase("output")) {
            long grafPortId = workflowXMLService.saveAOutput(newWorkflow, grafJobId, portId, portname, portseq, porttext, portx, porty);
            idHash.put(grafWorkflowId + "_" + grafJobId + "_" + portseq, grafPortId);
        }
        return idHash;
    }

    /**
     * Egy vectort ad vissza aminek minden eleme egy JobPropertyBean
     *
     * (ami egy abst (template) vagy real (concrete) workflow job-ot ir le).
     *
     * StorageWorkflowNamesBean bean - workflow, user details
     * @param value workflow element
     * @return JobPropertyBean vector
     */
    private Vector getVectorFromXMLElementWithRename(StorageWorkflowNamesBean bean, String grafParentWorkflowID, Element value, Hashtable idHash) throws Exception {
        Vector retVector = new Vector();
        NodeList jobList = value.getChildNodes();
        // System.out.println("jobList.size: " + jobList.getLength());
        // begin parse jobList
        if (jobList.getLength() > 0) {
            // get graf workflow long id
            long aworkflowId = (Long) idHash.get("graf_" + grafParentWorkflowID);
            //
            // System.out.println("aworkflowId: " + aworkflowId);
            // System.out.println("parseJobList...");
            for (int jobNode = 0; jobNode < jobList.getLength(); jobNode++) {
                Node jobObj = jobList.item(jobNode);
                if (jobObj instanceof Element) {
                    Element eJob = (Element) jobObj;
                    // System.out.println("Element name: " + eJob.getNodeName());
                    if (eJob.getNodeName().equalsIgnoreCase("job")) {
                        long ajobId = (Long) idHash.get(aworkflowId + "_" + eJob.getAttribute("name"));
                        //
                        // System.out.println("jobid: " + ajobId);
                        JobPropertyBean jobBean = new JobPropertyBean();
                        jobBean.setId(ajobId);
                        jobBean.setName(eJob.getAttribute("name"));
                        jobBean.setTxt(eJob.getAttribute("text"));
                        jobBean.setX(Long.parseLong(eJob.getAttribute("x")));
                        jobBean.setY(Long.parseLong(eJob.getAttribute("y")));
                        // begin parse subList...
                        NodeList subList = eJob.getChildNodes();
                        for (int subNode = 0; subNode < subList.getLength(); subNode++) {
                            Node subObj = subList.item(subNode);
                            if (subObj instanceof Element) {
                                Element eSub = (Element) subObj;
                                if (eSub.getNodeName().equalsIgnoreCase("input")) {
                                    long aPortId = 0;
                                    aPortId = (Long) idHash.get(aworkflowId + "_" + ajobId + "_" + eSub.getAttribute("seq"));
                                    //
                                    // System.out.println("portid: " + aPortId);
                                    PortDataBean portBean = new PortDataBean();
                                    portBean.setId(aPortId);
                                    portBean.setName(eSub.getAttribute("name"));
                                    portBean.setSeq(Long.parseLong(eSub.getAttribute("seq")));
                                    portBean.setTxt(eSub.getAttribute("text"));
                                    portBean.setPrejob(eSub.getAttribute("prejob"));
                                    portBean.setPreoutput(eSub.getAttribute("preoutput"));
                                    portBean.setX(Long.parseLong(eSub.getAttribute("x")));
                                    portBean.setY(Long.parseLong(eSub.getAttribute("y")));
                                    // parse port properties nodes
                                    parsePortPropertNoDES(eSub, portBean);
                                    jobBean.addInput(portBean);
                                }
                                if (eSub.getNodeName().equalsIgnoreCase("output")) {
                                    long aPortId = 0;
                                    aPortId = (Long) idHash.get(aworkflowId + "_" + ajobId + "_" + eSub.getAttribute("seq"));
                                    //
                                    // System.out.println("portid: " + aPortId);
                                    PortDataBean portBean = new PortDataBean();
                                    portBean.setId(aPortId);
                                    portBean.setName(eSub.getAttribute("name"));
                                    portBean.setSeq(Long.parseLong(eSub.getAttribute("seq")));
                                    portBean.setTxt(eSub.getAttribute("text"));
                                    // portBean.setPrejob(eSub.getAttribute("prejob"));
                                    // portBean.setPreoutput(eSub.getAttribute("preoutput"));
                                    portBean.setX(Long.parseLong(eSub.getAttribute("x")));
                                    portBean.setY(Long.parseLong(eSub.getAttribute("y")));
                                    // parse port properties nodes
                                    parsePortPropertNoDES(eSub, portBean);
                                    jobBean.addOutput(portBean);
                                }
                                if (eSub.getNodeName().equalsIgnoreCase("execute")) {
                                    String key = eSub.getAttribute("key");
                                    if (key.equals("iworkflow")) {
                                        // a beagyazott real (concrete) workflow nev referenciakat is at kell nevezni
                                        jobBean.addExe(key, getRealName(bean, eSub.getAttribute("value")));
                                    } else {
                                        jobBean.addExe(key, eSub.getAttribute("value"));
                                    }
                                    jobBean.getLabel().put(key, eSub.getAttribute("label"));
                                    jobBean.getDesc0().put(key, eSub.getAttribute("desc"));
                                    jobBean.getInherited().put(key, eSub.getAttribute("inh"));
                                }
                                if (eSub.getNodeName().equalsIgnoreCase("description")) {
                                    jobBean.addDesc(eSub.getAttribute("key"), eSub.getAttribute("value"));
                                }
                                if (eSub.getNodeName().equalsIgnoreCase("history")) {
                                    HistoryBean histBean = new HistoryBean();
                                    histBean.setTim(eSub.getAttribute("tim"));
                                    histBean.setPort(eSub.getAttribute("port"));
                                    histBean.setUser(eSub.getAttribute("user"));
                                    histBean.setMdyid(eSub.getAttribute("mdyid"));
                                    histBean.setOvalue(eSub.getAttribute("ovalue"));
                                    histBean.setNvalue(eSub.getAttribute("nvalue"));
                                    jobBean.addHistory(histBean);
                                }
                            }
                        }
                        // end parse subList...
                        retVector.addElement(jobBean);
                    }
                }
            }
        }
        // end parse jobList...
        return retVector;
    }

    /**
     * A megkapott eSub elementet jarja be
     * es a port data bean-ben beallitja
     * a port megfelelo parametereit:
     * pl: data, label, desc es inh
     */
    private void parsePortPropertNoDES(Element eSub, PortDataBean portBean) throws Exception {
        // parse port properties nodes
        NodeList propList = eSub.getChildNodes();
        for (int propNode = 0; propNode < propList.getLength(); propNode++) {
            Node propObj = propList.item(propNode);
            if (propObj instanceof Element) {
                Element eProp = (Element) propObj;
                // parse port_prop nodes
                if (eProp.getNodeName().equalsIgnoreCase("port_prop")) {
                    String key = eProp.getAttribute("key");
                    portBean.getData().put(key, eProp.getAttribute("value"));
                    portBean.getLabel().put(key, eProp.getAttribute("label"));
                    portBean.getDesc().put(key, eProp.getAttribute("desc"));
                    portBean.getInherited().put(key, eProp.getAttribute("inh"));
                }
            }
        }
    }

    /**
     * A parameterben megkapott xml element-et
     *
     * (real workflow instance leiro-t)
     *
     * ertelmezi es menti le az adatbazisba.
     *
     * @param StorageWorkflowNamesBean bean - workflow parameterek
     *    portalURL - portal azonosito
     *    userID - felhasznalo azonosito
     *    storageURL - storage azonosito
     *    uploadID az runtimeID vegehez lessz hozzafuzve
     * @param aworkflowName - aworkflow (graf) azonosito
     * @param workflowName - workflow (real) azonosito
     * @param value workflow element
     * @return true ha a workflow ertelmezese es a mentese sikeres
     */
    private boolean parseInstancesXML(StorageWorkflowNamesBean bean, String aworkflowName, String workflowName, Element einstances, Hashtable idHash) throws Exception {
        // get real workflow id
        String workflowID = new String("");
        ResultSet rsWfID = workflowXMLService.getWorkflowData(bean.getPortalURL(), bean.getUserID(), workflowName);
        // if (rsWfID == null) {
        //     throw new Exception("Not valid real workflow name ! workflowName = (" + workflowName + ")");
        // }
        if (rsWfID.next()) {
            workflowID = rsWfID.getString("id");
        }
        if (!"".equals(workflowID)) {
            // workflowID, *, storageurl , url (minden workflow-hoz csak egy sor tartozik)
            workflowXMLService.deleteWorkflowInstancesFromWorkflowProp(workflowID, "*", "storageurl");
            workflowXMLService.saveWorkflowInstancesToWorkflowProp(workflowID, "*", "storageurl", bean.getStorageURL());
        }
        // appmain real workflow flag beallitasa
        // application workflow feltoltese soran
        // ha az aktualis workflow main workflow,
        // (ebbe a workflow ba van beagyazva az
        // osszes tobbi workflow, ezt a
        // workflowt kell submittalni)
        // akkor bejegyzesre kerul egy "appmain" "true"
        // kulcs ertek par a workflow_prop tablaba
        // System.out.println("------------------------------------------------");
        // System.out.println("parse application xml     : " + bean.isAppl());
        // System.out.println("workflowName              : " + workflowName);
        // System.out.println("bean.getNewMainRealName() : " + bean.getNewMainRealName());
        // System.out.println("bean.getMainRealName()    : " + bean.getMainRealName());
        // System.out.println("------------------------------------------------");
        if (bean.isAppl()) {
            if (!"".equals(workflowID)) {
                String appMainKey = new String("appmain");
                // workflow prop tabla appmain bejegyzes torlese
                workflowXMLService.deleteWorkflowInstancesFromWorkflowProp(workflowID, "*", appMainKey);
                String appMainName = bean.getNewMainRealName();
                if ("".equals(appMainName)) {
                    appMainName = bean.getMainRealName();
                }
                if (workflowName.equals(appMainName)) {
                    // workflow prop tabla appmain bejegyzes elhelyezese
                    // ha az aktualis konkrete, real workflow main workflow
                    workflowXMLService.saveWorkflowInstancesToWorkflowProp(workflowID, "*", appMainKey, "true");
                }
            }
        }
        if (einstances != null) {
            // get real workflow aworkflow id
            long aworkflowIDLong = (Long) idHash.get("graf_" + aworkflowName);
            String aworkflowID = String.valueOf(aworkflowIDLong);
            //
            // get instance list
            NodeList instancesList = einstances.getChildNodes();
            // finishedInstances : azok a rtID-k instance-ok
            // kerulnek bele ebbe a listaba amik 6os vagy 7es statuszuak
            // azaz mar befejezodott a futasuk
            Hashtable finishedInstances = new Hashtable();
            // parse instances status
            // create finished instances list
            if (instancesList.getLength() > 0) {
                // System.out.println("parse instances status...");
                for (int iNode = 0; iNode < instancesList.getLength(); iNode++) {
                    Node obj = instancesList.item(iNode);
                    if (obj instanceof Element) {
                        Element eNode = (Element) obj;
                        // System.out.println("Element name: " + eNode.getNodeName());
                        if (eNode.getNodeName().equalsIgnoreCase("instance")) {
                            String rtID = eNode.getAttribute("rtid");
                            String name = eNode.getAttribute("name");
                            String value = eNode.getAttribute("value");
                            if ("status".equals(name)) {
                                if (("6".equals(value)) || ("7".equals(value))) {
                                    // felkerul a finished instances listara
                                    finishedInstances.put(rtID, "ok");
                                }
                            }
                        }
                    }
                }
            }
            // System.out.println("finishedInstances : " + finishedInstances);
            // parse and save instances
            if (instancesList.getLength() > 0) {
                // System.out.println("parse instances...");
                for (int iNode = 0; iNode < instancesList.getLength(); iNode++) {
                    Node obj = instancesList.item(iNode);
                    if (obj instanceof Element) {
                        Element eNode = (Element) obj;
                        // System.out.println("Element name: " + eNode.getNodeName());
                        if (finishedInstances.containsKey(eNode.getAttribute("rtid"))) {
                            // this istance status is finished now - begin
                            if (eNode.getNodeName().equalsIgnoreCase("instance")) {
                                // uploadID az runtimeID vegehez lessz hozzafuzve
                                String newRtID = createNewRuntimeID(eNode.getAttribute("rtid"), bean.getUploadID());
                                workflowXMLService.saveWorkflowInstancesToWorkflowProp(workflowID, newRtID, eNode.getAttribute("name"), eNode.getAttribute("value"));
                            }
                            if (eNode.getNodeName().equalsIgnoreCase("jobstatus")) {
                                // uploadID az runtimeID vegehez lessz hozzafuzve
                                String newRtID = createNewRuntimeID(eNode.getAttribute("rtid"), bean.getUploadID());
                                workflowXMLService.saveWorkflowInstancesToJobStatus(workflowID, newRtID, eNode.getAttribute("pid"), eNode.getAttribute("status"), eNode.getAttribute("resource"), aworkflowID, eNode.getAttribute("job"));
                            }
                            if (eNode.getNodeName().equalsIgnoreCase("times")) {
                                // uploadID az runtimeID vegehez lessz hozzafuzve
                                String newRtID = createNewRuntimeID(eNode.getAttribute("rtid"), bean.getUploadID());
                                workflowXMLService.saveWorkflowInstancesToStatus(workflowID, newRtID, eNode.getAttribute("tim2"), eNode.getAttribute("tim3"), eNode.getAttribute("tim4"), eNode.getAttribute("tim5"), eNode.getAttribute("tim6"), eNode.getAttribute("tim7"), aworkflowID, eNode.getAttribute("job"));
                            }
                            // this istance status is finished now - end
                        }
                    }
                }
            }
        } else {
            // throw new Exception("Not valid instances in workflow xml element !");
        }
        return true;
    }

    /**
     * Feltolteskor hoz letre egy uj runtimeID-t,
     * a regi-bol runtimeID es az uploadID-bol.
     *
     * (A storage es a wfs ben is megtalalhato ez a methodus !)
     *
     * @param runtimeID - regi instance azonosito
     * @param uploadID - feltoltesi azonosito
     * @return az elkeszult uj runtimeID
     */
    private String createNewRuntimeID(String runtimeID, String uploadID) throws Exception {
        if ((runtimeID == null) || ("".equals(uploadID))) {
            return runtimeID;
        }
        int index = runtimeID.lastIndexOf(")upload");
        if (index >= 0) {
            // regi uploadID csere ujra
            return new String(runtimeID.substring(0, index).concat(")" + uploadID));
        } else {
            return new String(runtimeID).concat(uploadID);
        }
    }

    /**
     * A graf workflow vegleges lementesre kerulo nevet adja vissza.
     * Ha main workflow akkor at lehet nevezni a bean ben levo
     * newMainXXXXName-val, opcionalis.
     */
    private String getGrafName(StorageWorkflowNamesBean bean, String wfName) throws Exception {
        if (!"".equals(wfName)) {
            wfName = wfName + bean.getImportID();
            if ((!"".equals(bean.getNewMainGrafName())) && (wfName.equals(bean.getMainGrafName()))) {
                return bean.getNewMainGrafName();
            }
        }
        return wfName;
    }

    /**
     * A abst workflow vegleges lementesre kerulo nevet adja vissza.
     * Ha main workflow akkor at lehet nevezni a bean ben levo
     * newMainXXXXName-val, opcionalis.
     */
    private String getAbstName(StorageWorkflowNamesBean bean, String wfName) throws Exception {
        if (!"".equals(wfName)) {
            wfName = wfName + bean.getImportID();
            if ((!"".equals(bean.getNewMainAbstName())) && (wfName.equals(bean.getMainAbstName()))) {
                return bean.getNewMainAbstName();
            }
        }
        return wfName;
    }

    /**
     * A real workflow vegleges lementesre kerulo nevet adja vissza.
     * Ha main workflow akkor at lehet nevezni a bean ben levo
     * newMainXXXXName-val, opcionalis.
     */
    private String getRealName(StorageWorkflowNamesBean bean, String wfName) throws Exception {
        if (!"".equals(wfName)) {
            wfName = wfName + bean.getImportID();
            if ((!"".equals(bean.getNewMainRealName())) && (wfName.equals(bean.getMainRealName()))) {
                return bean.getNewMainRealName();
            }
        }
        return wfName;
    }
}

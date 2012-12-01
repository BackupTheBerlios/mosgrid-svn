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
package hu.sztaki.lpds.pgportal.wfeditor.net.xmlrpc.server;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Feldolgoz, ertelmez es adatbazisban letarol egy workflow-t.
 * @author lpds
 */
public class XMLParser {

    private WEWFSServiceImpl wfsServiceImpl;
    private ArrayList oldWorkflowJobList, oldWorkflowInputPortList, oldWorkflowOutputPortList;
    private ArrayList newWorkflowJobList, newWorkflowInputPortList, newWorkflowOutputPortList;
    private boolean isParseError;

    public XMLParser(WEWFSServiceImpl wfsServiceImpl) {
        this.wfsServiceImpl = wfsServiceImpl;
    }

    public boolean getIsParseError() {
        return isParseError;
    }

    /**
     * A parameterekben megadott azonositok alapjan eltarolja a workflow-t. 
     * @param newWorkflow ha uj workflow-t kell letrehozni akkor true
     * @param portalid portal azonosito
     * @param userid felhasznalo azonosito
     * @param wfname workflow neve
     * @param xmlstr a workflow leiro xml string
     */
    public void parseXMLStr(boolean newWorkflow, String portalid, String userid, String wfname, String xmlstr) {
            System.out.println("start parseXMLStr...");
            System.out.println(xmlstr);

        try {
            // System.out.println("start parseXMLStr...");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            // Build document from xml file
            // Document doc = builder.parse(new FileInputStream("/home/username/1/1.xml"));
            // Build document from xmlstr string
            Document doc = builder.parse(new InputSource(new StringReader(xmlstr)));
            Element eroot = doc.getDocumentElement();
            String workflowIdstr = eroot.getAttribute("id");
            // boolean newWorkflow = true;
            long workflowId = 0;
            if ((workflowIdstr.trim().length() == 0) || ("0".equals(workflowIdstr))) {
                newWorkflow = true;
            }
            if (newWorkflow) {
                workflowId = 0;
            } else {
                workflowId = Long.parseLong(workflowIdstr);
            }
            if (!newWorkflow) {
                oldWorkflowJobList = wfsServiceImpl.getWorkflowJobList(workflowId);
                oldWorkflowInputPortList = wfsServiceImpl.getWorkflowPortList("ainput", workflowId);
                oldWorkflowOutputPortList = wfsServiceImpl.getWorkflowPortList("aoutput", workflowId);
                newWorkflowJobList = new ArrayList();
                newWorkflowInputPortList = new ArrayList();
                newWorkflowOutputPortList = new ArrayList();
            }
            String workflowName = eroot.getAttribute("name");
            String workflowText = getFilteredString(eroot.getAttribute("text"));
            // System.out.println("wf id str: " + workflowIdstr);
            // System.out.println("wf id: " + workflowId);
            // System.out.println("wf name: " + workflowName);
            // System.out.println("wf text: " + workflowText);
            long abstractWorkflowId = wfsServiceImpl.saveAbstractWorkflow(newWorkflow, workflowId, portalid, userid, workflowName, workflowText);
            NodeList joblist = eroot.getChildNodes();
            // System.out.println("joblist.size: " + joblist.getLength());
            if (joblist.getLength() > 0) {
                // parse jobs
                // System.out.println("parseJob...");
                for (int iNode = 0; iNode < joblist.getLength(); iNode++) {
                    Node obj = joblist.item(iNode);
                    if (obj instanceof Element) {
                        Element eJob = (Element) obj;
                        // System.out.println("Element name: " + eJob.getNodeName());
                        if (eJob.getNodeName().equalsIgnoreCase("job")) {
                            parseJob(eJob, newWorkflow, abstractWorkflowId);
                        }
                    }
                }
                // execute all update Sql
                // wfsServiceImpl.executeAllBatch();                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!newWorkflow) {
            // System.out.println("oldWorkflowJobList        : " + oldWorkflowJobList);
            // System.out.println("oldWorkflowInputPortList  : " + oldWorkflowInputPortList);
            // System.out.println("oldWorkflowOutputPortList : " + oldWorkflowOutputPortList);
            // System.out.println("newWorkflowJobList        : " + newWorkflowJobList);
            // System.out.println("newWorkflowInputPortList  : " + newWorkflowInputPortList);
            // System.out.println("newWorkflowOutputPortList : " + newWorkflowOutputPortList);
            oldWorkflowJobList.removeAll(newWorkflowJobList);
            oldWorkflowInputPortList.removeAll(newWorkflowInputPortList);
            oldWorkflowOutputPortList.removeAll(newWorkflowOutputPortList);
            ArrayList deletedJobList = new ArrayList(oldWorkflowJobList);
            ArrayList deletedInputPortList = new ArrayList(oldWorkflowInputPortList);
            ArrayList deletedOutputPortList = new ArrayList(oldWorkflowOutputPortList);
            // System.out.println("deleted job list         : " + deletedJobList);
            // System.out.println("deleted input port list  : " + deletedInputPortList);
            // System.out.println("deleted output port list : " + deletedOutputPortList);
            if (!deletedJobList.isEmpty()) {
                wfsServiceImpl.deleteList("ajob", deletedJobList);
            }
            if (!deletedInputPortList.isEmpty()) {
                wfsServiceImpl.deleteList("ainput", deletedInputPortList);
            }
            if (!deletedOutputPortList.isEmpty()) {
                wfsServiceImpl.deleteList("aoutput", deletedOutputPortList);
            }
        }
    }

    /**
     * Feldolgoz egy job-ot.
     * @param eJob
     * @param newWorkflow
     * @param abstractWorkflowId
     */
    private void parseJob(Element eJob, boolean newWorkflow, long abstractWorkflowId) {
        long jobId = 0;
        String jobIdstr = eJob.getAttribute("id");
        String jobname = eJob.getAttribute("name");
        String jobtext = getFilteredString(eJob.getAttribute("text"));
        String jobx = eJob.getAttribute("x");
        String joby = eJob.getAttribute("y");
        if ((jobIdstr.trim().length() == 0) || ("0".equals(jobIdstr))) {
            jobId = 0;
        } else {
            jobId = Long.parseLong(jobIdstr);
        }
        // System.out.println("Job id str: " + jobIdstr);
        // System.out.println("Job id: " + jobId);
        // System.out.println("Job name: " + jobname);
        // System.out.println("Job text: " + jobtext);
        // System.out.println("Job x: " + jobx);
        // System.out.println("Job y: " + joby);
        long abstractJobId = wfsServiceImpl.saveAbstractJob(newWorkflow, abstractWorkflowId, jobId, jobname, jobtext, jobx, joby);
        if (!newWorkflow) {
            newWorkflowJobList.add(String.valueOf(abstractJobId));
        }
        try {
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
                            parsePort(ePort, newWorkflow, abstractWorkflowId, abstractJobId);
                        }
                    }
                    portcnt++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Feldolgoz egy port-ot.
     * @param ePort
     * @param newWorkflow
     * @param abstractWorkflowId
     * @param abstractJobId
     */
    private void parsePort(Element ePort, boolean newWorkflow, long abstractWorkflowId, long abstractJobId) {
        try {
            long portId = 0;
            String portidstr = ePort.getAttribute("id");
            String portname = ePort.getAttribute("name");
            String portseq = ePort.getAttribute("seq");
            String porttext = getFilteredString(ePort.getAttribute("text"));
            String portx = ePort.getAttribute("x");
            String porty = ePort.getAttribute("y");
            if ((portidstr.trim().length() == 0) || ("0".equals(portidstr))) {
                portId = 0;
            } else {
                portId = Long.parseLong(portidstr);
            }
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
                long abstractPortId = wfsServiceImpl.saveAbstractInput(newWorkflow, abstractJobId, portId, portname, prejob, preoutput, portseq, porttext, portx, porty);
                if (!newWorkflow) {
                    newWorkflowInputPortList.add(String.valueOf(abstractPortId));
                }
            }
            // Output
            if (ePort.getNodeName().equalsIgnoreCase("output")) {
                long abstractPortId = wfsServiceImpl.saveAbstractOutput(newWorkflow, abstractJobId, portId, portname, portseq, porttext, portx, porty);
                if (!newWorkflow) {
                    newWorkflowOutputPortList.add(String.valueOf(abstractPortId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * html and script elements filter...
     *
     * @return clear text
     */
    private String getFilteredString(String text) {
        text = text.replaceAll("<", "");
        text = text.replaceAll(">", "");
        return text;
    }
}

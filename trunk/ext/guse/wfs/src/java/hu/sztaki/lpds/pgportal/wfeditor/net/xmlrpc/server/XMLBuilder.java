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

import java.io.StringWriter;
import java.sql.ResultSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Összeallit es visszaad egy adatbazisban letarolt workflow-t.
 * @author lpds
 */
public class XMLBuilder {
    
    private WEWFSServiceImpl wfsServiceImpl;
    
    private boolean isBuildError;
    
    private ResultSet rsWorkflow;
    
    private ResultSet rsAllJob;
    
    private ResultSet rsAllInp;
    
    private ResultSet rsAllOut;
    
    public XMLBuilder(WEWFSServiceImpl wfsServiceImpl) {
        this.wfsServiceImpl = wfsServiceImpl;
    }
    
    public boolean getIsParseError() {
        return isBuildError;
    }

    /**
     * A parameterekben megadott azonositok alapjan összeallitja a workflow xml-t.
     * @param portalid portal azonosito
     * @param userid felhasznalo azonosito
     * @param wfname workflow neve
     * @return String workflow xml
     */
    public String buildXMLStr(String portalid, String userid, String wfname) {
        String xmlstr = new String("");
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document doc = builder.newDocument();
            rsWorkflow = wfsServiceImpl.getAbstractWorkflow(portalid, userid, wfname);            
            if (rsWorkflow.next()) {
                String inpJobId = "";
                String outJobId = "";
                String aworkflowIdStr = rsWorkflow.getString("id");
                Long aworkflowId = new Long(aworkflowIdStr);
                String aworkflowText = rsWorkflow.getString("txt");
                rsAllJob = wfsServiceImpl.getAbstractJob(aworkflowId.longValue());
                rsAllInp = wfsServiceImpl.getAbstractInput(aworkflowId.longValue());
                rsAllOut = wfsServiceImpl.getAbstractOutput(aworkflowId.longValue());
                inpJobId = getNextInpPortJobId();
                outJobId = getNextOutPortJobId();                
                if (rsAllJob != null) {
                    try {
                        Element eroot = doc.createElement("workflow");                        
                        eroot.setAttribute("id", aworkflowIdStr);
                        eroot.setAttribute("name", wfname);
                        eroot.setAttribute("text", aworkflowText);
                        // add jobs XML elements
                        while (rsAllJob.next()) {
                            Element eJob = doc.createElement("job");
                            try {
                                String ajobIdStr = rsAllJob.getString("id");
                                // Long ajobId = new Long(ajobIdStr);
                                // ***************************************************
                                // job name
                                eJob.setAttribute("id", ajobIdStr);
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
                                    ePort.setAttribute("id", rsAllInp.getString("id"));
                                    ePort.setAttribute("name", rsAllInp.getString("name"));
                                    ePort.setAttribute("prejob", rsAllInp.getString("prejob"));
                                    ePort.setAttribute("preoutput", rsAllInp.getString("preoutput"));
                                    ePort.setAttribute("seq", rsAllInp.getString("seq"));
                                    ePort.setAttribute("text", rsAllInp.getString("txt"));
                                    ePort.setAttribute("x", rsAllInp.getString("x"));
                                    ePort.setAttribute("y", rsAllInp.getString("y"));
                                    eJob.appendChild(ePort);
                                    inpJobId = getNextInpPortJobId();
                                }
                                // get output ports
                                while (ajobIdStr.equals(outJobId)) {
                                    ePort = doc.createElement("output");
                                    ePort.setAttribute("id", rsAllOut.getString("id"));
                                    ePort.setAttribute("name", rsAllOut.getString("name"));
                                    ePort.setAttribute("seq", rsAllOut.getString("seq"));
                                    ePort.setAttribute("text", rsAllOut.getString("txt"));
                                    ePort.setAttribute("x", rsAllOut.getString("x"));
                                    ePort.setAttribute("y", rsAllOut.getString("y"));
                                    eJob.appendChild(ePort);
                                    outJobId = getNextOutPortJobId();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            eroot.appendChild(eJob);
                        }
                        // Create dom document
                        doc.appendChild(eroot);
                        // Generate XML output to string
                        TransformerFactory transFactory = TransformerFactory.newInstance();
                        Transformer transformer = transFactory.newTransformer();
                        // We want to pretty format the XML output...
                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
                        transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
                        // source: doc
                        DOMSource source = new DOMSource(doc);
                        // doc to file ver1
                        // StreamResult fileResult = new StreamResult(new FileOutputStream("/home/username/1/dom.xml"));
                        // transformer.transform(source, fileResult);
                        //
                        // doc to string ver2
                        // ByteArrayOutputStream xmlout = new ByteArrayOutputStream();
                        // transformer.transform(source, new StreamResult(xmlout));
                        // xmlstr = xmlout.toString();
                        //
                        // doc to string ver3
                        StringWriter stringWriter = new StringWriter();
                        StreamResult streamResult = new StreamResult(stringWriter);
                        transformer.transform(source, streamResult);
                        xmlstr = stringWriter.toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // System.out.println("xml: " + xmlstr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlstr;
    }
    
    /**
     * Lepteti a resultSet -et es visszaadja a következö porthoz tartozo jobid-t 
     * @return String  
     * @throws Exception
     */
    private String getNextInpPortJobId() throws Exception {
        if (rsAllInp.next()) {
            return rsAllInp.getString("id_ajob");
        }
        return new String("");   
    }

    /**
     * Lepteti a resultSet -et es visszaadja a következö porthoz tartozo jobid-t 
     * @return String  
     * @throws Exception
     */
    private String getNextOutPortJobId() throws Exception {
        if (rsAllOut.next()) {
            return rsAllOut.getString("id_ajob");
        }
        return new String("");   
    }
    
}

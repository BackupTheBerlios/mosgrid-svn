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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.bes;

import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.submitter.grids.Middleware;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.TransformerConfigurationException;

import javax.xml.transform.TransformerException;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStatusType;
import org.ggf.schemas.bes._2006._08.bes_factory.BESFactoryPortType;
import org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.FactoryResourceAttributesDocumentType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetActivityDocumentResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetActivityDocumentsResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetActivityStatusResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetActivityStatusesResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetFactoryAttributesDocumentResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAcceptingNewActivitiesFault;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAuthorizedFault;
import org.ggf.schemas.bes._2006._08.bes_factory.TerminateActivitiesResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.TerminateActivityResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.UnsupportedFeatureFault;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.ws.WebServiceContext;
import org.w3c.dom.Document;

import org.w3c.dom.Element;

import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import  com.sun.xml.bind.marshaller.MinimumEscapeHandler;
import java.util.List;
import org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFaultType;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAcceptingNewActivitiesFaultType;

/**
 * @author krisztian karoczkai
 */
@WebService(serviceName = "BESFactoryService", portName = "BESFactoryPort", endpointInterface = "org.ggf.schemas.bes._2006._08.bes_factory.BESFactoryPortType", targetNamespace = "http://schemas.ggf.org/bes/2006/08/bes-factory", wsdlLocation = "WEB-INF/wsdl/BesService/schemas.ogf.org/bes/2006/08/bes-factoryWrapper.wsdl")
public class BesService implements BESFactoryPortType {
    @Resource
    private WebServiceContext wsc;
/**
 * Job submit
 * @param parameters Submitalashoz szukseges leiro
 * @return szervizen beluli egyedi job azonosito
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.NotAcceptingNewActivitiesFault
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.UnsupportedFeatureFault
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.NotAuthorizedFault
 */
    public org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityResponseType createActivity(org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityType parameters) throws NotAcceptingNewActivitiesFault, InvalidRequestMessageFault, UnsupportedFeatureFault, NotAuthorizedFault {
        if(!Base.getI().isStatus()) throw new NotAcceptingNewActivitiesFault("DCI-BRIDGE is inactiv", new NotAcceptingNewActivitiesFaultType());
        List<String> anot=parameters.getActivityDocument().getJobDefinition().getJobDescription().getJobIdentification().getJobAnnotation();
        String id="";
        if(anot.size()==0){
            id=UUID.randomUUID().toString();
            parameters.getActivityDocument().getJobDefinition().getJobDescription().getJobIdentification().getJobAnnotation().add(id);
        }
        else id=anot.get(0);

        CreateActivityResponseType res=new CreateActivityResponseType();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        W3CEndpointReference tmp=null;
        try{
            DocumentBuilder builder= factory.newDocumentBuilder();
            Document d=builder.newDocument();
            Element e=d.createElement("job");
            e.setTextContent(id);
            tmp=(W3CEndpointReference) wsc.getEndpointReference(e);
            res.setActivityIdentifier(tmp);
            res.setActivityDocument(parameters.getActivityDocument());
        }
        catch(Exception e){e.printStackTrace();}

        Job job=new Job(parameters, id);
        Base.getI().newJob(id, job);

        return res;
    }
/**
 * Jobok statuszanak lekerdezese
 * @param parameters egyedi Job azonosito
 * @return statusz informaciok
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault
 */
    public org.ggf.schemas.bes._2006._08.bes_factory.GetActivityStatusesResponseType getActivityStatuses(org.ggf.schemas.bes._2006._08.bes_factory.GetActivityStatusesType parameters) throws InvalidRequestMessageFault {
        GetActivityStatusesResponseType res=new GetActivityStatusesResponseType();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        String id="";
        Job job;
        for(W3CEndpointReference t: parameters.getActivityIdentifier()){
            try{
                id=getJobID(t, factory);
                GetActivityStatusResponseType item= new GetActivityStatusResponseType();
                item.setActivityIdentifier(t);
                ActivityStatusType sx=new ActivityStatusType();
                if(Base.getI().getJob(id)!=null) {
                    job=Base.getI().getJob(id);
                    sx.setState(job.getPubStatus());
                    if(job.getPubStatus().equals(ActivityStateEnumeration.FINISHED) || job.getPubStatus().equals(ActivityStateEnumeration.FAILED) || job.getPubStatus().equals(ActivityStateEnumeration.CANCELLED) )
                        Base.getI().removeJob(id);
                }
                item.setActivityStatus(sx);
                res.getResponse().add(item);
            }
            catch(Exception e0){e0.printStackTrace();}
        }
        return res;
    }
/**
 * Running jobs interruption
 * @param parameters parameter for interruption 
 * @return result of interruption 
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault
 */
    public org.ggf.schemas.bes._2006._08.bes_factory.TerminateActivitiesResponseType terminateActivities(org.ggf.schemas.bes._2006._08.bes_factory.TerminateActivitiesType parameters) throws InvalidRequestMessageFault {
        TerminateActivitiesResponseType res=new TerminateActivitiesResponseType();
        String id="";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        for(W3CEndpointReference t: parameters.getActivityIdentifier()){
            try{
                id=getJobID(t, factory);
                Base.getI().getJob(id).setFlag(Middleware.ABORT);
                TerminateActivityResponseType item=new TerminateActivityResponseType();
                item.setCancelled(true);
                item.setActivityIdentifier(t);
                res.getResponse().add(item);
            }
            catch(Exception e){/*Job not exist*/ throw new InvalidRequestMessageFault("Job ("+id+") not exist", new InvalidRequestMessageFaultType());}
        }
        return res;
    }
/**
 * Getting JSDL documents futo according to running jobs 
 * @param parameters lekerdezo perameterek
 * @return JSDL-ek
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault
 */
    public org.ggf.schemas.bes._2006._08.bes_factory.GetActivityDocumentsResponseType getActivityDocuments(org.ggf.schemas.bes._2006._08.bes_factory.GetActivityDocumentsType parameters) throws InvalidRequestMessageFault {
        GetActivityDocumentsResponseType res=new GetActivityDocumentsResponseType();
        String id="";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        for(W3CEndpointReference t: parameters.getActivityIdentifier()){
            try{
                id=getJobID(t, factory);
                GetActivityDocumentResponseType item=new GetActivityDocumentResponseType();
                item.setActivityIdentifier(t);
                item.setJobDefinition(Base.getI().getJob(id).getJSDL());
                res.getResponse().add(item);
            }
            catch(Exception e){e.printStackTrace();}
        }
        return res;

    }

    public org.ggf.schemas.bes._2006._08.bes_factory.GetFactoryAttributesDocumentResponseType getFactoryAttributesDocument(org.ggf.schemas.bes._2006._08.bes_factory.GetFactoryAttributesDocumentType parameters) throws InvalidRequestMessageFault {
        GetFactoryAttributesDocumentResponseType res=new GetFactoryAttributesDocumentResponseType();
        FactoryResourceAttributesDocumentType tmp=new FactoryResourceAttributesDocumentType();
        tmp.setIsAcceptingNewActivities(true);
        tmp.setCommonName("MTA-SZTAKI DCI-BRIDGE Service");
        tmp.setLongDescription("");
        tmp.setTotalNumberOfActivities(Base.getI().getRunningJobs());
        tmp.setLocalResourceManagerType("");
        tmp.setTotalNumberOfContainedResources(Integer.MAX_VALUE);
        res.setFactoryResourceAttributesDocument(tmp);
        return res;
    }
/**
 * Getting job id from query
 * @param pValue parameter xml
 * @param factory XML Dokumentm factory
 * @return egyedi job id
 * @throws javax.xml.parsers.ParserConfigurationException xml configuration error
 * @throws javax.xml.bind.JAXBException process error
 */
    private String getJobID(W3CEndpointReference pValue,DocumentBuilderFactory factory) throws ParserConfigurationException, JAXBException{
        DocumentBuilder builder= factory.newDocumentBuilder();
        Document d=builder.newDocument();
        Element e=d.createElement("guse");
        JAXBContext jbc=JAXBContext.newInstance(W3CEndpointReference.class);
        Marshaller m=jbc.createMarshaller();
        Node node=e.cloneNode(true);
        m.marshal(pValue, node);
        Node ns=node.getFirstChild().getChildNodes().item(1).getFirstChild();
        return ns.getTextContent();
    }


    private W3CEndpointReference createID(String pJobID) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException, TransformerException{
        String xmlString = "<PHONEBOOK><jobid>"+pJobID+"</jobid></PHONEBOOK>";
        DocumentBuilder parser =DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = parser.parse( new InputSource(new StringReader(xmlString)) );
        Transformer transformer =TransformerFactory.newInstance().newTransformer();
        Source source = new DOMSource( document );
        Result output = new StreamResult( System.out );
        transformer.transform( source, output );
        W3CEndpointReference tmp=new W3CEndpointReference(source);
        return tmp;
    }


}



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
 * Kxml.java
 * Workflow leiro parsolasa
 */

package hu.sztaki.lpds.wfi.service.zen.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Vector;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.*;
import hu.sztaki.lpds.wfs.com.WorkflowConfigErrorBean;
import java.util.Hashtable;


public class Kxml extends DefaultHandler
{
    private Vector<Job> xmlData=new Vector<Job>();
    private Hashtable<String,Vector<Rescue>> rescue = new Hashtable<String, Vector<Rescue>>();
    private String zenID;
    private String jobID;

    // workflow config hibakat tarolja
    // (portal felulet info gomb tablazat)
    private Vector<WorkflowConfigErrorBean> error = new Vector<WorkflowConfigErrorBean>();

    public Kxml(String pZenID){zenID=pZenID;}
    
    @Override
    public void startElement(String namespaceURI,String lName,String qName,Attributes attrs) throws SAXException
    {
        if(qName.equals("job"))
        {
            if(attrs.getValue("workflow")==null)
                xmlData.add(new Job(zenID,attrs.getValue("name")));
            else
                xmlData.add(new Job(zenID,attrs.getValue("name"),attrs.getValue("workflow")));
        }
        if(qName.equals("input"))
        {
            Input tmpi=new Input();
            for(int i=0;i<attrs.getLength();i++){tmpi.setAttribute(attrs.getQName(i),attrs.getValue(i));}
            ((Job)xmlData.get(xmlData.size()-1)).addInput(tmpi);
        }
        if(qName.equals("output"))
        {
//            String[] s=attrs.getValue("fromembed").split("/");
            Output tmpo=new Output();
            for(int i=0; i<attrs.getLength();i++)
                tmpo.setAttribute(attrs.getQName(i),attrs.getValue(i));
            ((Job)xmlData.get(xmlData.size()-1)).addOutput(tmpo);
        }
        if(qName.equals("jobstatus"))
        {
            if (rescue.get(attrs.getValue("job")) == null) {
                    rescue.put(attrs.getValue("job"), new Vector<Rescue>());
            }
            rescue.get(attrs.getValue("job")).add(new Rescue(Long.parseLong(attrs.getValue("pid")), new Hashtable<String,Integer>()));
            jobID=attrs.getValue("job");
        }
        if(qName.equals("joboutput"))
        {
            rescue.get(jobID).get(rescue.get(jobID).size()-1).getOutputs().put(attrs.getValue("port"), new Integer(attrs.getValue("count")));
        }
        // parse config error
        if(qName.equals("error"))
        {
            WorkflowConfigErrorBean eBean = new WorkflowConfigErrorBean();
            eBean.setJobName(attrs.getValue("job"));
            eBean.setPortID(attrs.getValue("port"));
            eBean.setErrorID(attrs.getValue("error"));
            error.addElement(eBean);
        }
    }
    
/**
 * Parsolt workflow adatok lekerdezese
 * @return Job-ok
 * @see Vector
 */    
    public Vector<Job> getXMLData(){return xmlData;}
    
/**
 * Parsolt Job statuszok lekerdezese
 * @return Job statuszok
 * @see Vector
 */
    public Hashtable<String,Vector<Rescue>> getRescueData(){return rescue;}

/**
 * Parsolt workflow config hibak lekerdezese
 * @return workflow config hibak statuszok
 * @see Vector
 */
    public Vector<WorkflowConfigErrorBean> getErrorData(){return error;}

}


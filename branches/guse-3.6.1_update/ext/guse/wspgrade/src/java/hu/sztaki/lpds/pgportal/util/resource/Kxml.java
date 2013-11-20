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
 * XML file kezelo osztaly
 */

package hu.sztaki.lpds.pgportal.util.resource;

import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * XML file kezelo osztaly
 *
 * @author krisztian karoczkai
 */
public class Kxml extends DefaultHandler
{
    public Kxml(String pDir,String pFile)
    {
        wsdldir=pDir;
        wsdlfile=pFile;
    }
    
    private Vector xmlData=new Vector();
    
    private String url="";
    
    private String wsdlfile="";
    
    private String wsdldir="";
    
    public void startDocument()throws SAXException{}
    
    public void endDocument()throws SAXException{}

    public void startElement(String namespaceURI,String lName,String qName,Attributes attrs) throws SAXException
    {
        if(qName.equals("wsdl:definitions"))
            url=attrs.getValue("targetNamespace");
        
        if(qName.equals("wsdl:operation"))
        {
            xmlData.add(new ServiceResourceBean(wsdldir,wsdlfile,url,attrs.getValue("name")));
        }
    }
    
    public void endElement(String namespaceURI,String sName,String qName)throws SAXException{}
    
    public void characters(char buf[], int offset, int len) throws SAXException{}
    
    public Vector getXMLData(){return xmlData;}
    
}

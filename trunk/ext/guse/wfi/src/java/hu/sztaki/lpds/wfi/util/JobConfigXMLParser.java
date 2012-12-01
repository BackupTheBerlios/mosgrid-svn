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
 * JobConfigXMLParser.java
 */

package hu.sztaki.lpds.wfi.util;

import org.xml.sax.*;

import org.xml.sax.ext.DefaultHandler2;

public class JobConfigXMLParser extends DefaultHandler2 
{

    private JobConfig data=new JobConfig();
    private String pNameValue=null;
    private String pQnameInputAttr=null;
    private String pValueInputAttr=null;
/**
 * Class constructor
 */    
    public JobConfigXMLParser() {}
    
    @Override
    public void startElement(String namespaceURI,String lName,String qName,Attributes attrs) throws SAXException
    {
        if(qName.equals("job-property")){data.addProp(attrs.getQName(0),attrs.getValue(0));}
        if(qName.equals("job-description")){data.addDesc(attrs.getQName(0),attrs.getValue(0));}
        if(qName.equals("input-property"))
        {
            pNameValue=attrs.getValue("name");
            pQnameInputAttr=attrs.getQName(1);
            pValueInputAttr="";
        }
        if(qName.equals("output-property")){data.addOutput(attrs.getValue("name"),attrs.getQName(1),attrs.getValue(1));}
    }

    @Override
    public void endElement(String arg0, String arg1, String arg2) throws SAXException 
    {
        if(arg2.equals("input-property"))
        {
            data.addInput(pNameValue,pQnameInputAttr,pValueInputAttr);
            pNameValue=null;
        }
    }

    @Override
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException 
    {
        if(pNameValue!=null)
        {
            pValueInputAttr=pValueInputAttr.concat(new String(arg0,arg1,arg2));
            data.addInput(pNameValue,pQnameInputAttr,pValueInputAttr);
        }
    }
    
    
    
/**
 * Job config lekerdezese
 * @return JobConfig
 * @see JobConfig
 */    
    public JobConfig getJobConfig(){return data;}
}

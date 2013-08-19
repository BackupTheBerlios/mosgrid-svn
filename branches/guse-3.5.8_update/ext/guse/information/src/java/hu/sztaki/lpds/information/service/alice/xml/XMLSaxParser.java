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
 *
 * Local service register, handling XML
 */

package hu.sztaki.lpds.information.service.alice.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Vector;
import hu.sztaki.lpds.information.com.ServiceType;

public class XMLSaxParser extends DefaultHandler
{
    
    private Vector xmlData=new Vector();
    private boolean readText=false;
    private String keyTag=null;
    
    @Override
    public void startElement(String namespaceURI,String lName,String qName,Attributes attrs) throws SAXException
    {
        try
        {
            ServiceType t = new ServiceType();
            for(int i=0; i<attrs.getLength();i++){
                t.set(attrs.getQName(i).trim(),attrs.getValue(i).trim());
            }
            xmlData.add(t);
        }
        catch(Exception e){e.printStackTrace();}
    }
    
    public Vector getXMLData(){return xmlData;}

}


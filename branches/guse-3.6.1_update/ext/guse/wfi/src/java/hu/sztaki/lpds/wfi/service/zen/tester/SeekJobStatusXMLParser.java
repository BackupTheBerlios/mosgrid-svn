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

package hu.sztaki.lpds.wfi.service.zen.tester;

import java.util.Hashtable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author krisztian karoczkai
 */
public class SeekJobStatusXMLParser extends DefaultHandler
{

    private Hashtable<String,JobBean> anomalia=new Hashtable<String,JobBean>();
    private String tmp;

    public SeekJobStatusXMLParser(Hashtable<String,JobBean>value) {anomalia=value; }
    
    
    @Override    
    public void startElement(String namespaceURI,String lName,String qName,Attributes attrs) throws SAXException
    {
        tmp=attrs.getValue("job")+"."+attrs.getValue("pid");
        if(qName.equals("status"))
        {
            if(anomalia.get(tmp)!=null) 
            {
                anomalia.get(tmp).addStatus(new StatusBean(attrs.getValue("status"), attrs.getValue("time")));
            }
                
        }
    }

    public Hashtable<String,JobBean> getData(){return anomalia;}

}

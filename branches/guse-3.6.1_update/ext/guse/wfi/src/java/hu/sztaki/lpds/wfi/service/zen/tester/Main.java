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
 * Teszter funkcio inditasa
 */

package hu.sztaki.lpds.wfi.service.zen.tester;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author krisztian karoczkai
 */

public class Main 
{

    public static void main(String[] args)
    {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try
            {
                DoubleSubmitXMLParser handler=new DoubleSubmitXMLParser();
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse(new File("/home/krisztian/lpds/wfilogg/cgrid/1233070870184zentest.logg"), handler);
//                saxParser.parse(new File("/home/krisztian/lpds/wfilogg/cgrid/submit.xml"), handler);
                
                SeekJobStatusXMLParser handler0=new SeekJobStatusXMLParser(handler.getData());
                saxParser = factory.newSAXParser();
                saxParser.parse(new File("/home/krisztian/lpds/wfilogg/cgrid/1232454983252zentest.logg"), handler0);

                Hashtable<String,JobBean> res=handler0.getData();
                
                Enumeration<JobBean> enm =res.elements();
                while(enm.hasMoreElements())
                    System.out.println(enm.nextElement().toString()); 
                
            }
            catch(Exception e){e.printStackTrace();}
            

    }
}

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
package hu.sztaki.lpds.wfs.service.angie.utils;

import java.io.StringWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * XML utils... (helper class)
 *
 * @author lpds
 */
public class XMLUtils {
    
    private static XMLUtils instance = new XMLUtils();
    
    public XMLUtils() {
    }
    
    /**
     * XMLUtils peldanyt ad vissza.
     *
     * @return
     */
    public static XMLUtils getInstance() {
        return instance;
    }
    
    /**
     * A workflow element objektumbol keszit egy stringet.
     *
     * @param Document doc parent document
     * @param Element workflow element
     * @return workflow xml leiro string
     */
    public String transformElementToString(Document doc, Element element) throws Exception {
        // Create dom document
        doc.appendChild(element);
        // Generate XML output to string
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        // We want to pretty format the XML output...
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        // transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        // transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        // source: doc
        DOMSource source = new DOMSource(doc);
        // doc to string
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        transformer.transform(source, streamResult);
        String xmlstr = stringWriter.toString();
        // System.out.println("xmlstr : " + xmlstr);
        return xmlstr;
    }
    
}

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
 * Metabroker hasznalatat megvalosito kliens
 */

package hu.sztaki.lpds.metabroker.client;

import hu.sztaki.lpds.dcibridge.service.Base;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.xml.namespace.QName;
import server.MBServiceService;
import sun.misc.BASE64Encoder;

/**
 * @author krisztian karoczkai
 */
public class MBServiceClient {
    private MBServiceService mb;

/**
 * Konstruktor
 * @param wsdl servizhez tartozo wsdl elerhetosege
 * @throws java.net.MalformedURLException hibas url formatum, letrehozasi hiba
 */
    public MBServiceClient(URL wsdl) throws Exception {
            mb=new MBServiceService(wsdl,new QName("http://server/", "MBServiceService"));
    }

    public List<String> getOptimalResource(String pJobID) throws Exception{
        File f=new File(Base.getI().getJobDirectory(pJobID)+"outputs/guse.jsdl");
        String ln="";
        StringBuffer jsdl=new StringBuffer();

        BufferedReader br=new BufferedReader(new FileReader(f));

            while((ln=br.readLine())!=null)
                jsdl.append(ln+"\n");
            
            BASE64Encoder bs=new BASE64Encoder();
            List<String> res=mb.getMBServicePort().submitJSDL(bs.encode(jsdl.toString().getBytes()));
//            for(String t:res)System.out.println("-"+t);
            return res;
    }

    
}

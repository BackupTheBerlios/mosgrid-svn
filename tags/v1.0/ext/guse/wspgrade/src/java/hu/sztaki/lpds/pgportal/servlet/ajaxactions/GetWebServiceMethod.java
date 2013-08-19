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
 * GetWebServiceMethod.java
 * Querying Web service methods
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions;


import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;

/**
 *Querying Web service methods
 *
 * @author krisztian karoczkai
 */
public class GetWebServiceMethod extends BASEActions
{ 
        
    @Override
    public String getDispacher(Hashtable pParams){return null;}
//    public Hashtable getParameters(Hashtable pParams){return null;}
    @Override
    public Hashtable getParameters(Hashtable pParams){return null;}
    @Override
    public String getOutput(Hashtable pParams)
    {
        List<Middleware> resources=(List<Middleware>) getSessionVariable("resources");

        Vector<String> tmp=ConfigHandler.getServices(resources, "WebService", (String)pParams.get("j"),(String)pParams.get("p"));

        String res="job_servicemethod::::";
        for(String t:tmp)
            res=res+t+"::";
        return res;        
    }   
}

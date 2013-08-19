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
 * ServiceResourceBean.java
 * Publikus web Service leiro Bean
 */

package hu.sztaki.lpds.pgportal.util.resource;

import java.util.Hashtable;

/**
 * Publikus web Service leiro Bean
 *
 * @author krisztian karoczkai
 */
public class ServiceResourceBean {
    private Hashtable data=new Hashtable();
    
    /**
     * Class constructor
     */
    public ServiceResourceBean() {}
    
    /**
     * Class constructor
     * @param pType Service tipusa
     * @param pFile Service WSDL File-ja
     * @param pURL Service URL
     * @param pMethod Service metodus
     */
    public ServiceResourceBean(String pType,String pFile, String pURL, String pMethod) {
        data.put("type", pType);
        data.put("file", pFile);
        data.put("url", pURL);
        data.put("method", pMethod);
    }
    
    /**
     * Service tipus lekerdezese
     * @return Service tipusa
     */
    public String getType(){return (String)data.get("type");}
    
    /**
     * Service wsdl file lekerdezese
     * @return Service host-ja
     */
    public String getFile(){return (String)data.get("file");}
    
    /**
     * Service URL lekerdezese
     * @return Service neve
     */
    public String getUrl(){return (String)data.get("url");}
    
    /**
     * Service metodus lekerdezese
     * @return Service metodus
     */
    public String getMethod(){return (String)data.get("method");}

}

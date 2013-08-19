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
 * JAX-WS Webservice client DCI-Bridge eroforaslekerdezeshez
 */

package hu.sztaki.lpds.dcibridge.client;

import dci.data.Middleware;
import hu.sztaki.lpds.dcibridge.config.ResourceConfigurationService;
import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import java.net.URL;
import java.util.List;
import javax.xml.namespace.QName;


/**
 * @author krisztian karoczkai
 */
public class ResourceConfigJaxWsIMPL implements ResourceConfigurationFace,BaseCommunicationFace{
    private String webApplication;
    private String wsdl;
    ResourceConfigurationService client;

    @Override
    public List<Middleware> get() throws Exception {
        createClientIfNotExist();
        return client.getResourceConfigurationPort().get();
    }

    @Override
    public void setServiceURL(String value) {webApplication=value;}

    @Override
    public void setServiceID(String value) {wsdl=value;}

    private void createClientIfNotExist() throws Exception{
        if(webApplication==null || wsdl==null) throw new NullPointerException("Service URL is null, call setServiceURL([webapplication url]); setServiceID([wsdl resource])");
        if(client==null) client=new ResourceConfigurationService(new URL(webApplication+wsdl),new QName("http://config.dcibridge.lpds.sztaki.hu/", "ResourceConfigurationService"));
    }

}

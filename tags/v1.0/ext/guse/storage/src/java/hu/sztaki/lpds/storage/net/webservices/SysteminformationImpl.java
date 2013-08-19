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

/**
 * SystemInformation service for Storage
 */
package hu.sztaki.lpds.storage.net.webservices;

import hu.sztaki.lpds.service.SystemInformation;
import javax.jws.WebService;

/**
 * @author krisztian karoczkai
 */
@WebService(serviceName = "SystemInformationService", portName = "SystemInformationPort", endpointInterface = "hu.sztaki.lpds.service.SystemInformation", targetNamespace = "http://service.lpds.sztaki.hu/", wsdlLocation = "WEB-INF/wsdl/SysteminformationImpl/guse.hu/storage/services/monitoring/w3.wsdl")
public class SysteminformationImpl implements SystemInformation {

    public java.util.List<hu.sztaki.lpds.service.CommandValueBean> getInformation(java.util.List<java.lang.String> pCommands) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean ping() {
        return true;
    }

    public java.util.List<java.lang.String> getAllInformationCommands() {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean serviceTest(java.util.List<hu.sztaki.lpds.service.CommandValueBean> pCommands) {
        return true;
    }

}

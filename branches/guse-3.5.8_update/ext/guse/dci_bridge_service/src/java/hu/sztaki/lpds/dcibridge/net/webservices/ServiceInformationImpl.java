/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.dcibridge.net.webservices;

import hu.sztaki.lpds.service.SystemInformation;
import javax.jws.WebService;

/**
 *
 * @author csig
 */
@WebService(serviceName = "SystemInformationService", portName = "SystemInformationPort", endpointInterface = "hu.sztaki.lpds.service.SystemInformation", targetNamespace = "http://service.lpds.sztaki.hu/", wsdlLocation = "WEB-INF/wsdl/ServiceInformationImpl/guse.hu/storage/services/monitoring/w3.wsdl")
public class ServiceInformationImpl implements SystemInformation {

    public java.util.List<hu.sztaki.lpds.service.CommandValueBean> getInformation(java.util.List<java.lang.String> pCommands) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean ping() {
        //TODO implement this method
        return true;
    }

    public java.util.List<java.lang.String> getAllInformationCommands() {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean serviceTest(java.util.List<hu.sztaki.lpds.service.CommandValueBean> pCommands) {
        //TODO implement this method
        return true;
    }

}

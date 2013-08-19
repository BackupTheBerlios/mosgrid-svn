/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.dcibridge.test;

import hu.sztaki.lpds.dcibridge.client.ResourceConfigJaxWsIMPL;

/**
 * @author krisztian karoczkai
 */
public class TestResourceConfigService {

    public static void main(String[] args){
        ResourceConfigJaxWsIMPL client=new ResourceConfigJaxWsIMPL();
        client.setServiceID("http://192.168.143.151:8080/dci_bridge_service/ResourceConfiguration?wsdl");
        client.setServiceURL("");
        try{client.get();}
        catch(Exception e){e.printStackTrace();}
    }
}

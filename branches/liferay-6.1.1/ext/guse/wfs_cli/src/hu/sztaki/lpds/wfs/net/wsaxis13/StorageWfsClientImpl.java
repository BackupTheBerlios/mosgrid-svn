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
 * Storage szerviz es a wfs kozotti kommunikacio
 */
package hu.sztaki.lpds.wfs.net.wsaxis13;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.wfs.inf.StorageWfsClient;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import hu.sztaki.lpds.wfs.com.*;

/**
 * @author lpds
 */
public class StorageWfsClientImpl  implements StorageWfsClient {
    
    private String serviceURL="";
    
    private String serviceID="";
/**
 * Ures konstruktor
 */
    public StorageWfsClientImpl(){}
/**
 * Konstruktor
 * @param pServiceUrl serviceURL
 */
    public StorageWfsClientImpl(String pServiceUrl){}
    
/**
 * @see BaseCommunicationFace#setServiceURL(java.lang.String)
 */
@Override
    public void setServiceURL(String value){serviceURL=value;}
    
/**
 * @see BaseCommunicationFace#setServiceID(java.lang.String)
 */
@Override
    public void setServiceID(String value){serviceID=value;}
    
/**
 * @see StorageWfsClient#getWorkflowXML(hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean)
 */
@Override
    public String getWorkflowXML(StorageWorkflowNamesBean value) {
        String retWorkflowXML = new String();
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getWorkflowXML");
            
            qn=new QName( "urn:BeanService", "StorageWorkflowNamesBean" );
            call.registerTypeMapping(StorageWorkflowNamesBean.class, qn,
                    new BeanSerializerFactory(StorageWorkflowNamesBean.class, qn),
                    new BeanDeserializerFactory(StorageWorkflowNamesBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_STRING);
            
            retWorkflowXML = (String) call.invoke( new Object[] { value } );
            
            if (retWorkflowXML == null) {
                throw new Exception("Not valid workflowXML ! retWorkflowXML = (" + retWorkflowXML + ")");
            }

        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return retWorkflowXML;
    }
    
/**
 * @see StorageWfsClient#setWorkflowXML(hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean)
 */
@Override
    public String setWorkflowXML(StorageWorkflowNamesBean value) {
        String ret = new String("");
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("setWorkflowXML");
            
            qn=new QName( "urn:BeanService", "StorageWorkflowNamesBean" );
            call.registerTypeMapping(StorageWorkflowNamesBean.class, qn,
                    new BeanSerializerFactory(StorageWorkflowNamesBean.class, qn),
                    new BeanDeserializerFactory(StorageWorkflowNamesBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_STRING);
            
            ret = (String) call.invoke( new Object[] { value } );
            
            if(ret == null) {
                throw new Exception("Not valid response ! ret = (" + ret + ")");
            }
            return ret;
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return ret;
    }
    
}

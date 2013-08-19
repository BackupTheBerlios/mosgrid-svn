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
 * AXIS 1.4 based webservice client implementation for the communication between the storage and the portal
 */

package hu.sztaki.lpds.portal.net.wsaxis13;

import hu.sztaki.lpds.portal.com.StoragePortalWorkflowNamesBean;
import hu.sztaki.lpds.portal.inf.StoragePortalClient;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

/**
 * @author krisztian
 */
public class StoragePortalClientImpl implements StoragePortalClient {
    private String serviceURL="";
    private String serviceID="";

/**
 * @see hu.sztaki.lpds.portal.inf.StoragePortalClient
 */
@Override
    public void setServiceURL(String value){serviceURL=value;}

/**
 * @see hu.sztaki.lpds.portal.inf.StoragePortalClient
 */
@Override
    public void setServiceID(String value){serviceID=value;}

/**
 * @see hu.sztaki.lpds.portal.inf.StoragePortalClient
 */
@Override
    public void newOccupied(ComDataBean value)throws Exception {
        Service service;
        Call call;
        QName qn;
        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("newOccupied");
        qn=new QName( "urn:BeanService", "ComDataBean" );
        call.registerTypeMapping(ComDataBean.class, qn,
                new BeanSerializerFactory(ComDataBean.class, qn),
                new BeanDeserializerFactory(ComDataBean.class, qn));
        call.addParameter( "arg1", qn, ParameterMode.IN );
        call.setReturnType(Constants.XSD_BOOLEAN);
        call.invoke( new Object[] { value } );
    }
    
/**
 * @see hu.sztaki.lpds.portal.inf.StoragePortalClient
 */
@Override
    public Boolean newWorkflowNames(StoragePortalWorkflowNamesBean value) throws Exception {
        Boolean ret = new Boolean(false);
        try {
            Service service;
            Call call;
            QName qn;
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("newWorkflowNames");
            qn=new QName( "urn:BeanService", "StoragePortalWorkflowNamesBean" );
            call.registerTypeMapping(StoragePortalWorkflowNamesBean.class, qn,
                    new BeanSerializerFactory(StoragePortalWorkflowNamesBean.class, qn),
                    new BeanDeserializerFactory(StoragePortalWorkflowNamesBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            ret = (Boolean) call.invoke( new Object[] { value } );
        } catch (Exception fault) {
            System.out.println("HIBA"+fault.toString());fault.printStackTrace();
        }
        return ret;
    }
    
 /**
 * @see hu.sztaki.lpds.portal.inf.StoragePortalClient
 */
@Override
   public String checkWorkflowNames(StoragePortalWorkflowNamesBean value) throws Exception {
        String ret = new String("");
        try {
            Service service;
            Call call;
            QName qn;
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("checkWorkflowNames");
            qn=new QName( "urn:BeanService", "StoragePortalWorkflowNamesBean" );
            call.registerTypeMapping(StoragePortalWorkflowNamesBean.class, qn,
                    new BeanSerializerFactory(StoragePortalWorkflowNamesBean.class, qn),
                    new BeanDeserializerFactory(StoragePortalWorkflowNamesBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_STRING);
            ret = (String) call.invoke( new Object[] { value } );
        } catch (Exception fault) {
            System.out.println("HIBAitt:"+fault.toString());fault.printStackTrace();
            ret = fault.getLocalizedMessage();
            fault.printStackTrace();
        }
        return ret;
    }
    
}

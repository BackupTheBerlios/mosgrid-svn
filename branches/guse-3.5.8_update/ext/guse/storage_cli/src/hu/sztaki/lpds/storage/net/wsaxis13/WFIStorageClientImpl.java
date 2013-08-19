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
 * Communication between the WFI and the storage based on AXIS1.4
 *
 */

package hu.sztaki.lpds.storage.net.wsaxis13;

import hu.sztaki.lpds.storage.inf.WFIStorageClient;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import hu.sztaki.lpds.storage.com.*;

/**
 *
 * @author krisztian
 */
public class WFIStorageClientImpl implements WFIStorageClient{
    private String serviceURL="";    
    private String serviceID="";
    /**
     * Sets the URL of the service to be accessed
     * @param value Access point of the service to be used
     */
    public void setServiceURL(String value){serviceURL=value;}
    /**
     * Sets the ID of the service
     * @param value The ID of the service to be used
     */
    public void setServiceID(String value){serviceID=value;}
/**
 * @see WFIStorageClient#copyFile(hu.sztaki.lpds.storage.com.FileBean, hu.sztaki.lpds.storage.com.FileBean)
 */
    public void copyFile(FileBean pSRC, FileBean pDest) throws Exception {}
/**
 * @see WFIStorageClient#getNumberOfFileInDirectory(hu.sztaki.lpds.storage.com.FileBean)
 */
    public long getNumberOfFileInDirectory(FileBean pDirectory) throws Exception {
        long res=0;
        Service service;
        Call call;
        QName qn;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("getNumberOfFileInDirectory");

        qn=new QName( "urn:BeanService", "FileBean" );
        call.registerTypeMapping(FileBean.class, qn,
                    new BeanSerializerFactory(FileBean.class, qn),
                    new BeanDeserializerFactory(FileBean.class, qn));
        call.addParameter( "arg1", qn, ParameterMode.IN );
        call.setReturnType(Constants.XSD_LONG);

        res =(Long)call.invoke( new Object[] { pDirectory } );
        return res;
    }
/**
 * @see WFIStorageClient#ifTest(hu.sztaki.lpds.storage.com.IfBean)
 */
    public boolean ifTest(IfBean pValue) throws Exception {
        Boolean res=false;
        Service service;
        Call call;
        QName qn;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("ifTest");

        qn=new QName( "urn:BeanService", "IfBean" );
        call.registerTypeMapping(IfBean.class, qn,
                    new BeanSerializerFactory(IfBean.class, qn),
                    new BeanDeserializerFactory(IfBean.class, qn));
        call.addParameter( "arg1", qn, ParameterMode.IN );
        call.setReturnType(Constants.XSD_BOOLEAN);

        res =(Boolean)call.invoke( new Object[] { pValue } );
        return res.booleanValue();
    }

}


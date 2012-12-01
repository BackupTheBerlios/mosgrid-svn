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
 * AXIS 1.4 based webservice client implementation for the communication between the WFI and the portal
 *
 */

package hu.sztaki.lpds.portal.net.wsaxis13;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import java.util.Vector;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import hu.sztaki.lpds.portal.inf.WfsPortalClient;

/**
 * @author krisztian
 */
public class WfsPortalClientImpl implements WfsPortalClient
{
    private String serviceURL=""; 
    private String serviceID="";
/**
 * @see hu.sztaki.lpds.portal.inf.WfsPortalClient
 */
@Override
    public void setServiceURL(String value){serviceURL=value;}
    
/**
 * @see hu.sztaki.lpds.portal.inf.WfsPortalClient
 */
@Override
    public void setServiceID(String value){serviceID=value;}
   
/**
 * @see hu.sztaki.lpds.portal.inf.WfsPortalClient
 */
@Override
    public void setStatus(JobStatusBean pData) throws Exception
    {
           Service service;
           Call call;
           QName qn;
           service = new Service();
           call = (Call) service.createCall();
           call.setTargetEndpointAddress(serviceURL+serviceID);
           call.setOperationName("setStatus");
           qn=new QName( "urn:BeanService", "JobStatusBean" );
           call.registerTypeMapping(JobStatusBean.class, qn,
                     new BeanSerializerFactory(JobStatusBean.class, qn),
                     new BeanDeserializerFactory(JobStatusBean.class, qn));
           call.addParameter( "arg1", qn, ParameterMode.IN );
           call.setReturnType(Constants.XSD_STRING);
           call.invoke( new Object[] {pData } );
    }
/**
 * @see hu.sztaki.lpds.portal.inf.WfsPortalClient
 */
@Override
    public void setCollectionStatus(Vector pData) throws Exception
    {
        Service service;
        Call call;
        QName qn,qn0;
        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("setCollectionStatus");
        qn=new QName( "urn:BeanService", "JobStatusBean" );
        call.registerTypeMapping(JobStatusBean.class, qn,
                  new BeanSerializerFactory(JobStatusBean.class, qn),
                  new BeanDeserializerFactory(JobStatusBean.class, qn));
        qn0=new QName( "ns1:AxisVector", "Vector" );
        call.registerTypeMapping(Vector.class, qn0,
                  new VectorSerializerFactory(Vector.class, qn0),
                  new VectorDeserializerFactory(Vector.class,  qn0));
        call.addParameter( "arg1", qn0, ParameterMode.IN );
        call.setReturnType(Constants.XSD_STRING);
        
        call.invoke("setCollectionStatus", new Object[] {pData } );
    }
    
}

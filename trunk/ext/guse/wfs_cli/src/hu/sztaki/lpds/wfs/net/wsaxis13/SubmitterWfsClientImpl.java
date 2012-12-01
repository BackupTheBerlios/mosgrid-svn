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
 * Submitter es a WFS kozotti kommunikacio implementacioja
 */

package hu.sztaki.lpds.wfs.net.wsaxis13;

import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.inf.SubmitterWfsClient;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;

/**
 * @author krisztian
 */
public class SubmitterWfsClientImpl implements SubmitterWfsClient
{
       private String serviceURL=""; 
   private String serviceID="";
/**
 * Ures konstruktor
 */
    public SubmitterWfsClientImpl() {}

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
 * @see SubmitterWfsClient#getSubmitData(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public String getSubmitData(ComDataBean pValue) throws Exception{
           Service service;
           Call call;
           QName qn;
           service = new Service();
           call = (Call) service.createCall();
           call.setTargetEndpointAddress(serviceURL+serviceID);
           call.setOperationName("getSubmitData");
           qn=new QName( "urn:BeanService", "ComDataBean" );
           call.registerTypeMapping(ComDataBean.class, qn,
                     new BeanSerializerFactory(ComDataBean.class, qn),
                     new BeanDeserializerFactory(ComDataBean.class, qn));
           call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_STRING);
           return (String)call.invoke( new Object[] {pValue } );
   }
    
}
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
 * SubmitterWfiClientImpl.java
 * Submitter Axis1.x client for WFI call
 *
 */

package hu.sztaki.lpds.wfi.net.wsaxis13;

import hu.sztaki.lpds.wfs.com.JobStatusBean;
import java.util.Vector;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import hu.sztaki.lpds.wfi.inf.SubmitterWfiClient;


public class SubmitterWfiClientImpl implements SubmitterWfiClient
{
   private String serviceURL;
   private String serviceID;
   private Call call;
   private JobStatusBean tmp;

@Override
   public void setServiceURL(String value){serviceURL=value;}    
@Override
   public void setServiceID(String value){serviceID=value;}
@Override
   public boolean setStatus(JobStatusBean pData) throws Exception
   {
        tmp=pData;
            Service service;
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
            call.setReturnType(Constants.XSD_BOOLEAN);

            return ((Boolean)call.invoke(new Object[] {pData })).booleanValue();        
   }
@Override
   public boolean setCollectionStatus(Vector pData) throws Exception
   {
        {
            Service service;
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
                      new VectorDeserializerFactory(Vector.class, qn0));
            call.addParameter( "arg1", qn0, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            return ((Boolean)call.invoke(new Object[] {pData })).booleanValue();        
        } 
   }
@Override
    public int callJob(String pGridID, int pCount, int pTimeOut) throws Exception
    {
        Service service;
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("callJob");
            call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
            call.addParameter( "arg2", Constants.XSD_INT, ParameterMode.IN );
            call.addParameter( "arg3", Constants.XSD_INT, ParameterMode.IN );
            call.setReturnType(Constants.XSD_INT);
            return ((Integer)call.invoke(new Object[] {pGridID,pCount,pTimeOut }));
        }

}

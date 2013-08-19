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
 * WFI es wfs kozotti kommunikacio AXIS 1.x alapokon
 *
 */

package hu.sztaki.lpds.wfs.net.wsaxis13;

import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.ResourceCollectionBean;
import hu.sztaki.lpds.wfs.inf.WfiWfsClient;
import java.util.HashMap;
import java.util.Vector;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import hu.sztaki.lpds.wfs.com.JobStatusBean;

/**
 * @author krisztian
 */

public class WfiWfsClientImpl implements WfiWfsClient{
   private String serviceURL=""; 
   private String serviceID="";
/**
 * Ures konstruktor
 */
   public WfiWfsClientImpl() {}
   
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
 * @see WfiWfsClient#getWfiXML(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
   public String getWfiXML(ComDataBean pData) throws Exception{
       String res="";
           Service service;
           Call call;
           QName qn,qn1;
           service = new Service();
           call = (Call) service.createCall();
           call.setTargetEndpointAddress(serviceURL+serviceID);
           call.setOperationName("getWfiXML");
           qn=new QName( "urn:BeanService", "ComDataBean" );
           call.registerTypeMapping(ComDataBean.class, qn,
                     new BeanSerializerFactory(ComDataBean.class, qn),
                     new BeanDeserializerFactory(ComDataBean.class, qn));
           qn1=new QName("urn:wfsportalservice","ns1:AxisHashMap");
           call.registerTypeMapping(HashMap.class, qn1,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));
           
           call.addParameter( "arg1", qn, ParameterMode.IN );
           call.setReturnType(Constants.XSD_STRING);
           res = (String)call.invoke( new Object[] {pData } );
       return res;
   }
  
/**
 * @see WfiWfsClient#getWfiRescueXML(hu.sztaki.lpds.wfs.com.ComDataBean, java.lang.String)
 */
@Override
   public String getWfiRescueXML(ComDataBean pData, String index) throws Exception{
       String res="";
           Service service;
           Call call;
           QName qn,qn1;
           service = new Service();
           call = (Call) service.createCall();
           call.setTargetEndpointAddress(serviceURL+serviceID);
           call.setOperationName("getWfiRescueXML");
           qn=new QName( "urn:BeanService", "ComDataBean" );
           call.registerTypeMapping(ComDataBean.class, qn,
                     new BeanSerializerFactory(ComDataBean.class, qn),
                     new BeanDeserializerFactory(ComDataBean.class, qn));
           qn1=new QName("urn:wfsportalservice", "ns1:AxisHashMap");
           call.registerTypeMapping(HashMap.class, qn1,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));

           call.addParameter( "arg1", qn, ParameterMode.IN );
           call.addParameter( "arg2", Constants.XSD_STRING, ParameterMode.IN );
           call.setReturnType(Constants.XSD_STRING);
           res = (String)call.invoke( new Object[] {pData, index} );
       return res;
   }

/**
 * @see WfiWfsClient#setStatus(hu.sztaki.lpds.wfs.com.JobStatusBean)
 */
@Override
   public void setStatus(JobStatusBean pData) throws Exception{
       
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
           call.invoke( new Object[] {pData });
        
   } 
/**
 * @see WfiWfsClient#setCollectionStatus(java.util.Vector)
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
           qn0=new QName( "ns1:AxisPortalVector", "Vector" );
           call.registerTypeMapping(Vector.class, qn0,
                     new VectorSerializerFactory(Vector.class, qn0),
                     new VectorDeserializerFactory(Vector.class, qn0));
           call.addParameter( "arg1", qn0, ParameterMode.IN );
           call.setReturnType(Constants.XSD_STRING);
           String res = (String) call.invoke( new Object[] {pData} );
           if (!"".equals(res)) {
               throw new Exception(res);
           }
   }
/**
 * @see WfiWfsClient#getResourceType(hu.sztaki.lpds.wfs.com.JobStatusBean)
 */
@Override
   public String getResourceType(JobStatusBean pData){
       String res="";
       try{
           Service service;
           Call call;
           QName qn;
           service = new Service();
           call = (Call) service.createCall();
           call.setTargetEndpointAddress(serviceURL+serviceID);
           call.setOperationName("getResourceType");
           qn=new QName( "urn:BeanService", "JobStatusBean" );
           call.registerTypeMapping(JobStatusBean.class, qn,
                     new BeanSerializerFactory(JobStatusBean.class, qn),
                     new BeanDeserializerFactory(JobStatusBean.class, qn));
           call.addParameter( "arg1", qn, ParameterMode.IN );
           call.setReturnType(Constants.XSD_STRING);
           res=(String)call.invoke( new Object[] {pData });
       } 
       catch (Exception fault){
            System.out.println("HIBA");
            System.out.println("HIBA"+fault.toString());fault.printStackTrace();
       }
       return res;
   } 
/**
 * @see WfiWfsClient#getCollectionResourceType(hu.sztaki.lpds.wfs.com.ResourceCollectionBean)
 */
@Override
    public HashMap getCollectionResourceType(ResourceCollectionBean pData){
       HashMap res=new HashMap();
       Object p=new String("vvvvv");
       try{
           Service service;
           Call call;
           QName qn,qn1,qn2;
           service = new Service();
           call = (Call) service.createCall();
           call.setTargetEndpointAddress(serviceURL+serviceID);
           call.setOperationName("getCollectionResourceType");
           qn=new QName( "urn:wfswfiservice", "ResourceCollectionBean" );
           call.registerTypeMapping(ResourceCollectionBean.class, qn,
                     new BeanSerializerFactory(ResourceCollectionBean.class, qn),
                     new BeanDeserializerFactory(ResourceCollectionBean.class, qn));
           qn1=new QName("urn:wfswfiservice","ns1:AxisVector");
           call.registerTypeMapping(Vector.class, qn1,
                     new BeanSerializerFactory(org.apache.axis.encoding.ser.VectorSerializerFactory.class, qn1),
                     new BeanDeserializerFactory(org.apache.axis.encoding.ser.VectorDeserializerFactory.class, qn1));
           qn2=new QName("urn:wfswfiservice","ns1:AxisHashMap");
           call.registerTypeMapping(HashMap.class, qn2,
                     new VectorSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn2),
                     new VectorDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn2));


           call.addParameter( "arg1", qn, ParameterMode.IN );
           call.setReturnType(qn2, HashMap.class);
           res=(HashMap)call.invoke( new Object[] {pData});
       } 
       catch (Exception fault){
            System.out.println("HIBA:: "+fault.toString());fault.printStackTrace();
       }
       return res;
    }

/**
 * @see WfiWfsClient#getSubmitData(hu.sztaki.lpds.wfs.com.ComDataBean)
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

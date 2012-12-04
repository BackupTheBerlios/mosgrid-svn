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
package hu.sztaki.lpds.information.net.wsaxis13;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.inf.InformationClient;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

/**
 * @author krisztian karoczkai
 */
public class InformationClientImpl implements InformationClient
{
    private String serviceURL=""; 
    private String serviceID="";
   
/**
 * Sets the address of the service needed to be accessed
 * @param value access point of the service to be used
 */
    public void setServiceURL(String value){serviceURL=value;}    
    
/**
 * Sets the ID of the service needed to be accessed
 * @param value reference ID of the service to be used
 */
   public void setServiceID(String value){serviceID=value;}
       
/**
 * Class constructor
 */
    public InformationClientImpl() {}
    
/**
 * service query
 * @param pStype searched service type
 * @param pFrom search source
 * @param pParam search parameter hash
 * @param pFault prohibited services
 * @return Service descriptor object
 * @see serviceType
 * @throws Exception error
 */
    public ServiceType getService(String pStype, String pFrom, Hashtable pParam, Vector pFault) throws Exception
    {
      Service service;
      Call call;
      QName qn,qn1,qn0;

      service = new Service();
      call = (Call) service.createCall();
      call.setTargetEndpointAddress(serviceURL+serviceID);
      call.setOperationName("getService");

      qn=new QName( "urn:BeanService", "ServiceType" );
      call.registerTypeMapping(ServiceType.class, qn,
                      new BeanSerializerFactory(ServiceType.class, qn),
                      new BeanDeserializerFactory(ServiceType.class, qn));
      qn0=new QName("urn:wfsportalservice","ns1:AxisHashMap");
      call.registerTypeMapping(HashMap.class, qn0,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn0),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn0));
            
      qn1=new QName( "ns1:AxisVector", "Vector" );
      call.registerTypeMapping(Vector.class, qn1,
                      new VectorSerializerFactory(org.apache.axis.encoding.ser.VectorSerializerFactory.class, qn1),
                      new VectorDeserializerFactory(org.apache.axis.encoding.ser.VectorDeserializerFactory.class, qn1));
            
      call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
      call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
      call.addParameter( "arg2", qn0, ParameterMode.IN );
      call.addParameter( "arg3", qn1, ParameterMode.IN );
      call.setReturnType(qn);
      ServiceType res=(ServiceType)call.invoke( new Object[] {pStype,pFrom,pParam,pFault} );

              
      return res;
    }

/**
 * service query
 * @param pStype service type to be searched
 * @param pFrom search source
 * @param pParam search parameter hash
 * @return Service descriptor object vector (ServiceType)
 * @see serviceType
 * @throws Exception error
 */
    public Vector getAllService(String pStype, String pFrom, Hashtable pParam) throws Exception
    {
      
      Service service;
      Call call;
      QName qn,qn1,qn0;

      service = new Service();
      call = (Call) service.createCall();
      call.setTargetEndpointAddress(serviceURL+serviceID);
      call.setOperationName("getAllService");

      qn=new QName( "urn:BeanService", "ServiceType" );
      call.registerTypeMapping(ServiceType.class, qn,
                      new BeanSerializerFactory(ServiceType.class, qn),
                      new BeanDeserializerFactory(ServiceType.class, qn));
      qn0=new QName("urn:wfsportalservice","ns1:AxisHashMap");
      call.registerTypeMapping(HashMap.class, qn0,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn0),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn0));
            
      qn1=new QName( "ns1:AxisVector", "Vector" );
      call.registerTypeMapping(Vector.class, qn1,
                      new VectorSerializerFactory(org.apache.axis.encoding.ser.VectorSerializerFactory.class, qn1),
                      new VectorDeserializerFactory(org.apache.axis.encoding.ser.VectorDeserializerFactory.class, qn1));
            
      call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
      call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
      call.addParameter( "arg2", qn0, ParameterMode.IN );
      call.setReturnType(qn1);
      return (Vector)call.invoke( new Object[] {pStype,pFrom,pParam} );
    }

    public HashMap getAllProperties(String pServiceURL) throws Exception
    {
      Service service;
      Call call;
      QName qn0;

      service = new Service();
      call = (Call) service.createCall();
      call.setTargetEndpointAddress(serviceURL+serviceID);
      call.setOperationName("getAllProperties");

      qn0=new QName("urn:wfsportalservice","ns1:AxisHashMap");
      call.registerTypeMapping(HashMap.class, qn0,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn0),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn0));


      call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
      call.setReturnType(qn0);
      HashMap res=(HashMap)call.invoke( new Object[] {pServiceURL} );
      return res;
    }
}

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
package hu.sztaki.lpds.gemlcaquery.net.wsaxis13;

import hu.sztaki.lpds.gemlcaquery.com.GemlcaqueryDataBean;
import hu.sztaki.lpds.gemlcaquery.inf.PortalGemlcaqueryClient;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

/**
 * @author lpds
 */
public class PortalGemlcaqueryClientImpl implements PortalGemlcaqueryClient {
    
    private String serviceURL="";
    
    private String serviceID="";
    
    public PortalGemlcaqueryClientImpl(){}
    
    public PortalGemlcaqueryClientImpl(String pServiceUrl){}
    
    /**
     * Sets the address of the service which needs to be accessed.
     * @param value The access point of the requested service
     */
    public void setServiceURL(String value){serviceURL=value;}
    
    /**
     * Sets the ID of the service which needs to be accessed.
     * @param value The reference ID of the requested service
     */
    public void setServiceID(String value){serviceID=value;}
    
    /**
     * This is a gemlca service, it requests the parameters of the legacy code.
     *
     * The return value is a vector,
     * every element of it is a hashtable,
     * the GLC parameters are in the hashtables.
     *
     * @param bean The parameters describing the request
     * @return List of the GLCs
     */
    public Vector getGLCList(GemlcaqueryDataBean bean) {
        Vector retVector = new Vector();
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getGLCList");
            
            qn=new QName( "urn:BeanService", "GemlcaqueryDataBean" );
            call.registerTypeMapping(GemlcaqueryDataBean.class, qn,
                    new BeanSerializerFactory(GemlcaqueryDataBean.class, qn),
                    new BeanDeserializerFactory(GemlcaqueryDataBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            // call.setReturnType(Constants. XSD_STRING);
            call.setReturnType(new QName("urn:gemlcaqueryportalservice", "ns1:AxisVector"), Vector.class);
            
            retVector = (Vector) call.invoke( new Object[] { bean } );
            
            if (retVector == null) {
                throw new Exception("Not valid parameters ! retVector = (" + retVector + ")");
            }
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return retVector;
    }
    
    /**
     * It requests the legacy code (GLC) list of a gemlca service.
     *
     * The return value is a vector,
     * every element of it is a hashtable,
     * the parameters are in the hashtables.
     *
     * @param bean The parameters describing the request
     * @return List of the GLCs
     */
    public Vector getGLCParameterList(GemlcaqueryDataBean bean) {
        Vector retVector = new Vector();
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getGLCParameterList");
            
            qn=new QName( "urn:BeanService", "GemlcaqueryDataBean" );
            call.registerTypeMapping(GemlcaqueryDataBean.class, qn,
                    new BeanSerializerFactory(GemlcaqueryDataBean.class, qn),
                    new BeanDeserializerFactory(GemlcaqueryDataBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            // call.setReturnType(Constants. XSD_STRING);
            call.setReturnType(new QName("urn:gemlcaqueryportalservice", "ns1:AxisVector"), Vector.class);
            
            retVector = (Vector) call.invoke( new Object[] { bean } );
            
            if (retVector == null) {
                throw new Exception("Not valid parameters ! retVector = (" + retVector + ")");
            }
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return retVector;
    }

    /**
     * It requests the legacy code (GLC) list of a gemlca service.
     *
     * The return value is a vector,
     * the element of the "-GLCLIST-" is a vector with the LC list(vector)
     * Further components
     * key:lc value:lc parameters(vector)
     *
     * @param bean The parameters describing the request
     * @return List of the GLCs
     */
    public Hashtable getGLCwithParameterList(GemlcaqueryDataBean bean) {
        Hashtable retHashtable = new Hashtable();
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getGLCwithParameterList");
            
            qn=new QName( "urn:BeanService", "GemlcaqueryDataBean" );
            call.registerTypeMapping(GemlcaqueryDataBean.class, qn,
                    new BeanSerializerFactory(GemlcaqueryDataBean.class, qn),
                    new BeanDeserializerFactory(GemlcaqueryDataBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            // call.setReturnType(Constants. XSD_STRING);
            call.setReturnType(new QName("urn:gemlcaqueryportalservice", "ns1:AxisHashtable"), Hashtable.class);
            
            retHashtable = (Hashtable) call.invoke( new Object[] { bean } );
            
            if (retHashtable == null) {
                throw new Exception("Not valid parameters ! retHashtable = (" + retHashtable + ")");
            }
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return retHashtable;
    }
}

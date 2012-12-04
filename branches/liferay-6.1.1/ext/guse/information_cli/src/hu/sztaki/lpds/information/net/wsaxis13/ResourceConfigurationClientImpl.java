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
 * Resource configuration - client side
 */

package hu.sztaki.lpds.information.net.wsaxis13;

import hu.sztaki.lpds.information.data.ResourceBean;
import hu.sztaki.lpds.information.inf.ResourceConfigurationClient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
public class ResourceConfigurationClientImpl implements ResourceConfigurationClient
{
    private String serviceURL="";
    private String serviceID="";
   

    @Override
    public void setServiceURL(String value){serviceURL=value;}    
    
    @Override
    public void setServiceID(String value){serviceID=value;}

    @Override
    public List<String> getAllMidleware(String pPortalID, String pUserID) throws Exception
    {
        Service service;
        Call call;
        QName qn;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("getAllMidleware");

        qn=new QName( "ns1:AxisVector", "Vector" );
        call.registerTypeMapping(ArrayList.class, qn,
                      new VectorSerializerFactory(org.apache.axis.encoding.ser.VectorSerializerFactory.class, qn),
                      new VectorDeserializerFactory(org.apache.axis.encoding.ser.VectorDeserializerFactory.class, qn));

        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
        call.setReturnType(qn);
        return (List)call.invoke( new Object[] {pPortalID,pUserID} );
    }

    public List<String> getAllGrids(String pPortalID, String pUserID, String pMidleware) throws Exception
    {
        Service service;
        Call call;
        QName qn;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("getAllGrids");

        qn=new QName( "ns1:AxisVector", "Vector" );
        call.registerTypeMapping(ArrayList.class, qn,
                      new VectorSerializerFactory(org.apache.axis.encoding.ser.VectorSerializerFactory.class, qn),
                      new VectorDeserializerFactory(org.apache.axis.encoding.ser.VectorDeserializerFactory.class, qn));

        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg2", Constants.XSD_STRING, ParameterMode.IN );
        call.setReturnType(qn);
        return (List)call.invoke( new Object[] {pPortalID,pUserID, pMidleware} );
    }

    public HashMap getGridProperies(String pPortalID, String pUserID,String pMidleware, String pGrid) throws Exception
    {
        Service service;
        Call call;
        QName qn;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("getGridProperies");

        qn=new QName( "ns1:AxisHashMap", "HashMap" );
        call.registerTypeMapping(HashMap.class, qn,
                      new MapSerializerFactory(org.apache.axis.encoding.ser.MapSerializerFactory.class, qn),
                      new MapDeserializerFactory(org.apache.axis.encoding.ser.MapDeserializerFactory.class, qn));

        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg2", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg3", Constants.XSD_STRING, ParameterMode.IN );
        call.setReturnType(qn);
        return (HashMap)call.invoke( new Object[] {pPortalID,pUserID, pMidleware,pGrid} );
    }

    public String setGridProperies(String pPortalID, String pUserID,String pMidleware, String pGrid, HashMap pProps) throws Exception
    {
        Service service;
        Call call;
        QName qn;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("setGridProperies");

        qn=new QName( "ns1:AxisHashMap");
        call.registerTypeMapping(HashMap.class, qn,
                      new MapSerializerFactory(org.apache.axis.encoding.ser.MapSerializerFactory.class, qn),
                      new MapDeserializerFactory(org.apache.axis.encoding.ser.MapDeserializerFactory.class, qn));

        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg2", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg3", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg4", qn, ParameterMode.IN );
        call.setReturnType(Constants.XSD_STRING);
        return (String)call.invoke( new Object[] {pPortalID,pUserID, pMidleware,pGrid,pProps} );
    }

    public String addResource(String pPortalID, String pUserID, String pMidleware, String pGrid, String pSite, String pJobmanager) throws Exception
    {
        Service service;
        Call call;
        QName qn;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("addResource");


        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg2", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg3", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg4", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg5", Constants.XSD_STRING, ParameterMode.IN );
        call.setReturnType(Constants.XSD_STRING);
        return (String)call.invoke( new Object[] {pPortalID,pUserID, pMidleware,pGrid,pSite,pJobmanager} );

    }

    public Collection<ResourceBean> getAllResource(String pPortalID, String pUserID, String pMidleware, String pGrid)  throws Exception
    {
        Service service;
        Call call;
        QName qn,qn1,qn2;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("getAllResource");

        qn=new QName( "urn:BeanService", "ResourceBean" );
        call.registerTypeMapping(ResourceBean.class, qn,
                     new BeanSerializerFactory(ResourceBean.class, qn),
                     new BeanDeserializerFactory(ResourceBean.class, qn));
        qn1=new QName( "ns1:AxisVector", "Vector" );
        call.registerTypeMapping(Vector.class, qn1,
                         new VectorSerializerFactory(Vector.class, qn1),
                         new VectorDeserializerFactory(Vector.class, qn1));

        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg2", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg3", Constants.XSD_STRING, ParameterMode.IN );
        call.setReturnType(qn1);
        Collection res=(Collection)call.invoke( new Object[] {pPortalID,pUserID, pMidleware,pGrid} );
        return (Collection<ResourceBean>)res;
    }

    public String deleteResource(String pPortalID, String pUserID,String pMidleware,String pGrid,String pResourceID) throws Exception
    {
       Service service;
        Call call;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("deleteResource");


        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg2", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg3", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg4", Constants.XSD_STRING, ParameterMode.IN );
        call.setReturnType(Constants.XSD_STRING);
        return (String)call.invoke( new Object[] {pPortalID,pUserID, pMidleware,pGrid,pResourceID} );
    }

    public String getConfiguredResources(String pPortalID, String pUserID)  throws Exception
    {
        Service service;
        Call call;
        QName qn,qn1;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("getConfiguredResources");


        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
        call.setReturnType(Constants.XSD_STRING);
        String res=(String)call.invoke( new Object[] {pPortalID,pUserID} );
        return res;
    }

}

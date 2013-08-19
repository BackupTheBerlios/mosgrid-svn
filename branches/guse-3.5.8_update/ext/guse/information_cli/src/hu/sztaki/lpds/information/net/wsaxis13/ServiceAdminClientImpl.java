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
 * Implementation of the service administration client
 */

package hu.sztaki.lpds.information.net.wsaxis13;

import hu.sztaki.lpds.information.data.GuseServiceBean;
import hu.sztaki.lpds.information.data.GuseServiceCommunicationBean;
import hu.sztaki.lpds.information.data.GuseServiceTypeBean;
import hu.sztaki.lpds.information.data.ServicePropertyBean;
import hu.sztaki.lpds.information.data.ServiceResourceBean;
import hu.sztaki.lpds.information.data.ServiceUserBean;
import hu.sztaki.lpds.information.inf.ServiceAdminClient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.Constants;

/**
 * @author krisztian karoczkai
 */
public class ServiceAdminClientImpl implements ServiceAdminClient
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

    public ArrayList<GuseServiceBean> getAllServices()  throws Exception
    {
        return (ArrayList<GuseServiceBean>)callMethodWithNullParameter("getAllServices");
    }

    public ArrayList<GuseServiceTypeBean> getAllServiceTypes()  throws Exception
    {
        return (ArrayList<GuseServiceTypeBean>)callMethodWithNullParameter("getAllServiceTypes");
    }

    public ArrayList<GuseServiceCommunicationBean> getAllServiceComs()  throws Exception
    {
        return (ArrayList<GuseServiceCommunicationBean>)callMethodWithNullParameter("getAllServiceComs");
    }

    public String deleteService(String pID) throws Exception
    {
        return callMethodWithStringParameter("deleteService",pID);
    }

    public String deleteServiceType (String pID) throws Exception
    {
        return callMethodWithStringParameter("deleteServiceType",pID);
    }

    public String deleteServiceCom(String pID) throws Exception
    {
        return callMethodWithStringParameter("deleteServiceCom",pID);
    }

    public String dataManagement(Object pValue) throws Exception
    {
        String res="";
        if("hu.sztaki.lpds.information.data.GuseServiceBean".equals(pValue.getClass().getName()))
            res="dataService";
        else if("hu.sztaki.lpds.information.data.GuseServiceCommunicationBean".equals(pValue.getClass().getName()))
            res="dataServiceCom";
        else if("hu.sztaki.lpds.information.data.GuseServiceTypeBean".equals(pValue.getClass().getName()))
            res="dataServiceType";
        else return "error.api:"+pValue.getClass().getName();

        return callMethodWithDataObjectParameter(res, pValue);
    }


    public String addServiceUser(String pServiceID, ServiceUserBean pUserLogin) throws Exception
    {
        Service service;
        Call call;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("addServiceUser");

        QName qn0=new QName( "urn:BeanService", "ServiceUserBean" );
           call.registerTypeMapping(ServiceUserBean.class, qn0,
                     new BeanSerializerFactory(ServiceUserBean.class, qn0),
                     new BeanDeserializerFactory(ServiceUserBean.class, qn0));

        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", qn0, ParameterMode.IN );
        call.setReturnType(Constants.XSD_STRING);
        return (String)call.invoke(new Object[]{pServiceID,pUserLogin});
    }

    public String deleteServiceUser(String pServiceID, String pUserLoginID) throws Exception
    {
        return callMethodWithTwoStringParameter("deleteServiceUser",pServiceID,pUserLoginID);
    }

    public String addCommChanel(String pComID, ServiceResourceBean pResource) throws Exception
    {
        Service service;
        Call call;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("addCommChanel");

        QName qn0=new QName( "urn:BeanService", "ServiceResourceBean" );
           call.registerTypeMapping(ServiceResourceBean.class, qn0,
                     new BeanSerializerFactory(ServiceResourceBean.class, qn0),
                     new BeanDeserializerFactory(ServiceResourceBean.class, qn0));
        QName qn4=new QName( "urn:BeanService", "GuseServiceTypeBean" );
           call.registerTypeMapping(GuseServiceTypeBean.class, qn4,
                     new BeanSerializerFactory(GuseServiceTypeBean.class, qn4),
                     new BeanDeserializerFactory(GuseServiceTypeBean.class, qn4));
        QName qn5=new QName( "urn:BeanService", "GuseServiceBean" );
           call.registerTypeMapping(GuseServiceBean.class, qn5,
                     new BeanSerializerFactory(GuseServiceBean.class, qn5),
                     new BeanDeserializerFactory(GuseServiceTypeBean.class, qn5));
        QName qn6=new QName( "urn:BeanService", "ServicePropertyBean" );
           call.registerTypeMapping(ServicePropertyBean.class, qn6,
                     new BeanSerializerFactory(ServicePropertyBean.class, qn6),
                     new BeanDeserializerFactory(ServicePropertyBean.class, qn6));
        QName qn7=new QName( "urn:BeanService", "GuseServiceCommunicationBean" );
           call.registerTypeMapping(GuseServiceCommunicationBean.class, qn7,
                     new BeanSerializerFactory(GuseServiceCommunicationBean.class, qn7),
                     new BeanDeserializerFactory(GuseServiceCommunicationBean.class, qn7));


        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", qn0, ParameterMode.IN );
        call.setReturnType(Constants.XSD_STRING);
        return (String)call.invoke(new Object[]{pComID,pResource});

    }

    public String deleteCommChanel(String pComID, String pResourceID) throws Exception
    {
        return callMethodWithTwoStringParameter("deleteCommChanel",pComID,pResourceID);
    }


/**
 * Calling a service method without parameter, the return value is a list
 * @param pMethod method name
 * @return list data
 * @throws java.lang.Exception communication error
 */
    private ArrayList callMethodWithNullParameter(String pMethod)  throws Exception
    {
           Service service;
           Call call;
           QName qn0,qn1,qn2,qn3,qn4,qn5,qn6,qn7;
           service = new Service();
           call = (Call) service.createCall();

           call.setTargetEndpointAddress(serviceURL+serviceID);
           call.setOperationName(pMethod);
           qn0=new QName( "ns1:AxisList", "List" );
           qn1=new QName( "ns1:AxisCollection", "Collection" );
           call.registerTypeMapping(Collection.class, qn1,
                         new VectorSerializerFactory(Collection.class, qn1),
                         new VectorDeserializerFactory(Collection.class, qn1));
           call.registerTypeMapping(List.class, qn0,
                         new VectorSerializerFactory(List.class, qn0),
                         new VectorDeserializerFactory(List.class, qn0));

           qn2=new QName( "urn:BeanService", "GuseServiceBean" );
           call.registerTypeMapping(GuseServiceBean.class, qn2,
                     new BeanSerializerFactory(GuseServiceBean.class, qn2),
                     new BeanDeserializerFactory(GuseServiceBean.class, qn2));

           qn3=new QName( "urn:BeanService", "GuseServiceCommunicationBean" );
           call.registerTypeMapping(GuseServiceCommunicationBean.class, qn3,
                     new BeanSerializerFactory(GuseServiceCommunicationBean.class, qn3),
                     new BeanDeserializerFactory(GuseServiceCommunicationBean.class, qn3));

           qn4=new QName( "urn:BeanService", "GuseServiceTypeBean" );
           call.registerTypeMapping(GuseServiceTypeBean.class, qn4,
                     new BeanSerializerFactory(GuseServiceTypeBean.class, qn4),
                     new BeanDeserializerFactory(GuseServiceTypeBean.class, qn4));

           qn5=new QName( "urn:BeanService", "ServiceResourceBean" );
           call.registerTypeMapping(ServiceResourceBean.class, qn5,
                     new BeanSerializerFactory(ServiceResourceBean.class, qn5),
                     new BeanDeserializerFactory(ServiceResourceBean.class, qn5));

           qn6=new QName( "urn:BeanService", "ServiceUserBean" );
           call.registerTypeMapping(ServiceUserBean.class, qn6,
                     new BeanSerializerFactory(ServiceUserBean.class, qn6),
                     new BeanDeserializerFactory(ServiceUserBean.class, qn6));

           qn7=new QName( "urn:BeanService", "ServicePropertyBean" );
           call.registerTypeMapping(ServicePropertyBean.class, qn7,
                     new BeanSerializerFactory(ServicePropertyBean.class, qn7),
                     new BeanDeserializerFactory(ServicePropertyBean.class, qn7));

           call.setReturnType(qn0);

            ArrayList tmp=(ArrayList)call.invoke( new Object[] {} );
            return tmp;
    }

/**
 * Calling a service method with persisted object instance, the return value is a string
 * @param pMethod method name
 * @param pParam data parameter
 * @return list data
 * @throws java.lang.Exception communication error
 */
    private String callMethodWithDataObjectParameter(String pMethod,Object pParam)  throws Exception
    {
           Service service;
           Call call;
           QName qn0,qn1,qn2,qn3,qn4,qn5,qn6,qn7;
           service = new Service();
           call = (Call) service.createCall();

           call.setTargetEndpointAddress(serviceURL+serviceID);
           call.setOperationName(pMethod);
           qn0=new QName( "ns1:AxisList", "List" );
           call.registerTypeMapping(List.class, qn0,
                         new VectorSerializerFactory(List.class, qn0),
                         new VectorDeserializerFactory(List.class, qn0));
           qn1=new QName( "ns1:AxisCollection", "Collection" );

           call.registerTypeMapping(Collection.class, qn1,
                         new VectorSerializerFactory(Collection.class, qn1),
                         new VectorDeserializerFactory(Collection.class, qn1));

           qn2=new QName( "urn:BeanService", "GuseServiceBean" );
           call.registerTypeMapping(GuseServiceBean.class, qn2,
                     new BeanSerializerFactory(GuseServiceBean.class, qn2),
                     new BeanDeserializerFactory(GuseServiceBean.class, qn2));

           qn3=new QName( "urn:BeanService", "GuseServiceCommunicationBean" );
           call.registerTypeMapping(GuseServiceCommunicationBean.class, qn3,
                     new BeanSerializerFactory(GuseServiceCommunicationBean.class, qn3),
                     new BeanDeserializerFactory(GuseServiceCommunicationBean.class, qn3));

           qn4=new QName( "urn:BeanService", "GuseServiceTypeBean" );
           call.registerTypeMapping(GuseServiceTypeBean.class, qn4,
                     new BeanSerializerFactory(GuseServiceTypeBean.class, qn4),
                     new BeanDeserializerFactory(GuseServiceTypeBean.class, qn4));

           qn5=new QName( "urn:BeanService", "ServiceResourceBean" );
           call.registerTypeMapping(ServiceResourceBean.class, qn5,
                     new BeanSerializerFactory(ServiceResourceBean.class, qn5),
                     new BeanDeserializerFactory(ServiceResourceBean.class, qn5));

           qn6=new QName( "urn:BeanService", "ServiceUserBean" );
           call.registerTypeMapping(ServiceUserBean.class, qn6,
                     new BeanSerializerFactory(ServiceUserBean.class, qn6),
                     new BeanDeserializerFactory(ServiceUserBean.class, qn6));

           qn7=new QName( "urn:BeanService", "ServicePropertyBean" );
           call.registerTypeMapping(ServicePropertyBean.class, qn7,
                     new BeanSerializerFactory(ServicePropertyBean.class, qn7),
                     new BeanDeserializerFactory(ServicePropertyBean.class, qn7));

// parameter tipusanak beallitasa
            if("hu.sztaki.lpds.information.data.GuseServiceBean".equals(pParam.getClass().getName()))
                call.addParameter( "arg0", qn2, ParameterMode.IN );
            else if("hu.sztaki.lpds.information.data.GuseServiceCommunicationBean".equals(pParam.getClass().getName()))
                call.addParameter( "arg0", qn3, ParameterMode.IN );
            else if("hu.sztaki.lpds.information.data.GuseServiceTypeBean".equals(pParam.getClass().getName()))
                call.addParameter( "arg0", qn4, ParameterMode.IN );

            call.setReturnType(Constants.XSD_STRING);

            String tmp=(String)call.invoke( new Object[] {pParam} );
            return tmp;
    }

/**
 * Calling a service method with a string parameter, the return value is also a string
 * @param pMethod method name
 * @param pParam parameter value
 * @return returned string value
 * @throws java.lang.Exception calling error
 */
    private String callMethodWithStringParameter(String pMethod,String pParam) throws Exception
    {
        Service service;
        Call call;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName(pMethod);

        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.setReturnType(Constants.XSD_STRING);
        return (String)call.invoke(new Object[]{pParam});

    }

/**
 * Calling a service method with a string parameter, the return value is also a string
 * @param pMethod method name
 * @param pParam parameter value
 * @return returned string value
 * @throws java.lang.Exception calling error
 */
    private String callMethodWithTwoStringParameter(String pMethod,String pParam0,String pParam1) throws Exception
    {
        Service service;
        Call call;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName(pMethod);

        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
        call.setReturnType(Constants.XSD_STRING);
        return (String)call.invoke(new Object[]{pParam0,pParam1});

    }

    public String refreshService(String pServiceID) throws Exception
    {
        return callMethodWithStringParameter("refreshService",pServiceID);
    }

    public String addServiceProperty(String pServiceID, String pPropertyID, String pKey, String pValue) throws Exception
    {
        Service service;
        Call call;

        service = new Service();
        call = (Call) service.createCall();
        call.setTargetEndpointAddress(serviceURL+serviceID);
        call.setOperationName("addServiceProperty");

        call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg2", Constants.XSD_STRING, ParameterMode.IN );
        call.addParameter( "arg3", Constants.XSD_STRING, ParameterMode.IN );
        call.setReturnType(Constants.XSD_STRING);
        return (String)call.invoke(new Object[]{pServiceID, pPropertyID, pKey, pValue});
    }

    public String importServiceProperties(String pDSTServiceID, String pSRCServiceID) throws Exception {
        return callMethodWithTwoStringParameter("importServiceProperties",pDSTServiceID,pSRCServiceID);
    }

    public String export() throws Exception {
           Service service;
           Call call;
           service = new Service();
           call = (Call) service.createCall();

           call.setTargetEndpointAddress(serviceURL+serviceID);
           call.setOperationName("export");
            call.setReturnType(Constants.XSD_STRING);
            return (String)call.invoke(new Object[]{});
    }

}

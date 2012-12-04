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
package hu.sztaki.lpds.storage.net.wsaxis13;

import com.sun.org.apache.bcel.internal.generic.IFEQ;
import hu.sztaki.lpds.portal.util.stream.HttpClient;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.VolatileBean;
import hu.sztaki.lpds.wfs.com.VolatileEntryBean;
import java.util.Vector;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.util.HashMap;
import hu.sztaki.lpds.storage.com.*;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * @author lpds
 */
public class PortalStorageClientImpl implements PortalStorageClient {
    
    private String serviceURL="";
    
    private String serviceID="";
/**
 * Empty constructor
 */
    public PortalStorageClientImpl(){}
/**
 * Constructor
 * @param pServiceUrl service URL
 */
    public PortalStorageClientImpl(String pServiceUrl){}
    
    @Override
    public void setServiceURL(String value){serviceURL=value;}
    
    @Override
    public void setServiceID(String value){serviceID=value;}
    
    @Override
    public HashMap getWorkflowAllJobSize(StoragePortalUserWorkflowBean value) {
        HashMap res=new HashMap();
        try {
            Service service;
            Call call;
            QName qn,qn1,qn2;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getWorkflowAllJobSize");
            
            qn=new QName( "urn:BeanService", "PortalUserWorkflowBean" );
            call.registerTypeMapping(StoragePortalUserWorkflowBean.class, qn,
                    new BeanSerializerFactory(StoragePortalUserWorkflowBean.class, qn),
                    new BeanDeserializerFactory(StoragePortalUserWorkflowBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:portalservice","ns1:AxisHashMap"),HashMap.class);
            
            res = (HashMap)call.invoke( new Object[] { value } );
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
    
    
    @Override
    public HashMap getAllWorkflowSize(StoragePortalUserBean value) {
        HashMap res=new HashMap();
        try {
            Service service;
            Call call;
            QName qn,qn1,qn2;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getAllWorkflowSize");
            
            qn=new QName( "urn:BeanService", "PortalUserBean" );
            call.registerTypeMapping(StoragePortalUserBean.class, qn,
                    new BeanSerializerFactory(StoragePortalUserBean.class, qn),
                    new BeanDeserializerFactory(StoragePortalUserBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:portalservice","ns1:AxisHashMap"),HashMap.class);
            
            res = (HashMap)call.invoke( new Object[] { value } );
        }
        
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
    
    @Override
    public boolean deleteWorkflow(ComDataBean value) {
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("deleteWorkflow");
            
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                    new BeanSerializerFactory(ComDataBean.class, qn),
                    new BeanDeserializerFactory(ComDataBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            
            return ((Boolean)call.invoke( new Object[] { value } )).booleanValue();
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return false;
    }
    
    @Override
    public boolean deleteWorkflowInstance(ComDataBean value) {
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("deleteWorkflowInstance");
            
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                    new BeanSerializerFactory(ComDataBean.class, qn),
                    new BeanDeserializerFactory(ComDataBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            
            return ((Boolean)call.invoke( new Object[] { value } )).booleanValue();
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return false;
    }
    
    @Override
    public boolean deleteWorkflowOutputs(ComDataBean value) {
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("deleteWorkflowOutputs");
            
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                    new BeanSerializerFactory(ComDataBean.class, qn),
                    new BeanDeserializerFactory(ComDataBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            
            return ((Boolean)call.invoke( new Object[] { value } )).booleanValue();
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return false;
    }
    
    @Override
    public boolean copyWorkflowFiles(StoragePortalCopyWorkflowBean value) {
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("copyWorkflowFiles");
            
            qn = new QName("urn:BeanService", "StoragePortalCopyWorkflowBean");
            call.registerTypeMapping(StoragePortalCopyWorkflowBean.class, qn,
                    new BeanSerializerFactory(StoragePortalCopyWorkflowBean.class, qn),
                    new BeanDeserializerFactory(StoragePortalCopyWorkflowBean.class, qn));
            
            // call.setProperty(Call.CHARACTER_SET_ENCODING, "ISO-8859-1");
            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            
            return ((Boolean)call.invoke( new Object[] { value } )).booleanValue();
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return false;
    }
    
    @Override
    public boolean deleteWorkflowLogOutputs(ComDataBean idBean, Vector value) {
        try {
            Service service;
            Call call;
            QName qn, qn1;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("deleteWorkflowLogOutputs");
            
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));

            qn1=new QName( "ns1:AxisVector", "Vector" );
            call.registerTypeMapping(Vector.class, qn1,
                    new VectorSerializerFactory(Vector.class, qn1),
                    new VectorDeserializerFactory(Vector.class,  qn1));
            

            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.addParameter( "arg2", qn1, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            
            return ((Boolean)call.invoke( new Object[] { idBean, value } )).booleanValue();
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return false;
    }

    @Override
    public boolean uploadWorkflowFiles(UploadWorkflowBean value) {
        try {
            Service service;
            Call call;
            QName qn, qn1;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("uploadWorkflowFiles");
            
            qn=new QName( "ns1:AxisVector", "Vector" );
            call.registerTypeMapping(Vector.class, qn,
                    new VectorSerializerFactory(Vector.class, qn),
                    new VectorDeserializerFactory(Vector.class,  qn));
            
            qn1=new QName( "urn:BeanService", "UploadWorkflowBean" );
            call.registerTypeMapping(UploadWorkflowBean.class, qn1,
                      new BeanSerializerFactory(UploadWorkflowBean.class, qn1),
                      new BeanDeserializerFactory(UploadWorkflowBean.class, qn1));

            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            
            return ((Boolean)call.invoke( new Object[] { value } )).booleanValue();
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return false;
    }
    
    @Override
    public boolean deleteWorkflowVolatileOutputs(VolatileBean value) {
        try {
            Service service;
            Call call;
            QName qn, qn1, qn2;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("deleteWorkflowVolatileOutputs");
            
            qn=new QName( "urn:BeanService", "VolatileBean" );
            call.registerTypeMapping(VolatileBean.class, qn,
                    new BeanSerializerFactory(VolatileBean.class, qn),
                    new BeanDeserializerFactory(VolatileBean.class, qn));            
            qn1=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn1,
                    new BeanSerializerFactory(ComDataBean.class, qn1),
                    new BeanDeserializerFactory(ComDataBean.class, qn1));
            qn2=new QName( "urn:BeanService", "VolatileEntryBean" );
            call.registerTypeMapping(VolatileEntryBean.class, qn2,
                    new BeanSerializerFactory(VolatileEntryBean.class, qn2),
                    new BeanDeserializerFactory(VolatileEntryBean.class, qn2));            

            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            
            return ((Boolean) call.invoke( new Object[] { value } )).booleanValue();
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return false;
    }

    @Override
    public void fileUpload(File pFile,String pUploadField,Hashtable pValue) throws Exception{
        HttpClient http=new HttpClient();
        http.open(serviceURL+serviceID);
        int statusCode=http.fileUpload(pFile, pUploadField, pValue);
        if(statusCode!=200) throw new Exception("error.upload."+statusCode);

    }
    
    @Override
    public InputStream getStream(Hashtable<String, String> pValue) throws IOException{
        HttpClient http=new HttpClient();
        http.open(serviceURL+serviceID);
        return http.getStream(pValue);
    }


    public int getUploadingFilePercent(String sid,String filename) {
        try{
        Service service;
            Call call;
            QName qn, qn1, qn2;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getUploadingFilePercent");
            call.addParameter("sid_param", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("filename_param", org.apache.axis.Constants.XSD_STRING, javax.xml.rpc.ParameterMode.IN);


            call.setReturnType(org.apache.axis.Constants.XSD_INT);

            return ((Integer) call.invoke( new Object[] { new String(sid),new String(filename) } )).intValue();
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();return -1;}
        
    }
}

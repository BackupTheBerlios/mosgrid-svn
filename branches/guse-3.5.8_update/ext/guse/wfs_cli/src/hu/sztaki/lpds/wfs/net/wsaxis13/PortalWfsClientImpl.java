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
 * WS-Pgrade es wfs kozotti kommunikacio implementacioja
 */

package hu.sztaki.lpds.wfs.net.wsaxis13;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.util.HashMap;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.util.Vector;
import hu.sztaki.lpds.wfs.com.*;
/**
 * @author krisztian
 */

public class PortalWfsClientImpl implements PortalWfsClient
{
   private String serviceURL=""; 
   private String serviceID="";
/**
 * Ures konstruktor
 */
   public PortalWfsClientImpl(){}
/**
 * @see BaseCommunicationFace#setServiceID(java.lang.String)
 */
@Override
   public void setServiceURL(String value){serviceURL=value;}    
   
/**
 * @see BaseCommunicationFace#setServiceID(java.lang.String)
 */
@Override
   public void setServiceID(String value){serviceID=value;}
   
/**
 * @see PortalWfsClient#getAbstractWorkflows(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public Vector getAbstractWorkflows(ComDataBean value){
        Vector res=new Vector();
        try{
            Service service;
            Call call;
            QName qn,qn1;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getAbstractWorkflows");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn1=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn1,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));
            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("wfsurn:portalservice","ns1:AxisVector"),Vector.class);

            res = (Vector)call.invoke( new Object[] {value } );
            for(int i=0;i<res.size();i++)
            {
                ((ComDataBean)res.get(i)).setWfsID(serviceURL);
                ((ComDataBean)res.get(i)).setWfsIDService(serviceID);
            }         
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
    
/**
 * @see PortalWfsClient#getRealWorkflows(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public Vector getRealWorkflows(ComDataBean value){
        Vector res=new Vector();
        try{
            Service service;
            Call call;
            QName qn,qn1,qn2;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getRealWorkflows");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn1=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn1,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));


            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:wfsportalservice","ns1:AxisVector"),Vector.class);

            res = (Vector)call.invoke( new Object[] {value } );
            for(int i=0;i<res.size();i++){
                ((ComDataBean)res.get(i)).setWfsID(serviceURL);
                ((ComDataBean)res.get(i)).setWfsIDService(serviceID);
            }
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
    
/**
 * @see PortalWfsClient#getWorkflowJobs(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public Vector getWorkflowJobs(ComDataBean value){                             
        Vector res=new Vector();
        try{
            Service service;
            Call call;
            QName qn,qn1,qn2,qn3;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getWorkflowJobs");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn1=new QName( "urn:BeanService", "JobBean" );
            call.registerTypeMapping(JobBean.class, qn1,
                      new BeanSerializerFactory(JobBean.class, qn1),
                      new BeanDeserializerFactory(JobBean.class, qn1));
            qn2=new QName( "urn:BeanService", "PortBean" );
            call.registerTypeMapping(PortBean.class, qn2,
                      new BeanSerializerFactory(PortBean.class, qn2),
                      new BeanDeserializerFactory(PortBean.class, qn2));
            qn3=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn3,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn3),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn3));

            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:wfsportalservice","ns1:AxisVector"),Vector.class);

            res = (Vector)call.invoke( new Object[] {value } );
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
/**
 * @see PortalWfsClient#saveNewWorkflow(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public boolean saveNewWorkflow(ComDataBean value){
        boolean res=false;
        try{
            Service service;
            Call call;
            QName qn,qn1;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("saveNewWorkflow");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn1=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn1,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));
            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);

            res = ((Boolean)call.invoke( new Object[] {value } )).booleanValue();
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }

/**
 * @see PortalWfsClient#deleteWorkflow(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public boolean deleteWorkflow(ComDataBean value){
        boolean res=false;
        try{
            Service service;
            Call call;
            QName qn,qn1;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("deleteWorkflow");
                                   
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn1=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn1,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));
            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);

            res = ((Boolean)call.invoke( new Object[] {value } )).booleanValue();
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    
    }
/**
 * @see PortalWfsClient#deleteWorkflowGraf(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public boolean deleteWorkflowGraf(ComDataBean value){
        boolean res=false;
        try{
            Service service;
            Call call;
            QName qn,qn1;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("deleteWorkflowGraf");
                                   
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn1=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn1,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));
            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);

            res = ((Boolean)call.invoke( new Object[] {value } )).booleanValue();
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;   
    }
/**
 * @see PortalWfsClient#getJobProperty(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public JobPropertyBean getJobProperty(ComDataBean value){
        JobPropertyBean res=null;
        try{
            Service service;
            Call call;
            QName qn,qn0,qn1,qn2;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getJobProperty");
                                   
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn0=new QName( "urn:BeanService", "JobPropertyBean" );
            call.registerTypeMapping(JobPropertyBean.class, qn0,
                      new BeanSerializerFactory(JobPropertyBean.class, qn0),
                      new BeanDeserializerFactory(JobPropertyBean.class, qn0));

            qn1=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn1,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));
            qn2=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn2,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn2),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn2));

            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(qn0);

            res = (JobPropertyBean)call.invoke( new Object[] {value } );
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    
    }
/**
 * @see PortalWfsClient#setWorkflowConfigData(hu.sztaki.lpds.wfs.com.ComDataBean, java.util.Vector)
 */
@Override
    public void setWorkflowConfigData(ComDataBean pID,Vector pData){
        JobPropertyBean res=null;
        try{
            Service service;
            Call call;
            QName qn,qn0,qn1,qn2,qn3;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("setWorkflowConfigData");
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));

            qn0=new QName( "urn:BeanService", "JobPropertyBean" );
            call.registerTypeMapping(JobPropertyBean.class, qn0,
                      new BeanSerializerFactory(JobPropertyBean.class, qn0),
                      new BeanDeserializerFactory(JobPropertyBean.class, qn0));

            qn1=new QName( "urn:BeanService", "PortDataBean" );
            call.registerTypeMapping(PortDataBean.class, qn1,
                      new BeanSerializerFactory(PortDataBean.class, qn1),
                      new BeanDeserializerFactory(PortDataBean.class, qn1));
            qn2=new QName( "ns1:AxisVector", "Vector" );
            call.registerTypeMapping(Vector.class, qn2,
                      new VectorSerializerFactory(org.apache.axis.encoding.ser.VectorSerializerFactory.class, qn2),
                      new VectorDeserializerFactory(org.apache.axis.encoding.ser.VectorDeserializerFactory.class, qn2));
            qn3=new QName( "urn:BeanService", "HistoryBean" );
            call.registerTypeMapping(HistoryBean.class, qn3,
                      new BeanSerializerFactory(HistoryBean.class, qn3),
                      new BeanDeserializerFactory(HistoryBean.class, qn3));
            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.addParameter( "arg2", qn2, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);

            call.invoke( new Object[] {pID,pData} );
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
    
    }
/**
 * @see PortalWfsClient#getNormalInputs(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public Vector getNormalInputs(ComDataBean value){
        Vector res=new Vector();
        try{
            Service service;
            Call call;
            QName qn;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getNormalInputs");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));


            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:wfsportalservice","ns1:AxisVector"),Vector.class);

            res = (Vector)call.invoke( new Object[] {value } );
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
/**
 * @see PortalWfsClient#getNormalOutputs(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public Vector getNormalOutputs(ComDataBean value){
        Vector res=new Vector();
        try{
            Service service;
            Call call;
            QName qn;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getNormalOutputs");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));


            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:wfsportalservice","ns1:AxisVector"),Vector.class);

            res = (Vector)call.invoke( new Object[] {value } );
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
/**
 * @see PortalWfsClient#submitWorkflow(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public void submitWorkflow(ComDataBean value){}
/**
 * @see PortalWfsClient#getWorkflowConfigData(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public Vector getWorkflowConfigData(ComDataBean value){
        Vector res=new Vector();
        try{
            Service service;
            Call call;
            QName qn,qn0,qn1,qn2;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getWorkflowConfigData");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn0=new QName( "urn:BeanService", "JobPropertyBean" );
            call.registerTypeMapping(JobPropertyBean.class, qn0,
                      new BeanSerializerFactory(JobPropertyBean.class, qn0),
                      new BeanDeserializerFactory(JobPropertyBean.class, qn0));
            qn1=new QName( "urn:BeanService", "PortDataBean" );
            call.registerTypeMapping(PortDataBean.class, qn1,
                      new BeanSerializerFactory(PortDataBean.class, qn1),
                      new BeanDeserializerFactory(PortDataBean.class, qn1));
            qn2=new QName( "urn:BeanService", "HistoryBean" );
            call.registerTypeMapping(HistoryBean.class, qn2,
                      new BeanSerializerFactory(HistoryBean.class, qn2),
                      new BeanDeserializerFactory(HistoryBean.class, qn2));


            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:wfsportalservice","ns1:AxisVector"),Vector.class);

            res = (Vector)call.invoke( new Object[] {value } );
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
/**
 * @see PortalWfsClient#getWorkflowProps(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public Vector getWorkflowProps(ComDataBean value){
        Vector res=new Vector();
        try{
            Service service;
            Call call;
            QName qn,qn0,qn1,qn2;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getWorkflowProps");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn0=new QName( "urn:BeanService", "JobPropertyBean" );
            call.registerTypeMapping(JobPropertyBean.class, qn0,
                      new BeanSerializerFactory(JobPropertyBean.class, qn0),
                      new BeanDeserializerFactory(JobPropertyBean.class, qn0));
            qn1=new QName( "urn:BeanService", "PortDataBean" );
            call.registerTypeMapping(PortDataBean.class, qn1,
                      new BeanSerializerFactory(PortDataBean.class, qn1),
                      new BeanDeserializerFactory(PortDataBean.class, qn1));
            qn2=new QName( "urn:BeanService", "HistoryBean" );
            call.registerTypeMapping(HistoryBean.class, qn2,
                      new BeanSerializerFactory(HistoryBean.class, qn2),
                      new BeanDeserializerFactory(HistoryBean.class, qn2));


            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:wfsportalservice","ns1:AxisVector"),Vector.class);

            res = (Vector)call.invoke( new Object[] {value } );
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }    
/**
 * @param pID
 * @see PortalWfsClient#setWorkflowProps(hu.sztaki.lpds.wfs.com.ComDataBean, java.util.Vector)
 */
@Override
    public void setWorkflowProps(ComDataBean pID, Vector pData){
        JobPropertyBean res=null;
        try{
            Service service;
            Call call;
            QName qn,qn0,qn1,qn2,qn3;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("setWorkflowProps");
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));

            qn0=new QName( "urn:BeanService", "JobPropertyBean" );
            call.registerTypeMapping(JobPropertyBean.class, qn0,
                      new BeanSerializerFactory(JobPropertyBean.class, qn0),
                      new BeanDeserializerFactory(JobPropertyBean.class, qn0));

            qn1=new QName( "urn:BeanService", "PortDataBean" );
            call.registerTypeMapping(PortDataBean.class, qn1,
                      new BeanSerializerFactory(PortDataBean.class, qn1),
                      new BeanDeserializerFactory(PortDataBean.class, qn1));
            qn2=new QName( "ns1:AxisVector", "Vector" );
            call.registerTypeMapping(Vector.class, qn2,
                      new VectorSerializerFactory(org.apache.axis.encoding.ser.VectorSerializerFactory.class, qn2),
                      new VectorDeserializerFactory(org.apache.axis.encoding.ser.VectorDeserializerFactory.class, qn2));
            qn3=new QName( "urn:BeanService", "HistoryBean" );
            call.registerTypeMapping(HistoryBean.class, qn3,
                      new BeanSerializerFactory(HistoryBean.class, qn3),
                      new BeanDeserializerFactory(HistoryBean.class, qn3));
            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.addParameter( "arg2", qn2, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);

            call.invoke( new Object[] {pID,pData} );
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
    
    }    
/**
 * @see PortalWfsClient#getWorkflowInstanceDesc(java.lang.String)
 */
@Override
    public ComDataBean getWorkflowInstanceDesc(String pValue){
        ComDataBean res=new ComDataBean();
        try{
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getWorkflowInstanceDesc");
            
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                    new BeanSerializerFactory(ComDataBean.class, qn),
                    new BeanDeserializerFactory(ComDataBean.class, qn));
            call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
            call.setReturnType(qn);
            
            res = (ComDataBean) call.invoke( new Object[] { pValue } );
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return res;
    
    }
/**
 * @see PortalWfsClient#getTemplateWorkflows(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public Vector getTemplateWorkflows(ComDataBean value){
        Vector res=new Vector();
        try{
            Service service;
            Call call;
            QName qn,qn1,qn2;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getTemplateWorkflows");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn1=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn1,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));


            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:wfsportalservice","ns1:AxisVector"),Vector.class);

            res = (Vector)call.invoke( new Object[] {value } );
            for(int i=0;i<res.size();i++){
                ((ComDataBean)res.get(i)).setWfsID(serviceURL);
                ((ComDataBean)res.get(i)).setWfsIDService(serviceID);
            }
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
/**
 * @see PortalWfsClient#getWorkflowInstanceJobs(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
  public Vector getWorkflowInstanceJobs(ComDataBean value){
        Vector res=new Vector();
        try{
            Service service;
            Call call;
            QName qn,qn1,qn2;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getWorkflowInstanceJobs");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn2=new QName( "urn:BeanService", "JobInstanceBean" );
            call.registerTypeMapping(JobInstanceBean.class, qn2,
                      new BeanSerializerFactory(JobInstanceBean.class, qn2),
                      new BeanDeserializerFactory(JobInstanceBean.class, qn2));

            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:wfsportalservice","ns1:AxisVector"),Vector.class);

            res = (Vector)call.invoke( new Object[] {value } );
            
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
  
  }
/**
 * @see PortalWfsClient#getWorkflowConfigDataError(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
  public Vector getWorkflowConfigDataError(ComDataBean value)
  {
        Vector res=new Vector();
        try 
        {
            Service service;
            Call call;
            QName qn,qn1,qn2;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getWorkflowConfigDataError");

            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                      new BeanSerializerFactory(ComDataBean.class, qn),
                      new BeanDeserializerFactory(ComDataBean.class, qn));
            qn1=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn1,
                      new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                      new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));
            qn2=new QName( "urn:BeanService", "WorkflowConfigErrorBean" );
            call.registerTypeMapping(WorkflowConfigErrorBean.class, qn2,
                      new BeanSerializerFactory(WorkflowConfigErrorBean.class, qn2),
                      new BeanDeserializerFactory(WorkflowConfigErrorBean.class, qn2));

            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:wfsportalservice","ns1:AxisVector"),Vector.class);

            res = (Vector)call.invoke( new Object[] {value } );
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
  }
  
  /**
 * @see PortalWfsClient#getRepositoryItems(hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean)
 */
@Override
    public Vector getRepositoryItems(RepositoryWorkflowBean bean) {
        Vector res = new Vector();
        try {
            Service service;
            Call call;
            QName qn;            

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getRepositoryItems");

            qn=new QName( "urn:BeanService", "RepositoryWorkflowBean" );
            call.registerTypeMapping(RepositoryWorkflowBean.class, qn,
                      new BeanSerializerFactory(RepositoryWorkflowBean.class, qn),
                      new BeanDeserializerFactory(RepositoryWorkflowBean.class, qn));
            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(new QName("urn:wfsportalservice","ns1:AxisVector"), Vector.class);

            res = (Vector) call.invoke( new Object[] { bean } );
        } catch (Exception fault) {
            System.out.println("HIBA"+fault.toString());fault.printStackTrace();
        }
        return res;
    }  
    
/**
 * @see PortalWfsClient#setNewGraf(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public boolean setNewGraf(ComDataBean value) {
        boolean res=false;
        try {
            Service service;
            Call call;
            QName qn,qn1;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("setNewGraf");
            
            qn=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn,
                    new BeanSerializerFactory(ComDataBean.class, qn),
                    new BeanDeserializerFactory(ComDataBean.class, qn));
            qn1=new QName("urn:wfsportalservice","ns1:AxisHashMap");
            call.registerTypeMapping(HashMap.class, qn1,
                    new BeanSerializerFactory(org.apache.axis.encoding.ser.ArraySerializerFactory.class, qn1),
                    new BeanDeserializerFactory(org.apache.axis.encoding.ser.ArrayDeserializerFactory.class, qn1));
            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            
            res = ((Boolean)call.invoke( new Object[] {value } )).booleanValue();
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
    
/**
 * @see PortalWfsClient#setNewTemplate(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public String setNewTemplate(ComDataBean value) {
        String res="";
        try {
            Service service;
            Call call;
            QName qn,qn1;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("setNewTemplate");
            
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
            
            res = ((String)call.invoke( new Object[] {value } )).toString();
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
/**
 * @see PortalWfsClient#getVolatileOutputs(hu.sztaki.lpds.wfs.com.ComDataBean)
 */
@Override
    public VolatileBean getVolatileOutputs(ComDataBean value) {
        VolatileBean volBean = new VolatileBean();
        try {
            Service service;
            Call call;
            QName qn, qn1, qn2;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getVolatileOutputs");
            
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
            call.addParameter( "arg1", qn1, ParameterMode.IN );
            call.setReturnType(qn);
            
            volBean = (VolatileBean)call.invoke(new Object[] {value});
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return volBean;
    }
    
}

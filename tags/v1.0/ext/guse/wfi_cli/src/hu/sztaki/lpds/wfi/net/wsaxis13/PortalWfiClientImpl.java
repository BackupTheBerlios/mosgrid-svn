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
 * PortalWfiClientImpl
 * Portal Axis1.x client for WFI call
 */

package hu.sztaki.lpds.wfi.net.wsaxis13;
import hu.sztaki.lpds.wfi.com.WorkflowInformationBean;
import java.util.*;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.wfi.inf.PortalWfiClient;
import hu.sztaki.lpds.wfs.com.ComDataBean;
/**
 * @author krisztian
 */
public class PortalWfiClientImpl implements PortalWfiClient
{
    private String serviceURL;
    private String serviceID;
    private int maxerror=50;
@Override
   public void setServiceURL(String value){serviceURL=value;}    
@Override
   public void setServiceID(String value){serviceID=value;}
@Override
    public String submitWorkflow(WorkflowRuntimeBean value)
    {
        String res="";
        try 
        {
            Call call;
            QName qn;
            Service service;
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("submitWorkflow");

            qn=new QName( "urn:BeanService", "WorkflowRuntimeBean" );
            call.registerTypeMapping(WorkflowRuntimeBean.class, qn,
                      new BeanSerializerFactory(WorkflowRuntimeBean.class, qn),
                      new BeanDeserializerFactory(WorkflowRuntimeBean.class, qn));
            

            call.addParameter( "arg1", qn, ParameterMode.IN );
             call.setReturnType(Constants.XSD_STRING);
                
              boolean ok=true;
              while(ok)
              {  
                ok=false;
                try{res=(String)call.invoke(new Object[] {value });}
                catch(Exception e)
                {
                    
                    e.printStackTrace();
                    ok=true;
                }
              }
              
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
       
        return res;
    }
@Override
    public Boolean abortWorkflow(String value)
    {
        boolean res=true;
        try 
        {
            Service service;
            Call call;
            QName qn;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("abortWorkflow");
            call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            
            call.invoke( new Object[] {value} );
            res = new Boolean(true);
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
@Override
    public Boolean rescueWorkflow(WorkflowRuntimeBean value)
    {
        boolean res=true;
        try 
        {
            Service service;
            Call call;
            QName qn, qn1, qn2;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("rescueWorkflow");

            qn=new QName( "urn:BeanService", "WorkflowRuntimeBean" );
            call.registerTypeMapping(WorkflowRuntimeBean.class, qn,
                      new BeanSerializerFactory(WorkflowRuntimeBean.class, qn),
                      new BeanDeserializerFactory(WorkflowRuntimeBean.class, qn));

            qn1=new QName( "urn:BeanService", "ComDataBean" );
            call.registerTypeMapping(ComDataBean.class, qn1,
                      new BeanSerializerFactory(ComDataBean.class, qn1),
                      new BeanDeserializerFactory(ComDataBean.class, qn1));

            qn2=new QName( "ns1:AxisVector", "Vector" );
            call.registerTypeMapping(Vector.class, qn2,
                      new VectorSerializerFactory(org.apache.axis.encoding.ser.VectorSerializerFactory.class, qn2),
                      new VectorDeserializerFactory(org.apache.axis.encoding.ser.VectorDeserializerFactory.class, qn2));
            
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            call.invoke( new Object[] {value } );
            res = new Boolean(true);
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
/**
 * Lists the data of the available workflows to the server console
 * @deprecated
 * @return is the operation successful or not
 */
    public Boolean listWorkflows()
    {
        boolean res=true;
        try 
        {
            Service service;
            Call call;
            QName qn;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getWaitingJob");
            call.addParameter( "arg1",Constants.XSD_STRING, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            call.invoke( new Object[] {"" } );
        } 
        catch (Exception fault) {System.out.println("HIBA:"+serviceURL+serviceID+fault.toString());fault.printStackTrace();}
        return res;
        
    }

@Override
    public Boolean reloadWorkflow(WorkflowRuntimeBean value)
    {
        boolean res=true;
        try 
        {
            Service service;
            Call call;
            QName qn;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("reloadWorkflow");

            qn=new QName( "urn:BeanService", "WorkflowRuntimeBean" );
            call.registerTypeMapping(WorkflowRuntimeBean.class, qn,
                      new BeanSerializerFactory(WorkflowRuntimeBean.class, qn),
                      new BeanDeserializerFactory(WorkflowRuntimeBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            res = (Boolean)call.invoke( new Object[] {value } );
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
        
    }
@Override
    public Vector<WorkflowInformationBean> getInformation() 
    {
        Vector<WorkflowInformationBean> res=new Vector<WorkflowInformationBean>();
        try 
        {
            Service service;
            Call call;
            QName qn;

            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getInformation");

            qn=new QName( "urn:BeanService", "WorkflowInformationBean" );
            call.registerTypeMapping(WorkflowInformationBean.class, qn,
                      new BeanSerializerFactory(WorkflowInformationBean.class, qn),
                      new BeanDeserializerFactory(WorkflowInformationBean.class, qn));
            call.setReturnType(new QName("urn:portalservice","ns1:AxisVector"),Vector.class);
            res = (Vector<WorkflowInformationBean>)call.invoke( new Object[] {} );
        } 
        catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        return res;
    }
    
}




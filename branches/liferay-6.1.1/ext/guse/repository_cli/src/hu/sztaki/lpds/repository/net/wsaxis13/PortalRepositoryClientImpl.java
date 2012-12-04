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
package hu.sztaki.lpds.repository.net.wsaxis13;

import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.repository.inf.PortalRepositoryClient;
import java.util.Hashtable;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

/**
 * @author lpds
 */
public class PortalRepositoryClientImpl implements PortalRepositoryClient {
    
    private String serviceURL="";
    
    private String serviceID="";
    
    public PortalRepositoryClientImpl(){}
    
    public PortalRepositoryClientImpl(String pServiceUrl){}
    
    /**
     * Beallitja az elerendo szerviz cimet
     * @param value A hasznalando szerviz eleresi pontja
     */
    public void setServiceURL(String value){serviceURL=value;}
    
    /**
     * Beallitja az elerendo szervizID-t
     * @param value A hasznalando szerviz hivatkozasi ID-ja
     */
    public void setServiceID(String value){serviceID=value;}
    
    /**
     * Egy workflow-t exportal a repository teruletere.
     *
     * @param bean A workflow exportalast leiro parameterek
     * @return hibajelzes
     */
    public String exportWorkflow(RepositoryWorkflowBean bean) {
        String retStr = new String();
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("exportWorkflow");
            
            qn=new QName( "urn:BeanService", "RepositoryWorkflowBean" );
            call.registerTypeMapping(RepositoryWorkflowBean.class, qn,
                    new BeanSerializerFactory(RepositoryWorkflowBean.class, qn),
                    new BeanDeserializerFactory(RepositoryWorkflowBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_STRING);
            
            retStr = (String) call.invoke( new Object[] { bean } );
            
            if(retStr == null) {
                throw new Exception("Not valid parameters ! retStr = (" + retStr + ")");
            }
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return retStr;
    }
    
    /**
     * Egy workflow-t torol ki a repository teruleterol.
     *
     * @param bean A workflow torlest leiro parameterek
     * @return hibajelzes
     */
    public String deleteWorkflow(RepositoryWorkflowBean bean) {
        String retStr = new String();
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("deleteWorkflow");
            
            qn=new QName( "urn:BeanService", "RepositoryWorkflowBean" );
            call.registerTypeMapping(RepositoryWorkflowBean.class, qn,
                    new BeanSerializerFactory(RepositoryWorkflowBean.class, qn),
                    new BeanDeserializerFactory(RepositoryWorkflowBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_STRING);
            
            retStr = (String) call.invoke( new Object[] { bean } );
            
            if(retStr == null) {
                throw new Exception("Not valid parameters ! retStr = (" + retStr + ")");
            }
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return retStr;
    }
    
    /**
     * Egy workflow-t importal a repository teruleterol.
     *
     * @param bean A workflow importalast leiro parameterek
     * @return hibajelzes
     */
    public String importWorkflow(RepositoryWorkflowBean bean) {
        String retStr = new String();
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("importWorkflow");
            
            qn=new QName( "urn:BeanService", "RepositoryWorkflowBean" );
            call.registerTypeMapping(RepositoryWorkflowBean.class, qn,
                    new BeanSerializerFactory(RepositoryWorkflowBean.class, qn),
                    new BeanDeserializerFactory(RepositoryWorkflowBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_STRING);
            
            retStr = (String) call.invoke( new Object[] { bean } );
            
            if(retStr == null) {
                throw new Exception("Not valid parameters ! retStr = (" + retStr + ")");
            }
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return retStr;
    }
    
    /**
     * A repository-tol kerdezi le az aktualis quota erteket.
     *
     * @param bean A workflow importalast leiro parameterek
     * @return hibajelzes
     */
    public Hashtable getQuota() {
        Hashtable retHash = new Hashtable();
        try {
            Service service;
            Call call;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("getQuota");
            
            call.setReturnType(new QName("urn:repositoryportalservice","ns1:AxisHashtable"),Hashtable.class);            
            
            retHash = (Hashtable) call.invoke( new Object[] { } );
            
            if(retHash == null) {
                throw new Exception("Not valid return parameter ! retHash = (" + retHash + ")");
            }
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return retHash;
    }
    
}

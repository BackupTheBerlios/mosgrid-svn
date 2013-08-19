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
 * Repozitory es WFS kozotti kommunikacio implementacioja
 */

package hu.sztaki.lpds.wfs.net.wsaxis13;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.wfs.inf.RepositoryWfsClient;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.*;
import org.apache.axis.Constants;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

/**
 * @author lpds
 */
public class RepositoryWfsClientImpl  implements RepositoryWfsClient {
    
    private String serviceURL="";
    
    private String serviceID="";
/**
 * Ures konstruktor
 */
    public RepositoryWfsClientImpl(){}
/**
 * Konstruktor
 * @param pServiceUrl Service url
 */
    public RepositoryWfsClientImpl(String pServiceUrl){}
    
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
 * @see RepositoryWfsClient#setRepositoryItem(hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean)
 */
@Override
    public String setRepositoryItem(RepositoryWorkflowBean bean) {
        String ret = new String("");
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("setRepositoryItem");
            
            qn=new QName( "urn:BeanService", "RepositoryWorkflowBean" );
            call.registerTypeMapping(RepositoryWorkflowBean.class, qn,
                    new BeanSerializerFactory(RepositoryWorkflowBean.class, qn),
                    new BeanDeserializerFactory(RepositoryWorkflowBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_STRING);
            
            ret = (String) call.invoke( new Object[] { bean } );
            
            if (ret == null) {
                ret = new String("Not valid response ! ret = (" + ret + ")");
            }
            return ret;
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return ret;
    }
    
/**
 * @see RepositoryWfsClient#deleteRepositoryItem(hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean)
 */
@Override
    public String deleteRepositoryItem(RepositoryWorkflowBean bean) {
        String ret = new String("");
        try {
            Service service;
            Call call;
            QName qn;
            
            service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(serviceURL+serviceID);
            call.setOperationName("deleteRepositoryItem");
            
            qn=new QName( "urn:BeanService", "RepositoryWorkflowBean" );
            call.registerTypeMapping(RepositoryWorkflowBean.class, qn,
                    new BeanSerializerFactory(RepositoryWorkflowBean.class, qn),
                    new BeanDeserializerFactory(RepositoryWorkflowBean.class, qn));
            call.addParameter( "arg1", qn, ParameterMode.IN );
            call.setReturnType(Constants.XSD_STRING);
            
            ret = (String) call.invoke( new Object[] { bean } );
            
            if (ret == null) {
                ret = new String("Not valid response ! ret = (" + ret + ")");
            }
            return ret;
            
        } catch (Exception fault) {System.out.println("HIBA"+fault.toString());fault.printStackTrace();}
        
        return ret;
    }
    
}

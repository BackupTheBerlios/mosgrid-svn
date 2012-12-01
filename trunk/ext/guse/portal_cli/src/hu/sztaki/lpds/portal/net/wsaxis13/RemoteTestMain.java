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
package hu.sztaki.lpds.portal.net.wsaxis13;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.Constants;

public class RemoteTestMain {
    
    /** Creates a new instance of RemoteTestMain */
    public RemoteTestMain() {
    }
    
    /**
     * calls a remote workflow submit
     *
     * @param purl - portal url
     * @param user - user name
     * @param wkey - workflow key
     * @param text - workflow text
     */
    public Boolean submittWorkflow(String purl, String user, String wkey, String text) {
        Boolean res = true;
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress("http://" + purl + "/portal30/services/urn:remotesubmit");
            call.setOperationName("submit");
            call.addParameter( "arg0", Constants.XSD_STRING, ParameterMode.IN );
            call.addParameter( "arg1", Constants.XSD_STRING, ParameterMode.IN );
            call.addParameter( "arg2", Constants.XSD_STRING, ParameterMode.IN );
            call.setReturnType(Constants.XSD_BOOLEAN);
            // call service...
            res = (Boolean) call.invoke( new Object[] { user, wkey, text } );
        } catch (Exception fault) {
            System.out.println("Fault : " + fault.toString());
            fault.printStackTrace();
        }
        return res;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RemoteTestMain test = new RemoteTestMain();
        test.submittWorkflow("n47.hpcc.sztaki.hu:9080", "test", "asd", "text55");
    }
    
}

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

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.repository.inf.PortalRepositoryClient;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.wfs.inf.RepositoryWfsClient;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author lpds
 */
public class PortalRepositoryClientTest {
    
     /*
      Hashtable hsh = new Hashtable();
      // hsh.put("url", bean.getWfsID());
      ServiceType st = InformationBase.getI().getService("repository", "portal", hsh, new Vector());
      PortalRepositoryClient client = (PortalRepositoryClient) Class.forName(st.getClientObject()).newInstance();
      client.setServiceURL(st.getServiceUrl());
      client.setServiceID(st.getServiceID());
      */
    
    private void exportTest() {
        String ret = new String("");
        try {
            PortalRepositoryClientImpl client = new PortalRepositoryClientImpl();
            client.setServiceURL("http://andras.lpds.sztaki.hu:8080/repository");
            client.setServiceID("/services/urn:repositoryportalservice");
            //
            RepositoryWorkflowBean bean = new RepositoryWorkflowBean();
            bean.setPortalID("http://andras.lpds.sztaki.hu:8080/portal30");
            bean.setStorageID("http://andras.lpds.sztaki.hu:8080/storage");
            bean.setWfsID("http://andras.lpds.sztaki.hu:8080/wfs");
            bean.setUserID("test");
            bean.setWorkflowID("w4");
            //
            bean.setDownloadType("all"); // graf, abst, real, all
            bean.setInstanceType("none"); // none, all, one_runtimeID
            bean.setOutputLogType("none"); // none, all
            //
            bean.setExportType("proj"); // work, proj, appl
            bean.setExportText("export_text_w4_asdfasdfaksjdgfkjasgdfksgadkf...");
            //
            ret = client.exportWorkflow(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("ret : " + ret);
    }
    
    private void deleteTest() {
        String ret = new String("");
        try {
            PortalRepositoryClientImpl client = new PortalRepositoryClientImpl();
            client.setServiceURL("http://andras.lpds.sztaki.hu:8080/repository");
            client.setServiceID("/services/urn:repositoryportalservice");
            //
            RepositoryWorkflowBean bean = new RepositoryWorkflowBean();
            bean.setWfsID("http://andras.lpds.sztaki.hu:8080/wfs");
            bean.setId(new Long(1));
            //
            ret = client.deleteWorkflow(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("ret : " + ret);
    }
    
    private void importTest() {
        String ret = new String("");
        try {
            PortalRepositoryClientImpl client = new PortalRepositoryClientImpl();
            client.setServiceURL("http://andras.lpds.sztaki.hu:8080/repository");
            client.setServiceID("/services/urn:repositoryportalservice");
            //
            RepositoryWorkflowBean bean = new RepositoryWorkflowBean();
            bean.setPortalID("http://andras.lpds.sztaki.hu:8080/portal30");
            bean.setStorageID("http://andras.lpds.sztaki.hu:8080/storage");
            bean.setWfsID("http://andras.lpds.sztaki.hu:8080/wfs");
            bean.setUserID("test");
            // bean.setId(new Long(1));
            bean.setZipRepositoryPath("projects/w4_expo-4815162342-108497.zip");
//            bean.setNewGrafName("i1");
//            bean.setNewAbstName("i2");
//            bean.setNewRealName("i3");
            //
            ret = client.importWorkflow(bean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("ret : " + ret);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PortalRepositoryClientTest test = new PortalRepositoryClientTest();
        //
        // test.exportTest();
        //
        // test.deleteTest();
        //
        test.importTest();
        //
    }
    
}

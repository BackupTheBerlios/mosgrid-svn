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
package hu.sztaki.lpds.storage.service.carmen.server.delete;

import hu.sztaki.lpds.wfs.com.ComDataBean;

/**
 * Calls the classes of the DeleteWorkflowFiles for testing purposes.
 * 
 * @author lpds
 */
public class DeleteWorkflowFilesTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // System.out.println("run...");
        try {
            // old begin
            // DeleteWorkflowFiles deleteWorkflowFiles = new DeleteWorkflowFiles(comDataBean);
            // deleteWorkflowFiles.deleteWorkflow_all("delportal", "deluser", "delworkflow1");
            // deleteWorkflowFiles.deleteWorkflow_outputs_all("delportal", "deluser", "delworkflow2");
            // deleteWorkflowFiles.deleteWorkflow_outputs_rtID("delportal", "deluser", "delworkflow3", "rtID");
            // old end
            //
            ComDataBean comDataBean = null;
            DeleteWorkflowFiles deleteWorkflowFiles = null;
            
            
            
            comDataBean = new ComDataBean();
            comDataBean.setPortalID("delportal");
            comDataBean.setUserID("deluser");
            comDataBean.setWorkflowID("delworkflow1");
            //
            deleteWorkflowFiles = new DeleteWorkflowFiles();
            deleteWorkflowFiles.deleteWorkflow_all(comDataBean);
            
            
            comDataBean = new ComDataBean();
            comDataBean.setPortalID("delportal");
            comDataBean.setUserID("deluser");
            comDataBean.setWorkflowID("delworkflow2");
            //
            deleteWorkflowFiles = new DeleteWorkflowFiles();
            deleteWorkflowFiles.deleteWorkflow_outputs_all(comDataBean);
            
            
            
            comDataBean = new ComDataBean();
            comDataBean.setPortalID("delportal");
            comDataBean.setUserID("deluser");
            comDataBean.setWorkflowID("delworkflow3");
            comDataBean.setWorkflowRuntimeID("rtID");
            //
            deleteWorkflowFiles = new DeleteWorkflowFiles();
            deleteWorkflowFiles.deleteWorkflow_outputs_rtID(comDataBean);
            
            
            
            comDataBean = new ComDataBean();
            comDataBean.setPortalID("delportal");
            comDataBean.setUserID("deluser");
            comDataBean.setWorkflowID("delworkflow4");
            comDataBean.setJobID("job1");
            comDataBean.setWorkflowRuntimeID("rtID");
            //
            deleteWorkflowFiles = new DeleteWorkflowFiles();
            deleteWorkflowFiles.deleteWorkflow_log_outputs(comDataBean);
            
            
        } catch (Exception e) {
            // e.printStackTrace();
        }
        // System.out.println("end...");
    }

}

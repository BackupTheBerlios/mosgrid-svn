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
 * Definition of the services offered from the WFI to the portal
 */

package hu.sztaki.lpds.wfi.inf;

import hu.sztaki.lpds.wfi.com.WorkflowInformationBean;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import java.util.Vector;

/**
 * @author krisztian
 */
public interface WfiPortalService 
{
/**
 * Workflow Submit
 * @param pWorkflowData workflow descriptor
 * @return workflow runtime ID
 */    
    public String submitWorkflow(WorkflowRuntimeBean pWorkflowData);

/**
 * Workflow abort
 * @param pRuntimeID workflow runtime ID
 */
    public void abortWorkflow(String pRuntimeID);
/**
 * Workflow restart
 * @param pWorkflowData workflow descriptor
 */    
    public void rescueWorkflow(WorkflowRuntimeBean pWorkflowData);
    
/**
 * Listing the running workflows to the server console
 * @param pURL not used
 */
    public void getWaitingJob(String pURL);
    
/**
 * Getting information about the statuses of the running workflows
 * @return Workflow descriptions
 * @see WorkflowInformationBean
 */
    public Vector<WorkflowInformationBean> getInformation();

}

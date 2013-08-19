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
 *
 * Client interface of the communication between the portal and the WFI
 */

package hu.sztaki.lpds.wfi.inf;
import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.wfi.com.WorkflowInformationBean;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import java.util.Vector;

public interface PortalWfiClient extends BaseCommunicationFace
{
/**
 * Submitting the workflow
 * @param value The descriptor of the workflow submission
 */
    public String submitWorkflow(WorkflowRuntimeBean value);

/**
 * Aborts the running of a workflow
 * @param value The runtime descriptor ID of the workflow
 */
    public Boolean abortWorkflow(String value);

/**
 * Continues the run of a workflow after failure
 * @param value The descriptor of the workflow submission
 * @deprected
 */
    public Boolean rescueWorkflow(WorkflowRuntimeBean value);
    
/**
 * Reloads the workflow
 * @param value The descriptor of the workflow submission
 * @deprected
 */
    public Boolean reloadWorkflow(WorkflowRuntimeBean value);
    
/**
 * Query of the status of the running workflows
 * @return Workflow descriptions
 * @see WorkflowInformationBean
 */
    public Vector<WorkflowInformationBean> getInformation();
}

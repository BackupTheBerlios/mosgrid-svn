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

package hu.sztaki.lpds.pgportal.service.workflow.notify.eventhandlers;

import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowRunTime;
import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import hu.sztaki.lpds.pgportal.service.workflow.notify.Notify;
import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifyBean;
import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifySendThread;
import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifyUtils;
import hu.sztaki.lpds.wfs.com.JobStatusBean;

/**
 * Checks a workflow status and
 * sends a message to the user if it is needed.
 *
 * @author lpds
 */
public class NotifyWorkflowStatusChange {
    
    private static final String statusKey = new String("portal.WorkflowData.status.");
/**
 * Constructor
 */
    public NotifyWorkflowStatusChange() {}
    
    /**
     * Decides whether a message should be sent 
     * to the user about the status of the workflow.
     * @param sBean status information
     * @throws Exception execution error
     */
    public void notifyHandler(JobStatusBean sBean) throws Exception {
        WorkflowRunTime wf = PortalCacheService.getInstance().getUser(sBean.getUserID()).getWorkflow(sBean.getWorkflowID()).getRuntime(sBean.getWrtID());
        String wfchgType = wf.getNotifyWfchgType();
        if (!"".equals(wfchgType)) {
            String oldWfStatus = ""+wf.getLastWorkflowStatus();
            String newWfStatus = ""+wf.getStatus();
            if (!oldWfStatus.equals(newWfStatus)) {
                // clear chgStr - disable workflow chg notify
                if (("6".equals(newWfStatus)) || ("7".equals(newWfStatus)) || ("22".equals(newWfStatus)) || ("37".equals(newWfStatus))) {
                    PortalCacheService.getInstance().getUser(sBean.getUserID()).getWorkflow(sBean.getWorkflowID()).getRuntime(sBean.getWrtID()).setNotifyWfchgType("");
                }
                if ("chg".equals(wfchgType)) {
                    // in case of a workflow status change
                    // a notification should be sent to the user
                    //
                    sendNotify(sBean, oldWfStatus, newWfStatus);
                }
                if ("end".equals(wfchgType)) {
                    // when the workflow execution finishes
                    // a notification should be sent to the user
                    // if the status of the workflow is error or finished
                    //
                    if (("6".equals(newWfStatus)) || ("7".equals(newWfStatus)) || ("22".equals(newWfStatus)) || ("37".equals(newWfStatus))) {
                        sendNotify(sBean, oldWfStatus, newWfStatus);
                    }
                }
            }
        }
    }
    
    /**
     * Notifies the user about 
     * the status changes of the workflow.
     *
     * @throws Exception
     */
    private void sendNotify(JobStatusBean sBean, String oldWfStatus, String newWfStatus) throws Exception {
        // load event handler informations...
        NotifyBean handlerBean = NotifyUtils.getInstance().loadNotifyInformations(sBean.getUserID());
        if (Notify.enabled.equals(handlerBean.getValue(Notify.wfchg_enab))) {
            // set base information
            NotifyBean notifyBean = new NotifyBean();
            notifyBean.setValue(Notify.now, NotifyUtils.getInstance().getNowDateTimeStampWithSeconds());
            notifyBean.setValue(Notify.portal, NotifyUtils.getInstance().getPortalUrl());
            notifyBean.setValue(Notify.user, sBean.getUserID());
            notifyBean.setValue(Notify.workflow, sBean.getWorkflowID());
            notifyBean.setValue(Notify.instance, PortalCacheService.getInstance().getUser(sBean.getUserID()).getWorkflow(sBean.getWorkflowID()).getRuntime(sBean.getWrtID()).getText());
            oldWfStatus = "" + PortalMessageService.getI().getMessage(statusKey + oldWfStatus);
            newWfStatus = "" + PortalMessageService.getI().getMessage(statusKey + newWfStatus);
            notifyBean.setValue(Notify.oldstatus, oldWfStatus);
            notifyBean.setValue(Notify.newstatus, newWfStatus);
            // get template message
            String message = handlerBean.getValue(Notify.wfchg_mess).trim();
            // prepare message
            if ("".equals(message.trim())) {
                // message = PortalMessageService.getI().getMessage("portal.NotifyPortlet.default.wfchg.mess");
                message = "gUse_NotifyService_message";
            }
            message = message.replaceAll(Notify.now, notifyBean.getValue(Notify.now));
            message = message.replaceAll(Notify.portal, notifyBean.getValue(Notify.portal));
            message = message.replaceAll(Notify.user, notifyBean.getValue(Notify.user));
            message = message.replaceAll(Notify.workflow, notifyBean.getValue(Notify.workflow));
            message = message.replaceAll(Notify.instance, notifyBean.getValue(Notify.instance));
            message = message.replaceAll(Notify.oldstatus, notifyBean.getValue(Notify.oldstatus));
            message = message.replaceAll(Notify.newstatus, notifyBean.getValue(Notify.newstatus));
            // set prepared message
            notifyBean.setValue(Notify.message, message);
            // send notify
            new NotifySendThread(notifyBean);
        }
    }
    
}

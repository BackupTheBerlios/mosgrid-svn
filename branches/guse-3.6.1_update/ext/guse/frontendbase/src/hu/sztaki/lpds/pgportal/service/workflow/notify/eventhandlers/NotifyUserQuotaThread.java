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

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.workflow.notify.Notify;
import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifyBean;
import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifySendThread;
import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifyUtils;
import java.util.Enumeration;

/**
 * Checks the storage quota of every user and
 * sends a message to the user if it is needed.
 *
 * @author lpds
 */
public class NotifyUserQuotaThread extends Thread {
    
    private Long sleepTimeInHour = new Long("12");
    
    private Long quotaPercentMax = new Long("95");
/**
 * Constructor, thread start
 */
    public NotifyUserQuotaThread() {
        // System.out.println("NotifyUserQuotaThread start...");
        this.start();
    }
/**
 * @see Thread#run()
 */
@Override
    public void run() {
        //
        while (true) {
            try {
                // load propertyes...
                loadProperties();
                // sleep and dreaming...
                doSleep(this.sleepTimeInHour);
                // parse user list...
                Enumeration userListEnum = PortalCacheService.getInstance().getUserListEnum();
                while (userListEnum.hasMoreElements()) {
                    String userID = (String) userListEnum.nextElement();
                    notifyHandler(userID);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Loads the required parameters 
     * from the property file.
     */
    private void loadProperties() {
        try {
            // load property
            sleepTimeInHour = new Long("" + PropertyLoader.getInstance().getProperty(Notify.property_quota_time));
            if (sleepTimeInHour == null) {
                sleepTimeInHour = new Long("12");
            }
            if (sleepTimeInHour.longValue() < 1) {
                sleepTimeInHour = new Long("1");
            }
            // load property
            quotaPercentMax = new Long("" + PropertyLoader.getInstance().getProperty(Notify.property_quota_max_percent));
            if (quotaPercentMax == null) {
                quotaPercentMax = new Long("95");
            }
            if (quotaPercentMax.longValue() > 100) {
                quotaPercentMax = new Long("100");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Decides whether a message should be sent 
     * to the user about the status of the quota.
     *
     * @param userID - user ID
     * @throws Exception
     */
    private void notifyHandler(String userID) throws Exception {
        try {
            // System.out.println("user in notify user quota thread : " + userID);
            //
            long quotaSpace = PortalCacheService.getInstance().getUser(userID).getQuotaSpace(userID);
            long usedQuotaSpace = PortalCacheService.getInstance().getUser(userID).getUseQuotaSpace();
            long quotaPercent = usedQuotaSpace * 100 / quotaSpace;
            //
            if (quotaPercent > this.quotaPercentMax.longValue()) {
                sendNotify(userID, String.valueOf(quotaSpace), String.valueOf(usedQuotaSpace), String.valueOf(this.quotaPercentMax.longValue()), String.valueOf(quotaPercent));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Notifies the user about the status of the quota.
     *
     * @throws Exception
     */
    private void sendNotify(String userID, String quotaSpaceStr, String usedQuotaSpaceStr, String quotaPercentMaxStr, String quotaPercentStr) throws Exception {
        // load event handler informations...
        NotifyBean handlerBean = NotifyUtils.getInstance().loadNotifyInformations(userID);
        if (Notify.enabled.equals(handlerBean.getValue(Notify.quota_enab))) {
            // set base information
            NotifyBean notifyBean = new NotifyBean();
            notifyBean.setValue(Notify.now, NotifyUtils.getInstance().getNowDateTimeStampWithSeconds());
            notifyBean.setValue(Notify.portal, NotifyUtils.getInstance().getPortalUrl());
            notifyBean.setValue(Notify.user, userID);
            notifyBean.setValue(Notify.quota_space, quotaSpaceStr);
            notifyBean.setValue(Notify.quota_space_used, usedQuotaSpaceStr);
            notifyBean.setValue(Notify.quota_percent_max, quotaPercentMaxStr);
            notifyBean.setValue(Notify.quota_percent, quotaPercentStr);
            // get template message
            String message = handlerBean.getValue(Notify.quota_mess).trim();
            // prepare message
            if ("".equals(message.trim())) {
                // message = PortalMessageService.getI().getMessage("portal.NotifyPortlet.default.quota.mess");
                message = "gUse_NotifyService_message";
            }
            message = message.replaceAll(Notify.now, notifyBean.getValue(Notify.now));
            message = message.replaceAll(Notify.portal, notifyBean.getValue(Notify.portal));
            message = message.replaceAll(Notify.user, notifyBean.getValue(Notify.user));
            message = message.replaceAll(Notify.quota_space, notifyBean.getValue(Notify.quota_space));
            message = message.replaceAll(Notify.quota_space_used, notifyBean.getValue(Notify.quota_space_used));
            message = message.replaceAll(Notify.quota_percent_max, notifyBean.getValue(Notify.quota_percent_max));
            message = message.replaceAll(Notify.quota_percent, notifyBean.getValue(Notify.quota_percent));
            // set prepared message
            notifyBean.setValue(Notify.message, message);
            // send notify
            new NotifySendThread(notifyBean);
        }
    }
    
    /**
     * Sleeping some minutes.
     */
    private void doSleep(Long sleepTimeInHour) {
        try {
            // System.out.println("sleepTimeInHour : " + sleepTimeInHour);
            this.sleep(sleepTimeInHour.longValue() * 60 * 60 * 1000);
            // only test time 2 min // this.sleep(2 * 60 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

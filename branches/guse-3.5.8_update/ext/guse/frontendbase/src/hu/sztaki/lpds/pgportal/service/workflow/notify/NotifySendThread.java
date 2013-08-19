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

package hu.sztaki.lpds.pgportal.service.workflow.notify;

import hu.sztaki.lpds.pgportal.service.workflow.notify.plugins.NotifyPlugin;
import java.util.Enumeration;

/**
 * Forwards a notification, a message
 * with the proper message sending plugin.
 *
 * The plugins load dynamically.
 *
 * @author lpds
 */
public class NotifySendThread extends Thread {
    
    // in millisec
    private int randomSleepTime;
    
    private NotifyBean notifyBean;
/**
 * Constructor
 * @param notifyBean message descriptor
 */
    public NotifySendThread(NotifyBean notifyBean) {
        randomSleepTime = (int) Math.round(Math.random() * 1600);
        this.notifyBean = notifyBean;
        this.start();
    }
/**
 * @see Thread#run() 
 */
 @Override
    public void run() {
        doSleep();
        try {
            // try to load notify plugins, iterate plugins...
            Enumeration pluginListEnum = NotifyUtils.getInstance().getPluginList().elements();
            while (pluginListEnum.hasMoreElements()) {
                String notifyPlugin = (String) pluginListEnum.nextElement();
                if ((notifyPlugin != null) && (!"".equals(notifyPlugin))) {
                    // System.out.println("notifyPlugin : " + notifyPlugin);
                    // load plugin
                    NotifyPlugin plugin = (NotifyPlugin) Class.forName("hu.sztaki.lpds.pgportal.service.workflow.notify.plugins." + notifyPlugin.toLowerCase() + "_NotifyPluginImpl").newInstance();
                    // use plugin
                    plugin.sendNotify(notifyBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // while (true) {}
    }
    
    /**
     * Sleeping some minutes.
     */
    private void doSleep() {
        try {
            // System.out.println("randomSleepTime : " + randomSleepTime);
            this.sleep(randomSleepTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

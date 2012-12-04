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
/*
 * Implementation class of
 * a notifier plugin
 *
 * The notification will be sent in email to the user.
 *
 */

package hu.sztaki.lpds.pgportal.service.workflow.notify.plugins;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.workflow.notify.Notify;
import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifyBean;
import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifyUtils;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author lpds
 */
public class email_NotifyPluginImpl implements NotifyPlugin {
    
    /**
     * Constructor
     */
    public email_NotifyPluginImpl() {}
    
    public void sendNotify(NotifyBean notifyBean) throws Exception {
        try {
            String userID = notifyBean.getValue(Notify.user);
            // load plugin information
            NotifyBean pluginBean = NotifyUtils.getInstance().loadNotifyInformations(userID);
            //
            if (Notify.enabled.equals(pluginBean.getValue(Notify.email_enab))) {
                String smtp = PropertyLoader.getInstance().getProperty(Notify.property_smtp_host);
                String from = new String("gUse_NotifyService_NoReply");
                String addr = pluginBean.getValue(Notify.email_addr).trim();
                addr = addr.replaceAll(";", ", ");
                String subj = pluginBean.getValue(Notify.email_subj).trim();
                // prepare subj
                if ("".equals(subj.trim())) {
                    // subj = PortalMessageService.getI().getMessage("portal.NotifyPortlet.default.subj");
                    subj = "gUse_NotifyService_subject";
                }
                subj = subj.replaceAll(Notify.user, notifyBean.getValue(Notify.user));
                subj = subj.replaceAll(Notify.portal, notifyBean.getValue(Notify.portal));
                //
                String mess = notifyBean.getValue(Notify.message);
                // send email
                if ((smtp == null) || (from == null) || (addr == null) || (subj == null) || (mess == null)) {
                    throw new Exception("not valid email informations...");
                }
                if (("".equals(smtp)) || ("".equals(from)) || ("".equals(addr)) || ("".equals(subj)) || ("".equals(mess))) {
                    throw new Exception("not valid email informations...");
                }
                // Get system properties
                Properties props = System.getProperties();
                // Specify the desired SMTP server
                props.put("mail.smtp.host", smtp);
                // create a new Session object
                Session session = Session.getInstance(props, null);
                // create a new MimeMessage object (using the Session created above)
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from));
                InternetAddress[] addrs = new InternetAddress().parse(addr);
                message.setRecipients(Message.RecipientType.TO, addrs);
                // message.setRecipients(Message.RecipientType.CC, new InternetAddress[] { new InternetAddress(cc) });
                message.setSubject(subj);
                message.setContent(mess, "text/plain");
                Transport.send(message);
            } else {
                throw new Exception("plugin is disabled...");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

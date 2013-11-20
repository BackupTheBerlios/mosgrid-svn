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
package hu.sztaki.lpds.pgportal.portlets.settings;

import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import hu.sztaki.lpds.pgportal.service.workflow.notify.Notify;
import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifyBean;
import hu.sztaki.lpds.pgportal.service.workflow.notify.NotifyUtils;
import java.io.IOException;
import javax.mail.internet.InternetAddress;
import javax.portlet.*;


/**
 * A felhasznalo ertesitesehez szukseges konfiguracios
 * adatokat, beallitasokat kezelo portlet.
 *
 * (pl: notify email plugin configuracios
 * adatok, emailcimek...)
 *
 * @author lpds
 */
public class NotifyPortlet extends GenericWSPgradePortlet
{
    
    private String notify_main_jsp = "/jsp/workflow/notify.jsp";
    
    public NotifyPortlet() {
    }
    

    
    /**
     * View user notify settings informations...
     */
    @Override
    public void doView(RenderRequest req, RenderResponse res) throws PortletException,IOException
    {
        res.setContentType("text/html");
        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(req, res);
            return;
        }
        openRequestAttribute(req);
        try {
            // System.out.println("doView()...");
            String userID = req.getRemoteUser();
            // read settings information from notify xml file...
            try {
                NotifyBean settingsBean = NotifyUtils.getInstance().loadNotifyInformations(userID);
                // set values...
                req.setAttribute("value_email_enab" + settingsBean.getValue(Notify.email_enab), " selected=\"true\"");
                req.setAttribute("value_email_addr", settingsBean.getValue(Notify.email_addr));
                req.setAttribute("value_email_subj", settingsBean.getValue(Notify.email_subj));
                req.setAttribute("value_wfchg_enab" + settingsBean.getValue(Notify.wfchg_enab), " selected=\"true\"");
                req.setAttribute("value_wfchg_mess", settingsBean.getValue(Notify.wfchg_mess));
                req.setAttribute("value_quota_enab" + settingsBean.getValue(Notify.quota_enab), " selected=\"true\"");
                req.setAttribute("value_quota_mess", settingsBean.getValue(Notify.quota_mess));
            } catch (Exception e) {
                e.printStackTrace();
                // not found notify file
            }
            // dispatch jsp...
            PortletRequestDispatcher dispatcher;
            dispatcher = getPortletContext().getRequestDispatcher(notify_main_jsp);
            dispatcher.include(req, res);
        } catch (IOException e) {
            throw new PortletException("JSPPortlet.doView exception", e);
        }

        cleanRequestAttribute(req.getPortletSession());
    }
    
    /**
     * Save user notify informations...
     */
    public void doSave(ActionRequest request, ActionResponse response) throws PortletException
    {
        
        // System.out.println("doSave()...");
        String msg = new String("");
        try {
            NotifyBean notifyBean = new NotifyBean();
            String userID = request.getRemoteUser();
            notifyBean.setValue(Notify.email_enab, request.getParameter("email_enab"));
            notifyBean.setValue(Notify.email_addr, request.getParameter("email_addr"));
            notifyBean.setValue(Notify.email_subj, request.getParameter("email_subj"));
            notifyBean.setValue(Notify.wfchg_enab, request.getParameter("wfchg_enab"));
            notifyBean.setValue(Notify.wfchg_mess, request.getParameter("wfchg_mess"));
            notifyBean.setValue(Notify.quota_enab, request.getParameter("quota_enab"));
            notifyBean.setValue(Notify.quota_mess, request.getParameter("quota_mess"));
            // Enumeration enumParam = request.getParameterNames();
            // while (enumParam.hasMoreElements()) {
            //     String paramName = (String) enumParam.nextElement();
            //     String paramValue = (String) request.getParameter(paramName);
            //     System.out.println("paramName : " + paramName + " paramValue : " + paramValue);
            // }
            // Enumeration enumAttr = request.getAttributeNames();
            // while (enumAttr.hasMoreElements()) {
            //     String attrName = (String) enumAttr.nextElement();
            //     System.out.println("attrName : " + attrName + " attrValue : " + request.getAttribute(attrName));
            // }
            if ((notifyBean.getValue(Notify.quota_mess) == null) || ("".equals(notifyBean.getValue(Notify.quota_mess).trim()))) {
                notifyBean.setValue(Notify.quota_mess, PortalMessageService.getI().getMessage("portal.NotifyPortlet.default.quota.mess"));
                // msg = PortalMessageService.getI().getMessage("portal.NotifyPortlet.doSave.notvalid.quota.mess");
            }
            if ((notifyBean.getValue(Notify.wfchg_mess) == null) || ("".equals(notifyBean.getValue(Notify.wfchg_mess).trim()))) {
                notifyBean.setValue(Notify.wfchg_mess, PortalMessageService.getI().getMessage("portal.NotifyPortlet.default.wfchg.mess"));
                // msg = PortalMessageService.getI().getMessage("portal.NotifyPortlet.doSave.notvalid.wfchg.mess");
            }
            if ((notifyBean.getValue(Notify.email_subj) == null) || ("".equals(notifyBean.getValue(Notify.email_subj).trim()))) {
                notifyBean.setValue(Notify.email_subj, PortalMessageService.getI().getMessage("portal.NotifyPortlet.default.subj"));
                // msg = PortalMessageService.getI().getMessage("portal.NotifyPortlet.doSave.notvalid.subj");
            }
            // validate email address (null or empty)
            if ((notifyBean.getValue(Notify.email_addr) == null) || ("".equals(notifyBean.getValue(Notify.email_addr).trim()))) {
                msg = PortalMessageService.getI().getMessage("portal.NotifyPortlet.doSave.notvalid.addr");
            } else {
                // validate email address
                try {
                    String[] addrs = notifyBean.getValue(Notify.email_addr).split(";");
                    for (int n = 0; n < addrs.length; n++) {
                        String addr = addrs[n];
                        InternetAddress toAddr = new InternetAddress(addr);
                        toAddr.validate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg = PortalMessageService.getI().getMessage("portal.NotifyPortlet.doSave.notvalid.addr");
                }
            }
            // save user notify informations, settings...
            if ("".equals(msg.trim())) {
                try {
                    NotifyUtils.getInstance().saveNotifyInformations(userID, notifyBean);
                    // save ok
                    msg = PortalMessageService.getI().getMessage("portal.NotifyPortlet.doSave.success");
                } catch (Exception e) {
                    e.printStackTrace();
                    // save not ok
                    msg = PortalMessageService.getI().getMessage("portal.NotifyPortlet.doSave.notsuccess");
                }
            }
            // set last values...
            setRequestAttribute(request.getPortletSession(),"value_email_enab" + notifyBean.getValue(Notify.email_enab), " selected=\"true\"");
            setRequestAttribute(request.getPortletSession(),"value_email_addr", notifyBean.getValue(Notify.email_addr));
            setRequestAttribute(request.getPortletSession(),"value_email_subj", notifyBean.getValue(Notify.email_subj));
            setRequestAttribute(request.getPortletSession(),"value_wfchg_enab" + notifyBean.getValue(Notify.wfchg_enab), " selected=\"true\"");
            setRequestAttribute(request.getPortletSession(),"value_wfchg_mess", notifyBean.getValue(Notify.wfchg_mess));
            setRequestAttribute(request.getPortletSession(),"value_quota_enab" + notifyBean.getValue(Notify.quota_enab), " selected=\"true\"");
            setRequestAttribute(request.getPortletSession(),"value_quota_mess", notifyBean.getValue(Notify.quota_mess));
            // set message...
            setRequestAttribute(request.getPortletSession(),"notifymsg", msg);
        } catch (Exception e) {
            e.printStackTrace();
            // msg = PortalMessageService.getI().getMessage("portal.NotifyPortlet.doSave.exception");
            msg = e.getMessage();
            // System.out.println("msg2: " + msg);
            setRequestAttribute(request.getPortletSession(),"notifymsg", msg);
        }
        
    }
    
}

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
package hu.sztaki.lpds.pgportal.service.workflow;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import hu.sztaki.lpds.repository.inf.PortalRepositoryClient;
import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;


/**
 * Using the GrafEditorPortlet, TemplateListPortlet 
 * and the RealWorkflowPortlet. (helper class)
 *
 * (This makes the call of the repository,
 * the call of the export)
 *
 * @author lpds
 */
public class WorkflowExportUtils {
    
    private static WorkflowExportUtils instance = null;
/**
 * Constructor, creating the singleton instance
 */
    public WorkflowExportUtils() {
        if (instance == null) {
            instance = this;
        }
    }
    
    /**
     * Returns the WorkflowExportUtils instance.
     *
     * @return
     */
    public static WorkflowExportUtils getInstance() {
        if (instance == null) {
            instance = new WorkflowExportUtils();
        }
        return instance;
    }
    
    /**
     * Gets the required parameters from the request
     * and creates the repository workflow bean.
     *
     * @param request - hidden parameter hash
     * @param remoteUser logged in user
     * @return RepositoryWorkflowBean bean - export descriptor parameters
     * @throws Exception
     */
    public RepositoryWorkflowBean getBeanFromRequest(Map<String,String[]> request,String remoteUser) throws Exception {
        RepositoryWorkflowBean bean = new RepositoryWorkflowBean();
        bean.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
        WorkflowData wfd=PortalCacheService.getInstance().getUser(remoteUser).getWorkflow(request.get("workflowID")[0]);
        bean.setStorageID(wfd.getStorageID());
        bean.setWfsID(wfd.getWfsID());
        bean.setUserID(remoteUser);
        bean.setWorkflowID(request.get("workflowID")[0]);
        bean.setWorkflowType(request.get("typ")[0]); // graf, abst, work, proj, appl
        bean.setExportText(request.get("exporttext")[0].trim());
        //
        bean.setInstanceType("none"); // none, all, one_runtimeID
        bean.setOutputLogType("none"); // none, all
        //
        return bean;
    }
    
    /**
     * Calls the repository with a client and
     * starts a workflow export.
     * @param bean - export descriptor parameters
     * @return  error message
     */
    public String exportWorkflow(RepositoryWorkflowBean bean) {
        String msg = new String("");
        try {
            //
            bean.setExportText(getFilteredString(bean.getExportText()));
            //
            // export workflow to repository...
            Hashtable hsh = new Hashtable();
            // hsh.put("url", bean.getWfsID());
            ServiceType st = InformationBase.getI().getService("repository", "portal", hsh, new Vector());
            PortalRepositoryClient repoClient = (PortalRepositoryClient) Class.forName(st.getClientObject()).newInstance();
            repoClient.setServiceURL(st.getServiceUrl());
            repoClient.setServiceID(st.getServiceID());
            String retStr = repoClient.exportWorkflow(bean);
            // System.out.println("retStr : " + retStr);
            if (retStr == null) {
                throw new Exception("Not found repository client !");
            }
            if (!"".equals(retStr)) {
                msg = PortalMessageService.getI().getMessage("portal.ExportPortlets.doExport.notsuccess") + " : " + retStr;
            } else {
                msg = PortalMessageService.getI().getMessage("portal.ExportPortlets.doExport.success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = PortalMessageService.getI().getMessage("portal.ExportPortlets.doExport.notsuccess") + " : " + e.getLocalizedMessage();
            // System.out.println("msg2: " + msg);
        }
        return msg;
    }

    /**
     * HTML and script elements filter...
     *
     * @return clear text
     */
    private String getFilteredString(String text) {
        text = text.replaceAll("<", "");
        text = text.replaceAll(">", "");
        return text;
    }
    
}

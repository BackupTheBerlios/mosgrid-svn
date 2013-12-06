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
 *Graph editor inditasa
 */

package hu.sztaki.lpds.pgportal.portlets.workflow;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * @author Krisztian Karoczkai
 */
public class GraphEditorUtil {
/**
 * Graph editor elinditasa request.getParameter("wfId") tartalmazza a szerkesztendo graphot
 * @param request keres lieroja
 * @param response valasz
 * @throws javax.portlet.PortletException portlet hiba
 * @throws java.io.IOException IO hiba
 */
    public static void jnpl(ResourceRequest request, ResourceResponse response) throws PortletException, IOException{
        boolean secureFlag = true;
        String user=request.getRemoteUser();
        String wfId=request.getParameter("wfId");

        ServiceType st=null;
        st=InformationBase.getI().getService("wfs","portal",new Hashtable(),new Vector());
        String wfsID = new String(st.getServiceUrl());

        Hashtable h=new Hashtable();
        h.put("url",wfsID);
        ServiceType sts=InformationBase.getI().getService("wfs","portal",h,new Vector());
        String secureWfsUrl = st.getSecureServiceUrl();

        String portalID = new String(PropertyLoader.getInstance().getProperty("service.url"));
        h=new Hashtable();
        h.put("url",portalID);
        sts=InformationBase.getI().getService("portal","portal",h,new Vector());
        String securePortalUrl = sts.getSecureServiceUrl();
        String WEPortalServiceUrl = new String(securePortalUrl + "/xmlrpc");

        String WEWFSServiceUrl = new String(secureWfsUrl + "/xmlrpc");

        response.setContentType("application/x-java-jnlp-file");

        StringBuffer jnlp = new StringBuffer();
        jnlp.append("<?xml version=\"1.0\" encoding=\"utf-8\"?> \n<!--JNLP File for Workflow Application -->");
        jnlp.append("\n <jnlp spec=\"1.0+\"");
        jnlp.append("\n \t codebase=\"" + securePortalUrl + "/jsp/pgrade/\">");
        jnlp.append("\n <information>");
        jnlp.append("\n    <title>Workflow Application</title> ");
        jnlp.append("\n    <vendor>MTA SZTAKI Laboratory of Parallel and Distributed Systems</vendor>");
        jnlp.append("\n    <homepage href=\"http://www.lpds.sztaki.hu\"/>");
        jnlp.append("\n    <description></description>");
        jnlp.append("\n </information>");
        jnlp.append("\n <security>");
        jnlp.append("\n   <j2ee-application-client-permissions/>");
        jnlp.append("\n </security>");
        jnlp.append("\n <resources>");
        jnlp.append("\n   <j2se version=\"1.5+\"/>");
        jnlp.append("\n   <jar href=\"workflow.jar\"/>");
        jnlp.append("\n   <property name=\"secure\" value=\"" + Boolean.toString(secureFlag).toLowerCase() + "\"/>");
        jnlp.append("\n   <property name=\"WEPortalServiceUrl\" value=\"" + WEPortalServiceUrl + "\"/>");
        jnlp.append("\n   <property name=\"WEWFSServiceUrl\" value=\"" + WEWFSServiceUrl + "\"/>");
        jnlp.append("\n   <property name=\"portalID\" value=\"" + portalID + "\"/>");
        jnlp.append("\n   <property name=\"wfsID\" value=\"" + wfsID + "\"/>");
        jnlp.append("\n   <property name=\"visualisationRefreshTime\" value=\"60\"/>");
        jnlp.append("\n   <property name=\"userName\" value=\"" + user +"\"/>");
        jnlp.append("\n   <property name=\"debugErrorMessage\" value=\"\"/>");
        jnlp.append("\n   <property name=\"helpUrl\" value=\"http://www.lpds.sztaki.hu/gUSE/grapheditor/\"/>");
        jnlp.append("\n   <property name=\"isDemo\" value=\"false\"/>");
        String attachWorkflowName = (wfId == null) ? "" : wfId;
        jnlp.append("\n   <property name=\"attachedWorkflowName\" value=\"" + attachWorkflowName + "\"/>");
        jnlp.append("\n   <jar href=\"xmlrpc.jar\"/>");
        jnlp.append("\n   <jar href=\"dom.jar\"/>");
        jnlp.append("\n </resources>");
        jnlp.append("\n <application-desc main-class=\"hu.sztaki.lpds.pgportal.wfeditor.client.WorkflowEditor\"></application-desc>");
        jnlp.append("\n </jnlp>");
        response.getWriter().print(jnlp.toString());
    }


}

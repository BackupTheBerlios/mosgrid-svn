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
 * Workflow operations
 */


package hu.sztaki.lpds.pgportal.portlets.workflow;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.util.Hashtable;
import java.util.Vector;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * @author Krisztian Karoczkai
 */

public class RealWorkflowUtil {
/**
 * It replaces graph that is relates to an existing workflow
 * @param request message parameters (pgraf=new graph, workflow is the current wf)
 * @param response response message
 * @throws javax.portlet.PortletException Processing error
 */
    public static void changeGraph(ActionRequest request, ActionResponse response) throws PortletException{
        String newgrafname=""+request.getParameter("pgraf");
        String user=""+request.getRemoteUser();

        String workflow=""+request.getParameter("workflow");
        String wfsID=PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).getWfsID();
        Hashtable hsh=new Hashtable();
        hsh.put("url", wfsID);
        ServiceType st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());
        try{
            PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(wfsID);
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp=new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(user);
            tmp.setWorkflowID(workflow);

            tmp.setGraf(newgrafname);
            pc.setNewGraf(tmp);
            PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).setGraf(newgrafname);

        }catch(Exception e){e.printStackTrace();}
    }
}

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
package hu.sztaki.lpds.pgportal.portlets.informations;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * GliteInformotainsPortlet Portlet Class
 */
public class DCIBridgeInformotainsPortlet extends GenericWSPgradePortlet {

    
    @Override
    public void doView(RenderRequest request,RenderResponse response) throws PortletException,IOException {
        response.setContentType("text/html");
        ServiceType st=InformationBase.getI().getService("submitter", "wfi",new Hashtable(), new Vector());
        response.getWriter().write("<iframe src=\""+st.getServiceUrl()+"\" width=\"1024px\" height=\"800px\">");

    }
}
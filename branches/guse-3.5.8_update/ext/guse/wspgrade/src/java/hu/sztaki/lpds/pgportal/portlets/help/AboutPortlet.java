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
 * AboutPortlet.java
 *  About informaciok
 */

package hu.sztaki.lpds.pgportal.portlets.help;
import java.io.IOException;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author krisztian karoczkai
 */
public class AboutPortlet extends GenericPortlet{
    
    /** Creates a new instance of HelpPortlet */
    public AboutPortlet() {
    }

    @Override
    protected void doView(RenderRequest renderRequest, RenderResponse renderResponse) throws PortletException, IOException 
    {
        PortletRequestDispatcher pd=getPortletContext().getRequestDispatcher("/jsp/help/about.jsp");
        pd.include(renderRequest,renderResponse);
        
    }
    

}

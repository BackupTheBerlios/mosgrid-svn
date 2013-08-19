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
/**
 * Default plugin implementation
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.ActionHandler;
import java.util.Hashtable;
import javax.portlet.PortletSession;

/**
 * @author krisztian karoczkai
 */
public class BASEActions implements ActionHandler
{
    protected  PortletSession ps;

    public String getDispacher(Hashtable pParams) {return null;}

    public String getOutput(Hashtable pParams) {return null;}

    public Hashtable getParameters(Hashtable pParams) {return new Hashtable();}

    public void setSessionVariables(PortletSession pValue) {ps=pValue;}

    public void setSessionVariable(String pKey, Object pValue)
    {ps.setAttribute(pKey, pValue,ps.APPLICATION_SCOPE);}

    public Object getSessionVariable(String pKey)
    {return ps.getAttribute(pKey,ps.APPLICATION_SCOPE);}

    public void removeSessionVariable(String pKey)
    {ps.removeAttribute(pKey,ps.APPLICATION_SCOPE);}

}

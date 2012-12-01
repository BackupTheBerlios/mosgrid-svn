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
 * ActionHandler.java
 * Definition part of Ajax based event handling's build up
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf;

import java.util.Hashtable;
import javax.portlet.PortletSession;

/**
 * Definition part of Ajax based event handling's build up
 *
 * @author krisztian karoczkai
 */
public interface ActionHandler {
    
    /**
     * query of jsp page performing the view
     * @return jsp page handle
     */
    public String getDispacher(Hashtable pParams);
    
    /**
     * query of output to be send
     * @return output contetnt
     */
    public String getOutput(Hashtable pParams);
    
    /**
     * query of parameters to be forwarded to the jsp page performing the view
     * @return parameter hash
     */
    public Hashtable getParameters(Hashtable pParams);

    /**
     * Setting the session variables
     * @param pValue portlet session reference
     */
    public void setSessionVariables(PortletSession pValue);
/**
 * Setting a session variable
 * @param pKey key of variable
 * @param pValue value of variable
 */
    public void setSessionVariable(String pKey, Object pValue);
/**
 * Getting a session variable
 * @param pKey key of vaiable
 * @return value of variable
 */
    public Object getSessionVariable(String pKey);
/**
 * Remove a session variable
 * @param pKey key of variable
 */
    public void removeSessionVariable(String pKey);

}

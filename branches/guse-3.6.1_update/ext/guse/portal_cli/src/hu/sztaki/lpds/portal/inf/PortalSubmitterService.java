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
 * Definitions of the portal services offered to the submitter
 */

package hu.sztaki.lpds.portal.inf;

import java.util.Vector;

/**
 * @author krisztian
 */
public interface PortalSubmitterService 
{
    /**
     * Getting the proxy file from the portal
     * @param pUser - user name
     * @param pGrid - grid type, name
     * @return Proxy
     * @exception Exception any errors
     */
    public String getProxy(String pUser, String pGrid) throws Exception;
    /**
     * Getting all the active proxy files from the portal
     * @param pUser - user name
     * @return proxy list
     */
    public Vector getActiveProxys(String pUser);

}

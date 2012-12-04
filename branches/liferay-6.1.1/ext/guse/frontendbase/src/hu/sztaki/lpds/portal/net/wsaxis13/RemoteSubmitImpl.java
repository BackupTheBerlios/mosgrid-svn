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
 * RemoteSubmitImpl.java
 */

package hu.sztaki.lpds.portal.net.wsaxis13;

import hu.sztaki.lpds.pgportal.com.WorkflowSubmitThread;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import java.util.Enumeration;

/**
 * Handles the request from the 
 * remote on the portal side.
 *
 * @author krisztian
 */
public class RemoteSubmitImpl 
{
/**
 * Submit coming not from the wspgrade interface
 * @param pUser wspgrade user id
 * @param pID workflow name
 * @param pText submit text
 * @return true the submit was successful
 */
    public Boolean submit(String pUser, String pID, String pText)
    {
        if("".equals(pID))return new Boolean(false);
        try
        {
            Enumeration enm=PortalCacheService.getInstance().getUser(pUser).getWorkflows().keys();
            String key="";
            while(enm.hasMoreElements())
            {
                key=""+enm.nextElement();
                if(pID.equals(PortalCacheService.getInstance().getUser(pUser).getWorkflow(key).getRemoting()))
                {
                    new WorkflowSubmitThread(PortalCacheService.getInstance().getUser(pUser).getWorkflow(key),pUser,"E " + pText); 
                    return new Boolean(true);
                }
            }
            return new Boolean(false);
        }
        catch(Exception e){return new Boolean(false);}
    }
    
}

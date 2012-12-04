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
 * GetHelpText.java
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import java.util.Hashtable;

/**
 * Retrives the content of a help page selected by a key 
 *
 * @author krisztian karoczkai
 */
public class GetHelpText  extends BASEActions
{
    
    public String getDispacher(Hashtable pParams){return null;}
    
    public Hashtable getParameters(Hashtable pParams){return null;}
    
    /**
     * Retrives the content of a help page selected by a key
     */
    public String getOutput(Hashtable pParams) {
        return PortalMessageService.getI().getMessageNoCache((String)pParams.get("pkey"));
    }
    
}

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
 * GetMessage.java
 * query of of a text value
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import java.util.Hashtable;

/**
 * query of of a text value upon a key
 *
 * @author krisztian karoczkai
 */
public class GetMessage extends BASEActions
{
    
    public String getDispacher(Hashtable pParams){return null;}
    
    public Hashtable getParameters(Hashtable pParams){
        return null;
    }
    
    /**
     * query of of a text value upon a key
     */
    public String getOutput(Hashtable pParams) {
        return ""+pParams.get("d")+"::"+PortalMessageService.getI().getMessage(""+pParams.get("j"));
    }
    
}

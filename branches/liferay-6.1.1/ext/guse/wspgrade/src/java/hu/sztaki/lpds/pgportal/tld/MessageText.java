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
 * MessageText.java
 * Szoveges Portal Message Service uzenet megjelenitese
 */

package hu.sztaki.lpds.pgportal.tld;

import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import java.io.IOException;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Szoveges Portal Message Service uzenet megjelenitese
 *
 * @author krisztian karoczkai
 */
public class MessageText extends TagSupport {
    
    /**
     * Class constructor
     */
    public MessageText() {}
    
    /**
     * Uzenet kulcsanak beallitasa
     * @param pValue kulcs
     */
    public void setKey(String pValue){setValue("key",pValue);}
    
    /**
     * Uzenet kulcsannak lekerdezese
     * @return kulcs
     */
    public String getText(){return (String)getValue("key");}
    
    /**
     * tag feldolgozas befejezese
     * @return folytatas/befejezes allapot
     */
    public int doEndTag() throws JspException {
        try{
            pageContext.getOut().print(PortalMessageService.getI().getMessage((String)getValue("key")).trim());
        } catch(IOException e) {
        }
        return EVAL_PAGE;
    }
    
}

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
 * ToolTipp.java
 * Tooltip-et megjelenito JSP tag
 */

package hu.sztaki.lpds.pgportal.tld;

import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import javax.servlet.jsp.*;
import java.io.IOException;
import javax.servlet.jsp.tagext.*;

/**
 * Tooltip-et megjelenito JSP tag
 *
 * @author krisztian karoczkai
 */
public class ToolTipp extends TagSupport {
    
    public void setId(String pValue){setValue("id",pValue);}
    public void setTkey(String pValue){setValue("tkey",pValue);}
    public void setImg(String pValue){setValue("img",pValue);}
    
    /**
     * tag feldolgozas befejezese
     * @return folytatas/befejezes allapot
     */
    public int doEndTag() throws JspException {
//        try{pageContext.getOut().println("<a href=\"#\"  onmouseover=\"javascript:viewToolTip('"+getValue("id")+"','"+PortalMessageService.getI().getMessage((String)getValue("tkey"))+"');\" onmouseout=\"javascript:viewToolTip('"+getValue("id")+"','');\" title=\"\" alt=\"\" ><img src=\""+getValue("img")+"\" /></a>");}
        String msg=PortalMessageService.getI().getMessage((String)getValue("tkey")).replaceAll("\n", "<br>").replaceAll("\"", "&quot;").replaceAll("'", "&quot;");
        try{pageContext.getOut().println("<a href=\"javascript:viewToolTip('"+getValue("id")+"','"+msg+"');\" title=\"\" alt=\"\" ><img src=\""+getValue("img")+"\" /></a>");} catch(IOException e){}
        return EVAL_PAGE;
    }
    
}

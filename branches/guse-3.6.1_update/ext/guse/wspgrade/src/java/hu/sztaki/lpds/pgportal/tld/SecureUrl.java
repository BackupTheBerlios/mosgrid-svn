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
 * SecureUrl.java
 *
 */

package hu.sztaki.lpds.pgportal.tld;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 * @author krisztian karoczkai
 */

public class SecureUrl extends TagSupport
{
    
    public void setDest(String pValue){setValue("dest",pValue);}
    public void setUrl(String pValue){setValue("url",pValue);}
    
    /**
     * tag feldolgozas befejezese
     * @return folytatas/befejezes allapot
     */
    public int doEndTag() throws JspException 
    {
        
        try
        {
            Hashtable h=new Hashtable();
            h.put("url",getValue("url"));
            ServiceType st=InformationBase.getI().getService((String)getValue("dest"),"portal",h,new Vector());
            // System.out.println("dest : " + (String)getValue("dest"));
            // System.out.println("info ServiceType st : " + st);
            pageContext.getOut().print(st.getSecureServiceUrl().trim());
        } 
        catch(Exception e) 
        {
            // e.printStackTrace();
            System.out.println("SECURE URL ERROR");
            System.out.println("info getValue url  : " + (String) getValue("url"));
            System.out.println("info getValue dest : " + (String) getValue("dest"));            
        }
        return EVAL_PAGE;
    }    
}

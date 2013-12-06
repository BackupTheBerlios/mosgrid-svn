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
 * ActionSubmit.java
 * Ajax akcio gomb parameterekkel
 */

package hu.sztaki.lpds.pgportal.tld;

import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

/**
 * Ajax akcio gomb parameterekkel
 *
 * @author krisztian karoczkai
 */
public class ActionSubmit extends TagSupport {
    
    private String actionID=null;
    private String actionValue;
    private String paramID=null;
    private String paramValue;
    private String param0ID=null;
    private String param0Value;
    private String cssClass;
    private String txt;
    private String params=null;
    private String tkey=null;
    
    /**
     * Class constructor
     */
    public ActionSubmit() {}
    
    /**
     * Akcio http parameter nevenek lekerdezese
     * @return http parameter neve
     */
    public String getActionID(){return actionID;}
    
    /**
     * Akcio http parameter ertekenek lekerdezese
     * @return http parameter erteke
     */
    public String getActionValue(){return actionValue;}
    
    /**
     * Plusz parameter http parameter nevenek lekerdezese
     * @return http parameter neve
     */
    public String getParamID(){return paramID;}
    
    /**
     * Plusz http parameter ertekenek lekerdezese
     * @return http parameter erteke
     */
    public String getParamValue(){return paramValue;}
    
    /**
     * Plusz parameter http parameter nevenek lekerdezese
     * @return http parameter neve
     */
    public String getParam0ID(){return param0ID;}
    
    /**
     * Plusz http parameter ertekenek lekerdezese
     * @return http parameter erteke
     */
    public String getParam0Value(){return param0Value;}
    
    /**
     * Css class lekerdezese
     * @return css class
     */
    public String getCssClass(){return cssClass;}
    
    /**
     * Gomb szovegenek lekerdezese
     * @return gomb felirat
     */
    public String getTxt(){return txt;}
    
    /**
     * Akcio http parameter nevenek beallitasa
     * @param value parameter neve
     */
    public void setActionID(String value){actionID=value;}
    
    
    /**
     * Akcio http parameter ertekenek beallitasa
     * @param value parameter neve
     */
    public void setActionValue(String value){actionValue=value;}
    
    /**
     * Plusz parameter http parameter nevenek beallitasa
     * @param value parameter neve
     */
    public void setParam0ID(String value){param0ID=value;}
    
    /**
     * Plusz parameter http parameter ertekenek beallitasa
     * @param value parameter erteke
     */
    public void setParam0Value(String value){param0Value=value;}
    
    /**
     * Plusz parameter http parameter nevenek beallitasa
     * @param value parameter neve
     */
    public void setParamID(String value){paramID=value;}
    
    /**
     * Plusz parameter http parameter ertekenek beallitasa
     * @param value parameter erteke
     */
    public void setParamValue(String value){paramValue=value;}
    
    /**
     * Css class nevenek beallitasa
     * @param value css class
     */
    public void setCssClass(String value){cssClass=value;}
    
    /**
     * Gomb felirat szovegenek beallitasa
     * @param value css class
     */
    public void setTxt(String value){txt=value;}
    
    /**
     * Gomb felirat szovegenek beallitasa
     * @param value css class
     */
    public void setTkey(String value){tkey=value;}
    
    private String jsDOMId=null;
    
    @Override
    public void setId(String pValue){jsDOMId=pValue;}

    /**
     * Tag feldolgozas befejezese
     * @return befejezes/folytatas modja
     */
    @Override
    public int doEndTag() throws JspException {
        try {
            if(jsDOMId!=null) pageContext.getOut().println("<input id=\""+jsDOMId+"\" class=\""+cssClass+"\" type=\"submit\" onclick=\"javascript:");
            else pageContext.getOut().println("<input class=\""+cssClass+"\" type=\"submit\" onclick=\"javascript:");
            if(param0ID!=null)pageContext.getOut().println("document.getElementById('"+param0ID+"').value='"+param0Value+"';");
            if(paramID!=null)pageContext.getOut().println("document.getElementById('"+paramID+"').value='"+paramValue+"';");
            if(actionID!=null)pageContext.getOut().println("document.getElementById('"+actionID+"').value='"+actionValue+"';\"");
            if(!tkey.equals("true"))
                pageContext.getOut().println("value=\""+txt+"\" />");
            else
                pageContext.getOut().println("value=\""+PortalMessageService.getI().getMessage(txt)+"\" />");
        } catch(Exception e) {e.printStackTrace();}
        return EVAL_PAGE;
    }

}

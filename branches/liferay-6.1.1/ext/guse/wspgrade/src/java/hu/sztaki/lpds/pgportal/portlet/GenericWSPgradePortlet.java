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
 * Alapmuveletek kezeleset megvalosito generikus portlet ws-pgrade kornyezetben
 */

package hu.sztaki.lpds.pgportal.portlet;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.apache.commons.fileupload.portlet.PortletFileUpload;

/**
 * @author krisztian karoczkai
 */
public class GenericWSPgradePortlet extends GenericPortlet
{

    @Override
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException{
        if (PortletFileUpload.isMultipartContent(request))
            doUpload(request, response);
        else{
            String action = request.getParameter("guse");

            if((action!=null)&&(!("").equals(action))){
                try{
                    Method method = this.getClass().getMethod(action, new Class[]{ActionRequest.class, ActionResponse.class});
                    method.invoke(this, new Object[]{request, response});
                }
                catch(Exception e){
                    setRequestAttribute(request.getPortletSession(),"msg", "error.input:"+action+" method");
                    e.printStackTrace();
                }
            }
            else setRequestAttribute(request.getPortletSession(),"msg", "error.input:"+action+" method");
        }
    }

    @Override
    protected void doHelp(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        String helpkey="help."+response.getNamespace();
        if(request.getAttribute("helpmessage")!=null)
            helpkey=(String)request.getAttribute("helpmessage");
        else if(request.getParameter("helptext")!=null)
            helpkey=request.getParameter("helptext");
        response.getWriter().write(PortalMessageService.getI().getMessageNoCache(helpkey));
        request.removeAttribute("helpmessage");
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) throws PortletException, IOException {
        if(request.getParameter("helptext")!=null)
            response.getWriter().write(PortalMessageService.getI().getMessageNoCache(request.getParameter("helptext")));
    }




    protected void doUpload(ActionRequest request, ActionResponse response)
    {}

/**
 * Render kornyezetben a kesobbiekben attributumkent megjeleno objektum felvitele
 * @param pSess Aktualis kereshez tartozo PortletSession
 * @param pKey attributum azonositoja
 * @param pValue ertek
 */
    protected  void setRequestAttribute(PortletSession pSess, String pKey, Object pValue){
        if(pSess.getAttribute("requesattribute")==null)
            pSess.setAttribute("requesattribute",new Hashtable<String,Object>());
        ((Hashtable<String,Object>)pSess.getAttribute("requesattribute")).put(pKey, pValue);
    }
/**
 * Render kornyezetben a kesobbiekben attributumkent megjeleno objektum tarolasara hasznalt
 * session valtozo torlese
 * @param pSess
 */
    protected void cleanRequestAttribute(PortletSession pSess){
        pSess.removeAttribute("requesattribute");
    }
/**
 * Render kornyezetben attributumkent megjeleno objektum kifejtese a temporary session teruletrol
 * @param pReq Aktualis keres
 */
    protected void openRequestAttribute(RenderRequest pReq){
        Hashtable<String,Object> tmp=(Hashtable<String,Object>)pReq.getPortletSession().getAttribute("requesattribute");
        if(tmp!=null){
            Enumeration<String> enm=tmp.keys();
            String key;
            while(enm.hasMoreElements()){
                key=enm.nextElement();
                 pReq.setAttribute(key, tmp.get(key));
            }
        }
    }

    protected boolean isInited(){
        return PropertyLoader.getInstance().isProperty("service.url");
    }

}

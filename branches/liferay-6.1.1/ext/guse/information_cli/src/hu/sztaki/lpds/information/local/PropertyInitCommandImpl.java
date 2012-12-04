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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.information.local;

import hu.sztaki.lpds.information.inf.InitCommand;
import hu.sztaki.lpds.information.net.wsaxis13.InformationClientImpl;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author krisztian karoczkai
 */
public class PropertyInitCommandImpl implements InitCommand
{

    public void run(ServletContext cx,HttpServletRequest request)
    {
        System.out.println("---------propertyinit--------");
        Enumeration<String> enm=request.getParameterNames();
        String key;
        while(enm.hasMoreElements())
        {
            key=enm.nextElement();
            System.out.println("http-param:"+key+"-"+request.getParameter(key));
        }
        PropertyLoader.getInstance().setProperty("is.url", request.getParameter("is.url"));
        PropertyLoader.getInstance().setProperty("is.id", request.getParameter("is.id"));
        PropertyLoader.getInstance().setProperty("service.url", request.getParameter("service.url"));
        InformationClientImpl ic=new InformationClientImpl();
        ic.setServiceURL(PropertyLoader.getInstance().getProperty("is.url"));
        ic.setServiceID(PropertyLoader.getInstance().getProperty("is.id"));
        try
        {
            HashMap h=ic.getAllProperties(PropertyLoader.getInstance().getProperty("service.url"));
            Iterator i=h.keySet().iterator();
            Object k;
            while(i.hasNext())
            {
                k=i.next();
                PropertyLoader.getInstance().setProperty((String)k,(String)h.get(k));
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

}

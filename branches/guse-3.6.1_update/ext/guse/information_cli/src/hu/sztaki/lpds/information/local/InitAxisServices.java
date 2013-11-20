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
 * InitAxisServices.java
 * Asis service-ek inicializalasa
 */

package hu.sztaki.lpds.information.local;

import hu.sztaki.lpds.information.inf.InitCommand;
import hu.sztaki.lpds.information.inf.PropertyTrigger;
import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @author krisztian karoczkai
 */
public class InitAxisServices extends HttpServlet {
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws IOException IO error
     * @throws ServletException Servlet error
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        Enumeration enm=getInitParameterNames();
        String tmp="";
        boolean b=true;
        byte cnt=-1;
        while(b)
        {
            cnt++;
            b=false;
            tmp="install-"+cnt;
            if(getInitParameter(tmp)!=null)
            {
                b=true;
                String[] s=new String[2];
                s[0]="-l"+getInitParameter("baseurl");
                s[1]=System.getProperty("catalina.home")+"/"+getInitParameter(tmp);
                System.out.println(s);
                org.apache.axis.client.AdminClient.main(s);
            }
            tmp="run-"+cnt;
            if(getInitParameter(tmp)!=null)
            {
                b=true;
                try
                {
                    InitCommand c=(InitCommand)Class.forName(getInitParameter(tmp)).newInstance();
                    c.run(getServletContext(),request);
                }
                catch(Exception e){e.printStackTrace();}
            }
            tmp="property-"+cnt;
            if(getInitParameter(tmp)!=null)
            {
                b=true;
                try
                {
                    PropertyTrigger c=(PropertyTrigger)Class.forName(getInitParameter(tmp)).newInstance();
                    c.trigger();
                }
                catch(Exception e){e.printStackTrace();}
            }
        }

        System.gc();
    
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().close();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws IOException IO error
     * @throws ServletException Servlet error
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws IOException IO error
     * @throws ServletException Servlet error
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    

    // </editor-fold>
}

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
package hu.sztaki.lpds.repository.service.veronica.server.init;

import hu.sztaki.lpds.repository.service.veronica.server.quota.QuotaService;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Init Servlet.
 *
 * Ez a szervlet minden repository inditaskor,
 * init.jsp hivaskor lefut.
 *
 * @author lpds
 */
public class InitServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1208360042855926218L;
    
    public InitServlet() {
        // System.out.println("RepositoryInitServlet call...");
        this.doInitialize();
    }
    
    /**
     * Init, indulo adatok betoltese.
     */
    private void doInitialize() {
        // initialize quota service
        QuotaService.getInstance();
    }
    
    /**
     * Processes requests for <code>POST</code> methods.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     */
    protected void postProcessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // System.out.println("RepositoryInitServlet postProcessRequest begin...");
        // System.out.println("RepositoryInitServlet postProcessRequest end...");
    }
    
    /**
     * Processes requests for <code>GET</code> methods.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     */
    protected void getProcessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // System.out.println("RepositoryInitServlet getProcessRequest begin...");
        // System.out.println("RepositoryInitServlet getProcessRequest end...");
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getProcessRequest(request, response);
    }
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        postProcessRequest(request, response);
    }
    
    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "RepositoryInitServlet_Short_description";
    }
    
}

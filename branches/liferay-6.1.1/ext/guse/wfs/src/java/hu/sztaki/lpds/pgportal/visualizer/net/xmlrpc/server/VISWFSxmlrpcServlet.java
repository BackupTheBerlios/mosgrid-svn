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
package hu.sztaki.lpds.pgportal.visualizer.net.xmlrpc.server;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xmlrpc.XmlRpcServer;

/**
 * A visualizer-töl erkezö kereseket kiszolgalo servlet.
 *
 * @author lpds
 */
public class VISWFSxmlrpcServlet extends HttpServlet {
    
    private static final long serialVersionUID = 152015899582078852L;
    
    private XmlRpcServer xmlRpcServer;
    
    public VISWFSxmlrpcServlet() {
        xmlRpcServer = new XmlRpcServer();
        try {
            xmlRpcServer.addHandler("VISWFSServiceImplHandler", new VISWFSServiceImplHandler());
            // System.out.println("call VISWFSxmlrpcServlet() addHandler VISWFSServiceImplHandler...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        byte[] result = xmlRpcServer.execute(request.getInputStream());
        response.setContentType("text/xml");
        response.setContentLength(result.length);
        OutputStream out = response.getOutputStream();
        out.write(result);
        out.flush();
        // System.out.println("call processRequest()...");
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "VISWFSxmlrpcServlet_description";
    }
    // </editor-fold>
}

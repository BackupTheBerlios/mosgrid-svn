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

package hu.sztaki.lpds.storage.net.bes;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

/**
 *
 * @author krisztian
 */
public class FileUploadServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        ServletRequestContext servletRequestContext = new ServletRequestContext(request);
        boolean isMultipart = ServletFileUpload.isMultipartContent(servletRequestContext);
        if (isMultipart) {
            File newFile;
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
            servletFileUpload.setSizeMax(Long.MAX_VALUE);
            try{
                List<FileItem> listFileItems = servletFileUpload.parseRequest(request);
                String path=PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"storage/"+request.getParameter("path")+"/";
                String link=request.getParameter("link");
                File f=new File(path);
                f.mkdirs();

                String[] pathData=request.getParameter("path").split("/");
                for(FileItem t:listFileItems){
                    if(!t.isFormField()){
                        newFile=new File(path+"/"+t.getFieldName());
                        t.write(newFile);
                        QuotaService.getInstance().addPlussRtIDQuotaSize(pathData[0], pathData[1], pathData[2], pathData[5],newFile.length() );
//                        QuotaService.getInstance().get(pathData[0], pathData[1]).g
//                        System.out.println("STORAGE:"+newFile.getAbsolutePath());
                        if(link!=null)
                            if(!t.getFieldName().equals(link))
                                FileUtils.getInstance().createLink(path, t.getFieldName(), path+link+getGeneratorPostFix(t.getFieldName()));
                    }
                }
            }
            catch(Exception e){e.printStackTrace();}
        }
        
    } 

    private String getGeneratorPostFix(String pFileName){
        String res="";
        String[] t=pFileName.split("_");
        if(t.length>1) res="_"+t[t.length-1];
        return res;
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

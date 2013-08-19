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
package hu.sztaki.lpds.submitter.service.jobstatus;

import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.submitter.grids.glite.status.GStatusHandler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author csig
 */
public class JobStatusServlet extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //System.out.println("JobStatusServlet.processRequest called !!!!!!!!!!!!!!!");
        String uid = "";
        String jobid = "";
        int status = -1;
        //sysLog(jobid, "JobStatusServlet * * * JobStatusServlet called");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
            fileItemFactory.setSizeThreshold(30 * 1024 * 1024); //1 MB
            fileItemFactory.setRepository(new File(Base.getI().getPath()));//new File()

            ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);

            try {             
                List items = uploadHandler.parseRequest(request);
                Iterator itr = items.iterator();
                while (itr.hasNext()) {
                    FileItem item = (FileItem) itr.next();
                    if (item.isFormField()) {
//                        out.println("Field Name = " + item.getFieldName() + ", Value = " + item.getString());
//                        System.out.println("JobStatusServlet: Field Name = " + item.getFieldName() + ", Value = " + item.getString());
                        if ("uid".equals("" + item.getFieldName())) {
                            uid = item.getString();
                        } else if ("jobid".equals("" + item.getFieldName())) {
                            jobid = item.getString();
                        } else if ("status".equals("" + item.getFieldName())) {
                            status = Integer.parseInt(item.getString());
                        } 
                    } else {
//                        System.out.println("JobStatusServlet: Field Name = " + item.getFieldName()
//                                + ", File Name = " + item.getName()
//                                + ", Content type = " + item.getContentType()
//                                + ", File Size = " + item.getSize());
//                        out.println("Field Name = " + item.getFieldName()
//                                + ", File Name = " + item.getName()
//                                + ", Content type = " + item.getContentType()
//                                + ", File Size = " + item.getSize());
                        // outfiles.add(item.getName());
                        if (status == 1 && GStatusHandler.getI().setStatus(uid, jobid, status)) {//if status == uploading(1) and userid->jobid
                            File destinationDir = new File(Base.getI().getPath() + jobid + "/outputs");
                            File file = new File(destinationDir, item.getName());
                            item.write(file);
                        } else {
                            System.out.println("ILLEGAL ACCESS or Deleted job!? uid:" + uid + " jobid:" + jobid);
                        }
                    }

                }

            } catch (FileUploadException ex) {
                sysLog(jobid, "JobStatusServlet: Error encountered while parsing the request: " + ex + " --> try getparameters");
                //ex.printStackTrace();
                uid = request.getParameter("uid");
                jobid = request.getParameter("jobid");
                try {
                    if (request.getParameter("status") != null) {
                        status = Integer.parseInt(request.getParameter("status"));
                        if (status == 6) {
                            status = 66;
                        } else if (status == 7) {
                            status = 77;
                        }//running status = 55
                    }
                } catch (Exception ee) {
                    //ee.printStackTrace();
                }
            } catch (Exception ex) {
                sysLog(jobid, "JobStatusServlet:Error encountered while uploading file: " + ex);
                //ex.printStackTrace();
            }

            sysLog(jobid, " JobStatusServlet: status=" + status + " userid=" + uid );
            if (status != -1) {
                GStatusHandler.getI().setStatus(uid, jobid, status);
            }

            //RequestDispatcher comp = null;

            //comp = request.getRequestDispatcher("hiba.jsp");
            // comp.forward(request, response);
        } catch (Exception ee){ee.printStackTrace();
        } finally {
            out.close();
        }

//GStatusHandler.getI().getJob(jobID)

    }

    /** Write log into sys.log & stdout.
     */
    private void sysLog(String jobID, String txt) {
        try {
            if (Conf.getP().getDebug() > 0) {
//                System.out.println(" JSS " + txt);
                if (!"".equals(jobID)) {
                    File logfile = new File(Base.getI().getPath() + jobID + "/outputs/plugin.log");
                    if (logfile.exists()) {
                        FileWriter tmp = new FileWriter(logfile, true);
                        BufferedWriter out = new BufferedWriter(tmp);
                        out.newLine();
                        out.write(" JSS " + txt);
                        out.flush();
                        out.close();
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
            }
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

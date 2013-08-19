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
package hu.sztaki.lpds.storage.service.carmen.server.receiver;

import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.storage.service.carmen.server.receiver.plugins.ReceiverService;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import hu.sztaki.lpds.information.local.PropertyLoader;

/**
 * File Receiver Servlet. Sends and receives files from the submitter,
 * also receives from the portal when uploading.
 *
 * @author lpds
 */
public class FileReceiverServlet extends HttpServlet {
    
    private static final long serialVersionUID = -2017669244739618900L;
    
    // file receiver repository root dir, ide tarolja le a megkapott fileokat
    private String repositoryDir;
    
    // max request (receive file) size
    private long maxRequestSize = 500 * 1024 * 1024;

    private int maxEventCnt;

    private int actEventCnt;

    public FileReceiverServlet() {
        LoadProperty();
    }
    
    /**
     * Init, loading initialization data.
     */
    private void LoadProperty() {
        maxRequestSize = 500 * 1024 * 1024;
        repositoryDir = FileUtils.getInstance().getRepositoryDir();
        maxEventCnt = 10;
        actEventCnt = 0;
        try {
            maxEventCnt = new Integer(PropertyLoader.getInstance().getProperty("guse.storage.event.max")).intValue();
        } catch (Exception e) {maxEventCnt = 10;}
        if (maxEventCnt < 5) {maxEventCnt = 10;}
    }
    
    /**
     * Receiving files
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws ServletException Servlet processing error
     * @throws IOException data access error
     */
    protected void postProcessRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //
        FileUtils.getInstance().createRepositoryDirectory();
        String tempDirName = repositoryDir + FileUtils.getInstance().getUniqueTempDirName();
        try {
            //
            FileUtils.getInstance().createDirectory(tempDirName);
            // use cos.jar (com.oreilly.servlet.*) begin...
            MultipartRequest multipartRequest = new MultipartRequest(request, tempDirName, (int) maxRequestSize, new MyFileRenamePolicy());
            // use cos.jar (com.oreilly.servlet.*) end...
            //
            String wfiType = multipartRequest.getParameter("wfiType");
            // System.out.println("wfiType : " + wfiType);
            //
            if ((wfiType == null) || ("".equals(wfiType))) {
                // throw new Exception("Not valid wfiType");
                // default wfi
                wfiType = "zen";
            }
            ReceiverService service = (ReceiverService) Class.forName("hu.sztaki.lpds.storage.service.carmen.server.receiver.plugins." + wfiType.toLowerCase() + "_ReceiverServiceImpl").newInstance();
            //
            doMaxEventCntWaitingBegin();
            service.receive(multipartRequest, response);
            doMaxEventCntWaitingEnd();
            //
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERRORRR:"+e.getMessage());
            response.setStatus(500);
            response.flushBuffer();
        }
        // delete temp dir and file...
        try {
            FileUtils.getInstance().deleteDirectory(tempDirName);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        // System.out.println("FileReceiverServlet postProcessRequest end...");
    }

    private synchronized void doMaxEventCntWaitingBegin() {
        while (actEventCnt >= maxEventCnt) {
            try {
                // System.out.println("storage FileReceiverServlet doEventWaitingBegin while (actEventCnt >= maxEventCnt) sleep, actEventCnt : " + actEventCnt);
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        actEventCnt++;
        // System.out.println("storage FileReceiverServlet doEventWaitingBegin actEventCnt : " + actEventCnt);
    }

    private void doMaxEventCntWaitingEnd() {
        actEventCnt--;
        // System.out.println("storage FileReceiverServlet doEventWaitingEnd actEventCnt : " + actEventCnt);
    }

/**
 * @see HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) g
 */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
    
/**
 * @see HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) g
 */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        postProcessRequest(request, response);
    }
    
/**
 * @see HttpServlet#getServletInfo()
 */
    @Override
    public String getServletInfo() {
        return "FileReceiverServlet_Short_description";
    }
    
}

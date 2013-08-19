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
 * This file is part of P-GRADE Grid Portal.
 *
 * P-GRADE Grid Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * P-GRADE Grid Portal is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * P-GRADE Grid Portal.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2006-2008 MTA SZTAKI
 *
 */
// LFCFileStoragePortlet.java v1.04
// Birsen Omay

package hu.sztaki.lpds.pgportal.portlets.file;


import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.services.credential.SZGCredential;
import hu.sztaki.lpds.pgportal.services.credential.SZGCredentialManager;
import hu.sztaki.lpds.pgportal.services.credential.SZGStoreKey;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import hu.sztaki.lpds.pgportal.services.pgrade.*;
//import hu.sztaki.lpds.pgportal.services.credential.*;
import hu.sztaki.lpds.pgportal.services.is.lcg2.LCGGrid;
import hu.sztaki.lpds.pgportal.services.is.lcg2.LCGInformationSystem;
import hu.sztaki.lpds.pgportal.services.is.lcg2.storage.UserData;
import hu.sztaki.lpds.pgportal.services.is.lcg2.storage.UserDataStorage;
import hu.sztaki.lpds.pgportal.services.is.lcg2.vo.LCGVO;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import hu.sztaki.lpds.pgportal.services.file.RemoteFileManagerSingleton;
import javax.portlet.*;
//import org.gridsphere.portlet.PortletLog;
/*import hu.sztaki.lpds.pgportal.portlets.credential.SZGCredentialBean;
//import hu.sztaki.lpds.pgportal.services.pgrade.*;
import hu.sztaki.lpds.pgportal.services.credential.*;
//import hu.sztaki.lpds.pgportal.services.utils.*;
import hu.sztaki.lpds.pgportal.services.is.lcg2.LCGGrid;
import hu.sztaki.lpds.pgportal.services.is.lcg2.LCGInformationSystem;
import hu.sztaki.lpds.pgportal.services.is.lcg2.storage.UserData;
import hu.sztaki.lpds.pgportal.services.is.lcg2.storage.UserDataStorage;
import hu.sztaki.lpds.pgportal.services.is.lcg2.vo.LCGVO;
 *
 */
import javax.portlet.ActionResponse;
import org.apache.commons.io.FilenameUtils;
public class LFCFileStoragePortlet extends GenericWSPgradePortlet
{
     private PortletContext pContext;
     private String scripts_path;
	/**
         * Portlet inicializalasa
        */
    @Override
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        pContext = config.getPortletContext();
        scripts_path=pContext.getRealPath("/WEB-INF/scripts/")+"/";

    }
		

    @Override
     public void processAction(ActionRequest request, ActionResponse response) throws PortletException
    {
        try {
            String userId = request.getRemoteUser();
            
            /*      if(request.getAttribute(SportletProperties.ACTION_EVENT)!=null)
            action=(""+request.getAttribute(SportletProperties.ACTION_EVENT)).split("=")[1];
             */
            String action = null;
            if ((request.getParameter("action") != null) && (!request.getParameter("action").equals(""))) {
                action = request.getParameter("action");
            }

            if (PortletFileUpload.isMultipartContent(request)) {

            DiskFileItemFactory factory = new DiskFileItemFactory();
            PortletFileUpload pfu = new PortletFileUpload(factory);
            pfu.setSizeMax(10485760); // Maximum upload size
            //pfu.setProgressListener(new FileUploadProgressListener());
         /*   PortletSession ps = request.getPortletSession();
            ps.setAttribute("upload", pfu, ps.APPLICATION_SCOPE);
            String actionName = null;*/
            String fieldName;
            List fileItems = pfu.parseRequest(request);
            Iterator iter = fileItems.iterator();
            HashMap params = new HashMap();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                // retrieve hidden parameters if item is a form field
                if (item.isFormField()) {
                    //getting parameters
                    fieldName = item.getFieldName();
                        //      System.out.println("Name is : " + fieldName + "   value is : " + item.getString());
                    if (item.getFieldName().equals(new String("SEListBoxBean_select"))){
                    params.put("se",item.getString());
                 }
                 if (item.getFieldName().equals(new String("uploadName"))){
                    params.put("upName",item.getString());
                 }
                 if (item.getFieldName().equals(new String("ownRead"))){
                    params.put("urBean",item.getString());
                 }
                 if (item.getFieldName().equals(new String("ownWrite"))){
                    params.put("uwBean",item.getString());
                 }
                 if (item.getFieldName().equals(new String("ownExecute"))){
                    params.put("uxBean",item.getString());
                 }
                 if (item.getFieldName().equals(new String("grRead"))){
                    params.put("grBean",item.getString());
                 }
                 if (item.getFieldName().equals(new String("grWrite"))){
                    params.put("gwBean",item.getString());
                 }
                 if (item.getFieldName().equals(new String("grExecute"))){
                    params.put("gxBean",item.getString());
                 }
                 if (item.getFieldName().equals(new String("othRead"))){
                    params.put("orBean",item.getString());
                 }
                 if (item.getFieldName().equals(new String("othWrite"))){
                    params.put("owBean",item.getString());
                 }
                 if (item.getFieldName().equals(new String("othExecute"))){
                    params.put("oxBean",item.getString());
                 }

                 



                }
                else { // item is not a form field, do file upload
                String s = item.getName();
                s = FilenameUtils.getName(s);
                String tempDir = System.getProperty("java.io.tmpdir")+"/uploads/"+request.getRemoteUser();
                String origfile = tempDir +"/" + s;
                params.put("origfile", origfile);
                
                File f=new File(tempDir);
                if(!f.exists()) f.mkdirs();
                File serverSideFile = new File(tempDir, s);
                item.write(serverSideFile);
                item.delete();

             }
            }
            if (action == null){
                doUpload(request, response, params);
            }
            }








            
            if (action != null) {
                try {
                    Method method = this.getClass().getMethod(action, new Class[]{ActionRequest.class, ActionResponse.class});
                    method.invoke(this, new Object[]{request, response});
                } catch (IllegalArgumentException ex) {
                    
                    Logger.getLogger(LFCFileStoragePortlet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    
                    Logger.getLogger(LFCFileStoragePortlet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException e) {
                    System.out.println("-----------------------Nincs ilyen met�dus:");
                } catch (IllegalAccessException e) {
                    System.out.println("----------------------Nincs jogod a met�dushoz");
                }
            }
            PortletMode pMode = request.getPortletMode();
            // upload -- enctype="multipart/form-data"

        } catch (Exception ex) {
            Logger.getLogger(LFCFileStoragePortlet.class.getName()).log(Level.SEVERE, null, ex);
        } 
     }






	@Override
        public void doView(RenderRequest request, RenderResponse response)throws PortletException, IOException{
		response.setContentType("text/html");
        if(!isInited()){
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);
            return;
        }
                VOInfoRequester inforeq = new VOInfoRequester();

                String userID = this.getUserName(request);
                //setSelectedVOForUser(userID, "");
		String selectedGridName = "";
       		setSelectedGridForUser(userID, selectedGridName);
		viewGridResources(request, getSelectedGridForUser(userID), getSelectedVOForUser(userID));
                
                /*
                for(int i=0;i<RemoteFileManagerSingleton.getInstance().getgridListBoxBean(userID).size();++i){
                    System.out.println(RemoteFileManagerSingleton.getInstance().getgridListBoxBean(userID).get(i));
                }
                 *
                 */
		request.setAttribute("gridListBoxBean", RemoteFileManagerSingleton.getInstance().getgridListBoxBean(userID));


                //loadUsrCert(userID);
		


                String nextJSP = (String)request.getParameter("nextJSP");
		//String userID = (String)request.getParameter("userID");
		String showFileBrowser = (String)request.getParameter("showFileBrowser");
		String type = (String)request.getParameter("type");
		
		String isDetails = (String)request.getParameter("isDetails");
		String guid = (String)request.getParameter("guid");
		String fileName = (String)request.getParameter("fileName");
                String selectedItemName = (String) request.getParameter("selectedItemName");
		String dirName = (String)request.getParameter("dirName");
		String dirMode = (String)request.getParameter("dirMode");
		String ownerACL = (String)request.getParameter("ownerACL");
		String groupACL = (String)request.getParameter("groupACL");
		String otherACL = (String)request.getParameter("otherACL");
		String entryNum = (String)request.getParameter("entryNum");
		String status = (String)request.getParameter("status");
		String fileMode = (String)request.getParameter("fileMode");
		String owner = (String)request.getParameter("owner");
		String group = (String)request.getParameter("group");
		String modDate = (String)request.getParameter("modDate");
		String fileSize = (String)request.getParameter("fileSize");
		String path = (String)request.getParameter("path");
                String selecteditem = (String)request.getParameter("selecteditem");
		String showErrorButton = (String)request.getParameter("showErrorButton");
		String complete = (String)request.getParameter("completed");
		if (type != null)request.setAttribute("type", type);
		if (complete != null)request.setAttribute("completed", "0");
		if (userID != null)request.setAttribute("userId", userID);
		if (showFileBrowser != null) {
                   
                    if (Integer.parseInt(showFileBrowser) == 1){
                   
                        String isselected =  request.getParameter("showlastselected");
                        if (isselected != null){
                   
                            request.setAttribute("fileLsList", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userID).getLastSelectedItem().getItems());
                        }
                        else
                    	request.setAttribute("fileLsList", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userID).getRootItem().getItems());
                    }
                    request.setAttribute("showFileBrowser", showFileBrowser);
                }
                if (selecteditem != null) request.setAttribute("selecteditem", selecteditem);
		if (isDetails != null)request.setAttribute("isDetails", isDetails);
		if (guid != null)request.setAttribute("guid", guid);
		if (fileName != null)request.setAttribute("fileName", fileName);
		if (dirName != null)request.setAttribute("dirNamer", dirName);
		if (dirMode != null)request.setAttribute("dirMode", dirMode);
		if (ownerACL != null)request.setAttribute("ownerACL", ownerACL);
		if (groupACL != null)request.setAttribute("groupACL", groupACL);
		if (otherACL != null)request.setAttribute("otherACL", otherACL);
		if (entryNum != null)request.setAttribute("entryNum", entryNum);
		if (status != null)request.setAttribute("status", status);
		if (fileMode != null)request.setAttribute("fileMode", fileMode);
		if (owner != null)request.setAttribute("owner", owner);
		if (group != null)request.setAttribute("group", group);
		if (modDate != null)request.setAttribute("modDate", modDate);
		if (fileSize != null)request.setAttribute("fileSize", fileSize);
		if (path != null)request.setAttribute("path", path);
		if (showErrorButton != null)request.setAttribute("showErrorButton", showErrorButton);
		
		
		String hostNameListBoxshow = (String)request.getParameter("hostNameListBoxshow");
		String SEListBoxshow = (String)request.getParameter("SEListBoxshow");
		String prefListshow = (String)request.getParameter("prefListshow");
		String replicashow =(String)request.getParameter("replicashow");
               
		String selectedVO = (String)request.getParameter("selectedVO");
		//request.setAttribute("gridListBoxBean", RemoteFileManagerSingleton.getInstance().getgridListBoxBean(userID));
                if (selectedVO != null){
               
                    List<String> list = inforeq.listVOs();
                    
                    list.remove(selectedVO);
                    
                    list.add(0,selectedVO);
                    

                    request.setAttribute("voListBoxBean", list);
                }
                else{
                    
                    request.setAttribute("voListBoxBean", inforeq.listVOs());
                }
                  viewGridResources(request, selectedGridName, selectedVO);
                  //request.setAttribute("hostNameListBoxshow", "true");
		// do not enable file browser before VO and lfchost selection


                    
                

		//if (hostNameListBoxshow != null){
		if (selectedVO != null){
			//String selectedVO = (String)request.getParameter("selectedVO");
                        
			String portalPath= (String)request.getParameter("portalPath");
			// OLD
			//request.setAttribute("hostNameListBoxBean", listLFCHosts(selectedVO, portalPath));
                        //END
                        //NEW
                        ArrayList<String> lfcs = new ArrayList<String>();
                        if (selectedVO != null && !selectedVO.equals(new String("")))
                            lfcs.add(inforeq.getInfos(selectedVO).getLfc());
                        request.setAttribute("hostNameListBoxBean",lfcs);
		  	//END NEW
		}
		if (SEListBoxshow != null){
					
			String certPath = (String)request.getParameter("certPath");
			String lfcHost= (String)request.getParameter("lfcHost");
			String gridName= (String)request.getParameter("gridName");
			request.setAttribute("SEListBoxBean", listSE(certPath, lfcHost, gridName));
		}
		if (prefListshow != null){
			//ArrayList prefList = readUserPref(userID);
			//request.setAttribute("prefList", prefList);
		}
		if (replicashow != null){
			
			String certPath = (String)request.getParameter("certPath");
			String argument = (String)request.getParameter("argument");
			//String selectedItemName = (String)request.getParameter("selectedItemName");
			String lfcHost= (String)request.getParameter("lfcHost");
			String gridName= (String)request.getParameter("gridName");
			ArrayList 	replicaList = listReplicas(certPath, lfcHost, gridName, argument, selectedItemName);
			
			request.setAttribute("replicaList",replicaList);
		}
              
                String message = (String)request.getParameter("message");
                request.setAttribute("message", message);


                //OLD
                //request.setAttribute("voListBoxBean", RemoteFileManagerSingleton.getInstance().getvoListBoxBean(userID));
                //END OLD
                //getting info from guse service:


		request.setAttribute("fileBean", RemoteFileManagerSingleton.getInstance().getFileBean(userID));
		

              
                

              Enumeration enumParam = request.getParameterNames();
              while(enumParam.hasMoreElements()) {
              String paramName = (String) enumParam.nextElement();
              String paramValue = (String) request.getParameter(paramName);
              
              }
              Enumeration enumAttr = request.getAttributeNames();
              while(enumAttr.hasMoreElements()) {
              String attrName = (String) enumAttr.nextElement();
              Object attrValue = request.getAttribute(attrName);
              
              }

              



        try
        {
         //   if (action.equals("")) jsp=mainjsp;
            // Sorting...
           /* if (jsp.equals(mainjsp)) {
                request.setAttribute("rWorkflowList", Sorter.getInstance().sortFromValues(PortalCacheService.getInstance().getUser(request.getRemoteUser()).getWorkflows()));
            }
            *
            */
            // Sorting...
            PortletRequestDispatcher dispatcher=null;
            //dispatcher = pContext.getRequestDispatcher("/WEB-INF/jsp/file/File.jsp");
            if (nextJSP == null){
                nextJSP = "/WEB-INF/jsp/file/File.jsp";
            }
            dispatcher = pContext.getRequestDispatcher(nextJSP);
            dispatcher.include(request, response);
        }
        catch (PortletException ex) {
            Logger.getLogger(LFCFileStoragePortlet.class.getName()).log(Level.SEVERE, null, ex);
        }        catch (IOException e){try {
                throw new PortletException("JSPPortlet.doView exception", e);
            } catch (PortletException ex) {
                Logger.getLogger(LFCFileStoragePortlet.class.getName()).log(Level.SEVERE, null, ex);
            }
}
        //action="";

                //setNextState(event.getRenderRequest(),nextJSP);
	}
public synchronized void doReplicate(ActionRequest request,ActionResponse response) {
								// create a new replica of a file


                String sElement = request.getParameter("SEListBoxBean_select");
              
		String userId = getUserName(request);
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId ;
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();

		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
                VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
		String path = "/grid/"+gridName;

		if( !userCertCheckMG(userId, gridName) ) {
			response.setRenderParameter("message", "No valid certificate for " + gridName);
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", getUserName(request));



			//setNextState(req, "file/File.jsp");
			return;
		}
		try{
			Process p;
			String argument = "";
			for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				argument = argument + " " + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
			}
			String fileName = request.getParameter("fileName");
			// run the script to replicate the file

                       
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "replicateFile.sh  " +scripts_path+" " +bdii +" "+ certPath + "  " + lfcHost + "  " + sElement + " " + gridName + "  " + argument + " " + fileName  );
			p.waitFor();

			InputStream is = p.getInputStream();
       		InputStreamReader isr = new InputStreamReader(is);
       		BufferedReader br = new BufferedReader(isr);
			ArrayList temp = new ArrayList();
			String s;
			for(int i=0;(s = br.readLine())!= null;i++) {
				temp.add(s);
			}
			if( temp.get((temp.size()-1)).equals("0") ) {
				response.setRenderParameter("message", "The file is replicated");
				response.setRenderParameter("showErrorButton", "0");
			}
			else { // error on replication
				response.setRenderParameter("message", "The file cannot be replicated");
				response.setRenderParameter("showErrorButton", "1");
			}

			ArrayList replicaList = new ArrayList();
			// update replica list
			//replicaList = listReplicas(certPath, lfcHost, gridName, argument, fileName);

			// find  storage elements

			//response.setRenderParameter("SEListBoxBean", seLBB);
			response.setRenderParameter("SEListBoxshow","true");

			response.setRenderParameter("certPath",certPath);
			response.setRenderParameter("lfcHost",lfcHost);
			response.setRenderParameter("gridName",gridName);
			response.setRenderParameter("path", path);
			response.setRenderParameter("fileName", fileName);
			//event.getListBoxBean("replicaList").clear();
			response.setRenderParameter("selectedItemName", fileName);
			response.setRenderParameter("replicashow", "true");
			response.setRenderParameter("argument", argument);
			//response.setRenderParameter("replicaList", replicaList);
			//viewGridResources(req, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileReplicas.jsp");



			//setNextState(req, "file/fileReplicas.jsp");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

	}
	
	
	private String getUserName(PortletRequest req){
                
		return req.getRemoteUser();
		
	}


        public synchronized void doChangeGrid(ActionRequest request, ActionResponse response) {

       	String userId = getUserName(request);
       	 String selected_grid = request.getParameter("gridListBoxBean_select");
           
       	setSelectedGridForUser(userId, selected_grid);
       	String selectedVO = getSelectedVOForUser(userId);
       	viewGridResources(request, selected_grid, selectedVO);
		/*ListBoxBean fileList = event.getListBoxBean("fileLsList");
		fileList.clear();
		event.getTextFieldBean("dirName").setValue("");
		event.getTextFieldBean("newName").setValue("");
                 * */

		// do not enable file browser before VO and lfchost selection
                response.setRenderParameter("gridListBoxBean", selected_grid);
		response.setRenderParameter("showFileBrowser", "0");
		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
		
        
        }

        public synchronized void doListHostName(ActionRequest request, ActionResponse response) {
	//lists lfchosts of the selected VO

   	    String userId = getUserName(request);
        String portalPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir");
		String selectedGridName = getSelectedGridForUser(userId);

		String selectedVO = request.getParameter("VOListBoxBean_select");
              

		//hostNameLBB = listLFCHosts(selectedVO, portalPath);
		//response.setRenderParameter("hostNameListBoxBean", hostNameLBB);
	  	setSelectedVOForUser(userId, selectedVO);

        viewGridResources(request, selectedGridName, selectedVO);
		// do not enable file browser before VO and lfchost selection

        response.setRenderParameter("selectedVO", selectedVO);
        response.setRenderParameter("portalPath", portalPath);
        response.setRenderParameter("userID", userId);
        response.setRenderParameter("hostNameListBoxshow", "true");


        response.setRenderParameter("showFileBrowser", "0");
        response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
		//event.getActionRequest().getPortletSession().setAttribute("showFileBrowser", "0");
	  	
	}

public void doList(ActionRequest request, ActionResponse response) {
	//lists contents of the selected VO and lfchost
        
        	String userId = getUserName(request);
        //response.setRenderParameter("selectedVO",this.getSelectedVOForUser(userId));
              
                response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
                
        response.setRenderParameter("hostNameListBoxshow", "true");
        try{
		Process p;
                String lfcHost = request.getParameter("hostNameListBoxBean_select");
        

		String gridName = request.getParameter("VOListBoxBean_select");
	
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId;
                 VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();

		// No VO selected
		if(gridName.equals("All") ) {
              
                        response.setRenderParameter("message", "Select a VO and LFC Host to list");
			//response.setRenderParameter("message", "Select a VO and LFC Host to list", getInitedFileBean(req));
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("userID", userId);
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");

			response.setRenderParameter("showFileBrowser", "0");

			///response.setRenderParameter("showFileBrowser", "0");
			//setNextState(request, "file/File.jsp");
			return;
		}

		// lfchost is not selected or specified through the textbox
		if( request.getParameter("VOListBoxBean_select") == null &&  ( request.getParameter("hostNameText")).equals("")) {
                   
                    response.setRenderParameter("message", "Select or Enter an LFC Host to list");
			//response.setRenderParameter("message", "Select or Enter an LFC Host to list", getInitedFileBean(request));
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("userID", userId);
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
			response.setRenderParameter("showFileBrowser", "0");

			//response.setRenderParameter("showFileBrowser", "0");
			//response.setRenderParameter("showFileBrowser", "0");
			//setNextState(request, "file/File.jsp");
			return;
		}
		boolean b = false; // whether the lfchost is selected from the listbox or specified from the textbox

		// if lfchost is specified through the textbox, use this
		// otherwise choose the selection from the listbox
		if(!( request.getParameter("hostNameText")).equals("")) {
                   
			lfcHost =  request.getParameter("hostNameText");
			b = true;
		}
		else {
			lfcHost =  request.getParameter("hostNameListBoxBean_select");
		}
		if( !userCertCheckMG(userId, gridName) ) {
                   
                    response.setRenderParameter("message", "No valid certificate for " + gridName);
			//response.setRenderParameter("message", "No valid certificate for " + gridName, getInitedFileBean(request));
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			//fileList.clear();
			response.setRenderParameter("userID", userId);
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
			response.setRenderParameter("showFileBrowser", "0");


			//response.setRenderParameter("showFileBrowser", "0");
			//response.setRenderParameter("showFileBrowser", "0");
			//setNextState(request, "file/File.jsp");
			return;
		}
		// initialize a filebrowser
		// filebrowser is a tree structure, having /grid/VO as the root
		// lastselecteditem is the last selected directory item
		// selecteds array contains the traversed directories
		SZGDirectoryItem rootDirItem = new SZGDirectoryItem("ROOT", "", null);
		SZGDirectoryItem lastSelectedItem = new SZGDirectoryItem("","",null);
		ArrayList selecteds = new ArrayList();
		SZGFileBrowser fb = new SZGFileBrowser(userId);
		fb.setRootItem(rootDirItem);
		fb.setLastSelectedItem(lastSelectedItem);
		fb.setSelectedItems(selecteds);
		fb.setGridName(gridName);
		fb.setLfcHost(lfcHost);
		RemoteFileManagerSingleton.getInstance().setCurrentBrowser(userId, fb );
		RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).setIsFromTextField(b);
		//fileList.clear();
		RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getRootItem().getItems().clear();
		RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().clear();

		// run bin/file/fileLs.sh script
		try {
                  
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "fileLs.sh  " +scripts_path+" "  +bdii +" "+ certPath + "  " +  lfcHost + "  " + gridName);
			InputStream is = p.getInputStream();
     		InputStreamReader isr = new InputStreamReader(is);
     		BufferedReader br = new BufferedReader(isr);
			String s;
			// insert the output (directories and files) as the items belonging to the root
			while ((s = br.readLine()) != null) {
				insertRootItems(s, userId);
			}
			ArrayList aList = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getRootItem().getItems();
			if (aList.size() == 0) {
                  
                                response.setRenderParameter("message", "Directory/file entries cannot be listed for the selected VO and LFC Host");
				//response.setRenderParameter("message", "Directory/file entries cannot be listed for the selected VO and LFC Host", getInitedFileBean(request));
				viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
				response.setRenderParameter("userID", userId);
				response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
				response.setRenderParameter("showFileBrowser", "0");


				//response.setRenderParameter("showFileBrowser", "0");
				//response.setRenderParameter("showFileBrowser", "0");
				//setNextState(request, "file/File.jsp");
				return;
			}
			//fillFileList(aList, fileList, "" );

			response.setRenderParameter("showFileBrowser", "1");

			response.setRenderParameter("path", "/grid/" + gridName);
			//response.setRenderParameter("path", "/grid/" + gridName );
			//response.setRenderParameter("showFileBrowser", "1");
			RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).setLastSelectedItem( RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getRootItem() );
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
                response.setRenderParameter("message",  "LFC name server directory/file entries listed");
	    //response.setRenderParameter("message", "LFC name server directory/file entries listed", getInitedFileBean(request));
		setSelectedVOForUser(userId, gridName);
		viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");


        }
        catch(Exception e){
            e.printStackTrace();
        }
		//setNextState(request, "file/File.jsp");
	}

public void doShowDetails(ActionRequest request, ActionResponse response) { //display detailed information for an item
		Process p;

		String fileName, guid;
                 String filelist = "";
		if (request.getParameter("fileLsList_select") != null){
                    filelist = request.getParameter("fileLsList_select");
                }

		String userId = getUserName(request);
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId ;
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
                 VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
		//event.getTextFieldBean("dirName").setValue("");
		//event.getTextFieldBean("newName").setValue("");
		if( !userCertCheckMG(userId, gridName) ) {
                        response.setRenderParameter("message", "No valid certificate for " + gridName);
			//response.setRenderParameter("message", "No valid certificate for " + gridName, getInitedFileBean(req));
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			//fileList.clear();
                        response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
			//setNextState(req, "doView");

			//setNextState(req, "file/File.jsp");
			return;
		}

		if(gridName.equals("All") || lfcHost == null  || lfcHost.equals("") ) {
			response.setRenderParameter("message", "Select a VO and LFCHOST");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));

			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
			//setNextState(req, "doView");

			//setNextState(req, "file/File.jsp");
			return;
		}
		String path = "/grid/"+gridName;
		// an item is not selected on the filelist
		 if((filelist == null) || filelist.equals(new String(""))) {
			response.setRenderParameter("message",  "An item is not selected");
			//fileList.clear();
			//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, "");
			for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
			}
                        response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
			response.setRenderParameter("path", path);
                          response.setRenderParameter("showlastselected","true");
			response.setRenderParameter("showFileBrowser", "1");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
			//setNextState(req, "doView");

			//setNextState(req, "file/File.jsp");
			return;
		}
		// extract name and type information from the selected item
		String[] names = filelist.split("@");
		fileName = names[names.length-1];
		String argument = "";
		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
			argument = argument + " " + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
			path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		}

		if(names[0].charAt(0) == '+') { //display details of a directory
			try {
				p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "directoryDetails.sh " +scripts_path+" "  +bdii +" "+ certPath + " " + lfcHost + "  " + gridName + " " + argument);
				InputStream is = p.getInputStream();
      			InputStreamReader isr = new InputStreamReader(is);
       			BufferedReader br = new BufferedReader(isr);
       			String s;
				// get the result of the lfc-ls -l ... command line by line
				// temp[1] : # of entries
				// temp[5] temp[6] temp[7] : last modification date
				while ((s = br.readLine()) != null) {
					String[] fProperties = s.split("\\s");
					String[] temp = new String[15];
					int j =0;
					for(int i=0; i<fProperties.length ; i++) {
						if(fProperties[i].trim().length() != 0) {
							temp[j] = fProperties[i];
							j++;
						}
					}
					if (temp[j-1].equals(fileName)) {


						response.setRenderParameter("isDetails", "showDirDetails");
						response.setRenderParameter("dirName", fileName);
                                                response.setRenderParameter("fileName", fileName);
                                                response.setRenderParameter("selecteditem", names[1]);
						response.setRenderParameter("entryNum", temp[1]);
				        response.setRenderParameter("modDate", temp[5]+" "+temp[6]+" "+temp[7]);
						response.setRenderParameter("path", path);
                                                response.setRenderParameter("showlastselected","true");
						break;
					}
				}
				argument = argument + " " + fileName;
				// get ACL information
				// aclList[1] : owner info
				// aclList[2] : group info
				// aclList[3] : owner ACL
				// aclList[4] : group ACL
				// aclList[5] : others ACL
                             
				p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "directoryACL.sh "  +scripts_path+" " +bdii +" "+ certPath + " " + lfcHost + "  " + gridName + " " + argument);
				is = p.getInputStream();
   				isr = new InputStreamReader(is);
   				br = new BufferedReader(isr);
				String[] aclList = new String[15];
				for(int i=0;(s=br.readLine())!=null;i++) {
					aclList[i] = s;
				}
                                response.setRenderParameter("showlastselected","true");
                                response.setRenderParameter("selecteditem", names[1]);
				response.setRenderParameter("owner", aclList[1]);
				response.setRenderParameter("group", aclList[2]);
				response.setRenderParameter("ownerACL", aclList[3]);
				response.setRenderParameter("groupACL", aclList[4]);
				response.setRenderParameter("otherACL", aclList[5]);
				//fileList.clear();
				//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, fileName);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		else { // display details of a file
			try {
                               
				p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "fileDetails.sh " +scripts_path+" "  +bdii +" "+ certPath + " " + lfcHost + "  " + gridName + " " + argument + " " + fileName);
				InputStream is = p.getInputStream();
       			InputStreamReader isr = new InputStreamReader(is);
       			BufferedReader br = new BufferedReader(isr);
				String[] fProperties = br.readLine().split("\\s");
				guid = br.readLine();
				// get the result of the lfc-ls -l ... and lcg-lg ... commands
				String[] temp = new String[15];
				for(int i=0, j=0; i<fProperties.length ; i++) {
					if(fProperties[i].trim().length() != 0) {
						temp[j] = fProperties[i];
					j++;
					}
				}
				response.setRenderParameter("guid", guid);
				response.setRenderParameter("isDetails", "showFileDetails");
                                response.setRenderParameter("selecteditem", names[1]);
                                response.setRenderParameter("showlastselected","true");
				response.setRenderParameter("fileName", fileName);
				response.setRenderParameter("fileSize", temp[4]);
				response.setRenderParameter("modDate", temp[5]+" "+temp[6]+" "+temp[7]);
				response.setRenderParameter("path", path);
				// get ACL information
				// aclList[1] : owner info
				// aclList[2] : group info
				// aclList[3] : owner ACL
				// aclList[4] : group ACL
				// aclList[5] : others ACL
				p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "fileACL.sh "  +scripts_path+" " +bdii +" "+ certPath + " " + lfcHost + "  " + gridName + " " + argument + " " + fileName);
				is = p.getInputStream();
       			isr = new InputStreamReader(is);
       			br = new BufferedReader(isr);
				String[] aclList = new String[15];
				String s;
				for(int i=0;(s=br.readLine())!=null;i++) {
					aclList[i] = s;
				}
				response.setRenderParameter("owner", aclList[1]);
				response.setRenderParameter("group", aclList[2]);
				response.setRenderParameter("ownerACL", aclList[3]);
				response.setRenderParameter("groupACL", aclList[4]);
				response.setRenderParameter("otherACL", aclList[5]);
				//fileList.clear();
				//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, fileName);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
		response.setRenderParameter("showFileBrowser", "1");
                response.setRenderParameter("showlastselected","true");
                response.setRenderParameter("selecteditem", names[1]);
		response.setRenderParameter("userID", getUserName(request));
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
		//setNextState(req, "doView");

		//setNextState(req, "file/File.jsp");

	}


private ArrayList<String> listLFCHosts(String voName, String portalPath) {
	// list LFCHOSTs of a VO
         
             VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(voName).getBdii();
		ArrayList<String> hostNameLBB = new ArrayList<String>();
		Process p;
		try {
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "lfcHostNames.sh  " +scripts_path+" "  +bdii +" "+ voName + " " + portalPath);
			p.waitFor();
			InputStream is = p.getInputStream();
       		InputStreamReader isr = new InputStreamReader(is);
       		BufferedReader br = new BufferedReader(isr);
			String s;
                      
			while ((s = br.readLine()) != null){
				//setHostNameListBoxBean(hostNameLBB, s);
                            hostNameLBB.add(s);
                      
			}
		}
		catch(Exception ex) {
	  		ex.printStackTrace();
	  	}
		return hostNameLBB;
	}
private String updateCurrentBrowser(String userId,String method){
    int length = 0;
    String path = "";
    if (method.equals(new String("cancel"))){
        length = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();
    }
    else
        length = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size()-1 ;

     ArrayList<String> browserpath = new ArrayList<String>();
     for(int i=0;i<length;i++) {
         String foldername = (String)RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
	 path = path + "/" + foldername;
          browserpath.add(foldername);

     }
     RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).setSelectedItems(browserpath);
     return path;

}
public synchronized void doGoBack(ActionRequest request,ActionResponse response) {
		// the action for going back to File.jsp page

		String portalPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir");
      	String userId = getUserName(request);
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
                 VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
		//ListBoxBean hostNameLBB = listLFCHosts(gridName, portalPath);
		//response.setRenderParameter("hostNameListBoxBean", hostNameLBB);
		//if(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getIsFromTextField() )
		//	event.getTextFieldBean("hostNameText").setValue(lfcHost);
		//String itemName = request.getParameter("itemName");
		//ListBoxBean fileList = event.getListBoxBean("fileLsList");
		//fileList.clear();
                String path="/grid/" + gridName;
		if( request.getParameter("fromPage").equals("download")) {
		// if returning from the download page, remove the file(s) saved by the download operation
			try {
				Process p;
				p = Runtime.getRuntime().exec( "/bin/bash " +scripts_path + "deleteDownloaded.sh " +scripts_path+" "  +bdii +" "+ PropertyLoader.getInstance().getProperty("tomcat.absolute.path") + " " + userId);
				p.waitFor();
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
                else if (request.getParameter("fromPage").equals("delete")){
                    path +=this.updateCurrentBrowser(userId,"cancel");
                }
                else if (!request.getParameter("fromPage").equals("replicas")){
                    path += this.updateCurrentBrowser(userId,"parent");
                }
		//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, itemName);
		
                
               


                

		response.setRenderParameter("path", path );
		response.setRenderParameter("showFileBrowser", "1");
                response.setRenderParameter("showlastselected", "1");
		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");

		//setNextState(req, "doView");
		//response.setRenderParameter("path", path );
		//response.setRenderParameter("showFileBrowser", "1");
		viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
                response.setRenderParameter("selectedVO",getSelectedVOForUser(userId));
		//setNextState(req, "file/File.jsp");
    }


public void doListSelectedDir(ActionRequest request, ActionResponse response) {
 		String selectedItemName, selectedItem;
		Process p;
                String filelist = "";
		if (request.getParameter("fileLsList_select") != null){
                    filelist = request.getParameter("fileLsList_select");
                }


		String userId = getUserName(request);
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
           
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
           
		//event.getTextFieldBean("dirName").setValue("");
		//event.getTextFieldBean("newName").setValue("");
		if( !userCertCheckMG(userId, gridName) ) {
                    response.setRenderParameter("message",  "No valid certificate for " + gridName);
			//response.setRenderParameter("message", "No valid certificate for " + gridName, getInitedFileBean(req));
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			//fileList.clear();
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", userId);
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                        response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
			
			//setNextState(req, "file/File.jsp");
			return;
		}
		if(gridName.equals("All")  || lfcHost == null || lfcHost.equals("")) {
                    response.setRenderParameter("message",  "Select a VO and LFC Host, then a directory to list");
			//response.setRenderParameter("message", "Select a VO and LFC Host, then a directory to list", getInitedFileBean(req));
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", userId);
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
			//setNextState(req, "doView");
			//setNextState(req, "file/File.jsp");
			return;
		}
		String path = "/grid/"+gridName;

		//if(!fileList.hasSelectedValue()) {
                if((filelist == null) || filelist.equals(new String(""))) {
                 
                    response.setRenderParameter("message","Select a directory to list");
				//response.setRenderParameter("message", "Select a directory to list", getInitedFileBean(req));
				for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
					path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
				}
				response.setRenderParameter("path", path);
				viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
				//fileList.clear();
				//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, "");
				response.setRenderParameter("showFileBrowser", "1");
                                response.setRenderParameter("showLastSelected", "1");
				response.setRenderParameter("userID", userId);
				response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                                response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
				//setNextState(req, "doView");
				//setNextState(req, "file/File.jsp");
				return;
		}
		try {
			int index;
			// extract the name and directory/file information from the selected item
			String[] names = filelist.split("@");
			selectedItemName = names[names.length-1];
			if(names[0].charAt(0) != '+' ||  names[0].trim().length() == 0 ) {
                        
                            response.setRenderParameter("message", "Selected entry is not a directory");
				//response.setRenderParameter("message", "Selected entry is not a directory", getInitedFileBean(req));
				//fileList.clear();
				//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, selectedItemName);
				/*for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
					path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
				}*/
                                response.setRenderParameter("selectedItemName",selectedItemName);
				response.setRenderParameter("path", path);
				response.setRenderParameter("showFileBrowser", "1");
                                response.setRenderParameter("showLastSelected", "1");
				viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
				response.setRenderParameter("userID", userId);
				response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                                response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
				//setNextState(req, "doView");
				//setNextState(req, "file/File.jsp");
				return;
			}
			else {
				// find the selected item in the list of items of the lastselecteditem
				// the newly listed items will be inserted to the items of this item
				for(index=0 ; index < RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems().size() ; index++) {
					SZGItem x = (SZGItem)RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems().get(index);
					if (x.isDir() && x.getName().equals(selectedItemName)) {
						break;
					}
				}
	 			RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().add(selectedItemName);
				String argument = "";

				for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
					argument = argument + " " + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
					path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
				}
                                 VOInfoRequester inforeq = new VOInfoRequester();
                                String bdii = inforeq.getInfos(gridName).getBdii();
				String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId ;
                              
				p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "directoryLs.sh  "  +scripts_path+" " +bdii +" "+ certPath + "  " + lfcHost + "  " + gridName + "  " + argument);
       			InputStream is = p.getInputStream();
       			InputStreamReader isr = new InputStreamReader(is);
      	 		BufferedReader br = new BufferedReader(isr);
       			String s;
				SZGDirectoryItem dirItem = new SZGDirectoryItem(selectedItemName, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem());
				// add the listed items to the items of the selected item
				while ((s = br.readLine()) != null) {
					String[] itemProperties = s.split("\\s");
					String name = itemProperties[itemProperties.length-1];
					String accessRights = itemProperties[0];
					if (accessRights.charAt(0) == 'd') {
						SZGDirectoryItem dItem = new SZGDirectoryItem(name, "", dirItem);
						dirItem.addItem(dItem);
					}
					else if (accessRights.charAt(0) == '-') {
						SZGFileItem fItem = new SZGFileItem(name, "", dirItem);
						dirItem.addItem(fItem);
					}
				}
				response.setRenderParameter("path", path);
                                response.setRenderParameter("showlastselected", "1");
				response.setRenderParameter("showFileBrowser", "1");
				RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems().set(index, dirItem);
				//fileList.clear();

				//fillFileList(dirItem.getItems(), fileList, "");
                                response.setRenderParameter("message", "Directory/file entries in the selected directory listed");
				//response.setRenderParameter("message", "Directory/file entries in the selected directory listed", getInitedFileBean(req));
				RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).setLastSelectedItem(dirItem);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
		//setNextState(req, "doView");
		//setNextState(req, "file/File.jsp");
	}

public synchronized void doListReplicas(ActionRequest request,ActionResponse response) {
									// list replicas of a file on the page fileReplicas.jsp

		String filelist = "";
		if (request.getParameter("fileLsList_select") != null){
                    filelist = request.getParameter("fileLsList_select");
                }

		String userId = getUserName(request);
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId ;
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		//ListBoxBean seLBB = new ListBoxBean();
		//event.getTextFieldBean("dirName").setValue("");
		//event.getTextFieldBean("newName").setValue("");
		String path = "/grid/"+gridName;
		//
		if( !userCertCheckMG(userId, gridName) ) {
			response.setRenderParameter("message", "No valid certificate for " + gridName);
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			//fileList.clear();
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                        response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
			//setNextState(request, "doView");

			//setNextState(request, "file/File.jsp");
			return;
		}
		//
		if(gridName.equals("All")  || lfcHost == null || lfcHost.equals("")) {
			response.setRenderParameter("message", "Select a VO and LFCHOST");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
			//setNextState(request, "doView");

			//setNextState(request, "file/File.jsp");
			return;
		}

		  if((filelist == null) || filelist.equals(new String(""))) {
				response.setRenderParameter("message", "Select a file to list the replicas of that file");
				for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
					path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
				}
				response.setRenderParameter("path", path);
				viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
				if (RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getRootItem().getItems().size() != 0 ) {
					//fileList.clear();
					//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, "");
				}
				response.setRenderParameter("showFileBrowser", "1");
                                response.setRenderParameter("showLastSelected", "1");
				response.setRenderParameter("userID", getUserName(request));
				response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                                response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
				//setNextState(request, "doView");

				//setNextState(request, "file/File.jsp");
				return;
		}
		try {
			// extract name and type information for the selected item
			String[] names = filelist.split("@");
			String selectedItemName = names[names.length-1];
			if(names[0].charAt(0) != '-' ||  names[0].trim().length() == 0 ) {
				response.setRenderParameter("message", "Selected entry is not a file");
				//fileList.clear();
				//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, selectedItemName);
				for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
					path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
				}
				response.setRenderParameter("path", path);
				response.setRenderParameter("showFileBrowser", "1");
                                response.setRenderParameter("showLastSelected", "1");
				viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
				response.setRenderParameter("userID", getUserName(request));
				response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                                response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
				//setNextState(request, "doView");

				//setNextState(request, "file/File.jsp");
				return;
			}
			else {
				String argument = "";
				for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
					argument = argument + " " + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
					path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
				}
				ArrayList replicaList = new ArrayList();
				// list the replicas of the selected file
				//replicaList = listReplicas(certPath, lfcHost, gridName, argument, selectedItemName);
				//event.getListBoxBean("SEListBoxBean").clear();
				// list storage elements of the involved VO
				//seLBB = listSE(certPath, lfcHost, gridName);
				//response.setRenderParameter("SEListBoxBean", seLBB);
				response.setRenderParameter("SEListBoxshow","true");

				response.setRenderParameter("certPath",certPath);
				response.setRenderParameter("lfcHost",lfcHost);
				response.setRenderParameter("gridName",gridName);
				response.setRenderParameter("path", path);
				response.setRenderParameter("fileName", selectedItemName);
				response.setRenderParameter("selectedItemName", selectedItemName);
				response.setRenderParameter("replicashow", "true");
				response.setRenderParameter("argument", argument);

				//response.setRenderParameter("replicaList", replicaList);
				viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
				response.setRenderParameter("userID", getUserName(request));
				response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileReplicas.jsp");
                                response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
				//setNextState(request, "doView");


				//setNextState(request, "file/fileReplicas.jsp");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
public synchronized void doDeleteReplica(ActionRequest request,ActionResponse response) {
	// remove a replica of a file


                String replicaList = "";
		if (request.getParameter("replicaListBoxBean_select") != null){

                    replicaList = request.getParameter("replicaListBoxBean_select");
                }
                String filelist = "";
		if (request.getParameter("fileLsList_select") != null){
                    filelist = request.getParameter("fileLsList_select");
                }

		String userId = getUserName(request);
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId ;
		String portalPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir");
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		String path = "/grid/"+gridName;

		String fileName = request.getParameter("fileName");
		String replicaSize = request.getParameter("replicaSize");

		if( !userCertCheckMG(userId, gridName) ) {
			response.setRenderParameter("message", "No valid certificate for " + gridName);
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", getUserName(request));



			//setNextState(req, "file/File.jsp");
			return;
		}
		String argument = "";
		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
			argument = argument + " " + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
			path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		}
		// find storage elements of the involved VO


		if(((replicaList == null) || replicaList.equals(new String("")))) { // a replica is not selected to delete
			response.setRenderParameter("message", "A replica is not selected");
			ArrayList replicaList2 = new ArrayList();
			replicaList2 = listReplicas(certPath, lfcHost, gridName, argument, fileName);

			response.setRenderParameter("SEListBoxshow","true");

			response.setRenderParameter("certPath",certPath);
			response.setRenderParameter("lfcHost",lfcHost);
			response.setRenderParameter("gridName",gridName);
			//response.setRenderParameter("SEListBoxBean", seLBB);
			response.setRenderParameter("path", path);
			response.setRenderParameter("fileName", fileName);
			//event.getListBoxBean("replicaListBoxBean").clear();
			//response.setRenderParameter("replicaList", replicaList2);
			response.setRenderParameter("selectedItemName", fileName);
			response.setRenderParameter("replicashow", "true");
			response.setRenderParameter("argument", argument);
			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileReplicas.jsp");



			//setNextState(req, "file/fileReplicas.jsp");
			return;
		}
		try{
			Process p;
			// extract the storage element name from the replica name
			String sElement = replicaList;
			String[] sElementList = sElement.split("/");
			String seName = sElementList[2];
                         VOInfoRequester inforeq = new VOInfoRequester();
                        String bdii = inforeq.getInfos(gridName).getBdii();
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "deleteReplica.sh  "  +scripts_path+" " +bdii +" "+ certPath + "  " + lfcHost + "  " + sElement + " " + seName + " " + gridName + "  " + argument + " " + fileName  );
			p.waitFor();
			InputStream is = p.getInputStream();
       		InputStreamReader isr = new InputStreamReader(is);
       		BufferedReader br = new BufferedReader(isr);
			ArrayList temp = new ArrayList();
			String s;
			for(int i=0;(s = br.readLine())!= null;i++) {
				temp.add(s);
			}
			if( temp.get((temp.size()-1)).equals("0") ) {
				if(replicaSize.equals("1")) { // all replicas are deleted, delete the file from browser
											  // go back to File.jsp page
					SZGFileItem fItem = new SZGFileItem(fileName, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem());
					RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().removeItem(fItem);
					response.setRenderParameter("message", "The file is deleted");

					response.setRenderParameter("path", path );
					response.setRenderParameter("showFileBrowser", "1");
                                        response.setRenderParameter("showLastSelected", "1");
					viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
					//ListBoxBean hostNameLBB = listLFCHosts(gridName, portalPath);
					response.setRenderParameter("selectedVO", gridName);
					response.setRenderParameter("portalPath",portalPath);
					response.setRenderParameter("hostNameListBoxshow","true");
					response.setRenderParameter("selectedItemName", fileName);
					response.setRenderParameter("certPath",certPath);
					response.setRenderParameter("lfcHost",lfcHost);
					response.setRenderParameter("gridName",gridName);

					response.setRenderParameter("replicashow", "true");
					response.setRenderParameter("argument", argument);
					//response.setRenderParameter("hostNameListBoxBean", hostNameLBB);
					/*if(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getIsFromTextField() )
						event.getTextFieldBean("hostNameText").setValue(lfcHost);*/
					response.setRenderParameter("userID", getUserName(request));
					response.setRenderParameter("nextJSP","file/File.jsp");


					//setNextState(req, "file/File.jsp");
					return;
				}
				else
					response.setRenderParameter("message", "The replica is deleted");
				response.setRenderParameter("showErrorButton", "0");
			}
			else {
				response.setRenderParameter("message", "The replica cannot be deleted");
				response.setRenderParameter("showErrorButton", "1");
			}
			ArrayList replicaList2 = new ArrayList();
			// update the replica list after deletion
			//replicaList2 = listReplicas(certPath, lfcHost, gridName, argument, fileName);

			//response.setRenderParameter("SEListBoxBean", seLBB);
			response.setRenderParameter("SEListBoxshow","true");
			//event.getListBoxBean("replicaListBoxBean").clear();
			//response.setRenderParameter("replicaList", replicaList2);
			response.setRenderParameter("selectedItemName", fileName);
			response.setRenderParameter("replicashow", "true");
			response.setRenderParameter("argument", argument);


			response.setRenderParameter("certPath",certPath);
			response.setRenderParameter("lfcHost",lfcHost);
			response.setRenderParameter("gridName",gridName);
			response.setRenderParameter("path", path);
			response.setRenderParameter("fileName", fileName);
			//event.getListBoxBean("replicaListBoxBean").clear();
			//response.setRenderParameter("replicaList", replicaList2);
			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileReplicas.jsp");



			//setNextState(req, "file/fileReplicas.jsp");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
public void doMakeDir(ActionRequest request,ActionResponse response) { //create a directory
		Process p;
		 String filelist = "";
		if (request.getParameter("fileLsList_select") != null){
                    filelist = request.getParameter("fileLsList_select");
                }
		 String dirName = "";
		if (request.getParameter("dirName") != null){
                    dirName = request.getParameter("dirName");
                }


		String userId = getUserName(request);
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId;
                 VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
		//event.getTextFieldBean("newName").setValue("");
		if( !userCertCheckMG(userId, gridName) ) {
			response.setRenderParameter("message", "No valid certificate for " + gridName);
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
			response.setRenderParameter("showFileBrowser", "0");

			return;
		}

		if(gridName.equals("All") || lfcHost == null || lfcHost.equals("") ) {
			response.setRenderParameter("message", "Select a VO and an LFCHOST");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("showFileBrowser", "0");

			return;
		}
		// find the traversed path
		String path = "/grid/"+gridName;
		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		}
		try {
			// run the makedir script
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "makeDirectory.sh " +scripts_path+" "  +bdii +" "+ certPath + "  " + lfcHost + " " + gridName + " " + path + " " + dirName);
			p.waitFor();
			InputStream is = p.getInputStream();
       		InputStreamReader isr = new InputStreamReader(is);
       		BufferedReader br = new BufferedReader(isr);
			ArrayList temp = new ArrayList();
			String s;
			for(int i=0;(s = br.readLine())!= null;i++) {
				temp.add(s);
			}
			if( temp.get((temp.size()-1)).equals("0") ) {
				response.setRenderParameter("message", "The directory is created");
				SZGDirectoryItem dirItem = new SZGDirectoryItem(dirName, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem());
				RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().addItem(dirItem);
				response.setRenderParameter("showErrorButton", "0");
                                response.setRenderParameter("showlastselected","true");
                                
			}
			else {
				response.setRenderParameter("message", "The directory cannot be created");
				response.setRenderParameter("showErrorButton", "1");
			}

			//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, dirName);
			response.setRenderParameter("path", path );
			response.setRenderParameter("showFileBrowser", "1");
                        response.setRenderParameter("showLastSelected", "1");
                        response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("userID", getUserName(request));



			//setNextState(req, "file/File.jsp");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
public void doDelete(ActionRequest request,ActionResponse response) {
		Process p, p2, p3;

                String fileName = "";
                String type = "";
		if (request.getParameter("fileName") != null){
                    
                    type =request.getParameter("fileName").split("@")[0];
                    fileName=request.getParameter("fileName").split("@")[1];
                }
                String path = "";
		if (request.getParameter("path") != null){
                    path = request.getParameter("path");
                }
               

		String userId = getUserName(request);
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId;
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
                VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
		if( !userCertCheckMG(userId, gridName) ) {
			response.setRenderParameter("message", "No valid certificate for " + gridName);
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", getUserName(request));
                        response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));


			//setNextState(req, "file/File.jsp");
			return;
		}
		try {
			if(type.equals("-")) { //delete a file
                           
				p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "fileDelete.sh " +scripts_path+" "  +bdii +" "+ certPath + "  " + lfcHost + " " + gridName + " " + path + " " + fileName);
				p.waitFor();

			}
			else {
				// selected item is a directory
				// list the contents of this directory and try to remove all the elements in the directory
				// if the selected directory contains directories, the empty ones will be removed
				// if these directories are not empty, they will not be removed recursively
				// the selected directory will be removed if it is finally empty
				String argument="";
				for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++)
					argument = argument + " " + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
					argument = argument + " " + fileName;
					// list the directory
					p2 = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "directoryLs.sh  "  +scripts_path+" " +bdii +" "+ certPath + "  " + lfcHost + "  " + gridName + "  " + argument);
       				InputStream is = p2.getInputStream();
       				InputStreamReader isr = new InputStreamReader(is);
      	 			BufferedReader br = new BufferedReader(isr);
       				String s;
				while ((s = br.readLine()) != null) {
					// try to remove each item under this directory
					String[] itemProperties = s.split("\\s");
					String name = itemProperties[itemProperties.length-1];
					String accessRights = itemProperties[0];
					if (accessRights.charAt(0) == '-') {
						p3 = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "fileDelete.sh " +scripts_path+" "  +bdii +" "+ certPath + "  " + lfcHost + " " + gridName + " " + path + "/" + fileName + " " + name);
						p3.waitFor();
					}
				}
				// try to remove the directory
				// will not succeed if all the items were not deleted
				p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "directoryDelete.sh "  +scripts_path+" " +bdii +" "+ certPath + " " + lfcHost + " " + gridName + " " + path + " " +fileName);
				p.waitFor();
                                /*String[] splitted_path = path.split("/");
                                String parent_path = "";
                                for (int i=0;i<splitted_path.length-1;++i){
                                    parent_path+="/"+splitted_path[i];
                                }
                                path = parent_path;
                                 * 
                                 */
			}
			InputStream is = p.getInputStream();
       		InputStreamReader isr = new InputStreamReader(is);
       		BufferedReader br = new BufferedReader(isr);
			ArrayList temp = new ArrayList();
			String s;
			for(int i=0;(s = br.readLine())!= null;i++) {
				temp.add(s);
			}
			if( (temp.get(temp.size()-1)).equals("1") ) {
				response.setRenderParameter("message", "The file/directory cannot be removed");
				response.setRenderParameter("showErrorButton", "1");
			}
			else {
				response.setRenderParameter("message", "The file/directory is removed");
				response.setRenderParameter("showErrorButton", "0");
				if (type.equals("-")) {
					SZGFileItem fItem = new SZGFileItem(fileName, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem());
                                       RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().removeItem(fItem);
                                        RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).setLastSelectedItem((SZGDirectoryItem)fItem.getParent());
		RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().remove( (RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems()).size()-1);

				}
				else {

					SZGDirectoryItem dirItem = new SZGDirectoryItem(fileName, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem());
                                  

					RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().removeItem(dirItem);

                                        RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).setLastSelectedItem((SZGDirectoryItem)dirItem.getParent());
		RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().remove( (RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems()).size()-1);

				}
			}

                        response.setRenderParameter("selectedVO",RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName());
			response.setRenderParameter("completed", "1");
			response.setRenderParameter("path", path);
                        response.setRenderParameter("showFileBrowser", "1");
                        response.setRenderParameter("showlastselected","1");
			response.setRenderParameter("fileName", fileName);
			response.setRenderParameter("type", type);
			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");



			//setNextState(req, "file/fileDelete.jsp");
			return;
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

public void doGoDelete(ActionRequest request,ActionResponse response) {
		 String filelist = "";
		if (request.getParameter("fileLsList_select") != null){
                    filelist = request.getParameter("fileLsList_select");
                }

		String userId = getUserName(request);
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		//event.getTextFieldBean("dirName").setValue("");
		//event.getTextFieldBean("newName").setValue("");
		if( !userCertCheckMG(userId, gridName) ) {
			response.setRenderParameter("message", "No valid certificate for " + gridName);
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));

			response.setRenderParameter("showFileBrowser", "0");

                        //setNextState(req, "file/File.jsp");
			response.setRenderParameter("userID", getUserName(request));



			//setNextState(req, "file/File.jsp");
			return;
		}
	/*	if(gridName.equals("All") || lfcHost == null || lfcHost.equals("") ) {
			response.setRenderParameter("message", "Select a VO and an LFC HOST");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", getUserName(request));



			//setNextState(req, "file/File.jsp");
			return;
		}
         *
         */
		String path = "/grid/"+gridName;
		String fileName = "";
		String type = "";
		if((filelist == null) || filelist.equals(new String(""))) {

			//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, "");
			for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
			}
			response.setRenderParameter("path", path);
			response.setRenderParameter("type", type);
			response.setRenderParameter("showFileBrowser", "1");
                        response.setRenderParameter("showlastselected", "1");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("message", "A file or directory is not selected");
			response.setRenderParameter("userID", getUserName(request));

			//setNextState(req, "file/File.jsp");
			return;
		}
		// extract the name and the type of the selected item
		String[] names = filelist.split("@");
		fileName = names[names.length-1];
		type = names[0];
		
		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		}
		response.setRenderParameter("path", path);
		response.setRenderParameter("fileName", fileName);
		response.setRenderParameter("type", type);
		response.setRenderParameter("completed", "0");
		response.setRenderParameter("userID", getUserName(request));
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileDelete.jsp");
                String selectedVO = request.getParameter("VOListBoxBean_select");
                
                UserDataStorage.getInstance().getUserData(userId).setSelectedVO(selectedVO);


		//setNextState(req, "file/fileDelete.jsp");
		return;
	}
public synchronized void doRename(ActionRequest request,ActionResponse response) { // rename o directory-file
		Process p;

                String filelist = "";
		if (request.getParameter("fileLsList_select") != null){
                    filelist = request.getParameter("fileLsList_select");
                }
		String newName = "";
		if (request.getParameter("newName") != null){
                    newName= request.getParameter("newName");
                }


		String userId = getUserName(request);
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId;
 VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
		if( !userCertCheckMG(userId, gridName) ) {
			response.setRenderParameter("message", "No valid certificate for " + gridName);
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
                        response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", getUserName(request));

			//setNextState(req, "file/File.jsp");
			return;
		}
		String path = "/grid/"+gridName;
		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		}
		if((filelist == null) || filelist.equals(new String(""))) {
				response.setRenderParameter("message", "Select the directory/file to be renamed");
				for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
					path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
				}
				response.setRenderParameter("path", path);
				response.setRenderParameter("showFileBrowser", "1");
                                response.setRenderParameter("showlastselected", "1");
				viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));

				//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, "");
				response.setRenderParameter("userID", getUserName(request));
                                response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));

				//setNextState(req, "file/File.jsp");
				return;
		}
		// extract the name of selected item
		String[] names = filelist.split("@");
		String oldName = names[names.length-1];
		try {
                   
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "fileRename.sh "  +scripts_path+" " +bdii +" "+ certPath + " " + lfcHost + "  " + gridName + " " + path + "/" + oldName + "  " + path + "/" + newName);
			p.waitFor();
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
       		BufferedReader br = new BufferedReader(isr);
			String s = br.readLine();
			if(s.equals("1")){
				response.setRenderParameter("message", "The file/directory cannot be renamed");
				response.setRenderParameter("showErrorButton", "1");
			}
			else {
				response.setRenderParameter("message", "The file/directory is renamed");
				response.setRenderParameter("showErrorButton", "0");
				// file-directory is renamed
				// update the name of the item in the items of the lastselecteditem accordingly
				if(names[0].charAt(0) == '-') {
					SZGFileItem fItem = new SZGFileItem(oldName, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem());
					RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().removeItem(fItem);
					SZGFileItem fItem2 = new SZGFileItem(newName, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem());
					RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().addItem(fItem2);
				}
				else {
					SZGDirectoryItem dirItem = new SZGDirectoryItem(oldName, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem());
					RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().removeItem(dirItem);
					SZGDirectoryItem dirItem2 = new SZGDirectoryItem(newName, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem());
					RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().addItem(dirItem2);
				}
			}

			//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, newName);
			response.setRenderParameter("path", path );
			response.setRenderParameter("showFileBrowser", "1");
                        response.setRenderParameter("showlastselected", "1");

			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
                        response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
			response.setRenderParameter("userID", getUserName(request));

		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
		public synchronized void doChmode(ActionRequest request,ActionResponse response) { //changes mode of a file

		String userId = getUserName(request);
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId ;
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		if( !userCertCheckMG(userId, gridName) ) {
			response.setRenderParameter("message", "No valid certificate for " + gridName);
			response.setRenderParameter("fileName", request.getParameter("fileName"));
                        String arguments = request.getParameter("file_arguments");
			response.setRenderParameter("path", arguments.split("@")[1]);
			response.setRenderParameter("isDir", arguments.split("@")[0]);
			response.setRenderParameter("ownerACL", arguments.split("@")[2]);
			response.setRenderParameter("groupACL", arguments.split("@")[3]);
			response.setRenderParameter("otherACL", arguments.split("@")[4]);
			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileUpdate.jsp");

			//
			//setNextState(req, "file/fileUpdate.jsp");
			return;
		}
		Process p;
		String name = request.getParameter("fileName");

		String path = request.getParameter("file_arguments").split("@")[1];
		String urBean = request.getParameter("ownRead");
		String uwBean = request.getParameter("ownWrite");
		String uxBean = request.getParameter("ownExecute");
		String grBean = request.getParameter("grRead");
		String gwBean = request.getParameter("grWrite");
		String gxBean = request.getParameter("grExecute");
		String orBean = request.getParameter("othRead");
		String owBean = request.getParameter("othWrite");
		String oxBean = request.getParameter("othExecute");
		String umode = "";
		String gmode = "";
		String omode = "";
		String umode2 = "";
		String gmode2 = "";
		String omode2 = "";
		// construct file mode strings from the related checkboxes
		//user
		if(urBean == null && uwBean == null && uxBean == null)
			{ umode = "0"; umode2 = "---";}
		if(urBean == null && uwBean == null && uxBean != null)
			{ umode = "1"; umode2 = "--x";}
		if(urBean == null && uwBean != null && uxBean == null)
			{ umode = "2"; umode2= "-w-"; }
		if(urBean == null && uwBean != null && uxBean != null)
			{ umode = "3"; umode2 = "-wx"; }
		if(urBean != null && uwBean == null && uxBean == null)
			{ umode = "4"; umode2 = "r--"; }
		if(urBean != null && uwBean == null && uxBean != null)
			{ umode = "5"; umode2 = "r-x"; }
		if(urBean != null && uwBean != null && uxBean == null)
			{ umode = "6"; umode2= "rw-"; }
		if(urBean != null && uwBean != null && uxBean != null)
			{ umode = "7"; umode2 = "rwx"; }
		//group
		if(grBean == null && gwBean == null && gxBean == null)
			{ gmode = "0"; gmode2 = "---";}
		if(grBean == null && gwBean == null && gxBean != null)
			{ gmode = "1"; gmode2 = "--x";}
		if(grBean == null && gwBean != null && gxBean == null)
			{ gmode = "2"; gmode2= "-w-"; }
		if(grBean == null && gwBean != null && gxBean != null)
			{ gmode = "3"; gmode2 = "-wx"; }
		if(grBean != null && gwBean == null && gxBean == null)
			{ gmode = "4"; gmode2 = "r--"; }
		if(grBean != null && gwBean == null && gxBean != null)
			{ gmode = "5"; gmode2 = "r-x"; }
		if(grBean != null && gwBean != null && gxBean == null)
			{ gmode = "6"; gmode2= "rw-"; }
		if(grBean != null && gwBean != null && gxBean != null)
			{ gmode = "7"; gmode2 = "rwx"; }
		//other
		if(orBean == null && owBean == null && oxBean == null)
			{ omode = "0"; omode2 = "---";}
		if(orBean == null && owBean == null && oxBean != null)
			{ omode = "1"; omode2 = "--x";}
		if(orBean == null && owBean != null && oxBean == null)
			{ omode = "2"; omode2= "-w-"; }
		if(orBean == null && owBean != null && oxBean != null)
			{ omode = "3"; omode2 = "-wx"; }
		if(orBean != null && owBean == null && oxBean == null)
			{ omode = "4"; omode2 = "r--"; }
		if(orBean != null && owBean == null && oxBean != null)
			{ omode = "5"; omode2 = "r-x"; }
		if(orBean != null && owBean != null && oxBean == null)
			{ omode = "6"; omode2= "rw-"; }
		if(orBean != null && owBean != null && oxBean != null)
			{ omode = "7"; omode2 = "rwx"; }

		String mode = umode + gmode + omode;
                 VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
		try {
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "fileChmode.sh " +scripts_path+" " +bdii +" "+ certPath + " " + lfcHost + "  " + gridName + " " + mode + "  " + path + "/" + name );
			p.waitFor();
			InputStream is = p.getInputStream();
       		InputStreamReader isr = new InputStreamReader(is);
       		BufferedReader br = new BufferedReader(isr);
			String s = br.readLine();
			if(s.equals("1")){ //error on change mode
				response.setRenderParameter("message", "The file/directory mode cannot be changed");
				response.setRenderParameter("ownerACL",request.getParameter("file_arguments").split("@")[2]);
                                response.setRenderParameter("groupACL",request.getParameter("file_arguments").split("@")[3]);
                                response.setRenderParameter("otherACL",request.getParameter("file_arguments").split("@")[4]);
				
			}
			else { //file-directory mode is changed, update these information also on the form
				response.setRenderParameter("ownerACL", "user::" + umode2);
				response.setRenderParameter("groupACL", "group::" + gmode2);
				response.setRenderParameter("otherACL", "other::" + omode2);
				response.setRenderParameter("message", "The file/directory mode is changed");
			}
			response.setRenderParameter("fileName", request.getParameter("fileName"));
			response.setRenderParameter("path", request.getParameter("file_arguments").split("@")[1]);
			response.setRenderParameter("isDir", request.getParameter("file_arguments").split("@")[0]);

			response.setRenderParameter("userID", getUserName(request));
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileUpdate.jsp");

			return;
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

    	public void doGoUpdate(ActionRequest request,ActionResponse response) {


	//	event.getTextFieldBean("dirName").setValue("");
	//	event.getTextFieldBean("newName").setValue("");
                String arguments = null;
                if ( request.getParameter("file_arguments") != null ){
		arguments=request.getParameter("file_arguments");
                response.setRenderParameter("path", arguments.split("@")[1]);
		response.setRenderParameter("isDir",  arguments.split("@")[0]);
		response.setRenderParameter("ownerACL",  arguments.split("@")[2]);
		response.setRenderParameter("groupACL",  arguments.split("@")[3]);
		response.setRenderParameter("otherACL",  arguments.split("@")[4]);




		}
		if ( request.getParameter("fileName") != null ){
		response.setRenderParameter("fileName", request.getParameter("fileName"));
		}

		//response.setRenderParameter("name", request.getParameter("name"));
		
		response.setRenderParameter("userID", getUserName(request));
                UserDataStorage.getInstance().getUserData(getUserName(request)).setSelectedVO(request.getParameter("VOListBoxBean_select"));


		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileUpdate.jsp");

		//
		//setNextState(req, "file/fileUpdate.jsp");
		return;
	}
  public void doUpload(ActionRequest request, ActionResponse response,HashMap params)
  {
      PortletContext context = getPortletContext();
      context.log("[FileUploadPortlet] doUpload() called");
      try
      {
     /*     DiskFileItemFactory factory = new DiskFileItemFactory();
          PortletFileUpload pfu = new PortletFileUpload(factory);
          pfu.setSizeMax(10485760); // Maximum upload size
          //pfu.setProgressListener(new FileUploadProgressListener());
          PortletSession ps=request.getPortletSession();
          ps.setAttribute("upload", pfu,ps.APPLICATION_SCOPE);
      *
      */
    //get the FileItems
          String fieldName = null;
          String se= null;
          String upName= null;
          String urBean= null;
          String uwBean= null;
          String uxBean= null;
          String grBean= null;
          String gwBean= null;
          String gxBean= null;
          String orBean= null;
          String owBean= null;
          String oxBean= null;
          String origfile = "";
        //  List fileItems = pfu.parseRequest(request);
        //  Iterator iter = fileItems.iterator();
            Iterator iter = params.keySet().iterator();
          while (iter.hasNext())
          {
             //FileItem item = (FileItem)iter.next();
              String item = (String)iter.next();
          
      // retrieve hidden parameters if item is a form field
                 
                 if (item.equals(new String("se"))){
                    se = params.get(item).toString();
                 }
                 if (item.equals(new String("upName"))){
                    upName = params.get(item).toString();
                 }
                 if (item.equals(new String("urBean"))){
                    urBean = params.get(item).toString();
                 }
                 if (item.equals(new String("uwBean"))){
                    uwBean = params.get(item).toString();
                 }
                 if (item.equals(new String("uxBean"))){
                    uxBean = params.get(item).toString();
                 }
                 if (item.equals(new String("grBean"))){
                    grBean = params.get(item).toString();
                 }
                 if (item.equals(new String("gwBean"))){
                    gwBean = params.get(item).toString();
                 }
                 if (item.equals(new String("gxBean"))){
                    gxBean = params.get(item).toString();
                 }
                 if (item.equals(new String("orBean"))){
                    orBean = params.get(item).toString();
                 }
                 if (item.equals(new String("owBean"))){
                    owBean = params.get(item).toString();
                 }
                 if (item.equals(new String("oxBean"))){
                    oxBean = params.get(item).toString();
                 }
              if (item.equals(new String("origfile"))){
                    origfile = params.get(item).toString();
                 }
           

      }




      //uploading to remote host

                 Process p;
            String argument = "";
            String userId = getUserName(request);
            String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
            String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
            String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId;
            if (!userCertCheckMG(userId, gridName)) {
         
                response.setRenderParameter("message", "No valid certificate for ");
                viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
                String path = "/grid/" + gridName;
                for (int i = 0; i < RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size(); i++) {
                    path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
                }
                response.setRenderParameter("path", path);
                //ArrayList prefList = readUserPref(userId);
                //response.setRenderParameter("prefList", prefList);
                //response.setRenderParameter("SEListBoxBean", seLBB);
                response.setRenderParameter("SEListBoxshow", "true");
                response.setRenderParameter("prefListshow", "true");
                response.setRenderParameter("certPath", certPath);
                response.setRenderParameter("lfcHost", lfcHost);
                response.setRenderParameter("gridName", gridName);
                response.setRenderParameter("userID", userId);
                response.setRenderParameter("nextJSP", "/WEB-INF/jsp/file/fileUpload.jsp");
                return;
            }
            if (se == null || se.equals("")) {
                // SE is not selected
              
                response.setRenderParameter("message", "Select an SE to upload the file");
                String path = "/grid/" + gridName;
                for (int i = 0; i < RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size(); i++) {
                    path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
                }
                response.setRenderParameter("path", path);
                ArrayList prefList = readUserPref(userId);
                //response.setRenderParameter("prefList", prefList);
                //response.setRenderParameter("SEListBoxBean", seLBB);
                response.setRenderParameter("SEListBoxshow", "true");
                response.setRenderParameter("prefListshow", "true");
                response.setRenderParameter("certPath", certPath);
                response.setRenderParameter("lfcHost", lfcHost);
                response.setRenderParameter("gridName", gridName);
                response.setRenderParameter("userID", userId);
                response.setRenderParameter("nextJSP", "/WEB-INF/jsp/file/fileUpload.jsp");
                //setNextState(req, "file/fileUpload.jsp");
                return;
            }
            if (upName == null || upName.equals("")) {
                // The name for the uploaded file is not given
                response.setRenderParameter("message", "Specify a name for the uploaded file");
               
                String path = "/grid/" + gridName;
                for (int i = 0; i < RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size(); i++) {
                    path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
                }
                response.setRenderParameter("path", path);
                //response.setRenderParameter("SEListBoxBean", seLBB);
                ArrayList prefList = readUserPref(userId);
                //response.setRenderParameter("prefList", prefList);
                response.setRenderParameter("SEListBoxshow", "true");
                response.setRenderParameter("prefListshow", "true");
                response.setRenderParameter("certPath", certPath);
                response.setRenderParameter("lfcHost", lfcHost);
                response.setRenderParameter("gridName", gridName);
                response.setRenderParameter("userID", userId);
                response.setRenderParameter("nextJSP", "/WEB-INF/jsp/file/fileUpload.jsp");
                //setNextState(req, "file/fileUpload.jsp");
                return;
            } // this is to store the file mode combination that the user entered for upload operation and
            // display this combination to the user later on the upload page
            String umode = "";
            String gmode = "";
            String omode = "";
            String umode2 = "";
            String gmode2 = "";
            String omode2 = "";
            File prefFile = new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId + "/uploadPerm.txt");
            if (prefFile.exists()) {
                prefFile.delete();
            }
            try {
                FileOutputStream fout = new FileOutputStream(prefFile);
                PrintStream printstream = new PrintStream(fout);
                if (urBean != null) {
                    printstream.println("1");
                } else {
                    printstream.println("0");
                }
                if (uwBean != null) {
                    printstream.println("1");
                } else {
                    printstream.println("0");
                }
                if (uxBean != null) {
                    printstream.println("1");
                } else {
                    printstream.println("0");
                }
                if (grBean != null) {
                    printstream.println("1");
                } else {
                    printstream.println("0");
                }
                if (gwBean != null) {
                    printstream.println("1");
                } else {
                    printstream.println("0");
                }
                if (gxBean != null) {
                    printstream.println("1");
                } else {
                    printstream.println("0");
                }
                if (orBean != null) {
                    printstream.println("1");
                } else {
                    printstream.println("0");
                }
                if (owBean != null) {
                    printstream.println("1");
                } else {
                    printstream.println("0");
                }
                if (oxBean != null) {
                    printstream.println("1");
                } else {
                    printstream.println("0");
                }
            } catch (IOException e) {
                // build file mode from 0s and 1s
                //user
            } //user
            if (urBean == null && uwBean == null && uxBean == null) {
                umode = "0";
                umode2 = "---";
            }
            if (urBean == null && uwBean == null && uxBean != null) {
                umode = "1";
                umode2 = "--x";
            }
            if (urBean == null && uwBean != null && uxBean == null) {
                umode = "2";
                umode2 = "-w-";
            }
            if (urBean == null && uwBean != null && uxBean != null) {
                umode = "3";
                umode2 = "-wx";
            }
            if (urBean != null && uwBean == null && uxBean == null) {
                umode = "4";
                umode2 = "r--";
            }
            if (urBean != null && uwBean == null && uxBean != null) {
                umode = "5";
                umode2 = "r-x";
            }
            if (urBean != null && uwBean != null && uxBean == null) {
                umode = "6";
                umode2 = "rw-";
            }
            if (urBean != null && uwBean != null && uxBean != null) {
                umode = "7";
                umode2 = "rwx";
            }
            if (grBean == null && gwBean == null && gxBean == null) {
                gmode = "0";
                gmode2 = "---";
            }
            if (grBean == null && gwBean == null && gxBean != null) {
                gmode = "1";
                gmode2 = "--x";
            }
            if (grBean == null && gwBean != null && gxBean == null) {
                gmode = "2";
                gmode2 = "-w-";
            }
            if (grBean == null && gwBean != null && gxBean != null) {
                gmode = "3";
                gmode2 = "-wx";
            }
            if (grBean != null && gwBean == null && gxBean == null) {
                gmode = "4";
                gmode2 = "r--";
            }
            if (grBean != null && gwBean == null && gxBean != null) {
                gmode = "5";
                gmode2 = "r-x";
            }
            if (grBean != null && gwBean != null && gxBean == null) {
                gmode = "6";
                gmode2 = "rw-";
            }
            if (grBean != null && gwBean != null && gxBean != null) {
                gmode = "7";
                gmode2 = "rwx";
            }
            if (orBean == null && owBean == null && oxBean == null) {
                omode = "0";
                omode2 = "---";
            }
            if (orBean == null && owBean == null && oxBean != null) {
                omode = "1";
                omode2 = "--x";
            }
            if (orBean == null && owBean != null && oxBean == null) {
                omode = "2";
                omode2 = "-w-";
            }
            if (orBean == null && owBean != null && oxBean != null) {
                omode = "3";
                omode2 = "-wx";
            }
            if (orBean != null && owBean == null && oxBean == null) {
                omode = "4";
                omode2 = "r--";
            }
            if (orBean != null && owBean == null && oxBean != null) {
                omode = "5";
                omode2 = "r-x";
            }
            if (orBean != null && owBean != null && oxBean == null) {
                omode = "6";
                omode2 = "rw-";
            }
            if (orBean != null && owBean != null && oxBean != null) {
                omode = "7";
                omode2 = "rwx";
            }
            String mode = umode + gmode + omode;
            for (int i = 0; i < RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size(); i++) {
                argument = argument + " " + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
            }
          
            try {
                // upload the file to the portal server firstly
                // then run lcg-cr ... command
                // delete the file from the portal server finally
                //kf = event.getFileInputBean("uploadfile");
                /* File f = new File(certPath + "/fileUploadDir/" + inputfile.getName());
                if (f.exists()) {
                    f.delete();
                }
                System.out.println("file f ");
                BufferedOutputStream fOut = null;
                try {
                    fOut = new BufferedOutputStream(new FileOutputStream(f));
                    byte[] buffer = new byte[32 * 1024];
                    int bytesRead = 0;
                    while ((bytesRead = inputfile.getInputStream().read(buffer)) != -1) {
                        fOut.write(buffer, 0, bytesRead);
                        System.out.println("upload in progress");
                    }
                } catch (Exception e) {
                    throw new IOException("file-saving  failed, got: " + e.toString());
                } finally {
                    if (inputfile.getInputStream() != null) {
                        inputfile.getInputStream().close();
                        if (fOut != null) {
                            fOut.close();
                        }
                    }
                } */
                 VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
             
                p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "fileUpload.sh " +scripts_path+" " +bdii +" "+ mode + " " + certPath + "  " + lfcHost + "  " + se + " " + upName + " " + origfile + "  " + gridName + " " + argument);
                p.waitFor();
                InputStream is = p.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                ArrayList temp = new ArrayList();
                String s;
                for (int i = 0; (s = br.readLine()) != null; i++) {
                    temp.add(s);
                }
                if (temp.get(temp.size() - 1).equals("0")) {
                    response.setRenderParameter("message", "The file is uploaded");
                    response.setRenderParameter("showErrorButton", "0");
                    SZGFileItem fItem = new SZGFileItem(upName, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem());
                    RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().addItem(fItem);
                } else {
                    // Display the Error button if the lcg-cr... command fails
                    response.setRenderParameter("message", "The file cannot be uploaded");
                    response.setRenderParameter("showErrorButton", "1");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String path = "/grid/" + gridName;
            for (int i = 0; i < RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size(); i++) {
                path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
            }
            ArrayList prefList = readUserPref(userId);
            //response.setRenderParameter("prefList", prefList);
            response.setRenderParameter("path", path);
            //response.setRenderParameter("SEListBoxBean", seLBB);
            response.setRenderParameter("SEListBoxshow", "true");
            response.setRenderParameter("prefListshow", "true");
            response.setRenderParameter("certPath", certPath);
            response.setRenderParameter("lfcHost", lfcHost);
            response.setRenderParameter("gridName", gridName);
            response.setRenderParameter("userID", userId);
            response.setRenderParameter("nextJSP", "/WEB-INF/jsp/file/fileUpload.jsp");
            //setNextState(req, "file/fileUpload.jsp");

        }
    /*  catch (FileUploadException fue)
      {
          response.setRenderParameter("full", "error.upload");
          fue.printStackTrace();
          context.log("[FileUploadPortlet] - failed to upload file - "+ fue.toString());
          return;
     }*/
     catch (Exception e)
     {
        response.setRenderParameter("full", "error.exception");
        e.printStackTrace();
        context.log("[FileUploadPortlet] - failed to upload file - "+ e.toString());
        return;
     }

     response.setRenderParameter("full", "action.succesfull");
   }
   @Override
   public void serveResource(ResourceRequest request, ResourceResponse response) throws
        PortletException, IOException
   {
       //  PortletSession ps=request.getPortletSession();

        //Download Process

                   String filelist = "";
		if (request.getParameter("selected_file") != null && !request.getParameter("selected_file").equals(new String("")) ){
                    filelist = request.getParameter("selected_file");
                }
              
		String userId = getUserName(request);
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId;
		/*event.getTextFieldBean("newName").setValue("");
		event.getTextFieldBean("dirName").setValue("");
                 *
                 */
		if( !userCertCheckMG(userId, gridName) ) {
		/*	response.setRenderParameter("message", "No valid certificate for " + gridName);
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));

			response.setRenderParameter("showErrorButton", "0");
			//response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", userId);

			//setNextState(req, "file/File.jsp");
			return;
                 *
                 */
		}

		if(gridName.equals("All") || lfcHost == null || lfcHost.equals("") ) {
		/*	response.setRenderParameter("message", "Select a VO and an LFCHOST");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", userId);

			//setNextState(req, "file/File.jsp");
			return;
                 *
                 */
		}
		String path = "/grid/"+gridName;
		String fileName = "";

		if(((filelist == null) || filelist.equals(new String("")))) { // A file is not selected from the list

			//fillFileList(getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, "");
			for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
			}
		/*	response.setRenderParameter("path", path);
			response.setRenderParameter("showFileBrowser", "1");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("message", "A file is not selected");
			response.setRenderParameter("userID", userId);

			//setNextState(req, "file/File.jsp");
                 *

			return;
                 *
                 */

                        response.getWriter().write("<br><br><table align=\"center\" width=\"100%\" bgcolor=\"lightblue\" ><tr align=\"center\"><td>");
                    response.getWriter().write("<strong>ERROR! <br><br>No file selected!<br><br> Please use \"back\" button in your browser to go back to the previous page.</strong>");
                    response.getWriter().write("</td></tr></table>");

                            return;


		}
		// extract the name and '+' or' -', indicating whether the item is a directory or a file
		String[] names = filelist.split("@");
               
		fileName = names[names.length-1];

		/*  NO DIRECTORY DOWNLOAD */

		if(names[0].charAt(0) != '-' ||  names[0].trim().length() == 0 ) { // Selected item is not a file


                            response.getWriter().write("<br><br><table align=\"center\" width=\"100%\" bgcolor=\"lightblue\" ><tr align=\"center\"><td>");
                    response.getWriter().write("<strong>ERROR! <br><br>Selected entry is not a file!<br><br> Please use \"back\" button in your browser to go back to the previous page.</strong>");
                    response.getWriter().write("</td></tr></table>");
                            
                            return;
        //response.getWriter().write("0");

			

			/*//fillFileList(getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, "");
			for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
			}
			response.setRenderParameter("path", path);
			response.setRenderParameter("showFileBrowser", "1");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("userID", userId);

			//setNextState(req, "file/File.jsp");
			return;
                         *
                         */
		}
                String tempdir = System.getProperty("java.io.tmpdir") +"/users" ;
		String argument="";
		// find the path of the selected item
		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++)
			argument = argument + " " + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		Process p;
                for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		}
		ArrayList fList = new ArrayList();
		try {

			// save the file to the portal server
			fList.add(fileName);
                         VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
                   
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "fileDownload.sh " +scripts_path+" "  +bdii +" "+userId + " "+ tempdir +" " + certPath + " " + lfcHost + "  " +  " " + gridName + " "  + path +" "+fileName);
			p.waitFor();
			///response.setRenderParameter("isDirectory", "0");
		}
		catch(Exception ex) {
                    response.setContentType("text/plain");

			response.getWriter().write("<br><br><table align=\"center\" width=\"100%\" bgcolor=\"lightblue\" ><tr align=\"center\"><td>");
                    response.getWriter().write("<strong>ERROR! <br><br>File cannot be downloaded!<br><br> Please use \"back\" button in your browser to go back to the previous page.</strong>");
                    response.getWriter().write("</td></tr></table>");
                            return;
		}

		
		RemoteFileManagerSingleton.getInstance().putFileList(userId, fList);
		/*response.setRenderParameter("path", path);
		response.setRenderParameter("fileNames", "true");
		response.setRenderParameter("fileName", fileName);

		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileDownload.jsp");
*/

                // sending file back to the browser
                File f        = new File(tempdir+"/"+userId+"/fileDownloadDir/"+fileName);
                if (!f.exists()){
                    response.setContentType("text/plain");
                    response.getWriter().write("<br><br><table align=\"center\" width=\"100%\" bgcolor=\"lightblue\" ><tr align=\"center\"><td>");
                    response.getWriter().write("<strong>ERROR! <br><br>File cannot be downloaded!<br><br> Please use \"back\" button in your browser to go back to the previous page.</strong>");
                    response.getWriter().write("</td></tr></table>");
                            return;
                }
                int  length   = 0;

        //
        //  Set the response and go!
        //
        //
        response.setContentType("application/x-download" );
        
        response.addProperty("Content-Disposition","inline;  filename="+fileName);

        response.setContentLength( (int)f.length() );
        //response.setHeader( "Content-Disposition", "attachment; filename=\"" + original_filename + "\"" );

        //
        //  Stream to the requester.
        //
        byte[] bbuf = new byte[10*1024];

        DataInputStream in = new DataInputStream(new FileInputStream(f));

        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            response.getPortletOutputStream().write(bbuf,0,length);
        }

        in.close();
        response.getPortletOutputStream().flush();
        response.getPortletOutputStream().close();








            //END of Download Process



       // FileUploadProgressListener listener=(FileUploadProgressListener)
//((PortletFileUpload)ps.getAttribute("upload",ps.APPLICATION_SCOPE)).getProgressListener();
  //      response.getWriter().write(lisener.getFileuploadstatus());
     
   }

private ArrayList readUserPref(String userId) {
		// return the file mode combination that the user has selected  to upload a file previously
		// this combination is written in the file: portal_work/users/<userid>/uploadPerm.txt when the user uploads a file
		// the default combination is rwxrwxr-- if that file does not exist yet
		ArrayList prefList = new ArrayList();
		File prefFile = new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId + "/uploadPerm.txt");
		if(!prefFile.exists()) {
			prefList.add("1");
			prefList.add("1");
			prefList.add("0");
			prefList.add("1");
			prefList.add("1");
			prefList.add("0");
			prefList.add("1");
			prefList.add("0");
			prefList.add("0");
		}
		else {
			try{
                FileReader fr = new FileReader(prefFile);
				BufferedReader br = new BufferedReader(fr);
				String s;
                while ((s = br.readLine()) != null){
                    prefList.add(s);
                }
            }
			catch(IOException ioe) {}
		}
		return prefList;
	}

	public synchronized void doGoUpload(ActionRequest request,ActionResponse response) {
		// the action to go to  the upload page
		String filelist = "";
		if (request.getParameter("fileLsList_select") != null){
                    filelist = request.getParameter("fileLsList_select");
                }


		String userId = getUserName(request);
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId;
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		/*ListBoxBean seLBB = new ListBoxBean();
		event.getTextFieldBean("newName").setValue("");
		event.getTextFieldBean("dirName").setValue("");
                 *
                 */
		if( !userCertCheckMG(userId, gridName) ) {
			response.setRenderParameter("message", "No valid certificate for " + gridName);
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));

			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", userId);

			//setNextState(req, "file/File.jsp");
			return;
		}
		if(gridName.equals("All")  ||  lfcHost == null || lfcHost.equals("")) {
			response.setRenderParameter("message", "Select a VO and an LFCHOST ");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", userId);

			//setNextState(req, "file/File.jsp");
			return;
		}
		String path = "/grid/"+gridName;
		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		}
		response.setRenderParameter("SEListBoxshow","true");
		response.setRenderParameter("prefListshow","true");
		response.setRenderParameter("certPath",certPath);
		response.setRenderParameter("lfcHost",lfcHost);
		response.setRenderParameter("gridName",gridName);
		//response.setRenderParameter("SEListBoxBean", seLBB);
		response.setRenderParameter("path", path);
		/*ArrayList prefList = readUserPref(userId);
		response.setRenderParameter("prefList", prefList);
		*/
		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileUpload.jsp");
                UserDataStorage.getInstance().getUserData(getUserName(request)).setSelectedVO(request.getParameter("VOListBoxBean_select"));

		//setNextState(req, "file/File.jsp");
		//setNextState(req, "file/fileUpload.jsp");
	}

        public synchronized void doGoDownload(ActionRequest request,ActionResponse response) {

                String filelist = "";
		if (request.getParameter("fileLsList_select") != null){
                    filelist = request.getParameter("fileLsList_select");
                }

		String userId = getUserName(request);
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId;
		/*event.getTextFieldBean("newName").setValue("");
		event.getTextFieldBean("dirName").setValue("");
                 *
                 */
		if( !userCertCheckMG(userId, gridName) ) {
			response.setRenderParameter("message", "No valid certificate for " + gridName);
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));

			response.setRenderParameter("showErrorButton", "0");
			//response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", userId);

			//setNextState(req, "file/File.jsp");
			return;
		}

		if(gridName.equals("All") || lfcHost == null || lfcHost.equals("") ) {
			response.setRenderParameter("message", "Select a VO and an LFCHOST");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", userId);

			//setNextState(req, "file/File.jsp");
			return;
		}
		String path = "/grid/"+gridName;
		String fileName = "";

		if(((filelist == null) || filelist.equals(new String("")))) { // A file is not selected from the list

			//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, "");
			for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
			}
			response.setRenderParameter("path", path);
			response.setRenderParameter("showFileBrowser", "1");
                        response.setRenderParameter("showlastselected", "1");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("message", "A file is not selected");
			response.setRenderParameter("userID", userId);
			
			//setNextState(req, "file/File.jsp");
			return;
		}
		// extract the name and '+' or' -', indicating whether the item is a directory or a file
		String[] names = filelist.split("@");
		fileName = names[names.length-1];

		/*  NO DIRECTORY DOWNLOAD */

		if(names[0].charAt(0) != '-' ||  names[0].trim().length() == 0 ) { // Selected item is not a file
			response.setRenderParameter("message", "Selected entry is not a file");

			//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, "");
			for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
			}
			response.setRenderParameter("path", path);
			response.setRenderParameter("showFileBrowser", "1");
                        response.setRenderParameter("showlastselected", "1");
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("userID", userId);

			//setNextState(req, "file/File.jsp");
			return;
		}
		String argument="";
		// find the path of the selected item
		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++)
			argument = argument + " " + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		Process p;
		ArrayList fList = new ArrayList();
		try {
                    String tempdir = System.getProperty("java.io.tmpdir");
			// save the file to the portal server
			fList.add(fileName);
                         VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
                       
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "fileDownload.sh " +scripts_path+" "  +bdii +" "+ certPath + " " + lfcHost + "  " + userId + " " + gridName + " " + argument + " " + tempdir +" "+fileName);
			p.waitFor();
			response.setRenderParameter("isDirectory", "0");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
				path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		}
                UserDataStorage.getInstance().getUserData(getUserName(request)).setSelectedVO(request.getParameter("VOListBoxBean_select"));
		RemoteFileManagerSingleton.getInstance().putFileList(userId, fList);
		response.setRenderParameter("path", path);
		response.setRenderParameter("fileNames", "true");
		response.setRenderParameter("fileName", fileName);

		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileDownload.jsp");


        //setNextState(req, "file/fileDownload.jsp");
	}


public synchronized void doGoBackUpload(ActionRequest request,ActionResponse response) {
		// the action for going back to upload page (fileUpload.jsp)

		String userId = getUserName(request);
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		String certPath = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId;
		String path = "/grid/"+gridName;
              /*  ArrayList<String> browserpath = new ArrayList<String>();
		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
                      String foldername = (String)RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		      path = path + "/" + foldername;
                      browserpath.add(foldername);

		}


                RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).setSelectedItems(browserpath);
               *
               */

                path +=this.updateCurrentBrowser(userId,"cancel");
		//seLBB = listSE(certPath, lfcHost, gridName);


		//response.setRenderParameter("SEListBoxBean", seLBB);
		ArrayList prefList = readUserPref(userId);
		//response.setRenderParameter("prefList", prefList);
		response.setRenderParameter("path", path);
                response.setRenderParameter("selectedVO",this.getSelectedVOForUser(userId));
		response.setRenderParameter("SEListBoxshow","true");
		response.setRenderParameter("prefListshow","true");
		response.setRenderParameter("certPath",certPath);
		response.setRenderParameter("lfcHost",lfcHost);
		response.setRenderParameter("gridName",gridName);
                response.setRenderParameter("showFileBrowser", "1");

                response.setRenderParameter("showlastselected", "1");
		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");

		//response.setRenderParameter("path", path);
		//setNextState(req, "file/fileUpload.jsp");
	}
		public synchronized void doShowRemoveError(ActionRequest request,ActionResponse response) {
		// the action for the Error button onfilke/directory deletion page, (fileDelete.jsp)
		// displays the contents of the log file, which is under pgportal/portal_work/uesrs/<userid>/fileOperations.log

		String userId = getUserName(request);
		FileBean fBean = getInitedFileBean(request);
		String fileName = request.getParameter("itemName");

		response.setRenderParameter("fileName", fileName);
		//response.setRenderParameter("fileName", fileName );
		File logFile = new File( PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId + "/" + "fileOperations.log" );
		fBean.setLogFile(logFile);
                UserDataStorage.getInstance().getUserData(getUserName(request)).setSelectedVO(request.getParameter("VOListBoxBean_select"));
		response.setRenderParameter("message", "Log file successfully displayed.");
		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileLogViewRemove.jsp");

		//setNextState(req, "file/fileLogViewRemove.jsp");
	}
	public synchronized void doShowError(ActionRequest request,ActionResponse response) {
		// the action for the "Error" button on File.jsp page
		// displays the contents of the log file, which is under pgportal/portal_work/uesrs/<userid>/fileOperations.log

		String userId = getUserName(request);
		FileBean fBean = getInitedFileBean(request);
		File logFile = new File( PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId + "/" + "fileOperations.log" );
		fBean.setLogFile(logFile);
                UserDataStorage.getInstance().getUserData(getUserName(request)).setSelectedVO(request.getParameter("VOListBoxBean_select"));
		response.setRenderParameter("message", "Log file successfully displayed.");
		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/fileLogView.jsp");


		//setNextState(req, "file/fileLogView.jsp");
	}

	public synchronized void doShowRepError(ActionRequest request,ActionResponse response) {
		// the action for the Error button on replica management page, (fileReplicas.jsp)
		// displays the contents of the log file, which is under pgportal/portal_work/uesrs/<userid>/fileOperations.log

		String userId = getUserName(request);
		FileBean fBean = getInitedFileBean(request);
		String fileName = request.getParameter("fileName");

		response.setRenderParameter("fileName", fileName);
		//response.setRenderParameter("fileName", fileName );
		File logFile = new File( PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users" + "/" + userId + "/" + "fileOperations.log" );
		fBean.setLogFile(logFile);
		response.setRenderParameter("message", "Log file successfully displayed.");
		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","WEB-INF/jsp/file/fileLogViewRep.jsp");


		//setNextState(req, "file/fileLogViewRep.jsp");
	}

public void doGoUp(ActionRequest request, ActionResponse response) { //list the previous directory
		String filelist="";
		if (request.getParameter("fileLsList_select") != null){
                    filelist = request.getParameter("fileLsList_select");
                }
		String userId = getUserName(request);
		String lfcHost = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLfcHost();
		String gridName = RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getGridName();
		String path = "/grid/"+gridName;
		//event.getTextFieldBean("dirName").setValue("");
		//event.getTextFieldBean("newName").setValue("");
		if( !userCertCheckMG(userId, gridName) ) {
                    response.setRenderParameter("message",  "No valid certificate for " + gridName);
			//response.setRenderParameter("message", "No valid certificate for " + gridName, getInitedFileBean(req));
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			//fileList.clear();
			response.setRenderParameter("showFileBrowser", "0");
			response.setRenderParameter("userID", userId);
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                        response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));
			//setNextState(req, "doView");
			//setNextState(req, "file/File.jsp");
			return;
		}

		if(gridName.equals("All") ||  lfcHost.equals("") ) {
			return;
		}

		SZGDirectoryItem pItem = (SZGDirectoryItem) RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getParent();
		if (pItem ==  null) {
			//cannot go up from root item
			//fillFileList(RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getItems(), fileList, "");
			response.setRenderParameter("path", path );
			response.setRenderParameter("showFileBrowser", "1");
                        response.setRenderParameter("message",   "At the root directory");
			//response.setRenderParameter("message", "At the root directory", getInitedFileBean(req));
			viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
			response.setRenderParameter("userID", userId);
			response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                        response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));

			//setNextState(req, "file/File.jsp");
			return;
		}
		// fill file list with the items of lastselected item
		//fillFileList(pItem.getItems(), fileList, RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getLastSelectedItem().getName());
                response.setRenderParameter("showlastselected", "1");
		//update last selected item
		RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).setLastSelectedItem(pItem);
		RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().remove( (RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems()).size()-1);

		for(int i=0;i<RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().size();i++) {
			path = path + "/" + RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getSelectedItems().get(i);
		}
		response.setRenderParameter("path", path );
		response.setRenderParameter("showFileBrowser", "1");
                response.setRenderParameter("message", "Upper directory listed");
		//response.setRenderParameter("message", "Upper directory listed", getInitedFileBean(req));
		viewGridResources(request, getSelectedGridForUser(userId), getSelectedVOForUser(userId));
		response.setRenderParameter("userID", userId);
		response.setRenderParameter("nextJSP","/WEB-INF/jsp/file/File.jsp");
                response.setRenderParameter("selectedVO",request.getParameter("VOListBoxBean_select"));

		//setNextState(req, "file/File.jsp");
	}

private ArrayList listSE(String certPath, String lfcHost, String gridName) {
	// list storage elements of a VO
		ArrayList<String> return_seList = new ArrayList<String>();
                 VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
		Process p;
		try {
                
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "seList.sh " +scripts_path+" "  +bdii +" "+ certPath + "  " + lfcHost + "  " + gridName);
			p.waitFor();
			InputStream is = p.getInputStream();
       		InputStreamReader isr = new InputStreamReader(is);
       		BufferedReader br = new BufferedReader(isr);
			String s;
			br.readLine();
			br.readLine();

			while ((s = br.readLine()) != null){
				String[] seList = s.split("\\s");
				String name = seList[seList.length-1];
				return_seList.add(name);

			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return return_seList;
	}

	private ArrayList listReplicas(String certPath, String lfcHost, String gridName, String path, String fileName) {
		// list replicas of the involved file
		ArrayList replicaList = new ArrayList();
                 VOInfoRequester inforeq = new VOInfoRequester();
                String bdii = inforeq.getInfos(gridName).getBdii();
		try {
			Process p;
			p = Runtime.getRuntime().exec("/bin/bash " +scripts_path + "listReplicas.sh  "  +scripts_path+" " +bdii +" "+ certPath + "  " + lfcHost + "  " + gridName + "  " + path + " " + fileName);
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String s;
			while ((s = br.readLine()) != null) {
				replicaList.add(s);}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return replicaList;
	}
private void insertRootItems(String s, String userId) {
		// string s contains the output of lfc-ls -l ...
		// this function splits s and extracts name and directory/file information from s
		// directoryitems or fileitems are formed accordingly

		String[] itemProperties = s.split("\\s");
		String name = itemProperties[itemProperties.length-1];
		String accessRights = itemProperties[0];
		if (accessRights.charAt(0) == 'd') {
			SZGDirectoryItem dItem = new SZGDirectoryItem(name, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getRootItem());
			RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getRootItem().addItem(dItem);
		}
		else if (accessRights.charAt(0) == '-') {
			SZGFileItem fItem = new SZGFileItem(name, "", RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getRootItem());
			RemoteFileManagerSingleton.getInstance().getCurrentBrowser(userId).getRootItem().addItem(fItem);
		}
	}
private ArrayList<String> formatFileList(ArrayList aList) {
		// fileList listbox is filled with the contents of aList
		// '+' is added to the beginning of directories, '-' is added to the beginning of files
		// selectedName is used to store last selection on the list
    ArrayList<String> return_list = new ArrayList<String>();
		for(int i=0; i<aList.size(); i++) {
			SZGItem x = (SZGItem) aList.get(i);
                        String value ="";
			if (x.isDir()) {
				value = "+ " + x.getName();

			}
			else {
				value = "- " + x.getName();
			}
                    return_list.add(value);
		}
                return return_list;
	}
private boolean userCertCheckMG(String username, String gridName) {
    /* function to check for a valid certificate for the selected VO */
	/*
	The certificate mapped to <VO> <VO_LCG_2_BROKER> <VO_GLITE_BROKER> are tried to be used in the given order.
	If a certificate mapped to <VONAME> exists, this certificate is used. Others will not be considered.
	If a certificate mapped to <VONAME> does not exist, the certificate mapped to <VO_LCG_2_BROKER> will be used if it exists.
	If not, the certificate mapped to <VO_GLITE_BROKER> will be considered finally.
	*/
		SZGCredential c, c2, c3;
        SZGCredentialManager cm = SZGCredentialManager.getInstance();

     

        c = cm.getCredentialForGrid(username, gridName);
        c2 = cm.getCredentialForGrid(username, gridName+"_LCG_2_BROKER");
		c3 = cm.getCredentialForGrid(username, gridName+"_GLITE_BROKER");
		if(c == null) {
			if(c2 == null) {
            	if(c3 == null)
					return false;
				try {
					return c3.getTimeLeftInSeconds() > 0L;
				}
				catch(Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
        	try {
        		return c2.getTimeLeftInSeconds() > 0L;
        	}
			catch(Exception ex) {
        		ex.printStackTrace();
        		return false;
			}
		}

        try {
       			return c.getTimeLeftInSeconds() > 0L;
        }
		catch(Exception ex) {
        	ex.printStackTrace();
        	return false;
		}
    }

	private String loadUsrCert(String usr) throws Exception {

		SZGCredentialManager cm = SZGCredentialManager.getInstance();

		String usrDir = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "/users/" + usr + "/";
		File uDir = new File(usrDir);
		if (!uDir.exists())
			if (!uDir.mkdirs())
				return null;
		FileReader fin = new FileReader(usrDir + usr);
		BufferedReader in = new BufferedReader(fin);


			try {
				String sor = new String(" ");
				while ((sor = in.readLine()) != null) {
					int indx = 0;
					int indv = sor.indexOf(";", indx);
					String Id = new String(sor.substring(indx, indv));
					indx = indv + 1;
					indv = sor.indexOf(";", indx);
					String DownloadedFrom = new String(sor
							.substring(indx, indv));
					indx = indv + 1;
					indv = sor.indexOf(";", indx);
					String TimeLeft = new String(sor.substring(indx, indv));
					indx = indv + 1;
					indv = sor.indexOf(";#", indx);
					String Description = new String(sor.substring(indx, indv));
					indx = indv + 2;
					indv = sor.indexOf(";", indx);
					String gsVal = new String(sor.substring(indx, indv));
					
					SZGStoreKey key = new SZGStoreKey(usr, Id);
					String cfp;
					if (0 == gsVal.compareTo(" ")) {
						
						cfp = new String(usrDir + "x509up");
						InputStream crinstr = new FileInputStream(cfp);
						cm.loadFromFile(crinstr, DownloadedFrom, Integer
								.parseInt(TimeLeft), key, Description);
					} else {
						
						cfp = new String(usrDir + "x509up." + gsVal.trim());
						InputStream crinstr = new FileInputStream(cfp);
						cm.loadFromFile(crinstr, DownloadedFrom, Integer
								.parseInt(TimeLeft), key, Description);
						while (indv != -1) {
							cm.setCredentialForGrid(usr, Id, gsVal.trim());
							indx = indv + 1;
							indv = sor.indexOf(";", indx);
							if (indv != -1)
								gsVal = new String(sor.substring(indx, indv));
						}
					}
				}
				in.close();
			} catch (Exception e) {
				System.out.println("loadUsrCert ERROR:" + e);
			}

		return " ";
	}
   private FileBean getInitedFileBean(PortletRequest req) {
       	FileBean fBean = new FileBean(this);
       	/*Map userInfo = (Map) req.getAttribute(PortletRequest.USER_INFO);
		String name = (userInfo != null) ? (String)userInfo.get("user.name") : "";
       	 *
       	 */

       	fBean.setUsername(getUserName(req));
       	RemoteFileManagerSingleton.getInstance().putFileBean(getUserName(req),fBean);
       	return fBean;
    }
   private ArrayList<String> prepareVOListBoxBean(LCGGrid lcgGrid, String selectedVO) {
        //LCGGrid lcgGrid = LCGInformationSystem.getInstance().getGridInfo(selectedGridName);
		LCGVO lcgVO[] = lcgGrid.getVOList();
        ArrayList<String> voLBIB = new ArrayList<String>();

        //voLBIB.add("All");
        if(lcgVO != null) {
            for(int i = 0; i < lcgVO.length; i++) {
     		   voLBIB.add(lcgVO[i].getVOName());
                if(lcgVO[i].getVOName().equals(selectedVO))
                    //voLBIB.setSelected(true);
                System.out.println("voname " + lcgVO[i].getVOName() + "selected");
            }
        }
        return voLBIB;
    }

private void viewGridResources(PortletRequest request, String selectedGenuineGridName, String selectedVO)  {
        if(selectedVO == null)
			selectedVO = "";
       	ArrayList<String> gridListBoxBean = getgridListBoxBean(selectedGenuineGridName);

        ArrayList<String> voListBoxBean = new ArrayList<String>();
        String selectedGridName = assocNameToGenuineGridName(selectedGenuineGridName);
		if(selectedGridName.equals("")){
                   
                    request.setAttribute("message", "No LCG type grid is specified.");
			//response.setRenderParameter("message", "No LCG type grid is specified.", getInitedFileBean(req));
                }
		else {
			if(LCGInformationSystem.getInstance().getLcg2Enabled()) {
				LCGGrid lcgGrid = LCGInformationSystem.getInstance().getGridInfo(selectedGridName);
				if(LCGInformationSystem.getInstance().isExecutedFirstTime(selectedGridName)) {
					if(lcgGrid.getIsDataAvailable()) {
                                            voListBoxBean = prepareVOListBoxBean(lcgGrid, selectedVO);
						
					} else {
                                          
                                             request.setAttribute("message", "Cannot contact the BDII server.");
						//response.setRenderParameter("message", "Cannot contact the BDII server." , getInitedFileBean(req));
					}
				}else {
                                    
                                     request.setAttribute("message", "Resources from BDII are still not available, try later.");
					//response.setRenderParameter("message", "Resources from BDII are still not available, try later.", getInitedFileBean(req));
				}
			}
		}

		RemoteFileManagerSingleton.getInstance().putgridListBoxBean(this.getUserName(request), gridListBoxBean);
               

                /*for (int i=0;i<RemoteFileManagerSingleton.getInstance().getgridListBoxBean(this.getUserName(request)).size();++i){
                    System.out.println(RemoteFileManagerSingleton.getInstance().getgridListBoxBean(this.getUserName(request)).get(i));
                }*/
		RemoteFileManagerSingleton.getInstance().putvoListBoxBean(this.getUserName(request), voListBoxBean);
		/*response.setRenderParameter("gridListBoxBean", gridListBoxBean);
        response.setRenderParameter("voListBoxBean", voListBoxBean);
        */
    }
    
     private void setSelectedGridForUser(String userId, String selectedGrid) {
		UserDataStorage.getInstance().setSelectedGrid(userId, selectedGrid);
        setSelectedVOForUser(userId, "");
    }
private void setSelectedVOForUser(String userId, String selectedVO) {
        UserDataStorage uDS = UserDataStorage.getInstance();
        uDS.setSelectedVO(userId, selectedVO);
}
  private String getSelectedVOForUser(String userId) {

        UserData ud = UserDataStorage.getInstance().getUserData(userId);

        String selectedVO;
       // if(hasGridListElement()) {
            if(ud != null) {
                selectedVO = ud.getSelectedVO();
               /*if(selectedVO == null)
                    selectedVO = "All";
                else
                    if(selectedVO.equals(""))
                    	selectedVO = "All";
                *
                */
        	}
			else {
        	    //selectedVO = "All";}
                selectedVO = "";}
     /*   }
		else {
        	selectedVO = "";}
      *
      */
        return selectedVO;
	}




    private String getSelectedGridForUser(String userId) {
 	    UserData ud = UserDataStorage.getInstance().getUserData(userId);
       	GridConfiguration gc[] = GridConfigs.getInstance().getGridConfigs();
       	String selectedGrid;
       	if(gc != null) {
       		if(gc.length != 0) {
           		if(ud != null) {
	        		selectedGrid = ud.getSelectedGrid();
       		    	if(selectedGrid.equals(""))
       		    		selectedGrid = getFirstGridFromGridList();
           			else
               			if(!isGridListContainElement(selectedGrid))
                   			selectedGrid = getFirstGridFromGridList();
                }
			    else {
           			selectedGrid = getFirstGridFromGridList();}
            }
			else {
           		selectedGrid = "";}
        	}
	  	else {
            selectedGrid = ""; }
		return selectedGrid;
	}

	private boolean hasGridListElement() {
        	GridConfiguration gc[] = GridConfigs.getInstance().getGridConfigs();
        	if(gc != null) {
            		if(gc.length > 0) {
                		for(int i = 0; i < gc.length; i++)
                    			if(gc[i].getISType().equals("LCG2"))
                        			return true;
                		return false;
            		}
			else {
                	return false;
            	}
        	}
		else {
            		return false;
        }
    }

	private boolean isGridListContainElement(String gridName) {
	        GridConfiguration gc[] = GridConfigs.getInstance().getGridConfigs();
        	if(gc != null) {
        		for(int i = 0; i < gc.length; i++)
                		if(gc[i].getISType().equals("LCG2") && gc[i].getGenuineGridName().equals(gridName))
                    			return true;
        	}
        	return false;
    }

	private String getFirstGridFromGridList() {
        	GridConfiguration gc[] = GridConfigs.getInstance().getGridConfigs();
        	if(gc != null) {
            		for(int i = 0; i < gc.length; i++)
                		if(gc[i].getISType().equals("LCG2"))
                    			return gc[i].getGenuineGridName();
        	}
        	return "";
    }


        private ArrayList<String>
                getgridListBoxBean(String gridName) {
        ArrayList<String> gLB = new ArrayList<String>();
        GridConfiguration gc[] = GridConfigs.getInstance().getGridConfigs();
        if(gc != null) {
   	        for(int i = 0; i < gc.length; i++) {
                if(!gc[i].getISType().equals("LCG2"))
                    continue;
                String gLBI = gc[i].getGenuineGridName();
                if(gc[i].getGenuineGridName().equals(gridName))
                    //gLBI.setSelected(true);
                gLB.add(gLBI);
            }
        }
        return gLB;
    }

	private String assocNameToGenuineGridName(String genuineGridName) {
        String ret = "";
        GridConfiguration gc[] = GridConfigs.getInstance().getGridConfigs();
        if(gc != null && gc.length > 0) {
            int i = 0;
            do {
	            if(i >= gc.length)
        	        break;
                if(gc[i].getGenuineGridName().compareTo(genuineGridName) == 0) {
                    ret = gc[i].getName();
                    break;
                }
                i++;
            } while(true);
        }
        return ret;
    }
}


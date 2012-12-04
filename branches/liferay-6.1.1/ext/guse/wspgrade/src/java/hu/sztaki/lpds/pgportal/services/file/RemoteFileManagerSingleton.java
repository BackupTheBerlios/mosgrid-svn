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

package hu.sztaki.lpds.pgportal.services.file;

import hu.sztaki.lpds.pgportal.portlets.file.FileBean;
import hu.sztaki.lpds.pgportal.portlets.file.SZGFileBrowser;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author akos
 */
public class RemoteFileManagerSingleton {





    private RemoteFileManagerSingleton() {
         currentBrowsers = new Hashtable();
         fileBeanTable = new Hashtable<String,FileBean>();
	 gridListBoxBeanTable = new Hashtable<String,ArrayList<String>>();
	 voListBoxBeanTable = new Hashtable<String,ArrayList<String>>();
	 fileListTable = new Hashtable<String,ArrayList>();
   }
   public static RemoteFileManagerSingleton getInstance() {
      if(instance == null) {
         instance = new RemoteFileManagerSingleton();
      }
      return instance;
   }

        private static RemoteFileManagerSingleton instance = null;
	private Hashtable currentBrowsers;
	private Hashtable<String, FileBean> fileBeanTable = null;

	private Hashtable<String,ArrayList<String>> gridListBoxBeanTable = null;
        private Hashtable<String,ArrayList<String>> voListBoxBeanTable = null;
        private Hashtable<String,ArrayList> fileListTable = null;


    	public void putFileList(String userId,ArrayList fList){
	 if (fileListTable == null){
                fileListTable = new Hashtable<String,ArrayList>();
            }
            fileListTable.put(userId, fList);
	}
	public ArrayList getgFileList(String userId){
            if (fileListTable == null){
                fileListTable = new Hashtable<String,ArrayList>();
            }
		return fileListTable.get(userId);
	}


	public void putgridListBoxBean(String userId,ArrayList<String> fbean){
            if (gridListBoxBeanTable == null){
                gridListBoxBeanTable = new Hashtable<String,ArrayList<String>>();
            }
            if (fbean == null){
                ArrayList<String> tempbean = new ArrayList<String>();
                gridListBoxBeanTable.put(userId, tempbean);
            }
            else {
                gridListBoxBeanTable.put(userId, fbean);
            }
		
	}
	public ArrayList<String> getgridListBoxBean(String userId){
            if (gridListBoxBeanTable == null){
                gridListBoxBeanTable = new Hashtable<String,ArrayList<String>>();
            }
            if ( gridListBoxBeanTable.get(userId) != null)
		return gridListBoxBeanTable.get(userId);
            else return null;
	}
	public void putvoListBoxBean(String userId,ArrayList<String> fbean){
	 if (voListBoxBeanTable == null){
                 voListBoxBeanTable = new Hashtable<String,ArrayList<String>>();
            }
            voListBoxBeanTable.put(userId, fbean);
	}
	public ArrayList<String> getvoListBoxBean(String userId){

             if (voListBoxBeanTable == null){
                 voListBoxBeanTable = new Hashtable<String,ArrayList<String>>();
            }
            return voListBoxBeanTable.get(userId);
	}


	public void putFileBean(String userId,FileBean fbean){
            if(fileBeanTable == null){
                fileBeanTable = new Hashtable<String,FileBean>();
            }
            fileBeanTable.put(userId, fbean);
	}
	public FileBean getFileBean(String userId){
            if(fileBeanTable == null){
                fileBeanTable = new Hashtable<String,FileBean>();
            }
            return fileBeanTable.get(userId);
	}

	public void setCurrentBrowser(String username, SZGFileBrowser fb)
    {
            if(currentBrowsers == null){
                currentBrowsers = new Hashtable();
            }
        if(fb != null)
            currentBrowsers.put(username, fb);
    }

    public SZGFileBrowser getCurrentBrowser(String username)
    { if(currentBrowsers == null){
                currentBrowsers = new Hashtable();
            }
        return (SZGFileBrowser)currentBrowsers.get(username);
    }




}

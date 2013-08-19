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
// SZGDirectoryItem.java v1.0
// Birsen Omay

//Directory structure for the file browser

package hu.sztaki.lpds.pgportal.portlets.file;

import java.util.ArrayList;

public class SZGDirectoryItem extends SZGItem {

    public ArrayList items;
 
    public SZGDirectoryItem(String name, String pathName, SZGItem parent) {
		super(name,pathName,parent);
        items = new ArrayList();
    }
 
	public boolean isDir() {
        return true;
    }
	
	public ArrayList getItems() {
		return items;
	}
	  
	protected void setItems(ArrayList a) {
		items = a;
	}

    public boolean isRoot(){
        return parent == null;
    }

    protected void addItem(SZGItem newItem){
        items.add(newItem);
    }

	protected void removeItem(SZGItem newItem){
		int i;
		for(i=0;i<items.size();i++) {
			SZGItem x = (SZGItem) items.get(i);
			if( (x.getName()).equals(newItem.getName()) )					
				break;
		}	
            items.remove(i);
    }

    public boolean deneme() {
		return true;		
	}

    //Tests if the directory has specified subdirectory.
    public boolean hasSubDirectory(SZGDirectoryItem subDir){
        if(subDir.getPathName().equals(pathName))
            return true;
        SZGDirectoryItem di = subDir;
        while(!di.isRoot()){
            di = (SZGDirectoryItem)di.getParent();
            if(di.getPathName().equals(pathName))
                return true;
            }
        return false;
    }

}


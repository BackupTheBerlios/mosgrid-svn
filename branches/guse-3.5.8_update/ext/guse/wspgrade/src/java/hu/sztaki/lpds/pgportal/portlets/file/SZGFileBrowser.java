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
// SZGFileBrowser.java v1.0
// Birsen Omay

// File browser structure
// Filebrowser is a tree of SZGItems, having /grid/VO as the root (rootDirItem)
// lastSelectedItem is the last selected directory item
// selectedItems array contains the traversed directories

package hu.sztaki.lpds.pgportal.portlets.file;

import java.util.ArrayList;

public class SZGFileBrowser {
	private SZGDirectoryItem rootDirItem;
	private SZGDirectoryItem lastSelectedItem;
	private ArrayList selectedItems;
	private String gridName;
	private String lfcHost;
	private boolean isFromTextField; 
	
	private String userID;

	public SZGFileBrowser(String userID) {
		selectedItems = null;
		rootDirItem = null;
		lastSelectedItem = null;
		isFromTextField = false;
		this.userID = null;
        this.userID = userID;
    }
	
	public SZGFileBrowser() {
		selectedItems = null;
		rootDirItem = null;
		lastSelectedItem = null;
		isFromTextField = false;
    }
	
	public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }
	
	public void setIsFromTextField(boolean b) {
        this.isFromTextField = b;
    }

    public boolean getIsFromTextField() {
        return isFromTextField;
    }
	
	public void setRootItem(SZGDirectoryItem root) {
        this.rootDirItem = root;
    }

    public SZGDirectoryItem getRootItem() {
        return rootDirItem;
    }
	
	public void setLastSelectedItem(SZGDirectoryItem last) {
        this.lastSelectedItem = last;
    }

    public SZGDirectoryItem getLastSelectedItem() {
        return lastSelectedItem;
    }
	
	public void setSelectedItems(ArrayList selecteds) {
        this.selectedItems = selecteds;
    }

    public ArrayList getSelectedItems() {
        return selectedItems;
    }
	
	public void setLfcHost(String lfcHost) {
        this.lfcHost = lfcHost;
    }

    public String getLfcHost() {
        return lfcHost;
    }
	
	public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public String getGridName() {
        return gridName;
    }

}


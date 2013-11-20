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
// FileBean.java v1.0
// Birsen Omay

package hu.sztaki.lpds.pgportal.portlets.file;
import java.io.File;
//import hu.sztaki.lpds.pgportal.services.utils.LogFileUtil;
//import java.io.PrintStream;

public class FileBean {

    public FileBean(LFCFileStoragePortlet fPortlet) {
        message = null;
        username = null;
        jspToGo = null;
		logFile = null;
        this.filePortlet = null;
        this.filePortlet = fPortlet;
    }

    public FileBean() {
        message = null;
        username = null;
        jspToGo = null;
        filePortlet = null;
		logFile = null;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setJSPToGo(String jspName) {
		jspToGo = jspName;
    }

    public String getJSPToGo() {
        return jspToGo;
    }
		
	public void setLogFile(File lf) {
		logFile = lf;
	}
/*
    public String[] getLogLines() {
		return LogFileUtil.readInLogFile(logFile);
	}
*/
    private String message;
	private File logFile;
    private String username;
    private String jspToGo;
    private LFCFileStoragePortlet filePortlet;
}


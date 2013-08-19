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

package hu.sztaki.lpds.pgportal.wfeditor.net.xmlrpc.server;

import hu.sztaki.lpds.pgportal.wfeditor.net.xmlrpc.common.WEWFSService;

/**
 * @author lpds
 */
public class WEWFSServiceImplHandler implements WEWFSService {
    
    public WEWFSServiceImplHandler() {
    }
    
    public String saveWorkflow(boolean newWorkflow, String portalid, String userid, String wfname, String wfxml) {
        // System.out.println("now:" + System.currentTimeMillis());
        // System.out.println("saveWorkflow...");
        // System.out.println("newWorkflow: " + newWorkflow);
        // System.out.println("portalid: " + portalid);
        // System.out.println("userid: " + userid);
        // System.out.println("wfname: " + wfname);
        // System.out.println("wfxml: " + wfxml);
        WEWFSServiceImpl wfsServiceImpl = new WEWFSServiceImpl();
        XMLParser xmlparser = new XMLParser(wfsServiceImpl);
        XMLBuilder xmlbuilder = new XMLBuilder(wfsServiceImpl);
        xmlparser.parseXMLStr(newWorkflow, portalid, userid, wfname, wfxml);
        String newwfxml = xmlbuilder.buildXMLStr(portalid, userid, wfname);
        // System.out.println("now:" + System.currentTimeMillis());
        // mindig meg kell hivni...
        wfsServiceImpl.closeConnection();
        return newwfxml;
    }
    
    public String getWorkflow(String portalid, String userid, String wfname) {
        // System.out.println("now:" + System.currentTimeMillis());
        // System.out.println("getWorkflow...");
        // System.out.println("portalid: " + portalid);
        // System.out.println("userid: " + userid);
        // System.out.println("wfname: " + wfname);
        WEWFSServiceImpl wfsServiceImpl = new WEWFSServiceImpl();
        XMLBuilder xmlbuilder = new XMLBuilder(wfsServiceImpl);
        String retObj = xmlbuilder.buildXMLStr(portalid, userid, wfname);
        // System.out.println("now:" + System.currentTimeMillis());
        // mindig meg kell hivni...
        wfsServiceImpl.closeConnection();
        return retObj;
    }
    
}

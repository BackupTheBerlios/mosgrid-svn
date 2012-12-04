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

/**
 * Graph Editor (Workflow Editor) portal oldali service osztalya
 *
 * @author lpds
 */
public class WEPortalServiceImplHandler implements WEPortalService {
    
    private WEPortalServiceImpl portalServiceImpl;
    
    public WEPortalServiceImplHandler() {
        portalServiceImpl = new WEPortalServiceImpl();
    }
    
    /**
     * Egy megadott user osszes workflow-janak nevet adja vissza.
     *
     * (key: workflow name, value wfs service url)
     */
    public byte[] getMyWorkflowList(String username) {
        // System.out.println("getMyWorkflowList...");
        // System.out.println("username: " + username);
        byte[] retObj = portalServiceImpl.getMyWorkflowList(username);
        return retObj;
    }
    
    /**
     * Megallapitja hogy a megadott workflow nev foglalt e vagy sem.
     */
    public Boolean isNewWorkflow(String username, String newwfname, String wftext, String wfsid) {
        // System.out.println("isNewWorkflow...");
        // System.out.println("username   : " + username);
        // System.out.println("newwfname  : " + newwfname);
        // System.out.println("wftext     : " + wftext);
        // System.out.println("wfsid(url) : " + wfsid);
        Boolean retObj = portalServiceImpl.isNewWorkflow(username, newwfname, wftext, wfsid);
        return retObj;
    }
    
    /**
     * Visszaadja hogy a megadott nevu workflow fut e ebben a pillanatban.
     */    
    public Boolean isRunningWorkflow(String username, String runwfname) {
        // System.out.println("isRunningWorkflow...");
        // System.out.println("username: " + username);
        // System.out.println("runwfname: " + runwfname);
        Boolean retObj = portalServiceImpl.isRunningWorkflow(username, runwfname);
        return retObj;
    }
    
    /**
     * Visszaadja hogy a megadott nevu graf workflow-bol
     * van e letre hozva konkret vagy template workflow
     * mert ha igen akkor nem lehet save-vel menteni,
     * azaz modositani a grafot.
     */
    public Boolean isParentWorkflow(String username, String grafwfname) {
        // System.out.println("isParentWorkflow...");
        // System.out.println("username: " + username);
        // System.out.println("grafwfname: " + grafwfname);
        Boolean retObj = portalServiceImpl.isParentWorkflow(username, grafwfname);
        return retObj;
    }
    
}

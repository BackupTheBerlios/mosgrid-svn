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

import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.wfeditor.net.xmlrpc.common.ObjectSerializer;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Graph Editor (Workflow Editor) portal oldali service osztalya
 *
 * @author lpds
 */
public class WEPortalServiceImpl implements WEPortalService {
    
    /**
     * Egy megadott user osszes workflow-janak nevet adja vissza.
     *
     * (key: workflow name, value wfs service url)
     *
     * @param username
     * @return
     */
    public byte[] getMyWorkflowList(String username) {
        // ...
        // System.out.println("username: " + username);
        // ...
        Hashtable retObj = new Hashtable();
        String key = "";
        Enumeration enumWfList = PortalCacheService.getInstance().getUser(username).getAbstactWorkflows().keys();
        while (enumWfList.hasMoreElements()) {
            key = "" + enumWfList.nextElement();
            retObj.put(key, PortalCacheService.getInstance().getUser(username).getAbstactWorkflow(key).getWfsID() + "/xmlrpc");
        }
        // System.out.println("retObj: " + retObj);
        byte[] retObjBytes = ObjectSerializer.serializeObjectToBytes(retObj);
        return retObjBytes;
    }
    
    /**
     * Megallapitja hogy a megadott workflow nev foglalt e vagy sem.
     */
    public Boolean isNewWorkflow(String username, String newwfname, String wftext, String wfsid) {
        // ...
        // System.out.println("username: " + username);
        // System.out.println("wfname: " + newwfname);
        // System.out.println("wftext     : " + wftext);
        // System.out.println("wfsid(url) : " + wfsid);
        // ...
        // oldWorkflow = PortalCacheService.getInstance().getUser(username).isWorkflow(newwfname);
        boolean oldWorkflow = PortalCacheService.getInstance().getUser(username).isAbstactWorkflow(newwfname);
        Boolean retObj = new Boolean(!oldWorkflow);
        // return new ObjectSerializer((Object) retObj);
        if (retObj.booleanValue()) {
            PortalCacheService.getInstance().getUser(username).addAbstactWorkflows(newwfname, wftext, wfsid, "");
        } else {
            // text frissites
            if (PortalCacheService.getInstance().getUser(username).getAbstactWorkflow(newwfname) != null) {
                PortalCacheService.getInstance().getUser(username).getAbstactWorkflow(newwfname).setTxt(wftext);
            }
        }
        return retObj;
    }
    
    /**
     * Visszaadja hogy a megadott nevu workflow fut e ebben a pillanatban.
     */
    public Boolean isRunningWorkflow(String username, String runwfname) {
        // ...
        // System.out.println("username: " + username);
        // System.out.println("runwfname: " + runwfname);
        // ...
        Boolean retObj = new Boolean(false);
        // PortalCacheService.getInstance().getUser(username).getWorkflow(runwfname).getSubmittedStatus()
        // return new ObjectSerializer((Object) retObj);
        return retObj;
    }
    
    /**
     * Visszaadja hogy a megadott nevu graf workflow-bol
     * van e letre hozva konkret vagy template workflow
     * mert ha igen akkor nem lehet save-vel menteni,
     * azaz modositani a grafot.
     */
    public Boolean isParentWorkflow(String username, String grafwfname) {
        // ...
        // System.out.println("isParentWorkflow...");
        // System.out.println("username: " + username);
        // System.out.println("grafwfname: " + grafwfname);
        // ...
        Boolean retObj = PortalCacheService.getInstance().getUser(username).isWorkflowWithThisGraf(grafwfname);
        // return new ObjectSerializer((Object) retObj);
        return retObj;
    }
    
}

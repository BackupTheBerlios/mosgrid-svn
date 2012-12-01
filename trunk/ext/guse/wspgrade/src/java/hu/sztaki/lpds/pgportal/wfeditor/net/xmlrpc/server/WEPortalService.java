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
 * Graph Editor (Workflow Editor) portal oldali service
 * osztalyanak interface osztaly
 *
 * @author lpds
 */
public interface WEPortalService {
    
    /**
     * Egy megadott user osszes workflow-janak nevet adja vissza.
     *
     * (key: workflow name, value wfs service url)
     *
     * @param username
     * @return
     */
    public abstract byte[] getMyWorkflowList(String username);
    
    /**
     * Megallapitja hogy a megadott workflow nev foglalt e vagy sem.
     */
    public abstract Boolean isNewWorkflow(String username, String newwfname, String wftext, String wfsid);
    
    /**
     * Visszaadja hogy a megadott nevu workflow fut e ebben a pillanatban.
     */
    public abstract Boolean isRunningWorkflow(String username, String runwfname);
    
    /**
     * Visszaadja hogy a megadott nevu graf workflow-bol
     * van e letre hozva konkret vagy template workflow
     * mert ha igen akkor nem lehet save-vel menteni,
     * azaz modositani a grafot.
     */
    public abstract Boolean isParentWorkflow(String username, String grafwfname);
    
}

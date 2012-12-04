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
package hu.sztaki.lpds.pgportal.wfeditor.net.xmlrpc.common;

/**
 * A workflow editor es a workflow tar közti interface.
 * @author lpds
 */
public interface WEWFSService {

    /**
     * Lementi a parameterben megkapott aworkflow-t.
     */
    public abstract String saveWorkflow(boolean newWorkflow, String portalid, String userid, String wfname, String wfxml);

    /**
     * Lekerdezi a megadott nevu workflow leiro xml-jet.
     */
    public abstract String getWorkflow(String portalid, String userid, String wfname);

}

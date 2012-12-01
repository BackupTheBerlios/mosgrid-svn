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

package hu.sztaki.lpds.pgportal.visualizer.net.xmlrpc.server;

import hu.sztaki.lpds.pgportal.visualizer.net.xmlrpc.common.VISWFSService;

/**
 * @author lpds
 */
public class VISWFSServiceImplHandler implements VISWFSService {
    
    public VISWFSServiceImplHandler() {
    }
    
    /**
     * A parameterekben megkapott workflow jobjainak statuszat es kapcsolatait adja vissza.
     */
    public String getVisualizerWorkflow(String portalid, String userid, String wfname, String rtid) {
        // System.out.println("getVisualizerWorkflow...");
        // System.out.println("portalid : " + portalid);
        // System.out.println("userid   : " + userid);
        // System.out.println("wfname   : " + wfname);
        // System.out.println("rtid     : " + rtid);
        String retObj = new VISWFSServiceImpl().getVisualizerWorkflow(portalid, userid, wfname, rtid);
        return retObj;
    }
    
}

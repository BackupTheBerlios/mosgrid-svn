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
 * GetSshUserForHost.java
 * Job configuration.
 * Returns ssh users for ssh host (pbs, lsf ...)
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.Hashtable;
import hu.sztaki.lpds.pgportal.util.resource.SshResourceService;

public class GetSshUserForHost extends BASEActions{
    @Override
    public String getDispacher(Hashtable pParams){return null;}

    @Override
    public Hashtable getParameters(Hashtable pParams){return null;}

    @Override
    public String getOutput(Hashtable pParams){
        String user=""+pParams.get("user");
        String grid="";
        if (pParams.get("g") != null) {
            grid = "" + pParams.get("g");
        } else {
            grid = "" + ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("tmp*grid");
        }

        String res="job_jobmanager:: ::::"+SshResourceService.getI().getUserforHost(user, grid)+"::";

        return res;
    }

}

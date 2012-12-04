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
 * unicore config panel
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions.grids;

import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.JobConfigUI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import dci.data.Middleware;
import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.Vector;

public class JobConfigUI_arc implements JobConfigUI{

    public String getJsp() {
        return "/jsp/workflow/zen/middleware_arc.jsp";
    }

    public Hashtable getJobParameters(String user, HashMap exeParams,List<Middleware> sessionConfig) {
        Hashtable conf = new Hashtable(new JobConfigUtils().getMyJobParams(exeParams));
// job to be configured
        JobPropertyBean resJob=PortalCacheService.getInstance().getUser(user).getEditingJobData();
//temporary variables
        conf.put("grids", ConfigHandler.getGroups(sessionConfig, "arc"));
        String jobGrid=(String)conf.get("sgrid");
        if(jobGrid==null || !conf.get("gridtype").equals("arc"))
            jobGrid=""+((Vector)conf.get("grids")).get(0);

        conf.putAll(new JobConfigUtils().getMyJobParams(exeParams));
        return conf;
    }

}
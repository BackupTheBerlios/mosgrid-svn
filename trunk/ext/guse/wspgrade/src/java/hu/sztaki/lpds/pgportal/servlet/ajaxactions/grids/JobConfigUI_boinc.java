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
 * Glite config panel
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions.grids;

import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.JobConfigUI;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;

public class JobConfigUI_boinc implements JobConfigUI{

    public String getJsp() {
        return "/jsp/workflow/zen/middleware_boinc.jsp";
    }

    public Hashtable getJobParameters(String user, HashMap exeParams,List<Middleware> sessionConfig) {
//return values
        Hashtable res=new Hashtable(new JobConfigUtils().getGridParams("boinc", exeParams, sessionConfig));
        res.putAll(new JobConfigUtils().getMyJobParams(exeParams));
//job to be configured
        JobPropertyBean resJob=PortalCacheService.getInstance().getUser(user).getEditingJobData();
//temporary varibles
        String jobGrid=(String)res.get("sgrid");
        if(jobGrid==null || !res.get("gridtype").equals("boinc")) jobGrid=""+((Vector)res.get("grids")).get(0);
        boolean flag=false;

        String jobResource=null;
// resource list and the definition of the selected resource
        res.put("resources",ConfigHandler.getResources(sessionConfig, "boinc",jobGrid));

            jobResource=(String)resJob.getExe().get("resource");
            if(jobResource==null){
                if(((Vector)res.get("resources")).size()>0)
                    jobResource=""+((Vector)res.get("resources")).get(0);
                else jobResource="";
            }

            res.put("sresource", jobResource);
            res.put("sgrid", jobGrid);
            if(resJob.getExeDisabled().get("resource")!=null) res.put("eresource","disabled=\"true\"");


        return res;


    }

}
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
 * EDGI job configuration
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions.grids;

import dci.data.Item;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.JobConfigUI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import dci.data.Middleware;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.ArrayList;

/**
 * @author krisztian karoczkai
 */
public class JobConfigUI_edgi implements JobConfigUI{
    private static final String middleware="edgi";

    public String getJsp() {
        return "/jsp/workflow/zen/middleware_edgi.jsp";
    }

    public Hashtable getJobParameters(String user, HashMap exeParams, List<Middleware> sessionConfig) {
        Hashtable conf = new Hashtable(new JobConfigUtils().getMyJobParams(exeParams));
//job to configure
         JobPropertyBean cjob=PortalCacheService.getInstance().getUser(user).getEditingJobData();
         conf.put("cjob", cjob);
//config items
        conf.put("ars", getEdgiARItems(sessionConfig));

        if (exeParams.get("ar") == null) {
            conf.put("job_ar", "");
        } else {
            conf.put("job_ar", exeParams.get("ar"));
        }
        if (exeParams.get("arapp") == null) {
            conf.put("job_arapp", "");
        } else {
            conf.put("job_arapp", exeParams.get("arapp"));
        }
        if (exeParams.get("grid") == null) {
            conf.put("job_grid", "");
        } else {
            conf.put("job_grid", exeParams.get("grid"));
        }
        if (exeParams.get("resource") == null) {
            conf.put("job_resource", "");
        } else {
            conf.put("job_resource", exeParams.get("resource"));
        }
        if (exeParams.get("gliterole") == null) {
            conf.put("job_gliterole", "");
        } else {
            conf.put("gliterole", exeParams.get("gliterole"));
        }
        
        //conf.putAll(new JobConfigUtils().getMyJobParams(exeParams));
        return conf;
    }

    private List<Item.Edgi.Job> getJobsInAR(List<Middleware> sessionConfig, String pSelectedARName){
        List<Item.Edgi.Job> res=new ArrayList<Item.Edgi.Job>();
        for(Middleware t:sessionConfig)
            if(t.getType().equals(middleware)) //edgi
                for(Item it:t.getItem())
                    if(it.isEnabled() && pSelectedARName.equals(it.getName()))
                        res.addAll(it.getEdgi().getJob());
        return res;

    }

    private List<Item> getEdgiARItems(List<Middleware> sessionConfig){
        List<Item> res=new ArrayList<Item>();
        for(Middleware t:sessionConfig){
            if(t.getType().equals(middleware)) //edgi
                res.addAll(t.getItem());

        }
        return res;

    }

}

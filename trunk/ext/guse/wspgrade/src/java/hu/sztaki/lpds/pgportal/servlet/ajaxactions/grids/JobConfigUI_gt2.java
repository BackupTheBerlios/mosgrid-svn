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
 * gt2 config panel
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions.grids;

import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.JobConfigUI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import dci.data.Middleware;

public class JobConfigUI_gt2 implements JobConfigUI {

    public String getJsp() {
        return "/jsp/workflow/zen/middleware_gt.jsp";
    }

    public Hashtable getJobParameters(String user, HashMap exeParams,List<Middleware> sessionConfig) {
        Hashtable conf = new Hashtable(new JobConfigUtils().getGridResourceJobmngrParams("gt2", exeParams, sessionConfig));
        conf.putAll(new JobConfigUtils().getMyJobParams(exeParams));
        return conf;
    }
}

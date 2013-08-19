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
 * prepared unicore toolse
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions.grids;

import dci.data.Middleware;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.JobConfigUI;

import hu.sztaki.lpds.pgportal.util.resource.UnicoreIDBToolHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class JobConfigUI_unicore implements JobConfigUI{

    public String getJsp() {
        return "/jsp/workflow/zen/middleware_unicore.jsp";
    }

    public Hashtable getJobParameters(String user, HashMap exeParams, List<Middleware> sessionConfig) {
        Hashtable<String, Object> conf = new JobConfigUtils().getGridResourceJobmngrParams("unicore", exeParams, sessionConfig);
        conf.putAll(new JobConfigUtils().getMyJobParams(exeParams));

        Vector<String> idbTools = new Vector<String>();

        // add the selected tool to the list
        String idbTool = (String) exeParams.get("jobmanager");
        if (idbTool == null) {
            idbTool = "";
        }

        System.out.println("Setting IDB-tool to " + idbTool);
        conf.put("jobmanager", "" + idbTool);

        // aquire all IDB-tools from selected UNICORE resource
        String gridResource = (String) conf.get("sgrid");
        Vector<String> availableGrids = (Vector<String>)conf.get("grids");
        if (gridResource == null
            || gridResource.trim().equals("")
            || !availableGrids.contains(gridResource)){
            gridResource = availableGrids.firstElement();
        }
        idbTools.addAll(UnicoreIDBToolHandler.getIDBTools(user, gridResource, sessionConfig));

        if (idbTools != null) {
//            // add the entry if it is not contained in the list
//            if (!idbTool.trim().equals("")
//                && !idbTools.contains(idbTool)) {
//                idbTools.add(idbTool);
//            }
            // sort using case-insensitive order
            Collections.sort(idbTools, String.CASE_INSENSITIVE_ORDER);
        }

        // add the IDB-tools to the dropdown
        conf.put("idbToolList", idbTools);

        // idb-tools-lista es kivalasztott idb-tools meghatarozasa
        if ((gridResource != null) && (!gridResource.trim().equals(""))) {           
            // we have to set a resource, otherwise the "jobmanager"
            // property does not get passed to the dci-bdrige-service
            // see: hu.sztaki.lpds.wfi.util.JobConfig line 671
            conf.put("resource", gridResource);

            // delete value of binary
            conf.put("binary", "");
        }

        Object executeParser = (Object) exeParams.get("jobparser");
        if (executeParser != null) {
            System.out.println("Execution of Parser " + executeParser);
            conf.put("jobparser", "True");
        }

        // output parameters for debugging
//        System.out.println("Params");
//        for (Object e : conf.entrySet()) {
//            Map.Entry entry = (Map.Entry) e;
//            System.out.println(" \t" + entry.getKey() + " " + entry.getValue());
//        }
        return conf;
    }
	
}

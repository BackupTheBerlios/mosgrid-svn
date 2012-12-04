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
 * JobConfigUtils - helps building parameters jor job config jsp.
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions.grids;

import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;

public class JobConfigUtils {

    /**
     * Resource parameters for the executable. Returns the parameters for selecting grid, resource, jobmanager
     * @param name of the middleware
     * @param exeParams
     * @param sessionConfig - resource parameters
     * @return The parameters for jsp.
     */
    public Hashtable getGridResourceJobmngrParams(String jobMiddleware, HashMap exeParams, List<Middleware> sessionConfig) {
    
        //visszateresi ertek
        Hashtable<String, Object> res = new Hashtable<String, Object>();
    
        res.putAll(getGridParams(jobMiddleware, exeParams, sessionConfig));
    
        String jobGrid = (String) res.get("sgrid");
        String jobResource = null;

        //eroforraslista es kivalasztott eroforras meghatarozasa
        Vector<String> resources=ConfigHandler.getResources(sessionConfig, jobMiddleware, jobGrid);
        if (resources == null) {
            resources = new Vector<String>();
        }

        if ((jobGrid == null) || (jobGrid.equals("")) ||(resources.size()==0) ){
            jobGrid=((Vector<String>)res.get("grids")).get(0);
            resources=ConfigHandler.getResources(sessionConfig, jobMiddleware, jobGrid);        
        }
        res.put("resources", resources);

        jobResource = (String) exeParams.get("resource");        
        if (jobResource == null || "".equals(jobResource.trim()) || !((Vector) res.get("resources")).contains(jobResource)) {
            if (resources.size() > 0) {
                jobResource = "" + resources.get(0);
            } else {
                jobResource = "";
            }
        }
        res.put("sresource", jobResource);
    
    //jobmanagerlista es kivalasztott jobmanager meghatarozasa
        Vector<String> services=ConfigHandler.getServices(sessionConfig, jobMiddleware, jobGrid, jobResource);
        if (services == null) {
            services = new Vector<String>();
        }
        res.put("rdata", services);
        String jobService = (String) exeParams.get("jobmanager");
        if (jobService == null || "".equals(jobService.trim()) || !services.contains(jobService)) {
            if (services.size() > 0) {
                jobService = "" + services.get(0);
            }
            else {
                jobService = "";
            }

            res.put("jobmanager", "" + jobService);
        }

        return res;
    } 

    /**
     * Resource parameters for the executable. Returns only the parameters for selecting grid.
     * @param name of the middleware
     * @param exeParams
     * @param sessionConfig - resource parameters
     * @return The parameters for jsp.
     */
    public Hashtable getGridParams(String jobMiddleware, HashMap exeParams, List<Middleware> sessionConfig) {
    //visszateresi ertek
        Hashtable res = new Hashtable();
    //selected grid type

        System.out.println(this.getClass().getName()+":"+jobMiddleware);
    ////elerheto VO lista tovabbadadasa
        res.put("grids", ConfigHandler.getGroups(sessionConfig, jobMiddleware));
    // alapertelmezett(mar beallitott) VO
        String sGrid = (String) exeParams.get("grid");
        if (sGrid == null) {
            sGrid = "";
        }

    //gridlistaban van-e a kivalasztott grid...
    /*
            boolean flag = false;
            for (String t : (Vector<String>) res.get("grids")) {
                if (sGrid.equals(t)) {
                    flag = true;
                }
            }
            if (!flag) {
                sGrid = ((Vector<String>) res.get("grids")).get(0);
            }

            if (sGrid != null) {
                res.put("sgrid", sGrid);
            } else if (((Vector) res.get("grids")).size() > 0) {
                sGrid = "" + ((Vector) res.get("grids")).get(0);
            }
   */     res.put("sgrid", sGrid);

        return res;
    }


    public Hashtable getResourcesForSsh(String user, String jobMiddleware, HashMap exeParams, List<Middleware> sessionConfig) {

        //visszateresi ertek
        Hashtable res = new Hashtable();

        res.putAll(getGridParams(jobMiddleware, exeParams, sessionConfig));

        String jobGrid = (String) res.get("sgrid");
        String jobResource = null;
//eroforraslista es kivalasztott eroforras meghatarozasa
        Vector<String> resources=ConfigHandler.getResources(sessionConfig, jobMiddleware, jobGrid);
        if ((jobGrid == null) || (jobGrid.equals("")) ||(resources.size()==0) ){
            jobGrid=((Vector<String>)res.get("grids")).get(0);
            resources=ConfigHandler.getResources(sessionConfig, jobMiddleware, jobGrid);
        }
        res.put("resources", resources);
        jobResource = (String) exeParams.get("resource");
        if (jobResource == null || "".equals(jobResource.trim()) || !((Vector) res.get("resources")).contains(jobResource)) {
            if (((Vector) res.get("resources")).size() > 0)
                jobResource = "" + ((Vector) res.get("resources")).get(0);
        }
        res.put("sresource", jobResource);

//ssh user for host
      //res.put("rdata", SshResourceService.getI().getUserforHost(user, jobGrid));//rdata

        return res;
    }

    /**
     * Parameters for the executable.
     * @param exeParams
     * @return The parameters for jsp.
     */
    public Hashtable getMyJobParams(HashMap exeParams) {

        Hashtable<String, String> res = new Hashtable<String, String>();

        if (exeParams.get("gridtype") == null) res.put("gridtype", "");
        else res.put("gridtype", (String) exeParams.get("gridtype"));


        putParams(exeParams, res, "params", null, "");
        putParams(exeParams, res, "nodenumber", null, "");
        putParams(exeParams, res, "binary", "", "");
        putParams(exeParams, res, "erbinary", "", "");


        // modulsag vizsgalat es tiltas - begin
//        if (exeParams.containsKey("module")) {
//            res.put("ebinary", "disabled=\"true\"");
//        }
        // modulsag vizsgalat es tiltas - end
        putParams(exeParams, res, "type", "", "Sequence");

        return res;
    }

    public void putParams(
      HashMap exeParams,
      Hashtable<String, String> res,
      String name,
      String defaultValue,
      String emptyValue) {
        if (exeParams.get(name) == null) {
            res.put(name, emptyValue);
        } else {
            String value = String.valueOf(exeParams.get(name));
            value = (value!=null)? value : defaultValue;
            res.put(name, value);
        }
  }
}

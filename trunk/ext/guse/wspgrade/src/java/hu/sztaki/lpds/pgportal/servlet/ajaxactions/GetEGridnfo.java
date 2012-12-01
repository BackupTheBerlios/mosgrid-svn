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
 * GetGrids.java
 * Handling of grid resorurce realted definitons during job configuration.
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.util.resource.SshResourceService;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;

/**
 *  handling of definition items  "grid", "resource", "jobmanager" in case of EasyWF configuration.
 */
public class GetEGridnfo extends BASEActions {

    /** Creates a new instance of GetEGridnfo */
    public GetEGridnfo() {
    }

    @Override
    public String getDispacher(Hashtable pParams) {
        return null;
    }

    @Override
    public Hashtable getParameters(Hashtable pParams) {
        return null;
    }

    @Override
    public String getOutput(Hashtable pParams) {
        //usage:
        //getRemoteSelectOptions('m=GetEGridnfo&i=1&e=easyparamnum&v=selectedvalue');

        String res = "";
        String user = "" + pParams.get("user");
        PortalCacheService.getInstance().getUser(user).getConfiguringEParams();
        Hashtable psource = new Hashtable();        
        try {
            int source = Integer.parseInt("" + pParams.get("e"));//EParams parametersorszam
            psource = (Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(source);
            System.out.println("GetEGridnfo-psource:"+psource);
            int dest = Integer.parseInt("" + psource.get("i"));//EParams kovetkezo parametersorszam
            Hashtable pdest = (Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(dest);
            res = new String("eparam_" + psource.get("i") + ":: ");//setparam::defaultvalue
            Vector tmp = new Vector();

            if (pdest.get("name").equals("grid")) {//return: grid list
                if (pParams.get("v") != null) {//ha van param , elmenti az egridtyp-ot
                    ((Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(source)).put("value", "" + pParams.get("v"));
                }


                tmp = ConfigHandler.getGroups(( List<Middleware>) getSessionVariable("resources"), "" + pParams.get("v"));

                for (int i = 0; i < tmp.size(); i++) {
                    res = res.concat("::" + tmp.get(i));
                }
                res = res.concat("::");
                if (psource.get("i") != null) {
                    ((Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(dest)).put("value", "" + tmp.get(0));
                    res = res.concat("GetEGridnfo&e=" + psource.get("i"));
                }// next value
////////////////////////////////////////////////////////////////////////////////////
            } else if (pdest.get("name").equals("resource")) {//return: resource list
                String grid = "";
                String gridt = "";
                if (pParams.get("v") != null) {//ha van param , elmenti az egridet
                    ((Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(source)).put("value", "" + pParams.get("v"));
                    grid = "" + pParams.get("v");
                } else {
                    grid = "" + ((Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(source)).get("value");
                }
                
                if (psource.get("egridt") != null) {
                    int gnum = (Integer) psource.get("egridt");
                    gridt = "" + ((Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(gnum)).get("value");
                } else {
                    gridt = "" + pdest.get("gridtype");
                }


                    //tmp = ResourceServiceImpl.getI().getResources(grid, user);//grid
                tmp = ConfigHandler.getResources((List<Middleware>) getSessionVariable("resources"), gridt, grid);

                //save default value
                ((Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(dest)).put("value", "" + tmp.get(0));
                for (int i = 0; i < tmp.size(); i++) {
                    res = res.concat("::" + tmp.get(i));
                }
                res = res.concat("::");
                if (psource.get("i") != null) {
                    res = res.concat("GetEGridnfo&e=" + psource.get("i"));
                }// next value
////////////////////////////////////////////////////////////////////////////////////
            } else if (pdest.get("name").equals("jobmanager")) {//return: jobmanager list
                String gridt = "";
                String grid = "";
                String resource = "-";
                if (pParams.get("v") != null) {//ha van param , elmenti az eresourceot
                    ((Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(source)).put("value", "" + pParams.get("v"));
                    resource = "" + pParams.get("v");
                } else {
                    resource = "" + ((Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(source)).get("value");
                }

                if (psource.get("egridt") != null) {
                    int gnum = (Integer) psource.get("egridt");
                    gridt = "" + ((Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(gnum)).get("value");
                } else {
                    gridt = "" + pdest.get("gridtype");
                }

                if (psource.get("egrid") != null) {
                    int gnum = (Integer) psource.get("egrid");
                    grid = "" + ((Hashtable) PortalCacheService.getInstance().getUser(user).getConfiguringEParams().get(gnum)).get("value");
                } else {
                    grid = "" + pdest.get("grid");
                }
                
                if (SshResourceService.getI().isGroupforSshKey((List<Middleware>)getSessionVariable("resources"), gridt)) {
                    Vector userforhost = new Vector();
                    userforhost.add(SshResourceService.getI().getUserforHost(user, grid));
                    tmp = userforhost;
                }else{
                //tmp = ResourceServiceImpl.getI().getJobManagers(grid, resource, user);//
                    tmp = ConfigHandler.getServices((List<Middleware>) getSessionVariable("resources"), gridt, grid, resource);
                }
                for (int i = 0; i < tmp.size(); i++) {
                    res = res.concat("::" + tmp.get(i));
                }
                res = res.concat("::");//next value
            }
        } catch (Exception e) {
            //e.printStackTrace();
            if (psource.get("i") != null) {
                res = res.concat("::GetEGridnfo&e=" + psource.get("i"));
            }// next value
        }
        return res;
    }
}

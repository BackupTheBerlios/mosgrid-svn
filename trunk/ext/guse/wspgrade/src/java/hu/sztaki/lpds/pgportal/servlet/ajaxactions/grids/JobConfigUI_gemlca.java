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
 * To change this template, choose Tools | Templates
 * and open the template in the editor. 
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions.grids;

import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.GemlcaCacheService;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.JobConfigUI;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;

/**
 *
 * @author csig
 */
public class JobConfigUI_gemlca implements JobConfigUI {

    public String getJsp() {
        return "/jsp/workflow/zen/middleware_gemlca.jsp";
    }

    public Hashtable getJobParameters(String user, HashMap exeParams, List<Middleware> sessionConfig) {
        //System.out.println("XXX-getGemlcaParams p:"+pParams);
        Hashtable res = new Hashtable();

// job to configure
        JobPropertyBean resJob = PortalCacheService.getInstance().getUser(user).getEditingJobData();
// available VO list
        res.put("grids", ConfigHandler.getGroups(sessionConfig, "gemlca"));
// Default (already configured) VO
        String sGrid = (String) resJob.getExe().get("grid");
        if (sGrid == null || "-".equals(sGrid)) {
            sGrid = "-";
            res.put("sgrid", sGrid);
            res.put("resources", new Vector());
        } else {
            //System.out.println("O sgrid="+sGrid);
            String sResource = null;
            String sData = null;


            res.put("sgrid", sGrid);
//Editing disabled in template
            if (resJob.getExeDisabled().get("grid") != null) {
                res.put("egrid", "disabled=\"true\"");
            }

            if (resJob.getExe() != null) {
                //sGrid=(String)resJob.getExe().get("grid");
                sResource = (String) resJob.getExe().get("resource");
                sData = "" + resJob.getExe().get("jobmanager");
                if (resJob.getExeDisabled().get("gridtype") != null) {
                    res.put("egridtype", "disabled=\"true\"");
                }
            }

            try {// GEMLCA PARAMETERS
                Vector inp = ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getEditingJobData()).getInputs();
                Vector oup = ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getEditingJobData()).getOutputs();
                res.put("inpnmb", "" + inp.size());
                res.put("oupnmb", "" + oup.size());
                //gemlcaquery
                Vector v = GemlcaCacheService.getInstance().getGLCList("" + PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "/users/" + user + "/x509up." + sGrid, sGrid, inp.size(), oup.size());//sResource
                res.put("resources", v);//res.put("rdata", v);

                if (!v.isEmpty()) {
                    String GLCode = "";
                    if (sResource == null) {
                        GLCode = ((String) v.get(0)).split(":", 2)[0];
                    } else {
                        GLCode = sResource;
                    }
                    //LCcodes
                    Vector vgparams = null;
                    try {
                        vgparams = GemlcaCacheService.getInstance().getGLCparams(sGrid, GLCode);
                    } catch (Exception e) {
                        vgparams = GemlcaCacheService.getInstance().getGLCparams("" + sGrid, ((String) v.get(0)).split(":", 2)[0]);
                    }
                    //executor sites
                    Vector vgsites = null;
                    try {
                        vgsites = GemlcaCacheService.getInstance().getGLCsites(sGrid, GLCode);
                    // if (vgsites == null) {
                    } catch (Exception e) {
                        vgsites = GemlcaCacheService.getInstance().getGLCsites("" + sGrid, ((String) v.get(0)).split(":", 2)[0]);
                    }
                    res.put("sresource", GLCode);
                    res.put("rdata", vgsites);
                    res.put("sdata", sData);
                    //System.out.println("XXYYsdata:" + sData + "XX");
                    // gemlca parameters
                    String ggparams = "";
                    if (resJob.getExe().get("params") == null) {// default values
                        for (int i = 0; i < vgparams.size(); i++) {
                            ((HashMap) vgparams.get(i)).put("svalue", "" + ((HashMap) vgparams.get(i)).get("value"));
                            ((HashMap) vgparams.get(i)).put("nbr", "" + i);
                            ggparams += ((HashMap) vgparams.get(i)).get("value") + " ";
                        }
                        resJob.getExe().put("params", ggparams.trim());
                    } else {
                        String[] gsparams = ((String) resJob.getExe().get("params")).split(" ");
                        if (gsparams.length == vgparams.size()) {
                            for (int i = 0; i < vgparams.size(); i++) {
                                ((HashMap) vgparams.get(i)).put("svalue", "" + gsparams[i]);
                                ((HashMap) vgparams.get(i)).put("nbr", "" + i);
                            }
                        } else {// default values
                            for (int i = 0; i < vgparams.size(); i++) {
                                ((HashMap) vgparams.get(i)).put("svalue", "" + ((HashMap) vgparams.get(i)).get("value"));
                                ((HashMap) vgparams.get(i)).put("nbr", "" + i);
                                ggparams += ((HashMap) vgparams.get(i)).get("value") + " ";
                            }
                            resJob.getExe().put("params", ggparams.trim());
                        }
                    }
                    res.put("gparams", vgparams);
                    res.put("params", (String) resJob.getExe().get("params"));
                } else {//if no such LC exists
                    res.put("gmsg", "NO jobs with " + inp.size() + " input & " + oup.size() + " output port(s)");
                }
            } catch (Exception e) {
                res.put("gmsg", "" + e.getMessage());
            //e.printStackTrace();
            }
        }

        return res;
    }
}

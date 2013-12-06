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
/**
 * Loading the configuration panel belonging to a selected middleware
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.JobConfigUI;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;

/**
 * @author krisztian karoczkai
 */
public class GetMiddlewareConfigPanel extends BASEActions {

    @Override
    public String getDispacher(Hashtable pParams) {
        try {
            JobConfigUI jobui = null;
            jobui = (JobConfigUI) Class.forName("hu.sztaki.lpds.pgportal.servlet.ajaxactions.grids.JobConfigUI_" + pParams.get("j")).newInstance();
            return jobui.getJsp();
        }
        catch (ClassNotFoundException ce) {
            System.out.println("Plugin " + pParams.get("j") + " not installed. " + ce.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "/jsp/workflow/zen/middleware_nothing.jsp";
    }

    @Override
    public Hashtable getParameters(Hashtable pParams) {
        Hashtable res = new Hashtable();
        try {
            //resource konfiguracios adatok
            Hashtable<String, Hashtable<String, Hashtable<String, Vector<String>>>> sessionConfig = (Hashtable<String, Hashtable<String, Hashtable<String, Vector<String>>>>) ps.getAttribute("resources");
            //bejelentkezett user
            String user = "" + pParams.get("user");
            //konfiguralando job
            JobPropertyBean resJob = PortalCacheService.getInstance().getUser(user).getEditingJobData();

            JobConfigUI jobui = null;
            jobui = (JobConfigUI) Class.forName("hu.sztaki.lpds.pgportal.servlet.ajaxactions.grids.JobConfigUI_" + pParams.get("j")).newInstance();
            res.putAll(jobui.getJobParameters(user, resJob.getExe(),(List<Middleware>)getSessionVariable("resources")));
            res.put("jobjsp", jobui.getJsp());
            res.put("jobID",""+PortalCacheService.getInstance().getUser(user).getEditingJobData().getName());
            //disable exe parameters
            Iterator<String> itr = resJob.getExeDisabled().keySet().iterator();
            while (itr.hasNext()) {
                res.put("e" + itr.next(), "disabled=\"true\"");
            }
        } catch (ClassNotFoundException ce) {
            System.out.println("Plugin " + pParams.get("j") + " not installed. " + ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("GetMiddlewareConfigPanel-getParameters->  j:"+pParams.get("j"));
        //System.out.println("res:"+res);
        return res;
    }
}

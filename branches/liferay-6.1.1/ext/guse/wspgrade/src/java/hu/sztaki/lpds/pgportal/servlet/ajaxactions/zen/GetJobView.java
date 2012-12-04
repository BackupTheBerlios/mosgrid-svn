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
 * GetJobView.java
 * Setting job configuration surface
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions.zen;

import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.JobPlussData;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.BASEActions;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.inf.JobConfigUI;
import hu.sztaki.lpds.pgportal.util.cancer.BinaryUtils;

import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;

/**
 * Setting job configuration surface
 *
 * @author krisztian karoczkai
 */
public class GetJobView extends BASEActions
{

    /** Creates a new instance of GetJobView */
    public GetJobView() {}
    @Override
    public String getOutput(Hashtable pParams){return null;}
    @Override
    public String getDispacher(Hashtable pParams)
    {
        String res="/jsp/workflow/"+pParams.get("ws-pgrade.wftype")+"/edit.jsp";
        return res;
    }
    @Override
    public Hashtable getParameters(Hashtable pParams)
    {
	SaveAllData save=new SaveAllData();
        save.getParameters(pParams);
		
        List<Middleware> sessionConfig= (List<Middleware>)getSessionVariable("resources");
        Hashtable res=new Hashtable();
        String user=""+pParams.get("user");
        String workflow=""+pParams.get("workflow");
       
        if(pParams.get("job")!=null){
            try{
                String oldJobName=PortalCacheService.getInstance().getUser(user).getEditingJobData().getName();
                if(!pParams.get("job").equals(oldJobName))
                    res.put("oldjobname",oldJobName);
            }
            catch(Exception e){/*There is no selected job*/}
            PortalCacheService.getInstance().getUser(user).setEditingJobData(""+pParams.get("job"));
        }
        JobPropertyBean resJob=PortalCacheService.getInstance().getUser(user).getEditingJobData();
        res.put("jobid",""+PortalCacheService.getInstance().getUser(user).getEditingJobData().getId());
        res.put("job", new JobPlussData(PortalCacheService.getInstance().getUser(user).getEditingJobData()));
        res.put("workflows",PortalCacheService.getInstance().getUser(user).getWorkflows());
        res.put("iworkflow",""+pParams.get("iworkflow"));
        Vector tmp=new Vector();
//Configuration
/*        System.out.println("getJobView:"+user);
        System.out.println("getJobView:"+PortalCacheService.getInstance().getUser(user).getEditingJobData());
        System.out.println("getJobView:"+PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe());
        System.out.println("getJobView:"+res);
*/        Iterator<String> it=resJob.getExe().keySet().iterator();
        String it_key;
        while(it.hasNext()){
            it_key=it.next();
            if(resJob.getExe().get(it_key)!=null) res.put(it_key, resJob.getExe().get(it_key));
        }
//        res.putAll(PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe());
/*
//meta broker resources 
        Vector<String> mbt_conf_resurces=new Vector<String>();
        HashMap exe=PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe();
        Iterator<String> mbt_enm=exe.keySet().iterator();
        while(mbt_enm.hasNext()) mbt_conf_resurces.add(""+exe.get(mbt_enm.next()));
        res.put("resources",mbt_conf_resurces);
*/
// cloud types
        tmp=ConfigHandler.getCloudMidlewares((List<Middleware>)getSessionVariable("resources"));
        res.put("cloudtypes",tmp);
        if(tmp.size()>0) res.put("defaultGAEservice",tmp.get(0));

        HashMap jobProp=resJob.getExe();
        if(jobProp.get("cloudtype")!=null)
            res.put("icloud",jobProp.get("cloudtype"));
        if(jobProp.get("gaeurl")!=null)
            res.put("gaeurl",jobProp.get("gaeurl"));      

//grid types       
        Hashtable vGridTypes = new Hashtable();
        tmp = ConfigHandler.getGridMidlewares((List<Middleware>)getSessionVariable("resources"));
//        tmp.add("metabroker");
        for (int i = 0; i < tmp.size(); i++) {
            vGridTypes.put("" + tmp.get(i), "0");
        }

        res.put("gridtypes", vGridTypes);
        String sGridType = null;
        if (resJob != null) {
            if (resJob.getExe() != null) {
                sGridType = "" + resJob.getExe().get("gridtype");
                res.put("sgridtype", sGridType);
            }
        }

        if (!"".equals(sGridType)) {
//            System.out.println("GetJobView-getParameters- :sGridType="+sGridType+"/"+res.get("sgridtype")+" getmiddlewareconfig call!");
            try {
                JobConfigUI jobui = null;
                jobui = (JobConfigUI) Class.forName("hu.sztaki.lpds.pgportal.servlet.ajaxactions.grids.JobConfigUI_" + sGridType).newInstance();
                res.putAll(jobui.getJobParameters(user, jobProp, sessionConfig));
                res.put("jobjsp", jobui.getJsp());
            } catch (ClassNotFoundException ce) {
                System.out.println("Plugin " + pParams.get("j") + " not installed. " + ce.getMessage());
                res.put("jobjsp", "/jsp/workflow/zen/middleware_nothing.jsp");
            } catch (Exception e) {
                e.printStackTrace();
                res.put("jobjsp", "/jsp/workflow/zen/middleware_nothing.jsp");
            }
        }

        res.put("CGbinary", BinaryUtils.getInstance().getList());
        res.put("edgesbinary", BinaryUtils.getInstance().getList("edges"));

        res.put("iworkflow", "" + resJob.getExe().get("iworkflow"));
        res.put("iservice", "" + resJob.getExe().get("servicetype"));

        //disable exe parameters
        Iterator<String> itr = resJob.getExeDisabled().keySet().iterator();
        while (itr.hasNext()) {
            res.put("e" + itr.next(), "disabled=\"true\"");
        }




//WebService
        List<Middleware> configData=(List<Middleware>)getSessionVariable("resources");
        Vector<String> serviceMiddlewares=ConfigHandler.getServiceMidlewares(configData);
        Vector<String> sertviceTypes=new Vector<String>();
        for(String t:serviceMiddlewares)
            sertviceTypes.addAll(ConfigHandler.getGroups(sessionConfig, t));
        res.put("servicetypes",sertviceTypes);

        if(sertviceTypes.size()>0){
            if(resJob.getExeDisabled().get("servicetypes")!=null)
                res.put("eservicetypes","disabled=\"true\"");
            if(resJob.getExe().get("servicetypes")!=null)
                res.put("iservicetypes",resJob.getExe().get("servicetypes"));

            if(resJob.getExeDisabled().get("serviceurl")!=null)
                res.put("eserviceurl","disabled=\"true\"");
            if(resJob.getExe().get("serviceurl")!=null)
                res.put("iserviceurl",resJob.getExe().get("serviceurl"));

            if(resJob.getExeDisabled().get("servicemethod")!=null)
                res.put("eservicemethod","disabled=\"true\"");
            if(resJob.getExe().get("servicemethod")!=null)
                res.put("iservicemethod",resJob.getExe().get("servicemethod"));

        }

// job type is inherited from the template (Job is Workflow, Job is Service, Job is Binary) begin
        boolean isWflag = false; // Job is Workflow
        boolean isSflag = false; // Job is Service
        boolean isBflag = false; // Job is Binary
        String templatename = PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).getTemplate();
        if ((!"".equals(templatename)) && (!"--".equals(templatename)) && (templatename != null)) {
            HashMap tempMap = resJob.getExeDisabled(); // template tulajdonsagokat leiro hashMap
            if (tempMap.get("iworkflow") != null) {
                isWflag = true;
            } else if ((tempMap.get("servicetype") != null) || (tempMap.get("serviceurl") != null) || (tempMap.get("servicemethod") != null)) {
                isSflag = true;
            } else if(tempMap.get("gridtype") != null) {
                isBflag = true;
            }
            if (isWflag) {
                res.put("esflag", "disabled=\"true\"");
                res.put("ebflag", "disabled=\"true\"");
            } else if (isSflag) {
                res.put("ewflag", "disabled=\"true\"");
                res.put("ebflag", "disabled=\"true\"");
            } else if (isBflag) {
                res.put("ewflag", "disabled=\"true\"");
                res.put("esflag", "disabled=\"true\"");
            }
        }
// job type is inherited from the template end
        res.put("jobID",PortalCacheService.getInstance().getUser(user).getEditingJobData().getName());
        res.put("iservicetype", ""+PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("servicetype"));
        res.put("jobname",PortalCacheService.getInstance().getUser(user).getEditingJobData().getName());
        res.put("jobtxt",PortalCacheService.getInstance().getUser(user).getEditingJobData().getTxt());
        
        Vector itmp=PortalCacheService.getInstance().getUser(user).getEditingJobData().getInputs();
        for(int i=0;i<itmp.size();i++)
        {
            if(((PortDataBean)itmp.get(i)).get("remote")!=null)
                res.put("iworkflowdisabled","OK");
        }
        itmp=PortalCacheService.getInstance().getUser(user).getEditingJobData().getOutputs();
        for(int i=0;i<itmp.size();i++)
        {
            if(((PortDataBean)itmp.get(i)).get("remote")!=null)
                res.put("iworkflowdisabled","OK");
        }
        return res;
    }


    
}

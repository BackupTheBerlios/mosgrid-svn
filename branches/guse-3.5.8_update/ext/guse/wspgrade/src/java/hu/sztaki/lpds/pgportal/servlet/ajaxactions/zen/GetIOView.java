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
 * GetIOView.java
 * Preparation of I/O handling in case of job configuration
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions.zen;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.GemlcaCacheService;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.PortData;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.BASEActions;
import hu.sztaki.lpds.wfs.com.*;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.util.*;

/**
 * Preparation of I/O handling in case of job configuration
 * @author krisztian karoczkai
 */
public class GetIOView extends BASEActions
{
    
    /** Creates a new instance of GetIOView */
    public GetIOView() {}
    @Override
    public String getOutput(Hashtable pParams){return null;}
    @Override
    public String getDispacher(Hashtable pParams){return "/jsp/workflow/"+pParams.get("ws-pgrade.wftype")+"/edit.jsp";}
    @Override
    public Hashtable getParameters(Hashtable pParams)
    {
        SaveAllData save=new SaveAllData();
        save.getParameters(pParams);
		String enabledDinamicWF=PropertyLoader.getInstance().getUserProperty((String)pParams.get("user"),"guse.wspgrade.dinamicwf.enabled");

//        System.out.println("*****user:"+pParams.get("user"));
        String sdata="";
        Hashtable res=new Hashtable();
        String user=""+pParams.get("user");
        Vector tmp=new Vector();

        if(pParams.get("job")!=null){
            try{
                String oldJobName=PortalCacheService.getInstance().getUser(user).getEditingJobData().getName();
                if(!pParams.get("job").equals(oldJobName))
                    res.put("oldjobname",oldJobName);
            }
            catch(Exception e){/* There is no selected job */}
            PortalCacheService.getInstance().getUser(user).setEditingJobData(""+pParams.get("job"));
        }
        if(pParams.get("portyp")!=null)
        {
            if("input".equals(""+pParams.get("portyp")))
            tmp.add(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getInput(""+pParams.get("port")));
        }
        else tmp=((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getInputs();

// return the input list matching the conditions        
        Vector allinputsname=new Vector();
        for(int ai=0;ai<((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getInputs().size();ai++)
        {
            allinputsname.add(((PortDataBean)(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getInputs()).get(ai)).getName());
        }
        res.put("allinputs",allinputsname);
        
        Vector res0=new Vector();
        Vector res1=new Vector();
        String fileUpload="";
        for(int i=0;i<tmp.size();i++) 
        {
            PortData tmpPD=new PortData(new Hashtable(((PortDataBean)tmp.get(i)).getData()), new Hashtable(((PortDataBean)tmp.get(i)).getDataDisabled()),new Hashtable(((PortDataBean)tmp.get(i)).getLabel()),new Hashtable(((PortDataBean)tmp.get(i)).getDesc()),new Hashtable(((PortDataBean)tmp.get(i)).getInherited()));
            tmpPD.setId(""+((PortDataBean)tmp.get(i)).getId());
            tmpPD.setName(""+((PortDataBean)tmp.get(i)).getName());
            tmpPD.setTxt(""+((PortDataBean)tmp.get(i)).getTxt());
            tmpPD.setPreJob(""+((PortDataBean)tmp.get(i)).getPrejob());
            tmpPD.setSeq(""+((PortDataBean)tmp.get(i)).getSeq());
            if(((PortDataBean)tmp.get(i)).get("file")!=null)
                tmpPD.setExternalName(""+((PortDataBean)tmp.get(i)).get("file"));
            else tmpPD.setExternalName("N/A");
            res0.add(tmpPD); //,,
            if(tmpPD.getPreJob().equals(""))
                sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_max,input_"+((PortDataBean)tmp.get(i)).getId()+"_file,input_"+((PortDataBean)tmp.get(i)).getId()+"_remote,input_"+((PortDataBean)tmp.get(i)).getId()+"_value,input_"+((PortDataBean)tmp.get(i)).getId()+"_sqlurl,input_"+((PortDataBean)tmp.get(i)).getId()+"_sqlselect,input_"+((PortDataBean)tmp.get(i)).getId()+"_sqluser,input_"+((PortDataBean)tmp.get(i)).getId()+"_sqlpass,input_"+((PortDataBean)tmp.get(i)).getId()+"_clist,input_"+((PortDataBean)tmp.get(i)).getId()+"_cprop,");
            else
                sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_waiting,");
//            sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_intname,input_"+((PortDataBean)tmp.get(i)).getId()+"_eparam,input_"+((PortDataBean)tmp.get(i)).getId()+"_dpid,input_"+((PortDataBean)tmp.get(i)).getId()+"_hnt,input_"+((PortDataBean)tmp.get(i)).getId()+"_equaltype,input_"+((PortDataBean)tmp.get(i)).getId()+"_equalvalue,input_"+((PortDataBean)tmp.get(i)).getId()+"_equalinput,");
// No internal file name is needed in case of WS
            res.put("sworkflow", ""+PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("servicemethod"));
            if(res.get("sworkflow").equals("null"))
                sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_intname,");
            sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_dpid,input_"+((PortDataBean)tmp.get(i)).getId()+"_hnt,input_"+((PortDataBean)tmp.get(i)).getId()+"_pequaltype,input_"+((PortDataBean)tmp.get(i)).getId()+"_pequalvalue,input_"+((PortDataBean)tmp.get(i)).getId()+"_pequalinput,");
            if(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("iworkflow")!=null)
            {
//                System.out.println("++++++"+((PortDataBean)tmp.get(i)).getPrejob()+"::");
//                if(((PortDataBean)tmp.get(i)).getPrejob().equals(""))
                sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_iinput,");
            }
            fileUpload=fileUpload.concat("setFile0Upload('upload','input_"+tmpPD.getId()+"_intname','"+PortalCacheService.getInstance().getUser(user).getEditingJobID()+"');");
            //gemlca 
            if ("gemlca".equals("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype"))) {
               // String intname = ((PortDataBean)tmp.get(i)).getName();
               // GemlcaCacheService.getInstance().getPortINamePosition("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("grid"), "" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("resource"), intname, "" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("params"), false);                    
                sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_gout,");
            }            
        }
        res.put("inputs",res0);
        res.put("inputsnum",new Long(res0.size()));
        res.put("fileUpload",fileUpload);
        if(pParams.get("portyp")==null)
        {
            tmp=((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getOutputs();
        }
        else
        {
            if("output".equals(""+pParams.get("portyp")))
            tmp.add(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getOutput(""+pParams.get("port")));
            else tmp=new Vector();
        }
        
        for(int i=0;i<tmp.size();i++) 
        {
            PortData tmpPD=new PortData(new Hashtable(((PortDataBean)tmp.get(i)).getData()), new Hashtable(((PortDataBean)tmp.get(i)).getDataDisabled()),new Hashtable(((PortDataBean)tmp.get(i)).getLabel()),new Hashtable(((PortDataBean)tmp.get(i)).getDesc()),new Hashtable(((PortDataBean)tmp.get(i)).getInherited()));
            tmpPD.setId(""+((PortDataBean)tmp.get(i)).getId());
            tmpPD.setName(""+((PortDataBean)tmp.get(i)).getName());
            tmpPD.setTxt(""+((PortDataBean)tmp.get(i)).getTxt());
            tmpPD.setSeq(""+((PortDataBean)tmp.get(i)).getSeq());
            res1.add(tmpPD);
            sdata=sdata.concat("output_"+((PortDataBean)tmp.get(i)).getId()+"_maincount,output_"+((PortDataBean)tmp.get(i)).getId()+"_intname,output_"+((PortDataBean)tmp.get(i)).getId()+"_type,output_"+((PortDataBean)tmp.get(i)).getId()+"_remote,output_"+((PortDataBean)tmp.get(i)).getId()+"_remotehost,");
            if(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("iworkflow")!=null)
            sdata=sdata.concat("output_"+((PortDataBean)tmp.get(i)).getId()+"_maincount,output_"+((PortDataBean)tmp.get(i)).getId()+"_ioutput,");

            if("true".equals(enabledDinamicWF)){
                sdata=sdata.concat("output_"+((PortDataBean)tmp.get(i)).getId()+"_dinamic,");
                sdata=sdata.concat("output_"+((PortDataBean)tmp.get(i)).getId()+"_dinamicjobs");
            }
            //gemlca 
            if ("gemlca".equals("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype"))) {
                //String intname = ((PortDataBean)tmp.get(i)).getName();
               // GemlcaCacheService.getInstance().getPortINamePosition("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("grid"), "" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("resource"), intname, "" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("params"), false);                    
                sdata=sdata.concat("output_"+((PortDataBean)tmp.get(i)).getId()+"_gout,");
            }
        }
        res.put("outputs",res1);
        res.put("outputsnum",new Long(res1.size()));
        res.put("snd",sdata);
        if("true".equals(enabledDinamicWF))
            res.put("enabledDinamicWF","true");
        if(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("iworkflow")!=null)
        {
            String workflow=""+PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("iworkflow");
            String wfsID=PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).getWfsID();
            Hashtable hsh=new Hashtable();
            hsh.put("url", wfsID);
            ServiceType st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());
            try
            {
                PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());
                ComDataBean tmp0=new ComDataBean();
                tmp0.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                tmp0.setUserID(user);
                tmp0.setWorkflowID(workflow);
                tmp0.setJobID(""+pParams.get("j"));
                res.put("iinputs",pc.getNormalInputs(tmp0));
                res.put("ioutputs",pc.getNormalOutputs(tmp0));
            }
            catch(Exception e){e.printStackTrace();}
        }
        res.put("iworkflow", ""+PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("iworkflow"));
        res.put("service", ""+PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("servicetype"));
        
        res.put("jobID",PortalCacheService.getInstance().getUser(user).getEditingJobData().getName());
        res.put("jobname",PortalCacheService.getInstance().getUser(user).getEditingJobData().getName());
        res.put("jobtxt",PortalCacheService.getInstance().getUser(user).getEditingJobData().getTxt());
        if (PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype") != null) {
            res.put("jobgridtype",PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype"));            
        }
        res.put("jobid",""+PortalCacheService.getInstance().getUser(user).getEditingJobData().getId());
       //gemlca
        if ("gemlca".equals("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype"))) {
            try {
                String[] gparams = ("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("params")).split(" ");
                Vector vp = GemlcaCacheService.getInstance().getGLCparams("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("grid"), "" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("resource"));
                if (vp.size() == gparams.length) {


                    Vector vpi = new Vector();//input files
                    Vector vpitxt = new Vector();//input files friendlyName
                    Vector vpo = new Vector();//output files
                    Vector vpotxt = new Vector();//output files friendlyName
                    for (int j = 0; j < vp.size(); j++) {
                        if (((HashMap) vp.get(j)).get("file").equals("true")) {
                            if (((HashMap) vp.get(j)).get("input") != null && ((HashMap) vp.get(j)).get("input").equals("true")) {
                                //vpi.add("" + ((HashMap) vp.get(j)).get("value"));//nin++;
                                vpi.add(gparams[j]);
                                vpitxt.add("" + ((HashMap) vp.get(j)).get("friendlyName"));
                            } else {
                                //vpo.add("" + ((HashMap) vp.get(j)).get("value"));//nout++;
                                vpo.add(gparams[j]);
                                vpotxt.add("" + ((HashMap) vp.get(j)).get("friendlyName"));
                            }
                        }
                    }
                    res.put("gininames", vpi);
                    res.put("goutinames", vpo);
                    res.put("ginfriendlyNames", vpitxt);
                    res.put("goutfriendlyNames", vpotxt);
                } else {
                    res.put("gmsg", "ERROR! Correct and save the Job Executable configuration!!");
                }
            } catch (Exception e) {
                res.put("gmsg", "ERROR! Correct and save the Job Executable configuration!");
                e.printStackTrace();
            }
        }
        return res;
    }    

}

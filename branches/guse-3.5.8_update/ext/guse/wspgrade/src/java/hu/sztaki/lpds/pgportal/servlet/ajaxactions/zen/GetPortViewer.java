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
 * GetPortView.java
 * Preaparation of I/O handling in case of job configuration
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions.zen;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.PortData;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.BASEActions;
import hu.sztaki.lpds.wfs.com.*;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.util.*;

/**
 * Preaparation of I/O handling in case of job configuration
 *
 * @author krisztian karoczkai
 */
public class GetPortViewer extends BASEActions
{
    
    /** Creates a new instance of GetIOView */
    public GetPortViewer() {}
    public String getOutput(Hashtable pParams){return null;}
    public String getDispacher(Hashtable pParams){return "/jsp/workflow/"+pParams.get("ws-pgrade.wftype")+"/io.jsp";}
    public Hashtable getParameters(Hashtable pParams)
    {
        String sdata="";
        Hashtable res=new Hashtable();
        String user=""+pParams.get("user");
        Vector tmp=new Vector();
        if(pParams.get("job")!=null){
            try{    res.put("oldjobname",""+PortalCacheService.getInstance().getUser(user).getEditingJobData().getName());}
            catch(Exception e){/* There is no selected job */}

            PortalCacheService.getInstance().getUser(user).setEditingJobData(""+pParams.get("job"));
        }
        tmp.add(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getInput(""+pParams.get("port")));
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
            res0.add(tmpPD); //,,
            if(tmpPD.getPreJob().equals(""))
                sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_max,input_"+((PortDataBean)tmp.get(i)).getId()+"_file,input_"+((PortDataBean)tmp.get(i)).getId()+"_remote,input_"+((PortDataBean)tmp.get(i)).getId()+"_generator,");
            else
                sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_waiting,");
            sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_intname,input_"+((PortDataBean)tmp.get(i)).getId()+"_eparam,input_"+((PortDataBean)tmp.get(i)).getId()+"_dpid,input_"+((PortDataBean)tmp.get(i)).getId()+"_hnt,input_"+((PortDataBean)tmp.get(i)).getId()+"_pequaltype,input_"+((PortDataBean)tmp.get(i)).getId()+"_pequalvalue,input_"+((PortDataBean)tmp.get(i)).getId()+"_pequalinput,");
            if(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("iworkflow")!=null)
            {
                sdata=sdata.concat("input_"+((PortDataBean)tmp.get(i)).getId()+"_iinput,");
            }
            fileUpload=fileUpload.concat("setFile0Upload('upload','input_"+tmpPD.getId()+"_intname','"+PortalCacheService.getInstance().getUser(user).getEditingJobID()+"');");
        }
        res.put("inputs",res0);
        res.put("inputsnum",new Long(res0.size()));
        res.put("fileUpload",fileUpload);
        
        tmp=((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getOutputs();
        for(int i=0;i<tmp.size();i++) 
        {
            PortData tmpPD=new PortData(new Hashtable(((PortDataBean)tmp.get(i)).getData()), new Hashtable(((PortDataBean)tmp.get(i)).getDataDisabled()),new Hashtable(((PortDataBean)tmp.get(i)).getLabel()),new Hashtable(((PortDataBean)tmp.get(i)).getDesc()),new Hashtable(((PortDataBean)tmp.get(i)).getInherited()));
            tmpPD.setId(""+((PortDataBean)tmp.get(i)).getId());
            tmpPD.setName(""+((PortDataBean)tmp.get(i)).getName());
            tmpPD.setTxt(""+((PortDataBean)tmp.get(i)).getTxt());
            res1.add(tmpPD);
            sdata=sdata.concat("output_"+((PortDataBean)tmp.get(i)).getId()+"_maincount,output_"+((PortDataBean)tmp.get(i)).getId()+"_intname,output_"+((PortDataBean)tmp.get(i)).getId()+"_type,");
            if(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("iworkflow")!=null)
            sdata=sdata.concat("output_"+((PortDataBean)tmp.get(i)).getId()+"_maincount,output_"+((PortDataBean)tmp.get(i)).getId()+"_ioutput,");
            
        }
        res.put("outputs",res1);
        res.put("snd",sdata);
        
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
        
        res.put("jobID",pParams.get("job"));

        
        return res;
    }    
    
}

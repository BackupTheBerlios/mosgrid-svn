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
 * GetDescriptionView.java
 * Prepares the editing of the descriptor in case job configuration 
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions.zen;

import hu.sztaki.lpds.pgportal.servlet.ajaxactions.*;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Prepares the editing of the descriptor in case job configuration
 *
 * @author krisztian karoczkai
 */
public class GetDescriptionView extends BASEActions
{
    
    /** Creates a new instance of GetDescriptionView */
    public GetDescriptionView() {}
    @Override
    public String getOutput(Hashtable pParams){return null;}
    @Override
    public String getDispacher(Hashtable pParams){return "/jsp/workflow/"+pParams.get("ws-pgrade.wftype")+"/edit.jsp";}
    @Override
    public Hashtable getParameters(Hashtable pParams)
    {
        Hashtable res=new Hashtable();
        String user=""+pParams.get("user");
        List<String> cmidd=new ArrayList<String>();
        if(PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype")!=null)
           cmidd.add((String)PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("gridtype"));

        if(PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("mbt")!=null){
            HashMap md=PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe();
            Iterator<String> it=md.keySet().iterator();
            String key;
            String[] nam;
            while(it.hasNext()){
                key=it.next();
                nam=((String)PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get(key)).split("_");
                cmidd.add(nam[0]);
            }
        }
        res.put("jobdesc",new Hashtable());
        Hashtable tmp;
        for(String t:cmidd){
        System.out.println("LEIRO gridtype:"+t);
            tmp=getMiddlewareDescriptions(t);
            if(tmp.size()>0) ((Hashtable)res.get("jobdesc")).putAll(tmp);
        }
        
        res.put("jobname",PortalCacheService.getInstance().getUser(user).getEditingJobData().getName());
        res.put("jobtxt",PortalCacheService.getInstance().getUser(user).getEditingJobData().getTxt());

        GetDescriptionValues t=new GetDescriptionValues();
        res.putAll(t.getParameters(pParams));
        return res;
    }    

    private Hashtable getMiddlewareDescriptions(String pMd){
        return PortalMessageService.getI().getMessagesWithId(pMd+".key");
    }
}

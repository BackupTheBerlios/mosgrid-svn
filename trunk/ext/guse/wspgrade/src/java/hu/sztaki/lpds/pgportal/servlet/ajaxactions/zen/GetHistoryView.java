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
 * GetHistoryView.java
 * View the history of the job configuration
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions.zen;

import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.BASEActions;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.Hashtable;

/**
 * View the history of the job configuration
 *
 * @author krisztian karoczkai
 */
public class GetHistoryView extends BASEActions
{
    
    public GetHistoryView() { }
    
    public String getOutput(Hashtable pParams){return null;}
    
    public String getDispacher(Hashtable pParams){return "/jsp/workflow/"+pParams.get("ws-pgrade.wftype")+"/edit.jsp";}
 
    public Hashtable getParameters(Hashtable pParams)
    {
        Hashtable res=new Hashtable();

        String user=""+pParams.get("user");
        res.put("jobchis",((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getHistory());
        res.put("jobname",PortalCacheService.getInstance().getUser(user).getEditingJobData().getName());
        res.put("jobtxt",PortalCacheService.getInstance().getUser(user).getEditingJobData().getTxt());
        
        return res;
    }

}

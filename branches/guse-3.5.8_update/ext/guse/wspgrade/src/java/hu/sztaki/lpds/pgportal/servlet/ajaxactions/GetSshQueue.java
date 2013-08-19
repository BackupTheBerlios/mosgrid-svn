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
 * GetSshQueue.java
 * Job configuration.
 * Returns Queues for ssh resources (pbs, lsf)
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;

public class GetSshQueue extends BASEActions{
    @Override
    public String getDispacher(Hashtable pParams){return null;}

    @Override
    public Hashtable getParameters(Hashtable pParams){return null;}

    @Override
    public String getOutput(Hashtable pParams){
        String res=new String();
        String user=""+pParams.get("user");

        String grid="";
        String resource="";
        String midleware="";
        if(pParams.get("j")!=null) resource=""+pParams.get("j");
        else resource=""+((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("tmp*resource");
        if(pParams.get("g")!=null) grid=""+pParams.get("g");
        else grid=""+((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("tmp*grid");
        if(pParams.get("mw")!=null) midleware=""+pParams.get("mw");
        else midleware=""+((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("tmp*midleware");


        res=res.concat("job_jobmanager:: ::");
        Vector tmp=new Vector();
        //tmp=ResourceXMLHandler.getServices((Hashtable<String,Hashtable<String,Hashtable<String,Vector<String>>>>)ps.getAttribute("resources"),midleware,grid,resource);
//        tmp=SshResourceService.getI().getSshQueuesforHost(midleware, user,resource);
        tmp= ConfigHandler.getResources((List<Middleware>)getSessionVariable("resources"), midleware, resource);
        for(int i=0;i<tmp.size();i++){res=res.concat("::"+tmp.get(i));}
        res=res.concat("::");

        return res;
    }

}

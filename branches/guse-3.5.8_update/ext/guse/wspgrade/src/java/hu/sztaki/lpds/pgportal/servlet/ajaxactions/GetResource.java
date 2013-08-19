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
 * GetResource.java
 * query of resources
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;

/**
 * query of resources
 *
 * @author krisztian karoczkai
 */
public class GetResource extends BASEActions
{
    
    /** Creates a new instance of GetResource */
    public GetResource() {}
    
    @Override
    public String getDispacher(Hashtable pParams){return null;}
    @Override
    public Hashtable getParameters(Hashtable pParams){return null;}
    @Override
    public String getOutput(Hashtable pParams){
        String res=new String("job_resource:: ::");
        String user=""+pParams.get("user");
        String data=""+pParams.get("j");
        String midleware=""+pParams.get("mw");
        if(pParams.get("j")==null)data="";//+((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("grid");
        else ((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().put("tmp*grid", data);
        if(data.equals(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("grid")))
        {
            res=res.concat(""+((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("resource"));
        }
        
        Vector tmp=new Vector();
        try
        {
            tmp=ConfigHandler.getResources((List<Middleware>)getSessionVariable("resources"),midleware,data);

            for(int i=0;i<tmp.size();i++)res=res.concat("::"+tmp.get(i));
            if (!tmp.isEmpty()) {
                ((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().put("tmp*resource", ""+tmp.get(0));
            }
        }
        catch(Exception w){w.printStackTrace();}
        res=res.concat("::GetData");
        return res;
    
    }

}

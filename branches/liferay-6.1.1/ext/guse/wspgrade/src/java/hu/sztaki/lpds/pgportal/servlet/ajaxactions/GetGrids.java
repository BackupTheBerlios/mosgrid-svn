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
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import dci.data.Middleware;

/**
 * Handling of grid resorurce realted definitons during job configuration.
 *
 * @author krisztian karoczkai
 */
public class GetGrids  extends BASEActions
{
    
    /** Creates a new instance of GetGrids */
    public GetGrids() {}

    @Override
    public String getDispacher(Hashtable pParams)
    {
        return null;
    }
    @Override
    public Hashtable getParameters(Hashtable pParams)
    {
        return null;
    }
    @Override
    public String getOutput(Hashtable pParams)
    {
        String res=new String("job_grid:: ::");
        String user=""+pParams.get("user");
        String data=""+pParams.get("j");

//composing a list
        Vector tmp=ConfigHandler.getGroups(( List<Middleware>)getSessionVariable("resources"), data);

        if(data==null)data="";//+((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("gridtype");
        
        if(data.equals(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("gridtype")))
        {
            res=res.concat(""+((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("grid"));
            ((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().put("tmp*grid", ""+((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("grid"));
        }
        else {((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().put("tmp*grid", ""+tmp.get(0));}
        for(int i=0;i<tmp.size();i++)res=res.concat("::"+tmp.get(i));
        if(!data.equals(""))((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().put("tmp*gridtype", data);
        res=res.concat("::GetResource");

        return res;
    }
}

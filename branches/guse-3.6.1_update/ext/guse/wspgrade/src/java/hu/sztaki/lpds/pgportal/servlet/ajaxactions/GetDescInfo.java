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
 * GetDescInfo.java
 * get selected comment to job descriptor
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.Hashtable;

/**
 * get selected comment to job descriptor
 *
 * @author krisztian karoczkai
 */
public class GetDescInfo  extends BASEActions
{
    
    public GetDescInfo() {}
    
    public String getDispacher(Hashtable pParams){return null;}
    
    public Hashtable getParameters(Hashtable pParams){return null;}
    
    public String getOutput(Hashtable pParams)
    {   
        String user=""+pParams.get("user");

        String res="";
        res=PortalMessageService.getI().getMessage((""+pParams.get("j")).replaceFirst("key", "text"))+"::";
        if(((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getDesc().get(""+pParams.get("j"))!=null)
        {res=res.concat(""+((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getDesc().get(""+pParams.get("j")));}
        return res;
    }
}

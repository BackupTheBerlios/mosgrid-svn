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
 * GetDescriptionValues.java
 * retrives the descriptor of the selected job
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.wfs.com.*;
import java.util.*;

/**
 * retrives the descriptor of the selected job
 *
 * @author krisztian karoczkai
 */
public class GetDescriptionValues  extends BASEActions
{
    
    /** Creates a new instance of GetDescriptionValues */
    public GetDescriptionValues() {}

    public String getOutput(Hashtable pParams){return null;}
    public String getDispacher(Hashtable pParams){return "/jsp/workflow/descvalue.jsp";}
    public Hashtable getParameters(Hashtable pParams)
    {
        Hashtable res=new Hashtable();
        String user=""+pParams.get("user");
        HashMap tmp=new HashMap();
        tmp=((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getDesc();
        
        res.put("keys",tmp); 
        return res;
    }    
}

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
 * GetIInpit.java
 * Beagyazott workflowk inputjainak meghatarozasa
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Defines the inputs of an embedded workflow
 *
 * @author krisztian karoczkai
 */
public class GetIInput  extends BASEActions
{
    
    /** Creates a new instance of GetData */
    public GetIInput() {}
    
    public String getDispacher(Hashtable pParams){return null;}
    public Hashtable getParameters(Hashtable pParams){return null;}
    public String getOutput(Hashtable pParams)
    {
        String res=""+pParams.get("d")+"::";
        String user=""+pParams.get("user");
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
                Vector tmp=pc.getNormalInputs(tmp0);
                for(int i=0;i<tmp.size();i++)
                {
                    res=res.concat("::"+tmp.get(i));
                }
        }
        catch(Exception e){e.printStackTrace();}
        res=res.concat("::");
        
        return res;
    }
    
}

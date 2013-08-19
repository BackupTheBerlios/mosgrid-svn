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
 * GetUnicoreIDBTools.java
 * Job konfiguracios adatok megjelenitese
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import dci.data.Middleware;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.util.resource.UnicoreIDBToolHandler;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;

import java.util.Hashtable;
import java.util.List;
import java.util.Collections;
import java.util.Vector;


/**
 * Ajax extension
 *
 * @author sandra gesing, patrick schäfer
 */
public class GetUnicoreIDBTools extends BASEActions
{

    @Override
    public String getDispacher(Hashtable pParams){return null;}

    @Override
    public Hashtable getParameters(Hashtable pParams){return null;}

    @Override
    public String getOutput(Hashtable pParams) {
        StringBuilder res = new StringBuilder("job_jobmanager::");
        String user = ""+pParams.get("user");

        String resource = "";
        if(pParams.get("j") != null) {
        	resource=""+pParams.get("j");
        }
        else {
            resource=""+((JobPropertyBean)PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().get("tmp*grid");
        }

        List<Middleware> sessionConfig = (List<Middleware>) getSessionVariable("resources");

        Vector<String> tmp = UnicoreIDBToolHandler.getIDBTools(user, resource, sessionConfig);
        Collections.sort(tmp, String.CASE_INSENSITIVE_ORDER);

        for(int i=0; i<tmp.size(); i++){
        	res.append("::").append(tmp.get(i));
        }
        res.append("::");

        System.out.println("Called Job-Manager: " + res);

        return res.toString();
    }


}

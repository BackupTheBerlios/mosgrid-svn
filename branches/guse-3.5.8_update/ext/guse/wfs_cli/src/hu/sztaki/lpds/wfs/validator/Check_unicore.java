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
/**
 * Checking of UNICORE middleware configuration
 */

package hu.sztaki.lpds.wfs.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author krisztian
 */
public class Check_unicore extends Check_local {

    /**
     * @see ConfigureValidatorFace#getJobErrors(java.util.HashMap)
     */
    @Override
    public List<String> getJobErrors(HashMap<String, String> pProps) {
        List<String> res = new ArrayList<String>();
        if(pProps.get("grid")==null) { 
            res.add("error.executejob.grid");
        }
        if((pProps.get("binary")==null
               || pProps.get("binary").trim().equals(""))
           && (pProps.get("jobmanager") == null
               || pProps.get("jobmanager").trim().equals("")) // the idb tool could be set instread
           ) {
            res.add("error.executejob.binary");
        }
        if(pProps.get("type")==null) {
            res.add("error.executejob.type");
        }
        if("MPI".equals(pProps.get("type"))){
            if(pProps.get("nodenumber")==null) res.add("error.executejob.nodenumber");
            else{
                try{
                    int i=Integer.parseInt(pProps.get("nodenumber"));
                    if(i<1) res.add("error.executejob.nodenumber");
                }
                catch(Exception e){res.add("error.executejob.nodenumber");}
            }
        }
        return res;
    }
}

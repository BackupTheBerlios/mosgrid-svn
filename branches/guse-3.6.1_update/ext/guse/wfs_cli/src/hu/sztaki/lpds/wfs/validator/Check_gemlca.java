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
 * Checking of gemlca middleware job
 */

package hu.sztaki.lpds.wfs.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author krisztian
 */
public class Check_gemlca extends Check_local implements ConfigureValidatorFace
{
    @Override
    public List<String> getJobErrors(HashMap<String, String> pProps) {
        List<String> res=new ArrayList<String>();
        if(pProps.get("resource")==null) res.add("error.job.mainnoconfig");

        return res;
    }

}

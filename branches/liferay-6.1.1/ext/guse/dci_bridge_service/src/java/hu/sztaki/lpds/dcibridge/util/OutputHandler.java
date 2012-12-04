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
 * Operations with outputs
 */

package hu.sztaki.lpds.dcibridge.util;

import java.util.ArrayList;
import java.util.List;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;

/**
 * @author krisztian karoczkai
 */
public class OutputHandler {
    
    public static List<DataStagingType> getOutputs(JobDefinitionType pJSDL){
        List<DataStagingType> ports=pJSDL.getJobDescription().getDataStaging();
        List<DataStagingType> res=new ArrayList<DataStagingType>();
        
        for(DataStagingType t:ports) if(t.getTarget()!=null) res.add(t);
        return res;
    }

    public static List<DataStagingType> getRemoteOutputs(JobDefinitionType pJSDL){
        List<DataStagingType> ports=pJSDL.getJobDescription().getDataStaging();
        List<DataStagingType> res=new ArrayList<DataStagingType>();

        for(DataStagingType t:ports)
            if(t.getTarget()!=null)
                if(!t.getTarget().getURI().startsWith("http"))
                    res.add(t);
        return res;
    }

    public static List<DataStagingType> getLocalOutputs(JobDefinitionType pJSDL){
        List<DataStagingType> ports=pJSDL.getJobDescription().getDataStaging();
        List<DataStagingType> res=new ArrayList<DataStagingType>();

        for(DataStagingType t:ports)
            if(t.getTarget()!=null)
                if(t.getTarget().getURI().startsWith("http"))
                    res.add(t);
        return res;
    }

    public static String getOutputCatalog(DataStagingType pOutput){
        return (String)pOutput.getAny().get(0);
    }

}

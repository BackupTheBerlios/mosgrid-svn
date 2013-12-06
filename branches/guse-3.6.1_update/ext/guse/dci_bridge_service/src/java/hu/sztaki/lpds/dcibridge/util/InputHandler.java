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
 * Operations for input handling
 */

package hu.sztaki.lpds.dcibridge.util;

import hu.sztaki.lpds.dcibridge.service.Job;
import java.util.ArrayList;
import java.util.List;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;

/**
 * @author krisztian karoczkai
 */
public class InputHandler {
/**
 * Defining of job inputs on the grounds of JSDL 
 * @param pValue Job descriptor object instance
 * @return Input list
 */
    public static List<DataStagingType> getInputs(Job pValue){
        List<DataStagingType> ports=pValue.getJSDL().getJobDescription().getDataStaging();
        List<DataStagingType> res=new ArrayList<DataStagingType>();
        for(DataStagingType t:ports) if(t.getSource()!=null) res.add(t);
        return res;
    }

/**
 * Defining of remote outputs on the grounds of JSDL
 * @param pValue Job descriptor object instance
 * @return Input list
 */
    public static List<DataStagingType> getRemoteInputs(Job pValue){
        List<DataStagingType> ports=pValue.getJSDL().getJobDescription().getDataStaging();
        List<DataStagingType> res=new ArrayList<DataStagingType>();
        for(DataStagingType t:ports)
            if(t.getSource()!=null)
                if(!t.getSource().getURI().startsWith("http"))
                    res.add(t);
        return res;
    }

/**
 * Defining of job local inputs on the grounds of JSDL
 * @param pValue Job descriptor object instance
 * @return Input list
 */    
    public static List<DataStagingType> getlocalInputs(Job pValue){
        List<DataStagingType> ports=pValue.getJSDL().getJobDescription().getDataStaging();
        List<DataStagingType> res=new ArrayList<DataStagingType>();
        for(DataStagingType t:ports)
            if(t.getSource()!=null)
                if(t.getSource().getURI().startsWith("http"))
                    res.add(t);
        return res;
    }


}

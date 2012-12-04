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
 * GetPropertyWindow.java 
 * Yields a configuration window to an input field of property type 
 */

package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import java.util.Hashtable;

/**
 * @author krisztian karoczkai
 */
public class GetPropertyWindow extends BASEActions
{
    
    /** Creates a new instance of GetPropertyWindow */
    public GetPropertyWindow() {}

    public String getOutput(Hashtable pParams){return null;}
    public String getDispacher(Hashtable pParams){return "/props/"+pParams.get("job")+".jsp";}
    public Hashtable getParameters(Hashtable pParams)
    {
        return pParams;
    }    
}

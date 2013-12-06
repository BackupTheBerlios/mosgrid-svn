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
 * Checking of configuration
 */

package hu.sztaki.lpds.wfs.validator;

import java.util.Hashtable;

/**
 * @author krisztian
 */
public class ConfigureChecker
{
    private static Hashtable<String,ConfigureValidatorFace> checkers= new Hashtable<String, ConfigureValidatorFace>();

/**
 * Query configuration checker plugin
 * @param pMiddleware midlleware type
 * @return checker plugin instance
 * @throws java.lang.NullPointerException No plugin for middleawre
 */
    public static ConfigureValidatorFace getI(String pMiddleware) throws NullPointerException
    {
        if(checkers.get(pMiddleware)==null){
            try{
                ConfigureValidatorFace tmp=(ConfigureValidatorFace)Class.forName("hu.sztaki.lpds.wfs.validator.Check_"+pMiddleware).newInstance();
                checkers.put(pMiddleware, tmp);
            }
            catch(Exception e){
                /*ellenorzo plugin nem toltheto be*/
                new NullPointerException("configure checker plugin("+pMiddleware+") not avaible");
            }
        }
        return checkers.get(pMiddleware);
    }
}

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
 * InformationClient.java
 * IS communication client definition
 */

package hu.sztaki.lpds.information.inf;

import hu.sztaki.lpds.information.com.ServiceType;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author krisztian karoczkai
 */
public interface InformationClient extends BaseCommunicationFace
{
    

    
/**
 * Service query
 * @param pStype Service type
 * @param pFrom Calling from
 * @param pParam Call parameters
 * @param pFault Not eligible services
 * @return Service descriptor
 * @see ServiceType
 * @throws java.lang.Exception Communication error
 */    
    public ServiceType getService(String pStype, String pFrom, Hashtable pParam, Vector pFault) throws Exception;
    
/**
 * Service queries
 * @param pStype Service type
 * @param pFrom Calling from
 * @param pParam Call parameters
 * @return Service list ServiceType with service descriptors
 * @throws java.lang.Exception Communication error
 */    
    public Vector getAllService(String pStype, String pFrom, Hashtable pParam) throws Exception;

/**
 * Working parameters' request
 * @param pServiceURL URL of the service making the request
 * @return property list
 * @throws java.lang.Exception Communication error
 */
    public HashMap getAllProperties(String pServiceURL) throws Exception;
}

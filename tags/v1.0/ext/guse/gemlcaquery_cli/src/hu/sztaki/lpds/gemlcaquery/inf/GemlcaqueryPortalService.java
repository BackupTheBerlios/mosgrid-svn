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
package hu.sztaki.lpds.gemlcaquery.inf;

import hu.sztaki.lpds.gemlcaquery.com.GemlcaqueryDataBean;
//import hu.sztaki.lpds.gemlcaquery.service.anett.glclist.GLCListUtils;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author lpds
 */
public interface GemlcaqueryPortalService {
    
    /**
     * This is a gemlca service, it requests the parameters of the legacy code.
     *
     * The return value is a vector,
     * every element of it is a hashtable,
     * the GLC parameters are in the hashtables.
     *
     * @param bean The parameters describing the request
     * @return List of the GLCs
     */
    public Vector getGLCList(GemlcaqueryDataBean bean);
    
    /**
     * It requests the legacy code (GLC) list of a gemlca service.
     *
     * The return value is a vector,
     * every element of it is a hashtable,
     * the parameters are in the hashtables.
     *
     * @param bean The parameters describing the request
     * @return List of the GLC parameters
     */
    public Vector getGLCParameterList(GemlcaqueryDataBean bean);
    
    /**
     * Not implemented
     */
    public Hashtable getGLCwithParameterList(GemlcaqueryDataBean bean);
    
}

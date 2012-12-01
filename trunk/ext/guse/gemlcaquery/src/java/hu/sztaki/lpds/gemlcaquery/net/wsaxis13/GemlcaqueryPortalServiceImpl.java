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
package hu.sztaki.lpds.gemlcaquery.net.wsaxis13;

import hu.sztaki.lpds.gemlcaquery.com.GemlcaqueryDataBean;
import hu.sztaki.lpds.gemlcaquery.service.anett.glclist.GLCListUtils;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author lpds
 */
public class GemlcaqueryPortalServiceImpl {
    
    public GemlcaqueryPortalServiceImpl() {
    }
    
    /**
     * Queries the legacy code parameters of a gemlca service.
     *
     * The return value is a vector,
     * every element of it is a hashtable,
     * the GLC parameters are in the hashtables.
     *
     * @param bean The parameters describing the request 
     * @return List of the GLCs 
     */
    public Vector getGLCList(GemlcaqueryDataBean bean) {
       /* System.out.println("----getGLCList-----");
        System.out.println("GemlcaUrl : " + bean.getGemlcaUrl());
        System.out.println("GLCName   : " + bean.getGlcName());
        */
     //   System.out.println("Usercert  : " + bean.getUsercert());
        return new GLCListUtils().listLC(bean);
    }
    
    /**
     * Queries the legacy code (GLC) list of a gemlca service. 
     *
     * The return value is a vector,
     * every element of it is a hashtable,
     * the parameters are in the hashtables.
     *
     * @param bean The parameters describing the request 
     * @return List of the GLC parameters 
     */
    public Vector getGLCParameterList(GemlcaqueryDataBean bean) {
      /*  System.out.println("****getGLCParameterList----");
        System.out.println("GemlcaUrl : " + bean.getGemlcaUrl());
        System.out.println("GLCName   : " + bean.getGlcName());
       */
      //  System.out.println("Usercert  : " + bean.getUsercert());
        return new GLCListUtils().getLCParameters(bean);
    }
    /**
     * Queries the legacy code (GLC) list of a gemlca service with parameters.
     *
     * The return value is a hashtable,
     * the "-GLCLIST-" element is a vector with the LC list(vector)
     * further elements
     * key:lc value:lcparameters(vector)
     *
     * @param bean The parameters describing the request 
     * @return List of the GLC parameters 
     */
    public Hashtable getGLCwithParameterList(GemlcaqueryDataBean bean) {
      /*  System.out.println("getGLCwithParameterLis");
        System.out.println("GemlcaUrl : " + bean.getGemlcaUrl());
       */
     //   System.out.println("Usercert  : " + bean.getUsercert());
        return new GLCListUtils().getLcP(bean);
    }    
}

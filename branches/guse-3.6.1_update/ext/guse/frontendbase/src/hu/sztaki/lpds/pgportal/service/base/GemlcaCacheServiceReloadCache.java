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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.pgportal.service.base;

import java.util.Vector;

/**
 *
 * @author csig
 */
public class GemlcaCacheServiceReloadCache extends Thread {
    private String certfile;
    private String GLCurl;
    private Vector vLC;

    public GemlcaCacheServiceReloadCache(String pcertfile, String pGLCurl, Vector pvLC) {
        certfile = pcertfile;
        GLCurl = pGLCurl;
        vLC = pvLC;

    }

    public void run() {
        //cacheing.put(GLCurl, "" + true);
        try {
            sysLog("GEMLCA LC list: reloading.... " + GLCurl);
            // sysLog(""+getGLCwithParameterList(getFileAllLineValue(certfile), GLCurl) );
            for (int i = 0; i < vLC.size(); i++) {

                String GLCid = ((String) vLC.get(i)).split(":", 2)[0];
                if (!GLCid.startsWith("gusedelimit---")){
                Vector vLCparams = GemlcaCacheServiceServices.getInstance().getactualGLCParamList(certfile, GLCurl, GLCid);
                GemlcaCacheService.getInstance().setGurlGLC(GLCurl, GLCid, vLCparams);
                }
            }
            sysLog("GEMLCA LC list: reloading....end: " + GLCurl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GemlcaCacheService.getInstance().setReloadingStatusFalse(GLCurl);

    }

    /** std.out-ra logol
     */
    private void sysLog(String txt) {
         System.out.println("GEMLCACACHERELOAD " + txt);
    }
}

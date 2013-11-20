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
 * gLite config handler
 */
package hu.sztaki.lpds.submitter.grids.glite.config;

import dci.data.Item.Glite;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

public class GLiteConfig {

    private static GLiteConfig instance = null;
    private static Hashtable<String, String> vo = new Hashtable<String, String>();
    private static Hashtable<String, Glite> voparam = new Hashtable<String, Glite>();
    final static Object lLock = new Object();

    public GLiteConfig() {
    }

    /**
     * Returns the objects static instance
     * @return static object instance;
     */
    public static GLiteConfig getI() {
        if (instance == null) {
            instance = new GLiteConfig();
        }
        return instance;
    }
    
    public String getWMProxyUrl(String voname) throws Exception{
        if (!vo.containsKey(voname)){
           vo.put(voname, getWms(voname));
        }
        return vo.get(voname);
    }
    
    /**
     * Get the wms url from /opt/glite/etc/$vo/glite_wmsui.conf
     * @param vo
     * @return
     * @throws java.lang.Exception
     */
    private String getWms(String pvo) throws Exception {
        File wms = null;
        try {
            wms = new File("/etc/glite-wms/" + pvo + "/glite_wmsui.conf");
            if (!wms.exists()) {
                throw new Exception();
            }
        } catch (Exception e) {
            try {
                wms = new File("/opt/glite/etc/" + pvo + "/glite_wmsui.conf");
                if (!wms.exists()) {
                    throw new Exception();
                }
            } catch (Exception e1) {
                throw new Exception("The file glite_wmsui.conf for VO " + pvo + " is neither in '/etc/glite-wms/' nor in '/opt/glite/etc/' can not be found!");
            }
        }
        BufferedReader input = new BufferedReader(new FileReader(wms));
        try {
            String line = null;
            String wmp = "";//WMProxyEndpoints
            boolean startread = false;
            while ((line = input.readLine()) != null) {
                if (!line.trim().startsWith("#")) {
                    if (line.contains("WMProxyEndpoints")) {
                        startread = true;
                        if (line.contains("}")) {
                            wmp += line;
                            break;
                        }
                    }
                    if (startread) {
                        wmp += line;
                        if (line.contains("}")) {
                            break;
                        }
                    }
                }
            }
            if (!startread) {
                throw new Exception("WMS for VO " + pvo + " could not found in glite_wmsui.conf");
            }
            try {//return the first WMProxy url
                return wmp.substring(wmp.indexOf("{") + 1, wmp.indexOf("}")).split(",")[0].replaceAll("\"", "").trim();
            } catch (Exception e) {
                throw new Exception("WMS for VO " + pvo + " could not found in glite_wmsui.conf");
            }
        } finally {
            input.close();
        }
    }
    
    public Hashtable<String, Glite> voParam(){
        return voparam;
    }

    /**
     * lock WMS service call
     * @return
     */
    public Object getLock() {
        return lLock;
    }
}

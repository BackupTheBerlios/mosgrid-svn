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
 * GemlcaCacheService.java
 *
 * Gemlca LC es parameterlista cache
 */
package hu.sztaki.lpds.pgportal.service.base;

import hu.sztaki.lpds.information.local.PropertyLoader;
import java.util.Enumeration;
import java.util.Vector;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CSig
 */
public class GemlcaCacheService  {

    private static GemlcaCacheService instance = null;
    private static int refreshintervall = 60;//frissitsen, ha régebbi a cache, mint refreshintervall percben
    private ConcurrentHashMap cacheing = new ConcurrentHashMap();//folyamatban levo cacheing (blokkoló)
    private ConcurrentHashMap reloading = new ConcurrentHashMap();//folyamatban levo frissitesek
    private ConcurrentHashMap<String,ConcurrentHashMap<String,Vector>> Gurl = new ConcurrentHashMap(); //gemlca servicek listaja, ConcurrentHashMap-ben LC-parameterekkel
    private ConcurrentHashMap<String, Long> Gurlfreshed = new ConcurrentHashMap();//gemlca servicek listaja utolso frissites
    /** Creates a new instance of GemlcaCacheService */
    public GemlcaCacheService() {
        try {
            refreshintervall = Integer.parseInt("" + PropertyLoader.getInstance().getProperty("gemlcacache.refreshintervall_min"));
        } catch (Exception e) {
        }
        sysLog("refreshintervall= " + refreshintervall + " min");
    }

    /**
     * Visszaadja az objektum statikus peldanyat
     * @return statikus objektum peldany;
     */
    public static GemlcaCacheService getInstance() {
        if (instance == null) {
            instance = new GemlcaCacheService();
        }
        return instance;
    }

    public void setReloadingStatusFalse(String GLCurl) {
        reloading.remove(GLCurl);
    }

    /**
     * Visszaadja, hogy mikor frissult a megadott gurl-hez tartozo cache, 0 ha nincs tarolt ertek
     * @param GLCurl Gemlca service url (grid)
     */
    public long getGurlfreshedAt(String GLCurl) {
        try {
            return Gurlfreshed.get(GLCurl);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * Felulirja a megadott GLCurl-hez tartozo GLCket+parametereit ghs-el
     * Tarolja, hogy a hivas idopontjaban (System.currentTimeMillis()) frissult a megadott gurl-hez tartozo cache
     * @param GLCurl Gemlca service url (grid)
     * @param GLCid gemlca legacy code id
     * @param GLCparams glc parameters
     */
    public void setGurlGLC(String GLCurl, String GLCid, Vector GLCparams) {
        Gurl.get(GLCurl).put(GLCid, GLCparams);
        Gurlfreshed.put(GLCurl, System.currentTimeMillis());
    }

    /**
     * Gemlca LCode listat ad vissza. Cache kezeles.
     * @param certfile - cert file eleresi ut
     * @param GLCurl - Gemlca service url (grid)
     * @param inputnum - Ennyi input porttal rendelkezo LC-k lesznek a listaban. Ha &lt 0 , akkor minden LC-t visszaad
     * @param outputnum - Ennyi output porttal rendelkezo LC-k lesznek a listaban. Ha &lt 0 , akkor minden LC-t visszaad
     * @return GLC lista
     */
    public Vector getGLCList(String certfile, String GLCurl, int inputnum, int outputnum) throws Exception {
        Vector vLCfilt = new Vector();
        Vector vLC = GemlcaCacheServiceServices.getInstance().checkProxygetactualGLCList(certfile, GLCurl);

        //Gurl=new Hashtable();//nincs cache (csak debug)

        if (Gurl.get(GLCurl) == null) {// first load
            Gurl.put(GLCurl, new ConcurrentHashMap<String, Vector>());
            cacheing.put(GLCurl, "" + true);
            try {
                sysLog("GEMLCA LC list: cacheing....");
                // sysLog(""+getGLCwithParameterList(getFileAllLineValue(certfile), GLCurl) );
                for (int i = 0; i < vLC.size(); i++) {
                    Vector vLCparams = new Vector();
                    String GLCid = (((String) vLC.get(i)).split(":", 2)[0]);
                    if (!GLCid.startsWith("gusedelimit---")){
                    vLCparams = GemlcaCacheServiceServices.getInstance().getactualGLCParamList(certfile, GLCurl, GLCid);
                    setGurlGLC(GLCurl, GLCid, vLCparams);
                    }
                }
                sysLog("GEMLCA LC list: cacheing....end: ");
            } catch (Exception e) {
                cacheing.remove(GLCurl);
                throw new Exception(e);
            }
            cacheing.remove(GLCurl);

        } else if (cacheing.get(GLCurl) != null) {// first load, still loading
            sysLog("GEMLCA LC list Sleep.. cacheing:" + cacheing);

            while (cacheing.get(GLCurl) != null) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sysLog("GEMLCA LC list Wake up: cached::::::" + cacheing);
        } else {// can load from cache, refresh if necessary
            sysLog("GEMLCA LC list: cached....");
            if (reloading.get(GLCurl) == null) {
                try {
                    long elapsedtime = System.currentTimeMillis() - Gurlfreshed.get(GLCurl);
                    if ((elapsedtime > refreshintervall * 60000)) {
                        reloading.put(GLCurl, "" + true);
                        sysLog("RELOADING, elapsedtime " + elapsedtime);
                        new GemlcaCacheServiceReloadCache(certfile, GLCurl, vLC).start();
                    }
                } catch (Exception e) {
                }
            } else {
                sysLog("GEMLCA LC list: reloading....");
            }
        }

        if (inputnum < 0 || outputnum < 0) {//minden glc-t visszaad
            sysLog("minden LC");
            return vLC;
        } else {
            sysLog("parameterek szamolasa");
            for (int i = 0; i < vLC.size(); i++) {//parameterek szamolasa
                int nin = 0;
                int nout = 0;
                Vector vp = getGLCparams(GLCurl, (String) vLC.get(i), certfile);
                for (int j = 0; j < vp.size(); j++) {
                    if (((HashMap) vp.get(j)).get("file").equals("true")) {
                        if (((HashMap) vp.get(j)).get("input") != null && ((HashMap) vp.get(j)).get("input").equals("true")) {
                            nin++;
                        } else {
                            nout++;
                        }
                    }
                }
                //sysLog(""+vLC.get(i)+" nin"+nin+" nout"+nout );
                if (nin == inputnum && nout == outputnum || ((String)vLC.get(i)).startsWith("gusedelimit---")) {
                    vLCfilt.add((String) vLC.get(i));
                }
            }
        }
        //sysLog("Gurlhash: "+Gurl);
        return vLCfilt;
    }

    /**
     * Gemlca LCode-hoz tartozo parameterlistat adja vissza a cache-bol (vektorban hash)
     * @param GLCurl Gemlca service url
     * @param GLC LCode
     * @return GLC parameter lista
     */
    public Vector getGLCparams(String GLCurl, String GLC) throws Exception {
        Vector vLC = new Vector();
        String GLCid = GLC.split(":", 2)[0];
        if (!GLCid.startsWith("gusedelimit---")){
        Vector vLCparams = (Vector) ( Gurl.get(GLCurl).get(GLCid));
        sysLog("getGLCparams GLCurl:" + GLCurl + " GLC:" + GLC + " vLCparams.size:" + vLCparams.size());
        for (int j = 1; j < vLCparams.size(); j++) {
            vLC.add(vLCparams.get(j));
        }
        }
        return vLC;
    }

    /**
     * Gemlca LCode-hoz tartozo parameterlistat adja vissza a cache-bol (vektorban hash)
     * ha nincs a cache-ben, betolti.
     * @param GLCurl Gemlca service url
     * @param GLC LCode
     * @param certfile
     * @return GLC parameter lista
     */
    public Vector getGLCparams(String GLCurl, String GLC, String certfile) throws Exception {
        Vector vLC = new Vector();
        String GLCid = GLC.split(":", 2)[0];
        Vector vLCparams = new Vector();
        if (!GLCid.startsWith("gusedelimit---")){
        if ((vLCparams = Gurl.get(GLCurl).get(GLCid)) == null) {
            sysLog("getGLCparams loadnewglc------------------- param - GLCid=" + GLCid);
            vLCparams = GemlcaCacheServiceServices.getInstance().getactualGLCParamList(certfile, GLCurl, GLCid);
            setGurlGLC(GLCurl, GLCid, vLCparams);
            }
        }
        //sysLog("getGLCparams GLCurl:" + GLCurl + " GLC:" + GLC + " vLC0sites.size:" + vLCparams.size());
        for (int j = 1; j < vLCparams.size(); j++) {
            vLC.add(vLCparams.get(j));
        }
        return vLC;
    }

    /**
     * Gemlca LCode-hoz tartozo executor site-okat adja vissza a cache-bol (vektorban string)
     * @param GLCurl Gemlca service url
     * @param GLC LCode
     * @return GLC site lista
     */
    public Vector getGLCsites(String GLCurl, String GLC) throws Exception {
        String GLCid = GLC.split(":", 2)[0];

        if (!GLCid.startsWith("gusedelimit---")){
        Vector vLC0sites = (Vector) ( Gurl.get(GLCurl).get(GLCid));
        sysLog("getGLCsites GLC:" + GLC + " vLC0sites.size:" + vLC0sites.size() + " vLC0sites.get(0):" + vLC0sites.get(0));//+ " vLC0sites.get(s):" + vLC0sites.toString());
        return (Vector) vLC0sites.get(0);
        }
        else return new Vector();

    }

    /**
     * Gemlca LCode-hoz tartozo input file-ok szamat adja vissza a cache-bol (vektorban string)
     * @param GLCurl Gemlca service url
     * @param GLC LCode
     * @return GLC site lista
     */
    public int getGLCinputNmbr(String GLCurl, String GLC) throws Exception {
        Vector vp = getGLCparams(GLCurl, GLC);
        int nin = 0;
        for (int j = 0; j < vp.size(); j++) {
            if (((HashMap) vp.get(j)).get("file").equals("true")) {
                if (((HashMap) vp.get(j)).get("input") != null && ((HashMap) vp.get(j)).get("input").equals("true")) {
                    nin++;
                }
            }
        }
        return nin;
    }

    /**
     * Gemlca LCode-hoz tartozo output file-ok szamat adja vissza a cache-bol (vektorban string)
     * @param GLCurl Gemlca service url
     * @param GLC LCode
     * @return GLC site lista
     */
    public int getGLCoutNmbr(String GLCurl, String GLC) throws Exception {
        Vector vp = getGLCparams(GLCurl, GLC);
        int nout = 0;
        for (int j = 0; j < vp.size(); j++) {
            if (((HashMap) vp.get(j)).get("file").equals("true")) {
                if (((HashMap) vp.get(j)).get("input") != null && ((HashMap) vp.get(j)).get("input").equals("true")) {
                } else {
                    nout++;
                }
            }
        }
        return nout;
    }

    /**
     * Gemlca LCode-hoz tartozo in/output file internal name-t ellenorzi a cache-bol (boolean)
     * @param GLCurl Gemlca service url
     * @param GLC LCode
     * @param intname GLC output file internal name
     * @param GLC parameterei
     * @param true:input port false:output port
     * @return truhe, ha van ilyen internal file name, ha nincs false
     */
    public boolean isValidPortIName(String GLCurl, String GLC, String intname, String params, boolean input) {
        try {
            Vector vp = getGLCparams(GLCurl, GLC);
            String param[] = params.split(" ");

            for (int j = 0; j < vp.size(); j++) {
                if (((HashMap) vp.get(j)).get("file").equals("true")) {
                    if (((HashMap) vp.get(j)).get("input") != null && ((HashMap) vp.get(j)).get("input").equals("true")) {
                        if (input && param[j].equals(intname)) {
                            sysLog("isValidINputName->true " + GLC + " " + intname);
                            return true;
                        }
                    } else {
                        if (!input && param[j].equals(intname)) {
                            sysLog("isValidOutputName->true " + GLC + " " + intname);
                            return true;
                        }
                    }
                }
            }
            sysLog("isValidOutput/inputName->false " + GLC + " " + intname);
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Gemlca LCode-hoz tartozo elso in/output file internal name-t adja vissza a parameterbol
     * @param GLCurl Gemlca service url
     * @param GLC LCode
     * @param GLC parameterei
     * @param true:input port false:output port
     * @return internal file nev, "", ha nincs
     */
    public String getFirstValidPortIName(String GLCurl, String GLC, String params, boolean input) {
        try {
            Vector vp = getGLCparams(GLCurl, GLC);
            String param[] = params.split(" ");
            for (int j = 0; j < vp.size(); j++) {
                if (((HashMap) vp.get(j)).get("file").equals("true")) {
                    if (((HashMap) vp.get(j)).get("input") != null && ((HashMap) vp.get(j)).get("input").equals("true")) {
                        if (input) {
                            sysLog("1.ValidINputName->true " + GLC + " " + param[j]);
                            return param[j];
                        }
                    } else {
                        if (!input) {
                            sysLog("1.ValidOutputName->true " + GLC + " " + param[j]);
                            return param[j];
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * Gemlca LCode-hoz tartozo in/output file internal name-t ellenorzi a cache-bol, hanyadik parameter
     * @param GLCurl Gemlca service url
     * @param GLC LCode
     * @param intname GLC output file internal name
     * @param GLC parameterei
     * @param true:input port false:output port
     * @return parameter pozicioja, -1 ha hiba
     */
    public int getPortINamePosition(String GLCurl, String GLC, String intname, String params, boolean input) {
        try {
            Vector vp = getGLCparams(GLCurl, GLC);
            String param[] = params.split(" ");

            for (int j = 0; j < vp.size(); j++) {
                if (((HashMap) vp.get(j)).get("file").equals("true")) {
                    if (((HashMap) vp.get(j)).get("input") != null && ((HashMap) vp.get(j)).get("input").equals("true")) {
                        if (input && param[j].equals(intname)) {
                            sysLog("getPortINamePositionIN-> " + j + "" + GLC + " " + intname);
                            return j;
                        }
                    } else {
                        if (!input && param[j].equals(intname)) {
                            sysLog("getPortINamePositionOUT-> " + j + " " + GLC + " " + intname);
                            return j;
                        }
                    }
                }
            }
            sysLog("getPortINamePosition-isValidOutput/inputName->false " + GLC + " " + intname);
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    /**
     * Gemlca LCode-hoz tartozo in/output file internal name-t ellenorzi a cache-bol, hanyadik parameter
     * @param GLCurl Gemlca service url
     * @param GLC LCode
     * @param intname GLC output file internal name
     * @param GLC parameterei
     * @param true:input port false:output port
     * @return parameter pozicioja, -1 ha hiba
     */
    public String getNewExeParams(String GLCurl, String GLC, String newintname, String intname, String params, boolean input) {
        try {
            Vector vp = getGLCparams(GLCurl, GLC);
            String param[] = params.split(" ");

            for (int j = 0; j < vp.size(); j++) {
                if (((HashMap) vp.get(j)).get("file").equals("true")) {
                    if (((HashMap) vp.get(j)).get("input") != null && ((HashMap) vp.get(j)).get("input").equals("true")) {
                        if (input && param[j].equals(intname)) {
                            sysLog("getNewExeParamsIN-> " + j + " " + GLC + " intname:" + intname + " newintname: " + newintname);
                            param[j] = newintname;
                            return getParamasStr(param);
                        }
                    } else {
                        if (!input && param[j].equals(intname)) {
                            sysLog("getNewExeParamsOUT-> " + j + " " + GLC + " intname:" + intname + " newintname: " + newintname);
                            param[j] = newintname;
                            return getParamasStr(param);
                        }
                    }
                }
            }
            sysLog("getPortINamePosition-isValidOutput/inputName->false " + GLC + " " + intname);
        } catch (Exception e) {
            return params;
        }
        return params;
    }

    private String getParamasStr(String param[]) {
        String p = "";
        for (int i = 0; i < param.length; i++) {
            p = p.concat(param[i] + " ");
            sysLog("getParamasStr " + i + " " + param[i]);
        }
        sysLog("getParamasStr p=" + p);
        return p;
    }

    /**
     * GLC-ket adja vissza a cache-bol (vektorban string)
     * @param GLCurl Gemlca service url
     * @return GLC lista
     */
    private Vector getcachedGLCList(String GLCurl) throws Exception {
        try {
            //sysLog("getactualGLCList");
            Vector v = new Vector();
            ConcurrentHashMap GLCs = Gurl.get(GLCurl); //
            Enumeration eglc = GLCs.keys();
            while (eglc.hasMoreElements()) {
                v.add((String) eglc.nextElement());
            }
            return v;
        } catch (Exception e) {
            sysLog("getcachedlGLCList error");
            e.printStackTrace();
            throw new Exception(e);
        }

    }

    /** std.out-ra logol
     */
    private void sysLog(String txt) {
         System.out.println("GEMLCACACHE " + txt);
    }
}

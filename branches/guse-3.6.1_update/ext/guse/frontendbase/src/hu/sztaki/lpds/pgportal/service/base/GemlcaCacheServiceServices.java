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

import hu.sztaki.lpds.gemlcaquery.com.GemlcaqueryDataBean;
import hu.sztaki.lpds.gemlcaquery.inf.PortalGemlcaqueryClient;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import org.globus.gsi.GlobusCredential;

/**
 *
 * @author csig
 */
public class GemlcaCacheServiceServices {

    private static GemlcaCacheServiceServices instance = null;

    /**
     * Visszaadja az objektum statikus peldanyat
     * @return statikus objektum peldany;
     */
    public static GemlcaCacheServiceServices getInstance() {
        if (instance == null) {
            instance = new GemlcaCacheServiceServices();
        }
        return instance;
    }

    /**
     * leellenorzi, h érvényes e a proxy, és meghivja a getactualGLCList() -et
     * @param certfile cert file eleresi ut
     * @param GLCurl Gemlca service url (grid)
     * @return boolean
     */
    public Vector checkProxygetactualGLCList(String certfile, String GLCurl) throws Exception {
        Vector vLC = new Vector();
        File file = new File(certfile);
        if (file.exists()) {
            GlobusCredential gcred = new GlobusCredential(new FileInputStream(certfile));
            if (gcred != null) {
                sysLog("gemlca cred timeleft: " + gcred.getTimeLeft());
                if (gcred.getTimeLeft() > 0) {
                    // TODO : letezik es nem jart le a proxy !!!
                    vLC = getactualGLCList(certfile, GLCurl);
                    if (vLC.size() == 0) {
                        throw new Exception("error.gemlca.cannotretglcs");//Error. Can not retreive the Legacy Codes!
                    }
                } else {
                    // TODO : letezik de lejart a proxy !!!
                    throw new Exception("error.job.proxyexpired");
                }
            }
        } else {
            throw new Exception("error.job.noproxy");
        }
        return vLC;
    }

    /**
     * lekéri a GLCList-et
     * @param certfile cert file eleresi ut
     * @param GLCurl Gemlca service url (grid)
     * @return Vector GLC lista
     */
    public Vector getactualGLCList(String certfile, String GLCurl) throws Exception {
        try {
            //sysLog("getactualGLCList");
            Hashtable hsh = new Hashtable();
            ServiceType st = InformationBase.getI().getService("gemlcaquery", "portal", hsh, new Vector());
            PortalGemlcaqueryClient gmlcc = (PortalGemlcaqueryClient) Class.forName(st.getClientObject()).newInstance();
            gmlcc.setServiceURL(st.getServiceUrl());
            gmlcc.setServiceID(st.getServiceID());
            GemlcaqueryDataBean gtmp = new GemlcaqueryDataBean();
            gtmp.setGemlcaUrl("" + GLCurl);
            gtmp.setUsercert(getFileAllLineValue(certfile));
            String userID = certfile.split("/")[certfile.split("/").length-2];
            
            gtmp.setUserID(userID);
            Vector v = gmlcc.getGLCList(gtmp);
            return v;
        } catch (Exception e) {
            sysLog("getactualGLCList error");
            e.printStackTrace();
            throw new Exception(e);
        }

    }

    /**
     * lekéri a GLChez tartozo parametereket
     * @param certfile cert file eleresi ut
     * @param GLCurl Gemlca service url (grid)
     * @return Vector parameter lista (Vektor elemeiben HashMap)
     */
    public Vector getactualGLCParamList(String certfile, String GLCurl, String GLC) throws Exception {
        try {
            //sysLog("getactualGLCParamList "+GLCurl+" "+GLCParam);
            Hashtable hsh = new Hashtable();
            ServiceType st = InformationBase.getI().getService("gemlcaquery", "portal", hsh, new Vector());
            PortalGemlcaqueryClient gmlcc = (PortalGemlcaqueryClient) Class.forName(st.getClientObject()).newInstance();
            gmlcc.setServiceURL(st.getServiceUrl());
            gmlcc.setServiceID(st.getServiceID());
            GemlcaqueryDataBean gtmp = new GemlcaqueryDataBean();
            gtmp.setGemlcaUrl(GLCurl);
            gtmp.setUsercert(getFileAllLineValue(certfile));
            gtmp.setGlcName(GLC);
            Vector v = gmlcc.getGLCParameterList(gtmp);


            Vector vret = new Vector();

            //site lista
            String[] ssSites = ("" + ((HashMap) v.get(0)).get("sites")).split(" ");
            Vector vSites = new Vector();
            for (int j = 0; j < ssSites.length; j++) {
                vSites.add(ssSites[j]);
            }

            vret.add(vSites);//0. parameter tartalmazzaa siteok listajat vektorkent
            sysLog(GLC + " nbr of sites:" + ((Vector) vret.get(0)).size());



            //parameterek
            //parameterek szetvalogatasa:
            // 0Trace File,1No,2File,3file.trc,4Output,5null,6NoDefault
            // 0$$PARAMDESC$$,1$$MANDAT$$,2$$FILE$$,3$$DEFAULTVALUE$$,4$$INPUT$$,5$$VALIDATOR$$,6$$PRESENTINREPO$$
            //
            // 0$$PARAMDESC$$ - human readable desc
            // 1$$MANDAT$$ - yes/no, should not use the default value if yes
            // 2$$FILE$$ - file/cli - shows whether the value represents a filename required for the LC execution or it’s a simple string on the cli
            // 3
            // 4$$INPUT$$ - in case of file parameters the direction is shown here
            // 5$$VALIDATOR$$ -  is a java regex which should validate the values given for this parameter on the CLI
            // 6$$PRESENTINREPO$$ - default/nondefault – shows whether a file is in the repo
            for (int i = 1; i < v.size(); i++) {
                HashMap hps = new HashMap();//parameter hash
                String[] ps = ((String) ((HashMap) v.get(i)).get("params")).split(",");
                if (ps.length == 7) {
                    hps.put("friendlyName", ps[0]);//$$PARAMDESC$$
                    hps.put("value", ps[3]);//$$DEFAULTVALUEC$$
                    if (ps[4].equals("Input")) {//4$$INPUT$$
                        hps.put("input", "true");//
                    } else {
                        hps.put("input", "false");//
                    }
                    if (ps[2].equals("File")) {//2$$FILE$$
                        hps.put("file", "true");//
                    } else {
                        hps.put("file", "false");//
                    }

                } else {
                    sysLog(" Parameter ERROR:" + ((String) ((HashMap) v.get(i)).get("params")).split(","));
                }
                //sysLog(i + " - " + hps);
                vret.add(hps);
            }
            //sysLog("sites:"+vret.get(0));
            return vret;
        } catch (Exception e) {
            sysLog("getactualGLCParamList error");
            e.printStackTrace();
            throw new Exception(e);
        }

    }

    /*   private Hashtable getGLCwithParameterList(String certfile, String GLCurl) throws Exception{
    try{
    sysLog("getGLCwithParameterList start");
    Hashtable hsh=new Hashtable();
    ServiceType st=Base.getI().getService("gemlcaquery","portal",hsh,new Vector());
    PortalGemlcaqueryClient gmlcc=(PortalGemlcaqueryClient)Class.forName(st.getClientObject()).newInstance();
    gmlcc.setServiceURL(st.getServiceUrl());
    gmlcc.setServiceID(st.getServiceID());
    GemlcaqueryDataBean gtmp=new GemlcaqueryDataBean();
    gtmp.setGemlcaUrl(""+GLCurl);
    gtmp.setUsercert(certfile);
    Hashtable h = gmlcc.getGLCwithParameterList(gtmp);
    return h;
    }catch (Exception e){sysLog("getactualGLCList error");e.printStackTrace();throw new Exception(e);}
    }  */
    /**
     * Egy file tartalmat adja vissza string-ben. (Minden sort !)
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    private String getFileAllLineValue(String filePath) throws Exception {
        String retString = new String("");
        File file = new File(filePath);
        if (file.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String row = new String("");
            while ((row = bufferedReader.readLine()) != null) {
                retString += row + "\n";
            }
        } else {
            throw new Exception("error.job.noproxy");
        }
        return retString;
    }

    /** std.out-ra logol
     */
    private void sysLog(String txt) {
        // System.out.println("GEMLCACACHESERV " + txt);
    }
}

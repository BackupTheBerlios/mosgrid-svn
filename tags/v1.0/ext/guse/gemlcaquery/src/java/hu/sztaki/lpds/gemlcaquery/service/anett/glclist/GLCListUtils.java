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
package hu.sztaki.lpds.gemlcaquery.service.anett.glclist;

import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import hu.sztaki.lpds.gemlcaquery.com.GemlcaqueryDataBean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import uk.ac.wmin.cpc.gemlca.client.ClientGLCList;
import uk.ac.wmin.cpc.gemlca.client.ClientGLCProcess;
import uk.ac.wmin.cpc.gemlca.client.helpers.GlobusConnectionHelper;
import uk.ac.wmin.cpc.gemlca.frontend.stubs.GemlcaFactoryService.GetLCIDsResponseLCIDs;
import uk.ac.wmin.cpc.gemlca.frontend.stubs.GemlcaService.Parameter;

/**
 * GEMLCA calls
 *
 * @author lpds
 */
public class GLCListUtils {

    private GSSCredential userProxy = null;
    private String identity = null;
    private URL gemlcaURL = null;
    private static String GURLEND = "/wsrf/services/uk/ac/wmin/cpc/gemlca/frontend";
    private static String GURLFRONT = "https://";//gurlfront+sGrid+gurlend
    private static String REPURLEND="/sspimplist";
    //
    public Vector listLC(GemlcaqueryDataBean bean) {

        Vector<String> v = new Vector();
        String userproxyf = "";
        String userID = bean.getUserID();
        try {
            gemlcaURL = new URL(GURLFRONT + bean.getGemlcaUrl() + GURLEND);
            userproxyf = PropertyLoader.getInstance().getProperty("prefix.dir") + "gemlcaquery/" + "x509up." + System.nanoTime();
            saveCert(userproxyf, bean.getUsercert());
            userProxy = GlobusConnectionHelper.makeCredentialFromFile(userproxyf);

           GetLCIDsResponseLCIDs[] lcIDs = getLCListfromGemlca();

            if (lcIDs == null || lcIDs.length == 0) {
            //System.out.println("None.");
            } else {
               if (getRepositoryURL()!=null){
                ArrayList<String> lcIDRepository = getLCListfromRepository(userID);
                v = merge(lcIDs,lcIDRepository);
               }
               else{
                for (GetLCIDsResponseLCIDs lcID : lcIDs) {
                    v.add(lcID.getLcID() + ": " + lcID.getLcDescription().trim());
                //System.out.println("LcID:" + lcID.getLcID());
                //System.out.println("desc:" + lcID.getLcDescription());
                }
            }
            }

        } catch (MalformedURLException ex) {
            System.out.println("Gemlca url is malformed:" + bean.getGemlcaUrl());
            ex.printStackTrace();
        } catch (GSSException e) {
            System.out.println("Couldn't generate the proxy" + e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The Credential file is missing!");
        } catch (Exception e) {
            System.out.println("Exception while getting legacy codes");
            e.printStackTrace();
        }

        try {
            new File(userproxyf).delete();
        } catch (Exception e) {
        }

        return v;
    }


    private GetLCIDsResponseLCIDs[] getLCListfromGemlca() throws MalformedURLException, GSSException, Exception{
         //System.out.println("===Legacy codes for site:"+gemlcaURL.toString());
            GetLCIDsResponseLCIDs lcIDs[] = null;

            return ClientGLCList.getLCIDs(gemlcaURL, userProxy, identity);

    }

    private String getRepositoryURL(){
        String repourl = PropertyLoader.getInstance().getProperty("gemlca.repository.url");
        System.out.println("REPOSITORYURL:" + repourl);
        return repourl;
    }
    private ArrayList<String> getLCListfromRepository(String userID) throws IOException{
        ArrayList<String> response = new ArrayList<String>();
        String repositoryUrl =  getRepositoryURL();// Sample : http://dev17-portal.cpc.wmin.ac.uk:8080/shiwa-repo-testing
        String servicePass=PropertyLoader.getInstance().getProperty("gemlca.repository.servicepassword");
         URL url = new URL(repositoryUrl+REPURLEND);
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

        String data = URLEncoder.encode("sspUserId", "UTF-8") + "=" + URLEncoder.encode(userID, "UTF-8");
        data += "&" + URLEncoder.encode("sspServiceId", "UTF-8") + "=" + URLEncoder.encode(servicePass, "UTF-8");
        //writer.write("sspServiceId=12345678\\&sspUserId=1001");
        writer.write(data);
        writer.flush();
        ArrayList<String> selectedLCs = new ArrayList<String>();
        ArrayList<String> notselectedLCs = new ArrayList<String>();
        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        while ((line = reader.readLine()) != null) {
          // first value is the LC, second is true, if selected, and false if not
           
          Boolean selected = Boolean.parseBoolean(line.split("\\s+")[1]);
          String LC = line.split("\\s+")[0];
          if (selected){
              selectedLCs.add(LC);
          }
          else{
              notselectedLCs.add(LC);
          }


        }

        writer.close();
        reader.close();
        // Sorting them in lexicographical order
        Collections.sort(selectedLCs,String.CASE_INSENSITIVE_ORDER);
        Collections.sort(notselectedLCs,String.CASE_INSENSITIVE_ORDER);
        //
        response.add("gusedelimit---Legacy Codes selected in SHIWA Repository---");
        response.addAll(selectedLCs);
        response.add("gusedelimit---Other Legacy Codes available in SHIWA Repository---");
        response.addAll(notselectedLCs);
        response.add("gusedelimit---Legacy Codes available in GEMLCA---");


        return response;
    }


    // Format T_<LCID> or F_<LCID>

    public Vector<String> merge(GetLCIDsResponseLCIDs[] LCListfromGemlca,ArrayList<String> sortedLCListfromRepository){
        Vector<String> intersectedList= new Vector<String>();
        
        // Adding list items from repository that have already uploaded to Gemlca
        for (String lcFromRepository: sortedLCListfromRepository){
            
            for (GetLCIDsResponseLCIDs lcFromGemlca: LCListfromGemlca){
                if (lcFromRepository.equals(lcFromGemlca.getLcID())){
                    intersectedList.add(lcFromRepository+":" + lcFromGemlca.getLcDescription().trim()); // T_<LCID>:<LCDESCRIPTION>
                    break;
                }
                if ( lcFromRepository.startsWith("gusedelimit---")){
                    intersectedList.add(lcFromRepository);
                    break;
                }

            }
            

        }

        // Adding list items that are only in Gemlca and not in repository
         for (GetLCIDsResponseLCIDs lcFromGemlca: LCListfromGemlca){
                boolean contains = false;
             for (String lcFromRepository: sortedLCListfromRepository){
               if (!lcFromRepository.startsWith("gusedelimit---")){
                if (lcFromRepository.equals(lcFromGemlca.getLcID())){
                    contains = true;
                    break;
                }
               }
            }

            if (!contains){
                intersectedList.add(lcFromGemlca.getLcID()+":" + lcFromGemlca.getLcDescription().trim());
            }

        }


        return intersectedList;
    }

    public Vector getLCParameters(GemlcaqueryDataBean bean) {
        Vector v = new Vector();
        String userproxyf = "";
        try {
            gemlcaURL = new URL(GURLFRONT + bean.getGemlcaUrl() + GURLEND);
            userproxyf = PropertyLoader.getInstance().getProperty("prefix.dir") + "gemlcaquery/" + "x509up." + System.nanoTime();
            saveCert(userproxyf, bean.getUsercert());
            userProxy = GlobusConnectionHelper.makeCredentialFromFile(userproxyf);

            String lc_id = bean.getGlcName();
            //System.out.println("===Executor site for lc:" + lc_id);
            String sites[] = null;
            sites = ClientGLCList.getExecutorSites(gemlcaURL, userProxy, identity, lc_id);

            Hashtable hsh = new Hashtable();
            String ssites = "";
            if (sites != null) {
                for (String site : sites) {
                    ssites += site + " ";
                }
            } else {
                //System.out.println("NO sites");
                ssites = "-";
            }
            //System.out.println("Sites:" + ssites);
            hsh.put("sites", ssites);
            v.add(hsh);


            ClientGLCProcess client = new ClientGLCProcess(gemlcaURL, userProxy, identity);
            //System.out.println("client.createProcess:" + lc_id);

            client.createProcess(lc_id);

            //System.out.println("---Parameters for " + lc_id);

            for (Parameter param : client.getLCParameters(0)) {
                //System.out.println(param.getIndex() + ":" + param.getValue());
                Hashtable params = new Hashtable();
                params.put("params", param.getValue());
                v.add(params);
            }

            client.destroy();

        } catch (MalformedURLException ex) {
            System.out.println("Gemlca url is malformed:" + bean.getGemlcaUrl());
        } catch (GSSException e) {
            System.out.println("Couldn't generate the proxy" + e);
        } catch (IOException e) {
            System.out.println("The Credential file is missing!");
        } catch (Exception e) {
            System.out.println("Exception while getting executor sites/parameters for legacy code");
            e.printStackTrace();
        }

        try {
            new File(userproxyf).delete();
        } catch (Exception e) {
        }
        return v;
    }

    public Hashtable getLcP(GemlcaqueryDataBean bean) {
        Hashtable ghs = new Hashtable();
        /*   try{
        String CertFile = PropertyLoader.getInstance().getProperty("prefix.dir")+"/gemlcaquery/"+"x509up."+System.nanoTime();
        saveCert(CertFile, bean.getUsercert());
        String serviceurl = bean.getGemlcaUrl() ;
        // String GLC = bean.getGlcName();
        Vector vLC= new Vector();
        vLC= getlistLC(CertFile,serviceurl);
        /*     for (int i=0;i<vLC.size();i++){
        System.out.println("lc: "+vLC.get(i));
        Vector vLCparams= new Vector();
        vLCparams=getLCParameters(CertFile,serviceurl,((String)vLC.get(i)).split(":",2)[0]);
        ghs.put(((String)vLC.get(i)).split(":",2)[0],vLCparams);
        }*/
        /*        ghs.put("-GLCLIST-","vLC");
        new File(CertFile).delete();
        }catch (Exception e){
        e.printStackTrace();
        }*/
        return ghs;
    }

    private void saveCert(String path, String cert) throws Exception {
        try {
            File outFile = new File(path);
            new File(outFile.getParentFile().getAbsolutePath()).mkdirs();
            FileWriter out = new FileWriter(outFile);
            out.write(cert);
            out.close();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}

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
package hu.sztaki.lpds.pgportal.portlets.credential;

import dci.data.Certificate;
import hu.sztaki.lpds.dcibridge.client.ResourceConfigurationFace;
import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import dci.data.Middleware;
import java.io.FileNotFoundException;

/**
 * SSHKeyPortlet Portlet Class
 */
public class SSHKeyPortlet extends GenericWSPgradePortlet {

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.setContentType("text/html");
        if (!isInited()) {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);
            return;
        }
        loadAvailableResources(request);
        List<String> stypes = getAllGroupsforSshKey(request.getPortletSession());
        request.setAttribute("gridtypes", stypes);
        request.setAttribute("publickey", getPublicKey(request));
        Vector res = new Vector();
            Vector rr = getSavedResources(request.getRemoteUser());
            for (int j = 0; j < rr.size(); j++) {
                HashMap r = new HashMap();
                r.put("r", rr.elementAt(j));//resource
                r.put("n", j);//position
                res.add(r);
            }

        
        request.setAttribute( "rlist", res);
        PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/jsp/ssh/SshSettingsPortlet.jsp");
        dispatcher.include(request, response);
    }

    public void doAddRes(ActionRequest request, ActionResponse response) throws PortletException {
        //System.out.println("sshkey-doAddRes-user:" +request.getRemoteUser() + " sshuser:" +request.getParameter("sshuser") + " host:" + request.getParameter("stype"));
        //String type = request.getParameter("type");
        String sshuser = request.getParameter("sshuser").trim();
        String host = request.getParameter("stype").trim();
        String newres = "";

        request.setAttribute("stype", request.getParameter("stype"));
        if (!"".equals(host) && !"".equals(sshuser)) {
            newres = sshuser + "@" + host;            
            Vector<String> saved = getSavedResources(request.getRemoteUser());
            for (int i=0;i<saved.size();i++){
                String[] userhost = saved.get(i).split("@");
                if (userhost[1].equals(host)){//user name change for host
                    //System.out.println("sshkey-doAddRes-reconfigure user for host:"+host);
                    saved.remove(i);
                    break;
                }
            }

            saved.add(newres);
            try {
                //check, if it is a new type of submitter
                String keydir = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + request.getRemoteUser();
                String sshKey1 = keydir + "/x509up.pbs";//.ssh.id_rsa
                String newkey = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + request.getRemoteUser() + "/x509up." + host;
                File nkfile = new File(newkey);
                if (nkfile.exists()) {
                    nkfile.delete();
                }
                cpAndAdduser(sshKey1, newkey, sshuser);
            } catch (Exception ex) {
                request.setAttribute("msg", "Save failed!");
                ex.printStackTrace();
            }

            try {
                saveResources(request.getRemoteUser(), saved);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            request.setAttribute("msg", "Set User account!");
        }
    }

    public void doDelRes(ActionRequest request, ActionResponse response) throws PortletException {
        try {
            //System.out.println("doDelRes-   delid:" + request.getParameter("delid"));
            int delId = Integer.parseInt(request.getParameter("delid"));

            Vector r = getSavedResources(request.getRemoteUser());
            try {
                String filename = r.get(delId).toString().split("@")[1];
                File delfile = new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + request.getRemoteUser() + "/x509up." + filename);
                if (delfile.exists()){
                    delfile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }            
            r.remove(delId);
            saveResources(request.getRemoteUser(), r);
        } catch (Exception e) {
            request.setAttribute("msg", "Save failed!");
            e.printStackTrace();
        }
    }

    private String getPublicKey(RenderRequest request) {
        String user=request.getRemoteUser();
        File kgf = new File("/usr/bin/ssh-keygen");
        if (!kgf.exists()) {
            return "'/usr/bin/ssh-keygen' binary not exists ! Please install '/usr/bin/ssh-keygen' for PBS support.";
        } else {
            try {
                String keydir = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + user;
                File dir = new File(keydir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String sshKey1 = keydir + "/x509up.pbs";//.ssh.id_rsa
                String sshKey2 = keydir + "/x509up.pbs.pub";//.ssh.id_rsa.pub;
                File kf1 = new File(sshKey1);
                File kf2 = new File(sshKey2);
                if ((!kf1.exists()) || (!kf2.exists())) {
                    if (kf1.exists()) {
                        kf1.delete();
                    }
                    if (kf2.exists()) {
                        kf2.delete();
                    }
                    Process p;
                    String[] cmdarray = {"/usr/bin/ssh-keygen", "-t", "rsa", "-q", "-N", "", "-f", sshKey1};
                    p = Runtime.getRuntime().exec(cmdarray);
                    p.waitFor();
//                    List<String> stypes = getAllGroupsforSshKey(request.getPortletSession());
//                    for (int i = 0; i < stypes.size(); i++) {
//                        if (!"pbs".equals(stypes.get(i))) {
//                            cp(sshKey1, PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + user + "/x509up." + stypes.get(i));
//                        }
//                    }                    
                }
                if ((kf1.exists()) && (kf2.exists())) {
                    BufferedReader in = new BufferedReader(new FileReader(sshKey2));
                    String pubPart = in.readLine();
                    int key_length = pubPart.length() / 4;
                    String pubPart_1 = pubPart.substring(key_length * 0, key_length * 1);
                    String pubPart_2 = pubPart.substring(key_length * 1, key_length * 2);
                    String pubPart_3 = pubPart.substring(key_length * 2, key_length * 3);
                    String pubPart_4 = pubPart.substring(key_length * 3, pubPart.length());

                    return "Your SSH keypair's public part is (append this to ~/.ssh/authorized_keys on the remote server):<br/>"
                            + "<div style=\"background-color:lightblue\">"
                            + pubPart_1 + "<br>"
                            + pubPart_2 + "<br>"
                            + pubPart_3 + "<br>"
                            + pubPart_4 + "</div>";

                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Could not generate your key!";
            }
        }


        return "";
    }

    private void cpAndAdduser(String inf, String outf, String sshuser) throws FileNotFoundException, IOException {
        BufferedReader input = new BufferedReader(new FileReader(inf));
        BufferedWriter output = new BufferedWriter(new FileWriter(outf));
        try {
            String line = null;
            output.write("user=" + sshuser + "\n");
            while ((line = input.readLine()) != null) {
                output.write(line + "\n");
            }
        } finally {
            try {
                input.close();
            } catch (Exception e) {
            }
            try {
                output.close();
            } catch (Exception e) {
            }
        }
    }   
    
    /*
     * Load resources from users/" + user + "/.resources.conf.ssh
     */
    private Vector getSavedResources(String user) {
        Vector resources = new Vector();
        StringBuffer s = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + user + "/.resources.conf.ssh"));
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().startsWith("#")) {
                    resources.add(line);
                }
            }
        } catch (Exception e) {
        }
        try {
            br.close();
        } catch (Exception e) {
        }
        return resources;
    }

    /*
     * Sort and save resources to users/" + user + "/.resources.conf.type
     */
    private void saveResources(String user, Vector resources) throws IOException, Exception {
        File qdir = new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + user);
        if (!qdir.exists()) {
//            System.out.println("mkdirs for user:" + user);
            qdir.mkdirs();
        }
        Collections.sort(resources);
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + user + "/.resources.conf.ssh")));
        for (int i = 0; i < resources.size(); i++) {
            pw.print("" + resources.get(i) + "\n");
        }
        pw.close();        
    }

    private void loadAvailableResources(RenderRequest request) {
        //Session request
        PortletSession ps = request.getPortletSession();
        // Hashtable<String, Hashtable<String, Hashtable<String, Vector<String>>>> sessionConfig = (Hashtable<String, Hashtable<String, Hashtable<String, Vector<String>>>>) ps.getAttribute("resources");

  // query available resource configurations
            try{
                if (ps.getAttribute("resources") == null) {
                    ResourceConfigurationFace rc=(ResourceConfigurationFace)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                    List<Middleware> tmp_r=rc.get();
                    ps.setAttribute("resources", tmp_r);
                    ps.setAttribute("pub_resources", tmp_r,ps.APPLICATION_SCOPE);
                }
            }
            catch(Exception ex) {ex.printStackTrace();}


    }

    private Vector<String> getAllGroupsforSshKey(PortletSession ps){

  // query available resource configurations 
        try{
            if(ps.getAttribute("resources",ps.APPLICATION_SCOPE)==null){
                ResourceConfigurationFace rc=(ResourceConfigurationFace)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                List<Middleware> tmp_r=rc.get();
                ps.setAttribute("resources", tmp_r,ps.APPLICATION_SCOPE);
                ps.setAttribute("pub_resources", tmp_r,ps.APPLICATION_SCOPE);
            }
        }
        catch(Exception ex) {ex.printStackTrace();}

        Vector<String> gridNames = ConfigHandler.getAllGroupsforProxy((List<Middleware>)ps.getAttribute("resources", ps.APPLICATION_SCOPE),new Certificate[]{Certificate.SSH_KEY});
        return gridNames;

    }
}

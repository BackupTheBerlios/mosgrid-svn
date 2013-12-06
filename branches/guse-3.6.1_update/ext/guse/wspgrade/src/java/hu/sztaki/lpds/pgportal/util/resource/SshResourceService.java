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
package hu.sztaki.lpds.pgportal.util.resource;

import dci.data.Certificate;
import dci.data.Middleware;
import hu.sztaki.lpds.dcibridge.client.ResourceConfigurationFace;
import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;
import javax.portlet.PortletSession;

/**
 *
 * SshResourceService for ssh middlewares
 */
public class SshResourceService {

    private static SshResourceService instance=null;

    
    public static SshResourceService getI()
    {
        if(instance==null)instance=new SshResourceService();
        return instance;
    }

    /**
     * 
     * @param user - portal user id
     * @param host 
     * @return the ssh user
     */
    public String getUserforHost(String user, String host) {
        //System.out.println("getUserforHost: user:"+user + " host:"+host);
        BufferedReader br = null;
        String returnuser = "";
        try {
            br = new BufferedReader(new FileReader(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + user + "/.resources.conf.ssh"));
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().startsWith("#")) {
                    String[] userhost = line.split("@");
                    if (userhost.length > 1 && userhost[1].equals(host)){
                        //System.out.println("user for host:"+userhost[0]+" "+userhost[1]);
                        returnuser = userhost[0];
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }
        try {
            br.close();
        } catch (Exception e) {
        }
        return returnuser;
    }

    /**
     * Returns true, if pType is set for ssh key type middleware
     * @param resourcelist
     * @param pType
     * @return
     */
    public boolean isGroupforSshKey(List<Middleware> resourcelist, String pType){
        Vector<String> gridNames = ConfigHandler.getAllGroupsforProxy(resourcelist, new Certificate[]{Certificate.SSH_KEY});
        for (String t : gridNames){
            if (t.equals(pType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if pType is set for ssh key type middleware
     * @param ps
     * @param pType
     * @return
     */
    public boolean isGroupforSshKey(PortletSession ps, String pType){

  // elerheto eroforras konfiguracio lekerdezese
        try{
            if(ps.getAttribute("resources",ps.APPLICATION_SCOPE)==null){
                ResourceConfigurationFace rc=(ResourceConfigurationFace)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                List<Middleware> tmp_r=rc.get();
                ps.setAttribute("resources", tmp_r,ps.APPLICATION_SCOPE);
                ps.setAttribute("pub_resources", tmp_r,ps.APPLICATION_SCOPE);
            }
        }
        catch(Exception ex) {ex.printStackTrace();}

        return isGroupforSshKey((List<Middleware>)ps.getAttribute("resources", ps.APPLICATION_SCOPE), pType);
    }
}

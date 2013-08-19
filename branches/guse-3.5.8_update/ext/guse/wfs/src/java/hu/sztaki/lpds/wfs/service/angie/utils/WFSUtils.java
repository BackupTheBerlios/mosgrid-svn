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
package hu.sztaki.lpds.wfs.service.angie.utils;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.service.angie.Base;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;

/**
 * WFS utils... (helper class)
 *
 * @author lpds
 */
public class WFSUtils {
    
    private static WFSUtils instance = new WFSUtils();
    private Hashtable noproxyHash = null;
    private String sql;
    
    public WFSUtils() 
    {
        loadNoproxy();
    }
    
    /**
     * WFSUtils peldanyt ad vissza.
     * 
     * @return 
     */
    public static WFSUtils getInstance() {return instance;}
    
    /**
     * Betolti a noproxy listat a property filebol..
     */
    private void loadNoproxy() {
        try {
            if (noproxyHash == null) {
                noproxyHash = new Hashtable();
                String noproxy = PropertyLoader.getInstance().getProperty("noproxy");
                if (noproxy != null) {
                    noproxy = noproxy.toLowerCase();
                    if (noproxy.contains(",")) {
                        String[] rows = noproxy.split(",");
                        for (int iPos = 0; iPos < rows.length; iPos++) {
                            noproxyHash.put(rows[iPos], "");
                        }
                    } else {
                        noproxyHash.put(noproxy, "");
                    }
                }
                // System.out.println("loadNoproxy(noproxyHash : " + noproxyHash + ")...");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Megvizsgalja, hogy a megkapott parameter key benne
     * van e a noproxy kent beallitottak kozt.
     */
    public boolean isNoproxy(String key) {
        if (noproxyHash.containsKey(key.toLowerCase())) {
            return true;
        }
        return false;
    }
    
/**
 * Lement egy konfiguracios hibat
 * @param workflowID workflow neve
 * @param jobName job neve
 * @param portID I/O port
 * @param errorID hibakod
 */    
    public void saveWorkflowConfigDataError(long workflowID, String jobName, String portID, String errorID) 
    {
        try 
        {
            Connection conn = Base.getConnection();
            String esql="INSERT INTO error_prop (id_workflow, jobname, id_port, id_error) VALUES(?, ?, ?, ?)";
//            System.out.println("params: " + workflowID + ", " + jobName + ", " + portID + ", " + errorID);
            PreparedStatement prepStmpErrorProp = conn.prepareStatement(esql);
            prepStmpErrorProp.setLong(1, workflowID);
            prepStmpErrorProp.setString(2, jobName);
            prepStmpErrorProp.setString(3, portID);
            prepStmpErrorProp.setString(4, errorID);
            try {prepStmpErrorProp.execute();} 
            catch (Exception e) {e.printStackTrace();}
            conn.close();
        } 
        catch(Exception e){e.printStackTrace();}
    }

/**
 * Megallapitja hogy letezik-e ilyen nevu konkret
 * workflowja az aktualis felhasznalonak...
 *
 * (a beagyazashoz szukseges hogy templetbol
 * legyen letrehozva az adott konkrete wf,
 * azaz w.wtyp > 0)...
 *
 * @param value ComDataBean - userID - felhasznalo azonosito
 * @param embedWorkflowName - a beagyazott workflow neve, amit keresunk
 * @return boolean - true ha letezik a megadott workflow
 */
    public boolean isValidEmbedWorkflow(ComDataBean value, String embedWorkflowName) {
        boolean retBoolean = false;
        try {
            Connection conn = Base.getConnection();
            PreparedStatement preps = conn.prepareStatement("SELECT w.name FROM workflow as w, aworkflow as aw WHERE aw.id_portal=? and aw.id_user=? and w.id_aworkflow=aw.id and w.name=? and w.wtyp>0");
            preps.setString(1, value.getPortalID());
            preps.setString(2, value.getUserID());
            preps.setString(3, embedWorkflowName);
            try {
                ResultSet rs = preps.executeQuery();
                String wfName = "";
                while (rs.next()) {
                    wfName = rs.getString(1);
                }
                if (embedWorkflowName.equals(wfName)) {
                    retBoolean = true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retBoolean;
    }

}

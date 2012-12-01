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
package hu.sztaki.lpds.wfs.service.angie.repository;

import hu.sztaki.lpds.wfs.com.RepositoryWorkflowBean;
import hu.sztaki.lpds.wfs.service.angie.Base;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

/**
 * @author lpds
 */
public class RepositoryServiceImpl {
    
    public RepositoryServiceImpl() {
    }
    
    /**
     * Egy workflow-t jegyez be a repository
     * nyilvantartasaba, export soran hivodik meg.
     *
     * @param bean A workflow exportalast leiro parameterek
     * @return hibajelzes
     */
    public String setRepositoryItem(RepositoryWorkflowBean bean) throws Exception {
        String retStr = new String("");
        try {
            Connection connection = Base.getConnection();
            //
            // System.out.println("setRepositoryItem begin...");
            String sql = "INSERT INTO repository (id_portal, name, type, path, text, user) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stat = connection.prepareStatement(sql);
            stat.setString(1, bean.getPortalID());
            stat.setString(2, bean.getWorkflowID());
            stat.setString(3, bean.getWorkflowType());
            stat.setString(4, bean.getZipRepositoryPath());
            stat.setString(5, bean.getExportText());
            stat.setString(6, bean.getUserID());
            try {
                stat.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stat.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("setRepositoryItem end...");
        return retStr;
    }
    
    /**
     * Visszaadja a repository-ban talalhato
     * megadott tipusu workflow-k listajat. 
     *
     * (Az eredmeny egy vektor melyben
     * RepositoryWorkflowBean-ek vannak.)
     * 
     * @param RepositoryWorkflowBean bean - workflowType (pl: appl, proj, real, abst, graf)
     * @return workflowList - workflowk listaja
     */
    public Vector getRepositoryItems(RepositoryWorkflowBean bean) {
        Vector res = new Vector();
        try {
            Connection connection = Base.getConnection();
            //
            // System.out.println("getRepositoryItem begin...");            
            String sql = "SELECT r.id, r.id_portal, r.name, r.type, r.path, r.text, r.user FROM repository as r WHERE r.type = ? ORDER BY r.name, r.id";
            if (bean.getId() != null) {
                if (bean.getId().longValue() > 0) {
                    sql = "SELECT r.id, r.id_portal, r.name, r.type, r.path, r.text, r.user FROM repository as r WHERE r.type = ? and r.id = ? ORDER BY r.name, r.id";
                }
            }
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, bean.getWorkflowType());
            if (bean.getId().longValue() > 0) {
                statement.setLong(2, bean.getId().longValue());
            }            
            ResultSet rst = statement.executeQuery();
            while (rst.next()) {
                RepositoryWorkflowBean retbean = new RepositoryWorkflowBean();
                retbean.setId(new Long(rst.getLong("id")));
                retbean.setPortalID(rst.getString("id_portal"));
                retbean.setWorkflowID(rst.getString("name"));
                retbean.setWorkflowType(rst.getString("type"));
                retbean.setZipRepositoryPath(rst.getString("path"));
                // <br /> a felulet miatt
                retbean.setExportText(rst.getString("text").replaceAll("\n","<br />"));
                retbean.setUserID(rst.getString("user"));
                //
                res.add(retbean);
            }
            statement.close();
            connection.close();            
        } catch(Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
    /**
     * Egy workflow-t torol ki a repository nyilvantartasabol,
     * (tablabol) delete soran hivodik meg.
     *
     * @param bean A workflow torlest leiro parameterek
     * @return hibajelzes
     */
    public String deleteRepositoryItem(RepositoryWorkflowBean bean) throws Exception {
        String retStr = new String("");
        try {
            Connection connection = Base.getConnection();
            //
            // System.out.println("deleteRepositoryItem begin...");
            String sql = "DELETE FROM repository WHERE id = ?";
            PreparedStatement stat = connection.prepareStatement(sql);
            stat.setLong(1, bean.getId());
            try {
                stat.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
            stat.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("deleteRepositoryItem end...");
        return retStr;
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.db;

import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides information for the creation of menus to select which statistics are required.
 * There are five methods that return either a list of strings or a map of <String, String> where
 * the first string is the name of the concrete workflow for example and the value is the identifier for it.
 * Portals - No Input
 * Resource - No Input
 * Concrete Workflow - UserID
 * Workflow Instance - Concrete Workflow ID
 * Abstract Job - Concrete Workflow ID
 * Example:
 * <code>
 * MenuInformationHarvester mp = new MenuInformationHarvester(new DBBase);
 * Map<String, String> workflowNamesToWFIDs = mp.getWFIDs(USERID);
 * //workflowNamesToWFIDs contains a map where the key is the name of the workflow and the value is the WFID that is used by statistics
 * List<String> abstractJobNames = mp.get(WFID);
 * //abstractJobNames contains a list of all of the job names
 * </code>
 * @author smoniz
 */
public class MenuInformationHarvester extends InformationHarvester {

    /**
     * Requires a DBBase object to get the database connection from
     * @param conFactory
     */
    public MenuInformationHarvester(DBBase conFactory) {
        connectionSource = conFactory;
    }

    public String getPortal() {


            return PropertyLoader.getInstance().getProperty("portal.url");
        
    }

    /**
     * Please use getPortal() instead which gets this information from a propertry file. 
     * @return List<String> of all of the Portal URLs within the database
     */
       public List<String> getPortals() {
        ResultSet rst = null;
        PreparedStatement ps = null;
        Connection con = null;
        List<String> l = new ArrayList<String>();
        try {
            con = connectionSource.getConnection();
            ps = con.prepareStatement("SELECT URL FROM stat_portal");  //Select all Portal URLs
            rst = ps.executeQuery();

            while (rst.next()) {
                l.add(rst.getString("URL"));
            }
            rst.close();
            ps.close();
            con.close();
        }       //Exception Handling, print and close connection
        catch (SQLException e0) {
            e0.printStackTrace();
        }
        catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        catch (IOException e0) {
            e0.printStackTrace();
        }
        finally {
            if (rst != null) {
                try {
                    rst.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return l;
    }

    public List<String> getResource(String DCI) {
        ResultSet rst = null;
        PreparedStatement ps = null;
        Connection con = null;
        List<String> l = new ArrayList<String>();
        try {

            con = connectionSource.getConnection();
            ps = con.prepareStatement("SELECT URL FROM stat_resource WHERE DCI = ?");//Select all resources
            ps.setString(1, DCI);
            rst = ps.executeQuery();
            l = new ArrayList<String>();
            while (rst.next()) {
                l.add(rst.getString("URL"));
            }
            rst.close();
            ps.close();
            con.close();
        } //Exception Handling, print and close connection
        catch (SQLException e0) {
            e0.printStackTrace();
        }
        catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        catch (IOException e0) {
            e0.printStackTrace();
        }
        finally {
            if (rst != null) {
                try {
                    rst.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return l;
    }

    /**
     * @deprecated
     * REMOVE ASAP - use getResource(DCI NAME) instead
     * @return
     */
    @Deprecated
    public List<String> getResource() {
        ResultSet rst = null;
        PreparedStatement ps = null;
        Connection con = null;
        List<String> l = new ArrayList<String>();
        try {

            con = connectionSource.getConnection();
            ps = con.prepareStatement("SELECT URL FROM stat_resource ");//Select all resources

            rst = ps.executeQuery();
            l = new ArrayList<String>();
            while (rst.next()) {
                l.add(rst.getString("URL"));
            }
            rst.close();
            ps.close();
            con.close();
        } //Exception Handling, print and close connection
        catch (SQLException e0) {
            e0.printStackTrace();
        }
        catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        catch (IOException e0) {
            e0.printStackTrace();
        }
        finally {
            if (rst != null) {
                try {
                    rst.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return l;
    }

    /**
     *
     * @return List of strings of DCIs
     */
    public List<String> getDCIs() {
        ResultSet rst = null;
        PreparedStatement ps = null;
        Connection con = null;
        List<String> l = new ArrayList<String>();
        try {

            con = connectionSource.getConnection();
            ps = con.prepareStatement("SELECT name FROM stat_DCI ");//Select all resources

            rst = ps.executeQuery();
            l = new ArrayList<String>();
            while (rst.next()) {
                l.add(rst.getString("name"));
            }
            rst.close();
            ps.close();
            con.close();
        } //Exception Handling, print and close connection
        catch (SQLException e0) {
            e0.printStackTrace();
        }
        catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        catch (IOException e0) {
            e0.printStackTrace();
        }
        finally {
            if (rst != null) {
                try {
                    rst.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return l;
    }

    /**
     * Get map of concrete workflow ids and names associated with the given user
     * Map as the user does not need to see the ID for th concrete workflow and only needs to see the name
     * @param userID The current user id, will usually be supplied by LifeRay
     * @return Map<String,String> of <Abstract Workflow ID, Abstract Workflow Name>
     */
    public Map<String, String> getWFIDs(String userID) {
        //The joins ensure that there is data for the concrete workflow
        HashMap<String, String> m = executeQueryID_NAME("select workflow.id, workflow.name from workflow JOIN aworkflow ON id_aworkflow=aworkflow.id JOIN stat_ConcreteWorkflow ON workflow.id=wfid WHERE id_user =?", userID);
        return m;
    }

    /**
     * Get map of all the Workflow instance ids and names from a concrete workflow id,
     * one entry for each recorded execution of the concrete workflow
     *
     * @param  wfID Abstract Workflow Id
     * @return  Map<String,String> of <Workflow Instance Name, Workflow Instance ID>
     */
    public Map<String, String> getWRTIDs(String wfID) {
        HashMap<String, String> m = executeQueryID_NAME("SELECT wrtID as ID, startTime as name FROM stat_WorkflowInstance WHERE wfID=?", wfID);
        return m;
    }

    /**
     * Get list of abstract jobs in a given concrete workflow
     * stat_AbstractJob table
     * @param wfID abstract workflow ID
     * @return List of job names
     */
    public List<String> getAJobs(String wfID) {
        ResultSet rst = null;
        PreparedStatement ps = null;
        Connection con = null;
        List<String> l = new ArrayList<String>();
        try {
            con = connectionSource.getConnection();
            ps = con.prepareStatement("SELECT jobName FROM stat_AbstractJob WHERE wfid=?");
            ps.setString(1, wfID);
            rst = ps.executeQuery();

            while (rst.next()) {
                l.add(rst.getString("jobName"));
            }
            rst.close();
            ps.close();
            con.close();
        }
        catch (IOException e0) {
            e0.printStackTrace();
        }
        catch (SQLException e0) {
            e0.printStackTrace();
        }
        catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        finally {
            if (rst != null) {
                try {
                    rst.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return l;
    }

    /**
     * Since a couple of the above are very similar [returning a map] I extracted the code
     * @param query
     * @param param
     * @return
     */
    private HashMap<String, String> executeQueryID_NAME(String query, String param) {
        ResultSet rst = null;
        PreparedStatement ps = null;
        Connection con = null;
        HashMap<String, String> m = new HashMap<String, String>();
        try {
            con = connectionSource.getConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, param);
            rst = ps.executeQuery();

            while (rst.next()) {
                m.put(rst.getString("id"), rst.getString("name"));
            }
            rst.close();
            ps.close();
            con.close();
        }
        catch (IOException e0) {
            e0.printStackTrace();
        }
        catch (SQLException e0) {
            e0.printStackTrace();
        }
        catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        finally {
            if (rst != null) {
                try {
                    rst.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return m;
    }
}

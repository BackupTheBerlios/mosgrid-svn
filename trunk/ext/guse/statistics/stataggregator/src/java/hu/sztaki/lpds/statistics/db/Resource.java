package hu.sztaki.lpds.statistics.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Used to combine and calculate statistics from aggregate jobs for Resources.
 * <br>
 * Table stat_resource<br>
 *===================<br>
 *URL, statistics_ID<br>
 *-------------------<br>
 *URL              varchar(255) PK<br>
 *statistics_ID    bigint(20)<br>
 *
 * @author smoniz
 */
public class Resource extends Entity {

    StataggregateJob togetdci = null;
    private static String embeddedJobResource="gUSE WFI";
    String DCI = null;

    public String getDCI() {
        return DCI;
    }

    public void populateDCI(DBBase con) {
        if (DCI == null) {
            ResultSet rst = null;
            PreparedStatement ps = null;
            try {
                //todo: Does require GUSE to do resource ->dci mapping
                ps = con.getConnection().prepareStatement(" SELECT c.value FROM"
                                                          + " job_prop as c, ajob as d "
                                                          + " WHERE ?=c.id_workflow "
                                                          + " and c.id_ajob=d.id "
                                                          + " and d.name=? and c.name='grid' LIMIT 1");
                ps.setString(1, togetdci.getWfID());
                ps.setString(2, togetdci.getJobName());




                rst = ps.executeQuery();
                if (rst.next()) {
                    DCI = rst.getString(1);


                }
                else {
                    //it could be an embedded workflow represented by a job for outside
                    if (togetdci.getResource().startsWith("embed[")){
                        
                        DCI = embeddedJobResource;
                    }
                    
                    //BAD
                }


            }
            catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e2) {
                e2.printStackTrace();
            }
            finally {
                if (rst != null) {
                    try {
                        rst.close();
                    }
                    catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
                if (ps != null) {
                    try {
                        ps.close();
                    }
                    catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }


        }



    }

    /**
     * Resources require a url
     * @param URL
     */
    public Resource(String URL) {
        this.URL = URL;
    }
    String URL = "";

    @Override
    public String getKey() {
        return URL;

    }

    @Override
    public void setKeys(PreparedStatement ps) throws SQLException {
        ps.setString(1, getKey());
    }

    @Override
    public String getStatFKColumn() {
        return "`statistics_ID`";
    }

    @Override
    public String getWhereClause() {
        return getKeyColumn() + "=?";
    }

    /**
     *
     * @return Name of the primary key column in  @see getTable()
     */
    public String getKeyColumn() {
        return getTable() + ".`URL`";
    }

    @Override
    public String getTable() {
        return "`stat_resource`";
    }

    @Override
    public void insertEntity(Connection con) throws SQLException {

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO `stat_resource`"
                                      + "(`URL`,"
                                      + "`statistics_ID`, `DCI`)"
                                      + "VALUES"
                                      + "("
                                      + "?,"
                                      + "?, "
                                      + "?"
                                      + ")");
            ps.setString(1, URL);
            ps.setLong(2, stats.ID);
            ps.setString(3, DCI);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception e0) {
            e0.printStackTrace();
        }
        finally {
            try {
                ps.close();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }

        }
    }
}

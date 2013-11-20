/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * Used to combine and calculate statistics from aggregate jobs for portals.
 *
 *  <br>
 * Table stat_portal<br>
 *===================<br>
 *URL, statistics_ID<br>
 *-------------------<br>
 *URL              varchar(255) PK<br>
 *statistics_ID    bigint(20)<br>
 *
 *
 *
 * @author smoniz
 */
public class Portal extends Entity {

    /**
     * Portals are idenitfied by a url
     * @param URL
     */
    public Portal(String URL) {
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
     * @return Name of the column in @see getTable() that holds the primary key
     */
    public String getKeyColumn() {
        return getTable() + ".`URL`";
    }

    @Override
    public String getTable() {
        return "`stat_portal`";
    }

    @Override
    public void insertEntity(Connection con) throws SQLException {

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO `stat_portal`"
                                      + "(`URL`,"
                                      + "`statistics_ID`)"
                                      + "VALUES"
                                      + "("
                                      + "?,"
                                      + "?"
                                      + ")");
            ps.setString(1, URL);
            ps.setLong(2, stats.ID);
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

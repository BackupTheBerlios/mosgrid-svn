/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * Used to combine and calculate statistics from aggregate jobs for Users.
 *  <br>
 * Table stat_user<br>
 *===============<br>
 *userID, statistics_ID<br>
 *---------------<br>
 *userID           varchar(255) PK<br>
 *statistics_ID    bigint(20)<br>
 *<br>
 *
 * @author smoniz
 */
public class User extends Entity {

    String userID;
    /**
     * User requires a user ID
     * @param userID User ID 
     */
    public User(String userID) {
        this.userID = userID;
    }


    @Override
    public void insertEntity(Connection con) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO `stat_user`"
                                      + "(`userID`,"
                                      + "`statistics_ID`)"
                                      + "VALUES"
                                      + "("
                                      + "?,"
                                      + "?"
                                      + ")");
            ps.setString(1, userID);
            ps.setLong(2, stats.ID);
            ps.executeUpdate();
            ps.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
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

    @Override
    public String getKey() {
        return userID;
    }

    /**
     *
     * @return name of the primary key column
     */
    public String getKeyColumn() {
        return getTable() + ".`userID`";
    }

    @Override
    public String getStatFKColumn() {
        return "`statistics_ID`";
    }

    @Override
    public String getTable() {
        return "`stat_user`";
    }

    @Override
    public String getWhereClause() {
        return getKeyColumn() + "=?";
    }

    @Override
    public void setKeys(PreparedStatement ps) throws SQLException {
        ps.setString(1, getKey());
    }
}

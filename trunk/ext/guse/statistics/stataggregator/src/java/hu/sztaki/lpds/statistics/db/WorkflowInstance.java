/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**CREATE  TABLE `stat_WorkflowInstance` (
 *`wrtID` VARCHAR(255) NOT NULL COMMENT '	' ,
 *`statistics_ID` BIGINT NULL ,
 *PRIMARY KEY (`wrtID`) );
 * See Enity
 *
 * @author smoniz
 */
public class WorkflowInstance extends Entity {

    String wrtID = "";

    @Override
    public void insertEntity(Connection con) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO `stat_WorkflowInstance`"
                                      + " (`wrtID`,"
                                      + " `statistics_ID`)"
                                      + " VALUES"
                                      + " ("
                                      + " ?,"
                                      + " ?"
                                      + " )"
                                      + " ON DUPLICATE KEY UPDATE"
                                      + " statistics_ID = ?"
                                      + " ");
            ps.setString(1, getKey());
            ps.setLong(2, stats.ID);
            ps.setLong(3, stats.ID);
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

    /**
     *
     * @param wrtID
     */
    public WorkflowInstance(String wrtID) {
        this.wrtID = wrtID;
    }

    @Override
    public String getKey() {
        return wrtID;
    }

    @Override
    public String getStatFKColumn() {
        return "`statistics_ID`";
    }

    @Override
    public String getTable() {
        return "`stat_WorkflowInstance`";
    }

    /**
     *
     * @return name of the primary key column
     */
    public String getKeyColumn() {
        return getTable() + ".`wrtID`";
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

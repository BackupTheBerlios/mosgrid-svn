package hu.sztaki.lpds.statistics.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * Used to hold and populate information about an Abstract Job to be stored in
 * the stat_AbstractJob table and the stat_statistics table
 *
 *
 *Table stat_AbstractJob
 *======================
 *jobName, wfid, statistics_ID
 *----------------------
 *jobName          varchar(255) PK
 *wfid             varchar(255) PK
 *statistics_ID    bigint(20)
 * @author smoniz
 */
public class AbstractJob extends Entity {

    /**
     * An Abstract Job requires a name and a concrete workflow id as its identity
     *
     * @param jobName name of the job
     * @param wfID workflow id that contains this job
     */
    public AbstractJob(String jobName, String wfID) {
        this.jobName = jobName;
        this.wfID = wfID;
    }

    @Override
    public void insertEntity(Connection con) throws SQLException {

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO `stat_AbstractJob`"
                                      + "(`jobName`," + "`wfid`,"
                                      + "`statistics_ID`)"
                                      + "VALUES"
                                      + "("
                                      + "?,"
                                      + "?,?"
                                      + ")");
            ps.setString(1, jobName);
            ps.setString(2, wfID);
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

    @Override
    public String getKey() {
        return wfID + jobName;
    }

    @Override
    public String getStatFKColumn() {

        return "`statistics_ID`";

    }

    @Override
    public String getTable() {
        return "`stat_AbstractJob`";
    }

    @Override
    public String getWhereClause() {
        return getTable() + ".`jobName`=? AND " + getTable() + ".`wfid`=? ";
    }

    @Override
    public void setKeys(PreparedStatement ps) throws SQLException {
        ps.setString(1, jobName);
        ps.setString(2, wfID);
    }
    /**
     *
     */
    String jobName;
    /**
     * Concrete Workflow ID
     */
    String wfID;
}

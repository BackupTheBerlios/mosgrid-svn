package hu.sztaki.lpds.statistics.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import hu.sztaki.lpds.statistics.jobstate.JobState;
import hu.sztaki.lpds.statistics.jobstate.StateType;

/**
 *Used to combine and calculate statistics from aggregate jobs for Concrete Workflows.
 *
 *<br>
 *  CREATE  TABLE `stat_ConcreteWorkflow` ( <br>
 *   `wfid` VARCHAR(255) NOT NULL , <br>
 *   `statistics_ID` BIGINT NULL , <br>
 *  PRIMARY KEY (`wfid`) );<br>
 *
 *
 * @author smoniz
 */
public class ConcreteWorkflow extends Entity {

    /**
     * Change in the Total Time spent executing this concrete workflow
     */
    private long workflowTotalTime = 0;
    /**
     * Change in the Squares of the total time spent executing this concrete workflow
     */
    private long sqaresOfWorkflowTime = 0;
    /**
     * Change in the Number of time this workflow has been executed
     */
    private long numberOfWorkflows = 0;

    /**
     * SELECT from stat_WorkflowInstance all unconsumed workflow instances that are executions of this conrete workflow
     * in order to calculate the change in the workflow statistics
     * @param con Database Connection
     */
    public void GetConcreteWorkflowStatistics(Connection con) {
        PreparedStatement ps = null;
        ResultSet rst = null;
        try {
            StringBuilder statement = new StringBuilder();
            statement.append("SELECT delta, wrtID FROM stat_WorkflowInstance WHERE consumed = 0 AND wfID = ? AND status IN (");
            Collection<JobState> states = JobState.getByType(StateType.TERMINAL);
            //Note - Accepting all complete workflows, terminal and failed.
            states.addAll(JobState.getByType(StateType.FAIL));
            for (int i = 0; i < states.size() - 1; i++) {
                statement.append("?,");
            }

            statement.append("?");
            statement.append(")");

            ps = con.prepareStatement(statement.toString());
            ps.setString(1, wfid);
            int i = 1;
            for (JobState state : states) {
                i++;
                ps.setString(i, state.getID());

            }
            rst = ps.executeQuery();
            Collection<String> wrtIDs = new HashSet<String>();

            while (rst.next()) {
                int delta = rst.getInt("delta");
                this.workflowTotalTime += delta;

                this.sqaresOfWorkflowTime += Math.pow(delta, 2);

                this.numberOfWorkflows++;
                wrtIDs.add(rst.getString("wrtID").trim());

            }
            consumeWRTIDS(wrtIDs, con);

        }
        catch (SQLException e) {
            e.printStackTrace();


        }
        finally {
            try {
                if (rst != null) {
                    rst.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Uses the workflow instance ids that were consumed by GetConcreteWorkflowStatistics
     * and marks them as consumed in the database
     * @param wrtIDs Concrete Workflow IDs
     * @param con Database Connection
     */
    private void consumeWRTIDS(Collection<String> wrtIDs, Connection con) {
        if (wrtIDs.size() == 0) {
            return;
        }
        StringBuilder out = new StringBuilder();
        out.append(" UPDATE `stat_WorkflowInstance` "
                   + " SET "
                   + " `consumed` = 1 "
                   + " WHERE "
                   + " `wrtID` IN (");
        StringBuilder in = new StringBuilder();
        for (String wrtID : wrtIDs) {
            in.append(" '");
            in.append(wrtID);
            in.append("' ");
            in.append("  ,");

        }
        out.append(in.substring(0, in.length() - 2));
        out.append(")");

        PreparedStatement ps = null;

        try {
            ps = con.prepareStatement(out.toString());
            ps.executeUpdate();
            ps.close();
            return;
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
     * Because concrete has some special charateristics, that it always needs to at least update the workflow statistics,
     * I override this to always insert
     * @param con
     */
    @Override
    public void testThenInsertEntity(Connection con) {
        insertEntity(con);
    }

    /**
     * This inserts if the concrete workflow does not exist, or updates if it does already.
     * This is different in order to manage concrete workflow statistics.
     * @param con
     */
    @Override
    public void insertEntity(Connection con) {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO `stat_ConcreteWorkflow`"
                                      + " (`wfid`,"
                                      + " `statistics_ID`,"
                                      + " `totalWorkflowTime`,"
                                      + " `sumOfSquaresWorkflowTime`,"
                                      + " `numberOfWorkflows`"
                                      + " )"
                                      + " VALUES"
                                      + " ("
                                      + " ?,"
                                      + " ?,"
                                      + " ?,"
                                      + " ?,"
                                      + " ?"
                                      + ")"
                                      + "ON DUPLICATE KEY UPDATE"
                                      + "`totalWorkflowTime` = `totalWorkflowTime` + ?,"
                                      + "`sumOfSquaresWorkflowTime` = `sumOfSquaresWorkflowTime` + ?,"
                                      + "`numberOfWorkflows` = `numberOfWorkflows` + ?");
            ps.setString(1, this.wfid);
            ps.setLong(2, this.stats.ID);
            ps.setLong(3, this.workflowTotalTime);
            ps.setLong(4, this.sqaresOfWorkflowTime);
            ps.setLong(5, this.numberOfWorkflows);
            ps.setLong(6, this.workflowTotalTime);
            ps.setLong(7, this.sqaresOfWorkflowTime);
            ps.setLong(8, this.numberOfWorkflows);

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
     * A concrete workflow requires a concrete workflow id : wfid
     * @param wfid
     */
    public ConcreteWorkflow(String wfid) {
        this.wfid = wfid;
    }

    @Override
    public String getKey() {
        return wfid;
    }

    @Override
    public String getStatFKColumn() {
        return "`statistics_ID`";
    }

    @Override
    public String getTable() {
        return "`stat_ConcreteWorkflow`";
    }

    @Override
    public String getWhereClause() {
        return getKeyColumn() + "=?";
    }

    /**
     * Get the primary key column for concrete workflow
     * @return Name of the primary key column
     */
    public String getKeyColumn() {
        return getTable() + ".`wfid`";
    }

    @Override
    public void setKeys(PreparedStatement ps) throws SQLException {
        ps.setString(1, getKey());
    }
    /**
     * Concrete Workflow ID
     */
    String wfid;
}

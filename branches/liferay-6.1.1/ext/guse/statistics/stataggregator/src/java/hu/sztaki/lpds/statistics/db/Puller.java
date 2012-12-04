package hu.sztaki.lpds.statistics.db;

import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import hu.sztaki.lpds.statistics.jobstate.JobState;
import hu.sztaki.lpds.statistics.jobstate.StateType;

/**
 * Comsuming a database connection, this class collects a set  
 * stat_AggregateJob entries and stores them into objects.
 * @author smoniz
 */
public class Puller {

    DBBase conFactory = null;

    /**
     *
     * @param con Connection to database
     */
    public Puller(DBBase con) {
        conFactory = con;
    }

    /**
     *
     * @return Connection Database connection
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Connection getCon() throws SQLException, ClassNotFoundException, IOException {
        return conFactory.getConnection();
    }

    /**
     * Close connection
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void close() throws SQLException, ClassNotFoundException {
        conFactory.close();

    }

    /**
     * Retrieve a set of aggregate jobs
     * @return Collection<StataggregateJob> objects for all the jobs pulled from the database
     */
    public Collection<StataggregateJob> getAggregateJobs() {
        // Not processing as I recieve as that would be annoying
        // Should this process per job rather then store? Or just store the ids?
        StringBuilder query = new StringBuilder();
        query.append(
                "SELECT  stat_AggregateJob.ID ,   stat_AggregateJob.Resource ,   stat_AggregateJob.NumberOfJobs ,   stat_AggregateJob.JobName ,   stat_AggregateJob.StartTS ,  "
                + " stat_AggregateJob.EndTS ,   stat_AggregateJob.wfID ,   stat_AggregateJob.wrtID ,   stat_AggregateJob.userID ,   stat_AggregateJob.portalID, stat_AggregateJob.squaresOfRunningTime  "
                + " FROM  stat_AggregateJob"
                + " JOIN stat_WorkflowInstance ON stat_WorkflowInstance.wrtID = stat_AggregateJob.wrtID "
                + "WHERE stat_AggregateJob.consumed = 0 "
                + "AND stat_WorkflowInstance.status IN  (");

        /**
         *
         */
        Collection<JobState> states = JobState.getByType(StateType.TERMINAL);
//Note, this accepts all terminated workflows, terminal or fail
        states.addAll(JobState.getByType(StateType.FAIL));
        for (int i = 0; i < states.size() - 1; i++) {
            query.append("?,");
        }
        Collection<StataggregateJob> aJobs = new HashSet<StataggregateJob>();


        PreparedStatement ps = null;
        ResultSet rst = null;
        query.append("?");
                try {
            query.append(")   LIMIT "
                         + PropertyLoader.getInstance().getProperty("aggregatejoblimit"));





            ps = getCon().prepareStatement(query.toString());
            int i = 0;
            //Set the allowed states

            for (JobState state : states) {
                i++;
                ps.setString(i, state.getID());

            }


            rst = ps.executeQuery();

            while (rst.next()) {
                StataggregateJob aj = StataggregateJob.fromrst(rst);
                aj.populateStatus(getCon());
                aJobs.add(aj);
            }



            rst.close();
            ps.close();
        }
        catch (Exception e0) {
            e0.printStackTrace();
        }
        finally {
            try {
                if (rst != null) {
                    rst.close();
                }
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }

            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }

        }






        return aJobs;


    }

    /**
     * Mark all jobs consumed, so they won't be counted twice for the statistics
     * @param jobs jobs to be consumed
     */
    public void markEntered(Collection<StataggregateJob> jobs) {

        if (jobs.size() == 0) {
            return;


        }
        StringBuilder out = new StringBuilder();
        out.append(" UPDATE `stat_AggregateJob` "
                   + " SET "
                   + " `consumed` = 1 "
                   + " WHERE "
                   + " ID IN (");
        StringBuilder in = new StringBuilder();


        for (StataggregateJob j : jobs) {
            in.append(
                    j.getId().toString());
            in.append("  ,");

        }
        out.append(in.substring(0, in.length() - 2));
        out.append(")");
        PreparedStatement ps = null;
        try {
            ps = getCon().prepareStatement(out.toString());
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
     * 
     * Code to clean up stat_ running since it cannot be done via trigger
     * Deletes all entries marked entered
     */
    public void cleanStatRunning() {
        PreparedStatement ps = null;

        try {
            ps = getCon().prepareStatement("DELETE FROM stat_running WHERE entered=1");
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
     * Code to clean up stat_JobInstance since it cannot be done via trigger
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void cleanJobInstance() throws SQLException, ClassNotFoundException, IOException {
        //fk should cause cascade to delete from stat_JobInstancetatus as a side effect as well.
        if (getCon() != null) {
            PreparedStatement ps = null;
            try {
                ps = getCon().prepareStatement("DELETE FROM stat_JobInstance WHERE entered=1");
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

    /**
     * Removes stat running entries older then WEEK
     */
    public void cleanOldStatRunning() {
        PreparedStatement ps = null;

        try {
            ps = getCon().prepareStatement("DELETE FROM stat_running WHERE TIMESTAMPDIFF("
                                           + "DAY, tim, NOW())>"
                                           + PropertyLoader.getInstance().getProperty("statrunningtimeout"));
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
     * Removes job instance data older than a month
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void cleanOldJobInstance() throws SQLException, ClassNotFoundException, IOException {
        //fk should cause cascade to delete from stat_JobInstancetatus as a side effect as well.
        if (getCon() != null) {
            PreparedStatement ps = null;
            try {

                ps = getCon().prepareStatement("DELETE FROM stat_JobInstance WHERE TIMESTAMPDIFF(DAY, startTime, NOW())> "
                                               + PropertyLoader.getInstance().getProperty("jobinstancetimeout"));

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
}

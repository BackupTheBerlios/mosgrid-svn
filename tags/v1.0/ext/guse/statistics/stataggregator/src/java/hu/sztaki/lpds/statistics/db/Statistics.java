/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.db;

import hu.sztaki.lpds.statistics.jobstate.StateType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the statistics for the Entity, and knows how to insert/update itself
 * in the database
 * @author smoniz
 */
public class Statistics {


    /**
     *
     */
    public int TotalJobTime = 0;
    /**
     *
     */
    public int SquaresJobTime = 0;
    /**
     *
     */
    public int NumFailedJobs = 0;
    /**
     *
     */
    public int NumJobs = 0;
    long ID = -1;
    Entity e;

    /**
     * Adds given statistics to this one
     * @param s
     */
    public void addStatistics(Statistics s) {
        TotalJobTime += s.TotalJobTime;
        SquaresJobTime += s.SquaresJobTime;
        NumFailedJobs += s.NumFailedJobs;
        NumJobs += s.NumJobs;
        for (StateType type : StateType.values()) {
            StateStats.get(type).addStatistics(s.StateStats.get(type));
        }



    }

    /**
     * Entity this is referenced by
     * @param e
     */
    public Statistics(Entity e) {
        this.e = e;
        //Set each state type to 0
        for (StateType type : StateType.values()) {
            StateStats.put(type, new StateStatistic(type));

        }


    }
    Map<StateType, StateStatistic> StateStats = new HashMap<StateType, StateStatistic>();

    /**
     * Add a single aggregate job to this statisitc.
     * @param ajob
     */
    public void addAggregateJob(StataggregateJob ajob) {
        TotalJobTime += ajob.getTotalRunTime();
        SquaresJobTime += ajob.getSquaresOfRunningTime();
        NumFailedJobs += ajob.getFailures();
        NumJobs += ajob.getNumberOfJobs();
        for (StateType type : StateType.values()) {
            StateStats.get(type).addAggregateJob(ajob);
        }


    }

    /**
     * Store this in the db
     * @param con Database connection
     * @return statistics identifier
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public long updateStatistics(Connection con) throws SQLException, ClassNotFoundException {
        //con = getConnection();
        ID = e.selectStatisticsID(con);
        //If an appropiate row already exists in the database
        PreparedStatement ps = null;
        ResultSet rst = null;
        if (ID != -1) { //I already have a statistics row
            try {
                ps = con.prepareStatement(" UPDATE `stat_statistics`"
                                          + " SET"
                                          + " `TotalJobTime` = `TotalJobTime` + ?,"
                                          + " `SquaresJobTime` = `SquaresJobTime` + ?,"
                                          + " `NumFailedJobs` = `NumFailedJobs` + ?,"
                                          + " `NumJobs` = `NumJobs` +?"
                                          + " WHERE ID = ?");
                ps.setInt(1, this.TotalJobTime);
                ps.setInt(2, this.SquaresJobTime);
                ps.setInt(3, this.NumFailedJobs);
                ps.setInt(4, this.NumJobs);
                ps.setLong(5, ID);
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
        else {//Create a new row
            try {
                ps = con.prepareStatement(" INSERT INTO `stat_statistics` "
                                          + " SET "
                                          + " `TotalJobTime` =   ?,"
                                          + " `SquaresJobTime` =    ?,"
                                          + " `NumFailedJobs` =   ?,"
                                          + " `NumJobs` = ?");
                ps.setInt(1, this.TotalJobTime);
                ps.setInt(2, this.SquaresJobTime);
                ps.setInt(3, this.NumFailedJobs);
                ps.setInt(4, this.NumJobs);
                ps.executeUpdate();
                ps.close();

                ps = con.prepareStatement("SELECT LAST_INSERT_ID()");

                ps.executeQuery();

                rst = ps.getResultSet();
                rst.next();


                ID = rst.getLong("LAST_INSERT_ID()");
                rst.close();
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
                try {
                    if (rst != null) {
                        rst.close();
                    }
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }
        }
        //Insert data for each state type
        for (StateType type : StateType.values()) {
            StateStats.get(type).updateStateStatistics(ID, con);
        }


        return ID;

    }
}

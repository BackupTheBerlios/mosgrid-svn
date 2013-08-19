/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.db;

import hu.sztaki.lpds.statistics.jobstate.StateType;
import hu.sztaki.lpds.statistics.jobstate.JobState;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A single stat_AggregateJob entry
 * @author smoniz
 */
public class StataggregateJob {

    private Long id;
    private int squaresOfRunningTime;
    String DCI = "";

    /**
     *
     * @return sum of the squares of the running time column in stat_AggregateJob
     */
    public int getSquaresOfRunningTime() {

        return squaresOfRunningTime;
    }

    /**
     *
     * @param squaresOfRunningTime
     */
    public void setSquaresOfRunningTime(int squaresOfRunningTime) {
        this.squaresOfRunningTime = squaresOfRunningTime;
    }
    private String resource;
    private Integer numberOfJobs;
    private String jobName;
    private Timestamp startTS;
    private Timestamp endTS;
    private String wfID;
    private String wrtID;
    private String userID;
    private String portalID;
    private Map<String, StataggregateJobStatus> states = null;

    /**
     *
     * @return
     */
    public Map<String, StataggregateJobStatus> getStatesMap() {
        return states;
    }

    /**
     *
     * @return
     */
    public Collection<StataggregateJobStatus> getStates() {
        return states.values();
    }

    /**
     * Build from a result set
     * @param rst
     * @return
     * @throws SQLException
     */
    static StataggregateJob fromrst(ResultSet rst) throws SQLException {
        StataggregateJob aj = new StataggregateJob();
        aj.setId(rst.getLong("id"));
        aj.setResource(rst.getString("Resource"));
        aj.setNumberOfJobs(rst.getInt("NumberOfJobs"));
        aj.setJobName(rst.getString("JobName"));
        aj.setStartTS(rst.getTimestamp("StartTS"));
        aj.setEndTS(rst.getTimestamp("EndTS"));
        aj.setWfID(rst.getString("wfID"));
        aj.setWrtID(rst.getString("wrtID"));
        aj.setUserID(rst.getString("userID"));
        aj.setPortalID(rst.getString("portalID"));
        aj.setSquaresOfRunningTime(rst.getInt("squaresOfRunningTime"));


        return aj;
    }

    /**
     *
     * Occationally there will be two entrys for the same status [this is OK {Somewhat, has been changed so this shouldn't happen}]
     * before calcultion, they need to be combined.
     * @param a 
     * @param b
     * @return
     */
    private StataggregateJobStatus combine(StataggregateJobStatus a, StataggregateJobStatus b) {

        StataggregateJobStatus ns = new StataggregateJobStatus();

        ns.setJobstate(a.getJobstate());
        ns.setMax((a.getMax() > b.getMax() ? a.getMax() : b.getMax()));
        ns.setMin((a.getMin() < b.getMin() ? a.getMin() : b.getMin()));
        ns.setNum(a.getNum() + b.getNum());
        ns.setSquares(a.getSquares() + b.getSquares());
//        ns.setTerminal(a.getTerminal() && b.getTerminal());
        ns.setTotal(a.getTotal() + b.getTotal());

        return ns;

    }

    /**
     * Populate the status
     * @param con
     * @throws SQLException
     */
    public void populateStatus(Connection con) throws SQLException {

        if (states == null) {
            PreparedStatement ps = con.prepareStatement("SELECT     `stat_AggregateJob_ID`, `jobstate`,    `min`,  `max`,  `total`, `squares`,  `num`  FROM  `stat_AggregateJobStatus`  WHERE `stat_AggregateJob_ID` = ?");// + String.valueOf(this.getId()));


            ps.setLong(1, this.id);
            ResultSet rst = ps.executeQuery();

            states = new HashMap<String, StataggregateJobStatus>();
            while (rst.next()) {

                StataggregateJobStatus ajs = StataggregateJobStatus.fromrst(rst);
                if (states.containsKey(ajs.getJobstate())) {

                    states.put(ajs.getJobstate(), combine(ajs, states.get(ajs.getJobstate())));
                }
                else {
                    states.put(ajs.getJobstate(), ajs);
                }
            }

            rst.close();
            ps.close();



        }


    }

    /**
     *
     */
    public StataggregateJob() {
    }

    /**
     *
     * @param id
     */
    public StataggregateJob(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getResource() {
        return resource;
    }

    /**
     *
     * @param resource
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     *
     * @return
     */
    public Integer getNumberOfJobs() {
        return numberOfJobs;
    }

    /**
     *
     * @param numberOfJobs
     */
    public void setNumberOfJobs(Integer numberOfJobs) {
        this.numberOfJobs = numberOfJobs;
    }

    /**
     *
     * @return
     */
    public String getJobName() {
        return jobName;
    }

    /**
     *
     * @param jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     *
     * @return
     */
    public Date getStartTS() {
        return startTS;
    }

    /**
     *
     * @param startTS
     */
    public void setStartTS(Timestamp startTS) {
        this.startTS = startTS;
    }

    /**
     *
     * @return
     */
    public Date getEndTS() {
        return endTS;
    }

    /**
     *
     * @param endTS
     */
    public void setEndTS(Timestamp endTS) {
        this.endTS = endTS;
    }

    /**
     *
     * @return
     */
    public String getWfID() {
        return wfID;
    }

    /**
     *
     * @param wfID
     */
    public void setWfID(String wfID) {
        this.wfID = wfID;
    }

    /**
     *
     * @return
     */
    public String getWrtID() {
        return wrtID;
    }

    /**
     *
     * @param wrtID
     */
    public void setWrtID(String wrtID) {
        this.wrtID = wrtID;
    }

    /**
     *
     * @return
     */
    public String getUserID() {
        return userID;
    }

    /**
     *
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     *
     * @return
     */
    public String getPortalID() {
        return portalID;
    }

    /**
     *
     * @param portalID
     */
    public void setPortalID(String portalID) {
        this.portalID = portalID;
    }

    /**
     *
     * @param s
     */
    public void setStates(Map<String, StataggregateJobStatus> s) {
        states = s;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "db.StataggregateJob[id=" + id + "]{states=" + states.toString() + "}";
    }
    private int total = -1;

    /**
     *
     * @return
     */
    public int getTotalRunTime() {
        if (total == -1) {//int total = 0 ;
            total = 0;
            for (StataggregateJobStatus status : this.getStates()) {
                total += status.getTotal();

            }
        }
        return total;
    }
    private int fails = -1;

    /**
     *
     * @return
     */
    public int getFailures() {
        if (fails == -1) {
            fails = 0;
            for (JobState st : JobState.getByType(StateType.FAIL)) {
                StataggregateJobStatus a = this.getStatesMap().get(st.toString());
                fails += (a == null ? 0 : a.getNum());

            }
        }
        return fails;
    }

    /**
     *
     * @param type
     * @return
     */
    public int getTimeInStateType(StateType type) {
        Collection<JobState> state = JobState.getByType(type);
        int time = 0;
        for (JobState str : state) {
            StataggregateJobStatus s = this.getStatesMap().get(str.getID());
            if (s != null) {
                time += s.getTotal();

            }

        }
        return time;
    }

    /**
     *
     * @param type
     * @return
     */
    public int getSquaresOfTimeInStateType(StateType type) {
        Collection<JobState> state = JobState.getByType(type);
        int squares = 0;
        for (JobState str : state) {
            StataggregateJobStatus s = this.getStatesMap().get(str.getID());
            if (s != null) {
                squares += s.getSquares();

            }

        }
        return squares;
    }

    /**
     *
     * @param type
     * @return
     */
    public int getNumInStateType(StateType type) {
        Collection<JobState> state = JobState.getByType(type);
        int num = 0;
        for (JobState str : state) {
            StataggregateJobStatus s = this.getStatesMap().get(str.getID());
            if (s != null) {
                num += s.getNum();
            }

        }
        return num;
    }
}

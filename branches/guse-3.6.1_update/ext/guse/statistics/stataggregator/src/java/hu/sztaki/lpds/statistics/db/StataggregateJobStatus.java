/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Holds data for each state that the aggregate Job entered
 * One object per entry retrieved
 * Table stat_AggregateJobStatus
 * 
 *=============================
 *stat_AggregateJob_ID, jobstate, min, max, total, squares, num
 *-----------------------------
 *stat_AggregateJob_ID bigint(20) PK
 *jobstate         varchar(255) PK
 *min              int(11)
 *max              int(11)
 *total            int(11)
 *squares          int(11)
 *num              int(11)
 * @author smoniz
 */
public class StataggregateJobStatus {

    private Long stataggregateJobID;
    private String jobstate;
    private Integer min;
    private Integer max;
    private Integer total;
    private Integer squares;
    private Integer num;

    /**
     * Creates a new job status from a single result set entry
     * @param rst
     * @return
     * @throws SQLException
     */
    static StataggregateJobStatus fromrst(ResultSet rst) throws SQLException {
        // SELECT  `stat_AggregateJobStatus`.`ID`, `stat_AggregateJobStatus`.`stat_AggregateJob_ID`, `stat_AggregateJobStatus`.`jobstate`, `stat_AggregateJobStatus`.`terminal`, `stat_AggregateJobStatus`.`min`, `stat_AggregateJobStatus`.`max`, `stat_AggregateJobStatus`.`total`, `stat_AggregateJobStatus`.`squares`, `stat_AggregateJobStatus`.`num`  FROM  GUSE.stat_AggregateJobStatus WHERE stat_AggregateJob_ID = " + id);
        StataggregateJobStatus ajs = new StataggregateJobStatus();

        ajs.stataggregateJobID = rst.getLong("stat_AggregateJob_ID");
        ajs.jobstate = rst.getString("jobstate");

        ajs.min = rst.getInt("min");
        ajs.max = rst.getInt("max");
        ajs.total = rst.getInt("total");
        ajs.squares = rst.getInt("squares");
        ajs.num = rst.getInt("num");



        return ajs;


    }

    /**
     * 
     */
    public StataggregateJobStatus() {
    }

    /**
     * Id of the job this status is associated with
     * @return
     */
    public Long getStataggregateJobID() {
        return stataggregateJobID;
    }

    /**
     *
     * @param stataggregateJobID
     */
    public void setStataggregateJobID(Long stataggregateJobID) {
        this.stataggregateJobID = stataggregateJobID;
    }

    /**
     * Which state this represents?
     * @return
     */
    public String getJobstate() {
        return jobstate;
    }

    /**
     *
     * @param jobstate
     */
    public void setJobstate(String jobstate) {
        this.jobstate = jobstate;
    }

    /**
     * The minimum amount of time in this state for this aggregate job
     * @return
     */
    public Integer getMin() {
        return min;
    }

    /**
     *
     * @param min
     */
    public void setMin(Integer min) {
        this.min = min;
    }

    /**
     * The maximum amount of time in this state for this aggregate job
     * @return
     */
    public Integer getMax() {
        return max;
    }

    /**
     *
     * @param max
     */
    public void setMax(Integer max) {
        this.max = max;
    }

    /**
     * Total time in this state for this aggregate job
     * @return
     */
    public Integer getTotal() {
        return total;
    }

    /**
     *
     * @param total
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * Sum of the squares of all of the times all of the jobs aggregated into this job were in this state
     * @return
     */
    public Integer getSquares() {
        return squares;
    }

    /**
     *
     * @param squares
     */
    public void setSquares(Integer squares) {
        this.squares = squares;
    }

    /**
     * Number times all of the jobs aggregated into this aggregate job reached THIS state
     * @return
     */
    public Integer getNum() {
        return num;
    }

    /**
     *
     * @param num
     */
    public void setNum(Integer num) {
        this.num = num;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "db.StataggregateJobStatus[id=" + this.getStataggregateJobID() + "," + this.getJobstate() + "]";
    }
}

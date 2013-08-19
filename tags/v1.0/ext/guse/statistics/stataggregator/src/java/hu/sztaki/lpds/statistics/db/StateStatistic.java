package hu.sztaki.lpds.statistics.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import hu.sztaki.lpds.statistics.jobstate.StateType;

/**
 * Holds statistics about the different states that the job goes through.
 */
public class StateStatistic {

    /**
     * used to combine values of statistics. Will add all of the total/squares/num from s to this.total, this.squares, this.num
     * @param s StateStatistics
     */
    public void addStatistics(StateStatistic s) {
        total += s.total;
        squares += s.squares;
        num += s.num;
    }
    /**
     * Type of job state this represents statistics for.
     */
    StateType type;
    /**
     * Total time spent in this StateType
     */
    int total = 0;

    /**
     * Sum of the squares of the total time spent in this statetype
     */
    int squares = 0; // = aJob.getSquaresOfTimeInStateType(type);
    /**
     * Number of touches in this statetype
     */
    int num = 0;

    /**
     *
     * @param type @see jobState.StateType
     */
    public StateStatistic(StateType type) {
        this.type = type;
    }

    /**
     * Add data from given aggregate job to this state
     * @param ajob
     */
    public void addAggregateJob(StataggregateJob ajob) {
        total += ajob.getTimeInStateType(type);
        squares += ajob.getSquaresOfTimeInStateType(type);
        num += ajob.getNumInStateType(type);
    }

    /**
     * Insert this into the database OR update
     * @param statID
     * @param con
     * @throws SQLException
     */
    public void updateStateStatistics(long statID, Connection con) throws SQLException {
        if (total == 0 && squares == 0 && num == 0) {
            return;
        }
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(" INSERT INTO `stat_JobStateTypeStatistics`" + " (`stat_JobStateTypeStatistics`.`TotalTimeInStates`," + " `stat_JobStateTypeStatistics`.`SquaresTimeInStates`," + " `stat_JobStateTypeStatistics`.`Num`, " + " `stat_JobStateTypeStatistics`.`statistics_ID`," + " `stat_JobStateTypeStatistics`.`StateType`)" + " VALUES " + " (?,?,?,?,?)" + " ON DUPLICATE KEY UPDATE" + "  " + " TotalTimeInStates = TotalTimeInStates + ?," + " SquaresTimeInStates= SquaresTimeInStates + ?," + " Num = Num + ?" + "   ");
            ps.setInt(1, total);
            ps.setInt(2, squares);
            ps.setInt(3, num);
            ps.setLong(4, statID);
            ps.setString(5, type.toString());
            ps.setInt(6, total);
            ps.setInt(7, squares);
            ps.setInt(8, num);
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

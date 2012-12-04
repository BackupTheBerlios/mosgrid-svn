
package hu.sztaki.lpds.statistics.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * Base class, all that is shared for each of the levels that stats are being stored for
 * Each class inheirting entity knows how to insert or update itself in the database, and lives in the table defined in getTable():String
 * Knows how to insert or update its statistics
 * Knows how to retrieve its statistic's identifier
 * @author smoniz
 */
public abstract class Entity {

    /**
     * Statistics Object. Contains the change in the statistics value for this entity.
     */
    Statistics stats = new Statistics(this);
    /**
     * Whether this entity already has an entry in @see getTable() or a new entry needs to be inserted
     */
    private boolean insert = true;

    /**
     *
     * @return Statistics object
     */
    public Statistics getStats() {

        return stats;
    }

    /**
     * Get the table name
     * @return Formatted for use in SQL queries the name of the table, eg `stat_portal`
     */
    public abstract String getTable();

    /**
     * Get the value of the key
     * @return A way of identifying different entities of the same type, eg url for resource, wfid for concrete workflow.
     */
    public abstract String getKey();

    /**
     * Used in generating the SELECT query to determine if the statistics object for this already exisists in the database
     * @return
     */
    public abstract String getWhereClause();

    /**
     * 
     * @return The name of the column that is the foreign key to stat_statistics
     */
    public abstract String getStatFKColumn();

    /**
     * set the keys in the preparedstatement [as there could be more than one]
     * @param ps
     * @throws SQLException
     */
    public abstract void setKeys(PreparedStatement ps) throws SQLException;

    /**
     * See if this already exists in the database, and if it does return the id of the associated statistics object
     * @param con Database connection
     * @return Id of the statistics object, or -1 if it needs to be inserted
     * @throws SQLException
     */
    public long selectStatisticsID(Connection con) throws SQLException {
        long statID = -1;
        PreparedStatement ps = null;
        ResultSet rst = null;
        try {
            ps = con.prepareStatement(" SELECT "
                                      + getStatFKColumn()
                                      + " FROM "
                                      + getTable()
                                      + " WHERE "
                                      + getWhereClause());
            setKeys(ps);
            rst = ps.executeQuery();

            if (rst.next()) {
                statID = rst.getInt(1);
                if (statID > 0) {
                    insert = false;
                }
                else {
                    statID = -1;
                }
            }
            rst.close();
            ps.close();
        }
        catch (Exception e0) {
            e0.printStackTrace();
        }
        finally {
            try {
                if (ps != null) {
                    ps.close();
                }
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
        // stats.ID = statID;
        return statID;



    }

    /**
     * Only insert if it does not already exist
     * It does not already exist if I did not find the stat id when searched for
     * @param con
     * @throws SQLException
     */
    public void testThenInsertEntity(Connection con) throws SQLException {
        if (insert) {
            insertEntity(con);
        }
    }

    /**
     * Insert this entity into the table from @see getTable()
     * @param con database connection
     * @throws SQLException
     */
    public abstract void insertEntity(Connection con) throws SQLException;
}

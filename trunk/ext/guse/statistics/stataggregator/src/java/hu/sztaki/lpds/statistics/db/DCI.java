package hu.sztaki.lpds.statistics.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Table stat_DCI
 *==============
 *name, statistics_ID
 *--------------
 *name             varchar(255) PK
 *statistics_ID    bigint(20)
 *
 *Used for DCI level statistics
 * @author smoniz
 */
public class DCI extends Entity {

    /**
     * dci's require a name
     * @param dciName
     */
    public DCI(String dciName) {
        this.dciName = dciName;
    }
    /**
     * DCI Name
     */
    String dciName = "";

    @Override
    public String getKey() {
        return dciName;

    }

    @Override
    public void setKeys(PreparedStatement ps) throws SQLException {
        ps.setString(1, getKey());
    }

    @Override
    public String getStatFKColumn() {
        return "`statistics_ID`";
    }

    @Override
    public String getWhereClause() {
        return getKeyColumn() + "=?";
    }

    /**
     *
     * @return Name of the primary key column in   
     */
    public String getKeyColumn() {
        return getTable() + ".`name`";
    }

    @Override
    public String getTable() {
        return "`stat_DCI`";
    }

    @Override
    public void insertEntity(Connection con) throws SQLException {

        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO `stat_DCI`"
                                      + " (`name`,"
                                      + " `statistics_ID`)"
                                      + " VALUES"
                                      + " ("
                                      + " ?,"
                                      + " ?"
                                      + " )");
            ps.setString(1, dciName);
            ps.setLong(2, stats.ID);

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

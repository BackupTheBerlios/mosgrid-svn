package hu.sztaki.lpds.statistics.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import hu.sztaki.lpds.statistics.jobState.StateType;

/**
 * Retrieves the data from the stat_metric_descrption table and creates a map<STRING CATEGORY, METRICINFORMATION>
 * of the result of the query
 * For that table: Table stat_metric_description
 *=============================
 *column_name, pretty_name, category, units, percision, source, forlevel, statetype, id
 *-----------------------------
 *column_name      varchar(255)
 *pretty_name      varchar(255)
 *category         varchar(255)
 *units            varchar(255)
 *percision        int(11)
 *source           varchar(255)
 *forlevel         varchar(255)
 *statetype        varchar(255)
 *id               int(11) PK
 *
 * If the category =='0' will not return
 *
 * Example of the usage
 * <code>
 * Map<String, List<MetricInformation>> ms = null;
 *       MetricInformationHarvester mif = new MetricInformationHarvester(new DBBase());
 *       try {
 *           ms = mif.getMetric("user"); }... </code>
 * The ms object would then be given to a StatisticsFactory object to have the data populated.
 *
 * @author smoniz
 */
public class MetricInformationHarvester extends InformationHarvester {

    

    /**
     * Requires a DBBase object to manage the connection
     * @param connectionFactory
     */
    public MetricInformationHarvester(DBBase connectionFactory) {
        connectionSource = connectionFactory;
    }

    /**
     * Returns a map of ordered lists of all the entries in stat_metric_description table
     * that have category <>0 and level = all or the parameter.
     * This is then populated by StatisticsFactory so that the
     * metricinformations also contain the data
     * @param type StatisticLevel.USER / StatisticLevel.WORKFLOWINSTANCE etc
     * @return Map<String, List<MetricInformation>> String is the category from the database, and the List is all of the metric informatin rows that have that category, ORDERED BY their ID in the database. List is used to garuntee the order will not change
     */
    public Map<String, List<MetricInformation>> getMetric(StatisticLevel type) {
        PreparedStatement ps = null;
        ResultSet rst = null;
        Connection con = null;
        Map<String, List<MetricInformation>> cat = new HashMap<String, List<MetricInformation>>();
        try {
            con = connectionSource.getConnection();

            
            ps = con.prepareStatement("SELECT column_name , pretty_name ,category , units ,percision,source, statetype"
                                      + " FROM stat_metric_description WHERE forlevel=? OR forlevel=? "
                                      + " AND category<>0 ORDER BY `id`");
            ps.setString(1, type.toString());
            ps.setString(2, "all");
            rst = ps.executeQuery();

            List<MetricInformation> l;
            String pretty_name;
            String column_Name;
            String category;
            String units;
            int percision;
            String source;
            StateType st = null;
            while (rst.next()) {
                pretty_name = rst.getString("pretty_name");
                column_Name = rst.getString("column_Name");
                category = rst.getString("category");
                units = rst.getString("units");
                percision = rst.getInt("percision");
                source = rst.getString("source");
                String t = rst.getString("statetype");
                if (t != null) {
                    st = StateType.valueOf(t.toUpperCase());
                }

                if (!cat.containsKey(category)) {
                    l = new ArrayList<MetricInformation>();

                    cat.put(category, l);
                }
                else {
                    l = cat.get(category);

                }
                l.add(new MetricInformation(pretty_name, column_Name, category, units, percision, source, st));

            }
            rst.close();
            ps.close();
            con.close();
        }catch (IOException e0){
            e0.printStackTrace();
        }
        catch (SQLException e0) {
            e0.printStackTrace();
        }
        catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
         
        finally {
            if (rst != null) {
                try {
                    rst.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return cat;
    }
}

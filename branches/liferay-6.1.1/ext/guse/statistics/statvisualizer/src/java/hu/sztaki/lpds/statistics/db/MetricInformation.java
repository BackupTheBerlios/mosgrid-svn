package hu.sztaki.lpds.statistics.db;

import hu.sztaki.lpds.information.local.PropertyLoader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import hu.sztaki.lpds.statistics.jobState.StateType;

/**
 * Holds information about ONE metric.
 * One object per stat_metric_description row.
 * This structure should handle the formatting of the data and the information required to populate the data
 * @author smoniz
 */
public class MetricInformation {

    private static Locale locale;

    static {
        try {
            locale = new Locale(PropertyLoader.getInstance().getProperty("language"), PropertyLoader.getInstance().getProperty("country"));
        }//Default to GERMANY
        catch (Exception e) {
            locale = Locale.GERMANY;
        }
    }

    /**
     * Constanct for formatting of the numbers
     */
    private StateType type;
    private String pretty_name;
    private String column_Name;
    private String category;
    private String units;
    private int percision;
    private String source;
    private double data = 0;

    /**
     * Which group of metrics this belongs to. Used for grouping when displaying
     * @return
     */
    String getCategory() {


        return category;
    }

    /**
     * USed to populate the data, what is the name of the column that is the source of this data
     * @return
     */
    String getColumn_Name() {
        return column_Name;
    }

    /**
     * Get the number of decimal points to be displayed with the data
     * @return
     */
    int getPercision() {
        return percision;
    }

    /**
     * Get the display name for the data
     * @return
     */
    public String getPretty_name() {
        return pretty_name;
    }

    /**
     * Get which database table this data is from
     * @return
     */
    public String getSource() {
        return source;
    }

    /**
     * Get the units of this data
     * @return
     */
    public String getUnits() {
        return units;
    }

    /**
     *
     * @param pretty_name
     * @param column_Name
     * @param category
     * @param units
     * @param percision
     * @param source
     * @param type @see jobState.StateType
     */
    MetricInformation(String pretty_name, String column_Name, String category, String units, int percision, String source, StateType type) {
        this.pretty_name = pretty_name;
        this.type = type;
        this.column_Name = column_Name;
        this.category = category;
        this.units = units;
        this.percision = percision;
        this.source = source;
    }

    /**
     * Returns the formated data for the web page. To change formatting style, change the locale constant.
     * @return
     */
    public String getData() {

        NumberFormat format = null;

        if (units.equalsIgnoreCase("%")) {
            String out = "";
            format = DecimalFormat.getPercentInstance(locale);
            format.setMinimumFractionDigits(percision);
            format.setMaximumFractionDigits(percision);
            
            out = format.format(data);

            
            out = out.replace('%', ' ');
            return out;
        }
        else {

            format = DecimalFormat.getNumberInstance(locale);
            format.setMaximumFractionDigits(percision);
            format.setMinimumFractionDigits(percision);

        }
        
        return format.format(data);
        


    }

    /**
     * Unformatted, unlocalized number output. Used for google visualizations
     * @return this.data
     */
    public double getUglyData() {

        if (units.equalsIgnoreCase("%")) {

            return data * 100;

        }
        return data;

    }

    /**
     *
     * @param data
     */
    void setData(double data) {
        this.data = data;
    }

    /**
     * Get the job state category for this data [If applicable] @see jobState.StateType and jobState.JobState
     * @return
     */
    public StateType getType() {
        return type;
    }
}

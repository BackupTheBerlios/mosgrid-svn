 
package hu.sztaki.lpds.statistics.db;

/**
 * Used by MetricInformationFactory
 * Defines which level of statistics we want the stat_metric_description rows for
 * String needs to match values used in the table
 * @author smoniz
 */
public enum StatisticLevel {

    PORTAL("portal"), USER("user"), DCI("dci"), RESOURCE("resource"), WORKFLOWINSTANCE("workflowinstance"), CONCRETEWORKFLOW("concreteworkflow"), ABSTRACTJOB("abstractjob");

    StatisticLevel(String name) {
    }
    /**
     * Description of level
     */
    private String name;
    /**
     *
     * @return name
     */
    public String toString() {
        return name;
    }
}

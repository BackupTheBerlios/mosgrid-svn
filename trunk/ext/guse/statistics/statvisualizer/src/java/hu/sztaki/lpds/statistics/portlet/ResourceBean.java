/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.portlet;

import hu.sztaki.lpds.statistics.db.DBBase;
import hu.sztaki.lpds.statistics.db.MetricInformationHarvester;
import hu.sztaki.lpds.statistics.db.StatisticLevel;
import hu.sztaki.lpds.statistics.db.StatistiticsInformationHarvester;
import java.sql.SQLException;
import java.util.Map;

/**
 * Bean for Resource
 * @author Alessandra
 */
public class ResourceBean {

    public String resourceName;
    public String DCIName;
    private Map<java.lang.String, java.util.List<hu.sztaki.lpds.statistics.db.MetricInformation>> ms = null;
    private MetricInformationHarvester mif;
    private hu.sztaki.lpds.statistics.db.MenuInformationHarvester mp;
    private StatistiticsInformationHarvester statsFac;
    public StringBuilder cat1Stats;
    public StringBuilder cat2Stats;
    public StringBuilder cat3Stats;
    public StringBuilder cat4Stats;
    public StringBuilder cat5Stats;
    public StringBuilder cat6Stats;
    public StringBuilder cat7Stats;
    public StringBuilder cat8Stats;
    private String failrate;

    public String getFailrate() {
        return failrate;
    }

    public void setFailrate(String failrate) {
        this.failrate = failrate;
    }

    public StringBuilder getCat1Stats() {
        return cat1Stats;
    }

    public void setCat1Stats(StringBuilder cat1Stats) {
        this.cat1Stats = cat1Stats;
    }

    public StringBuilder getCat2Stats() {
        return cat2Stats;
    }

    public void setCat2Stats(StringBuilder cat2Stats) {
        this.cat2Stats = cat2Stats;
    }

    public StringBuilder getCat3Stats() {
        return cat3Stats;
    }

    public void setCat3Stats(StringBuilder cat3Stats) {
        this.cat3Stats = cat3Stats;
    }

    public StringBuilder getCat4Stats() {
        return cat4Stats;
    }

    public void setCat4Stats(StringBuilder cat4Stats) {
        this.cat4Stats = cat4Stats;
    }

    public StringBuilder getCat5Stats() {
        return cat5Stats;
    }

    public void setCat5Stats(StringBuilder cat5Stats) {
        this.cat5Stats = cat5Stats;
    }

    public StringBuilder getCat6Stats() {
        return cat6Stats;
    }

    public void setCat6Stats(StringBuilder cat6Stats) {
        this.cat6Stats = cat6Stats;
    }

    public StringBuilder getCat7Stats() {
        return cat7Stats;
    }

    public void setCat7Stats(StringBuilder cat7Stats) {
        this.cat7Stats = cat7Stats;
    }

    public StringBuilder getCat8Stats() {
        return cat8Stats;
    }

    public void setCat8Stats(StringBuilder cat8Stats) {
        this.cat8Stats = cat8Stats;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
     public String getDCIName() {
        return DCIName;
    }

    public void setDCIName(String DCIName) {
        this.DCIName = DCIName;
    }

    public ResourceBean(String resourceName, String DCIURL) throws ClassNotFoundException, SQLException {
        this.resourceName = resourceName;
        this.DCIName = DCIURL;

        mif = new MetricInformationHarvester(new DBBase());
        mp = new hu.sztaki.lpds.statistics.db.MenuInformationHarvester(new DBBase());
        statsFac = new hu.sztaki.lpds.statistics.db.StatistiticsInformationHarvester(new DBBase());
        cat1Stats = new StringBuilder();
        cat2Stats = new StringBuilder();
        cat3Stats = new StringBuilder();
        cat4Stats = new StringBuilder();
        cat5Stats = new StringBuilder();
        cat6Stats = new StringBuilder();
        cat7Stats = new StringBuilder();
        cat8Stats = new StringBuilder();
        ms = mif.getMetric(StatisticLevel.RESOURCE);        //Gets metric information for Resource
        statsFac.getResource(ms, resourceName);             //Gets Statistics for Resource

         SimpleStats s = new SimpleStats(ms);
        cat1Stats = s.getCategoryStat("1");
        cat2Stats = s.getCategoryStat("2");
        cat3Stats = s.getCategoryStat("3");
        cat4Stats = s.getCategoryStat("4");
        cat5Stats = s.getCategoryStat("5");
        //cat6Stats = SimpleStats.statReturnCat6(ms);
       // cat7Stats = s.getCategoryStat("7");
       // cat8Stats = s.getCategoryStat("8");

        //cat7Stats = SimpleStats.createTimeChart(ms);
        failrate = Double.toString(s.getChartData());


        //a cat8Stats = SimpleStats.createMeter(ms);
    }
}

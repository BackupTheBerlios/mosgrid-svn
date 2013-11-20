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
import java.util.List;
import java.util.Map;

/**
 *Bean for DCI Stats
 * @author Alessandra
 */
public class DCIStatsBean {

    public String DCIName;
    public java.util.List<java.lang.String> resources;
    
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
   
    private String failrate;

    public String getFailrate() {
        return failrate;
    }

    public void setFailrate(String failrate) {
        this.failrate = failrate;
    }
    public String getDCIName() {
        return DCIName;
    }

    public void setDCIName(String DCIName) {
        this.DCIName = DCIName;
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

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public DCIStatsBean(String DCIId) throws ClassNotFoundException, SQLException {
        this.DCIName = DCIId;

        
        mif = new MetricInformationHarvester(new DBBase());
        mp = new hu.sztaki.lpds.statistics.db.MenuInformationHarvester(new DBBase());
        statsFac = new hu.sztaki.lpds.statistics.db.StatistiticsInformationHarvester(new DBBase());
        cat1Stats = new StringBuilder();
        cat2Stats = new StringBuilder();
        cat3Stats = new StringBuilder();
        cat4Stats = new StringBuilder();
        cat5Stats = new StringBuilder();
        cat6Stats = new StringBuilder();
       
        ms = mif.getMetric(StatisticLevel.DCI);         //Gets Metric Information
        statsFac.getDCI(ms, DCIId);                     //Fills metric information with statistics for DCI

        resources = mp.getResource(DCIId);              //Gets possible resources for list
        
        SimpleStats s = new SimpleStats(ms);
        cat1Stats = s.getCategoryStat("1");
        cat2Stats = s.getCategoryStat("2");
        cat3Stats = s.getCategoryStat("3");
        cat4Stats = s.getCategoryStat("4");
        cat5Stats = s.getCategoryStat("5");

       
         failrate = Double.toString(s.getChartData());
      
    }
}

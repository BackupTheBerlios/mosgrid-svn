/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.portlet;

import hu.sztaki.lpds.statistics.db.DBBase;
import hu.sztaki.lpds.statistics.db.MetricInformationHarvester;
import hu.sztaki.lpds.statistics.db.StatisticLevel;
import hu.sztaki.lpds.statistics.db.StatistiticsInformationHarvester;
import hu.sztaki.lpds.statistics.db.DataHarvester;
import java.sql.SQLException;

/**
 * Bean for Abstract Job
 * @author Alessandra
 */
public class JobBean {

    public String jobName;
    public String workflowId;
    private java.util.Map<java.lang.String, java.util.List<hu.sztaki.lpds.statistics.db.MetricInformation>> ms = null;
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
    private DataHarvester tgjob;

    public DataHarvester getTgjob() {
        return tgjob;
    }

    public void setTgjob(DataHarvester tgjob) {
        this.tgjob = tgjob;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
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

   

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public JobBean(String abjobName, String wfId) throws ClassNotFoundException, SQLException {
        this.jobName = abjobName;
        this.workflowId = wfId;
/*
        mif = new MetricInformationHarvester(new DBBase());
        mp = new hu.sztaki.lpds.statistics.db.MenuInformationHarvester(new DBBase());
        statsFac = new hu.sztaki.lpds.statistics.db.StatistiticsInformationHarvester(new DBBase());
        cat1Stats = new StringBuilder();
        cat2Stats = new StringBuilder();
        cat3Stats = new StringBuilder();
        cat4Stats = new StringBuilder();
        cat5Stats = new StringBuilder();
        cat6Stats = new StringBuilder();
        

        ms = mif.getMetric(StatisticLevel.ABSTRACTJOB);         //Gets Metric Information for an Abstract Job
        statsFac.getAbstractJob(ms, abjobName, wfId);           //Gets Statistics for an Abstract Job

         SimpleStats s = new SimpleStats(ms);
 */
        tgjob = new DataHarvester(wfId+"@"+abjobName,"job");
        cat1Stats = tgjob.getCat1Stats();
        cat2Stats = tgjob.getCat2Stats();
        cat3Stats = tgjob.getCat3Stats();;
        cat4Stats = tgjob.getCat4Stats();
        cat5Stats = tgjob.getCat5Stats();
       
        failrate = Double.toString(tgjob.getChartdata());
        

    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.statistics.portlet;

import hu.sztaki.lpds.statistics.db.DBBase;
import hu.sztaki.lpds.statistics.db.MetricInformation;
import hu.sztaki.lpds.statistics.db.MetricInformationHarvester;
import hu.sztaki.lpds.statistics.db.StatisticLevel;
import hu.sztaki.lpds.statistics.db.StatistiticsInformationHarvester;
import hu.sztaki.lpds.statistics.db.DataHarvester;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Alessandra
 */
public class WorkflowInstanceBean {

    public String workflowInstId;
    private java.util.Map<String, List<MetricInformation>> ms = null;
    private MetricInformationHarvester mif;
    private hu.sztaki.lpds.statistics.db.MenuInformationHarvester mp;
    private StatistiticsInformationHarvester statsFac;
    //public StringBuilder abJobList;
   // public StringBuilder wfIntList;
    public StringBuilder cat1Stats;
    public StringBuilder cat2Stats;
    public StringBuilder cat3Stats;
    public StringBuilder cat4Stats;
    public StringBuilder cat5Stats;
    public StringBuilder cat6Stats;
    public StringBuilder cat7Stats;
    public StringBuilder cat8Stats;
    private DataHarvester tginstance;
    private String failrate;

    public String getFailrate() {
        return failrate;
    }

    public void setFailrate(String failrate) {
        this.failrate = failrate;
    }
    public DataHarvester getTginstance() {
        return tginstance;
    }

    public void setTginstance(DataHarvester tginstance) {
        this.tginstance = tginstance;
    }

 /*   public StringBuilder getAbJobList() {
        return abJobList;
    }

    public void setAbJobList(StringBuilder abJobList) {
        this.abJobList = abJobList;
    }
  *
  */

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

   /* public StringBuilder getWfIntList() {
        return wfIntList;
    }

    public void setWfIntList(StringBuilder wfIntList) {
        this.wfIntList = wfIntList;
    }
    *
    */

    public String getWorkflowInstId() {
        return workflowInstId;
    }

    public void setWorkflowInstId(String workflowInstId) {
        this.workflowInstId = workflowInstId;
    }

 public WorkflowInstanceBean(String workflowInstId) throws ClassNotFoundException, SQLException {
        this.workflowInstId = workflowInstId;
        /*mif = new MetricInformationHarvester(new DBBase());

        mp = new hu.sztaki.lpds.statistics.db.MenuInformationHarvester(new DBBase());


        statsFac = new hu.sztaki.lpds.statistics.db.StatistiticsInformationHarvester(new DBBase());*/
        cat1Stats = new StringBuilder();
        cat2Stats = new StringBuilder();
        cat3Stats = new StringBuilder();
        cat4Stats = new StringBuilder();
        cat5Stats = new StringBuilder();
        cat6Stats = new StringBuilder();
        cat7Stats = new StringBuilder();
        cat8Stats = new StringBuilder();
/*
        ms = mif.getMetric(StatisticLevel.WORKFLOWINSTANCE);

        statsFac.getWorkflowInstance(ms, workflowInstId);
        SimpleStats s = new SimpleStats(ms);
  */

        tginstance = new DataHarvester(workflowInstId,"instance");
        cat1Stats = tginstance.getCat1Stats();
        cat2Stats = tginstance.getCat2Stats();
        cat3Stats = tginstance.getCat3Stats();
        cat4Stats = tginstance.getCat4Stats();
        cat5Stats = tginstance.getCat5Stats();
        //cat6Stats = SimpleStats.statReturnCat6(ms);
       /* cat7Stats = s.getCategoryStat("7");
        cat8Stats = s.getCategoryStat("8");
        */
        failrate = Double.toString(tginstance.getChartdata());
        
    }
}

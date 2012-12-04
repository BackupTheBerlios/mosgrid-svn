/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.statistics.portlet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akos
 */
public class DetailedWorkflowStats {

    private WorkflowStatsBean wfstats = null;
    private ArrayList<JobBean> jobstats = null;
    private ArrayList<WorkflowInstanceBean> inststats = null;

    public ArrayList<WorkflowInstanceBean> getInststats() {
        return inststats;
    }

    public void setInststats(ArrayList<WorkflowInstanceBean> inststats) {
        this.inststats = inststats;
    }

    public ArrayList<JobBean> getJobstats() {
        return jobstats;
    }

    public void setJobstats(ArrayList<JobBean> jobstats) {
        this.jobstats = jobstats;
    }

    public WorkflowStatsBean getWfstats() {
        return wfstats;
    }

    public void setWfstats(WorkflowStatsBean wfstats) {
        this.wfstats = wfstats;
    }


    public DetailedWorkflowStats(String userId, String wfId){
        try {
            jobstats = new ArrayList<JobBean>();
            wfstats = new WorkflowStatsBean(userId, wfId);
            inststats = new ArrayList<WorkflowInstanceBean>();
            for (String jobname : wfstats.getJobNames()) {
                jobstats.add(new JobBean(jobname, wfId));
            }

            for (String instance: wfstats.getWorkflowInstances().keySet()){
                inststats.add(new WorkflowInstanceBean(instance));
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DetailedWorkflowStats.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DetailedWorkflowStats.class.getName()).log(Level.SEVERE, null, ex);
        }





    }

}

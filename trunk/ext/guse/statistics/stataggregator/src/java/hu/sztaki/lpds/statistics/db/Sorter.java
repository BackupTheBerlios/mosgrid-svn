/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Sorts the set of aggregate jobs into maps for each of teh six types of statistics to be stored
 * Each map has a key which is equivilent to an entry of the idenity of the entity
 * [portal have URL, resource have URL, Abstract jobs have jobNames and wfID]
 *
 * @author smoniz
 */
public class Sorter {

    private Map<String, Portal> portals = new HashMap<String, Portal>();
    private Map<String, Resource> resources = new HashMap<String, Resource>();
    private Map<String, User> users = new HashMap<String, User>();
    private Map<String, AbstractJob> abstractJobs = new HashMap<String, AbstractJob>();
    private Map<String, ConcreteWorkflow> concreteWorkflows = new HashMap<String, ConcreteWorkflow>();
    private Map<String, WorkflowInstance> workflowInstances = new HashMap<String, WorkflowInstance>();
    private Map<String, DCI> dcis = new HashMap<String, DCI>();

    /**
     * Add each of the given ajobs to each of the six. Every aJob entry will be added to exactly one entry of each map
     * @param aJobs
     */
    public void sort(Collection<StataggregateJob> aJobs, DBBase con) {


        for (StataggregateJob job : aJobs) {
            addToPortals(job);
            addToResources(job, con);
            addToUsers(job);
            addToAbstractJobs(job);
            addToWorkflowInstances(job);

        }
        addToDCIs();
        addToConcreteWorkflows(con);

    }

    /**
     * Add the job to the correct concrete workflow
     * @param job
     */
    private void addToWorkflowInstances(StataggregateJob job) {
        String wrtid = job.getWrtID();
        if (workflowInstances.containsKey(wrtid)) {
            WorkflowInstance r = workflowInstances.get(wrtid);
            r.stats.addAggregateJob(job);
        }
        else {
            WorkflowInstance r = new WorkflowInstance(wrtid);
            r.stats.addAggregateJob(job);
            workflowInstances.put(wrtid, r);
        }
    }

    /**
     * As an abstract workflow is composed of abstract jobs, rather then loop through the jobs just aggregate the abstract jobs
     */
    private void addToConcreteWorkflows(DBBase con) {
        for (AbstractJob j : abstractJobs.values()) {
            if (concreteWorkflows.containsKey(j.wfID)) {
                concreteWorkflows.get(j.wfID).stats.addStatistics(j.stats);
            }
            else {
                ConcreteWorkflow awf = new ConcreteWorkflow(j.wfID);
                awf.stats.addStatistics(j.stats);
                concreteWorkflows.put(j.wfID, awf);

            }


        }
        for (ConcreteWorkflow wf : concreteWorkflows.values()) {
            try {
                wf.GetConcreteWorkflowStatistics(con.getConnection());
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
  catch (IOException e1) {
                e1.printStackTrace();
            }


        }
    }

    private void addToDCIs() {
        for (Resource res : resources.values()) {

            if (dcis.containsKey(res.DCI)) {
                dcis.get(res.DCI).stats.addStatistics(res.stats);
            }
            else {

                DCI adci = new DCI(res.DCI);
                adci.stats.addStatistics(res.stats);
                dcis.put(res.DCI, adci);

            }

        }

    }

    /**
     * Add the job to the correct abstract job OR create a new abstract job entry if it does not yet exist.
     * @param job
     */
    private void addToAbstractJobs(StataggregateJob job) {
        String jobName = job.getJobName();
        String wfid = job.getWfID();
        String key = jobName + wfid;
        if (abstractJobs.containsKey(key)) {
            AbstractJob r = abstractJobs.get(key);
            r.stats.addAggregateJob(job);
        }
        else {
            AbstractJob r = new AbstractJob(jobName, wfid);
            r.stats.addAggregateJob(job);
            abstractJobs.put(key, r);
        }
    }

    /**
     * Add the job to the correct user OR create a new user entry if it does not yet exist.
     * @param job
     */
    private void addToUsers(StataggregateJob job) {
        String userID = job.getUserID();
        if (users.containsKey(userID)) {
            User r = users.get(userID);
            r.stats.addAggregateJob(job);
        }
        else {
            User r = new User(userID);
            r.stats.addAggregateJob(job);
            users.put(userID, r);
        }
    }

    /**
     * Add the job to the correct resource OR create a new resource entry if it does not yet exist.
     * @param job
     */
    private void addToResources(StataggregateJob job, DBBase con) {
        String URL = job.getResource();
        if (resources.containsKey(URL)) {
            Resource r = resources.get(URL);
            r.stats.addAggregateJob(job);
            r.togetdci = job;
        }
        else {
            Resource r = new Resource(URL);
            r.stats.addAggregateJob(job);
            resources.put(URL, r);
            r.togetdci = job; 
            r.populateDCI(con);
        }
    }

    /**
     * Add the job to the correct portal OR create a new portal entry if it does not yet exist.
     * @param job
     */
    private void addToPortals(StataggregateJob job) {
        String portalURL = job.getPortalID();
        if (portals.containsKey(portalURL)) {
            Portal p = portals.get(portalURL);
            p.stats.addAggregateJob(job);
        }
        else {
            Portal p = new Portal(portalURL);
            p.stats.addAggregateJob(job);
            portals.put(portalURL, p);
        }
    }

    /**
     * Combine all the maps for insertion
     * @return
     */
    public Collection<Entity> getEntities() {
        Collection<Entity> entities = new HashSet<Entity>();
        entities.addAll(portals.values());
        entities.addAll(resources.values());
        entities.addAll(concreteWorkflows.values());
        entities.addAll(abstractJobs.values());
        entities.addAll(workflowInstances.values());
        entities.addAll(users.values());
        entities.addAll(dcis.values());


        return entities;
    }

    /**
     * Insert all of the new statistics entries to the database
     * @param con
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void put(Connection con) throws SQLException, ClassNotFoundException {
        for (Entity p : getEntities()) {
            p.stats.updateStatistics(con);
            p.testThenInsertEntity(con);
        }

    }
}

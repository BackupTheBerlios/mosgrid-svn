/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package hu.sztaki.lpds.pgportal.visualizer.net.xmlrpc.server;

import hu.sztaki.lpds.pgportal.visualizer.net.xmlrpc.common.ClientJob;
import hu.sztaki.lpds.pgportal.visualizer.net.xmlrpc.common.ObjectSerializer;
import hu.sztaki.lpds.pgportal.visualizer.net.xmlrpc.common.VISWFSService;
import hu.sztaki.lpds.pgportal.visualizer.net.xmlrpc.common.VisualizerObject;

import hu.sztaki.lpds.wfs.service.angie.Base;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * A visualizerObject (job statuszok es kapcsolatok) objektum osszeallitasa.
 *
 * @author lpds
 */
public class VISWFSServiceImpl implements VISWFSService {
    
    
    // old system begin
    // from workflowDetatils.jsp
    // private String[] Status_Names = { "init", "submitted", "running", "finished", "error", "hold", "migrating", "waiting", "scheduled" };
    // private String[] Status_Colors = { "white", "orange", "red", "lime", "blue", "yellow", "brown", "darkslateblue", "darkmagenta" };
    //
    // 0 - init - white
    // 1 - submitted - orange
    // 2 - running - red
    // 3 - finished - lime
    // 4 - error - blue
    // 5 - hold - yellow
    // 6 - migrating - brown
    // 7 - waiting - darkslateblue
    // 8 - scheduled - darkmagenta
    //
    // lefutas okey: 1 8 7 2 3
    // submitted - scheduled - waiting - running - finished
    //
    // lefutas error: 1 8 7 2 4
    // submitted - scheduled - waiting - running - error
    // old system end
    
    // new system begin
    // status 0, 1, 2, 3, 4, 5, 6, 7, 8, 9
    //   tim0          tim1   tim2        tim3      tim4        tim5      tim6       tim7    tim8   tim9
    // {"incompleted","init","submitted","waiting","scheduled","running","finished","error","hold","migrating"}
    // use only: tim2 - tim7
    //
    // 0 - incompleted - ??? (not used)
    // 1 - init - white (not used)
    //
    // 2 - submitted - orange
    // 3 - waiting - darkslateblue
    // 4 - scheduled - darkmagenta
    // 5 - running - red
    // 6 - finished - lime
    // 7 - error - blue
    //
    // 8 - hold - yellow (not used)
    // 9 - migrating - brown (not used)
    //
    // lefutas okey: 2 3 4 5 6
    // submitted - waiting - scheduled - running - finished
    //
    // lefutas error: 2 3 4 5 7
    // submitted - waiting - scheduled - running - error
    // new system end
    
    public VISWFSServiceImpl() {}
    
    /**
     * Visszateresi erteke a kert workflow objektum (visualizerObject) szerializalva string-ben.
     */
    public String getVisualizerWorkflow(String portalid, String userid, String wfname, String rtid) {
        return getWorkflow(portalid, userid, wfname, rtid);
    }
    
    /**
     * Visszateresi erteke a kert workflow objektum (visualizerObject) szerializalva string-ben.
     * @param portalid
     * @param userid
     * @param wfname
     * @param rtid
     * @return
     */
    private String getWorkflow(String portalid, String userid, String wfname, String rtid) {
        VisualizerObject visualizerObject = new VisualizerObject();
        Vector jobNames = SelectSqlJobNames(portalid, userid, wfname);
        Vector jobStatus = SelectSqlJobStatus(portalid, userid, wfname, rtid);
        Vector preJobInfo = SelectSqlPreJobInfo(portalid, userid, wfname, rtid);
        Hashtable tmpJobNameIdTable = new Hashtable();
        Hashtable tmpNoStatusTable = new Hashtable();
        // System.out.println("JobStatus vector.size: " + jobStatus.size());
        // System.out.println("JobStatus: " + jobStatus.toString());
        // System.out.println("preJobInfo vector.size: " + preJobInfo.size());
        // System.out.println("preJobInfo: " + preJobInfo.toString());
        // create job NameId table
        for (int i = 0; i < jobNames.size(); i++) {
            Hashtable idHash = (Hashtable) jobNames.elementAt(i);
            tmpJobNameIdTable.put(idHash.get("name").toString(), idHash.get("id").toString());
            tmpNoStatusTable.put(idHash.get("name").toString(), idHash.get("id").toString());
        }
        // add job status info
        for (int i = 0; i < jobStatus.size(); i++) {
            Hashtable jobHash = (Hashtable) jobStatus.elementAt(i);
            Date tim2 = null;
            if (jobHash.containsKey("tim2")) {
                tim2 = (Date) jobHash.get("tim2");
            }
            if ((tim2 != null) && (tim2.getTime() > 0)) {
                ClientJob tmpjob = new ClientJob();
                tmpNoStatusTable.remove(jobHash.get("name").toString());
                tmpjob.setId(jobHash.get("id").toString());
                tmpjob.setName(jobHash.get("name").toString());
                tmpjob.setResource(jobHash.get("resource").toString());
                tmpjob.clearStatuslist();
                long start = tim2.getTime();
                Date tim3 = (Date) jobHash.get("tim3");
                Date tim4 = (Date) jobHash.get("tim4");
                Date tim5 = (Date) jobHash.get("tim5");
                Date tim6 = (Date) jobHash.get("tim6");
                Date tim7 = (Date) jobHash.get("tim7");
                // tmpjob.addStatus(new Integer(0), tim0); // incompleted not used
                // tmpjob.addStatus(new Integer(1), tim1); // init not used
                tmpjob.addStatus(new Integer(2), tim2);
                if (tim3.getTime() > start) {
                    tmpjob.addStatus(new Integer(3), tim3);
                }
                if (tim4.getTime() > start) {
                    tmpjob.addStatus(new Integer(4), tim4);
                }
                if (tim5.getTime() > start) {
                    tmpjob.addStatus(new Integer(5), tim5);
                }
                if ((tim6.getTime() > start) && (tim7.getTime() > start)) {
                    tmpjob.addStatus(new Integer(6), tim6);
                } else {
                    if (tim6.getTime() > start) {
                        tmpjob.addStatus(new Integer(6), tim6);
                    }
                    if (tim7.getTime() > start) {
                        tmpjob.addStatus(new Integer(7), tim7);
                    }
                }
                // tmpjob.addStatus(new Integer(8), tim8); // hold not used
                // tmpjob.addStatus(new Integer(9), tim9); // migrating not used
                tmpjob.orderStatusListByTime();
                // addConnections, scan preJobInfo
                for (int ipre = 0; ipre < preJobInfo.size(); ipre++) {
                    Hashtable preHash = (Hashtable) preJobInfo.elementAt(ipre);
                    // add inConnectionS
                    String jobname = (String) preHash.get("jobname");
                    String prejobname = (String) preHash.get("prejobname");
                    // System.out.println("jobname    : " + jobname);
                    // System.out.println("prejobname : " + prejobname);
                    // addInConnection
                    if (tmpjob.getName().equals(jobname)) {
                        tmpjob.addInConnection(tmpjob.getId(), tmpjob.getName(), tmpjob.getStatuslist(new Integer(2)), (String) tmpJobNameIdTable.get(prejobname));
                    }
                    // addOutConnection
                    if (tmpjob.getName().equals(prejobname)) {
                        // tmpjob.addOutConnection(tmpjob.getId(), tmpjob.getName(), tmpjob.getStatuslist(new Integer(2)), (String) tmpJobNameIdTable.get(jobname));
                        tmpjob.addOutConnection(tmpjob.getId(), tmpjob.getName(), tmpjob.getExit(), (String) tmpJobNameIdTable.get(jobname));
                    }
                }
                visualizerObject.addJob(tmpjob);
            }
        }
        // add jobs without status info
        if (!tmpNoStatusTable.isEmpty()) {
            Enumeration enumNo = tmpNoStatusTable.keys();
            while (enumNo.hasMoreElements()) {
                String noKey = (String) enumNo.nextElement();
                String noVal = (String) tmpNoStatusTable.get(noKey);
                ClientJob tmpjob = new ClientJob();
                tmpjob.setName(noKey);
                tmpjob.setId(noVal);
                tmpjob.setResource("init");
                tmpjob.clearStatuslist();
                tmpjob.orderStatusListByTime();
                // addConnections, scan preJobInfo
                for (int ipre = 0; ipre < preJobInfo.size(); ipre++) {
                    Hashtable preHash = (Hashtable) preJobInfo.elementAt(ipre);
                    // add inConnectionS
                    String jobname = (String) preHash.get("jobname");
                    String prejobname = (String) preHash.get("prejobname");
                    // System.out.println("jobname    : " + jobname);
                    // System.out.println("prejobname : " + prejobname);
                    // addInConnection
                    if (tmpjob.getName().equals(jobname)) {
                        tmpjob.addInConnection(tmpjob.getId(), tmpjob.getName(), tmpjob.getStatuslist(new Integer(2)), (String) tmpJobNameIdTable.get(prejobname));
                    }
                    // addOutConnection
                    if (tmpjob.getName().equals(prejobname)) {
                        // tmpjob.addOutConnection(tmpjob.getId(), tmpjob.getName(), tmpjob.getStatuslist(new Integer(2)), (String) tmpJobNameIdTable.get(jobname));
                        tmpjob.addOutConnection(tmpjob.getId(), tmpjob.getName(), tmpjob.getExit(), (String) tmpJobNameIdTable.get(jobname));
                    }
                }
                visualizerObject.addJob(tmpjob);
            }
        }
        // System.out.println("visObj.getJobs : " + visualizerObject.getJobs());
        return ObjectSerializer.serializeObjectToSting(visualizerObject);
    }
    
    /**
     * A job neveket keri le az adatbazistol es adja vissza a hivonak vector-ban.
     * @param portalid
     * @param userid
     * @param wfname
     * @return
     */
    private Vector SelectSqlJobNames(String portalid, String userid, String wfname) {
        Vector retVector = new Vector();
        Hashtable hash = null;
        StringBuffer sql;
        PreparedStatement preparedStatementJobNames;
        try {
            Connection connection = Base.getConnection();
            sql = new StringBuffer();
            sql.append("SELECT aj.id as id, aj.name as name ");
            sql.append("FROM `workflow` as w, `aworkflow` as aw, `ajob` as aj ");
            sql.append("WHERE aw.id_portal = ? AND aw.id_user = ? AND w.name = ? ");
            sql.append("AND w.id_aworkflow = aw.id ");
            sql.append("AND w.id_aworkflow = aj.id_aworkflow ");
            sql.append("ORDER BY aj.id ");
            preparedStatementJobNames = connection.prepareStatement(sql.toString());
            preparedStatementJobNames.setString(1, portalid);
            preparedStatementJobNames.setString(2, userid);
            preparedStatementJobNames.setString(3, wfname);
            ResultSet resultSet = preparedStatementJobNames.executeQuery();
            while (resultSet.next()) {
                hash = new Hashtable();
                hash.put("id", resultSet.getString("id"));
                hash.put("name", resultSet.getString("name"));
                retVector.addElement(hash);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVector;
    }
    
    /**
     * A job statuszokat keri le az adatbazistol es adja vissza a hivonak vector-ban.
     * @param portalid
     * @param userid
     * @param wfname
     * @param rtid
     * @return
     */
    private Vector SelectSqlJobStatus(String portalid, String userid, String wfname, String rtid) {
        Vector retVector = new Vector();
        Hashtable hash = null;
        StringBuffer sql;
        PreparedStatement preparedStatementJobStatus;
        try {
            Connection connection = Base.getConnection();
            sql = new StringBuffer();
            sql.append("SELECT s.id_ajob as id, aj.name as name, js.resource as resource, ");
            sql.append("s.tim0, s.tim1, s.tim2, s.tim3, s.tim4, s.tim5, s.tim6, s.tim7, s.tim8 ");
            sql.append("FROM `status` as s, `workflow` as w, `aworkflow` as aw, `job_status` as js, `ajob` as aj ");
            sql.append("WHERE aw.id_portal = ? AND aw.id_user = ? AND w.name = ? AND s.id_rt = ? ");
            sql.append("AND w.id_aworkflow = aw.id ");
            sql.append("AND s.id_ajob = aj.id ");
            sql.append("AND w.id = js.id_workflow AND s.id_ajob = js.id_ajob AND s.id_rt = js.wrtid ");
            sql.append("ORDER BY s.id_ajob ");
            preparedStatementJobStatus = connection.prepareStatement(sql.toString());
            preparedStatementJobStatus.setString(1, portalid);
            preparedStatementJobStatus.setString(2, userid);
            preparedStatementJobStatus.setString(3, wfname);
            preparedStatementJobStatus.setString(4, rtid);
            ResultSet resultSet = preparedStatementJobStatus.executeQuery();
            while (resultSet.next()) {
                hash = new Hashtable();
                hash.put("id", resultSet.getString("id"));
                hash.put("name", resultSet.getString("name"));
                hash.put("resource", resultSet.getString("resource"));
                // hash.put("tim0", new Date(resultSet.getLong("tim0"))); // incompleted
                // hash.put("tim1", new Date(resultSet.getLong("tim1"))); // init
                hash.put("tim2", new Date(resultSet.getLong("tim2")));
                hash.put("tim3", new Date(resultSet.getLong("tim3")));
                hash.put("tim4", new Date(resultSet.getLong("tim4")));
                hash.put("tim5", new Date(resultSet.getLong("tim5")));
                hash.put("tim6", new Date(resultSet.getLong("tim6")));
                hash.put("tim7", new Date(resultSet.getLong("tim7")));
                // hash.put("tim8", new Date(resultSet.getLong("tim8"))); // hold
                // hash.put("tim9", new Date(resultSet.getLong("tim9"))); // migrating
                retVector.addElement(hash);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVector;
    }
    
    /**
     * A job kapcsolatokat adja vissza.
     * @param portalid
     * @param userid
     * @param wfname
     * @param rtid
     * @return
     */
    private Vector SelectSqlPreJobInfo(String portalid, String userid, String wfname, String rtid) {
        Vector retVector = new Vector();
        Hashtable hash = null;
        StringBuffer sql;
        PreparedStatement preparedStatementPreJobInfo;
        try {
            Connection connection = Base.getConnection();
            sql = new StringBuffer();
            sql.append("SELECT aj.name as jobname, ai.prejob as prejobname ");
            sql.append("FROM `status` as s, `workflow` as w, `aworkflow` as aw, `ajob` as aj, `ainput` as ai ");
            sql.append("WHERE aw.id_portal = ? AND aw.id_user = ? AND w.name = ? AND s.id_rt = ? ");
            sql.append("AND w.id_aworkflow = aw.id ");
            sql.append("AND w.id = s.id_workflow ");
            sql.append("AND s.id_ajob = aj.id ");
            sql.append("AND s.id_ajob = ai.id_ajob ");
            sql.append("AND ai.prejob <> '' ");
            sql.append("ORDER BY s.id_ajob ");
            preparedStatementPreJobInfo = connection.prepareStatement(sql.toString());
            preparedStatementPreJobInfo.setString(1, portalid);
            preparedStatementPreJobInfo.setString(2, userid);
            preparedStatementPreJobInfo.setString(3, wfname);
            preparedStatementPreJobInfo.setString(4, rtid);
            ResultSet resultSet = preparedStatementPreJobInfo.executeQuery();
            while (resultSet.next()) {
                hash = new Hashtable();
                hash.put("jobname", resultSet.getString("jobname"));
                hash.put("prejobname", resultSet.getString("prejobname"));
                retVector.addElement(hash);
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVector;
    }
    
}

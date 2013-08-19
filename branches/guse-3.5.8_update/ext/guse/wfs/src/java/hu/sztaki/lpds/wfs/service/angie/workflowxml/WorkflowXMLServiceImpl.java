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
package hu.sztaki.lpds.wfs.service.angie.workflowxml;

import hu.sztaki.lpds.wfs.service.angie.Base;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * A workflow kezeleshez szükseges adatbazis müveletek implementaciojat tartalmazza.
 * @author lpds
 */
public class WorkflowXMLServiceImpl implements WorkflowXMLService {

    private Connection connection;

    public WorkflowXMLServiceImpl() throws Exception {
        connection = Base.getConnection();
    }

    public long saveAWorkflow(boolean newWorkflow, long aworkflowid, String portalid, String userid, String wfname, String text) throws Exception {
        long retAutoIncrementID = 0;
        if ((newWorkflow) || (aworkflowid == 0)) {
            // letrehoz uj abstract workflow (autoincrement id)
            retAutoIncrementID = InsertSqlAWorkflow(portalid, userid, wfname, text);
        }
        return retAutoIncrementID;
    }

    public long saveAJob(boolean newWorkflow, long aworkflowid, long ajobid, String wfname, String text, String x, String y) throws Exception {
        long retAutoIncrementID = 0;
        if ((newWorkflow) || (ajobid == 0)) {
            // letrehoz uj abstract job (autoincrement id)
            retAutoIncrementID = InsertSqlAJob(aworkflowid, wfname, text, x, y);
        }
        return retAutoIncrementID;
    }

    public long saveAInput(boolean newWorkflow, long abstractJobId, long abstractPortId, String portname, String prejob, String preoutput, String portseq, String text, String x, String y) throws Exception {
        long retAutoIncrementID = 0;
        if ((newWorkflow) || (abstractPortId == 0)) {
            // letrehoz uj abstract input (autoincrement id)
            retAutoIncrementID = InsertSqlAInput(abstractJobId, portname, prejob, preoutput, portseq, text, x, y);
        }
        return retAutoIncrementID;
    }

    public long saveAOutput(boolean newWorkflow, long abstractJobId, long abstractPortId, String portname, String portseq, String text, String x, String y) throws Exception {
        long retAutoIncrementID = 0;
        if ((newWorkflow) || (abstractPortId == 0)) {
            // letrehoz uj abstract output (autoincrement id)
            retAutoIncrementID = InsertSqlAOutput(abstractJobId, portname, portseq, text, x, y);
        }
        return retAutoIncrementID;
    }

    /**
     * Letrehoz egy aworkflow-t az adatbazisban.
     */
    private long InsertSqlAWorkflow(String portalid, String userid, String wfname, String text) throws Exception {
        long retAutoIncrementID = 0;
        String sql = "INSERT INTO aworkflow (id_portal, id_user, name, txt) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatementInsertSqlAWorkflow = connection.prepareStatement(sql);
        preparedStatementInsertSqlAWorkflow.setString(1, portalid);
        preparedStatementInsertSqlAWorkflow.setString(2, userid);
        preparedStatementInsertSqlAWorkflow.setString(3, wfname);
        preparedStatementInsertSqlAWorkflow.setString(4, text);
        preparedStatementInsertSqlAWorkflow.execute();
        ResultSet resultSet = preparedStatementInsertSqlAWorkflow.getGeneratedKeys();
        if (resultSet.next()) {
            retAutoIncrementID = resultSet.getLong(1);
        } else {
            // throw an exception
        }
        return retAutoIncrementID;
    }

    /**
     * Letrehoz egy ajob-ot az adatbazisban.
     */
    private long InsertSqlAJob(long aworkflowid, String wfname, String text, String x, String y) throws Exception {
        long retAutoIncrementID = 0;
        String sql = "INSERT INTO ajob (id_aworkflow, name, txt, x, y) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatementInsertSqlAJob = connection.prepareStatement(sql);
        preparedStatementInsertSqlAJob.setLong(1, aworkflowid);
        preparedStatementInsertSqlAJob.setString(2, wfname);
        preparedStatementInsertSqlAJob.setString(3, text);
        preparedStatementInsertSqlAJob.setString(4, x);
        preparedStatementInsertSqlAJob.setString(5, y);
        preparedStatementInsertSqlAJob.execute();
        ResultSet resultSet = preparedStatementInsertSqlAJob.getGeneratedKeys();
        if (resultSet.next()) {
            retAutoIncrementID = resultSet.getLong(1);
        } else {
            // throw an exception
        }
        return retAutoIncrementID;
    }

    /**
     * Letrehoz egy ainput port-ot az adatbazisban.
     */
    private long InsertSqlAInput(long abstractJobId, String portname, String prejob, String preoutput, String portseq, String text, String x, String y) throws Exception {
        long retAutoIncrementID = 0;
        String sql = "INSERT INTO ainput (id_ajob, name, prejob, preoutput, seq, txt, x, y) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatementInsertSqlAInput = connection.prepareStatement(sql);
        preparedStatementInsertSqlAInput.setLong(1, abstractJobId);
        preparedStatementInsertSqlAInput.setString(2, portname);
        preparedStatementInsertSqlAInput.setString(3, prejob);
        preparedStatementInsertSqlAInput.setString(4, preoutput);
        preparedStatementInsertSqlAInput.setString(5, portseq);
        preparedStatementInsertSqlAInput.setString(6, text);
        preparedStatementInsertSqlAInput.setString(7, x);
        preparedStatementInsertSqlAInput.setString(8, y);
        preparedStatementInsertSqlAInput.execute();
        ResultSet resultSet = preparedStatementInsertSqlAInput.getGeneratedKeys();
        if (resultSet.next()) {
            retAutoIncrementID = resultSet.getLong(1);
        } else {
            // throw an exception
        }
        return retAutoIncrementID;
    }

    /**
     * Letrehoz egy aoutput port-ot az adatbazisban.
     */
    private long InsertSqlAOutput(long abstractJobId, String portname, String portseq, String text, String x, String y) throws Exception {
        long retAutoIncrementID = 0;
        String sql = "INSERT INTO aoutput (id_ajob, name, seq, txt, x, y) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatementInsertSqlAOutput = connection.prepareStatement(sql);
        preparedStatementInsertSqlAOutput.setLong(1, abstractJobId);
        preparedStatementInsertSqlAOutput.setString(2, portname);
        preparedStatementInsertSqlAOutput.setString(3, portseq);
        preparedStatementInsertSqlAOutput.setString(4, text);
        preparedStatementInsertSqlAOutput.setString(5, x);
        preparedStatementInsertSqlAOutput.setString(6, y);
        preparedStatementInsertSqlAOutput.execute();
        ResultSet resultSet = preparedStatementInsertSqlAOutput.getGeneratedKeys();
        if (resultSet.next()) {
            retAutoIncrementID = resultSet.getLong(1);
        } else {
            // throw an exception
        }
        return retAutoIncrementID;
    }

    public ResultSet getAWorkflow(String portalid, String userid, String wfname) throws Exception {
        return SelectSqlAWorkflow(portalid, userid, wfname);
    }

    public ResultSet getAJob(long aworkflowid) throws Exception {
        return SelectSqlAJob(aworkflowid);
    }

    public ResultSet getAInput(long aworkflowid) throws Exception {
        return SelectSqlAllAInput(aworkflowid);
    }

    public ResultSet getAOutput(long aworkflowid) throws Exception {
        return SelectSqlAllAOutput(aworkflowid);
    }

    /**
     * Visszaadja az ajob adatait ResultSet-ben.
     *
     * @param aworkflowid
     * @param jobname
     * @return ResulSet
     * @throws Exception
     */
    public ResultSet getAJobData(long aworkflowid, String jobname) throws Exception {
        return SelectSqlAJobData(aworkflowid, jobname);
    }

    /**
     * Visszaadja az ainput adatait ResultSet-ben.
     *
     * @param ajobid
     * @param portname
     * @return ResulSet
     * @throws Exception
     */
    public ResultSet getAInputData(long ajobid, String portname) throws Exception {
        return SelectSqlAInputData(ajobid, portname);
    }

    /**
     * Visszaadja az aoutput adatait ResultSet-ben.
     *
     * @param ajobid
     * @param portname
     * @return ResulSet
     * @throws Exception
     */
    public ResultSet getAOutputData(long ajobid, String portname) throws Exception {
        return SelectSqlAOutputData(ajobid, portname);
    }

    public String getAWorkflowName(String portalID, String userID, String workflowname) throws Exception {
        return SelectSqlAWorkflowName(portalID, userID, workflowname);
    }

    public ResultSet getWorkflowData(String portalID, String userID, String workflowname) throws Exception {
        return SelectSqlWorkflowData(portalID, userID, workflowname);
    }

    public ResultSet getGrafWorkflowData(String portalID, String userID, String grafworkflowname) throws Exception {
        return SelectSqlGrafWorkflowData(portalID, userID, grafworkflowname);
    }

    public ResultSet getAllWorkflowData(String portalID, String userID, String grafName, String abstName, String realName) throws Exception {
        return SelectSqlAllWorkflowData(portalID, userID, grafName, abstName, realName);
    }

    public String getAbstractWorkflowName(String portalID, String userID, String workflowname) throws Exception {
        return SelectSqlAbstWorkflowName(portalID, userID, workflowname);
    }

    /**
     * Bejegyzeseket torol.
     *
     * (a workflow_prop tablabol)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @param name - property name
     * @throws Exception
     */
    public void deleteWorkflowInstancesFromWorkflowProp(String workflowID, String runtimeID, String name) throws Exception {
        deleteSqldeleteWorkflowInstancesFromWorkflowProp(workflowID, runtimeID, name);
    }

    /**
     * A workflow (real) osszes peldanyat adja vissza.
     *
     * (a workflow_prop tablabol)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @return ResultSet - instances
     * @throws Exception
     */
    public ResultSet getWorkflowInstancesFromWorkflowProp(String workflowID, String runtimeID) throws Exception {
        return SelectSqlgetWorkflowInstancesFromWorkflowProp(workflowID, runtimeID);
    }

    /**
     * A workflow (real) osszes peldanyat adja vissza.
     *
     * (a job_status tablabol)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @return ResultSet - instances
     * @throws Exception
     */
    public ResultSet getWorkflowInstancesFromJobStatus(String workflowID, String runtimeID) throws Exception {
        return SelectSqlgetWorkflowInstancesFromJobStatus(workflowID, runtimeID);
    }

    /**
     * A workflow (real) osszes peldanyat adja vissza.
     *
     * (a status tablabol)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @return ResultSet - instances
     * @throws Exception
     */
    public ResultSet getWorkflowInstancesFromStatus(String workflowID, String runtimeID) throws Exception {
        return SelectSqlgetWorkflowInstancesFromStatus(workflowID, runtimeID);
    }

    /**
     * A workflow (real) osszes peldanyat menti le.
     *
     * (a workflow_prop tablaba)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @param name - property neve
     * @param value - property ertek
     * @throws Exception
     */
    public void saveWorkflowInstancesToWorkflowProp(String workflowID, String runtimeID, String name, String value) throws Exception {
        InsertSqlsaveWorkflowInstancesToWorkflowProp(workflowID, runtimeID, name, value);
    }

    /**
     * A workflow (real) osszes peldanyat menti le.
     *
     * (a job_status tablaba)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @param pid - job pid azonosito
     * @param status - job status
     * @param resource - job resource
     * @param aworkflowid - aworkflow azonosito
     * @param jobname - job name
     * @throws Exception
     */
    public void saveWorkflowInstancesToJobStatus(String workflowID, String runtimeID, String pid, String status, String resource, String aworkflow, String jobname) throws Exception {
        InsertSqlsaveWorkflowInstancesToJobStatus(workflowID, runtimeID, pid, status, resource, aworkflow, jobname);
    }

    /**
     * A workflow (real) osszes peldanyat menti le.
     *
     * (a status tablaba)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @param tim2 - status 2 idopont
     * @param tim3 - status 3 idopont
     * @param tim4 - status 4 idopont
     * @param tim5 - status 5 idopont
     * @param tim6 - status 6 idopont
     * @param tim7 - status 7 idopont
     * @param aworkflowid - aworkflow azonosito
     * @param jobname - job name
     * @throws Exception
     */
    public void saveWorkflowInstancesToStatus(String workflowID, String runtimeID, String tim2, String tim3, String tim4, String tim5, String tim6, String tim7, String aworkflow, String jobname) throws Exception {
        InsertSqlsaveWorkflowInstancesToStatus(workflowID, runtimeID, tim2, tim3, tim4, tim5, tim6, tim7, aworkflow, jobname);
    }

    /**
     * A workflow adatait adja vissza ResultSet-ben.
     * @param portalID - portal azonosito
     * @param userID - felhasznalo azonosito
     * @param wfname
     * @return
     * @throws Exception
     */
    private ResultSet SelectSqlAWorkflow(String portalid, String userid, String wfname) throws Exception {
        String sql = "SELECT id, txt FROM aworkflow WHERE id_portal = ? and id_user = ? and name = ? ORDER BY id";
        PreparedStatement preparedStatementAWorkflow = connection.prepareStatement(sql);
        preparedStatementAWorkflow.setString(1, portalid);
        preparedStatementAWorkflow.setString(2, userid);
        preparedStatementAWorkflow.setString(3, wfname);
        return preparedStatementAWorkflow.executeQuery();
    }

    /**
     * A workflow-ban szerplö összes job adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return
     * @throws Exception
     */
    private ResultSet SelectSqlAJob(long aworkflowid) throws Exception {
        String sql = "SELECT id, name, txt, x, y FROM ajob WHERE id_aworkflow = ? ORDER BY id";
        PreparedStatement preparedStatementAllAJob = connection.prepareStatement(sql);
        preparedStatementAllAJob.setLong(1, aworkflowid);
        return preparedStatementAllAJob.executeQuery();
    }

    /**
     * A workflow-ban szerplö összes input port adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return
     * @throws Exception
     */
    private ResultSet SelectSqlAllAInput(long aworkflowid) throws Exception {
        String sql = "SELECT i.id_ajob, i.id, i.name, i.prejob, i.preoutput, i.seq, i.txt, i.x, i.y " +
                "FROM ajob as j, ainput as i " +
                "WHERE j.id_aworkflow = ? and j.id = i.id_ajob " +
                "ORDER BY i.id_ajob, i.id";
        PreparedStatement preparedStatementAllAInput = connection.prepareStatement(sql);
        preparedStatementAllAInput.setLong(1, aworkflowid);
        return preparedStatementAllAInput.executeQuery();
    }

    /**
     * A workflow-ban szerplö összes output port adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return
     * @throws Exception
     */
    private ResultSet SelectSqlAllAOutput(long aworkflowid) throws Exception {
        String sql = "SELECT o.id_ajob, o.id, o.name, o.seq, o.txt, o.x, o.y " +
                "FROM ajob as j, aoutput as o " +
                "WHERE j.id_aworkflow = ? and j.id = o.id_ajob " +
                "ORDER BY o.id_ajob, o.id";
        PreparedStatement preparedStatementAllAOutput = connection.prepareStatement(sql);
        preparedStatementAllAOutput.setLong(1, aworkflowid);
        return preparedStatementAllAOutput.executeQuery();
    }

    /**
     * Visszaadja az ajob adatait ResultSet-ben.
     *
     * @param aworkflowid
     * @param jobname
     * @return ResulSet
     * @throws Exception
     */
    private ResultSet SelectSqlAJobData(long aworkflowid, String jobname) throws Exception {
        String sql = "SELECT aj.id, aj.id_aworkflow, aj.name, aj.txt, aj.x, aj.y " +
                "FROM ajob as aj WHERE aj.id_aworkflow = ? and aj.name = ?";
        PreparedStatement preparedStatementAJobData = connection.prepareStatement(sql);
        preparedStatementAJobData.setLong(1, aworkflowid);
        preparedStatementAJobData.setString(2, jobname);
        return preparedStatementAJobData.executeQuery();
    }

    /**
     * Visszaadja az ainput adatait ResultSet-ben.
     *
     * @param ajobid
     * @param portname
     * @return ResulSet
     * @throws Exception
     */
    private ResultSet SelectSqlAInputData(long ajobid, String portname) throws Exception {
        String sql = "SELECT ai.id_ajob, ai.id, ai.name, ai.prejob, ai.preoutput, ai.seq, ai.txt, ai.x, ai.y " +
                "FROM ainput as ai WHERE ai.id_ajob = ? and ai.name = ?";
        PreparedStatement preparedStatementAInputData = connection.prepareStatement(sql);
        preparedStatementAInputData.setLong(1, ajobid);
        preparedStatementAInputData.setString(2, portname);
        return preparedStatementAInputData.executeQuery();
    }

    /**
     * Visszaadja az aoutput adatait ResultSet-ben.
     *
     * @param ajobid
     * @param portname
     * @return ResulSet
     * @throws Exception
     */
    private ResultSet SelectSqlAOutputData(long ajobid, String portname) throws Exception {
        String sql = "SELECT ao.id_ajob, ao.id, ao.name, ao.seq, ao.txt, ao.x, ao.y " +
                "FROM aoutput as ao WHERE ao.id_ajob = ? and ao.name = ?";
        PreparedStatement preparedStatementAOutputData = connection.prepareStatement(sql);
        preparedStatementAOutputData.setLong(1, ajobid);
        preparedStatementAOutputData.setString(2, portname);
        return preparedStatementAOutputData.executeQuery();
    }

    /**
     * A workflow aworkflow nevet adja vissza.
     * @param workflow neve
     * @return aworkflow neve
     * @throws Exception
     */
    private String SelectSqlAWorkflowName(String portalID, String userID, String workflowname) throws Exception {
        String sql = "SELECT aw.name FROM aworkflow as aw, workflow as w WHERE " +
                "w.id_aworkflow = aw.id and aw.id_portal = ? and aw.id_user = ? and w.name = ?";
        PreparedStatement preparedStatementWorkflowName = connection.prepareStatement(sql);
        preparedStatementWorkflowName.setString(1, portalID);
        preparedStatementWorkflowName.setString(2, userID);
        preparedStatementWorkflowName.setString(3, workflowname);
        ResultSet resultSet = preparedStatementWorkflowName.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("name");
        }
        return new String("");
    }

    /**
     * A workflow adatait adja vissza.
     * @param workflow neve
     * @return aworkflow neve
     * @throws Exception
     */
    private ResultSet SelectSqlWorkflowData(String portalID, String userID, String workflowname) throws Exception {
        String sql = "SELECT w.id, w.name, w.txt FROM aworkflow as aw, workflow as w WHERE " +
                "aw.id = w.id_aworkflow and aw.id_portal = ? and aw.id_user = ? and w.name = ?";
        PreparedStatement preparedStatementWorkflowData = connection.prepareStatement(sql);
        preparedStatementWorkflowData.setString(1, portalID);
        preparedStatementWorkflowData.setString(2, userID);
        preparedStatementWorkflowData.setString(3, workflowname);
        return preparedStatementWorkflowData.executeQuery();
    }

    /**
     * A graf workflow adatait adja vissza.
     * @param workflow neve
     * @return aworkflow neve
     * @throws Exception
     */
    private ResultSet SelectSqlGrafWorkflowData(String portalID, String userID, String grafworkflowname) throws Exception {
        String sql = "SELECT id, name, txt FROM aworkflow WHERE id_portal = ? and id_user = ? and name = ?";
        PreparedStatement preparedStatementGrafWorkflowData = connection.prepareStatement(sql);
        preparedStatementGrafWorkflowData.setString(1, portalID);
        preparedStatementGrafWorkflowData.setString(2, userID);
        preparedStatementGrafWorkflowData.setString(3, grafworkflowname);
        return preparedStatementGrafWorkflowData.executeQuery();
    }

    /**
     * A graf, abstract es real workflow adatait adja vissza.
     * @param grafName - graf workflow neve
     * @param abstName - abstract workflow neve
     * @param realName - real workflow neve
     * @return ResultSet
     * @throws Exception
     */
    private ResultSet SelectSqlAllWorkflowData(String portalID, String userID, String grafName, String abstName, String realName) throws Exception {
        String sql = "SELECT id, name, txt FROM aworkflow WHERE id_portal = ? and id_user = ? and name = ? UNION ALL " +
                "SELECT w.id, w.name, w.txt FROM aworkflow as aw, workflow as w WHERE " +
                "(aw.id = w.id_aworkflow and aw.id_portal = ? and aw.id_user = ?) and " +
                "(w.name = ? or w.name = ?)";
        PreparedStatement preparedStatementAllWorkflowData = connection.prepareStatement(sql);
        preparedStatementAllWorkflowData.setString(1, portalID);
        preparedStatementAllWorkflowData.setString(2, userID);
        preparedStatementAllWorkflowData.setString(3, grafName);
        preparedStatementAllWorkflowData.setString(4, portalID);
        preparedStatementAllWorkflowData.setString(5, userID);
        preparedStatementAllWorkflowData.setString(6, abstName);
        preparedStatementAllWorkflowData.setString(7, realName);
        return preparedStatementAllWorkflowData.executeQuery();
    }

    /**
     * A real workflow abstract workflow nevet adja vissza.
     * @param abstract workflow neve
     * @return real workflow neve
     * @throws Exception
     */
    private String SelectSqlAbstWorkflowName(String portalID, String userID, String workflowname) throws Exception {
        String sql = "SELECT w.name FROM workflow as w WHERE w.id = (SELECT w.wtyp FROM aworkflow as aw, workflow as w " +
                "WHERE w.id_aworkflow = aw.id and aw.id_portal = ? and aw.id_user = ? and w.name = ?)";
        PreparedStatement preparedStatementAbstWorkflowName = connection.prepareStatement(sql);
        preparedStatementAbstWorkflowName.setString(1, portalID);
        preparedStatementAbstWorkflowName.setString(2, userID);
        preparedStatementAbstWorkflowName.setString(3, workflowname);
        ResultSet resultSet = preparedStatementAbstWorkflowName.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("name");
        }
        return new String("");
    }

    /**
     * Bejegyzeseket torol.
     *
     * (a workflow_prop tablabol)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @param name - property name
     * @throws Exception
     */
    private void deleteSqldeleteWorkflowInstancesFromWorkflowProp(String workflowID, String runtimeID, String name) throws Exception {
        String sql = "DELETE FROM workflow_prop where id_workflow = ? and wrtid = ? and name = ?";
        PreparedStatement preparedStatementDeleteInstancesFromWorkflowProp = connection.prepareStatement(sql);
        preparedStatementDeleteInstancesFromWorkflowProp.setString(1, workflowID);
        preparedStatementDeleteInstancesFromWorkflowProp.setString(2, runtimeID);
        preparedStatementDeleteInstancesFromWorkflowProp.setString(3, name);
        preparedStatementDeleteInstancesFromWorkflowProp.executeUpdate();
    }

    /**
     * A workflow (real) osszes peldanyat adja vissza.
     *
     * (a workflow_prop tablabol)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @return ResultSet - instances
     * @throws Exception
     */
    private ResultSet SelectSqlgetWorkflowInstancesFromWorkflowProp(String workflowID, String runtimeID) throws Exception {
        String sql = "SELECT wp.wrtid, wp.name, wp.value FROM workflow_prop as wp where wp.id_workflow = ? and wp.wrtid like ?";
        PreparedStatement preparedStatementInstancesFromWorkflowProp = connection.prepareStatement(sql);
        preparedStatementInstancesFromWorkflowProp.setString(1, workflowID);
        preparedStatementInstancesFromWorkflowProp.setString(2, "%" + runtimeID);
        ResultSet resultSet = preparedStatementInstancesFromWorkflowProp.executeQuery();
        return preparedStatementInstancesFromWorkflowProp.executeQuery();
    }

    /**
     * A workflow (real) osszes peldanyat adja vissza.
     *
     * (a job_status tablabol)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @return ResultSet - instances
     * @throws Exception
     */
    private ResultSet SelectSqlgetWorkflowInstancesFromJobStatus(String workflowID, String runtimeID) throws Exception {
        String sql = "SELECT js.wrtid, aj.name as jobname, js.pid, js.status, js.resource " +
                "FROM job_status as js, ajob as aj where js.id_workflow = ? and js.wrtid like ? and js.id_ajob = aj.id";
        PreparedStatement preparedStatementInstancesFromJobStatus = connection.prepareStatement(sql);
        preparedStatementInstancesFromJobStatus.setString(1, workflowID);
        preparedStatementInstancesFromJobStatus.setString(2, "%" + runtimeID);
        ResultSet resultSet = preparedStatementInstancesFromJobStatus.executeQuery();
        return preparedStatementInstancesFromJobStatus.executeQuery();
    }

    /**
     * A workflow (real) osszes peldanyat adja vissza.
     *
     * (a status tablabol)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @return ResultSet - instances
     * @throws Exception
     */
    private ResultSet SelectSqlgetWorkflowInstancesFromStatus(String workflowID, String runtimeID) throws Exception {
        String sql = "SELECT st.id_rt, aj.name as jobname, st.tim2, st.tim3, st.tim4, st.tim5, st.tim6, st.tim7 " +
                "FROM status as st, ajob as aj where st.id_workflow = ? and st.id_rt like ? and st.id_ajob = aj.id";
        PreparedStatement preparedStatementInstancesFromStatus = connection.prepareStatement(sql);
        preparedStatementInstancesFromStatus.setString(1, workflowID);
        preparedStatementInstancesFromStatus.setString(2, "%" + runtimeID);
        ResultSet resultSet = preparedStatementInstancesFromStatus.executeQuery();
        return preparedStatementInstancesFromStatus.executeQuery();
    }

    /**
     * A workflow (real) osszes peldanyat menti le.
     *
     * (a workflow_prop tablaba)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @param name - property neve
     * @param value - property ertek
     * @throws Exception
     */
    private void InsertSqlsaveWorkflowInstancesToWorkflowProp(String workflowID, String runtimeID, String name, String value) throws Exception {
        String sql = "INSERT INTO workflow_prop (id_workflow, wrtid, name, value) " +
                "VALUES (?, ?, ?, ?)"; // multi: + ", (?, ?, ?, ?)";
        PreparedStatement preparedStatementInstancesToWorkflowProp = connection.prepareStatement(sql);
        preparedStatementInstancesToWorkflowProp.setString(1, workflowID);
        preparedStatementInstancesToWorkflowProp.setString(2, runtimeID);
        preparedStatementInstancesToWorkflowProp.setString(3, name);
        preparedStatementInstancesToWorkflowProp.setString(4, value);
        preparedStatementInstancesToWorkflowProp.execute();
    }

    /**
     * A workflow (real) osszes peldanyat menti le.
     *
     * (a job_status tablaba)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @param pid - job pid azonosito
     * @param status - job status
     * @param resource - job resource
     * @param aworkflowid - aworkflow azonosito
     * @param jobname - job name
     * @throws Exception
     */
    private void InsertSqlsaveWorkflowInstancesToJobStatus(String workflowID, String runtimeID, String pid, String status, String resource, String aworkflow, String jobname) throws Exception {
        String sql = "INSERT INTO job_status (id_workflow, id_ajob, wrtid, pid, status, resource) " +
                "VALUES (?, (SELECT aj.id as id_ajob FROM ajob as aj where aj.id_aworkflow = ? and aj.name = ?), ?, ?, ?, ?)";
        PreparedStatement preparedStatementInstancesToJobStatus = connection.prepareStatement(sql);
        preparedStatementInstancesToJobStatus.setString(1, workflowID);
        preparedStatementInstancesToJobStatus.setString(2, aworkflow);
        preparedStatementInstancesToJobStatus.setString(3, jobname);
        preparedStatementInstancesToJobStatus.setString(4, runtimeID);
        preparedStatementInstancesToJobStatus.setString(5, pid);
        preparedStatementInstancesToJobStatus.setString(6, status);
        preparedStatementInstancesToJobStatus.setString(7, resource);
        preparedStatementInstancesToJobStatus.execute();
    }

    /**
     * A workflow (real) osszes peldanyat menti le.
     *
     * (a status tablaba)
     *
     * @param workflowID - workflow azonosito
     * @param runtimeID - runtime azonosito
     * @param tim2 - status 2 idopont
     * @param tim3 - status 3 idopont
     * @param tim4 - status 4 idopont
     * @param tim5 - status 5 idopont
     * @param tim6 - status 6 idopont
     * @param tim7 - status 7 idopont
     * @param aworkflowid - aworkflow azonosito
     * @param jobname - job name
     * @throws Exception
     */
    private void InsertSqlsaveWorkflowInstancesToStatus(String workflowID, String runtimeID, String tim2, String tim3, String tim4, String tim5, String tim6, String tim7, String aworkflow, String jobname) throws Exception {
        String sql = "INSERT INTO status (id_workflow, id_ajob, id_rt, tim2, tim3, tim4, tim5, tim6, tim7) " +
                "VALUES (?, (SELECT aj.id as id_ajob FROM ajob as aj where aj.id_aworkflow = ? and aj.name = ?), ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatementInstancesToStatus = connection.prepareStatement(sql);
        preparedStatementInstancesToStatus.setString(1, workflowID);
        preparedStatementInstancesToStatus.setString(2, aworkflow);
        preparedStatementInstancesToStatus.setString(3, jobname);
        preparedStatementInstancesToStatus.setString(4, runtimeID);
        preparedStatementInstancesToStatus.setString(5, tim2);
        preparedStatementInstancesToStatus.setString(6, tim3);
        preparedStatementInstancesToStatus.setString(7, tim4);
        preparedStatementInstancesToStatus.setString(8, tim5);
        preparedStatementInstancesToStatus.setString(9, tim6);
        preparedStatementInstancesToStatus.setString(10, tim7);
        preparedStatementInstancesToStatus.execute();
    }

    public void closeConnection() {
        // System.out.println("Call closeConnection() in WorkflowXMLServiceImpl... connection.close()...");
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

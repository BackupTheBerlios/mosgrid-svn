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
package hu.sztaki.lpds.pgportal.wfeditor.net.xmlrpc.server;

import hu.sztaki.lpds.wfs.service.angie.Base;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * A workflow kezeleshez szükseges adatbazis müveletek implementaciojat tartalmazza.
 *
 * @author lpds
 */
public class WEWFSServiceImpl implements WEWFSService {
    
    
    private Connection connection;
    
    private ResultSet resultSet;
    
    private String sql;
    
    private PreparedStatement preparedStatementInsertSqlWorkflow;
    
    private PreparedStatement preparedStatementUpdateSqlWorkflow;
    
    private PreparedStatement preparedStatementInsertSqlJob;
    
    private PreparedStatement preparedStatementUpdateSqlJob;
    
    private PreparedStatement preparedStatementInsertSqlInput;
    
    private PreparedStatement preparedStatementUpdateSqlInput;
    
    private PreparedStatement preparedStatementInsertSqlOutput;
    
    private PreparedStatement preparedStatementUpdateSqlOutput;
    
    private PreparedStatement preparedStatementWorkflow;
    
    private PreparedStatement preparedStatementAllJob;
    
    private PreparedStatement preparedStatementAllInput;
    
    private PreparedStatement preparedStatementAllOutput;
    
    private PreparedStatement preparedStatementGetWorkflowJobList;
    
    // private boolean isEmptyJobBatch, isEmptyInputBatch, isEmptyOutputBatch;
    
    public WEWFSServiceImpl() {
        try {
            connection = Base.getConnection();
            //
            sql = "INSERT INTO aworkflow (id_portal, id_user, name, txt) VALUES (?, ?, ?, ?)";
            preparedStatementInsertSqlWorkflow = connection.prepareStatement(sql);
            //
            sql = "UPDATE aworkflow SET id_portal = ?, id_user = ?, name = ?, txt = ? WHERE id = ?";
            preparedStatementUpdateSqlWorkflow = connection.prepareStatement(sql);
            //
            sql = "INSERT INTO ajob (id_aworkflow, name, txt, x, y) VALUES (?, ?, ?, ?, ?)";
            preparedStatementInsertSqlJob = connection.prepareStatement(sql);
            //
            sql = "UPDATE ajob SET id_aworkflow = ?, name = ?, txt = ?, x = ?, y = ? WHERE id = ?";
            preparedStatementUpdateSqlJob = connection.prepareStatement(sql);
            //
            sql = "INSERT INTO ainput (id_ajob, name, prejob, preoutput, seq, txt, x, y) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatementInsertSqlInput = connection.prepareStatement(sql);
            //
            sql = "UPDATE ainput SET name = ?, prejob = ?, preoutput = ?, seq = ?, txt = ?, x = ?, y = ? WHERE id = ? and id_ajob = ?";
            preparedStatementUpdateSqlInput = connection.prepareStatement(sql);
            //
            sql = "INSERT INTO aoutput (id_ajob, name, seq, txt, x, y) VALUES (?, ?, ?, ?, ?, ?)";
            preparedStatementInsertSqlOutput = connection.prepareStatement(sql);
            //
            sql = "UPDATE aoutput SET name = ?, seq = ?, txt = ?, x = ?, y = ? WHERE id = ? and id_ajob = ?";
            preparedStatementUpdateSqlOutput = connection.prepareStatement(sql);
            //
            sql = "SELECT id, txt FROM aworkflow WHERE id_portal = ? and id_user = ? and name = ? ORDER BY id";
            preparedStatementWorkflow = connection.prepareStatement(sql);
            //
            sql = "SELECT id, name, txt, x, y FROM ajob WHERE id_aworkflow = ? ORDER BY id";
            preparedStatementAllJob = connection.prepareStatement(sql);
            //
            sql = "SELECT i.id_ajob, i.id, i.name, i.prejob, i.preoutput, i.seq, i.txt, i.x, i.y " +
                    "FROM ajob as j, ainput as i " +
                    "WHERE j.id_aworkflow = ? and j.id = i.id_ajob " +
                    "ORDER BY i.id_ajob, i.id";
            preparedStatementAllInput = connection.prepareStatement(sql);
            //
            sql = "SELECT o.id_ajob, o.id, o.name, o.seq, o.txt, o.x, o.y " +
                    "FROM ajob as j, aoutput as o " +
                    "WHERE j.id_aworkflow = ? and j.id = o.id_ajob " +
                    "ORDER BY o.id_ajob, o.id";
            preparedStatementAllOutput = connection.prepareStatement(sql);
            //
            sql = "SELECT id FROM ajob WHERE id_aworkflow = ? ORDER BY id";
            preparedStatementGetWorkflowJobList = connection.prepareStatement(sql);
            //
            resultSet = null;
            // isEmptyJobBatch = true;
            // isEmptyInputBatch = true;
            // isEmptyOutputBatch = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Letrehoz egy aworkflow-t az adatbazisban.
     */
    public long saveAbstractWorkflow(boolean newWorkflow, long aworkflowid, String portalid, String userid, String wfname, String text) {
        long retAutoIncrementID = 0;
        if ((newWorkflow) || (aworkflowid == 0)) {
            // letrehoz uj abstract workflow (autoincrement id)
            retAutoIncrementID = InsertSqlAbstractWorkflow(portalid, userid, wfname, text);
        } else {
            // modosit regi abstract workflow
            retAutoIncrementID = UpdateSqlAbstractWorkflow(aworkflowid, portalid, userid, wfname, text);
        }
        return retAutoIncrementID;
    }
    
    /**
     * Letrehoz egy ajob-ot az adatbazisban.
     */
    public long saveAbstractJob(boolean newWorkflow, long aworkflowid, long ajobid, String wfname, String text, String x, String y) {
        long retAutoIncrementID = 0;
        if ((newWorkflow) || (ajobid == 0)) {
            // letrehoz uj abstract job (autoincrement id)
            retAutoIncrementID = InsertSqlAbstractJob(aworkflowid, wfname, text, x, y);
        } else {
            // modosit regi abstract job
            retAutoIncrementID = UpdateSqlAbstractJob(aworkflowid, ajobid, wfname, text, x, y);
        }
        return retAutoIncrementID;
    }
    
    /**
     * Letrehoz egy ainput port-ot az adatbazisban.
     */
    public long saveAbstractInput(boolean newWorkflow, long abstractJobId, long abstractPortId, String portname, String prejob, String preoutput, String portseq, String text, String x, String y) {
        long retAutoIncrementID = 0;
        if ((newWorkflow) || (abstractPortId == 0)) {
            // letrehoz uj abstract input (autoincrement id)
            retAutoIncrementID = InsertSqlAbstractInput(abstractJobId, portname, prejob, preoutput, portseq, text, x, y);
        } else {
            // modosit regi abstract input
            retAutoIncrementID = UpdateSqlAbstractInput(abstractJobId, abstractPortId, portname, prejob, preoutput, portseq, text, x, y);
        }
        return retAutoIncrementID;
    }
    
    /**
     * Letrehoz egy aoutput port-ot az adatbazisban.
     */
    public long saveAbstractOutput(boolean newWorkflow, long abstractJobId, long abstractPortId, String portname, String portseq, String text, String x, String y) {
        long retAutoIncrementID = 0;
        if ((newWorkflow) || (abstractPortId == 0)) {
            // letrehoz uj abstract output (autoincrement id)
            retAutoIncrementID = InsertSqlAbstractOutput(abstractJobId, portname, portseq, text, x, y);
        } else {
            // modosit regi abstract output
            retAutoIncrementID = UpdateSqlAbstractOutput(abstractJobId, abstractPortId, portname, portseq, text, x, y);
        }
        return retAutoIncrementID;
    }
    
    private long InsertSqlAbstractWorkflow(String portalid, String userid, String wfname, String text) {
        long retAutoIncrementID = 0;
        try {
            preparedStatementInsertSqlWorkflow.setString(1, portalid);
            preparedStatementInsertSqlWorkflow.setString(2, userid);
            preparedStatementInsertSqlWorkflow.setString(3, wfname);
            preparedStatementInsertSqlWorkflow.setString(4, text);
            preparedStatementInsertSqlWorkflow.execute();
            resultSet = preparedStatementInsertSqlWorkflow.getGeneratedKeys();
            if (resultSet.next()) {
                retAutoIncrementID = resultSet.getLong(1);
            } else {
                // throw an exception
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retAutoIncrementID;
    }
    
    private long UpdateSqlAbstractWorkflow(long aworkflowid, String portalid, String userid, String wfname, String text) {
        try {
            preparedStatementUpdateSqlWorkflow.setString(1, portalid);
            preparedStatementUpdateSqlWorkflow.setString(2, userid);
            preparedStatementUpdateSqlWorkflow.setString(3, wfname);
            preparedStatementUpdateSqlWorkflow.setString(4, text);
            preparedStatementUpdateSqlWorkflow.setLong(5, aworkflowid);
            preparedStatementUpdateSqlWorkflow.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aworkflowid;
    }
    
    private long InsertSqlAbstractJob(long aworkflowid, String wfname, String text, String x, String y) {
        long retAutoIncrementID = 0;
        try {
            preparedStatementInsertSqlJob.setLong(1, aworkflowid);
            preparedStatementInsertSqlJob.setString(2, wfname);
            preparedStatementInsertSqlJob.setString(3, text);
            preparedStatementInsertSqlJob.setString(4, x);
            preparedStatementInsertSqlJob.setString(5, y);
            preparedStatementInsertSqlJob.execute();
            resultSet = preparedStatementInsertSqlJob.getGeneratedKeys();
            if (resultSet.next()) {
                retAutoIncrementID = resultSet.getLong(1);
            } else {
                // throw an exception
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retAutoIncrementID;
    }
    
    private long UpdateSqlAbstractJob(long aworkflowid, long ajobid, String wfname, String text, String x, String y) {
        try {
            preparedStatementUpdateSqlJob.setLong(1, aworkflowid);
            preparedStatementUpdateSqlJob.setString(2, wfname);
            preparedStatementUpdateSqlJob.setString(3, text);
            preparedStatementUpdateSqlJob.setString(4, x);
            preparedStatementUpdateSqlJob.setString(5, y);
            preparedStatementUpdateSqlJob.setLong(6, ajobid);
            preparedStatementUpdateSqlJob.executeUpdate();
            // preparedStatementUpdateSqlJob.addBatch();
            // isEmptyJobBatch = false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ajobid;
    }
    
    private long InsertSqlAbstractInput(long abstractJobId, String portname, String prejob, String preoutput, String portseq, String text, String x, String y) {
        long retAutoIncrementID = 0;
        try {
            preparedStatementInsertSqlInput.setLong(1, abstractJobId);
            preparedStatementInsertSqlInput.setString(2, portname);
            preparedStatementInsertSqlInput.setString(3, prejob);
            preparedStatementInsertSqlInput.setString(4, preoutput);
            preparedStatementInsertSqlInput.setString(5, portseq);
            preparedStatementInsertSqlInput.setString(6, text);
            preparedStatementInsertSqlInput.setString(7, x);
            preparedStatementInsertSqlInput.setString(8, y);
            preparedStatementInsertSqlInput.execute();
            resultSet = preparedStatementInsertSqlInput.getGeneratedKeys();
            if (resultSet.next()) {
                retAutoIncrementID = resultSet.getLong(1);
            } else {
                // throw an exception
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retAutoIncrementID;
    }
    
    private long UpdateSqlAbstractInput(long abstractJobId, long abstractPortId, String portname, String prejob, String preoutput, String portseq, String text, String x, String y) {
        try {
            preparedStatementUpdateSqlInput.setString(1, portname);
            preparedStatementUpdateSqlInput.setString(2, prejob);
            preparedStatementUpdateSqlInput.setString(3, preoutput);
            preparedStatementUpdateSqlInput.setString(4, portseq);
            preparedStatementUpdateSqlInput.setString(5, text);
            preparedStatementUpdateSqlInput.setString(6, x);
            preparedStatementUpdateSqlInput.setString(7, y);
            preparedStatementUpdateSqlInput.setLong(8, abstractPortId);
            preparedStatementUpdateSqlInput.setLong(9, abstractJobId);
            preparedStatementUpdateSqlInput.executeUpdate();
            // preparedStatementUpdateSqlInput.addBatch();
            // isEmptyInputBatch = false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return abstractPortId;
    }
    
    private long InsertSqlAbstractOutput(long abstractJobId, String portname, String portseq, String text, String x, String y) {
        long retAutoIncrementID = 0;
        try {
            preparedStatementInsertSqlOutput.setLong(1, abstractJobId);
            preparedStatementInsertSqlOutput.setString(2, portname);
            preparedStatementInsertSqlOutput.setString(3, portseq);
            preparedStatementInsertSqlOutput.setString(4, text);
            preparedStatementInsertSqlOutput.setString(5, x);
            preparedStatementInsertSqlOutput.setString(6, y);
            preparedStatementInsertSqlOutput.execute();
            resultSet = preparedStatementInsertSqlOutput.getGeneratedKeys();
            if (resultSet.next()) {
                retAutoIncrementID = resultSet.getLong(1);
            } else {
                // throw an exception
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retAutoIncrementID;
    }
    
    private long UpdateSqlAbstractOutput(long abstractJobId, long abstractPortId, String portname, String portseq, String text, String x, String y) {
        try {
            preparedStatementUpdateSqlOutput.setString(1, portname);
            preparedStatementUpdateSqlOutput.setString(2, portseq);
            preparedStatementUpdateSqlOutput.setString(3, text);
            preparedStatementUpdateSqlOutput.setString(4, x);
            preparedStatementUpdateSqlOutput.setString(5, y);
            preparedStatementUpdateSqlOutput.setLong(6, abstractPortId);
            preparedStatementUpdateSqlOutput.setLong(7, abstractJobId);
            preparedStatementUpdateSqlOutput.executeUpdate();
            // preparedStatementUpdateSqlOutput.addBatch();
            // isEmptyOutputBatch = false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return abstractPortId;
    }
    
    /**
     * Az aworkflow adatait adja vissza ResultSet-ben.
     * @param portalid
     * @param userid
     * @param wfname
     * @return
     */
    public ResultSet getAbstractWorkflow(String portalid, String userid, String wfname) {
        return SelectSqlAbstractWorkflow(portalid, userid, wfname);
    }
    
    /**
     * Az aworkflow-ban szerplö összes job adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return
     */
    public ResultSet getAbstractJob(long aworkflowid) {
        return SelectSqlAbstractJob(aworkflowid);
    }
    
    /**
     * Az aworkflow-ban szerplö összes input port adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return
     */
    public ResultSet getAbstractInput(long aworkflowid) {
        return SelectSqlAbstractAllInput(aworkflowid);
    }
    
    /**
     * Az aworkflow-ban szerplö összes output port adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return ResultSet
     */
    public ResultSet getAbstractOutput(long aworkflowid) {
        return SelectSqlAbstractAllOutput(aworkflowid);
    }
    
    /**
     * Az aworkflow adatait adja vissza ResultSet-ben.
     * @param portalid
     * @param userid
     * @param wfname
     * @return
     */
    private ResultSet SelectSqlAbstractWorkflow(String portalid, String userid, String wfname) {
        try {
            preparedStatementWorkflow.setString(1, portalid);
            preparedStatementWorkflow.setString(2, userid);
            preparedStatementWorkflow.setString(3, wfname);
            return resultSet = preparedStatementWorkflow.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    
    /**
     * Az aworkflow-ban szerplö összes job adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return
     */
    private ResultSet SelectSqlAbstractJob(long aworkflowid) {
        try {
            preparedStatementAllJob.setLong(1, aworkflowid);
            return resultSet = preparedStatementAllJob.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    
    /**
     * Az aworkflow-ban szerplö összes input port adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return
     */
    private ResultSet SelectSqlAbstractAllInput(long aworkflowid) {
        try {
            preparedStatementAllInput.setLong(1, aworkflowid);
            return resultSet = preparedStatementAllInput.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    
    /**
     * Az aworkflow-ban szerplö összes output port adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return
     */
    private ResultSet SelectSqlAbstractAllOutput(long aworkflowid) {
        try {
            preparedStatementAllOutput.setLong(1, aworkflowid);
            return resultSet = preparedStatementAllOutput.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    
    /**
     * Az aworkflow-ban szerplö job-ok id-jat adja vissza arrayList-ben.
     * @param aworkflowid
     * @return
     */
    public ArrayList getWorkflowJobList(long aworkflowid) {
        ArrayList retArrayList = new ArrayList();
        try {
            preparedStatementGetWorkflowJobList.setLong(1, aworkflowid);
            resultSet = preparedStatementGetWorkflowJobList.executeQuery();
            while (resultSet.next()) {
                retArrayList.add(new String(resultSet.getString("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retArrayList;
    }
    
    /**
     * Az aworkflow-ban szerplö portok id-jat adja vissza arrayList-ben.
     * @param portTableName
     * @param aworkflowid
     * @return
     */
    public ArrayList getWorkflowPortList(String portTableName, long aworkflowid) {
        ArrayList retArrayList = new ArrayList();
        try {
            sql = "SELECT id FROM " + portTableName + " WHERE id_ajob in (SELECT id FROM ajob WHERE id_aworkflow = ?)  ORDER BY id";
            PreparedStatement preparedStatementGetWorkflowPortList = connection.prepareStatement(sql);
            preparedStatementGetWorkflowPortList.setLong(1, aworkflowid);
            resultSet = preparedStatementGetWorkflowPortList.executeQuery();
            while (resultSet.next()) {
                retArrayList.add(new String(resultSet.getString("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return retArrayList;
    }
    
    /*
    public void deleteJobList(ArrayList portList) {
        try {
            PreparedStatement preparedStatementDeleteJobList = null;
            sql = "DELETE FROM `ajob` WHERE id in ( ? )";
            preparedStatementDeleteJobList = connection.prepareStatement(sql);
            preparedStatementDeleteJobList.setString(1, arrayListToString(portList));
            preparedStatementDeleteJobList.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     */
    
    /**
     * Az id alapjan töröl rekordokat, a parameterben megadott tablabol.
     */
    public void deleteList(String tableName, ArrayList portList) {
        try {
            sql = "DELETE FROM " + tableName + " WHERE id in ( " + arrayListToString(portList) + " )";
            PreparedStatement preparedStatementDeleteList = connection.prepareStatement(sql);
            preparedStatementDeleteList.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * A törölni kivant id-kat arrayList-böl string-be alakitja.
     * @param arrayList
     * @return
     */
    private String arrayListToString(ArrayList arrayList) {
        String sqlStr = new String("0");
        for (int i = 0; i < arrayList.size(); i++) {
            sqlStr += ", " + ((String) arrayList.get(i));
        }
        return sqlStr;
    }
    
    public void closeConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // public void executeAllBatch() {
    //     UpdateSqlExecuteAllBatch();
    // }
    
//    private void UpdateSqlExecuteAllBatch() {
//        try {
//            if (!isEmptyJobBatch) {
//                preparedStatementUpdateSqlJob.executeBatch();
//            }
//            if (!isEmptyInputBatch) {
//                preparedStatementUpdateSqlInput.executeBatch();
//            }
//            if (!isEmptyOutputBatch) {
//                preparedStatementUpdateSqlOutput.executeBatch();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    
}

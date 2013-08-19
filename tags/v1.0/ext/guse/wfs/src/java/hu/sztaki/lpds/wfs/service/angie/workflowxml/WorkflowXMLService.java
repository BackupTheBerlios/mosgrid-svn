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

import hu.sztaki.lpds.wfs.com.ComDataBean;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Vector;

/**
 * A workflow kezeleshez szükseges adatbazis müveletek interface osztalya.
 *
 * @author lpds
 */
public interface WorkflowXMLService {
    
    /**
     * Letrehoz egy aworkflow-t az adatbazisban.
     * 
     * @param newWorkflow - true ha uj a workflow
     * @param aworkflowid - aworkflow azonosito
     * @param portalid - portal azonosito
     * @param userid - felhasznalo azonosito
     * @param wfname - workflow neve
     * @param text - workflow szoveges leiras(text)
     * @return aworkflow azonosito
     * @throws Exception
     */public abstract long saveAWorkflow(boolean newWorkflow, long aworkflowid, String portalid, String userid, String wfname, String text) throws Exception;

    /**
     * Letrehoz egy ajob-ot az adatbazisban.
     * 
     * @param newWorkflow - true ha uj a workflow
     * @param aworkflowid
     * @param ajobid
     * @param wfname
     * @param text
     * @param x
     * @param y
     * @return ajob azonosito
     * @throws Exception
     */
    public abstract long saveAJob(boolean newWorkflow, long aworkflowid, long ajobid, String wfname, String text, String x, String y) throws Exception;
    
    /**
     * Letrehoz egy ainput port-ot az adatbazisban.
     * 
     * @param newWorkflow - true ha uj a workflow
     * @param abstractJobId
     * @param abstractPortId
     * @param portname
     * @param prejob
     * @param preoutput
     * @param portseq
     * @param text
     * @param x
     * @param y
     * @return ainput azonosito
     * @throws Exception
     */
    public abstract long saveAInput(boolean newWorkflow, long abstractJobId, long abstractPortId, String portname, String prejob, String preoutput, String portseq, String text, String x, String y) throws Exception;
    
    /**
     * Letrehoz egy aoutput port-ot az adatbazisban.
     * 
     * @param newWorkflow - true ha uj a workflow
     * @param abstractJobId
     * @param abstractPortId
     * @param portname
     * @param portseq
     * @param text
     * @param x
     * @param y
     * @return aoutput azonosito
     * @throws Exception
     */
    public abstract long saveAOutput(boolean newWorkflow, long abstractJobId, long abstractPortId, String portname, String portseq, String text, String x, String y) throws Exception;
    
    /**
     * Az aworkflow adatait adja vissza ResultSet-ben.
     * 
     * @param portalid - portal azonosito
     * @param userid - felhasznalo azonosito
     * @param wfname - workflow azonosito
     * @return ResultSet
     * @throws Exception
     */
    public abstract ResultSet getAWorkflow(String portalid, String userid, String wfname) throws Exception;
    
    /**
     * Az aworkflow-ban szereplö összes job adatait adja vissza ResultSet-ben.
     * 
     * @param aworkflowid
     * @return ResultSet
     * @throws Exception
     */
    public abstract ResultSet getAJob(long aworkflowid) throws Exception;
    
    /**
     * Az aworkflow-ban szerplö összes input port adatait adja vissza ResultSet-ben.
     * 
     * @param aworkflowid - aworkflow azonosito
     * @return ResultSet
     * @throws Exception
     */
    public abstract ResultSet getAInput(long aworkflowid) throws Exception;
    
    /**
     * Az aworkflow-ban szerplö összes output port adatait adja vissza ResultSet-ben.
     * 
     * @param aworkflowid - aworkflow azonosito
     * @return ResultSet
     * @throws Exception
     */
    public abstract ResultSet getAOutput(long aworkflowid) throws Exception;

    /**
     * Visszaadja az ajob adatait ResultSet-ben.
     *
     * @param aworkflowid
     * @param jobname
     * @return ResulSet
     * @throws Exception
     */    
    public abstract ResultSet getAJobData(long aworkflowid, String jobname) throws Exception;

    /**
     * Visszaadja az ainput adatait ResultSet-ben.
     *
     * @param ajobid
     * @param portname
     * @return ResulSet
     * @throws Exception
     */    
    public abstract ResultSet getAInputData(long ajobid, String portname) throws Exception;
    
    /**
     * Visszaadja az aoutput adatait ResultSet-ben.
     *
     * @param ajobid
     * @param portname
     * @return ResulSet
     * @throws Exception
     */
    public abstract ResultSet getAOutputData(long ajobid, String portname) throws Exception;
   
    /**
     * A workflow aworkflow nevet adja vissza.
     * 
     * @param portalID - portal azonosito
     * @param userID - felhasznalo azonosito
     * @param workflowname - workflow neve
     * @return aworkflow neve
     * @throws Exception
     */
    public abstract String getAWorkflowName(String portalID, String userID, String workflowname) throws Exception;
    
    /**
     * A workflow adatait adja vissza.
     *
     * (abstract vagy real (konkret) workflow adatait)
     *
     * @param portalID - portal azonosito
     * @param userID - felhasznalo azonosito
     * @param workflowname - workflow neve
     * @return ResultSet
     * @throws Exception
     */
    public abstract ResultSet getWorkflowData(String portalID, String userID, String workflowname) throws Exception;
    
    /**
     * A graf workflow adatait adja vissza.
     * 
     * @param portalID - portal azonosito
     * @param userID - felhasznalo azonosito
     * @param grafworkflowname - graf workflow neve
     * @return ResultSet
     * @throws Exception
     */
    public abstract ResultSet getGrafWorkflowData(String portalID, String userID, String grafworkflowname) throws Exception;
    
    /**
     * A graf, abstract es real workflow adatait adja vissza.
     * 
     * @param portalID - portal azonosito
     * @param userID - felhasznalo azonosito
     * @param grafName - graf workflow neve
     * @param abstName - abstract workflow neve
     * @param realName - real workflow neve
     * @return ResultSet
     * @throws Exception
     */
    public abstract ResultSet getAllWorkflowData(String portalID, String userID, String grafName, String abstName, String realName) throws Exception;
    
    /**
     * A real workflow abstract workflow nevet adja vissza.
     * 
     * @param portalID - portal azonosito
     * @param userID - felhasznalo azonosito
     * @param workflowname - abstarct workflow neve
     * @return real workflow neve
     * @throws Exception
     */
    public abstract String getAbstractWorkflowName(String portalID, String userID, String workflowname) throws Exception;

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
    public abstract void deleteWorkflowInstancesFromWorkflowProp(String workflowID, String runtimeID, String name) throws Exception;

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
    public abstract ResultSet getWorkflowInstancesFromWorkflowProp(String workflowID, String runtimeID) throws Exception;

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
    public abstract ResultSet getWorkflowInstancesFromJobStatus(String workflowID, String runtimeID) throws Exception;

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
    public abstract ResultSet getWorkflowInstancesFromStatus(String workflowID, String runtimeID) throws Exception;

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
    public abstract void saveWorkflowInstancesToWorkflowProp(String workflowID, String runtimeID, String name, String value) throws Exception;

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
    public abstract void saveWorkflowInstancesToJobStatus(String workflowID, String runtimeID, String pid, String status, String resource, String aworkflow, String jobname) throws Exception;

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
    public abstract void saveWorkflowInstancesToStatus(String workflowID, String runtimeID, String tim2, String tim3, String tim4, String tim5, String tim6, String tim7, String aworkflow, String jobname) throws Exception;
    
    /**
     * Hasznalat utan mindig meg kell hivni...
     * Itt zarodik be a connection
     * amit hasznalat az osztaly...
     */
    public abstract void closeConnection();    
    
}

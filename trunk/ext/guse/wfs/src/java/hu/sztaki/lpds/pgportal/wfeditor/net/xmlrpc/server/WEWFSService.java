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

import java.sql.ResultSet;

/**
 * A workflow kezeleshez szükseges adatbazis müveletek interface osztalya.
 * @author lpds
 */
public interface WEWFSService {
    
    /**
     * Letrehoz egy aworkflow-t az adatbazisban.
     */
    public abstract long saveAbstractWorkflow(boolean newWorkflow, long aworkflowid, String portalid, String userid, String wfname, String text);
    
    /**
     * Letrehoz egy ajob-ot az adatbazisban.
     */
    public abstract long saveAbstractJob(boolean newWorkflow, long aworkflowid, long ajobid, String wfname, String text, String x, String y);
    
    /**
     * Letrehoz egy ainput port-ot az adatbazisban.
     */
    public abstract long saveAbstractInput(boolean newWorkflow, long abstractJobId, long abstractPortId, String portname, String prejob, String preoutput, String portseq, String text, String x, String y);
    
    /**
     * Letrehoz egy aoutput port-ot az adatbazisban.
     */
    public abstract long saveAbstractOutput(boolean newWorkflow, long abstractJobId, long abstractPortId, String portname, String portseq, String text, String x, String y);
    
    /**
     * Az aworkflow adatait adja vissza ResultSet-ben.
     * @param portalid
     * @param userid
     * @param wfname
     * @return
     */
    public abstract ResultSet getAbstractWorkflow(String portalid, String userid, String wfname);
    
    /**
     * Az aworkflow-ban szerplö összes job adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return
     */
    public abstract ResultSet getAbstractJob(long aworkflowid);
    
    /**
     * Az aworkflow-ban szerplö összes input port adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return
     */
    public abstract ResultSet getAbstractInput(long aworkflowid);
    
    /**
     * Az aworkflow-ban szerplö összes output port adatait adja vissza ResultSet-ben.
     * @param aworkflowid
     * @return ResultSet
     */
    public abstract ResultSet getAbstractOutput(long aworkflowid);
    
    /**
     * Hasznalat utan mindig meg kell hivni...
     * Itt zarodik be a connection
     * amit hasznalat az osztaly...
     */
    public abstract void closeConnection();
    
}

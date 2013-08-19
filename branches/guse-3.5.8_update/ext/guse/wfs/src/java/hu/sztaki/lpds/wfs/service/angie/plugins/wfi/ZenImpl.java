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
/**
 * Zen(gUSE default) workflowfeldolgozoknak szolgaltathato leiro
 */

package hu.sztaki.lpds.wfs.service.angie.plugins.wfi;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;
import hu.sztaki.lpds.wfs.com.WorkflowConfigErrorBean;
import hu.sztaki.lpds.wfs.net.wsaxis13.WfsPortalServiceImpl;
import hu.sztaki.lpds.wfs.service.angie.Base;
import hu.sztaki.lpds.wfs.service.angie.plugins.wfi.inf.WorkflowDescriptionGenerator;
import hu.sztaki.lpds.wfs.service.angie.utils.WFSUtils;
import hu.sztaki.lpds.wfs.validator.ConfigureChecker;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * @author krisztian
 */
public class ZenImpl implements WorkflowDescriptionGenerator
{

    
    public ZenImpl() {}
    
     
    @Override
    public String getWFIWorkflowDescription(ComDataBean pData)
    {
        try
        {            
            if(pData.getTxt()!=null)
            {
                if(!pData.getTxt().equals("null"))
                {
                    try
                    {
//uj workflow peldanyhoz tartozo workflow id lekerdezese                    
                        Connection conn=Base.getConnection();
                        PreparedStatement ps=conn.prepareStatement("SELECT workflow.id FROM workflow,aworkflow WHERE id_portal=? and id_user=? and id_aworkflow=aworkflow.id and workflow.name=? ");
                        ps.setString(1,pData.getPortalID());
                        ps.setString(2,pData.getUserID());
                        ps.setString(3,pData.getWorkflowID());
                        ResultSet rs=ps.executeQuery();
                        if (rs.next()) {
                            long wfid = rs.getLong(1);
                            //
//workflow peldany adatok mentedse a db-be
                            // de elotte a regi adatok torlese, pl: resume eseten
                            // ne legyen tobb azonos runtimeid hoz tartozo bejegyzes
                            ps=conn.prepareStatement("DELETE FROM workflow_prop WHERE id_workflow=? and wrtid=?");
                            ps.setLong(1, wfid);
                            ps.setString(2, pData.getWorkflowRuntimeID());
                            ps.executeUpdate();
//workflow peldany mentedse a db-be
                            ps=conn.prepareStatement("INSERT INTO workflow_prop VALUES(?,?,?,?)");
                            ps.setLong(1,wfid);
                            ps.setString(2,pData.getWorkflowRuntimeID());
                            ps.setString(3,"storage");
                            ps.setString(4,pData.getStorageURL());
                            ps.executeUpdate();

                            ps=conn.prepareStatement("INSERT INTO workflow_prop VALUES(?,?,?,?)");
                            ps.setLong(1,wfid);
                            ps.setString(2,pData.getWorkflowRuntimeID());
                            ps.setString(3,"text");
                            ps.setString(4,pData.getTxt());
                            ps.executeUpdate();

                            ps=conn.prepareStatement("INSERT INTO workflow_prop VALUES(?,?,?,?)");
                            ps.setLong(1,wfid);
                            ps.setString(2,pData.getWorkflowRuntimeID());
                            ps.setString(3,"wfiurl");
                            ps.setString(4,pData.getWfiURL());
                            ps.executeUpdate();

                            ps=conn.prepareStatement("INSERT INTO workflow_prop VALUES(?,?,?,?)");
                            ps.setLong(1,wfid);
                            ps.setString(2,pData.getWorkflowRuntimeID());
                            ps.setString(3,"status");
                            ps.setString(4,"5");
                            ps.executeUpdate();
                        }
                        conn.close();
                    }
                    catch(SQLException e){e.printStackTrace();}
                }
            }
            StringBuffer res=new StringBuffer("<workflow>\n");
            JobPropertyBean tJob=new JobPropertyBean();
            PortDataBean tPort=new PortDataBean();
//workflow config lekerdezese
            WfsPortalServiceImpl wfsPortalServiceImpl = new WfsPortalServiceImpl();
            Vector wfconfig = wfsPortalServiceImpl.getWorkflowConfigData(pData);
            // System.out.println("wfs ZenImpl wfconfig vector size : " + wfconfig.size());
//xml osszepakolasa
            for(int i=0;i<wfconfig.size();i++)
            {
                tJob=(JobPropertyBean)wfconfig.get(i);
                if(tJob.getExe().get("iworkflow")==null)
                    res.append("\t<job name=\""+tJob.getName()+"\">\n");
                else
                    res.append("\t<job name=\""+tJob.getName()+"\" workflow=\""+tJob.getExe().get("iworkflow")+"\">\n");
                for(int j=0;j<tJob.getInputs().size();j++)
                {
                    tPort=(PortDataBean)tJob.getInputs().get(j);
                      
                    if(tPort.getPrejob().equals(""))
                    {
                        if(tPort.getData().get("iinput")==null)      
                            res.append("\t\t<input name=\""+tPort.getName()+"\" seq=\""+tPort.getSeq()+"\" enough=\"finished\" waiting=\"one\" count=\""+((tPort.getData().get("max")==null)?"1":tPort.getData().get("max"))+"\"  dpid=\""+tPort.getData().get("dpid")+"\"  internal=\""+tPort.getData().get("intname")+"\" />\n");
                        else
                        {
                            String[] ss=((String)tPort.getData().get("iinput")).split("/");
                            res.append("\t\t<input name=\""+tPort.getName()+"\" seq=\""+tPort.getSeq()+"\" iinput=\""+ss[1]+"\" ijob=\""+ss[0]+"\" enough=\"finished\" waiting=\"one\" count=\""+((tPort.getData().get("max")==null)?"1":tPort.getData().get("max"))+"\"  dpid=\""+tPort.getData().get("dpid")+"\"  internal=\""+tPort.getData().get("intname")+"\" />\n");
                        }
                        
                    }
                    else
                    {
                        int k=0, l=0;;
                        for(k=0;!((JobPropertyBean)wfconfig.get(k)).getName().equals(tPort.getPrejob());k++);
                        try{for(l=0;((PortDataBean)((JobPropertyBean)wfconfig.get(k)).getOutputs().get(l)).getSeq()!=Long.parseLong(tPort.getPreoutput());l++);}
                        catch(ArrayIndexOutOfBoundsException e0){l--;}
                        String output_name=((PortDataBean)((JobPropertyBean)wfconfig.get(k)).getOutputs().get(l)).getName();
                    
                        if(tPort.getData().get("iinput")==null)      
                            res.append("\t\t<input name=\""+tPort.getName()+"\" seq=\""+tPort.getSeq()+"\"  prejob=\""+tPort.getPrejob()+"\" preoutput=\""+output_name+"\"  dpid=\""+tPort.getData().get("dpid")+"\" waiting=\""+tPort.getData().get("waiting")+"\" internal=\""+tPort.getData().get("intname")+"\" />\n");
                        else
                        {
                            String[] ss=((String)tPort.getData().get("iinput")).split("/");
                            res.append("\t\t<input name=\""+tPort.getName()+"\" seq=\""+tPort.getSeq()+"\" iinput=\""+ss[1]+"\" ijob=\""+ss[0]+"\" prejob=\""+tPort.getPrejob()+"\" preoutput=\""+output_name+"\"  dpid=\""+tPort.getData().get("dpid")+"\" waiting=\""+tPort.getData().get("waiting")+"\" internal=\""+tPort.getData().get("intname")+"\" />\n");
                        }
                    }
                }
                for(int j=0;j<tJob.getOutputs().size();j++)
                {
                    tPort=(PortDataBean)tJob.getOutputs().get(j);
                
                    if(tPort.getData().get("ioutput")==null)      
                        res.append("\t\t<output name=\""+tPort.getName()+"\" maincount=\""+tPort.getData().get("maincount")+"\" />\n");
                    else
                    {
                        String[] ss=((String)tPort.getData().get("ioutput")).split("/");
                        res.append("\t\t<output name=\""+tPort.getName()+"\" ioutput=\""+ss[1]+"\" ijob=\""+ss[0]+"\" maincount=\""+tPort.getData().get("maincount")+"\" />\n");
                    }
                }
            
                res.append("\t</job>\n");
            }
            //
//long start = System.currentTimeMillis();
//System.out.println("wfs ZenImpl create rescue xml begin " + start);
            //
//workflow config hibak osszegyujtese
            res.append("\t<config>\n");
            Vector<WorkflowConfigErrorBean> confError = wfsPortalServiceImpl.getWorkflowConfigDataError(pData);
            // (ez lathato a portal feluleten is az info gomb megnyomasa utan)
            for (int ePos = 0; ePos < confError.size(); ePos++) {
                WorkflowConfigErrorBean eBean = confError.get(ePos);
                String errorID = eBean.getErrorID();
                //
                if ("iworkflow".equals(errorID)) {
                    // beagyazott workflow van ebben a workflow-ban...
                    String embedWorkflowName = eBean.getPortID();
                    boolean isValid = WFSUtils.getInstance().isValidEmbedWorkflow(pData, embedWorkflowName);
                    // System.out.println("isValidEmbedWorkflowName(" + embedWorkflowName + ") = " + isValid);
                    if (!isValid) {
                        // ha ebben a pillanatban nem letezik a beagyazott workflow pl:
                        // kitoroltek, akkor jelezzuk a konfiguracios hibat az xml ben...
                        res.append("\t\t<error job=\""+eBean.getJobName()+"\" port=\""+eBean.getPortID()+"\" error=\""+eBean.getErrorID()+"\" />\n");
                    }
                }
                if ((!"certtype".equals(errorID)) && (!"iworkflow".equals(errorID))) {
                    // cert adatokat, hibakat nem teszunk bele az xml be...
                    res.append("\t\t<error job=\""+eBean.getJobName()+"\" port=\""+eBean.getPortID()+"\" error=\""+eBean.getErrorID()+"\" />\n");
                }
            }
            res.append("\t</config>\n");
            //
//rescue
            if (pData.getWorkflowRuntimeID() != null)
            {
                // a res be csak az elso megtalalt pid hez tartozo jobstatus
                // tag et teszunk bele, a wfi oldalon csak azt jelezzuk ezzel
                // hogy ez a workflow leiro rescue adatokat is tartalmaz es
                // ezeket az adatokat egy tovabbi keressel kell kerdezni...
                String sql="SELECT b.name,a.status,a.pid,b.id FROM job_status as a,ajob as b WHERE a.wrtid='"+pData.getWorkflowRuntimeID()+"' and a.status=6 and a.id_ajob=b.id LIMIT 1";
                try
                {
                    Connection conn=Base.getConnection();
                    Statement stmt1s = conn.createStatement();
                    ResultSet rst1s =stmt1s.executeQuery(sql);
//                    ResultSet rsr;
//                    Connection conn0=Base.getConnection();
//                    PreparedStatement psr=conn0.prepareStatement("SELECT output,cnt FROM job_outputs WHERE wrtid=? AND id_ajob=? AND pid=?");
//                    psr.setString(1, pData.getWorkflowRuntimeID());
                    res.append("\t<statuses>\n");
                    while(rst1s.next())
                    {
                        res.append("\t\t<jobstatus job='"+rst1s.getString(1)+"' pid='"+rst1s.getString(3)+"' status='"+rst1s.getString(2)+"' />\n");
//                        psr.setLong(2, rst1s.getLong(4));
//                        psr.setLong(3, rst1s.getLong(3));
//                        rsr=psr.executeQuery();
//                        while(rsr.next())
//                            res.append("\t\t\t<joboutput port=\""+rsr.getString(1)+"\" count=\""+rsr.getString(2)+"\" />\n");
//                        res.append("\t\t</jobstatus>\n");
                    }
                    res.append("\t</statuses>\n");
                    conn.close();
//                    conn0.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //
//long stop = System.currentTimeMillis();
//System.out.println("wfs ZenImpl create rescue xml end " + stop);
//System.out.println("wfs ZenImpl create rescue xml end " + (stop - start) + " msec...");
            //
            res.append("</workflow>\n");
//System.out.println("wfs ZenImpl create workflow xml : " + res.toString());
            return res.toString();
        }
        catch(Exception e){e.printStackTrace(); return "<workflow>\n</workflow>\n";}
    }

    @Override
    public String getWFIWorkflowRescueDescription(ComDataBean pData, String index)
    {
        try
        {
            StringBuffer res = new StringBuffer("<workflow>\n");
            //
//long start = System.currentTimeMillis();
//System.out.println("wfs ZenImpl getWFIWorkflowRescueDescription create rescue xml begin " + start);
            //
//rescue
            if(pData.getWorkflowRuntimeID()!=null)
            {
                long delta = 100;
                try {
                    delta = Integer.parseInt(PropertyLoader.getInstance().getProperty("guse.wfs.rescue.collection.size"));
                } catch (Exception e) {
                    delta = 100;
                }
                long lindex = 0;
                try {
                    lindex = Long.parseLong(index);
                } catch (Exception e) {
                    lindex = 0;
                }
                //
                long limitFrom = lindex * delta;
//System.out.println("wfs ZenImpl getWFIWorkflowRescueDescription index   : " + index);
//System.out.println("wfs ZenImpl getWFIWorkflowRescueDescription lindex  : " + lindex);
//System.out.println("wfs ZenImpl getWFIWorkflowRescueDescription limitFrom : " + limitFrom);
                //
                String sql="SELECT b.name,a.status,a.pid,b.id FROM job_status as a,ajob as b WHERE a.wrtid='"+pData.getWorkflowRuntimeID()+"' and a.status=6 and a.id_ajob=b.id and b.name='"+pData.getJobID()+"' LIMIT "+limitFrom+", "+delta;
//System.out.println("wfs ZenImpl getWFIWorkflowRescueDescription sql     : " + sql);
                try
                {
                    Connection conn=Base.getConnection();
                    Statement stmt1s = conn.createStatement();
                    ResultSet rst1s =stmt1s.executeQuery(sql);
                    ResultSet rsr;
                    Connection conn0=Base.getConnection();
                    PreparedStatement psr=conn0.prepareStatement("SELECT output,cnt FROM job_outputs WHERE wrtid=? AND id_ajob=? AND pid=?");
                    psr.setString(1, pData.getWorkflowRuntimeID());
                    res.append("\t<statuses>\n");
                    while(rst1s.next())
                    {
                        res.append("\t\t<jobstatus job='"+rst1s.getString(1)+"' pid='"+rst1s.getString(3)+"' status='"+rst1s.getString(2)+"' >\n");
                        psr.setLong(2, rst1s.getLong(4));
                        psr.setLong(3, rst1s.getLong(3));
                        rsr=psr.executeQuery();
                        while(rsr.next())
                            res.append("\t\t\t<joboutput port=\""+rsr.getString(1)+"\" count=\""+rsr.getString(2)+"\" />\n");
                        res.append("\t\t</jobstatus>\n");
                    }
                    res.append("\t</statuses>\n");
                    conn.close();
                    conn0.close();
                }
                catch(Exception e){e.printStackTrace();}
            }
            //
//long stop = System.currentTimeMillis();
//System.out.println("wfs ZenImpl getWFIWorkflowRescueDescription create rescue xml end " + stop);
//System.out.println("wfs ZenImpl getWFIWorkflowRescueDescription create rescue xml end " + (stop - start) + " msec...");
            //
            res.append("</workflow>\n");
            return res.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "<workflow>\n</workflow>\n";
        }
    }

    @Override
    public String getStorageWorkflowDescription(ComDataBean pData) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public void workflowConfigDataErrorCheck(String workflowIDStr, ComDataBean value, Vector pData)
    {

        try {
            long workflowID = ((Long) Long.parseLong(workflowIDStr)).longValue();
            // grid VO list
            Hashtable gridHash = new Hashtable();
            // prejob preoutput memory table
            // (preoutput kigyujtes, az output ellenorzeshez kell)
            Hashtable preJobInputMemory = new Hashtable();
            // create preout memory table
            for (int jobPos = 0; jobPos < pData.size(); jobPos++) {
                JobPropertyBean job = (JobPropertyBean) pData.get(jobPos);
                String memJobName = job.getName();
                // parse inputList
                Vector inputsList = job.getInputs();
                for (int portPos = 0; portPos < inputsList.size(); portPos++) 
                {
                    PortDataBean port = (PortDataBean) inputsList.get(portPos);
                    String memPortSeq = String.valueOf(port.getSeq());
                    String putMemKey = port.getPrejob() + "#" + port.getPreoutput();
                    String putMemValue = memJobName + "#" + memPortSeq;
                    // System.out.println("putMemKey  : " + putMemKey);
                    // System.out.println("putMemValue: " + putMemValue);
                    if (!"#".equals(putMemKey)) {preJobInputMemory.put(putMemKey, putMemValue);}
                }
            }
            // System.out.println("pre input memory:" + preJobInputMemory);
            // parse jobList (pData)
            for (int jobPos = 0; jobPos < pData.size(); jobPos++) {
                JobPropertyBean job = (JobPropertyBean) pData.get(jobPos);
                String jobName = job.getName();
                String portID = new String("*");
                boolean executeJob = false;
                boolean workflowJob = false;
                boolean serviceJob = false;
                boolean metabroker = false;
                boolean cloud = false;
                // parse job
                if (job.getExe().get("iworkflow") != null) {
                    workflowJob = true;
                }
                if (job.getExe().get("servicetype") != null) {
                    serviceJob = true;
                }
                if (job.getExe().get("gridtype") != null) {
                    executeJob = true;
                }
                if (job.getExe().get("cloudtype") != null) {
                    metabroker = true;
                }
                if (job.getExe().get("mbt") != null) {
                    metabroker = true;
                }
                if ((!workflowJob) && (!serviceJob) && (!executeJob) && !metabroker) {
                    WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.job.mainnoconfig");
                }
                // System.out.println("job booleans : " + executeJob + ", " + workflowJob + ", " + serviceJob);
                // configured job
                if (workflowJob) {
                        List<String> errors=ConfigureChecker.getI("workflow").getJobErrors(job.getExe());
                        for(String t:errors)
                            WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, t);
                }
                if (serviceJob) {
                        List<String> errors=ConfigureChecker.getI("webservice").getJobErrors(job.getExe());
                        for(String t:errors)
                            WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, t);
                }
                if (executeJob)
                {
                    String middleware=(String) job.getExe().get("gridtype");

                    List<String> errors=ConfigureChecker.getI(middleware).getJobErrors(job.getExe());
                        for(String t:errors)
                            WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, t);
                }



                // parse inputList
                Vector inputsList = job.getInputs();
                for (int portPos = 0; portPos < inputsList.size(); portPos++) {
                    PortDataBean port = (PortDataBean) inputsList.get(portPos);
                    portID = String.valueOf(port.getSeq());
                    boolean filePort = false;
                    boolean remotePort = false;
                    boolean valuePort = false;
                    boolean sqlPort = false;
                    boolean chanelPort = false;
                    boolean propPort=false;
                    boolean embedPort=false;
                    // create helper prejob flag
                    boolean preInfo = false;
                    if ((port.getPrejob() != null) && (port.getPreoutput() != null)) {
                        if ((!"".equals(port.getPrejob())) && ((!"".equals(port.getPreoutput())))) {
                            preInfo = true;
                        }
                    }
                    // create helper sql flag
                    boolean sqlInfo = false;
                    // System.out.println("portname: " + port.getName() + " port.getData: " + port.getData());
                    if ((port.getData().containsKey("sqlurl")) || (port.getData().containsKey("sqlselect")) || (port.getData().containsKey("sqluser")) || (port.getData().containsKey("sqlpass"))) {
                        sqlInfo = true;
                    }
                    // parse input port
                    if ((port.getData().containsKey("file")) && (!port.getData().containsKey("remote")) && (!port.getData().containsKey("value")) && (!sqlInfo) && (!preInfo)) {
                        filePort = true;
                    }
                    if ((!port.getData().containsKey("file")) && (port.getData().containsKey("remote")) && (!port.getData().containsKey("value")) && (!sqlInfo) && (!preInfo)) {
                        remotePort = true;
                    }
                    if ((!port.getData().containsKey("file")) && (!port.getData().containsKey("remote")) && (port.getData().containsKey("value")) && (!sqlInfo) && (!preInfo)) {
                        valuePort = true;
                    }
                    if (port.getData().containsKey("cprop")) {
                        propPort = true;
                    }
                    if ((!port.getData().containsKey("file")) && (!port.getData().containsKey("remote")) && (!port.getData().containsKey("value")) && (sqlInfo) && (!preInfo)) {
                        sqlPort = true;
                    }
                    if ((!port.getData().containsKey("file")) && (!port.getData().containsKey("remote")) && (!port.getData().containsKey("value")) && (!sqlInfo) && (preInfo)) {
                        chanelPort = true;
                    }
                    if (port.getData().containsKey("iinput")) {
                        embedPort = true;
                    }
                    // System.out.println("flags: "+filePort+remotePort+valuePort+sqlPort+chanelPort+embedPort);
                    // System.out.println("port name : " + port.getName() + ", port.getData() : " + port.getData() + ", embedPort : " + embedPort);
                    if ((!filePort) && (!remotePort) && (!valuePort) && (!sqlPort) && (!chanelPort) && (!propPort) && (!embedPort)) {
                        WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.noconfig");
                    }
                    // System.out.println("port.getData() : " + port.getData());
                    if ((filePort) || (remotePort) || (valuePort) || (sqlPort)) {
                        if (port.getData().containsKey("intname")) {
                            if ("".equals(((String) port.getData().get("intname")).trim())) {WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.intname");}
                        }
                    }
                    if (executeJob) {
                        String gridtype = (String) job.getExe().get("gridtype");
                        // System.out.println("gridtype = " + gridtype);
                        // System.out.println("intname  = " + "" + port.getData().get("intname"));
                        if (gridtype.equalsIgnoreCase("gemlca")) {
                            if (!port.getData().containsKey("intname")) {
                                WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.intname-A");
                            } else {
                                if ("".equals(((String) port.getData().get("intname")).trim())) {
                                    WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.intname-B");
                                } else {//if param not contains the intname
                                    String params = "" + job.getExe().get("params");
                                    System.out.println("ZenImpl job.params=" + params);
                                    if (!params.contains(((String) port.getData().get("intname"))) ){
                                        WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.gemlcaintname");
                                        System.out.println("IN not contains");
                                    } else if ( port.getData().get("gout") == null ){
                                        WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.gemlcaintname");
                                        System.out.println(" -GOUT- input NOT contains - null");
                                    } else {
                                        System.out.println(" input OK");
                                    }
                                }
                            }
                        }
                    }
                    // configured input port
                    if (filePort) {
                        if (!port.getData().containsKey("file")) {WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.file");}
                    }
                    if (remotePort) {
                        if (!port.getData().containsKey("remote")) {WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.remote");}
                    }
                    if (valuePort) {
                        if (!port.getData().containsKey("value")) {WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.value");}
                    }
                    if (propPort) {
                        if (!port.getData().containsKey("cprop")) {WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.cprop");}
                    }
                    if (sqlPort) {
                        if (!port.getData().containsKey("sqlurl")) {WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.sqlurl");}
                        if (!port.getData().containsKey("sqlselect")) {WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.sqlselect");}
                        if (!port.getData().containsKey("sqluser")) {WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.sqluser");}
                        if (!port.getData().containsKey("sqlpass")) {WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.inputport.sqlpass");}
                    }
                    if (chanelPort) {
                        // nem kell semmit ellenorizni...
                    }
                } // parse inputList
                // parse outputList
                Vector outputsList = job.getOutputs();
                for (int portPos = 0; portPos < outputsList.size(); portPos++) {
                    PortDataBean port = (PortDataBean) outputsList.get(portPos);
                    portID = String.valueOf(port.getSeq());
                    String getMemKey = new String(jobName + "#" + portID);
                    if (workflowJob) {
                        if (preJobInputMemory.containsKey(getMemKey)) {
                            if (!port.getData().containsKey("ioutput")) {WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.outputport.ioutput");}
                        }
                    }
                    if (executeJob) {
                        String gridtype = (String) job.getExe().get("gridtype");
                        if (gridtype.equalsIgnoreCase("gemlca")) {
                            if (!port.getData().containsKey("intname")) {
                                WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.outputport.intname-A");
                            } else {
                                if ("".equals(((String) port.getData().get("intname")).trim())) {
                                    WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.outputport.intname-B");
                                } else {//if param not contains the intname
                                    String params = "" + job.getExe().get("params");
                                    System.out.println("ZenImpl job.params=" + params);
                                    if (!params.contains(((String) port.getData().get("intname"))) ){
                                        WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.outputport.gemlcaintname");
                                        System.out.println("OUT not contains");
                                    } else if ( port.getData().get("gout") == null ){
                                        WFSUtils.getInstance().saveWorkflowConfigDataError(workflowID, jobName, portID, "error.outputport.gemlcaintname");
                                        System.out.println(" -GOUT- output NOT contains - null");
                                    } else {
                                        System.out.println(" output OK");
                                    }
                                }
                            }
                        }
                    }
                } // parse outputList
            } // parse jobList (pData)
            // parse grid vo list
            if (!gridHash.isEmpty()) {
                Enumeration enumVOList = gridHash.keys();
                while (enumVOList.hasMoreElements()) {
                    String key = "" + enumVOList.nextElement();
                    WorkflowConfigErrorBean eBean = (WorkflowConfigErrorBean) gridHash.get(key);
                    // save gridVO to error_prop table
                    WFSUtils.getInstance().saveWorkflowConfigDataError(eBean.getWorkflowID(), eBean.getJobName(), eBean.getPortID(), eBean.getErrorID());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

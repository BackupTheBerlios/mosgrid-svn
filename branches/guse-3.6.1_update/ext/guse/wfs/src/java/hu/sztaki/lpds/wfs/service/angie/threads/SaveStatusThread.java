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
/*
 *  Kapott statuszok mentes adatbazisba
 */

package hu.sztaki.lpds.wfs.service.angie.threads;

import java.util.*;
//////import hu.sztaki.lpds.wfs.service.angie.Base;
//////import java.sql.*;
//////import javax.naming.*;
//////import javax.sql.*;
//////
//////import hu.sztaki.lpds.wfs.com.JobStatusBean;
//////import hu.sztaki.lpds.information.local.PropertyLoader;

                        // Nincs hasznalatban !!!
                        // Nincs hasznalatban !!!
                        // Nincs hasznalatban !!!

public class SaveStatusThread extends Thread
{
//////    private Vector data;
//////    private Context ctx;
//////    private DataSource ds;
//////    private boolean saveproperty;
//////    private boolean vizualizerenabled;

    public SaveStatusThread(Vector pData)
    {
//////        data=pData;
//////        init();
    }
//////
//////
//////    public SaveStatusThread(JobStatusBean pData)
//////    {
//////        data=new Vector();
//////        data.add(pData);
//////        init();
//////    }
//////
//////    private void init()
//////    {
//////        saveproperty="true".equals(PropertyLoader.getInstance().getProperty("guse.wfs.system.saveonlyendstatus"));
//////        vizualizerenabled="true".equals(PropertyLoader.getInstance().getProperty("guse.wfs.system.savevisualizerdata"));
//////        try
//////        {
//////            ctx = new InitialContext();
//////            ds = (DataSource)ctx.lookup("java:comp/env/jdbc/WFSpool");
//////        }
//////        catch(NamingException e) {e.printStackTrace();}
//////        start();
//////    }
//////
//////    @Override
//////    public void run()
//////    {
//////        JobStatusBean tmp;
//////        for(int i=0;i<data.size();i++)
//////        {
//////            tmp=(JobStatusBean)data.get(i);
//////            if(saveproperty)
//////            {
//////                if((tmp.getStatus()==6)||(tmp.getStatus()==7)||(tmp.getStatus()==15)) saveStatus(tmp);
//////            }
//////            else saveStatus(tmp);
//////        }
//////    }
//////
//////    private synchronized void saveStatus(JobStatusBean pData)
//////    {
//////        boolean workflowstatussave;
//////        try
//////        {
//////            String sql;
//////            Connection conn= ds.getConnection();
//////            Statement stmt = conn.createStatement();
//////            String jobID="";
//////            String workflowID="";
//////            ResultSet rst;
//////            try
//////            {
//////                workflowID=Base.getI().getWorkflowIdFromJobIdCache(pData.getPortalID(),pData.getUserID(),pData.getWorkflowID());
//////                jobID=Base.getI().getJobIdFromJobIdCache(pData.getPortalID(),pData.getUserID(),pData.getWorkflowID(),pData.getJobID());
//////            }
//////            catch(NullPointerException e)
//////            {
//////                sql="SELECT a.id,b.id FROM ajob as a, workflow as b, aworkflow as c WHERE c.id_portal='"+pData.getPortalID()+"' and c.id_user='"+pData.getUserID()+"' and c.id=b.id_aworkflow and b.name='"+pData.getWorkflowID()+"' and a.id_aworkflow=c.id and a.name='"+pData.getJobID()+"'";
//////                rst =stmt.executeQuery(sql);
//////                while(rst.next())
//////                {
//////                    jobID=rst.getString(1);
//////                    workflowID=rst.getString(2);
//////                    Base.getI().addJobIdToJobIdCache(pData.getPortalID(),pData.getUserID(),pData.getWorkflowID(),pData.getJobID(), jobID);
//////                    Base.getI().addWorkflowIdToJobIdCache(pData.getPortalID(),pData.getUserID(),pData.getWorkflowID(), workflowID);
//////                }
//////
//////            }
//////
//////            stmt = conn.createStatement();
//////            if(Base.getI().setLastModify(pData.getWrtID()+pData.getPID()+pData.getJobID(), pData.getTim()))
//////            {
//////                sql="UPDATE job_status SET status='"+pData.getStatus()+"', resource='"+pData.getResource()+"' WHERE id_workflow="+workflowID+" and id_ajob="+jobID+" and wrtid='"+pData.getWrtID()+"' and pid="+pData.getPID();
////////                System.out.println(">>>>>"+sql);
//////                if(stmt.executeUpdate(sql)==0)
//////                {
//////                    sql="INSERT INTO job_status VALUES("+workflowID+","+jobID+",'"+pData.getWrtID()+"','"+pData.getPID()+"','"+pData.getStatus()+"','"+pData.getResource()+"')";
////////                    System.out.println(">>>>>"+sql);
//////                    stmt.executeUpdate(sql);
//////                }
//////                    if(pData.getStatus()==6)
//////                    {
//////System.out.println("wfs insert into job_outputs...1...: " + Long.parseLong(jobID) + ", " + pData.getPID());
//////                        PreparedStatement pso=conn.prepareStatement("INSERT INTO job_outputs VALUES(?,?,?,?,?)");
//////                        pso.setString(1, pData.getWrtID());
//////                        pso.setLong(2, Long.parseLong(jobID));
//////                        pso.setLong(3, pData.getPID());
//////                        Enumeration enmo=pData.getOutputs().keys();
//////                        System.out.println("WFS:"+pData.getWrtID()+"/"+jobID+"."+pData.getPID()+"=>"+pData.getOutputs().size());
//////                        Object keyo;
//////                        while(enmo.hasMoreElements())
//////                        {
//////                            keyo=enmo.nextElement();
//////                            pso.setString(4, ""+keyo);
//////                            pso.setLong(5, pData.getOutputCount(""+keyo));
//////                            pso.executeUpdate();
//////                        }
//////                        pso.close();
//////                    }
//////
//////                workflowstatussave=true;
//////                if(saveproperty)
//////                {
//////                    if(pData.getWorkflowStatus().equals("6") || pData.getWorkflowStatus().equals("7") || pData.getWorkflowStatus().equals("15"))
//////                        workflowstatussave=true;
//////                    else workflowstatussave=false;
//////                }
//////                if(workflowstatussave)
//////                {
//////                    sql="UPDATE workflow_prop SET value='"+pData.getWorkflowStatus()+"' WHERE id_workflow="+workflowID+" and wrtid='"+pData.getWrtID()+"' and name='status'";
//////                    stmt.executeUpdate(sql);
//////                    if(stmt.executeUpdate(sql)==0)
//////                    {
//////                        sql="INSERT INTO workflow_prop VALUES("+workflowID+",'"+pData.getWrtID()+"','status','"+pData.getStatus()+"')";
//////                        stmt.executeUpdate(sql);
//////                    }
//////                    // if(pData.getStatus()==6)
//////                    // abort eseten is kell torolni kulonben
//////                    // tobbszor lesz benne output bejegyzes
//////                    if((pData.getStatus()==6)||(pData.getStatus()==15))
//////                    {
//////                        PreparedStatement pso=conn.prepareStatement("DELETE FROM job_outputs WHERE wrtid=?");
//////                        pso.setString(1, pData.getWrtID());
//////                        pso.executeUpdate();
//////                        pso.close();
//////                    }
//////                    if((pData.getStatus()==6)||(pData.getStatus()==7)||(pData.getStatus()==15))
//////                    {
//////                        Base.getI().delete(pData.getPortalID()+pData.getUserID()+pData.getWorkflowID()+pData.getWrtID());
//////                        Base.getI().deleteJobDescriptionFromCache(pData.getPortalID(),pData.getUserID(),pData.getWorkflowID(),pData.getWrtID());
//////                    }
//////                }
//////
//////            }
//////            if(vizualizerenabled)
//////            {
//////// parametric status handling
//////                String actstatus = String.valueOf(pData.getStatus() % 10);
//////                sql="SELECT tim" + actstatus + " FROM status WHERE id_workflow=" + workflowID + " and id_rt='" + pData.getWrtID() + "' and id_ajob=" + jobID;
//////                rst = stmt.executeQuery(sql);
//////                String timx = new String("0");
//////                while(rst.next()) {timx = rst.getString(1);}
//////                if("0".equals(timx))
//////                {
//////                    sql="UPDATE status SET tim"+actstatus+"="+pData.getTim()+" WHERE id_workflow="+workflowID+" and id_rt='"+pData.getWrtID()+"' and id_ajob="+jobID;
//////                    if(stmt.executeUpdate(sql)==0)
//////                    {
//////                        sql="INSERT INTO status(id_workflow,id_rt,id_ajob,tim"+actstatus+") VALUES("+workflowID+",'"+pData.getWrtID()+"',"+jobID+","+pData.getTim()+")";
//////                        stmt.executeUpdate(sql);
//////                    }
//////                }
//////            }
//////            conn.close();
//////        }
//////        catch(Exception e){e.printStackTrace();}
//////
//////    }
//////
////// protected void finalize() throws Throwable
//////    {
//////        // conn.close();
//////        // conn=null;
//////        ds=null;
//////        ctx=null;
//////        System.gc();
//////    }
}

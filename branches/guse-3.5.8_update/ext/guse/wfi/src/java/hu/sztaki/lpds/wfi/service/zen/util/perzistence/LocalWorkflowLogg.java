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
 *Lokalis file-ba loggolja a workflow vegrehajtasi fazisokat.
 */

package hu.sztaki.lpds.wfi.service.zen.util.perzistence;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.submitter.com.JobIOBean;
import hu.sztaki.lpds.submitter.com.JobRuntime;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.wfi.service.zen.Base;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Job;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstance;
//import hu.sztaki.lpds.wfi.zen.pools.WorkHandler;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author krisztian karoczkai
 */
public class LocalWorkflowLogg 
{
    private static LocalWorkflowLogg instance=new LocalWorkflowLogg();
    boolean loggEnabel = false;
    String path;
    // private File fl;
    Hashtable<String,FileWriter> flwlist=new Hashtable<String,FileWriter>();
    

    /** Creates a new instance of LocalWorkflowLogg */
    public LocalWorkflowLogg() 
    {
        if (PropertyLoader.getInstance().getProperty("wfi.zen.loggfile") != null) {
            path=(PropertyLoader.getInstance().getProperty("prefix.dir") + PropertyLoader.getInstance().getProperty("wfi.zen.loggfile"));
            // if(!((new File(path)).exists())) fl.mkdirs();
            if(!((new File(path)).exists())) {new File(path).mkdirs();}
            loggEnabel = true;
        }
    }
    
    public static LocalWorkflowLogg getI(){return instance;}
    
    public void writeSubmitColl(String pZenID,String pTHid, ServiceType st,int max,int act) throws IOException
    {
        if (loggEnabel) {
        long memory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        FileWriter flw=getFileWriter(pZenID);
        flw.write("<submited  instance=\""+pZenID+"\" submitter=\""+st.getServiceUrl()+"\" max=\""+max+"\" actual=\""+act+"\" thid=\""+pTHid+"\" time=\""+Calendar.getInstance().getTime()+"\" utc=\""+System.currentTimeMillis()+"\" memory=\""+memory+"\"/>\n");
        flw.flush();
        }
    }

    public void writeSubmit(JobRuntime tmpl) throws IOException
    {
        if (loggEnabel) {
            if (Base.getZenRunner(tmpl.getWorkflowRuntimeID()) != null ) {
                long memory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
//                Base.getI().writeLogg(tmpl);
                FileWriter flw=getFileWriter(tmpl);
                flw.write("<submit time=\""+System.currentTimeMillis()+"."+System.nanoTime()+"\" instance=\""+tmpl.getWorkflowRuntimeID()+"\" job=\""+tmpl.getJobID()+"\" pid=\""+tmpl.getPID()+"\" memory=\""+memory+"\">\n");
                Job job=Base.getZenRunner(tmpl.getWorkflowRuntimeID()).getJob(tmpl.getJobID());
                Hashtable inputs=tmpl.getInputsCount();
//                Hashtable inputs=WorkHandler.getI().getInstancePool().getPSInstance(tmpl.getWorkflowRuntimeID(),tmpl.getJobID(),tmpl.getPID()).getAllInput();
                Enumeration enm=inputs.keys();
                String key;
                PSInstance psi;
                int pid=0;
                while(enm.hasMoreElements())
                {
                    key=(String)enm.nextElement();
                    JobIOBean jb=(JobIOBean)inputs.get(key);
                    if(jb.getRuntimeID()==null)
                        flw.write("\t<input name=\""+key+"\"  file=\""+jb.getSeq()+"/"+jb.getPID()+"\" />\n");
                    else
                        flw.write("\t<input name=\""+key+"\"  file=\""+jb.getJobID()+"/"+jb.getPID()+"/"+jb.getName()+"_"+jb.getIndex()+"\" collector=\""+jb.getCollectorNumber()+"\" />\n");
//                    flw.write("\t<nativ>"+jb.toString()+"</nativ>\n");
                }

                flw.write("</submit>\n");

                flw.flush();
            }
        }
    } 
    
    public void writeStatus(JobStatusBean tmpl) throws IOException
    {
        if (loggEnabel) {
        long memory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
//        Base.getI().writeLogg(tmpl);
        FileWriter flw=getFileWriter(tmpl.getWrtID());
        if(tmpl.getOutputs()!=null)
        {
            flw.write("<status time=\""+System.currentTimeMillis()+"."+System.nanoTime()+"\" instance=\""+tmpl.getWrtID()+"\" job=\""+tmpl.getJobID()+"\" pid=\""+tmpl.getPID()+"\" status=\""+tmpl.getStatus()+"\" memory=\""+memory+"\" />\n");
            Enumeration<String> enm=tmpl.getOutputs().keys();
            String key;
            while(enm.hasMoreElements())
            {
                key=enm.nextElement();
                flw.write("\t<output name=\""+key+"\" count=\""+tmpl.getOutputCount(key)+"\"  />\n");
            }
            flw.write("</status>\n");            
        }
        else
            flw.write("<status time=\""+System.currentTimeMillis()+"."+System.nanoTime()+"\" instance=\""+tmpl.getWrtID()+"\" job=\""+tmpl.getJobID()+"\" pid=\""+tmpl.getPID()+"\" status=\""+tmpl.getStatus()+"\" memory=\""+memory+"\" />\n");
        flw.flush();
        }
    }

    public void writeRescue(String pZenID,String pJobID, long pPID) throws IOException
    {
        if (loggEnabel) {
        long memory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
//        Base.getI().writeLogg(tmpl);
        FileWriter flw=getFileWriter(pZenID);
        flw.write("<rescue time=\""+System.currentTimeMillis()+"."+System.nanoTime()+"\" instance=\""+pZenID+"\" job=\""+pJobID+"\" pid=\""+pPID+"\" status=\"6\" memory=\""+memory+"\" />\n");
        flw.flush();
        }
    }

    private FileWriter getFileWriter(JobRuntime tmpl) throws IOException
    {
        FileWriter fw;
        if(flwlist.get(tmpl.getWorkflowRuntimeID())==null)
        {
            fw=getFileWriter(tmpl.getWorkflowRuntimeID());
            initWorkFlow(tmpl.getWorkflowRuntimeID(), tmpl.getWorkflowSubmitID());
        }
        else fw=getFileWriter(tmpl.getWorkflowRuntimeID());
        return  fw;
    }
    
    private FileWriter getFileWriter(String pZenID) throws IOException
    {

        if(flwlist.get(pZenID)==null)
        {
            File f=new File(path+"/"+pZenID+".logg");
            if(f.exists())
                flwlist.put(pZenID, new FileWriter(f,true));
            else
                flwlist.put(pZenID, new FileWriter(f));
        }
        return flwlist.get(pZenID);
    }

    public void initWorkFlow(String pZenID, long pSubmitID) throws IOException
    {
        if (loggEnabel) {
        long memory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        FileWriter fw=getFileWriter(pZenID);
        WorkflowRuntimeBean wrb=Base.getZenRunner(pZenID).getWorkflowData();
            fw.write("<gusezenlogg time=\""+Calendar.getInstance().getTime()+"\">\n");
            fw.write("<workflowdata>\n");
            fw.write("\t<param name=\"portalid\">"+wrb.getPortalID()+"</param>\n");
            fw.write("\t<param name=\"userid\">"+wrb.getUserID()+"</param>\n");
            fw.write("\t<param name=\"workflowid\">"+wrb.getWorkflowID()+"</param>\n");
            fw.write("\t<param name=\"runtimeid\">"+pZenID+"</param>\n");
            fw.write("\t<param name=\"submitid\">"+String.valueOf(pSubmitID)+"</param>\n");
            fw.write("\t<param name=\"instancetext\">"+wrb.getInstanceText()+"</param>\n");
            fw.write("\t<param name=\"utc\">"+(new Date(System.currentTimeMillis()))+"</param>\n");
            fw.write("</workflowdata>\n");        
            fw.flush();
        }
    }
    
    public void writeLogg(String pZenID,String pLoggValue, int pTab)
    {
        if (loggEnabel) 
        {
            try
            {
                FileWriter fw=getFileWriter(pZenID);
                for(int i=0;i<pTab;i++)fw.write("\t");
                fw.flush();
                long memory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
                fw.write("<"+pLoggValue +" time=\""+Calendar.getInstance().getTime()+"\" utc=\""+System.currentTimeMillis()+"\" memory=\""+memory+"\"/>\n");
                fw.flush();
            }
            catch(Exception e){/*log nem irhato*/}
        }
    }
    
    public void writeCleanStart(String pZenID,String pJob, long pPid) throws IOException
    {
        if (loggEnabel) {
        FileWriter fw=getFileWriter(pZenID);
        long memory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        fw.write("<cleaning job=\""+pJob+"\" pid=\""+pPid+"\"  time=\""+Calendar.getInstance().getTime()+"\" utc=\""+System.currentTimeMillis()+"\" memory=\""+memory+"\">\n");
        fw.flush();
        }
    }
    
    public void writeGC(String pZenID) throws IOException
    {
        if (loggEnabel) {
        FileWriter fw=getFileWriter(pZenID);
        long memory0=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        long memory1=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        fw.write("<runninggc >\n");
        fw.flush();
        }
    }

    public void writeLoggALL(String pLoggValue) throws IOException
    {
        if (loggEnabel) {
        Enumeration<String> enm=flwlist.keys();
        while(enm.hasMoreElements())
        {
        FileWriter fw=getFileWriter(enm.nextElement());
        long memory=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        fw.write("<"+pLoggValue +"time=\""+Calendar.getInstance().getTime()+"\" utc=\""+System.currentTimeMillis()+"\" memory=\""+memory+"\"/>\n");
        fw.flush();
        }
        }
    }

    public void closeLoggFile(String pZenID)throws IOException
    {
        if (loggEnabel) {
        if(flwlist.get(pZenID)!=null)
        {
            flwlist.get(pZenID).write("<endtime time=\""+Calendar.getInstance().getTime()+"\" status=\""+Base.getZenRunner(pZenID).getWorkflowStatus()+"\"/>\n");
            flwlist.get(pZenID).write("</gusezenlogg>\n");
            flwlist.get(pZenID).close();
            flwlist.remove(pZenID);
        }
        }
    }

    @Override
    protected void finalize() throws Throwable 
    {
        Enumeration<String> enm=flwlist.keys();
        while(enm.hasMoreElements())closeLoggFile(enm.nextElement());
    }
    
}

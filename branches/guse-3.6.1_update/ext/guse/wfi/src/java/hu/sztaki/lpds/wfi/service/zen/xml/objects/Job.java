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
 * jobs.java
 * Job leiro
 */

package hu.sztaki.lpds.wfi.service.zen.xml.objects;

import hu.sztaki.lpds.logging.Logger;
import hu.sztaki.lpds.wfi.util.JobConfig;
import hu.sztaki.lpds.wfi.zen.pools.JobInstanceReferenceBean;
import hu.sztaki.lpds.wfi.zen.pools.WorkHandler;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.ws.wsaddressing.W3CEndpointReference;

public class Job{
    private JobConfig jc;
    private int status=0;
    private String name="";
    private Hashtable inputs=new Hashtable();
    private Hashtable outputs=new Hashtable();
    private String parentID=null;
    private long maxinputcount=0;
    private Hashtable<String,Long> dpid=new Hashtable<String,Long>();
    private Hashtable<String,Vector<Input>> dpidGroups=new Hashtable<String,Vector<Input>>();
    private Vector<Job> postJobs=new Vector<Job>();
    private Vector<Job> preJobs=new Vector<Job>();
 
    private ConcurrentHashMap<Long,JobInstanceReferenceBean> instances=new ConcurrentHashMap<Long,JobInstanceReferenceBean>();

    public ConcurrentHashMap<Long, JobInstanceReferenceBean> getInstances() {return instances;}

    public void setInstances(ConcurrentHashMap<Long, JobInstanceReferenceBean> instances) {this.instances = instances;}




/**
 * Class constructor
 * @param pName Job neve
 */    
    public Job(String pZenID,String pName) 
    {
        name=pName;
        WorkHandler.getI().getInstancePool().createJob(pZenID, name);
    }

    public JobConfig getJc() {return jc;}

    public void setJc(JobConfig jc) {this.jc = jc;}



/**
 * Class constructor
 * @param pName Job neve
 * @param pParent Szulo workflowID
 */    
    public Job(String pZenID,String pName,String pParent) 
    {
        name=pName;
        parentID=pParent;
        WorkHandler.getI().getInstancePool().createJob(pZenID, name);
    }    
/**
 * Jobhoz post job felvitele
 * @param pPortName
 * @param pJob
 */    
    public void addPostJob(String pPortName,Job pJob)
    {
        ((Output)outputs.get(pPortName)).addPostJob(pJob);
        boolean b=false;
        for(int i=0;(i<postJobs.size())&&(!b);i++) 
            b=postJobs.get(i).equals(pJob);
        if(!b){postJobs.add(pJob);}
    }
/**
 * Jobhoz pre job felvitele
 * @param pPortName
 * @param pJob
 */    
    public void addPreJob(Job pJob)
    {
        boolean b=false;
        for(int i=0;(i<preJobs.size())&&(!b);i++) 
            b=preJobs.get(i).equals(pJob);
        if(!b) preJobs.add(pJob);
    }
    
/**
 * A job output(post) csatornfuggosegeinek lekerdezese
 * @return
 */    
    public Vector<Job> getPostJobs() {return postJobs;}
/**
 * A job output(post) csatornfuggosegeinek lekerdezese
 * @return
 */    
    public Vector<Job> getPreJobs() {return preJobs;}
    

/**
 * Szulo lekerdezese
 * @return Szulo azonositoja
 */    
    public String getParent(){return parentID;}
/**
 * Job nevenek lekerdezese
 * @return Job neve
 */    
    public String getName(){return name;}
/**
 * Uj input Jobhoz adasa
 * @param pValue Input leiro object
 */    
    public void addInput(Input pValue) {
        inputs.put(pValue.getName(), pValue);
    }
/**
 * Uj output Jobhoz adasa
 * @param pValue Output leiro object
 */    
    public void addOutput(Output pValue)
    {
        if (parentID!=null) {
            pValue.setAttribute("maincount", "2");
        }
        outputs.put(pValue.getName(),pValue);
    }
/**
 * Output lekerdezese
 * @return Output leiro object
 */    
    public Output getOutput(String pValue){return (Output)outputs.get(pValue);}
/**
 * Intput lekerdezese
 * @return Intput leiro object
 */    
    public Input getInput(String pValue){return (Input)inputs.get(pValue);}

/**
 * Job peldanyok szamanak lekerdezese
 * @return Job peldanyok szama
 */    
    public int getCount(){return (int)maxinputcount;}
    
/**
 * Futtatatando jobpeldanyok meghatarozasa
 */    
   public void jobRunningCount(String pZenID)
   {
//ha nincs inputja a jobnak
        if(inputs.size()==0)
        {
            maxinputcount=1;
            return;
        }
//parametrikus csomortokban levo inputok szamanak meghatarozasa
        Enumeration enm=inputs.elements();
        Input tmpInput;
        long tmpInputCount=0;
        while(enm.hasMoreElements())
        {
            tmpInput=(Input)enm.nextElement();
            if(tmpInput.getCount()==0) return;
            if("all".equals(tmpInput.getWaiting())) tmpInputCount=1;
            else tmpInputCount=tmpInput.getCount();
            if(dpid.get(tmpInput.getDPID())==null)
            {
                dpid.put(tmpInput.getDPID(),tmpInputCount);
                dpidGroups.put(tmpInput.getDPID(),new Vector<Input>());
                dpidGroups.get(tmpInput.getDPID()).add(tmpInput);
            }
            else
            {
                dpid.put(tmpInput.getDPID(),dpid.get(tmpInput.getDPID()).longValue()*tmpInputCount);
                Vector<Input> inputsInPID=dpidGroups.get(tmpInput.getDPID());
                long aseq=Long.parseLong(tmpInput.getSeq());
                if(Long.parseLong(inputsInPID.get(0).getSeq())>aseq)
                    inputsInPID.add(0,tmpInput);
                else
                {
                    boolean newElement=false;
                    for(int i=0;(i<inputsInPID.size()-1)&&(!newElement);i++)
                    {
                        long b0seq=Long.parseLong(inputsInPID.get(i).getSeq());
                        long b1seq=Long.parseLong(inputsInPID.get(i+1).getSeq());
                        if((b0seq<aseq)&&(b1seq>aseq))
                        {
                            newElement=true;
                            inputsInPID.add(i+1,tmpInput);
                        }
                    }
                    if(!newElement) inputsInPID.add(tmpInput);
                }
                dpidGroups.put(tmpInput.getDPID(), inputsInPID);
            }
        }
// loggs
        Enumeration<String> enmLog=inputs.keys();
        String logg,dpididx;
        while(enmLog.hasMoreElements()){
            dpididx=enmLog.nextElement();
            logg="info-inputport job=\""+getName()+"\" group=\""+dpididx+"\" value=\""+((Input)inputs.get(dpididx)).getCount()+"\" ";
            Logger.getI().workflow(pZenID,Logger.INFO,logg);
        }

        enmLog=dpid.keys();
        while(enmLog.hasMoreElements()){
            dpididx=enmLog.nextElement();
            logg="info-inputgroup job=\""+getName()+"\" group=\""+dpididx+"\" value=\""+dpid.get(dpididx).longValue()+"\" ";
            Logger.getI().workflow(pZenID,Logger.INFO,logg);
        }

//maximalis szamu DPID csoport megkeresese        
        enm=dpid.elements();
        long max=0,tmpmax;
        while(enm.hasMoreElements())
        {
            tmpmax=((Long)enm.nextElement()).longValue();
            if(max<tmpmax)max=tmpmax;
        }
//futtatando job peldany beallitasa        
        maxinputcount=max;
//elore kiszamolhato outputok szamossaganak meghatarozasa        
        enm=outputs.elements();
        Output tmpOutput;
        while(enm.hasMoreElements())
        {
            tmpOutput=(Output)enm.nextElement();
            if((tmpOutput.getMainCount()==1)) tmpOutput.setAttribute("realCount", ""+maxinputcount);
            else tmpOutput.setAttribute("realCount", "0");
        }
        
   }
/**
 * Nem collector tipusu Input port index meghatarozasa Job peldanyhoz 
 * @param pInputName input port neve
 * @param pJobPID job PID
 * @return input index(ennyiedik sorszamu input file kell ebbol az inputbol ehhez a jobhoz)
 */   
   public long getInputIndexForPID(String pInputName, long pJobPID)
   {
        long res=-2;
        Input baseInput=getInput(pInputName);
        String tmpDPID=baseInput.getDPID();
        long realindex=(pJobPID%dpid.get(tmpDPID).longValue());
        Vector<Input> ilist=dpidGroups.get(tmpDPID);
        int i=0;
        for(i=0;i<ilist.size();i++)
        {
            if(baseInput.equals(ilist.get(i)))
            {
                if(i==(ilist.size()-1))
                {
                    res=realindex%baseInput.getCount();
                }
                else
                {
                    long all=1;
                    for(int j=i+1;j<ilist.size();j++) all*=ilist.get(j).getCount();
                    res=(realindex/all)%baseInput.getCount();
                }
            }
        }
        return res;
   }
/**
 * Input indexhez tartozo JOB PIDek meghatarozasa
 * @param pInputName input port neve
 * @param pIndex file sorszama az input streamben
 * @return azon PID-ek listaja amelyek pInputName-ben pIndex-et tarlamaznak
 */    
   public Vector<Long> getPidsForIndex(String pInputName, long pIndex)
   {
        Input tmp=getInput(pInputName);
        Vector<Long> res=new Vector<Long>();
        
        Vector<Input> inputsInGroup=dpidGroups.get(tmp.getDPID());
        boolean last=inputsInGroup.get(inputsInGroup.size()-1).equals(tmp);
        if(last)
        {
            for(long i=pIndex;i<getCount();i+=tmp.getCount())
            {
                res.add(new Long(i));
            }
        }
        else
        {
            long nextStreamsCount=0;
            for(int i=0;i<inputsInGroup.size();i++)
            {
                if(inputsInGroup.get(i).equals(tmp)) nextStreamsCount=1;
                else nextStreamsCount*=inputsInGroup.get(i).getCount();
            }
            long pid;
            for(int i=0;i<(getCount()/(tmp.getCount()*nextStreamsCount))+1;i++)
                for(int j=0; j<nextStreamsCount;j++)
                {
                    pid=pIndex*nextStreamsCount+j+(i*tmp.getCount()*nextStreamsCount);
                    if(pid<getCount()) res.add(new Long(pid));
                }

        }
        return res;
   }
   
    public long getMaxInputCount(){return maxinputcount;}
    
/**
 * Postjobhoz input portok meghatarozasa
 * @param pJob post job neve
 * @return azon inputok listaja amik kepcsolodik  pJob-hoz
 */    
    public Vector<String> getInputsForJob(String pJob)
    {
        Vector<String> res= new Vector<String>();
        Enumeration<String> enm=inputs.keys();
        Input tmp;
        while(enm.hasMoreElements())
        {
            tmp=(Input)inputs.get(enm.nextElement());
            if(pJob.equals(tmp.getPreJob())) res.add(tmp.getName());
        }
        return res;
    }
    
/**
 * Output port generatorsaganak lekerdezese
 * @param pOutput port neve
 * @return true=a port generator output
 */
    
    public boolean outputIsGenerator(String pOutput)
    {
        return ((Output)outputs.get(pOutput)).getMainCount()>1;
    }
    
/**
 * Job statuszanak lekerdezese
 * @return Job Status
 */    
    public int getJobStatus(){return status;}

/**
 * Job statuszanak beallitasa
 * @param pValue workflow jobjai Vector<JOB>
 */    
    public void setJobStatus(Hashtable pJobs)
    {

    }

    
/**
 * Job status lekerdezese
 * @return jobstatus
 */ 
    public int getStatus(){return status;}
    
/**
 * Osszes output lekerdezese
 * @return Output hash(Output)
 */ 
    public Hashtable getOutputs(){return outputs;}
/**
 * Osszes intput lekerdezese
 * @return Intput hash(Intput)
 */ 
    public Hashtable getInputs(){return inputs;}
    
    
/**
 * Kiirja a szerver konzolra a parametrikus Job p_ld_nyok st_tusz_t
 */    
    public void printStatus()
    {
        System.out.println("*"+name+":"+status);
    }    
    
}
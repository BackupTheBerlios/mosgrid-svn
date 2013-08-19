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
 * Memoria alapu bejovo status kezeles
 */

package hu.sztaki.lpds.wfi.zen.pools;

import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.logging.Logger;
import hu.sztaki.lpds.storage.com.FileBean;
import hu.sztaki.lpds.storage.inf.WFIStorageClient;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.wfi.service.zen.Base;
import hu.sztaki.lpds.wfi.service.zen.Runner;
import hu.sztaki.lpds.wfs.utils.Status;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Job;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Output;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstance;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstanceOutput;
import hu.sztaki.lpds.wfi.zen.pools.inf.IncomingStatusPool;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author krisztian karoczkai
 */
public class RAMIncomingStatusPollImpl extends Thread implements IncomingStatusPool {

    private BlockingQueue<JobStatusBean> pool=new LinkedBlockingQueue<JobStatusBean>();

    public RAMIncomingStatusPollImpl() {
        setName("RAMIncomingStatusPollHandler:"+System.currentTimeMillis());
        start();
    }

    public void setNoRuning(JobStatusBean pStatus){
        Job tmp=Base.getZenRunner(pStatus.getWrtID()).getJob(pStatus.getJobID());
        Logger.getI().workflow(pStatus.getWrtID(),Logger.INFO,"notfinish job=\"" + pStatus.getJobID() + "\" pid=\"" + pStatus.getPID() + "\" status=\"" + pStatus.getStatus() + "\"");
        JobStatusBean tj;
        Enumeration<String> enmiKeys;
        String inputKey;
        Vector<Long> inputpids;
        for(Job t:tmp.getPostJobs()){
            if(t.getCount()>0){ //be van initalva
                enmiKeys=t.getInputs().keys();
                while(enmiKeys.hasMoreElements()){
                    inputKey=enmiKeys.nextElement();
                    if(t.getInput(inputKey).getPreJob()!=null)
                    if(t.getInput(inputKey).getPreJob().equals(tmp.getName()))
                    if("one".equals(t.getInput(inputKey).getWaiting())){
                        inputpids=t.getPidsForIndex(inputKey, pStatus.getPID());
                        for(int i=0;i<inputpids.size();i++){
                            if(WorkHandler.getI().getInstancePool().getPSInstance(pStatus.getWrtID(), t.getName(), inputpids.get(i).longValue())==null){
                                Logger.getI().workflow(pStatus.getWrtID(),Logger.INFO,"propagated job=\"" + t.getName() + "\" pid=\"" + (inputpids.get(i).longValue()) + "\" ");
                                tj=new JobStatusBean();
                                tj.setWorkflowID(pStatus.getWorkflowID());
                                tj.setWrtID(pStatus.getWrtID());
                                tj.setWorkflowSubmitID(pStatus.getWorkflowSubmitID());
                                tj.setStatus(Status.NOTRUNNABLE);
                                tj.setTim(System.nanoTime());
                                tj.setPID(inputpids.get(i).longValue());
                                tj.setJobID(t.getName());
                                tj.setOutputs(new Hashtable());
                                tj.setResource("gUSE-wfi");
                                tj.setWorkflowSubmitID(pStatus.getWorkflowSubmitID());
                                WorkHandler.getI().getIncomingStatusPool().addNewStatusInformation(tj);
                                Logger.getI().workflow(pStatus.getWrtID(),Logger.INFO,"notrunning job=\"" + tj.getJobID() + "\" pid=\"" + tj.getPID() + "\" status=\"" + tj.getStatus() + "\"");
                                try{
                                    PSInstance ins=new PSInstanceOutput(t.getName(), inputpids.get(i).longValue());
                                    ins.setStatus(Status.NOTRUNNABLE);
                                    WorkHandler.getI().getInstancePool().addInstanceJobPool(pStatus.getWrtID(), t.getName(), inputpids.get(i).longValue(),ins);}
                                catch(Exception e){e.printStackTrace();/*peldany meg nem hozhato letre*/}
                            } //if WorkHandler
                            else
                                Logger.getI().workflow(pStatus.getWrtID(),Logger.INFO,"no-propagated job=\"" + t.getName() + "\" pid=\"" + (inputpids.get(i).longValue()) + "\" ");
                        }// for
                    }// if "one"
                }// while
            }//if t.getCount
        }//for
    }

    public synchronized void addNewStatusInformation(JobStatusBean pValue){
        try{
            Base.getZenRunner(pValue.getWrtID()).incWorkingstatus();
            pool.put(pValue);
            Logger.getI().pool("incoming-status","add workflow=\""+pValue.getWrtID()+"\" job=\"" + pValue.getJobID() + "\" pid=\"" + pValue.getPID() + "\" status=\"" + pValue.getStatus() + "\" resource=\"" + pValue.getResource() + "\"",pool.size());
        }
        catch(Exception e){System.out.println("--"+pValue.getWrtID());e.printStackTrace();/*wf nem letezik*/}
    }

    public synchronized void addNewStatusInformations(Vector<JobStatusBean> pValue)
    {
        for (JobStatusBean p:pValue) 
            addNewStatusInformation(p);
    }

    public long getPoolSize() {return pool.size();}

    @Override
    public void run(){
        Vector<JobStatusBean> sendingpool; //kimeno statuszok
        JobStatusBean tmp = null; // temporari status info

        while(Base.getI().applicationrun){
                sendingpool = new Vector<JobStatusBean>();
//statuszok feldolgozas
                try{
                    tmp = pool.take();
                    Base.getZenRunner(tmp.getWrtID()).decWorkingstatus();
                    tmp=manageStatus(tmp);
                    Logger.getI().pool("incoming-status","add workflow=\""+tmp.getWrtID()+"\" submitID=\""+tmp.getWorkflowSubmitID()+"\" job=\"" + tmp.getJobID() + "\" pid=\"" + tmp.getPID() + "\" status=\""+tmp.getStatus()+"\"", sendingpool.size());
                    if (!"rescue".equals(tmp.getResource()))
                        sendingpool.add(tmp);
// tovabbkuldendo sztatuszok tovabbkuldese
                    Base.getI().getStatusforward().add(tmp);
                    endWorkflow(tmp);
                }
                catch(Exception e){
                    Logger.getI().workflow(tmp.getWrtID(), e, "job=\"" + tmp.getJobID() + "\" pid=\"" + tmp.getPID() + "\" status=\""+tmp.getStatus()+"\"");
                    Logger.getI().pool("incoming-status","exception workflow=\""+tmp.getWrtID()+"\" submitID=\""+tmp.getWorkflowSubmitID()+"\" job=\"" + tmp.getJobID() + "\" pid=\"" + tmp.getPID() + "\" status=\""+tmp.getStatus()+"\"", e, pool.size());
                }
                sendingpool=null;
        }// while(true)
    }

    private Hashtable getOutputsCount(JobStatusBean pValue){
        Hashtable res=new Hashtable();
        WorkflowRuntimeBean wrb=Base.getZenRunner(pValue.getWrtID()).getWorkflowData();
        Hashtable<String,Output> outputs=Base.getZenRunner(pValue.getWrtID()).getJob(pValue.getJobID()).getOutputs();
        Enumeration<String> enm=outputs.keys();
        String key;
        Output out;
        while(enm.hasMoreElements()){
            key=enm.nextElement();
            out=outputs.get(key);
            if(out.isGenerator()){
                try{
                    WFIStorageClient wsc=(WFIStorageClient)InformationBase.getI().getServiceClient("storage", "wfi");
                    FileBean fb=new FileBean();
                    fb.setPortalid(wrb.getPortalID());
                    fb.setUserid(wrb.getUserID());
                    fb.setWorkflowname(wrb.getWorkflowID());
                    fb.setRuntimeid(pValue.getWrtID());
                    fb.setJobname(pValue.getJobID());
                    fb.setPid(""+pValue.getPID());
                    fb.setPrefix(key+"_");
                    long cnt=wsc.getNumberOfFileInDirectory(fb);
                    res.put(key, (int)cnt);
                }
                catch(Exception e){e.printStackTrace(); res.put(key, 0);}
            }
            else res.put(key, 1);
        }

        return res;
    }

private synchronized JobStatusBean manageStatus(JobStatusBean tmp) throws Exception
{
    if(tmp == null) throw new NullPointerException("not valid status info");

    int jobStatus=tmp.getStatus(); // temporary job status informacio


        Runner workflow=Base.getZenRunner(tmp.getWrtID()); //workflow of job;



// job status is end-status
        if(Status.isEndStatus(jobStatus)){
            if(!Status.isAbort(jobStatus)){
                    if(workflow == null) throw new NullPointerException("not valid workflow info");
                    PSInstance endJob=WorkHandler.getI().getInstancePool().getPSInstance(tmp.getWrtID(),tmp.getJobID(), tmp.getPID());
                    try{endJob.setStatus(jobStatus);}
                    catch(Exception e){Logger.getI().workflow(tmp.getWrtID(), e, "nincs-job job=\""+tmp.getJobID()+"\" pid=\""+tmp.getPID()+"\"" );}
                    Logger.getI().workflow(tmp.getWrtID(),Logger.INFO,"status-end job=\"" + tmp.getJobID() + "\" pid=\"" + tmp.getPID() + "\" status=\"" + tmp.getStatus() + "\"");
                    if(Status.isFinished(jobStatus)) {
                        tmp.setOutputs(getOutputsCount(tmp));
                        workflow.finishedJobInstance(tmp.getJobID(), tmp.getPID(),tmp.getOutputs());
                    }
                    else setNoRuning(tmp);
                    workflow.nextevent(tmp.getJobID(), tmp.getPID());
            }
            Base.getZenRunner(tmp.getWrtID()).getJob(tmp.getJobID()).getInstances().remove(tmp.getPID());
            workflow.setWorkflowStatus(tmp);
        }

        tmp.setWorkflowStatus(""+workflow.getWorkflowStatus());
        tmp.setPortalID(workflow.getWorkflowData().getPortalID());
        tmp.setUserID(workflow.getWorkflowData().getUserID());
        tmp.setWorkflowID(workflow.getWorkflowData().getWorkflowID());

//    if (!"rescue".equals(tmp.getResource())) return tmp;
//    else throw new NullPointerException("rescue job");
        return tmp;
}




/**
 * workflow vege vizsgalat
 * @param pValue statusz lista
 */
    private void endWorkflow(JobStatusBean t)
    {
            if(("6".equals(t.getWorkflowStatus()))||("7".equals(t.getWorkflowStatus()))||("28".equals(t.getWorkflowStatus())))
            {
                try{Base.deleteWorkflow(t.getWrtID());}
                catch(Exception de)
                {
                    System.out.println("DELETE ERROR:"+t.getWrtID());
                    de.printStackTrace();
                }
            }
        
    }


    public JobStatusBean removeFirstElementFromPool() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JobStatusBean getFirstElement(String pZenID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int removeJobs(String pZenID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

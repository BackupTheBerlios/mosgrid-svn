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
 * ZenService.java
 * Zen WFI Singleton-ja
 */

package hu.sztaki.lpds.wfi.service.zen;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.logging.Logger;
import hu.sztaki.lpds.wfi.com.WorkflowInformationBean;
import hu.sztaki.lpds.wfi.service.zen.data.JobInstanceRunner;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.wfi.zen.pools.RunableInstanceBean;
import hu.sztaki.lpds.wfi.zen.pools.WorkHandler;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author krisztian karoczkai
 */
public class Base extends Thread{
    private static Hashtable<String,Runner> zens=new Hashtable<String,Runner>(); //workflowID , workflow object
    private WorkflowJobsAbortThread abort=new WorkflowJobsAbortThread();
    private WorkflowAbortThread abortWF=new WorkflowAbortThread();
    private static Base instance=null; //singleton
    private long maxZen=0;
    private long eZen=0;    
    private boolean sendInit = false;
    private long maxJobCount=1000000;
    private long maxUserJobCount=100000;
    private static long activateJobCount=0;

    private BlockingQueue<String> queue = new LinkedBlockingQueue<String>(); //workflow submit queue
    public boolean applicationrun=true; //queue pooling

    private StatusForwardThread statusforward=new StatusForwardThread();

/**
 * Class constructor
 */    
    public Base()
    {
        System.out.println("*******create WFI base********");
        maxZen=1000000;
        // read send init
        String sendInitProp = PropertyLoader.getInstance().getProperty("guse.wfi.zen.sendinitstatus");
        try{maxJobCount=Long.parseLong(PropertyLoader.getInstance().getProperty("wfi.zen.activeingjobs.max"));}
        catch(Exception e){}
        try{maxUserJobCount=Long.parseLong(PropertyLoader.getInstance().getProperty("wfi.zen.activeingjobs.usermax"));}
        catch(Exception e){maxUserJobCount=maxJobCount;}
        if ("true".equals(sendInitProp))  sendInit = true;
        start();
        abort.start();
        abortWF.start();
        statusforward.start();
    }

    public Hashtable<String,Runner> getAllWorkflowInstance(){return zens;}

    public long getMaxUserJobCount() {
        return maxUserJobCount;
    }


    public WorkflowJobsAbortThread getAbortJobs() {return abort;}
    public WorkflowAbortThread getAbortWFs() {return abortWF;}

    public void setAbort(WorkflowJobsAbortThread abort) {
        this.abort = abort;
    }



    @Override
    public void run() {
        while(applicationrun){
            try {Base.getZenRunner(queue.take()).run();}
            catch (Exception ex) {ex.printStackTrace();}            
        }
    }

    public StatusForwardThread getStatusforward() {
        return statusforward;
    }

    public void setStatusforward(StatusForwardThread statusforward) {
        this.statusforward = statusforward;
    }


    public boolean isBigWorkflow(long jobNumber,WorkflowRuntimeBean pWFD) {
        Enumeration<Runner> runningwf=zens.elements();
        Runner tmp;
        WorkflowRuntimeBean wfuserdata;
        long userJobs=0;
        while(runningwf.hasMoreElements()){
            tmp=runningwf.nextElement();
            wfuserdata=tmp.getWorkflowData();
            if(wfuserdata.getPortalID().equals(pWFD.getPortalID()) &&
               wfuserdata.getUserID().equals(pWFD.getPortalID())     
                    )userJobs+=tmp.getJobsNumber();
        }
        if (jobNumber < getMaxJobs() && userJobs<=getMaxUserJobCount())
           return false;
        else return true;
    }

    public synchronized static void incJobCount(long newjobCount) {activateJobCount += newjobCount;}

    public synchronized static void decJobCount(long jobCount) {activateJobCount -= jobCount;}

/**
 * Maximalisan kezelheto jobok lekerdezese
 * @return a Jobok szama
 */    
    public long getMaxJobs(){return maxJobCount;}
/**
 *Aktualisan kezelendo jobok lekerdezese
 * @return a Jobok szama
 */    
    public long getCreatedJobs(){return activateJobCount;}
    
/**
 * Workflow futtato objektum lekerdezese
 * @param pZenID zenID
 * @return wokrflow futtatast felugyelo objektum
 */
    public static synchronized Runner getZenRunner(String pZenID) {
        return (Runner)zens.get(pZenID);
    }

/**
 * Singleton peldanyt visszaado metodus
 * @return Singleton peldany
 * @see hu.sztaki.lpds.wfi.service.zen.Base
 */    
    public static Base getI()
    {
        if(instance==null)instance=new Base();
        return instance;
    }

/**
 * Uj workflow feldolgozas inditasa
 * @param pWorkflowData Workflow leiro
 * @param pParentID Szulo workflow azonositoja
 * @param pIR Beagyazast megvalosito Job leiroja
 * @param ppi orokolt inputok
 * @param ppo orokolt outputok
 * @return Workflow runtime ID
 * @see String
 * @see WorkflowRuntimeBean
 * @see JobInstanceRunner
 * @see Hashtable
 */    
    public synchronized String addZenRunner(WorkflowRuntimeBean pWorkflowData, String pParentID, RunableInstanceBean pIR, Hashtable ppi, Hashtable ppo )
    {
        eZen++;
        String sid="zentest";//PropertyLoader.getInstance().getProperty("service.id");
        String pZenID=System.nanoTime()+sid;
        if (!pWorkflowData.getRuntimeID().equals("")) pZenID=pWorkflowData.getRuntimeID();
        
        while(zens.get(pZenID)!=null) pZenID=System.nanoTime()+sid;
        if (pParentID != null) {
            if(getZenRunner(pParentID).isAborting()) return "";
            else getZenRunner(pParentID).addNewEmbedWf(pZenID);
        }

        zens.put(pZenID, new Runner(pWorkflowData, pZenID, pParentID, pIR, ppi, ppo));
        queue.add(pZenID);

        
        return pZenID;


    }


/**
 * Running/error statuszban levo workflow
 * error statuszban levo jobjainak
 * futaskozbeni ujrakuldese...
 *
 * @param WorkflowRuntimeBean pWorkflowData - workflow azonositokat tartalmaz
 */
    public synchronized void rescueZenRunner(WorkflowRuntimeBean pWorkflowData) {
        try {
            String pRuntimeID = pWorkflowData.getRuntimeID();
            if (zens.get(pRuntimeID) != null) {
                ((Runner) zens.get(pRuntimeID)).rescue(pWorkflowData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
/**
 * Workflow futatasi vizsgalat
 * @return true a workflow futtathato, false Zen nem tud tobb workflowt futatni
 */    
    public boolean isRunnable(){return zens.size()<maxZen;}
    
/**
 * Lealit egy futo workflowt
 * @param pZenID Workflow runtimeID
 */    
    public static synchronized void deleteWorkflow(String pZenID)
    {
// InstancePool takaritas
        if (getZenRunner(pZenID) == null)
            return;

        if (getZenRunner(pZenID).getParentZenID() == null) 
            WorkHandler.getI().getInstancePool().finishWorkflow(pZenID);
        
//logg
        Logger.getI().workflow(pZenID, Logger.INFO, "endtime");
        try
        {
//kimeno pool torlese
//beagyazasok torlesenek ellenorzese
            Vector<String> tmpe=getZenRunner(pZenID).getEmbedWFS();
            for(int i=0;i<tmpe.size();i++) {
                if (getZenRunner(tmpe.get(i))!=null) {
                    deleteWorkflow(tmpe.get(i));
                }
            }

            // wfi jobok szamanak aktualizalasa
            long jobCnt = getZenRunner(pZenID).getJobsNumber();
            // real remove from wfi...
            zens.remove(pZenID);
                decJobCount(jobCnt);

        }
        catch(NullPointerException e){e.printStackTrace();}
        System.gc();
    }

/**
 * Runner kereses
 * @deprected
 * @param pPortalID Portal URL
 * @param pUserID Portal user
 * @param pWorkflowID Workflow neve
 * @return Feldolgozando workflow
 * @see Runner
 */    
    public Runner isEqual(String pPortalID, String pUserID, String pWorkflowID)
    {
        Runner tmp;
        Enumeration enm=zens.keys();
        while(enm.hasMoreElements())
        {
            String key=""+enm.nextElement();
            tmp=(Runner)zens.get(key);
            if(tmp.isEqual(pPortalID, pUserID, pWorkflowID))return tmp;
        }
        return null;
    }
    
/**
 * Listazza a server konzolra a feldolgozas alat levo workflowkat
 * (debug)
 */        
    public void listZens()
    {
        Runner tmp;
        Enumeration enm=zens.keys();
        int i=0;
        while(enm.hasMoreElements())
        {
            String key=""+enm.nextElement();
            tmp=(Runner)zens.get(key);
            tmp.getStatus();
        }
    }

    public void listZens2()
    {
        Enumeration enm=zens.keys();
        while(enm.hasMoreElements())
        {
            String key=""+enm.nextElement();
            System.out.println("*"+key);
        }
    }

    /**
     * Visszaadja a send init property erteket.
     * Ettol fugg hogy az init job statuszokat
     * a wfi elkuldi e a wfs-nek es portal-nak.
     */
    public boolean getSendInit() {
        return sendInit;
    }


    
    /**
     * futtatando workflowk informacios listajanak eloallitasa
     * (ez jelenik meg a system load portleten)
     * 
     * @return workflowinformacios lista
     * @see WorkflowInformationBean
     */
    public Vector<WorkflowInformationBean> getInformation() {
        Vector<WorkflowInformationBean> res = new Vector<WorkflowInformationBean>();
        if (!zens.isEmpty()) {
            WorkflowInformationBean tmp;
            Runner trunner;
            // workflowk sorbarendezese
            Vector<String> sortvector = new Vector<String>(zens.keySet());
            Collections.sort(sortvector);
            //
            for (String enm : sortvector) {
                try {
                    res.add(new WorkflowInformationBean());
                    trunner = zens.get(enm);
                    tmp = res.get(res.size() - 1);
                    tmp.setPortalid(trunner.getWorkflowData().getPortalID());
                    tmp.setUserid(trunner.getWorkflowData().getUserID());
                    tmp.setWorkflowid(trunner.getWorkflowData().getWorkflowID());
                    tmp.setInstancename(trunner.getWorkflowData().getInstanceText());
                    tmp.setStatus(trunner.getWorkflowStatus());
                    tmp.setJobNumbers(trunner.getJobsNumber());
                    tmp.setRuntimeid(trunner.getZenID());
                    tmp.setStatuses(trunner.getAllJobStatus());
                } catch (Exception e) {
                }
            }
        }
        return res;
    }
    
}

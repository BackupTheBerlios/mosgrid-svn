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
 * ZenRunner.java
 * Egy workflow feldolgozast elvegzo objektum
 */

package hu.sztaki.lpds.wfi.service.zen;

import hu.sztaki.lpds.wfs.utils.Status;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.logging.Logger;
import hu.sztaki.lpds.portal.inf.WfsPortalClient;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.wfi.zen.pools.WorkHandler;
import hu.sztaki.lpds.wfi.service.zen.data.IOInherited;
import hu.sztaki.lpds.wfi.service.zen.util.perzistence.LocalFilesImpl;
import hu.sztaki.lpds.wfi.service.zen.xml.Kxml;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Input;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Job;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstance;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Rescue;
import hu.sztaki.lpds.wfs.com.WorkflowConfigErrorBean;
import hu.sztaki.lpds.wfi.zen.pools.RunableInstanceBean;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.wfi.util.WfiCoreUtil;
import hu.sztaki.lpds.wfi.zen.pools.JobInstanceReferenceBean;
import hu.sztaki.lpds.wfs.com.ComDataBean;

import javax.xml.parsers.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Runner{
    
    private Hashtable<String,Job> jobs = new Hashtable<String,Job>(); //jobok
    private String zenID = ""; // workflow peldany azonosito

    // submitID : workflow peldany eseten is elter kulonbozo
    // submittalasok alkalmaval pl: submit majd suspend majd resume
    // igy a submit es resume alkalmaval elter ez az id...

    private Vector<String> embedWFs=new Vector<String>(); //Beagyazott wf-k ZenID-i
    private WorkflowRuntimeBean workflowData; //workflow leiro
    private String parentZenID=null; //szulo wf zenID-ja
    private RunableInstanceBean jobIR=null; //Szulo wf beagyazott job leiro
    private Hashtable pi=new Hashtable();  //inputok szulo wf-bol
    private Hashtable po=new Hashtable(); // outpok szulo wf-ba
    private int status = 1;//a default workflow statusz erteke : init


    private int status_finish=0;
    private int status_error=0;
    private int status_noinput=0;
    private int status_termisfalse=0;
    private int status_abort=0;
    private int status_aborting=0;
    private boolean aborting=false;

    private long workingstatus=0;
    private long workingsubmit=0;

    public long getWorkingstatus() {return workingstatus;}
    public synchronized void setWorkingstatus(long workingstatus) {this.workingstatus = workingstatus;}
    public synchronized void incWorkingstatus() {this.workingstatus++;}
    public synchronized void decWorkingstatus() {this.workingstatus--;}
    public boolean isWorkingstatus(){return workingstatus!=0;}

    public long getWorkingsubmit() {return workingsubmit;}
    public void setWorkingsubmit(long workingsubmit) {this.workingsubmit = workingsubmit;}
    public synchronized void incWorkingsubmit(){workingsubmit++;}
    public synchronized void decWorkingsubmit(){workingsubmit--;}
    public boolean isWorkingsubmit(){return workingsubmit>0;}


    private Hashtable<String,Vector<Rescue>> rescue = null;
    private Vector<WorkflowConfigErrorBean> error = null;
    private CopyOnWriteArrayList<RunableInstanceBean> submitting=new CopyOnWriteArrayList<RunableInstanceBean>();

    public CopyOnWriteArrayList<RunableInstanceBean> getSubmitingQueue(){return submitting;}

    // ebben a workflowban ennyi job fog
    // keletkezni de csak akkor ha elindul
    private long jobnumber=0;

    //private Hashtable<String,JobRuntime> running=new Hashtable<String,JobRuntime>();
    
    /**
     * Class constructor
     */
    public Runner(){
        zenID=""+System.currentTimeMillis();
        WorkHandler.getI().getInstancePool().createWorkflow(zenID);

    }    
    

    /**
     * Class constructor
     * @param pWorkflowData Workflow leiro
     * @param pZenID workflow Runtime ID
     * @param pParentID Szulo workflow azonositoja
     * @param pIR Beagyazast megvalosito Job leiroja
     * @param ppi orokolt inputok
     * @param ppo orokolt outputok
     */
    public Runner(WorkflowRuntimeBean pWorkflowData, String pZenID, String pParentID, RunableInstanceBean pIR, Hashtable ppi, Hashtable ppo)
    {   

        workflowData=pWorkflowData;
        zenID=pZenID;
        parentZenID=pParentID;
        jobIR=pIR;
        pi=ppi;
        po=ppo;
    }

    public void addNewEmbedWf(String pValue){embedWFs.add(pValue);}
    public Vector<String> getEmbedWFS(){return embedWFs;}
    public Hashtable getParentInput(){return pi;}
    public Hashtable getParentOutput(){return po;}

    /**
     * A tenylegesen elindult workflow
     * jobjainak szamat adja vissza...
     * Ez a jobszam futas kozben megvaltozhat
     * a generatoroknak koszonhetoen...
     */
    public long getJobsNumber(){return jobnumber;}

    
  
    public void run() {
        try{
            if(parentZenID!=null){
                if(Base.getZenRunner(parentZenID)==null) {
                    Logger.getI().workflow(zenID, Logger.INFO, "parent-not-exist");
                    Base.deleteWorkflow(zenID);
                    return;
                }
                if(Base.getZenRunner(parentZenID).isAborting()){
                    Logger.getI().workflow(zenID, Logger.INFO, "parent-aborting");
//                    Base.deleteWorkflow(zenID);
                    return;
                }
            }
        }
        catch(Exception e){e.printStackTrace();Base.deleteWorkflow(zenID);return;}
        this.status = 1;

// logg
            Logger.getI().workflow(zenID, Logger.INFO,"param name=\"portalid\" value=\""+workflowData.getPortalID()+"\"");
            Logger.getI().workflow(zenID, Logger.INFO,"param name=\"userid\" value=\""+workflowData.getUserID()+"\"");
            Logger.getI().workflow(zenID, Logger.INFO,"param name=\"workflowid=\" value=\""+workflowData.getWorkflowID()+"\"");
            Logger.getI().workflow(zenID, Logger.INFO,"param name=\"runtimeid\" value=\""+zenID+"\"");
            Logger.getI().workflow(zenID, Logger.INFO,"param name=\"instancetext\" value=\""+workflowData.getInstanceText()+"\"");


        try {
// read workflow xml from wfs
            readRemoteWorkflowConfigXML();
// konfiguracios hibaval submitalodott wf
            if (error.size() > 0) {
                this.status = Status.ERROR;
                setParentJobStatus();
                Base.deleteWorkflow(zenID);
                return;
            }
// read workflow rescue xml from wfs
            if (rescue != null) {
                if (!rescue.isEmpty()) {
                    readRemoteWorkflowRescueXML();
                }
            }
// jobok szamanak meghatarozasa
            initWorkflowJobs();
            this.status = 2;
            //
            if (isNotBigWorkflow()) {
                Base.getI().incJobCount(jobnumber);
                Enumeration enm;
//orokles
                if (parentZenID != null) {
                    workflowData.setStorageID(Base.getZenRunner(parentZenID).getWorkflowData().getStorageID());
                    workflowData.setPortalID(Base.getZenRunner(parentZenID).getWorkflowData().getPortalID());
                    workflowData.setUserID(Base.getZenRunner(parentZenID).getWorkflowData().getUserID());
                    workflowData.setWfsID(Base.getZenRunner(parentZenID).getWorkflowData().getWfsID());
                    enm = pi.keys();
                    IOInherited tmp;
                    try {
//                        System.out.println("************"+workflowData.getWorkflowID()+"*****************");
                        while (enm.hasMoreElements()) {
                            Object key = enm.nextElement();
                            String[] s = ((String) key).split("/");
                            tmp = (IOInherited) pi.get(key);
//                            System.out.println("**"+s[0]+"/"+s[1]+"="+tmp.getWorkflow()+"/"+tmp.getJob()+"/"+tmp.getPort()+"_"+tmp.getPID()+"_"+tmp.getIndex());
                            ((Input) ( jobs.get(s[0])).getInputs().get(s[1])).setAttribute("rworkflow", tmp.getWorkflow());
                            if (tmp.getWorkflowRID() != null) {
                                ((Input) ( jobs.get(s[0])).getInputs().get(s[1])).setAttribute("rworkflowrid", tmp.getWorkflowRID());
                            }
                            if (tmp.getPort() != null) {
                                ((Input) ( jobs.get(s[0])).getInputs().get(s[1])).setAttribute("rport", tmp.getPort());
                            }
                            ((Input) ( jobs.get(s[0])).getInputs().get(s[1])).setAttribute("rjob", tmp.getJob());
                            ((Input) ( jobs.get(s[0])).getInputs().get(s[1])).setAttribute("rseq", tmp.getSeq());
                            ((Input) ( jobs.get(s[0])).getInputs().get(s[1])).setAttribute("rpid", tmp.getPID());
                        }
                    } catch (Exception e1) {
                        System.out.println("PARENT ERROR");
                        e1.printStackTrace();
                        this.status = Status.ERROR;
                        Base.deleteWorkflow(zenID);
                        return;
                    }
                } //ororkles vege

//workflow fuggosegek cache-lese
                Enumeration enmCJob = jobs.keys();
                Input cacheInput;
                Job cacheJob;
                Enumeration enmCInput;
                while (enmCJob.hasMoreElements()) {
                    cacheJob = getJob((String) enmCJob.nextElement());
                    enmCInput = cacheJob.getInputs().keys();
                    while (enmCInput.hasMoreElements()) {
                        cacheInput = cacheJob.getInput((String) enmCInput.nextElement());
                        if (cacheInput.getPreJob() != null) {
                            getJob(cacheInput.getPreJob()).addPostJob(cacheInput.getPreOutput(), cacheJob);
                            cacheJob.addPreJob(getJob(cacheInput.getPreJob()));
                        }
                    }
                }
                if(!aborting) {firsRun();status=5;}
                else Base.deleteWorkflow(zenID);
                if(parentZenID!=null) setParentJobStatus();
            }//isNotBigWorkflow
        } catch (Exception e) {
            System.out.println("BIG ERROR");
            e.printStackTrace();
            try{abort();}
            catch(Exception e0){e0.printStackTrace();}
        }

        // workflow queue kezeles, minden keppen keruljon ki a queue bol...
    }

    public IOInherited getParentOutputPort(String pJob,String pPort) throws NullPointerException
    {
        Enumeration enm=po.keys();//Base.getI().getRunner(parentZenID).getParentOutput().keys();
        String key;
        IOInherited tmp;
        while(enm.hasMoreElements())
        {
            key=(String)enm.nextElement();
            tmp=(IOInherited)po.get(key);
            if(key.endsWith(pJob+"/"+pPort))
            {
                return tmp;
            }
        }
        throw new NullPointerException("");
    
    }

    public IOInherited getParentInputPort(String pJob,String pPort) throws NullPointerException {
        if (pi.get(pJob+"/"+pPort)!=null) {
            return (IOInherited)pi.get(pJob+"/"+pPort);
        }
        throw new NullPointerException("not embed:"+workflowData.getWorkflowID()+"("+zenID+")."+pJob+"/"+pPort);
    }

    public void setJobStatus(int pJobState) {
        if(Status.isFinished(pJobState)) status_finish++;
        else if(Status.isError(pJobState)) status_error++;
        else if(pJobState==Status.NOINPUT) status_noinput++;
        else if(pJobState==Status.FALSEINPUT) status_termisfalse++;
        else if(pJobState==Status.ABORTING) status_aborting++;
        else if(pJobState==Status.ABORTED) status_abort++;
    }
    
    public HashMap getAllJobStatus() {
        HashMap res = new HashMap();
        long t0, t1, t2, t3;
        t0 = getRunningJobs();
        t1 = workingsubmit;
        if (status_finish > 0) res.put(Status.FINISH, status_finish);
        if (status_error > 0) res.put(7, status_error);
        if (status_noinput > 0) res.put(25, status_noinput);
        if (status_termisfalse > 0) res.put(21, status_termisfalse);
        if (status_abort > 0) res.put(28, status_abort);
        if (status_aborting > 0) res.put(22, status_abort);
        t3 = WorkHandler.getI().getInstancePool().getManagedInstance(zenID);
        t2 = (int) (jobnumber - t3 - t1- status_finish - status_error - status_noinput - status_termisfalse);
        if (t2 < 0) t2 = 0;

        if (t0 > 0) res.put(5, t0-t1); // running
        if (t1 > 0) res.put(66, t1);// waiting for resource
        res.put(1, t2);// init
        if (t3 > 0) res.put(98, t3);// managed
        return res;
    }
    
    /**
     * Workflow jobjainak lekerdezese
     * @return jobokat tartalmazo hashtable<String,Job>
     */
    public Hashtable getJobs(){return jobs;}

    /**
     * Workflow egy Jobjanak lekerdezese
     * @return Job
     * @see Job
     */
    public Job getJob(String pJobId){return jobs.get(pJobId);}
    
    /**
     * Workflow leiro lekerdezese
     * @return liiro xml string
     */
    private String readRemoteWorkflowConfigXML(){
        Hashtable sUrl=new Hashtable();
        sUrl.put("url",workflowData.getWfsID());
        ServiceType st=InformationBase.getI().getService("wfs","wfi",sUrl,new Vector());
        String pXMLData="";
            
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try
            {
                SAXParser saxParser = factory.newSAXParser();
                Kxml handler=new Kxml(zenID);
                pXMLData=WfiCoreUtil.readRemoteXML(workflowData, zenID);
                LocalFilesImpl perzis=new LocalFilesImpl();
                Hashtable persistdata=new Hashtable();
                persistdata.put("portalid", workflowData.getPortalID());
                persistdata.put("storageid", workflowData.getStorageID());
                persistdata.put("wfsid", workflowData.getWfsID());
                persistdata.put("workflow", workflowData.getWorkflowID());
                persistdata.put("workflow-type", workflowData.getWorkflowType());
                persistdata.put("user", workflowData.getUserID());
                persistdata.put("instance-text", workflowData.getInstanceText());
//        jobIR=pIR;
//        pi=ppi;
//        po=ppo;
                Enumeration enm;
                Object key;
                if(po!=null)
                {
                    enm=po.keys();
                    while(enm.hasMoreElements())
                    {
                        key=enm.nextElement();
                        persistdata.put("parent-output-"+key, po.get(key));
                    }
                }
                if(pi!=null)
                {
                    enm=pi.keys();
                    while(enm.hasMoreElements())
                    {
                        key=enm.nextElement();
                        persistdata.put("parent-input-"+key, pi.get(key));
                    }
                }
                if(jobIR!=null)
                {
                    persistdata.put("parent-job", jobIR.getJobName());
                    persistdata.put("parent-pid", jobIR.getPid());
                }
                perzis.newInstance(zenID,parentZenID,  pXMLData, persistdata);
                perzis=null;
                persistdata=null;
                
                saxParser.parse(new ByteArrayInputStream(pXMLData.getBytes()), handler);
                for (int i=0;i<handler.getXMLData().size();i++) {
                    jobs.put(((Job)handler.getXMLData().get(i)).getName(), handler.getXMLData().get(i));
                }
                
                Hashtable sData=new Hashtable();
                sData.put("url", workflowData.getPortalID());
                st=InformationBase.getI().getService("portal","wfs",sData,new Vector());
//rescue
                rescue = handler.getRescueData();
//workflow config error
                error = handler.getErrorData();
                //
                if (handler.getXMLData().size() == 0) {
                    error.addElement(new WorkflowConfigErrorBean(0, "", "", "not_valid_workflow"));
                }
            }
            catch (Throwable t) 
            {
                System.out.println("Hib_s workflowleiro ADAT::"+zenID+"::\n"+pXMLData+"::");
                t.printStackTrace();
            }
            
        return pXMLData;
    }

    /**
     * Workflow rescue adat leiro lekerdezese
     * @return liiro xml string
     */
    private void readRemoteWorkflowRescueXML() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        rescue = new Hashtable<String, Vector<Rescue>>();
        //
        Enumeration jobEnum = jobs.keys();
        //
        while ((jobEnum.hasMoreElements()) && (isNotAbortedZenRunner())) {
            String jobName = (String) jobEnum.nextElement();
            long cnt = 0;// 0 tol megy n ig (amig kapunk vissza adatokat)
            long retCnt = 100;// a visszakapott rescue statuszok szama
            //
            while ((retCnt > 0) && (isNotAbortedZenRunner())) {
                // addig fut a ciklus mig kapunk vissza adatokat es
                // letezik ez a runner a base nyilvantartasaban...
                String rescueXML = "";
                Hashtable<String,Vector<Rescue>> retRescueHash = new Hashtable<String, Vector<Rescue>>();
                try {
                    // read rescue xml
                    rescueXML = WfiCoreUtil.readRemoteRescueXML(workflowData, zenID, jobName, String.valueOf(cnt));
//System.out.println("wfi readRemoteWorkflowRescueXML rescueXML : " + rescueXML);
                    try {
                        // parse rescue xml
                        Kxml handler = new Kxml(zenID);
                        SAXParser saxParser = factory.newSAXParser();
                        saxParser.parse(new ByteArrayInputStream(rescueXML.getBytes()), handler);
                        retRescueHash = handler.getRescueData();
                    } catch (Exception e) {
                        retRescueHash = new Hashtable<String, Vector<Rescue>>();
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    System.out.println("ERROR OF WORKFLOW DESCIPTION::" + zenID + "::\n" + rescueXML + "::");
                    e.printStackTrace();

                }
                if (retRescueHash.isEmpty()) {
                    retCnt = 0;
                } else {
                    Vector tmpVect = retRescueHash.get(jobName);
                    retCnt = tmpVect.size();
                    // uj adatok hozzaadasa a rescue hash hez
                    if (rescue.get(jobName) == null) {
                        rescue.put(jobName, tmpVect);
                    } else {
                        rescue.get(jobName).addAll(tmpVect);
                    }
                    cnt++;
                }
            }
        }
    }

    /**
     * Workflow futasanak megszakitasa
     */
    public void abort() throws Exception{
        Logger.getI().workflow(zenID, Logger.INFO, "aborted-workflow");
        aborting=true;
        if(jobs.size()==0) status=Status.ABORTED;
        else status=Status.ABORTING;
        submitting.clear();
        workingsubmit=0;
        setParentJobStatus();
        Logger.getI().workflow(zenID, Logger.INFO, "aborting-workflow");
        Long pid;
        for(String ewfid:embedWFs){
            try{Base.getI().getAbortWFs().add(ewfid);}
            catch(Exception e){/* wf mar nem letezik*/}
        }
        Enumeration<String> enm=jobs.keys();
        Enumeration<Long> enmI;
        String jobName;
        while(enm.hasMoreElements()){
            jobName=enm.nextElement();
            enmI=getJob(jobName).getInstances().keys();

            JobInstanceReferenceBean jirb;
            while(enmI.hasMoreElements()){
                pid=enmI.nextElement();
                Logger.getI().workflow(zenID, Logger.INFO, "aborting-job name=\""+jobName+"\" pid=\""+pid+"\"");
                jirb=getJob(jobName).getInstances().get(pid);
                if(getJob(jobName).getParent()==null){
                        try{
                            Base.getI().getAbortJobs().getJobs().add(jirb);
                            JobStatusBean newStatus=createAbortStatus();
                            newStatus.setJobID(jobName);
                            newStatus.setPID(pid);
                            WorkHandler.getI().getIncomingStatusPool().addNewStatusInformation(newStatus);
                        }
                        catch(Exception e){/*job mar nem letezik*/}
                }
            }
        }
        Logger.getI().workflow(zenID, Logger.INFO, "aborted-workflow");
    }

    private JobStatusBean createAbortStatus(){
        JobStatusBean newStatus=new JobStatusBean();
        newStatus.setWrtID(zenID);
        newStatus.setResource("user abort");
        newStatus.setStatus(Status.ABORTED);
        newStatus.setTim(System.currentTimeMillis());
        return  newStatus;
    }

    /**
     * Running/error statuszban levo workflow error
     * statuszban levo jobjainak futaskozbeni ujrakuldese...
     *
     * @param WorkflowRuntimeBean pWorkflowData - workflow azonositokat tartalmaz
     */
    public synchronized void rescue(WorkflowRuntimeBean pWorkflowData) throws Exception {
        if (pWorkflowData.getErrorJobList() != null) {
            if (pWorkflowData.getErrorJobList().size() > 0) {
                for (int jPos = 0; jPos < pWorkflowData.getErrorJobList().size(); jPos++) {
                    try {
                        ComDataBean jobBean = (ComDataBean) pWorkflowData.getErrorJobList().get(jPos);
                        String jobName = jobBean.getJobID();
                        long jobPid = new Long(jobBean.getJobPID()).longValue();
                        if (WorkHandler.getI().getInstancePool().getPSInstance(zenID, jobName, jobPid) != null) {
                            int jobStatus = WorkHandler.getI().getInstancePool().getPSInstance(zenID, jobName, jobPid).getStatus();
                            if (Status.isError(jobStatus)) {
                                rescueJob(jobName, jobPid);
                            }
                        }
                    } catch (Exception e) {e.printStackTrace();}
                }
            }
        }
    }

    /**
     * Az errorban levo job instance peldanyt
     * ujbol az outputpoolba teszi, es
     * runningba allitja a statuszat...
     *
     * @param jobName - job neve
     * @param jobPid - job pid azonosito
     * @throws Exception
     */
    private void rescueJob(String jobName, long jobPid) throws Exception {
        Job tmpJob =  jobs.get(jobName);
        if (tmpJob != null) {
            RunableInstanceBean instanceBean = new RunableInstanceBean(this.getZenID(),tmpJob.getName(), jobPid);
            WorkHandler.getI().getInstancePool().getPSInstance(zenID, jobName, jobPid).setStatus(4);
            WorkHandler.getI().getOutPool().addJob(zenID, instanceBean);
            status_error--;
        }
    }

    private void localStorageCopyFile(String pSrc, String pDest) throws Exception{
//        System.out.println("::::::zen copy file("+pSrc+","+pDest+")");
        byte[] b=new byte[512];
        FileInputStream fis=new FileInputStream(PropertyLoader.getInstance().getProperty("prefix.dir")+pSrc);
        FileOutputStream fos= new FileOutputStream(PropertyLoader.getInstance().getProperty("prefix.dir")+pDest);
        int i=0;
        while((i=fis.read(b))>(-1)){
            fos.write(b,0,i);
            fos.flush();
        }
        fis.close();
        fos.close();
    }

    /**
     * Szulo workflow Job status valtoztat_sa
     * @return pStatus uj statusz
     */
    public void setParentJobStatus(){
        if(parentZenID!=null){
            if(Base.getZenRunner(parentZenID)!=null){
                Runner tmp=Base.getZenRunner(parentZenID);
                Hashtable embedOutputsCount=new Hashtable();
                if(Status.isFinished(status)){
                    Enumeration<String> enmOuts=po.keys();
                    String keyOut;
                    String[] outData;
                    IOInherited valueOut;
                    while(enmOuts.hasMoreElements()){
                        keyOut=enmOuts.nextElement();
                        valueOut=(IOInherited)po.get(keyOut);
                        outData=keyOut.split("/");
                        int cnt=(int)WorkHandler.getI().getInstancePool().getOutputCounts(zenID, outData[0], outData[1]);
                        embedOutputsCount.put(valueOut.getPort(), cnt);
                        String userPath="storage/"+workflowData.getPortalID().replace("/", "_")+"/"+workflowData.getUserID()+"/";
                        
                        String outputPath=userPath+valueOut.getWorkflow()+"/"+valueOut.getJob()+"/outputs/"+valueOut.getWorkflowRID()+"/"+valueOut.getPID()+"/";
                        
                        File dir=new File(PropertyLoader.getInstance().getProperty("prefix.dir")+outputPath);
                        if(!dir.exists()) dir.mkdirs();
                        String logpath=userPath+valueOut.getWorkflow()+"/"+valueOut.getJob()+"/outputs/"+valueOut.getWorkflowRID()+"/"+valueOut.getPID()+"/";
                        outputPath=outputPath+valueOut.getPort()+"_";

                        String inputPath=userPath+workflowData.getWorkflowID()+"/"+outData[0]+"/outputs/"+zenID+"/";
                        Hashtable<String,PSInstance> jobInstances=WorkHandler.getI().getInstancePool().getAllJobInstances(zenID, outData[0]);
                        int parentOutputIndex=0;
                        long jobInstanceOutputIndex;

                        for(int i=0;i<jobInstances.size();i++){
//                            try{localStorageCopyFile(inputPath+i+"/guse.jsdl", logpath+"guse.jsdl0");}
//                            catch(Exception e){System.out.println("FILE NOT FOUND:"+inputPath+i+"/guse.jsdl=>"+logpath+"guse.jsdl0");}
                            jobInstanceOutputIndex=jobInstances.get(""+i).getOutputCount(outData[1]);
                            if(jobInstances.size()==1 && jobInstanceOutputIndex==1){ //normal
                                try{localStorageCopyFile(inputPath+i+"/"+outData[1], outputPath+"0");}
                                catch(Exception e){
                                    try{localStorageCopyFile(inputPath+i+"/"+outData[1]+"_0", outputPath+"0");}
                                    catch(Exception e0){
//                                        System.out.println("FILE NOT FOUND:"+inputPath+i+"/"+outData[1]+"_0"+"=>"+outputPath+"0");
                                        e.printStackTrace();
                                    }
//                                    System.out.println("FILE NOT FOUND:"+inputPath+i+"/"+outData[1]+"=>"+outputPath+"0");
                                }
                            }
                            else if(jobInstances.size()>1 && jobInstanceOutputIndex==1){ //ps
                                try{localStorageCopyFile(inputPath+i+"/"+outData[1], outputPath+parentOutputIndex);}
                                catch(Exception e){
                                    try{localStorageCopyFile(inputPath+i+"/"+outData[1]+"_0",outputPath+parentOutputIndex);}
                                    catch(Exception e0){e0.printStackTrace();}
//                                    System.out.println(inputPath+i+"/"+outData[1]+"=>"+outputPath+parentOutputIndex);
                                    e.printStackTrace();
                                }
                                parentOutputIndex++;
                            }
                            else{//generator
                                for(int j=0;j<jobInstanceOutputIndex;j++){
                                    try{localStorageCopyFile(inputPath+i+"/"+outData[1]+"_"+j,outputPath+parentOutputIndex);}
                                    catch(Exception e){
//                                        System.out.println(inputPath+i+"/"+outData[1]+"_"+j+"=>"+outputPath+parentOutputIndex);
                                        e.printStackTrace();
                                    }
                                    parentOutputIndex++;
                                }
                            }
                        }

                    }
                }
                JobStatusBean pData=new JobStatusBean(parentZenID,jobIR.getJobName(),(int)jobIR.getPid(),status, workflowData.getInstanceText(), -1);
                pData.setPortalID(tmp.getWorkflowData().getPortalID());
                pData.setUserID(tmp.getWorkflowData().getUserID());
                pData.setWorkflowID(Base.getZenRunner(parentZenID).getWorkflowData().getWorkflowID());
                pData.setWorkflowStatus(""+tmp.getWorkflowStatus());
                pData.setOutputs(embedOutputsCount);
                WorkHandler.getI().getIncomingStatusPool().addNewStatusInformation(pData);
                Logger.getI().workflow(zenID, Logger.INFO, "send-parent-status staus=\""+status+"\"");
                Logger.getI().workflow(parentZenID, Logger.INFO, "send-embed-status staus=\""+status+"\"");
            }
        }
    }
    
    /**
     * Futtatasi adatok lekerdezese
     * @return Workflow futtatasi leiro
     * @see WorkflowRuntimeBean
     */
    public  WorkflowRuntimeBean getWorkflowData(){return workflowData;}

    /**
     * Workflow staus lekerdezese
     * @return workflow statusa
     */
    public int getWorkflowStatus(){return this.status;}

    /**
     * Workflow status kiszamitasa
     * @param pValue JobStatus leiro bean
     */
    public void setWorkflowStatus(JobStatusBean pValue)
    {
        setJobStatus(pValue.getStatus());
        setWorkflowStatus();
    }

    private long getRunningJobs(){
        int res=0;
        Enumeration<String> enm=getJobs().keys();
        String jobName;
        while(enm.hasMoreElements()){
            jobName=enm.nextElement();
            res+=getJob(jobName).getInstances().size();
        }
        return res;
    }

    public boolean isFinish(){

        long res=getRunningJobs();
        Logger.getI().workflow(zenID, Logger.INFO, "pool-size running=\"" + res + "\" submit=\"" + workingsubmit + "\" status=\""+workingstatus+"\"");

        return res==0 && !isWorkingstatus() && !isWorkingsubmit();
    }

    public void setWorkflowStatus(){
        int ostatus=status;
        if (isBigWorkflow()) this.status = 37;
        else {

            if (isFinish()) {
                if(aborting) this.status=Status.ABORTED;
                else if (status_error > 0) this.status = Status.ERROR;// error
                else this.status = Status.FINISH;// finished
            }
            else {
                if(aborting) status=this.status=Status.ABORTING;
                else if (status_error > 0 ) this.status = 23;// running/error
                else this.status = 5;// running
            }
            if (parentZenID != null && ostatus!=status) setParentJobStatus();
        }
        Logger.getI().workflow(zenID, Logger.INFO, "workflow-info status=\"" + status + "\"");
    }

    public boolean isAborting(){return aborting;}


    
    /**
     * Szulo workflow Runtime ID lekerdezese
     * @return Szulo workflow Runtime IDja
     * @see String
     */
    public String getParentZenID(){return parentZenID;}


    
    /**
     * Workflow job statuszainak kiiratasa a konzolra
     */
    public void getStatus()
    {
        Enumeration enm=jobs.keys();
        while(enm.hasMoreElements())
        {
            String key=""+enm.nextElement();
            jobs.get(key).printStatus();
        }
    }

    /**
     * Workflow egyezesog vizsgalat
     * @deprected
     * @param pPortalID Portal URL
     * @param pUserID Portal User
     * @param pWorkflowID Workflow neve
     * @return Egyezoseg
     * @see String
     */
    public boolean isEqual(String pPortalID, String pUserID, String pWorkflowID)
    {   
        return pPortalID.trim().equals(workflowData.getPortalID().trim())&&
               pUserID.trim().equals(workflowData.getUserID().trim())&&
               pWorkflowID.trim().equals(workflowData.getWorkflowID().trim());    
    }

    /**
     * Workflow egyezoseg vizsgalat
     * @param pWorkflowData Workflow leira
     * @return Egyezoseg
     * @see WorkflowRuntimeBean
     */
    public boolean isEqual(WorkflowRuntimeBean pWorkflowData)
    {
        return pWorkflowData.getPortalID().trim().equals(workflowData.getPortalID().trim())&&
               pWorkflowData.getUserID().trim().equals(workflowData.getUserID().trim())&&
               pWorkflowData.getWorkflowID().trim().equals(workflowData.getWorkflowID().trim())&&  
               pWorkflowData.getStorageID().trim().equals(workflowData.getStorageID().trim())&&
               pWorkflowData.getWfsID().trim().equals(workflowData.getWfsID().trim());    
    }
    
    /**
     * Workflow egy Job statuszanak lekerdezese
     * @param jobID job neve
     * @return status
     */
    public int getJobStatus(String jobID){return jobs.get(jobID).getStatus();}

    /**
     * Workflow egyedi futasi azonositojanak lekerdezes
     * @return runtimeID
     */
    public String getZenID(){return zenID;}
/**
 * lefutott Job peldany vegenek elkonyvelese
 * @param pJob JOB neve
 * @param pPID PS instance szama
 * @param pOutputs outputok <output neve, szama>
 */
    public synchronized void finishedJobInstance(String pJob, long pPID, Hashtable pOutputs)
    {
        Logger.getI().workflow(zenID, Logger.INFO, "FINISH JOB="+pJob+"."+pPID);
        PSInstance endJob=WorkHandler.getI().getInstancePool().getPSInstance(zenID, pJob, pPID);
        if(endJob==null){
            Logger.getI().workflow(zenID, Logger.WARNING, "not-exist-instance job=\"" + pJob + "\" pid=\"" + pPID + "\"");
            WorkHandler.getI().getInstancePool().createJob(zenID, pJob);
            endJob=WorkHandler.getI().getInstancePool().getPSInstance(zenID, pJob, pPID);
        }

//keletkezett outputok szamanak aktualizalasa
        Enumeration<String> enmport=pOutputs.keys();
        String outputName;
        long outputCount;
        while (enmport.hasMoreElements()) {
            outputName=enmport.nextElement();
            try {outputCount = ((Integer)pOutputs.get(outputName)).longValue();}
            catch (ClassCastException ec) {outputCount = ((Long) pOutputs.get(outputName)).longValue();}

            Logger.getI().workflow(zenID, Logger.INFO, "add-output job=\"" + pJob + "\" pid=\"" + pPID + "\" output=\""+outputName+"\" count=\""+outputCount+"\"");
            endJob.addOutputCount(outputName, outputCount);

            getJob(pJob).getOutput(outputName).addCounts(outputCount);
            getJob(pJob).getOutput(outputName).setAttribute("realCount", ""+getJob(pJob).getOutput(outputName).getCounts());
            getJob(pJob).getOutput(outputName).setAttribute("count", ""+getJob(pJob).getOutput(outputName).getCounts());
        }        
    }

    public void nextevent(String pJob, long pPID)
    {
        Logger.getI().workflow(zenID,Logger.INFO,"next-event job=\"" + pJob + "\" pid=\"" + pPID + "\" ");

            initWorkflowJobs(); 

//workflow jobjainak bejarasa
            Enumeration<Job> enmj = jobs.elements();
            while(enmj.hasMoreElements()) {
                Job tmpJob = enmj.nextElement();
                //
                if (tmpJob.getCount() > 0) {
                    // ha be van mar initalva a job csak
                    // akor megyunk tovabb a vizsgalatra...
//inputok bejarasa                
                    Enumeration<Input> enmi = tmpJob.getInputs().elements();
                    while(enmi.hasMoreElements())
                    {
                        Input tmpInput = enmi.nextElement();
                        if(pJob.equals(tmpInput.getPreJob()))
                        {
                            // ez lefut ha van olyan job aminek a most lefutott
                            // (pJob) ez egyik input portjanak prejobja
                            if ((getJob(pJob).getOutput(tmpInput.getPreOutput()).getMainCount() > 1)) {
                                // ha az output portja generator (yes)
                                generatorOutput(pJob, pPID, tmpJob, tmpInput);
                            }
                            else runAlgorithmAction(pJob, pPID, tmpJob, tmpInput);
                            
                        }
                    }
                }
            }
    }

    /**
     * Generator csatora output eseten elvegzendo feladatok
     * @param pEndJobName Befejezodot JOB neve
     * @param pEndJobPid Befejezodott JOB PID-je
     * @param pActualJob Aktualis JOB (ennek a pre jobja)
     * @param pActualInput Aktualis Input (ennek a pre inputja)
     */
    private void generatorOutput(String pEndJobName, long pEndJobPid, Job pActualJob, Input pActualInput)
    {
        Logger.getI().workflow(zenID,Logger.INFO,"generator-output job=\"" + pEndJobName + "\" pid=\"" + pEndJobPid + "\" ");
        long allCNT=Base.getZenRunner(zenID).getJob(pEndJobName).getCount(); //futtathato peldanyok
        long actualCNT=WorkHandler.getI().getInstancePool().getInstanceCount(zenID, pEndJobName);
        Logger.getI().workflow(zenID, Logger.INFO, "generatorOutput endJob=\""+pEndJobName+"\" endPid=\""+pEndJobPid+" runJob=\""+pActualJob.getName()+" \" runInput=\""+pActualInput.getName()+"\"");
        Logger.getI().workflow(zenID, Logger.INFO, "generatorOutput all=\""+allCNT+"\"  acl=\""+actualCNT+" \" ");
        if(allCNT==(actualCNT)){
            int statuss=-1;
            boolean finishAllInstances=true;
            for(int i=0;i<allCNT;i++){
                if(WorkHandler.getI().getInstancePool().getPSInstance(zenID, pEndJobName, i)!=null){
                    statuss=WorkHandler.getI().getInstancePool().getPSInstance(zenID, pEndJobName, i).getStatus();
                    if(!Status.isEndStatus(statuss)) finishAllInstances=false;
                }
            }

        Logger.getI().workflow(zenID, Logger.INFO, "generatorOutput finish=\""+finishAllInstances+"\"  ");
// minden peldanya a generator jobnak lefutott
            if(finishAllInstances){
                long outCNT=WorkHandler.getI().getInstancePool().getOutputCounts(zenID, pEndJobName, pActualInput.getPreOutput());
                Base.getZenRunner(zenID).getJob(pEndJobName).getOutput(pActualInput.getPreOutput()).setAttribute("realCount", ""+outCNT);
                Base.getZenRunner(zenID).getJob(pEndJobName).getOutput(pActualInput.getPreOutput()).setAttribute("count", ""+outCNT);

                if (isNotBigWorkflow()) {
                    if(pActualJob.getCount()==0) return;
                    if("one".equals(pActualInput.getWaiting())){
                        for(int i=0;i<pActualJob.getCount();i++){
                            long inputIndexForPID=pActualJob.getInputIndexForPID(pActualInput.getName(), i);
                            Logger.getI().workflow(zenID, Logger.INFO, "get-output-index job=\""+pEndJobName+"\" output=\""+pActualInput.getPreOutput()+"\" index=\""+inputIndexForPID+"\"");
                            Hashtable<Long,Long> outI=WorkHandler.getI().getInstancePool().getPSInstanceInPreJobInstance(zenID, pEndJobName, pActualInput.getPreOutput(),inputIndexForPID);
                            if(outI.size()>0){
                                long outPid=outI.keys().nextElement().longValue();
                                long outIndex=outI.elements().nextElement().longValue();
                                WorkHandler.getI().getAlgorithm().action(pEndJobName,outPid,pActualInput.getPreOutput(),outIndex,pActualInput.getName(),zenID,pActualJob.getName());
                            }
                        }
                    }
                    else runAlgorithmAction(pEndJobName, pEndJobPid, pActualJob, pActualInput);
                }//isNotBigWorkflow
            }
        }
    }
    
    private void runAlgorithmAction(String pEndJobName, long pEndJobPid, Job pActualJob, Input pActualInput)
    {
        if(isNotBigWorkflow())
                WorkHandler.getI().getAlgorithm().action(pEndJobName,pEndJobPid,pActualInput.getPreOutput(),0,pActualInput.getName(),zenID,pActualJob.getName());
    }
    

    
    /**
     * Indito jobok legeneralasa
     */
    private void firsRun() throws Exception
    {
      
        Enumeration<Job> enm;
        Enumeration<Input> enmi;
        Job tmp;
        Input itmp;
//workflow jobjainak bejarasa
        enm=jobs.elements();
        while(enm.hasMoreElements())
        {
            tmp=enm.nextElement();
//inputok bejarasa
            enmi=tmp.getInputs().elements();
            boolean runall=true;
            while(enmi.hasMoreElements()&&runall)
            {
                itmp=enmi.nextElement();
                if(itmp.getPreJob()!=null)runall=false;
            }
// job futtathato rogton
            if(runall)
            {
                for(int i=0;i<tmp.getCount();i++)
                {
                    WorkHandler.getI().getInstancePool().createJobInstance(zenID, tmp.getName(), i);
                    RunableInstanceBean rbi=new RunableInstanceBean();
                    rbi.setJobName(tmp.getName());
                    rbi.setPid(i);
                    rbi.setWf(this.getZenID());
                    addOutputPoolIfNoRescue(rbi);
                }
            }
        }
    }
    
    public synchronized void addOutputPoolIfNoRescue(RunableInstanceBean jt) throws Exception
    {
        if(aborting) return;
        boolean runjob = true;
        if (rescue != null && !aborting){
            if (rescue.get(jt.getJobName())!=null){
                Vector<Rescue> tmpRescue = rescue.get(jt.getJobName());
                Hashtable outputs = new Hashtable();
                for (int ir=0;(ir<tmpRescue.size())&&runjob;ir++) {
                    if (tmpRescue.get(ir).getPid()==jt.getPid()) {
                        runjob=false;
                        outputs=tmpRescue.get(ir).getOutputs();
                    }
                }
                if(!runjob){
                // rescue, resume eseten
                        if (WorkHandler.getI().getInstancePool().getPSInstance(zenID, jt.getJobName(), (int)jt.getPid()) == null) {
//                            WorkHandler.getI().getInstancePool().succesfullInput(zenID, jt.getJob().getName(), (int)jt.getPid(), "", 0, 0);
                            WorkHandler.getI().getInstancePool().createJobInstance(zenID, jt.getJobName(), (int)jt.getPid());
                        }
                        WorkHandler.getI().getInstancePool().getPSInstance(zenID, jt.getJobName(), (int)jt.getPid()).setStatus(4);
                        Logger.getI().workflow(zenID, Logger.INFO, "rescue instance=\""+zenID+"\" job=\""+jt.getJobName()+"\" pid=\""+jt.getPid()+"\" status=\"6\"");

                        JobStatusBean pData = new JobStatusBean(zenID, jt.getJobName(), (int)jt.getPid(), Status.FINISH, "rescue", -1);
                        pData.setPortalID(getWorkflowData().getPortalID());
                        pData.setUserID(getWorkflowData().getUserID());
                        pData.setWorkflowID(getWorkflowData().getWorkflowID());
                        pData.setWorkflowStatus("5");
                        pData.setOutputs(outputs);
                        WorkHandler.getI().getIncomingStatusPool().addNewStatusInformation(pData);                
                }
            }
        }//(rescue != null)

        if(runjob&& !aborting){
            Logger.getI().workflow(zenID, Logger.INFO, "workflow status=\""+status+"\"");
            WorkHandler.getI().getInstancePool().getPSInstance(zenID, jt.getJobName(), jt.getPid()).setStatus(4);
            WorkHandler.getI().getOutPool().addJob(zenID, jt);
        }
    }

    /**
     * Csatorna inputok darabszamanak meghatarozasa
     * @param pValue job
     * @return true=nincs nem lefutott generator csatorna, PS ter legeneralhato, elerheto peldanyok submitalhatoak
     */
    private boolean setJobInputCount(Job pValue){
        boolean res = true; //return
        Vector<JobStatusBean> notFinish=new Vector<JobStatusBean>(); //prejob is error
        Job tJob; //prejob
        long preJobCount=0; //number of pre job's output

        Enumeration<Input> enm=pValue.getInputs().elements();
        while(enm.hasMoreElements()){
            Input tmpInput = enm.nextElement();
            if(tmpInput.getPreJob()!=null){
                tJob=getJob(tmpInput.getPreJob());
                long preOutputMaincount = tJob.getOutput(tmpInput.getPreOutput()).getMainCount();
                if (preOutputMaincount > 1) {
                    int preJobMaxCount = tJob.getCount();
                    int preJobFinishedCnt = 0;
                    int instanceStatus;
                    for (int pjPid = 0; pjPid < preJobMaxCount; pjPid++) {
                        if (WorkHandler.getI().getInstancePool().getPSInstance(zenID, tmpInput.getPreJob(), pjPid) != null) {
                            instanceStatus=WorkHandler.getI().getInstancePool().getPSInstance(zenID, tmpInput.getPreJob(), pjPid).getStatus();
                            if (Status.isEndStatus(instanceStatus)) preJobFinishedCnt++;
                        }
                    }
                    if (preJobFinishedCnt < preJobMaxCount) return false;
                    preJobCount = tJob.getOutput(tmpInput.getPreOutput()).getRealCount();
                }
                else{
                    preJobCount = tJob.getCount();
                    if(preJobCount>0){
                        PSInstance tmp;
                        for(int i=0;i<preJobCount;i++){
                            tmp=WorkHandler.getI().getInstancePool().getPSInstance(zenID, tmpInput.getPreJob(), i);
                            if(tmp!=null)
                            if(Status.isEndStatusNotFinished(tmp.getStatus())) {
                                JobStatusBean jb=new JobStatusBean();
                                jb.setWrtID(zenID);
                                jb.setWorkflowID(workflowData.getWorkflowID());
                                jb.setJobID(tmpInput.getPreJob());
                                jb.setPID(i);
                                jb.setStatus(tmp.getStatus());
                                notFinish.add(jb);
                            }
                        }
                    }
                }
                if (preJobCount == 0) res = false;
                else tmpInput.setAttribute("count", "" + preJobCount);
            }
        }
        if(res){
            pValue.jobRunningCount(zenID);
            Logger.getI().workflow(zenID, Logger.INFO, "start-init-job job=\""+pValue.getName()+"\"");
            for(JobStatusBean t: notFinish){
                WorkHandler.getI().getIncomingStatusPool().setNoRuning(t);
            }
            Logger.getI().workflow(zenID, Logger.INFO, "stop-init-job job=\""+pValue.getName()+"\"");
        }
        return res;
    }

    /**
     * Workflow jobjainak initalasa
     * futtatando job peldanyok
     * kiszamotasa, kesobb aktualizalasa...
     */
    private void initWorkflowJobs(){
        boolean generateStatus=true;
        Vector<Rescue> tmpRescue;
        Enumeration<Job> enm;
        long oldjobnumber = jobnumber;
        int index=0;
        Job tmp;

        while(generateStatus){
            index++;
            generateStatus=false;
            enm=jobs.elements();
            jobnumber=0;
            while(enm.hasMoreElements()){
                tmp=enm.nextElement();
                if(tmp.getCount()==0){
                    if(setJobInputCount(tmp)){
                        generateStatus=true;
                        if("true".equals(PropertyLoader.getInstance().getProperty("guse.wfi.zen.sendinitstatus"))){
                            tmpRescue=rescue.get(tmp.getName());
                            boolean noRescue=true;
                            for(int i=0;i<tmp.getCount();i++){
                                noRescue=true;
                                if(tmpRescue!=null){
                                    for(int j=0;(j<tmpRescue.size())&&noRescue;j++)
                                        if(tmpRescue.get(j).getPid()==i) noRescue=false;
                                }
                                if(noRescue){
                                    JobStatusBean tStatus=new JobStatusBean();
                                    tStatus.setPortalID(workflowData.getPortalID());
                                    tStatus.setUserID(workflowData.getUserID());
                                    tStatus.setWorkflowID(workflowData.getWorkflowID());
                                    tStatus.setWrtID(zenID);
                                    tStatus.setJobID(tmp.getName());
                                    tStatus.setPID(i);
                                    tStatus.setTim(-1);
                                    tStatus.setResource("guse-wfi");
                                    tStatus.setStatus(1);
                                    tStatus.setWorkflowStatus("5");
                                    WorkHandler.getI().getIncomingStatusPool().addNewStatusInformation(tStatus);
                                }
                            }
                        }
                    }
                }
                jobnumber+=tmp.getCount(); 
            }
        }
        if (isBigWorkflow()) {
            if (oldjobnumber > 0) {
                Base.getI().decJobCount(oldjobnumber);
                try{abort();}
                catch(Exception e){e.printStackTrace();}
            }
            handleBigWorkflow();
        } else {
            if (oldjobnumber > 0) {
                long plusJobCount = jobnumber - oldjobnumber;
                if (plusJobCount > 0) {
                    Base.getI().incJobCount(plusJobCount);
                }
            }
        }
    }

    private boolean isBigWorkflow() {
        return Base.getI().isBigWorkflow(jobnumber,workflowData);
    }

    private boolean isNotBigWorkflow(){
        return !Base.getI().isBigWorkflow(jobnumber,workflowData);
    }

    /**
     * Ez a workflow az aktualis wfi job max
     * korlat beallitasok mellett nem tud
     * lefutni azaz "big workflow"
     * nak minosul...
     */
    private void handleBigWorkflow() {
        String logg="bigworkflow jobnumber=\""+jobnumber+"\" maxjob=\""+Base.getI().getMaxJobs()+"\" ";
        Logger.getI().workflow(zenID,Logger.INFO,logg);
        // workflow delete form wfi...
        try {Base.deleteWorkflow(zenID);}
        catch (Exception e) {e.printStackTrace();}
        try {
            Hashtable sUrl = new Hashtable();
            sUrl.put("url", workflowData.getPortalID());
            ServiceType st = InformationBase.getI().getService("portal", "wfs", sUrl, new Vector());
            WfsPortalClient pc = (WfsPortalClient) Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            JobStatusBean statBean = new JobStatusBean();
            statBean.setPortalID(workflowData.getPortalID());
            statBean.setUserID(workflowData.getUserID());
            statBean.setWorkflowID(workflowData.getWorkflowID());
            statBean.setWrtID(zenID);
            // too many job in the workflow (status 37 = big workflow)
            statBean.setWorkflowStatus("37");
            statBean.setResource("");
            statBean.setJobID("");
            statBean.setStatus(0);
            statBean.setPID(0);
            statBean.setTim(0);
            pc.setStatus(statBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        String msg = "Big workflow not supported jobnum=" + jobnumber;
//        System.out.println(msg);
    }

    private boolean isNotAbortedZenRunner() {
        if (Base.getZenRunner(zenID) != null) {
            return true;
        }
        return false;
    }

}
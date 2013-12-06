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
 * Memoriaban tarolo submitalas kezelo
 */
package hu.sztaki.lpds.wfi.zen.pools;

import dci.extension.ExtensionType;
import hu.sztaki.lpds.dcibridge.client.SubmitterFace;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.logging.Logger;
import hu.sztaki.lpds.submitter.com.JobIOBean;
import hu.sztaki.lpds.submitter.com.JobRuntime;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.wfi.service.zen.Base;
import hu.sztaki.lpds.wfi.service.zen.Runner;
import hu.sztaki.lpds.wfi.service.zen.data.IOInherited;
import hu.sztaki.lpds.wfi.service.zen.data.JobInstanceRunner;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Input;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Job;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Output;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInputBean;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstance;
import hu.sztaki.lpds.wfi.util.IfException;
import hu.sztaki.lpds.wfi.util.JobConfig;
import hu.sztaki.lpds.wfi.util.JobConfigXMLParser;
import hu.sztaki.lpds.wfi.zen.pools.inf.*;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.wfs.inf.WfiWfsClient;
import hu.sztaki.lpds.wfs.utils.Status;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityDocumentType;
import org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.EnvironmentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.FileNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.GroupNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.LimitsType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.UserNameType;
import uri.mbschedulingdescriptionlanguage.SDLType;

/**
 * @author krisztian karoczkai
 */
public class RamSubmitPoolImpl extends Thread {

    BlockingQueue<RunableInstanceBean> submiting=new LinkedBlockingQueue<RunableInstanceBean>();
    ConcurrentHashMap<String,RunableInstanceBean> running=new ConcurrentHashMap<String, RunableInstanceBean>();

    public RamSubmitPoolImpl() {start();}

    public ConcurrentHashMap<String, RunableInstanceBean> getRunning() {
        return running;
    }

    public BlockingQueue<RunableInstanceBean> getSubmiting() {
        return submiting;
    }

   
    public synchronized  void addJob(String pZenID, RunableInstanceBean pJob) {

        pJob.getWf().incWorkingsubmit();
        pJob.getJob().getInstances().put(pJob.getPid(), new JobInstanceReferenceBean());
        String grid ="bes";

        String logg = "newrunnablejob job=\"" + pJob.getJobName() + "\" pid=\"" + pJob.getPid() + "\" grid=\"" + grid + "\" all=\"" + Base.getZenRunner(pZenID).getJob(pJob.getJobName()).getCount() + "\" poolsize=\"" + WorkHandler.getI().getInstancePool().getManagedInstance(pZenID) + "\"";
        Logger.getI().workflow(pZenID, Logger.EVERYTHING, logg);

//embed wf
        if(pJob.getJob().getParent()!=null){
            grid="wfi";
            WorkflowRuntimeBean thisWFData=pJob.getWf().getWorkflowData();
            WorkflowRuntimeBean newWFData= new WorkflowRuntimeBean();
            newWFData.setPortalID(thisWFData.getPortalID());
            newWFData.setUserID(thisWFData.getUserID());
            newWFData.setWorkflowID(pJob.getJob().getParent());
            newWFData.setWfsID(thisWFData.getWfsID());
            newWFData.setStorageID(thisWFData.getStorageID());
            newWFData.setWorkflowType("zen");
            newWFData.setInstanceText("embed["+thisWFData.getWorkflowID()+" - ("+thisWFData.getInstanceText()+")]");
            Enumeration<String> portEnm=pJob.getJob().getInputs().keys();
            String portName,ijobName,iportName;
            Hashtable<String,IOInherited> inputs=new Hashtable<String, IOInherited>();
            String embedInputKey="",newEmbedInputKey="";
            while(portEnm.hasMoreElements()){
                portName=portEnm.nextElement();
                ijobName=pJob.getJob().getInput(portName).getIjob();
                iportName=pJob.getJob().getInput(portName).getIinput();
                if( ijobName!=null && iportName!=null){
                    IOInherited input=new IOInherited();
                    input.setWorkflow(thisWFData.getWorkflowID());
                    input.setWorkflowRID(pJob.getWfID());
                    input.setJob(pJob.getJob().getName());
                    input.setPID(""+pJob.getJob().getInputIndexForPID(portName, pJob.getPid()));
                    input.setPort(portName);
                    input.setSeq(pJob.getJob().getInput(portName).getSeq());
                    input.setIndex("-1");

                    if(null!=pJob.getJob().getInput(portName).getPreJob())
                    {
                        String prejob=pJob.getJob().getInput(portName).getPreJob();
                        String preoutput=pJob.getJob().getInput(portName).getPreOutput();
//                        if(pJob.getWf().getJob(prejob).getOutput(preoutput).getMainCount()>1)
                        {
                            PSInstance psInstance= WorkHandler.getI().getAlgorithm().createNewPSInstance(pJob.getWfID(),pJob.getJobName(),pJob.getPid(),true);
                            Hashtable<String,PSInputBean> tims=psInstance.getAllInput();
                            input.setPID(""+tims.get(portName).getPid());
                            input.setIndex(""+tims.get(portName).getIndex());
                        }
                    }
                    newEmbedInputKey=input.getJob()+"/"+input.getPort();
                    embedInputKey=pJob.getJob().getInput(portName).getIjob()+"/"+pJob.getJob().getInput(portName).getIinput();
                    if(pJob.getWf().getParentInput().get(newEmbedInputKey)==null) inputs.put(embedInputKey, input);
                    else inputs.put(embedInputKey, (IOInherited)pJob.getWf().getParentInput().get(newEmbedInputKey));
                }
            }
            Hashtable<String,IOInherited> outputs=new Hashtable<String, IOInherited>();
            portEnm=pJob.getJob().getOutputs().keys();
            while(portEnm.hasMoreElements()){
                portName=portEnm.nextElement();
                ijobName=pJob.getJob().getOutput(portName).getIJob();
                iportName=pJob.getJob().getOutput(portName).getIOutput();
               if( ijobName!=null && iportName!=null){
                    IOInherited output=new IOInherited();
                    output.setWorkflow(thisWFData.getWorkflowID());
                    output.setWorkflowRID(pJob.getWfID());
                    output.setJob(pJob.getJob().getName());
                    output.setPID(""+pJob.getPid());
                    output.setPort(portName);
                    outputs.put(pJob.getJob().getOutput(portName).getIJob()+"/"+pJob.getJob().getOutput(portName).getIOutput(), output);
                }
            }
//            Base.getI().addZenRunnerAndRun(newWFData, pJob.getWfID(),pJob,inputs,outputs);
            Base.getI().addZenRunner(newWFData, pJob.getWfID(),pJob,inputs,outputs);
            pJob.getWf().decWorkingsubmit();

            logg = "submitedjob job=\"" + pJob.getJobName() + "\" pid=\"" + pJob.getPid() + "\" grid=\"" + grid + "\" all=\"" + Base.getZenRunner(pZenID).getJob(pJob.getJobName()).getCount() + "\" poolsize=\"" + WorkHandler.getI().getInstancePool().getManagedInstance(pZenID) + "\"";
            Logger.getI().workflow(pZenID, Logger.EVERYTHING, logg);

        }
        else {
            //submiting.add(pJob);
            pJob.getWf().getSubmitingQueue().add(pJob);


        }

    }

    public RunableInstanceBean getRunningJob(String pID){return running.get(pID);}


    private JobRuntime createJobRuntime(RunableInstanceBean rbi){
        JobRuntime jr = new JobRuntime();
        WorkflowRuntimeBean pWorkflowData = rbi.getWf().getWorkflowData();
        jr.setJobID(rbi.getJobName());
        jr.setPID((int) rbi.getPid());
        jr.setWorkflowRuntimeID(rbi.getWfID());
        jr.setWfiID(PropertyLoader.getInstance().getProperty("service.url"));
        jr.setPortalID(pWorkflowData.getPortalID());
        jr.setUserID(pWorkflowData.getUserID());
        jr.setStorageID(pWorkflowData.getStorageID());
        jr.setWfsID(pWorkflowData.getWfsID());
        jr.setWorkflowID(pWorkflowData.getWorkflowID());
        PSInstance ps = WorkHandler.getI().getInstancePool().getPSInstance(rbi.getWfID(), jr.getJobID(), jr.getPID());
        jr.setInputsCount(ps.getAllInput());
        String logg = "runningpool grid=\"bes\" job=\"" + jr.getJobID() + "\" pid=\"" + jr.getPID() + "\" alcount=\"\" ";
        Logger.getI().workflow(rbi.getWfID(), Logger.EVERYTHING, logg);
        return jr;
    }


/**
 * Job Adatok lekerdezes a WFS-tol
 * @param jobData Futtathato job leiro
 * @return JobConfiguracio
 * @throws java.lang.Exception kommunikacis vagy adatfeldolgozasi hiba
 */
    private JobConfig getWFSData(JobRuntime jobData) throws Exception{
        if(Base.getZenRunner(jobData.getWorkflowRuntimeID()).getJob(jobData.getJobID()).getJc()!=null)
            return  Base.getZenRunner(jobData.getWorkflowRuntimeID()).getJob(jobData.getJobID()).getJc();

        ComDataBean cmd=new ComDataBean();
        cmd.setPortalID(jobData.getPortalID());
        cmd.setUserID(jobData.getUserID());
        cmd.setWorkflowID(jobData.getWorkflowID());
        cmd.setJobID(jobData.getJobID());

        WfiWfsClient clientWFS=(WfiWfsClient)InformationBase.getI().getServiceClient("wfs", "wfi");
        String jxml=clientWFS.getSubmitData(cmd);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        JobConfigXMLParser handler=new JobConfigXMLParser();
//        System.out.println(jxml);
        saxParser.parse(new ByteArrayInputStream(jxml.getBytes()), handler);
        JobConfig jc=handler.getJobConfig();
        Base.getZenRunner(jobData.getWorkflowRuntimeID()).getJob(jobData.getJobID()).setJc(jc);

        return jc;
    }



    private static Class[] classes=new Class[]{
        JobDefinitionType.class,UserNameType.class,GroupNameType.class,FileNameType.class,
        ArgumentType.class,LimitsType.class,EnvironmentType.class,POSIXApplicationType.class,
        ExtensionType.class,SDLType.class};

    private  JobDefinitionType readJSDLFromString(String pValue) throws JAXBException, FileNotFoundException{
        JAXBContext jc= JAXBContext.newInstance(classes);

	    Unmarshaller u = jc.createUnmarshaller();
	    JAXBElement<JobDefinitionType> obj=u.unmarshal( new StreamSource(new StringReader(pValue)), JobDefinitionType.class );
        JobDefinitionType jsdl = (JobDefinitionType) obj.getValue();

        return jsdl;
	}


    public static String getJSDLXML(JobDefinitionType pValue) throws Exception{
        ByteArrayOutputStream res=new ByteArrayOutputStream();
        try{//
            JAXBContext jc = JAXBContext.newInstance(classes);
            Marshaller msh=jc.createMarshaller();
            JAXBElement<JobDefinitionType> jbx = wrap( "http://schemas.ggf.org/jsdl/2005/11/jsdl", "JobDefinition_Type",  pValue);
            msh.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            msh.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://schemas.ggf.org/jsdl/2005/11/jsdl");
            msh.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            msh.marshal(jbx,res);
        }
        catch(Exception e){e.printStackTrace();}
        return new String(res.toByteArray());
    }

    private static <T> JAXBElement<T> wrap( String ns, String tag, T o ){
        QName qtag = new QName( ns, tag,"jsdl" );
        Class<?> clazz = o.getClass();
        @SuppressWarnings( "unchecked" )
        JAXBElement<T> jbe = new JAXBElement( qtag, clazz, o );
        return jbe;
    }




  

    @Override
    public void run() {
        setPriority(MIN_PRIORITY);
        System.out.println("thread start:"+this.getClass().getName());
        SubmitterFace client=null;
        try{client=(SubmitterFace)InformationBase.getI().getServiceClient("submitter", "wfi");}
        catch(Exception e){e.printStackTrace();}

        RunableInstanceBean submitingJob=null;
        JobRuntime jr;
        JobConfig jc;
        Enumeration<String> wfridEnm;
        Runner wf;
        boolean submitFlag=false;
        while (Base.getI().applicationrun) {
            wfridEnm=Base.getI().getAllWorkflowInstance().keys();
            submitFlag=false;
            while(wfridEnm.hasMoreElements()){
                wf=Base.getZenRunner(wfridEnm.nextElement());                
                try{
                if(wf.getSubmitingQueue().size()>0){
                    submitingJob=wf.getSubmitingQueue().remove(0);
                    submitFlag=true;
                    try{
                        jr=setInputsAndOutputs(createJobRuntime(submitingJob));
                        jc=getWFSData(jr);
                        jc.setJobData(jr);
                        jc.init();
                        submitingJob.getJob().setJc(jc);

                        CreateActivityType submit=new CreateActivityType();
                        submit.setActivityDocument(new ActivityDocumentType());

                        JobDefinitionType jsdl = readJSDLFromString(getJSDLXML(submitingJob.getJob().getJc().toXML())); // FIXME !!!

                        //System.out.println(getJSDLXML(jsdl));
                        
                        submit.getActivityDocument().setJobDefinition(jsdl);
                        CreateActivityResponseType rep=client.createActivity(submit);
                        int j0=rep.getActivityIdentifier().toString().indexOf("<job ")+5;
                        j0=rep.getActivityIdentifier().toString().indexOf(">",j0)+1;
                        int j1=rep.getActivityIdentifier().toString().indexOf("</job>");
                        String besID=rep.getActivityIdentifier().toString().substring(j0,j1);
                        running.put(besID, submitingJob);
                        submitingJob.getJob().getInstances().get(submitingJob.getPid()).setJobid(rep.getActivityIdentifier());
                        eventAdministration(submitingJob);
                    }
                    catch(IfException eJSDL){
                        JobStatusBean newStatus=new JobStatusBean();
                        newStatus.setWorkflowID(submitingJob.getWf().getWorkflowData().getWorkflowID());
                        newStatus.setWrtID(submitingJob.getWfID());
                        newStatus.setJobID(submitingJob.getJobName());
                        newStatus.setPID(submitingJob.getPid());
                        newStatus.setResource("gUSE WFI");
                        newStatus.setTim(System.currentTimeMillis());
                        newStatus.setStatus(Status.FALSEINPUT);
                        eventAdministration(submitingJob);
                        WorkHandler.getI().getIncomingStatusPool().addNewStatusInformation(newStatus);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        JobStatusBean newStatus=new JobStatusBean();
                        newStatus.setWorkflowID(submitingJob.getWf().getWorkflowData().getWorkflowID());
                        newStatus.setWrtID(submitingJob.getWfID());
                        newStatus.setJobID(submitingJob.getJobName());
                        newStatus.setPID(submitingJob.getPid());
                        newStatus.setResource("DCI-BRIDGE ERROR");
                        newStatus.setTim(System.currentTimeMillis());
                        newStatus.setStatus(Status.ERROR);
                        eventAdministration(submitingJob);
                        WorkHandler.getI().getIncomingStatusPool().addNewStatusInformation(newStatus);
                    }
                }//if
                }
                catch(Exception e){/*wf end*/}
                if(!submitFlag){
//                    System.out.println("sleeping submit");
                    try{sleep(10000);}
                    catch(InterruptedException e){}
                }
            }//while(wfridEnm.hasMoreElements())
        }//while
        System.out.println("thread stop:"+this.getClass().getName());
    }

    private void eventAdministration(RunableInstanceBean submitingJob){
        submitingJob.getWf().decWorkingsubmit();
        String logg = "submitedjob job=\"" + submitingJob.getJobName() + "\" pid=\"" + submitingJob.getPid() + "\"";
        Logger.getI().workflow(submitingJob.getWfID(), Logger.EVERYTHING, logg);

    }



    private JobRuntime setInputsAndOutputs(JobRuntime tmp){
       PSInstance psInstance= WorkHandler.getI().getAlgorithm().createNewPSInstance(tmp.getWorkflowRuntimeID(), tmp.getJobID(), tmp.getPID(), true);

        String workflowRuntimeID=tmp.getWorkflowRuntimeID();
        WorkflowRuntimeBean workflowData=Base.getZenRunner(tmp.getWorkflowRuntimeID()).getWorkflowData();
        Enumeration<String> enm,enm0;
        String key,key0;
        Input tmpi;
        Output tmpo;
        {
                Hashtable inputs=new Hashtable();
                Hashtable outputs=new Hashtable();
                Job jobData=Base.getZenRunner(tmp.getWorkflowRuntimeID()).getJob(tmp.getJobID());
//altualis inputok meghatarozasa
            enm=jobData.getInputs().keys();
            while(enm.hasMoreElements())
            {
                key=enm.nextElement();
                tmpi=jobData.getInput(key);

                if(tmpi.getPreJob()==null) //nem csatorna file
                {
                    if(tmpi.getRworkflow()==null)
                    {

                        tmpi.setAttribute("rworkflow",tmp.getWorkflowID());
                        tmpi.setAttribute("rseq",tmpi.getSeq());
                        tmpi.setAttribute("rjob",""+tmp.getJobID());
                    }
                }
                else //csatorna
                {
                            tmpi.setAttribute("rworkflow",tmp.getWorkflowID());
                            tmpi.setAttribute("rworkflowrid",""+tmp.getWorkflowRuntimeID());
                            tmpi.setAttribute("rjob",""+tmpi.getPreJob());
                            tmpi.setAttribute("rport",""+tmpi.getPreOutput());
                }
            }

//binaris futtatasa
            if(jobData.getParent()==null)
            {
                enm=jobData.getInputs().keys();
                while(enm.hasMoreElements())
                {
                    key=enm.nextElement();
                    tmpi=jobData.getInput(key);
                    if(tmpi.getRworkflowRID()==null)
                    {
                        try
                        {
                            IOInherited tmpioin=null;
                            if(!Base.getZenRunner(tmp.getWorkflowRuntimeID()).getParentInput().isEmpty())
                            {
                                 tmpioin=getParentInput(tmp.getWorkflowRuntimeID(),tmp.getJobID(),tmpi.getName());
                            }
                            if(tmpioin!=null)
                            {
                                String strategi="one";
                                try{strategi=Base.getZenRunner(tmp.getWorkflowRuntimeID()).getJob(tmpioin.getJob()).getInput(tmpioin.getPort()).getWaiting();}
                                catch(Exception e)
                                {
                                    System.out.println("EMBED IO ERROR ");
                                    System.out.println("-"+tmp.getWorkflowID()+"/"+tmp.getWorkflowRuntimeID());
                                    System.out.println("-"+tmp.getJobID()+"/"+tmpi.getName());
                                    System.out.println("-"+tmpioin.getJob());
                                    System.out.println("-"+tmpioin.getJob());
                                    e.printStackTrace();
                                }
                                if("one".equals(strategi))
                                {
//                                    inputs.put(key,new JobIOBean(tmpi.getRworkflow(),tmpi.getRjob(),tmpi.getRSeq(),(String)key,tmpioin.getPID(),"-1"));
                                    String wn=Base.getZenRunner(tmpioin.getWorkflowRID()).getWorkflowData().getWorkflowID();
                                    inputs.put(key,new JobIOBean(wn,tmpioin.getJob(),tmpioin.getSeq(),(String)key,tmpioin.getPID(),"-1"));
                                }
                                else
                                {
                                    long cnt=Base.getZenRunner(tmp.getWorkflowRuntimeID()).getJob(tmpioin.getJob()).getInput(tmpioin.getPort()).getCount();
                                    for(int icnt=0;icnt<cnt;icnt++)
                                    inputs.put(key+"_"+icnt,new JobIOBean(tmpi.getRworkflow(),tmpi.getRjob(),tmpi.getRSeq(),(String)key,""+icnt,"-1"));
                                }
                            }
                            else //nincs beagyazas
                            {
                                 Hashtable<String,PSInputBean> tims=psInstance.getAllInput();
                                inputs.put(key,new JobIOBean(tmpi.getRworkflow(),tmpi.getRjob(),tmpi.getRSeq(),(String)key,""+tims.get(tmpi.getName()).getPid(),""+tims.get(tmpi.getName()).getIndex()));
                            }
                        }
                        catch(Exception er){er.printStackTrace();}
                    }
                    else//csatorna
                    {
                        IOInherited tmpioin=null;
                        if(Base.getZenRunner(tmp.getWorkflowRuntimeID()).getParentZenID()!=null)
                            tmpioin=getParentInput(tmp.getWorkflowRuntimeID(),tmp.getJobID(),tmpi.getName());
                        //csatornaba agyazott wf job
//beagyazott csatorna**()**
                        if(tmpioin!=null)
                        {
                                 String wn=Base.getZenRunner(tmpioin.getWorkflowRID()).getWorkflowData().getWorkflowID();
                            String strategi=Base.getZenRunner(tmpioin.getWorkflowRID()).getJob(tmpioin.getJob()).getInput(tmpioin.getPort()).getWaiting();
                            if("one".equals(strategi)) //atmeno
                            {
                                PSInstance psiEm=WorkHandler.getI().getInstancePool().getPSInstance(tmpioin.getWorkflowRID(), tmpioin.getJob(),Long.parseLong(tmpioin.getPID()));
                                String prejob=Base.getZenRunner(tmpioin.getWorkflowRID()).getJob(tmpioin.getJob()).getInput(tmpioin.getPort()).getPreJob();
                                String preOutput=Base.getZenRunner(tmpioin.getWorkflowRID()).getJob(tmpioin.getJob()).getInput(tmpioin.getPort()).getPreOutput();
                                if(prejob!=null)
                                    inputs.put(key,new JobIOBean(tmpioin.getWorkflow(),tmpioin.getWorkflowRID(),prejob,tmpi.getRSeq(),preOutput,""+tmpioin.getPID(),tmpioin.getIndex()));
                                else
                                {
                                    inputs.put(key,new JobIOBean(tmpi.getRworkflow(),tmpioin.getJob(),tmpioin.getSeq(),(String)key,tmpioin.getPID(),"-1"));
                                    inputs.put(key,new JobIOBean(tmpioin.getWorkflow(),tmpioin.getJob(),tmpioin.getSeq(),(String)key,tmpioin.getPID(),"-1"));
                                }
                            }
                            else//collector
                            {
                                Hashtable parentjobs=Base.getZenRunner(tmpioin.getWorkflowRID()).getJobs();
                                String prejob=Base.getZenRunner(tmpioin.getWorkflowRID()).getJob(tmpioin.getJob()).getInput(tmpioin.getPort()).getPreJob();
                                String preOutput=Base.getZenRunner(tmpioin.getWorkflowRID()).getJob(tmpioin.getJob()).getInput(tmpioin.getPort()).getPreOutput();
                                long mco=((Job)parentjobs.get(prejob)).getOutput(preOutput).getRealCount();//MainCount();
                                long mc=mco*((Job)parentjobs.get(prejob)).getCount();
                                long rPID=Long.parseLong(tmpioin.getPID())%mco;
                                inputs.put(key,new JobIOBean(tmpioin.getWorkflow(),tmpioin.getWorkflowRID(),prejob,tmpi.getRSeq(),preOutput,""+rPID,""+mc,"-1"));
                            }
                        }
                        else //nincs beagyazas
                        {
                            if(tmpi.getWaiting().equals("one"))//atmencsatorna
                            {
                                try
                                {
                                    Hashtable<String,PSInputBean> tims=psInstance.getAllInput();
                                    inputs.put(key,new JobIOBean(tmpi.getRworkflow(),tmpi.getRworkflowRID(),tmpi.getRjob(),tmpi.getSeq(),tmpi.getRPort(),""+tims.get(tmpi.getName()).getPid(),""+tims.get(tmpi.getName()).getIndex()));
                                }
                                catch(NullPointerException e0)
                                {
                                    System.out.println("ERROR:"+tmp.getJobID()+"."+tmp.getPID());
                                    System.out.println(tmpi.getRjob()+".");
                                }
                            }
                            else//collector
                            {
                                Hashtable<String,PSInputBean> tims=psInstance.getAllInput();
                                Enumeration<String> enmTims=tims.keys();
                                String keyTims;
                                while(enmTims.hasMoreElements())
                                {
                                    keyTims=enmTims.nextElement();
                                    if(keyTims.startsWith(tmpi.getName()+"_"))
                                        inputs.put(keyTims,new JobIOBean(tmpi.getRworkflow(),tmpi.getRworkflowRID(),tmpi.getRjob(),tmpi.getSeq(),tmpi.getRPort(),""+tims.get(keyTims).getPid(),""+tims.get(keyTims).getIndex()));
                                }
                            }

                        }
                    }
                }
// bekuldendo job-ok OUTPUT logjai
                enm=jobData.getOutputs().keys();
                while(enm.hasMoreElements())
                {
                    key=enm.nextElement();
                    tmpo=jobData.getOutput(key);
//generator
//normal
                    {
                        outputs.put(new JobIOBean(tmp.getWorkflowID(),tmp.getWorkflowRuntimeID(),tmp.getJobID(),(String)key,""+tmp.getPID(),"",true,"-1"),key);
                                    Vector<JobIOBean> reqo=getParentOutputs(tmp.getWorkflowRuntimeID(),tmp.getJobID(),(String)key);
                                    for(JobIOBean treqo:reqo)  outputs.put(treqo, key);
                    }
                }

//oi jobhoz allitasa
                tmp.setInputsCount(inputs);
                tmp.setEmbedOutputs(outputs);

            }
//beagyazott workflow
            else
            {
                    WorkflowRuntimeBean wb=new WorkflowRuntimeBean();
                    wb.setWorkflowID(jobData.getParent());
                    wb.setInstanceText("called from["+workflowData.getWorkflowID()+"("+workflowData.getInstanceText()+")/"+tmp.getJobID()+"("+tmp.getPID()+")]");
                    wb.setPortalID(workflowData.getPortalID());
                    wb.setStorageID(wb.getStorageID());
                    wb.setUserID(workflowData.getUserID());
                    wb.setWfsID(workflowData.getWfsID());
                    wb.setWorkflowType("zen");
/*- itt meg be kell allitani a tobbi workflow parametert is -*/
                    JobInstanceRunner jr=new JobInstanceRunner(tmp.getJobID(),tmp.getPID());
//inputok atorokitese
                    String idx;
                    Hashtable inheritedInputs=new Hashtable();
                    enm=jobData.getInputs().keys();
                    while(enm.hasMoreElements())
                    {
                        key=enm.nextElement();
                        tmpi=jobData.getInput(key);
                        if(tmpi.getIinput()!=null)
                        {
                            idx=jobData.getInput(key).getIjob()+"/"+jobData.getInput(key).getIinput();
                            IOInherited tmp0=new IOInherited();
                            tmp0.setWorkflow(workflowData.getWorkflowID());
                            tmp0.setWorkflowRID(tmp.getWorkflowRuntimeID());
                            tmp0.setJob(tmp.getJobID());
                            tmp0.setPID(""+tmp.getPID());
                            tmp0.setIndex("-1");
                            if(null!=Base.getZenRunner(workflowRuntimeID).getJob(tmp.getJobID()).getInput(tmpi.getName()).getPreJob())
                            {
                                String prejob=Base.getZenRunner(workflowRuntimeID).getJob(tmp.getJobID()).getInput(tmpi.getName()).getPreJob();
                                String preoutput=Base.getZenRunner(workflowRuntimeID).getJob(tmp.getJobID()).getInput(tmpi.getName()).getPreOutput();
                                if(Base.getZenRunner(workflowRuntimeID).getJob(prejob).getOutput(preoutput).getMainCount()>1)
                                {
                                    Hashtable<String,PSInputBean> tims=psInstance.getAllInput();
                                    tmp0.setPID(""+tims.get(tmpi.getName()).getPid());
                                    tmp0.setIndex(""+tims.get(tmpi.getName()).getIndex());
                                }

                            }
                            tmp0.setPort(tmpi.getName());
                            tmp0.setSeq(tmpi.getSeq());
                            inheritedInputs.put(idx,tmp0);
                        }
                    }
/*- outputok atorokitese - */
                    Hashtable inheritedOutputs=new Hashtable();
                    enm=jobData.getOutputs().keys();
                    while(enm.hasMoreElements())
                    {
                        key=enm.nextElement();
                        tmpo=jobData.getOutput(key);
                        if(tmpo.getIJob()!=null)
                        {
                            IOInherited tmpio=new IOInherited();
                            tmpio.setWorkflow(workflowData.getWorkflowID());
                            tmpio.setWorkflowRID(workflowRuntimeID);
                            tmpio.setJob(tmp.getJobID());
                            tmpio.setPort(""+key);
                            tmpio.setPID(""+tmp.getPID());
                            inheritedOutputs.put(tmpo.getIJob()+"/"+tmpo.getIOutput(),tmpio);
                        }
                    }
//Vegrehajtas
//                    String ss=Base.getI().addZenRunner(wb,workflowRuntimeID,jr,inheritedInputs,inheritedOutputs);
                }
        }
        return tmp;
    }


    private Vector<JobIOBean> getParentOutputs(String pWFRID,String pJob,String pPort)
    {
        Vector<JobIOBean> res=new Vector<JobIOBean>();
        if(Base.getZenRunner(pWFRID).getParentZenID()!=null)
        {
            try
            {
                IOInherited t=Base.getZenRunner(pWFRID).getParentOutputPort(pJob, pPort);
                res.add(new JobIOBean(t.getWorkflow(),t.getWorkflowRID(),t.getJob(),t.getPort(),t.getPID(),"",true,"-1"));
                res.addAll(getParentOutputs(t.getWorkflowRID(), t.getJob(), t.getPort()));
            }
            catch(NullPointerException e){}
        }
        return res;
    }

    private IOInherited getParentInput(String pWFRID,String pJob,String pPort)
    {
        IOInherited res=null;
        if(Base.getZenRunner(pWFRID).getParentZenID()!=null){
            try{
                IOInherited t=Base.getZenRunner(pWFRID).getParentInputPort(pJob, pPort);
                res=getParentInput(Base.getZenRunner(pWFRID).getParentZenID(), t.getJob(), t.getPort());
                if(res==null) return t;
            }
            catch(NullPointerException e){/*nincs parent input*/}
        }
        return res;
    }



}

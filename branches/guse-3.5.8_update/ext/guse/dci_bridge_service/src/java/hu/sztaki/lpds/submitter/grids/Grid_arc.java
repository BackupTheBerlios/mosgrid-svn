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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.submitter.grids;

import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.util.BinaryHandler;
import hu.sztaki.lpds.dcibridge.util.InputHandler;
import hu.sztaki.lpds.dcibridge.util.LinuxWrapperForArc;
import hu.sztaki.lpds.dcibridge.util.OutputHandler;
import hu.sztaki.lpds.dcibridge.util.XMLHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import nordugrid.arc.*;

import nordugrid.arc.Software.ComparisonOperatorEnum;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import uri.mbschedulingdescriptionlanguage.JobTypeEnumeration;
import uri.mbschedulingdescriptionlanguage.SDLType;

/**
 *
 * @author akos
 */
public class Grid_arc extends Middleware {

    final static String GENERATOR ="generator.tgz";
    final static String LOCALINPUTS = "localinputs.tgz";
    final static String LOCALOUTPUTS = "localoutputs.tgz";
    final static String WRAPPER ="wrapper.sh";
    private static boolean isJarcLoaded = false;
    //private JobList joblist; -> ((JobList)pJob.getMiddlewareObj())
    private String clientconfpath;


    public Grid_arc() throws Exception{
        if (!isJarcLoaded) {
            try {
                System.loadLibrary("jarc");
            } catch (UnsatisfiedLinkError le) {
                System.out.println("" + le.getMessage());
            } 
            isJarcLoaded = true;
        }        

        System.out.println("ARC submitter has been initialized for a job...");
        THIS_MIDDLEWARE=Base.MIDDLEWARE_ARC;
         
         clientconfpath = Conf.getMiddleware(THIS_MIDDLEWARE).getItem().get(0).getArc().getConfigpath();
        
        

    }


    @Override
    protected void abort(Job pJob) throws Exception {

        String localDir = Base.getI().getJobDirectory(pJob.getId());
        UserConfig usercfg = createUserCfg(localDir);
        JobSupervisor jobsup = new JobSupervisor(usercfg,((JobList)pJob.getMiddlewareObj()));
        String jobID = pJob.getMiddlewareId();
        if (jobsup != null){
            JobControllerList jobclist = jobsup.GetJobControllers();
            for (JobControllerListIteratorHandler it = new JobControllerListIteratorHandler(jobclist.begin()); !it.equal(jobclist.end()); it.next())
            {
                JobController jobcontroller = it.pointer();
                JobList jobs = jobcontroller.GetJobs();
                JobListIteratorHandler jobit = new JobListIteratorHandler(jobs.begin());
                while (!jobit.equal(jobs.end())){
                    String jobid = jobit.pointer().getJobID().str();
                    if (jobid.equals(new String(jobID))){
                        jobcontroller.GetJobInformation();
                        JobState statusObj = jobit.pointer().getState();
                        jobcontroller.CancelJob(jobit.pointer());
                    }
                   jobit.next();
                }
            }

    }
      pJob.setStatus(ActivityStateEnumeration.CANCELLED);
    }

    private UserConfig createUserCfg(String localDir){
        UserConfig usercfg = new UserConfig();
        usercfg.ProxyPath(localDir+"/x509up");
        usercfg.JobListFile(localDir+"/job.arc");
        usercfg.LoadConfigurationFile(clientconfpath);  
        return usercfg;
    }
    
    @Override
    protected void submit(Job pJob) throws Exception {
        //Conf.getMiddleware(THIS_MIDDLEWARE).getItem().get(0).getArc().
        //resource = pJob.getConfiguredResource().getResource();                
        String localDir = Base.getI().getJobDirectory(pJob.getId());
        UserConfig usercfg = createUserCfg(localDir);
        JobDescription jobdesc = new JobDescription();
        this.createWrapper(pJob,jobdesc);

        JobDefinitionType jsdl = pJob.getJSDL();
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);

        String executable = BinaryHandler.getBinaryFileName(pType);
        String[] sJobID = localDir.split("/");

        // Setup logging.
        // Possible log-levels: FATAL, ERROR, WARNING, INFO, VERBOSE and DEBUG.
        new Logger(Logger.getRootLogger(), "arcsub");
        Logger.getRootLogger().addDestination(new LogStream_ostream(arc.getStdout()));
        Logger.getRootLogger().setThreshold(LogLevel.FATAL);

        
        // Fetch information about services and create ExecutionTargets from these (CEs).

        // Create job description.
        // Set the executable.
        ApplicationType app = new ApplicationType();
        ExecutableType et = new ExecutableType();
        et.setName("/bin/sh");
        StringList arglist = new StringList();
        arglist.add(WRAPPER);
        et.setArgument(arglist);
        
        app.setExecutable(et);
        // set standard error
        app.setError("stderr.log");

        // Set standard output file.
        app.setOutput("stdout.log");

        
        jobdesc.setApplication(app);

        FileTypeList ftl = new FileTypeList();

        // setting executable
    /*    FileType exec = new FileType();
        exec.setName(WRAPPER);
        URL url_exec = new URL(localDir+"/"+WRAPPER);
        URLList execlist = new URLList();
        execlist.add(url_exec);
        exec.setSource(execlist);
        ftl.add(exec);
*/
        // todo COMPRESSING INPUTS
        compressInputs(pJob);

        // adding compressed input tgz and wrapper as input files
        ArrayList<String> infiles = new ArrayList<String>();
        infiles.add(LOCALINPUTS);
        infiles.add(WRAPPER);

        for (String inputfilename : infiles){
            FileType ifile = new FileType();
            ifile.setName(inputfilename);
            ifile.setKeepData(true);
            URL url = new URL(localDir+"/"+inputfilename);
            URLList urllist = new URLList();
            urllist.add(url);
            ifile.setSource(urllist);
            ftl.add(ifile);
        }


        // setting outputs

        // for (S outputfilename: outfiles){
            FileType ofile = new FileType();
            ofile.setKeepData(true);
            ofile.setName(LOCALOUTPUTS);
            ftl.add(ofile);
        // }

        jobdesc.setFiles(ftl);

        // Job description created.
        ExecutionTargetList list = getExecutionTargetList(usercfg);
        if (list.size() != 0){
        
          // Get submitter object from target.
          //ExecutionTarget target = this.getExecutionTarget(resource);
            ExecutionTarget target = this.getBrokeredTarget(list, jobdesc, usercfg);
          // Try to submitting to target.
          boolean submitted = false;
          nordugrid.arc.Job job = new nordugrid.arc.Job();
          if (target != null){
              Submitter submitter = target.GetSubmitter(usercfg);
              submitted = submitter.Submit(jobdesc, target, job);              
              //joblist.add(job);
              pJob.setMiddlewareObj(new JobList());
              ((JobList)pJob.getMiddlewareObj()).add(job);
          }

          if (submitted){
                pJob.setResource(pJob.getConfiguredResource().getVo() + " broker");
                try {
                    // If successful write job id.

                    pJob.setMiddlewareId(job.getJobID().fullstr());
                    this.saveJobID(localDir, job.getJobID().fullstr());
                    
                    pJob.setStatus(ActivityStateEnumeration.PENDING);

                } catch (IOException ex) {
                   ex.printStackTrace();
                }

          }
          else {
            errorLog(Base.getI().getJobDirectory(pJob.getId()), "Unable to submit to " + target.getUrl());
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            

          }
        
        }
 else {
            errorLog(Base.getI().getJobDirectory(pJob.getId()), "no resource set! Please contact the admin");
            pJob.setStatus(ActivityStateEnumeration.FAILED);
 }
    }

    
    protected void getOutputs(Job pJob) throws Exception {
        
    }

    @Override
    protected void getStatus(Job pJob) throws Exception {
            String localDir = Base.getI().getJobDirectory(pJob.getId());
            UserConfig usercfg = createUserCfg(localDir);        
            JobSupervisor jobsup = new JobSupervisor(usercfg,((JobList)pJob.getMiddlewareObj()));//joblist
            String jobID = pJob.getMiddlewareId();
            String OutputDir = Base.getI().getJobDirectory(pJob.getId()) + "outputs/";
            if (jobsup != null){
                JobControllerList jobclist = jobsup.GetJobControllers();
                
                for (JobControllerListIteratorHandler it = new JobControllerListIteratorHandler(jobclist.begin()); !it.equal(jobclist.end()); it.next())
                {
                    JobController jobcontroller = it.pointer();
                    JobList jobs = jobcontroller.GetJobs();
                    
                    JobListIteratorHandler jobit = new JobListIteratorHandler(jobs.begin());
                    while (!jobit.equal(jobs.end())){
                         
                        String jobid = jobit.pointer().getJobID().str();
                        
                        if (jobid.equals(new String(jobID))){
                            // setting Resource

                            pJob.setResource(jobit.pointer().getCluster().Host());

                            jobcontroller.GetJobInformation();
                            JobState statusObj = jobit.pointer().getState();
                            String tmp_status = statusObj.GetGeneralState();

                        if (isFinished(tmp_status)){
                            String downloaddir = Base.getI().getJobDirectory(pJob.getId()) + "outputs";
                            this.getOutput(downloaddir, jobID,jobcontroller, tmp_status);
                            if (!extractFile(downloaddir + "/", LOCALOUTPUTS)) {
                                sysLog(OutputDir, "Could not extract outputsand: " + LOCALOUTPUTS);
                                errorLog(OutputDir, "Could not extract outputsand: " + LOCALOUTPUTS);
                                pJob.setStatus(ActivityStateEnumeration.FAILED);
                                return;
                            }
                            if (!checkOutputFiles(pJob)) {
                                pJob.setStatus(ActivityStateEnumeration.FAILED);                                
                            } else {
                                pJob.setStatus(ActivityStateEnumeration.FINISHED);
                            }                                                      
                            return;
                        }
                        else if (isSubmitted(tmp_status) || isWaiting(tmp_status) || isScheduled(tmp_status)){
                            pJob.setStatus(ActivityStateEnumeration.PENDING);

                        }
                        else if (isRunning(tmp_status)){
                            pJob.setStatus(ActivityStateEnumeration.RUNNING);
                            
                        }
                        else if (isError(tmp_status)){
                            pJob.setStatus(ActivityStateEnumeration.FAILED);
                            
                        }
                        else if (isKilled(tmp_status)){
                            pJob.setStatus(ActivityStateEnumeration.CANCELLED);
                        }

                    }
                   jobit.next();
                }
            }

    }
    else{
        errorLog(Base.getI().getJobDirectory(pJob.getId()), "Internal error: jobsupervisor does not contain the job ID");
        pJob.setStatus(ActivityStateEnumeration.FAILED);

    }
    
    }


    private boolean checkOutputFiles(Job pJob) {
        String OutputDir=Base.getI().getJobDirectory(pJob.getId())+"/outputs/";
        boolean success = true;
        try {
            //az ellenorzest csekkolni kell !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            List<DataStagingType> outputs = OutputHandler.getLocalOutputs(pJob.getJSDL());

            for (DataStagingType t : outputs) {
                File of = new File(OutputDir+t.getFileName());
                if (!of.exists()){
                    //if not exists, it can be a generator, check the first element:
                    of = new File(OutputDir+t.getFileName()+"_0");
                    if (!of.exists()){
                        success = false;
                        sysLog(OutputDir, "Can not copy the Output file:" + t.getFileName());
                        errorLog(OutputDir, "Can not copy the Output file:" + t.getFileName());
                    }

                }                
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            success = false;
        }
        if (success) {
            success = checkGridnfo(Base.getI().getJobDirectory(pJob.getId())+"outputs/");
        }
        return success;
    }

    private boolean checkGridnfo(String OutputDir) {
        try {
            BufferedReader input = new BufferedReader(new FileReader(OutputDir + "/gridnfo.log"));
            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    if (line.contains("Wrapper script finished succesfully")){
                        input.close();
                        return true;
                    }
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
        return false;
    }
       
    /** Extracts a file in a specified dir, and deletes it.
     *  @return boolean
     */
    private boolean extractFile(String dir, String file) {
        try {
            String sor = "";
            String cmd = "tar -xzf " + file;
            //sysLog(cmd);
            Process p;
            p = Runtime.getRuntime().exec(cmd, null, new File(dir));
            BufferedReader sin = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            BufferedReader sinerr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((sor = sinerr.readLine()) != null) {
                //System.out.println(sor);
                }
            sinerr.close();
            int exitv = p.waitFor();
            if (exitv == 0) {
                sin.close();
                new File(dir + "/" + file).delete();
                return true;
            } else {
                while ((sor = sin.readLine()) != null) {
                    //sysLog(sor);
                    System.out.println(sor);
                }
                sin.close();
                return false;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }

    private void getOutput(String downloaddir, String jobID, JobController controller, String jobstatus) {
        sysLog(downloaddir, "getOutput - jobID:" + jobID + " jobstatus:" + jobstatus);
        //= localDir +"/outputs";
        String[] splitted_jobId = jobID.split("/");
        String arc_jobId = splitted_jobId[splitted_jobId.length - 1];
        StringList list = new StringList();
        list.add(jobstatus);
        //boolean keep = Boolean.TRUE;
        //controller.Get(list, downloaddir, keep, true);
        controller.Get(list, downloaddir, false, true, true);

        // File (or directory) to be moved
        File t = new File(downloaddir);
        File real_downloaddir = new File(downloaddir + "/" + arc_jobId);
        if (real_downloaddir.isDirectory() && real_downloaddir.list().length != 0) {

            // firstly extract generator outputs if exist
            //this.extractGenerator(downloaddir + "/" + arc_jobId);


            String[] outputfilelist = real_downloaddir.list();

            // moving to the standard gUSE output folder

            // Destination directory
            File dir = new File(downloaddir);

            for (String outfile : outputfilelist) {
                String sourcefile = downloaddir + "/" + arc_jobId + "/" + outfile;
                String destfile = downloaddir + "/" + outfile;
                moveFile(sourcefile, destfile);
            }


        }
        real_downloaddir.delete();

    }


    private void savetoFile(String what, String where, String filename) throws IOException
    {


        File f  = new File(where + "/" + filename);
        f.createNewFile();
        FileWriter furl = new FileWriter(where + "/" + filename);
        BufferedWriter out = new BufferedWriter(furl);
        out.write("" + what);
        out.flush();
        out.close();
    }
    
    private void saveJobID(String localDir, String jobID) throws IOException{
        savetoFile(jobID,localDir,"job.URL");
    }

    private void moveFile(String sourcefile, String destfile){
          InputStream inStream = null;
          OutputStream outStream = null;
          try{
                File sourceFile =new File(sourcefile);
                File destFile =new File(destfile);

                inStream = new FileInputStream(sourceFile);
                outStream = new FileOutputStream(destFile);

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes
                while ((length = inStream.read(buffer)) > 0){

                    outStream.write(buffer, 0, length);

                }

                inStream.close();
                outStream.close();
                sourceFile.delete();

            }catch(IOException e){
                e.printStackTrace();
            }
      }

    private boolean isFinished(String act_status){
        return (act_status.equals(new String("Finished")) ||
                act_status.equals(new String("Deleted")));
    }
    private boolean isSubmitted(String act_status){
        return (act_status.equals(new String("Accepted")) ||
                act_status.equals(new String("Preparing")) ||
                act_status.equals(new String("Submitting")));
    }
    private boolean isWaiting(String act_status){
        return act_status.equals(new String("Hold"));
    }
    private boolean isScheduled(String act_status){
        return act_status.equals(new String("Queuing"));
    }
    private boolean isRunning(String act_status){
        return (act_status.equals(new String("Running")) ||
                act_status.equals(new String("Finishing")));
    }
    private boolean isError(String act_status){
        return (act_status.equals(new String("Undefined")) ||
                act_status.equals(new String("Failed")) ||
                act_status.equals(new String("Other")));
    }
    private boolean isKilled(String act_status){
        return act_status.equals(new String("Killed"));
    }


    /** Legeneralja a WRAPPER scriptet
     */
    private void createWrapper(Job pJob,JobDescription jobdesc) throws Exception {

        JobDefinitionType jsdl = pJob.getJSDL();
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);

         String path = Base.getI().getJobDirectory(pJob.getId());
        String binname = BinaryHandler.getBinaryFileName(pType);
        String params="";
        for(String t:BinaryHandler.getCommandLineParameter(pType)) params=params.concat(" "+t);
        String stdOut = BinaryHandler.getStdOutFileName(pType);
        String stdErr = BinaryHandler.getStdErrorFileName(pType);
        //compress the output files
        Vector localoutfiles = new Vector();
        List<DataStagingType> outputs = OutputHandler.getLocalOutputs(pJob.getJSDL());
        for (DataStagingType t : outputs) {
            if (!("stderr.log".equals(t.getFileName()) ||
                    "stdout.log".equals(t.getFileName()) ||
                    "guse.jsdl".equals(t.getFileName()) ||
                    "guse.logg".equals(t.getFileName()))) {
                localoutfiles.add(t.getFileName());
            }
        }

        LinuxWrapperForArc w = null;
        try {
            w = new LinuxWrapperForArc(path);


            /* File f = new File(localDir + "/" + WRAPPER);
            f.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            out.write("#!/bin/sh \n");
            out.write("chmod 775 ./"+binname+" \n");
            //decompressing inputs
            out.write("tar -xzf "+LOCALINPUTS+" \n");
            out.write("rm -f ./"+LOCALINPUTS+" \n");
            out.write("ARGUMENTS=`cat " + params+"` \n");
            out.write("./"+binname+" $ARGUMENTS \n"); */
            // compress outputs
            String coutfiles = "";
            w.setLocalOutputs(localoutfiles, LOCALOUTPUTS);
            w.addFunctionsAndStartScript();
            w.export_LD_LIBRARY_PATH();

            w.extractAndDelete(LOCALINPUTS);

            boolean isMPI = false;
            SDLType mbsdl=XMLHandler.getData(pJob.getJSDL().getAny(), SDLType.class);
            if (mbsdl.getConstraints().getJobType().size() == 1 && mbsdl.getConstraints().getJobType().get(0).equals(JobTypeEnumeration.MPI)){
                 isMPI = true;

            }
            //app.tgz eseten
            if (BinaryHandler.isAppTgzExtension(jsdl)) {
                w.extractAndDelete(binname);
                binname = BinaryHandler.getAppTgzBase(jsdl);
            }
            /**@ToDO remote inputs */
            if (BinaryHandler.isJavaJob(jsdl)) {
                w.setJavaEnviroments(Conf.getP().getJava());
                w.runJava(binname, params, stdOut, stdErr);
            } if (isMPI){
                Double nodeNumber = jsdl.getJobDescription().getResources().getTotalCPUCount().getUpperBoundedRange().getValue();

                String nodeNumberstr = Double.toString(nodeNumber);

                 RangeInt noderange = new RangeInt();
                 noderange.setMax(nodeNumber.intValue());
                 jobdesc.getResources().getSlotRequirement().setProcessPerHost(noderange);

                 // openMPI will be default!!!
                 Software openmpi = new Software("ENV/GCC/MPI/OPENMPI-1.3.2");
                 
                 SoftwareRequirement runtime = new SoftwareRequirement();


                 runtime.add(openmpi,ComparisonOperatorEnum.GREATERTHANOREQUAL);
                 jobdesc.getResources().setRunTimeEnvironment(runtime);



                w.runMPI(binname, nodeNumberstr, params, stdOut, stdErr);
            }
            else {
                w.runBinary(binname, params, stdOut, stdErr);
            }

        /**@Todo remote outputs*/
        } finally {
            w.close();
        }
    }

//private ExecutionTarget getExecutionTarget(String requiredResource){
//    ExecutionTargetList targetlist = getExecutionTargetList();
//
//    for (ExecutionTargetListIteratorHandler it = new ExecutionTargetListIteratorHandler(targetlist.begin());
//             !it.equal(targetlist.end()); it.next()) {
//        
//          String hostName = it.pointer().getUrl().fullstr();
//          if (hostName.equals(new String(requiredResource))){
//              return it.pointer();
//          }
//    }
//    return null;
//}

private ExecutionTargetList getExecutionTargetList(UserConfig usercfg){

        // Initialise the UserConfig object.
        // This object holds various attributes, including proxy location and selected services.
        
        TargetGenerator targen = new TargetGenerator(usercfg,1);

        targen.RetrieveExecutionTargets();
        return targen.GetExecutionTargets();

}

private ExecutionTarget getBrokeredTarget(ExecutionTargetList executionlist, JobDescription jobdesc, UserConfig usercfg){
    BrokerLoader loader = new BrokerLoader();

    Broker broker = loader.load(usercfg.Broker().getFirst(), usercfg);
    broker.PreFilterTargets(executionlist, jobdesc);
    return broker.GetBestTarget();



}
 /** Creates LOCALINPUTS in the jobs directory, if it do not exist or is empty.
     *  @return
     */
    private void compressInputs(Job pJob) throws IOException, InterruptedException{
        String path=Base.getI().getJobDirectory(pJob.getId());
        String OutputDir = path + "outputs/";

        //List<String> fileList = new ArrayList<String>();
            File f = new File(path + "/" + LOCALINPUTS);
            if (!f.exists()) {
                String compressfiles = "";
                List<DataStagingType> inputs = InputHandler.getlocalInputs(pJob);
                for(DataStagingType inp:inputs){
                    //fileList.add(inp.getFileName() );
                    compressfiles += inp.getFileName() + " ";
                }

               // new ZipUtil().compressFiles(path.substring(0,path.length()-1) , fileList, path + LOCALINPUTS);

                String cmd = "tar -czf " + LOCALINPUTS + " " + compressfiles + " ";
                sysLog(path, cmd);
                Process p;
                p = Runtime.getRuntime().exec(cmd,null,new File(path));
                BufferedReader sin = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                int exitv = p.waitFor();
                if (exitv == 0) {
                    sin.close();
                    //return true;
                } else {
                    String sor = "";
                    while ((sor = sin.readLine()) != null) {
                        sysLog(OutputDir, sor);
                    }
                    sin.close();
                    //return false;
                }
            } else {
                sysLog(OutputDir, LOCALINPUTS + " exists");
            }
    }

    /** Write log into sys.log & stdout.
     */
    private void sysLog(String logdir, String txt) {
        try {
            if (Conf.getP().getDebug() > 0) {
                System.out.println("-" + txt);
                FileWriter tmp = new FileWriter(logdir + "/plugin.log", true);
                BufferedWriter out = new BufferedWriter(tmp);
                out.newLine();
                out.write(txt);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            }
    }
    /** Write log into stderr.log
     */
    private void errorLog(String localDir, String txt) {
        try {
            FileWriter tmp = new FileWriter(localDir + "/outputs/stderr.log", true);
            BufferedWriter out = new BufferedWriter(tmp);
            out.newLine();
            out.write(txt);
            out.flush();
            out.close();
        } catch (Exception e) {
            sysLog(localDir, e.toString());
        }
    }

}

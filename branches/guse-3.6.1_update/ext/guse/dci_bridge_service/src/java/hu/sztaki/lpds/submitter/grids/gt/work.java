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
 * work.java
 *
 */
package hu.sztaki.lpds.submitter.grids.gt;

import hu.sztaki.lpds.dcibridge.config.Conf;
import org.globus.gsi.*;
import org.globus.gsi.gssapi.*;
import java.io.*;
import org.ietf.jgss.GSSCredential;
import java.util.*;
import org.globus.ftp.*;
import java.io.File;
import org.globus.cog.abstraction.impl.common.task.*;
import org.globus.cog.abstraction.interfaces.*;
import org.globus.cog.abstraction.interfaces.StatusListener;
import org.globus.cog.abstraction.impl.common.StatusEvent;
import org.globus.cog.abstraction.interfaces.TaskHandler;
import org.globus.cog.abstraction.impl.common.AbstractionFactory;
import org.globus.cog.abstraction.impl.file.gridftp.FileResourceImpl;

import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.util.BinaryHandler;
import hu.sztaki.lpds.dcibridge.util.InputHandler;
import hu.sztaki.lpds.dcibridge.util.LinuxWrapperForGrids;
import hu.sztaki.lpds.dcibridge.util.OutputHandler;
import hu.sztaki.lpds.dcibridge.util.XMLHandler;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import uri.mbschedulingdescriptionlanguage.OtherType;
import uri.mbschedulingdescriptionlanguage.SDLType;

public class work implements StatusListener {//
    // c: clear afrer submit

    GSSCredential gssproxy;
    String OutputDir = "OUTPUT";
    String gridjobdir = "jobdir";
    String host = "";
    int iStatus = 2; //1=init
    List<DataStagingType> outputs;
    String jobmngr[];//c    
    private static int DEBUG = -1;
    final static String WRAPPER = "wrapper.sh";
    final static String LOCALINPUTS = "localinputs.tgz";
    final static String LOCALOUTPUTS = "localoutputs.tgz";
    Task task1;
    TaskHandler handler;

    /** Creates a new instance of work and submit */
    /**
     * 
     * @param pJob
     * @param pProvider "gt2" or "gt4"
     */
    public work(Job pJob, String pProvider) {
        String localDir;
        try {
            if (!("gt2".equals(pProvider) || "gt4".equals(pProvider))) {
                throw new Exception("Invalig gt provider: " + pProvider);
            }
//            try {
//                DEBUG = Integer.parseInt(PropertyLoader.getInstance().getProperty("debug"));
//            } catch (Exception e) {
//                DEBUG = 0;
//            }
//            System.out.println("Debug level: " + DEBUG);
            localDir = Base.getI().getJobDirectory(pJob.getId());
            OutputDir = localDir + "/outputs";
            gridjobdir = pJob.getId();

            sysLog("mv: " + pJob.getConfiguredResource().getMiddleware() + " vo: " + pJob.getConfiguredResource().getVo() + " host:" + pJob.getConfiguredResource().getResource() + " jobmanager:" + pJob.getConfiguredResource().getJobmanager());

            /**@todo SET resource AND jobmanager!!!! */
            //host=(String)jc.getJobPropertyValue("resource");
//            host=pJob.getConfiguredResource().getResource();
//            try{
//                jobmngr = ((String)jc.getJobPropertyValue("jobmanager")).toLowerCase().split("-");
//                if (jobmngr.length < 2) {
//                    throw new Exception();
//                }
//            }catch (Exception ex) {
//                ex.printStackTrace();
//                throw new Exception("Invalid jobmanager. ");
//            }
//
//            if( (pJob.getConfiguredResource().getJobmanager()).toLowerCase().contains("fork") ){
//                jobmngr = ((String)"jobmanager-fork").split("-");
//            }else{
//                jobmngr = pJob.getConfiguredResource().getJobmanager().split("-");
//            }

            //jobmngr="jobmanager-pbs-seegrid".split("-");
            //jobmngr="/jobmanager-lcgpbs-gilda".split("-");
            jobmngr = "jobmanager-pbs".split("-");
            //jobmngr="jobmanager-fork".split("-");
            host = "ngs.wmin.ac.uk";//"ce.yerphi-cluster.grid.am";//"gn0.hpcc.sztaki.hu";//ce.sg.grena.ge:2119/jobmanager-pbs-seegrid
            pJob.setResource(host + "/" + jobmngr[0] + "-" + jobmngr[1]);
        } catch (Exception e) {
            errorLog("Configuration error. - " + e.getMessage());
            sysLog("Configuration error. - " + e.getMessage());
            e.printStackTrace();
            iStatus = 7; // error
            return;
        }

        /** Submission of the Job.
         *  Copies the inputs, the executables and the wrapper, executes the chmod operation.
         *  If the operations above are successful, then ->  gsubmit()
         */
        sysLog(" GT PROVIDER: " + pProvider);
        FileResourceImpl client = null;
        try {
            GlobusCredential gcred = new GlobusCredential(localDir + "/x509up");
            gssproxy = new GlobusGSSCredentialImpl(gcred, GSSCredential.INITIATE_AND_ACCEPT);
            sysLog("rem.life: " + gssproxy.getRemainingLifetime());
            if (gssproxy.getRemainingLifetime() == 0) {
                errorLog("The certificate has expired.");
                iStatus = 7;
                return;
            }

            outputs = OutputHandler.getOutputs(pJob.getJSDL());
            createWrapper(pJob);
            compressInputs(pJob);
            sysLog("resource" + host);

            client = new FileResourceImpl();
            ServiceContact c = new ServiceContactImpl();
            c.setHost(host);
            c.setPort(2811);
            client.setServiceContact(c);
            SecurityContext secu = new SecurityContextImpl();
            secu.setCredentials(gssproxy);
            client.setSecurityContext(secu);
            client.start();

            // copy of inputs
            client.createDirectory(gridjobdir);

            sysLog("copy:" + LOCALINPUTS);
            try {
                client.putFile(localDir + "/" + LOCALINPUTS, gridjobdir + "/" + LOCALINPUTS);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Can not copy the file:" + LOCALINPUTS + " - " + e.getMessage());
            }
            sysLog("copy:" + WRAPPER);
            try {
                client.putFile(localDir + "/" + WRAPPER, gridjobdir + "/" + WRAPPER);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("Can not copy the file:" + WRAPPER + " - " + e.getMessage());
            }

            client.changeMode(gridjobdir + "/" + WRAPPER, 777);

            gsubmit(pJob, pProvider);
            if (iStatus == 7) {
                try {
                    client.deleteDirectory(gridjobdir, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    errorLog("Warning: Cannot delete the jobs directory.");
                }
            }
        } catch (Exception e) {
            try {
                client.deleteDirectory(gridjobdir, true);
            } catch (Exception ee) {
            }
            if (!"".equals(e.getMessage())) {
                errorLog("Submit error: ", e);
            }
            sysLog(" submit error: " + e.getMessage());
            //e.printStackTrace();
            iStatus = 7; // error
        } finally {
            try {
                client.stop();
            } catch (Exception ee) {
            }
        }
        //takarit
        jobmngr = null;
    }

    private void gsubmit(Job pJob, String pProvider) {
        try {
            JobDefinitionType jsdl = pJob.getJSDL();
            task1 = new TaskImpl("myTestTask", Task.JOB_SUBMISSION);
            JobSpecification spec = new JobSpecificationImpl();

            /**@todo set MPI job type*/
            //if (jobtype.equals("MPI")) {
            //        spec.setAttribute("jobtype", "mpi");
            //        spec.setAttribute("count", "2");//(String)jc.getJobPropertyValue("nodenumber"));
            //    /**@todo set nodenumber*/
            //    }
            spec.setDirectory(gridjobdir);
            spec.setExecutable(WRAPPER);
            spec.setStdOutput("stdout.log");
            spec.setStdError("stderr.log");
            if (!jobmngr[1].equals("fork")) {
                if (jobmngr.length > 2) {
                    spec.setAttribute("queue", jobmngr[2]);
                }
            }

            /**@todo set user spec */
            String JDKEY = "gt2.key";
            if ("gt4".equals(pProvider)) {
                JDKEY = "gt4.key";
            }

            SDLType sdlType = XMLHandler.getData(jsdl.getAny(), SDLType.class);
            List ls = sdlType.getConstraints().getOtherConstraint();
            Iterator it = ls.iterator();

            while (it.hasNext()) {
                OtherType value = (OtherType) it.next();
                System.out.println("Value :" + value.getName()+" = "+value.getValue());

                if(value.getName().indexOf(JDKEY)>-1){
                    String sKey = value.getName().replaceAll(JDKEY,"");
                    String sValue = value.getValue();
                    sysLog( "KEY: "+sKey);
                    sysLog(" Value: "+sValue);
                        if (sKey.equals("environment")) {
                            try {
                                String[] vars = sValue.trim().split(" ");
                                for (int v = 0; v < vars.length; v++) {
                                    String[] env = vars[v].trim().split("=");
                                    spec.addEnvironmentVariable("" + env[0], "" + env[1]);
                                }
                            } catch (Exception e) {
                                sysLog( "" + e.toString());
                                errorLog("Syntax error in RSL attribute: environment (" + sValue + ")");
                            }
                        } else {
                            try {
                                spec.setAttribute(sKey, "" + sValue);
                            } catch (Exception e) {
                                sysLog("" + e.toString());
                                errorLog( e.getMessage());
                            }
                        }
                }
            }

            //for testing:
            //spec.addEnvironmentVariable("KULCS", "ERTEK");
            //spec.setAttribute("minMemory","32");
            sysLog("rsl spec:" + spec.toString());
            task1.setSpecification(spec);
            // ExecutionService service = new ExecutionServiceImpl(Service.JOB_SUBMISSION);
            ExecutionService service = new ExecutionServiceImpl();
            service.setProvider(pProvider);

            SecurityContext securityContext = AbstractionFactory.newSecurityContext(pProvider);
            securityContext.setCredentials(gssproxy); // e.g. set it to default in ./globus
            service.setSecurityContext(securityContext);
            ServiceContact serviceContact = new ServiceContactImpl("" + host);
            //ServiceContact serviceContact = new ServiceContactImpl("gn0.hpcc.sztaki.hu" );
            serviceContact.setPort(2119);
            if ("gt4".equals(pProvider)) {
                serviceContact = new ServiceContactImpl("https://" + host + ":8443/wsrf/services/ManagedJobFactoryService");
            }
            service.setServiceContact(serviceContact);
            service.setJobManager(jobmngr[0] + "-" + jobmngr[1]);//resource = serviceContact.toString() + " - " + jobmngr[0] + "-" + jobmngr[1];
            task1.setService(Service.JOB_SUBMISSION_SERVICE, service);
            task1.addService(service);
            task1.addStatusListener(this);

            handler = AbstractionFactory.newExecutionTaskHandler(pProvider);
            sysLog(" task1.submit()");
            //Util.registerTransport();
            handler.submit(task1);
        } catch (Exception e) {
            errorLog("Submit failed: ", e);
            sysLog("Submit failed: ");
            e.printStackTrace();
            iStatus = 7; // error
        }
        sysLog("submit end");
    }

    /** Gets the ouputs of the job
     */
    private boolean getOutput() {
        boolean ok = true;
        sysLog(" getoutput:");
        try {
            FileResourceImpl client = new FileResourceImpl();
            ServiceContact c = new ServiceContactImpl();
            c.setHost(host);
            c.setPort(2811);
            client.setServiceContact(c);
            SecurityContext secu = new SecurityContextImpl();
            secu.setCredentials(gssproxy);
            client.setSecurityContext(secu);
            client.start();
            try {
                DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/" + LOCALOUTPUTS));
                sysLog(" X get: " + OutputDir + "/" + LOCALOUTPUTS);
                client.get(gridjobdir + "/" + LOCALOUTPUTS, sink, null);
                client.deleteFile(gridjobdir + "/" + LOCALOUTPUTS);
            } catch (Exception e) {
                ok = false;
                sysLog("Can not copy the LOCALOUTPUTS - " + e);
                errorLog("Can not copy the LOCALOUTPUTS - ", e);
            }
            try {
                DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/stdout.log"));
                sysLog(" X get: " + OutputDir + "/stdout.log -");
                client.get(gridjobdir + "/stdout.log", sink, null);
                client.deleteFile(gridjobdir + "/stdout.log");
            } catch (Exception ee) {
                ok = false;
                sysLog("Can not copy the stderr.log - " + ee);
                errorLog("Can not copy the stdout.log - ", ee);
            }
            try {
                DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/stderr.log"));
                sysLog(" X get: " + OutputDir + "/stderr.log -");
                client.get(gridjobdir + "/stderr.log", sink, null);
                client.deleteFile(gridjobdir + "/stderr.log");
            } catch (Exception ee) {
                ok = false;
                sysLog("Can not copy the stderr.log - " + ee);
                errorLog("Can not copy the stderr.log - ", ee);
            }

            try {
                client.deleteDirectory(gridjobdir, true);
            } catch (Exception e) {
                e.printStackTrace();
                errorLog("Warning: Cannot delete the jobs directory.");
            }
            client.stop();

            if (ok) {
                //succes = checkGridnfo();
                if (!extractFile(OutputDir, LOCALOUTPUTS)) {
                    sysLog("Could not extract outputsandbox: " + LOCALOUTPUTS);
                    errorLog("Could not extract outputsandbox: " + LOCALOUTPUTS);
                    //pJob.setStatus(ActivityStateEnumeration.FAILED);
                    ok = false;
                }
                ok = checkOutputFiles();
            }

        } catch (Exception e) {
            ok = false;
            e.printStackTrace();
            errorLog("Get Output Error: ", e);
        }
        if (ok) {
            iStatus = 6; // finished
        } else {
            iStatus = 7; // errror
        }
        clenup();
        return ok;
    }

    private boolean checkOutputFiles() {//Job pJob
        //String OutputDir=Base.getI().getJobDirectory(pJob.getId())+"/outputs/";
        boolean success = true;
        try {
            for (DataStagingType t : outputs) {
                File of = new File(OutputDir + "/" + t.getFileName());
                if (!of.exists()) {
                    //if not exists, it can be a generator, check the first element:
                    of = new File(OutputDir + "/" + t.getFileName() + "_0");
                    if (!of.exists()) {
                        success = false;
                        sysLog("Can not copy the Output file:" + t.getFileName());
                        errorLog("Can not copy the Output file:" + t.getFileName());
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            success = false;
        }
        if (success) {
            success = checkGridnfo(OutputDir);
        }
        return success;
    }

    private boolean checkGridnfo(String OutputDir) {
        try {
            BufferedReader input = new BufferedReader(new FileReader(OutputDir + "/gridnfo.log"));
            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    if (line.contains("Wrapper script finished succesfully")) {
                        input.close();
                        return true;
                    }
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /** Extracts a file in a specified dir, and deletes it.
     *  @return boolean
     */
    private boolean extractFile(String dir, String file) {
        try {
            String cmd = "tar -xzf " + file;
            //sysLog(cmd);
            Process p;
            p = Runtime.getRuntime().exec(cmd, null, new File(dir));
            BufferedReader sin = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            int exitv = p.waitFor();
            if (exitv == 0) {
                sin.close();
                new File(dir + "/" + file).delete();
                return true;
            } else {
                String sor = "";
                while ((sor = sin.readLine()) != null) {
                    //sysLog(sor);
                    System.out.println(sor);
                }
                sin.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Creates LOCALINPUTS in the jobs directory, if it do not exist or is empty.
     *  @return
     */
    private void compressInputs(Job pJob) throws IOException, InterruptedException {
        String path = Base.getI().getJobDirectory(pJob.getId());

        //List<String> fileList = new ArrayList<String>();
        File f = new File(path + "/" + LOCALINPUTS);
        if (!f.exists()) {
            String compressfiles = "";
            List<DataStagingType> inputs = InputHandler.getInputs(pJob);
            for (DataStagingType inp : inputs) {
                //fileList.add(inp.getFileName() );
                compressfiles += inp.getFileName() + " ";
            }

            // new ZipUtil().compressFiles(path.substring(0,path.length()-1) , fileList, path + LOCALINPUTS);

            String cmd = "tar -czf " + LOCALINPUTS + " " + compressfiles + " ";
            sysLog(cmd);
            Process p;
            p = Runtime.getRuntime().exec(cmd, null, new File(path));
            BufferedReader sin = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            int exitv = p.waitFor();
            if (exitv == 0) {
                sin.close();
            //return true;
            } else {
                String sor = "";
                while ((sor = sin.readLine()) != null) {
                    sysLog(sor);
                }
                sin.close();
            //return false;
            }
        } else {
            sysLog(LOCALINPUTS + " exists");
        }
    }

    /** Interrupts the job
     */
    public void abort() {
        sysLog(" ABORT ");
        errorLog("- - - - - - - -");
        errorLog("ABORTED by user");
        iStatus = 7;
        try {
            handler.cancel(task1);
        } catch (Exception e) {
            e.printStackTrace();
            errorLog("Abort failed:", e);
        }
        clenup();
    }

    /** Gets the status of the job
     *  @return a status kod
     */
    public int getStatus() {
        return iStatus;
    }

    /** Generates the WRAPPER script
     */
    private void createWrapper(Job pJob) throws Exception {
        JobDefinitionType jsdl = pJob.getJSDL();
        //String userid = BinaryHandler.getUserName(jsdl);
        String path = Base.getI().getJobDirectory(pJob.getId());
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String userid = BinaryHandler.getUserName(pType);

        String binname = BinaryHandler.getBinaryFileName(pType);
        String params ="";
        for(String s:BinaryHandler.getCommandLineParameter(pType)) params=params.concat(" "+s);
        String stdOut = BinaryHandler.getStdOutFileName(pType);
        String stdErr = BinaryHandler.getStdErrorFileName(pType);

        //compress the output files
        Vector localoutfiles = new Vector();
        //List<DataStagingType> outputs = OutputHandler.getOutputs(pJob.getJSDL());
        for (DataStagingType t : outputs) {
            if (!("stderr.log".equals(t.getFileName()) || "stdout.log".equals(t.getFileName()))) {
                localoutfiles.add(t.getFileName());
            }
        }

        LinuxWrapperForGrids w = null;
        try {
            w = new LinuxWrapperForGrids(path);
            //w.setLocalOutsAndCallback(localoutfiles, LOCALOUTPUTS, CALLBACK_STATUS_SERVLET_URL, userid, pJob.getId());
            w.setLocalOutputs(localoutfiles, LOCALOUTPUTS);
            w.addFunctionsAndStartScript();
            w.export_LD_LIBRARY_PATH();

            w.extractAndDelete(LOCALINPUTS);
            //app.tgz eseten
            if (BinaryHandler.isAppTgzExtension(jsdl)) {
                w.extractAndDelete(binname);
                binname = BinaryHandler.getAppTgzBase(jsdl);
            }
            /**@ToDO remote inputs */
            if (BinaryHandler.isJavaJob(jsdl)) {
                w.setJavaEnviroments(Conf.getP().getJava());
                w.runJava(binname, params, stdOut, stdErr);
            } else {
                w.runBinary(binname, params, stdOut, stdErr);
            }

        /**@Todo remote outputs*/
        } finally {
            w.close();
        }
    }

    /** Creates a log entry in the file stderr.log 
     */
    private void errorLog(String txt) {
        try {
            FileWriter tmp = new FileWriter(OutputDir + "/stderr.log", true);
            BufferedWriter out = new BufferedWriter(tmp);
            out.newLine();
            out.write(txt);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Creates a log entry in the file stderr.log 
     */
    private void errorLog(String pMsg, Exception pEx) {
        try {
            File f = new File(OutputDir + "/stderr.log");
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            fw.write(pMsg);
            fw.write(pEx.getMessage() + "\n");
            if (pEx.getCause() != null) {
                fw.write(pEx.getCause().getMessage() + "\n");
            }
            fw.write("\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Creates a log entry in the file std.out 
     */
    private void sysLog(String txt) {
//        System.out.println(" " + txt);
//        if (DEBUG > 0) {
//            try {
////                System.out.println(gridjobdir+" "+txt);
//                FileWriter tmp = new FileWriter(OutputDir + "/history/sys.log", true);
//                BufferedWriter out = new BufferedWriter(tmp);
//                out.newLine();
//                out.write(txt);
//                out.flush();
//                out.close();
//            } catch (Exception e) {
//            }
//        }
    }

    // class StatusListenerRun implements StatusListener {
    public void statusChanged(StatusEvent gjob) {
        sysLog("--statusChanged: " + gjob.getStatus().getStatusString() + " " + gjob.getStatus().getStatusCode() + " msg:" +  gjob.getStatus().getMessage());
        if (gjob.getStatus().getStatusCode() == 7) {
            sysLog("getOutput()");
            getOutput();
        } else if (gjob.getStatus().getStatusCode() == 5) {//error
            getOutput();
            if (gjob.getStatus().getMessage() != null) {
                errorLog("Status: FAILED error code: " + gjob.getStatus().getMessage());
            }
            iStatus = 7;

        } else if (gjob.getStatus().getStatusCode() == 2) {
            iStatus = 5;
        } else if (gjob.getStatus().getStatusCode() == 1) {
            iStatus = 13;
        }

    }

    /** Cleanig up
     */
    private void clenup() {
        /*    try {
        task1.removeAllServices();
        } catch (Exception e) {
        }
        try {
        handler.remove(task1);
        } catch (Exception e) {
        }
        try {
        gssproxy.dispose();
        } catch (Exception e) {
        e.printStackTrace();
        }
        handler=null;
        task1=null;
        gssproxy = null;
         */
    }
}

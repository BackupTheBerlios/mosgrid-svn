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
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import uri.mbschedulingdescriptionlanguage.OtherType;
import uri.mbschedulingdescriptionlanguage.SDLType;

public class work_dci {

    public static final byte STATUSLISTENER = 0;
    public static final byte JOBTASK = 1;
   
    final static String WRAPPER = "wrapper.sh";
    final static String LOCALINPUTS = "localinputs.tgz";
    final static String LOCALOUTPUTS = "localoutputs.tgz";

    private String provider;
    private String JDKEY = "gt2.key";     
    private TaskHandler handler;

    /** Creates a new instance of work    
     * 
     * @param pJob
     * @param pProvider "gt2" or "gt4"
     */
    public work_dci(String pProvider) throws Exception {
        if (!("gt2".equals(pProvider) || "gt4".equals(pProvider))) {
            throw new Exception("Invalig gt provider: " + pProvider);
        } else {
            provider = pProvider;
            if ("gt4".equals(provider)) {
                JDKEY = "gt4.key";
            }
            //handler = AbstractionFactory.newExecutionTaskHandler(provider);
            System.out.println("provider:" + provider + " - handler created");
        }

    }

    public void submit(Job pJob) {
        String gridjobdir = pJob.getId();
        String localDir = Base.getI().getJobDirectory(pJob.getId());
        String OutputDir = localDir + "/outputs";
        String[] jobmngr;
        int iStatus = 2;
        try {
            if (!("gt2".equals(provider) || "gt4".equals(provider))) {
                throw new Exception("Invalig gt provider: " + provider);
            }

            sysLog(OutputDir, "mv: " + pJob.getConfiguredResource().getMiddleware() + " vo: " + pJob.getConfiguredResource().getVo() + " host:" + pJob.getConfiguredResource().getResource() + " jobmanager:" + pJob.getConfiguredResource().getJobmanager());

            /**@todo SET resource AND jobmanager!!!! */
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
            if( (pJob.getConfiguredResource().getJobmanager()).toLowerCase().contains("fork") ){
                jobmngr = ((String)"jobmanager-fork").split("-");
            }else{
                jobmngr = pJob.getConfiguredResource().getJobmanager().split("-");
            }

            pJob.setResource(pJob.getConfiguredResource().getResource() + "/" + jobmngr[0] + "-" + jobmngr[1]);
        } catch (Exception e) {
            errorLog(OutputDir, "Configuration error. - " + e.getMessage());
            sysLog(OutputDir, "Configuration error. - " + e.getMessage());
            //e.printStackTrace();
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            iStatus = 7; // error
            return;
        }

        /** 
         *  Uploads the input files and wrapper
         */
        sysLog(OutputDir, " GT PROVIDER: " + provider);
        FileResourceImpl client = null;
        try {
            GlobusCredential gcred = new GlobusCredential(localDir + "/x509up");
            GSSCredential gssproxy = new GlobusGSSCredentialImpl(gcred, GSSCredential.INITIATE_AND_ACCEPT);
            sysLog(OutputDir, "rem.life: " + gssproxy.getRemainingLifetime());
            if (gssproxy.getRemainingLifetime() == 0) {
                errorLog(OutputDir, "The certificate has expired.");
                pJob.setStatus(ActivityStateEnumeration.FAILED);
                iStatus = 7;
                return;
            }
            
            createWrapper(pJob);
            compressInputs(pJob);
            sysLog(OutputDir, "resource: " + pJob.getConfiguredResource().getResource());

            client = new FileResourceImpl();
            ServiceContact c = new ServiceContactImpl();
            c.setHost(pJob.getConfiguredResource().getResource());
            c.setPort(2811);
            client.setServiceContact(c);
            SecurityContext secu = new SecurityContextImpl();
            secu.setCredentials(gssproxy);
            client.setSecurityContext(secu);
            client.start();

            // inputok felmasolasa
            client.createDirectory(gridjobdir);

            sysLog(OutputDir, "copy:" + LOCALINPUTS);
            try {
                client.putFile(localDir + "/" + LOCALINPUTS, gridjobdir + "/" + LOCALINPUTS);
            } catch (Exception e) {
                //e.printStackTrace();
                throw new Exception("Can not copy the file:" + LOCALINPUTS + " - " + e.getMessage());
            }
            sysLog(OutputDir, "copy:" + WRAPPER);
            try {
                client.putFile(localDir + "/" + WRAPPER, gridjobdir + "/" + WRAPPER);
            } catch (Exception e) {
                //e.printStackTrace();
                throw new Exception("Can not copy the file:" + WRAPPER + " - " + e.getMessage());
            }

            client.changeMode(gridjobdir + "/" + WRAPPER, 777);


            /*
             * Submits the job
             */
            try {
                JobDefinitionType jsdl = pJob.getJSDL();
                pJob.setMiddlewareObj(new HashMap());
                JobSpecification spec = new JobSpecificationImpl();

//                if (BinaryHandler.isMPI(jsdl)) {
//                    //get nodenumber
//                    int nodenumber = 0;
//                    try {
//                        nodenumber = (int) jsdl.getJobDescription().getResources().getTotalCPUCount().getUpperBoundedRange().getValue();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    spec.setAttribute("jobtype", "mpi");
//                    spec.setAttribute("count", "" + nodenumber);//(String)jc.getJobPropertyValue("nodenumber"));
//                }

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
//            String JDKEY = "gt2.key";
//            if ("gt4".equals(provider)) {
//                JDKEY = "gt4.key";
//            }

            SDLType sdlType = XMLHandler.getData(jsdl.getAny(), SDLType.class);
            List ls = sdlType.getConstraints().getOtherConstraint();
            Iterator it = ls.iterator();

            while (it.hasNext()) {
                OtherType value = (OtherType) it.next();
                //System.out.println("Value :" + value.getName()+" = "+value.getValue());

                if(value.getName().indexOf(JDKEY)>-1){
                    String sKey = value.getName().replaceAll(JDKEY,"");
                    String sValue = value.getValue();
                    sysLog(OutputDir, "KEY: "+sKey);
                    sysLog(OutputDir, " Value: "+sValue);
                        if (sKey.equals("environment")) {
                            try {
                                String[] vars = sValue.trim().split(" ");
                                for (int v = 0; v < vars.length; v++) {
                                    String[] env = vars[v].trim().split("=");
                                    spec.addEnvironmentVariable("" + env[0], "" + env[1]);
                                }
                            } catch (Exception e) {
                                sysLog(OutputDir, "" + e.toString());
                                errorLog(OutputDir, "Syntax error in RSL attribute: environment (" + sValue + ")");
                            }
                        } else {
                            try {
                                spec.setAttribute(sKey, "" + sValue);
                            } catch (Exception e) {
                                sysLog(OutputDir, "" + e.toString());
                                errorLog(OutputDir, e.getMessage());
                            }
                        }
                }
            }

//                sysLog(OutputDir, "rslkeys:" + rsl.toString());
//                Object[] keys = rsl.keySet().toArray();
//                for (int i = 0; i < keys.length; i++) {
//                    if (((String) keys[i]).indexOf(JDKEY) > -1) {
//                        String sKey = ((String) keys[i]).replaceAll(JDKEY, "");
//                        sysLog(OutputDir, "KEY: " + sKey);
//                        sysLog(OutputDir, " Value: " + rsl.get((String) keys[i]));
//                        if (sKey.equals("environment")) {
//                            try {
//                                String[] vars = ((String) rsl.get((String) keys[i])).trim().split(" ");
//                                for (int v = 0; v < vars.length; v++) {
//                                    String[] env = vars[v].trim().split("=");
//                                    spec.addEnvironmentVariable("" + env[0], "" + env[1]);
//                                }
//                            } catch (Exception e) {
//                                sysLog(OutputDir, "" + e.toString());
//                                errorLog(OutputDir, "Syntax error in RSL attribute: environment (" + rsl.get((String) keys[i]) + ")");
//                            }
//                        } else {
//                            try {
//                                spec.setAttribute(sKey, "" + rsl.get((String) keys[i]));
//                            } catch (Exception e) {
//                                sysLog(OutputDir, "" + e.toString());
//                                errorLog(OutputDir, e.getMessage());
//                            }
//                        }
//                    }
//                }

                //for testing:
                //spec.addEnvironmentVariable("KULCS", "ERTEK");
                //spec.setAttribute("minMemory","32");
                sysLog(OutputDir, "rsl spec:" + spec.toString());

                ((HashMap) pJob.getMiddlewareObj()).put(JOBTASK, new TaskImpl("myTestTask", Task.JOB_SUBMISSION));//stores the JOBTASK object
                //Task task1 = new TaskImpl("myTestTask", Task.JOB_SUBMISSION);
                //((Task)  (((HashMap)pJob.getMiddlewareObj()).get(JOBTASK)) );
                ((Task) (((HashMap) pJob.getMiddlewareObj()).get(JOBTASK))).setSpecification(spec);
                // ExecutionService service = new ExecutionServiceImpl(Service.JOB_SUBMISSION);
                ExecutionService service = new ExecutionServiceImpl();
                service.setProvider(provider);

                SecurityContext securityContext = AbstractionFactory.newSecurityContext(provider);
                securityContext.setCredentials(gssproxy); // e.g. set it to default in ./globus
                service.setSecurityContext(securityContext);
                ServiceContact serviceContact = new ServiceContactImpl(pJob.getConfiguredResource().getResource());
                //ServiceContact serviceContact = new ServiceContactImpl("gn0.hpcc.sztaki.hu" );
                serviceContact.setPort(2119);
                if ("gt4".equals(provider)) {
                    serviceContact = new ServiceContactImpl("https://" + pJob.getConfiguredResource().getResource() + ":8443/wsrf/services/ManagedJobFactoryService");
                }
                service.setServiceContact(serviceContact);
                service.setJobManager(jobmngr[0] + "-" + jobmngr[1]);//resource = serviceContact.toString() + " - " + jobmngr[0] + "-" + jobmngr[1];
                ((Task) (((HashMap) pJob.getMiddlewareObj()).get(JOBTASK))).setService(Service.JOB_SUBMISSION_SERVICE, service);
                ((Task) (((HashMap) pJob.getMiddlewareObj()).get(JOBTASK))).addService(service);
                //pJob.setMiddlewareObj(new gtStatusListener());
                ((HashMap) pJob.getMiddlewareObj()).put(STATUSLISTENER, new gtStatusListener());//stores the statuslistener object
                ((Task) (((HashMap) pJob.getMiddlewareObj()).get(JOBTASK))).addStatusListener((gtStatusListener) (((HashMap) pJob.getMiddlewareObj()).get(STATUSLISTENER)));

                if (handler == null) {
                    handler = AbstractionFactory.newExecutionTaskHandler(provider); //->moved to constructor
                    System.out.println("p" + provider + " handler succ created");
                }
                sysLog(OutputDir, " task1.submit()");
                //Util.registerTransport();
                handler.submit(((Task) (((HashMap) pJob.getMiddlewareObj()).get(JOBTASK))));
            } catch (Exception e) {
                errorLog(OutputDir, "Submit failed: ", e);
                sysLog(OutputDir, "Submit failed: ");
                //e.printStackTrace();
                pJob.setStatus(ActivityStateEnumeration.FAILED);
                iStatus = 7; // error
            }
            sysLog(OutputDir, "submit end");


            if (iStatus == 7) {
                try {
                    client.deleteDirectory(gridjobdir, true);
                } catch (Exception e) {
                    //e.printStackTrace();
                    errorLog(OutputDir, "Warning: Cannot delete the jobs directory.");
                }
            }
        } catch (Exception e) {
            try {
                client.deleteDirectory(gridjobdir, true);
            } catch (Exception ee) {
            }
            if (!"".equals(e.getMessage())) {
                errorLog(OutputDir, "Submit error: ", e);
            }
            sysLog(OutputDir, " Submit error: " + e.getMessage());
            //e.printStackTrace();
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            iStatus = 7; // error
        } finally {
            try {
                client.stop();
            } catch (Exception ee) {
            }
        }
    }

    /**
     * downloads the output
     * @param pJob
     * @return
     */
    public boolean getOutput(Job pJob) {
        String localDir = Base.getI().getJobDirectory(pJob.getId());
        String OutputDir = localDir + "/outputs";
        String gridjobdir = pJob.getId();

        boolean ok = true;
        sysLog(OutputDir, " getoutput:");
        try {
            GlobusCredential gcred = new GlobusCredential(localDir + "/x509up");
            GSSCredential gssproxy = new GlobusGSSCredentialImpl(gcred, GSSCredential.INITIATE_AND_ACCEPT);            
            
            FileResourceImpl client = new FileResourceImpl();
            ServiceContact c = new ServiceContactImpl();
            c.setHost(pJob.getConfiguredResource().getResource());
            c.setPort(2811);
            client.setServiceContact(c);
            SecurityContext secu = new SecurityContextImpl();
            secu.setCredentials(gssproxy);
            client.setSecurityContext(secu);
            client.start();
            try {
                DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/" + LOCALOUTPUTS));
                sysLog(OutputDir, " X get: " + OutputDir + "/" + LOCALOUTPUTS);
                client.get(gridjobdir + "/" + LOCALOUTPUTS, sink, null);
                client.deleteFile(gridjobdir + "/" + LOCALOUTPUTS);
            } catch (Exception e) {
                ok = false;
                sysLog(OutputDir, "Can not copy the LOCALOUTPUTS - " + e);
                errorLog(OutputDir, "Can not copy the LOCALOUTPUTS - ", e);
            }
            try {
                DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/stdout.log"));
                sysLog(OutputDir, " X get: " + OutputDir + "/stdout.log -");
                client.get(gridjobdir + "/stdout.log", sink, null);
                client.deleteFile(gridjobdir + "/stdout.log");
            } catch (Exception ee) {
                ok = false;
                sysLog(OutputDir, "Can not copy the stderr.log - " + ee);
                errorLog(OutputDir, "Can not copy the stdout.log - ", ee);
            }
            try {
                DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/stderr.log"));
                sysLog(OutputDir, " X get: " + OutputDir + "/stderr.log -");
                client.get(gridjobdir + "/stderr.log", sink, null);
                client.deleteFile(gridjobdir + "/stderr.log");
            } catch (Exception ee) {
                ok = false;
                sysLog(OutputDir, "Can not copy the stderr.log - " + ee);
                errorLog(OutputDir, "Can not copy the stderr.log - ", ee);
            }

            try {
                client.deleteDirectory(gridjobdir, true);
            } catch (Exception e) {
                //e.printStackTrace();
                errorLog(OutputDir, "Warning: Cannot delete the jobs directory.");
            }
            client.stop();

            if (ok) {
                if (!extractFile(OutputDir, LOCALOUTPUTS)) {
                    sysLog(OutputDir, "Could not extract outputsandbox: " + LOCALOUTPUTS);
                    errorLog(OutputDir, "Could not extract outputsandbox: " + LOCALOUTPUTS);
                    //pJob.setStatus(ActivityStateEnumeration.FAILED);
                    ok = false;
                }
                ok = checkOutputFiles(pJob);
            }

        } catch (Exception e) {
            ok = false;
            //e.printStackTrace();
            errorLog(OutputDir, "Get Output Error: ", e);
        }
        if (ok) {
            pJob.setStatus(ActivityStateEnumeration.FINISHED);
        } else {
            pJob.setStatus(ActivityStateEnumeration.FAILED);
        }
        clenup();
        return ok;
    }

    private boolean checkOutputFiles(Job pJob) {//
        String OutputDir = Base.getI().getJobDirectory(pJob.getId()) + "/outputs/";
        List<DataStagingType> outputs = OutputHandler.getLocalOutputs(pJob.getJSDL());
        boolean success = true;
        try {
            for (DataStagingType t : outputs) {
                File of = new File(OutputDir + "/" + t.getFileName());
                if (!of.exists()) {
                    //if not exists, it can be a generator, check the first element:
                    of = new File(OutputDir + "/" + t.getFileName() + "_0");
                    if (!of.exists()) {
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

    /** Creates LOCALINPUTS in the jobs directory, if it do not exist or is empty.
     *  @return
     */
    private void compressInputs(Job pJob) throws IOException, InterruptedException {
        String path = Base.getI().getJobDirectory(pJob.getId());
        String OutputDir = path + "outputs";
        //List<String> fileList = new ArrayList<String>();
        File f = new File(path + "/" + LOCALINPUTS);
        if (!f.exists()) {
            String compressfiles = "";
            List<DataStagingType> inputs = InputHandler.getlocalInputs(pJob);
            for (DataStagingType inp : inputs) {
                //fileList.add(inp.getFileName() );
                compressfiles += inp.getFileName() + " ";
            }

            // new ZipUtil().compressFiles(path.substring(0,path.length()-1) , fileList, path + LOCALINPUTS);

            String cmd = "tar -czf " + LOCALINPUTS + " " + compressfiles + " ";
            sysLog(OutputDir, cmd);
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
                    sysLog(OutputDir, sor);
                }
                sin.close();
            //return false;
            }
        } else {
            sysLog(OutputDir, LOCALINPUTS + " exists");
        }
    }

    /** Interrupts the given job
     */
    public void abort(Job pJob) {
        String OutputDir = Base.getI().getJobDirectory(pJob.getId()) + "output";
        sysLog(OutputDir, " ABORT ");
        errorLog(OutputDir, "- - - - - - - -");
        errorLog(OutputDir, "ABORTED by user");
        //iStatus = 7;
        try {
            handler.cancel(((Task) (((HashMap) pJob.getMiddlewareObj()).get(JOBTASK))));
        } catch (Exception e) {
            //e.printStackTrace();
            errorLog(OutputDir, "Abort failed:", e);
        }
        clenup();
    //pJob.setStatus(ActivityStateEnumeration.FAILED);
    }

    /** Generates the WRAPPER script
     */
    private void createWrapper(Job pJob) throws Exception {
        JobDefinitionType jsdl = pJob.getJSDL();
        //String userid = BinaryHandler.getUserName(jsdl);
        String path = Base.getI().getJobDirectory(pJob.getId());
        POSIXApplicationType pType = XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);        

        String binname = BinaryHandler.getBinaryFileName(pType);
        String params = "";
        for (String s : BinaryHandler.getCommandLineParameter(pType)) {
            params = params.concat(" " + s);
        }
        String stdOut = BinaryHandler.getStdOutFileName(pType);
        String stdErr = BinaryHandler.getStdErrorFileName(pType);

        //compress the output files
        Vector localoutfiles = new Vector();
        List<DataStagingType> loutputs = OutputHandler.getLocalOutputs(pJob.getJSDL());
        for (DataStagingType t : loutputs) {
            if (!("stderr.log".equals(t.getFileName()) ||
                    "stdout.log".equals(t.getFileName()) ||
                    "guse.jsdl".equals(t.getFileName()) ||
                    "guse.logg".equals(t.getFileName()))) {
                localoutfiles.add(t.getFileName());
            }
        }

        LinuxWrapperForGrids w = null;
        try {
            w = new LinuxWrapperForGrids(path);
            //w.setLocalOutsAndCallback(localoutfiles, LOCALOUTPUTS, CALLBACK_STATUS_SERVLET_URL, userid, pJob.getId());
            w.setLocalOutputs(localoutfiles, LOCALOUTPUTS);
            w.addFunctionsAndStartScript(null, LinuxWrapperForGrids.FUNCTION_GSIFTPREMOTEIO);
            w.export_LD_LIBRARY_PATH();

            w.extractAndDelete(LOCALINPUTS);
            //app.tgz eseten
            if (BinaryHandler.isAppTgzExtension(jsdl)) {
                w.extractAndDelete(binname);
                binname = BinaryHandler.getAppTgzBase(jsdl);
            }
            /** remote inputs */
            List<DataStagingType> rinputs = InputHandler.getRemoteInputs(pJob);
            for (DataStagingType t : rinputs) {
                w.writeln("if (! globus-url-copy "+t.getSource().getURI()+" file:`pwd`/"+t.getFileName()+" ) then \n echo Can not copy the Input file "+t.getFileName()+" from:"+t.getSource().getURI()+" >> stderr.log \n ext 1 \n fi ");
            }

            if (BinaryHandler.isMPI(jsdl)) {
                //get nodenumber
                int nodenumber = 0;
                try {
                    nodenumber = (int) jsdl.getJobDescription().getResources().getTotalCPUCount().getUpperBoundedRange().getValue();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                //w.runMPIallocatenodes(binname, params, stdOut, stdErr, "" + nodenumber, pJob.getId());
                w.runMPI(binname, params, stdOut, stdErr, "" + nodenumber);
                //w.runBinary(binname, params, stdOut, stdErr);
            } else if (BinaryHandler.isJavaJob(jsdl)) {
                w.setJavaEnviroments(Conf.getP().getJava());
                w.runJava(binname, params, stdOut, stdErr);
            } else {
                w.runBinary(binname, params, stdOut, stdErr);
            }

            List<DataStagingType> routputs = OutputHandler.getRemoteOutputs(pJob.getJSDL());
            for (DataStagingType t : routputs) {
                String remotefile = t.getTarget().getURI();
                //..value22/0/       pert levág          -> ..value22_0
                if (remotefile.endsWith("/")) {
                    remotefile = remotefile.substring(0, remotefile.lastIndexOf("/"));
                }
                //w.writeln("if (! globus-url-copy file:`pwd`/"+t.getFileName()+" "+t.getTarget().getURI()+" ) then \n echo Can not copy the Output file "+t.getFileName()+" to "+t.getTarget().getURI()+" >> stderr.log \n  ext 1 \n fi ");
                w.writeln(" gsiftpupload " + remotefile + " " + t.getFileName());
            }
        } finally {
            w.close();
        }
    }

    /** Creates a log entry in the file stderr.log 
     */
    private void errorLog(String OutputDir, String txt) {
        try {
            FileWriter tmp = new FileWriter(OutputDir + "/stderr.log", true);
            BufferedWriter out = new BufferedWriter(tmp);
            out.newLine();
            out.write(txt);
            out.flush();
            out.close();
        } catch (Exception e) {
            sysLog(OutputDir, e.toString());
        }
    }

    /** Creates a log entry in the file stderr.log 
     */
    private void errorLog(String OutputDir, String pMsg, Exception pEx) {
        try {
            File f = new File(OutputDir + "/stderr.log");
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            fw.write(pMsg + "\n");
            fw.write(pEx.getMessage() + "\n");
            if (pEx.getCause() != null) {
                fw.write(pEx.getCause().getMessage() + "\n");
            }
            fw.write("\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            //e.printStackTrace();
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

    // class StatusListenerRun implements StatusListener {
//    public void statusChanged(StatusEvent gjob) {
//        sysLog("--statusChanged: " + gjob.getStatus().getStatusString() + " " + gjob.getStatus().getStatusCode() + " msg:" +  gjob.getStatus().getMessage());
//        if (gjob.getStatus().getStatusCode() == 7) {
//            sysLog("getOutput()");
//            getOutput();
//        } else if (gjob.getStatus().getStatusCode() == 5) {//error
//            getOutput();
//            if (gjob.getStatus().getMessage() != null) {
//                errorLog("Status: FAILED error code: " + gjob.getStatus().getMessage());
//            }
//            iStatus = 7;
//
//        } else if (gjob.getStatus().getStatusCode() == 2) {
//            iStatus = 5;
//        } else if (gjob.getStatus().getStatusCode() == 1) {
//            iStatus = 13;
//        }
//
//    }
    /** Cleans up
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

    public void statusChanged(StatusEvent arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

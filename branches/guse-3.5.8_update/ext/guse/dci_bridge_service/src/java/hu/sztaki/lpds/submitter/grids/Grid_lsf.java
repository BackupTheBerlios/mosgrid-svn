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
 * LSF submitter
 */

package hu.sztaki.lpds.submitter.grids;

import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.util.BinaryHandler;
import hu.sztaki.lpds.dcibridge.util.InputHandler;
import hu.sztaki.lpds.dcibridge.util.LinuxWrapperForGrids;
import hu.sztaki.lpds.dcibridge.util.OutputHandler;
import hu.sztaki.lpds.dcibridge.util.XMLHandler;
import hu.sztaki.lpds.submitter.service.ssh.sshService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.globus.util.Util;

public class Grid_lsf extends Middleware{
    private static String keycachepath = null;
    private static final String SSHDIR = "gusesubmitter/";
    final static String WRAPPER ="wrapper.sh";
    final static String GENERATOR ="generator.tgz";
    final static String LOCALINPUTS = "localinputs.tgz";
    final static String LOCALOUTPUTS = "localoutputs.tgz";
    final static String INITRESOURCE="DCI-BRIDGE";
    private static String LSFSCRIPT = "wrapper.sh";

    public Grid_lsf(){
        //System.out.println("LSF");
        if (keycachepath == null) {
            keycachepath = System.getProperty("java.io.tmpdir")+"/dci_bridge/keycache/";
            File kp = new File(keycachepath);
            if (!kp.exists()) {
                kp.mkdir();
            }
        }
    }
    /**
     * Aborts the job.
     * @param pJob
     * @throws java.lang.Exception
     */
    @Override
    protected void abort(Job pJob) throws Exception {
        pJob.setStatus(ActivityStateEnumeration.FAILED);// = 7;// "Aborted";
        JobDefinitionType jsdl = pJob.getJSDL();
        POSIXApplicationType pType = XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String guseuserid = BinaryHandler.getUserName(pType);
        bkill(pJob, guseuserid);
        errorLog(Base.getI().getJobDirectory(pJob.getId()), "- - - - - - - - \nABORTED by user");
    }

    /**Submits the job.
     *
     * @param pLocalDir
     * @param pJC
     */
    @Override
    protected void submit(Job pJob) throws Exception {
        //System.out.println("pPath" + pPath);
        //System.out.println("getJobPropertyHash" + pJC.getJobPropertyHash());
        //System.out.println("getJobDescriptionHash" + pJC.getJobDescriptionHash());
        boolean griddircreated = false;
        JobDefinitionType jsdl = pJob.getJSDL();
        POSIXApplicationType pType = XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String guseuserid = BinaryHandler.getUserName(pType);
        String localDir = Base.getI().getJobDirectory(pJob.getId());

        String sshuser = null;
        String sshhost = pJob.getConfiguredResource().getVo();
        //String queue = pJob.getConfiguredResource().getResource(); //job queue

        try {
            sysLog(localDir, "----------- LSF -----------------");
//            if (!jc.getRInHash().isEmpty() || !jc.getROutHash().isEmpty() || !jc.getRGeneratorHash().isEmpty()) {
//                errorLog("Remote I/O files are not supoprted!");
//                status = 7;
//                return;
//            }

            sshuser = sshService.getI().setSshKey(localDir + "x509up", guseuserid, sshhost);
            if (sshuser == null){
                throw new Exception("Ssh User Id is not set! ");
            }
            pJob.setMiddlewareObj(sshuser);
            sysLog(localDir, "user from key:" + sshuser + " host:" + sshhost);
            createLSFWrapper(pJob);
            compressInputs(pJob);
            sshService.getI().sshExec(guseuserid, sshuser, sshhost, "mkdir -p ~/" + SSHDIR + pJob.getId());// create jobs dir & auth
            griddircreated = true;
            sshService.getI().scpTo(guseuserid, sshuser, sshhost, localDir + LOCALINPUTS, "~/" + SSHDIR + pJob.getId() + "/" + LOCALINPUTS);
            sshService.getI().scpTo(guseuserid, sshuser, sshhost, localDir  + LSFSCRIPT, "~/" + SSHDIR + pJob.getId() + "/guse_" + pJob.getId());

            String params = "";
            for (String t : BinaryHandler.getCommandLineParameter(pType)) {
                params = params.concat(" " + t);
            }
            //get bsub params, (optional): parameters_to_binary # parameters_to_bsub
            String bsubparams = "";
            if (params != null) {
                if (params.contains("#")) {
                    bsubparams = params.split("#", 2)[1].trim();
                }
            }

            pJob.setMiddlewareId(bsub(guseuserid, sshuser, sshhost, pJob, bsubparams));
            sysLog(localDir, "bjobid:" + pJob.getMiddlewareId());
            pJob.setStatus(ActivityStateEnumeration.PENDING); // 2;
            pJob.setResource(sshhost + "/" + sshuser);
        } catch (Exception e) {
            if (pJob.getStatus() != ActivityStateEnumeration.FAILED) {
                pJob.setStatus(ActivityStateEnumeration.FAILED);
                e.printStackTrace();
                errorLog(localDir, "Could not submit the jod. ", e);
                if (griddircreated) {
                    deljobdir(guseuserid, sshuser,sshhost, pJob);
                }
            }
        }
    }

    @Override
    protected void getOutputs(Job pJob) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void getStatus(Job pJob) throws Exception {
        JobDefinitionType jsdl = pJob.getJSDL();
        POSIXApplicationType pType = XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String guseuserid = BinaryHandler.getUserName(pType);
        String sshuser = pJob.getMiddlewareObj().toString();
        String sshhost = pJob.getConfiguredResource().getVo();
        try {
            String sstatus = bjobs(guseuserid, sshuser, sshhost, pJob);
            //sysLog("bjobID:" + pJob.getMiddlewareId() + " status:" + pJob.getStatus().name());
            //PEND,PSUSP,RUN,USUSP,SSUSP,DONE(6),EXIT(7),UNKWN(7),WAIT,ZOMBI
            if ("DONE".equals(sstatus) || "EXIT".equals(sstatus) || "".equals(sstatus)) {//job finished;job lost, perhaps finished
                if (getOutput(pJob, guseuserid)) {
                    pJob.setStatus(ActivityStateEnumeration.FINISHED);
                } else {
                    pJob.setStatus(ActivityStateEnumeration.FAILED);
                }
                //finished
            } else if ("RUN".equals(sstatus)) {
                pJob.setStatus(ActivityStateEnumeration.RUNNING);//running
            } else {
                pJob.setStatus(ActivityStateEnumeration.PENDING);//waiting
            }
        } catch (Exception e) {
            System.out.println("status error:" + e);
        }
    }

    /** Retrieves the job outputs.
     */
    private boolean getOutput(Job pJob, String guseuserid) {
        String sshuser = pJob.getMiddlewareObj().toString();
        String sshhost = pJob.getConfiguredResource().getVo();
        String OutputDir = Base.getI().getJobDirectory(pJob.getId()) + "outputs/";
        boolean succes=true;
        try {
            sshService.getI().scpFrom(guseuserid, sshuser, sshhost, "~/" + SSHDIR + pJob.getId() + "/" + LOCALOUTPUTS, OutputDir + LOCALOUTPUTS);
        } catch (Exception e) {
            succes = false;
            e.printStackTrace();
        }
        try {
            sshService.getI().scpFrom(guseuserid, sshuser, sshhost, "~/" + SSHDIR + pJob.getId() + "/stderr.log" , OutputDir + "stderr.log");
        } catch (Exception e) {
            succes = false;
            errorLog(Base.getI().getJobDirectory(pJob.getId()), "Can not copy the stderr.log file.");
            e.printStackTrace();
        }
        try {
            sshService.getI().scpFrom(guseuserid, sshuser, sshhost, "~/" + SSHDIR + pJob.getId() + "/stdout.log" , OutputDir + "stdout.log");
        } catch (Exception e) {
            succes = false;
            errorLog(Base.getI().getJobDirectory(pJob.getId()), "Can not copy the stdout.log file.");
            e.printStackTrace();
        }
        deljobdir(guseuserid, sshuser, sshhost, pJob);

        if (!extractFile(OutputDir, LOCALOUTPUTS)) {
            sysLog(OutputDir, "Could not extract outputsandbox: " + LOCALOUTPUTS);
            errorLog(OutputDir, "Could not extract outputsandbox: " + LOCALOUTPUTS);
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            succes = false;
        }
        if (succes) {
            succes = checkOutputFiles(pJob);
        }

        return succes;
    }

    /**
     * LSF submit. Submits the job.
     * @param user
     * @param host
     * @param pJob
     * @param params bsub parameters
     * @return the job id
     * @throws java.lang.Exception
     */
    private String bsub(String guseuserid, String user, String host, Job pJob, String params) throws Exception {//             ?
        //command example : cd ~/gusesubmitter/be078f96-8ab1-477f-999e-7b22f66b4639/; bsub < guse_be078f96-8ab1-477f-999e-7b22f66b4639
        //response example: "Generic job. \nJob <280431> is submitted to queue <pub.1h>."
        String resp = sshService.getI().sshExec(guseuserid, user, host, "cd ~/" + SSHDIR + pJob.getId() + "/; bsub -N -u \"\" " + params + " < guse_" + pJob.getId() + " 2> gridnfo.log");
        return resp.substring(resp.indexOf("<") + 1, resp.indexOf(">"));
    }

    /**
     * LSF status request
     * @return the status:PEND,PSUSP,RUN,USUSP,SSUSP,DONE,EXIT,UNKWN,WAIT,ZOMBI
     * @throws Exception
     */
    private String bjobs(String guseuserid, String user, String host, Job pJob) throws Exception {
        //command example : bjobs 281558
        //response example:
        //Cannot connect to LSF. Please wait ...
        //JOBID   USER    STAT  QUEUE      FROM_HOST   EXEC_HOST   JOB_NAME   SUBMIT_TIME
        //281558  behulla DONE  pub.1h     brutus3     a1172       testjob    Mar 25 14:20

        //or: "Job <281558> is not found"
        String resp = sshService.getI().sshExec(guseuserid, user, host, "bjobs " + pJob.getMiddlewareId() + " ");//2>&1
        //sysLog("bjobs resp: " + resp);
        if (resp.length() == 0) {
            //sysLog("resp.length:0");
            //Thread.sleep(2000);
            return "WAIT";
        }

        String[] lines = resp.split("\n");
        for (int i = 0; i < lines.length; i++) {
            //System.out.println(i+":"+lines[i]);
            if (lines[i].contains(pJob.getMiddlewareId())) {
                if (lines[i].contains("is not found")) {//Job <45181> is not found
                    //System.out.println("none");
                } else {
                    //System.out.println("Status:" + lines[i].split(" * ")[2]);
                    return lines[i].split(" * ")[2];
                }
            }
        }
        return "";
        //qstat 622.izumi.lpds.sztaki.hu|tail -n+3|tr [[:space:]] \ |tr -s \ |cut -f5 -d\   2> tmp.err
    }

    //LSF Kill a running job
    private void bkill(Job pJob, String guseuserid) {
        String sshuser = pJob.getMiddlewareObj().toString();
        String sshhost = pJob.getConfiguredResource().getVo();
        try {
            /**@todo check the command */
            sshService.getI().sshExec(guseuserid, sshuser, sshhost, "bkill " + pJob.getMiddlewareId() + " 2> /dev/null");
        } catch (Exception e) {
            //errorLog("WARNING: Could not abort job (" + pJob.getMiddlewareId() + ") on " + host + ". ", e);
        }
        deljobdir(guseuserid, sshuser, sshhost, pJob);
    }

    private void deljobdir(String guseuserid, String user, String host, Job pJob) {
        try {
            sshService.getI().sshExec(guseuserid, user, host, "rm -r -f ~/" + SSHDIR + pJob.getId());// delete jobs dir
        } catch (Exception e) {
            //errorLog(localDir, "WARNING: Could not delete jobs directory on " + host + ". ", e);
        }
    }

    /**
     * Generates the Wrapper script for pbs
     * @param pJob
     * @throws java.lang.Exception
     */
    private void createLSFWrapper(Job pJob) throws Exception {
        JobDefinitionType jsdl = pJob.getJSDL();
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        //String userid = BinaryHandler.getUserName(jsdl);
        String path = Base.getI().getJobDirectory(pJob.getId());
        String binname = BinaryHandler.getBinaryFileName(pType);
        String params="";
        for(String t:BinaryHandler.getCommandLineParameter(pType)) params=params.concat(" "+t);
        String stdOut = BinaryHandler.getStdOutFileName(pType);
        String stdErr = BinaryHandler.getStdErrorFileName(pType);

        //lsf parameters cut
        String jparams = "";
        if (params != null) {
            if (params.contains("#")) {
                jparams = params.split("#", 2)[0].trim();
            } else {
                jparams = params;
            }
        }

        String jobmanager= pJob.getConfiguredResource().getResource(); //job queue

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

        LinuxWrapperForGrids w = null;
        try {
            w = new LinuxWrapperForGrids(path);

        if (!"default".equals(jobmanager)) {
            w.writeln("#BSUB-q " + jobmanager + " ");
        }
                w.writeln("#BSUB-J guse_" + pJob.getId() + " \n"
                + "#BSUB-e stderr.log \n"
                + "#BSUB-o gridnfo.log \n"
                + "#BSUB-i localinputs.tgz \n");
        /**@todo set MPI job type*/
//        if ((jc.getJobPropertyValue("type") != null) && ((String) jc.getJobPropertyValue("type")).equals("MPI")) {
//             w.writeln("#BSUB-n " + (String) jc.getJobPropertyValue("nodenumber") + " \n");
//        }

            //w.setLocalOutsAndCallback(localoutfiles, LOCALOUTPUTS, CALLBACK_STATUS_SERVLET_URL, userid, pJob.getId());
            w.setLocalOutputs(localoutfiles, LOCALOUTPUTS);
            w.addFunctionsAndStartScript();
            w.export_LD_LIBRARY_PATH();

            w.extractAndDelete(LOCALINPUTS);
            //in case of app.tgz 
            if (BinaryHandler.isAppTgzExtension(jsdl)) {
                w.extractAndDelete(binname);
                binname = BinaryHandler.getAppTgzBase(jsdl);
            }
            /**@ToDO remote inputs */
            if (BinaryHandler.isJavaJob(jsdl)) {
                w.setJavaEnviroments(Conf.getP().getJava());
                w.runJava(binname, jparams, stdOut, stdErr);
            } else {
                //w.runBinary(binname, jparams, stdOut, stdErr);
                w.writeln("if (! chmod +x ./" + binname + " ) then \n ext 1 \n fi ");
                w.writeln("echo '- Exe started at          :' `date` >> gridnfo.log ");
                w.writeln("./" + binname + " " + jparams + " >" + stdOut +" 2>" + stdErr + " ");
                w.writeln("exitc=$? ");
                w.writeln("echo '- Exe finished at         :' `date` >> gridnfo.log ");
                w.writeln("echo '- The exit code of the exe:' $exitc >> gridnfo.log ");
                w.writeln("if test $exitc -ne 0 \n then \n ext $exitc \n fi ");
            }

        /**@Todo remote outputs*/
        } finally {
            w.close();
        }
    }



    private Vector getOutputSandboxFileNames(Job pJob){
        JobDefinitionType jsdl = pJob.getJSDL();
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String stdErr = BinaryHandler.getStdErrorFileName(pType);

        Vector files = new Vector();
        files.add(stdErr);
        files.add(LOCALOUTPUTS);
        return files;

    }

    private boolean checkOutputFiles(Job pJob) {
        String OutputDir=Base.getI().getJobDirectory(pJob.getId())+"outputs/";
        boolean success = true;
        try {
            //the algorithm need review !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            List<DataStagingType> outputs = OutputHandler.getLocalOutputs(pJob.getJSDL());

            for (DataStagingType t : outputs) {
                File of = new File(OutputDir+t.getFileName());
                if (!of.exists()){
                    //if not exists, it can be a generator, check the first element:
                    of = new File(OutputDir+t.getFileName()+"_0");
                    if (!of.exists()){
                        success = false;
                        sysLog(Base.getI().getJobDirectory(pJob.getId()), "Can not copy the Output file:" + t.getFileName());
                        errorLog(Base.getI().getJobDirectory(pJob.getId()), "Can not copy the Output file:" + t.getFileName());
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
            ex.printStackTrace();
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
    private void compressInputs(Job pJob) throws IOException, InterruptedException{
        String path=Base.getI().getJobDirectory(pJob.getId());

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
                        sysLog(path, sor);
                    }
                    sin.close();
                    //return false;
                }
            } else {
                sysLog(path, LOCALINPUTS + " exists");
            }
    }

    private void cp(String inf, String outf) throws Exception {
        Exception e = null;
        File inputFile = new File(inf);
        File outputFile = new File(outf);
        FileReader in = new FileReader(inputFile);
        FileWriter out = new FileWriter(outputFile);
        int c;
        try {
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            Util.setFilePermissions(outf, 600);
        } catch (Exception ee) {
            e = ee;
        } finally {
            in.close();
            out.close();
        }
        if (e != null) {
            throw e;
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

    /** Write log into stderr.log
     */
    private void errorLog(String localDir, String pMsg, Exception pEx) {
        try {
            File f = new File(localDir + "/outputs/stderr.log");
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

    /** Write log into gridnfo.log
     */
//    private void gridnfoLog(String txt){
//        try{
//            FileWriter tmp = new FileWriter(OutputDir+"/gridnfo.log",true);
//            BufferedWriter   out = new BufferedWriter(tmp);
//            out.newLine();
//            out.write(txt);
//            out.flush();
//            out.close();
//        }catch (Exception e){sysLog(e.toString());}
//    }
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

}

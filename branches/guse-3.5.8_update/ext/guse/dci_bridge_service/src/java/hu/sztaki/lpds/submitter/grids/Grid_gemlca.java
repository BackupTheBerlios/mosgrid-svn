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
package hu.sztaki.lpds.submitter.grids;

import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.util.BinaryHandler;
import hu.sztaki.lpds.dcibridge.util.InputHandler;
import hu.sztaki.lpds.dcibridge.util.OutputHandler;
import hu.sztaki.lpds.dcibridge.util.XMLHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.globus.cog.abstraction.impl.common.task.SecurityContextImpl;
import org.globus.cog.abstraction.impl.common.task.ServiceContactImpl;
import org.globus.cog.abstraction.impl.file.gridftp.FileResourceImpl;
import org.globus.cog.abstraction.interfaces.*;
import org.globus.ftp.DataSink;
import org.globus.ftp.DataSinkStream;
import org.ietf.jgss.GSSCredential;
import uk.ac.wmin.cpc.gemlca.client.ClientGLCAdmin;
import uk.ac.wmin.cpc.gemlca.client.ClientGLCProcess;
import uk.ac.wmin.cpc.gemlca.client.helpers.GlobusConnectionHelper;
import uk.ac.wmin.cpc.gemlca.exception.GEMLCAUserFriendlyException;
import uk.ac.wmin.cpc.gemlca.frontend.stubs.GemlcaService.Parameter;
import uk.ac.wmin.cpc.gemlca.frontend.stubs.common.DescriptionProperty;

public class Grid_gemlca extends Middleware {

    private static ConcurrentHashMap<String, ClientGLCProcess.JobExecutor> jobexecutor = new ConcurrentHashMap();
    static Object vomsLock = new Object();
    private static int DEBUG = -1;

    /**
     * Constructor
     */
    public Grid_gemlca()  throws Exception{
        THIS_MIDDLEWARE=Base.MIDDLEWARE_GEMLCA;
        threadID++;
        setName("guse/dci-bridge:Middleware handler(gemlca) - "+threadID);
    }

    @Override
    protected void submit(Job pJob) throws Exception {
        System.out.println("gemlca submit.");
        String identity = null;
        URL GURL = null;////GEMLCAURL
        String GLC = null;//GEMLCALCName
        String GLCSite = null;//executor site
        String[] lc_args;
        String localDir = Base.getI().getJobDirectory(pJob.getId());
        try {
//            try {
//                DEBUG = Integer.parseInt(PropertyLoader.getInstance().getProperty("debug"));
//            } catch (Exception e) {
//                DEBUG = 0;
//            }
//            System.out.println("Debug level: " + DEBUG);



            sysLog(localDir, "mv: " + pJob.getConfiguredResource().getMiddleware() + " vo: " + pJob.getConfiguredResource().getVo() + " host:" + pJob.getConfiguredResource().getResource() + " jobmanager:" + pJob.getConfiguredResource().getJobmanager());

            GURL = new URL("https://" + pJob.getConfiguredResource().getVo() + "/wsrf/services/uk/ac/wmin/cpc/gemlca/frontend");
            GLC = pJob.getConfiguredResource().getResource().split(":", 2)[0];//"resource: rrr"
            GLCSite = pJob.getConfiguredResource().getJobmanager();//"jobmanager";

            JobDefinitionType jsdl = pJob.getJSDL();
            POSIXApplicationType pType = XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
            String params = "";
            for (String t : BinaryHandler.getCommandLineParameter(pType)) {
                params = params.concat(" " + t);
            }
            lc_args = params.trim().split(" ");

            sysLog(localDir,"GURL:"+GURL.toString()+" GLC:"+GLC+" GLCSite:"+GLCSite);
            sysLog(localDir, "params:"+params+" lc_args.length:"+lc_args.length);

        } catch (Exception e) {
            errorLog(localDir, "Configuration error.");
            errorLog(localDir, e.getMessage());
            e.printStackTrace();
            pJob.setStatus(ActivityStateEnumeration.FAILED);//iStatus = 7;
            throw e;
        }

        /* ------------------------------------------submit-------------------------------------------------*/

        ClientGLCProcess gclient;
        ClientGLCProcess.JobExecutor myje;

        try {
            GSSCredential userProxy = GlobusConnectionHelper.makeCredentialFromFile(localDir + "x509up");
            sysLog(localDir, "userProxy:"+userProxy.getRemainingLifetime());
            if (userProxy.getRemainingLifetime() == 0) {
                throw new Exception("The certificate has expired. ");
            }
            // Creation of the handle
            if ((GURL == null) || (GLC == null)) {
                sysLog(localDir, " inproper handle request");
                throw new UnknownHostException();
            }

            //Gethering of Gemlca infos 
            //String[] xpaths= "/LCEnvironment/backendSpecificData".split(" ");
            String Vo = null;
            String owneremail = "unknown";
            String stderrlog = "stderr.log";
            String stdoutlog = "stdout.log";
            //boolean sitefound = false;
            String[] xpaths = "/LCEnvironment".split(" ");
            ClientGLCAdmin admin = new ClientGLCAdmin(GURL, userProxy, identity, GLC);
            DescriptionProperty dps[] = admin.getDescriptionProperties(xpaths);
            if (dps != null) {
                for (DescriptionProperty dp : admin.getDescriptionProperties(xpaths)) {
                    //System.out.println(dp.getXpath() + "," + dp.getValue(0));
                    if (dp.getXpath().contains("@error")) {
                        stderrlog = dp.getValue(0);
                    } else if (dp.getXpath().contains("@output")) {
                        stdoutlog = dp.getValue(0);
                    } else if (dp.getXpath().contains("/LCEnvironment/authorizationInfo/email")) {
                        owneremail = dp.getValue(0);
                    } else if (dp.getXpath().contains("VirtualOrganisation")) {
                        Vo = dp.getValue(0);
                        sysLog(localDir, "vo: " + Vo);
                    }

                /*else if (dp.getXpath().contains("VirtualOrganisation") && !sitefound) {
                Vo = dp.getValue(0);
                sysLog(localDir, "vo: "+Vo);
                } else if (dp.getXpath().contains("site") && !sitefound && dp.getValue(0).equals(GLCSite)) {
                sitefound=true;
                sysLog(localDir, GLCSite+" found, vo: "+Vo);
                }*/

                }
            List<DataStagingType> outputs = OutputHandler.getLocalOutputs(pJob.getJSDL());
            for (DataStagingType op : outputs) {
                if ("stderr.log".equals(op.getFileName()) ){
                    sysLog(localDir, "stderr.log->"+stderrlog);
                    op.setName(stderrlog);
                }else if("stdout.log".equals(op.getFileName())){
                    sysLog(localDir, "stdout.log->"+stdoutlog);
                    op.setName(stdoutlog);
                }

            }
            }
            admin.destroy();
            sysLog(localDir, "stderrlog:" + stderrlog + " stdoutlog:" + stdoutlog + " owneremail:" + owneremail);
            gridnfoLog(localDir, "The GEMLCA Legacy Code administrator's e-mail address: " + owneremail);
            //Voms extension of the proxy
            if (Vo != null) {
                if (addVomsC(localDir, Vo)) {
                    sysLog(localDir, "VOMS ok, reload proxy");
                    userProxy = GlobusConnectionHelper.makeCredentialFromFile(localDir + "x509up");
                }
            }

            sysLog(localDir, " Client creation");
            gclient = new ClientGLCProcess(GURL, userProxy, identity);
            gclient.createProcess(GLC);
            gclient.setExecutorSite(GLCSite, 0);

            List<DataStagingType> inputs = InputHandler.getInputs(pJob);
            List<DataStagingType> outputs = OutputHandler.getOutputs(pJob.getJSDL());

            //Parameter check
            sysLog(localDir, " Parameter check");
            Parameter[] par = gclient.getLCParameters(0);
            sysLog(localDir, "par.length:" + par.length);
            if (par.length != lc_args.length) {
                pJob.setStatus(ActivityStateEnumeration.FAILED);
                throw new Exception("Configuration error. Invalid number of parameter.  Please configure the Job.");
            }
            for (int i = 0; i < par.length; i++) {
                String[] ps = ((String) (par[i].getValue())).split(",");
                sysLog(localDir, "parameter " + par[i].getIndex() + "(" + ps[3] + ")->" + lc_args[i]);
                gridnfoLog(localDir, "parameter " + par[i].getIndex() + "(" + ps[3] + ")->" + lc_args[i]);
                if (ps[2].equals("File")) {
                    if (ps[4].equals("Output")) {
                        boolean outtest = false;
                        for (DataStagingType op : outputs) {//check output port
                            if (lc_args[i].equals(op.getFileName())) {
                                outtest = true;
                            }
                        }
                        if (!outtest) {
                            pJob.setStatus(ActivityStateEnumeration.FAILED);
                            gridnfoLog(localDir, "Configuration error. Please configure the Output port(s).");
                            throw new Exception("Configuration error. Please configure the Output port(s).");
                        }
                    } else if (ps[4].equals("Input")) {
                        boolean intest = false;
                        for (DataStagingType inp : inputs) {//check input port
                            if (lc_args[i].equals(inp.getFileName())) {
                                intest = true;
                            }
                        }
                        if (!intest) {
                            pJob.setStatus(ActivityStateEnumeration.FAILED);
                            gridnfoLog(localDir, "Configuration error. Please configure the Input port(s).");
                            throw new Exception("Configuration error. Please configure the Input port(s).");
                        }
                    }
                }
            }


            //Writing of handle content in a file 
/*            sysLog(localDir, " Handle creation");
            String hndle = gclient.getHandleAsString();
            String GHandle = localDir + "/handle";//GEMLCAHandle
            FileWriter tmp = new FileWriter(GHandle, true);
            BufferedWriter out = new BufferedWriter(tmp);
            out.write(hndle);
            out.close();
             */

            // upload input files
            inputs = InputHandler.getlocalInputs(pJob);
            if (!inputs.isEmpty()) {
                String resulturl = gclient.getResultsURL(0);
                sysLog(localDir, "resulturl:_" + resulturl + "_");
                URI url = new URI(resulturl.replace(" ", "%20"));
                FileResourceImpl ftpclient = new FileResourceImpl();
                ServiceContactImpl c = new ServiceContactImpl();
                c.setContact(resulturl);
                ftpclient.setServiceContact(c);
                SecurityContext secu = new SecurityContextImpl();
                secu.setCredentials(userProxy);
                ftpclient.setSecurityContext(secu);
                ftpclient.start();

                for (DataStagingType inp : inputs) {
                    sysLog(localDir, " copy:Directory:" + ftpclient.getCurrentDirectory() + " filename:" + inp.getFileName());
                    try {
                        sysLog(localDir, localDir + inp.getFileName() + " " + url.getPath().replace("%20", " ") + "/" + inp.getFileName());
                        ftpclient.putFile(localDir + inp.getFileName(), "" + url.getPath().replace("%20", " ") + "/" + inp.getFileName());
                    } catch (Exception e) {
                        pJob.setStatus(ActivityStateEnumeration.FAILED);
                        e.printStackTrace();
                        throw new Exception("Can not copy the Input file:" + inp.getFileName() + " - " + e.getMessage());
                    }
//                        } else {
//                            sysLog(localDir, "Default input for parameter: " + ipSandbox[i]);
//                            gridnfoLog(localDir, "Default input for parameter: " + ipSandbox[i]);
//                        }
                }
                ftpclient.stop();
            }

            sysLog(localDir, " Job submission");
            myje = gclient.run(lc_args);
            sysLog(localDir, " running");
            int jid = myje.getJobData().submitID;//job id
            sysLog(localDir, " jobid:" + jid);
            if (jid < 1) {
                sysLog(localDir, " submission failed");
                throw new UnknownHostException();
            }
            pJob.setStatus(ActivityStateEnumeration.RUNNING);//iStatus = 5;
            pJob.setResource(GURL.getHost() + "-> " + GLCSite);
            jobexecutor.put(pJob.getId(), myje);
        } catch (GEMLCAUserFriendlyException e) {
            e.printStackTrace();
            errorLog(localDir, e.getMessage());
            pJob.setStatus(ActivityStateEnumeration.FAILED);//iStatus = 7;
        } catch (Exception e) {
            e.printStackTrace();
            errorLog(localDir, "Could not submit the job. ", e);
            pJob.setStatus(ActivityStateEnumeration.FAILED);//iStatus = 7;
        }
    }

    /**
     * Adds the proper voms extension to the proxy being in the subdirectory of the job
     * @param localDir
     * @param voname
     * @return boolean
     */
    private boolean addVomsC(String localDir, String voname) {
        try {
            //String addvomssext = PropertyLoader.getInstance().getProperty("prefix.dir")+"addvomsext/addvomsext";
            //String cmd =new String(addvomssext+" "+voname+" "+localDir+"/x509up"+ " "+localDir+"/x509upvoms");//
            String cmd = "voms-proxy-init -voms " + voname + ": -noregen -out x509up";
            sysLog(localDir, "localDir:" + localDir + " cmd:" + cmd);
            sysLog(localDir, cmd);
            Process p;
            p = Runtime.getRuntime().exec(cmd, null, new File(localDir));
            BufferedReader sin = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            int exitv = p.waitFor();
            if (exitv == 0) {
                sin.close();
                return true;
            } else {
                String sor = "";
                errorLog(localDir, cmd + " \n failed.");
                while ((sor = sin.readLine()) != null) {
                    sysLog(localDir, sor);
                    errorLog(localDir, sor);
                }
                errorLog(localDir, "\n");
                sin.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void getStatus(Job pJob) throws Exception {

        //if (!(cstate.equals("Done") || cstate.equals("Failed") || iStatus == 7 || myje == null)) {
        String cstate = jobexecutor.get(pJob.getId()).getCurrentState();
        String GLCSite = jobexecutor.get(pJob.getId()).getJobData().executorSite;
        pJob.setResource(pJob.getConfiguredResource().getVo() + " - " + GLCSite);

        String localDir = Base.getI().getJobDirectory(pJob.getId());
        sysLog(localDir, "Job status:." + cstate + ".");
        try {
            String errormsg = jobexecutor.get(pJob.getId()).getDeathCause().getMessage();
            sysLog(localDir, "Job failed: " + errormsg);
            errorLog(localDir, "Job failed: " + errormsg);
            cstate = "Failed";
        } catch (Exception e) {
            //sysLog("death ex:"+e.getMessage());
            }

        if (cstate.equals("Done")) {
            //iStatus = 5; //getoutput
            if (getOutput(pJob)) {
                pJob.setStatus(ActivityStateEnumeration.FINISHED);
            } else {
                pJob.setStatus(ActivityStateEnumeration.FAILED);
            }
            cleanup(pJob);
        } else if (cstate.equals("Failed")) {
            //iStatus = 5; //getoutput
            getOutput(pJob);
            errorLog(localDir, "Job status: " + cstate);
            errorLog(localDir, " If this error message appears frequently, please contact the GEMLCA Legacy Code administrator about this job. You can find the e-mail address in the Logbook. ");
            pJob.setStatus(ActivityStateEnumeration.FAILED);//iStatus = 7;
            cleanup(pJob);
        }
    //}
    //return iStatus;
    }

    @Override
    protected void getOutputs(Job pJob) throws Exception {
    }

    /** Gets the output of the job
     */
    private boolean getOutput(Job pJob) {
        String localDir = Base.getI().getJobDirectory(pJob.getId());
        sysLog(localDir, " getoutput...");
        //iStatus = 5; //getoutput
        boolean ok = true;
        try {
            GSSCredential userProxy = GlobusConnectionHelper.makeCredentialFromFile(localDir + "/x509up");
            String resURL = jobexecutor.get(pJob.getId()).getJobData().resultURL;
            sysLog(localDir, " resURL:" + resURL);

            FileResourceImpl client = new FileResourceImpl();
            ServiceContact c = new ServiceContactImpl();
            //c.setHost(ftpHost);//gurlhost
            // c.setPort(2811);
            URI url = new URI(resURL.replace(" ", "%20"));
            c.setContact(resURL);
            client.setServiceContact(c);
            SecurityContext secu = new SecurityContextImpl();
            secu.setCredentials(userProxy);
            client.setSecurityContext(secu);
            client.start();
            String OutputDir = localDir + "/outputs";
//            try {
//                DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/" + "stderr.log"));
//                sysLog(localDir, " Y get: " + OutputDir + "/" + stderrlog);
//                client.get("" + url.getPath().replace("%20", " ") + "/" + stderrlog, sink, null);
//            } catch (Exception e) {
//                sysLog(localDir, " Y error " + e);
//                e.printStackTrace();
//                errorLog(localDir, "Can not copy the stderr.log file (" + stderrlog + "): " + e.getMessage());
//            }
            List<DataStagingType> outputs = OutputHandler.getLocalOutputs(pJob.getJSDL());
            for (DataStagingType op : outputs) {
                if (!"stderr.log".equals(op.getFileName()) &&
                        !"stdout.log".equals(op.getFileName()) &&
                        !"gridnfo.log".equals(op.getFileName()) &&
                        !"guse.jsdl".equals(op.getFileName()) &&
                        !"guse.logg".equals(op.getFileName())) {
                    try {
                        DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/" + op.getFileName()));
                        sysLog(localDir, " get: " + OutputDir + "/" + op.getFileName());
                        client.get("" + url.getPath().replace("%20", " ") + "/" + op.getFileName(), sink, null);

                    } catch (Exception e) {
                        ok = false;
                        sysLog(localDir, "Can not copy the Output file:" + op.getFileName() + " - " + e.getMessage());
                        errorLog(localDir, "Can not copy the Output file:" + op.getFileName() + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                }else if("stderr.log".equals(op.getFileName())){
                    try {
                        DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/" + "stderr.log"));
                        sysLog(localDir, " stderr.log get: " + OutputDir + "/" + op.getName());
                        client.get("" + url.getPath().replace("%20", " ") + "/" + op.getName(), sink, null);
                    } catch (Exception e) {
                        sysLog(localDir, " stderr.log error " + e);
                        e.printStackTrace();
                        errorLog(localDir, "Can not copy the stderr.log file (" + op.getName() + "): " + e.getMessage());
                    }
                }else if("stdout.log".equals(op.getFileName())){
                    try {
                        DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/" + "stdout.log"));
                        sysLog(localDir, " stdout.log get: " + OutputDir + "/" + op.getName());
                        client.get("" + url.getPath().replace("%20", " ") + "/" + op.getName(), sink, null);
                    } catch (Exception e) {
                        sysLog(localDir, " stdout.log error " + e);
                        errorLog(localDir, "Can not copy the stdout.log file (" + op.getName() + "): " + e.getMessage());
                    }
                }
            }

//            try {
//                DataSink sink = new DataSinkStream(new FileOutputStream(OutputDir + "/" + "stdout.log"));
//                sysLog(localDir, " X get: " + OutputDir + "/stdout.log -");
//                client.get("" + url.getPath().replace("%20", " ") + "/" + stdoutlog, sink, null);
//            } catch (Exception e) {
//                sysLog(localDir, " X error " + e);
//                errorLog(localDir, "Can not copy the stdout.log file (" + stdoutlog + "): " + e.getMessage());
//            }
            try {
                client.deleteDirectory("" + url.getPath().replace("%20", " "), true);
            } catch (Exception e) {
                e.printStackTrace();
                errorLog(localDir, "Warning: Cannot delete the jobs directory.");
            }
            client.stop();

            //Thread.sleep(5000);
        } catch (Exception e) {
            ok = false;
            e.printStackTrace();
            errorLog(localDir, e.getMessage());
        }

//        try {
//            //sysLog(localDir, "jobexecutor.terminate()");
//            //gclient.destroy();
//            jobexecutor.get(pJob.getId()).terminate();
//            //sysLog(localDir, "jobexecutor.terminate() end");
//        } catch (Exception clientex) {
//            clientex.printStackTrace();
//        }
//        jobexecutor.remove(pJob.getId());
        // deljob();
        return ok;
    }

    @Override
    protected void abort(Job pJob) throws Exception {
        String localDir = Base.getI().getJobDirectory(pJob.getId());
        sysLog(localDir, " ABORT ");
        errorLog(localDir, "- - - - - - - -");
        errorLog(localDir, "ABORTED by user");
        pJob.setStatus(ActivityStateEnumeration.FAILED);
        cleanup(pJob);
    }

    private void cleanup(Job pJob){
        jobexecutor.get(pJob.getId()).terminate();
        jobexecutor.remove(pJob.getId());
    }

    /** logs to stderr.log
     */
    private void errorLog(String pLocaldir, String txt) {
        try {
            FileWriter tmp = new FileWriter(pLocaldir + "outputs/stderr.log", true);
            BufferedWriter out = new BufferedWriter(tmp);
            out.newLine();
            out.write(txt);
            out.flush();
            out.close();
        } catch (Exception e) {
            sysLog(pLocaldir, e.toString());
        }
    }

    /** logs to stderr.log
     */
    private void errorLog(String pLocaldir, String pMsg, Exception pEx) {
        try {
            File f = new File(pLocaldir + "outputs/stderr.log");
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
            e.printStackTrace();
        }
    }

    /** logs to gridnfo.log
     */
    private void gridnfoLog(String pLocaldir, String txt) {
        try {
            FileWriter tmp = new FileWriter(pLocaldir + "outputs/gridnfo.log", true);
            BufferedWriter out = new BufferedWriter(tmp);
            out.newLine();
            out.write(txt);
            out.flush();
            out.close();
        } catch (Exception e) {
            sysLog(pLocaldir, e.toString());
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
}

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

import dci.data.Item;
import dci.data.Item.*;
import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;


import hu.sztaki.lpds.dcibridge.service.LB;
import hu.sztaki.lpds.dcibridge.util.BinaryHandler;
import hu.sztaki.lpds.dcibridge.util.InputHandler;
import hu.sztaki.lpds.dcibridge.util.LinuxWrapperForGrids;
import hu.sztaki.lpds.dcibridge.util.OutputHandler;
import hu.sztaki.lpds.dcibridge.util.XMLHandler;
//import hu.sztaki.lpds.submitter.grids.util.voms.AddVoms;
import hu.sztaki.lpds.submitter.grids.glite.config.GLiteConfig;
import hu.sztaki.lpds.submitter.grids.glite.status.DisabledHost;
import java.util.*;
import java.util.Hashtable;
import hu.sztaki.lpds.submitter.grids.glite.status.GStatusHandler;
import org.glite.wms.wmproxy.WMProxyAPI;
import org.glite.wms.wmproxy.JobIdStructType;
import org.glite.wms.wmproxy.StringAndLongType;
import org.glite.wms.wmproxy.StringAndLongList;
import org.glite.jdl.JobAd;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.util.GlobusURL;
import org.globus.util.Util;
import org.globus.io.urlcopy.UrlCopy;
import java.io.*;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.glite.wms.wmproxy.AuthenticationFaultException;
import org.glite.wms.wmproxy.AuthorizationFaultException;
import org.glite.wms.wmproxy.ServerOverloadedFaultException;
import org.glite.wms.wmproxy.ServiceException;
import org.ietf.jgss.GSSCredential;
import uri.mbschedulingdescriptionlanguage.OtherType;
import uri.mbschedulingdescriptionlanguage.SDLType;

/**
 *
 * gLite plugin
 */
public class Grid_glite extends Middleware{

    final static String WRAPPER ="wrapper.sh";
    final static String GENERATOR ="generator.tgz";
    final static String LOCALINPUTS = "localinputs.tgz";
    final static String LOCALOUTPUTS = "localoutputs.tgz";
    final static String INITRESOURCE="DCI-BRIDGE";

    final static int MAXINFILES = 1;//maximum num of inputsandbox files (incl exe, whidout WRAPPER)
    String EXECUTE ="execute.bin";
    final static String JDKEY="glite.key";

    static Object vomsLock = new Object();
    static int submitted=0;
    static int getout_ok=0;

    static int gop=0; // actual number of getoutput
    static int sub=0; // actual number of jobs to be submitted
    static int gst=0; // actual number of get states

    boolean abort=false;
    private static long maxstatuswait=600000;// kill, if job state time exceeds this threshold
    private static long maxdisablehost=1800000;//upper limit for ban a site from the JDL pool
    static Hashtable userproxy = new Hashtable(); //user proxy creation time

    private WMProxyAPI client=null;
    
/**
 * Constructor
 * @param pCount index of the thread handling the middleware
 */
    public Grid_glite() throws Exception{
        THIS_MIDDLEWARE=Base.MIDDLEWARE_GLITE;
        threadID++;
        setName("guse/dci-bridge:Middleware handler(glite) - "+threadID);
    }

    @Override
    public void setConfiguration()throws Exception {
            for (Item t : Conf.getMiddleware(Base.MIDDLEWARE_GLITE).getItem()) {
                try {
                    t.getGlite().setWms(GLiteConfig.getI().getWMProxyUrl(t.getName()));

                    System.out.println(t.getName() + " BDII:" + t.getGlite().getBdii());
                    System.out.println(t.getName() + " LFC:" + t.getGlite().getLfc());
                    System.out.println(t.getName() + " type:" + t.getGlite().getType());
                    System.out.println(t.getName() + " wms:" + t.getGlite().getWms());
                    System.out.println(t.getName() + " getAccessdata:" + t.getGlite().getAccessdata());
                    GLiteConfig.getI().voParam().put(t.getName(), t.getGlite());
                } catch (Exception e) {
                    t.setEnabled(false);
                    Base.writeLogg(THIS_MIDDLEWARE, new LB(e.getMessage() + " Vo " + t.getName() + " DISABLED!"));
                    System.out.println(e.getMessage() + " Vo " + t.getName() + " DISABLED!");
                }
            }
    }



    //private static HashMap<String, String> gridprops = new HashMap();

/*
        JobDefinitionType jsdl=pJob.getJSDL();
        String path=Base.getI().getJobDirectory(pJob.getId());
        String binname=BinaryHandler.getBinaryFileName(jsdl);
        String params=BinaryHandler.getCommandLineParameter(jsdl);
        String stdOut=BinaryHandler.getStdOutFileName(jsdl);
        String stdErr=BinaryHandler.getStdErrorFileName(jsdl);
        String portalid=BinaryHandler.getGroupName(jsdl);
        String userid = BinaryHandler.getUserName(jsdl);
 */

    /**
     * Submits the job
     * @param pJob
     * @throws java.lang.Exception
     */
    protected void submit(Job pJob) throws Exception {
        //Thread.sleep(5000);
        JobDefinitionType jsdl = pJob.getJSDL();
        String path = Base.getI().getJobDirectory(pJob.getId());
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String userid = BinaryHandler.getUserName(pType);
        String grid = pJob.getConfiguredResource().getVo();

        sysLog(path + "outputs/", "submit . path:" + path + " userid:" + userid + " grid:" + grid);

        //sysLog(path + "outputs/", "1sleep 5 000");
        //Thread.sleep(5000);
        try {
            //load resources conf
            //gridprops.put("primary.wms", getWms(grid));//"https://wms.ipb.ac.rs:7443/glite_wms_wmproxy_server");
            compressInputs(pJob);
            createWrapper(pJob);
            createJobad(pJob);

            if (!getProxy(path, grid, userid, false)) {
                System.out.println("failed creating proxy");
                pJob.setStatus(ActivityStateEnumeration.FAILED);
                //Thread.sleep(5000);
                return;
            }
            doSubmit(pJob);
        } catch (Exception e) {
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            errorLog(path + "outputs/", "Job submit failed. ", e);
            //e.printStackTrace();
        }

        //sysLog(path + "outputs/", "2sleep 5 000");
        //Thread.sleep(5000);

    }

    /*
     * resubmits the job without generating a new proxy
     */
    private void resubmit(Job pJob) throws Exception{
        JobDefinitionType jsdl = pJob.getJSDL();
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String userid = BinaryHandler.getUserName(pType);
        String path = Base.getI().getJobDirectory(pJob.getId());
        String grid = pJob.getConfiguredResource().getVo();

        //enablegetstatus=false;
        pJob.setMiddlewareStatus((byte)0);
        GStatusHandler.getI().getJob(pJob.getId()).setStatus(userid, 0);
        pJob.setStatus(ActivityStateEnumeration.PENDING);//status = 3;//"submitting";
        pJob.setResource(INITRESOURCE + " - resubmitting");//resource = "not defined - resubmitting";
        try {
            synchronized (GLiteConfig.getI().getLock()) {
                client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), path + "/x509up");
                sysLog(path + "outputs/"," ABORT  and resubmit");
                client.jobCancel(pJob.getMiddlewareId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client = null;
        }
        sub++;
        //sysLog(" resubmit submitstart :" + sub + "(db) - ");
        try {
            //load resources conf
            //gridprops.put("primary.wms", getWms(grid));//"https://wms.ipb.ac.rs:7443/glite_wms_wmproxy_server");
            createJobad(pJob);

            doSubmit(pJob);
        } catch (Exception e) {
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            errorLog(path + "outputs/", "Job resubmit failed. ", e);
            //e.printStackTrace();
        }
        sub--;
        //sysLog(path + "outputs/", "resubmit - end status:" + status+" resubmit time:"+(System.currentTimeMillis()-statuschanged) );
        //statuschanged=System.currentTimeMillis();
        //enablegetstatus=true;
        //return true;
    }

    /**
     * Is not used, the job output is get after getstatus or send by callback
     * @param pJob
     * @throws java.lang.Exception
     */
    protected void getOutputs(Job pJob) throws Exception {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

//    /**
//     * Get the wms url from the temp. config file
//     * @param vo
//     * @return
//     * @throws java.lang.Exception
//     */
//    private String getWms(String vo) throws Exception {
//        File wms = new File(System.getProperty("java.io.tmpdir") + "/dci_bridge/wms");
//        BufferedReader input = new BufferedReader(new FileReader(wms));
//        try {
//            String line = null;
//            while ((line = input.readLine()) != null) {
//                String[] kv = line.split("=");
//                if (vo.equals(kv[0].trim())) {
//                    return kv[1].trim();
//                }
//            }
//        } finally {
//            input.close();
//        }
//        throw new Exception("WMS for VO " + vo + " not set!");
//    }

    private boolean cp(String inf, String outf) {
        try {
            File inputFile = new File(inf);
            File outputFile = new File(outf);
            FileReader in = new FileReader(inputFile);
            FileWriter out = new FileWriter(outputFile);
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            in.close();
            out.close();
            Util.setFilePermissions(outf, 600);
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }

/**
 * Try to get proxy from cache, if failed create it
 * @param path
 * @param grid
 * @param userid
 * @param renew if TRUE, the proxy already exists, do not save as x509up_o
 * @return
 */
    private boolean getProxy(String path, String grid, String userid, boolean renew) {
        String proxy = path+"x509up";
        boolean success = true;
        synchronized (vomsLock) {
            try {
                if ((System.currentTimeMillis() - Long.parseLong("" + userproxy.get(userid + grid))) < 1800000) {
                    sysLog(path + "outputs/", "+--------- getProxy - use cache --------");
                    if (!renew) {
                        try {
                            new File(proxy).renameTo(new File(proxy + "_o"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            new File(proxy).delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    cp(getProxyCachedir() + userid + "/x509up." + grid, proxy);
                } else {
                    if (!renew) {
                        cp(proxy, proxy + "_o");
                    } else {
                        try {
                            new File(proxy).delete();
                            cp(proxy+"_o",proxy);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    success = createProxy(path, grid, userid);
                }
            } catch (Exception e) {
                sysLog(path + "outputs/", "+--------- getProxy - not cached --------");
                try {
                    cp(proxy, proxy+"_o");
                    //new File(proxy + "_o").renameTo(new File(proxy));
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                success = createProxy(path, grid, userid);
            }
        }
        return success;
    }

    /*
     * voms extension of a proxy + delegation
     */
    private boolean createProxy(String path, String grid, String userid) {
        String OutputDir = path+"outputs/";
        String proxy = path+"x509up";
        sysLog(OutputDir, "+--------- getProxy - start - proxy:" + proxy + "-------");
        try {
            GlobusCredential agcred = new GlobusCredential(proxy);
            GlobusGSSCredentialImpl agssproxy = new GlobusGSSCredentialImpl(agcred, GSSCredential.INITIATE_AND_ACCEPT);
            sysLog(OutputDir, "rem.life: " + agssproxy.getRemainingLifetime());
            if (agssproxy.getRemainingLifetime() < 600) {
                throw new Exception("Certificate expired. ");
            }

            if (addVomsC(path, grid)) {
                sysLog(OutputDir, "VOMS ext. succesfully added");
            } else {
                throw new Exception("Add VOMS - error. The remaining lifetime of the proxy in the submission time: "+calculateHMS(agssproxy.getRemainingLifetime()));
            }

//            try {
//                String p = PropertyLoader.getInstance().getProperty("glite." + grid + ".sleep");
//                int is = Integer.parseInt(p);
//                sysLog(OutputDir, " sleep (s): " + is);
//                try {
//                    Thread.sleep(is * 1000);
//                } catch (Exception e2) {
//                    e2.printStackTrace();
//                }
//            } catch (Exception e) {
//                sysLog(OutputDir, "glite." + grid + ".sleep DISABLED ");
//            }

            synchronized (GLiteConfig.getI().getLock()) {
                client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), proxy);
                sysLog(OutputDir, "getProxyReq .....");
                String rproxy = "";
                int ntry = 0;
                boolean btry = true;
                while (btry) {
                    try {
                        rproxy = client.getProxyReq(userid);//delegationId
                        btry = false;
                    } catch (AuthenticationFaultException e1){
                        //System.out.println("***                       !!! W A R N I N G !!!      Syncronise system clock!                ***");
                        retryOrThrowException(OutputDir, ntry++, 1, "Authentication Failed: " + e1.getMessage());
                    } catch (AuthorizationFaultException e2){
                        //System.out.println("***                       !!! W A R N I N G !!!      Syncronise system clock!                ***");
                        retryOrThrowException(OutputDir, ntry++, 1, "Authorization Failed: " + e2.getMessage());
                    } catch (ServerOverloadedFaultException e3){
                        retryOrThrowException(OutputDir, ntry++, 6, "Server is Overloaded: " + e3.getMessage());
                    } catch (ServiceException e4) {
                        if (e4.getMessage().contains("unsupported_certificate")){
                            throw new Exception("Your proxy is not valid! Please upload your user cert and key from command line to the myproxy server and download the new proxy. " +
                                    "You can use the following command:\n\n" +
                                    "myproxy-init -s myproxy.server.hostname -l MyProxyAccount -c 0 -t 100" +
                                    " \n\n\n WMS Service Error: " + e4.getMessage());//throw new Exception(excmsg);
                        }
                        retryOrThrowException(OutputDir, ntry++, 2, "WMS Service Error: " + e4.getMessage());
                    }
                }
                sysLog(OutputDir, "getProxyReq result [" + rproxy + "]");
                sysLog(OutputDir, "grstPutProxy .....");
                client.grstPutProxy(userid, rproxy);//delegationId=userid
            }
        } catch (Exception exc) {
            errorLog(OutputDir, "Preparation of the proxy for VO "+ grid + " failed. " , exc);
            sysLog(OutputDir, exc.toString());
            //exc.printStackTrace();
            //status = 7;// "Aborted";
            client = null;
            return false;
        } finally {
            client = null;
        }
        sysLog(OutputDir, "+-----------------------------------getProxy - succes-----------------------------------+");
        if (!new File(getProxyCachedir() + userid).exists()){
            new File(getProxyCachedir() + userid).mkdirs();
        }
        new File(getProxyCachedir() + userid + "/x509up."+grid).delete();
        cp(proxy,getProxyCachedir() + userid + "/x509up."+grid);
        userproxy.put("" + userid+grid, System.currentTimeMillis());
        sysLog(OutputDir, "userproxy creation times:"+userproxy);
        return true;
    }

    private void retryOrThrowException(String logdir, int numoftry, int maxnumoftry, String excmsg) throws Exception {
        if (numoftry < maxnumoftry) {
            sysLog(logdir, numoftry + "/" + maxnumoftry + " sleep 10sec msg:" + excmsg);
            try {
                Thread.sleep(10000);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } else {
            throw new Exception(excmsg);
        }
    }

    /*
     * Converts the value defined in seconds into a string having the form hour:minute:second 
     */
    private String calculateHMS(int timeInSeconds) {
        int hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        return (hours + ":" + minutes + ":" + seconds);
    }

    private String getProxyCachedir(){
        return Base.getI().getPath()+"proxycache/";
    }
    
    /**
     * Upload local inputs, and submit.
     * @param pJob
     * @return
     * @throws java.lang.Exception
     */
    private boolean doSubmit(Job pJob) throws Exception {
        POSIXApplicationType pType=XMLHandler.getData(pJob.getJSDL().getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String userid = BinaryHandler.getUserName(pType);
        String path=Base.getI().getJobDirectory(pJob.getId());
        String grid = pJob.getConfiguredResource().getVo();
        String proxy = path+"/x509up";
        String OutputDir = path + "outputs/";

        String[] reduced_path;

        sysLog(OutputDir, "+--------- doSubmit Contacting:" + GLiteConfig.getI().getWMProxyUrl(grid) + " -------");
        try {
            // Reads JDL
            JobAd jad = new JobAd();
            jad.fromFile(path+"outputs/job.jdl");
            String jdlString = jad.toString();

            sysLog(OutputDir, "jobRegister...");
            synchronized (GLiteConfig.getI().getLock()) {
                client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), proxy);
                JobIdStructType jobIDurl = client.jobRegister(jdlString, userid);//delegationId

                if (jobIDurl != null) {
                    pJob.setMiddlewareId(jobIDurl.getId());
                    sysLog(OutputDir, " submit successfull jobID:" + pJob.getMiddlewareId());
                    FileWriter furl = new FileWriter(path+"outputs/job.url");
                    BufferedWriter out = new BufferedWriter(furl);
                    out.write("" + pJob.getMiddlewareId());
                    out.flush();
                    out.close();
                } else {
                    sysLog(OutputDir, "SUBMIT E R R O R ! - Job submission failed. ");
                    errorLog(OutputDir, "Job submission failed. ");
                    client = null;
                    return false;
                }

                org.glite.wms.wmproxy.StringList InputSandboxURI = client.getSandboxDestURI(pJob.getMiddlewareId(), "gsiftp");
                //Convert listURI into a String.
                reduced_path = InputSandboxURI.getItem();
            }

            // Creation of the "toURL" link to copy the file(s).
            int pos = (reduced_path[0]).indexOf("2811");
            int length = (reduced_path[0]).length();
            String front = (reduced_path[0]).substring(0, pos);
            String rear = (reduced_path[0]).substring(pos + 4, length);
            String TURL = front + "2811/" + rear;
            GlobusCredential gcred = new GlobusCredential(proxy);
            GSSCredential gssproxy = new GlobusGSSCredentialImpl(gcred, GSSCredential.INITIATE_AND_ACCEPT);

            String toURL = TURL + "/" + LOCALINPUTS;
            String fromURL = "file:///" + path + "/" + LOCALINPUTS;
            sysLog(OutputDir, "fromURL:" + fromURL + " toURL:" + toURL);
            try {
                GlobusURL from = new GlobusURL(fromURL);
                GlobusURL to = new GlobusURL(toURL);
                UrlCopy uCopy = new UrlCopy();
                uCopy.setCredentials(gssproxy);
                uCopy.setDestinationUrl(to);
                uCopy.setSourceUrl(from);
                uCopy.setUseThirdPartyCopy(true);
                uCopy.copy();
            } catch (Exception e) {
                sysLog(OutputDir, "Can not copy the Input files:" + LOCALINPUTS + " - " + e.toString());
                errorLog(OutputDir, "Can not copy the Input files:" + LOCALINPUTS + " - " + e.getMessage());
                //e.printStackTrace();
                client = null;
                return false;
            }


            toURL = TURL + "/" + WRAPPER;
            fromURL = "file:///" + path + "/" + WRAPPER;
            sysLog(OutputDir, "fromURL:" + fromURL + " toURL:" + toURL);
            try {
                GlobusURL from = new GlobusURL(fromURL);
                GlobusURL to = new GlobusURL(toURL);
                UrlCopy uCopy = new UrlCopy();
                uCopy.setCredentials(gssproxy);
                uCopy.setDestinationUrl(to);
                uCopy.setSourceUrl(from);
                uCopy.setUseThirdPartyCopy(true);
                uCopy.copy();
            } catch (Exception e) {
                sysLog(OutputDir, "Can not copy the wrapper file:" + WRAPPER + " - " + e.toString());
                errorLog(OutputDir, "Can not copy the wrapper file:" + WRAPPER + " - " + e.getMessage());
                //e.printStackTrace();
                client = null;
                return false;
            }
            synchronized (GLiteConfig.getI().getLock()) {
                client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), proxy);
                client.jobStart(pJob.getMiddlewareId());
            }

            GStatusHandler.getI().initJobStatus(userid, pJob.getId(), 0);//init
            pJob.setStatus(ActivityStateEnumeration.RUNNING);
            pJob.setResource(grid + " WMS");
        } catch (Exception exc) {
            errorLog(OutputDir, "Job submit ERROR. \n " + exc.getMessage());
            sysLog(OutputDir, exc.toString());
            //exc.printStackTrace();
            //status = 7;// "Aborted";
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            client = null;
            return false;
        } finally {
            client = null;
        }
        sysLog(OutputDir, "+-----------------------------------doSubmit - succes-----------------------------------+");
        return true;
    }

    /**
     * Adds voms extension to the jobs proxy
     * @param localDir
     * @param voname
     * @return boolean
     */
    private boolean addVomsC(String localDir, String voname){
            try{
                //String addvomssext = PropertyLoader.getInstance().getProperty("prefix.dir")+"addvomsext/addvomsext";
                //String cmd =new String(addvomssext+" "+voname+" "+localDir+"/x509up"+ " "+localDir+"/x509upvoms");//
                String cmd ="voms-proxy-init -voms "+voname+": -noregen -out x509up";
                sysLog(localDir + "outputs/", "localDir:"+localDir+" cmd:"+cmd);
                sysLog(localDir + "outputs/", cmd);
                Process p;
                p = Runtime.getRuntime().exec(cmd,null,new File(localDir));
                BufferedReader sin = new BufferedReader(new InputStreamReader(p.getErrorStream() ));
                int exitv =p.waitFor();
                if (exitv==0){
                    sin.close();
                    return true;
                }
                else{
                    String sor="";
                    errorLog(localDir + "outputs/", cmd + " \n failed.");
                    while ((sor = sin.readLine()) != null) {
                      sysLog(localDir + "outputs/", sor);
                      errorLog(localDir + "outputs/", sor);
                    }
                    errorLog(localDir + "outputs/", "\n");
                    sin.close();
                    return false;
                }
            }catch(Exception e){
                //e.printStackTrace();
                return false;
            }
    }

    /**
     * Adds to disabled hosts
     * @param resource
     */
    private void addDisablehost(String resource) {
        if (!resource.contains(INITRESOURCE)) {//"not defined"
            DisabledHost.getI().add(resource);
        }

    }

    /**
     * Get jobs status, stores it in pJob
     * If finished or error, get outputs.
     * @param pJob
     */
    protected void getStatus(Job pJob) {
        String OutputDir = Base.getI().getJobDirectory(pJob.getId()) + "outputs/";
//        sysLog(OutputDir, "getStatus() called. current status:" + pJob.getStatus().value() + " abort:" + abort + " enablegetstatus:" + enablegetstatus);
        sysLog(OutputDir, "getStatus() called. current status:" + pJob.getStatus().value() + " abort:" + abort );

        int s = -1;
        try {
            s = GStatusHandler.getI().getJob(pJob.getId()).getStatus();
            if (s == 6 || s == 7) {//(jc.getJobPropertyValue("userid"), jobID, status);
                sysLog(OutputDir, "--- STATUS CALLBACK: JOB FINISHED, JOBCANCEL callback status:" + s + "  -------------------------");//resource
                if (pJob.getResource().contains("DCI-BRIDGE")) {//try 1x get resource
                    pJob.setResource(gresource(pJob));
                }

                //if (status != 6 && status != 7) {
                if ( !pJob.getStatus().equals(ActivityStateEnumeration.FINISHED) && !pJob.getStatus().equals(ActivityStateEnumeration.FAILED)){
                    //extract
                    if (!extractFile(Base.getI().getJobDirectory(pJob.getId()) + "outputs/", LOCALOUTPUTS)) {
                        sysLog(OutputDir, "Could not extract outputsand: " + LOCALOUTPUTS);
                        errorLog(OutputDir, "Could not extract outputsand: " + LOCALOUTPUTS);
                        pJob.setStatus(ActivityStateEnumeration.FAILED);
                        return;
                    }
                    if (!checkOutputFiles(pJob)) {
                        s = 7;
                        pJob.setStatus(ActivityStateEnumeration.FAILED);
                        return;
                    }else {
                        //sysLog(OutputDir, "sleep after callback 5000");
                        //Thread.sleep(5000);
                        pJob.setStatus(ActivityStateEnumeration.FINISHED);
                    }
                    cleanupJob(pJob);
                }
                GStatusHandler.getI().removeJob(pJob.getId());
                return;

            } else if (s == 66) {// there is no "curl" only "wget"
                ///gLite state must be controlled
                long elapsed = System.currentTimeMillis() - GStatusHandler.getI().getJob(pJob.getId()).getSetStatusTime();
                if (elapsed > maxstatuswait * 2) {
                    sysLog(OutputDir, "last try to get output");
                    gstatus(pJob);
                    if (pJob.getMiddlewareStatus() != 6 && pJob.getMiddlewareStatus() != 7) {//last try to get output
                        sysLog(OutputDir, "RESUBMIT JOB!! FINISHED, but could not get output - current status: " + pJob.getMiddlewareStatus() + " elapsed time: " + elapsed + " resource: " + pJob.getResource());
                        addDisablehost(pJob.getResource());
                        resubmit(pJob);
                        pJob.setStatus(ActivityStateEnumeration.PENDING);
                        return;
                    }
                }
                ////gstatus(pJob);// kieg!!!!!!! ???
            }


            if (abort || pJob.getStatus().equals(ActivityStateEnumeration.FAILED)) {//"Aborted"
                GStatusHandler.getI().removeJob(pJob.getId());
                return;
            }
//            //if (!(status.equals("getOutput")||status.equals("Done") || status.equals("submitting")))
//            if (status != 6 && enablegetstatus) {
//                gstatus();
//            }
            if (!pJob.getStatus().equals(ActivityStateEnumeration.FINISHED)) {
                gstatus(pJob);
            }

            //running, but no status callback until maxstatuswait
            //egyenlore letiltva
//            if (status == 5 && GStatusHandler.getI().getJob(pJob.getId()).getStatus() == 0) {
//                long elapsed = System.currentTimeMillis() - statuschanged;
//                if (elapsed > maxstatuswait * 2) {
//                    sysLog(OutputDir, "RESUBMIT JOB!! status is RUNNING, but no callback : status: " + status + " elapsed time: " + elapsed + " resource: " + resource);
//                    addDisablehost(resource);
//                    resubmit();
//                    return status;
//
//                }
//            }


            if ( pJob.getStatus().equals(ActivityStateEnumeration.FINISHED) || pJob.getStatus().equals(ActivityStateEnumeration.FAILED)){
                GStatusHandler.getI().removeJob(pJob.getId());
            }
        } catch (Exception e) {
            //e.printStackTrace();
            sysLog(OutputDir, "RETURN PREV STATUS.");
        }
        //return status;
    }

    /** Converts status string to byte status code
     *  @return status code
     */
    private byte toStatusCode(String st){
        if (st.equals("Submitted")) return 2;
        else if (st.equals("Waiting")) return 3;
        else if (st.equals("Ready")) return 10;
        else if (st.equals("Scheduled")) return 4;
        else if (st.equals("Running")) return 5;
        else if (st.equals("Cancelled")) return 7;
        else if (st.equals("Aborted")) return 7;
        else if (st.equals("Done")) return 6;
        else if (st.equals("Done error")) return 9;
        else if (st.equals("Cleared")) return 7;
        else if (st.equals("getOutput")) return 5;//disable getstatus
        else if (st.equals("submitting")) return 3;//disable getstatus
        return 20;
    }

    /** Converts status string to ActivityStateEnumeration status code
     *  @return status code
     */
    private ActivityStateEnumeration toActivityStatusCode(String st){
        if (st.equals("Submitted")) return ActivityStateEnumeration.PENDING;//2;
        else if (st.equals("Waiting")) return ActivityStateEnumeration.PENDING;//3;
        else if (st.equals("Ready")) return ActivityStateEnumeration.PENDING;//10;
        else if (st.equals("Scheduled")) return ActivityStateEnumeration.PENDING;//4;
        else if (st.equals("Running")) return ActivityStateEnumeration.RUNNING;//5;
        else if (st.equals("Cancelled")) return ActivityStateEnumeration.FAILED;//7;
        else if (st.equals("Aborted")) return ActivityStateEnumeration.FAILED;//7;
        else if (st.equals("Done")) return ActivityStateEnumeration.FINISHED;//6;
        else if (st.equals("Done error")) return ActivityStateEnumeration.PENDING;//9;
        else if (st.equals("Cleared")) return ActivityStateEnumeration.FAILED;//7;
        else if (st.equals("getOutput")) return ActivityStateEnumeration.RUNNING;//5;//disable getstatus
        else if (st.equals("submitting")) return ActivityStateEnumeration.PENDING;//3;//disable getstatus
        else if (st.equals("Done (Failed)")) return ActivityStateEnumeration.FAILED;//7;
        return ActivityStateEnumeration.PENDING;//20;
    }

    /**
     * Get job status from grid.
     * Resubmit/ get output if necessary.
     * @param pJob
     */
    private void gstatus(Job pJob) {
        String OutputDir = Base.getI().getJobDirectory(pJob.getId()) + "outputs/";
        String stat="";
        BufferedReader sin = null;
        BufferedReader sinerr = null;
        try{
            String cmd ="glite-wms-job-status "+pJob.getMiddlewareId();

            Process p;
            p = Runtime.getRuntime().exec(cmd,null,new File(Base.getI().getJobDirectory(pJob.getId())));//localDir
            sin = new BufferedReader(new InputStreamReader(p.getInputStream()));
            sinerr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            int exitv =p.waitFor();
            String sor;
            String msg="";
            boolean resubmit=false;
            sysLog(OutputDir, cmd);
            if (exitv == 0) {
                while ((sor = sin.readLine()) != null) {
                    if (sor.contains("Current Status:")) {
                        stat = sor.substring(16, sor.length()).trim();
                    } else if (sor.contains("Destination:")) {
                        String resource = sor.substring(13, sor.length()).trim();
                        pJob.setResource(resource);
                    } else if (sor.contains("hit job shallow retry count") || sor.contains("failed (LB query failed)")) {//Status Reason:
                        sysLog(OutputDir, sor);
                        resubmit = true;
                    }
                    msg += sor + "\n";
                //  sysLog(sor);
                }
                sin.close();

                if (pJob.getMiddlewareStatus() != toStatusCode(stat)) {//watching state change
/////******                if (!pJob.getStatus().equals(toActivityStatusCode(stat))) {//state change
                    sysLog(OutputDir, "gstat.exit:" + exitv + " Current Status:" + stat + " Destination:" + pJob.getResource());
//                    statuschanged = System.currentTimeMillis();
                    if (stat.equals("Done (Success)")) {
                        if (getOutputFilesAndPurge(pJob)){//;//getOutput();
                            pJob.setStatus(ActivityStateEnumeration.FINISHED);
                        }else{
                            pJob.setStatus(ActivityStateEnumeration.FAILED);
                        }
                        //sysLog(OutputDir, "end sleep 5000");
                        //Thread.sleep(5000);
                    } else if (stat.equals("Done (Exit Code !=0)") || stat.equals("Done (Failed)")) {
                        sysLog(OutputDir, msg);
                        getOutputFilesAndPurge(pJob);//getOutput();
                        errorLog(OutputDir, msg);
                        //sysLog(OutputDir, "end sleep 5000");
                        //Thread.sleep(5000);
                        pJob.setStatus(ActivityStateEnumeration.FAILED);//status = 7;// "Aborted";
                    } else if (!stat.equals("")) {
                        //status = toStatusCode(stat);
                        pJob.setStatus(toActivityStatusCode(stat)); 
                        pJob.setMiddlewareStatus(toStatusCode(stat));
                    }
                    if (stat.equals("Aborted") || stat.equals("Cancelled")) {
                        errorLog(OutputDir, msg);
                        sysLog(OutputDir, msg);
                        if (resubmit) {
                            resubmit(pJob);
                        }
                    }
                }
                else {//did not change, observation of state change has failed
                    long elapsed = System.currentTimeMillis() - pJob.getMiddlewareStatusChanged();
                    //if (elapsed > maxstatuswait && !status.equals("Running") && resource.indexOf("not defined")==-1 ) {
                    if (elapsed > maxstatuswait && pJob.getMiddlewareStatus() != 5 && pJob.getResource().indexOf(INITRESOURCE) == -1 && GStatusHandler.getI().getJob(pJob.getId()).getStatus()==0) {//and no status callback
                        sysLog(OutputDir, "RESUBMIT JOB!! current status: " + pJob.getMiddlewareStatus() + " elapsed time: " + elapsed + " resource: " + pJob.getResource());
                        //disablehost.put(pJob.getResource().split(":")[0], System.currentTimeMillis());
                        addDisablehost(pJob.getResource());
                        sysLog(OutputDir, "disablehost:" + DisabledHost.getI().getAll());
                        resubmit(pJob);
                    }
                }

            }else{//error during state query
                sysLog(OutputDir, "gstat.exit:" + exitv + " Current Status:" + stat + " Destination:" + pJob.getResource());
                while ((sor = sinerr.readLine()) != null) {
                    msg += sor + "\n";
                }
                sinerr.close();
                if (msg.contains("PROXY_EXPIRED")) {//renew voms
                    sysLog(OutputDir, "PROXY_EXPIRED -> renew voms");

                    boolean success=false;
                    synchronized (vomsLock) {
                        String path=Base.getI().getJobDirectory(pJob.getId());
                        String grid=pJob.getConfiguredResource().getVo();
                        JobDefinitionType jsdl=pJob.getJSDL();
                        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
                        String userid = BinaryHandler.getUserName(pType);
                        success=getProxy(path,grid,userid,true);
                    }
                    if (success){
                        return;
                    }
                } else if (msg.contains("server closed the connection, probably due to overload") ||
                        msg.contains("Connection timed out")) {
                    synchronized (GLiteConfig.getI().getLock()) {
                        try {sysLog(OutputDir, msg+" SLEEP 60 sec");Thread.sleep(60000);}catch(Exception err){err.printStackTrace();}
                    }
                    //return status;
                    return;
                } 
                errorLog(OutputDir, "It was not possible to query the status. \n "+msg);
                sysLog(OutputDir, msg);
                synchronized (GLiteConfig.getI().getLock()) {
                    try {sysLog(OutputDir, "SLEEP 60 sec");Thread.sleep(60000);}catch(Exception err){err.printStackTrace();}
                }
                pJob.setStatus(ActivityStateEnumeration.FAILED);//status = 7;// "Aborted";

            }
            //return status;
        }catch (Exception ex) {
            sysLog(OutputDir, "ERROR! gstatus"+ex.getMessage() );
            //ex.printStackTrace();
            pJob.setStatus(ActivityStateEnumeration.FAILED);//status = 7;// "Aborted";
        } finally {
            try {
                sin.close();
            } catch (Exception e) {
            }
            try {
                sinerr.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Get the jobs resource.
     * @param pJob
     * @return resource string
     */
    private String gresource(Job pJob) {
        String resource = pJob.getConfiguredResource().getVo();
        String OutputDir = Base.getI().getJobDirectory(pJob.getId()) + "outputs/";
        BufferedReader sin = null;
        try {
            String cmd = "glite-wms-job-status " + pJob.getMiddlewareId();
            Process p;
            p = Runtime.getRuntime().exec(cmd, null, new File(Base.getI().getJobDirectory(pJob.getId())));//localDir
            sin = new BufferedReader(new InputStreamReader(p.getInputStream()));
            int exitv = p.waitFor();
            String sor;
            sysLog(OutputDir, "gresource() cmd:" + cmd);
            if (exitv == 0) {
                while ((sor = sin.readLine()) != null) {
                    if (sor.contains("Destination:")) {
                        resource = sor.substring(13, sor.length()).trim();
                    }
                }
            }
        } catch (Exception e) {
            sysLog(OutputDir, "gresource() ERROR:" + e.getMessage());
        } finally {
            try {
                sin.close();
            } catch (Exception ee) {
            }
        }
        return resource;
    }

    /**
     * Get jobs local outputs.
     * @param pJob
     * @return
     */
    private boolean getOutputFilesAndPurge(Job pJob) {
        String grid = pJob.getConfiguredResource().getVo();
        String OutputDir=Base.getI().getJobDirectory(pJob.getId())+"outputs/";
        String proxy = Base.getI().getJobDirectory(pJob.getId())+"x509up";

        StringAndLongList result = null;
        StringAndLongType[ ] list = null;
        int size = 0;
        boolean succes=true;
        sysLog(OutputDir, "+--------------------------getoutput----------------------------------------------+");

        try {
            synchronized (GLiteConfig.getI().getLock()) {
                client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), proxy);
                sysLog(OutputDir, "getOutputFileList ...");
                result = client.getOutputFileList(pJob.getMiddlewareId(), "gsiftp");
            }
            if ( result != null ) {
                Vector outputf = getOutputSandboxFileNames(pJob);
                // list of files+their size
                list = (StringAndLongType[ ]) result.getFile();
               // size = list.length;
                size =outputf.size();
                if (list != null) {
                    if (list.length!=size){
                        sysLog(OutputDir, "Some file(s) listed in the output sandbox were not available..");
                        errorLog(OutputDir, "Some file(s) listed in the output sandbox were not available..");
                    }
                    sysLog(OutputDir, "Downloading output files ...");
                    GlobusCredential gcred = new GlobusCredential(proxy);
                    GSSCredential gssproxy =new GlobusGSSCredentialImpl(gcred,GSSCredential.INITIATE_AND_ACCEPT);
                    for (int i=0; i<size ; i++){
                        try {
                            // Creation of the "fromURL" link from where download the file(s).
                            int pos = (list[i].getName()).indexOf("2811");
                            int length = (list[i].getName()).length();
                            String front = (list[i].getName()).substring(0, pos);
                            String rear = (list[i].getName()).substring(pos + 4, length);
                            String fromURL = front + "2811/" + rear;

                            // Creation of the "toURL" link from where download the file(s).
                            String toURL = "file:///" + OutputDir + rear.substring(rear.lastIndexOf("/"));
                            sysLog(OutputDir, i + " get fromURL:" + fromURL + " toURL:" + toURL);
                            GlobusURL from = new GlobusURL(fromURL);
                            GlobusURL to = new GlobusURL(toURL);
                            UrlCopy uCopy = new UrlCopy();
                            uCopy.setCredentials(gssproxy);
                            uCopy.setDestinationUrl(to);
                            uCopy.setSourceUrl(from);
                            uCopy.setUseThirdPartyCopy(true);
                            uCopy.copy();
                        }catch (ArrayIndexOutOfBoundsException ae){
                            succes=false;
                            sysLog(OutputDir, i+"Can not copy the Output file:"+outputf.get(i)+" - the file does not exist."+ae.getMessage());
                            //ae.printStackTrace();
                            errorLog(OutputDir, "Can not copy the Output file:"+outputf.get(i)+" - the file does not exist.");
                        } catch (Exception e) {
                            succes=false;
                            sysLog(OutputDir, i+" Can not copy the Output file:"+outputf.get(i)+" - "+e);
                            //e.printStackTrace();
                            errorLog(OutputDir, "Can not copy the Output file:"+outputf.get(i)+" - "+e.getMessage());
                        }
                    }
                } else 	sysLog(OutputDir, "No output files for this job!");
            } else sysLog(OutputDir, "An empty list has been received");
            sysLog(OutputDir, "+--------------------------getoutput-Success---------------------------------------------+");
        } catch (Exception exc) {
            errorLog(OutputDir, exc.getMessage());
            sysLog(OutputDir, exc.toString());
            sysLog(OutputDir, "+--------------------------getoutput-FAILED-----------------------------------------------+");
            //exc.printStackTrace();
            succes=false;
        } finally {
            try {
                synchronized (GLiteConfig.getI().getLock()) {
                    client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), proxy);
                    sysLog(OutputDir, "jobPurge ...");
                    client.jobPurge(pJob.getMiddlewareId());
                }
            } catch (Exception exc) {
                sysLog(OutputDir, "jobPurge ERROR:" + exc.toString());
                //exc.printStackTrace();
            }
            client = null;
        }

        if (succes) {
            if (!extractFile(OutputDir, LOCALOUTPUTS)) {
                sysLog(OutputDir, "Could not extract outputsandbox: " + LOCALOUTPUTS);
                errorLog(OutputDir, "Could not extract outputsandbox: " + LOCALOUTPUTS);
                pJob.setStatus(ActivityStateEnumeration.FAILED);
                return false;
            }
            succes = checkOutputFiles(pJob);
        }

        return succes;
    }

    private Vector getOutputSandboxFileNames(Job pJob){
        JobDefinitionType jsdl=pJob.getJSDL();
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String stdErr=BinaryHandler.getStdErrorFileName(pType);
        Vector files = new Vector();
        files.add(stdErr);
        files.add(LOCALOUTPUTS);
        return files;

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
                sysLog(OutputDir, cmd);
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

    /**
     * Aborts the job.
     * @param pJob
     */
    public void abort(Job pJob) {
        GStatusHandler.getI().removeJob(pJob.getId());
        String path=Base.getI().getJobDirectory(pJob.getId());
        cleanupJob(pJob);
        errorLog(path+"outputs/", "- - - - - - - - \nABORTED by user");
        pJob.setStatus(ActivityStateEnumeration.CANCELLED);
    }

    /**
     * Abort job, and cleanup.
     * @param pJob
     */
    private void cleanupJob(Job pJob) {
        String path=Base.getI().getJobDirectory(pJob.getId());
        if (pJob.getMiddlewareId() != null) {
            synchronized (GLiteConfig.getI().getLock()) {
                try {
                    client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(pJob.getConfiguredResource().getVo()), path + "/x509up");
                    sysLog(path + "outputs/", " ABORT - jobcancel");
                    client.jobCancel(pJob.getMiddlewareId());
                } catch (Exception e) {
                    try {
                        sysLog(path + "outputs/", "ABORT - jobcancel failed -> jobPurge ...   reason:" + e.getMessage());
                        client.jobPurge(pJob.getMiddlewareId());
                    } catch (Exception ee) {
                        //ee.printStackTrace();
                    }
                } finally {
                    client = null;
                }
            }
        }
    }

    /** Generation of the a WRAPPER script
     */
    private void createWrapper(Job pJob) throws Exception{
        JobDefinitionType jsdl=pJob.getJSDL();
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String userid = BinaryHandler.getUserName(pType);
        String path=Base.getI().getJobDirectory(pJob.getId());
        String binname=BinaryHandler.getBinaryFileName(pType);
        String params="";
        for(String t:BinaryHandler.getCommandLineParameter(pType)) params=params.concat(" "+t);
        String stdOut=BinaryHandler.getStdOutFileName(pType);
        String stdErr=BinaryHandler.getStdErrorFileName(pType);
        String grid = pJob.getConfiguredResource().getVo();//vo name

        Vector localoutfiles = new Vector();
        List<DataStagingType> loutputs = OutputHandler.getLocalOutputs(pJob.getJSDL());
        for (DataStagingType t : loutputs) {
            if (!("guse.jsdl".equals(t.getFileName()) ||
                    "guse.logg".equals(t.getFileName()))) {
                localoutfiles.add(t.getFileName());
            }
        }

        LinuxWrapperForGrids w = null;
        try {
            w = new LinuxWrapperForGrids(path);
            String callbackStatusServletUrl = null;
            try {
                String callback = Conf.getP().getCallbackurl();
                if (callback == null || "".equals(callback.trim())) {
                    callbackStatusServletUrl = null;
                } else {
                    if (callback.endsWith("/")) {
                        callbackStatusServletUrl = callback + "JobStatusServlet";
                    } else {
                        callbackStatusServletUrl = callback + "/JobStatusServlet";
                    }
                }
            } catch (Exception e) {
            }
            w.setLocalOutsAndCallback(localoutfiles, LOCALOUTPUTS, callbackStatusServletUrl, userid, pJob.getId());
            int addremoteio = 0;
            addremoteio = LinuxWrapperForGrids.FUNCTION_LCGREMOTEIO;
            w.addFunctionsAndStartScript(null, addremoteio);
            w.export_LD_LIBRARY_PATH();

            w.writeln("export LCG_CATALOG_TYPE=lfc");
            try {
                w.writeln("export LFC_HOST=" + GLiteConfig.getI().voParam().get(grid).getLfc());
                w.writeln("export LCG_GFAL_INFOSYS=" + GLiteConfig.getI().voParam().get(grid).getBdii());
            } catch (Exception e) {
                sysLog(path + "outputs/", "Could not get LFC_HOST and LCG_GFAL_INFOSYS.");
                e.printStackTrace();
            }

            // w.writeln("export LCG_GFAL_INFOSYS=bdii.ipb.ac.rs");

            w.extractAndDelete(LOCALINPUTS);
            //app.tgz eseten
            if (BinaryHandler.isAppTgzExtension(jsdl)) {
                w.extractAndDelete(binname);
                binname = BinaryHandler.getAppTgzBase(jsdl);
            }
            /** remote inputs */
            List<DataStagingType> rinputs = InputHandler.getRemoteInputs(pJob);
            for (DataStagingType t : rinputs) {
                w.writeln("lcgdownload " + grid + " \"" + t.getSource().getURI() + "\" \"" + t.getFileName() + "\" ");
            }

            if (BinaryHandler.isMPI(jsdl)) {
                //get nodenumber
                int nodenumber =0;
                try {
                    nodenumber = (int)jsdl.getJobDescription().getResources().getTotalCPUCount().getUpperBoundedRange().getValue();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                w.runMPI(binname, params, stdOut, stdErr, "" + nodenumber);
            } else if (BinaryHandler.isJavaJob(jsdl)) {
                w.setJavaEnviroments(Conf.getP().getJava());
                w.runJava(binname, params, stdOut, stdErr);
            } else {
                w.runBinary(binname, params, stdOut, stdErr);
            }

            /**@Todo remote outputs*/            
            List<DataStagingType> routputs = OutputHandler.getRemoteOutputs(pJob.getJSDL());
            for (DataStagingType t : routputs) {
                String s_lfn = t.getTarget().getURI();
                //..value22/0/       pert levág          -> ..value22_0
//                if (s_lfn.endsWith("/")){
//                    s_lfn=s_lfn.substring(0, s_lfn.lastIndexOf("/"));
//                }
                //String se = " -d se01.isabella.grnet.gr";//storage element
                String se = "";//storage element
                if (t.getFilesystemName()!=null && t.getFilesystemName().length()>0){
                    se = " -d " + t.getFilesystemName();
                }
                String mkdir = s_lfn.substring(4, s_lfn.lastIndexOf("/"));
                w.writeln("lfc-mkdir -p " + mkdir + " >> gridnfo.log ");
                w.writeln("lcgupload " + s_lfn + " " + t.getFileName() + " " + grid + " \"" + se + "\" ");
            }

        } finally {
           w.close();
        }
    }

    /**
     * Generates the JDL
     * @param pJob
     * @return
     */
    private JobAd createJobad(Job pJob){
        JobDefinitionType jsdl=pJob.getJSDL();
        String path=Base.getI().getJobDirectory(pJob.getId());
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String stdErr=BinaryHandler.getStdErrorFileName(pType);
        String OutputDir = path + "outputs/";
        String voname=pJob.getConfiguredResource().getVo();
        
//        String jobtype = null;
//        if (BinaryHandler.isJavaJob(jsdl)){
//            jobtype = "Java";
//        }
        
        JobAd jobad = new JobAd() ;
        try{
            jobad.addAttribute("Executable",WRAPPER);
            jobad.addAttribute("StdOutput","stdout.log");
            jobad.addAttribute("StdError","stderr.log");
            jobad.addAttribute("VirtualOrganisation",voname);


            if (BinaryHandler.isMPI(jsdl)) {
                //get nodenumber
                int nodenumber =0;
                try {
                    nodenumber = (int)jsdl.getJobDescription().getResources().getTotalCPUCount().getUpperBoundedRange().getValue();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                jobad.addAttribute("JobType", "MPICH");
                jobad.addAttribute("NodeNumber", nodenumber);//Integer.parseInt( ((String)jc.getJobPropertyValue("nodenumber")) ) /// !!!!!!!!!! NODEnumber nincs megadva!!!!!
            } else {
                jobad.addAttribute("JobType", "Normal");
            }

//            if (OutputSandboxFiles!=null){
//                for (int i=0;i<OutputSandboxFiles.length;i++){
//                        jobad.addAttribute("OutputSandbox",""+OutputSandboxFiles[i]);
//                }
//            }
            jobad.addAttribute("OutputSandbox",""+stdErr);//error.log
            jobad.addAttribute("OutputSandbox",""+LOCALOUTPUTS);
            jobad.addAttribute("InputSandbox",""+WRAPPER);
            jobad.addAttribute("InputSandbox",""+LOCALINPUTS);
//            if (InputSandboxFiles!=null){
//                for (int i=0;i<InputSandboxFiles.length;i++){
//                        jobad.addAttribute("InputSandbox",""+InputSandboxFiles[i]);
//                }
//            }

            /*add user specific jdl attributes */
            SDLType sdlType = XMLHandler.getData(jsdl.getAny(), SDLType.class);
            List ls = sdlType.getConstraints().getOtherConstraint();
            Iterator it = ls.iterator();

            String userreq="";
            while (it.hasNext()) {
                OtherType value = (OtherType) it.next();
                //System.out.println("Value :" + value.getName()+" = "+value.getValue());

                if(value.getName().indexOf(JDKEY)>-1){
                    String sKey = value.getName().replaceAll(JDKEY,"");
                    String sValue = value.getValue();
                    sysLog(OutputDir, "KEY: "+sKey);
                    sysLog(OutputDir, " Value: "+sValue);
                    if (!(sValue).trim().equals("")){
                        try{
                            if (sKey.equals("requirements")){
                                if (sValue.contains("other.GlueCEStateStatus") ){
                                    userreq=sValue;
                                }else{
                                    userreq="(other.GlueCEStateStatus==\"Production\") && ("+sValue+")";
                                }
                            }else if (sKey.equals("Rank")){
                                jobad.setAttributeExpr(sKey,sValue);
                            }else if (sKey.equals("ShallowRetryCount") ){
                                jobad.addAttribute(sKey,Integer.parseInt( sValue ));
                            }else if (sKey.equals("RetryCount") ){
                                jobad.addAttribute(sKey,Integer.parseInt( sValue ));
                            }else if (sKey.equals("Environment") ){
//                                jobad.setAttribute("Environment",envs+" "+sValue );
                            }else{
                                jobad.addAttribute(sKey, sValue);
                            }
                        }catch (Exception e){
                            //e.printStackTrace();
                                //sysLog(e.toString());errorLog(e.getMessage());
                        }
                    }
                }
            }

            try {
                String myproxy = sdlType.getConstraints().getMiddleware().get(0).getMyProxy().getServerName();
                jobad.addAttribute("MyProxyServer", myproxy);
            } catch (Exception e) {
                //e.printStackTrace();
            }

            //mandatory..
            //excluding of erroneous sites
            String ds = "";
            String req = "";
            Object[] sites = DisabledHost.getI().getAll().keySet().toArray();
            for (int s = 0; s < sites.length; s++) {
                long disabled = System.currentTimeMillis() - DisabledHost.getI().get((String)sites[s]);//Long.parseLong("" + disablehost.get(sites[s]));
                if (disabled > maxdisablehost) {// enable branch
                    DisabledHost.getI().remove((String)sites[s]);
                } else {// prohibit branch: if "userreq" does not conatins the host
                    if (!userreq.contains(""+sites[s])){
                        ds = ds.concat("other.GlueCEInfoHostname!=\"" + sites[s] + "\" ");
                    }
                }
            }
            ds = ds.trim().replaceAll(" ", " && ");
            if (!"".equals(ds) && !"".equals(userreq)) {
                req = userreq + " && ( " + ds + " )";
            } else if ("".equals(ds) && !"".equals(userreq)) {
                req = userreq;
            } else if (!"".equals(ds) && "".equals(userreq)) {
                req = "(other.GlueCEStateStatus==\"Production\") && ( " + ds + " )";
            } else {
                req = "other.GlueCEStateStatus==\"Production\"";
            }
            sysLog(OutputDir, "disablehost:" + DisabledHost.getI().getAll());
            sysLog(OutputDir, "requirements:" + req);
            try {jobad.setAttributeExpr("requirements", req);
            } catch (Exception e) {sysLog(OutputDir, "requirements: " + e.toString());errorLog(OutputDir, "Error in JDL attribute requirements: \""+req+"\" ", e);return null;}

            try{jobad.setAttributeExpr("Rank","-other.GlueCEStateEstimatedResponseTime");
            }catch (Exception e){sysLog(OutputDir, "Rank: "+e.toString());}
//            if (!jobad.hasAttribute("RetryCount")) {
//                try {
//                    String p = PropertyLoader.getInstance().getProperty("glite." + voname + ".jdl.RetryCount");
//                    jobad.addAttribute("RetryCount", Integer.parseInt(p));
//                } catch (Exception e) {
//                    sysLog(OutputDir, "RetryCount not defined, set to 2: " + e.toString());
//                    jobad.addAttribute("RetryCount", 2);
//                }
//                jobad.addAttribute("RetryCount", 2);
//            }
            if (!jobad.hasAttribute("ShallowRetryCount")) {
//                try {
//                    String p = PropertyLoader.getInstance().getProperty("glite." + voname + ".jdl.ShallowRetryCount");
//                    jobad.addAttribute("ShallowRetryCount", Integer.parseInt(p));
//                } catch (Exception e) {
//                    sysLog(OutputDir, "ShallowRetryCount not defined, set to 2: " + e.toString());
//                    jobad.addAttribute("ShallowRetryCount", 2);
//                }
                jobad.addAttribute("ShallowRetryCount", 3);
            }
            //   sysLog(jobad.toSubmissionString());
            sysLog(OutputDir, jobad.toLines());
            try{
                FileWriter tmp = new FileWriter(path+"outputs/job.jdl", false);
                BufferedWriter   out = new BufferedWriter(tmp);
                out.write(jobad.toLines());
                out.flush();
                out.close();
            }catch (Exception e){sysLog(OutputDir, e.toString());}
        }catch (Exception e){e.printStackTrace();return null;}
        return jobad;
    }

    /** log info in "stderr.log" 
     */
    private void errorLog(String OutputDir, String txt){
        try{
            FileWriter tmp = new FileWriter(OutputDir+"/stderr.log",true);
            BufferedWriter   out = new BufferedWriter(tmp);
            out.newLine();
            out.write(txt);
            out.flush();
            out.close();
        }catch (Exception e){sysLog(OutputDir, e.toString());}
    }

    /** log info in "stderr.log" 
     */
    private void errorLog(String OutputDir, String pMsg, Exception pEx) {
        try {
            File f = new File(OutputDir + "/stderr.log");
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            fw.write(pMsg + "\n");
            fw.write(pEx.getMessage() + "\n");
            if (pEx.getCause()!=null){
                fw.write(pEx.getCause().getMessage()+ "\n");
            }
            fw.write("\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** log info in "gridinfo.log" 
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

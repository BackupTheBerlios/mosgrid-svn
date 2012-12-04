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
 * Submission of local job
 */

package hu.sztaki.lpds.submitter.grids;


import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.service.LB;
import hu.sztaki.lpds.dcibridge.util.BinaryHandler;
import hu.sztaki.lpds.dcibridge.util.LinuxWrapper;
import hu.sztaki.lpds.dcibridge.util.OutputHandler;
import hu.sztaki.lpds.dcibridge.util.XMLHandler;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;

/**
 * @author krisztian karoczkai
 */
public class Grid_local extends Middleware {
        private static final int WAITINGHW=10000;
    

/**
 * Constructor, loading  of the configuration
 * @param pCount index of thread handling the middleware
 */
    public Grid_local() {
        THIS_MIDDLEWARE=Base.MIDDLEWARE_LOCAL;
        threadID++;
        setName("guse/dci-bridge:Middleware handler("+THIS_MIDDLEWARE+") - "+threadID);
    }

/**
* @see hu.sztaki.lpds.submitter.grids.Middleware#submit(hu.sztaki.lpds.dcibridge.service.Job)
*/
@Override
    protected void submit(Job pJob) throws Exception{
        pJob.setResource(pJob.getConfiguredResource().getVo());
        LinuxWrapper w=null;
        JobDefinitionType jsdl=pJob.getJSDL(); 
        String path=Base.getI().getJobDirectory(pJob.getId());
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String binname=BinaryHandler.getBinaryFileName(pType);
        String params="";
        for(String s:BinaryHandler.getCommandLineParameter(pType)) params=params.concat(" "+s);
        String stdOut=BinaryHandler.getStdOutFileName(pType);
        String stdErr=BinaryHandler.getStdErrorFileName(pType);
        pJob.setPubStatus(ActivityStateEnumeration.RUNNING);
        pJob.setStatus(ActivityStateEnumeration.RUNNING);
        Process p=null;
        try{
            w=new LinuxWrapper(path);
            w.cd();
            if(BinaryHandler.isAppTgzExtension(jsdl)){
                w.unCompressBinary();
                binname=BinaryHandler.getAppTgzBase(jsdl);
            }
            if(BinaryHandler.isJavaJob(jsdl)){
                w.setJavaEnviroments(System.getenv("java.home"));
                w.runJava(binname, params, path+"outputs/"+stdOut, path+"outputs/"+stdErr);
            }
            else{
                w.setPermission(binname);
                w.setBinaryEnviroments();
                w.runBinary(binname, params, path+"outputs/"+stdOut, path+"outputs/"+stdErr);
            }
            w.close();
            boolean fl=true;
            while(fl){
                try{
                    FileReader fr=new FileReader(w.getFilepath());
                    fl=false;
                }
                catch(Exception e){
                    Base.writeLogg(THIS_MIDDLEWARE, new LB(e));
                    try{sleep(WAITINGHW);}
                    catch(InterruptedException e0){}
                }
            }
            String[] cmds=new String[1];
            cmds[0]=path+w.getWrapperName();
            p = Runtime.getRuntime().exec(cmds,null, new File(path+"outputs"));
            p.waitFor();
            if(p.exitValue()==0) pJob.setStatus(ActivityStateEnumeration.FINISHED);
            else pJob.setStatus(ActivityStateEnumeration.FAILED);
            p.destroy();
            w.writeFile("gridnfo.log", "local resource", true);
        } catch(Exception e) {
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            try {w.writeFile(stdErr, e, true);}
            catch(Exception ee){Base.writeJobLogg(pJob, ee, "error.local.writestderr:"+stdErr);}
            Base.writeJobLogg(pJob, e, "error.local.running");
        }
    }
/**
* @see hu.sztaki.lpds.submitter.grids.Middleware#abort(hu.sztaki.lpds.dcibridge.service.Job)
*/
    @Override
    protected void abort(Job pJob) throws Exception {}
/**
* @see hu.sztaki.lpds.submitter.grids.Middleware#getOutputs(hu.sztaki.lpds.dcibridge.service.Job)
*/
    @Override
    protected void getOutputs(Job pJob) throws Exception {
        List<DataStagingType> outputs=OutputHandler.getOutputs(pJob.getJSDL());
        File workDir=new File(Base.getI().getJobDirectory(pJob.getId()));
        String[] files=workDir.list();
        for(DataStagingType t:outputs)
            for(String s:files){
                if(s.startsWith(t.getFileName())){
                    File f=new File(Base.getI().getJobDirectory(pJob.getId())+s);
                    f.renameTo(new File(Base.getI().getJobDirectory(pJob.getId())+"outputs/"+s));
                }
            }
    }
/**
* @see hu.sztaki.lpds.submitter.grids.Middleware#getStatus(hu.sztaki.lpds.dcibridge.service.Job)
*/
    @Override
    protected void getStatus(Job pJob) throws Exception {}
}
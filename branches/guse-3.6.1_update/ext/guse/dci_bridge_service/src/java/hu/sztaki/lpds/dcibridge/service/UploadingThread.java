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
/**
 * Uploading the result files 
 */

package hu.sztaki.lpds.dcibridge.service;

import hu.sztaki.lpds.dcibridge.util.OutputHandler;
import hu.sztaki.lpds.dcibridge.util.io.HttpHandler;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;

/**
 * @author krisztian karoczkai
 */
public class UploadingThread extends Thread{

    private BlockingQueue<Job> jobs=new LinkedBlockingQueue<Job>();
/**
 * Constructor
 */
    public UploadingThread() {
        setName("guse/dci-bridge:output file uploadThread("+System.currentTimeMillis()+")");
    }


/**
 * Add a new job to the pool
 * @param pValue
 * @throws java.lang.InterruptedException
 */
    public void addJob(Job pValue) throws InterruptedException{
        Base.initLogg(pValue.getId(), "logg.job.adduploadqueue");
        jobs.put(pValue);
        Base.endJobLogg(pValue, LB.INFO,"");
    }


/**
 * @see Thread#run()
 */
    @Override
    public void run() {

        Job tmp;
        while(true){
            try{
                tmp=jobs.take();
                if(isEndStatus(tmp)){
                    try{uploadFiles(tmp);}
                    catch(Exception e){Base.writeJobLogg(tmp, e,"error.output.upload");}
                }
                tmp.setPubStatus(tmp.getStatus());
                Base.getI().removeJob(tmp.getId());
            }
            catch(InterruptedException e){Base.writeLogg(Base.MIDDLEWARE_SYSTEM, new LB(e));}
        }
    }

    protected boolean isEndStatus(Job job){
        return job.getStatus().equals(ActivityStateEnumeration.FINISHED) ||
                job.getStatus().equals(ActivityStateEnumeration.FAILED);
    }

    private  void uploadFiles(Job pJob) throws IOException{
        List<DataStagingType> outputs=OutputHandler.getLocalOutputs(pJob.getJSDL());
        File workDir=new File(Base.getI().getJobDirectory(pJob.getId())+"outputs/");
        String[] files=workDir.list();
        List<File> upload;
        for(DataStagingType t:outputs){
            upload=new ArrayList<File>();
            for(String s:files){
                if(s.startsWith(t.getFileName())){
                    File f=new File(Base.getI().getJobDirectory(pJob.getId())+"outputs/"+s);
                    if(f.exists()){
                        if ("REMOTEGENERATORS_PID".equals(f.getName())){
                            String remotegeneratorsPath = f.getAbsolutePath();
                            File foriginal=new File(remotegeneratorsPath+"_original");
                            f.renameTo(foriginal);
                            replaceRemoteGenerators(remotegeneratorsPath, OutputHandler.getRemoteOutputs(pJob.getJSDL()));
                            f = new File(remotegeneratorsPath);
                        }
                        upload.add(f);
                        Base.writeJobLogg(pJob.getId(), LB.INFO, "uploading:"+s);
                    }
                }
            }
            HttpHandler http=new HttpHandler();
            http.write(t.getTarget().getURI(), upload);
            http.close();
        }
    }

    /**
     * Replaces internal file name strings to port names
     * @param pFilePath
     * @param remoteouts
     */
    private void replaceRemoteGenerators(String pFilePath, List<DataStagingType> remoteouts) {
        //System.out.println("replaceRemoteGenerators REMOTEGENERATORS_PID!");
        String newPids = "";
        try {
            BufferedReader input = new BufferedReader(new FileReader(pFilePath + "_original"));
            try {
                String line = null;
                while ((line = input.readLine()) != null) {
                    if (line.contains("#")) {
                        //System.out.println("line:" + line);
                        String[] l = line.trim().split("#", 2);
                        //newPids+=l[0]+"pp#"+l[1]+"\n";
                        for (DataStagingType t : remoteouts) {
                            if (l[0].equals(t.getFileName())) {
                                //System.out.println(l[0]+" found, replace it:"+t.getName());
                                newPids += t.getName() + "#" + l[1] + "\n";
                            }
                        }
                    }
                }
            } finally {
                input.close();
            }
            writeToFile(pFilePath, newPids.trim());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void writeToFile(String pFilePath, String txt) throws IOException {
        //System.out.println("writeToFile:" + pFilePath + " " + txt);
        FileWriter tmp = new FileWriter(pFilePath, true);
        BufferedWriter out = new BufferedWriter(tmp);
        out.write(txt);
        out.flush();
        out.close();
    }

}

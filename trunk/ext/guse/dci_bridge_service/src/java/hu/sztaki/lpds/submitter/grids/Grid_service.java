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
 * Implementation of job submissions of type "service call" 
 */

package hu.sztaki.lpds.submitter.grids;

import dci.data.Configure.*;
import dci.data.Item;
import dci.data.Item.*;
import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.util.OutputHandler;
import hu.sztaki.lpds.submitter.grids.service.Axis;
import hu.sztaki.lpds.submitter.grids.service.ServiceCall;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.ConcurrentHashMap;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;

/**
 * @author krisztian karoczkai
 */
public class Grid_service extends Middleware{
    
    private ConcurrentHashMap<String,ServiceCall> serviceTypes=new ConcurrentHashMap<String, ServiceCall>();

/**
 * Constructor
 * @param pCount index of thread handling the midleware
 */
    public Grid_service() {
        THIS_MIDDLEWARE=Base.MIDDLEWARE_SERVICE;
        threadID++;
        setName("guse/dci-bridge:Middleware handler(Service) - "+threadID);
        serviceTypes.put("service", new Axis());
    }


    @Override
    protected void abort(Job pJob) throws Exception {}

    @Override
    protected void submit(Job pJob) throws Exception {
        try{
            Object res=serviceTypes.get(pJob.getConfiguredResource().getMiddleware()).call(pJob);

            File f=new File(Base.getI().getJobDirectory(pJob.getId())+"outputs/"+OutputHandler.getOutputs(pJob.getJSDL()).get(0).getName());
            f.createNewFile();
            FileWriter fw=new FileWriter(f);
            fw.write((String)res);
            fw.flush();
            fw.close();
            pJob.setStatus(ActivityStateEnumeration.FINISHED);
        }
        catch(Exception e){
            File f=new File(Base.getI().getJobDirectory(pJob.getId())+"outputs/stderr.log");
            f.createNewFile();
            FileWriter fw=new FileWriter(f);
            fw.write("Middleware:"+pJob.getConfiguredResource().getMiddleware());
            fw.write("VO:"+pJob.getConfiguredResource().getVo());
            fw.write("Resource:"+pJob.getConfiguredResource().getResource());
            fw.write("Jobmanager:"+pJob.getConfiguredResource().getJobmanager());
            fw.write(e.getMessage()+"\n");
            for(int ii=0;ii<e.getStackTrace().length;ii++)
            fw.write(e.getStackTrace()[ii].toString()+"\n");
            fw.flush();
            fw.close();
            pJob.setStatus(ActivityStateEnumeration.FAILED);
        }
    }

    @Override
    protected void getOutputs(Job pJob) throws Exception {}

    @Override
    protected void getStatus(Job pJob) throws Exception {}

}

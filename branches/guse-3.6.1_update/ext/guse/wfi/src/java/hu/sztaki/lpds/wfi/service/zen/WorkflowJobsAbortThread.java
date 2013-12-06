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
 * Futo wf megszakitasat hatter folyamatkent vegzo osztaly
 */
package hu.sztaki.lpds.wfi.service.zen;

import hu.sztaki.lpds.dcibridge.client.SubmitterFace;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.wfi.zen.pools.JobInstanceReferenceBean;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.ggf.schemas.bes._2006._08.bes_factory.TerminateActivitiesType;

/**
 * @author krisztian karoczkai
 */
public class WorkflowJobsAbortThread extends Thread {
    private static WorkflowJobsAbortThread instance=new WorkflowJobsAbortThread();
    private BlockingQueue<JobInstanceReferenceBean> jobs=new LinkedBlockingQueue<JobInstanceReferenceBean>();//megszakitando wf belso azonositoja

    /**
     * Ures Konstructor
     */
    public WorkflowJobsAbortThread() {}

    public static WorkflowJobsAbortThread getInstance() {return instance;}

    public static void setInstance(WorkflowJobsAbortThread instance) {WorkflowJobsAbortThread.instance = instance;}

    public BlockingQueue<JobInstanceReferenceBean> getJobs() {return jobs;}

    public void setJobs(BlockingQueue<JobInstanceReferenceBean> jobs) {this.jobs = jobs;}


    @Override
    public void run() {
        setPriority(MIN_PRIORITY);
        SubmitterFace client;
        JobInstanceReferenceBean tmp;
        TerminateActivitiesType abort;
        while(Base.getI().applicationrun){
            try{
                tmp=jobs.take();
                client=(SubmitterFace)InformationBase.getI().getServiceClient("submitter", "wfi");
                abort=new TerminateActivitiesType();
                abort.getActivityIdentifier().add(tmp.getJobid());
                client.terminateActivities(abort);
            }
            catch(Exception e){/*abort hiba*/}
        }
    }
}

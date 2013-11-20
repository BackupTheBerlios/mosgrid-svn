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

package hu.sztaki.lpds.wfi.net.wsaxis13.zen;

import hu.sztaki.lpds.wfi.zen.pools.RunableInstanceBean;
import hu.sztaki.lpds.wfi.zen.pools.WorkHandler;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.wfs.utils.Status;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStatusType;
/**
 *
 * @author krisztian karoczkai
 */
@WebService()
public class JobStatusService {

    /**
     * Web service operation
     */
    @WebMethod(operationName = "sendStatus")
    public String sendStatus(@WebParam(name = "pJobID")
    String pJobID, @WebParam(name = "pJobStatus")
    ActivityStatusType pJobStatus, @WebParam(name = "pResource")
    String pResource) {
RunableInstanceBean tmp=WorkHandler.getI().getOutPool().getRunningJob(pJobID);
        try{
            JobStatusBean newStatus=new JobStatusBean();
            newStatus.setWrtID(tmp.getWfID());
            newStatus.setWorkflowID(tmp.getWf().getWorkflowData().getWorkflowID());
            newStatus.setJobID(tmp.getJobName());
            newStatus.setPID(tmp.getPid());
            newStatus.setResource(pResource);
            newStatus.setTim(System.currentTimeMillis());


            if(pJobStatus.getState().equals(ActivityStateEnumeration.FINISHED))
                newStatus.setStatus(Status.FINISH);
            else if(pJobStatus.getState().equals(ActivityStateEnumeration.FAILED))
                newStatus.setStatus(Status.ERROR);
            else if(pJobStatus.getState().equals(ActivityStateEnumeration.RUNNING))
                newStatus.setStatus(Status.RUNNING);
            else if(pJobStatus.getState().equals(ActivityStateEnumeration.PENDING))
                newStatus.setStatus(Status.SUBMITED);
            else if(pJobStatus.getState().equals(ActivityStateEnumeration.CANCELLED))
                return "";
                //newStatus.setStatus(Status.ABORTED);
            WorkHandler.getI().getIncomingStatusPool().addNewStatusInformation(newStatus);
        }
        catch(Exception e){/*System.out.println("NEM TALAHATO A JOB:"+pJobID); e.printStackTrace();*/}
        return "";
    }

}

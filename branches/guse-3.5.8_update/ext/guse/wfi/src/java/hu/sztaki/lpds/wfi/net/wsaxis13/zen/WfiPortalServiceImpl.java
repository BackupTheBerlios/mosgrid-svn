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
 * WfiPortalServiceImpl 
 * WFI service portal szamara
 */

package hu.sztaki.lpds.wfi.net.wsaxis13.zen;

import hu.sztaki.lpds.logging.Logger;
import hu.sztaki.lpds.wfi.com.WorkflowInformationBean;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.wfi.inf.WfiPortalService;
import hu.sztaki.lpds.wfi.service.zen.Base;
import hu.sztaki.lpds.wfi.service.zen.Runner;
import java.util.Hashtable;
import java.util.Vector;

public class WfiPortalServiceImpl implements WfiPortalService{
    public WfiPortalServiceImpl() {}
        
    @Override
    public String submitWorkflow(WorkflowRuntimeBean pWorkflowData){
        Logger.getI().service(Logger.EVERYTHING, "workflow submit", false);
        try{
            return Base.getI().addZenRunner(pWorkflowData, null,null,new Hashtable(),new Hashtable());
        }
        catch(Exception e){e.printStackTrace();return "error";}
    }

    @Override
    public void abortWorkflow(String pRuntimeID){
        Logger.getI().service(Logger.EVERYTHING, "workflow abort", false);
        Base.getI().getAbortWFs().add(pRuntimeID);
    }
    
    @Override
    public void rescueWorkflow(WorkflowRuntimeBean pWorkflowData){
        Logger.getI().service(Logger.EVERYTHING, "workflow rescue", false);
        try{
        if (Base.getZenRunner(pWorkflowData.getRuntimeID()) == null) {
            // suspend utan hivott rescue (resume) eseten
            // a submitWorkflow() hivodik meg...
            submitWorkflow(pWorkflowData);
        } else {
            // running/error statuszban levo workflow
            // eseten a Base.getI().rescueZenRunner(pWorkflowData);
            // hivodik meg...
            Base.getI().rescueZenRunner(pWorkflowData);
        }
        }
        catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void getWaitingJob(String pURL){Base.getI().listZens();}

    @Override
    public Vector<WorkflowInformationBean> getInformation() {
        Logger.getI().service(Logger.EVERYTHING, "WFI status", false);
        return Base.getI().getInformation();
    }
    
}

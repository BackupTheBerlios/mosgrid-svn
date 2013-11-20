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
 * Status listener for gt2 / gt4 job. 
 */

package hu.sztaki.lpds.submitter.grids.gt;

import hu.sztaki.lpds.dcibridge.service.Job;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.globus.cog.abstraction.impl.common.StatusEvent;
import org.globus.cog.abstraction.interfaces.StatusListener;

public class gtStatusListener implements StatusListener{
    int iStatus= 0;
    public void statusChanged(StatusEvent gjob) {
                //System.out .println("gtStatusListener--statusChanged: " + gjob.getStatus().getStatusString() + " " + gjob.getStatus().getStatusCode() + " msg:" +  gjob.getStatus().getMessage());
        if (gjob.getStatus().getStatusCode() == 7) {
            //System.out .println("getOutput()");
            //getOutput();
            iStatus=6;
        } else if (gjob.getStatus().getStatusCode() == 5) {//error
            //getOutput();
            if (gjob.getStatus().getMessage() != null) {
                //System.out .println("Status: FAILED error code: " + gjob.getStatus().getMessage());
            }
            iStatus = 7;
        } else if (gjob.getStatus().getStatusCode() == 6) {//canceled (aborted by user)
            iStatus = 8;
        } else if (gjob.getStatus().getStatusCode() == 2) {
            iStatus = 5;
        } else if (gjob.getStatus().getStatusCode() == 1) {
            iStatus = 13;
        }
    }
    public void getStatus(Job pJob) {
        //System.out.println("gtStatusListener-getStatus:"+iStatus);
        if (iStatus == 5) {
            pJob.setStatus(ActivityStateEnumeration.RUNNING);
        } else if (iStatus == 6) {
            pJob.setStatus(ActivityStateEnumeration.FINISHED);
        } else if (iStatus == 7) {
            pJob.setStatus(ActivityStateEnumeration.FAILED);
        } else if (iStatus == 8) {
            pJob.setStatus(ActivityStateEnumeration.CANCELLED);
        } else {
            pJob.setStatus(ActivityStateEnumeration.PENDING);
        }
    }

}

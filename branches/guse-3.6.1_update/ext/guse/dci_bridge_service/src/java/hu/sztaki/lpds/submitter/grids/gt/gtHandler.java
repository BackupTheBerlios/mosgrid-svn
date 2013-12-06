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
package hu.sztaki.lpds.submitter.grids.gt;

import hu.sztaki.lpds.dcibridge.service.Job;
import java.util.Hashtable;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;

/**
 *
 * @author csig
 */
public class gtHandler {

    private static gtHandler instance = null;
    private static Hashtable<String, work> jobs = new Hashtable<String, work>();

    public gtHandler() {
    }

    public static gtHandler getI() {
        if (instance == null) {
            instance = new gtHandler();
        }
        return instance;
    }

    public void submit(Job pJob, String pProv) {
        jobs.put(pJob.getId(), new work(pJob, pProv));        
    }

    public void getStatus(Job pJob) {
        int s = jobs.get(pJob.getId()).getStatus();
        if (s == 5) {
            pJob.setStatus(ActivityStateEnumeration.RUNNING);
        } else if (s == 6) {
            pJob.setStatus(ActivityStateEnumeration.FINISHED);
        } else if (s == 7) {
            pJob.setStatus(ActivityStateEnumeration.FAILED);
        } else {
            pJob.setStatus(ActivityStateEnumeration.PENDING);
        }
    }
    public void abort(Job pJob){
        jobs.get(pJob.getId()).abort();
    }
}

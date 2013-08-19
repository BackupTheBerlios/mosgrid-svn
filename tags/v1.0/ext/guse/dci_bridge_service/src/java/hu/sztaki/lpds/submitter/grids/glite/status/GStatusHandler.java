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
package hu.sztaki.lpds.submitter.grids.glite.status;

import java.util.Hashtable;

/**
 *
 * @author csig
 */
public class GStatusHandler {

    private static GStatusHandler instance = null;
    private static Hashtable<String, Job> status = new Hashtable<String, Job>();

    public GStatusHandler() {
    }

    /**
     * returns the static instance of the object
     * @return static instance of the object
     */
    public static GStatusHandler getI() {
        if (instance == null) {
            instance = new GStatusHandler();
        }
        return instance;
    }

    public void initJobStatus(String puserid, String pjobid, int pstatus) {
//        System.out.println("GStatusHandler.initJobStatus-> puserid:" + puserid + " pjobid:" + pjobid + " pstatus:" + pstatus);
        status.put(pjobid, new Job(puserid, pstatus));
    }

    public boolean setStatus(String puserid, String pjobid, int pstatus) {
        if (status.containsKey(pjobid)) {
//            System.out.println("GStatusHandler.setStatus()"+puserid+" "+pjobid+" "+pstatus);
            status.get(pjobid).setStatus(puserid, pstatus);
            return true;
        }else{
//            System.out.println("GStatusHandler.setStatus()-------------tatus.containsKey:"+status.containsKey(pjobid));
            return false;
        }
    }

    public Job getJob(String pjobid){
//        System.out.println("GStatusHandler.getjob() jobid:"+pjobid+" status"+status.get(pjobid).getStatus());
        return status.get(pjobid);
    }

    public void removeJob(String pjobid){
//        System.out.println("GStatusHandler.removeJob() jobid:"+pjobid);
        status.remove(pjobid);
//        System.out.println("GStatusHandler.size="+status.size());
    }
}
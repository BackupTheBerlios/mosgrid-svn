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

public class Job {

    private String userid;
    private long setstatustime = 0;
    private int status = 0;

    public Job(String puid, int pstat) {
        userid = puid;
        status = pstat;
        setstatustime = System.currentTimeMillis();
    }

    public String getUserid() {
        return userid;
    }

    public int getStatus() {
        return status;
    }

    public long getSetStatusTime() {
        return setstatustime;
    }

    public boolean setStatus(String puid, int pstat) {
        if (userid.equals(puid)) {
            status = pstat;
            setstatustime = System.currentTimeMillis();
            return true;
        }
//        System.out.println("Job.setStatus: user error!:" + puid + "!=" + userid);
        return false;
    }
}
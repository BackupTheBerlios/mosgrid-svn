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
 * egy file logikai elerhetosegenek deffinicioja
 */

package hu.sztaki.lpds.storage.com;

/**
 * @author krisztian
 */
public class FileBean {
    private String portalid;
    private String userid;
    private String workflowname;
    private String jobname;
    private String runtimeid;
    private String portnumber;
    private String pid;
    private String prefix;

    public String getPid() {return pid;}

    public void setPid(String pid) {this.pid = pid;}

    public String getJobname() {return jobname;}

    public void setJobname(String jobname) {this.jobname = jobname;}

    public String getPortalid() {return portalid;}

    public void setPortalid(String portalid) {this.portalid = portalid;}

    public String getPortnumber() {return portnumber;}

    public void setPortnumber(String portnumber) {this.portnumber = portnumber;}

    public String getRuntimeid() {return runtimeid;}

    public void setRuntimeid(String runtimeid) {this.runtimeid = runtimeid;}

    public String getUserid() {return userid;}

    public void setUserid(String userid) {this.userid = userid;}

    public String getWorkflowname() {return workflowname;}

    public void setWorkflowname(String workflowname) {this.workflowname = workflowname;}

    public String getPrefix() {return prefix;}

    public void setPrefix(String prefix) {this.prefix = prefix;}



}

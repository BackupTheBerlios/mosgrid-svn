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
 * Job-hoz tartozo outputok leiroja
 */

package hu.sztaki.lpds.wfs.service.angie.datas;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author krisztian
 */
public class JobAndOutputsBean {
    private long jobid;
    private String jobname;
//<name,id>
    private Hashtable<String,OutputDescrioptionBean> outputs=new Hashtable<String, OutputDescrioptionBean>();

    public long getJobid() {return jobid;}

    public void setJobid(long jobid) {this.jobid = jobid;}

    public String getJobname() {return jobname;}

    public void setJobname(String jobname) {this.jobname = jobname;}

    public Hashtable<String, OutputDescrioptionBean> getOutputs() {return outputs;}

    public void setOutputs(Hashtable<String, OutputDescrioptionBean> outputs) {this.outputs = outputs;}

    public Enumeration<String> getOutputsName(){return outputs.keys();}

    public OutputDescrioptionBean getOutputDescription(String pName){return outputs.get(pName);}

    public void setOutputDescription(String pName, OutputDescrioptionBean pValue){outputs.put(pName,pValue);}

    public void addOutput(String pName, OutputDescrioptionBean pID){outputs.put(pName, pID);}

    

}

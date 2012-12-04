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
 * Egy tarolt wf leiroja
 */

package hu.sztaki.lpds.wfs.service.angie.datas;

/**
 * @author krisztian
 */
public class WorkflowBean {
    private long workflowid,templateid,id_aworkflow;
    private String name,txt;

    public long getTemplateid() {return templateid;}

    public void setTemplateid(long templateid) {this.templateid = templateid;}

    public long getWorkflowid() {return workflowid;}

    public void setWorkflowid(long workflowid) {this.workflowid = workflowid;}

    public long getId_aworkflow() {return id_aworkflow;}

    public void setId_aworkflow(long id_aworkflow) {this.id_aworkflow = id_aworkflow;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getTxt() {return txt;}

    public void setTxt(String txt) {this.txt = txt;}


}

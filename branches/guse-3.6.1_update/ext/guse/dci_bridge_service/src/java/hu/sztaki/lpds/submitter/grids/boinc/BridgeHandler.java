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
 * This class handles the 3gBridge
 */

package hu.sztaki.lpds.submitter.grids.boinc;

import eu.edges_grid.wsdl._3gbridge.G3BridgeSubmitter_Service;
import eu.edges_grid.wsdl._3gbridge.JobIDList;
import hu.sztaki.lpds.dcibridge.service.Job;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author krisztian karoczkai
 */
public class BridgeHandler {
    private G3BridgeSubmitter_Service client;
    private List<Job> submitQueue=new CopyOnWriteArrayList<Job>();
    private JobIDList statusQueue=new JobIDList();
    private JobIDList deleteQueue=new JobIDList();
    private JobIDList downloadQueue=new JobIDList();
    private Hashtable<String,String> mapping=new Hashtable<String, String>();
	private long lastEventTimeStamp=0;

    public void setEventTimeStamp() {this.lastEventTimeStamp = System.currentTimeMillis();}

    public boolean isEnabledNextEvent(long pDiff){return lastEventTimeStamp+pDiff<System.currentTimeMillis();}
    public G3BridgeSubmitter_Service getClient() {return client;}

    public void setClient(G3BridgeSubmitter_Service client) {this.client = client;}

    public JobIDList getStatusQueue() {return statusQueue;}

    public void setStatusQueue(JobIDList statusQueue) {this.statusQueue = statusQueue;}

    public List<Job> getSubmitQueue() {return submitQueue;}

    public void setSubmitQueue(List<Job> submitQueue) {this.submitQueue = submitQueue;}

    public JobIDList getDeleteQueue() {return deleteQueue;}

    public void setDeleteQueue(JobIDList deleteQueue) {this.deleteQueue = deleteQueue;}

    public Hashtable<String, String> getMapping() {return mapping;}

    public void setMapping(Hashtable<String, String> mapping) {this.mapping = mapping;}

    public JobIDList getDownloadQueue() {return downloadQueue;}

    public void setDownloadQueue(JobIDList downloadQueue) {this.downloadQueue = downloadQueue;}



}

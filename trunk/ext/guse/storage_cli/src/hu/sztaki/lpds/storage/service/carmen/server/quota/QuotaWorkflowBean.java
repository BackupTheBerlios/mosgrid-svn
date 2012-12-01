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
 * QuotaWorkflowBean.java
 *
 * Stores the data of a workflow of the user.
 */

package hu.sztaki.lpds.storage.service.carmen.server.quota;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author lpds
 */
public class QuotaWorkflowBean {
    
    private String workflowID;
    
    private long allOthersSize;
    
    private long allOutputsSize;
    
    private long allWorkflowSize;
    
    private Hashtable runtimes = new Hashtable();
    
    /**
     * Constructor for JavaBeans
     */
    public QuotaWorkflowBean() {
    }
    
    /**
     * Constructor for easier use.
     * @param workflowID workflow ID
     */
    public QuotaWorkflowBean(String workflowID) {
        setWorkflowID(workflowID);
        allOthersSize = 0;
        allOutputsSize = 0;
        allWorkflowSize = 0;
        runtimes = new Hashtable();
    }
    
    /**
     * Returns the workflow ID
     * @return workflow ID
     */
    public String getWorkflowID() {
        return workflowID;
    }
    
    /**
     * Sets the workflow ID
     * @param value workflow ID
     */
    public void setWorkflowID(String value) {
        workflowID = value;
    }
    
    /**
     * Returns the workflow
     * size in bytes.
     * @return allWorkflowSize
     */
    public long getAllWorkflowSize() {
        return allWorkflowSize;
    }
    
    /**
     * Returns the runtime output size
     * @param runtimeID runtime ID
     * @return size of the runtime
     */
    public long getRuntimeSize(String runtimeID) {
        long oldSize = 0;
        if (runtimes.containsKey(runtimeID)) {
            oldSize = ((Long) runtimes.get(runtimeID)).longValue();
        }
        return oldSize;
    }
    
    /**
     * Returns the total size of all of the input and 
     * executable files in the workflow in bytes.
     * @return allOthersSize
     */
    public long getAllOthersSize() {
        return allOthersSize;
    }
    
    /**
     * Increases the allOthersSize
     * (input and executable files)
     * size.
     * @param plussSize the size it will be increased with, in bytes
     * @return allOtherSize (current value)
     */
    public long addOthersSize(long plussSize) {
        this.allOthersSize += plussSize;
        // System.out.println("quota add allOthers : " + allOthersSize);
        this.allWorkflowSize = allOthersSize + allOutputsSize;
        // System.out.println("quota allWorkflowSize : " + allOthersSize);
        return allOthersSize;
    }
    
    /**
     * Returns the total size of all of the output 
     * files in the workflow in bytes.
     * @return allOutputsSize
     */
    public long getAllOutputsSize() {
        return allOutputsSize;
    }
    
    /**
     * Increases the size of the given runtime output.
     * @param runtimeID runtime ID
     * @param plussSize the size it will be increased with, in bytes
     * @return size current runtime stored value
     */
    public long addRuntimeSize(String runtimeID, long plussSize) {
        long size = 0;
        if (runtimes.containsKey(runtimeID)) {
            size = ((Long) runtimes.get(runtimeID)).longValue();
        }
        size += plussSize;
        runtimes.put(runtimeID, new Long(size));
        allOutputsSize += plussSize;
        allWorkflowSize = allOthersSize + allOutputsSize;
        // System.out.println("quota add runtimes : " + runtimes);
        // System.out.println("allOthersSize   : " + allOthersSize);
        // System.out.println("allOutputsSize  : " + allOutputsSize);
        // System.out.println("allWorkflowSize : " + allWorkflowSize);
        return size;
    }
    
    /**
     * Removes the stored size of the given runtime output.
     * @param runtimeID runtime ID
     */
    public void deleteRuntime(String runtimeID) {
        long oldSize = 0;
        // System.out.println("quota delete runtimes : " + runtimes);
        if (runtimes.containsKey(runtimeID)) {
            oldSize = ((Long) runtimes.get(runtimeID)).longValue();
            runtimes.remove(runtimeID);
            allOutputsSize -= oldSize;
            allWorkflowSize = allOthersSize + allOutputsSize;
        }
        // System.out.println("quota delet runtimes : " + runtimes);
        // System.out.println("allWorkflowSize : " + allWorkflowSize);
    }
    
    /**
     * Removes all the stored output sizes.
     */
    public void deleteAllOutputs() {
        runtimes.clear();
        allOutputsSize = 0;
        allWorkflowSize = allOthersSize + allOutputsSize;
    }
    
    /**
     * Returns the workflowHash value stored 
     * in the current workflow repository.
     *
     * The content of the workflowHash:
     *
     * (key: "rtID", value: The total output size of the 
     * workflow runtimeID given in the key)
     *
     * (key: allOutputsKey, value: size of every job's every outputs in bytes)
     *
     * (key: allOthersKey, value: workflow details data, every job input and
     * executable file size in bytes).
     *
     * (key: allWorkflowKey, value: total size of the workflow in bytes)
     *
     * note: allOutputsKey = the total size of all runtimeID output
     *
     * note: allOthersKey = workflow details, the total size of every job input and executable
     * files 
     *
     * note: allWorkflowKey = allOutputsKey + allOthersKey size 
     *
     * @return workflowHash
     */
    public Hashtable getWorkflowHash() {
        Hashtable workflowHash = new Hashtable();
        workflowHash.put(QuotaKeys.allOthersKey, new Long(allOthersSize));
        workflowHash.put(QuotaKeys.allOutputsKey, new Long(allOutputsSize));
        workflowHash.put(QuotaKeys.allWorkflowKey, new Long(allWorkflowSize));
        Enumeration enumRtID = runtimes.keys();
        while (enumRtID.hasMoreElements()) {
            String runtimeID = (String) enumRtID.nextElement();
            Long runtimeIDSize = (Long) runtimes.get(runtimeID);
            workflowHash.put(runtimeID, runtimeIDSize);
        }
        return workflowHash;
    }
    
}

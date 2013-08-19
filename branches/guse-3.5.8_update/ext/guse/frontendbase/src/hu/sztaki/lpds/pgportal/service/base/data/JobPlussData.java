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
/* Stores the extra data of the job's data parts
 * This data can be created during making a template and
 * they can belong to every job property.
*/
package hu.sztaki.lpds.pgportal.service.base.data;

import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import java.util.HashMap;

/**
 *
 * @author lpds
 */
public class JobPlussData { 
    private HashMap label = new HashMap();
    private HashMap desc = new HashMap();
    private HashMap inh = new HashMap();
    
    /**
     * Empty constructor
     */
    public JobPlussData() {}
    
    /**
     * Constructor, creation based on the job property descriptor
     * @param bean job property descriptor
     */
    public JobPlussData(JobPropertyBean bean) {
        label = bean.getLabel();
        desc = bean.getDesc0();
        inh = bean.getInherited();
    }
    
    /**
     * Getting the template configuration label
     * @param key configuration key
     * @return value
     */
    public String labelKey(String key) {
        return (label.get(key)==null)?"":""+label.get(key);
    }
    
    /**
     * Getting the template configuration description
     * @param key configuration key
     * @return value
     */
    public String descKey(String key) {
        return (desc.get(key)==null)?"":""+desc.get(key);
    }
    
    // labels
    /**
     * Getting the template configuration label for embedded workflow
     * @return label value
     */
    public String getLabelIWorkflow() {return labelKey("iworkflow");}
    //
    /**
     * Getting the template configuration label belongs to a service type
     * @return label value
     */
    public String getLabelServiceType() {return labelKey("servicetype");}
    /**
     * Getting the template configuration label belongs to a service URL
     * @return label value
     */
    public String getLabelServiceUrl() {return labelKey("serviceurl");}
    /**
     * Getting the template configuration label belongs to a service method
     * @return label value
     */
    public String getLabelServiceMethod() {return labelKey("servicemethod");}
    //
    /**
     * Getting the template configuration label belongs to a grid type
     * @return label value
     */
    public String getLabelGridType() {return labelKey("gridtype");}
    /**
     * Getting the template configuration label belongs to a chosen grid
     * @return label value
     */
    public String getLabelGrid() {return labelKey("grid");}
    /**
     * Getting the template configuration label belongs to the jobmanager
     * @return label value
     */
    public String getLabelJobManager() {return labelKey("jobmanager");}
    /**
     * Getting the template configuration label belongs to the executor resource
     * @return label value
     */
    public String getLabelResource() {return labelKey("resource");}
    /**
     * Getting the template configuration label belongs to the job type
     * @return label value
     */
    public String getLabelType() {return labelKey("type");}
    /**
     * Getting the template configuration label belongs to the MPI node number
     * @return label value
     */
    public String getLabelNodeNumber() {return labelKey("nodenumber");}
    /**
     * Getting the template configuration label belongs to the command line parameter
     * @return label value
     */
    public String getLabelParams() {return labelKey("params");}
    /**
     * Getting the template configuration label belongs to the uploaded binary
     * @return label value
     */
    public String getLabelBinary() {return labelKey("binary");}
    
    // descs
    /**
     * Getting the template configuration description belongs to the embedded workflow
     * @return content of the description
     */
    public String getDescIWorkflow() {return descKey("iworkflow");}
    //
    /**
     * Getting the template configuration description belongs to the service type
     * @return content of the description
     */
    public String getDescServiceType() {return descKey("servicetype");}
    /**
     * Getting the template configuration description belongs to the service URL
     * @return content of the description
     */
    public String getDescServiceUrl() {return descKey("serviceurl");}
    /**
     * Getting the template configuration description belongs to the service method
     * @return content of the description
     */
    public String getDescServiceMethod() {return descKey("servicemethod");}
    //
    /**
     * Getting the template configuration description belongs to the grid type
     * @return content of the description
     */
    public String getDescGridType() {return descKey("gridtype");}
    /**
     * Getting the template configuration description belongs to the grid/VO type
     * @return content of the description
     */
    public String getDescGrid() {return descKey("grid");}
    /**
     * Getting the template configuration description belongs to the jobmanager
     * @return content of the description
     */
    public String getDescJobManager() {return descKey("jobmanager");}
    /**
     * Getting the template configuration description belongs to the executor resource
     * @return content of the description
     */
    public String getDescResource() {return descKey("resource");}
    /**
     * Getting the template configuration description belongs to the job type
     * @return content of the description
     */
    public String getDescType() {return descKey("type");}
    /**
     * Getting the template configuration description belongs to the number of MPI nodes
     * @return content of the description
     */
    public String getDescNodeNumber() {return descKey("nodenumber");}
    /**
     * Getting the template configuration description belongs to the command line parameter
     * @return content of the description
     */
    public String getDescParams() {return descKey("params");}
    /**
     * Getting the template configuration description belongs to the uploaded binary
     * @return content of the description
     */
    public String getDescBinary() {return descKey("binary");}
    
}

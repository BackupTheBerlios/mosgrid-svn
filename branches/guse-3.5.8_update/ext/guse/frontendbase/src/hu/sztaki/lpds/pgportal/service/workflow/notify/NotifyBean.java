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

package hu.sztaki.lpds.pgportal.service.workflow.notify;

import java.util.Hashtable;

/**
 * The bean class which contains 
 * the required data for the message.
 *
 * @author lpds
 */
public class NotifyBean {
    
    // The table which contains
    // the required data for the message, hash
    private Hashtable data;
    
    /**
     * Constructor for the JavaBeans
     */
    public NotifyBean() {
        setData(new Hashtable());
    }
    
    /**
     * Creates a notify bean with the data from the parameters.
     *
     * @param notifyHash - data required to send the message
     */
    public NotifyBean(Hashtable notifyHash) {
        setData(notifyHash);
    }
    
    /**
     * Gets the notify hash data.
     * @return notify parameter hash
     */
    public Hashtable getData() {
        return data;
    }
    
    /**
     * Sets the notify hash data.
     * @param data parameter hash
     */
    public void setData(Hashtable data) {
        this.data = data;
    }
    
    /**
     * Gets the value of a key 
     * of the notify hash.
     * @param key parameter key
     * @return kert value belongs to the key
     */
    public String getValue(String key) {
        if (data.containsKey(key)) {
            return "" + data.get(key);
        }
        return "";
    }
    
    /**
     * Sets the value of a key 
     * of the notify hash.
     * @param key key
     * @param value value
     */
    public void setValue(String key, String value) {
        data.put(key, value);
    }
    
}

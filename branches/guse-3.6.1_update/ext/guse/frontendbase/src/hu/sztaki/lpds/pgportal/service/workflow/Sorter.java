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
package hu.sztaki.lpds.pgportal.service.workflow;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sorts the workflow lists to a queue.
 *
 * @author lpds
 */
public class Sorter {
    
    private static Sorter instance = null;
/**
 * Constructor, creating the singleton instance
 */
    public Sorter() {
        if (instance == null) {
            instance = this;
        }
    }
    
    /**
     * Returns the sorter instance.
     *
     * @return
     */
    public static Sorter getInstance() {
        if (instance == null) {
            instance = new Sorter();
        }
        return instance;
    }
    
    /**
     * Sorts the workflow list given from the parameter
     * and returns a vector.
     *
     * (In the result the hash table keys will be sorted)
     *
     * @param workflows - workflow list
     * @return sorted workflow list in the vector
     *
     */
    public Vector sortFromKeys(ConcurrentHashMap workflows) {
        Vector shortedWorkflows = new Vector();
        try {
            Vector sortvector = new Vector(workflows.keySet());
            Collections.sort(sortvector);
            shortedWorkflows = sortvector;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shortedWorkflows;
    }

    /**
     * Sorts the workflow list given from the parameter
     * and returns a vector.
     *
     * (In the result the hash table values will be sorted)
     *
     * @param workflows - workflow list
     * @return sorted workflow list in the vector
     *
     */
    public Vector sortFromValues(ConcurrentHashMap workflows) {
        Vector shortedWorkflows = new Vector();
        try {
            Vector sortvector = new Vector(workflows.keySet());
            Collections.sort(sortvector);
            Enumeration enumskeys = sortvector.elements();
            while (enumskeys.hasMoreElements()) {
                shortedWorkflows.addElement(workflows.get(enumskeys.nextElement()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shortedWorkflows;
    }
    
}

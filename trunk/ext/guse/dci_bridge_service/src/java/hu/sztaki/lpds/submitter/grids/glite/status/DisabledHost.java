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
 * Handles the disabled hosts
 */
package hu.sztaki.lpds.submitter.grids.glite.status;

import java.util.Hashtable;

/**
 * DisabledHost
 */
public class DisabledHost {

    private static DisabledHost instance = null;
    private static Hashtable<String, Long> disablehost = new Hashtable<String, Long>(); //jdl-ben tiltando hostok

    /**
     * returns a static instance of the object
     * @return static instance of the object
     */
    public static DisabledHost getI() {
        if (instance == null) {
            instance = new DisabledHost();
        }
        return instance;
    }

    /**
     * Adds to disabled hosts
     * @param resource
     */
    public void add(String resource) {
        disablehost.put(resource.split(":")[0], System.currentTimeMillis());
        //sysLog(OutputDir, "disablehost:" + disablehost);
        //System.out.println("add ->disablehost:" + disablehost);
    }

    /**
     * removes from disabled hosts
     * @param resource
     */
    public void remove(String resource) {
        disablehost.remove(resource);
        //System.out.println("rem ->disablehost:" + disablehost);
    }

    /**
     * get the disabled time
     * @param resource
     * @return
     */
    public long get(String resource) {
        return (long)disablehost.get(resource);
    }
    public Hashtable getAll() {
        //System.out.println("getall ->disablehost:" + disablehost);
        return disablehost;
    }
}

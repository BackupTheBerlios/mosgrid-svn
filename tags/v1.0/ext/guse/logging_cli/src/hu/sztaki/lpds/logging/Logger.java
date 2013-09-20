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
 * gUSE Logger
 */
package hu.sztaki.lpds.logging;

import hu.sztaki.lpds.logging.inf.LoggerClient;

/**
 * @author krisztian
 */
public class Logger extends LocalEngine implements LoggerClient {

    private static LoggerClient instance = new LocalEngine();

/**
 * Getting singleton instance
 * @return static object instance
 */
    public static LoggerClient getI() {
        return instance;
    }

    /**
     * Setting the log properties
     */
    public static synchronized void initLoggetTrigger() {
        getI().trigger();
    }
}
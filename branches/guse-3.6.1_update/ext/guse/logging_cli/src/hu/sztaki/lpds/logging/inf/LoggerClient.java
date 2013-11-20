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
 * Log client definition
 */
package hu.sztaki.lpds.logging.inf;

/**
 * @author krisztian
 */
public interface LoggerClient {

    /**
     * EVERYTHING log level(1)
     */
    public static final byte EVERYTHING = 1;
    /**
     * INFO log level(2)
     */
    public static final byte INFO = 2;
    /**
     * DEBUG log level(3)
     */
    public static final byte DEBUG = 3;
    /**
     * SYSTEM log level(4)
     */
    public static final byte SYSTEM = 4;
    /**
     * WARNING log level(5)
     */
    public static final byte WARNING = 5;
    /**
     * EXCEPTION log level(6)
     */
    public static final byte EXCEPTION = 6;
    /**
     *
     */
    public static final byte CRITICAL = 7;

    /**
     * Service log message handling
     * @param pLevel level
     * @param pMsg message
     * @param pCtrace trace saving claim
     */
    public void service(byte pLevel, String pMsg, boolean pCtrace);

/**
 * WF log writing
 * @param pWRID wf instance ID (zenID)
 * @param pLevel log level
 * @param pMsg message
 */
    public void workflow(String pWRID, byte pLevel, String pMsg);

/**
 * WF log writing in case of an error
 * @param pWRID wf instance ID (zenID)
 * @param pEx raised exception
 * @param pMsg message
 */
    public void workflow(String pWRID, Exception pEx, String pMsg);

/**
 * Pool log writing
 * @param pPool pool ID
 * @param pMsg message
 * @param pCount pool current content
 */
    public void pool(String pPool, String pMsg, long pCount);

/**
 * Pool log writing in case of an error
 * @param pPool pool ID
 * @param pData
 * @param pEx raised exception
 * @param pCount pool current content
 */
    public void pool(String pPool, String pData,Exception pEx, long pCount);

/**
 * Job log writing
 * @param pJobID job ID (tuntime)
 * @param pLevel log level
 * @param pMsg message
 */
    public void job(String pJobID, byte pLevel, String pMsg);

    /**
     * Property reload
     */
    public void trigger();
}

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
 * JobInstaneBean.java
 * Job instnace statusz leiro bean...
 */
package hu.sztaki.lpds.wfs.com;

/**
 * @author lpds
 */
public class JobInstanceBean {

    String jobID;
    int jobPidID;
    int jobStatus;
    String jobResource;
    long jobTime;

    /**
     * Class construktor
     */
    public JobInstanceBean() {
        setJobID("");
        setPID(0);
        setStatus(0);
        setResource("");
        setTim(0);
    }

    /**
     * Class construktor
     * @param pJobID    Job azonosito
     * @param pPID      Job Parametrikus ID
     * @param pStatus   Job Status
     * @param pResource Job Eroforras
     * @param pTime      Status valtozas bekovetkezesi idobelyege
     */
    public JobInstanceBean(String pJobID, int pPID, int pStatus, String pResource, long pTime) {
        setJobID(pJobID);
        setPID(pPID);
        setStatus(pStatus);
        setResource(pResource);
        setTim(pTime);
    }

    /**
     * Visszaadja a Job azonositojot
     * @return A Job azonositoja
     * @see String
     */
    public String getJobID() {
        return jobID;
    }

    /**
     * Beallitja a Job azonositojot
     * @param value A Job azonositoja
     * @see String
     */
    public void setJobID(String value) {
        this.jobID = value;
    }

    /**
     * Job parametrikus ID lekerdezese
     * @return PID
     * @see String
     */
    public int getPID() {
        return jobPidID;
    }

    /**
     * Job parametrikus ID beallitasa
     * @param value PID
     */
    public void setPID(int value) {
        this.jobPidID = value;
    }

    /**
     * Visszaadja a Job Statusat
     * @return A Job Statusa
     */
    public int getStatus() {
        return jobStatus;
    }

    /**
     * Beallitja a Job Statusat
     * @param value A Job Statusa
     */
    public void setStatus(int value) {
        this.jobStatus = value;
    }

    /**
     * Visszaadja a hasznalt Eroforrast
     * @return A hasznalt Eroforras
     * @see String
     */
    public String getResource() {
        return jobResource;
    }

    /**
     * Beallitja a Job Eroforrasat
     * @param value A Jobot futtato Eroforras
     * @see String
     */
    public void setResource(String value) {
        this.jobResource = value;
    }

    /**
     * Status valtozas idobelyeg lekerdezese
     * @return idobelyeg
     * @see String
     */
    public long getTim() {
        return jobTime;
    }

    /**
     * Status valtozas idobelyeg beallitasa
     * @param value idobelyeg
     */
    public void setTim(long value) {
        this.jobTime = value;
    }
}

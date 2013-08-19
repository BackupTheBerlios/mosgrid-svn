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

package hu.sztaki.lpds.pgportal.visualizer.net.xmlrpc.common;

import java.io.Serializable;
import java.util.Date;

/**
 * A visualizer-ben hasznalt es megjelenitett job-statuszat leiro osztaly.
 *
 * @author lpds
 */
public class ClientJobStatus implements Serializable {

    private static final long serialVersionUID = 8125841452211257842L;

    private Date time;

    private Integer status;

    public ClientJobStatus getInstance() {
        return this;
    }

    public ClientJobStatus(Integer status, Date time) {
        this.time = time;
        this.status = status;
    }

    /**
     * @return Returns the status.
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return Returns the time.
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time
     *            The time to set.
     */
    public void setTime(Date time) {
        this.time = time;
    }
    
    /**
     * Job statusz informaciok kiiratasa.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("status: " + this.status + " time: " + this.time.getTime() + "\n");
        return sb.toString();
    }    
    
}

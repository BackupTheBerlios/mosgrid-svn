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
 * A visualizer-ben hasznalt es megjelenitett job-kapcsolatot leiro osztaly.
 *
 * @author lpds
 */
public class ClientJobConnection implements Serializable {
    
    private static final long serialVersionUID = 2456467770975743570L;
    
    private String sourcejobid;
    
    private String sourcejobname;
    
    private Date sourcetimestamp;
    
    private int sourcejobcoordinatey;
    
    private String destinationjobid;
    
    private String destinationjobname;
    
    private Date destinationtimestamp;
    
    public ClientJobConnection getInstance() {
        return this;
    }
    
    public ClientJobConnection() {
        this.sourcejobid = new String();
        this.sourcejobname = new String();
        this.sourcetimestamp = new Date(0);
        this.sourcejobcoordinatey = 0;
        this.destinationjobid = new String();
        this.destinationjobname = new String();
        this.destinationtimestamp = new Date(0);
    }
    
    /**
     * @return Returns the destinationjobname.
     */
    public String getDestinationjobname() {
        return destinationjobname;
    }
    
    /**
     * @param destinationjobname
     *            The destinationjobname to set.
     */
    public void setDestinationjobname(String destinationjobname) {
        this.destinationjobname = destinationjobname;
    }
    
    /**
     * @return Returns the destinationjobid.
     */
    public String getDestinationjobid() {
        return destinationjobid;
    }
    
    /**
     * @param destinationjobid
     *            The destinationjobid to set.
     */
    public void setDestinationjobid(String destinationjobid) {
        this.destinationjobid = destinationjobid;
    }
    
    /**
     * @return Returns the destinationtimestamp.
     */
    public Date getDestinationtimestamp() {
        return destinationtimestamp;
    }
    
    /**
     * @param destinationtimestamp
     *            The destinationtimestamp to set.
     */
    public void setDestinationtimestamp(Date destinationtimestamp) {
        this.destinationtimestamp = destinationtimestamp;
    }
    
    /**
     * @return Returns the sourcejobname.
     */
    public String getSourcejobname() {
        return sourcejobname;
    }
    
    /**
     * @param sourcejobname
     *            The sourcejobname to set.
     */
    public void setSourcejobname(String sourcejobname) {
        this.sourcejobname = sourcejobname;
    }
    
    /**
     * @return Returns the sourcejobid.
     */
    public String getSourcejobid() {
        return sourcejobid;
    }
    
    /**
     * @param sourcejobid
     *            The sourcejobid to set.
     */
    public void setSourcejobid(String sourcejobid) {
        this.sourcejobid = sourcejobid;
    }
    
    /**
     * @return Returns the sourcetimestamp.
     */
    public Date getSourcetimestamp() {
        return sourcetimestamp;
    }
    
    /**
     * @param sourcetimestamp
     *            The sourcetimestamp to set.
     */
    public void setSourcetimestamp(Date sourcetimestamp) {
        this.sourcetimestamp = sourcetimestamp;
    }
    
    /**
     * @return Returns the sourcejobcoordinatey.
     */
    public int getSourcejobcoordinatey() {
        return sourcejobcoordinatey;
    }
    
    /**
     * @param sourcejobcoordinatey
     *            The sourcejobcoordinatey to set.
     */
    public void setSourcejobcoordinatey(int sourcejobcoordinatey) {
        this.sourcejobcoordinatey = sourcejobcoordinatey;
    }
    
    /**
     * JobConnection informaciok kiiratasa.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append("sourcejobid: " + this.sourcejobid + "\n");
        sb.append("sourcejobname: " + this.sourcejobname + "\n");
        if (this.sourcetimestamp != null) {
            sb.append("sourcetimestamp: " + this.sourcetimestamp.getTime() + "\n");
        } else {
            sb.append("sourcetimestamp: timeatamp is null" + "\n");
        }
        sb.append("sourcejobcoordinatey: " + this.sourcejobcoordinatey + "\n");
        sb.append("destinationjobid: " + this.destinationjobid + "\n");
        sb.append("destinationjobname: " + this.destinationjobname + "\n");
        if (this.destinationtimestamp != null) {
            sb.append("destinationtimestamp: " + this.destinationtimestamp.getTime() + "\n");
        } else {
            sb.append("destinationtimestamp: timestamp is null" + "\n");
        }
        return sb.toString();
    }
    
}

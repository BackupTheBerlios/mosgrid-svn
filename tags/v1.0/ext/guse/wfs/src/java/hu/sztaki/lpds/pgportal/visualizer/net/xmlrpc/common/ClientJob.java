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
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A visualizer-ben hasznalt es megjelenitett job-ot megvalosito osztaly.
 *
 * @author lpds
 */
public class ClientJob implements Serializable {
    
    private static final long serialVersionUID = 4837162446857068175L;
    
    private String id;
    
    private String name;
    
    private String resource;
    
    private Date start;
    
    private Date exit;
    
    private boolean hide;
    
    // A timestamp szerint rendezett statuszokat tarolja
    private Vector statuslist;
    
    // Nem rendezett statusz lista, a gyors elereshez
    private Hashtable statuslistHash;
    
    private Vector outconnections;
    
    private Vector inconnections;
    
    public ClientJob getInstance() {
        return this;
    }
    
    public ClientJob() {
        this.id = new String();
        this.name = new String();
        this.resource = new String();
        this.start = new Date(0);
        this.exit = new Date(0);
        this.hide = false;
        this.clearStatuslist();
        this.clearConnections();
    }
    
    /**
     * @return Returns the Exit - Start time.
     */
    public int getDuration() {
        return (int) Math.abs((exit.getTime() - start.getTime()));
    }
    
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return Returns the job id.
     */
    public String getId() {
        return id;
    }
    
    /**
     * @param id
     *            The job id to set.
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * @return Returns the resource.
     */
    public String getResource() {
        return resource;
    }
    
    /**
     * @param resource
     *            The resource to set.
     */
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    /**
     * @return Returns the exit.
     */
    public Date getExit() {
        return exit;
    }
    
    /**
     * @param exit
     *            The exit to set.
     */
    public void setExit(Date exit) {
        this.exit = exit;
    }
    
    /**
     * @return Returns the hide.
     */
    public boolean isHide() {
        return hide;
    }
    
    /**
     * @param hide
     *            The hide to set.
     */
    public void setHide(boolean hide) {
        this.hide = hide;
    }
    
    /**
     * @return Returns the start.
     */
    public Date getStart() {
        return start;
    }
    
    /**
     * @param start
     *            The start to set.
     */
    public void setStart(Date start) {
        this.start = start;
    }
    
    /**
     * A job indulasi es leallasi idejet gyujti ki
     * es allitja be a meglevo
     * statuszinformaciok alapjan.
     */
    public void setStartExitTime() {
        Date tmpStatus = new Date();
        Date minStatus = new Date();
        Date maxStatus = new Date();
        if (this.getStatuslistSize() > 0) {
            minStatus = ((ClientJobStatus) statuslist.elementAt(0)).getTime();
            maxStatus = ((ClientJobStatus) statuslist.elementAt(0)).getTime();
            for (int sindex = 0; sindex < statuslist.size(); sindex++) {
                tmpStatus = ((ClientJobStatus) statuslist.elementAt(sindex)).getTime();
                if (tmpStatus.before(minStatus)) {
                    minStatus = tmpStatus;
                }
                if (tmpStatus.after(maxStatus)) {
                    maxStatus = tmpStatus;
                }
            }
            //
            // if (statuslistHash.get(new Integer(1)) != null) {
            // minStatus = (Date) statuslistHash.get(new Integer(1)); //
            // submitted
            // }
            // if (statuslistHash.get(new Integer(3)) != null) {
            // maxStatus = (Date) statuslistHash.get(new Integer(3)); //
            // finished
            // }
            // if (maxStatus.getTime() == 0) {
            // if (statuslistHash.get(new Integer(4)) != null) {
            // maxStatus = (Date) statuslistHash.get(new Integer(4)); // error
            // }
            // }
            // setUp Start and Exit time (status)
            this.setStart(minStatus);
            this.setExit(maxStatus);
        }
    }
    
    /**
     * A statuszok listajat allitja idorendi sorrendbe.
     */
    public void orderStatusListByTime() {
        if (this.getStatuslistSize() > 0) {
            // create list from values (all timestamps)
            ArrayList valueList = new ArrayList();
            // create list from keys (all status)
            ArrayList keyList = new ArrayList();
            for (int i = 0; i < statuslist.size(); i++) {
                ClientJobStatus tmpStatus = (ClientJobStatus) statuslist.elementAt(i);
                valueList.add(tmpStatus.getTime());
                keyList.add(tmpStatus.getStatus());
            }
            // System.out.println("before order: ");
            // System.out.println("valuesList: " + valueList);
            // System.out.println("keyList: " + keyList);
            // order value and key lists
            for (int i = 0; i < valueList.size(); i++) {
                for (int j = 0; j < valueList.size(); j++) {
                    if (((Date) valueList.get(i)).before((Date) valueList.get(j))) {
                        // syncronize valueList
                        Object tmpValue = valueList.get(i);
                        valueList.set(i, valueList.get(j));
                        valueList.set(j, tmpValue);
                        // syncronize keyList
                        Object tmpKey = keyList.get(i);
                        keyList.set(i, keyList.get(j));
                        keyList.set(j, tmpKey);
                        // System.out.println("change order: " + i + " <=> " +
                        // j);
                    }
                }
            }
            // System.out.println("after order: ");
            // System.out.println("valueList: " + valueList);
            // System.out.println("keyList: " + keyList);
            // create new ordered statuslist and statuslistHash table ordered by
            // values (timestamps)
            Vector tmpVector = new Vector();
            Hashtable tmpHash = new Hashtable();
            for (int i = 0; i < valueList.size(); i++) {
                tmpVector.add(new ClientJobStatus((Integer) keyList.get(i), (Date) valueList.get(i)));
                tmpHash.put((Integer) keyList.get(i), (Date) valueList.get(i));
            }
            // replace original status list and statuslistHash to ordered (by
            // time) lists
            statuslist = tmpVector;
            statuslistHash = tmpHash;
            this.setStartExitTime();
        }
    }
    
    /**
     * @return Returns the statuslist size.
     */
    public int getStatuslistSize() {
        return statuslist.size();
    }
    
    /**
     * @return Returns the statuslistHash.
     */
    public Vector getStatuslistHash() {
        return statuslist;
    }
    
    /**
     * @return Returns the status timestamp (Date).
     */
    public Date getStatuslist(Integer status) {
        return (Date) statuslistHash.get(status);
    }
    
    /**
     * @return Returns the statusList (Vector).
     */
    public Vector getStatuslistAll() {
        return statuslist;
    }
    
    /**
     * Clear Status List.
     */
    public void clearStatuslist() {
        this.statuslist = new Vector();
        this.statuslistHash = new Hashtable();
    }
    
    /**
     * Clear Connections.
     */
    public void clearConnections() {
        this.outconnections = new Vector();
        this.inconnections = new Vector();
    }
    
    /**
     * @return Returns the outconnections.
     */
    public Vector getOutConnections() {
        return outconnections;
    }
    
    /**
     * @return Returns the outconnections size.
     */
    public int getOutConnectionsSize() {
        return outconnections.size();
    }
    
    /**
     * @return Returns the inconnections.
     */
    public Vector getInConnections() {
        return inconnections;
    }
    
    /**
     * @return Returns the inconnections size.
     */
    public int getInConnectionsSize() {
        return inconnections.size();
    }
    
    /**
     * Egy statuszt ad a job statusz listajahoz.
     *
     * @param status
     * @param timestamp
     */
    public void addStatus(Integer status, Date timestamp) {
        try {
            if (timestamp != null) {
                if (timestamp.getTime() > 0) {
                    statuslist.addElement(new ClientJobStatus(status, timestamp));
                    statuslistHash.put(status, timestamp);
                }
            } else {
                statuslist.addElement(new ClientJobStatus(status, new Date(0)));
                statuslistHash.put(status, new Date(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Egy out kapcsolatot ad a job-hoz.
     *
     * @param fromjobid
     * @param fromjobname
     * @param fromtimestamp
     * @param tojobid
     */
    public void addOutConnection(String fromjobid, String fromjobname, Date fromtimestamp, String tojobid) {
        ClientJobConnection tmpconn = new ClientJobConnection();
        tmpconn.setSourcejobid(fromjobid);
        tmpconn.setSourcejobname(fromjobname);
        tmpconn.setSourcetimestamp(fromtimestamp);
        tmpconn.setDestinationjobid(tojobid);
        outconnections.addElement(tmpconn);
    }
    
    /**
     * Egy in kapcsolatot ad a job-hoz.
     *
     * @param tojobid
     * @param tojobname
     * @param totimestamp
     * @param fromjobid
     */
    public void addInConnection(String tojobid, String tojobname, Date totimestamp, String fromjobid) {
        ClientJobConnection tmpconn = new ClientJobConnection();
        tmpconn.setDestinationjobid(tojobid);
        tmpconn.setDestinationjobname(tojobname);
        tmpconn.setDestinationtimestamp(totimestamp);
        tmpconn.setSourcejobid(fromjobid);
        inconnections.addElement(tmpconn);
    }
    
    /**
     * Job informaciok kiiratasa.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n----------------\n");
        sb.append("id: " + this.id + "\n");
        sb.append("name: " + this.name + "\n");
        sb.append("resource: " + this.resource + "\n");
        if (this.start != null) {
            sb.append("start: " + this.start.getTime() + "\n");
        } else {
            sb.append("start timestamp is null" + "\n");
        }
        if (this.exit != null) {
            sb.append("exit: " + this.exit.getTime() + "\n");
        } else {
            sb.append("exit timestamp is null " + "\n");
        }
        sb.append("duration: " + getDuration() + "\n");
        sb.append("----------------\n");
        sb.append("statuslist size: " + this.getStatuslistSize() + "\n");
        Enumeration tmpKeys = statuslistHash.keys();
        while (tmpKeys.hasMoreElements()) {
            Integer tmpKey = (Integer) tmpKeys.nextElement();
            Date tmpDate = (Date) statuslistHash.get(tmpKey);
            if (tmpDate != null) {
                sb.append("status: " + tmpKey + " time: " + tmpDate.getTime() + "\n");
            } else {
                sb.append("status: " + tmpKey + " timestamp is null " + "\n");
            }
        }
        /*
         * ClientJobStatus tmpstatus; try { for (int sindex = 0; sindex <
         * statuslist.size(); sindex++) { // Load time, status tmpstatus =
         * (ClientJobStatus) statuslist.elementAt(sindex); sb.append((sindex +
         * 1) + ". status time: " + tmpstatus.getTime().getTime() + ", status: " +
         * tmpstatus.getStatus() + "\n"); } } catch (Exception e) {
         * System.out.println(e.toString());
         * e.printStackTrace();}
         */
        sb.append("----------------\n");
        sb.append("out connections size: " + this.outconnections.size() + "\n");
        sb.append("----------------\n");
        ClientJobConnection tmpfconn;
        try {
            for (int ocindex = 0; ocindex < outconnections.size(); ocindex++) {
                // Load from, time, to
                tmpfconn = (ClientJobConnection) outconnections.elementAt(ocindex);
                sb.append((ocindex + 1) + ". outconnections: " + tmpfconn.toString() + "\n");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        sb.append("----------------\n");
        sb.append("in connections size: " + this.inconnections.size() + "\n");
        sb.append("----------------\n");
        ClientJobConnection tmptconn;
        try {
            for (int icindex = 0; icindex < inconnections.size(); icindex++) {
                // Load from, time, to
                tmptconn = (ClientJobConnection) inconnections.elementAt(icindex);
                sb.append((icindex + 1) + ". inconnections: " + tmptconn.toString() + "\n");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        sb.append("----------------\n");
        // Date now = new Date();
        // DateFormat dateformat = DateFormat.getDateInstance();
        // sb.append("LongNow: " + now.getTime() + "\n");
        // sb.append("Formatted Now, Today is: " + dateformat.format(now) +
        // "\n");
        // sb.append("----------------\n");
        return sb.toString();
    }
    
}

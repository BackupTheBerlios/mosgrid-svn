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
 * Stores the data of the running instances of a workflow
 * (instance or runtime workflow)
 */

package hu.sztaki.lpds.pgportal.service.base.data;

import hu.sztaki.lpds.wfs.utils.Status;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author krisztian
 */
public class WorkflowRunTime 
{
    private int workflowStatus=2;
    private int lastWorkflowStatus=0;

    String wfiURL="",wfiService="";
    String wfsURL="",wfsService="";
    ConcurrentHashMap<String,ConcurrentHashMap<String,JobStatusData>> jobsStatus=new ConcurrentHashMap<String,ConcurrentHashMap<String,JobStatusData>>();

    String text=null;
    long tim=-2;
    long size=0;
    String resource="";
    private ConcurrentHashMap<String,Long> finished=new ConcurrentHashMap<String,Long>();
    private ConcurrentHashMap<String,Long> noinput=new ConcurrentHashMap<String,Long>();

    private String notifyWfchgType="";
    private boolean volatileOutputsDeleted=false;
    
    /** Creates a new instance of WorkflowRunTime */

    private void printCall(){
        System.out.println("CREATE:WorkflowRunTime");
        StackTraceElement[] l = Thread.currentThread().getStackTrace();
        StackTraceElement t;
        for(int i=0;i<l.length;i++){
            t=l[i];
            if(i>2 && t.getClassName().startsWith("hu."))
                System.out.println("--"+t.getClassName()+"."+t.getMethodName()+"() ("+t.getFileName()+":"+t.getLineNumber()+")");
        }
    }

    public WorkflowRunTime() {/*printCall();*/}
    
    /**
     * constructor
     * @param pWfiUrl the URL of the WFS service which implements storing
     * @param pWfiService WFS service resource which implements storing
     * @param pText text descriptor of the instance
     */
    public WorkflowRunTime(String pWfiUrl, String pWfiService, String pText)
    {
//        printCall();
        setWfsData(pWfiUrl,pWfiService);
        setText(pText);
    }
    
    /**
     * constructor
     * @param pWfiUrl URL of the WFS service which implements storing
     * @param pWfiService WFS service resource which implements storing
     * @param pText text descriptor of the instance
     * @param pStatus instance status
     */
    public WorkflowRunTime(String pWfiUrl, String pWfiService, String pText,String pStatus)
    {
  //      printCall();
        setWfsData(pWfiUrl,pWfiService);
        setText(pText);
        setStatus(pStatus,1);
    }

    /**
     * Getting the number of the successfully finished jobs of the instance
     * @param pJob job name
     * @return number of the jobs
     */
    public long getFinishedJobCount(String pJob)
    {
        if(finished.get(pJob)==null) return 0;
        return finished.get(pJob).longValue();
    }

    /**
     * Adding the newly successfully finished job
     * @param pJobID job name
     * @param pPid PS ID
     */
    public synchronized void addFinishedJob(String pJobID,String pPid)
    {
        if(finished.get(pJobID)==null) finished.put(pJobID,new Long(0));
        finished.put(pJobID,new Long(finished.get(pJobID).longValue()+1));
//        try{jobsStatus.get(pJobID).remove(pPid);}
//        catch(Exception e){}
    }

    /**
     * The number of the jobs which have not been started because of the missing input
     * @param pJob job name
     * @return number of the jobs
     */
    public long getNoInputJobCount(String pJob)
    {
        if(noinput.get(pJob)==null) return 0;
        return noinput.get(pJob).longValue();
    }

 

    /**
     * Adding new status information
     * @param pJobId job name
     * @param pJobPid PS ID
     * @param pStatus current status code
     * @param pResource executor resource
     * @param pTim status change time stamp
     */
    public synchronized void addJobbStatus(String pJobId, String pJobPid, String pStatus, String pResource, long pTim){
        int js=Integer.parseInt(pStatus);
        if(Status.isAbort(workflowStatus)&& (!Status.isEndStatus(js))) return;
        jobsStatus.putIfAbsent(new String(pJobId), new ConcurrentHashMap<String, JobStatusData>());
        jobsStatus.get(pJobId).putIfAbsent(new String(pJobPid),new JobStatusData());
        jobsStatus.get(pJobId).get(pJobPid).setPid(pJobPid);
/*
        if(jobsStatus.get(pJobId)==null) {
            System.out.println("createJOB:"+pJobId);
            Enumeration<String> enm=jobsStatus.keys();
            String key;
            while(enm.hasMoreElements()){
                key=enm.nextElement();
                System.out.println("liveJOB:"+key+":"+jobsStatus.get(key));
            }
        }
        if(jobsStatus.get(pJobId).get(pJobPid)==null) {
            System.out.println("createPid:"+pJobPid);
            jobsStatus.get(pJobId).put(new String(pJobPid),new JobStatusData());
            jobsStatus.get(pJobId).get(pJobPid).setPid(pJobPid);
        }
*/
        JobStatusData jobInstance=jobsStatus.get(pJobId).get(pJobPid);
//        if(Status.isFinished(jobInstance.getStatus())) return;

        

        if(pTim==(-1) || pTim>jobInstance.getTim()){
            jobsStatus.get(pJobId).get(pJobPid).setResource(new String(pResource));
            jobsStatus.get(pJobId).get(pJobPid).setTim(pTim);
            jobsStatus.get(pJobId).get(pJobPid).setStatus(js);
        }
    }
    
    /**
     * Removing job status from the registry
     * @param pJobId job name
     * @param pJobPid PS ID
     */
    public void removeJobStatus(String pJobId, String pJobPid) {
        if (jobsStatus.get(pJobId) != null) {
            if(jobsStatus.get(pJobId).get(pJobPid) != null) jobsStatus.get(pJobId).remove(pJobPid);
            if (jobsStatus.get(pJobId).isEmpty())jobsStatus.remove(pJobId);
        }
    }

    /**
     * Adding new jobs to the workflow
     * @param pValue job hash
     */
/*    public void addJobs(Hashtable pValue)
    {
        Enumeration enm=pValue.keys();
        String key="";
        while(enm.hasMoreElements())
        {
            key=""+enm.nextElement();
            jobsStatus.put(key, new ConcurrentHashMap());
        }
    }
*/
    /**
     * The status of the job instance
     * @return job status hash
     */
    public ConcurrentHashMap<String,ConcurrentHashMap<String,JobStatusData>> getJobsStatus()
    {
        return jobsStatus;
    }

    /**
     * Getting the status of the job's PS instances
     * @param pValue status hash <pd,status>
     * @return
     */
    public ConcurrentHashMap getJobStatus(String pValue)
    {
        return (ConcurrentHashMap)jobsStatus.get(pValue);
    }

    /**
     * Getting the job status
     * @return status hash
     */
    public Hashtable getCollectionJobsStatus()
    {
        Hashtable res=new Hashtable();
        Enumeration enm=jobsStatus.keys();
        String key="",skey="";
        while(enm.hasMoreElements())
        {
            key=""+enm.nextElement();
            Hashtable res0=new Hashtable();
            Enumeration enm0=jobsStatus.get(key).keys();
            if(0!=getFinishedJobCount(key)) res0.put("6", ""+getFinishedJobCount(key));
            if(0!=getNoInputJobCount(key)) res0.put("25", ""+getNoInputJobCount(key));

            while(enm0.hasMoreElements())
            {
                skey=""+enm0.nextElement();
                JobStatusData tmp=jobsStatus.get(key).get(skey);
                if(res0.get(tmp.getStatus())==null)
                {
                    res0.put(tmp.getStatus(), "1");
                }
                else
                {
                    long l=Long.parseLong(""+res0.get(tmp.getStatus()));
                    l++;
                    res0.put(tmp.getStatus(), ""+l);
                }
            }
            res.put(key, res0);
        }
        return res;
    }
    
    
    /**
     * Getting the status of the workflow instance
     * @return status code
     */
    public int getStatus(){return workflowStatus;}

    /**
     * Getting the text instance descriptor
     * @return text
     */
    public String getText(){return text;}
    
    /**
     * Getting the URL of the executor WFI service
     * @return URL
     */
    public String getWfiURL(){return wfiURL;}
    /**
     * Getting the resource of the executor WFI service
     * @return resource
     */
    public String getWfiService(){return wfiService;}
    /**
     * Setting the size the instance uses on the storage
     * @param pValue in bytes
     */
    public void setSize(long pValue){size=pValue;}
    /**
     * Getting the size the instance uses on the storage
     * @return pValue in bytes
     *
     */
    public long getSize(){return size;}

    /**
     * Setting the instance status
     * @param newWfStatus status code
     * @param pLastTime time/number stamp
     */
    public void setStatus(String newWfStatus, long pLastTime) {

        if(newWfStatus != null){
            int intNewWorkflowStatus=Integer.parseInt(newWfStatus);

            if(Status.isEndStatus(intNewWorkflowStatus) || pLastTime==(-1) ||
                !Status.isAborting(workflowStatus)
              ) {
                try {
                    setLastWorkflowStatus();
                    workflowStatus = intNewWorkflowStatus;
                    tim = pLastTime;
                } catch (Exception e) {e.printStackTrace();}
            }
        }
    }


    /**
     * Setting the status
     * @param pWorkflowStatus instance status
     * @param pLastTime time stamp
     * @param pJobName job name
     * @param pJobPid PS ID
     * @param pJobStatus job status
     * @param pJobResource executor resource
     */
    public synchronized  void setStatus(String pWorkflowStatus,long pLastTime, String pJobName, String pJobPid, String pJobStatus, String pJobResource)
    {
//        if(jobsStatus.get(pJobName)==null)jobsStatus.put(pJobName, new ConcurrentHashMap());
        setStatus(pWorkflowStatus, pLastTime);
//        System.out.println("WFSTATUS:"+pWorkflowStatus+":"+getStatus()+":"+pJobName+"."+pJobPid+"="+pJobStatus+"("+pLastTime+")");
        addJobbStatus(pJobName,pJobPid,pJobStatus,pJobResource,pLastTime);
/*        Map tmp=getJobStatus(pJobName);
        Iterator it=tmp.keySet().iterator();
        JobStatusData d;
        Object key;
        while(it.hasNext()){
            key=it.next();
            d=(JobStatusData)tmp.get(key);
            System.out.println("--"+pJobName+"."+key+"=>"+d.getName()+"."+d.getPid()+"="+d.getStatus()+"("+d.getTim()+")");
        }
*/
    }
    /**
     * Setting the instance's text descriptor
     * @param pValue text descriptor
     */
    public void setText(String pValue){text=pValue;}
    /**
     * Setting the WFI service data
     * @param pWfiUrl service URL
     * @param pWfiService service resource
     */
    public void setWfsData(String pWfiUrl, String pWfiService)
    {
        // System.out.println("NEW INSTANCE:"+pWfiUrl+"::"+pWfiService);
        wfiURL=pWfiUrl;
        wfiService=pWfiService;
    }

    /**
     * Getting the workflow instance status code before the last modification
     * @return status code
     */
    public int getLastWorkflowStatus() {
        return lastWorkflowStatus;
    }

    /**
     * Setting the workflow instance status code before the last modification
     */
    public void setLastWorkflowStatus() {
        this.lastWorkflowStatus = this.workflowStatus;
    }

    /**
     * Getting the notification descriptor
     * @return notification descriptor
     */
    public synchronized String getNotifyWfchgType() {
        return notifyWfchgType;
    }

    /**
     * Setting the notification descriptor
     * @param notifyWfchgType
     */
    public synchronized void setNotifyWfchgType(String notifyWfchgType) {
        // System.out.println("wfchgtype : " + notifyWfchgType);
        if (notifyWfchgType == null) {
            notifyWfchgType = new String("");
        }
        this.notifyWfchgType = notifyWfchgType;
    }
    
    /**
     * Deleting volatile outputs
     * @return if the operation was successful
     */
    public synchronized boolean volatileOutputsIsDeleted() {
        if (volatileOutputsDeleted) {
            return volatileOutputsDeleted;
        } else {
            // runs only at the first reference
            volatileOutputsDeleted = true;
            return false;
        }
    }
    
}

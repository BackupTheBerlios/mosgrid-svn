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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.pgportal.services.asm.threads;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.storage.com.UploadWorkflowBean;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.HistoryBean;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author akos
 */
public class ASMUploadThread  extends Thread {

    private String SID ;
    private String filename;
    private int uploadpercent = 0;
    private String userID;
    private String workflowID;
    private String confID;
    private String jobID;
    private boolean go = false;

    public boolean isGo() {
        return go;
    }

    public void setGo(boolean go) {
        this.go = go;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getPortID() {
        return portID;
    }

    public void setPortID(String portID) {
        this.portID = portID;
    }
    private String portID;

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }

    public String getConfID() {
        return confID;
    }

    public void setConfID(String confID) {
        this.confID = confID;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getUploadpercent() {
        return uploadpercent;
    }

    public void setUploadpercent(int uploadpercent) {
        this.uploadpercent = uploadpercent;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getWorkflowID() {
        return workflowID;
    }

    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
    }
    private PortalStorageClient uploadserviceclient;
    public ASMUploadThread(String userID,String workflowID,String jobID,String portID,String confID,String SID,String filename) {
        try {
            this.filename = filename + "_file";
            this.SID = SID;
            this.userID = userID;
            this.workflowID = workflowID;
            this.jobID = jobID;
            this.portID = portID;
            this.confID = confID;
            Hashtable hsup = new Hashtable();
            System.out.println("creating new service object...");
            ServiceType stup = InformationBase.getI().getService("storage", "portal", hsup, new Vector());
            uploadserviceclient = (PortalStorageClient) Class.forName(stup.getClientObject()).newInstance();
            uploadserviceclient.setServiceURL(stup.getServiceUrl());
            uploadserviceclient.setServiceID(stup.getServiceID());
        } catch (InstantiationException ex) {
            Logger.getLogger(ASMUploadThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ASMUploadThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ASMUploadThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    public void run() {
        try {


            int percent = uploadserviceclient.getUploadingFilePercent(SID, filename);


            while (percent < 100 && percent != -1) {
                
                percent = uploadserviceclient.getUploadingFilePercent(SID, filename);
                this.sleep(500);

            }

            //DB update
             PortalCacheService.getInstance().getUser(userID).setEditingJobData(jobID);
             if(PortalCacheService.getInstance().getUser(userID).getEditingJobData() != null){
                if (PortalCacheService.getInstance().getUser(userID).getEditingJobData().getInput(portID) != null){
                    if (PortalCacheService.getInstance().getUser(userID).getEditingJobData().getInput(portID).getData() != null){
                        PortalCacheService.getInstance().getUser(userID).getEditingJobData().getInput(portID).getData().remove("file");
                        PortalCacheService.getInstance().getUser(userID).getEditingJobData().getInput(portID).getData().put("file",filename);
                    }
                }

            
             }
             // end of dbUpdate
        // updating inputs
             // setting workflow to configure
            Hashtable hsh=new Hashtable();
              ServiceType st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());

            PortalWfsClient pc = null;
            try {
                try {
                    pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
                    pc.setServiceURL(st.getServiceUrl());
                    pc.setServiceID(st.getServiceID());
                    ComDataBean tmp=new ComDataBean();
                    tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                    tmp.setUserID(userID);
                    tmp.setWorkflowID(workflowID);

                PortalCacheService.getInstance().getUser(userID).setConfiguringWorkflow(pc.getWorkflowConfigData(tmp));
                } catch (InstantiationException ex) {
                    Logger.getLogger(ASMUploadThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ASMUploadThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ASMUploadThread.class.getName()).log(Level.SEVERE, null, ex);
            }

            Vector<JobPropertyBean> jobs = PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow();
        for(int i=0;i<jobs.size();i++)
            {
            
                if (jobs.get(i).getName().equals(new String(jobID))){
                    Vector<PortDataBean> ports = jobs.get(i).getInputs();
                    for (int j=0;j<ports.size();++j){
                        
                        if (Long.toString(ports.get(j).getSeq()).equals(new String(portID)) ){
                            ports.get(j).getData().remove(new String("file"));
                            ports.get(j).getData().put("file", filename);
                        }
                    }
                }
    
            }
            PortalCacheService.getInstance().getUser(userID).setConfiguringWorkflow(jobs);
        //end updating inputs

            String wfsID=PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getWfsID();
       

// history update

            PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow();
            String key="";
            String ovalue="",nvalue="";
            for(int i=0;i<PortalCacheService.getInstance().getUser(userID).getOConfiguringWorkflow().size();i++)
            {
                for(int k=0;k<((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getOConfiguringWorkflow().get(i)).getInputs().size();k++)
                {
                    PortDataBean tmp2=(PortDataBean)((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(i)).getInputs().get(k);
                    PortDataBean tmp1=(PortDataBean)((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getOConfiguringWorkflow().get(i)).getInputs().get(k);
                //torolve, modositva
                   Iterator itl=tmp1.getData().keySet().iterator();
                    while(itl.hasNext())
                        {
                        key=""+itl.next();
                //                    System.out.println("----"+key);
                        if(tmp2.getData().get(key)==null)
                        {
//                        System.out.println("-------delete.job."+tmp2.getSeq());
                            ovalue=""+tmp1.getData().get(key);
                            ((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("",""+tmp2.getSeq(),userID,"delete.input."+key,ovalue,""));
                        }
                        else
                            if(!tmp1.getData().get(key).equals(tmp2.getData().get(key)))
                            {
//                            System.out.println("-------modify.job."+key);
                            ovalue=""+tmp1.getData().get(key);
                            nvalue=""+tmp2.getData().get(key);
                          ((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("",""+tmp2.getSeq(),userID,"modify.input."+key,ovalue, nvalue));
                            }
                    }
//uj
                    itl=tmp2.getData().keySet().iterator();
                    while(itl.hasNext())
                        {
                        key=""+itl.next();
    //                    System.out.println("----"+key);
                        if(tmp1.getData().get(key)==null)
                            {
    //                      System.out.println("-------new.job."+key);
                              nvalue=""+tmp2.getData().get(key);
                            ((JobPropertyBean)PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("",""+tmp2.getSeq(),userID,"new.input."+key,"",nvalue));
                            }
                    }
                }
            }
         st=InformationBase.getI().getService("wfs","portal",hsh,new Vector());
        try
        {
             pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(wfsID);
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp=new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(userID);
            tmp.setWorkflowID(workflowID);
            tmp.setWorkflowtype(PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).getWorkflowType());
            pc.setWorkflowConfigData(tmp, PortalCacheService.getInstance().getUser(userID).getConfiguringWorkflow());
            System.out.println("SEND");
        }
        catch(Exception e){System.out.println("------------------");e.printStackTrace();}
            
            UploadWorkflowBean uwb = new UploadWorkflowBean();
            uwb.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            uwb.setUserID(userID);
            uwb.setWorkflowID(workflowID);
            uwb.setConfID(confID);
            
            uploadserviceclient.uploadWorkflowFiles(uwb);
            

            this.go = true;
        } catch (InterruptedException ex) {
            Logger.getLogger(ASMUploadThread.class.getName()).log(Level.SEVERE, null, ex);



    }
}
}

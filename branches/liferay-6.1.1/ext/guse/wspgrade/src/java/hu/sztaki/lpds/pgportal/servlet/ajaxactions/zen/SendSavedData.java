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
 * SendSavedData.java
 * Raw saving of workflow configiguration data on the server side ( WFS) 
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions.zen;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.GemlcaCacheService;
import hu.sztaki.lpds.pgportal.service.workflow.RealWorkflowUtils;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.servlet.ajaxactions.BASEActions;
import hu.sztaki.lpds.storage.com.UploadWorkflowBean;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import hu.sztaki.lpds.wfs.com.*;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Raw saving of workflow configiguration data on the server side ( WFS) 
 *
 * @author krisztian karoczkai
 */
public class SendSavedData extends BASEActions {

    public SendSavedData() {
    }

    @Override
    public String getDispacher(Hashtable pParams) {
        return "/jsp/msg.jsp";
    }

    @Override
    public String getOutput(Hashtable pParams) {
        return null;
    }

    @Override
    public Hashtable getParameters(Hashtable pParams) {
        Enumeration<String> enmParams = pParams.keys();
        String keyParams;
        while (enmParams.hasMoreElements()) {
            keyParams = enmParams.nextElement();
            System.out.println("---" + keyParams + "=" + pParams.get(keyParams));
        }

        String user = "" + pParams.get("user");

        PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow();
        String key = "";
        String ovalue = "", nvalue = "";
        for (int i = 0; i < PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().size(); i++) {
// init history vector
            ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().clear();
// init set jobistype (Job is Workflow, Job is Service, Job is Binary) begin
            // key = "jobistype"
            // In case of "Job is Workflow"  jobistype key = workflow
            // In case of "Job is Service"   jobistype key = service
            // In case of "Job is Binary"    jobistype key = binary
            System.out.println("*****" + ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getName());

            HashMap jhash = ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe();
            String jobistype = null;
            if (jhash.get("iworkflow") != null) {
                jobistype = "workflow";
            } else if ((jhash.get("servicetype") != null) || (jhash.get("serviceurl") != null) || (jhash.get("servicemethod") != null)) {
                jobistype = "service";
            } else if ((jhash.get("cloudtype") != null) || (jhash.get("gaeurl") != null)) {
                jobistype = "cloud";
            } else if (jhash.get("gridtype") != null) {
                jobistype = "binary";
            }
            Iterator<String> enmJ = jhash.keySet().iterator();
            String kesJ;
            while (enmJ.hasNext()) {
                kesJ = enmJ.next();
                System.out.println(kesJ + "=" + jhash.get(kesJ));
            }

            if (jobistype != null) {
                if (!"binary".equals(jobistype)) {
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("binary");
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("grid");
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("type");
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("gridtype");
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("jobmanager");
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("nodenumber");
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("resource");
                }
                if (!"service".equals(jobistype)) {
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("servicetype");
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("serviceurl");
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("servicemethod");
                }
                if (!"workflow".equals(jobistype)) {
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().remove("iworkflow");
                }
                if (!"cloud".equals(jobistype)) {
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().remove("cloudtype");
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().remove("gaeurl");
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getEditingJobData()).getExe().remove("defaultgaeservice");
                }

                ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().put("jobistype", jobistype);
            }


// init set jobistype end
//        System.out.println("::::"+i);
// Previous value of the job has been changed or deleted
            Iterator itl = ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getExe().keySet().iterator();
            while (itl.hasNext()) {
                key = "" + itl.next();
                if (((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().get(key) == null) {
                    ovalue = "" + ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getExe().get(key);
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "-", user, "delete.job." + key, ovalue, ""));
                } else if (!((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().get(key).equals(((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getExe().get(key))) {
                    ovalue = "" + ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getExe().get(key);
                    nvalue = "" + ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().get(key);
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "-", user, "modify.job." + key, ovalue, nvalue));
                }
            }
// New value for the job
            itl = ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().keySet().iterator();
            while (itl.hasNext()) {
                key = "" + itl.next();
                if (((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getExe().get(key) == null) {
                    nvalue = "" + ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().get(key);
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "-", user, "new.job." + key, "", nvalue));
                }
            }


//input             
            for (int k = 0; k < ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getInputs().size(); k++) {
                PortDataBean tmp2 = (PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getInputs().get(k);
                PortDataBean tmp1 = (PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getInputs().get(k);
//deleted, changed                
                itl = tmp1.getData().keySet().iterator();
                while (itl.hasNext()) {
                    key = "" + itl.next();
//                    System.out.println("----"+key);
                    if (tmp2.getData().get(key) == null) {
//                        System.out.println("-------delete.job."+tmp2.getSeq());
                        ovalue = "" + tmp1.getData().get(key);
                        ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "" + tmp2.getSeq(), user, "delete.input." + key, ovalue, ""));
                    } else if (!tmp1.getData().get(key).equals(tmp2.getData().get(key))) {
//                            System.out.println("-------modify.job."+key);
                        ovalue = "" + tmp1.getData().get(key);
                        nvalue = "" + tmp2.getData().get(key);
                        ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "" + tmp2.getSeq(), user, "modify.input." + key, ovalue, nvalue));
                    }
                }
//new
                itl = tmp2.getData().keySet().iterator();
                while (itl.hasNext()) {
                    key = "" + itl.next();
//                    System.out.println("----"+key);
                    if (tmp1.getData().get(key) == null) {
                        //                      System.out.println("-------new.job."+key);
                        nvalue = "" + tmp2.getData().get(key);
                        ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "" + tmp2.getSeq(), user, "new.input." + key, "", nvalue));
                    }
                }
            }
//output
            for (int k = 0; k < ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getOutputs().size(); k++) {
                PortDataBean tmp2 = (PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getOutputs().get(k);
                PortDataBean tmp1 = (PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getOutputs().get(k);
//deleted,changed                
                itl = tmp1.getData().keySet().iterator();
                while (itl.hasNext()) {
                    key = "" + itl.next();
                    if (tmp2.getData().get(key) == null) {
                        ovalue = "" + tmp1.getData().get(key);
                        ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "" + tmp1.getSeq(), user, "delete.output." + key, ovalue, ""));
                    } else if (!tmp1.getData().get(key).equals(tmp2.getData().get(key))) {
                        ovalue = "" + tmp1.getData().get(key);
                        nvalue = "" + tmp2.getData().get(key);
                        ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "" + tmp2.getSeq(), user, "modify.output." + key, ovalue, nvalue));
                    }
                }
//new
                itl = tmp2.getData().keySet().iterator();
                while (itl.hasNext()) {
                    key = "" + itl.next();
                    if (tmp1.getData().get(key) == null) {
                        nvalue = "" + tmp2.getData().get(key);
                        ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "" + tmp2.getSeq(), user, "new.output." + key, "", nvalue));
                    }
                }
            }
//descriptor

            HashMap ndesc = ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getDesc();
            HashMap odesc = ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getDesc();

//deleted, changed 
            Iterator it0 = odesc.keySet().iterator();
            key = "";
            while (it0.hasNext()) {
                key = "" + it0.next();
                if (ndesc.get(key) != null) {
                    if (ndesc.get(key).equals("")) {
                        ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "-", user, "delete.desc." + key, "" + odesc.get(key), ""));
                    } else {
                        if (!ndesc.get(key).equals(odesc.get(key))) {
                            ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "-", user, "modify.desc." + key, "" + odesc.get(key), "" + ndesc.get(key)));
                        }
                    }
                }
            }
// uj            
            Iterator it = ndesc.keySet().iterator();
            while (it.hasNext()) {
                key = "" + it.next();
                if (odesc.get(key) == null) {
                    ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getHistory().add(new HistoryBean("", "-", user, "new.desc." + key, "", "" + ndesc.get(key)));
                }
            }
        /*
        // JDL/RSL deleted entries - begin
        HashMap newdesc = ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getDesc();
        HashSet removeList = new HashSet();
        // System.out.println("JDL/RSL newmap : " + newdesc);
        // System.out.println("JDL/RSL jkey begin");
        Iterator jit = newdesc.keySet().iterator();
        String jkey = new String("");
        String startStr = new String(((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().get("gridtype") + ".key");
        // System.out.println("JDL/RSL startStr : " + startStr);
        while(jit.hasNext()) {
        jkey = "" + jit.next();
        // System.out.println("JDL/RSL jkey : " + jkey);
        if(newdesc.get(jkey) != null) {
        if(newdesc.get(jkey).equals("")) {
        // System.out.println("JDL/RSL delete jkey : " + jkey);
        removeList.add(jkey);
        }
        if(!jkey.startsWith(startStr)) {
        // System.out.println("JDL/RSL delete start jkey : " + jkey);
        removeList.add(jkey);
        }
        }
        }
        // System.out.println("JDL/RSL removeList : " + removeList);
        jit = removeList.iterator();
        while(jit.hasNext()) {
        jkey = "" + jit.next();
        // System.out.println("JDL/RSL removeList jkey : " + jkey);
        ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getDesc().remove(jkey);
        if (((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getDesc().containsKey(jkey)) {
        ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(i)).getDesc().remove(jkey);
        }
        }
        // System.out.println("JDL/RSL newmap : " + newdesc);
        // System.out.println("JDL/RSL newmap2: " + ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getDesc());
        // System.out.println("JDL/RSL jkey end");
        // JDL/RSL deleted entries - end
         */
        }


        // This part is needed because of gemlca related checks
        // parse job list
        HashMap tGelmca;
        for (int i = 0; i < PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().size(); i++) {
            tGelmca = ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe();
            String gridtype = "" + tGelmca.get("gridtype");
            if (gridtype != null) {
                if (gridtype.equalsIgnoreCase("gemlca")) {
                    String grid = (String) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().get("grid");
                    String resource = (String) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().get("resource");
                    // parsee job inputs list
                    for (int k = 0; k < ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getInputs().size(); k++) {
                        // tmp = aktualis output port bean
                        PortDataBean tmp = (PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getInputs().get(k);
                        String intname = (String) tmp.getData().get("intname");
                        String gparams = (String) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().get("params");//("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("params")).split(" ");

                        if (!GemlcaCacheService.getInstance().isValidPortIName(grid, resource, intname, gparams, true)) {
                            // Deletes the "intname". The command "info"
                            // indicates the configuration error
                            String newiname = GemlcaCacheService.getInstance().getFirstValidPortIName(grid, resource, gparams, true);
                            if (((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getInputs().size() == 1 && !newiname.equals("")) { //If there is only a single port, it receives the default value
                                ((PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getInputs().get(k)).getData().put("intname", newiname);
                            } else {
                                ((PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getInputs().get(k)).getData().remove("intname");
                            }
                        }
                    }
                    // parsee job outputs list
                    for (int k = 0; k < ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getOutputs().size(); k++) {
                        // tmp = aktual output port bean
                        PortDataBean tmp = (PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getOutputs().get(k);
                        String intname = (String) tmp.getData().get("intname");
                        String gparams = (String) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getExe().get("params");//("" + PortalCacheService.getInstance().getUser(user).getEditingJobData().getExe().get("params")).split(" ");

                        if (!GemlcaCacheService.getInstance().isValidPortIName(grid, resource, intname, gparams, false)) {
                            // Deletes the "intname". The command "info"
                            // indicates the configuration error
                            String newiname = GemlcaCacheService.getInstance().getFirstValidPortIName(grid, resource, gparams, false);
                            if (((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getOutputs().size() == 1 && !newiname.equals("")) { //ha csak egy port van, default értéket kapjon
                                ((PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getOutputs().get(k)).getData().put("intname", newiname);
                            } else {
                                ((PortDataBean) ((JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(i)).getOutputs().get(k)).getData().remove("intname");//put("intname", "");
                            }
                        }
                    }
                }
            }
        }
        // This part is needed because of gemlca related checks



// Gathers the files which must be deleted because the configuration of the workflow has been changed
        // 
        // Example: If the type of an input port has been changed from "local" to a different one ("vale", "remote","sql")
        // then the uploaded file must be deleted from the server side storage storage.
        // 
        // 
        // The same is true for the executable in the caase when the job type has been modified.
        // 
        Hashtable deletedFiles = new Hashtable();
        String sep = FileUtils.getInstance().getSeparator();
        // parse job list...
        for (int jPos = 0; jPos < PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().size(); jPos++) {
            try {
                JobPropertyBean oldJob = (JobPropertyBean) PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().get(jPos);
                if (oldJob.getExe().containsKey("jobistype")) {
                    String oldJobType = (String) oldJob.getExe().get("jobistype");
                    // System.out.println("deletedFiles oldJob.getName() : " + oldJob.getName());
                    // System.out.println("deletedFiles oldJob.getExe() : " + oldJob.getExe());
                    // System.out.println("deletedFiles oldJobType : " + oldJobType);
                    JobPropertyBean newJob = (JobPropertyBean) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow().get(jPos);
                    String newJobType = (String) newJob.getExe().get("jobistype");
                    // System.out.println("deletedFiles newJob.getName() : " + newJob.getName());
                    // System.out.println("deletedFiles newJob.getExe() : " + newJob.getExe());
                    // System.out.println("deletedFiles newJobType : " + newJobType);
                    String newJobName = (String) newJob.getName();
                    //
                    // csak azoknal a joboknal toroljuk az execute file-t
                    // amely jobok binary tipus-rol valtoztak
                    if ("binary".equals(oldJobType)) {
                        if (!oldJobType.equals(newJobType)) {
                            String keyPath = newJobName + sep + FileUtils.getInstance().getDefaultBinaryName();
                            deletedFiles.put(keyPath, "");
                        }
                    }
                    // service es workflow job tipus eseten
                    // is kell input portokat vizsgalni...
                    // parse input port list...
                    for (int pPos = 0; pPos < oldJob.getInputs().size(); pPos++) {
                        try {
                            PortDataBean oldPort = (PortDataBean) oldJob.getInputs().get(pPos);
                            // System.out.println("deletedFiles oldPort.getSeq() : " + oldPort.getSeq());
                            // System.out.println("deletedFiles oldPort.getData() : " + oldPort.getData());
                            PortDataBean newPort = (PortDataBean) newJob.getInputs().get(pPos);
                            // System.out.println("deletedFiles newPort.getSeq() : " + newPort.getSeq());
                            // System.out.println("deletedFiles newPort.getData() : " + newPort.getData());
                            //
                            String oldPortType = "";
                            if (oldPort.getData().containsKey("file")) {
                                oldPortType = "file";
                            }
                            // System.out.println("deletedFiles oldPortType : " + oldPortType);
                            String newPortType = "";
                            if (newPort.getData().containsKey("file")) {
                                newPortType = "file";
                            }
                            // System.out.println("deletedFiles newPortType : " + newPortType);
                            if ("file".equals(oldPortType)) {
                                if (!oldPortType.equals(newPortType)) {
                                    String keyPath = newJobName + sep + "inputs" + sep + new String().valueOf(newPort.getSeq()) + sep;
                                    deletedFiles.put(keyPath, "");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Gathers the files which must be deleted




        String workflow = "" + pParams.get("workflow");
        String wfsID = PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).getWfsID();
        Hashtable hsh = new Hashtable();
        hsh.put("url", wfsID);
        ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
        try {
            PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(wfsID);
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp = new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(user);
            tmp.setWorkflowID(workflow);
            tmp.setWorkflowtype(PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).getWorkflowType());
            pc.setWorkflowConfigData(tmp, PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow());
            System.out.println("SEND");
            Vector<JobPropertyBean> tl = (Vector<JobPropertyBean>) PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow();
            HashMap<String, String> ttll;
            String keyll;
            Iterator<String> enmll;
            for (JobPropertyBean tll : tl) {
                ttll = tll.getDesc();
                enmll = ttll.keySet().iterator();
                while (enmll.hasNext()) {
                    keyll = enmll.next();
                    System.out.println(tll.getName() + ":" + keyll + "=" + ttll.get(keyll));
                }

            }
// history mododifications are saved, updating of "old history" data in order to aviod the multiplicated savings of the new items
            PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().clear();
            PortalCacheService.getInstance().getUser(user).getOConfiguringWorkflow().addAll(PortalCacheService.getInstance().getUser(user).getConfiguringWorkflow());
// delete old instances begin
            String radioDeleteYes = (String) pParams.get("radioDeleteYes");
            if ("true".equalsIgnoreCase(radioDeleteYes)) {
                DeleteOldWorkflowInstance backThread = new DeleteOldWorkflowInstance(user, workflow);
                backThread.start();
            }
        } catch (Exception e) {
            System.out.println("------------------");
            e.printStackTrace();
        }
        Hashtable res = new Hashtable();
        res.put("msg", "workflow.config.save.data");



// send configure ID to storage begin
        boolean retUploadFiles = true;
        try {
            String storageID = PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).getStorageID();
            String confID = (String) pParams.get("confIDparam");
            Hashtable hshsto = new Hashtable();
            hshsto.put("url", storageID);
            ServiceType sts = InformationBase.getI().getService("storage", "portal", hshsto, new Vector());
            //
            PortalStorageClient psc = (PortalStorageClient) Class.forName(sts.getClientObject()).newInstance();
            psc.setServiceURL(storageID);
            psc.setServiceID(sts.getServiceID());
            UploadWorkflowBean uwb = new UploadWorkflowBean();
            uwb.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            uwb.setUserID(user);
            uwb.setWorkflowID(workflow);
            uwb.setConfID(confID);
            uwb.setDeletedFiles(deletedFiles);
            retUploadFiles = psc.uploadWorkflowFiles(uwb);
            if (!retUploadFiles) {
                res.put("msg", "workflow.config.files.notuploaded");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        // return mess
        return res;
    }
}

class DeleteOldWorkflowInstance extends Thread {

    private String user,  workflow;

    public DeleteOldWorkflowInstance(String user, String workflow) {
        this.user = user;
        this.workflow = workflow;
    }

    @Override
    public void run() {
        setPriority(MIN_PRIORITY);
        String runtimeID;
        //nyilvantartas
        ConcurrentHashMap rtiHash = PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).getAllRuntimeInstance();
        PortalCacheService.getInstance().getUser(user).getWorkflow(workflow).getAllRuntimeInstance().clear();
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!rtiHash.isEmpty()) {
            Iterator rit = rtiHash.keySet().iterator();
            while (rit.hasNext()) {
                runtimeID = (String) rit.next();
                RealWorkflowUtils.getInstance().deleteWorkflowInstance(user, workflow, runtimeID);
            }
        }
    }
}
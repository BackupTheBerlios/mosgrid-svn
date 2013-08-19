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
package hu.sztaki.lpds.pgportal.service.workflow;

import dci.data.Certificate;
import dci.data.Middleware;
import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.WorkflowData;
import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;
import hu.sztaki.lpds.storage.inf.PortalStorageClient;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.PortDataBean;
import hu.sztaki.lpds.wfs.com.WorkflowConfigErrorBean;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.globus.gsi.GlobusCredential;
import xmlbeans.org.oasis.saml2.assertion.AssertionDocument;

/**
 * Using the RealWorkflowPortlet and the
 * SendSavedData. (helper class)
 *
 * @author lpds
 */
public class RealWorkflowUtils {

    private static RealWorkflowUtils instance = null;
/**
 * Constructor, creating singleton instance
 */
    public RealWorkflowUtils() {
        if (instance == null) {
            instance = this;
        }
    }

    /**
     * Returns the RealWorkflowUtils instance.
     * 
     * @return 
     */
    public static RealWorkflowUtils getInstance() {
        if (instance == null) {
            instance = new RealWorkflowUtils();
        }
        return instance;
    }

    /**
     * Deletes a workflow instance.
     *
     * @param userID - user ID
     * @param workflowID - workflow ID
     * @param runtimeID - workflow instance, runtime ID
     */
    public synchronized void deleteWorkflowInstance(String userID, String workflowID, String runtimeID) {
        WorkflowData wData = PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID);
//inventory
        PortalCacheService.getInstance().getUser(userID).getWorkflow(workflowID).deleteRuntime(runtimeID);
//wfs
        Hashtable hsh = new Hashtable();
        hsh.put("url", wData.getWfsID());
        ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
        try {
            PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            ComDataBean tmp = new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(userID);
            tmp.setWorkflowID(workflowID);
            tmp.setWorkflowRuntimeID(runtimeID);
            pc.deleteWorkflow(tmp);
//storage
            hsh = new Hashtable();
            hsh.put("url", wData.getStorageID());
            st = InformationBase.getI().getService("storage", "portal", hsh, new Vector());
            // try {
            PortalStorageClient ps = (PortalStorageClient) Class.forName(st.getClientObject()).newInstance();
            ps.setServiceURL(st.getServiceUrl());
            ps.setServiceID(st.getServiceID());
            ComDataBean tmp2 = new ComDataBean();
            tmp2.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp2.setUserID(userID);
            tmp2.setWorkflowID(workflowID);
            tmp2.setWorkflowRuntimeID(runtimeID);
            ps.deleteWorkflowInstance(tmp2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the given workflow
     * is configured properly
     *
     * @param userID - user ID
     * @param wfData - workflow data
     * @return true - if the workflow is configured properly
     */
    public synchronized boolean isWellConfiguredWorkflow(String userID, WorkflowData wfData) {
        if (getWorkflowConfigErrorVector(null, userID, wfData).size() > 0) {
            return false;
        }
        return true;
    }

    /**
     * Checks whether the given workflow
     * is configured properly
     * (Gets the configuration error data of the given workflow from the wfs)
     * If mvList is NULL, proxy and resource check is skipped.
     *
     * @param mvList - middleware list
     * @param userID - user ID
     * @param wfData - workflow data
     * @return Vector - list of configuration errors
     */
    public Vector getWorkflowConfigErrorVector(List<Middleware> mvList, String userID, WorkflowData wfData) {
        Vector<WorkflowConfigErrorBean> retErrorVector = new Vector<WorkflowConfigErrorBean>();
        try {
            //
            ComDataBean tmpBean = new ComDataBean();
            tmpBean.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmpBean.setUserID(userID);
            tmpBean.setWorkflowID(wfData.getWorkflowID());
            //
            Hashtable hsh = new Hashtable();
            hsh.put("url", wfData.getWfsID());
            ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
            PortalWfsClient wfsClient = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
            wfsClient.setServiceURL(st.getServiceUrl());
            wfsClient.setServiceID(st.getServiceID());
            // get error
            retErrorVector = wfsClient.getWorkflowConfigDataError(tmpBean);

//            for (int ePos = 0; ePos < errorVector.size(); ePos++) {
//                WorkflowConfigErrorBean eBean = errorVector.get(ePos);
//                //System.out.println("WorkflowConfigErrorBean:" +eBean.getWorkflowID()+" "+eBean.getPortID()+" "+eBean.getJobName()+" "+eBean.getErrorID() );
//                // simply error mess
//                eBean.setErrorID(PortalMessageService.getI().getMessage(eBean.getErrorID()));
//                retErrorVector.addElement(eBean);
//            }// for end

            if (mvList != null) {// check proxy
                Vector<JobPropertyBean> jobs = wfsClient.getWorkflowConfigData(tmpBean);
                //System.out.println("getWorkflowConfigErrorVector - jobs.size:" + jobs.size());
                String gridType;
                File proxyFile;
                for (JobPropertyBean job : jobs) {
                    gridType = (String) job.getExe().get("gridtype");
                    if (gridType != null) {
                        if ("edgi".equals(gridType)){
                            gridType="glite";
                        }
                        List<Certificate> supportedcert = ConfigHandler.getSupportedCertsIfAvailable(mvList, gridType, "" + job.getExe().get("grid"));
                        //System.out.println("gridType" + gridType + " job.getExe().get(grid):"+job.getExe().get("grid"));
                        if (supportedcert == null) {//ERROR in WF config, middleware/resource not supported                            
                            WorkflowConfigErrorBean eBean = new WorkflowConfigErrorBean();
                            eBean.setJobName(job.getName());
                            eBean.setErrorID("error.job.notsupported");// middleware/resource is disabled or not supported
                            retErrorVector.add(eBean);
                        } else {
                            Iterator it = supportedcert.iterator();
                            Certificate cert = null;
                            while (it.hasNext()) {/**@toDO only the last allowed cert type is checked!!! **/
                                cert = (Certificate) it.next();
                            //System.out.println("cert.value" + cert.value());
                            }

                            if (cert != null) {//need cert
                                proxyFile = new File(PropertyLoader.getInstance().getProperty("prefix.dir") + "users/" + userID + "/x509up." + job.getExe().get("grid"));
                                if (!proxyFile.exists()) {
                                    WorkflowConfigErrorBean eBean = new WorkflowConfigErrorBean();
                                    eBean.setJobName(job.getName());
                                    eBean.setErrorID("error.job.noproxy");
                                    retErrorVector.add(eBean);
                                } else {//cert exist, check
                                    if (cert.equals(Certificate.X_509_GSI) || cert.equals(Certificate.X_509_RFC)) {
                                        GlobusCredential gcred = new GlobusCredential(new FileInputStream(proxyFile));
                                        if (gcred != null) {
                                            if (gcred.getTimeLeft() <= 0) {
                                                WorkflowConfigErrorBean eBean = new WorkflowConfigErrorBean();
                                                eBean.setJobName(job.getName());
                                                eBean.setErrorID("error.job.proxyexpired");
                                                retErrorVector.add(eBean);
                                            }
                                        }
                                    } else if (cert.equals(Certificate.SAML)) {
                                        AssertionDocument td = AssertionDocument.Factory.parse(proxyFile);
                                        long endTime = td.getAssertion().getConditions().getNotOnOrAfter().getTimeInMillis();
                                        if (!isValidSaml(endTime)) {
                                            WorkflowConfigErrorBean eBean = new WorkflowConfigErrorBean();
                                            eBean.setJobName(job.getName());
                                            eBean.setErrorID("error.job.samlexpired");
                                            retErrorVector.add(eBean);
                                        }
                                    }
                                }
                            }
                        }
                    } else { // embedded WF
                        String internalWorkflowName = (String) job.getExe().get("iworkflow");
                        //System.out.println("B embedWorkflowName : " + internalWorkflowName);
                        if (internalWorkflowName != null) {
                            boolean wfExist = PortalCacheService.getInstance().getUser(userID).getWorkflows().containsKey(internalWorkflowName);
                            //System.out.println("B wfExist           : " + wfExist);
                            if (!wfExist) {
                                WorkflowConfigErrorBean eBean = new WorkflowConfigErrorBean();
                                eBean.setJobName(job.getName());
                                //eBean.setErrorID("error.workflowjob.iworkflow");
                                eBean.setErrorID(PortalMessageService.getI().getMessage("error.workflowjob.iworkflow") + " (" + internalWorkflowName + ")");
                                retErrorVector.add(eBean);
                            }
                            //if the jobs input ports contains remote input -> error
                            for (int i = 0; i < job.getInputs().size(); i++) {
                                PortDataBean input = (PortDataBean) job.getInputs().get(i);
                                //System.out.println("PortDataBean-input.getData():" + input.getData().toString());
                                //System.out.println("prejob:" + input.getPrejob() + " preout:" + input.getPreoutput());
                                if (!"".equals(input.getPrejob())) {
                                    if (isRemoteOut(jobs, input.getPrejob(), input.getPreoutput())) {
                                        WorkflowConfigErrorBean eBean = new WorkflowConfigErrorBean();
                                        eBean.setJobName(job.getName());
                                        eBean.setPortID("" + input.getSeq());
                                        eBean.setErrorID("error.workflowjob.iworkflow.remotepre");
                                        retErrorVector.add(eBean);
                                    }
                                } else if (!input.getData().containsKey("file")) {//remote,sql, value
                                    WorkflowConfigErrorBean eBean = new WorkflowConfigErrorBean();
                                    eBean.setJobName(job.getName());
                                    eBean.setPortID("" + input.getSeq());
                                    eBean.setErrorID("error.workflowjob.iworkflow.notallowedinput");
                                    retErrorVector.add(eBean);
                                }

                            }
                        }
                    }
                }
            } else {
                //System.out.println("getWorkflowConfigErrorVector - no resource list, resources are not verified");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retErrorVector;
    }

    private boolean isRemoteOut(Vector<JobPropertyBean> jobs, String jobname, String portname) {
        for (JobPropertyBean job : jobs) {
            if (jobname.equals(job.getName())) {
                try {
                    //System.out.println("isRemoteOut-getdata:" + ((PortDataBean)job.getOutputs().get(Integer.parseInt(portname))).getData());
                    if (((PortDataBean) job.getOutputs().get(Integer.parseInt(portname))).getData().containsKey("remote")) {
                        return true;
                    }
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

/**
     * Calculates the validity of the SAML Assertion.
     * @param endt Long
     * @return valid or not
     */
   private boolean isValidSaml(long endt) {
        long endTime = endt;
        long currTime = new Date().getTime();
        if (endTime > currTime) {
            return true;
        }
        return false;
    }
   
}

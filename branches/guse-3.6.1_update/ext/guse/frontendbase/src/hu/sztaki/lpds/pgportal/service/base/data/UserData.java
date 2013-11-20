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
 * Data from the user stored in the memory cache
 */

package hu.sztaki.lpds.pgportal.service.base.data;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.storage.com.StoragePortalUserBean;
import hu.sztaki.lpds.storage.net.wsaxis13.PortalStorageClientImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import hu.sztaki.lpds.wfs.com.*;
import hu.sztaki.lpds.wfs.inf.PortalWfsClient;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.information.local.InformationBase;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author krisztian
 */
public class UserData 
{
    private ConcurrentHashMap<String,WorkflowData> abstracktWorkflows=new ConcurrentHashMap<String,WorkflowData>();
    private ConcurrentHashMap<String,WorkflowData> templateWorkflows=new ConcurrentHashMap<String,WorkflowData>();
    private ConcurrentHashMap<String,WorkflowData> workflows=new ConcurrentHashMap<String,WorkflowData>();
    private JobPropertyBean aJobData;
    private Vector configuringWorkflow=new Vector();
    private Vector oconfiguringWorkflow=new Vector();
    private Hashtable configuringWorkflowProp=new Hashtable();
    private Hashtable oconfiguringWorkflowProp=new Hashtable();   
    private Vector configuringEParams=new Vector();
    private String aJobID;
    private long timeOut=0;
    private long quotaBytes=0;

    /**
     * Constructor 
     */
    public UserData() {/*System.out.println("CREATE:UserData");*/}
    
    /**
     * Sets the workflow property data on the currently configured workflow
     * ( calls the setConfiguringWorkflow(Vector pValue) )
     * @param pValue jobs of the workflow
     * @param wfprop workflow configuration parameters
     */
    public void setConfiguringWorkflowWFProp(Vector pValue, Vector wfprop) {
        setConfiguringWorkflow(pValue);
        configuringWorkflowProp.clear();
        oconfiguringWorkflowProp.clear();
        if (!wfprop.isEmpty()) {
            for (int i = 0; i < wfprop.size(); i++) {
                String[] kv = ((String) wfprop.get(i)).split(";=", 3);
                if (kv.length == 3) {
                    if (!configuringWorkflowProp.containsKey(kv[0])) {
                        configuringWorkflowProp.put(kv[0], new Hashtable());
                        oconfiguringWorkflowProp.put(kv[0], new Hashtable());
                    }
                    ((Hashtable) configuringWorkflowProp.get(kv[0])).put(kv[1], kv[2]);
                    ((Hashtable) oconfiguringWorkflowProp.get(kv[0])).put(kv[1], kv[2]);
                } else if (kv.length == 2) {
                    configuringWorkflowProp.put(kv[0], kv[1]);
                    oconfiguringWorkflowProp.put(kv[0], kv[1]);
                } else {
//                    System.out.println("setConfiguringWorkflowWFProp - key-value error");
                }
            }
        }
    }
    
    /**
     * Sets the workflow to be configured
     * @param pValue job list vector
     */
    public void setConfiguringWorkflow(Vector pValue)
    {
        configuringWorkflow=pValue;
        oconfiguringWorkflow=new Vector();
        for(int i=0;i<configuringWorkflow.size();i++)
        {
            JobPropertyBean tmp=new JobPropertyBean();
            tmp.setId(((JobPropertyBean)configuringWorkflow.get(i)).getId());
            tmp.setName(((JobPropertyBean)configuringWorkflow.get(i)).getName());
            tmp.setTxt(((JobPropertyBean)configuringWorkflow.get(i)).getTxt());
            tmp.setX(((JobPropertyBean)configuringWorkflow.get(i)).getX());
            tmp.setY(((JobPropertyBean)configuringWorkflow.get(i)).getY());
            Iterator itr=((JobPropertyBean)configuringWorkflow.get(i)).getExe().keySet().iterator();
            while(itr.hasNext())
            {
                String key=""+itr.next();
                tmp.addExe(key,""+((JobPropertyBean)configuringWorkflow.get(i)).getExe().get(key));
            }

            for(int j=0;j<((JobPropertyBean)configuringWorkflow.get(i)).getInputs().size();j++) 
            {
                PortDataBean tmp0=new PortDataBean();
                HashMap tmp1=new HashMap();
                PortDataBean tmpp=(PortDataBean)((JobPropertyBean)configuringWorkflow.get(i)).getInputs().get(j);
                tmp0.setId(tmpp.getId());
                tmp0.setName(tmpp.getName());
                itr=tmpp.getData().keySet().iterator();
                while(itr.hasNext())
                {
                    String key=""+itr.next();
                    tmp1.put(key,""+tmpp.getData().get(key));
                }
                tmp0.setData(tmp1);
                tmp.addInput(tmp0);
            }

            for(int j=0;j<((JobPropertyBean)configuringWorkflow.get(i)).getOutputs().size();j++) 
            {
                PortDataBean tmp0=new PortDataBean();
                HashMap tmp1=new HashMap();
                PortDataBean tmpp=(PortDataBean)((JobPropertyBean)configuringWorkflow.get(i)).getOutputs().get(j);
                tmp0.setId(tmpp.getId());
                tmp0.setName(tmpp.getName());
                itr=tmpp.getData().keySet().iterator();
                while(itr.hasNext())
                {
                    String key=""+itr.next();
                    tmp1.put(key,""+tmpp.getData().get(key));
                }
                tmp0.setData(tmp1);
                tmp.addOutput(tmp0);
            }
            
            oconfiguringWorkflow.add(tmp);
        }
    }
    
    /**
     * Getting the workflow being configured
     * @return List of the containing jobs of the workflow
     */
    public Vector getConfiguringWorkflow(){return configuringWorkflow;}
    
    /**
     * Getting the original (before the configuration) data of the workflow being configured
     * @return job list
     */
    public Vector getOConfiguringWorkflow(){return oconfiguringWorkflow;}
    
    /**
     * Getting the workflow properties of the workflow being configured
     * @return property hash
     */
    public Hashtable getConfiguringWorkflowProp(){return configuringWorkflowProp;}
    
    /**
     * Getting the original (before the configuration) values of the parameters of the workflow being configured
     * @return property hash
     */
    public Hashtable getOConfiguringWorkflowProp(){return oconfiguringWorkflowProp;}
   
    /**
     * Returns the workflow properties in a vector in a suitable format to save in a database
     * @return property list
     */
    public Vector getConfiguringWorkflowPropV() {
        Vector vp = new Vector();

        Iterator itr = configuringWorkflowProp.keySet().iterator();
        while (itr.hasNext()) {
            String key = "" + itr.next();
            if (configuringWorkflowProp.get(key).getClass().equals(Hashtable.class)) {//if 2 levels
                Iterator itr2 = ((Hashtable) configuringWorkflowProp.get(key)).keySet().iterator();
                while (itr2.hasNext()) {
                    String key2 = "" + itr2.next();
                    vp.add(key + ";=" + key2 + ";=" + ((Hashtable) configuringWorkflowProp.get(key)).get(key2));
                }
            } else {
                vp.add(key + ";=" + configuringWorkflowProp.get(key));
            }
        }
        return vp;
    }   
   
    /**
     * Sets the expiration time of the user data
     */
    public void setTimeOut() {timeOut=System.currentTimeMillis()+Integer.parseInt(PropertyLoader.getInstance().getProperty("cache.time.msec"));}
    
    /**
     * Frees the memory allocated for the user's data
     */
    public void releaseUserData()
    {
        abstracktWorkflows=null;
        workflows=null;
        //System.gc();
    }
    
    /**
     * Frees the memory allocated by the user's data and workflow
     * @param pWorkflowName  workflow name
     */
    public void deleteWorkflow(String pWorkflowName)
    {
        workflows.remove(pWorkflowName);
        //System.gc();
    }

    
    /**
     * Reading the maximum quota for the storage space from the configuration file
     * @param userID user name
     */
    public void readQuotaSpace(String userID)
    {
        try
        {
            String fpath=PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"users/"+userID+"/.quota";
            if(!(new File(fpath)).exists())
                fpath=PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"users/.quota";
            BufferedReader fr=new BufferedReader(new FileReader(fpath));    
            quotaBytes=Long.parseLong(fr.readLine());
        }
        catch(Exception e){ quotaBytes=1;}
    
    }
    
    /**
     * Getting the maximum quota for the storage space
     * @param userID user ID
     * @return the usable storage space in bytes
     */
    public long getQuotaSpace(String userID) {
        // a "readQuotaSpace(userID);"
        // is temporally here
        // (this way the quota size
        // refreshes) until the proper
        // portlet not imported
        // from the supergrid
        readQuotaSpace(userID);
        //
        return quotaBytes*(1024*1024);
    }
    
    /**
     * Getting the used quota storage space
     * @return the used storage space in bytes
     */
    public long getUseQuotaSpace()
    { 
        long tmp=0;
        Enumeration enm=workflows.keys();
        String key="";
        while(enm.hasMoreElements())
        {
            key=(String)enm.nextElement();
            tmp+=((WorkflowData)workflows.get(key)).getSize();
        }
        return tmp;
    }    
    
   /**
    * Loads the user's concrete (real) workflow
    * and sets the expiration time
    *
    * @param userID - user ID
    * @param wfsID - the wfs ID where the workflow is
    * @param realWorkflowID - the real workflow ID
    */
    public void initDataRealWorkflow(String userID, String wfsID, String realWorkflowID) 
    {
        try 
        {
            Hashtable hsh = new Hashtable();
            hsh.put("url", wfsID);
            ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
            PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            // getting the real workflow
            ComDataBean tmp=new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(userID);
            tmp.setWorkflowID(realWorkflowID);
            Vector v = pc.getRealWorkflows(tmp);
            for(int i=0; i<v.size();i++) 
            {
                tmp=(ComDataBean)v.get(i);
                if(workflows.get(tmp.getWorkflowID())==null)
                    workflows.put(tmp.getWorkflowID(), new WorkflowData(tmp.getWorkflowID(),tmp.getWfsID(),tmp.getWfsIDService(),tmp.getStorageURL(),tmp.getTxt(),0,tmp.getGraf(),tmp.getParentWorkflowID(),tmp.getWorkflowtype()));
                if(!tmp.getWorkflowRuntimeID().equals("*")) 
                    getWorkflow(tmp.getWorkflowID()).addRuntimeID(tmp.getWorkflowRuntimeID(),new WorkflowRunTime(tmp.getWfiURL(),"/services/urn:portalwfiservice",tmp.getInstanceTxt(),tmp.getStatus()));
                // setting the appmain flag
                if (workflows.get(tmp.getWorkflowID()) != null) 
                    if ("true".equals(tmp.getAppmain())) 
                        getWorkflow(tmp.getWorkflowID()).setAppMain(true);
            }
        }
        catch(InstantiationException e){e.printStackTrace();}
        catch(ClassNotFoundException e){e.printStackTrace();}
        catch(IllegalAccessException e){e.printStackTrace();}
        setTimeOut();
    }

    /**
     * New abstract workflow
     * (with storageID)
     * @param pName workflow name
     * @param pText text description
     * @param pWFSID storage wfs access
     * @param pStorageID  access of the storage storing the files
     */
    public void addAbstactWorkflows(String pName, String pText, String pWFSID, String pStorageID)
    {
        // get storageID
        if ((pStorageID == null) || ("".equals(pStorageID))) 
        {
            Hashtable hshstorage=new Hashtable();
            ServiceType ststorage = InformationBase.getI().getService("storage","portal",hshstorage, new Vector());
            pStorageID = ststorage.getServiceUrl();
        }
        Hashtable hsh=new Hashtable();
        hsh.put("url", pWFSID);
        ServiceType st=InformationBase.getI().getService("wfs","portal",hsh, new Vector());
        // abstracktWorkflows.put(pName, new WorkflowData(pName,st.getServiceUrl(),st.getServiceID(),pText,0,"")); 
        abstracktWorkflows.put(pName, new WorkflowData(pName, st.getServiceUrl(), st.getServiceID(), pStorageID, pText, 0, "", "",""));
    }

    /**
     * New template workflow
     * (with storageID)
     * @param pName workflow name
     * @param pText text description
     * @param pWFSID storage wfs access
     * @param pStorageID  access of the storage storing the files
     * @param pGraf implemented graph internal ID
     */
    public void addTemplateWorkflows(String pName, String pText, String pWFSID, String pStorageID, String pGraf)
    {
        // get storageID
        if ((pStorageID == null) || ("".equals(pStorageID))) {
            Hashtable hshstorage=new Hashtable();
            ServiceType ststorage = InformationBase.getI().getService("storage","portal",hshstorage, new Vector());
            pStorageID = ststorage.getServiceUrl();
        }
        Hashtable hsh=new Hashtable();
        hsh.put("url", pWFSID);
        ServiceType st=InformationBase.getI().getService("wfs","portal",hsh, new Vector());
        // templateWorkflows.put(pName, new WorkflowData(pName,st.getServiceUrl(),st.getServiceID(),pText,0,pGraf)); 
        templateWorkflows.put(pName, new WorkflowData(pName, st.getServiceUrl(), st.getServiceID(), pStorageID, pText, 0, pGraf, "",""));
    }
    
   /**
    * Loads the user's given 
    * template (abstract) workflow
    * into the portal cache
    * and sets the expiration time
    *
    * (it refreshes only one workflow (workflowID) in the PortalCache
    * which has not been in there yet, the storage calls it after the upload,
    * PortalStorageServiceImpl.newWorkflowNames())
    *
    * @param userID - user ID
    * @param workflowID - workflow ID
    * @param wfsID - the wfs ID where the workflow is
    * @param storageID - the storage ID where the workflow is
    */
   public void addOneTemplateWorkflow(String userID, String workflowID, String wfsID, String storageID) {
        try {
            Hashtable hsh = new Hashtable();
            hsh.put("url", wfsID);
            ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
            PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            // getting the abst workflow
            ComDataBean tmp = new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(userID);
            tmp.setWorkflowID(workflowID);
            Vector v = pc.getTemplateWorkflows(tmp);
            for (int i=0; i<v.size();i++) 
            {
                tmp=(ComDataBean)v.get(i);
                if (templateWorkflows.get(tmp.getWorkflowID()) == null) 
                {
                    templateWorkflows.put(tmp.getWorkflowID(), new WorkflowData(tmp.getWorkflowID(),st.getServiceUrl(),st.getServiceID(),tmp.getTxt(),0,tmp.getGraf()));
                    // set storageID
                    getTemplateWorkflow(tmp.getWorkflowID()).setStorageID(storageID);
                }
            }
        }
        catch(InstantiationException e){e.printStackTrace();}
        catch(ClassNotFoundException e){e.printStackTrace();}
        catch(IllegalAccessException e){e.printStackTrace();}
        setTimeOut();
    }

   /**
    * Loads the user's given 
    * concrete (real) workflow
    * into the portal cache
    * and sets the expiration time
    *
    * (it refreshes only one workflow (workflowID) in the PortalCache
    * which has not been in there yet, the storage calls it after the upload,
    * PortalStorageServiceImpl.newWorkflowNames())
    *
    * @param userID - user ID
    * @param workflowID - workflow ID
    * @param wfsID - the wfs ID where the workflow is
    * @param storageID - the storage ID where the workflow is
    * @param workflowType  - workflow type
    */
    public void addOneRealWorkflow(String userID, String workflowID, String wfsID, String storageID, String workflowType) 
    {
        try 
        {
            Hashtable hsh = new Hashtable();
            hsh.put("url", wfsID);
            ServiceType st = InformationBase.getI().getService("wfs", "portal", hsh, new Vector());
            PortalWfsClient pc = (PortalWfsClient) Class.forName(st.getClientObject()).newInstance();
            pc.setServiceURL(st.getServiceUrl());
            pc.setServiceID(st.getServiceID());
            // getting the abst workflow
            ComDataBean tmp = new ComDataBean();
            tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
            tmp.setUserID(userID);
            tmp.setWorkflowID(workflowID);
            tmp.setWorkflowtype(workflowType);
            Vector v = pc.getRealWorkflows(tmp);
            for (int i=0; i<v.size();i++) 
            {
                tmp=(ComDataBean)v.get(i);
                if (workflows.get(tmp.getWorkflowID()) == null) 
                {
                    workflows.put(tmp.getWorkflowID(), new WorkflowData(tmp.getWorkflowID(),tmp.getWfsID(),tmp.getWfsIDService(),tmp.getStorageURL(),tmp.getTxt(),0,tmp.getGraf(),tmp.getParentWorkflowID(),workflowType));
                    // set storageID
                    getWorkflow(tmp.getWorkflowID()).setStorageID(storageID);
                }
                if(!((ComDataBean)v.get(i)).getWorkflowRuntimeID().equals("*")) 
                    getWorkflow(tmp.getWorkflowID()).addRuntimeID(tmp.getWorkflowRuntimeID(),new WorkflowRunTime(tmp.getWfiURL(),"/services/urn:portalwfiservice",tmp.getInstanceTxt(),tmp.getStatus()));
                // setting appmain flag
                if (workflows.get(tmp.getWorkflowID()) != null) 
                    if ("true".equals(tmp.getAppmain())) 
                        getWorkflow(tmp.getWorkflowID()).setAppMain(true);
            }
            
        }
        catch(InstantiationException e){e.printStackTrace();}
        catch(ClassNotFoundException e){e.printStackTrace();}
        catch(IllegalAccessException e){e.printStackTrace();}
        setTimeOut();
    }

    /**
     * Loads the user's abstract and concrete workflows and sets the expiration times
     * @param userID  user ID
     */
    public void initData(String userID)
    {
       readQuotaSpace(userID);
        try
        {
//getting the wfs URLs
//            Vector allWfs=InformationBase.getI().getAllService("wfs","portal",new Hashtable());
            Vector allWfs=new Vector();
            allWfs.add(InformationBase.getI().getService("wfs","portal",new Hashtable(),new Vector()));
            for(int j=0; j<allWfs.size();j++)
            {
                ServiceType st=(ServiceType)allWfs.get(j);
                PortalWfsClient pc=(PortalWfsClient)Class.forName(st.getClientObject()).newInstance();
                pc.setServiceURL(st.getServiceUrl());
                pc.setServiceID(st.getServiceID());

//abstract workflows
                ComDataBean tmp=new ComDataBean();
                tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                tmp.setUserID(userID);
                Vector v=pc.getAbstractWorkflows(tmp);
                for(int i=0; i<v.size();i++)
                abstracktWorkflows.put(((ComDataBean)v.get(i)).getWorkflowID(), new WorkflowData(((ComDataBean)v.get(i)).getWorkflowID(),st.getServiceUrl(),st.getServiceID(),((ComDataBean)v.get(i)).getTxt(),0,""));
//Template workflows
                tmp=new ComDataBean();
                tmp.setPortalID(PropertyLoader.getInstance().getProperty("service.url"));
                tmp.setUserID(userID);
                Vector vt=pc.getTemplateWorkflows(tmp);
                for(int i=0; i<vt.size();i++)
                templateWorkflows.put(((ComDataBean)vt.get(i)).getWorkflowID(), new WorkflowData(((ComDataBean)vt.get(i)).getWorkflowID(),st.getServiceUrl(),st.getServiceID(),((ComDataBean)vt.get(i)).getTxt(),0,((ComDataBean)vt.get(i)).getGraf()));
//real workflows
                if(v.size()>0)
                {
                    v=pc.getRealWorkflows(tmp);
                    for(int i=0; i<v.size();i++)
                    {
                        tmp=(ComDataBean)v.get(i);
                        if(workflows.get(tmp.getWorkflowID())==null)
                            workflows.put(((ComDataBean)v.get(i)).getWorkflowID(), new WorkflowData(tmp.getWorkflowID(),tmp.getWfsID(),tmp.getWfsIDService(),tmp.getStorageURL(),tmp.getTxt(),0,tmp.getGraf(),tmp.getParentWorkflowID(),tmp.getWorkflowtype()));
                        if(!tmp.getWorkflowRuntimeID().equals("*"))
                            getWorkflow(tmp.getWorkflowID()).addRuntimeID(tmp.getWorkflowRuntimeID(),new WorkflowRunTime(tmp.getWfiURL(),"/services/urn:portalwfiservice",tmp.getInstanceTxt(),tmp.getStatus()));
                        // setting appmain flag
                        // System.out.println("initAppMain - " + ((ComDataBean)v.get(i)).getWorkflowID() + " : " + ((ComDataBean)v.get(i)).getAppmain());
                        if (workflows.get(tmp.getWorkflowID()) != null) 
                            if ("true".equals(tmp.getAppmain())) 
                                getWorkflow(tmp.getWorkflowID()).setAppMain(true);                        
                    }
                }
            }
//getting storage URLs
            Vector allStorage=new Vector();
            allStorage.add(InformationBase.getI().getService("storage","portal",new Hashtable(),new Vector()));
            String p="";
            for(int j=0; j<allStorage.size();j++)
            {
                ServiceType st=(ServiceType)allStorage.get(j);
                PortalStorageClientImpl sc=(PortalStorageClientImpl)Class.forName(st.getClientObject()).newInstance();
                
                p=st.getServiceUrl();
                sc.setServiceURL(p);
                sc.setServiceID(st.getServiceID());
                
                HashMap wsize=sc.getAllWorkflowSize(new StoragePortalUserBean(userID,PropertyLoader.getInstance().getProperty("service.url")));
                Iterator wsize0=wsize.keySet().iterator();
                while(wsize0.hasNext())
                {
                    String key=""+wsize0.next();
                    try
                    {
                        // System.out.println("getWorkflow(key).setStorageID(p); : " + key + " : " + p);
                        // System.out.println("workflows : " + workflows);
                        if (getWorkflow(key) != null) {
                            getWorkflow(key).setStorageID(p);
                        Iterator wsize1=((HashMap)wsize.get(key)).keySet().iterator();
                        long tmpsize=0;
                        while(wsize1.hasNext())
                        {
                            String key0=""+wsize1.next();
                            if((!key0.equals("allothers"))&&(!key0.equals("alloutputs"))&&(!key0.equals("allworkflow"))) {
                                if (getWorkflow(key).getRuntime(key0) != null) {
                                    getWorkflow(key).getRuntime(key0).setSize(((Long)((HashMap)wsize.get(key)).get(key0)).longValue());
                                }
                            }
                        }
                        getWorkflow(key).setSize(((Long)((HashMap)wsize.get(key)).get("allworkflow")).longValue());
//                        System.out.println("portal storage::::"+key+"!="+wsize.get(key));
                        }                        
                    }
                    catch(Exception e){System.out.println("*******"+key);e.printStackTrace();}
                }
            }
//assign storage to the abstract workflows            
            Enumeration enm=getAbstactWorkflows().keys();
            while(enm.hasMoreElements())
            {
                getAbstactWorkflow(""+enm.nextElement()).setStorageID(p);
            }
//assign storage to the template workflows            
            Enumeration templenu=getTemplateWorkflows().keys();
            while(templenu.hasMoreElements())
            {
                getTemplateWorkflow(""+templenu.nextElement()).setStorageID(p);
            }
        }
        catch(InstantiationException e){e.printStackTrace();}
        catch(ClassNotFoundException e){e.printStackTrace();}
        catch(IllegalAccessException e){e.printStackTrace();}
        setTimeOut();    
    } 
    
    /**
     * Creating a new abstract workflow (graph)
     * @param pName workflow name
     * @param pText workflow longer description
     * @param pWFSID wfs service access
     */
    public void addAbstactWorkflows(String pName, String pText, String pWFSID)
    {
        Hashtable hsh=new Hashtable();
        ServiceType st=InformationBase.getI().getService("wfs","portal",hsh, new Vector());
        abstracktWorkflows.put(pName, new WorkflowData(pName,st.getServiceUrl(),st.getServiceID(),pText,0,"")); 
    }
    
    /**
     * New template workflow
     * @param pName workflow name
     * @param pText workflow description
     * @param pWFSID access of the wfs containing the workflow
     * @param pGraf implemented graph ID (name)
     */
    public void addTemplateWorkflows(String pName, String pText, String pWFSID, String pGraf)
    {
        Hashtable hsh=new Hashtable();
        ServiceType st=InformationBase.getI().getService("wfs","portal",hsh, new Vector());
        templateWorkflows.put(pName, new WorkflowData(pName,st.getServiceUrl(),st.getServiceID(),pText,0,pGraf)); 
    }
    
    /**
     * Returns the user's abstract workflows
     * @return  the vector containing the user's abstract workflows
     */
    public ConcurrentHashMap<String,WorkflowData> getAbstactWorkflows(){return abstracktWorkflows;}

    /**
     * Returns the user's template workflows
     * @return  the vector containing the user's template workflows
     */
    public ConcurrentHashMap<String,WorkflowData> getTemplateWorkflows(){return templateWorkflows;}

    /**
     * Returns the list of the user's templated workflows
     * @return  the vector containing the user's templated workflows
     */
    public ConcurrentHashMap getTemplateedConcrateWorkflows()
    {
        ConcurrentHashMap res=new ConcurrentHashMap();
        Enumeration enm=workflows.keys();
        Object key;
        while(enm.hasMoreElements())
        {
            key=enm.nextElement();
            if(!((WorkflowData)workflows.get(key)).getTemplate().equals("--"))
            if(!((WorkflowData)workflows.get(key)).getTemplate().equals(""))
                res.put(key,workflows.get(key));
        }
        return res;
    }
    
    /**
     * Returns the user's workflows
     * @return  the vector containing the user's workflows
     */
    public ConcurrentHashMap<String,WorkflowData> getWorkflows(){return workflows;}
    
    /**
     * Returns one of the user's workflows
     * @param pWorkflowID workflow name
     * @return  workflow descriptor
     */
    public WorkflowData getWorkflow(String pWorkflowID){return (WorkflowData)workflows.get(pWorkflowID);}
        
    /**
     *  Returns if the stored data is still valid
     * @return  true = the user's operation was within the cache expiration time
     */
    public boolean isActive(){ return timeOut>System.currentTimeMillis(); }

    /**
     *  Returns if the user has an abstract workflow with the given name
     * @param pAbstracktWorkflowID 
     * @return true = the workflow name is already taken
     */
    public boolean isAbstactWorkflow(String pAbstracktWorkflowID){ return abstracktWorkflows.get(pAbstracktWorkflowID)!=null; }
    
    /**
     *  Returns if the user has an concrete workflow with the given name
     * @param pWorkflowID 
     * @return true = the workflow name is already taken
     */
    public boolean isWorkflow(String pWorkflowID){ return workflows.get(pWorkflowID)!=null; }

    /**
     *  Returns if the user has an template workflow with the given name
     * @param pWorkflowID 
     * @return true = the workflow name is already taken
     */
    public boolean isTemplateWorkflow(String pWorkflowID){ return templateWorkflows.get(pWorkflowID)!=null; }

    /**
     * Adding new workflow (WFS service call)
     * @param pNewWkf  communication object instance describing the workflow
     * @param pWfiID WFS service URL
     * @param pWfiIDs  WFS service resource
     */
    public void newWorkflow(ComDataBean pNewWkf,String pWfiID,String pWfiIDs)
    {
//        System.out.println("**********"+pWfiID+pWfiIDs);
        try
        {
            PortalWfsClient pc=(PortalWfsClient)Class.forName(InformationBase.getI().getService("wfs","portal", new Hashtable(),new Vector()).getClientObject()).newInstance();
            pc.setServiceURL(pWfiID);
            pc.setServiceID(pWfiIDs);
            if(pc.saveNewWorkflow(pNewWkf))
            {
                if(pNewWkf.getTyp().intValue()==0)
                    workflows.put(pNewWkf.getWorkflowID(), new WorkflowData(pNewWkf.getWorkflowID(), pWfiID,pWfiIDs,InformationBase.getI().getService("storage","portal", new Hashtable(),new Vector()).getServiceUrl(),pNewWkf.getTxt(), 0,pNewWkf.getGraf(),"",pNewWkf.getWorkflowtype()));
                if(pNewWkf.getTyp().intValue()==1)
                    workflows.put(pNewWkf.getWorkflowID(), new WorkflowData(pNewWkf.getWorkflowID(), pWfiID,pWfiIDs,InformationBase.getI().getService("storage","portal", new Hashtable(),new Vector()).getServiceUrl(),pNewWkf.getTxt(), 0,pNewWkf.getGraf(),pNewWkf.getParentWorkflowID(),pNewWkf.getWorkflowtype()));
                if(pNewWkf.getTyp().intValue()==4)
                {
                    WorkflowData ttmp=PortalCacheService.getInstance().getUser(pNewWkf.getUserID()).getWorkflow(pNewWkf.getParentWorkflowID());
                    workflows.put(pNewWkf.getWorkflowID(), new WorkflowData(pNewWkf.getWorkflowID(), pWfiID,pWfiIDs,InformationBase.getI().getService("storage","portal", new Hashtable(),new Vector()).getServiceUrl(),pNewWkf.getTxt(), 0,ttmp.getGraf(),ttmp.getTemplate(),pNewWkf.getWorkflowtype()));
                }
            }
            
        }
        catch(InstantiationException e){}
        catch(ClassNotFoundException e){}
        catch(IllegalAccessException e){}
        
    }
    
    /**
     * Returns the user's given abstract workflow
     * @param pWorkflowID  workflow name
     * @return  the user's abstract workflow
     */
    public WorkflowData getAbstactWorkflow(String pWorkflowID){return (WorkflowData)abstracktWorkflows.get(pWorkflowID);}
    
    /**
     * Returns the user's given template workflow
     * @param pWorkflowID workflow name
     * @return  the user's template workflow
     */
    public WorkflowData getTemplateWorkflow(String pWorkflowID){return (WorkflowData)templateWorkflows.get(pWorkflowID);}

    /**
     * Sets the job chosen for editing from the workflow being configured
     * @param pID job name
     */
    public void setEditingJobData(String pID)
    {
        aJobID=pID;
        for(int i=0;i<configuringWorkflow.size();i++)
        {
            if(((JobPropertyBean)configuringWorkflow.get(i)).getName().equals(pID))
            {
                aJobData=(JobPropertyBean)configuringWorkflow.get(i);
                return;
            }
        }
    }
    
    /**
     * Getting the currently configured job's data
     * @return job descriptor bean
     */
    public JobPropertyBean getEditingJobData(){return aJobData;}
    
    /**
     * Getting the currently configured job's name
     * @return job name
     */
    public String getEditingJobID(){return aJobID;}
    
    /**
     * Returns the list of the timing workflows.
     * This list contains the workflows
     * which has a future date set
     * when the portal will automatically
     * submit the workflow without
     * any user interaction.
     * @return the list of timed workflows
     * @deprecated
     */
    public Vector getTimingWorkflows()
    {
        Vector res=new Vector();
        Enumeration enm=workflows.keys();
        String key="";
        while(enm.hasMoreElements())
        {
            key=""+enm.nextElement();
            if(((WorkflowData)workflows.get(key)).getTiming()!=null)res.add(workflows.get(key));
        }
        return res;
    }
    
    /**
     * Returns if from the given workflows are
     * any concrete or templated workflow created
     * @param grafName 
     * @return true = there is a workflow or a template belongs to the graph (cannot be deleted)
     */
    public Boolean isWorkflowWithThisGraf(String grafName) {
        Boolean ret = new Boolean(false);
        if (!ret.booleanValue()) {
            // concrete workflow list parsing
            Enumeration wfenum = workflows.elements();
            while (wfenum.hasMoreElements()) {
                if (((WorkflowData) wfenum.nextElement()).getGraf().equals(grafName)) {
                    ret = new Boolean(true);
                }
            }
        }
        if (!ret.booleanValue()) {
            // template workflow list parsing
            Enumeration wfenum = templateWorkflows.elements();
            while (wfenum.hasMoreElements()) {
                if (((WorkflowData) wfenum.nextElement()).getGraf().equals(grafName)) {
                    ret = new Boolean(true);
                }
            }
        }
        return ret;
    }
    
    /**
     * Sets the EasyWF parameters
     * 
     * @param pValue
     */
    public void setConfiguringEParams(Vector pValue) {
        configuringEParams = pValue;
    }
    
    /**
     * Returns the EasyWF parameters
     * 
     * @return End user parameters
     */
    public Vector getConfiguringEParams() {
        return configuringEParams;
    }    
    
    
}

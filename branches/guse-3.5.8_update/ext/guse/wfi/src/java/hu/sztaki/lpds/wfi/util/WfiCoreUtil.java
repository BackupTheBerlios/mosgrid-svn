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
 * Minden WFI implementacio alltal hasznalhato funkciok
 */

package hu.sztaki.lpds.wfi.util;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.wfi.com.WorkflowRuntimeBean;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.inf.WfiWfsClient;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author krisztian karoczkai
 */
public class WfiCoreUtil 
{
/**
 * ne lehessen peldanyositani
 */    
    private WfiCoreUtil(){}
    
/**
 * WFI leiro elkerese wfs-tol
 * @param workflowData
 * @param workflowRID
 * @return
 */    
    public static String readRemoteXML(WorkflowRuntimeBean workflowData, String workflowRID) throws ClassNotFoundException,InstantiationException,IllegalAccessException,Exception
    {
        Hashtable sUrl=new Hashtable();
        sUrl.put("url",workflowData.getWfsID());
        //
        ServiceType st=InformationBase.getI().getService("wfs","wfi",sUrl,new Vector());
        WfiWfsClient pc=(WfiWfsClient)Class.forName(st.getClientObject()).newInstance();
        pc.setServiceURL(workflowData.getWfsID());
        pc.setServiceID(st.getServiceID());
        ComDataBean tmp=new ComDataBean();
        tmp.setPortalID(workflowData.getPortalID());
        tmp.setUserID(workflowData.getUserID());
        tmp.setWorkflowID(workflowData.getWorkflowID());
        tmp.setWorkflowRuntimeID(workflowRID);
        tmp.setStorageURL(workflowData.getStorageID());
        tmp.setTxt(workflowData.getInstanceText());
        tmp.setWorkflowtype(workflowData.getWorkflowType());
        //
        if(!workflowData.getRuntimeID().equals(""))
            tmp.setWorkflowRuntimeID(workflowData.getRuntimeID());
        tmp.setWfiURL(PropertyLoader.getInstance().getProperty("service.url"));
        return pc.getWfiXML(tmp);
    }

/**
 * WFI leiro elkerese wfs-tol
 * @param workflowData
 * @param workflowRID
 * @param String job neve
 * @param String index honnan kezdje a listazast
 * @return
 */
    public static String readRemoteRescueXML(WorkflowRuntimeBean workflowData, String workflowRID, String jobName, String index) throws ClassNotFoundException,InstantiationException,IllegalAccessException,Exception
    {
        Hashtable sUrl=new Hashtable();
        sUrl.put("url",workflowData.getWfsID());
        //
        ServiceType st=InformationBase.getI().getService("wfs","wfi",sUrl,new Vector());
        WfiWfsClient pc=(WfiWfsClient)Class.forName(st.getClientObject()).newInstance();
        pc.setServiceURL(workflowData.getWfsID());
        pc.setServiceID(st.getServiceID());
        ComDataBean tmp=new ComDataBean();
        tmp.setPortalID(workflowData.getPortalID());
        tmp.setUserID(workflowData.getUserID());
        tmp.setWorkflowID(workflowData.getWorkflowID());
        tmp.setWorkflowRuntimeID(workflowRID);
        tmp.setJobID(jobName);
        tmp.setStorageURL(workflowData.getStorageID());
        tmp.setTxt(workflowData.getInstanceText());
        tmp.setWorkflowtype(workflowData.getWorkflowType());
        //
        if(!workflowData.getRuntimeID().equals(""))
            tmp.setWorkflowRuntimeID(workflowData.getRuntimeID());
        tmp.setWfiURL(PropertyLoader.getInstance().getProperty("service.url"));
        return pc.getWfiRescueXML(tmp, index);
    }

}
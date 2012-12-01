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
 * Statusz informaciok tovabbkuldese a ws-pgrade-nak es WFS-nek
 */

package hu.sztaki.lpds.wfi.service.zen;

import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.logging.Logger;
import hu.sztaki.lpds.portal.inf.WfsPortalClient;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.wfs.inf.WfiWfsClient;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author Krisztian Karoczkai
 */
public class StatusForwardThread extends Thread{
    private static final int WAITING=1000;
    ConcurrentHashMap<String,CopyOnWriteArrayList<JobStatusBean>> portalStatusQueue=new ConcurrentHashMap<String,CopyOnWriteArrayList<JobStatusBean>>();
    ConcurrentHashMap<String,CopyOnWriteArrayList<JobStatusBean>> wfsStatusQueue=new ConcurrentHashMap<String,CopyOnWriteArrayList<JobStatusBean>>();


    public void add(JobStatusBean pData){
        if(portalStatusQueue.get(pData.getPortalID())==null)
            portalStatusQueue.put(pData.getPortalID(),new CopyOnWriteArrayList<JobStatusBean>());
        portalStatusQueue.get(pData.getPortalID()).add(pData);

        String wfsURL=Base.getZenRunner(pData.getWrtID()).getWorkflowData().getWfsID();
        if(wfsStatusQueue.get(wfsURL)==null)
            wfsStatusQueue.put(wfsURL, new CopyOnWriteArrayList<JobStatusBean>());
        wfsStatusQueue.get(wfsURL).add(pData);
        Logger.getI().workflow(pData.getWrtID(), Logger.INFO, "add-status jobid=\""+pData.getJobID()+"\"  pid=\""+pData.getPID()+"\"  status=\""+pData.getStatus()+"\"");
    }

    public void add(List<JobStatusBean> pData){for(JobStatusBean t:pData) add(t);}



    @Override
    public void run() {
        Enumeration<String> serviceIDList;
        String serviceID="";
        while(Base.getI().applicationrun){
            serviceIDList=portalStatusQueue.keys();
            while(serviceIDList.hasMoreElements()){
                serviceID=serviceIDList.nextElement();
                try{sendingPortalStatus(serviceID);}
                catch(Exception e){Logger.getI().pool("sending-status","error service=\""+serviceID+"\"",e,0);}
            }
            serviceIDList=wfsStatusQueue.keys();
            while(serviceIDList.hasMoreElements()){
                serviceID=serviceIDList.nextElement();
                try{sendingWFSStatus(serviceID);}
                catch(Exception e){Logger.getI().pool("sending-status","error service=\""+serviceID+"\"",e,0);}
            }
            try{
//                Logger.getI().pool("sending-status","start-waiting service=\""+serviceID+"\"",0);
                sleep(WAITING);
//                Logger.getI().pool("sending-status","stop-waiting service=\""+serviceID+"\"",0);
            }
            catch(InterruptedException e){e.printStackTrace();}
        }
    }


/**
 * Status kuldes portalnak
 * @param pServiceID szerviceID
 */
    private synchronized void sendingPortalStatus(String pServiceID) throws Exception{
        Hashtable params=new Hashtable();
        params.put("url", pServiceID);
        if(portalStatusQueue.get(pServiceID).size()==0) return;
        Vector<JobStatusBean> sendingStatus=new Vector(portalStatusQueue.get(pServiceID));
        Logger.getI().pool("sending-status","ws-pgrade service=\""+pServiceID+"\"",sendingStatus.size());
        for(JobStatusBean t:sendingStatus)
            Logger.getI().pool("sending-status","status-info rid=\""+t.getWrtID()+"\" workflow-status=\""+t.getWorkflowStatus()+"\" jobid=\""+t.getJobID()+"\" pid=\""+t.getPID()+"\" job-status=\""+t.getStatus()+"\" ",1);
        WfsPortalClient client=(WfsPortalClient)InformationBase.getI().getServiceClient("portal", "wfi", params);
        client.setCollectionStatus(sendingStatus);
        for(JobStatusBean t0:(Vector<JobStatusBean>)sendingStatus){
            Logger.getI().pool("incoming-status", "send-portal workflow=\""+t0.getWrtID()+"\" job=\"" + t0.getJobID() + "\" pid=\"" + t0.getPID() + "\" status=\"" + t0.getStatus() + "\" resource=\"" + t0.getResource() + "\"", sendingStatus.size());
            portalStatusQueue.get(pServiceID).remove(0);
        }
    }

/**
 * Status kuldes portalnak
 * @param pServiceID Service ID
 */
    private synchronized void sendingWFSStatus(String pServiceID) throws Exception{
        Hashtable params=new Hashtable();
        params.put("url", pServiceID);
        if(wfsStatusQueue.get(pServiceID).size()==0) return;
        Vector sendingStatus=new Vector(wfsStatusQueue.get(pServiceID));
        Logger.getI().pool("sending-status","wfs service=\""+pServiceID+"\"",sendingStatus.size());
        WfiWfsClient client=(WfiWfsClient)InformationBase.getI().getServiceClient("wfs", "wfi", params);
        client.setCollectionStatus(sendingStatus);
        for(JobStatusBean t0:(Vector<JobStatusBean>)sendingStatus){
            Logger.getI().pool("incoming-status", "send-portal workflow=\""+t0.getWrtID()+"\" job=\"" + t0.getJobID() + "\" pid=\"" + t0.getPID() + "\" status=\"" + t0.getStatus() + "\" resource=\"" + t0.getResource() + "\"", sendingStatus.size());
            wfsStatusQueue.get(pServiceID).remove(0);
        }
    }



}

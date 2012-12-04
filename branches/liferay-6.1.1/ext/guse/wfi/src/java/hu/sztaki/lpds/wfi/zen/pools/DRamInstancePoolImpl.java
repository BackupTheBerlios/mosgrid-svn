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

package hu.sztaki.lpds.wfi.zen.pools;

import hu.sztaki.lpds.logging.Logger;
import hu.sztaki.lpds.wfi.service.zen.Base;
import hu.sztaki.lpds.wfs.utils.Status;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstance;
import hu.sztaki.lpds.wfi.zen.pools.inf.InstancePool;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author krisztian karoczkai
 */
public class DRamInstancePoolImpl implements InstancePool
{
    @Override
    public void createJobInstance(String pZenID, String pJobID, long pPID) 
    {
        addInstanceJobPool(pZenID, pJobID, pPID, WorkHandler.getI().getAlgorithm().createNewPSInstance(pZenID, pJobID,pPID,false));

    }



    @Override
    public void addInstanceJobPool(String pZenID, String pJobID, long pPID, PSInstance pInstance)
    {
        try{datas.get(pZenID).get(pJobID).put(""+pPID,pInstance);}
        catch(Exception e){
            if(datas.get(pZenID)==null) createWorkflow(pZenID);
            if(datas.get(pZenID).get(pJobID)==null) createJob(pZenID, pJobID);
            datas.get(pZenID).get(pJobID).put(""+pPID,pInstance);
        }
    }

    @Override
    public long getInstanceCount(String pZenID, String pJobName)
    {
        return datas.get(pZenID).get(pJobName).size();
    }
    
    @Override
    public long getOutputCounts(String pZenID, String pJobName, String pOutputName )
    {
        long res=0;
        Enumeration<String>enm=datas.get(pZenID).get(pJobName).keys();
        String key;
        while(enm.hasMoreElements())
        {
            key=enm.nextElement();
            try{res+=datas.get(pZenID).get(pJobName).get(key).getOutputCount(pOutputName);}
            catch(NullPointerException e)
            {
            }
        }
        return res;
    }
    
    @Override
    public Hashtable<Long,Long> getPSInstanceInPreJobInstance(String pZenID, String pJobName, String pOutputName, long pIndex)
    {
        Hashtable<Long,Long> res=new Hashtable<Long,Long>();
        PSInstance tmp;
        long c=pIndex;
        if(Base.getZenRunner(pZenID).getJob(pJobName).getOutput(pOutputName).getMainCount()>1)
        {

            for(long i=0;i<Base.getZenRunner(pZenID).getJob(pJobName).getCount();i++)
            {
                 tmp=WorkHandler.getI().getInstancePool().getPSInstance(pZenID, pJobName, i);
                 if(tmp!=null)
                 {
                     if(Status.isFinished(tmp.getStatus()))
                     {
                        if(c<tmp.getOutputCount(pOutputName))
                        {
                            res.put(i, c);
                            return res;
                        }
                        else c-=tmp.getOutputCount(pOutputName);
                     }
                 }
            }
        }
        else
        {
            res.put(pIndex, new Long(-1));
        }
        return res;
    }

    @Override
    public long getFirstSreamIndexForPID(String pZenID, String pJobName, String pOutputName, long pPid)
    {
        long res=0;
        if(Base.getZenRunner(pZenID).getJob(pJobName).outputIsGenerator(pOutputName))
        {
            for(long i=0;i<pPid;i++)
                res+=datas.get(pZenID).get(pJobName).get(""+i).getOutputCount(pOutputName);
        }
        else res=pPid;
        return res;
    }
    @Override    
    public long getStreamIndex(String pZenID, String pJobName, long pPID, String pOutputName, long pIndex)
    {
        long res=0;
//generator esetben meghatarozzuk a stream indexet        
        if(Base.getZenRunner(pZenID).getJob(pJobName).getOutput(pOutputName).getMainCount()>1)
        {
            PSInstance tmp;
            for(long i=0;i<pPID;i++)
            {
                tmp=datas.get(pZenID).get(pJobName).get(""+i);
                if(tmp!=null)
                {
                    try{res+=tmp.getOutputCount(pOutputName);}
                    catch(Exception e)
                    {
                    }
                }
            }
            res=res+pIndex;
        }
//nem generator esetben a stream index a PID        
        else res=pPID;
        return res;
    }


    protected Hashtable<String,Hashtable<String,Hashtable<String,PSInstance>>> datas=new Hashtable<String,Hashtable<String,Hashtable<String,PSInstance>>>();

    @Override
    public void createWorkflow(String pZenID)
    {
        if(datas.get(pZenID)==null)
            datas.put(pZenID, new Hashtable<String,Hashtable<String,PSInstance>>());
    }

    @Override
    public void createJob(String pZenID, String pJobID)
    {
        createWorkflow(pZenID);
        if(datas.get(pZenID).get(pJobID)==null)
            datas.get(pZenID).put(pJobID, new Hashtable<String,PSInstance>());
    }


    @Override
    public void finishWorkflow(String pZeniD)  {
        Logger.getI().pool("jobinstance","add-finishWF workflow=\""+pZeniD+"\"",datas.get(pZeniD).size());
        datas.remove(pZeniD);
    }

    @Override
    public Hashtable<String,PSInstance> getAllJobInstances(String pZenID, String pJobName)  {return datas.get(pZenID).get(pJobName);}

    @Override
    public RunableInstanceBean runnableJob(String pZenID, String pJobID, long pPID) throws NullPointerException
    {
        if(datas.get(pZenID).get(pJobID).get(""+pPID)!=null)
        {
            RunableInstanceBean res=new RunableInstanceBean();
            res.setJobName(pJobID);
            res.setPid(pPID);
            res.setWf(pZenID);
            return res;
        }
        throw new NullPointerException();
    }

    @Override
    public PSInstance getPSInstance(String pZenID, String pJobName, long pPID)
    {
        return datas.get(pZenID).get(pJobName).get(""+pPID);
    }

    @Override
    public int getManagedInstance(String pZenId) {
        int res = 0;
        Hashtable<String, Hashtable<String, PSInstance>> tmp = datas.get(pZenId);
        Enumeration<String> enm = tmp.keys();
        while (enm.hasMoreElements()) {
            res += tmp.get(enm.nextElement()).size();
        }
        return res;
    }





}

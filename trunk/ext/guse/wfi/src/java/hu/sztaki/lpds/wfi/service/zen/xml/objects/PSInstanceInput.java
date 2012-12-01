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
 * PSInstance.java
 *
 * Egy parametrikus job peldany leiroja
 */

package hu.sztaki.lpds.wfi.service.zen.xml.objects;

import hu.sztaki.lpds.information.com.ServiceType;
import java.util.Enumeration;
import java.util.Hashtable;

public class PSInstanceInput implements PSInstance
{
    private Hashtable<String,PSInputBean> inputs=new Hashtable<String,PSInputBean>();
    private int status=1;
//    private long psID=0;
//    private String name="";

public void addOutputCount(String pPort, long pCount){}
public long getOutputCount(String pPort){return 0;}
    
/**
 * Class constructor
 * @param pJobName job neve
 * @pPID pid
 */    
    public PSInstanceInput(String pJobName, long pPID) {}
    
/**
 * Input hozzaadasa a hivasi historyhoz
 * @param pName job neve
 * @pInstance pid
 */    
    public void addInput(String pName, String pInstance)
    {
        if(pName==null)
            inputs.put(pName,new PSInputBean(Long.parseLong(pInstance)));
        else if(inputs.get(pName)==null)
            inputs.put(pName,new PSInputBean(Long.parseLong(pInstance)));
    }

    public void addInput(String pName, String pInstance, long pIndex,boolean pstate)
    {
        if(pName==null)
            inputs.put(pName,new PSInputBean(Long.parseLong(pInstance),pIndex));
        else if(inputs.get(pName)==null)
            inputs.put(pName,new PSInputBean(Long.parseLong(pInstance),pIndex));
    }

/**
 * Input hozzaadasa a hivasi historyhoz csatorna input eseteben
 * @param pName job neve
 * @pInstance pid
 * @param pHistory preJob hivaso historyja
 */    
    public void addInput(String pName, String pInstance, String pHistory)
    {
        if(pName==null)
            inputs.put(pName,new PSInputBean(Long.parseLong(pInstance)));
        else if(inputs.get(pName)==null)
            inputs.put(pName,new PSInputBean(Long.parseLong(pInstance)));
    }


/**
 * Aktualisan futu jobhoz tartozo submiter service adatok lekerdezese
 * @return pValue Service leiro
 */    
    public ServiceType getActualServiceType(){return new ServiceType();}

/**
 * Status lekerdezese
 * @return status
 */    
    public int getStatus(){return status;}
/**
 * Status beallitasa
 * @param pValue status kod
 */    
    public void setStatus(int pValue){status=pValue;}
    
    public void deleteInputs(){inputs=null;}
    
/**
 * Osszes input lekerdezese
 * @return input hash(String)
 */    
    public Hashtable<String,PSInputBean> getAllInput()
    {
        return inputs;
    }

    public Hashtable getAllInputStatus()
    {
        Hashtable<String,String> res=new Hashtable<String,String>();
        Enumeration<String> enm=inputs.keys();
        String key;
        while(enm.hasMoreElements())
        {
            key=enm.nextElement();
            res.put(key, ""+inputs.get(key).getPid()+"/"+inputs.get(key).getIndex());
        }
        return res;
    }

    public Hashtable<String, Long> getAllOutputs() {return new Hashtable<String, Long>();}
    
}

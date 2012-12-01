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
import hu.sztaki.lpds.wfs.utils.Status;
import java.util.Enumeration;
import java.util.Hashtable;

public class PSInstanceOutput  implements PSInstance
{
    private Hashtable<String,Long> outputs=null;
    private int status=1;
//    private long psID=0;
//    private String name="";

    @Override
public void addOutputCount(String pPort, long pCount)
{outputs.put(pPort, new Long(pCount));}
    @Override
public long getOutputCount(String pPort){return outputs.get(pPort).longValue();}
    
/**
 * Class constructor
 * @param pJobName job neve
 * @pPID pid
 */    
    public PSInstanceOutput(String pJobName, long pPID) {}
    
/**
 * Input hozzaadasa a hivasi historyhoz
 * @param pName job neve
 * @pInstance pid
 */    
    public void addInput(String pName, String pInstance){}

    public void addInput(String pName, String pInstance, long pIndex,boolean pstate){}

/**
 * Input hozzaadasa a hivasi historyhoz csatorna input eseteben
 * @param pName job neve
 * @pInstance pid
 * @param pHistory preJob hivaso historyja
 */    
    public void addInput(String pName, String pInstance, String pHistory){}


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
    public void setStatus(int pValue)
    { 
        if(Status.isFinished(pValue)) outputs=new Hashtable<String,Long>();
        status=pValue;
    }
    
    public void deleteInputs(){}
    
/**
 * Osszes input lekerdezese
 * @return input hash(String)
 */    
    public Hashtable<String,PSInputBean> getAllInput()
    {
        return new Hashtable<String,PSInputBean>();
    }

    public Hashtable getAllInputStatus()
    {
        Hashtable<String,String> res=new Hashtable<String,String>();
        return res;
    }

    public Hashtable<String, Long> getAllOutputs() {return outputs;}
    
}

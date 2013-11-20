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
import java.util.Hashtable;

public interface PSInstance 
{
/**
 * Uj output es generalt darabszam felvitele az instance-hoz
 * @param pPort port neve
 * @param pCount outputok szama
 */
    public void addOutputCount(String pPort, long pCount);
/**
 * Output porton leve fileok szamanak lekerdezese
 * @param pPort port neve
 * @return instance alltal letrehozott oututok szama 
 */    
    public long getOutputCount(String pPort);

    public Hashtable<String,Long> getAllOutputs();
/**
 * Input hozzaadasa az Instance-hoz
 * @param pName port neve
 * @pInstance pid 
 */    
    public void addInput(String pName, String pInstance);
/**
 * Input hozzaadasa az Instance-hoz
 * @param pName port neve
 * @pInstance pid 
 * @param pIndex file index
 * @param pstate true=csatorna output mar rendelkezesere all
 */
    public void addInput(String pName, String pInstance, long pIndex,boolean pstate);

/**
 * Input hozzaadasa az Instance-hoz
 * @param pName port neve
 * @pInstance pid
 * @param pHistory preJob hivaso historyja
 * @deprecated 
 */    
    public void addInput(String pName, String pInstance, String pHistory);

/**
 * Aktualisan futu jobhoz tartozo submiter service adatok lekerdezese
 * @return pValue Service leiro
 */    
    public ServiceType getActualServiceType();

/**
 * Status lekerdezese
 * @return status
 */    
    public int getStatus();
/**
 * Status beallitasa
 * @param pValue status kod
 */    
    public void setStatus(int pValue);
/**
 *  input lista felszabaditasa
 */  
    public void deleteInputs();
    
/**
 * Osszes input lekerdezese
 * @return inputok
 */    
    public Hashtable<String,PSInputBean> getAllInput();

/**
 * Osszes input lekerdezese
 * @return input hash(String)
 */    
    public Hashtable getAllInputStatus();
    
}

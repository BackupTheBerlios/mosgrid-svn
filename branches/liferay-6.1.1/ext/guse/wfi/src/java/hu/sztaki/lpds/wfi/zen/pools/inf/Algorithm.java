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

package hu.sztaki.lpds.wfi.zen.pools.inf;

import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstance;


/**
 *
 * @author krisztian karoczkai
 */
public interface Algorithm 
{
/**
 * meglevo Job peldany futtatasa
 * @param pEndJob befejezodott Job peldany neve
 * @param pPID  befejezodott Job peldany indexe
 * @param pOutput Befejezodott job outputja
 * @param pIndex output index(generator eseten van jelenosege)
 * @param pInput Input neve
 * @param zenID ZenID
 * @param aJob futtatando job
 */
        public void  action(String pEndJob,long pPID,String pOutput, long pIndex,String pInput,String zenID,String aJob);

/**
 * Egy uj parametrikus peldany letrehozasa
 * @param pZenID workflow peldany azonosito
 * @param pJobID job azonosito
 * @param pPid job PID
 * @param pGenInput input generalasi strategia(true=letrehozza, false nem hozza letre az inputokat)
 * @return egy uj PS Job peldany
 * @throws java.lang.NullPointerException Ha a peldany input felteteli nem teljesultek
 */       
        public PSInstance createNewPSInstance(String pZenID, String pJobID, long pPid, boolean pGenInput) throws NullPointerException;
/**
 * Jobpeldany elozmenyeinek meghazarozasa
 * @param pZenID workflowID
 * @param pJob JobID
 * @param pPID PID
 * @return elozmenyjobok listaja
 * @throws java.lang.NullPointerException Ha a peldany input felteteli nem teljesultek meg
 */    
//        public Vector<CleaningBean> getPreJobsInstances(String pZenID, String pJob, long pPID) throws NullPointerException;
        
}

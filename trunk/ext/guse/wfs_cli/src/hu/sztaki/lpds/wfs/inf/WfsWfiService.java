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
 * Wfs alltal a Wfi nek nyujtando szolgaltatasok deffinicioja
 */

package hu.sztaki.lpds.wfs.inf;

import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.wfs.com.ResourceCollectionBean;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author krisztian
 */
public interface WfsWfiService 
{

/**
 * Visszaadja a workflow leirojat
 * @param pData a workflow adatai
 * @return A leiro XML
 * @throws Exception adatbazishiba
 */    
public String getWfiXML(ComDataBean pData) throws Exception;

/**
 * Visszaadja a workflow rescue adat leirojat
 * @param pData a workflow adatai
 * @param index honnan kezdje a listazast
 * @return A leiro XML
 * @throws Exception adatbazishiba
 */
public String getWfiRescueXML(ComDataBean pData, String index) throws Exception;
  
/**
 * Beallitja job statuszat
 * @param pData a Job adatai
 * @throws Exception adatbazishiba
 */    
   public void setStatus(JobStatusBean pData) throws Exception;
  
/**
 * Beallitja job statuszat
 * @param pData a Job adatai
 * @return ures String
 * @throws Exception adatbazishiba
 */    
   public String setCollectionStatus(Vector pData) throws Exception;
   
/**
 * Visszaadja hogy milyen tipusu eroforrason kell elinditani a Job-ot
 * @param pData a Job adatai
 * @return jobhoz konfiguralt futtato eroforras
 */    
   public String getResourceType(JobStatusBean pData);
   
/**
 * Visszaadja hogy milyen tipusu eroforrason kell elinditani az atadott job collection elemeit
 * @param pData a Job adatai
 * @return jobokhoz konfiguralt futtato eroforrasok
 */    
   public HashMap getCollectionResourceType(ResourceCollectionBean pData);

    /**
     * Job leiro lekérdezese
     * @param pValue Job deffinicio
     * @return leiro xml
     */
    public String getSubmitData(ComDataBean pValue);
}

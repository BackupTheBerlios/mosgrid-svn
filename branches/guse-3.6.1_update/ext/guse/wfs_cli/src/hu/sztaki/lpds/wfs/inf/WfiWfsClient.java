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
 * WfiWfsClient.java
 * WFInek nyujtando servicek leirasa
 */

package hu.sztaki.lpds.wfs.inf;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import hu.sztaki.lpds.wfs.com.ResourceCollectionBean;
import java.util.HashMap;
import java.util.Vector;

/**
 * @author krisztian
 */

public interface WfiWfsClient extends BaseCommunicationFace
{

/**
 * Visszaadja a workflow leirojat
 * @param pData a workflow adatai
 * @return A leiro XML
 * @throws Exception adatbazis hiba
 */    
   public String getWfiXML(ComDataBean pData) throws Exception;
  
/**
 * Visszaadja a workflow rescue adat leirojat
 * @param pData a workflow adatai
 * @param index honnan kezdje a listazast
 * @return A leiro XML
 * @throws Exception adatbazis hiba
 */
   public String getWfiRescueXML(ComDataBean pData, String index) throws Exception;

/**
 * Beallitja job statuszat
 * @param pData a Job adatai
 * @throws Exception adatbazis hiba
 */    
   public void setStatus(JobStatusBean pData) throws Exception;
  
/**
 * Beallitja job statuszat
 * @param pData a Job adatai
 * @throws Exception adatbazis hiba
 */    
   public void setCollectionStatus(Vector pData) throws Exception;
   
/**
 * Visszaadja hogy milyen tipusu eroforrason kell elinditani a Job-ot
 * @param pData a Job adatai
 * @return A jobhoz tartozo konfiguralt eroforras
 */    
   public String getResourceType(JobStatusBean pData);
   
/**
 * Visszaadja hogy milyen tipusu eroforrason kell elinditani az atadott job collection elemeit
 * @param pData a Job adatai
 * @return jobokhoz tartozo konfiguralt eroforrasok
 */    
   public HashMap getCollectionResourceType(ResourceCollectionBean pData);

   /**
 * Submitacios adatok szolgaltatasa
 * @param pValue Kommunikacios leiro
 * @return jobleiro xml string
 * @throws Exception Adatbazis/kommunikacios hiba
 */
    public String getSubmitData(ComDataBean pValue) throws Exception;

}

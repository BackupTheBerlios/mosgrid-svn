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

package hu.sztaki.lpds.dcibridge.client;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAcceptingNewActivitiesFault;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAuthorizedFault;
import org.ggf.schemas.bes._2006._08.bes_factory.UnsupportedFeatureFault;

/**
 * @author krisztian karoczkai karoczkai
 */
public interface SubmitterFace extends BaseCommunicationFace{
/**
 * Uj aktivitas letrehozasa(Job Submit)
 * @param parameters job leiro
 * @return egyedi Job azonosito es job leiro
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.NotAuthorizedFault authorizacios hiba
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.NotAcceptingNewActivitiesFault keres nem dolgozhato fel
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault hibas keres
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.UnsupportedFeatureFault nem tamogatott features
 * @throws Exception kommunikacios hiba
 */
    public org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityResponseType createActivity(org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityType parameters) throws NotAuthorizedFault, NotAcceptingNewActivitiesFault, InvalidRequestMessageFault, UnsupportedFeatureFault,Exception ;
/**
 * Aktivitas allapotanak lekerdezese
 * @param parameters aktivitas azonosito
 * @return
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault hibas keres
 * @throws Exception kommunikacios hiba
 */
    public org.ggf.schemas.bes._2006._08.bes_factory.GetActivityStatusesResponseType getActivityStatuses(org.ggf.schemas.bes._2006._08.bes_factory.GetActivityStatusesType parameters) throws InvalidRequestMessageFault,Exception ;
/**
 * Aktivitas megszekitasa
 * @param parameters egyedi aktivitas leiro
 * @return
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault hibas keres
 * @throws Exception kommunikacios hiba
 */
    public org.ggf.schemas.bes._2006._08.bes_factory.TerminateActivitiesResponseType terminateActivities(org.ggf.schemas.bes._2006._08.bes_factory.TerminateActivitiesType parameters) throws InvalidRequestMessageFault,Exception ;
/**
 * Aktivitashoz tartozo leiro lekerdezese
 * @param parameters egyedi aktivitas azonosito
 * @return
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault hibas keres
 * @throws Exception kommunikacios hiba
 */
    public org.ggf.schemas.bes._2006._08.bes_factory.GetActivityDocumentsResponseType getActivityDocuments(org.ggf.schemas.bes._2006._08.bes_factory.GetActivityDocumentsType parameters) throws InvalidRequestMessageFault,Exception;
/**
 * DCI-Bridge szerviz allapot dokumentum lekerdezese
 * @param parameters  kert dokumentumok
 * @return dokumetum leiro
 * @throws org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault hibas keres
 * @throws Exception kommunikacios hiba
 */
    public org.ggf.schemas.bes._2006._08.bes_factory.GetFactoryAttributesDocumentResponseType getFactoryAttributesDocument(org.ggf.schemas.bes._2006._08.bes_factory.GetFactoryAttributesDocumentType parameters) throws InvalidRequestMessageFault,Exception;

}

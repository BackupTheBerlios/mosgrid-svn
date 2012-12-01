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
 * SubmitterWfsClient.java
 * Submiternek nyujtott WFS szolgaltatasok elerese
 */

package hu.sztaki.lpds.wfs.inf;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.wfs.com.ComDataBean;

/**
 * @author krisztian
 */
public interface SubmitterWfsClient extends BaseCommunicationFace
{
   
/**
 * Submitacios adatok szolgaltatasa
 * @param pValue Kommunikacios leiro
 * @return jobleiro xml string
 * @throws Exception Adatbazis/kommunikacios hiba
 */
    public String getSubmitData(ComDataBean pValue) throws Exception;
}

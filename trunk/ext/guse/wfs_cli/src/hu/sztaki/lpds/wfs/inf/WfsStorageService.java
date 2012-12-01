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
 * Storage szamara nyujtando WFS szolgaltatasok deffinicioja
 */

package hu.sztaki.lpds.wfs.inf;

import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;

/**
 *
 * @author krisztian
 */
public interface WfsStorageService 
{
    /**
     * Lekerdezi egy workflow adatait (xml-ben).
     *
     * @param value A felhasznaloi adatok
     * @return workflow XML string
     */
    public String getWorkflowXML(StorageWorkflowNamesBean value);
    
    /**
     * Beallitja egy workflow adatait (xml-bol).
     *
     * @param value A felhasznaloi adatok
     * @return ures string ha a workflow ertelmezese es a mentese sikeres
     *         hibauzenet string ha nem sikerult vagy nem volt mit menteni
     */
    public String setWorkflowXML(StorageWorkflowNamesBean value);

}

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
 * WS-pgrade altal igenyelt felhasznaloi informacioszolgaltato felulet
 */

package hu.sztaki.lpds.pgportal.user;

/**
 * @author krisztian karoczkai
 */
public interface UserHandlerFace {
/**
 * Felhasznalo valos nevenek lekerdezese
 * @param pUID felhasznalo azonositoja
 * @return felhasznalo neve
 */
    public String getUserName(String pUID);
/**
 * Felhasznalo neve
 * @param pUID
 * @param pRoleName
 * @return
 */
    public boolean isUserInRole(String pUID, String pRoleName);
}

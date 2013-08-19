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
 * Definition of the resource configuration interface
 */

package hu.sztaki.lpds.information.inf;

import hu.sztaki.lpds.information.data.ResourceBean;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @author krisztian karoczkai
 */

public interface ResourceConfigurationClient extends BaseCommunicationFace
{

    
/**
 * Query of the available middlewares accessible for the user from the given interface
 * @param pPortalID interface ID
 * @param pUserID user ID
 * @return middleware list
 * @throws java.lang.Exception communication error
 */
    public List<String> getAllMidleware(String pPortalID, String pUserID) throws Exception;

/**
 * Query of the available grids/VOs of the chosen middleware accessible for the user from the given interface
 * @param pPortalID interface ID
 * @param pUserID user ID
 * @param pMidleware chosen middleware name
 * @return grid/VO list
 * @throws java.lang.Exception communication error
 */
    public List<String> getAllGrids(String pPortalID, String pUserID,String pMidleware) throws Exception;


/**
 * Query of the settings of the grid/VO chosen by the user
 * @param pPortalID interface ID
 * @param pUserID user ID
 * @param pMidleware architecture name
 * @param pGrid   chosen grid/VO name
 * @return property list
 * @throws java.lang.Exception communication error
 */
    public HashMap getGridProperies(String pPortalID, String pUserID,String pMidleware,String pGrid) throws Exception;

/**
 * Modification of the settings of the grid/VO chosen by the user
 * @param pPortalID interface ID
 * @param pUserID user ID
 * @param pMidleware architecture
 * @param pGrid   chosen grid/VO name
 * @param pProps properties
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String setGridProperies(String pPortalID, String pUserID,String pMidleware,String pGrid,HashMap pProps) throws Exception;

/**
 * Adding a new executor resource
 * @param pPortalID interface ID
 * @param pUserID user ID
 * @param pMidleware architecture
 * @param pGrid   chosen grid/VO name
 * @param pSite site name
 * @param pJobmanager available jobmanager
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String addResource(String pPortalID, String pUserID,String pMidleware,String pGrid,String pSite, String pJobmanager) throws Exception;

/**
 * Query of the list of executor resources belong to a grid/VO
 * @param pPortalID interface ID
 * @param pUserID user ID
 * @param pMidleware architecture
 * @param pGrid   chosen grid/VO name
 * @return list of available resources
 * @throws java.lang.Exception communication error
 */
    public Collection<ResourceBean> getAllResource(String pPortalID, String pUserID,String pMidleware,String pGrid) throws Exception;

/**
 * resource deletion
* @param pPortalID interface ID
 * @param pUserID user ID
 * @param pMidleware architecture
 * @param pGrid   chosen grid/VO name
 * @param pResourceID resource internal ID
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String deleteResource(String pPortalID, String pUserID,String pMidleware,String pGrid,String pResourceID) throws Exception;

/**
 * Query of the full list of configured resources
 * @param pPortalID interface ID
 * @param pUserID user ID
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String getConfiguredResources(String pPortalID, String pUserID)  throws Exception;
}

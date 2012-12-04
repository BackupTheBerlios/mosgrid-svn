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
 * Service admin manager interface definition
 */

package hu.sztaki.lpds.information.inf;

import hu.sztaki.lpds.information.data.GuseServiceBean;
import hu.sztaki.lpds.information.data.GuseServiceCommunicationBean;
import hu.sztaki.lpds.information.data.GuseServiceTypeBean;
import hu.sztaki.lpds.information.data.ServiceResourceBean;
import hu.sztaki.lpds.information.data.ServiceUserBean;
import java.util.ArrayList;

/**
 * @author krisztian karoczkai
 */
public interface ServiceAdminClient extends BaseCommunicationFace
{

/**
 * Query of the available services
 * @return service list
 * @throws java.lang.Exception communication error
 */
    public ArrayList<GuseServiceBean> getAllServices()  throws Exception;
/**
 * Query of the available service type list
 * @return service type list
 * @throws java.lang.Exception communication error
 */
    public ArrayList<GuseServiceTypeBean> getAllServiceTypes()  throws Exception;

/**
 * Query of the communication types between services
 * @return communication type list
 * @throws java.lang.Exception communication error
 */
    public ArrayList<GuseServiceCommunicationBean> getAllServiceComs()  throws Exception;

/**
 * Service deletion
 * @param pID Service ID
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String deleteService(String pID) throws Exception;

 /**
  * Service type deletion
  * @param pID service type ID
  * @return operation result
  * @throws java.lang.Exception communication error
  */
    public String deleteServiceType (String pID) throws Exception;


/**
 * Service deletion
 * @param pID service ID
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String deleteServiceCom(String pID) throws Exception;

/**
 * Data manipulation
 * @param pValue entity needed to be manipulated
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String dataManagement(Object pValue) throws Exception;


/**
 * Service permission setting
 * @param pServiceID service id
 * @param pUserLogin user descriptor
 * @return
 * @throws java.lang.Exception
 */
    public String addServiceUser(String pServiceID, ServiceUserBean pUserLogin) throws Exception;


/**
 * Deleting user from the service role
 * @param pServiceID service ID
 * @param pUserLoginID user login
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String deleteServiceUser(String pServiceID, String pUserLoginID) throws Exception;

/**
 * Adding new communication channel
 * @param pComID channel ID
 * @param pResource resource descriptor
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String addCommChanel(String pComID, ServiceResourceBean pResource) throws Exception;

/**
 * Communication channel deletion
 * @param pComID channel ID
 * @param pResourceID resource ID
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String deleteCommChanel(String pComID, String pResourceID) throws Exception;

/**
 * Service refreshing (reloading properties, service initialization)
 * @param pServiceID resource ID
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String refreshService(String pServiceID) throws Exception;

/**
 * Service refreshing (reloading properties, service initialization)
 * @param pServiceID resource ID
 * @param pPropertyID property ID (0 if new, otherwise the ID of the editable property)
 * @param pKey  property name
 * @param pValue  property value
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String addServiceProperty(String pServiceID, String pPropertyID, String pKey,String pValue) throws Exception;

/**
 * Copy of the service properties between services
 * @param pDSTServiceID target service (the properties of this service will increase)
 * @param pSRCServiceID  source service (this service's properties will be shown in the target service properties)
 * @return operation result
 * @throws java.lang.Exception communication error
 */
    public String importServiceProperties(String pDSTServiceID,String pSRCServiceID) throws Exception;

/**
 * Query of the gUSE service configuration XML
 * @return XML content
 * @throws java.lang.Exception communication error
 */
    public String export() throws Exception;

}

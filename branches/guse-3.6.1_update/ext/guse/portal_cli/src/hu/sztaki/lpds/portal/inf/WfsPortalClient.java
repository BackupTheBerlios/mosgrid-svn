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
 * Definition of the client used for the WFI to access the portal
 */

package hu.sztaki.lpds.portal.inf;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import java.util.Vector;

/**
 * @author krisztian
 */
public interface WfsPortalClient extends BaseCommunicationFace
{    
    /**
     * Sending the job status information
     * @param pData job descriptor
     * @exception Exception communication or service call error
     */
    public void setStatus(JobStatusBean pData) throws Exception;
    
    /**
     * Sending a group of statuses
     * @param pData job descriptor vector
     * @exception Exception communication or service call error
     */
    public void setCollectionStatus(Vector pData) throws Exception;
    
}

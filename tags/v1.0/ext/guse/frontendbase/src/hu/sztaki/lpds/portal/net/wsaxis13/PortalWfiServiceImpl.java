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
 * PortalWfiServiceImpl.java
 */

package hu.sztaki.lpds.portal.net.wsaxis13;

import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.portal.inf.PortalWfiService;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import java.util.Vector;

/**
 * Handles the request from the 
 * wfi on the portal side.
 *
 * @author krisztian
 */

public class PortalWfiServiceImpl implements PortalWfiService
{
/**
 * Constructor
 */
    public PortalWfiServiceImpl(){}
/**
 * @see PortalWfiService#setStatus(hu.sztaki.lpds.wfs.com.JobStatusBean)
 */
    @Override
    public String setStatus(JobStatusBean pData){
        StatusHandlerThread.getI().add(pData);
        return "";
    }
/**
 * @see PortalWfiService#setCollectionStatus(java.util.Vector)
 */
    @Override
    public String setCollectionStatus(Vector pData){   
        StatusHandlerThread.getI().add(pData);
        return "";
    }
    
/**
 * Temporary disable of the service access
 * @param pServiceType service type
 * @param pServiceID service ID
 * @deprecated
 */
    public void unForbidden(String pServiceType, String pServiceID)
    {
        InformationBase.getI().unSetForbiddenService(pServiceType, pServiceID);
    }
    
}

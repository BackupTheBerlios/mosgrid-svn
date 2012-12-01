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
 * HistoryData.java
 */

package hu.sztaki.lpds.pgportal.service.base.data;

import hu.sztaki.lpds.pgportal.service.message.PortalMessageService;

/**
 * Not used
 *
 * @deprected
 * @author krisztian
 */
public class HistoryData 
{
    private String dat;
    private String remote;
    private String user;
    private String actionID;
/**
 * Empty constructor
 */
    public HistoryData() {}
/**
 * Constructor
 * @param pDat entry date
 * @param pRemote place of entry
 * @param pUser ID of the user made the entry
 * @param pActionID entered operation
 */
    public HistoryData(String pDat,String pRemote, String pUser, String pActionID)
    {
        dat=pDat;
        remote=pRemote;
        user=pUser;
        actionID=pActionID;
    }
    /**
     * Getting the entry date
     * @return entry date
     */
    public String getDat(){return dat;}
    /**
     * Getting the place of entry (portalID)
     * @return place of entry
     */
    public String getRemote(){return remote;}
    /**
     * Getting the user who made the entry
     * @return the user made the entry
     */
    public String getUser(){return user;}
    /**
     * Getting the ID of the entry process
     * @return ID of the entry process
     */
    public String getActionID(){return actionID;}
    /**
     * Description belongs to the entry based on the text cache
     * @return entry text description
     */
    public String getActionTxt(){return PortalMessageService.getI().getMessage(actionID);}
}

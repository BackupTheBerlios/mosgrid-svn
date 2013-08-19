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
 * StoragePortalUserBean.java
 *
 * Simple bean that stores userID and portalID 
 */

package hu.sztaki.lpds.storage.com;

/**
 * @author K. Karoczkai
 * @version 3.3
 */ 
public class StoragePortalUserBean {
    
    private String userID;
    
    private String portalID;
    
     /**
     * Default constructor
     */ 
    public StoragePortalUserBean() {
    }
    
    /**
     * Constructor with default arguments
     *
     * @param pUserID   ID of the user
     * @param pPortalID ID of the portal
     */ 
    public StoragePortalUserBean(String pUserID, String pPortalID) {
        portalID=pPortalID;
        userID=pUserID;
    }
    
     /**
     * Gets the ID of the User
     * @return userID
     */ 
    public String getUserID() {
        return userID;
    }
    
    /**
     * Gets the ID of the Portal Service
     * @return portalID
     */ 
    public String getPortalID() {
        return portalID;
    }
    
    /**
     * Sets the ID of the User
     * @param value userID
     */ 
    public void setUserID(String value) {
        userID=value;
    }
    
    /**
     * Sets the ID of the Portal Service
     * @param value portalID
     */ 
    public void setPortalID(String value) {
        portalID=value;
    }
    
}

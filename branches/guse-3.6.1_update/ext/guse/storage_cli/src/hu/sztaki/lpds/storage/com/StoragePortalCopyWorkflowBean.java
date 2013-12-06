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

package hu.sztaki.lpds.storage.com;

import java.util.Hashtable;

/**
 *  @author lpds MTA SZTAKI
 *  @version 3.3
 */ 
public class StoragePortalCopyWorkflowBean {

    private String portalID;
    
    private String userID;
    
    private String sourceWorkflowID;
    
    private String destinWorkflowID;    
   
    private Hashtable copyHash;
    
    /**
     * Default constructor
     */ 
    public StoragePortalCopyWorkflowBean() {
    }
    
    /**
     * Overloaded constructor for easier usage
     * @param pUserID   ID of the user
     * @param pPortalID ID of the portal service
     * @param pSourceWorkflowID ID of the source Workflow
     * @param pDestinWorkflowID ID of the destination workflow
     * @param pCopyHash hashtable that provides successibility of copying
     */ 
    public StoragePortalCopyWorkflowBean(String pUserID, String pPortalID, String pSourceWorkflowID, String pDestinWorkflowID, Hashtable pCopyHash) {
        portalID=pPortalID;
        userID=pUserID;
        sourceWorkflowID=pSourceWorkflowID;
        destinWorkflowID=pDestinWorkflowID;
        copyHash=pCopyHash;
    }
    
    /**
     * Getter method of user ID
     * @return value user ID
     */ 
    public String getUserID(){return userID;}
    
    /**
     * Getter method of portal ID
     * @return value portal ID
     */ 
    public String getPortalID(){return portalID;}
    
    /**
     *Getter method of the source workflow ID
     *@return value ID of the source workflow
     */ 
    public String getSourceWorkflowID(){return sourceWorkflowID;}
    
     /**
     *Getter method of destination workflow ID
     * @return value id of the destination workflow
     */ 
    public String getDestinWorkflowID(){return destinWorkflowID;}

    /**
     * Getter method of hashtable
     * @return copyHash
     */ 
    public Hashtable getCopyHash() {return copyHash;}
    
     /**
     * Setter method of user ID
     * @param value user ID
     */ 
    public void setUserID(String value){userID=value;}
    
    /**
     * Setter method of portal ID
     * @param value portal ID
     */ 
    public void setPortalID(String value){portalID=value;}
    
   /**
     * Setter method of the source workflow ID
     * @param value ID of the source workflow
     */ 
    public void setSourceWorkflowID(String value){sourceWorkflowID=value;}

    /**
     * Setter method of destination workflow ID
     * @param value id of the destination workflow
     */ 
    public void setDestinWorkflowID(String value){destinWorkflowID=value;}

    /**
     * Setter method of hashtable
     * @param value copyHash
     */ 
    public void setCopyHash(Hashtable value){copyHash = value;}
    
}

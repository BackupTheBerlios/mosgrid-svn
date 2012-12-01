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

package hu.sztaki.lpds.pgportal.services.asm.beans;

/**
 * Class to represent an item exported to the Repository
 * @author akos
 * @version 3.3
 */

public class ASMRepositoryItemBean
 {

  private Long id;
  private String userID;
  private String itemID;
  private String exportType;
  private String exportText;
  

/**
 * Default constructor
 */
  public ASMRepositoryItemBean(){}
  
   
/**
 * Gets the description typed by the developer of the item in the export form
 * @return description
 */
    public String getExportText() {
        return exportText;
    }
/**
 * Setter method
 * @param exportText
 */
    public void setExportText(String exportText) {
        this.exportText = exportText;
    }
/**
 * Gets the type of the exportation (see @RepositoryItemTypeConstants)
 * @return type
 */
    public String getExportType() {
        return exportType;
    }
/**
 * Setter method
 * @param exportType
 */
    public void setExportType(String exportType) {
        this.exportType = exportType;
    }
/**
 * Gets the id of the item
 * @return id
 */
    public Long getId() {
        return id;
    }
/**
 * Setter method
 * @param id
 */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the id of the developer (who exported the item)
     * @return user id
     */
    public String getUserID() {
        return userID;
    }
/**
 * Setter method
 * @param userID
 */
    public void setUserID(String userID) {
        this.userID = userID;
    }
/**
 * Gets the id of the workflow (or the item in different cases)
 * @return item id
 */
    public String getItemID() {
        return itemID;
    }
/**
 * Setter method
 * @param itemID
 */
    public void setItemID(String itemID) {
        this.itemID = itemID;
    }


}

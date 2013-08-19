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
package hu.sztaki.lpds.storage.service.carmen.server.upload;

import java.util.Hashtable;

/**
 * Stores the files uploaded by the UploadServlet and their upload status
 * in the items hashtable. In the items there can be more fileHash and in a
 * fileHash there can be more UploadItemBean as well.
 *
 * The uploaded file names, the upload status percentage
 * and the possible error messages are stored in the UploadItemBeans.
 *
 * @author lpds
 */
public class UploadItemsList {
    
    private static UploadItemsList instance = null;
    
    private Hashtable items;
/**
 * Creating the constructor singleton instance
 */
    public UploadItemsList() {
        if (instance == null) {
            instance = this;
        }
        items = new Hashtable();
    }
    
    /**
     * Returns the UploadItems instance.
     *
     * @return
     */
    public static UploadItemsList getInstance() {
        if (instance == null) {
            instance = new UploadItemsList();
        }
        return instance;
    }
    
    /**
     * Adds the file's perCent value to the stored items.
     *
     * (No error message entry.)
     *
     * @param SID
     * @param fileName
     * @param perCent
     */
    public void addFile(String SID, String fileName, Integer perCent) {
        this.addFile(SID, fileName, perCent, new String(""));
    }
    
    /**
     * Adds the file's perCent value to the stored items.
     *
     * If there is an error in the uploaded (zip) file, then the errorStr is not empty.
     *
     * @param SID
     * @param fileName
     * @param perCent
     * @param errorStr
     */
    public synchronized void addFile(String SID, String fileName, Integer perCent, String errorStr) {
        if (perCent.intValue() < 0) {
            perCent = new Integer(0);
        }
//        if (perCent.intValue() > 100) {
//            perCent = new Integer(100);
//        }
        if (errorStr == null) {
            errorStr = new String("");
        }
        // create upload item bean
        UploadItemBean uploadItemBean = new UploadItemBean(fileName, perCent, errorStr);
        // System.out.println("addFile(): SID: " + SID);
        // System.out.println("addFile(): fileName: " + fileName);
        // System.out.println("addFile(): perCent: " + perCent);
        // System.out.println("addFile(): errorStr: " + errorStr);
        // System.out.println("addFile(): before items: " + items.toString());
        if (items.get(SID) == null) {
            // fileHash does not exist, first call
            Hashtable fileHash = new Hashtable();
            fileHash.put(fileName, uploadItemBean);
            items.put(SID, fileHash);
            // System.out.println("addFile(): items.get(SID) == null");
        } else {
            // fileHash already exists
            ((Hashtable) items.get(SID)).put(fileName, uploadItemBean);
            // System.out.println("addFile(): items.get(SID) != null");
        }
        // System.out.println("addFile(): after_ items: " + items.toString());
        // System.out.println("items : " + items.toString());
    }
    
    /**
     * Removes a SID group from the items.
     *
     * @param SID
     */
    public void removeSID(String SID) {
        // System.out.println("removeSID(): SID: " + SID);
        // System.out.println("removeSID(): before items: " + items.toString());
        items.remove(SID);
        // System.out.println("removeSID(): after_ items: " + items.toString());
    }
    
    /**
     * Returns the fileHash belonging to the SID, received in the parameters.
     *
     * @param SID
     * @return fileHash
     */
    public Hashtable getFileHash(String SID) {
        // System.out.println("before items : " + items.toString());
        Hashtable retHash = (Hashtable) this.items.get(SID);
        // System.out.println("retHash : " + retHash);
        if (this.items.containsKey(SID)) {
            // System.out.println("items.containsKey(SID) : " + this.items.get(SID));
        } else {
            // System.out.println("items.NOT.containsKey(SID) : " + this.items.get(SID));
        }
        return retHash;
    }
    
}

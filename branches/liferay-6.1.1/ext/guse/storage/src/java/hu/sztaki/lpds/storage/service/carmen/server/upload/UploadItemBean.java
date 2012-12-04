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
 * Stores the name of the uploaded file,
 * the status of the upload in percentage,
 * and the error messages if they exist.
 *
 * @author lpds
 */
public class UploadItemBean {
    
    // uploaded file name
    private String fileName;
    
    // status of the upload in percentage
    private Integer perCent;
    
    // upload error message
    private String errorStr;
/**
 * Empty constructor
 */
    public UploadItemBean() {
    }
    
    /**
     * Constructor for easier use
     *
     * @param fileName - file name
     * @param perCent - status of the upload in percentage
     * @param errorStr - upload error message
     */
    public UploadItemBean(String fileName, Integer perCent, String errorStr) {
        this.setFileName(fileName);
        this.setPerCent(perCent);
        this.setErrorStr(errorStr);
    }
    
    /**
     * Returns the file name
     * @return fileName
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Sets the file name
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Returns the status of the upload in percentage
     * @return perCent
     */
    public Integer getPerCent() {
        return perCent;
    }
    
    /**
     * Sets the status of the upload in percentage
     * @param perCent
     */
    public void setPerCent(Integer perCent) {
        this.perCent = perCent;
    }
    
    /**
     * Returns the upload error message
     * @return errorStr
     */
    public String getErrorStr() {
        return errorStr;
    }
    
    /**
     * Sets the upload error message
     * @param errorStr
     */
    public void setErrorStr(String errorStr) {
        this.errorStr = errorStr;
    }
    
    /**
     * Returns the content of the bean
     * @return Bean fields in a string
     */
    public String toString() {
        return new String("fileName: " + fileName + " perCent: " + perCent + " errorStr: " + errorStr);
    }
    
}

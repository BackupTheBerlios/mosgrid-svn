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
package hu.sztaki.lpds.storage.service.carmen.client;

/**
 *
 * @author schandras
 */
public class RequestUtils {
    
    // boundary characters
    private String boundary;
    
    // request opening string (parameters)
    private String preRequestString;
    
    // request closing string
    private String postRequestString;
    
    /**
     * Call initialize.
     *
     */
    public RequestUtils() {
        initRequest();
    }
    
    /**
     * Initializing the strings containing the request
     *
     */
    private void initRequest() {
        this.boundary = getNewBoundary();
        this.preRequestString = new String("");
        this.postRequestString = new String("");
    }
    
    /**
     * Returns a unique boundary string
     *
     * @return
     */
    private String getNewBoundary() {
        Long randomNum = new Long(Math.round(Math.random() * 1000000));
        return new String("---------------------------608686321477698861354216387AaB03x" + randomNum);
    }
    
    /**
     * Adds a parameter descriptor string to the request
     *
     * @param parameterName
     * @param parameterValue
     */
    public void preRequestAddParameter(String parameterName, String parameterValue) {
        preRequestString += "--" + this.boundary + "\r\n" + "Content-Disposition: form-data; name=\"" + parameterName + "\"\r\n\r\n" + parameterValue + "\r\n";
    }
    
    /**
     * Adds a file descriptor string to the request
     *
     * @param name
     * @param filename
     */
    public void preRequestAddFile(String name, String filename) {
        // "Content-Type: application/zip" or "Content-Type: plain/text"
        preRequestString += "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"" + "\r\n" + "Content-Type: application/zip" + "\r\n\r\n";
    }
    
    /**
     * Adds a file renaming string to the request
     *
     * @param newName
     * @param oldPathName
     */
    public void preRequestAddRenameFile(String newName, String oldPathName) {
        preRequestAddParameter("file_" + newName, oldPathName);
    }
    
    /**
     * Sets the request closing string
     *
     */
    public void createPostRequest() {
        postRequestString = new String("\r\n--" + boundary + "--\r\n");
    }
    
    /**
     * Returns a boundary string
     *
     * @return
     */
    public String getBoundary() {
        return boundary;
    }
    
    /**
     * Returns the preRequestString-et in byte[] form
     *
     * @return
     */
    public byte[] getPreRequestStringBytes() {
        try {
            // System.out.print(preRequestString);
            return preRequestString.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
    
    /**
     * Returns the postRequestString-et in byte[] form
     *
     * @return
     */
    public byte[] getPostRequestStringBytes() {
        try {
            // System.out.print(postRequestString + "\n");
            return postRequestString.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
    
}

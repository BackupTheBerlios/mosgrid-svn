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
package hu.sztaki.lpds.repository.service.veronica.commons;

public class RepositoryRequestUtils {

    // keres nyito string (parameterek)
    private String preRequestString;

    // keres lezaro string
    private String postRequestString;

    // helper flag
    boolean first;

    /**
     * Call initialize.
     */    
    public RepositoryRequestUtils() {
        initRequest();
    }
    
    /**
     * Initializalja a kerest tartalmazo stringeket.
     */
    private void initRequest() {
        this.first = true;
        this.preRequestString = new String("");
        this.postRequestString = new String("\r\n");        
    }
    
    /**
     * A kereshez ad egy parametert leiro stringet.
     * 
     * @param parameterName
     * @param parameterValue
     */
    public void preRequestAddParameter(String parameterName, String parameterValue) {
        // enter : "\r\n"
        if (first) {
            first = false;
            preRequestString += parameterName + "=" + parameterValue;
        } else {
            preRequestString += "&" + parameterName + "=" + parameterValue;
        }     
    }

    /**
     * Visszaadja a preRequestString-et byte[] alakban.
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
     * Visszaadja a postRequestString-et byte[] alakban.
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

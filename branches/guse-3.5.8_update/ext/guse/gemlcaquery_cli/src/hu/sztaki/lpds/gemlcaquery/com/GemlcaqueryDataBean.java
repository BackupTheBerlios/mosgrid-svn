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

package hu.sztaki.lpds.gemlcaquery.com;

/**
 * @author lpds
 */
public class GemlcaqueryDataBean {
    
    // gemlca szolgaltatas
    // azonositoja (url)
    private String gemlcaUrl;
    
    // gemlca legacycode
    private String glcName;
    
    // a lekerdezeshez szukseges
    // certfile tartalma
    private String usercert;
    

    // UserID for getting LClist from SSP
    private String userID;


    /**
     * Required constructor for JavaBeans
     */
    public GemlcaqueryDataBean() {
        setGemlcaUrl("");
        setGlcName("");
        setUsercert("");
    }
    
    /**
     * Constructor for easier usage
     *
     * @param gemlcaUrl The identifier of the gemlca service (url)
     * @param usercert The content of the certificate file for the query
     */
    public GemlcaqueryDataBean(String gemlcaUrl, String usercert, String userID) {
        setGemlcaUrl(gemlcaUrl);
        setUsercert(usercert);
        setUserID(userID);
    }
    
////////////////////////////////////////////
/// getter setter methodes
////////////////////////////////////////////
    
    public String getGemlcaUrl() {
        return gemlcaUrl;
    }
    
    public void setGemlcaUrl(String gemlcaUrl) {
        this.gemlcaUrl = gemlcaUrl;
    }
    
    public String getGlcName() {
        return glcName;
    }
    
    public void setGlcName(String glcName) {
        this.glcName = glcName;
    }
    
    public String getUsercert() {
        return usercert;
    }
    
    public void setUsercert(String usercert) {
        this.usercert = usercert;
    }
    public String getUserID() {
        return userID;
    }
    
    public void setUserID(String userID) {
        this.userID = userID;
    }
}

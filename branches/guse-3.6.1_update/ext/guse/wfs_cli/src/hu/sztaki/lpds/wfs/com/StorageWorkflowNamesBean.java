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

package hu.sztaki.lpds.wfs.com;

/**
 * @author lpds
 */
public class StorageWorkflowNamesBean {
    
   /** az osszes input es a megadott rtID-vel az outputok (graf es abst es real workflow)*/
    public static final String downloadType_inputs_rtID = new String("inputs_");
    
    private String portalID;
    
    private String storageID;
    
    private String userID;
    
    private String workflowID;
    
    private String downloadType;
    
    private String workflowXML;
    
    private String newMainGrafName;
    
    private String newMainRealName;
    
    private String newMainAbstName;
    
    private String instanceType;
    
    private String uploadID;
    
    private String exportType;
    
    private String portalURL;
    
    private String storageURL;
    
    private String wfsID;
    
    private String zipFilePathStr;
    
    private String mainGrafName;
    
    private String mainRealName;
    
    private String mainAbstName;
    
    // time stemp
    private String importID;
    
    /**
     * JavaBeaneknen szukseges konstruktor
     */
    public StorageWorkflowNamesBean() {
        setDownloadType("");
        setInstanceType("");
        setExportType("work");
        setPortalURL("");
        setStorageURL("");
        setWfsID("");
        setZipFilePathStr("");
        setNewMainGrafName("");
        setNewMainRealName("");
        setNewMainAbstName("");
        setMainGrafName("");
        setMainRealName("");
        setMainAbstName("");
        setImportID("");
    }
    
    /**
     * Egyszerubb hasznalat erdekeben alkalmazott konstruktor
     *
     * @param pStorageID A storage azonositoja (url)
     * @param pPortalID A portal azonositoja
     * @param pUserID A felhasznalo neve
     * @param pWorkflowID A workflow azonositoja
     * @param pDownloadType A letoltes tipusa
     * @param pWorkflowXML A workflow xml leiro string
     * @param pNewMainGrafName A new main graf workflow azonositoja
     * @param pNewMainRealName A new main real, konkret workflow azonositoja
     * @param pNewMainAbstName Az new main abstract, template workflow azonositoja
     * @param pInstanceType le kell e tolteni az instance (runtimeID) leiro xml-t
     * @param pUploadID upload azonosito
     */
    public StorageWorkflowNamesBean(String pStorageID, String pPortalID, String pUserID, String pWorkflowID, String pDownloadType, String pWorkflowXML, String pNewMainGrafName, String pNewMainRealName, String pNewMainAbstName, String pInstanceType, String pUploadID) {
        setStorageID(pStorageID);
        setPortalID(pPortalID);
        setUserID(pUserID);
        setWorkflowID(pWorkflowID);
        setDownloadType(pDownloadType);
        setWorkflowXML(pWorkflowXML);
        setNewMainGrafName(pNewMainGrafName);
        setNewMainRealName(pNewMainRealName);
        setNewMainAbstName(pNewMainAbstName);
        setInstanceType(pInstanceType);
        setUploadID(pUploadID);
        setExportType("work");
        setPortalURL("");
        setStorageURL("");
        setWfsID("");
        setZipFilePathStr("");
        setMainGrafName("");
        setMainRealName("");
        setMainAbstName("");
    }
    
    /**
     * Portal azonosito lekerdezese
     * @return port azonosito
     */
    public String getPortalID() {
        return portalID;
    }
    
    /**
     * Beallitja a portal azonositojat
     * @param portalID portal azonosito
     */
    public void setPortalID(String portalID) {
        this.portalID = portalID;
    }
    
    /**
     * Felhasznalo azonosito lekerdezese
     * @return user ID
     */
    public String getUserID() {
        return userID;
    }
    
    /**
     * Beallitja a felhasznalo azonositojat
     * @param userID felhasznalo ID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    /**
     * Workflow azonosito lekerdezese
     * @return workflow ID
     */
    public String getWorkflowID() {
        return workflowID;
    }
    
    /**
     * Beallitja a workflow azonositojat
     * @param workflowID wfID
     */
    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
    }
    
    /**
     * Visszaadja a download tipusat
     * @return tipus neve
     */
    public String getDownloadType() {
        return downloadType;
    }
    
    /**
     * Beallitja a download tipusat
     * @param downloadType letoltes tipusa
     */
    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }
    
    /**
     * Workflow leiro xml lekerdezese
     * @return xml String formatumban
     */
    public String getWorkflowXML() {
        return workflowXML;
    }
    
    /**
     * Beallitja a workflow leiro xml-t
     * @param workflowXML XML String
     */
    public void setWorkflowXML(String workflowXML) {
        this.workflowXML = workflowXML;
    }
    
    /**
     * Visszaadja a new main graf workflow neve-t
     * @return uj graf neve
     */
    public String getNewMainGrafName() {
        return newMainGrafName;
    }
    
    /**
     * Beallitja a new main graf workflow neve-t
     * @param newMainGrafName uj graf neve
     */
    public void setNewMainGrafName(String newMainGrafName) {
        this.newMainGrafName = newMainGrafName;
    }
    
    /**
     * Visszaadja a new main real (konkret) workflow neve-t
     * @return uj wf neve
     */
    public String getNewMainRealName() {
        return newMainRealName;
    }
    
    /**
     * Beallitja a new main real (konkret) workflow neve-t
     * @param newMainRealName uj wf neve
     */
    public void setNewMainRealName(String newMainRealName) {
        this.newMainRealName = newMainRealName;
    }
    
    /**
     * Visszaadja az new main abstract (template) workflow neve-t
     * @return uj template neve
     */
    public String getNewMainAbstName() {
        return newMainAbstName;
    }
    
    /**
     * Beallitja az new main abstract (template) workflow neve-t
     * @param newMainAbstName uj template neve
     */
    public void setNewMainAbstName(String newMainAbstName) {
        this.newMainAbstName = newMainAbstName;
    }
    
    /**
     * Visszaadja az instanceType erteke-t
     * @return peldanyleiro szoveg
     */
    public String getInstanceType() {
        return instanceType;
    }
    
    /**
     * Beallitja az downloadInstance-t
     * @param instanceType letoltendo peldany azonositoja
     */
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }
    
    /**
     * Visszaadja az upload azonositojat
     * @return feltoltes egyedi azonositoja
     */
    public String getUploadID() {
        return uploadID;
    }
    
    /**
     * Beallitja az upload azonositojat
     * @param uploadID feltoltes egyedi azonositoja
     */
    public void setUploadID(String uploadID) {
        this.uploadID = uploadID;
    }
    
    /**
     * Visszaadja a storage azonositojat
     * @return tarolo storage service URL-je
     */
    public String getStorageID() {
        return storageID;
    }
    
    /**
     * Beallitja a storage azonositojat
     * @param storageID tarolo storage service URL
     */
    public void setStorageID(String storageID) {
        this.storageID = storageID;
    }
    
    /**
     * Visszaadja az exportalas tipusat.
     * @return export type
     */
    public String getExportType() {
        return exportType;
    }
    
    /**
     * Beallitja az exportalas tipusat
     * @param exportType export tipus
     */
    public void setExportType(String exportType) {
        this.exportType = exportType;
    }
    
    /**
     * Megvizsgalja a workflow letoltes tipusat.
     * @return true ha a workflow download type all
     */
    public boolean isAll() {
        // az osszes input es output minden rtID-vel (graf, real es abst workflow)
        return downloadType.equalsIgnoreCase("all");
    }
    
    /**
     * Megvizsgalja a workflow tipusat.
     * @return true ha a workflow egy graf
     */
    public boolean isGraf() {
        // (graf workflow)
        return downloadType.equalsIgnoreCase("graf");
    }
    
    /**
     * Megvizsgalja a workflow tipusat.
     * @return true ha a workflow egy template
     */
    public boolean isAbst() {
        // az osszes input (graf es abst workflow)
        return downloadType.equalsIgnoreCase("abst");
    }
    
    /**
     * Megvizsgalja a workflow tipusat.
     * @return true ha a workflow egy real, konkret wf
     */
    public boolean isReal() {
        // az osszes input es output minden rtID-vel (graf es real workflow)
        return downloadType.equalsIgnoreCase("real");
    }
    
    /**
     * Megvizsgalja a workflow tipusat.
     * @return true ha a workflow egy appl, application
     */
    public boolean isAppl() {
        // az osszes beagyazott workflow-t le kell tolteni
        // pluss module mode on
        return exportType.equalsIgnoreCase("appl");
    }
    
    /**
     * Megvizsgalja a workflow tipusat.
     * @return true ha a workflow egy proj, project
     */
    public boolean isProj() {
        // az osszes beagyazott workflow-t le kell tolteni
        // pluss module mode off
        return exportType.equalsIgnoreCase("proj");
    }
    
    /**
     * Megvizsgalja a workflow tipusat.
     * @return true ha a workflow egy work, konkret workflow
     */
    public boolean isWork() {
        // nem kell letolteni az osszes beagyazott workflow-t
        // pluss module mode off
        return exportType.equalsIgnoreCase("work");
    }
    
    /**
     * Megvizsgalja az instance tipusat.
     * @return true ha a instance type all
     */
    public boolean isInstanceAll() {
        // minden instance-t
        return instanceType.equalsIgnoreCase("all");
    }
    
    /**
     * Megvizsgalja az instance tipusat.
     * @return true ha a instance type one, egy konkret instance
     */
    public boolean isInstanceOne() {
        // egy konkret instance
        return instanceType.startsWith("one_");
    }
    
    /**
     * Megvizsgalja az instance tipusat.
     * @return true ha a instance type none
     */
    public boolean isInstanceNone() {
        // egyik instance sem
        return instanceType.equalsIgnoreCase("none");
    }
    
    /**
     * Visszaadja a portal azonositojat. (url)
     * @return portal url
     */
    public String getPortalURL() {
        return portalURL;
    }
    
    /**
     * Beallitja a portal azonositojat. (url)
     * @param portalURL portal url
     */
    public void setPortalURL(String portalURL) {
        this.portalURL = portalURL;
    }
    
    /**
     * Visszaadja a storage azonositojat. (url)
     * @return storage url
     */
    public String getStorageURL() {
        return storageURL;
    }
    
    /**
     * Beallitja a storage azonositojat. (url)
     * @param storageURL storage url
     */
    public void setStorageURL(String storageURL) {
        this.storageURL = storageURL;
    }
    
    /**
     * Visszaadja a wfs azonositojat.
     * @return wfs id
     */
    public String getWfsID() {
        return wfsID;
    }
    
    /**
     * Beallitja a wfs azonositojat.
     * @param wfsID wfs id
     */
    public void setWfsID(String wfsID) {
        this.wfsID = wfsID;
    }
    
    /**
     * Visszaadja a zip file eleresi utjat.
     * @return zip file path
     */
    public String getZipFilePathStr() {
        return zipFilePathStr;
    }
    
    /**
     * Beallitja a zip file eleresi utjat.
     * @param zipFilePathStr zip file path
     */
    public void setZipFilePathStr(String zipFilePathStr) {
        this.zipFilePathStr = zipFilePathStr;
    }
    
    /**
     * Visszaadja a main graf nevet.
     * @return main graf name
     */
    public String getMainGrafName() {
        return mainGrafName;
    }
    
    /**
     * Beallitja a main graf nevet.
     * @param mainGrafName main graf name
     */
    public void setMainGrafName(String mainGrafName) {
        this.mainGrafName = mainGrafName;
    }
    
    /**
     * Visszaadja a main real, konkret workflow nevet.
     * @return main real name
     */
    public String getMainRealName() {
        return mainRealName;
    }
    
    /**
     * Beallitja a main real, konkret workflow nevet.
     * @param mainRealName main real name
     */
    public void setMainRealName(String mainRealName) {
        this.mainRealName = mainRealName;
    }
    
    /**
     * Visszaadja a main abst, template nevet.
     * @return main abst name
     */
    public String getMainAbstName() {
        return mainAbstName;
    }
    
    /**
     * Beallitja a main abst, template nevet.
     * @param mainAbstName main abst name
     */
    public void setMainAbstName(String mainAbstName) {
        this.mainAbstName = mainAbstName;
    }
    
    /**
     * Visszaadja az import azonositot.
     * @return import id
     */
    public String getImportID() {
        return importID;
    }
    
    /**
     * Beallitja az import azonositot.
     * @param importID import id
     */
    public void setImportID(String importID) {
        this.importID = importID;
    }
    
}

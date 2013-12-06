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
/**
 * Repozitory es a WFS kozotti kommunikacioban resztvevo adat
 */
package hu.sztaki.lpds.wfs.com;

/**
 * @author lpds
 */
public class RepositoryWorkflowBean {
    
    private Long id;
    
    private String portalID;
    
    private String storageID;
    
    private String wfsID;
    
    private String userID;
    
    private String workflowID;
    
    private String exportType;
    
    private String downloadType;
    
    private String instanceType;
    
    private String outputLogType;
    
    private String repositoryID;
    
    private String exportText;
    
    private String exportID;
    
    // a repository-n beluli file path
    private String zipRepositoryPath;
    
    // a teljes file path, oprendszeren beluli
    // repository base path + repository-n beluli path
    private String zipFileFullPath;
    
    // import soran a main graf, graph workflow ezt a nevet kapja
    private String newGrafName;
    
    // import soran a main abst, template workflow ezt a nevet kapja
    private String newAbstName;
    
    // import soran a main real, concrete workflow ezt a nevet kapja
    private String newRealName;
    
    /**
     * JavaBeaneknek szukseges konstruktor
     */
    public RepositoryWorkflowBean() {
        setId(new Long(0));
        setPortalID("");
        setStorageID("");
        setWfsID("");
        setUserID("");
        setWorkflowID("");
        setExportType("");
        setDownloadType("");
        setInstanceType("");
        setOutputLogType("");
        setRepositoryID("");
        setExportText("");
        setExportID("");
        setZipRepositoryPath("");
        setZipFileFullPath("");
        setNewGrafName("");
        setNewAbstName("");
        setNewRealName("");
    }
    
    /**
     * Egyszerubb hasznalat erdekeben alkalmazott konstruktor
     *
     * @param pPortalID A portal azonositoja (url)
     * @param pStorageID A storage azonositoja (url)
     * @param pWfsID A wfs azonositoja (url)
     * @param pUserID A felhasznalo neve
     * @param pWorkflowID A workflow azonositoja
     * @param pExportText Az export megjegyzes text
     */
    public RepositoryWorkflowBean(String pPortalID, String pStorageID, String pWfsID, String pUserID, String pWorkflowID, String pExportText) {
        setId(new Long(0));
        setPortalID(pPortalID);
        setStorageID(pStorageID);
        setWfsID(pWfsID);
        setUserID(pUserID);
        setWorkflowID(pWorkflowID);
        setExportType("");
        setDownloadType("");
        setInstanceType("");
        setOutputLogType("");
        setRepositoryID("");
        setExportText(pExportText);
        setExportID("");
        setZipRepositoryPath("");
        setZipFileFullPath("");
        setNewGrafName("");
        setNewAbstName("");
        setNewRealName("");
    }
    
    /**
     * Visszaadja a portal azonositojat.
     * @return portal id
     */
    public String getPortalID() {
        return portalID;
    }
    
    /**
     * Beallitja a portal azonositojat.
     * @param portalID portal id
     */
    public void setPortalID(String portalID) {
        this.portalID = portalID;
    }
    
    /**
     * Visszaadja a storage azonositojat.
     * @return storage id
     */
    public String getStorageID() {
        return storageID;
    }
    
    /**
     * Beallitja a storage azonositojat.
     * @param storageID storage id
     */
    public void setStorageID(String storageID) {
        this.storageID = storageID;
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
     * Visszaadja a felhasznalo nevet.
     * @return user name
     */
    public String getUserID() {
        return userID;
    }
    
    /**
     * Beallitja a felhasznalo nevet.
     * @param userID user name
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }
    
    /**
     * Visszaadja a workflow nevet.
     * @return workflow name
     */
    public String getWorkflowID() {
        return workflowID;
    }
    
    /**
     * Beallitja a workflow nevet.
     * @param workflowID workflow name
     *
     */
    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
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
     * Visszaadja a letoltes tipusat.
     * @return letoltes tipusa
     */
    public String getDownloadType() {
        return downloadType;
    }
    
    /**
     * Beallitja a letoltes tipusat.
     * @param downloadType letoltes tipusa
     */
    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }
    
    /**
     * Visszaadja az instance tipusat.
     * @return instance type
     */
    public String getInstanceType() {
        return instanceType;
    }
    
    /**
     * Beallitja az instance tipusat.
     * @param instanceType instance type
     */
    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }
    
    /**
     * Visszaadja az export azonositot.
     * @return export id
     */
    public String getExportID() {
        return exportID;
    }
    
    /**
     * Beallitja az export azonositot
     * @param exportID export id
     */
    public void setExportID(String exportID) {
        this.exportID = exportID;
    }
    
    /**
     * Visszaadja az export text-et
     * @return export text
     */
    public String getExportText() {
        return exportText;
    }
    
    /**
     * Beallitja az export text-et
     * @param exportText export text
     */
    public void setExportText(String exportText) {
        this.exportText = exportText;
    }
    
    /**
     * Visszaadja a repository azonositojat
     * @return repository id
     */
    public String getRepositoryID() {
        return repositoryID;
    }
    
    /**
     * Beallitja a repository azonositojat.
     * @param repositoryID repository id
     */
    public void setRepositoryID(String repositoryID) {
        this.repositoryID = repositoryID;
    }
    
    /**
     * Visszaadja a letoltes output log tipusat.
     * @return letoltes output log tipusa
     */
    public String getOutputLogType() {
        return outputLogType;
    }
    
    /**
     * Beallitja a letoltes output log tipusat.
     * @param outputLogType  letoltes output log tipus
     */
    public void setOutputLogType(String outputLogType) {
        this.outputLogType = outputLogType;
    }
    
    /**
     * Visszaadja a repository-ban letarolt
     * zip file teljes eleresi utjat.
     * @return zip file full repository path
     */
    public String getZipFileFullPath() {
        return zipFileFullPath;
    }
    
    /**
     * Beallitja a repository-ban letarolt
     * zip file teljes eleresi utjat.
     * @param zipFileFullPath zip file full repository path
     */
    public void setZipFileFullPath(String zipFileFullPath) {
        this.zipFileFullPath = zipFileFullPath;
    }
    
    /**
     * Visszaadja a repository-ban letarolt
     * zip file eleresi utjat.
     * @return zip file repository path
     */
    public String getZipRepositoryPath() {
        return zipRepositoryPath;
    }
    
    /**
     * Beallitja a repository-ban letarolt
     * zip file eleresi utjat.
     * @param zipRepositoryPath zip file repository path
     */
    public void setZipRepositoryPath(String zipRepositoryPath) {
        this.zipRepositoryPath = zipRepositoryPath;
    }
    
    /**
     * Visszaadja az adatbazis tablaban
     * szereplo id-t (long).
     * @return azonosito erteke
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Beallitja az adatbazis tablaban
     * szereplo id-t (long).
     *
     * Adatbazis olvasaskor hasznalatos.
     * @param id egyedi ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Visszaadja egy workflow tipusat.
     *
     * Erteke lehet: (appl, proj, real, abst, graf, "")
     * (szamolt ertek, az export es a download type hatarozza meg)
     * @return adat tipusa
     */
    public String getWorkflowType() {
        if (!"".equals(exportType.trim())) {
            if (exportType.equals("work")) {
                // real, abst, graf
                return downloadType;
            } else {
                // appl, proj
                return exportType;
            }
        }
        return new String("");
    }
    
    /**
     * Beallitja egy workflow tipusat.
     *
     * Erteke lehet: (appl, proj, real, abst, graf)
     *
     * (szamolt ertek, az export es a download type-ot hatarozza meg)
     *
     * exportType - downloadType
     * "appl" - "all"
     * "proj" - "all"
     * "work" - "real, abst, graf"
     * @param workflowType workflow tipusa
     */
    public void setWorkflowType(String workflowType) {
        if ((workflowType.equals("appl")) || (workflowType.equals("proj"))) {
            setExportType(workflowType);
            setDownloadType("all");
        } else {
            setExportType("work");
            if (("real".equals(workflowType)) || ("abst".equals(workflowType)) || ("graf".equals(workflowType))) {
                setDownloadType(workflowType);
            } else if ("work".equals(workflowType)) {
                setDownloadType("real");
            } else {
                setDownloadType("all");
            }
        }
    }
    
    /**
     * Visszaadja a graf nevet.
     * @return graf name
     */
    public String getNewGrafName() {
        return newGrafName;
    }
    
    /**
     * Beallitja a graf nevet.
     * @param newGrafName graf name
     */
    public void setNewGrafName(String newGrafName) {
        this.newGrafName = newGrafName;
    }
    
    /**
     * Visszaadja az abst, template nevet.
     * @return abst name
     */
    public String getNewAbstName() {
        return newAbstName;
    }
    
    /**
     * Beallitja az abst, template nevet.
     * @param newAbstName abst name
     */
    public void setNewAbstName(String newAbstName) {
        this.newAbstName = newAbstName;
    }
    
    /**
     * Visszaadja a real, konkret workflow nevet.
     * @return real name
     */
    public String getNewRealName() {
        return newRealName;
    }
    
    /**
     * Beallitja a real, konkret workflow nevet.
     * @param newRealName real name
     */
    public void setNewRealName(String newRealName) {
        this.newRealName = newRealName;
    }
    
}

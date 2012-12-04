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
package hu.sztaki.lpds.repository.service.veronica.server.quota;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.repository.service.veronica.commons.RepositoryFileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * A repository teruleten tarolt workflow-k (zip fileok)
 * (osszes) meretet tartja nyilvan.
 *
 * @author lpds
 */
public class QuotaService {
    
    private static QuotaService instance = null;
    
    // repository root dir
    private String repositoryDir;
    
    // File separator (for example, "/")
    private String sep;
    
    // a repositoryban tarolt osszes zip file
    // (workflow) ossz merete byte-ban.
    private long quotaLong;

    // a repositoryban tarolt osszes zip file
    // (workflow) ossz meretenek felso hatarerteke
    // byte-ban, ha ezt az erteket elerte a quotaLong
    // akkor mar nem indithato ujjabb sikeres exportalas.
    private long quotaMaxLong;
    
    public QuotaService() {
        if (instance == null) {
            instance = this;
        }
        LoadProperty();
        quotaLong = 0;
        // first init
        try {
            initQuotaService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * QuotaService peldanyt ad vissza.
     *
     * @return
     */
    public static QuotaService getInstance() {
        if (instance == null) {
            instance = new QuotaService();
        }
        return instance;
    }
    
    /**
     * Init, indulo adatok betoltese.
     */
    private void LoadProperty() {
        sep = RepositoryFileUtils.getInstance().getSeparator();
        repositoryDir = RepositoryFileUtils.getInstance().getRepositoryDir();
        RepositoryFileUtils.getInstance().createRepositoryDirectory();
    }
    
    /**
     * A repository teruleten talalhato osszes workflow zip
     * mereterol keszit nyilvantartast.
     */
    private void initQuotaService() throws Exception {
        // init dir all zip files...
        // (applications, projects, workflows (real, abst, graf)...)
        initDirAllZipFile("applications");
        initDirAllZipFile("projects");
        initDirAllZipFile("workflows" + sep + "real");
        initDirAllZipFile("workflows" + sep + "abst");
        initDirAllZipFile("workflows" + sep + "graf");
        quotaMaxLong = Long.parseLong(PropertyLoader.getInstance().getProperty("quotamax")) * 1024 * 1024;
        // System.out.println("set quotaMaxlong : " + quotaMaxLong + " byte...");
    }
    
    /**
     * A megadott konyvtarban talalhato osszes zip file
     * mereterol keszit nyilvantartast.
     */
    private void initDirAllZipFile(String repDirName) throws Exception {
        String baseDir = repositoryDir + repDirName + sep;
        // get and parse zip file list
        ArrayList fileList = RepositoryFileUtils.getInstance().getDirFileList(baseDir, ".zip");
        if (fileList.size() > 0) {
            for (int fPos = 0; fPos < fileList.size(); fPos++) {
                String fileID = ((String) fileList.get(fPos));
                File f = new File(baseDir + fileID);
                // System.out.println("zip file path : " + f.getAbsolutePath());
                addZipFileSize(f.length());
            }
        }
    }
    
    /**
     * Noveli a quota erteket.
     */
    public void addZipFileSize(long zipSize) {
        // System.out.println("add to quota : " + zipSize + " byte...");
        quotaLong += zipSize;
        // System.out.println("Repository quota now : " + quotaLong + " byte...");
    }
    
    /**
     * Visszaadja az aktualis quota erteket byte-ban.
     */
    public long getQuota() {
        return quotaLong;
    }

    /**
     * Visszaadja az aktualis quota ertekeket egy hashtable-ban.
     *
     * key: quota, value: aktualis quota erteke byte-ban.
     * key: quotamax, value: maximalis quota erteke byte-ban.
     */
    public Hashtable getQuotaHash() {
        Hashtable retHash = new Hashtable();
        retHash.put("quota", quotaLong);
        retHash.put("quotamax", quotaMaxLong);
        return retHash;
    }
    
    /**
     * Visszaadja van e meg szabad hely a repository teruleten.
     *
     * (property file beallitas alapjan dont)
     *
     */
    public boolean isFreeQuota() {
        return (quotaLong < (quotaMaxLong * 0.95));
    }
    
}

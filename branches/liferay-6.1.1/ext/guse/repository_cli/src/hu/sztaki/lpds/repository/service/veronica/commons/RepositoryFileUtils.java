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

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.storage.service.carmen.commons.FileUtils;
import java.io.File;
import java.util.ArrayList;

/**
 * Allomany, könyvtar kezelest valosit meg. (helper class)
 *
 * @author lpds
 */
public class RepositoryFileUtils {
    
    private static RepositoryFileUtils instance = null;
    
    // File separator (for example, "/")
    private String sep;
    
    // Repository repository root dir
    private String repositoryDir;
    
    // repository service URL, repositoryID
    private String repositoryURL;

    // repository root user id
    private String rootUserID;
    
    public RepositoryFileUtils() {
        if (instance == null) {
            instance = this;
        }
        sep = System.getProperty("file.separator");
        repositoryDir = PropertyLoader.getInstance().getProperty("prefix.dir");
        if ((repositoryDir == null) || (repositoryDir.trim().equals(""))) {
            repositoryDir = PropertyLoader.getInstance().getProperty("portal.prefix.dir");
        }
        if ((repositoryDir == null) || (repositoryDir.trim().equals(""))) {
            repositoryDir = System.getProperty("java.io.tmpdir");
        }
        if (!repositoryDir.endsWith(sep)) {
            repositoryDir += sep;
        }
        repositoryDir += "repository/";
        repositoryURL = PropertyLoader.getInstance().getProperty("service.url");
        rootUserID = PropertyLoader.getInstance().getProperty("rootuser");
        if ((rootUserID == null) || ("".equals(rootUserID))) {
            rootUserID = "root";
        }
    }
    
    /**
     * RepositoryFileUtils peldanyt ad vissza.
     *
     * @return
     */
    public static RepositoryFileUtils getInstance() {
        if (instance == null) {
            instance = new RepositoryFileUtils();
        }
        return instance;
    }
    
    /**
     * Visszaadja a file separator erteket.
     *
     * @return
     */
    public String getSeparator() {
        return sep;
    }
    
    /**
     * Visszaadja a repository repository directory erteket.
     *
     * @return
     */
    public String getRepositoryDir() {
        return repositoryDir;
    }
    
    /**
     * Visszaadja a repository service url erteket.
     *
     * @return string
     */
    public String getRepositoryUrl() {
        return repositoryURL;
    }

    /**
     * Visszaadja a root user nevet.
     * 
     * (csak enek a user-nek van joga
     * torolni a repository-bol)
     *
     * (a repository webapp alatt talalhato 
     * service.property-ben van megadva)
     *
     * @return
     */
    public String getRootUserID() {
        return rootUserID;
    }
    
    /**
     * Letrehozza a repository konyvtarat.
     */
    public void createRepositoryDirectory() {
        try {
            FileUtils.getInstance().createDirectory(this.repositoryDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Visszaadja a megkapott konyvtar alatt talalhato
     * megadott (kiterjesztesu, file nev vegu pl: .zip)
     * file-ok neveinek listajat.
     *
     * @param baseDir
     * @param nameEndWithExt
     * @return - ArrayList file nevek listaja
     * @throws Exception
     */
    public ArrayList getDirFileList(String baseDir, String nameEndWithExt) throws Exception {
        if (!baseDir.endsWith(sep)) {
            baseDir += sep;
        }
        ArrayList retList = new ArrayList();
        File dir = new File(baseDir);
        if (dir.exists()) {
            if (dir.isDirectory()) {
                String[] filesList = dir.list();
                if (filesList.length > 0) {
                    for (int pos = 0; pos < filesList.length; pos++) {
                        String entry = baseDir + filesList[pos];
                        if (!new File(entry).isDirectory()) {
                            if (entry.endsWith(nameEndWithExt)) {
                                retList.add(filesList[pos]);
                            }
                        }
                    }
                }
            } else {
                throw new Exception("baseDir is not directory ! (" + baseDir + ")");
            }
        }
        // else {
        // throw new Exception("baseDir is not exist ! (" + baseDir + ")");
        // }
        return retList;
    }
    
}

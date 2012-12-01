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
 * LocalEngin Logger data presentation
 */
package hu.sztaki.lpds.logging.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author krisztian
 */
public class LocalEngineDataBean {

    private String filePrefix; //logfile prefix
    private File logfile; //tenyleges file
    private long logline; //sorok szama

/**
 * Constructor
 * @param pPath file path
 * @throws IOException file opertaion error
 */

    public LocalEngineDataBean(String pPath) throws IOException{
        filePrefix=pPath;
        initLogFile();
        
    }
/**
 * Creating new log file
 * @throws IOException file opertaion error
 */
    private void initLogFile() throws IOException{
        long item=System.currentTimeMillis();
        logfile=new File(filePrefix+"-"+item);
        if (!logfile.getParentFile().exists()) {
            logfile.getParentFile().mkdirs();
            logfile.createNewFile();
            FileWriter fw=new FileWriter(logfile);
            fw.write("<guse>\n");
            fw.close();
        }
    }

/**
 * Getting log file
 * @return file descriptor instance
 */
    public File getLogfile() {
        return logfile;
    }

/**
 * Creating new line in the log file
 */
    public synchronized void newLine() {
        if (logline == 1000 || !getLogfile().exists()) {
            try{initLogFile();}
            catch(IOException e){/**/}
            logline = 0;
        }
        else logline++;
    }

/**
 * Getting the logfile's path
 * @return
 */
    public String getFilePrefix() {
        return filePrefix;
    }
}

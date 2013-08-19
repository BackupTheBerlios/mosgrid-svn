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
 * Local file based log, saving text messages (xml)
 */
package hu.sztaki.lpds.logging;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.logging.data.LocalEngineDataBean;
import hu.sztaki.lpds.logging.inf.LoggerClient;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * @author krisztian
 */
public class LocalEngine implements LoggerClient {

    private static Hashtable<String, LocalEngineDataBean> paths = new Hashtable<String, LocalEngineDataBean>();
    private static String dir = null;//PropertyLoader.getInstance().getProperty("prefix.dir") + PropertyLoader.getInstance().getProperty("guse.logdir");
    private static byte loglevel = 0;//Byte.parseByte(PropertyLoader.getInstance().getProperty("service.loglevel"));
    private boolean inited = false;
    private static boolean trace=false;
    private static String service=""; //service url psecalis jelek nelkul

    @Override
    public synchronized void service(byte pLevel, String pMsg, boolean pCtrace) {
        if ((loglevel <= pLevel) & inited) {
            try {
                long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                File f = getLogFile("/sercivce/calling");
                FileWriter fw = new FileWriter(f, true);
                fw.write("<msg txt=\"" + pMsg + "\" level=\"" + pLevel + "\" memory=\"" + memory + "\" time=\"" + Calendar.getInstance().getTime() + "\" utc=\"" + System.currentTimeMillis() + "\"");
                if (!pCtrace) {
                    fw.write(" />\n");
                } else {
                    fw.write(">\n");
                    if(trace) fw.write(getTrace());
                    fw.write("</msg>\n");
                }
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void workflow(String pWRID, byte pLevel, String pMsg) {
        if ((loglevel <= pLevel) & inited) {
            try {
                File f = getLogFile("/workflows/" + pWRID+".xml");
                FileWriter fw = new FileWriter(f, true);
                fw.write("<" + pMsg + getTimeAndMemoryInfo() + "/>\n");
                if(trace) fw.write(getTrace());
                fw.flush();
                fw.close();
            } catch (Exception e) {/*delete logg*/}

        }
    }

    @Override
    public void workflow(String pWRID, Exception pEx, String pMsg) {
        if ((loglevel <= EXCEPTION) & inited) {
            try {
                File f = getLogFile("/workflows/" + pWRID+".xml");
                FileWriter fw = new FileWriter(f, true);
                fw.write("<exception msg=\"" + pEx.getMessage() + "\" " + pMsg + getTimeAndMemoryInfo() + " />\n");
                for (StackTraceElement p : pEx.getStackTrace()) {
                    fw.write("\t" + p.toString() + "\n");
                }
                fw.write("</exception> \n");
                fw.flush();
                fw.close();
            } catch (Exception e) {/*delete logg*/

            }
        }
    }

    @Override
    public void job(String pJobID, byte pLevel, String pMsg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public synchronized void trigger() {
        System.out.println("LOGGER ACTIVALVA:"+PropertyLoader.getInstance().getProperty("service.url"));
        try {
            paths = new Hashtable<String, LocalEngineDataBean>();
            dir = PropertyLoader.getInstance().getProperty("prefix.dir") + PropertyLoader.getInstance().getProperty("guse.logdir");
            loglevel = Byte.parseByte(PropertyLoader.getInstance().getProperty("service.loglevel"));
            inited = true;
            trace=Boolean.parseBoolean(PropertyLoader.getInstance().getProperty("service.logtrace"));
            service=PropertyLoader.getInstance().getProperty("service.url").replace("/", "_").replace("\\", "_");

        } catch (Exception e) {
            e.printStackTrace();
            inited = false;
        }
    }

    /**
     * serviceLog file query
     * @param pURL service URL
     * @return path
     */
    private static File getLogFile(String pFile) throws IOException {
        String path=service+pFile;
        if (paths.get(service+pFile) == null) {
            LocalEngineDataBean tmp = new LocalEngineDataBean(dir+"/"+path);
            paths.put(path, tmp);
        }
        paths.get(path).newLine();
        return paths.get(path).getLogfile();
        
    }

    /**
     * Getting stack trace
     * @return
     */
    private static String getTrace() {
        StringBuffer res = new StringBuffer();
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement t;
        for (int i = 3; i < stackTrace.length; i++) {
            t = stackTrace[i];
            res.append("\t" + t.getClassName() + "(" + t.getMethodName() + " " + t.getLineNumber() + ")\n");
        }
        return res.toString();
    }

/**
 * Closing all open log files
 * @throws IOException file operation unsuccessful
 */
    private static synchronized void closeAllFiles() throws IOException {
        Enumeration<LocalEngineDataBean> enm = paths.elements();
        while (enm.hasMoreElements()) {
            closeFile(enm.nextElement().getLogfile());
        }
    }

    private static void closeFile(File f) throws IOException {
        FileWriter fw = new FileWriter(f);
        fw.write("</guse>");
        fw.flush();
        fw.close();
    }

    /**
     * close all file
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeAllFiles();
    }




    public void pool(String pPool, String pMsg, long pCount) {
        if ((loglevel <= DEBUG) & inited) {
            try {
                File f = getLogFile("/pools/" + pPool);
                FileWriter fw = new FileWriter(f, true);
                fw.write("<" + pMsg + " count=\"" + pCount + "\"" + getTimeAndMemoryInfo() + "/>\n");
                fw.flush();
                fw.close();
            } catch (Exception e) {e.printStackTrace();/*delele log*/

            }
        }
    }

    public void pool(String pPool,  String pData, Exception pEx, long pCount) {
        if ((loglevel <= DEBUG) & inited) {
            try {
                File f = getLogFile("/pools/" + pPool);
                FileWriter fw = new FileWriter(f, true);
                fw.write("<exception msg=\"" + pEx.getMessage() + "\" "+pData+" count=\"" + pCount + "\"" + getTimeAndMemoryInfo() + ">\n");
                for (StackTraceElement p : pEx.getStackTrace()) {
                    fw.write("\t" + p.toString() + "\n");
                }
                fw.write("</exception>\n");
                fw.flush();
                fw.close();
            } catch (Exception e) {e.printStackTrace();/*delele logg*/

            }
        }
    }
    

    /**
     * Generating time and memory XML attributes for tags
     * @return String tag part
     */
    private String getTimeAndMemoryInfo() {
        long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        return " memory=\"" + memory + "\" time=\"" + Calendar.getInstance().getTime() + "\" utc=\"" + System.currentTimeMillis() + "\" ";
    }


}




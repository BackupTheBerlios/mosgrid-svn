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
 * Linux-based wrapper script generation
 */

package hu.sztaki.lpds.dcibridge.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author krisztian karoczkai
 */
public class LinuxWrapper {
    private String path;
    private File f;
    private FileWriter fw;
/**
 * Constructor, wrapper creation
 * @param pPath Path for new wrapper 
 * @throws java.lang.Exception Exception if the file is not able to create
 */
    public LinuxWrapper(String pPath) throws Exception{
        path=pPath;
        createFile(path);
    }
    
    /*
     *Default constructor to be able to extend 
     */
    public LinuxWrapper(){}


/**
 * Wrapper file name query
 * @return file name
 */
    public String getWrapperName(){return "wrapper.sh";}
/**
 * Wrapper file creation, certificate setting
 * @param pPath
 * @throws java.lang.Exception
 */
    public void createFile(String pPath) throws Exception{
        f=new File(getFilepath());
        f.createNewFile();
        setPermission(getWrapperName());
        fw=new FileWriter(f);
        fw.write("#!/bin/bash\n");
        fw.flush();
    }
/**
 * Working/Utility library for relative file, certificate settings.
 * @param pFile file neve
 * @throws java.lang.Exception Certificate setting is not permitted
 */
    public void setPermission(String pFile) throws Exception{
        File f=new File(path+pFile);
        f.setReadable(true);
        f.setWritable(true);
        f.setExecutable(true);
    }
/**
 * Generating unzipping command at app.tgz extension
 * @throws java.io.IOException file irasi hiba
 */
    public void unCompressBinary() throws IOException{fw.write("tar -xvzf execute.bin\n");fw.flush();}
/**
 * Generating command for entry to working library
 * @throws java.io.IOException file writing error
 */
    public void cd() throws IOException{fw.write("cd "+path+"\n");fw.flush();}
/**
 * Setting environment values in case of Java job
 * @param pJavaHome JAVA_HOME value
 * @throws java.io.IOException file writing error
 */
    public void setJavaEnviroments(String pJavaHome) throws IOException{
        fw.write("export JAVA_HOME="+System.getenv("java.home")+"\n");
        fw.write("export PATH="+System.getenv("PATH")+":"+pJavaHome+"/bin/\n");
        fw.write("LOCALCLASSPATH=$CLASSPATH\n");
        fw.write("for JAR in *.jar; do\n");
        fw.write("LOCALCLASSPATH=\"$LOCALCLASSPATH:$JAR\"\n");
        fw.write("done\n");
        fw.write("export CLASSPATH=$LOCALCLASSPATH\n");
        fw.write("LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$PWD\n");
        fw.write("export LD_LIBRARY_PATH\n");
        fw.flush();
    }
/**
 * Generating start command for Java job 
 * @param pBin bibary name of working library 
 * @param pParams command line parameters
 * @param pStdErr working library relative std error
 * @param pStdOut working library std out
 * @throws java.io.IOException file writng error
 */
    public void runJava(String pBin,String pParams, String pStdOut, String pStdErr)throws IOException{
        fw.write("java -jar "+pBin+" "+pParams+" >"+pStdOut+" 2>"+pStdErr+" \n");
        fw.flush();
    }
/**
 * Settings environment parameters for binary running 
 * @throws java.io.IOException file writing error
 */
    public void setBinaryEnviroments() throws IOException{
        fw.write("LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$PWD\n");
        fw.write("export LD_LIBRARY_PATH\n");
        fw.flush();
    }
/**
 * Genrating command to run binary 
 * @param pBin working library relative binaris neve
 * @param pParams parancssori parameterek
 * @param pStdErr working library relative std error
 * @param pStdOut working library relative std out
 * @throws java.io.IOException file writing error
 */
    public void runBinary(String pBin,String pParams, String pStdOut, String pStdErr)throws IOException{
        fw.write(path+pBin+" "+pParams+" >"+pStdOut+" 2>"+pStdErr+"\n");
        fw.flush();
    }

    public void writeFile(String pFile,String pText,boolean pAppend)throws Exception{
        File nfo=new File(path+"outputs/"+pFile);
        if(!nfo.exists()) nfo.createNewFile();
        FileWriter nfoFW=new FileWriter(nfo, pAppend);
        nfoFW.write(pText);
        nfoFW.flush();
        nfoFW.close();
    }

    public void writeFile(String pFile,Exception pEx,boolean pAppend)throws Exception{
        File nfo=new File(path+"outputs/"+pFile);
        if(!nfo.exists()) nfo.createNewFile();
        FileWriter nfoFW=new FileWriter(nfo, pAppend);
        nfoFW.write("\n"+pEx.getMessage()+"("+pEx.getLocalizedMessage()+")\n");
        for(StackTraceElement t:pEx.getStackTrace())
            nfoFW.write(t.getClassName()+"."+t.getMethodName()+"("+t.getFileName()+":"+t.getLineNumber()+")\n");
        nfoFW.flush();
        nfoFW.close();
    }

    public String getFilepath(){return path+getWrapperName();}
    public void setMPIEnviroments() throws IOException{}
    public void downloadRemoteInputs(List<String> pValues) throws IOException{}
    public void uploadRemoteOutputs(List<String> pValues) throws IOException{}
    public void uncompressCollectors(List<String> pValues) throws IOException{}
    public void compressGenerators(List<String> pValues) throws IOException{}
    public void close() throws IOException{fw.close();}

}

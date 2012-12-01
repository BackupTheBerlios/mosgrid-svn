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
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * @author krisztian karoczkai
 */
public class LinuxWrapperForGrids {
    public static int FUNCTION_NONE = 0;
    public static int FUNCTION_LCGREMOTEIO = 1;
    /**
     * adds gsiftpupload() function
     */
    public static int FUNCTION_GSIFTPREMOTEIO = 2;

    public static String REMOTEGENERATORS_PID = "REMOTEGENERATORS_PID";

    protected String path;
    private File f;
    protected FileWriter fw;

    private String stderr = "stderr.log";
    private String stdout = "stdout.log";
    private String gridnfo = "gridnfo.log";

    private String userID;
    private String jobID;


    private boolean callback = false;

    protected String compressedlocalout = null; //the file name
    protected boolean compressoutputs = false;
    private String callbackStatusServletURL = null;
    protected Vector localoutputs;
    
/**
 * Constructor, wrapper creation
 * @param pPath Path for new wrapper
 * @param callbackStatusServletURL if not NULL, enables the callback functions,
 * it trys call back to a servlet (JobStatusServlet) when the script starts, and
 * try to send the output files, when finished. (uses curl, wget)
 * @throws java.lang.Exception file creation is not possible 
 */
    public LinuxWrapperForGrids(String pPath) throws Exception{
        path=pPath;       
        createFile(path);
    }

    /*
     *Default constructor to be able to extend
     */
    public LinuxWrapperForGrids(){}

/** 
 * @param callbackStatusServletURL if not NULL, enables the callback functions,
 * it trys call back to a servlet (JobStatusServlet) when the script starts, and
 * try to send the output files, when finished. (uses curl, wget).
 * @param pLocalOutputs contains the local output file names (String).
 * @param pCompressLocalOuts if true, the local output files wil be compressed, 
 * whidout the standard error(stderr.log) but including gridnfo.log and stdout.log.
 */    
    public void setLocalOutsAndCallback(Vector pLocalOutputs, String pCompressLocalOuts, String pcallbackStatusServletURL, String pUserID, String pJobID){
        if (pcallbackStatusServletURL!=null){
            callback=true;
            callbackStatusServletURL=pcallbackStatusServletURL;
        }
        userID = pUserID;
        jobID = pJobID;
        setLocalOutputs(pLocalOutputs, pCompressLocalOuts);
    }
    
 /** 
 * @param pLocalOutputs contains the local output file names (String)
 * @param pCompressLocalOuts if not null, the local output files will be compressed,
 * whidout the standard error(stderr.log) but including gridnfo.log and stdout.log
  * and if exists REMOTEGENERATORS_PID
 */
    public void setLocalOutputs(Vector pLocalOutputs, String pCompressLocalOuts) {
        localoutputs = pLocalOutputs;
        if (pCompressLocalOuts != null) {
            compressoutputs = true;
            compressedlocalout = pCompressLocalOuts;
        }
  }
    


    public void setRemoteInputs(Vector pRemoteInputs) {
    }

    public void setRemoteOutputs(Vector pRemoteOutputs) {
    }

  /**
   * Sets the log file names, default is: stderr.log, stdout.log, gridnfo.log
   * @param pStderr
   * @param pStdout
   * @param pGridnfo
   */
public void setLogfileNames(String pStderr, String pStdout, String pGridnfo){
    stderr=pStderr;
    stdout=pStdout;
    gridnfo=pGridnfo;
}

/**
 * Wrapper file name query
 * @return file name
 */
    public String getWrapperName(){return "wrapper.sh";}


/**
 * Wrapper file creation,+  #!/bin/bash + add default functions
 * @param pPath
 * @throws java.lang.Exception
 */
    public void createFile(String pPath) throws Exception{
        f=new File(pPath+getWrapperName());
        f.createNewFile();
        fw=new FileWriter(f);
        fw.write("#!/bin/bash\n");    
        fw.flush();
        //setPermission(getWrapperName());
    }

    
/**
 * add dolog() ext() and nfo() functions
 * and uploadouts(), if callabck is enabled
 * and compressouts(), if local output file compression is enabled.
 * Use setLocalOutsAndCallback before calling this function.
 * @throws java.io.IOException file writing error
 */
    public void addFunctionsAndStartScript() throws IOException {
        addFunctionsAndStartScript(null, 0);
    }

/**
 * add dolog() ext() and nfo() functions 
 * and uploadouts(), if callabck is enabled
 * and compressouts(), if local output file compression is enabled.
 * Use setLocalOutsAndCallback before calling this function.
 * @param pNewFunctions if not NULL, add private functions to shell script
 * @param builtInExtrafunctions supported: FUNCTION_LCGREMOTEIO
 * @throws java.io.IOException file writing error
 */
    public void addFunctionsAndStartScript(String pNewFunctions, int builtInExtrafunctions) throws IOException {
        fw.write("function dolog()  \n" +
                "{  \n" +
                "if [ $# -eq 0 ]; then \n" +
                "    cat >> gridnfo.log \n" +
                "else \n" +
                "    echo \"$@\" >> gridnfo.log  \n" +
                "fi \n" +
                "} \n");
        if (compressoutputs){//compress local outputs
            fw.write("function compressouts() \n{ \n");
            String coutfiles = "";
            for (int i = 0; i < localoutputs.size(); i++) {
                if (!"REMOTEGENERATORS_PID".equals(localoutputs.get(i)) ){
                coutfiles+=localoutputs.get(i)+"* ";
                }
            }
            fw.write("if [ -e `pwd`/REMOTEGENERATORS_PID ]; then \n");
            compressFiles(compressedlocalout, "REMOTEGENERATORS_PID " + coutfiles);
            fw.write("else \n");//nincs remote generator
            compressFiles(compressedlocalout, coutfiles);
            fw.write("fi \n");
            fw.write("} \n\n");
        }
        fw.write("function ext() \n");
        fw.write("{ \n");
        fw.write("echo '- An error occured!' >> gridnfo.log \n");
        fw.write("nfo \n ");
        if (compressoutputs) {
            fw.write("compressouts \n");
        }
        if (callback){
            fw.write("uploadouts '7' \n");
        }
        fw.write("exit $1 \n");
        fw.write("} \n");
        fw.write("function nfo() \n");
        fw.write("{ \n");
        fw.write("echo '- Ran on host (hostname)  :' `hostname` >> gridnfo.log \n");
        fw.write("echo '- Host info-s (uname -a)  :' `uname -a` >> gridnfo.log \n");
        fw.write("echo '- Directory list (ls -la) : - - - - -' >> gridnfo.log \n");
        fw.write("ls -la >> gridnfo.log \n");
        fw.write("echo '- Environment var. (env): - - - - - -'>> gridnfo.log \n");
        fw.write("env >> gridnfo.log \n");
        fw.write("} \n");

        if (pNewFunctions != null){
            fw.write(pNewFunctions);
        }
        if (builtInExtrafunctions==FUNCTION_LCGREMOTEIO){
            addFunctionsLCGRemoteIO();
        }else if (builtInExtrafunctions==FUNCTION_GSIFTPREMOTEIO){
            addFunctionsGSIFTPremoteIO();
        }

        if (callback){// upload local outputs
            fw.write("function uploadouts() \n");
            fw.write("{ \n");
            fw.write("if test $callback -ne 0 \n then \n"
                    + "  wget --no-check-certificate -O /dev/null -q -t 2 -T 5 '" + callbackStatusServletURL + "?uid=" + userID + "&jobid=" + jobID + "&status='$1 2> /dev/null 1> /dev/null \n" // -Ooutput-document -quiet -try 2 -Timeout 5
                    + "else \n");
            if (localoutputs != null) {
                if (compressoutputs) {
                    fw.write("  curl -k -F uid=" + userID + " -F jobid=" + jobID + " -F status=1 -F upload1=@" + compressedlocalout + " " + callbackStatusServletURL + " 2> /dev/null 1> /dev/null \n");
                    fw.write("  upl=$? \n "
                           + "  if test $upl -ne 0 \n then \n "
                           + "    echo '- STATUS CALLBACK: CAN NOT SEND COMPRESSED OUTPUTS'>> gridnfo.log \n "
                           + "    $1= '5' \n "
                           + "  else \n "
                           + "    rm -f ./" + compressedlocalout + "  \n"
                           + "  fi \n");
                } else {
                    for (int i = 0; i < localoutputs.size(); i++) {
                        fw.write("  curl -k -F uid=" + userID + " -F jobid=" + jobID + " -F status=1 -F upload" + i + "=@" + localoutputs.get(i) + " " + callbackStatusServletURL + " 2> /dev/null 1> /dev/null \n");
                    }
                }
            }
            //send finished signal (finished status 6 or 7)  exit=$status
            fw.write("  curl -k -F uid=" + userID + " -F jobid=" + jobID + " -F status=$1 " + callbackStatusServletURL + " 2> /dev/null 1> /dev/null \n"
                    + "fi \n"
                    + "} \n\n");                              

            //script starts
            //send start signal (running status 5)
            fw.write("curl -k -F uid=" + userID + " -F jobid=" + jobID + " -F status=5 " + callbackStatusServletURL + " 2> /dev/null 1> /dev/null \n");
            fw.write("callback=$? \n ");
            fw.write("if test $callback -ne 0 \n then \n "
                    + "  echo '- STATUS CALLBACK: CAN NOT SEND OUTPUTS'>> gridnfo.log \n "
                    + //                    "  if ( wget --no-check-certificate -q "+JRE_URL+"curl ) then \n  " +//curl downloaded
                    //                    "      callback=0 \n" +
                    //                    "      chmod +x ./curl \n " +
                    //                    "       pre='./' \n" +
                    //                    "   else \n"+
                    "      if (! wget --no-check-certificate -O /dev/null -t 2 -T 5 '" + callbackStatusServletURL + "?uid=" + userID + "&jobid=" + jobID + "&status=55' 2> /dev/null 1> /dev/null ) then \n" +//2> /dev/null 1> /dev/null
                    "       echo '- STATUS CALLBACK: CAN NOT SEND STATUS'>> gridnfo.log \n "
                    + "      fi \n"
                    + //                    "   fi \n"+
                    "fi \n");
        }                       
        
        fw.flush();
    }

/**
 * Adds lcgupload function, usage:
 * lcgupload  1:s_lfn 2:internalfilename 3:voname 4:se
 * Adds lcgdownload function, usage:
 * lcgdownload  1:voname 2:jc.getRInHash().get(rinput) 3:rinput
 * @throws java.io.IOException
 */
    private void addFunctionsLCGRemoteIO() throws IOException{
// lcgupload  1:s_lfn 2:internalfilename 3:voname 4:se

            fw.write("function lcgrealupload()  \n"
                    + "{ \n"
                    + "succ=false \n"
                    + "for (( t = 1; t <= 2; t++ )) \n"
                    + "do \n"
                    + "  dolog \"$t. try to upload remote output ($2): $1 :\" \n"
                    + "  lfc-ls ${1/lfn:/} &> /dev/null \n"
                    + "  if [ $? = 0 ]; then \n"
                    + "        dolog 'The remote file exists, we are about to remove it...' \n"
                    + "        GUID=`lcg-lg $1 2> /dev/null` \n"
                    + "        REPS=`lcg-lr $1 2> /dev/null` \n"
                    + "        for i in $REPS; do \n"
                    + "            lcg-del $i &> /dev/null \n"
                    + "            lcg-uf $GUID $i &> /dev/null \n"
                    + "        done \n"
                    + "        lcg-del -a $1 &> /dev/null \n"
                    + "        lfc-rm ${1/lfn:/} &> /dev/null \n"
                    + "  fi \n"
                    + "  if ( lcg-cr --vo $3 $4 -l $1 file:`pwd`/$2  >> gridnfo.log ) then \n"
                    + "     if (lcg-lr --vo $3 $1 >> gridnfo.log) then \n"
                    + "       succ=true \n"
                    + "       t=10 \n"
                    + "     else \n"
                    + "       dolog 'Error. Sleep 3 sec.' \n"
                    + "      sleep 3 \n"
                    + "    fi \n"
                    + "  else \n"
                    + "    dolog 'Error. Sleep 3 sec...' \n"
                    + "    sleep 3 \n"
                    + "  fi \n"
                    + "done \n"
                    + "if ($succ) then \n"
                    + "    return 0 \n"
                    + "else \n"
                    + "    return 1 \n"
                    + "fi \n"
                    + "} \n");

            fw.write("function lcgupload()  \n"
                    + "{ \n"
                    + "success=1 \n"//false
                    + "if [ -e `pwd`/$2 ]; then \n"
                        + "   lcgrealupload $1 $2 $3 \"$4\"  \n"
                        + "   success=$? \n"
                    + "else \n"//nincs meg a file, lehet, h generator?
                    +"\n"
                    + "if [ -e `pwd`/$2_0 ]; then \n"
                        + "PID=0 \n"
                        + "while [  -f \"./$2_${PID}\" ] \n"
                        + "do \n"
                        + "   lcgrealupload $1_${PID} $2_${PID} $3 \"$4\" \n"
                        + "   success=$? \n"
                        + "   PID=`expr $PID + 1` \n"
                        + "done \n"
                        + "echo \"Generator: number of remote output ${OUT}:\"${PID} >> gridnfo.log \n"
                        //+ "echo ${PID}>$2\"_PID\" \n \n"
                        + "echo $2#${PID}>>\"REMOTEGENERATORS_PID\" \n \n"
                    + "else \n"
                    + "  dolog \"$2: No such file or directory\" \n"
                    + "fi \n"
                    + "fi \n"
                    //+ "echo $success \" upload success\" \n"
                    + "if (test $success -eq 0) then \n"
                    + "    dolog ' ' \n"
                    + "else \n"
                    + "    ext 1 \n"
                    + "fi \n"
                    + "} \n");
//                out.write("if (! lcg-cr --vo " + voname + jc.getROutHostHash().get(routput) + " -l " + s_lfn + " file:`pwd`/" + routput + "  >> gridnfo.log ) then \n  ext \n fi \n");

//lcgdownload  1:voname 2:jc.getRInHash().get(rinput) 3:rinput
            fw.write("function lcgdownload() \n"
                    + "{ \n"
                    + "success=false \n"
                    + "for (( t = 1; t <= 3; t++ )) \n"
                    + "do \n"
                    + " if ( lcg-cp --vo $1 $2 file:`pwd`/$3 ) then \n"
                    + "   if ( test  $t -gt 1) then \n"
                    + "    dolog \"$t. try: $3 successfully downloaded. \" \n"
                    + "   fi \n"
                    + "   success=true \n"
                    + "   t=10 \n"
                    + "else \n"
                    + "       dolog \"$t. try to download $3 as remote input $2 failed. Sleep 3 sec. \" \n"
                    + "      sleep 3 \n"
                    + " fi \n"
                    + "done \n"
                    + "if ($success) then \n"
                    + "  sleep 0 \n"
                    + "else \n"
                    + "ext 1 \n"
                    + "fi \n"
                    + "} \n");
            fw.flush();
    }
/**
 * globus-url-copy file:`pwd`/"+t.getFileName()+" "+t.getTarget().getURI()+"
 * @throws java.io.IOException
 */
private void addFunctionsGSIFTPremoteIO() throws IOException{
            fw.write("function gsiftpupload()  \n"
                    + "{ \n"
                    + "success=1 \n"//false
                    + "if [ -e `pwd`/$2 ]; then \n"
                        + "   globus-url-copy file:`pwd`/$2 $1 \n"
                        + "   success=$? \n"
                    + "else \n"//nincs meg a file, lehet, h generator?
                    +"\n"
                    + "if [ -e `pwd`/$2_0 ]; then \n"
                        + "PID=0 \n"
                        + "while [  -f \"./$2_${PID}\" ] \n"
                        + "do \n"
                        + "   globus-url-copy file:`pwd`/$2_${PID} $1_${PID} \n"
                        + "   success=$? \n"
                        + "   PID=`expr $PID + 1` \n"
                        + "done \n"
                        + "echo \"Generator: number of remote output $2}:\"${PID} >> gridnfo.log \n"
                        //+ "echo ${PID}>$2\"_PID\" \n \n"
                        + "echo $2#${PID}>>\"REMOTEGENERATORS_PID\" \n \n"
                    + "else \n"
                    + "  dolog \"$2: No such file or directory\" \n"
                    + "fi \n"
                    + "fi \n"
                    //+ "echo $success \" upload success\" \n"
                    + "if (test $success -eq 0) then \n"
                    + "    dolog ' ' \n"
                    + "else \n"
                    + "    ext 1 \n"
                    + "fi \n"
                    + "} \n");
}

/**
 * LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$PWD
 * export LD_LIBRARY_PATH
 * @throws java.io.IOException file writing error
 */
    public void export_LD_LIBRARY_PATH() throws IOException {
        fw.write("LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$PWD\n");
        fw.write("export LD_LIBRARY_PATH\n");
        fw.flush();
    }

/**
 * Exports specified enviroment variables
 * @param env key=the name of the variable, value= value of the variable
 * @throws java.io.IOException file writing error
 */
    public void export(HashMap env) throws IOException {

    }

    public void extractAndDelete(String filename) throws IOException{
        //fw.write("unzip -q "+filename+" \n");
        fw.write("tar -xzf "+filename+" \n");//-k, --keep-old-files keep existing files; don’t overwrite them from archive
        fw.write("rm -f ./"+filename+" \n");
        fw.flush();
    }

    public void compressFiles(String compressedfile, String compressfiles) throws IOException{
        fw.write("tar -czf " + compressedfile + " " + compressfiles + " \n");
        fw.flush();
    }

/**
 * @param pJREUrl url of the compressed jre (http://test.com/jre/jre.tgz) If null or "" or "http://test.com/jre/", jre downloading is disabled, only the localy installed java env can be used.
 * set java environment
 * @throws java.io.IOException file irasi hiba
 */
    public void setJavaEnviroments(String pJREUrl) throws IOException { 
        String pJRE = null;
        try {
            if (pJREUrl != null && !"".equals(pJREUrl.trim()) && !pJREUrl.endsWith("/")) {
                String[] urlparts = pJREUrl.split("/");
                pJRE = urlparts[urlparts.length-1];
            }
        } catch (Exception e) {
        }
        

        fw.write("LOCALCLASSPATH=$CLASSPATH\n");
        fw.write("for JAR in *.jar; do\n");
        fw.write("LOCALCLASSPATH=\"$LOCALCLASSPATH:$JAR\"\n");
        fw.write("done\n");

        fw.write("export CLASSPATH=$LOCALCLASSPATH\n");

        fw.write("java -version 1>gridnfo.log 2>gridnfo.log  \n");
        fw.write("pjava=$?  \n");        
        fw.write("if test $pjava -ne 0  \n");
        fw.write("then \n");
        if (pJRE != null){//download java
        fw.write("mkdir java \n");
        fw.write("cd java \n");
        fw.write("if ( ! wget --no-check-certificate -q " + pJREUrl + " ) then \n wget --no-check-certificate -nv " + pJREUrl + " \n fi \n");
        fw.write("if ( ! tar -xvzf " + pJRE + " >> /dev/null ) then \n echo Unable to setup JVM >>stderr.log \n fi \n");
        fw.write("rm -f ./" + pJRE + "  \n");
        fw.write("cd .. \n");
        fw.write("export JAVA_HOME=\"`pwd`\"/java \n");
        fw.write("export PATH=$JAVA_HOME:$JAVA_HOME/bin:$PATH \n");
        fw.write("java -version 1>gridnfo.log 2>gridnfo.log \n");
         } else{
            fw.write("echo 'JVM is not found on the selected remote resource.' >>stderr.log \n " +
                    "echo 'The DCI Bridge is able to send a JVM. However this service is switched of.' >>stderr.log \n " +
                    "echo 'Either select a proper remote resource,' >>stderr.log \n " +
                    "echo 'or ask the system administrator of the DCI Bridge for enabling the JVM transfer. \n' >>stderr.log \n");
         }
        fw.write("fi \n");
       
    }

/**
 * run Java job
 * @param pBin binary name relativ to workdir
 * @param pParams command line parameters
 * @param pStdErr std error name relativ to workdir
 * @param pStdOut std out name relativ to workdir
 * @throws java.io.IOException file writing error
 */
    public void runJava(String pBin, String pParams, String pStdOut, String pStdErr) throws IOException {
        //fw.write("java -jar " + pBin + " " + pParams + " >" + pStdOut + " 2>" + pStdErr + " \n");
        fw.write("echo '- Exe started at          :' `date` >> gridnfo.log \n");
        if (pParams != null) {
            fw.write("java -jar " + pBin + " " + pParams + " \n");
        } else {
            fw.write("java -jar " + pBin + " \n");
        }
        fw.write("exitc=$? \n");
        fw.write("echo '- Exe finished at         :' `date` >> gridnfo.log \n");
        fw.write("echo '- The exit code of the exe:' $exitc >> gridnfo.log \n");
        fw.write("rm -f -r ./java \n");
        fw.write("if test $exitc -ne 0 \n then \n ext 1 \n fi \n");
        fw.flush();
    }

/**
 * Command generation for binary program running 
 * @param pBin binary name relativ to workdir
 * @param pParams command line parameters
 * @param pStdErr std error name relativ to workdir
 * @param pStdOut std out name relativ to workdir
 * @throws java.io.IOException file writing error
 */
    public void runBinary(String pBin,String pParams, String pStdOut, String pStdErr)throws IOException{
                fw.write("if (! chmod +x ./" + pBin + " ) then \n ext 1 \n fi \n");
                fw.write("echo '- Exe started at          :' `date` >> gridnfo.log \n");
                if (pParams != null) {
                    fw.write("./" + pBin + " " + pParams + " \n");
                } else {
                    fw.write("./" + pBin + " \n");
                }
                fw.write("exitc=$? \n");
                fw.write("echo '- Exe finished at         :' `date` >> gridnfo.log \n");
                fw.write("echo '- The exit code of the exe:' $exitc >> gridnfo.log \n");
                fw.write("if test $exitc -ne 0 \n then \n ext $exitc \n fi \n");
                fw.flush();
    }

/**
 * set mpi environment and run mpi job, try default, or mpirun
 * @param pBin binary name relativ to workdir
 * @param pParams command line parameters
 * @param pStdErr std error name relativ to workdir
 * @param pStdOut std out name relativ to workdir
 * @param pNodenumber nodenumber
 * @throws java.io.IOException file writing error
 */
//    public void runMPI(String pBin,String pParams, String pStdOut, String pStdErr, String pNodenumber)throws IOException{
//                fw.write("if (! chmod +x ./" + pBin + " ) then \n ext 1 \n fi \n");
//
//                fw.write("MPIRUN=\"$GLOBUS_SH_MPIRUN\" \n if [ -z \"$MPIRUN\" ]; then \n MPIRUN=mpirun \n fi \n");//echo $MPIRUN \n
//                if (pParams != null) {
//                    fw.write("$MPIRUN -np " + pNodenumber + " -machinefile $PBS_NODEFILE ./" + pBin + " " + pParams + " @0 \n");
//
//                } else {
//                    fw.write(" $MPIRUN -np " + pNodenumber + " -machinefile $PBS_NODEFILE ./" + pBin + " @0  \n");
//                }
//                fw.write("exitc=$? \n echo '- The exit code of the exe:' $exitc >> gridnfo.log \n");
//                fw.write("if test $exitc -ne 0 \n then \n ext $exitc \n fi \n");
//                fw.flush();
//    }

public void runMPI(String pBin,String pParams, String pStdOut, String pStdErr, String pNodenumber)throws IOException{
                fw.write("if (! chmod +x ./" + pBin + " ) then \n ext 1 \n fi \n");

                if (pParams != null) {
                    fw.write("mpirun -np "+pNodenumber+" ./" + pBin + " " + pParams + " \n");
                } else {
                    fw.write("mpirun -np "+pNodenumber+" ./" + pBin + " \n");
                }
                fw.write("exitc=$? \n echo '- The exit code of the exe:' $exitc >> gridnfo.log \n");
                fw.write("if test $exitc -ne 0 \n then \n ext $exitc \n fi \n");
                fw.flush();
    }    

/**
 * set mpi environment, copy the input files to the worker nodes and run mpi job, try default, or mpirun
 * for gt2/gt4
 * @param pBin binary name relativ to workdir
 * @param pParams command line parameters
 * @param pStdErr std error name relativ to workdir
 * @param pStdOut std out name relativ to workdir
 * @param pNodenumber nodenumber
 * @param pGridjobdir (jobid)
 * @throws java.io.IOException file writing error
 */
    public void runMPIallocatenodes(String pBin,String pParams, String pStdOut, String pStdErr, String pNodenumber, String pGridjobdir)throws IOException{
                fw.write("if (! chmod +x ./" + pBin + " ) then \n ext 1 \n fi \n");

                fw.write("MPIRUN=\"$GLOBUS_SH_MPIRUN\" \n if [ -z \"$MPIRUN\" ]; then \n MPIRUN=mpirun \n fi \n");//echo $MPIRUN \n
                      //("MPIRUN=\"$GLOBUS_SH_MPIRUN\" \n if [ -z \"$MPIRUN\" ]; then \n MPIRUN=mpirun \n echo $MPIRUN \n fi \n");
                
                fw.write("for i in `cat $PBS_NODEFILE` ; do \n echo \"Mirroring via SSH to $i\" \n");
                //	# creates the working directories on all the nodes allocated for parallel execution.
                fw.write("ssh $i mkdir -p `pwd` \n");
                //	# copies the needed files on all the nodes allocated for parallel execution.
                fw.write("/usr/bin/scp -rp ./"+pGridjobdir+"/* $i:`pwd` \n");
                //	# checks that all files are present on all the nodes allocated for parallel execution.
                fw.write("ssh $i ls `pwd` \n ssh $i chmod 755 "+pBin+" \n done \n");
                fw.write("chmod 755 "+pBin+" \n cat $PBS_NODEFILE \n");                
                if (pParams != null) {
                    fw.write(" $MPIRUN -np " + pNodenumber + " -machinefile $PBS_NODEFILE ./" + pBin + " " + pParams + " @0  \n");
                } else {
                    fw.write(" $MPIRUN -np " + pNodenumber + " -machinefile $PBS_NODEFILE ./" + pBin + " @0  \n");
                }
                fw.write("exitc=$? \n echo '- The exit code of the exe:' $exitc >> gridnfo.log \n");
                fw.write("if test $exitc -ne 0 \n then \n ext $exitc \n fi \n");
                
                //copy the local output files
                for (int i = 0; i < localoutputs.size(); i++) {
                    fw.write("scp ./" + localoutputs.get(i) + " $i:./" + pGridjobdir + "/" + localoutputs.get(i) + "  \n");
                }

                
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

    public void setMPIEnviroments() throws IOException{}
    public void downloadRemoteInputs(List<String> pValues) throws IOException{}
    public void uploadRemoteOutputs(List<String> pValues) throws IOException{}
    public void uncompressCollectors(List<String> pValues) throws IOException{}
    public void compressGenerators(List<String> pValues) throws IOException{}

    /**
     * Writes to the wrapper, and adds '\n'
     * @param line
     * @throws java.io.IOException
     */
    public void writeln(String line) throws IOException {
        fw.write(line + " \n");
    }
    
    /**
     * Finishes the wrapper script, print succes to gridnfo, is success,
     * compress/upload the outputs, if needed.
     * Close the FileWriter.
     */
    public void close() throws IOException {

        fw.write("echo '- Wrapper script finished succesfully '>> gridnfo.log \n nfo \n");
        if (compressoutputs){
            fw.write("compressouts \n");
        }        
        if (callback) {
            fw.write("uploadouts '6' \n");
        }
        fw.write("exit 0 \n");
        fw.flush();
        fw.close();
    }

}

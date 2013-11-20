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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.dcibridge.util;

import java.io.IOException;

/**
 *
 * @author akos
 */
public class LinuxWrapperForArc extends LinuxWrapperForGrids {

    public LinuxWrapperForArc(String pPath) throws Exception{
        path=pPath;
        createFile(path);
    }

    /**
 * add dolog() ext() and nfo() functions
 * and uploadouts(), if callabck is enabled
 * and compressouts(), if local output file compression is enabled.
 * Use setLocalOutsAndCallback before calling this function.
 * @param pNewFunctions if not NULL, add private functions to shell script
 * @param builtInExtrafunctions supported: FUNCTION_LCGREMOTEIO
 * @throws java.io.IOException file irasi hiba
 */
//    public void addFunctionsAndStartScript(String pNewFunctions, int builtInExtrafunctions) throws IOException {
//        fw.write("function dolog()  \n" +
//                "{  \n" +
//                "if [ $# -eq 0 ]; then \n" +
//                "    cat >> gridnfo.log \n" +
//                "else \n" +
//                "    echo \"$@\" >> gridnfo.log  \n" +
//                "fi \n" +
//                "} \n");
//        if (compressoutputs){//compress local outputs
//            fw.write("function compressouts() \n{ \n");
//            String coutfiles = "";
//            for (int i = 0; i < localoutputs.size(); i++) {
//                coutfiles+=localoutputs.get(i)+"* ";
//            }
//            fw.write("if [ -e `pwd`/REMOTEGENERATORS_PID ]; then \n");
//            compressFiles(compressedlocalout, "REMOTEGENERATORS_PID* " + coutfiles);
//            fw.write("else \n");//nincs remote generator
//            compressFiles(compressedlocalout, coutfiles);
//            fw.write("fi \n");
//            fw.write("} \n\n");
//        }
//        fw.write("function ext() \n");
//        fw.write("{ \n");
//        fw.write("echo '- An error occured!' >> gridnfo.log \n");
//        fw.write("nfo \n ");
//        if (compressoutputs) {
//            fw.write("compressouts \n");
//        }
//
//        fw.write("exit $1 \n");
//        fw.write("} \n");
//        fw.write("function nfo() \n");
//        fw.write("{ \n");
//        fw.write("echo '- Ran on host (hostname)  :' `hostname` >> gridnfo.log \n");
//        fw.write("echo '- Directory list (ls -la) : - - - - -' >> gridnfo.log \n");
//        fw.write("ls -la >> gridnfo.log \n");
//        fw.write("echo '- Environment var. (env): - - - - - -'>> gridnfo.log \n");
//        fw.write("env >> gridnfo.log \n");
//        fw.write("} \n");
//
//        if (pNewFunctions != null){
//            fw.write(pNewFunctions);
//        }
//
//
//        fw.flush();
//    }
public void runMPI(String pBin,String nodeNumber,String pParams, String pStdOut, String pStdErr)throws IOException{
                fw.write("if (! chmod +x ./" + pBin + " ) then \n ext 1 \n fi \n");

                if (pParams != null) {
                    fw.write("mpirun -np "+nodeNumber+" ./" + pBin + " " + pParams + " \n");
                } else {
                    fw.write("mpirun -np "+nodeNumber+" ./" + pBin + " \n");
                }
                fw.write("exitc=$? \n echo '- The exit code of the exe:' $exitc >> gridnfo.log \n");
                fw.write("if test $exitc -ne 0 \n then \n ext $exitc \n fi \n");
                fw.flush();
    }

}

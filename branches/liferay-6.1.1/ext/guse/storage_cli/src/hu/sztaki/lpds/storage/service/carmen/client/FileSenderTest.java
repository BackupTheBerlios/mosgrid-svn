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
package hu.sztaki.lpds.storage.service.carmen.client;

import java.util.Hashtable;

/**
 * Calls the FileSender class for testing purposes
 *
 * @author lpds
 */
public class FileSenderTest {
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // System.out.println("run...");
        try {
            Hashtable copyHash = new Hashtable();
            copyHash.put("/copyworkflow1/copyjob1/outputs/copyRtID1/copynewfilename1.txtcopy", "newFileName1.txt");
            copyHash.put("/copyworkflow2/copyjob2/outputs/copyRtID2/copynewfilename2.txtcopy", "newFileName2.txt");
            FileSender fileSender = new FileSender("http://localhost:8080/storage/receiver");
            fileSender.setParameters("/home/user/1/zip", "testportal", "testuser", "testworkflow", "testjob", "37", "testruntime2", copyHash);
            boolean localMode = true;
            fileSender.sendFiles(localMode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("end...");
    }
    
}

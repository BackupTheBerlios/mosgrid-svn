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
 * Calls the FileGetter class for testing purposes
 * 
 * @author lpds
 */
public class FileGetterTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // System.out.println("run...");
        try {
            FileGetter fileGetter = new FileGetter("http://localhost:9080/storage/receiver");
            // create fileRenameHash
            Hashtable fileRenameHash = new Hashtable();
            // newname, oldname
            fileRenameHash.put("newFileName1.txt", "/testworkflow1/job1/inputs/1/file1.txt");
            fileRenameHash.put("newFileName1-1.txt", "/testworkflow1/job1/inputs/1/file1.txt");
            fileRenameHash.put("newFileName1-2.txt", "/testworkflow1/job1/inputs/1/file1.txt");
            fileRenameHash.put("newFileName2.txt", "testworkflow2/job2/inputs/2/file2.txt");
            fileRenameHash.put("newFileName3.txt", "testworkflow3/job3/inputs/3/file3.txt");
            fileRenameHash.put("newFileName4.txt", "testworkflow3/job4/inputs/3/file4.txt");
            fileRenameHash.put("newFileName5.txt", "testworkflow5/job4/inputs/3/file5.txt");
            fileRenameHash.put("newFileName6.txt", "testworkflow6/job3/inputs/6/file6.txt");
            fileRenameHash.put("newFileName7.txt", "testworkflow7/job3/inputs/7/file7.txt");
            fileRenameHash.put("new-execute.bin", "testworkflow2/job2/execute.bin");
            fileGetter.setParameters("/home/user/1/get", "http://localhost:8080/portal30", "testuser", fileRenameHash);
            boolean localMode = true;            
            boolean resp = fileGetter.getFiles(localMode);
            // System.out.println("resp: " + resp);

            /*
            // speed test start
            // long start = System.currentTimeMillis();
            // System.out.println("start: " + start);
            // for (int i = 0; i < 10; i++) {
            //    String randID = "d_" + i;//+ Math.random();
            //    FileUtils.getInstance().createDirectory("/home/user/1/aaaaaaaaaaaaaaaaaaaa/" + randID);
            // }
            // speed test stop
            // long stop = System.currentTimeMillis();
            // System.out.println("stop: " + stop);
            // System.out.println("stop-start: " + (stop - start) + " msec");
            */

        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("end...");
    }

}

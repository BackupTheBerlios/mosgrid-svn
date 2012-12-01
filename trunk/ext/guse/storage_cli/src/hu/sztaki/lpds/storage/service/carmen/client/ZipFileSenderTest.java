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
 * Calls the ZipFileSender class for testing purposes
 * 
 * @author lpds
 */
public class ZipFileSenderTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // System.out.println("run...");
        try {
            ZipFileSender zipFileSender = new ZipFileSender("http://localhost:8080/storage");
            // fileSender.setParameters(zipFilePath, portalID, wfsID, userID);
            zipFileSender.setParameters("/home/user/1/zipUpload/1.zip", "http://andras.lpds.sztaki.hu:8080/portal30", "http://andras.lpds.sztaki.hu:8080/wfs", "root", "1", "2", "3");
            boolean localMode = true;
            zipFileSender.sendZipFile(localMode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println("end...");
    }

}

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
package hu.sztaki.lpds.storage.service.carmen.commons;

/**
 * FileUtils tester class
 *
 * @author lpds
 */
public class FileUtilsTest {
    
    /**
     * constructor
     */
    public FileUtilsTest() {
    }
    
    /**
     * Test method
     */
    public void test01() {
        try {
            //
            FileUtils.getInstance();
            Thread.sleep(2000);
            //
            // System.out.println();
            // System.out.println("Start test 01...");
            //
            FileUtils.getInstance().getNormalFromPidName("output.jpg_0");
            FileUtils.getInstance().getNormalFromPidName("output.jpg");
            FileUtils.getInstance().getNormalFromPidName("output_0");
            //
            FileUtils.getInstance().getNormalFromPidName("out_put.jpg_37");
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/**
 * Executable function
 * @param args CLI parameters
 */
    public static void main(String[] args) {
        new FileUtilsTest().test01();
    }
    
}

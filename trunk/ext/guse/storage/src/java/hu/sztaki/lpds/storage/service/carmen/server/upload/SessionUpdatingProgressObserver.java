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
package hu.sztaki.lpds.storage.service.carmen.server.upload;

import java.lang.ref.WeakReference;

import javax.servlet.http.HttpServletRequest;

/**
 * Monitors a file during its upload.
 *
 * @author lpds
 */
public class SessionUpdatingProgressObserver implements ProgressObserver {
    
    private WeakReference requestRef;
    
    private String fieldName;
    
    private String fileName;
/**
 * Constructor
 * @param requestRef reference
 * @param fieldName field name
 * @param fileName file name
 */
    public SessionUpdatingProgressObserver(WeakReference requestRef, String fieldName, String fileName) {
        this.requestRef = requestRef;
        this.fieldName = fieldName;
        this.fileName = fileName;
    }
    
    /**
     * Creates an entry to the uploadItems registry. (Notes the file name
     * and the current status of the upload in percentage).
     * @param progress the percentage of the uploaded data
     */
    public void setProgress(double progress) {
        // System.out.println("setProcess()");
        HttpServletRequest request = (HttpServletRequest) requestRef.get();
        if (request != null) {
            // System.out.println("request != null...");
            // System.out.println("in observer...");
            // System.out.println("fieldName: " + fieldName);
            // System.out.println("fileName : " + fileName);
            // System.out.println("progress : " + progress);
            Integer perCent = Integer.valueOf((int) Math.round(progress));
            // fieldName = "file_SID_randomID"
            String[] list = fieldName.split("_");
            String SID = new String("");
            String randomID = new String("");
            if (list.length >= 3) {
                // SID wacid biztonsagi szures
                String sidrow = list[1];
                if (sidrow.indexOf(";") > -1) {
                    SID = sidrow.split(";")[0];
                } else {
                    SID = sidrow;
                }
                randomID = list[2];
                // System.out.println("SID : " + SID);
                // System.out.println("randomID : " + randomID);
                UploadItemsList.getInstance().addFile(SID, fileName + "_" + randomID, perCent);
            }
        } else {
            // System.out.println("request == null...");
        }
    }
}

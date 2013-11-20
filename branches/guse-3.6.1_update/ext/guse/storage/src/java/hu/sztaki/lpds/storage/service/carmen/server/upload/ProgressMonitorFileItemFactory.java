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

import java.io.File;
import java.lang.ref.WeakReference;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 * Monitors a file during its upload.
 * 
 * @author lpds
 */
public class ProgressMonitorFileItemFactory extends DiskFileItemFactory {

    private File temporaryDirectory;

    private WeakReference requestRef;

    private long requestLength;
/**
 * Constructor
 * @param request http request
 */
    public ProgressMonitorFileItemFactory(HttpServletRequest request) {
        super();
        temporaryDirectory = (File) request.getSession().getServletContext().getAttribute("javax.servlet.context.tempdir");
        requestRef = new WeakReference(request);
        String contentLength = request.getHeader("content-length");
        if (contentLength != null) {
            requestLength = Long.parseLong(contentLength.trim());
        }
    }
/**
 *
 * @see DiskFileItemFactory#createItem(java.lang.String, java.lang.String, boolean, java.lang.String)
 */
    @Override
    public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName) {
        SessionUpdatingProgressObserver observer = null;
        // This must be a file upload.
        if (isFormField == false) {
            observer = new SessionUpdatingProgressObserver(requestRef, fieldName, fileName);
        }
        ProgressMonitorFileItem item = new ProgressMonitorFileItem(fieldName, contentType, isFormField, fileName, DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, temporaryDirectory, observer, requestLength);
        return item;
    }

}

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
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.fileupload.disk.DiskFileItem;

/**
 * Monitors a file during its upload.
 *
 * @author lpds
 */
public class ProgressMonitorFileItem extends DiskFileItem {
    
    private static final long serialVersionUID = 1L;
    
    private ProgressObserver observer;
    
    private long passedInFileSize;
    
    private long bytesRead;
    
    private boolean isFormField;
/**
 * Constructor
 * @param fieldName field name
 * @param contentType content type
 * @param isFormField true=text field
 * @param fileName file name
 * @param sizeThreshold size
 * @param repository repository
 * @param observer observer during upload
 * @param passedInFileSize -not used-
 */
    public ProgressMonitorFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold, File repository, ProgressObserver observer, long passedInFileSize) {
        super(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
        this.observer = observer;
        this.passedInFileSize = passedInFileSize;
        this.isFormField = isFormField;
    }
/**
 * @see DiskFileItem#getOutputStream()
 */
@Override
    public OutputStream getOutputStream() throws IOException {
        OutputStream baseOutputStream = super.getOutputStream();
        if (isFormField == false) {
            return new BytesCountingOutputStream(baseOutputStream);
        } else {
            return baseOutputStream;
        }
    }
    
    private class BytesCountingOutputStream extends OutputStream {
        
        private long previousProgressUpdate;
        
        private OutputStream base;
        
        public BytesCountingOutputStream(OutputStream ous) {
            base = ous;
            // begin
            // System.out.println("BytesCountingOutputStream begin");
            fireProgressEvent(0);
        }
        
        public void close() throws IOException {
            base.close();
            // end
            // System.out.println("BytesCountingOutputStream end");
            // fireProgressEvent(0);
        }
        
        public boolean equals(Object arg0) {
            return base.equals(arg0);
        }
        
        public void flush() throws IOException {
            base.flush();
        }
        
        public int hashCode() {
            return base.hashCode();
        }
        
        public String toString() {
            return base.toString();
        }
        
        public void write(byte[] bytes, int offset, int len) throws IOException {
            base.write(bytes, offset, len);
            fireProgressEvent(len);
        }
        
        public void write(byte[] bytes) throws IOException {
            base.write(bytes);
            fireProgressEvent(bytes.length);
        }
        
        public void write(int b) throws IOException {
            base.write(b);
            fireProgressEvent(1);
        }
        
        private void fireProgressEvent(int b) {
            bytesRead += b;
            // System.out.println("bytesRead : " + bytesRead);
            // System.out.println("previousProgressUpdate : " + previousProgressUpdate);
            // System.out.println("passedInFileSize : " + passedInFileSize);
            // old if (bytesRead - previousProgressUpdate > (passedInFileSize / 500.0)) {
            // old    observer.setProgress((((double) (bytesRead)) / passedInFileSize) * 100.0);
            // old    previousProgressUpdate = bytesRead;
            // old }
            observer.setProgress(((double) (bytesRead * 100.0)) / passedInFileSize);
            previousProgressUpdate = bytesRead;
        }
    }
    
}

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
package hu.sztaki.lpds.storage.service.carmen.server.receiver;

import java.io.File;
import java.io.IOException;

import com.oreilly.servlet.multipart.FileRenamePolicy;

/**
 * Own RenamePolicy (deletes the old file with the same name). 
 * 
 * @author lpds 
 */
public class MyFileRenamePolicy implements FileRenamePolicy {  

/**
 * Empty constructor
 */
    public MyFileRenamePolicy() {
    }

    /**
     * File rename. (the file will be overwritten).
     * @param f File descriptor
     * @return new file
     */
    public File rename(File f) {
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException io) {
            // io.printStackTrace();
        }
        return f;
    }

}

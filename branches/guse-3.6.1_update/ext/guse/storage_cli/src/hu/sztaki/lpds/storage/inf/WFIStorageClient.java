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
 * Definition of the communication between the WFI and the storage
 */

package hu.sztaki.lpds.storage.inf;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.storage.com.FileBean;
import hu.sztaki.lpds.storage.com.IfBean;

/**
 * @author krisztian
 */
public interface WFIStorageClient extends BaseCommunicationFace{
/**
 * File copy on the storage
 * @param pSRC source file
 * @param pDest target file
 * @throws java.lang.Exception copy error
 */
    public void copyFile(FileBean pSRC,FileBean pDest) throws Exception;
/**
 * Getting the number of files (and also the prefixes) of a given directory
 * @param pDirectory directory descriptor
 * @return number of files
 * @throws java.lang.Exception file process error
 */
    public long getNumberOfFileInDirectory(FileBean pDirectory) throws Exception;
/**
 * Test input file value
 * @param pValue description of test
 * @return result of the operation
 * @throws java.lang.Exception file process error
 */
    public boolean ifTest(IfBean pValue) throws Exception;
}

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
 * Xiaobo Yang, STFC e-Science Centre, Daresbury Laboratory, UK
 * Created: 30 April 2007
 * Last modified: 30 April 2007
 * 
 */
package hu.sztaki.lpds.portal.util.stream;

import java.text.NumberFormat;

import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author schandras
 */
public class FileUploadProgressListener implements ProgressListener
{
	private static Log log = LogFactory.getLog(FileUploadProgressListener.class);

	private static long bytesTransferred = 0;

	private static long fileSize = -100;

	private long tenKBRead = -1;

    /**
     * Empty constructor
     */
    public FileUploadProgressListener() {
	}

    /**
     * Getting the upload status
     * @return
     */
    public String getFileuploadstatus() {
		// per looks like 0% - 100%, remove % before submission
		String per = NumberFormat.getPercentInstance().format(
				(double) bytesTransferred / (double) fileSize);
		return per.substring(0, per.length() - 1);
	}

//    public long getTransferedbytes(){return bytesTransferred;}
//    public long getFilesize(){return (fileSize>0)?fileSize:0;}

@Override
    public void update(long bytesRead, long contentLength, int items) {
		// update bytesTransferred and fileSize (if required) every 10 KB is
		// read
		long tenKB = bytesRead / 10240;
		if (tenKBRead == tenKB)
			return;
		tenKBRead = tenKB;

		bytesTransferred = bytesRead;
		if (fileSize != contentLength)
			fileSize = contentLength;
	}

}

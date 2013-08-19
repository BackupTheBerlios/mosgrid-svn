package org.xtreemfs.portlet.ui;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.StreamResource;

public class FileDownloadResource extends StreamResource {


  private long size = 0;

  private static final long serialVersionUID = -8015602880299649458L;

  public FileDownloadResource(StreamSource streamSource, String filename, long fileSize, Application application) {
    super(streamSource, filename, application);
    this.size = fileSize;
  }


  @Override
  public DownloadStream getStream() {
    final DownloadStream ds = new DownloadStream(getStreamSource().getStream(), getMIMEType(), getFilename());
    ds.setCacheTime(getCacheTime());
    ds.setParameter("Content-Disposition", "attachment; filename=\"" +getFilename() + "\"");
    ds.setBufferSize(128*1024);
    ds.setCacheTime(getCacheTime());

    if (this.size > 0) {
      ds.setParameter("Content-Length", String.valueOf(this.size));
    }
    return ds;
  }

}

package org.xtreemfs.portlet.util.vaadin;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.StreamResource;

/**
 * A resource for File-downloads from XtreemFS
 *  
 * @author pschaefe
 *
 */
public class VaadinFileDownloadResource extends StreamResource {  

  private long size = 0;
  
  private static final long serialVersionUID = -8015602880299649458L;

  public VaadinFileDownloadResource(StreamSource streamSource, String filename, long fileSize, Application application) {
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
    
    if (size > 0) {
      ds.setParameter("Content-Length", String.valueOf(size));
    }
    return ds;
  }

}

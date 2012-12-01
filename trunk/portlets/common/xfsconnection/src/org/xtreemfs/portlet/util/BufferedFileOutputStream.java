package org.xtreemfs.portlet.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.xtreemfs.common.clients.File;
import org.xtreemfs.common.clients.RandomAccessFile;

/**
 * Ein Outputstream für XtreemFS-Files
 */
public class BufferedFileOutputStream extends ByteArrayOutputStream {
  
  protected File f;
  protected RandomAccessFile ra;  
  protected int count = 0;

  //128 Kbyte
  protected int blocksize = 2 * 128 * 1024;
  
  // buffers blocksize of data
  protected byte buf[] = new byte[blocksize]; 
  
  // current position in the buffer
  protected int buffercount = 0;

  public BufferedFileOutputStream (File f) throws IOException {
    this.f = f;
    open(f);
  }
  
  private void open(File f) throws IOException {
    this.ra = f.open("rw", 0700);
  }

  @Override
  public void close() throws IOException {
    super.close();
    flushBuffer();
    this.ra.close();
  }
  
  @Override
  public synchronized void reset() {
    super.reset();
    this.buffercount = 0;
    this.buf = new byte[blocksize];
    this.ra.seek(0);
    this.count = 0;
  }

  @Override
  public synchronized int size() {
    return this.count;
  }

  @Override
  public synchronized byte[] toByteArray() {
    byte[] content = new byte[count];
    try {
      ra.seek(0);
      ra.read(content, 0, count);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return content; 
  }

  /** Flush the internal buffer */
  private void flushBuffer() {
    try {
      if (buffercount > 0) {
        int c = this.ra.write(buf, 0, buffercount);
        buffercount = 0;
        if (c > 0) {
          this.count += c;
        }      
        // TODO what if writeerror: c < buffercount?
      }
    } catch (IOException e) {
      e.printStackTrace();
    }      
  }

  @Override
  public synchronized void write(byte[] b, int off, int len) {
    if (len >= buf.length) {
      /* If the request length exceeds the size of the output buffer,
      flush the output buffer and then write the data directly.
      In this way buffered streams will cascade harmlessly. */
      flushBuffer();
      try {
        // set offset
        this.ra.seek(off);
        
        // write 'len' bytes
        int c = this.ra.write(b, off, len);
        if (c > 0) {
          this.count += c;
        }      
      } catch (IOException e) {
        e.printStackTrace();
      }
      return;
    }    
    if (len > buf.length - buffercount) {
      flushBuffer();
    }
    System.arraycopy(b, off, buf, buffercount, len);
    buffercount += len;
  }

  public synchronized void write(int b)  {
    if (buffercount >= buf.length) {
      flushBuffer();
    }
    buf[count++] = (byte)b;
  }

  @Override
  public synchronized void writeTo(OutputStream out) throws IOException {
    ra.seek(0);
    int chunkSize = 8012*128;
    byte[] content = new byte[chunkSize];
    int i = 0;
    
    // Read data in chunks
    while ((i = ra.read(content, 0, chunkSize))>0) {
      out.write(content, 0, i);
    }
  }

  @Override
  public void flush() throws IOException {
    flushBuffer();
    this.ra.flush();
  }

}

package org.xtreemfs.portlet.util;

import java.io.IOException;
import java.io.InputStream;

import org.xtreemfs.common.clients.File;
import org.xtreemfs.common.clients.RandomAccessFile;

/**
 * Ein InputStream für XtreemFS-Files
 */
public class BufferedFileInputStream extends InputStream {

    protected File f;
    protected RandomAccessFile ra;
    protected int read = 0;
    
    // 128 Kbyte
    protected int blocksize = 128 * 1024;
    
    // buffers blocksize of data
    protected byte buf[] = new byte[blocksize]; 
    
    // current position in the buffer
    protected int bufferoffset = 0;
    protected int bufferlength = 0; 
    
    public BufferedFileInputStream (File f) throws IOException {
        this.f = f;
        open(f);
    }

    private void open(File f) throws IOException {
        this.ra = f.open("r", 0700);
    }
    
    @Override
    public int available() throws IOException {
        return (int)f.length() - read;
    }

    @Override
    public void close() throws IOException {
        this.ra.close();
    }

    private synchronized int fillBuffer(int off) {
      try {
        // set offset
        ra.seek(off);
        
        // read block of 'blocksize' size
        int r = ra.read(buf, 0, blocksize);
        this.bufferoffset = off;
        this.bufferlength = r;
        return r;
      } catch (IOException e) {
        e.printStackTrace();
      }
      return -1;
    }
    
    @Override
    public synchronized int read(byte[] b, final int off, final int len) throws IOException {
      int newLen = len;
      int newOff = off;

      // initially fill the buffer
      if (bufferlength == 0) {
        if (fillBuffer(newOff) <= 0) {
          return -1;
        }
      }
      
      while (newLen > 0) {

        // check if we already buffered relevant data
        if (newOff >= bufferoffset
            && newOff < bufferoffset + bufferlength) {          
          int pos = newOff-bufferoffset;          
          int end = Math.min(bufferlength-pos, newLen);
          System.arraycopy(buf, pos, b, off-newOff, end);         

          this.read += end;
          newOff += end;
          newLen -= end;
        }
        // get the new bytes
        else if (newOff >= bufferoffset + bufferlength){
          // stop if there are no more bytes to read
          if (fillBuffer(newOff) <= 0) {
            if (len - newLen == 0) {
              return -1;
            }
            else {
              return len-newLen;
            }
          }
        }
      }
      
      return len-newLen;
    }

    @Override
    public synchronized int read(byte[] b) throws IOException {
      return read(b, read, b.length);
   }

    @Override
    public synchronized void reset() throws IOException {
        this.ra.seek(0);
        this.read = 0;
    }

    @Override
    public synchronized int read() throws IOException {
      byte[] bytes = new byte[1];
      if (read(bytes, read, 1) > -1) {
        return bytes[0];
      }
      return -1;
    }

}

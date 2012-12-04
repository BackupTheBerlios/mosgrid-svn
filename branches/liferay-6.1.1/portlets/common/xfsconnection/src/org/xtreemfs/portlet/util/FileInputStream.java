package org.xtreemfs.portlet.util;

import java.io.IOException;
import java.io.InputStream;

import org.xtreemfs.common.clients.File;
import org.xtreemfs.common.clients.RandomAccessFile;

/**
 * Ein InputStream für XtreemFS-Files
 */
public class FileInputStream extends InputStream {

    protected File f;
    protected RandomAccessFile ra;
    protected int read = 0;
    
    public FileInputStream (File f) throws IOException {
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

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int r = ra.read(b, off, len);
        if (r > 0) {
            this.read += r;
            return r;
        }
        return -1; 
    }

    @Override
    public int read(byte[] b) throws IOException {
        int r = ra.read(b, 0, b.length);
        if (r > 0) {
            this.read += r;
            return r;
        }
        return -1;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.ra.seek(0);
        this.read = 0;
    }

    @Override
    public int read() throws IOException {
        
        int r = ra.read(new byte[1], 0, 1);
        if (r == 0) {
            return -1;
        }
        r++;
        return r;
    }

}

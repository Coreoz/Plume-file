package com.coreoz.plume.file.services.data;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An input stream wrapper that exposes a method to know
 * the number of bytes that has been read in the stream.
 */
public class MeasuredSizeInputStream extends FilterInputStream {
    private long inputStreamTotalSize;

    public MeasuredSizeInputStream(InputStream baseInputStream) {
        super(baseInputStream);
        this.inputStreamTotalSize = 0;
    }

    @Override
    public int read() throws IOException {
        int read = super.in.read();
        if (read != -1) {
            inputStreamTotalSize += 1;
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);
        if (read != -1) {
            inputStreamTotalSize += read;
        }
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        long result = this.in.skip(n);
        this.inputStreamTotalSize += result;
        return result;
    }

    /**
     * Returns the number of bytes that has been read
     */
    public long getInputStreamTotalSize() {
        return inputStreamTotalSize;
    }
}

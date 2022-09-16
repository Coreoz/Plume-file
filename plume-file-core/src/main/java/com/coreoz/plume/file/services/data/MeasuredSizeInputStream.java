package com.coreoz.plume.file.services.data;

import java.io.IOException;
import java.io.InputStream;

public class MeasuredSizeInputStream extends InputStream {
    private final InputStream baseInputStream;
    private long inputStreamTotalSize;

    public MeasuredSizeInputStream(InputStream baseInputStream) {
        this.baseInputStream = baseInputStream;
        this.inputStreamTotalSize = 0;
    }

    @Override
    public int read() throws IOException {
        int read = baseInputStream.read();
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

    public long getInputStreamTotalSize() {
        return inputStreamTotalSize;
    }
}

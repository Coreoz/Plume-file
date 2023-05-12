package com.coreoz.plume.file.services.mimetype;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * An InputStream wrapper that provides a peeking feature.
 * See {@link FileMimeTypeDetector} for usage
 */
public class PeekingInputStream {
    // This number comes from mime-types from method MimeTypeDetector.getMaxGetBytesLength() on version 1.0.4
    // When upgrading mime-types, this number should be recalculated to make sure there is no change
    private static final int BUFFER_SIZE = 18730;

    private final PushbackInputStream fileStream;

    public PeekingInputStream(InputStream fileStream) {
        this.fileStream = new PushbackInputStream(fileStream, BUFFER_SIZE);
    }

    public byte[] peek(int bytesToRead) throws IOException {
        byte[] bytesPeeked = fileStream.readNBytes(bytesToRead);
        this.fileStream.unread(bytesPeeked);
        return bytesPeeked;
    }

    public PushbackInputStream peekedStream() {
        return this.fileStream;
    }
}

package com.coreoz.plume.file.services.mimetype;

import org.overviewproject.mime_types.MimeTypeDetector;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * A {@link FileMimeTypeDetector} using the library mime-types.
 */
public class MimeTypesDetector implements FileMimeTypeDetector {
    private final MimeTypeDetector detector = new MimeTypeDetector();

    @Nullable
    @Override
    public String guessMimeType(String fileName, PeekingInputStream fileData) throws IOException {
        try {
            return detector.detectMimeType(
                fileName,
                () -> fileData.peek(detector.getMaxGetBytesLength())
            );
        } catch (Exception e) {
            if (e.getCause() != null) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                }
                throw new IOException(e.getCause());
            }
            throw new IOException(e);
        }
    }
}

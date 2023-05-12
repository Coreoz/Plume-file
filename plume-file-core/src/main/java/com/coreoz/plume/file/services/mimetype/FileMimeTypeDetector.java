package com.coreoz.plume.file.services.mimetype;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Provide mime type detection for Plume File.<br>
 * <br>
 * Default implementation uses the lightweight library mime-types.
 * This implementation can be easily replaced by Apache Tika if needed (though the dependency is much bigger!).
 */
public interface FileMimeTypeDetector {
    /**
     * Find the corresponding mime type for the file name.<br>
     * <br>
     * Usage:<br>
     * <pre>
     * {@code
     * InputStream fileStream = ... // First you need the initial file stream
     * PeekingInputStream filePeekingStream = new PeekingInputStream(fileStream);
     * String mimeType = guessMimeType(fileName, filePeekingStream);
     * otherOperationsWithStream(filePeekingStream.peekedStream());
     * }
     * </pre>
     * @param fileName the file name
     * @param fileData The file input stream data wrapped inside a PeekingInputStream
     * @return the mime type guessed, null if no one is found
     * @throws IOException if the base input stream cannot be read
     */
    @Nullable
    String guessMimeType(String fileName, PeekingInputStream fileData) throws IOException;
}

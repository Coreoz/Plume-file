package com.coreoz.plume.file.services.metadata;

import java.time.Instant;

public interface FileMetadata {
    String getUniqueName();
    String getFileOriginalName();
    String getFileType();
    String getFileExtension();
    String getMimeType();
    Long getFileSize();
    Instant getCreationDate();
}

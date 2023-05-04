package com.coreoz.plume.file.validator;

import lombok.Value;

/**
 * Contains file upload metadata.
 * It must be created using {@code FileUploadValidator.from(fileMetadata).validate[...].finish()}
 */
@Value
public class FileUploadMetadata {
    String fileName;
    String fileExtension;
    String mimeType;
    long fileSize;
    FileUploadMetadata(String fileName, String fileExtension, String mimeType, long fileSize) {
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
    }
}

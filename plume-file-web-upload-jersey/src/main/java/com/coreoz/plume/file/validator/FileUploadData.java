package com.coreoz.plume.file.validator;

import lombok.Value;

import java.io.InputStream;

/**
 * Contains file upload data.
 * It must be created using {@code FileUploadValidator.from(...).validate[...].finish()}
 */
@Value
public class FileUploadData {
    InputStream fileData;
    String fileName;
    String fileExtension;
    String mimeType;
    long fileSize;
    FileUploadData(InputStream fileData, String fileName, String fileExtension, String mimeType, long fileSize) {
        this.fileData = fileData;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
    }
}

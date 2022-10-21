package com.coreoz.plume.file.data;

import lombok.Value;

@Value(staticConstructor = "of")
public class FileUploadMetadata {
    String originalName;
    String mimeType;
    Long fileSize;
}

package com.coreoz.plume.file;

import com.coreoz.plume.file.data.FileUploadMetadata;
import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.filetype.FileType;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;

@Singleton
public class FileUploadWebJerseyService {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadWebJerseyService.class);
    private final FileService fileService;

    @Inject
    public FileUploadWebJerseyService(FileService fileService) {
        this.fileService = fileService;
    }

    public String add(
        FileType fileType,
        InputStream fileData,
        FormDataBodyPart formDataBodyPart
    ) throws IOException {
        logger.info("uploading file with type {}", fileType);
        FileUploadMetadata fileMetadata = FileUploadMetadata.of(
            formDataBodyPart.getContentDisposition().getFileName(),
            formDataBodyPart.getMediaType().toString(),
            formDataBodyPart.getFormDataContentDisposition().getSize()
        );
        checkFile(fileType, fileData);
        checkFileMetadata(fileMetadata);
        return this.fileService.add(
            fileType,
            fileData,
            fileMetadata.getOriginalName(),
            fileMetadata.getMimeType()
        );
    }

    private static void checkFile(FileType fileType, InputStream fileData) {
        if (fileType == null) {
            throw new RuntimeException("File must have a type");
        }
        if (fileData == null) {
            throw new RuntimeException("File stream must not be empty");
        }
    }

    private static void checkFileMetadata(FileUploadMetadata fileMetadata) {
        if (fileMetadata == null) {
            throw new RuntimeException("File must have metadata");
        }
        if (fileMetadata.getMimeType() == null) {
            throw new RuntimeException("File must have a mime type");
        }
    }
}

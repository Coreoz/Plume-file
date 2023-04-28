package com.coreoz.plume.file;

import com.coreoz.plume.file.data.FileUploadMetadata;
import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.validator.FileUploadValidators;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
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
    ) {
        logger.trace("Uploading file with type: {}", fileType);
        FileUploadMetadata fileMetadata = FileUploadMetadata.of(
            formDataBodyPart.getContentDisposition().getFileName(),
            formDataBodyPart.getMediaType().toString(),
            formDataBodyPart.getFormDataContentDisposition().getSize()
        );
        FileUploadValidators.verifyRequiredMetadata(fileMetadata);
        return this.fileService.add(
            fileType,
            fileData,
            // TODO à modifier en FileNames.clean(fileMetadata.getOriginalName())
            fileMetadata.getOriginalName(),
            fileMetadata.getMimeType()
        );
    }
}

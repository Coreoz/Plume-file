package com.coreoz.plume.file;

import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.utils.FileNames;
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
        return this.fileService.add(
            fileType,
            fileData,
            FileNames.cleanFileName(formDataBodyPart.getContentDisposition().getFileName()),
            formDataBodyPart.getMediaType().toString()
        );
    }
}

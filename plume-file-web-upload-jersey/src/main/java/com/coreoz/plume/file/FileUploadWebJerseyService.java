package com.coreoz.plume.file;

import com.coreoz.plume.file.data.FileUploadMetadata;
import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.filetype.FileType;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;

// TODO proposer une classe FileUploadValidators en plus qui va contenir
//  - De quoi valider la taille maximale d'un fichier (en se basant sur la taille retournée par Jersey)
//  - De quoi valider les extensions autorisées d'un fichier
//  - De quoi valider la longueur des noms de fichier
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
        logger.trace("uploading file with type {}", fileType);
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

    // TODO à mettre dans une classe static dédiée : FileUploadValidators
    // TODO méthode à supprimer pour intégrer directement dans add(...)
    private static void checkFile(FileType fileType, InputStream fileData) {
        // TODO remplacer par Guava : Objects.requireNonNull()
        if (fileType == null) {
            throw new RuntimeException("File must have a type");
        }
        if (fileData == null) {
            throw new RuntimeException("File stream must not be empty");
        }
    }

    // TODO à renommer en verifyRequiredMetadata()
    private static void checkFileMetadata(FileUploadMetadata fileMetadata) {
        // TODO remplacer par Guava : Objects.requireNonNull()
        if (fileMetadata == null) {
            throw new RuntimeException("File must have metadata");
        }
        if (fileMetadata.getMimeType() == null) {
            throw new RuntimeException("File must have a mime type");
        }
    }
}

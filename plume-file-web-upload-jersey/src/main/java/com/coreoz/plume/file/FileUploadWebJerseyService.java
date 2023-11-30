package com.coreoz.plume.file;

import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.validator.FileUploadData;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FileUploadWebJerseyService {
    private final FileService fileService;

    @Inject
    public FileUploadWebJerseyService(FileService fileService) {
        this.fileService = fileService;
    }

    public String add(
        FileType fileType,
        FileUploadData fileUploadData
    ) {
        return this.fileService.add(
            fileType,
            fileUploadData.getFileData(),
            fileUploadData.getFileName(),
            fileUploadData.getMimeType()
        );
    }
}

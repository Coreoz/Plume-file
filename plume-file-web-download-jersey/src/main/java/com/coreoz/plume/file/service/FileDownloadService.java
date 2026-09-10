package com.coreoz.plume.file.service;

import com.coreoz.plume.file.services.FileService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@Deprecated(forRemoval = true)
public class FileDownloadService extends com.coreoz.plume.file.download.jersey.FileDownloadService implements FileDownloadJerseyService {

    @Inject
    public FileDownloadService(FileService fileService) {
        super(fileService);
    }
}

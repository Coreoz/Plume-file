package com.coreoz.plume.file.service;

import com.coreoz.plume.file.services.FileService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * @deprecated For removal in V5
 * Call {@link com.coreoz.plume.file.download.jersey.FileDownloadService} instead
 */
@Singleton
@Deprecated(forRemoval = true)
public class FileDownloadService extends com.coreoz.plume.file.download.jersey.FileDownloadService implements FileDownloadJerseyService {

    @Inject
    public FileDownloadService(FileService fileService) {
        super(fileService);
    }
}

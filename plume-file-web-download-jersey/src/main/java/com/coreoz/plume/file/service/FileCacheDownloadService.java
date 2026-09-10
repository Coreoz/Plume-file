package com.coreoz.plume.file.service;

import com.coreoz.plume.file.cache.FileCacheService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@Deprecated(forRemoval = true)
public class FileCacheDownloadService extends com.coreoz.plume.file.download.jersey.FileCacheDownloadService implements FileDownloadJerseyService {

    @Inject
    public FileCacheDownloadService(FileCacheService fileCacheService) {
        super(fileCacheService);
    }
}

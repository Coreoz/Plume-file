package com.coreoz.plume.file.service;

import com.coreoz.plume.file.cache.FileCacheService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * @deprecated For removal in V5
 * Call {@link com.coreoz.plume.file.download.jersey.FileCacheDownloadService} instead
 */
@Singleton
@Deprecated(forRemoval = true)
public class FileCacheDownloadService extends com.coreoz.plume.file.download.jersey.FileCacheDownloadService implements FileDownloadJerseyService {

    @Inject
    public FileCacheDownloadService(FileCacheService fileCacheService) {
        super(fileCacheService);
    }
}

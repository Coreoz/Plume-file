package com.coreoz.plume.file.cache;

import com.coreoz.plume.file.service.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.file.services.FileService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@Deprecated(forRemoval = true)
public class FileCacheServiceGuava extends com.coreoz.plume.file.download.jersey.cache.FileCacheServiceGuava implements FileCacheService {

    @Inject
    public FileCacheServiceGuava(FileService fileService, FileDownloadConfigurationService configurationService) {
        super(fileService, configurationService);
    }
}

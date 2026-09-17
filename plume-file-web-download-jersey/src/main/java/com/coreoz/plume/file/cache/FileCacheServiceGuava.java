package com.coreoz.plume.file.cache;

import com.coreoz.plume.file.download.jersey.cache.FileCacheConfigurationService;
import com.coreoz.plume.file.services.FileService;
import com.typesafe.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * @deprecated For removal in V5
 * Call {@link com.coreoz.plume.file.download.jersey.cache.FileCacheService} instead
 */
@Singleton
@Deprecated(forRemoval = true)
public class FileCacheServiceGuava extends com.coreoz.plume.file.download.jersey.cache.FileCacheServiceGuava implements FileCacheService {

    @Inject
    public FileCacheServiceGuava(FileService fileService, Config config) {
        super(fileService, new FileCacheConfigurationService(config));
    }
}

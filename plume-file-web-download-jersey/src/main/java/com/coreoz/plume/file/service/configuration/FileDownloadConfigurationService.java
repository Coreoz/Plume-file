package com.coreoz.plume.file.service.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Duration;

@Singleton
public class FileDownloadConfigurationService {
    private final Config config;

    @Inject
    public FileDownloadConfigurationService(Config config) {
        // the reference file is not located in src/main/resources/ to ensure
        // that it is not overridden by another config file when a "fat jar" is created.
        this.config = config.withFallback(
            ConfigFactory.parseResources(FileDownloadConfigurationService.class, "reference.conf")
        );
    }

    public Duration fileCacheControlMaxAge() {
        return config.getDuration("file.cache.http.max-age");
    }

    public Duration fileDataCacheExpiresAfterAccessDuration() {
        return config.getDuration("file.cache.data.expires-after-access-duration");
    }

    public Duration fileMetadataCacheExpiresAfterAccessDuration() {
        return config.getDuration("file.cache.metadata.expires-after-access-duration");
    }

    public long fileDataCacheMaximumSizeInBytes() {
        return config.getBytes("file.cache.data.max-cache-size");
    }

    public long fileMetadataCacheMaximumSize() {
        return config.getLong("file.cache.metadata.max-elements");
    }

    /**
     * File URL base path used in {@link com.coreoz.plume.file.service.url.FileDownloadUrlService}
     * The URL should not be padded by a '/'
     * @return the base path
     */
    public String fileUrlBasePath() {
        String basePath = config.getString("file.url.base-path");
        if (basePath.endsWith("/")) {
            return basePath.substring(0, basePath.length() - 1);
        }
        return basePath;
    }
}

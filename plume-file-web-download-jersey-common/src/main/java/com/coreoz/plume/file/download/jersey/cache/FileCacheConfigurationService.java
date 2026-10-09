package com.coreoz.plume.file.download.jersey.cache;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Duration;

@Singleton
public class FileCacheConfigurationService {
    private final Config config;

    @Inject
    public FileCacheConfigurationService(Config config) {
        // the reference file is not located in src/main/resources/ to ensure
        // that it is not overridden by another config file when a "fat jar" is created.
        this.config = config.withFallback(
            ConfigFactory.parseResources(FileCacheConfigurationService.class, "reference.conf")
        );
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
}

package com.coreoz.plume.file.service.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
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

    public long fileDataCacheMaximumSize() {
        return config.getBytes("file.cache.data.max-cache-size");
    }

    public long fileMetadataCacheMaximumSize() {
        return config.getLong("file.cache.metadata.max-elements");
    }

    public boolean keepOriginalNameOnDownload() {
        return config.getBoolean("file.keep-original-name-on-download");
    }

    public int fileUidLength() {
        return config.getInt("file.uid.length");
    }
}

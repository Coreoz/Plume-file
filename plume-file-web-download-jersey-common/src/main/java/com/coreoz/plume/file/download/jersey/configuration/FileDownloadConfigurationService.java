package com.coreoz.plume.file.download.jersey.configuration;

import com.coreoz.plume.file.download.jersey.cache.FileCacheConfigurationService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Duration;

@Singleton
public class FileDownloadConfigurationService {
    private final Config config;
    private final FileCacheConfigurationService cacheConfiguration;

    @Inject
    public FileDownloadConfigurationService(Config config, FileCacheConfigurationService cacheConfiguration) {
        // the reference file is not located in src/main/resources/ to ensure
        // that it is not overridden by another config file when a "fat jar" is created.
        this.config = config.withFallback(
            ConfigFactory.parseResources(FileDownloadConfigurationService.class, "reference.conf")
        );
        this.cacheConfiguration = cacheConfiguration;
    }

    /**
     * Convenience constructor kept for manual instantiation (e.g. in tests).
     * The {@link FileCacheConfigurationService} is created with the same configuration.
     */
    public FileDownloadConfigurationService(Config config) {
        this(config, new FileCacheConfigurationService(config));
    }

    public Duration fileCacheControlMaxAge() {
        return config.getDuration("file.cache.http.max-age");
    }

    /**
     * @deprecated For removal in V5
     * moved to {@link FileCacheConfigurationService}
     */
    @Deprecated(forRemoval = true)
    public Duration fileDataCacheExpiresAfterAccessDuration() {
        return this.cacheConfiguration.fileDataCacheExpiresAfterAccessDuration();
    }

    /**
     * @deprecated For removal in V5
     * moved to {@link FileCacheConfigurationService}
     */
    @Deprecated(forRemoval = true)
    public Duration fileMetadataCacheExpiresAfterAccessDuration() {
        return this.cacheConfiguration.fileMetadataCacheExpiresAfterAccessDuration();
    }

    /**
     * @deprecated For removal in V5
     * moved to {@link FileCacheConfigurationService}
     */
    @Deprecated(forRemoval = true)
    public long fileDataCacheMaximumSizeInBytes() {
        return this.cacheConfiguration.fileDataCacheMaximumSizeInBytes();
    }

    /**
     * @deprecated For removal in V5
     * moved to {@link FileCacheConfigurationService}
     */
    @Deprecated(forRemoval = true)
    public long fileMetadataCacheMaximumSize() {
        return this.cacheConfiguration.fileMetadataCacheMaximumSize();
    }

    /**
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

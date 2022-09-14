package com.coreoz.plume.file.services.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;

@Singleton
public class FileWebJerseyConfigurationService {
    private final Config config;

    @Inject
    public FileWebJerseyConfigurationService(Config config) {
        // the reference file is not located in src/main/resources/ to ensure
        // that it is not overridden by another config file when a "fat jar" is created.
        this.config = config.withFallback(
            ConfigFactory.parseResources(FileConfigurationService.class, "reference.conf")
        );
    }

    public String apiBasePath() {
        String apiBasePath = config.getString("application.api-base-path");
        checkPathConfiguration(apiBasePath);
        return apiBasePath;
    }

    public String fileWsPath() {
        String wsPath = config.getString("file.ws-path");
        checkPathConfiguration(wsPath);
        return wsPath;
    }

    public Duration fileCacheMaxAge() {
        return config.getDuration("file.cache.max-age");
    }

    public long fileCacheMaxSize() {
        return config.getLong("file.max-file-mb-size");
    }

    private static void checkPathConfiguration(String configuration) {
        if (!configuration.startsWith("/") || configuration.endsWith("/")) {
            throw new RuntimeException("Path configuration should start with '/' and not end with one : " + configuration);
        }
    }
}

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
        // TODO use config.getBytes() so dev can specify the unit directly: https://github.com/lightbend/config/blob/main/HOCON.md#size-in-bytes-format
        // TODO il faudra renommer la clé de config après ça
        return config.getLong("file.cache.max-file-mb-size") * 1000000;
    }

    public boolean keepOriginalNameOnDownload() {
        return config.getBoolean("file.keep-original-name-on-download");
    }

    private static void checkPathConfiguration(String configuration) {
        if (!configuration.startsWith("/") || configuration.endsWith("/")) {
            throw new RuntimeException("Path configuration should start with '/' and not end with one : " + configuration);
        }
    }
}

package com.coreoz.plume.file.configuration;

import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import javax.inject.Inject;

public class FileStorageConfigurationService {
    private final Config config;

    @Inject
    public FileStorageConfigurationService(Config config) {
        // the reference file is not located in src/main/resources/ to ensure
        // that it is not overridden by another config file when a "fat jar" is created.
        this.config = config.withFallback(
            ConfigFactory.parseResources(FileStorageConfigurationService.class, "reference.conf")
        );
    }

    public String mediaLocalPath() {
        return config.getString("file.storage.local-path");
    }
}

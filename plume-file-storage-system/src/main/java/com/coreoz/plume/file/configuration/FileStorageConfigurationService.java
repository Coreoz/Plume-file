package com.coreoz.plume.file.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.stream.Collectors;

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
        if (!config.hasPath("file.storage.local-path")) {
            throw new IllegalStateException("Missing configuration: file.storage.local-path");
        }
        return config.getString("file.storage.local-path");
    }

    public boolean useSubdirectories() {
        return config.getBoolean("file.storage.use-subdirectories");
    }

    public Map<String, String> subdirectoriesFileTypeMapping() {
        return config.getConfig("file.storage.subdirectories-file-type-mapping").entrySet().stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toUpperCase(),
                    entry -> entry.getValue().unwrapped().toString()
                )
            );
    }
}

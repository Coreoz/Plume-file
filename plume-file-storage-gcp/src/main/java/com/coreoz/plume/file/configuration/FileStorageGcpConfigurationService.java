package com.coreoz.plume.file.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jakarta.inject.Inject;

import java.util.Map;
import java.util.stream.Collectors;

public class FileStorageGcpConfigurationService {
    private final Config config;

    @Inject
    public FileStorageGcpConfigurationService(Config config) {
        // the reference file is not located in src/main/resources/ to ensure
        // that it is not overridden by another config file when a "fat jar" is created.
        this.config = config.withFallback(
            ConfigFactory.parseResources(FileStorageGcpConfigurationService.class, "reference.conf")
        );
    }

    public String gcpProjectId() {
        return config.getString("file.storage.gcp.project-id");
    }

    public String gcpBucketName() {
        return config.getString("file.storage.gcp.bucket-name");
    }

    public String gcpBucketBasePath() {
        return config.getString("file.storage.gcp.bucket-base-path");
    }

    public String gcpCredentialPath() {
        return config.getString("file.storage.gcp.credentials-path");
    }

    public boolean useSubdirectories() {
        return config.getBoolean("file.storage.gcp.use-subdirectories");
    }

    public Map<String, String> subdirectoriesFileTypeMapping() {
        return config.getConfig("file.storage.gcp.subdirectories-file-type-mapping").entrySet().stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toUpperCase(),
                    entry -> entry.getValue().unwrapped().toString()
                )
            );
    }
}

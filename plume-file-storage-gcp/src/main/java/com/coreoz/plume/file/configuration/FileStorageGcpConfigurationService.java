package com.coreoz.plume.file.configuration;

import com.typesafe.config.Config;
import jakarta.inject.Inject;

public class FileStorageGcpConfigurationService {
    private final Config config;

    @Inject
    public FileStorageGcpConfigurationService(Config config) {
        this.config = config;
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
}

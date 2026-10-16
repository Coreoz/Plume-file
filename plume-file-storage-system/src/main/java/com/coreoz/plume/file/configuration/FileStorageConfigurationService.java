package com.coreoz.plume.file.configuration;

import java.util.Optional;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import jakarta.inject.Inject;

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

    /**
     * Whether the files stored on disk should be encrypted.
     * Encryption is enabled by default and must be disabled explicitly through
     * {@code file.storage.encryption.disabled = true}.
     */
    public boolean isEncryptionEnabled() {
        return !config.getBoolean("file.storage.encryption.disabled");
    }

    /**
     * The optional Base64 encoded AES-256 key (exactly 32 bytes once decoded) used to encrypt
     * the files stored on disk.
     */
    public Optional<String> encryptionKeyBase64() {
        return config.hasPath("file.storage.encryption.key")
            ? Optional.of(config.getString("file.storage.encryption.key"))
            : Optional.empty();
    }
}

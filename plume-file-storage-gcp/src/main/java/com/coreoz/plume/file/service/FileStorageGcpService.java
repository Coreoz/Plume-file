package com.coreoz.plume.file.service;

import com.coreoz.plume.file.configuration.FileStorageGcpConfigurationService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.google.auth.Credentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.List;
import java.util.Optional;

@Slf4j
@Singleton
public class FileStorageGcpService implements FileStorageService {

    private final Storage storage;
    private final String bucketName;
    private final String bucketBasePath;

    @Inject
    public FileStorageGcpService(
        FileStorageGcpConfigurationService configurationService,
        Credentials credentials
    ) {
        this.storage = StorageOptions.newBuilder()
            .setProjectId(configurationService.gcpProjectId())
            .setCredentials(credentials)
            .build()
            .getService();
        this.bucketName = configurationService.gcpBucketName();
        this.bucketBasePath = normalizeBasePath(configurationService.gcpBucketBasePath());
        if (this.storage == null || this.bucketName == null) {
            throw new IllegalStateException("GCP Storage or bucket name is not configured properly");
        }
    }

    @Override
    public void add(String fileUniqueName, InputStream fileData) throws IOException {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName(fileUniqueName)).build();
        storage.createFrom(blobInfo, fileData);
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName) {
        Blob blob = storage.get(BlobId.of(bucketName, objectName(fileUniqueName)));
        if (blob == null) {
            return Optional.empty();
        }
        return Optional.of(
            Channels.newInputStream(blob.reader())
        );
    }

    @Override
    public void deleteAll(List<String> fileUniqueNames) {
        for (String fileUniqueName : fileUniqueNames) {
            BlobId blobId = BlobId.of(bucketName, objectName(fileUniqueName));
            try {
                storage.delete(blobId);
            } catch (StorageException e) {
                logger.warn("Failed to delete file: {}", fileUniqueName, e);
            }
        }
    }

    /**
     * Construct the object name by combining the base path and the file unique name.
     *
     * @param fileUniqueName the unique name of the file
     * @return the full object name
     */
    private String objectName(String fileUniqueName) {
        return bucketBasePath + fileUniqueName;
    }

    /**
     * Normalize the base path to ensure it ends with a slash if not empty.
     *
     * @param basePath the base path to normalize
     * @return the normalized base path
     */
    private static String normalizeBasePath(String basePath) {
        if (basePath == null || basePath.isBlank()) {
            return "";
        }
        return basePath.endsWith("/") ? basePath : basePath + "/";
    }
}

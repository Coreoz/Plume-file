package com.coreoz.plume.file.service;

import com.coreoz.plume.file.configuration.FileStorageGcpConfigurationService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.coreoz.plume.file.utils.FilePathSanitizer;
import com.google.auth.Credentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.google.common.base.Strings;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Singleton
public class FileStorageGcpService implements FileStorageService {
    private final Storage storage;
    private final String bucketName;
    private final String bucketBasePath;

    private final boolean useSubdirectories;
    private final Map<String, String> subdirectoriesFileTypeMapping;

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
        this.bucketBasePath = FilePathSanitizer.sanitizePath(configurationService.gcpBucketBasePath());
        if (this.storage == null || this.bucketName == null) {
            throw new IllegalStateException("GCP Storage or bucket name is not configured properly");
        }

        this.useSubdirectories = configurationService.useSubdirectories();
        this.subdirectoriesFileTypeMapping = configurationService.subdirectoriesFileTypeMapping();
    }

    @Override
    public void add(String fileUniqueName, InputStream fileData) throws IOException {
        if (useSubdirectories) {
            throw new IllegalArgumentException("File metadata must be provided to fetch files when subdirectories are used");
        }
        add(fileUniqueName, fileData, null);
    }

    @Override
    public void add(String fileUniqueName, InputStream fileData, FileMetadata metadata) throws IOException {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName(fileUniqueName, metadata)).build();
        storage.createFrom(blobInfo, fileData);
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName) {
        if (useSubdirectories) {
            throw new IllegalArgumentException("File metadata must be provided to fetch files when subdirectories are used");
        }
        return fetch(fileUniqueName, null);
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName, FileMetadata metadata) {
        Blob blob = storage.get(BlobId.of(bucketName, objectName(fileUniqueName, metadata)));
        if (blob == null) {
            return Optional.empty();
        }
        return Optional.of(
            Channels.newInputStream(blob.reader())
        );
    }

    @Override
    public void deleteAll(List<String> fileUniqueNames) {
        if (useSubdirectories) {
            throw new IllegalArgumentException("File metadata must be provided to fetch files when subdirectories are used");
        }
        logger.debug("Deleting {} files from GCP Storage...", fileUniqueNames.size());
        for (String fileUniqueName : fileUniqueNames) {
            BlobId blobId = BlobId.of(bucketName, objectName(fileUniqueName, null));
            try {
                storage.delete(blobId);
            } catch (StorageException e) {
                logger.warn("Failed to delete file: {}", fileUniqueName, e);
            }
        }
    }

    @Override
    public void deleteFiles(List<FileMetadata> filesToDelete) {
        logger.debug("Deleting {} files from GCP Storage...", filesToDelete.size());
        for (FileMetadata fileMetadata : filesToDelete) {
            BlobId blobId = BlobId.of(bucketName, objectName(fileMetadata.getUniqueName(), fileMetadata));
            try {
                storage.delete(blobId);
            } catch (StorageException e) {
                logger.warn("Failed to delete file: {}", fileMetadata.getUniqueName(), e);
            }
        }
    }

    /**
     * Construct the object name by combining the base path, optional subdirectory path, and the file unique name.
     *
     * @param fileUniqueName the unique name of the file
     * @param metadata the file metadata, which can be used to determine the subdirectory path based on the file type
     * @return the full object name
     */
    private String objectName(String fileUniqueName, FileMetadata metadata) {
        if (!this.useSubdirectories || metadata == null || Strings.isNullOrEmpty(metadata.getFileType())) {
            return bucketBasePath + fileUniqueName;
        }
        return FilePathSanitizer.combinePaths(this.bucketBasePath, mapFileTypeToSubdirectory(metadata.getFileType())) + fileUniqueName;
    }

    @Nullable
    private String mapFileTypeToSubdirectory(String fileType) {
        if (this.subdirectoriesFileTypeMapping == null || fileType == null) {
            return null;
        }
        return this.subdirectoriesFileTypeMapping.getOrDefault(fileType, fileType);
    }
}

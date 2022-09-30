package com.coreoz.plume.file.services;

import com.coreoz.plume.file.cache.FileCacheService;
import com.coreoz.plume.file.hash.ChecksumService;
import com.coreoz.plume.file.services.beans.FileData;
import com.coreoz.plume.file.services.configuration.FileWebJerseyConfigurationService;
import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

@Singleton
public class FileWebJerseyService {
    private static final Logger logger = LoggerFactory.getLogger(FileWebJerseyService.class);

    private final FileWebJerseyConfigurationService configurationService;
    private final FileService fileService;
    private final FileCacheService fileCacheService;
    private final ChecksumService checksumService;

    @Inject
    public FileWebJerseyService(
        FileWebJerseyConfigurationService configurationService,
        FileService fileService,
        FileCacheService fileCacheService,
        ChecksumService checksumService
    ) {
        this.configurationService = configurationService;
        this.fileService = fileService;
        this.checksumService = checksumService;
        this.fileCacheService = fileCacheService;

        this.fileCacheService.initializeFileDataCache(uid ->
            this.fileService.fetchData(uid).flatMap(data -> {
                try {
                    return Optional.of(ByteStreams.toByteArray(data));
                } catch (IOException e) {
                    logger.error("Error while fetching file {} : ", uid, e);
                }
                return Optional.empty();
            })
        );
        this.fileCacheService.initializeFileMetadataCache(this.fileService::fetchMetadata);
    }

    public Optional<FileData> fetchCachedFile(String fileUniqueName) {
        return this.fileCacheService.fetchFileMetadata(fileUniqueName)
            .flatMap(metadata -> this.fileCacheService.fetchFileData(fileUniqueName)
                .map(data -> new FileData(
                    data,
                    this.checksumService.hash(data),
                    metadata.getMimeType(),
                    metadata.getFileOriginalName()
                ))
            );
    }
}

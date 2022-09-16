package com.coreoz.plume.file.services;

import com.coreoz.plume.file.cache.FileCacheService;
import com.coreoz.plume.file.hash.ChecksumService;
import com.coreoz.plume.file.services.beans.FileData;
import com.coreoz.plume.file.services.configuration.FileWebJerseyConfigurationService;
import com.google.common.io.ByteStreams;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

@Singleton
public class FileWebJerseyService {

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
                    e.printStackTrace();
                }
                return Optional.empty();
            })
        );
        this.fileCacheService.initializeFileMetadataCache(this.fileService::fetchMetadata);
    }

    public Optional<FileData> fetchFile(String fileUniqueName) {
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

    public Optional<String> url(String fileUniqueName) {
        return this.fileCacheService.fetchFileMetadata(fileUniqueName)
            .map(fileMetadata ->
                this.configurationService.apiBasePath()
                    + this.configurationService.fileWsPath()
                    + fileMetadata.getUniqueName()
            );
    }
}

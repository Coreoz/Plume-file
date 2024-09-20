package com.coreoz.plume.file.service;

import com.coreoz.plume.file.cache.FileCacheService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

@Singleton
public class FileCacheDownloadService implements FileDownloadJerseyService {
    private static final Logger logger = LoggerFactory.getLogger(FileCacheDownloadService.class);
    private final FileCacheService fileCacheService;

    @Inject
    public FileCacheDownloadService(FileCacheService fileCacheService) {
        this.fileCacheService = fileCacheService;
    }

    @Override
    public Optional<FileMetadata> fetchMetadata(String fileUniqueName) {
        logger.trace("Fetching metadata in cache of file {}", fileUniqueName);
        return this.fileCacheService.fetchFileMetadata(fileUniqueName);
    }

    @Override
    public Optional<InputStream> fetchData(String fileUniqueName) {
        logger.trace("Fetching data in cache of file {}", fileUniqueName);
        return this.fileCacheService.fetchFileData(fileUniqueName)
            .map(ByteArrayInputStream::new);
    }
}

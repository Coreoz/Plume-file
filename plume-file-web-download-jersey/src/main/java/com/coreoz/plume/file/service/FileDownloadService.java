package com.coreoz.plume.file.service;

import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Singleton
public class FileDownloadService implements FileDownloadJerseyService {
    private final FileService fileService;

    @Inject
    public FileDownloadService(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public Optional<FileMetadata> fetchMetadata(String fileUniqueName) {
        logger.trace("Fetching metadata of file {}", fileUniqueName);
        return this.fileService.fetchMetadata(fileUniqueName);
    }

    @Override
    public Optional<InputStream> fetchData(String fileUniqueName) {
        return this.fetchData(fileUniqueName, null);
    }

    @Override
    public Optional<InputStream> fetchData(String fileUniqueName, FileMetadata metadata) {
        logger.trace("Fetching file {}", fileUniqueName);
        return this.fileService.fetchData(fileUniqueName, metadata);
    }
}

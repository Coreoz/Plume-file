package com.coreoz.plume.file.service;

import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.util.Optional;

@Singleton
public class FileDownloadService implements FileDownloadJerseyService {
    private static final Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

    private final FileService fileService;

    @Inject
    public FileDownloadService(FileService fileService) {
        this.fileService = fileService;
    }

    public Optional<FileMetadata> fetchMetadata(String fileUniqueName) {
        logger.trace("Fetching metadata of file {}", fileUniqueName);
        return this.fileService.fetchMetadata(fileUniqueName);
    }

    public Optional<InputStream> fetchData(String fileUniqueName) {
        logger.trace("Fetching file {}", fileUniqueName);
        return this.fileService.fetchData(fileUniqueName);
    }
}

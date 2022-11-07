package com.coreoz.plume.file.service;

import com.coreoz.plume.file.cache.FileCacheService;
import com.coreoz.plume.file.service.beans.FileData;
import com.coreoz.plume.file.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class FileCacheDownloadService implements FileDownloadJerseyService {
    private static final Logger logger = LoggerFactory.getLogger(FileCacheDownloadService.class);

    private final FileService fileService;
    private final FileCacheService fileCacheService;

    @Inject
    public FileCacheDownloadService(
        FileService fileService,
        FileCacheService fileCacheService
    ) {
        this.fileService = fileService;
        this.fileCacheService = fileCacheService;

        this.fileCacheService.initializeFileDataCache(uid ->
            this.fileService.fetchData(uid).flatMap(FileDownloadJerseyService.readFile())
        );
        this.fileCacheService.initializeFileMetadataCache(this.fileService::fetchMetadata);
    }

    public Optional<FileData> fetchFile(String fileUniqueName) {
        logger.debug("Fetching file {}", fileUniqueName);
        return this.fileCacheService.fetchFileMetadata(fileUniqueName)
            .flatMap(metadata -> this.fileCacheService.fetchFileData(fileUniqueName)
                .map(data -> new FileData(
                    data,
                    metadata.getChecksum(),
                    metadata.getMimeType(),
                    metadata.getFileOriginalName()
                ))
            );
    }
}

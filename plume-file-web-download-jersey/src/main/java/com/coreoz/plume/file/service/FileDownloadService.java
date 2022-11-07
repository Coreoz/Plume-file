package com.coreoz.plume.file.service;

import com.coreoz.plume.file.service.beans.FileData;
import com.coreoz.plume.file.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class FileDownloadService implements FileDownloadJerseyService {
    private static final Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

    private final FileService fileService;

    @Inject
    public FileDownloadService(FileService fileService) {
        this.fileService = fileService;
    }

    public Optional<FileData> fetchFile(String fileUniqueName) {
        logger.debug("Fetching file {}", fileUniqueName);
        return this.fileService.fetchMetadata(fileUniqueName)
            .flatMap(metadata -> this.fileService.fetchData(fileUniqueName)
                .flatMap(FileDownloadJerseyService.readFile())
                .map(data -> new FileData(
                    data,
                    metadata.getChecksum(),
                    metadata.getMimeType(),
                    metadata.getFileOriginalName()
                ))
            );
    }
}

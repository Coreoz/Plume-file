package com.coreoz.plume.file.service.url;

import com.coreoz.plume.file.service.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.url.FileUrlService;

import javax.inject.Inject;

public class FileDownloadUrlService implements FileUrlService {

    private final FileService fileService;
    private final String fileUrlBasePath;

    @Inject
    public FileDownloadUrlService(FileDownloadConfigurationService configurationService, FileService fileService) {
        this.fileService = fileService;
        this.fileUrlBasePath = configurationService.fileUrlBasePath();
    }

    @Override
    public String url(String uniqueName) {
        return this.fileService.fetchMetadata(uniqueName)
            .map(fileMetadata -> this.fileUrlBasePath + "/" + fileMetadata.getUniqueName())
            .orElse(null);
    }
}

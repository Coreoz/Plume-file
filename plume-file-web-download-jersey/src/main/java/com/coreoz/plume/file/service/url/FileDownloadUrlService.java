package com.coreoz.plume.file.service.url;

import com.coreoz.plume.file.service.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.file.services.url.FileUrlService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FileDownloadUrlService implements FileUrlService {
    private final String fileUrlBasePath;

    @Inject
    public FileDownloadUrlService(FileDownloadConfigurationService configurationService) {
        this.fileUrlBasePath = configurationService.fileUrlBasePath();
    }

    @Override
    public String url(String uniqueName) {
        return this.fileUrlBasePath + "/" + uniqueName;
    }
}

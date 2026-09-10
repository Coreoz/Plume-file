package com.coreoz.plume.file.service.configuration;

import com.typesafe.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
@Deprecated(forRemoval = true)
public class FileDownloadConfigurationService extends com.coreoz.plume.file.download.jersey.configuration.FileDownloadConfigurationService {

    @Inject
    public FileDownloadConfigurationService(Config config) {
        super(config);
    }
}

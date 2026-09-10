package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.service.FileDownloadJerseyService;
import com.coreoz.plume.file.service.FileDownloadService;
import com.coreoz.plume.file.service.url.FileDownloadUrlService;
import com.coreoz.plume.file.services.url.FileUrlService;
import com.google.inject.AbstractModule;

@Deprecated(forRemoval = true)
public class GuiceFileDownloadModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(com.coreoz.plume.file.download.jersey.FileDownloadJerseyService.class).to(com.coreoz.plume.file.download.jersey.FileDownloadService.class);
        bind(FileDownloadJerseyService.class).to(FileDownloadService.class);
        bind(FileUrlService.class).to(FileDownloadUrlService.class);
    }
}

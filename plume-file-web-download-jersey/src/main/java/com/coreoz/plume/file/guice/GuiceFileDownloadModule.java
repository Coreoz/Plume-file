package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.service.FileDownloadJerseyService;
import com.coreoz.plume.file.service.FileDownloadService;
import com.google.inject.AbstractModule;

public class GuiceFileDownloadModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileDownloadJerseyService.class).to(FileDownloadService.class);
    }
}

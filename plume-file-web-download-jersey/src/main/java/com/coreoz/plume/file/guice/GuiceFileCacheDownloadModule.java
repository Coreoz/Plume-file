package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.cache.FileCacheService;
import com.coreoz.plume.file.cache.FileCacheServiceGuava;
import com.coreoz.plume.file.service.FileCacheDownloadService;
import com.coreoz.plume.file.service.FileDownloadJerseyService;
import com.google.inject.AbstractModule;

public class GuiceFileCacheDownloadModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileDownloadJerseyService.class).to(FileCacheDownloadService.class);
        bind(FileCacheService.class).to(FileCacheServiceGuava.class);
    }
}

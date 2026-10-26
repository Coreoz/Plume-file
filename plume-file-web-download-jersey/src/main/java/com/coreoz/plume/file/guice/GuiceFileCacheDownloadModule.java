package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.download.jersey.cache.FileCacheService;
import com.coreoz.plume.file.download.jersey.cache.FileCacheServiceGuava;
import com.coreoz.plume.file.download.jersey.FileCacheDownloadService;
import com.coreoz.plume.file.download.jersey.FileDownloadJerseyService;
import com.google.inject.AbstractModule;

public class GuiceFileCacheDownloadModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new com.coreoz.plume.file.download.jersey.guice.GuiceFileCacheDownloadModule());
        bind(FileCacheService.class).to(FileCacheServiceGuava.class);
        bind(FileDownloadJerseyService.class).to(FileCacheDownloadService.class);
    }
}

package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.cache.FileCacheService;
import com.coreoz.plume.file.cache.FileCacheServiceGuava;
import com.google.inject.AbstractModule;

@Deprecated(forRemoval = true)
public class GuiceFileCacheDownloadModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new GuiceFileDownloadModule());
        bind(com.coreoz.plume.file.download.jersey.cache.FileCacheService.class).to(com.coreoz.plume.file.download.jersey.cache.FileCacheServiceGuava.class);
        bind(FileCacheService.class).to(FileCacheServiceGuava.class);
    }
}

package com.coreoz.plume.file.download.jersey.guice;

import com.coreoz.plume.file.download.jersey.FileCacheDownloadService;
import com.coreoz.plume.file.download.jersey.FileDownloadJerseyService;
import com.coreoz.plume.file.download.jersey.cache.FileCacheService;
import com.coreoz.plume.file.download.jersey.cache.FileCacheServiceGuava;
import com.google.inject.AbstractModule;

/**
 * Wires the shared file download services with in-memory caching enabled.
 * It rebinds {@link FileDownloadJerseyService} to the caching implementation
 * and binds the Guava-backed {@link FileCacheService}.
 */
public class GuiceFileCacheDownloadModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new GuiceFileDownloadModule());
        bind(FileCacheService.class).to(FileCacheServiceGuava.class);
        bind(FileDownloadJerseyService.class).to(FileCacheDownloadService.class);
    }
}

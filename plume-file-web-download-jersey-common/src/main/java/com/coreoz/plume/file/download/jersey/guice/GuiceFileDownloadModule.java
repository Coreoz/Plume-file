package com.coreoz.plume.file.download.jersey.guice;

import com.coreoz.plume.file.download.jersey.FileDownloadJerseyService;
import com.coreoz.plume.file.download.jersey.FileDownloadService;
import com.google.inject.AbstractModule;

/**
 * Wires the shared file download services that are common to both the plain
 * and the signed download modules. It binds the default (non-cached) implementation
 * of {@link FileDownloadJerseyService}.
 */
public class GuiceFileDownloadModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileDownloadJerseyService.class).to(FileDownloadService.class);
    }
}

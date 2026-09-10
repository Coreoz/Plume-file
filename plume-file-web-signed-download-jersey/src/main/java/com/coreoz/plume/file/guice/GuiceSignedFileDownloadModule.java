package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.download.jersey.FileDownloadJerseyService;
import com.coreoz.plume.file.download.jersey.FileDownloadService;
import com.coreoz.plume.file.signed.guice.GuiceSignedFileUrlModule;
import com.google.inject.AbstractModule;

public class GuiceSignedFileDownloadModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new GuiceSignedFileUrlModule());
        bind(FileDownloadJerseyService.class).to(FileDownloadService.class);
    }
}

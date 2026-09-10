package com.coreoz.plume.file.signed.guice;

import com.coreoz.plume.file.signed.SignedFileUrlService;
import com.coreoz.plume.file.services.url.FileUrlService;
import com.google.inject.AbstractModule;

public class GuiceSignedFileUrlModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileUrlService.class).to(SignedFileUrlService.class);
    }
}

package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.credential.GcpCredentialsProvider;
import com.coreoz.plume.file.service.FileStorageGcpService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.google.auth.Credentials;
import com.google.inject.AbstractModule;

public class GuiceFileStorageGcpModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileStorageService.class).to(FileStorageGcpService.class);
        bind(Credentials.class).toProvider(GcpCredentialsProvider.class);
    }
}

package com.coreoz.plume.file.credential;

import com.coreoz.plume.file.configuration.FileStorageGcpConfigurationService;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import lombok.SneakyThrows;

import java.io.FileInputStream;

public class GcpCredentialsProvider implements Provider<Credentials> {
    private final Credentials credentials;

    @SneakyThrows
    @Inject
    private GcpCredentialsProvider(FileStorageGcpConfigurationService configurationService) {
        this.credentials = ServiceAccountCredentials.fromStream(
            new FileInputStream(configurationService.gcpCredentialPath())
        );
    }

    @Override
    public Credentials get() {
        return credentials;
    }
}

package com.coreoz.plume.file.services.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.concurrent.TimeUnit;

@Singleton
public class SignedUrlConfigurationService {
    private final Config config;

    @Inject
    public SignedUrlConfigurationService(Config config) {
        this.config = config.withFallback(
            ConfigFactory.parseResources(SignedUrlConfigurationService.class, "reference.conf")
        );
    }

    public long fileSignedUrlExpiration() {
        return config.getDuration("file.signed-url.expiration", TimeUnit.SECONDS);
    }

    public String fileSignedUrlHashKey() {
        return config.getString("file.signed-url.hash-key");
    }

}

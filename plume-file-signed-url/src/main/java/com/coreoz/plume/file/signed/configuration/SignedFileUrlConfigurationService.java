package com.coreoz.plume.file.signed.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Duration;

@Singleton
public class SignedFileUrlConfigurationService {
    private final Config config;

    @Inject
    public SignedFileUrlConfigurationService(Config config) {
        this.config = config.withFallback(
            ConfigFactory.parseResources(SignedFileUrlConfigurationService.class, "reference.conf")
        );
        String secret = secretKey();
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException("The signed URL secret key 'file.signed-url.secret-key' must be configured.");
        }
    }

    public String secretKey() {
        if (!config.hasPath("file.signed-url.secret-key") || config.getIsNull("file.signed-url.secret-key")) {
            return null;
        }
        return config.getString("file.signed-url.secret-key");
    }

    public Duration defaultValidityDuration() {
        return config.getDuration("file.signed-url.default-validity-duration");
    }

    public String basePath() {
        String basePath = config.getString("file.signed-url.base-path");
        if (basePath.endsWith("/")) {
            return basePath.substring(0, basePath.length() - 1);
        }
        return basePath;
    }
}

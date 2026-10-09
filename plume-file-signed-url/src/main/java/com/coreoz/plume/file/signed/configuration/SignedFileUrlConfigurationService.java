package com.coreoz.plume.file.signed.configuration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import javax.annotation.Nonnull;
import java.time.Duration;

@Singleton
public class SignedFileUrlConfigurationService {
    private static final String CONFIG_PATH_SECRET_KEY = "file.signed-url.secret-key";
    private final Config config;

    @Inject
    public SignedFileUrlConfigurationService(Config config) {
        this.config = config.withFallback(
            ConfigFactory.parseResources(SignedFileUrlConfigurationService.class, "reference.conf")
        );
        if (!this.config.hasPath(CONFIG_PATH_SECRET_KEY)
            || this.config.getIsNull(CONFIG_PATH_SECRET_KEY)
            || this.config.getString(CONFIG_PATH_SECRET_KEY).trim().isEmpty()) {
            throw new IllegalStateException("The signed URL secret key 'file.signed-url.secret-key' must be configured.");
        }
    }

    @Nonnull
    public String secretKey() {
        return config.getString(CONFIG_PATH_SECRET_KEY);
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

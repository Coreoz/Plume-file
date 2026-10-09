package com.coreoz.plume.file.signed;

import com.coreoz.plume.file.signed.configuration.SignedFileUrlConfigurationService;
import com.coreoz.plume.file.services.url.FileUrlService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Clock;
import java.time.Instant;

@Singleton
public class SignedFileUrlService implements FileUrlService {
    private final SignedFileUrlSigner signer;
    private final SignedFileUrlConfigurationService configurationService;
    private final Clock clock;

    @Inject
    public SignedFileUrlService(
        SignedFileUrlSigner signer,
        SignedFileUrlConfigurationService configurationService,
        Clock clock
    ) {
        this.signer = signer;
        this.configurationService = configurationService;
        this.clock = clock;
    }

    @Override
    public String url(String uniqueName) {
        Instant expiresAt = this.clock.instant().plus(this.configurationService.defaultValidityDuration());
        return url(uniqueName, expiresAt);
    }

    public String url(String uniqueName, Instant expiresAt) {
        long expiresAtSeconds = expiresAt.getEpochSecond();
        String signature = this.signer.sign(uniqueName, expiresAtSeconds);
        return this.configurationService.basePath()
            + "/" + uniqueName
            + "?expiresAt=" + expiresAtSeconds
            + "&signature=" + signature;
    }

    public SignatureValidationResult validate(String uniqueName, long expiresAt, String signature) {
        if (this.clock.instant().getEpochSecond() > expiresAt) {
            return SignatureValidationResult.EXPIRED;
        }

        if (!this.signer.verify(uniqueName, expiresAt, signature)) {
            return SignatureValidationResult.INVALID_SIGNATURE;
        }

        return SignatureValidationResult.VALID;
    }
}

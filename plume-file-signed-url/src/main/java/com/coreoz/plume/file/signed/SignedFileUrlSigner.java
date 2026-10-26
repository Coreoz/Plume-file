package com.coreoz.plume.file.signed;

import com.coreoz.plume.file.signed.configuration.SignedFileUrlConfigurationService;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class SignedFileUrlSigner {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private final SecretKeySpec secretKeySpec;

    @Inject
    public SignedFileUrlSigner(SignedFileUrlConfigurationService configurationService) {
        byte[] secretKeyBytes = configurationService.secretKey().getBytes(StandardCharsets.UTF_8);
        this.secretKeySpec = new SecretKeySpec(secretKeyBytes, HMAC_SHA256);
        // Validate that the HMAC-SHA256 algorithm is available and the secret key is valid,
        // so that any configuration error is raised at initialization rather than later at runtime
        this.initializeMac();
    }

    private Mac initializeMac() {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(this.secretKeySpec);
            return mac;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to generate HMAC-SHA256 signature", e);
        }
    }

    public String sign(String uniqueName, long expiresAtEpochSeconds) {
        Mac mac = this.initializeMac();
        String rawData = uniqueName + expiresAtEpochSeconds;
        byte[] rawHmac = mac.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }

    public boolean verify(String uniqueName, long expiresAtEpochSeconds, String signature) {
        if (signature == null) {
            return false;
        }
        String computedSignature = sign(uniqueName, expiresAtEpochSeconds);
        return MessageDigest.isEqual(
            computedSignature.getBytes(StandardCharsets.UTF_8),
            signature.getBytes(StandardCharsets.UTF_8)
        );
    }
}

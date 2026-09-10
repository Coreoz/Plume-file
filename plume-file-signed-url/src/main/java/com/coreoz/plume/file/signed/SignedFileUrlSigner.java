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
    private final byte[] secretKeyBytes;

    @Inject
    public SignedFileUrlSigner(SignedFileUrlConfigurationService configurationService) {
        String secretKey = configurationService.secretKey();
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("The signed URL secret key must be defined and not blank");
        }
        this.secretKeyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    }

    public String sign(String uniqueName, long expiresAtEpochSeconds) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.secretKeyBytes, HMAC_SHA256);
            mac.init(secretKeySpec);

            String rawData = uniqueName + expiresAtEpochSeconds;
            byte[] rawHmac = mac.doFinal(rawData.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to generate HMAC-SHA256 signature", e);
        }
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

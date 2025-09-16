package com.coreoz.plume.file.services.url;

import com.coreoz.plume.file.services.configuration.SignedUrlConfigurationService;
import com.google.common.hash.Hashing;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Singleton
public class SignedUrlFileService implements FileUrlService {
    public static final String SIGNATURE_QUERY_PARAM = "signature";
    public static final String EXPIRATION_QUERY_PARAM = "expiration";
    private static final String API_ENDPOINT = "/api/files/";

    private final long fileSignedUrlExpiration;
    private final String fileSignedUrlHashKey;

    @Inject
    private SignedUrlFileService(SignedUrlConfigurationService configurationService) {
        this.fileSignedUrlHashKey = configurationService.fileSignedUrlHashKey();
        this.fileSignedUrlExpiration = configurationService.fileSignedUrlExpiration();
    }

    @Override
    public String url(String uniqueName) {
        Instant linkExpiration = Instant.now().plusSeconds(
            this.fileSignedUrlExpiration
        );
        return API_ENDPOINT + uniqueName
            + "?" + SIGNATURE_QUERY_PARAM + "="
            + generateSignature(this.fileSignedUrlHashKey, uniqueName, linkExpiration.toEpochMilli())
            + "&"+ EXPIRATION_QUERY_PARAM +"=" + linkExpiration.toEpochMilli();
    }

    public static String generateSignature(String base64Key, String uuid, long expirationTime) {
        String toSign = uuid + "\n" + expirationTime;
        return Base64.getUrlEncoder().encodeToString(
            Hashing
                .hmacSha256(Base64.getDecoder().decode(base64Key))
                .hashString(toSign, StandardCharsets.UTF_8)
                .asBytes()
        );
    }
}

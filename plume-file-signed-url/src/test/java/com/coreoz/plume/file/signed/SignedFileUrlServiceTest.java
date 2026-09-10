package com.coreoz.plume.file.signed;

import com.coreoz.plume.file.signed.configuration.SignedFileUrlConfigurationService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SignedFileUrlServiceTest {

    private SignedFileUrlConfigurationService createConfig(String secretKey, String duration, String basePath) {
        StringBuilder configStr = new StringBuilder();
        if (secretKey != null) {
            configStr.append("file.signed-url.secret-key = \"").append(secretKey).append("\"\n");
        } else {
            configStr.append("file.signed-url.secret-key = null\n");
        }
        if (duration != null) {
            configStr.append("file.signed-url.default-validity-duration = \"").append(duration).append("\"\n");
        }
        if (basePath != null) {
            configStr.append("file.signed-url.base-path = \"").append(basePath).append("\"\n");
        }
        Config config = ConfigFactory.parseString(configStr.toString());
        return new SignedFileUrlConfigurationService(config);
    }

    @Test
    public void config_should_fail_fast_if_secret_key_is_missing_or_blank() {
        assertThatThrownBy(() -> createConfig(null, "1 hour", "/api/files"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("file.signed-url.secret-key");

        assertThatThrownBy(() -> createConfig("", "1 hour", "/api/files"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("file.signed-url.secret-key");

        assertThatThrownBy(() -> createConfig("  ", "1 hour", "/api/files"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("file.signed-url.secret-key");
    }

    @Test
    public void signer_should_be_deterministic_and_fail_on_invalid_signatures() {
        SignedFileUrlConfigurationService config = createConfig("my-super-secret-key-for-testing", "1 hour", "/api/files");
        SignedFileUrlSigner signer = new SignedFileUrlSigner(config);

        String uniqueName = "file-123.pdf";
        long expiresAt = Instant.now().plusSeconds(3600).getEpochSecond();

        String signature1 = signer.sign(uniqueName, expiresAt);
        String signature2 = signer.sign(uniqueName, expiresAt);

        assertThat(signature1).isEqualTo(signature2);
        assertThat(signer.verify(uniqueName, expiresAt, signature1)).isTrue();

        // Tamper with uniqueName
        assertThat(signer.verify("file-124.pdf", expiresAt, signature1)).isFalse();

        // Tamper with expiresAt
        assertThat(signer.verify(uniqueName, expiresAt + 1, signature1)).isFalse();

        // Tamper with signature
        assertThat(signer.verify(uniqueName, expiresAt, signature1 + "a")).isFalse();
    }

    @Test
    public void service_should_generate_and_validate_correctly() {
        SignedFileUrlConfigurationService config = createConfig("my-super-secret-key-for-testing", "1 hour", "/api/files");
        SignedFileUrlSigner signer = new SignedFileUrlSigner(config);
        SignedFileUrlService service = new SignedFileUrlService(signer, config);

        String uniqueName = "photo.png";
        String url = service.url(uniqueName);

        assertThat(url).startsWith("/api/files/photo.png?expiresAt=");
        assertThat(url).contains("&signature=");

        // Parse query params from generated URL to validate
        String queryStr = url.substring(url.indexOf('?') + 1);
        String[] parts = queryStr.split("&");
        long expiresAt = 0;
        String signature = null;
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if ("expiresAt".equals(keyValue[0])) {
                expiresAt = Long.parseLong(keyValue[1]);
            } else if ("signature".equals(keyValue[0])) {
                signature = keyValue[1];
            }
        }

        assertThat(expiresAt).isGreaterThan(Instant.now().getEpochSecond());
        assertThat(signature).isNotNull();

        // Validate should return VALID
        assertThat(service.validate(uniqueName, expiresAt, signature)).isEqualTo(SignatureValidationResult.VALID);

        // Tampered parameters
        assertThat(service.validate("other-photo.png", expiresAt, signature)).isEqualTo(SignatureValidationResult.INVALID_SIGNATURE);
        assertThat(service.validate(uniqueName, expiresAt + 1, signature)).isEqualTo(SignatureValidationResult.INVALID_SIGNATURE);
        assertThat(service.validate(uniqueName, expiresAt, signature + "tampered")).isEqualTo(SignatureValidationResult.INVALID_SIGNATURE);

        // Expired URL
        long pastExpiresAt = Instant.now().minusSeconds(10).getEpochSecond();
        String expiredSignature = signer.sign(uniqueName, pastExpiresAt);
        assertThat(service.validate(uniqueName, pastExpiresAt, expiredSignature)).isEqualTo(SignatureValidationResult.EXPIRED);
    }
}

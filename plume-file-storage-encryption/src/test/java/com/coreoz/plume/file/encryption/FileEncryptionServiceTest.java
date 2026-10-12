package com.coreoz.plume.file.encryption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.junit.jupiter.api.Test;

class FileEncryptionServiceTest {

    private static FileEncryptionService newService() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return new AesGcmFileEncryptionService(key);
    }

    @Test
    void encrypt_then_decrypt_must_return_the_original_content() throws IOException {
        FileEncryptionService service = newService();
        byte[] plain = "Hello Plume File encryption!".getBytes(StandardCharsets.UTF_8);

        byte[] encrypted = readAll(service.encryptStream(new ByteArrayInputStream(plain), "file-a"));
        assertThat(encrypted).isNotEqualTo(plain);

        byte[] decrypted = readAll(service.decryptStream(new ByteArrayInputStream(encrypted), "file-a"));
        assertThat(decrypted).isEqualTo(plain);
    }

    @Test
    void decrypt_must_fail_when_the_file_name_does_not_match() throws IOException {
        FileEncryptionService service = newService();
        byte[] plain = "Hello Plume File encryption!".getBytes(StandardCharsets.UTF_8);

        byte[] encrypted = readAll(service.encryptStream(new ByteArrayInputStream(plain), "file-a"));

        // decrypting under another file name must fail the GCM authentication (AAD mismatch)
        assertThatThrownBy(() -> readAll(service.decryptStream(new ByteArrayInputStream(encrypted), "file-b")))
            .isInstanceOf(IOException.class);
    }

    @Test
    void decrypt_must_return_legacy_unencrypted_content_as_is() throws IOException {
        FileEncryptionService service = newService();
        byte[] legacyPlain = "I was stored before encryption was enabled".getBytes(StandardCharsets.UTF_8);

        byte[] result = readAll(service.decryptStream(new ByteArrayInputStream(legacyPlain), "file-a"));

        assertThat(result).isEqualTo(legacyPlain);
    }

    private static byte[] readAll(InputStream inputStream) throws IOException {
        try (InputStream stream = inputStream) {
            return stream.readAllBytes();
        }
    }
}

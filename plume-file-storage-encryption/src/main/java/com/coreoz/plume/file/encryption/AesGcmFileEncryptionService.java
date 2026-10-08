package com.coreoz.plume.file.encryption;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.SequenceInputStream;
import java.io.UncheckedIOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Default {@link FileEncryptionService} implementation using AES in GCM mode (authenticated
 * encryption).
 * <p>
 * The encrypted stream starts with a small header so that stored files can be identified:
 * <pre>
 *   [ 4 magic bytes "PFE1" ][ 12 bytes IV ][ GCM ciphertext + auth tag ]
 * </pre>
 * When decrypting, a stream that does not start with the magic header is considered to be a
 * legacy, unencrypted file and is returned as-is. This allows encrypted and plain files to
 * coexist, for instance when encryption is enabled on an existing storage.
 */
public class AesGcmFileEncryptionService implements FileEncryptionService {
    private static final byte[] MAGIC = {'P', 'F', 'E', '1'};
    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private final SecretKey secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * @param key the raw AES key bytes (16, 24 or 32 bytes for AES-128/192/256)
     */
    public AesGcmFileEncryptionService(byte[] key) {
        validateKeyLength(key);
        this.secretKey = new SecretKeySpec(key, "AES");
    }

    /**
     * Build an {@link AesGcmFileEncryptionService} from a Base64 encoded AES key.
     *
     * @param base64Key the Base64 encoded AES key (16, 24 or 32 bytes once decoded)
     * @throws IllegalArgumentException if the value is not valid Base64 or if the decoded key does
     * not have a valid AES length
     */
    public static AesGcmFileEncryptionService fromBase64Key(String base64Key) {
        return new AesGcmFileEncryptionService(Base64.getDecoder().decode(base64Key));
    }

    private static void validateKeyLength(byte[] key) {
        if (key == null || (key.length != 16 && key.length != 24 && key.length != 32)) {
            throw new IllegalArgumentException(
                "Invalid AES key length: expected 16, 24 or 32 bytes (AES-128/192/256) but got "
                + (key == null ? "null" : key.length + " bytes")
            );
        }
    }

    @Override
    public InputStream encryptStream(InputStream plainData) throws IOException {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));

            byte[] header = new byte[MAGIC.length + IV_LENGTH];
            System.arraycopy(MAGIC, 0, header, 0, MAGIC.length);
            System.arraycopy(iv, 0, header, MAGIC.length, IV_LENGTH);

            InputStream cipherStream = new CipherInputStream(plainData, cipher);
            return new SequenceInputStream(new ByteArrayInputStream(header), cipherStream);
        } catch (GeneralSecurityException e) {
            throw new IOException("Could not initialize the encryption cipher", e);
        }
    }

    @Override
    public InputStream decryptStream(InputStream storedData) {
        try {
            PushbackInputStream pushbackStream = new PushbackInputStream(storedData, MAGIC.length);
            byte[] magic = new byte[MAGIC.length];
            int readMagic = readFully(pushbackStream, magic);
            if (readMagic != MAGIC.length || !Arrays.equals(magic, MAGIC)) {
                // legacy unencrypted file: restore the consumed bytes and return the stream as-is
                if (readMagic > 0) {
                    pushbackStream.unread(magic, 0, readMagic);
                }
                return pushbackStream;
            }

            byte[] iv = new byte[IV_LENGTH];
            if (readFully(pushbackStream, iv) != IV_LENGTH) {
                throw new IOException("Invalid encrypted file header: could not read the IV");
            }

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            return new CipherInputStream(pushbackStream, cipher);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not decrypt the file data", e);
        } catch (GeneralSecurityException e) {
            throw new UncheckedIOException(new IOException("Could not initialize the decryption cipher", e));
        }
    }

    private static int readFully(InputStream inputStream, byte[] buffer) throws IOException {
        int total = 0;
        while (total < buffer.length) {
            int read = inputStream.read(buffer, total, buffer.length - total);
            if (read < 0) {
                break;
            }
            total += read;
        }
        return total;
    }
}

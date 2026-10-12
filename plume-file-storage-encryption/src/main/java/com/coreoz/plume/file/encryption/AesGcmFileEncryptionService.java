package com.coreoz.plume.file.encryption;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.SequenceInputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Default {@link FileEncryptionService} implementation using AES in GCM mode (authenticated
 * encryption).
 * <p>
 * The encrypted stream starts with a small header so that stored files can be identified:
 * <pre>
 *   [ 3 magic bytes "PFE" ][ 1 format version byte ][ 12 bytes IV ][ GCM ciphertext + auth tag ]
 * </pre>
 * The format version byte makes it possible to evolve the on-disk format later (algorithm change,
 * key rotation, ...) while still being able to read files written by previous versions.
 * <p>
 * The file unique name is bound to the ciphertext as GCM <em>Additional Authenticated Data</em>
 * (AAD) together with the format version. This authenticates the header and prevents an attacker
 * with write access to the storage from swapping one encrypted file for another one that was
 * legitimately encrypted with the same key (a substitution/relocation attack): decryption of a
 * file under a different name (or a tampered version byte) fails with an authentication error.
 * <p>
 * When decrypting, a stream that does not start with the magic header is considered to be a
 * legacy, unencrypted file and is returned as-is. This allows encrypted and plain files to
 * coexist, for instance when encryption is enabled on an existing storage.
 */
public class AesGcmFileEncryptionService implements FileEncryptionService {
    private static final byte[] MAGIC = {'P', 'F', 'E'};
    private static final byte FORMAT_VERSION = 1;
    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final int HEADER_LENGTH = MAGIC.length + 1 + IV_LENGTH;
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
    public InputStream encryptStream(InputStream plainData, String fileUniqueName) throws IOException {
        try {
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            cipher.updateAAD(associatedData(FORMAT_VERSION, fileUniqueName));

            byte[] header = ByteBuffer.allocate(HEADER_LENGTH)
                .put(MAGIC)
                .put(FORMAT_VERSION)
                .put(iv)
                .array();

            InputStream cipherStream = new CipherInputStream(plainData, cipher);
            return new SequenceInputStream(new ByteArrayInputStream(header), cipherStream);
        } catch (GeneralSecurityException e) {
            throw new IOException("Could not initialize the encryption cipher", e);
        }
    }

    @Override
    public InputStream decryptStream(InputStream storedData, String fileUniqueName) {
        try {
            PushbackInputStream pushbackStream = new PushbackInputStream(storedData, MAGIC.length);
            byte[] magic = new byte[MAGIC.length];
            int readMagic = pushbackStream.readNBytes(magic, 0, magic.length);
            if (readMagic != MAGIC.length || !Arrays.equals(magic, MAGIC)) {
                // legacy unencrypted file: restore the consumed bytes and return the stream as-is
                if (readMagic > 0) {
                    pushbackStream.unread(magic, 0, readMagic);
                }
                return pushbackStream;
            }

            int version = pushbackStream.read();
            if (version != FORMAT_VERSION) {
                throw new IOException("Unsupported encrypted file format version: " + version);
            }

            byte[] iv = new byte[IV_LENGTH];
            if (pushbackStream.readNBytes(iv, 0, iv.length) != IV_LENGTH) {
                throw new IOException("Invalid encrypted file header: could not read the IV");
            }

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
            cipher.updateAAD(associatedData((byte) version, fileUniqueName));
            return new CipherInputStream(pushbackStream, cipher);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not decrypt the file data", e);
        } catch (GeneralSecurityException e) {
            throw new UncheckedIOException(new IOException("Could not initialize the decryption cipher", e));
        }
    }

    /**
     * Builds the Additional Authenticated Data (AAD) bound to the ciphertext: the format version
     * followed by the UTF-8 bytes of the file unique name. This authenticates the header version
     * and ties the ciphertext to its logical file name.
     */
    private static byte[] associatedData(byte version, String fileUniqueName) {
        byte[] name = fileUniqueName == null
            ? new byte[0]
            : fileUniqueName.getBytes(StandardCharsets.UTF_8);
        return ByteBuffer.allocate(1 + name.length)
            .put(version)
            .put(name)
            .array();
    }
}

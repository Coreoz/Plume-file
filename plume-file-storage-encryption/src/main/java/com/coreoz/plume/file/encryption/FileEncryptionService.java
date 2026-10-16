package com.coreoz.plume.file.encryption;

import java.io.IOException;
import java.io.InputStream;

/**
 * Encrypts and decrypts file data streams.
 * <p>
 * This is the extension point for the encryption strategy used by {@link EncryptingFileStorageService}.
 * The default implementation is {@link AesGcmFileEncryptionService} (AES-GCM authenticated
 * encryption), but any custom strategy (another algorithm, a KMS/HSM backed key, key rotation, etc.)
 * can be provided by implementing this interface and binding it in the dependency injection
 * container.
 * <p>
 * Implementations must be streaming oriented (they must not buffer the whole file in memory) and
 * are expected to be thread-safe so that a single instance can be shared as a singleton.
 */
public interface FileEncryptionService {
    /**
     * Wrap a plaintext stream so that reading from the returned stream produces the encrypted
     * representation of the data.
     * <p>
     * The {@code fileUniqueName} identifies the file being stored. Implementations may bind it to
     * the ciphertext (for instance as authenticated associated data) so that an encrypted file
     * cannot be substituted for another one; the same value must be provided to
     * {@link #decryptStream(InputStream, String)}.
     *
     * @param plainData the plaintext data stream
     * @param fileUniqueName the unique name of the file being stored
     * @return a stream producing the encrypted data
     * @throws IOException if the encryption could not be initialized
     */
    InputStream encryptStream(InputStream plainData, String fileUniqueName) throws IOException;

    /**
     * Wrap a stored stream so that reading from the returned stream produces the decrypted
     * plaintext.
     * <p>
     * Implementations are encouraged to support reading back data that was stored before
     * encryption was enabled (for instance by detecting a format marker), so that encrypted and
     * plain files can coexist.
     * <p>
     * The {@code fileUniqueName} must be the same value that was passed to
     * {@link #encryptStream(InputStream, String)} when the file was stored.
     *
     * @param storedData the stored (possibly encrypted) data stream
     * @param fileUniqueName the unique name of the file being fetched
     * @return a stream producing the decrypted plaintext
     */
    InputStream decryptStream(InputStream storedData, String fileUniqueName);
}

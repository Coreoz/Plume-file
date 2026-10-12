package com.coreoz.plume.file.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.coreoz.plume.file.services.storage.FileStorageService;

/**
 * A {@link FileStorageService} decorator that transparently encrypts file data before delegating
 * the storage and decrypts it when the data is fetched back.
 * <p>
 * This keeps the encryption concern out of the core and out of the underlying storage
 * implementation: any {@link FileStorageService} can be wrapped to add application level
 * encryption.
 */
public class EncryptingFileStorageService implements FileStorageService {
    private final FileStorageService delegate;
    private final FileEncryptionService encryptionService;

    public EncryptingFileStorageService(
        FileStorageService delegate,
        FileEncryptionService encryptionService
    ) {
        this.delegate = delegate;
        this.encryptionService = encryptionService;
    }

    @Override
    public void add(String fileUniqueName, InputStream fileData) throws IOException {
        delegate.add(fileUniqueName, encryptionService.encryptStream(fileData, fileUniqueName));
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName) {
        return delegate.fetch(fileUniqueName)
            .map(storedData -> encryptionService.decryptStream(storedData, fileUniqueName));
    }

    @Override
    public void deleteAll(List<String> fileUniqueNames) throws IOException {
        delegate.deleteAll(fileUniqueNames);
    }
}

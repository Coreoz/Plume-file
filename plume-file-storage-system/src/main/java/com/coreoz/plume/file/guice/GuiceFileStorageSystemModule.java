package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.configuration.FileStorageConfigurationService;
import com.coreoz.plume.file.encryption.AesGcmFileEncryptionService;
import com.coreoz.plume.file.encryption.EncryptingFileStorageService;
import com.coreoz.plume.file.encryption.FileEncryptionService;
import com.coreoz.plume.file.service.FileStorageSystemService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import jakarta.inject.Singleton;

public class GuiceFileStorageSystemModule extends AbstractModule {

    @Override
    protected void configure() {
        // FileStorageSystemService is instantiated through its @Inject constructor
    }

    /**
     * Expose the {@link FileStorageService} used by the application.
     * By default the files stored on disk are encrypted; encryption can be disabled explicitly
     * through the {@code file.storage.encryption.disabled} configuration.
     */
    @Provides
    @Singleton
    FileStorageService fileStorageService(
        FileStorageSystemService diskStorageService,
        FileStorageConfigurationService configurationService
    ) {
        if (!configurationService.isEncryptionEnabled()) {
            return diskStorageService;
        }
        String keyBase64 = configurationService.encryptionKeyBase64().orElseThrow(() -> new IllegalStateException(
            "File storage encryption is enabled but no encryption key is configured. "
            + "Set 'file.storage.encryption.key' (Base64 encoded AES key) "
            + "or disable encryption with 'file.storage.encryption.disabled = true'."
        ));
        FileEncryptionService encryptionService = AesGcmFileEncryptionService.fromBase64Key(keyBase64);
        return new EncryptingFileStorageService(diskStorageService, encryptionService);
    }
}

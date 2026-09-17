package com.coreoz.plume.file.guice;

import com.coreoz.plume.file.signed.guice.GuiceSignedFileUrlModule;
import com.google.inject.AbstractModule;

/**
 * Wires the signed temporary download URL services only.
 * The {@code FileDownloadJerseyService} binding is intentionally <b>not</b>
 * declared here: the user chooses explicitly which download implementation to
 * use by installing either {@code GuiceFileDownloadModule} (no cache) or
 * {@code GuiceFileCacheDownloadModule} (cache) from the
 * {@code plume-file-web-download-jersey-common} module.
 */
public class GuiceSignedFileDownloadModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new GuiceSignedFileUrlModule());
    }
}

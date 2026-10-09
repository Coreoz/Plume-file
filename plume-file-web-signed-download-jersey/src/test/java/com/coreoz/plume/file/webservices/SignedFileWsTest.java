package com.coreoz.plume.file.webservices;

import com.coreoz.plume.file.download.jersey.FileDownloadJerseyService;
import com.coreoz.plume.file.download.jersey.configuration.FileDownloadConfigurationService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.signed.SignedFileUrlService;
import com.coreoz.plume.file.signed.SignedFileUrlSigner;
import com.coreoz.plume.file.signed.configuration.SignedFileUrlConfigurationService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SignedFileWsTest {

    private SignedFileUrlService signedFileUrlService;
    private SignedFileWs signedFileWs;
    private TestFileDownloadJerseyService fileDownloadJerseyService;

    @BeforeEach
    void setup() {
        Config urlConfig = ConfigFactory.parseString(
            "file.signed-url.secret-key = \"test-secret-key-1234567890-test-secret-key\"\n" +
            "file.signed-url.default-validity-duration = \"1 hour\"\n" +
            "file.signed-url.base-path = \"/api/files\""
        );
        SignedFileUrlConfigurationService signedUrlConfigService = new SignedFileUrlConfigurationService(urlConfig);
        SignedFileUrlSigner signer = new SignedFileUrlSigner(signedUrlConfigService);
        this.signedFileUrlService = new SignedFileUrlService(signer, signedUrlConfigService, Clock.systemUTC());

        Config wsConfig = ConfigFactory.parseString("file.cache.http.max-age = \"365 days\"");
        FileDownloadConfigurationService fileDownloadConfig = new FileDownloadConfigurationService(wsConfig);

        this.fileDownloadJerseyService = new TestFileDownloadJerseyService();
        this.signedFileWs = new SignedFileWs(
            this.fileDownloadJerseyService,
            this.signedFileUrlService,
            fileDownloadConfig
        );
    }

    @Test
    void fetch_should_fail_with_403_if_query_params_are_missing() {
        Response response = this.signedFileWs.fetch("some-file.pdf", null, null, false, null);
        assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        response = this.signedFileWs.fetch("some-file.pdf", 123456L, null, false, null);
        assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());

        response = this.signedFileWs.fetch("some-file.pdf", null, "sig", false, null);
        assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
    }

    @Test
    void fetch_should_fail_with_403_if_url_is_expired() {
        String uniqueName = "123456789012345678901234567890123456.pdf"; // 36 chars UUID + extension
        long pastExpiresAt = Instant.now().minusSeconds(10).getEpochSecond();
        String expiredUrl = this.signedFileUrlService.url(uniqueName, Instant.ofEpochSecond(pastExpiresAt));

        // Parse signature
        String signature = expiredUrl.substring(expiredUrl.indexOf("signature=") + 10);

        Response response = this.signedFileWs.fetch(uniqueName, pastExpiresAt, signature, false, null);
        assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
    }

    @Test
    void fetch_should_fail_with_403_if_signature_is_invalid() {
        String uniqueName = "123456789012345678901234567890123456.pdf";
        long expiresAt = Instant.now().plusSeconds(3600).getEpochSecond();

        Response response = this.signedFileWs.fetch(uniqueName, expiresAt, "invalid-sig", false, null);
        assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
    }

    @Test
    void fetch_should_succeed_and_return_file_data_with_headers_if_valid() {
        String uniqueName = "123456789012345678901234567890123456.pdf";
        long expiresAt = Instant.now().plusSeconds(3600).getEpochSecond();
        String url = this.signedFileUrlService.url(uniqueName, Instant.ofEpochSecond(expiresAt));
        String signature = url.substring(url.indexOf("signature=") + 10);

        // Setup mock file
        TestFileMetadata metadata = new TestFileMetadata(
            uniqueName, "original-name.pdf", "pdf", "application/pdf", "checksum123"
        );
        this.fileDownloadJerseyService.setMetadata(metadata);
        byte[] data = "Hello World".getBytes();
        this.fileDownloadJerseyService.setData(new ByteArrayInputStream(data));

        Response response = this.signedFileWs.fetch(uniqueName, expiresAt, signature, true, null);
        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        assertThat(response.getHeaderString("Content-Type")).isEqualTo("application/pdf");
        assertThat(response.getHeaderString("ETag")).isEqualTo("checksum123");
        assertThat(response.getHeaderString("Content-Disposition")).contains("original-name.pdf");
    }

    @Test
    void fetch_should_fail_with_404_if_file_not_found() {
        String uniqueName = "123456789012345678901234567890123456.pdf";
        long expiresAt = Instant.now().plusSeconds(3600).getEpochSecond();
        String url = this.signedFileUrlService.url(uniqueName, Instant.ofEpochSecond(expiresAt));
        String signature = url.substring(url.indexOf("signature=") + 10);

        Response response = this.signedFileWs.fetch(uniqueName, expiresAt, signature, false, null);
        assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void fetch_should_fail_with_404_if_extension_mismatch() {
        String uniqueName = "123456789012345678901234567890123456.pdf";
        long expiresAt = Instant.now().plusSeconds(3600).getEpochSecond();
        String url = this.signedFileUrlService.url(uniqueName, Instant.ofEpochSecond(expiresAt));
        String signature = url.substring(url.indexOf("signature=") + 10);

        // Setup metadata with a different extension (e.g. png)
        TestFileMetadata metadata = new TestFileMetadata(
            uniqueName, "original-name.png", "png", "image/png", "checksum123"
        );
        this.fileDownloadJerseyService.setMetadata(metadata);

        Response response = this.signedFileWs.fetch(uniqueName, expiresAt, signature, false, null);
        assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
    }

    private static class TestFileDownloadJerseyService implements FileDownloadJerseyService {
        private FileMetadata metadata;
        private InputStream data;

        public void setMetadata(FileMetadata metadata) {
            this.metadata = metadata;
        }

        public void setData(InputStream data) {
            this.data = data;
        }

        @Override
        public Optional<FileMetadata> fetchMetadata(String fileUniqueName) {
            return Optional.ofNullable(metadata);
        }

        @Override
        public Optional<InputStream> fetchData(String fileUniqueName) {
            return Optional.ofNullable(data);
        }
    }

    private static class TestFileMetadata implements FileMetadata {
        private final String uniqueName;
        private final String originalName;
        private final String fileExtension;
        private final String mimeType;
        private final String checksum;

        public TestFileMetadata(String uniqueName, String originalName, String fileExtension, String mimeType, String checksum) {
            this.uniqueName = uniqueName;
            this.originalName = originalName;
            this.fileExtension = fileExtension;
            this.mimeType = mimeType;
            this.checksum = checksum;
        }

        @Override
        public String getUniqueName() { return uniqueName; }
        @Override
        public String getFileOriginalName() { return originalName; }
        @Override
        public String getFileType() { return "TEST"; }
        @Override
        public String getFileExtension() { return fileExtension; }
        @Override
        public String getMimeType() { return mimeType; }
        @Override
        public String getChecksum() { return checksum; }
        @Override
        public Long getFileSize() { return 11L; }
        @Override
        public Instant getCreationDate() { return Instant.now(); }
    }
}

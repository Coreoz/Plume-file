package com.coreoz.plume.file;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.metadata.FileMetadataService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileUploadTestModule.class)
public class FileUploadWebJerseyServiceTest {

    @Inject
    FileConfigurationService fileConfigurationService;
    FileUploadWebJerseyService fileUploadWebJerseyService;

    @Before
    public void before_test() {
        FileMetadataService fileMetadataService = new FileMetadataServiceTest();
        FileStorageService fileStorageService = new FileStorageServiceTest();
        this.fileUploadWebJerseyService = new FileUploadWebJerseyService(
            new FileService(fileMetadataService, fileStorageService, fileConfigurationService)
        );
    }

    @Test
    public void add_file_with_all_metadata_should_not_fail() throws IOException {
        FormDataBodyPart formDataBodyPart = new FormDataBodyPart();
        FormDataContentDisposition formDataContentDisposition = FormDataContentDisposition.name("test")
            .fileName("File Name")
            .size(12)
            .build();
        formDataBodyPart.setFormDataContentDisposition(formDataContentDisposition);
        formDataBodyPart.setMediaType(MediaType.TEXT_XML_TYPE);

        String uid = this.fileUploadWebJerseyService.add(
            TestFileType.TEST,
            new ByteArrayInputStream(new byte[127]),
            formDataBodyPart
        );

        Assert.assertNotNull(uid);
    }

    @Test(expected = RuntimeException.class)
    public void add_file_with_no_file_mime_type_should_fail() throws IOException {
        FormDataBodyPart formDataBodyPart = new FormDataBodyPart();
        FormDataContentDisposition formDataContentDisposition = FormDataContentDisposition.name("test")
            .fileName("File Name")
            .size(12)
            .build();
        formDataBodyPart.setFormDataContentDisposition(formDataContentDisposition);
        formDataBodyPart.setMediaType(null);

        this.fileUploadWebJerseyService.add(
            TestFileType.TEST,
            new ByteArrayInputStream(new byte[127]),
            formDataBodyPart
        );
    }

    @Test(expected = RuntimeException.class)
    public void add_file_with_no_file_should_fail() throws IOException {
        FormDataBodyPart formDataBodyPart = new FormDataBodyPart();

        this.fileUploadWebJerseyService.add(
            TestFileType.TEST,
            null,
            formDataBodyPart
        );
    }

    @Test(expected = RuntimeException.class)
    public void add_file_with_no_file_type_should_fail() throws IOException {
        FormDataBodyPart formDataBodyPart = new FormDataBodyPart();

        this.fileUploadWebJerseyService.add(
            null,
            new ByteArrayInputStream(new byte[127]),
            formDataBodyPart
        );
    }

    private static class FileMetadataServiceTest implements FileMetadataService {

        @Override
        public void add(String fileUniqueName, String originalName, String fileType, String fileExtension, String mimeType) {
            // empty method
        }

        @Override
        public void updateFileSizeAndChecksum(String fileUniqueName, long fileSize, String checksum) {
            // empty method
        }

        @Override
        public Optional<FileMetadata> fetch(String fileUniqueName) {
            return Optional.empty();
        }

        @Override
        public List<String> findUnreferencedFiles() {
            return null;
        }

        @Override
        public void deleteAll(List<String> fileUniqueNamesDeleted) {
            // empty method
        }
    }

    private static class FileStorageServiceTest implements FileStorageService {
        @Override
        public void add(String fileUniqueName, InputStream fileData) {
        }

        @Override
        public Optional<InputStream> fetch(String fileUniqueName) {
            return Optional.empty();
        }

        @Override
        public void deleteAll(List<String> fileUniqueNames) {
        }
    }

    private enum TestFileType implements FileType {
        TEST;
    }
}

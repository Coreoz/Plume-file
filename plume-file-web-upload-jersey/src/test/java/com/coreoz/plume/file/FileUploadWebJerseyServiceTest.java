package com.coreoz.plume.file;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.metadata.FileMetadataService;
import com.coreoz.plume.file.services.mimetype.MimeTypesDetector;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.coreoz.plume.file.validator.FileUploadData;
import com.coreoz.plume.file.validator.FileUploadValidator;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileUploadTestModule.class)
public class FileUploadWebJerseyServiceTest {
    @Inject
    FileConfigurationService fileConfigurationService;
    FileUploadWebJerseyService fileUploadWebJerseyService;
    MimeTypesDetector mimeTypesDetector;

    @Before
    public void before_test() throws NoSuchAlgorithmException {
        FileMetadataService fileMetadataService = new FileMetadataServiceTest();
        FileStorageService fileStorageService = new FileStorageServiceTest();
        this.mimeTypesDetector = new MimeTypesDetector();
        this.fileUploadWebJerseyService = new FileUploadWebJerseyService(
            new FileService(fileMetadataService, fileStorageService, mimeTypesDetector, fileConfigurationService)
        );
    }

    private FileUploadData makeUploadData(FormDataBodyPart formDataBodyPart) {
        return ((FileUploadValidator) FileUploadValidator
            .from(formDataBodyPart, InputStream.nullInputStream(), mimeTypesDetector))
            .finish();
    }

    @Test
    public void add_file_with_all_metadata_should_not_fail() {
        FormDataBodyPart formDataBodyPart = new FormDataBodyPart();
        FormDataContentDisposition formDataContentDisposition = FormDataContentDisposition.name("test")
            .fileName("File Name")
            .size(12)
            .build();
        formDataBodyPart.setFormDataContentDisposition(formDataContentDisposition);
        formDataBodyPart.setMediaType(MediaType.TEXT_XML_TYPE);

        String uid = this.fileUploadWebJerseyService.add(
            TestFileType.TEST,
            makeUploadData(formDataBodyPart)
        );

        Assert.assertNotNull(uid);
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
        TEST
    }
}

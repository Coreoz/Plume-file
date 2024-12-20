package com.coreoz.plume.file;

import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.metadata.FileMetadataService;
import com.coreoz.plume.file.services.mimetype.MimeTypesDetector;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.coreoz.plume.file.validator.FileUploadData;
import com.coreoz.plume.file.validator.FileUploadValidator;
import com.coreoz.test.GuiceTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@GuiceTest(FileUploadTestModule.class)
public class FileUploadWebJerseyServiceTest {
    @Inject
    FileConfigurationService fileConfigurationService;
    FileUploadWebJerseyService fileUploadWebJerseyService;
    MimeTypesDetector mimeTypesDetector;

    @BeforeEach
    public void before_test() throws NoSuchAlgorithmException {
        FileMetadataService fileMetadataService = new FileMetadataServiceTest();
        FileStorageService fileStorageService = new FileStorageServiceTest();
        this.mimeTypesDetector = new MimeTypesDetector();
        this.fileUploadWebJerseyService = new FileUploadWebJerseyService(
            new FileService(fileMetadataService, fileStorageService, mimeTypesDetector, fileConfigurationService)
        );
    }

    private FileUploadData makeUploadData(String fileName, long size) {
        return ((FileUploadValidator) FileUploadValidator
            .from(fileName, size, InputStream.nullInputStream(), mimeTypesDetector))
            .finish();
    }

    @Test
    public void add_file_with_all_metadata_should_not_fail() {
        String uid = this.fileUploadWebJerseyService.add(
            TestFileType.TEST,
            makeUploadData("File Name", 12)
        );

        Assertions.assertThat(uid).isNotNull();
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
        public List<String> findFilesHavingDeletedTypes() {
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

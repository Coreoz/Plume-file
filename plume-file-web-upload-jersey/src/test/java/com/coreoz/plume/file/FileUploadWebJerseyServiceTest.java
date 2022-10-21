package com.coreoz.plume.file;

import com.coreoz.plume.file.services.FileService;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.metadata.FileMetadataService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class FileUploadWebJerseyServiceTest {

    FileUploadWebJerseyService fileUploadWebJerseyService = new FileUploadWebJerseyService(new FileServiceTest());

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
            new ByteArrayInputStream(new byte[127]),
            formDataBodyPart
        );

        Assert.assertNotNull(uid);
    }

    @Test(expected = RuntimeException.class)
    public void add_file_with_no_file_mime_type_should_fail() {
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
    public void add_file_with_no_file_should_fail() {
        FormDataBodyPart formDataBodyPart = new FormDataBodyPart();

        this.fileUploadWebJerseyService.add(
            TestFileType.TEST,
            null,
            formDataBodyPart
        );
    }

    @Test(expected = RuntimeException.class)
    public void add_file_with_no_file_type_should_fail() {
        FormDataBodyPart formDataBodyPart = new FormDataBodyPart();

        this.fileUploadWebJerseyService.add(
            null,
            new ByteArrayInputStream(new byte[127]),
            formDataBodyPart
        );
    }

    private static class FileServiceTest extends FileService {
        public FileServiceTest() {
            super(new FileMetadataServiceTest(), new FileStorageServiceTest());
        }
    }

    private static class FileMetadataServiceTest implements FileMetadataService {

        @Override
        public void add(String fileUniqueName, String originalName, String fileType, String fileExtension, String mimeType, long fileSize) {
            // empty method
        }

        @Override
        public void updateFileSize(String fileUniqueName, long fileSize) {
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
        public long add(String fileUniqueName, InputStream fileData) {
            return 0;
        }

        @Override
        public Optional<InputStream> fetch(String fileUniqueName) {
            return Optional.empty();
        }

        @Override
        public List<String> deleteAll(List<String> fileUniqueNames) {
            return null;
        }
    }

    private enum TestFileType implements FileType {
        TEST;
    }
}

package com.coreoz.plume.file.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.coreoz.test.GuiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.coreoz.plume.file.db.FileMetadataDatabaseDao;
import com.coreoz.plume.file.db.beans.FileMetadataQuerydsl;
import com.coreoz.plume.file.filetype.FileTypeDatabase;
import com.coreoz.plume.file.filetype.FileTypesProvider;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.metadata.FileMetadataService;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.StringPath;

@GuiceTest(FileTestModule.class)
public class FileMetadataDatabaseServiceTest {
    private FileMetadataService fileMetadataService;

    @BeforeEach
    public void init_metadata_service() {
        this.fileMetadataService = new FileMetadataDatabaseService(this.mockDao, new TestTypeProvider());
    }

    @Test
    public void fetch_file_with_known_unique_file_name_must_return_object() {
        Optional<? extends FileMetadata> fileMetadata = this.fileMetadataService.fetch("random-uid-to-fetch");
        Assertions.assertTrue(fileMetadata.isPresent());
        Assertions.assertEquals("TEST", fileMetadata.get().getFileType());
        Assertions.assertEquals("application/pdf", fileMetadata.get().getMimeType());
    }

    @Test
    public void fetch_file_with_unknown_unique_file_name_must_return_empty() {
        Optional<? extends FileMetadata> fileMetadata = this.fileMetadataService.fetch("unknown-uid-to-fetch");
        Assertions.assertFalse(fileMetadata.isPresent());
    }

    @Test
    public void upload_file_must_return_valid_id() {
        this.fileMetadataService.add("unknown-uid-to-fetch", "original_name", "TEST", "pdf", "application/pdf");
        Assertions.assertTrue(true);
    }

    @Test
    public void unreferenced_files_must_return_valid_list() {
        List<String> unreferencedFiles = this.fileMetadataService.findUnreferencedFiles();
        Assertions.assertEquals(0, unreferencedFiles.size());
    }

    @Test
    public void unreferenced_files_must_not_fail() {
        this.fileMetadataService.deleteAll(Collections.singletonList("random-uid-to-fetch"));
        Assertions.assertTrue(true);
    }

    private final FileMetadataDatabaseDao mockDao = new FileMetadataDatabaseDao(null) {
        @Override
        public FileMetadataQuerydsl add(String fileUniqueName, String originalName, String fileType, String fileExtension, String mimeType) {
            FileMetadataQuerydsl fileMetadataQuerydsl = new FileMetadataQuerydsl();
            fileMetadataQuerydsl.setFileType(fileType);
            fileMetadataQuerydsl.setMimeType(mimeType);
            fileMetadataQuerydsl.setUniqueName(fileUniqueName);
            return fileMetadataQuerydsl;
        }

        @Override
        public Optional<FileMetadata> fetch(String fileUniqueName) {
            if (!"random-uid-to-fetch".equals(fileUniqueName)) {
                return Optional.empty();
            }
            FileMetadataQuerydsl fileMetadataQuerydsl = new FileMetadataQuerydsl();
            fileMetadataQuerydsl.setUniqueName(fileUniqueName);
            fileMetadataQuerydsl.setFileType("TEST");
            fileMetadataQuerydsl.setMimeType("application/pdf");
            return Optional.of(fileMetadataQuerydsl);
        }

        @Override
        public List<String> findUnreferencedFiles(Collection<FileTypeDatabase> fileTypes) {
            return new ArrayList<>();
        }

        @Override
        public List<String> findFilesHavingDeletedTypes(Collection<FileTypeDatabase> remainingFileTypes) {
            return new ArrayList<>();
        }

        @Override
        public long deleteFilesMetadata(List<String> fileUniqueNames) {
            return 0;
        }
    };

    private static class TestTypeProvider implements FileTypesProvider {

        @Override
        public Collection<FileTypeDatabase> fileTypesAvailable() {
            return Arrays.asList(TestFileType.values());
        }
    }

    private enum TestFileType implements FileTypeDatabase {
        TEST;

        @Override
        public EntityPath<?> getFileEntity() {
            return null;
        }

        @Override
        public StringPath getJoinColumn() {
            return null;
        }
    }
}

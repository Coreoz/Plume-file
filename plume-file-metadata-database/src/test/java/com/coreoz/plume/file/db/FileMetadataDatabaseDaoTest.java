package com.coreoz.plume.file.db;

import com.coreoz.plume.file.filetype.FileTypeDatabase;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.test.GuiceTest;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.StringPath;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@GuiceTest(FileTestModule.class)
public class FileMetadataDatabaseDaoTest {
    @Inject
    private FileMetadataDatabaseDao fileMetadataDao;

    @Test
    public void fetch_should_return_value_if_present_in_database() {
        String uid = "7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1";
        Optional<FileMetadata> fileMetadata = fileMetadataDao.fetch(uid);
        Assertions.assertTrue(fileMetadata.isPresent());
        Assertions.assertEquals(fileMetadata.get().getUniqueName(), uid);
    }

    @Test
    public void add_should_not_fail() {
        String uid = "c70f9b94-30e2-4e10-b84d-b964ef972067";
        fileMetadataDao.add(uid, "original_name", "TEST", "pdf", "application/pdf");
        Assertions.assertTrue(true);
    }

    @Test
    public void delete_should_not_fail() {
        String uid = "846c36cc-f973-11e8-8eb2-f2801f1b9fd1";
        long deleted = fileMetadataDao.deleteFilesMetadata(Collections.singletonList(uid));
        Assertions.assertEquals(1, deleted);
    }

    @Test
    public void update_file_size_should_update_file_size() {
        String uid = "c70f9b94-30e2-4e10-b84d-b964ef972067";
        fileMetadataDao.updateFileSizeAndChecksum(uid, 1L, "sha256");

        FileMetadata metadata = fileMetadataDao.fetch(uid).orElse(null);
        Assertions.assertNotNull(metadata);
        Assertions.assertEquals(1, metadata.getFileSize().intValue());
    }

    @Test
    public void find_unreferenced_files_should_not_be_empty() {
        List<String> unreferencedFiles = fileMetadataDao.findUnreferencedFiles(List.of(TestFileType.TEST));

        Assertions.assertFalse(unreferencedFiles.isEmpty());
    }

    @Test
    public void find_files_having_unused_known_type_should_return_1() {
        List<String> unreferencedFiles = fileMetadataDao.findFilesHavingDeletedTypes(List.of(TestFileType.TEST));

        // see file V1__file_setup.sql, there is only one file with the type TEST_DELETED
        Assertions.assertEquals(1, unreferencedFiles.size());
    }

    private enum TestFileType implements FileTypeDatabase {
        TEST;

        @Override
        public EntityPath<?> getFileEntity() {
            return QUserFile.userFile;
        }

        @Override
        public StringPath getJoinColumn() {
            return QUserFile.userFile.uniqueName;
        }
    }
}

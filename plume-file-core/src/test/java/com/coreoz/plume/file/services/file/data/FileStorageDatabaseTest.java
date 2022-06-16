package com.coreoz.plume.file.services.file.data;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.dao.FileDatabaseDao;
import com.coreoz.plume.file.services.file.FileStorageDatabase;
import com.coreoz.plume.file.services.file.FileTestModule;
import com.coreoz.plume.file.services.filetype.FileType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestModule.class)
public class FileStorageDatabaseTest {

    private FileStorageDatabase fileStorageDatabase;

    @Before
    public void init_metadata_service() {
        this.fileStorageDatabase = new FileStorageDatabase(this.mockDao);
    }

    @Test
    public void fetch_file_with_known_unique_file_name_must_return_byte_array() {
        Optional<byte[]> fileMetadata = this.fileStorageDatabase.fetch(TestFileType.TEST.name(), "random-uid-to-fetch");
        Assert.assertTrue(fileMetadata.isPresent());
        Assert.assertEquals(0, fileMetadata.get().length);
    }

    @Test
    public void fetch_file_with_unknown_unique_file_name_must_return_empty() {
        Optional<byte[]> bytes = this.fileStorageDatabase.fetch(TestFileType.TEST.name(), "unknown-uid-to-fetch");
        Assert.assertFalse(bytes.isPresent());
    }

    @Test
    public void upload_file_should_not_fail() {
        this.fileStorageDatabase.upload(1L, "random-uid-to-fetch", new byte[0]);
        Assert.assertTrue(true);
    }

    @Test
    public void delete_valid_files_should_return_empty_list() {
        List<String> filesNotDeleted = this.fileStorageDatabase.deleteFiles(Collections.singletonList("random-uid-to-fetch"));
        Assert.assertTrue(filesNotDeleted.isEmpty());
    }

    @Test
    public void delete_not_valid_files_should_return_list() {
        List<String> filesNotDeleted = this.fileStorageDatabase.deleteFiles(Collections.singletonList("unknown-uid-to-fetch"));
        Assert.assertFalse(filesNotDeleted.isEmpty());
    }

    private final FileDatabaseDao mockDao = new FileDatabaseDao(null) {
        @Override
        public Optional<byte[]> fetch(String fileUniqueName) {
            if (!"random-uid-to-fetch".equals(fileUniqueName)) {
                return Optional.empty();
            }
            return Optional.of(new byte[0]);
        }

        @Override
        public void upload(Long fileId, byte[] fileData) {
            // not used
        }

        @Override
        public boolean delete(String fileUniqueName) {
            return "random-uid-to-fetch".equals(fileUniqueName);
        }
    };

    private enum TestFileType implements FileType {
        TEST;
    }
}

package com.coreoz.plume.file.service;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.FileStorageDao;
import com.coreoz.plume.file.services.data.MeasuredSizeInputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestModule.class)
public class FileStorageDatabaseServiceTest {
    private FileStorageDatabaseService fileStorageDatabase;

    @Before
    public void init_metadata_service() {
        this.fileStorageDatabase = new FileStorageDatabaseService(this.mockDao);
    }

    @Test
    public void fetch_file_with_known_unique_file_name_must_return_byte_array() throws IOException {
        Optional<MeasuredSizeInputStream> fileMetadata = this.fileStorageDatabase.fetch("random-uid-to-fetch");
        Assert.assertTrue(fileMetadata.isPresent());
        Assert.assertEquals(-1, fileMetadata.get().read());
    }

    @Test
    public void fetch_file_with_unknown_unique_file_name_must_return_empty() {
        Optional<MeasuredSizeInputStream> bytes = this.fileStorageDatabase.fetch("unknown-uid-to-fetch");
        Assert.assertFalse(bytes.isPresent());
    }

    @Test
    public void upload_file_should_not_fail() {
        this.fileStorageDatabase.add("random-uid-to-add", new MeasuredSizeInputStream(new ByteArrayInputStream(new byte[1])));
        Assert.assertTrue(true);
    }

    @Test
    public void delete_valid_files_should_return_empty_list() {
        List<String> filesNotDeleted = this.fileStorageDatabase.deleteAll(Collections.singletonList("random-uid-to-fetch"));
        Assert.assertTrue(filesNotDeleted.isEmpty());
    }

    @Test
    public void delete_not_valid_files_should_return_list() {
        List<String> filesNotDeleted = this.fileStorageDatabase.deleteAll(Collections.singletonList("unknown-uid-to-fetch"));
        Assert.assertFalse(filesNotDeleted.isEmpty());
    }

    private final FileStorageDao mockDao = new FileStorageDao(null) {
        @Override
        public Optional<MeasuredSizeInputStream> fetch(String fileUniqueName) {
            if (!"random-uid-to-fetch".equals(fileUniqueName)) {
                return Optional.empty();
            }
            return Optional.of(new MeasuredSizeInputStream(new ByteArrayInputStream(new byte[0])));
        }

        @Override
        public long add(String fileUniqueName, MeasuredSizeInputStream inputStream) {
            return 0;
        }

        @Override
        public boolean delete(String fileUniqueName) {
            return "random-uid-to-fetch".equals(fileUniqueName);
        }
    };
}

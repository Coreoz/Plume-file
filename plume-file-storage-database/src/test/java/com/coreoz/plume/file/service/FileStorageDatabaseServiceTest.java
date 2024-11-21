package com.coreoz.plume.file.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.coreoz.test.GuiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.coreoz.plume.file.db.FileStorageDao;

@GuiceTest(FileTestModule.class)
public class FileStorageDatabaseServiceTest {
    private FileStorageDatabaseService fileStorageDatabase;

    @BeforeEach
    public void init_metadata_service() {
        this.fileStorageDatabase = new FileStorageDatabaseService(this.mockDao);
    }

    @Test
    public void fetch_file_with_known_unique_file_name_must_return_byte_array() throws IOException {
        Optional<InputStream> fileMetadata = this.fileStorageDatabase.fetch("random-uid-to-fetch");
        Assertions.assertTrue(fileMetadata.isPresent());
        Assertions.assertEquals(-1, fileMetadata.get().read());
    }

    @Test
    public void fetch_file_with_unknown_unique_file_name_must_return_empty() {
        Optional<InputStream> bytes = this.fileStorageDatabase.fetch("unknown-uid-to-fetch");
        Assertions.assertFalse(bytes.isPresent());
    }

    @Test
    public void upload_file_should_not_fail() throws IOException {
        this.fileStorageDatabase.add("random-uid-to-add", new ByteArrayInputStream(new byte[1]));
        Assertions.assertTrue(true);
    }

    private final FileStorageDao mockDao = new FileStorageDao(null) {
        @Override
        public Optional<InputStream> fetch(String fileUniqueName) {
            if (!"random-uid-to-fetch".equals(fileUniqueName)) {
                return Optional.empty();
            }
            return Optional.of(new ByteArrayInputStream(new byte[0]));
        }

        @Override
        public void add(String fileUniqueName, InputStream inputStream) {
        }

        @Override
        public void deleteAll(List<String> fileUniqueNames) {
        }
    };
}

package com.coreoz.plume.file.db;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.coreoz.plume.file.service.FileTestModule;
import com.coreoz.test.GuiceTest;
import jakarta.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

@GuiceTest(FileTestModule.class)
public class FileStorageDaoTest {
    @Inject
    private FileStorageDao fileDao;

    @Test
    public void fetch_existing_file_should_return_the_file() {
        Optional<InputStream> file = fileDao.fetch("846c36cc-f973-11e8-8eb2-f2801f1b9fd1");
        Assertions.assertThat(file).isPresent();
    }

    @Test
    public void fetch_unknown_file_should_return_empty() {
        Optional<InputStream> file = fileDao.fetch("unknown");
        Assertions.assertThat(file).isEmpty();
    }

    @Test
    public void delete_existing_file_should_not_fail() {
        fileDao.deleteAll(List.of("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1"));
    }

    @Test
    public void delete_unknown_file_should_not_fail() {
        fileDao.deleteAll(List.of("unknown"));
    }

    @Test
    public void upload_should_not_fail() {
        fileDao.add("5yt69yuf-r879-zc5h-8eb2-g5469f1c9fd1", new ByteArrayInputStream(new byte[127]));
        Optional<InputStream> uploadedFile = fileDao.fetch("5yt69yuf-r879-zc5h-8eb2-g5469f1c9fd1");
        Assertions.assertThat(uploadedFile).isPresent();
    }

    @Test
    public void download_should_return_a_correct_file_data_stream() throws IOException {
    	InputStream data = new ByteArrayInputStream(new byte[] { 'A', 'B', 'C' });
        fileDao.add("5yt69yuf-r879-zc5h-8eb2-g5469f1c9fd2", data);
        Optional<InputStream> uploadedFile = fileDao.fetch("5yt69yuf-r879-zc5h-8eb2-g5469f1c9fd2");

        Assertions.assertThat(uploadedFile).isPresent();
        Assertions.assertThat(uploadedFile.get()).hasContent("ABC");
    }
}

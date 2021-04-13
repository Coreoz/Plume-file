package com.coreoz.plume.file.services.file;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.beans.FileEntryDisk;
import com.coreoz.plume.file.db.querydsl.disk.FileDaoDiskQuerydsl;
import com.coreoz.plume.file.services.cache.FileCacheServiceGuava;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.hash.ChecksumServiceSha1;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestModule.class)
public class FileServiceDiskTest {
    @Inject
    private FileConfigurationService configurationService;

    @Inject
    private ChecksumServiceSha1 checksumService;

    @Inject
    private FileCacheServiceGuava fileCacheService;

    @Test
    public void url__should_return_empty_if_id_file_is_null() {
        FileServiceDisk fileService = new FileServiceDisk(null, null, configurationService, checksumService, fileCacheService);

        assertThat(fileService.url((Long) null)).isEmpty();
        assertThat(fileService.url((String) null)).isEmpty();
    }

    @Test
    public void url__should_load_file_name_from_dao() {
        FileServiceDisk fileService = new FileServiceDisk(fileDaoMock(), null, configurationService, checksumService, fileCacheService);

        assertThat(fileService.url("846c36cc-f973-11e8-8eb2-f2801f1b9fd1"))
            .hasValue("/api/files/846c36cc-f973-11e8-8eb2-f2801f1b9fd1.ext");
    }

    @Test
    public void fetch__should_return_empty_if_no_file_exists() {
        FileServiceDisk fileService = new FileServiceDisk(
            fileDaoMock(), null, configurationService, checksumService, fileCacheService
        );

        assertThat(fileService.fetch("efaaeb68-f973-11e8-8eb2-f2801f1b9fd1")).isEmpty();
    }

    @Test
    public void fetch__should_return_empty_if_the_file_does_not_exist_on_db() {
        FileServiceDisk fileService = new FileServiceDisk(
            fileDaoMock(), null, configurationService, checksumService, fileCacheService
        );
        Optional<FileData> file = fileService.fetch(3L);
        assertThat(file).isEmpty();
    }

    private FileDaoDiskQuerydsl fileDaoMock() {
        return new FileDaoDiskQuerydsl(null) {
            @Override
            public Optional<String> fileName(String fileUid) {
                if ("846c36cc-f973-11e8-8eb2-f2801f1b9fd1".equals(fileUid)) {
                    return Optional.of("846c36cc-f973-11e8-8eb2-f2801f1b9fd1.ext");
                }
                if ("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1".equals(fileUid)) {
                    return Optional.of("");
                }
                return Optional.empty();
            }

            @Override
            public Optional<FileEntry> findByUid(String uid) {
                if ("846c36cc-f973-11e8-8eb2-f2801f1b9fd1".equals(uid)) {
                    FileEntryDisk fileEntryDisk = FileEntryDisk.of(
                        5L,
                        "846c36cc-f973-11e8-8eb2-f2801f1b9fd1",
                        "846c36cc-f973-11e8-8eb2-f2801f1b9fd1.ext",
                        null
                    );
                    return Optional.of(fileEntryDisk);
                }
                return Optional.empty();
            }

            @Override
            public Optional<FileEntry> findById(Long id) {
                if (5L == id) {
                    FileEntryDisk fileEntryDisk = FileEntryDisk.of(
                        5L,
                        "7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1",
                        "ext",
                        null
                    );
                    return Optional.of(fileEntryDisk);
                }
                return Optional.empty();
            }
        };
    }
}

package com.coreoz.plume.file.services.file.metadata;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.beans.FileMetadataQuerydsl;
import com.coreoz.plume.file.db.dao.FileMetadata;
import com.coreoz.plume.file.db.dao.FileMetadataDao;
import com.coreoz.plume.file.services.file.FileTestModule;
import com.coreoz.plume.file.services.filetype.FileType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestModule.class)
public class FileMetadataServiceTest {

    private FileMetadataService fileMetadataService;

    @Before
    public void init_metadata_service() {
        this.fileMetadataService = new FileMetadataDatabaseService(this.mockDao);
    }

    @Test
    public void fetch_file_with_known_unique_file_name_must_return_object() {
        Optional<FileMetadata> fileMetadata = this.fileMetadataService.fetch("random-uid-to-fetch");
        Assert.assertTrue(fileMetadata.isPresent());
        Assert.assertEquals("TEST", fileMetadata.get().getFileType());
        Assert.assertEquals("application/pdf", fileMetadata.get().getMimeType());
    }

    @Test
    public void fetch_file_with_unknown_unique_file_name_must_return_empty() {
        Optional<FileMetadata> fileMetadata = this.fileMetadataService.fetch("unknown-uid-to-fetch");
        Assert.assertFalse(fileMetadata.isPresent());
    }

    @Test
    public void upload_file_must_return_valid_id() {
        long fileId = this.fileMetadataService.upload("unknown-uid-to-fetch", "TEST", "application/pdf", 0);
        Assert.assertEquals(1L, fileId);
    }

    @Test
    public void unreferenced_files_must_return_valid_list() {
        List<String> unreferencedFiles = this.fileMetadataService.findUnreferencedFiles(Collections.singleton(TestFileType.TEST));
        Assert.assertEquals(0, unreferencedFiles.size());
    }

    @Test
    public void unreferenced_files_must_not_fail() {
        this.fileMetadataService.deleteFiles(Collections.singletonList("random-uid-to-fetch"));
        Assert.assertTrue(true);
    }

    private final FileMetadataDao mockDao = new FileMetadataDao(null) {
        @Override
        public FileMetadataQuerydsl upload(String fileUniqueName, String fileType, String mimeType, long fileSize) {
            FileMetadataQuerydsl fileMetadataQuerydsl = new FileMetadataQuerydsl();
            fileMetadataQuerydsl.setId(1L);
            fileMetadataQuerydsl.setFileSize(fileSize);
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
            return Optional.of(new FileMetadata("TEST", "application/pdf"));
        }

        @Override
        public List<String> findUnreferencedFiles(Collection<FileType> fileTypes) {
            return new ArrayList<>();
        }

        @Override
        public void deleteFilesMetadata(List<String> fileUniqueNames) {
            // unused
        }
    };

    private enum TestFileType implements FileType {
        TEST;
    }
}

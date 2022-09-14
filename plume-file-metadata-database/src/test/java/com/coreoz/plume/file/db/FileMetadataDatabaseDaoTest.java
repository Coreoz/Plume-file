package com.coreoz.plume.file.db;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestModule.class)
public class FileMetadataDatabaseDaoTest {
    @Inject
    private FileMetadataDatabaseDao fileMetadataDao;

    @Test
    public void fetch_should_return_value_if_present_in_database() {
        String uid = "7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1";
        Optional<FileMetadata> fileMetadata = fileMetadataDao.fetch(uid);
        Assert.assertTrue(fileMetadata.isPresent());
        Assert.assertEquals(fileMetadata.get().getUniqueName(), uid);
    }

    @Test
    public void add_should_not_fail() {
        String uid = "c70f9b94-30e2-4e10-b84d-b964ef972067";
        fileMetadataDao.add(uid, "original_name", "TEST", "pdf", "application/pdf", 12);
        Assert.assertTrue(true);
    }

    @Test
    public void delete_should_not_fail() {
        String uid = "846c36cc-f973-11e8-8eb2-f2801f1b9fd1";
        long deleted = fileMetadataDao.deleteFilesMetadata(Collections.singletonList(uid));
        Assert.assertEquals(1, deleted);
    }
}

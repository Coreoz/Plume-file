package com.coreoz.plume.file.db;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import com.coreoz.plume.file.db.dao.FileMetadata;
import com.coreoz.plume.file.db.dao.FileMetadataDao;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Optional;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestModule.class)
public class FileMetadataDaoTest {

    @Inject
    private FileMetadataDao fileMetadataDao;

    @Test
    public void fetch_should_return_value_if_present_in_database() {
        Optional<FileMetadata> fileMetadata = fileMetadataDao.fetch("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1");
    }
}

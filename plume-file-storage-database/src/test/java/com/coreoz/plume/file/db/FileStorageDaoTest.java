package com.coreoz.plume.file.db;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestModule.class)
public class FileStorageDaoTest {
    @Inject
    private FileStorageDao fileDao;

    @Test
    public void fetch_existing_file_should_return_the_file() throws IOException {
        Optional<InputStream> file = fileDao.fetch("846c36cc-f973-11e8-8eb2-f2801f1b9fd1");
        Assert.assertTrue(file.isPresent());
    }

    @Test
    public void fetch_unknown_file_should_return_empty() {
        Optional<InputStream> file = fileDao.fetch("unknown");
        Assert.assertFalse(file.isPresent());
    }

    @Test
    public void delete_existing_file_should_return_true() {
        boolean hasBeenDeleted = fileDao.delete("7b3cf3de-f973-11e8-8eb2-f2801f1b9fd1");
        Assert.assertTrue(hasBeenDeleted);
    }

    @Test
    public void delete_unknown_file_should_return_false() {
        boolean hasBeenDeleted = fileDao.delete("unknown");
        Assert.assertFalse(hasBeenDeleted);
    }

    @Test
    public void upload_should_not_fail() {
        fileDao.add("5yt69yuf-r879-zc5h-8eb2-g5469f1c9fd1", new ByteArrayInputStream(new byte[0]));
        Assert.assertTrue(true);
    }
}

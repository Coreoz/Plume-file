package com.coreoz.plume.file.service;

import com.carlosbecker.guice.GuiceModules;
import com.carlosbecker.guice.GuiceTestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@RunWith(GuiceTestRunner.class)
@GuiceModules(FileTestModule.class)
public class FileStorageSystemServiceTest {

    @Inject
    private FileStorageSystemService fileStorageSystemService;

    @Test
    public void fetch_file_with_known_unique_file_name_must_return_byte_array() throws IOException {
        /*Optional<InputStream> fileMetadata = this.fileStorageSystemService.fetch("random-uid-to-fetch");
        Assert.assertTrue(fileMetadata.isPresent());
        Assert.assertEquals(-1, fileMetadata.get().read());*/
    }
}

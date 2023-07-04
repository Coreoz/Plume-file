package com.coreoz.plume.file.service;

import org.junit.Test;

import java.io.IOException;

public class FileStorageSystemServiceTest {

    private FileStorageSystemService fileStorageSystemService;

    @Test
    public void fetch_file_with_known_unique_file_name_must_return_byte_array() throws IOException {
        /*Optional<InputStream> fileMetadata = this.fileStorageSystemService.fetch("random-uid-to-fetch");
        Assert.assertTrue(fileMetadata.isPresent());
        Assert.assertEquals(-1, fileMetadata.get().read());*/
    }
}

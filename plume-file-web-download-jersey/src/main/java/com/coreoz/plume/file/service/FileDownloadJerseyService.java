package com.coreoz.plume.file.service;

import com.coreoz.plume.file.services.metadata.FileMetadata;

import java.io.InputStream;
import java.util.Optional;

public interface FileDownloadJerseyService {
    Optional<FileMetadata> fetchMetadata(String fileUniqueName);
    Optional<InputStream> fetchData(String fileUniqueName);
}

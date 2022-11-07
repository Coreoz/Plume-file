package com.coreoz.plume.file.service;

import com.coreoz.plume.file.service.beans.FileData;
import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

public interface FileDownloadJerseyService {
    Logger logger = LoggerFactory.getLogger(FileDownloadJerseyService.class);

    Optional<FileData> fetchFile(String fileUniqueName);

    static Function<InputStream, Optional<byte[]>> readFile() {
        return data -> {
            try {
                return Optional.of(ByteStreams.toByteArray(data));
            } catch (IOException e) {
                logger.error("Error while reading file", e);
            }
            return Optional.empty();
        };
    }
}

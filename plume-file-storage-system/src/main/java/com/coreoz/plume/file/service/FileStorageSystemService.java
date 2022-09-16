package com.coreoz.plume.file.service;

import com.coreoz.plume.file.configuration.FileStorageConfigurationService;
import com.coreoz.plume.file.services.data.MeasuredSizeInputStream;
import com.coreoz.plume.file.services.storage.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class FileStorageSystemService implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageSystemService.class);

    private final String path;

    @Inject
    public FileStorageSystemService(FileStorageConfigurationService configurationService) {
        this.path = configurationService.mediaLocalPath();
    }

    @Override
    public long add(String fileUniqueName, MeasuredSizeInputStream inputStream) {
        logger.debug("Creating file : {}", fileUniqueName);
        this.createFile(
            fileUniqueName,
            inputStream
        );
        return inputStream.getInputStreamTotalSize();
    }

    @Override
    public Optional<MeasuredSizeInputStream> fetch(String fileUniqueName) {
        logger.debug("Fetching file {}", fileUniqueName);
        try (MeasuredSizeInputStream fileInputStream = new MeasuredSizeInputStream(new FileInputStream(this.getFullPath(fileUniqueName)))) {
            return Optional.of(fileInputStream);
        } catch (IOException e) {
            logger.error("Exception while retrieving file :", e);
            return Optional.empty();
        }
    }

    @Override
    public List<String> deleteAll(List<String> fileUniqueNames) {
        logger.debug("Deleting files");
        boolean directoryExists = FileUtils.directoryExists(this.getFolderPath());
        List<String> filesInError = new ArrayList<>();
        if (!directoryExists) {
            logger.warn("The file directory {} can't be created", this.getFolderPath());
            return fileUniqueNames;
        }
        for (String fileToDelete : fileUniqueNames) {
            boolean hasBeenDeleted = FileUtils.deleteFile(fileToDelete);
            if (!hasBeenDeleted) {
                logger.warn("File {} hase not been deleted", fileToDelete);
                filesInError.add(fileToDelete);
            }
        }
        return filesInError;
    }

    private void createFile(String fileName, InputStream inputStream) {
        logger.debug("Creating file : {}", fileName);
        boolean directoryExists = FileUtils.directoryExists(this.getFolderPath());

        if (directoryExists) {
            logger.debug("Writing file in the directory");
            // creating the file
            FileUtils.writeFile(inputStream, this.getFullPath(fileName));
        }
    }

    private String getFolderPath() {
        return this.path;
    }

    private String getFullPath(String fileName) {
        return this.path + fileName;
    }
}

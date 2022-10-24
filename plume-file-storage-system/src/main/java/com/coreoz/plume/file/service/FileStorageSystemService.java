package com.coreoz.plume.file.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.plume.file.configuration.FileStorageConfigurationService;
import com.coreoz.plume.file.services.storage.FileStorageService;

@Singleton
public class FileStorageSystemService implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageSystemService.class);

    private final String path;

    @Inject
    public FileStorageSystemService(FileStorageConfigurationService configurationService) {
        this.path = configurationService.mediaLocalPath();
    }

    @Override
    public void add(String fileUniqueName, InputStream fileData) {
    	logger.debug("Saving file '{}' to disk...", fileUniqueName);
        this.createFile(
            fileUniqueName,
            fileData
        );
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName) {
        logger.debug("Fetching file {}", fileUniqueName);
        try (InputStream fileInputStream = new FileInputStream(this.getFullPath(fileUniqueName))) {
            return Optional.of(fileInputStream);
        } catch (IOException e) {
            logger.error("Exception while retrieving file :", e);
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll(List<String> fileUniqueNames) throws IOException {
        logger.debug("Deleting files");
        boolean directoryExists = FileUtils.directoryExists(this.getFolderPath());
        if (!directoryExists) {
        	throw new IOException("Files directory " + this.getFolderPath() + " does not exist");
        }
        for (String fileToDelete : fileUniqueNames) {
            boolean hasBeenDeleted = FileUtils.deleteFile(fileToDelete);
            if (!hasBeenDeleted) {
                logger.warn("File {} hase not been deleted", fileToDelete);
            }
        }
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

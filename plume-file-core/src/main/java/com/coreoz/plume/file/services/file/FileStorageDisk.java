package com.coreoz.plume.file.services.file;

import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.storage.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileStorageDisk implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageDisk.class);

    private final String path;

    @Inject
    public FileStorageDisk(FileConfigurationService configurationService) {
        this.path = configurationService.mediaLocalPath();
    }

    @Override
    public void upload(Long fileId, String fileUniqueName, byte[] fileData) {
        logger.debug("Creating file : {}", fileUniqueName);
        this.createFile(
            fileUniqueName,
            fileData
        );
    }

    @Override
    public Optional<byte[]> fetch(String fileType, String fileUniqueName) {
        return Optional.empty();
    }

    @Override
    public List<String> deleteFiles(List<String> fileUniqueNames) {
        logger.debug("Deleting files");
        boolean directoryExists = retrieveDirectory();
        List<String> filesInError = new ArrayList<>();
        if (!directoryExists) {
            logger.warn("The file directory {} can't be created", this.getFolderPath());
            return fileUniqueNames;
        }
        for (String fileToDelete : fileUniqueNames) {
            boolean hasBeenDeleted = deleteFile(fileToDelete);
            if (!hasBeenDeleted) {
                logger.warn("File {} hase not been deleted", fileToDelete);
                filesInError.add(fileToDelete);
            }
        }
        return filesInError;
    }

    private void createFile(String fileName, byte[] fileData) {
        logger.debug("Creating file : {}", fileName);
        boolean directoryExists = retrieveDirectory();

        if (directoryExists) {
            logger.debug("Writing file in the directory");
            // creating the file
            File fileToSave = new File(this.getFullPath(fileName));
            writeFile(fileToSave, fileData);
        }
    }

    private boolean deleteFile(String fileName) {
        logger.debug("Deleting file : {}", fileName);
        File currentFile = new File(this.getFullPath(fileName));
        if (currentFile.exists()) {
            return currentFile.delete();
        }
        return false;
    }

    private boolean retrieveDirectory() {
        String currentPath = this.getFolderPath();
        File filePath = new File(currentPath);
        boolean directoryExists = filePath.exists();
        boolean directoryCreated = false;
        if (!directoryExists) {
            logger.debug("Creating file directory");
            // creating the directory
            directoryCreated = filePath.mkdirs();
        }
        return directoryExists || directoryCreated;
    }

    private void writeFile(File outputFile, byte[] fileData) {
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(fileData);
        } catch (IOException e) {
            logger.error("An error occured during file creation : ", e);
        }
    }

    private String getFolderPath() {
        return this.path;
    }

    private String getFullPath(String fileName) {
        return this.path + fileName;
    }
}

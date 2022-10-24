package com.coreoz.plume.file.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
        // empty constructor to hide the implicit one
    }

    public static boolean directoryExists(String directoryPath) {
        File filePath = new File(directoryPath);
        boolean directoryExists = filePath.exists();
        boolean directoryCreated = false;
        if (!directoryExists) {
            logger.debug("Creating file directory");
            // creating the directory
            directoryCreated = filePath.mkdirs();
        }
        return directoryExists || directoryCreated;
    }

    public static void writeFile(InputStream inputStream, String filePath) {
        File fileToSave = new File(filePath);
        try (FileOutputStream outputStream = new FileOutputStream(fileToSave)) {
        	inputStream.transferTo(outputStream);
        } catch (IOException e) {
            logger.error("An error occurred during file creation", e);
        }
    }

    public static boolean deleteFile(String filePath) {
        logger.debug("Deleting file : {}", filePath);
        File currentFile = new File(filePath);
        if (currentFile.exists()) {
            try {
                Files.delete(currentFile.toPath());
                return true;
            } catch (IOException e) {
                logger.error("Can't delete file {} : ", filePath, e);
            }
            return false;
        }
        return false;
    }
}

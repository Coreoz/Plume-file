package com.coreoz.plume.file.service;

import com.coreoz.plume.file.configuration.FileStorageConfigurationService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Singleton
public class FileStorageSystemService implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageSystemService.class);

    private final String path;

    @Inject
    @SneakyThrows
    public FileStorageSystemService(FileStorageConfigurationService configurationService) {
        this.path = configurationService.mediaLocalPath();
        useMediaDirectory(this.path);
    }

    @Override
    public void add(String fileUniqueName, InputStream fileData) throws IOException {
    	useMediaDirectory(path);
    	logger.debug("Saving file '{}' to disk...", fileUniqueName);
        File fileToSave = new File(computeFilePath(fileUniqueName));
        try (FileOutputStream outputStream = new FileOutputStream(fileToSave)) {
        	fileData.transferTo(outputStream);
        }
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName) {
        logger.debug("Fetching file {}...", fileUniqueName);
        try {
            return Optional.of(new FileInputStream(computeFilePath(fileUniqueName)));
        } catch (IOException e) {
            logger.error("Could not retrieve file {}", fileUniqueName, e);
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll(List<String> fileUniqueNames) throws IOException {
        logger.debug("Deleting {} files...", fileUniqueNames.size());
        useMediaDirectory(path);
        for (String fileNameToDelete : fileUniqueNames) {
        	String filePath = computeFilePath(fileNameToDelete);
            File currentFile = new File(filePath);
            if (currentFile.exists()) {
            	Files.delete(currentFile.toPath());
            }
        }
    }

    private String computeFilePath(String fileName) {
        return this.path + fileName;
    }

    /**
     * Create or use the directory specified by the path directoryPath
     *
     * @param directoryPath the directory path
     * @throws IOException If the directory
     */
    public static void useMediaDirectory(String directoryPath) throws IOException {
        File filePath = new File(directoryPath);
        if(!filePath.exists()) {
        	logger.debug("Creating file directory {}...", directoryPath);
        	if (!filePath.mkdirs()) {
        		throw new IOException("Directory " + directoryPath + " does not exist and could not be created");
        	}
        }
    }
}

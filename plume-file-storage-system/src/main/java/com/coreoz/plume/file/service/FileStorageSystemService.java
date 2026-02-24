package com.coreoz.plume.file.service;

import com.coreoz.plume.file.configuration.FileStorageConfigurationService;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.coreoz.plume.file.utils.FilePathSanitizer;
import com.google.common.base.Strings;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Singleton
public class FileStorageSystemService implements FileStorageService {
    private final String path;
    private final boolean useSubdirectories;
    private final Map<String, String> subdirectoriesFileTypeMapping;

    @Inject
    public FileStorageSystemService(FileStorageConfigurationService configurationService) throws IOException {
        this.path = configurationService.mediaLocalPath();
        this.useSubdirectories = configurationService.useSubdirectories();
        this.subdirectoriesFileTypeMapping = configurationService.subdirectoriesFileTypeMapping();
        // verify on startup that the target folder to store files exists or else try to create it
        useMediaDirectory(this.path);
    }

    @Override
    public void add(String fileUniqueName, InputStream fileData) throws IOException {
        if (useSubdirectories) {
            throw new IllegalArgumentException("File metadata must be provided to fetch files when subdirectories are used");
        }
        add(fileUniqueName, fileData, null);
    }

    @Override
    public void add(String fileUniqueName, InputStream fileData, FileMetadata fileMetadata) throws IOException {
        // To handle gracefully the case where the target files folder has been deleted
        String targetPath = computeDirectoryPath(fileMetadata);
        useMediaDirectory(targetPath);
        logger.debug("Saving file '{}' to disk...", fileUniqueName);
        File fileToSave = new File(computeFilePath(fileUniqueName, fileMetadata));
        try (FileOutputStream outputStream = new FileOutputStream(fileToSave)) {
            fileData.transferTo(outputStream);
        }
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName) {
        if (useSubdirectories) {
            throw new IllegalArgumentException("File metadata must be provided to fetch files when subdirectories are used");
        }
        return fetch(fileUniqueName, null);
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName, FileMetadata metadata) {
        logger.debug("Fetching file {}...", fileUniqueName);
        try {
            return Optional.of(new FileInputStream(computeFilePath(fileUniqueName, metadata)));
        } catch (IOException e) {
            logger.error("Could not retrieve file {}", fileUniqueName, e);
            return Optional.empty();
        }
    }

    @Override
    public void deleteAll(List<String> fileUniqueNames) throws IOException {
        if (useSubdirectories) {
            throw new IllegalArgumentException("File metadata must be provided to fetch files when subdirectories are used");
        }
        logger.debug("Deleting {} files from system storage...", fileUniqueNames.size());
        // To handle gracefully the case where the target files folder has been deleted
        useMediaDirectory(path);
        for (String fileNameToDelete : fileUniqueNames) {
        	String filePath = computeFilePath(fileNameToDelete, null);
            File currentFile = new File(filePath);
            if (currentFile.exists()) {
            	Files.delete(currentFile.toPath());
            }
        }
    }

    @Override
    public void deleteFiles(List<FileMetadata> filesToDelete) throws IOException {
        logger.debug("Deleting {} files from system storage...", filesToDelete.size());
        // To handle gracefully the case where the target files folder has been deleted
        useMediaDirectory(path);
        for (FileMetadata fileMetadata : filesToDelete) {
            String filePath = computeFilePath(fileMetadata.getUniqueName(), fileMetadata);
            File currentFile = new File(filePath);
            if (currentFile.exists()) {
                Files.delete(currentFile.toPath());
            }
        }
    }

    private String computeFilePath(String fileName, @Nullable FileMetadata metadata) {
        return computeDirectoryPath(metadata) + fileName;
    }

    private String computeDirectoryPath(@Nullable FileMetadata metadata) {
        if (!this.useSubdirectories || metadata == null || Strings.isNullOrEmpty(metadata.getFileType())) {
            return this.path;
        }
        return FilePathSanitizer.combinePaths(this.path, mapFileTypeToSubdirectory(metadata.getFileType()));
    }

    @Nullable
    private String mapFileTypeToSubdirectory(String fileType) {
        if (this.subdirectoriesFileTypeMapping == null || fileType == null) {
            return null;
        }
        return this.subdirectoriesFileTypeMapping.getOrDefault(fileType, fileType).toLowerCase();
    }

    /**
     * Create or use the directory specified by the path directoryPath
     *
     * @param directoryPath the directory path
     * @throws IOException If the directory does not exist and could not be created
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

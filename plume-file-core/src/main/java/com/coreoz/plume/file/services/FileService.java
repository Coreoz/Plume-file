package com.coreoz.plume.file.services;

import com.coreoz.plume.file.services.data.MeasuredSizeInputStream;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.metadata.FileMetadataService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.coreoz.plume.file.utils.FileNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    private final FileMetadataService fileMetadataService;
    private final FileStorageService fileStorageService;

    @Inject
    public FileService(
        FileMetadataService fileMetadataService,
        FileStorageService fileStorageService
    ) {
        this.fileMetadataService = fileMetadataService;
        this.fileStorageService = fileStorageService;
    }

    // upload

    /**
     * Save a new file
     *
     * @param fileType         the type of the file {@link FileType}
     * @param inputStream      the file data
     * @param originalName     the original name of the file. This won't be the name under the one the file will be saved
     * @param fileExtension    the file extension
     * @param expectedFileSize the expected file size in bytes
     * @return the unique file name of the file. This will be the name under the one the file will be saved
     */
    public String add(FileType fileType, InputStream inputStream, String originalName, String fileExtension, long expectedFileSize) {
        String fileCleanExtension = FileNameUtils.cleanExtensionName(fileExtension);
        String fileUniqueName = UUID.randomUUID() + (fileCleanExtension.isEmpty() ? "" : "." + fileCleanExtension);
        this.fileMetadataService.add(
            fileUniqueName,
            originalName,
            fileType.name(),
            fileExtension,
            FileNameUtils.guessMimeType(fileUniqueName),
            expectedFileSize
        );
        long actualSize = this.fileStorageService.add(fileUniqueName, new MeasuredSizeInputStream(inputStream));
        if (expectedFileSize != actualSize) {
            this.fileMetadataService.updateFileSize(fileUniqueName, actualSize);
        }

        return fileUniqueName;
    }

    /**
     * Consume the stream to produce a byte array,
     * then call {@link #add(FileType, InputStream, String)}
     */
    public String add(FileType fileType, InputStream fileData) {
        return add(fileType, fileData, null);
    }

    /**
     * Consume the stream to produce a byte array,
     * then call {@link #add(FileType, InputStream, String, String, long)}
     */
    public String add(FileType fileType, InputStream fileData, String fileName) {
        return add(fileType, fileData, FileNameUtils.getExtensionFromFilename(fileName), fileName, 0);
    }

    // file data

    public Optional<InputStream> fetchData(String fileUniqueName) {
        return this.fileStorageService.fetch(fileUniqueName);
    }

    public Optional<FileMetadata> fetchMetadata(String fileUniqueName) {
        return fileMetadataService
            .fetch(fileUniqueName);
    }

    // clean up

    public void deleteUnreferenced() {
        List<String> fileUniqueNamesToDelete = fileMetadataService.findUnreferencedFiles();
        List<String> fileUniqueNamesDeleted = fileStorageService.deleteAll(fileUniqueNamesToDelete);
        fileMetadataService.deleteAll(fileUniqueNamesDeleted);
        logger.debug("{} unreferenced files deleted", fileUniqueNamesDeleted.size());
    }

}

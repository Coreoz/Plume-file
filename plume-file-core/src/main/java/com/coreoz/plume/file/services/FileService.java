package com.coreoz.plume.file.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.plume.file.services.data.MeasuredSizeInputStream;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.metadata.FileMetadataService;
import com.coreoz.plume.file.services.storage.FileStorageService;
import com.coreoz.plume.file.utils.FileNameUtils;

import lombok.SneakyThrows;

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
     * @param inputStream      The file data stream, that will be closed automatically in this method
     * @param originalName     the original name of the file. This won't be the name under the one the file will be saved
     * @param fileExtension    the file extension
     * @param mimeType         the mime type
     * @param expectedFileSize the expected file size in bytes
     * @return the unique file name of the file. This will be the name under the one the file will be saved
     * @throws IOException in case the file could not be saved
     */
    @SneakyThrows
    public String add(FileType fileType, InputStream inputStream, String originalName, String fileExtension, String mimeType, Long expectedFileSize) {
        String fileCleanExtension = FileNameUtils.cleanExtensionName(fileExtension);
        String fileUniqueName = UUID.randomUUID() + (fileCleanExtension.isEmpty() ? "" : "." + fileCleanExtension);
        this.fileMetadataService.add(
            fileUniqueName,
            originalName,
            fileType.name(),
            fileExtension,
            mimeType,
            expectedFileSize
        );
        try (MeasuredSizeInputStream measingSizeInputStream = new MeasuredSizeInputStream(inputStream)) {
        	this.fileStorageService.add(fileUniqueName, measingSizeInputStream);
            if (expectedFileSize == null || expectedFileSize != measingSizeInputStream.getInputStreamTotalSize()) {
                this.fileMetadataService.updateFileSize(fileUniqueName, measingSizeInputStream.getInputStreamTotalSize());
            }
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
     * then call {@link #add(FileType, InputStream, String, String, String, long)}
     */
    public String add(FileType fileType, InputStream fileData, String fileName, String mimeType, Long expectedFileSize) {
        return add(fileType, fileData, fileName, FileNameUtils.getExtensionFromFilename(fileName), mimeType, expectedFileSize);
    }

    /**
     * Consume the stream to produce a byte array,
     * then call {@link #add(FileType, InputStream, String, String, String, long)}
     */
    public String add(FileType fileType, InputStream fileData, String fileName, String mimeType) {
        return add(fileType, fileData, fileName, FileNameUtils.getExtensionFromFilename(fileName), mimeType, null);
    }

    /**
     * Consume the stream to produce a byte array,
     * then call {@link #add(FileType, InputStream, String, String, String, long)}
     */
    public String add(FileType fileType, InputStream fileData, String fileName) {
        return add(fileType, fileData, fileName, FileNameUtils.getExtensionFromFilename(fileName), FileNameUtils.guessMimeType(fileName), null);
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

    /**
     * Delete unreferenced files.
     * 
     * @throws IOException is a file could not be deleted.
     * It is possible to retry if the deletion failed.
     */
    @SneakyThrows
    public void deleteUnreferenced() {
        List<String> fileUniqueNamesToDelete = fileMetadataService.findUnreferencedFiles();
        fileStorageService.deleteAll(fileUniqueNamesToDelete);
        fileMetadataService.deleteAll(fileUniqueNamesToDelete);
        logger.debug("{} unreferenced files deleted", fileUniqueNamesToDelete.size());
    }

}

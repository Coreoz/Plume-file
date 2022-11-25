package com.coreoz.plume.file.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.plume.file.services.configuration.FileConfigurationService;
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
    private final String checksumAlgorithm;

    @Inject
    public FileService(
        FileMetadataService fileMetadataService,
        FileStorageService fileStorageService,
        FileConfigurationService fileConfigurationService
    ) throws NoSuchAlgorithmException {
        this.fileMetadataService = fileMetadataService;
        this.fileStorageService = fileStorageService;
        this.checksumAlgorithm = fileConfigurationService.checksumAlgorithm();
        // verify on startup that the checksumAlgorithm is available
        MessageDigest.getInstance(checksumAlgorithm);
    }

    // upload

    /**
     * Save a new file
     *
     * @param fileType        the type of the file {@link FileType}
     * @param fileInputStream the file data stream, that will be closed automatically in this method
     * @param originalName    the original name of the file. This won't be the name under the one the file will be saved
     * @param fileExtension   the file extension
     * @param mimeType        the mime type
     * @return the unique file name of the file. This will be the name under the one the file will be saved
     * @throws UncheckedIOException in case the file could not be saved
     */
    @SneakyThrows
    public String add(
        FileType fileType,
        InputStream fileInputStream,
        String originalName,
        String fileExtension,
        String mimeType
    ) throws UncheckedIOException {
        String fileCleanExtension = FileNameUtils.cleanExtensionName(fileExtension);
        String fileUniqueName = UUID.randomUUID() + (fileCleanExtension.isEmpty() ? "" : "." + fileCleanExtension);
        this.fileMetadataService.add(
            fileUniqueName,
            originalName,
            fileType.name(),
            fileExtension,
            mimeType
        );
        DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, MessageDigest.getInstance(checksumAlgorithm));
        try (MeasuredSizeInputStream measuredSizeInputStream = new MeasuredSizeInputStream(digestInputStream)) {
            this.fileStorageService.add(fileUniqueName, measuredSizeInputStream);
            this.fileMetadataService.updateFileSizeAndChecksum(
                fileUniqueName,
                measuredSizeInputStream.getInputStreamTotalSize(),
                Base64.getEncoder().encodeToString(digestInputStream.getMessageDigest().digest())
            );
        } catch (IOException e) {
			throw new UncheckedIOException(e);
		}

        return fileUniqueName;
    }

    /**
     * Consume the stream to produce a byte array,
     * then call {@link #add(FileType, InputStream, String)}
     */
    public String add(FileType fileType, InputStream fileData) throws UncheckedIOException {
        return add(fileType, fileData, null);
    }

    /**
     * Consume the stream to produce a byte array,
     * then call {@link #add(FileType, InputStream, String, String, String)}
     */
    public String add(FileType fileType, InputStream fileData, String fileName, String mimeType) throws UncheckedIOException {
        return add(fileType, fileData, fileName, FileNameUtils.getExtensionFromFilename(fileName), mimeType);
    }

    /**
     * Consume the stream to produce a byte array,
     * then call {@link #add(FileType, InputStream, String, String, String)}
     */
    public String add(FileType fileType, InputStream fileData, String fileName) throws UncheckedIOException {
        return add(fileType, fileData, fileName, FileNameUtils.getExtensionFromFilename(fileName), FileNameUtils.guessMimeType(fileName));
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
     * @throws UncheckedIOException is a file could not be deleted.
     * It is possible to retry if the deletion failed.
     */
    public void deleteUnreferenced() throws UncheckedIOException {
        List<String> fileUniqueNamesToDelete = fileMetadataService.findUnreferencedFiles();
        try {
        	fileStorageService.deleteAll(fileUniqueNamesToDelete);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
        fileMetadataService.deleteAll(fileUniqueNamesToDelete);
        logger.debug("{} unreferenced files deleted", fileUniqueNamesToDelete.size());
    }
}

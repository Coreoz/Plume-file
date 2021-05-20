package com.coreoz.plume.file.services.file;

import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.file.data.FileUploadBase64;
import com.coreoz.plume.file.services.file.data.FileUploaded;
import com.coreoz.plume.file.services.file.metadata.FileMetadataService;
import com.coreoz.plume.file.services.file.storage.FileStorageService;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.filetype.FileTypesProvider;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.coreoz.plume.file.utils.FileNameUtils;
import com.google.common.io.ByteStreams;

import lombok.SneakyThrows;

@Singleton
public class FileService {

	private static final Logger logger = LoggerFactory.getLogger(FileService.class);

	private final FileMetadataService fileMetadataService;
	private final FileStorageService fileStorageService;
	private final FileCacheService fileCacheService;
	private final FileTypesProvider fileTypesProvider;
	private final ChecksumService checksumService;

	private final String fileApiBaseUrl;

	@SuppressWarnings("deprecation")
	@Inject
	public FileService(FileMetadataService fileMetadataService, FileStorageService fileStorageService,
			FileCacheService fileCacheService, ChecksumService checksumService,
			FileTypesProvider fileTypesProvider, FileConfigurationService configurationService) {
		this.fileMetadataService = fileMetadataService;
		this.fileStorageService = fileStorageService;
		this.fileCacheService = fileCacheService;
		this.fileTypesProvider = fileTypesProvider;
		this.checksumService = checksumService;

		this.fileApiBaseUrl = configurationService.apiBasePath() + configurationService.fileWsPath();

		this.fileCacheService.initializeFileIdCache(fileMetadataService::fetchUniqueName);
		this.fileCacheService.initializeFileDataCache(this::fetchUncached);
	}

	// upload

	/**
	 * Save an uploaded file.
	 */
	public FileUploaded upload(FileType fileType, String fileExtension, byte[] fileData) {
		String fileCleanExtension = FileNameUtils.cleanExtensionName(fileExtension);
		String fileUniqueName = UUID.randomUUID().toString()
				+ (fileCleanExtension.isEmpty() ? "" : "." + fileCleanExtension);
		long fileId = fileMetadataService.upload(
			fileUniqueName,
			fileType.name(),
			FileNameUtils.guessMimeType(fileUniqueName),
			fileData.length * 4L // *4L to convert Java Bytes length to bytes
		);
		fileStorageService.upload(fileType.name(), fileUniqueName, fileData);

		return FileUploaded.of(fileId, fileUniqueName, url(fileUniqueName));
	}

	// upload alias
	public FileUploaded upload(FileType fileType, byte[] fileData) {
		return upload(fileType, null, fileData);
	}

	public Optional<FileUploaded> upload(FileType fileType, @Nullable FileUploadBase64 file) {
		if(file == null || file.getBase64() == null) {
			return Optional.empty();
		}

		return Optional.of(upload(
			fileType,
			FileNameUtils.getExtensionFromFilename(file.getFilename()),
			Base64.getDecoder().decode(file.getBase64())
		));
	}

	/**
	 * Consume the stream to produce a byte array,
	 * then call {@link #upload(FileType, InputStream, String))}
	 */
	public FileUploaded upload(FileType fileType, InputStream fileData) {
		return upload(fileType, fileData, null);
	}

	/**
	 * Consume the stream to produce a byte array,
	 * then call {@link #upload(FileType, String, byte[]))}
	 */
	@SneakyThrows
	public FileUploaded upload(FileType fileType, InputStream fileData, String fileName) {
		return upload(fileType, FileNameUtils.getExtensionFromFilename(fileName), ByteStreams.toByteArray(fileData));
	}

	// file URL

    /**
     * @deprecated Unique name should be used instead of the raw identifier, see {@link FileService#url(String)}
     */
    @Deprecated
    public Optional<String> url(Long fileId) {
        return fileCacheService
        	.fetchFileUniqueName(fileId)
        	.map(this::url);
    }

    public String url(String fileUniqueName) {
    	return fileApiBaseUrl + "/" + fileUniqueName;
    }

	// file data

	public Optional<FileData> fetch(String fileUniqueName) {
		return fileCacheService.fetchFileData(fileUniqueName);
	}

	/**
     * @deprecated Unique name should be used instead of the raw identifier, see {@link FileService#fetch(String)}
     */
	@Deprecated
	public Optional<FileData> fetch(Long fileId) {
		return fileCacheService
			.fetchFileUniqueName(fileId)
			.flatMap(this::fetch);
	}

	private Optional<FileData> fetchUncached(String fileUniqueName) {
		return fileMetadataService
    		.fetch(fileUniqueName)
    		.flatMap(fileMetadata -> fileStorageService
    			.fetch(fileMetadata.getFileType(), fileUniqueName)
    			.map(fileData -> new FileData(
    				fileUniqueName,
    				fileMetadata.getMimeType(),
    				checksumService.hash(fileData),
    				fileData
    			))
    		);
	}

	// clean up

	public void deleteUnreferenced() {
		List<String> fileUniqueNamesToDelete = fileMetadataService
			.findUnreferencedFiles(fileTypesProvider.fileTypesAvailable());
		if (!fileUniqueNamesToDelete.isEmpty()) {
			List<String> fileUniqueNamesDeleted = fileStorageService.deleteFiles(fileUniqueNamesToDelete);
			fileMetadataService.deleteFiles(fileUniqueNamesDeleted);
            logger.debug("{} unreferenced files deleted", fileUniqueNamesDeleted.size());
        }
	}

}

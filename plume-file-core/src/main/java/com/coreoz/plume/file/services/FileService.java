package com.coreoz.plume.file.services;

import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.filetype.FileTypesProvider;
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
	private final FileTypesProvider fileTypesProvider;

	@Inject
	public FileService(
		FileMetadataService fileMetadataService,
		FileStorageService fileStorageService,
		FileTypesProvider fileTypesProvider
	) {
		this.fileMetadataService = fileMetadataService;
		this.fileStorageService = fileStorageService;
		this.fileTypesProvider = fileTypesProvider;
	}

	// upload

	/**
	 * Save a new  file.
	 */
	public String add(String originalName, FileType fileType, String fileExtension, InputStream inputStream) {
		String fileCleanExtension = FileNameUtils.cleanExtensionName(fileExtension);
		String fileUniqueName = UUID.randomUUID() + (fileCleanExtension.isEmpty() ? "" : "." + fileCleanExtension);
		this.fileMetadataService.add(
			fileUniqueName,
			originalName,
			fileType.name(),
			fileExtension,
			FileNameUtils.guessMimeType(fileUniqueName),
			// TODO FETCH SIZE
			4L // *4L to convert Java Bytes length to bytes
		);
		this.fileStorageService.add(fileUniqueName, inputStream);

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
	 * then call {@link #add(String, FileType, String, InputStream)}
	 */
	public String add(FileType fileType, InputStream fileData, String fileName) {
		return add(fileName, fileType, FileNameUtils.getExtensionFromFilename(fileName), fileData);
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
		List<String> fileUniqueNamesToDelete = fileMetadataService
			.findUnreferencedFiles(fileTypesProvider.fileTypesAvailable());
		if (!fileUniqueNamesToDelete.isEmpty()) {
			List<String> fileUniqueNamesDeleted = fileStorageService.deleteAll(fileUniqueNamesToDelete);
			fileMetadataService.deleteAll(fileUniqueNamesDeleted);
            logger.debug("{} unreferenced files deleted", fileUniqueNamesDeleted.size());
        }
	}

}

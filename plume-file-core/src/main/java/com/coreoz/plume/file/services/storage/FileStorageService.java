package com.coreoz.plume.file.services.storage;

import com.coreoz.plume.file.services.metadata.FileMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface FileStorageService {
	/**
	 * Add a file
	 * @param fileUniqueName the file unique name
	 * @param fileData the file input stream
	 * @throws IOException if a file could not be stored
	 */
	void add(String fileUniqueName, InputStream fileData) throws IOException;

	/**
	 * Add a file with optional path support
	 * @param fileUniqueName the file unique name
	 * @param fileData the file input stream
	 * @param metadata the file metadata, which can be used by some storage implementations
	 * @throws IOException if a file could not be stored
	 */
	void add(String fileUniqueName, InputStream fileData, FileMetadata metadata) throws IOException;

	/**
	 * Fetch a file by unique name without path support
	 * @param fileUniqueName the file unique name
	 * @return the file input stream if found
	 */
	Optional<InputStream> fetch(String fileUniqueName);

	/**
	 * Fetch a file with optional path support
	 * @param fileUniqueName the file unique name
	 * @param metadata the file metadata, which can be used by some storage implementations
	 * @return the file input stream if found
	 */
	Optional<InputStream> fetch(String fileUniqueName, FileMetadata metadata);

	/**
	 * Delete the files data identified by the fileUniqueNames.
	 * If a file is already deleted, no exception must be raised
	 * Must not fail if the files have already been deleted
	 * @param fileUniqueNames The unique names of the files that should be deleted
	 * @throws IOException if a file could not be deleted
	 */
	void deleteAll(List<String> fileUniqueNames) throws IOException;

	/**
	 * Delete the files data identified by their metadata.
	 * If a file is already deleted, no exception must be raised
	 * Must not fail if the files have already been deleted
	 * @param filesToDelete The metadata of the files that should be deleted
	 * @throws IOException if a file could not be deleted
	 */
	void deleteFiles(List<FileMetadata> filesToDelete) throws IOException;
}

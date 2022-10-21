package com.coreoz.plume.file.services.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface FileStorageService {
	/**
	 * Add file
	 * @param fileUniqueName the file unique name
	 * @param fileData the file input stream
	 * @return the file size
	 */
	long add(String fileUniqueName, InputStream fileData);

	Optional<InputStream> fetch(String fileUniqueName);

	/**
	 * Delete the files data identified by the fileUniqueNames.
	 * 
	 * If a file is already deleted, no exception must be raised
	 * 
	 * Must not fail if the files have already been deleted
	 * @param fileUniqueNames The unique names of the files that should be deleted
	 * @throws IOException is a file could not be deleted
	 */
	void deleteAll(List<String> fileUniqueNames) throws IOException;
}

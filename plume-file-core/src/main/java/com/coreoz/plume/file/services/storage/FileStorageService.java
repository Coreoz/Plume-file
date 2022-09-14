package com.coreoz.plume.file.services.storage;

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
	 * Must not fail if the files have already been deleted
	 * @param fileUniqueNames The unique names of the files that should be deleted
	 * @return The file unique names that have not been deleted OR that did not exist.
	 * Only files that exist and that have failed to be deleted must not be present in this list
	 */
	List<String> deleteAll(List<String> fileUniqueNames);
}

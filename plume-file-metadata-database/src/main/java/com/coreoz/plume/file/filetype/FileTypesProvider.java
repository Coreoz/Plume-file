package com.coreoz.plume.file.filetype;

import java.util.Collection;

/**
 * Provider of file types.
 */
public interface FileTypesProvider {

	/**
	 * @return the file types available in the application
	 */
	Collection<FileTypeDatabase> fileTypesAvailable();
}

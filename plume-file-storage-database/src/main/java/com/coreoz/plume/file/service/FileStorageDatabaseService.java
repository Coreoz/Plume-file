package com.coreoz.plume.file.service;

import com.coreoz.plume.file.db.FileStorageDao;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.storage.FileStorageService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
@Singleton
public class FileStorageDatabaseService implements FileStorageService {
    private final FileStorageDao fileStorageDao;

    @Inject
    public FileStorageDatabaseService(FileStorageDao fileStorageDao) {
        this.fileStorageDao = fileStorageDao;
    }

    @Override
    public void add(String fileUniqueName, InputStream fileData) throws IOException {
        add(fileUniqueName, fileData, null);
    }

    @Override
    public void add(String fileUniqueName, InputStream fileData, FileMetadata metadata) throws IOException {
        // Path is ignored in database storage - files are stored by unique name only
        logger.debug("Saving file '{}' to database...", fileUniqueName);
        try {
	        this.fileStorageDao.add(
	            fileUniqueName,
	            fileData
	        );
        } catch (Exception e) {
			throw new IOException(e);
		}
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName) {
        return fetch(fileUniqueName, null);
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName, FileMetadata fileMetadata) {
        return this.fileStorageDao.fetch(fileUniqueName);
    }

    @Override
    public void deleteAll(List<String> fileUniqueNames) throws IOException {
        logger.debug("Deleting files '{}' from database...", fileUniqueNames);
    	try {
    		this.fileStorageDao.deleteAll(fileUniqueNames);
    	} catch (Exception e) {
			throw new IOException(e);
		}
    }

    @Override
    public void deleteFiles(List<FileMetadata> filesToDelete) throws IOException {
        this.deleteAll(filesToDelete.stream().map(FileMetadata::getUniqueName).toList());
    }
}

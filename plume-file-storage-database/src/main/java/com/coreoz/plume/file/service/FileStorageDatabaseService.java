package com.coreoz.plume.file.service;

import com.coreoz.plume.file.db.FileStorageDao;
import com.coreoz.plume.file.services.storage.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class FileStorageDatabaseService implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageDatabaseService.class);

    private final FileStorageDao fileStorageDao;

    @Inject
    public FileStorageDatabaseService(FileStorageDao fileStorageDao) {
        this.fileStorageDao = fileStorageDao;
    }

    @Override
    public long add(String fileUniqueName, InputStream fileData) {
        logger.info("Uploading file {}", fileUniqueName);
        this.fileStorageDao.add(
            fileUniqueName,
            fileData
        );
        // TODO FETCH SIZE
        return 0;
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName) {
        return this.fileStorageDao.fetch(fileUniqueName);
    }

    @Override
    public List<String> deleteAll(List<String> filesUid) {
        List<String> filesNotDeleted = new ArrayList<>();
        for (String fileToDelete : filesUid) {
            boolean hasBeenDeleted = this.fileStorageDao.delete(fileToDelete);
            if (!hasBeenDeleted) {
                filesNotDeleted.add(fileToDelete);
            }
        }
        return filesNotDeleted;
    }
}

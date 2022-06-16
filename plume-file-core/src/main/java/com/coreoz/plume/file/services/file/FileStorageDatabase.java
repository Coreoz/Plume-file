package com.coreoz.plume.file.services.file;

import com.coreoz.plume.file.db.dao.FileDatabaseDao;
import com.coreoz.plume.file.services.file.storage.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class FileStorageDatabase implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageDatabase.class);

    private final FileDatabaseDao fileDaoDatabase;

    @Inject
    public FileStorageDatabase(FileDatabaseDao fileDaoDatabase) {
        this.fileDaoDatabase = fileDaoDatabase;
    }

    @Override
    public void upload(Long fileId, String fileUniqueName, byte[] fileData) {
        logger.info("Uploading file {}", fileUniqueName);
        this.fileDaoDatabase.upload(
            fileId,
            fileData
        );
    }

    @Override
    public Optional<byte[]> fetch(String fileType, String fileUniqueName) {
        return this.fileDaoDatabase.fetch(fileUniqueName);
    }

    @Override
    public List<String> deleteFiles(List<String> fileUniqueNames) {
        List<String> filesNotDeleted = new ArrayList<>();
        for (String fileToDelete : fileUniqueNames) {
            boolean hasBeenDeleted = this.fileDaoDatabase.delete(fileToDelete);
            if (!hasBeenDeleted) {
                filesNotDeleted.add(fileToDelete);
            }
        }
        return filesNotDeleted;
    }
}

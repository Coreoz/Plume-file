package com.coreoz.plume.file.service;

import com.coreoz.plume.file.db.FileMetadataDatabaseDao;
import com.coreoz.plume.file.filetype.FileTypeDatabase;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.metadata.FileMetadataService;
import com.coreoz.plume.file.services.filetype.FileType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Singleton
public class FileMetadataDatabaseService implements FileMetadataService {

    private final FileMetadataDatabaseDao fileMetadataDao;

    @Inject
    public FileMetadataDatabaseService(FileMetadataDatabaseDao fileMetadataDao) {
        this.fileMetadataDao = fileMetadataDao;
    }

    @Override
    public void add(String fileUniqueName, String originalName, String fileType, String fileExtension, String mimeType, long fileSize) {
        fileMetadataDao.add(fileUniqueName, originalName, fileType, fileExtension, mimeType, fileSize);
    }

    @Override
    public void updateFileSize(String fileUniqueName, long fileSize) {
        fileMetadataDao.updateFileSize(fileUniqueName, fileSize);
    }

    @Override
    public Optional<FileMetadata> fetch(String fileUniqueName) {
        return fileMetadataDao.fetch(fileUniqueName);
    }

    @Override
    public List<String> findUnreferencedFiles(Collection<FileType> fileTypes) {
        Collection<FileTypeDatabase> fileTypesDatabase = new ArrayList<>();
        for (FileType fileType : fileTypes) {
            if (fileType instanceof FileTypeDatabase) {
                fileTypesDatabase.add((FileTypeDatabase) fileType);
                continue;
            }
            throw new IllegalArgumentException();
        }
        return this.fileMetadataDao.findUnreferencedFiles(fileTypesDatabase);
    }

    @Override
    public void deleteAll(List<String> fileUniqueNamesDeleted) {
        this.fileMetadataDao.deleteFilesMetadata(fileUniqueNamesDeleted);
    }

}

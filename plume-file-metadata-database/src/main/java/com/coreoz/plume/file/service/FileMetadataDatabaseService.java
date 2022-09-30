package com.coreoz.plume.file.service;

import com.coreoz.plume.file.db.FileMetadataDatabaseDao;
import com.coreoz.plume.file.filetype.FileTypeDatabase;
import com.coreoz.plume.file.filetype.FileTypesProvider;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.coreoz.plume.file.services.metadata.FileMetadataService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Singleton
public class FileMetadataDatabaseService implements FileMetadataService {

    private final FileMetadataDatabaseDao fileMetadataDao;
    private final Collection<FileTypeDatabase> fileTypes;

    @Inject
    public FileMetadataDatabaseService(FileMetadataDatabaseDao fileMetadataDao, FileTypesProvider fileTypesProvider) {
        this.fileMetadataDao = fileMetadataDao;
        this.fileTypes = fileTypesProvider.fileTypesAvailable();
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
    public FileType fileType(String fileType) {
        return this.fileTypes.stream().filter(fileTypeDatabase -> fileTypeDatabase.name().equals(fileType))
            .findFirst()
            .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public List<String> findUnreferencedFiles() {
        return this.fileMetadataDao.findUnreferencedFiles(this.fileTypes);
    }

    @Override
    public void deleteAll(List<String> fileUniqueNamesDeleted) {
        this.fileMetadataDao.deleteFilesMetadata(fileUniqueNamesDeleted);
    }

}

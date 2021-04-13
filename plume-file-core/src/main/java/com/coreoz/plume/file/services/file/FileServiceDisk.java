package com.coreoz.plume.file.services.file;

import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.beans.FileEntryDisk;
import com.coreoz.plume.file.db.querydsl.disk.FileDaoDiskQuerydsl;
import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.file.data.FileUploaded;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.filetype.FileTypesProvider;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.coreoz.plume.file.utils.FileNameUtils;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

// TODO file creation should be unit tested
@Singleton
public class FileServiceDisk extends FileStorageAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceDisk.class);

    private final String path;

    @Inject
    public FileServiceDisk(
        FileDaoDiskQuerydsl fileDao,
        FileTypesProvider fileTypesProvider,
        FileConfigurationService configurationService,
        ChecksumService checksumService,
        FileCacheService fileCacheService
    ) {
        super(fileDao, fileTypesProvider, checksumService, configurationService, fileCacheService);
        this.path = configurationService.mediaLocalPath();
    }

    @Override
    public FileUploaded upload(FileType fileType, String fileExtension, byte[] fileData) {
        if (Strings.isNullOrEmpty(fileExtension)) {
            throw new RuntimeException("File extension must be declared to be saved on disk");
        }

        FileEntry file = this.fileDao.upload(fileType.name(), fileExtension, null);

        this.createFile(
            file.getFileType(),
            file.getFileName(),
            fileData
        );

        return FileUploaded.of(
            file.getId(),
            file.getUid(),
            urlRaw(file.getUid())
        );
    }

    private void createFile(String fileType, String fileName, byte[] fileData) {
        String currentPath = this.getFolderPath(fileType);
        try {
            if (!new File(currentPath).exists()) {
                new File(currentPath).mkdirs();
            }

            File fileToSave = new File(this.getFullPath(fileType, fileName));
            Files.write(fileData, fileToSave);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String fileUid) {
        Optional<FileData> fileData = super.fetch(fileUid);
        if (fileData.isPresent()) {
            deleteFile(this.getFullPath(fileData.get().getFileType(), fileData.get().getFileName()));
            super.delete(fileData.get().getUid());
        }
    }

    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.delete()) {
                logger.debug("{} deleted", file);
            } else {
                logger.error("Cannot delete file {}", file);
            }
        } else {
            logger.warn("{} does not exists ", file);
        }
    }

    @Override
    public byte[] getData(FileEntry fileEntry) {
        try {
            if (fileEntry instanceof FileEntryDisk) {
                File file = new File(this.getFullPath(fileEntry.getFileType(), fileEntry.getFileName()));
                if (!file.exists()) {
                    return new byte[0];
                }
                return Files.toByteArray(file);
            }
            return new byte[0];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFolderPath(String fileType) {
        return String.format("%s/%s", this.path, fileType);
    }
    private String getFullPath(String fileType, String fileName) {
        return String.format("%s/%s/%s", this.path, fileType, fileName);
    }
}

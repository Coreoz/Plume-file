package com.coreoz.plume.file.services.file;

import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.beans.FileEntryDatabase;
import com.coreoz.plume.file.db.querydsl.database.FileDaoDatabaseQuerydsl;
import com.coreoz.plume.file.services.cache.FileCacheService;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.data.FileUploaded;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.filetype.FileTypesProvider;
import com.coreoz.plume.file.services.hash.ChecksumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FileServiceDatabase extends FileStorageAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceDatabase.class);

    @Inject
    public FileServiceDatabase(
        FileDaoDatabaseQuerydsl fileDaoDatabase,
        FileTypesProvider fileTypesProvider,
        ChecksumService checksumService,
        FileConfigurationService fileConfigurationService,
        FileCacheService cacheService
    ) {
        super(fileDaoDatabase, fileTypesProvider, checksumService, fileConfigurationService, cacheService);
    }

    @Override
    public FileUploaded upload(FileType fileType, String fileExtension, byte[] fileData) {
        FileEntry file = this.fileDao.upload(
            fileType.name(),
            fileExtension,
            fileData
        );

        return FileUploaded.of(
            file.getId(),
            file.getUid(),
            super.urlRaw(file.getUid())
        );
    }

    @Override
    public byte[] getData(FileEntry fileEntry) {
        if (fileEntry instanceof FileEntryDatabase) {
            return ((FileEntryDatabase) fileEntry).getData();
        }
        return new byte[0];
    }

}

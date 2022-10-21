package com.coreoz.plume.file.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.plume.file.db.FileStorageDao;
import com.coreoz.plume.file.services.data.MeasuredSizeInputStream;
import com.coreoz.plume.file.services.storage.FileStorageService;

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
        MeasuredSizeInputStream measuredSizeInputStream = new MeasuredSizeInputStream(fileData);
        this.fileStorageDao.add(
            fileUniqueName,
            measuredSizeInputStream
        );
        return measuredSizeInputStream.getInputStreamTotalSize();
    }

    @Override
    public Optional<InputStream> fetch(String fileUniqueName) {
        return this.fileStorageDao.fetch(fileUniqueName);
    }

    @Override
    public void deleteAll(List<String> fileUniqueNames) throws IOException {
    	try {
    		this.fileStorageDao.deleteAll(fileUniqueNames);
    	} catch (Exception e) {
			throw new IOException(e);
		}
    }
}

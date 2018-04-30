package com.coreoz.plume.file.services.file;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreoz.plume.db.utils.IdGenerator;
import com.coreoz.plume.file.db.querydsl.FileDaoDiskQuerydsl;
import com.coreoz.plume.file.db.querydsl.FileEntityDiskQuerydsl;
import com.coreoz.plume.file.db.querydsl.FileEntityQuerydsl;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.file.data.FileUploaded;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.filetype.FileTypesProvider;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.coreoz.plume.file.utils.FileNameUtils;
import com.google.common.base.Strings;
import com.google.common.io.Files;

// TODO should be unit tested
public class FileServiceDisk implements FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceDisk.class);
    protected String path;
    private String baseUrl;
    private final FileDaoDiskQuerydsl fileDao;
    private final FileTypesProvider fileTypesProvider;
    private final ChecksumService checksumService;

    @Inject
    public FileServiceDisk(FileDaoDiskQuerydsl fileDao,
                           FileTypesProvider fileTypesProvider,
                           FileConfigurationService configurationService,
                           ChecksumService checksumService) {
        path = configurationService.mediaLocalPath();
        baseUrl = configurationService.apiBasePath() + "/";
        this.fileDao = fileDao;
        this.fileTypesProvider = fileTypesProvider;
        this.checksumService = checksumService;
    }

    @Override
    public FileUploaded upload(FileType fileType, byte[] fileData, @Nullable String filename) {
        String fileName = FileNameUtils.sanitize(Strings.nullToEmpty(filename));

        String relativePath = String.valueOf(IdGenerator.generate()) + extractFileExtension(fileName);

        FileEntityQuerydsl file = fileDao.upload(fileType.name(), fileName, relativePath);

        createFile(path, fileData, relativePath);

        return FileUploaded.of(file.getId(), url(file.getId(), fileName));
    }

    private void createFile(String path, byte[] fileData, @Nullable String fileName) {
        try {
            if (!new File(path).exists()) {
                new File(path).mkdirs();
            }

            File fileToSave = new File(path + fileName);
            Files.write(fileData, fileToSave);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long fileId) {
        FileEntityDiskQuerydsl fileEntry = fileDao.findFileDiskById(fileId);
        deleteFile(path + fileEntry.getPath());
        fileDao.delete(fileId);
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
    public void deleteUnreferenced() {
        fileTypesProvider
            .fileTypesAvailable()
            .forEach(fileType ->
                fileDao.selectUnreferenced(
                    fileType.name(),
                    fileType.getFileEntity(),
                    fileType.getJoinColumn()
                ).forEach(file -> delete(file.getId()))
            );
    }

    @Override
    public Optional<String> url(Long fileId) {
        if (fileId == null) {
            return Optional.empty();
        }

        return Optional
        	// TODO instead of fecthing the whole table row, this should only fetch the file name
        	.ofNullable(fileDao.findFileDiskById(fileId))
        	.map(FileEntityDiskQuerydsl::getPath)
        	.map(fileName -> url(fileId, fileName));
    }

    private String url(Long fileId, String fileName) {
    	return baseUrl + "files/" + fileId + "/" + fileName;
    }

    @Override
    public String urlRaw(Long fileId) {
        throw new RuntimeException("This method is unavailable when files are stored on disk");
    }

    @Override
    public Optional<FileData> fetch(Long fileId) {
        if (fileId == null) {
            return Optional.empty();
        }
        FileEntityDiskQuerydsl fileEntityDisk = fileDao.findFileDiskById(fileId);
        FileEntityQuerydsl fileEntity = fileDao.findById(fileId);
        return fetchFile(fileEntity, path + fileEntityDisk.getPath());
    }

    private Optional<FileData> fetchFile(FileEntityQuerydsl fileEntity, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return Optional.empty();
        }

        try {
            return Optional.of(FileData.of(
                fileEntity.getId(),
                fileEntity.getFilename(),
                fileEntity.getFileType(),
                FileNameUtils.guessMimeType(fileEntity.getFilename()),
                checksumService.hash(fileEntity.getFilename().getBytes()),
                Files.toByteArray(file)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractFileExtension(String filename) {
        String[] filenameSplited = filename.split("[.]");
        if (filenameSplited.length < 1) {
            return "";
        }
        return "." + filenameSplited[filenameSplited.length - 1].toLowerCase();
    }
}

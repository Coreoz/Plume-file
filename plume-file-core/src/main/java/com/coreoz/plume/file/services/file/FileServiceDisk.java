package com.coreoz.plume.file.services.file;

import com.coreoz.plume.file.db.querydsl.beans.FileEntryDisk;
import com.coreoz.plume.file.db.querydsl.disk.FileDaoDiskQuerydsl;
import com.coreoz.plume.file.services.configuration.FileConfigurationService;
import com.coreoz.plume.file.services.file.data.FileData;
import com.coreoz.plume.file.services.file.data.FileUploaded;
import com.coreoz.plume.file.services.filetype.FileType;
import com.coreoz.plume.file.services.filetype.FileTypesProvider;
import com.coreoz.plume.file.services.hash.ChecksumService;
import com.coreoz.plume.file.utils.FileNameUtils;
import com.coreoz.plume.jersey.errors.WsError;
import com.coreoz.plume.jersey.errors.WsException;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

// TODO should be unit tested
@Singleton
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
        this.path = configurationService.mediaLocalPath();
        this.baseUrl = configurationService.apiBasePath() + "/";
        this.fileDao = fileDao;
        this.fileTypesProvider = fileTypesProvider;
        this.checksumService = checksumService;
    }

    @Override
    public FileUploaded upload(FileType fileType, byte[] fileData, String filename) {
        if (Strings.isNullOrEmpty(filename)) {
            throw new WsException(new WsError.WsErrorInternal("File name must be declared to be saved on disk"));
        }
        String fileName = FileNameUtils.sanitize(filename);

        FileEntryDisk file = this.fileDao.upload(fileType.name(), fileName, fileName);

        createFile(this.path, fileData, file.getPath());

        return FileUploaded.of(
            file.getId(),
            file.getUid(),
            url(file.getUid(), fileName)
        );
    }

    private void createFile(String path, byte[] fileData, String fileName) {
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
    public void delete(String fileUid) {
        FileEntryDisk fileEntry = this.fileDao.findByUid(fileUid);
        deleteFile(this.path + fileEntry.getPath());
        fileDao.delete(fileUid);
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
                this.fileDao.deleteUnreferenced(
                    fileType.name(),
                    fileType.getFileEntity(),
                    fileType.getJoinColumn()
                ));
    }

    @Override
    public Optional<String> url(String fileUid) {
        if (fileUid == null) {
            return Optional.empty();
        }

        return Optional
            // TODO instead of fecthing the whole table row, this should only fetch the file name
            .ofNullable(this.fileDao.findByUid(fileUid))
            .map(FileEntryDisk::getFilename)
            .map(fileName -> url(fileUid, fileName));
    }

    @Override
    public Optional<String> url(Long fileId) {
        FileEntryDisk fileEntryDisk = this.fileDao.findById(fileId);
        return this.url(fileEntryDisk.getUid());
    }

    private String url(String fileUid, String fileName) {
        return baseUrl + "files/" + fileUid + "/" + fileName;
    }

    @Override
    public String urlRaw(String fileUid) {
        throw new RuntimeException("This method is unavailable when files are stored on disk");
    }

    @Override
    public Optional<FileData> fetch(String fileUid) {
        if (fileUid == null) {
            return Optional.empty();
        }
        FileEntryDisk fileWithPath = fileDao.findByUid(fileUid);
        if (fileWithPath == null) {
            return Optional.empty();
        }
        return fetchFile(fileWithPath, path + fileWithPath.getPath());
    }

    private Optional<FileData> fetchFile(FileEntryDisk fileEntity, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return Optional.empty();
        }

        try {
            return Optional.of(FileData.of(
                fileEntity.getId(),
                fileEntity.getUid(),
                fileEntity.getFilename(),
                fileEntity.getFileType(),
                FileNameUtils.guessMimeType(fileEntity.getFilename()),
                checksumService.hash(fileEntity.getFilename().getBytes()),
                Files.toByteArray(file)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

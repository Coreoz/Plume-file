package com.coreoz.plume.file.db.dao;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.db.utils.IdGenerator;
import com.coreoz.plume.file.db.beans.FileMetadataQuerydsl;
import com.coreoz.plume.file.db.beans.QFileMetadataQuerydsl;
import com.coreoz.plume.file.services.filetype.FileType;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class FileMetadataDao {

    private final TransactionManagerQuerydsl transactionManager;

    @Inject
    public FileMetadataDao(TransactionManagerQuerydsl transactionManager) {
        this.transactionManager = transactionManager;
    }

    public FileMetadataQuerydsl upload(String fileUniqueName, String fileType, String mimeType, long fileSize) {
        FileMetadataQuerydsl file = new FileMetadataQuerydsl();
        file.setId(IdGenerator.generate());
        file.setUniqueName(fileUniqueName);
        file.setFileType(fileType);
        file.setMimeType(mimeType);
        file.setFileSize(fileSize);

        transactionManager
            .insert(QFileMetadataQuerydsl.fileMetadata)
            .populate(file)
            .execute();

        return file;
    }

	public Optional<FileMetadata> fetch(String fileUniqueName) {
		return Optional.ofNullable(
				transactionManager
				.selectQuery()
				.select(
					QFileMetadataQuerydsl.fileMetadata.fileType,
					QFileMetadataQuerydsl.fileMetadata.mimeType
				)
				.from(QFileMetadataQuerydsl.fileMetadata)
				.where(QFileMetadataQuerydsl.fileMetadata.uniqueName.eq(fileUniqueName))
				.fetchOne()
			)
			.map(tuple -> new FileMetadata(
				tuple.get(QFileMetadataQuerydsl.fileMetadata.fileType),
				tuple.get(QFileMetadataQuerydsl.fileMetadata.mimeType)
			));
	}

	public List<String> findUnreferencedFiles(Collection<FileType> fileTypes) {
		return this.transactionManager.selectQuery()
			.select(QFileMetadataQuerydsl.fileMetadata.uniqueName)
			.from(QFileMetadataQuerydsl.fileMetadata)
			.where(QFileMetadataQuerydsl.fileMetadata.fileType.in(
				fileTypes.stream().map(FileType::name).collect(Collectors.toList()))
			)
			.fetch();
	}

    public void deleteFilesMetadata(List<String> fileUniqueNames) {
        this.transactionManager.delete(QFileMetadataQuerydsl.fileMetadata)
            .where(QFileMetadataQuerydsl.fileMetadata.uniqueName.in(fileUniqueNames))
            .execute();
    }

}

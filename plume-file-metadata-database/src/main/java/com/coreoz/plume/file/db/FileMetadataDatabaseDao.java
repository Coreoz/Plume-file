package com.coreoz.plume.file.db;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.beans.FileMetadataQuerydsl;
import com.coreoz.plume.file.db.beans.QFileMetadataQuerydsl;
import com.coreoz.plume.file.filetype.FileTypeDatabase;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLExpressions;

@Singleton
public class FileMetadataDatabaseDao {
    private final TransactionManagerQuerydsl transactionManager;

    @Inject
    public FileMetadataDatabaseDao(TransactionManagerQuerydsl transactionManager) {
        this.transactionManager = transactionManager;
    }

    public FileMetadataQuerydsl add(
        String fileUniqueName,
        String fileOriginalName,
        String fileType,
        String fileExtension,
        String mimeType
    ) {
        FileMetadataQuerydsl fileMetadata = new FileMetadataQuerydsl();
        fileMetadata.setUniqueName(fileUniqueName);
        fileMetadata.setFileOriginalName(fileOriginalName);
        fileMetadata.setFileType(fileType);
        fileMetadata.setFileExtension(fileExtension);
        fileMetadata.setMimeType(mimeType);
        fileMetadata.setCreationDate(Instant.now());

        transactionManager
            .insert(QFileMetadataQuerydsl.fileMetadata)
            .populate(fileMetadata)
            .execute();

        return fileMetadata;
    }

    public void updateFileSizeAndChecksum(String fileUniqueName, long fileSize, String checksum) {
        transactionManager.update(QFileMetadataQuerydsl.fileMetadata)
            .set(QFileMetadataQuerydsl.fileMetadata.fileSize, fileSize)
            .set(QFileMetadataQuerydsl.fileMetadata.checksum, checksum)
            .where(QFileMetadataQuerydsl.fileMetadata.uniqueName.eq(fileUniqueName))
            .execute();
    }

    public Optional<FileMetadata> fetch(String fileUniqueName) {
        return Optional.ofNullable(
            transactionManager.selectQuery()
                .select(QFileMetadataQuerydsl.fileMetadata)
                .from(QFileMetadataQuerydsl.fileMetadata)
                .where(QFileMetadataQuerydsl.fileMetadata.uniqueName.eq(fileUniqueName))
                .fetchOne()
        );
    }

    public List<String> findUnreferencedFiles(Collection<FileTypeDatabase> fileTypes) {
        List<BooleanExpression> conditions = new ArrayList<>();
        for (FileTypeDatabase fileTypeDatabase : fileTypes) {
            conditions.add(
                QFileMetadataQuerydsl.fileMetadata.uniqueName.notIn(
                    SQLExpressions.select(fileTypeDatabase.getJoinColumn())
                        .from(fileTypeDatabase.getFileEntity())
                        .where(fileTypeDatabase.getJoinColumn().isNotNull())
                ).and(
                    QFileMetadataQuerydsl.fileMetadata.fileType.eq(fileTypeDatabase.name())
                )
            );
        }
        return this.transactionManager.selectQuery()
            .select(QFileMetadataQuerydsl.fileMetadata.uniqueName)
            .from(QFileMetadataQuerydsl.fileMetadata)
            .where(conditions.stream().reduce(BooleanExpression::or).orElse(Expressions.asBoolean(true)))
            .fetch();
    }

    public List<String> findFilesHavingDeletedTypes(Collection<FileTypeDatabase> remainingFileTypes) {
        List<BooleanExpression> conditions = new ArrayList<>();
        for (FileTypeDatabase fileTypeDatabase : remainingFileTypes) {
            conditions.add(
                QFileMetadataQuerydsl.fileMetadata.fileType.ne(fileTypeDatabase.name())
            );
        }
        return this.transactionManager.selectQuery()
            .select(QFileMetadataQuerydsl.fileMetadata.uniqueName)
            .from(QFileMetadataQuerydsl.fileMetadata)
            .where(conditions.stream().reduce(BooleanExpression::and).orElse(Expressions.asBoolean(true)))
            .fetch();
    }

    public long deleteFilesMetadata(List<String> fileUniqueNames) {
        return this.transactionManager.delete(QFileMetadataQuerydsl.fileMetadata)
            .where(QFileMetadataQuerydsl.fileMetadata.uniqueName.in(fileUniqueNames))
            .execute();
    }
}

package com.coreoz.plume.file.db;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.beans.FileMetadataQuerydsl;
import com.coreoz.plume.file.db.beans.QFileMetadataQuerydsl;
import com.coreoz.plume.file.filetype.FileTypeDatabase;
import com.coreoz.plume.file.services.metadata.FileMetadata;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLExpressions;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        String mimeType,
        long fileSize
    ) {
        FileMetadataQuerydsl fileMetadata = new FileMetadataQuerydsl();
        fileMetadata.setUniqueName(fileUniqueName);
        fileMetadata.setFileOriginalName(fileOriginalName);
        fileMetadata.setFileType(fileType);
        fileMetadata.setFileExtension(fileExtension);
        fileMetadata.setMimeType(mimeType);
        fileMetadata.setFileSize(fileSize);
        fileMetadata.setCreationDate(Instant.now());

        transactionManager
            .insert(QFileMetadataQuerydsl.fileMetadata)
            .populate(fileMetadata)
            .execute();

        return fileMetadata;
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

    public long deleteFilesMetadata(List<String> fileUniqueNames) {
        return this.transactionManager.delete(QFileMetadataQuerydsl.fileMetadata)
            .where(QFileMetadataQuerydsl.fileMetadata.uniqueName.in(fileUniqueNames))
            .execute();
    }
}

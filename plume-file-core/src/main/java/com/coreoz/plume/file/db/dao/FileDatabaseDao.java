package com.coreoz.plume.file.db.dao;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.beans.QFileDataQueryDsl;
import com.coreoz.plume.file.db.beans.QFileMetadataQuerydsl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class FileDatabaseDao {

    private final TransactionManagerQuerydsl transactionManager;

    @Inject
    public FileDatabaseDao(TransactionManagerQuerydsl transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void upload(Long fileId, byte[] fileData) {
        transactionManager
            .insert(QFileDataQueryDsl.file)
            .columns(
                QFileDataQueryDsl.file.id,
                QFileDataQueryDsl.file.data
            )
            .values(
                fileId,
                fileData
            )
            .execute();
    }

    public Optional<byte[]> fetch(String fileUniqueName) {
        return Optional.ofNullable(
            this.transactionManager.selectQuery()
                .select(QFileDataQueryDsl.file.data)
                .from(QFileDataQueryDsl.file)
                .innerJoin(QFileMetadataQuerydsl.fileMetadata)
                .on(QFileMetadataQuerydsl.fileMetadata.id.eq(QFileDataQueryDsl.file.id))
                .where(QFileMetadataQuerydsl.fileMetadata.uniqueName.eq(fileUniqueName))
                .fetchOne()
        );
    }

    public boolean delete(String fileUniqueName) {
        long nbDeleted = transactionManager
            .delete(QFileDataQueryDsl.file)
            .where(QFileDataQueryDsl.file.id.eq(
                this.transactionManager.selectQuery()
                    .select(QFileMetadataQuerydsl.fileMetadata.id)
                    .from(QFileMetadataQuerydsl.fileMetadata)
                    .where(QFileMetadataQuerydsl.fileMetadata.uniqueName.eq(fileUniqueName))
            ))
            .execute();
        return nbDeleted > 0;
    }

}

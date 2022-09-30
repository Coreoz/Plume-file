package com.coreoz.plume.file.db;

import java.io.InputStream;
import java.sql.Blob;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.beans.QFileDataQueryDsl;

import lombok.SneakyThrows;

@Singleton
public class FileStorageDao {
    private final TransactionManagerQuerydsl transactionManager;

    @Inject
    public FileStorageDao(TransactionManagerQuerydsl transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void add(String uniqueName, InputStream fileData) {
        transactionManager
            .insert(QFileDataQueryDsl.file)
            .columns(
                QFileDataQueryDsl.file.uniqueName,
                QFileDataQueryDsl.file.data
            )
            .values(
                uniqueName,
                fileData
            )
            .execute();
    }

    @SneakyThrows
    public Optional<InputStream> fetch(String fileUniqueName) {
        Blob fileData = this.transactionManager.selectQuery()
                .select(QFileDataQueryDsl.file.data)
                .from(QFileDataQueryDsl.file)
                .where(QFileDataQueryDsl.file.uniqueName.eq(fileUniqueName))
                .fetchFirst();
        return fileData == null ? Optional.empty() : Optional.of(fileData.getBinaryStream());
    }

    public boolean delete(String fileUniqueName) {
        long nbDeleted = transactionManager
            .delete(QFileDataQueryDsl.file)
            .where(QFileDataQueryDsl.file.uniqueName.eq(fileUniqueName))
            .execute();
        return nbDeleted > 0;
    }
}

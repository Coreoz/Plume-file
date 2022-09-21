package com.coreoz.plume.file.db;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.beans.QFileDataQueryDsl;
import com.coreoz.plume.file.services.data.MeasuredSizeInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.Optional;

@Singleton
public class FileStorageDao {
    private final TransactionManagerQuerydsl transactionManager;

    @Inject
    public FileStorageDao(TransactionManagerQuerydsl transactionManager) {
        this.transactionManager = transactionManager;
    }

    public long add(String uniqueName, MeasuredSizeInputStream fileData) {
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

        return fileData.getInputStreamTotalSize();
    }

    public Optional<InputStream> fetch(String fileUniqueName) {
        return Optional.ofNullable(
            this.transactionManager.selectQuery()
                .select(QFileDataQueryDsl.file.data)
                .from(QFileDataQueryDsl.file)
                .where(QFileDataQueryDsl.file.uniqueName.eq(fileUniqueName))
                .fetchOne()
        );
    }

    public boolean delete(String fileUniqueName) {
        long nbDeleted = transactionManager
            .delete(QFileDataQueryDsl.file)
            .where(QFileDataQueryDsl.file.uniqueName.eq(fileUniqueName))
            .execute();
        return nbDeleted > 0;
    }
}

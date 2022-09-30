package com.coreoz.plume.file.db;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.beans.QFileDataQueryDsl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Function;

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

    public Optional<InputStream> fetch(String fileUniqueName) {
        Blob fileData = this.transactionManager.selectQuery()
                .select(QFileDataQueryDsl.file.data)
                .from(QFileDataQueryDsl.file)
                .where(QFileDataQueryDsl.file.uniqueName.eq(fileUniqueName))
                .fetchFirst();
        return Optional.ofNullable(fileData)
            .map(unwrapBlob());
    }

    public boolean delete(String fileUniqueName) {
        long nbDeleted = transactionManager
            .delete(QFileDataQueryDsl.file)
            .where(QFileDataQueryDsl.file.uniqueName.eq(fileUniqueName))
            .execute();
        return nbDeleted > 0;
    }

    private static Function<Blob, InputStream> unwrapBlob() {
        return blob -> {
            try {
                return blob.getBinaryStream();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }
}

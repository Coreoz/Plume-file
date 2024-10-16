package com.coreoz.plume.file.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.beans.QFileDataQueryDsl;

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

    public void deleteAll(List<String> fileUniqueNames) {
        transactionManager
            .delete(QFileDataQueryDsl.file)
            .where(QFileDataQueryDsl.file.uniqueName.in(fileUniqueNames))
            .execute();
    }

    private static Function<Blob, InputStream> unwrapBlob() {
        return blob -> {
            try {
                return blob.getBinaryStream();
            } catch (SQLException e) {
                throw new UncheckedIOException(new IOException(e));
            }
        };
    }
}

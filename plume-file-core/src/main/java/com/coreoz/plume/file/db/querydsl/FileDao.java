package com.coreoz.plume.file.db.querydsl;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.db.utils.IdGenerator;
import com.coreoz.plume.file.db.FileEntry;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public abstract class FileDao {

    protected final TransactionManagerQuerydsl transactionManager;

    public FileDao(TransactionManagerQuerydsl transactionManager) {
        this.transactionManager = transactionManager;
    }

    public abstract FileEntry upload(String fileType, String fileExtension, @Nullable byte[] fileData);

    public abstract SQLQuery<Tuple> fileQuery();

    public abstract Function<Tuple, FileEntry> toFileEntry();

    public FileEntityQuerydsl uploadEntity(String fileType, String fileExtension, Connection connection) {
        FileEntityQuerydsl file = new FileEntityQuerydsl();
        file.setFileExtension(fileExtension);
        file.setFileType(fileType);
        file.setId(IdGenerator.generate());
        file.setUid(UUID.randomUUID().toString());

        this.transactionManager
            .insert(QFileEntityQuerydsl.file, connection)
            .populate(file)
            .execute();

        return file;
    }

    /**
     * Fetch the file name corresponding to the file identifier.
     *
     * @return The file name if the file exists and the file name is not null.
     * If the file name is null an empty string is returned.
     * If the file is null, null is returned
     */
    public Optional<String> fileName(String fileUid) {
        return Optional.ofNullable(
            this.transactionManager.selectQuery()
                .select(QFileEntityQuerydsl.file.uid, QFileEntityQuerydsl.file.fileExtension)
                .from(QFileEntityQuerydsl.file)
                .where(QFileEntityQuerydsl.file.uid.eq(fileUid))
                .fetchFirst()
        ).map(tuple -> fileName(tuple.get(QFileEntityQuerydsl.file.uid), tuple.get(QFileEntityQuerydsl.file.fileExtension)));
    }

    public Optional<FileEntry> findByUid(String fileUid) {
        return Optional.ofNullable(
            this.fileQuery()
                .where(QFileEntityQuerydsl.file.uid.eq(fileUid))
                .fetchFirst()
        ).map(this.toFileEntry());
    }

    public Optional<FileEntry> findById(Long fileId) {
        return Optional.ofNullable(
            this.fileQuery()
                .where(QFileEntityQuerydsl.file.id.eq(fileId))
                .fetchFirst()
        ).map(this.toFileEntry());
    }

    public long deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
        return this.selectUnreferenced(fileType, fileEntity, column)
            .stream()
            .mapToLong(file -> this.transactionManager.executeAndReturn(connection -> delete(file.getId(), connection)) ? 1 : 0)
            .count();
    }

    public List<FileEntityQuerydsl> selectUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
        return transactionManager
            .selectQuery()
            .select(QFileEntityQuerydsl.file)
            .from(QFileEntityQuerydsl.file)
            .where(QFileEntityQuerydsl.file.fileType.eq(fileType))
            .where(QFileEntityQuerydsl.file.id.notIn(
                SQLExpressions
                    .select(column)
                    .from(fileEntity)
            ))
            .fetch();
    }

    public boolean delete(Long fileId) {
        return this.transactionManager.executeAndReturn(connection -> this.delete(fileId, connection));
    }

    public boolean delete(String fileUid) {
        return this.transactionManager.executeAndReturn(connection -> {
            Long fileId = this.transactionManager.selectQuery(connection)
                .select(QFileEntityQuerydsl.file.id)
                .from(QFileEntityQuerydsl.file)
                .where(QFileEntityQuerydsl.file.uid.eq(fileUid))
                .fetchOne();
            return this.delete(fileId, connection);
        });
    }

    public boolean delete(Long fileId, Connection connection) {
        return transactionManager.delete(QFileEntityQuerydsl.file, connection)
            .where(QFileEntityQuerydsl.file.id.eq(fileId))
            .execute() > 0;
    }

    public static String fileName(String uid, String fileExtension) {
        return String.format("%s.%s", uid, fileExtension);
    }
}

package com.coreoz.plume.file.db.querydsl.database;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.db.utils.IdGenerator;
import com.coreoz.plume.file.db.FileDaoDatabase;
import com.coreoz.plume.file.db.FileEntry;
import com.coreoz.plume.file.db.querydsl.FileEntityQuerydsl;
import com.coreoz.plume.file.db.querydsl.QFileEntityQuerydsl;
import com.coreoz.plume.file.db.querydsl.beans.FileEntryDatabase;
import com.google.common.base.Strings;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class FileDaoDatabaseQuerydsl implements FileDaoDatabase {
    private final TransactionManagerQuerydsl transactionManager;

    @Inject
    public FileDaoDatabaseQuerydsl(TransactionManagerQuerydsl transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public FileEntry upload(String fileType, byte[] fileData, String fileName) {
        FileEntityQuerydsl file = new FileEntityQuerydsl();
        file.setId(IdGenerator.generate());
        file.setFileType(fileType);
        file.setFilename(fileName);
        file.setUid(UUID.randomUUID().toString());

        transactionManager.execute(connection -> {
            transactionManager
                .insert(QFileEntityQuerydsl.file, connection)
                .populate(file)
                .execute();

            transactionManager
                .insert(QFileDatabaseEntityQuerydsl.fileData, connection)
                .columns(
                    QFileDatabaseEntityQuerydsl.fileData.idFile,
                    QFileDatabaseEntityQuerydsl.fileData.data
                )
                .values(file.getId(), fileData)
                .execute();
        });

        return FileEntryDatabase.of(
            file.getId(),
            file.getUid(),
            fileName,
            fileType,
            fileData
        );
    }

    @Override
    public String delete(String uid) {
        return transactionManager
            .executeAndReturn(connection ->
                this.delete(uid, connection)
            );
    }

    private String delete(String uid, Connection connection) {
        Long fileId = transactionManager
            .selectQuery(connection)
            .select(QFileEntityQuerydsl.file.id)
            .from(QFileEntityQuerydsl.file)
            .where(QFileEntityQuerydsl.file.uid.eq(uid))
            .fetchFirst();

        transactionManager
            .delete(QFileDatabaseEntityQuerydsl.fileData, connection)
            .where(QFileDatabaseEntityQuerydsl.fileData.idFile.eq(fileId))
            .execute();

        transactionManager
            .delete(QFileEntityQuerydsl.file, connection)
            .where(QFileEntityQuerydsl.file.id.eq(fileId))
            .execute();

        return uid;
    }

    @Override
    public String fileName(String fileUid) {
        Tuple tuple = transactionManager
            .selectQuery()
            .select(QFileEntityQuerydsl.file.id, QFileEntityQuerydsl.file.filename)
            .from(QFileEntityQuerydsl.file)
            .where(QFileEntityQuerydsl.file.uid.eq(fileUid))
            .fetchOne();

        return tuple == null ?
            null
            : Strings.nullToEmpty(tuple.get(QFileEntityQuerydsl.file.filename));
    }

    @Override
    public FileEntryDatabase findByUid(String fileUid) {
        return Optional.ofNullable(this.getBasicQuery()
                .where(QFileEntityQuerydsl.file.uid.eq(fileUid))
                .fetchFirst())
                .map(this.toFileEntryDisk())
                .orElse(null);
    }

    @Override
    public FileEntryDatabase findById(Long fileId) {
        return Optional.ofNullable(
            this.getBasicQuery()
                .where(QFileEntityQuerydsl.file.id.eq(fileId))
                .fetchFirst())
            .map(this.toFileEntryDisk())
            .orElse(null);
    }

    @Override
    public List<String> deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
        return this.selectUnreferenced(fileType, fileEntity, column)
            .stream()
            .map(file -> transactionManager.executeAndReturn(connection -> delete(file.getUid(), connection)))
            .collect(Collectors.toList());
    }

    private List<FileEntityQuerydsl> selectUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
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

    private SQLQuery<Tuple> getBasicQuery() {
        return transactionManager
            .selectQuery()
            .select(
                QFileEntityQuerydsl.file.id,
                QFileEntityQuerydsl.file.uid,
                QFileEntityQuerydsl.file.fileType,
                QFileEntityQuerydsl.file.filename,
                QFileDatabaseEntityQuerydsl.fileData.data
            )
            .from(QFileEntityQuerydsl.file)
            .join(QFileDatabaseEntityQuerydsl.fileData)
            .on(QFileEntityQuerydsl.file.id.eq(QFileDatabaseEntityQuerydsl.fileData.idFile));
    }

    private Function<Tuple, FileEntryDatabase> toFileEntryDisk() {
        return row ->
            FileEntryDatabase.of(
                row.get(QFileEntityQuerydsl.file.id),
                row.get(QFileEntityQuerydsl.file.uid),
                row.get(QFileEntityQuerydsl.file.filename),
                row.get(QFileEntityQuerydsl.file.fileType),
                row.get(QFileDatabaseEntityQuerydsl.fileData.data)
            );
    }

}

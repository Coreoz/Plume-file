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
import java.util.UUID;

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
                .columns(
                    QFileEntityQuerydsl.file.uid,
                    QFileEntityQuerydsl.file.filename,
                    QFileEntityQuerydsl.file.fileType
                )
                .values(file.getUid(), file.getFilename(), file.getFileType())
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
    public Long deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
        return transactionManager.executeAndReturn(connection -> {
            SQLQuery<Long> unreferencedQuery = SQLExpressions
                .select(column)
                .from(fileEntity);
            //TODO a valider avec Aurélien
            transactionManager
                .delete(QFileDatabaseEntityQuerydsl.fileData, connection)
                .where(QFileEntityQuerydsl.file.fileType.eq(fileType)
                    .and(QFileEntityQuerydsl.file.id.notIn(unreferencedQuery)))
                .execute();

            return transactionManager
                .delete(QFileEntityQuerydsl.file, connection)
                .where(QFileEntityQuerydsl.file.fileType.eq(fileType)
                    .and(QFileEntityQuerydsl.file.id.notIn(unreferencedQuery))
                )
                .execute();
        });
    }

    @Override
    public FileEntryDatabase findByUid(String fileUid) {
        return transactionManager
            .selectQuery()
            .select(
                QFileEntityQuerydsl.file.id,
                QFileEntityQuerydsl.file.fileType,
                QFileEntityQuerydsl.file.filename,
                QFileDatabaseEntityQuerydsl.fileData.data
            )
            .from(QFileEntityQuerydsl.file)
            .join(QFileDatabaseEntityQuerydsl.fileData)
            .on(QFileEntityQuerydsl.file.id.eq(QFileDatabaseEntityQuerydsl.fileData.idFile))
            .where(QFileEntityQuerydsl.file.uid.eq(fileUid))
            .fetch()
            .stream()
            .map(row ->
                FileEntryDatabase.of(
                    row.get(QFileEntityQuerydsl.file.id),
                    row.get(QFileEntityQuerydsl.file.uid),
                    row.get(QFileEntityQuerydsl.file.filename),
                    row.get(QFileEntityQuerydsl.file.fileType),
                    row.get(QFileDatabaseEntityQuerydsl.fileData.data)
                )
            )
            .findFirst()
            .orElse(null);
    }

    @Override
    public String delete(String uid) {
        transactionManager
            .execute(connection -> {
                Long fileId = transactionManager
                    .selectQuery()
                    .select(QFileEntityQuerydsl.file.id)
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
            });

        return uid;
    }

}

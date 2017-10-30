package com.coreoz.plume.file.db.querydsl;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.file.db.FileDao;
import com.coreoz.plume.file.db.FileEntry;
import com.google.common.base.Strings;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.SQLExpressions;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FileDaoDbQuerydsl implements FileDao {
    private final TransactionManagerQuerydsl transactionManager;

    @Inject
    public FileDaoDbQuerydsl(TransactionManagerQuerydsl transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public FileEntry upload(String fileType, byte[] fileData, String fileName) {
        FileEntityQuerydsl file = new FileEntityQuerydsl();
        FileDataEntityQuerydsl fileDataBlob = new FileDataEntityQuerydsl();

        fileDataBlob.setData(fileData);
        file.setFileType(fileType);
        file.setFilename(fileName);

        final Long[] fileUploadedId = new Long[1];

        transactionManager.execute(connection -> {
            fileUploadedId[0] =
                transactionManager
                    .insert(QFileEntityQuerydsl.file, connection)
                    .values(file)
                    .execute();
            fileDataBlob.setId_file(fileUploadedId[0]);

            transactionManager
                .insert(QFileDataEntityQuerydsl.fileData, connection)
                .values(fileDataBlob)
                .execute();

        });

        return FileEntryUploaded.of(
            fileUploadedId[0],
            fileName,
            fileType,
            fileData
        );
    }

    @Override
    public String fileName(Long fileId) {
        Tuple tuple = transactionManager
            .selectQuery()
            .select(QFileEntityQuerydsl.file.id, QFileEntityQuerydsl.file.filename)
            .from(QFileEntityQuerydsl.file)
            .where(QFileEntityQuerydsl.file.id.eq(fileId))
            .fetchOne();

        return tuple == null ?
            null
            : Strings.nullToEmpty(tuple.get(QFileEntityQuerydsl.file.filename));
    }

    @Override
    public Long deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
        return transactionManager
            .delete(QFileEntityQuerydsl.file)
            .where(QFileEntityQuerydsl.file.fileType.eq(fileType))
            .where(QFileEntityQuerydsl.file.id.notIn(
                SQLExpressions
                    .select(column)
                    .from(fileEntity)
            ))
            .execute();
    }

    @Override
    public FileEntryUploaded findById(Long id) {
        return transactionManager
            .selectQuery()
            .select(
                QFileEntityQuerydsl.file.id,
                QFileEntityQuerydsl.file.fileType,
                QFileEntityQuerydsl.file.filename,
                QFileDataEntityQuerydsl.fileData.data
            )
            .from(QFileEntityQuerydsl.file)
            .join(QFileDataEntityQuerydsl.fileData)
            .on(QFileEntityQuerydsl.file.id.eq(QFileDataEntityQuerydsl.fileData.idFile))
            .fetch()
            .stream()
            .map(row ->
                FileEntryUploaded.of(
                    row.get(QFileEntityQuerydsl.file.id),
                    row.get(QFileEntityQuerydsl.file.filename),
                    row.get(QFileEntityQuerydsl.file.fileType),
                    row.get(QFileDataEntityQuerydsl.fileData.data)
                )
            )
            .findFirst()
            .orElse(null);
    }

    @Override
    public long delete(Long id) {
        transactionManager
            .execute(connection -> {
                transactionManager
                    .delete(QFileDataEntityQuerydsl.fileData, connection)
                    .where(QFileDataEntityQuerydsl.fileData.idFile.eq(id))
                    .execute();

                transactionManager
                    .delete(QFileEntityQuerydsl.file, connection)
                    .where(QFileEntityQuerydsl.file.id.eq(id))
                    .execute();
            });

        return id;
    }

}

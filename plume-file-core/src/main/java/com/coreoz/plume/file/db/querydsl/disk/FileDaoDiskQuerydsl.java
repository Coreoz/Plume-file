package com.coreoz.plume.file.db.querydsl.disk;

import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.db.utils.IdGenerator;
import com.coreoz.plume.file.db.FileDaoDisk;
import com.coreoz.plume.file.db.querydsl.FileEntityQuerydsl;
import com.coreoz.plume.file.db.querydsl.QFileEntityQuerydsl;
import com.coreoz.plume.file.db.querydsl.beans.FileEntryDisk;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.SQLExpressions;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileDaoDiskQuerydsl implements FileDaoDisk {
    private final TransactionManagerQuerydsl transactionManager;

    @Inject
    public FileDaoDiskQuerydsl(TransactionManagerQuerydsl transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public FileEntryDisk upload(String fileType, String fileName, String path) {
        FileEntityQuerydsl file = new FileEntityQuerydsl();
        file.setFilename(fileName);
        file.setFileType(fileType);
        file.setId(IdGenerator.generate());
        file.setUid(UUID.randomUUID().toString());

        transactionManager.execute(connection -> {
            transactionManager
                .insert(QFileEntityQuerydsl.file, connection)
                .populate(file)
                .execute();

            transactionManager
                .insert(QFileEntityDiskQuerydsl.fileDisk, connection)
                .columns(
                    QFileEntityDiskQuerydsl.fileDisk.path,
                    QFileEntityDiskQuerydsl.fileDisk.idFile
                )
                .values(path, file.getId())
                .execute();
        });

        return FileEntryDisk.of(
            file.getId(),
            file.getUid(),
            file.getFilename(),
            file.getFileType(),
            path
        );
    }

    @Override
    public String delete(String uid) {
        transactionManager.execute(connection -> {
            Long fileId = transactionManager
                .selectQuery()
                .select(QFileEntityQuerydsl.file.id)
                .where(QFileEntityQuerydsl.file.uid.eq(uid))
                .fetchFirst();

            transactionManager
                .delete(QFileEntityDiskQuerydsl.fileDisk, connection)
                .where(QFileEntityDiskQuerydsl.fileDisk.idFile.eq(fileId))
                .execute();

            transactionManager
                .delete(QFileEntityQuerydsl.file, connection)
                .where(QFileEntityQuerydsl.file.id.eq(fileId))
                .execute();
        });
        return uid;
    }

    @Override
    public String fileName(String fileUid) {
        return this.transactionManager.selectQuery()
            .select(QFileEntityQuerydsl.file.filename)
            .from(QFileEntityQuerydsl.file)
            .where(QFileEntityQuerydsl.file.uid.eq(fileUid))
            .fetchFirst();
    }

    @Override
    public FileEntryDisk findByUid(String fileUid) {
        return Optional.ofNullable(transactionManager
            .selectQuery()
            .select(
                QFileEntityQuerydsl.file.id,
                QFileEntityQuerydsl.file.uid,
                QFileEntityQuerydsl.file.fileType,
                QFileEntityQuerydsl.file.filename,
                QFileEntityDiskQuerydsl.fileDisk.path
            )
            .from(QFileEntityDiskQuerydsl.fileDisk)
            .innerJoin(QFileEntityQuerydsl.file)
            .on(QFileEntityQuerydsl.file.id.eq(QFileEntityDiskQuerydsl.fileDisk.idFile))
            .where(QFileEntityQuerydsl.file.uid.eq(fileUid))
            .fetchFirst()
        ).map(file -> FileEntryDisk.of(
            file.get(QFileEntityQuerydsl.file.id),
            file.get(QFileEntityQuerydsl.file.uid),
            file.get(QFileEntityQuerydsl.file.filename),
            file.get(QFileEntityQuerydsl.file.fileType),
            file.get(QFileEntityDiskQuerydsl.fileDisk.path))
        ).orElse(null);
    }

    @Override
    public FileEntryDisk findById(Long fileId) {
        Optional<String> uid = Optional.ofNullable(transactionManager
            .selectQuery()
            .select(QFileEntityQuerydsl.file.uid)
            .where(QFileEntityQuerydsl.file.id.eq(fileId))
            .fetchFirst());
        return uid.map(this::findByUid).orElse(null);
    }

    @Override
    public List<String> deleteUnreferenced(String fileType, EntityPath<?> fileEntity, NumberPath<Long> column) {
        return this.selectUnreferenced(fileType, fileEntity, column)
            .stream()
            .map(file -> delete(file.getUid()))
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

}

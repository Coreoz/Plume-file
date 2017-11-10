package com.coreoz.plume.file.db.querydsl;

import com.coreoz.plume.db.querydsl.crud.CrudDaoQuerydsl;
import com.coreoz.plume.db.querydsl.transaction.TransactionManagerQuerydsl;
import com.coreoz.plume.db.utils.IdGenerator;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.sql.SQLExpressions;

import javax.inject.Inject;
import java.util.List;

public class FileDaoDiskQuerydsl extends CrudDaoQuerydsl<FileEntityQuerydsl> {

    @Inject
    public FileDaoDiskQuerydsl(TransactionManagerQuerydsl transactionManager) {
        super(transactionManager, QFileEntityQuerydsl.file);
    }

    public FileEntityQuerydsl upload(String fileType, String fileName, String path) {
        FileEntityQuerydsl file = new FileEntityQuerydsl();
        file.setFilename(fileName);
        file.setFileType(fileType);

        transactionManager.execute(connection -> {
            Long idFile = save(file, connection).getId();

            transactionManager
                .insert(QFileEntityDiskQuerydsl.fileDisk, connection)
                .columns(
                    QFileEntityDiskQuerydsl.fileDisk.id,
                    QFileEntityDiskQuerydsl.fileDisk.path,
                    QFileEntityDiskQuerydsl.fileDisk.idFile
                )
                .values(IdGenerator.generate(), path, idFile)
                .execute();
            file.setId(idFile);

        });

        return file;
    }

    public List<FileEntityQuerydsl> selectUnreferenced(String fileType,
                                                           EntityPath<?> fileEntity,
                                                           NumberPath<Long> column) {
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

    @Override
    public long delete(Long id) {
        transactionManager.execute(connection -> {
            transactionManager
                .delete(QFileEntityDiskQuerydsl.fileDisk, connection)
                .where(QFileEntityDiskQuerydsl.fileDisk.idFile.eq(id))
                .execute();

            transactionManager
                .delete(QFileEntityQuerydsl.file, connection)
                .where(QFileEntityQuerydsl.file.id.eq(id))
                .execute();
        });
        return id;
    }

    public FileEntityDiskQuerydsl findFileDiskById(Long id) {
        return transactionManager
            .selectQuery()
            .select(QFileEntityDiskQuerydsl.fileDisk)
            .from(QFileEntityDiskQuerydsl.fileDisk)
            .where(QFileEntityDiskQuerydsl.fileDisk.idFile.eq(id))
            .fetchFirst();
    }

}

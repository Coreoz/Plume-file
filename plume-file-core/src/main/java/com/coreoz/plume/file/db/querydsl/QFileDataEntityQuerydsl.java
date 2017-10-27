package com.coreoz.plume.file.db.querydsl;


import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

public class QFileDataEntityQuerydsl extends com.querydsl.sql.RelationalPathBase<FileDataEntityQuerydsl>{

    private static final long serialVersionUID = 43454386;

    public static final QFileDataEntityQuerydsl fileData = new QFileDataEntityQuerydsl("PLM_FILE_DATA");

    public final SimplePath<byte[]> data = createSimple("data", byte[].class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> idFile = createNumber("idFile", Long.class);

    public final com.querydsl.sql.PrimaryKey<FileDataEntityQuerydsl> plmFilePk = createPrimaryKey(id);

    public final com.querydsl.sql.ForeignKey<com.coreoz.plume.file.db.querydsl.FileEntityQuerydsl> plmFileFk = createForeignKey(idFile, "ID");

    public QFileDataEntityQuerydsl(String variable) {
        super(FileDataEntityQuerydsl.class, forVariable(variable), "OTA", "PLM_FILE_DATA");
        addMetadata();
    }

    public QFileDataEntityQuerydsl(String variable, String schema, String table) {
        super(FileDataEntityQuerydsl.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFileDataEntityQuerydsl(String variable, String schema) {
        super(FileDataEntityQuerydsl.class, forVariable(variable), schema, "PLM_FILE_DATA");
        addMetadata();
    }

    public QFileDataEntityQuerydsl(Path<? extends FileDataEntityQuerydsl> path) {
        super(path.getType(), path.getMetadata(), "OTA", "PLM_FILE_DATA");
        addMetadata();
    }

    public QFileDataEntityQuerydsl(PathMetadata metadata) {
        super(FileDataEntityQuerydsl.class, metadata, "OTA", "PLM_FILE_DATA");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(this.data, ColumnMetadata.named("DATA").withIndex(4).ofType(2004).withSize(2147483647).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.DECIMAL).withSize(19).notNull());
        addMetadata(idFile, ColumnMetadata.named("ID_FILE").withIndex(2).ofType(Types.DECIMAL).withSize(19).notNull());
    }
}

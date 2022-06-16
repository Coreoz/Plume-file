package com.coreoz.plume.file.db.beans;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.sql.ColumnMetadata;

import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

public class QFileDataQueryDsl extends com.querydsl.sql.RelationalPathBase<FileDataQueryDsl> {

    private static final long serialVersionUID = 43454386;

    public static final QFileDataQueryDsl file = new QFileDataQueryDsl("PLM_FILE_DATA");

    public final NumberPath<Long> id = createNumber("id", Long.class);
    public final SimplePath<byte[]> data = createSimple("data", byte[].class);

    public QFileDataQueryDsl(String variable) {
        super(FileDataQueryDsl.class, forVariable(variable), "PLM", "PLM_FILE_DATA");
        addMetadata();
    }

    public QFileDataQueryDsl(String variable, String schema, String table) {
        super(FileDataQueryDsl.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFileDataQueryDsl(String variable, String schema) {
        super(FileDataQueryDsl.class, forVariable(variable), schema, "PLM_FILE_DATA");
        addMetadata();
    }

    public QFileDataQueryDsl(Path<? extends FileDataQueryDsl> path) {
        super(path.getType(), path.getMetadata(), "PLM", "PLM_FILE");
        addMetadata();
    }

    public QFileDataQueryDsl(PathMetadata metadata) {
        super(FileDataQueryDsl.class, metadata, "PLM", "PLM_FILE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(id, ColumnMetadata.named("id").withIndex(1).ofType(Types.DECIMAL).withSize(19).notNull());
        addMetadata(data, ColumnMetadata.named("data").withIndex(2).ofType(Types.BLOB).withSize(2147483647).notNull());
    }
}

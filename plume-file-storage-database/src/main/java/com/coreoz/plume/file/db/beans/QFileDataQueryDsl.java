package com.coreoz.plume.file.db.beans;

import com.coreoz.plume.file.services.data.MeasuredSizeInputStream;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

import java.io.ByteArrayInputStream;
import java.sql.Types;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

public class QFileDataQueryDsl extends com.querydsl.sql.RelationalPathBase<FileDataQueryDsl> {

    private static final long serialVersionUID = 43454386;

    public static final QFileDataQueryDsl file = new QFileDataQueryDsl("PLM_FILE_DATA");

    public final StringPath uniqueName = createString("unique_name");
    public final SimplePath<MeasuredSizeInputStream> data = createSimple("data", MeasuredSizeInputStream.class);

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
        super(path.getType(), path.getMetadata(), "PLM", "PLM_FILE_DATA");
        addMetadata();
    }

    public QFileDataQueryDsl(PathMetadata metadata) {
        super(FileDataQueryDsl.class, metadata, "PLM", "PLM_FILE_DATA");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(uniqueName, ColumnMetadata.named("unique_name").withIndex(1).ofType(Types.VARCHAR).withSize(127).notNull());
        addMetadata(data, ColumnMetadata.named("data").withIndex(2).ofType(Types.BLOB).withSize(2147483647).notNull());
    }
}

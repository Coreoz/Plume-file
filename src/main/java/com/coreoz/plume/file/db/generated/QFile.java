package com.coreoz.plume.file.db.generated;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;

import com.querydsl.sql.ColumnMetadata;
import java.sql.Types;




/**
 * QFile is a Querydsl query type for File
 */
@Generated("com.querydsl.sql.codegen.MetaDataSerializer")
public class QFile extends com.querydsl.sql.RelationalPathBase<File> {

    private static final long serialVersionUID = 117412621;

    public static final QFile file = new QFile("PLM_FILE");

    public final SimplePath<byte[]> data = createSimple("data", byte[].class);

    public final StringPath filename = createString("filename");

    public final StringPath fileType = createString("fileType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.querydsl.sql.PrimaryKey<File> constraintB = createPrimaryKey(id);

    public QFile(String variable) {
        super(File.class, forVariable(variable), "PUBLIC", "PLM_FILE");
        addMetadata();
    }

    public QFile(String variable, String schema, String table) {
        super(File.class, forVariable(variable), schema, table);
        addMetadata();
    }

    public QFile(Path<? extends File> path) {
        super(path.getType(), path.getMetadata(), "PUBLIC", "PLM_FILE");
        addMetadata();
    }

    public QFile(PathMetadata metadata) {
        super(File.class, metadata, "PUBLIC", "PLM_FILE");
        addMetadata();
    }

    public void addMetadata() {
        addMetadata(data, ColumnMetadata.named("DATA").withIndex(4).ofType(Types.BLOB).withSize(2147483647).notNull());
        addMetadata(filename, ColumnMetadata.named("FILENAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
        addMetadata(fileType, ColumnMetadata.named("FILE_TYPE").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
        addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
    }

}


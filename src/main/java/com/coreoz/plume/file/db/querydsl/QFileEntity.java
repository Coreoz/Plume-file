package com.coreoz.plume.file.db.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import java.sql.Types;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

class QFileEntity extends com.querydsl.sql.RelationalPathBase<FileEntity> {

	private static final long serialVersionUID = 117412621;

	public static final QFileEntity file = new QFileEntity("PLM_FILE");

	public final SimplePath<byte[]> data = createSimple("data", byte[].class);

	public final StringPath filename = createString("filename");

	public final StringPath fileType = createString("fileType");

	public final NumberPath<Long> id = createNumber("id", Long.class);

	public final com.querydsl.sql.PrimaryKey<FileEntity> constraintB = createPrimaryKey(id);

	public QFileEntity(String variable) {
		super(FileEntity.class, forVariable(variable), "PUBLIC", "PLM_FILE");
		addMetadata();
	}

	public QFileEntity(String variable, String schema, String table) {
		super(FileEntity.class, forVariable(variable), schema, table);
		addMetadata();
	}

	public QFileEntity(Path<? extends FileEntity> path) {
		super(path.getType(), path.getMetadata(), "PUBLIC", "PLM_FILE");
		addMetadata();
	}

	public QFileEntity(PathMetadata metadata) {
		super(FileEntity.class, metadata, "PUBLIC", "PLM_FILE");
		addMetadata();
	}

	public void addMetadata() {
		addMetadata(data, ColumnMetadata.named("DATA").withIndex(4).ofType(Types.BLOB).withSize(2147483647).notNull());
		addMetadata(filename, ColumnMetadata.named("FILENAME").withIndex(2).ofType(Types.VARCHAR).withSize(255));
		addMetadata(fileType, ColumnMetadata.named("FILE_TYPE").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
		addMetadata(id, ColumnMetadata.named("ID").withIndex(1).ofType(Types.BIGINT).withSize(19).notNull());
	}

}
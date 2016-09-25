package com.coreoz.plume.file.db.hibernate;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.ArrayPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

/**
 * QFileEntity is a Querydsl query type for FileEntity
 */
class QFileEntity extends EntityPathBase<FileEntity> {

	private static final long serialVersionUID = -1594138963L;

	public static final QFileEntity fileEntity = new QFileEntity("fileEntity");

	public final ArrayPath<byte[], Byte> data = createArray("data", byte[].class);

	public final StringPath filename = createString("filename");

	public final StringPath fileType = createString("fileType");

	public final NumberPath<Long> id = createNumber("id", Long.class);

	public QFileEntity(String variable) {
		super(FileEntity.class, forVariable(variable));
	}

	public QFileEntity(Path<? extends FileEntity> path) {
		super(path.getType(), path.getMetadata());
	}

	public QFileEntity(PathMetadata metadata) {
		super(FileEntity.class, metadata);
	}

}

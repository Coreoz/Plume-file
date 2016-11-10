package com.coreoz.plume.file.gallery.db.querydsl;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import java.sql.Types;

import com.coreoz.plume.file.db.querydsl.FileEntityQuerydsl;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.ColumnMetadata;

public class QFileGallerydsl extends com.querydsl.sql.RelationalPathBase<FileGalleryQuerydsl> {

	private static final long serialVersionUID = -908780557;

	public static final QFileGallerydsl fileGallery = new QFileGallerydsl("PLM_FILE_GALLERY");

	public final StringPath galleryType = createString("galleryType");

	public final NumberPath<Long> idData = createNumber("idData", Long.class);

	public final NumberPath<Long> idFile = createNumber("idFile", Long.class);

	public final NumberPath<Integer> position = createNumber("position", Integer.class);

	public final com.querydsl.sql.PrimaryKey<FileGalleryQuerydsl> plmFileGalleryPk = createPrimaryKey(idFile);

	public final com.querydsl.sql.ForeignKey<FileEntityQuerydsl> plmFileGalleryFk = createForeignKey(idFile, "ID");

	public QFileGallerydsl(String variable) {
		super(FileGalleryQuerydsl.class, forVariable(variable), "PUBLIC", "PLM_FILE_GALLERY");
		addMetadata();
	}

	public QFileGallerydsl(String variable, String schema, String table) {
		super(FileGalleryQuerydsl.class, forVariable(variable), schema, table);
		addMetadata();
	}

	public QFileGallerydsl(Path<? extends FileGalleryQuerydsl> path) {
		super(path.getType(), path.getMetadata(), "PUBLIC", "PLM_FILE_GALLERY");
		addMetadata();
	}

	public QFileGallerydsl(PathMetadata metadata) {
		super(FileGalleryQuerydsl.class, metadata, "PUBLIC", "PLM_FILE_GALLERY");
		addMetadata();
	}

	public void addMetadata() {
		addMetadata(galleryType, ColumnMetadata.named("GALLERY_TYPE").withIndex(3).ofType(Types.VARCHAR).withSize(255).notNull());
		addMetadata(idData, ColumnMetadata.named("ID_DATA").withIndex(2).ofType(Types.DECIMAL).withSize(19).notNull());
		addMetadata(idFile, ColumnMetadata.named("ID_FILE").withIndex(1).ofType(Types.DECIMAL).withSize(19).notNull());
		addMetadata(position, ColumnMetadata.named("POSITION").withIndex(4).ofType(Types.DECIMAL).withSize(8).notNull());
	}

}
